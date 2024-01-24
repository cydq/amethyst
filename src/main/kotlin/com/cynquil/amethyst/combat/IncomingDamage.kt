package com.cynquil.amethyst.combat

import com.cynquil.amethyst.extensions.amDefense
import com.cynquil.amethyst.extensions.amHealth
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.DamageUtil
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.damage.DamageTypes
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.passive.WolfEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.registry.tag.DamageTypeTags
import net.minecraft.registry.tag.EntityTypeTags
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundEvent
import net.minecraft.stat.Stats
import kotlin.math.max
import kotlin.math.min

object IncomingDamage {
    private fun scaledDamage(entity: LivingEntity, source: DamageSource, amount: Double): Double {
        if (source.isOf(DamageTypes.PLAYER_ATTACK) || source.isOf(DamageTypes.MOB_ATTACK))
            return amount

        return max(amount * 5, amount / 20 * entity.amHealth)
    }

    private fun defenseMitigation(entity: LivingEntity, source: DamageSource, amount: Double): Double {
        if (source.isOf(DamageTypes.OUT_OF_WORLD) || source.isOf(DamageTypes.OUTSIDE_BORDER))
            return .0

        val def = entity.amDefense
        return def / (def + max(100.0, amount))
    }

    private fun shieldMitigation(entity: LivingEntity, source: DamageSource, amount: Double): Double {
        val shield = amount > 0.0f && entity.blockedByShield(source)
        if (!shield) return 0.0

        entity.damageShield(amount.toFloat())

        if (!source.isIn(DamageTypeTags.IS_PROJECTILE)) {
            val sourceEntity = source.source
            if (sourceEntity is LivingEntity) sourceEntity.takeShieldHit(sourceEntity)
        }

        return defenseMitigation(entity, source, amount)
    }

    private fun freezeMultiplier(entity: LivingEntity, source: DamageSource): Double =
        if (source.isIn(DamageTypeTags.IS_FREEZING) && entity.type.isIn(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES))
            5.0
        else
            1.0

    private fun damageEntity(entity: LivingEntity, source: DamageSource, damage: Float): Pair<Boolean, Boolean> {
        if (entity.timeUntilRegen > 10 && !source.isIn(DamageTypeTags.BYPASSES_COOLDOWN)) {
            if (damage <= entity.lastDamageTaken)
                return false to true

            entity.applyDamage(source, damage - entity.lastDamageTaken)
            entity.lastDamageTaken = damage
            return false to false
        }

        entity.lastDamageTaken = damage
        entity.timeUntilRegen = 20
        entity.applyDamage(source, damage)
        entity.maxHurtTime = 10
        entity.hurtTime = entity.maxHurtTime

        return true to false
    }

    private fun applyHelmet(entity: LivingEntity, source: DamageSource, damage: Float) {
        if (source.isIn(DamageTypeTags.DAMAGES_HELMET) && !entity.getEquippedStack(EquipmentSlot.HEAD).isEmpty)
            entity.damageHelmet(source, damage)
    }

    private fun applyAttacker(entity: LivingEntity, source: DamageSource) {
        val attacker = source.attacker ?: return

        if (attacker is LivingEntity && !source.isIn(DamageTypeTags.NO_ANGER))
            entity.attacker = attacker

        if (attacker is PlayerEntity) {
            entity.playerHitTimer = 100
            entity.attackingPlayer = attacker
        }

        if (attacker is WolfEntity && attacker.isTamed) {
            val owner = attacker.owner

            entity.playerHitTimer = 100
            entity.attackingPlayer = if (owner is PlayerEntity) owner else null
        }
    }

    private fun applyKnockback(entity: LivingEntity, source: DamageSource, damage: Float, shieldMit: Double) {
        if (!source.isIn(DamageTypeTags.NO_IMPACT) && (shieldMit > 0 || damage > 0.0f))
            entity.scheduleVelocityUpdate()

        val attacker = source.attacker ?: return

        if (source.isIn(DamageTypeTags.NO_KNOCKBACK))
            return

        var dx = attacker.x - entity.x
        var dz = attacker.z - entity.z

        while (dx * dx + dz * dz < 1.0E-4) {
            dx = (Math.random() - Math.random()) * 0.01
            dz = (Math.random() - Math.random()) * 0.01
        }

        entity.takeKnockback(0.4000000059604645, dx, dz)
        if (shieldMit <= 0) entity.tiltScreen(dx, dz)
    }

    fun damage(entity: LivingEntity, source: DamageSource, amount: Float): Boolean {
        if (entity.isInvulnerableTo(source) || entity.world.isClient || entity.isDead) return false
        if (source.isIn(DamageTypeTags.IS_FIRE) && entity.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) return false

        if (entity.isSleeping && !entity.world.isClient) entity.wakeUp()

        entity.despawnCounter = 0

        val attacker = source.attacker
        val initialDamage = scaledDamage(entity, source, amount.toDouble())

        val shieldMit = shieldMitigation(entity, source, initialDamage)
        val freezeMulti = freezeMultiplier(entity, source)

        val damage = (initialDamage * (1 - shieldMit) * freezeMulti).toFloat()

        entity.limbAnimator.speed = 1.5f

        val (damaged, exit) = damageEntity(entity, source, damage)
        if (exit) return false

        applyHelmet(entity, source, damage)
        applyAttacker(entity, source)

        if (damaged) {
            if (shieldMit > 0)
                entity.world.sendEntityStatus(entity, 29.toByte())

            entity.world.sendEntityDamage(entity, source)
            applyKnockback(entity, source, damage, shieldMit)
        }

        if (entity.isDead && !entity.tryUseTotem(source)) {
            val soundEvent: SoundEvent? = entity.deathSound
            if (damaged && soundEvent != null) {
                entity.playSound(soundEvent, entity.soundVolume, entity.soundPitch)
            }

            entity.onDeath(source)
        } else if (damaged) {
            entity.playHurtSound(source)
        }

        if (damage > 0) {
            entity.lastDamageSource = source
            entity.lastDamageTime = entity.world.time
        }

        if (entity is ServerPlayerEntity) {
            Criteria.ENTITY_HURT_PLAYER.trigger(entity, source, initialDamage.toFloat(), damage, false)

            if (shieldMit > 0.0f)
                entity.increaseStat(Stats.DAMAGE_BLOCKED_BY_SHIELD, Math.round(damage * shieldMit * 10.0f).toInt())
        }

        if (attacker is ServerPlayerEntity)
            Criteria.PLAYER_HURT_ENTITY.trigger(attacker, entity, source, initialDamage.toFloat(), damage, false)

        return damage > 0
    }

    fun modifyAppliedDamage(entity: LivingEntity, source: DamageSource, amount: Float): Float {
        if (source.isIn(DamageTypeTags.BYPASSES_EFFECTS)) return amount

        val defenseMit = defenseMitigation(entity, source, amount.toDouble())
        val resMit = resistanceEffectMitigation(entity, source, amount.toDouble())
        val protMit = protectionMitigation(entity, source, amount.toDouble())

        val final = amount * (1 - defenseMit) * (1 - resMit) * (1 - protMit)
        return max(final.toFloat(), 0f)
    }

    private fun resistanceEffectMitigation(entity: LivingEntity, source: DamageSource, amount: Double): Double {
        if (!entity.hasStatusEffect(StatusEffects.RESISTANCE) || source.isIn(DamageTypeTags.BYPASSES_RESISTANCE))
            return 0.0

        val level: Int = ((entity.getStatusEffect(StatusEffects.RESISTANCE)?.amplifier ?: 0) + 1) * 5

        val mitigation = min(level * 0.15, 0.8)
        val mitigatedDamage = amount * mitigation

        if (mitigatedDamage > 0) {
            if (entity is ServerPlayerEntity)
                entity.increaseStat(Stats.DAMAGE_RESISTED, Math.round(mitigatedDamage.toFloat() * 10.0f))

            val attacker = source.attacker
            if (attacker is ServerPlayerEntity)
                attacker.increaseStat(Stats.DAMAGE_DEALT_RESISTED, Math.round(mitigatedDamage.toFloat() * 10.0f))
        }

        return mitigation
    }

    private fun protectionMitigation(entity: LivingEntity, source: DamageSource, amount: Double): Double {
        if (source.isIn(DamageTypeTags.BYPASSES_ENCHANTMENTS)) return 0.0

        val level = EnchantmentHelper.getProtectionAmount(entity.armorItems, source)
        if (level <= 0) return 0.0

        return min(4 / amount * level, 0.7)
    }
}

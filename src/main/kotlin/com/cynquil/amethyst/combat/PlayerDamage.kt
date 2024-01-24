package com.cynquil.amethyst.combat

import com.cynquil.amethyst.extensions.*
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityGroup
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.boss.dragon.EnderDragonPart
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.SwordItem
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.stat.Stats
import net.minecraft.util.Hand
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d

object PlayerDamage {

    fun attack(player: PlayerEntity, target: Entity) {
        if (!target.isAttackable || target.handleAttack(player)) return

        val weapon = player.getStackInHand(Hand.MAIN_HAND)

        val targetGroup = if (target is LivingEntity) target.group else EntityGroup.DEFAULT
        val initialHealth = if (target is LivingEntity) target.health else 0.0f
        val initialVelocity = target.velocity

        // Cooldown
        val cooldown = player.getAttackCooldownProgress(0.5f)
        val cooldownMulti = 0.2 + cooldown * cooldown * 0.8
        player.resetLastAttackedTicks()

        // Base Damage
        val baseDamage = player.amDamage
        val enchantDamage = EnchantmentHelper.getAttackDamage(player.mainHandStack, targetGroup) * cooldown * 5

        // Check
        if (baseDamage <= 0 && enchantDamage <= 0) return
        val knockback = calculateKnockback(player, cooldown)
        val sweeping = calculateSweeping(target, player, weapon, cooldown)
        val fireAspect = calculateFireAspect(target, player)

        // Final Damage
        val finalDamage = Combat.calculateDamage(player, baseDamage, enchantDamage.toDouble(), cooldownMulti)

        // Apply damage
        val damaged = target.damage(player.damageSources.playerAttack(player), finalDamage.amount)
        if (!damaged) {
            player.play(SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE)
            if (fireAspect <= 0) target.extinguish()
            return
        }

        applyKnockback(target, player, knockback)
        applySweeping(target, player, finalDamage.amount, sweeping)
        applyVelocity(target, initialVelocity)

        if (finalDamage.didCrit) {
            player.play(SoundEvents.ENTITY_PLAYER_ATTACK_CRIT)
            player.addCritParticles(target)
        } else if (!sweeping) {
            player.play(
                if (cooldown > 0.9f) SoundEvents.ENTITY_PLAYER_ATTACK_STRONG
                else SoundEvents.ENTITY_PLAYER_ATTACK_WEAK
            )
        }

        if (enchantDamage > 0.0f)
            player.addEnchantedHitParticles(target)

        player.onAttacking(target)

        if (target is LivingEntity)
            EnchantmentHelper.onUserDamaged(target, player)

        EnchantmentHelper.onTargetDamaged(player, target)

        // Item post hit handler
        itemPostHit(player, target)

        if (target is LivingEntity) {
            val damageDealt = initialHealth - target.health
            player.increaseStat(Stats.DAMAGE_DEALT, Math.round(damageDealt * 10.0f))

            if (fireAspect > 0)
                target.setOnFireFor(fireAspect * 4)

            if (player.world is ServerWorld && damageDealt > 2.0f) {
                val n = (damageDealt.toDouble() * 0.5).toInt()
                (player.world as ServerWorld).spawnParticles(
                    ParticleTypes.DAMAGE_INDICATOR,
                    target.x,
                    target.getBodyY(0.5),
                    target.z,
                    n,
                    0.1,
                    0.0,
                    0.1,
                    0.2
                )
            }
        }

        // Attack exhaustion
        player.addExhaustion(0.1f)
    }

    private fun calculateKnockback(player: PlayerEntity, cooldown: Float): Int {
        var knockback = EnchantmentHelper.getKnockback(player)

        if (player.isSprinting && cooldown > 0.9f) {
            player.play(SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK)
            knockback++
        }

        return knockback
    }

    private fun calculateSweeping(
        target: Entity,
        player: PlayerEntity,
        weapon: ItemStack,
        cooldown: Float,
    ): Boolean {
        val minecraftCritical = cooldown > 0.9f
            && player.fallDistance > 0.0f
            && !player.isOnGround
            && !player.isClimbing
            && !player.isTouchingWater
            && !player.hasStatusEffect(StatusEffects.BLINDNESS)
            && !player.hasVehicle()
            && target is LivingEntity
            && !player.isSprinting

        return cooldown > 0.9f
            && !minecraftCritical
            && !player.isSprinting // Spring knockback
            && player.isOnGround
            && player.horizontalSpeed - player.prevHorizontalSpeed < player.movementSpeed
            && weapon.item is SwordItem
    }

    private fun calculateFireAspect(target: Entity, player: PlayerEntity): Int {
        val fireAspect = EnchantmentHelper.getFireAspect(player)
        if (target is LivingEntity && fireAspect > 0 && !target.isOnFire()) target.setOnFireFor(1)
        return fireAspect
    }

    private fun applyKnockback(target: Entity, player: PlayerEntity, knockback: Int) {
        if (knockback <= 0) return

        if (target is LivingEntity) {
            target.takeKnockback(
                (knockback.toFloat() * 0.5f).toDouble(),
                MathHelper.sin(player.yaw * 0.017453292f).toDouble(),
                (-MathHelper.cos(player.yaw * 0.017453292f)).toDouble()
            )
        } else {
            target.addVelocity(
                (-MathHelper.sin(player.yaw * 0.017453292f) * knockback.toFloat() * 0.5f).toDouble(),
                0.1,
                (MathHelper.cos(player.yaw * 0.017453292f) * knockback.toFloat() * 0.5f).toDouble()
            )
        }

        player.velocity = player.velocity.multiply(0.6, 1.0, 0.6)
        player.isSprinting = false
    }

    private fun applySweeping(target: Entity, player: PlayerEntity, damage: Float, sweeping: Boolean) {
        if (!sweeping) return

        val sweepingDamage = 1.0f + EnchantmentHelper.getSweepingMultiplier(player) * damage

        val nearbyEntities = player.world.getNonSpectatingEntities(
            LivingEntity::class.java,
            target.boundingBox.expand(1.0, 0.25, 1.0)
        )

        for (entity in nearbyEntities) {
            if (
                entity === player ||
                entity === target ||
                player.isTeammate(entity) ||
                (entity is ArmorStandEntity && entity.isMarker) ||
                player.squaredDistanceTo(entity) >= 9
            ) {
                continue
            }

            entity.takeKnockback(
                0.4000000059604645,
                MathHelper.sin(player.yaw * 0.017453292f).toDouble(),
                -MathHelper.cos(player.yaw * 0.017453292f).toDouble()
            )

            entity.damage(player.damageSources.playerAttack(player), sweepingDamage.toFloat())
        }

        player.play(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP)
        player.spawnSweepAttackParticles()
    }

    private fun applyVelocity(target: Entity, initialVelocity: Vec3d) {
        if (target !is ServerPlayerEntity || !target.velocityModified) return

        target.networkHandler.sendPacket(EntityVelocityUpdateS2CPacket(target))
        target.velocityModified = false
        target.setVelocity(initialVelocity)
    }

    private fun itemPostHit(player: PlayerEntity,  target: Entity) {
        val hand = player.mainHandStack
        val entity = if (target is EnderDragonPart) target.owner else target

        if (player.world.isClient || hand.isEmpty || entity !is LivingEntity)
            return

        hand.postHit(entity, player)

        if (hand.isEmpty)
            player.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY)
    }

    private fun PlayerEntity.play(sound: SoundEvent): Unit =
        world.playSound(null, x, y, z, sound, soundCategory, 1.0f, 1.0f)
}

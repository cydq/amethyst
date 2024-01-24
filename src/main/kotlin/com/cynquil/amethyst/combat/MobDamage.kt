package com.cynquil.amethyst.combat

import com.cynquil.amethyst.extensions.amDamage
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.math.MathHelper
import kotlin.math.max

object MobDamage {
    fun attack(mob: MobEntity, target: Entity): Boolean {
        val baseDamage = max(mob.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) * 5, mob.amDamage)
        val knockback = mob.getAttributeValue(EntityAttributes.GENERIC_ATTACK_KNOCKBACK)

        val enchantmentDamage = if (target is LivingEntity) EnchantmentHelper.getAttackDamage(mob.mainHandStack, target.group) else 0
        val enchantmentKnockback = if (target is LivingEntity) EnchantmentHelper.getKnockback(mob) else 0

        val fireAspect = EnchantmentHelper.getFireAspect(mob)
        if (fireAspect > 0) target.setOnFireFor(fireAspect * 4)

        val finalDamage = Combat.calculateDamage(mob, baseDamage, enchantmentDamage.toDouble())
        val finalKnockback = knockback + enchantmentKnockback

        val damaged = target.damage(mob.damageSources.mobAttack(mob), finalDamage.amount)
        if (!damaged) return false

        if (finalKnockback > 0.0f && target is LivingEntity) {
            target.takeKnockback(
                finalKnockback * 0.5,
                MathHelper.sin(mob.yaw * 0.017453292f).toDouble(),
                (-MathHelper.cos(mob.yaw * 0.017453292f)).toDouble()
            )

            mob.velocity = mob.velocity.multiply(0.6, 1.0, 0.6)
        }

        if (target is PlayerEntity) {
            mob.disablePlayerShield(
                target,
                mob.mainHandStack,
                if (target.isUsingItem) target.activeItem else ItemStack.EMPTY
            )
        }

        mob.applyDamageEffects(mob, target)
        mob.onAttacking(target)

        return true
    }
}

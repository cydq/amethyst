package com.cynquil.amethyst.effects

import com.cynquil.amethyst.extensions.id
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectCategory
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry

object RespawnEffect : StatusEffect(StatusEffectCategory.BENEFICIAL, 0x76eec6) {
    override fun canApplyUpdateEffect(duration: Int, amplifier: Int): Boolean =
        true

    override fun applyUpdateEffect(entity: LivingEntity?, amplifier: Int) {
        if (entity == null) return //  || entity.maxHealth <= entity.defaultMaxHealth

        entity.health = entity.maxHealth
//        entity.removeStatusEffect(this)
    }

    fun register() {
        Registry.register(Registries.STATUS_EFFECT, "amethyst:fresh_soul".id, this)
    }
}

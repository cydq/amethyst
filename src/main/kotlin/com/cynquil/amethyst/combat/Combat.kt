package com.cynquil.amethyst.combat

import com.cynquil.amethyst.extensions.amCritChance
import com.cynquil.amethyst.extensions.amCritDamage
import com.cynquil.amethyst.extensions.amFerocity
import com.cynquil.amethyst.extensions.amStrength
import net.minecraft.entity.LivingEntity
import kotlin.math.floor

object Combat {
    private fun calculateStrengthMulti(str: Double): Double =
        str / 100

    private fun calculateFerocity(fero: Double): Double =
        floor(fero / 100) + (Math.random() < (fero % 100) / 100).compareTo(false)

    private fun calculateCrit(cc: Double, cd: Double): Double =
        (Math.random() < cc / 100).compareTo(false) * cd / 100

    fun calculateDamage(
        entity: LivingEntity,
        initialDamage: Double,
        enchantDamage: Double,
        cooldownMultiplier: Double = 1.0
    ): Damage {
        val strengthMulti = calculateStrengthMulti(entity.amStrength)
        val critMulti = calculateCrit(entity.amCritChance, entity.amCritDamage)
        val feroMulti = calculateFerocity(entity.amFerocity)

        val baseDamage = (initialDamage * cooldownMultiplier + enchantDamage)
        val finalDamage = baseDamage * (1 + strengthMulti) * (1 + critMulti) * (1 + feroMulti)

        return Damage(finalDamage.toFloat(), critMulti > 0)
    }

    data class Damage(
        val amount: Float,
        val didCrit: Boolean,
    )
}

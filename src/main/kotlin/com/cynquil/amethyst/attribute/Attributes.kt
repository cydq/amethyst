package com.cynquil.amethyst.attribute

import com.cynquil.amethyst.extensions.id
import net.minecraft.entity.attribute.EntityAttributes

object Attributes {
    private val values = mutableListOf<Attribute>()

    val Health = attr("amethyst:health", min = 100.0, default = 100.0)
        .sync(EntityAttributes.GENERIC_MAX_HEALTH) { v, _ -> v / 5 }

    val AttackSpeed = attr("amethyst:attack_speed", min = 0.0, default = 100.0)
        .sync(EntityAttributes.GENERIC_ATTACK_SPEED) { v, o -> o * v / 100 }

    val Speed = attr("amethyst:speed", min = 0.0, default = 100.0, max = 500.0)
        .sync(EntityAttributes.GENERIC_MOVEMENT_SPEED) { v, o -> o * v / 100 }
        .sync(EntityAttributes.GENERIC_FLYING_SPEED) { v, o -> o * v / 100 }

    val Intelligence = attr("amethyst:intelligence", min = 0.0, default = 0.0)
    val Strength = attr("amethyst:strength", min = 0.0, default = 0.0)
    val CriticalChance = attr("amethyst:critical_chance", min = 0.0, max = 100.0, default = 0.0)
    val CriticalDamage = attr("amethyst:critical_damage", min = 0.0, default = 0.0)
    val Defense = attr("amethyst:defense", min = 0.0, default = 0.0)
    val Ferocity = attr("amethyst:ferocity", min = 0.0, default = 0.0)
    val MagicFind = attr("amethyst:magic_find", default = 0.0)

    private fun attr(
        id: String,
        min: Double = Double.NEGATIVE_INFINITY,
        max: Double = Double.POSITIVE_INFINITY,
        default: Double = .0,
    ): Attribute =
        Attribute(id.id, min, max, default)
            .register()
            .also { values.add(it) }

    fun values(): List<Attribute> =
        values
}

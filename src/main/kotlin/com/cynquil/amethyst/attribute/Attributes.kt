package com.cynquil.amethyst.attribute

import com.cynquil.amethyst.AmRegistries
import com.cynquil.amethyst.extensions.id

object Attributes {
    @JvmStatic
    val attackSpeed by lazy { AmRegistries.Attribute.get("amethyst:attack_speed".id)!! }

    @JvmStatic
    val criticalChance by lazy { AmRegistries.Attribute.get("amethyst:critical_chance".id)!! }

    @JvmStatic
    val criticalDamage by lazy { AmRegistries.Attribute.get("amethyst:critical_damage".id)!! }

    @JvmStatic
    val damage by lazy { AmRegistries.Attribute.get("amethyst:damage".id)!! }

    @JvmStatic
    val defense by lazy { AmRegistries.Attribute.get("amethyst:defense".id)!! }

    @JvmStatic
    val ferocity by lazy { AmRegistries.Attribute.get("amethyst:ferocity".id)!! }

    @JvmStatic
    val health by lazy { AmRegistries.Attribute.get("amethyst:health".id)!! }

    @JvmStatic
    val intelligence by lazy { AmRegistries.Attribute.get("amethyst:intelligence".id)!! }

    @JvmStatic
    val magicFind by lazy { AmRegistries.Attribute.get("amethyst:magic_find".id)!! }

    @JvmStatic
    val speed by lazy { AmRegistries.Attribute.get("amethyst:speed".id)!! }

    @JvmStatic
    val strength by lazy { AmRegistries.Attribute.get("amethyst:strength".id)!! }

    @JvmStatic
    fun values(): List<Attribute> =
        allValues().filter { !it.hidden }

    @JvmStatic
    fun allValues(): List<Attribute> =
        AmRegistries.Attribute.toList()
}

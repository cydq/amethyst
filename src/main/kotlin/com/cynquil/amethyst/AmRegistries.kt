package com.cynquil.amethyst

import com.cynquil.amethyst.ability.Ability
import com.cynquil.amethyst.attribute.Attribute
import com.cynquil.amethyst.extensions.id
import com.cynquil.amethyst.rarity.Rarity
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder
import net.minecraft.registry.RegistryKey

object AmRegistries {
    val Attribute = FabricRegistryBuilder.createSimple(RegistryKey.ofRegistry<Attribute>("amethyst:attribute".id))
        .buildAndRegister()!!

    val Rarity = FabricRegistryBuilder.createSimple(RegistryKey.ofRegistry<Rarity>("amethyst:rarity".id))
        .buildAndRegister()!!

    val Ability = FabricRegistryBuilder.createSimple(RegistryKey.ofRegistry<Ability>("amethyst:ability".id))
        .buildAndRegister()!!
}

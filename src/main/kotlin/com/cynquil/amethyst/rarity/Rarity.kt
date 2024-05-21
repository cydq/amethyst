package com.cynquil.amethyst.rarity

import com.cynquil.amethyst.AmRegistries
import com.cynquil.amethyst.extensions.id
import com.cynquil.amethyst.mod.Registrable
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

data class Rarity(
    val id: Identifier,
    val weight: Int,
    val itemRarity: net.minecraft.util.Rarity,
    val enchantmentRarity: net.minecraft.enchantment.Enchantment.Rarity,
) : Comparable<Rarity>, Registrable {
    override operator fun compareTo(other: Rarity) =
        weight.compareTo(other.weight)

    override fun register() {
        Registry.register(AmRegistries.Rarity, id, this)
    }

    companion object {
        fun fromItemRarity(rarity: net.minecraft.util.Rarity) =
            AmRegistries.Rarity.get(when (rarity) {
                net.minecraft.util.Rarity.COMMON -> "amethyst:common"
                net.minecraft.util.Rarity.UNCOMMON -> "amethyst:uncommon"
                net.minecraft.util.Rarity.RARE -> "amethyst:rare"
                net.minecraft.util.Rarity.EPIC -> "amethyst:epic"
            }.id)!!

        fun fromEnchantmentRarity(rarity: net.minecraft.enchantment.Enchantment.Rarity) =
            AmRegistries.Rarity.get(when (rarity) {
                net.minecraft.enchantment.Enchantment.Rarity.COMMON -> "amethyst:common"
                net.minecraft.enchantment.Enchantment.Rarity.UNCOMMON -> "amethyst:uncommon"
                net.minecraft.enchantment.Enchantment.Rarity.RARE -> "amethyst:rare"
                net.minecraft.enchantment.Enchantment.Rarity.VERY_RARE -> "amethyst:epic"
            }.id)
    }
}

package com.cynquil.amethyst.rarity

import com.cynquil.amethyst.extensions.id
import net.minecraft.util.Identifier

enum class Rarity(val id: Identifier) {
    Common("amethyst:common".id),
    Uncommon("amethyst:uncommon".id),
    Rare("amethyst:rare".id),
    Epic("amethyst:epic".id),
    Legendary("amethyst:legendary".id),
    Mythic("amethyst:mythic".id),
    Special("amethyst:special".id);

    val itemRarity
        get() = when (this) {
            Common -> net.minecraft.util.Rarity.COMMON
            Uncommon -> net.minecraft.util.Rarity.UNCOMMON
            Rare -> net.minecraft.util.Rarity.RARE
            else -> net.minecraft.util.Rarity.EPIC
        }

    val enchantmentRarity
        get() = when (this) {
            Common -> net.minecraft.enchantment.Enchantment.Rarity.COMMON
            Uncommon -> net.minecraft.enchantment.Enchantment.Rarity.UNCOMMON
            Rare -> net.minecraft.enchantment.Enchantment.Rarity.RARE
            else -> net.minecraft.enchantment.Enchantment.Rarity.VERY_RARE
        }

    operator fun compareTo(other: net.minecraft.util.Rarity) =
        ordinal.compareTo(other.ordinal)

    companion object {
        fun fromItemRarity(rarity: net.minecraft.util.Rarity) = when (rarity) {
            net.minecraft.util.Rarity.COMMON -> Common
            net.minecraft.util.Rarity.UNCOMMON -> Uncommon
            net.minecraft.util.Rarity.RARE -> Rare
            net.minecraft.util.Rarity.EPIC -> Epic
        }

        fun fromEnchantmentRarity(rarity: net.minecraft.enchantment.Enchantment.Rarity) = when (rarity) {
            net.minecraft.enchantment.Enchantment.Rarity.COMMON -> Common
            net.minecraft.enchantment.Enchantment.Rarity.UNCOMMON -> Uncommon
            net.minecraft.enchantment.Enchantment.Rarity.RARE -> Rare
            net.minecraft.enchantment.Enchantment.Rarity.VERY_RARE -> Epic
        }
    }
}

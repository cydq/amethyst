package com.cynquil.amethyst.rarity

import net.minecraft.enchantment.Enchantment
import net.minecraft.util.Identifier

data class RarityData(
    val weight: Int,
    val item_rarity: String,
    val enchantment_rarity: String
) {
    fun build(id: Identifier): Rarity =
        Rarity(
            id,
            weight,
            net.minecraft.util.Rarity.valueOf(item_rarity.uppercase()),
            Enchantment.Rarity.valueOf(enchantment_rarity.uppercase())
        )
}

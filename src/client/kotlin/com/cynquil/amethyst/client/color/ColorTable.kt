package com.cynquil.amethyst.client.color

import com.cynquil.amethyst.attribute.Attribute
import com.cynquil.amethyst.attribute.Attributes
import com.cynquil.amethyst.rarity.Rarity
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import net.minecraft.util.Formatting

private val attributeTable = mapOf(
    Attributes.Health to Formatting.RED,
    Attributes.Speed to Formatting.WHITE,
    Attributes.Defense to Formatting.GREEN,
    Attributes.Ferocity to Formatting.RED,
    Attributes.Strength to Formatting.RED,
    Attributes.AttackSpeed to Formatting.YELLOW,
    Attributes.CriticalChance to Formatting.BLUE,
    Attributes.CriticalDamage to Formatting.BLUE,
    Attributes.Intelligence to Formatting.AQUA,
    Attributes.MagicFind to Formatting.AQUA
)

val Attribute.formatting: Formatting
    get() = attributeTable[this] ?: Formatting.RESET

val Attribute.style: Style
    get() = Style.EMPTY.withFormatting(formatting)

private val rarityTable = mapOf(
    Rarity.Common to Formatting.WHITE,
    Rarity.Uncommon to Formatting.GREEN,
    Rarity.Rare to Formatting.BLUE,
    Rarity.Epic to Formatting.DARK_PURPLE,
    Rarity.Legendary to Formatting.YELLOW,
    Rarity.Mythic to Formatting.LIGHT_PURPLE,
    Rarity.Special to Formatting.RED
)

val Rarity.color: TextColor
    get() = TextColor.fromFormatting(rarityTable[this] ?: Formatting.RESET)
        ?: TextColor.fromRgb(0)

val Rarity.style: Style
    get() = Style.EMPTY
        .withColor(rarityTable[this] ?: Formatting.RESET)
        .withBold(true)

val Rarity.tooltipText: Text
    get() = Text.translatable("rarity.${id.namespace}.${id.path}").setStyle(style)

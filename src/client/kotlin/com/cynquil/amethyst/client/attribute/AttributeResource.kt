package com.cynquil.amethyst.client.attribute

import kotlinx.serialization.Serializable
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting

@Serializable
class AttributeResource(
    private val icon: String,
    private val color: String,
) {
    val formatting: Formatting = Formatting.byName(color) ?: Formatting.RESET
    val symbol: Text = Text.literal(icon).formatted(formatting)

    val style: Style
        get() = Style.EMPTY.withFormatting(formatting).withBold(true)
}

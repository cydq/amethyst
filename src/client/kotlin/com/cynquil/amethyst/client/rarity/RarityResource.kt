package com.cynquil.amethyst.client.rarity

import kotlinx.serialization.Serializable
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting

@Serializable
class RarityResource(
    private val color: String,
) {
    val formatting: Formatting = Formatting.byName(color) ?: Formatting.RESET

    val style: Style
        get() = Style.EMPTY.withFormatting(formatting).withBold(true)
}

package com.cynquil.amethyst.client.rarity

import com.cynquil.amethyst.rarity.Rarity
import net.minecraft.text.Text
import net.minecraft.util.Identifier

internal val resourceMap = mutableMapOf<Identifier, RarityResource>()

val Rarity.resource
    get() = resourceMap[id] ?: RarityResource("reset")

val Rarity.tooltipText: Text
    get() = Text.translatable("rarity.${id.namespace}.${id.path}").setStyle(resource.style)

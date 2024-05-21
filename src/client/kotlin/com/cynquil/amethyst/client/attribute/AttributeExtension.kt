package com.cynquil.amethyst.client.attribute

import com.cynquil.amethyst.attribute.Attribute
import net.minecraft.util.Identifier

internal val resourceMap = mutableMapOf<Identifier, AttributeResource>()

val Attribute.resource
    get() = resourceMap[id] ?: AttributeResource("", "reset")

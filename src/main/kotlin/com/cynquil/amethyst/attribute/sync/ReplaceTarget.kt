package com.cynquil.amethyst.attribute.sync

import com.cynquil.amethyst.attribute.Attribute
import net.minecraft.entity.attribute.EntityAttribute

data class ReplaceTarget(
    val target: Attribute,
    val replaced: EntityAttribute,
    val transform: ((Double) -> Double)? = null
)

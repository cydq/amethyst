package com.cynquil.amethyst.attribute.sync

import com.cynquil.amethyst.attribute.Attribute
import net.minecraft.entity.attribute.EntityAttribute

data class SyncTarget(
    val target: Attribute,
    val synced: EntityAttribute,
    val syncBase: Boolean = false,
    private val transform: (Double, Double) -> Double
) {
    fun transform(value: Double, original: Double): Double =
        transform.invoke(value, original)
}

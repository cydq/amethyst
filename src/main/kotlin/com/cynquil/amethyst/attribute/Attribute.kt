package com.cynquil.amethyst.attribute

import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper

open class Attribute(
    val id: Identifier,
    val min: Double = Double.NEGATIVE_INFINITY,
    val max: Double = Double.POSITIVE_INFINITY,
    val default: Double = .0,
) : EntityAttribute("attribute.name.${id.namespace}.${id.path}", default) {
    init {
        assert(min <= default)
        assert(default <= max)

        super.setTracked(true)
    }

    fun sync(target: EntityAttribute, transform: (Double, Double) -> Double = { x, _ -> x }): Attribute {
        syncTable[target] = SyncTarget(this, transform)
        return this
    }

    fun register(): Attribute =
        Registry.register(Registries.ATTRIBUTE, id, this)

    override fun clamp(value: Double): Double =
        MathHelper.clamp(value, min, max)

    data class SyncTarget(val target: Attribute, private val transform: (Double, Double) -> Double) {
        fun transform(value: Double, original: Double): Double =
            transform.invoke(value, original)
    }

    companion object {
        private val syncTable = mutableMapOf<EntityAttribute, SyncTarget>()

        fun getSyncTarget(attribute: EntityAttribute): SyncTarget? =
            syncTable[attribute]
    }
}

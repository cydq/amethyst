package com.cynquil.amethyst.attribute

import com.cynquil.amethyst.attribute.sync.ReplaceTarget
import com.cynquil.amethyst.attribute.sync.SyncTarget
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
    private val syncedAttributeList = mutableListOf<EntityAttribute>()

    val syncedAttributes: Set<EntityAttribute>
        get() = syncedAttributeList.toSet()

    var replaces: ReplaceTarget? = null
        private set

    init {
        assert(min <= default)
        assert(default <= max)

        super.setTracked(true)
    }

    fun sync(
        target: EntityAttribute,
        syncBase: Boolean = false,
        transform: (Double, Double) -> Double = { x, _ -> x }
    ): Attribute {
        syncTable[target] = SyncTarget(this, target, syncBase, transform)
        syncedAttributeList.add(target)
        return this
    }

    fun replace(target: EntityAttribute, transform: ((Double) -> Double)? = null): Attribute {
        val replaceTarget = ReplaceTarget(this, target, transform)
        replaces = replaceTarget
        replaceTable[target] = replaceTarget
        return this
    }

    fun register(): Attribute =
        Registry.register(Registries.ATTRIBUTE, id, this)

    override fun clamp(value: Double): Double =
        MathHelper.clamp(value, min, max)

    companion object {
        private val syncTable = mutableMapOf<EntityAttribute, SyncTarget>()
        private val replaceTable = mutableMapOf<EntityAttribute, ReplaceTarget>()

        @JvmStatic
        fun getSyncTarget(attribute: EntityAttribute): SyncTarget? =
            syncTable[attribute]

        @JvmStatic
        fun getReplaceTarget(attribute: EntityAttribute): ReplaceTarget? =
            replaceTable[attribute]
    }
}

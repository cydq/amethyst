package com.cynquil.amethyst.attribute

import com.cynquil.amethyst.AmRegistries
import com.cynquil.amethyst.attribute.sync.ReplaceTarget
import com.cynquil.amethyst.attribute.sync.SyncTarget
import com.cynquil.amethyst.mod.Registrable
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper

data class Attribute(
    val id: Identifier,
    val min: Double = Double.NEGATIVE_INFINITY,
    val max: Double = Double.POSITIVE_INFINITY,
    val default: Double = .0,
    val hidden: Boolean = false,
) : EntityAttribute("attribute.name.${id.namespace}.${id.path}", default), Registrable {
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
        transform: ((Double, Double) -> Double)? = null
    ): Attribute {
        syncTable[target] = SyncTarget(this, target, syncBase, transform ?: { x, _ -> x })
        syncedAttributeList.add(target)
        return this
    }

    fun replace(target: EntityAttribute, transform: ((Double) -> Double)? = null): Attribute {
        val replaceTarget = ReplaceTarget(this, target, transform)
        replaces = replaceTarget
        replaceTable[target] = replaceTarget
        return this
    }

    override fun register() {
        Registry.register(Registries.ATTRIBUTE, id, this)
        Registry.register(AmRegistries.Attribute, id, this)
    }

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

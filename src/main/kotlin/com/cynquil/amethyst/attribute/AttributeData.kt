package com.cynquil.amethyst.attribute

import com.cynquil.amethyst.extensions.id
import kotlinx.serialization.Serializable
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

@Serializable
data class AttributeData(
    val min: Double = Double.NEGATIVE_INFINITY,
    val default: Double = 0.0,
    val max: Double = Double.POSITIVE_INFINITY,
    val hidden: Boolean = false,
    val sync: List<Sync>?
) {
    private val syncTransforms = mapOf(
        "factor" to { v: Double, o: Double -> o * v / 100 },
        null to null,
    )

    private val replaceTransforms = mapOf(
        "replace_health" to { v: Double -> v * 5 },
        null to null,
    )

    fun build(id: Identifier): Attribute {
        val attribute = Attribute(id, min, max, default)

        for (entry in sync ?: emptyList()) {
            val target = Registries.ATTRIBUTE.get(entry.attribute.id) ?: continue

            if (entry.type == "sync")
                attribute.sync(target, transform = syncTransforms[entry.transform])

            if (entry.type == "replace")
                attribute.replace(target, transform = replaceTransforms[entry.transform])
        }

        return attribute
    }

    @Serializable
    data class Sync(
       val type: String,
       val attribute: String,
       val transform: String?
    )
}

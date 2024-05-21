package com.cynquil.amethyst.client.attribute

import com.cynquil.amethyst.extensions.id
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.minecraft.resource.ResourceManager
import kotlin.text.endsWith

class AttributeResourceListener : SimpleSynchronousResourceReloadListener {
    override fun getFabricId() = "amethyst:attribute".id

    @OptIn(ExperimentalSerializationApi::class)
    override fun reload(manager: ResourceManager?) {
        resourceMap.clear()

        for (resource in manager?.findResources("attributes") { it.path.endsWith(".json") } ?: return)
            resourceMap.put(resource.key, Json.decodeFromStream<AttributeResource>(resource.value.inputStream))
    }
}

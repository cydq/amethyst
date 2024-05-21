package com.cynquil.amethyst.rarity

import com.cynquil.amethyst.extensions.id
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.minecraft.resource.ResourceManager
import kotlin.text.endsWith

class RarityResourceListener : SimpleSynchronousResourceReloadListener {
    override fun getFabricId() = "amethyst:rarity".id

    @OptIn(ExperimentalSerializationApi::class)
    override fun reload(manager: ResourceManager?) {
        for (resource in manager?.findResources("rarities") { it.path.endsWith(".json") } ?: return)
            Json.decodeFromStream<RarityData>(resource.value.inputStream)
                .build(resource.key)
                .register()
    }
}

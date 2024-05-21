package com.cynquil.amethyst.client

import com.cynquil.amethyst.client.attribute.AttributeResourceListener
import com.cynquil.amethyst.client.command.StatisticsCommand
import com.cynquil.amethyst.client.rarity.RarityResourceListener
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.resource.ResourceType

object AmethystClient : ClientModInitializer {
    override fun onInitializeClient() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(AttributeResourceListener())
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(RarityResourceListener())

        StatisticsCommand.register()
    }
}

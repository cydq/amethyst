package com.cynquil.amethyst.rarity

import com.cynquil.amethyst.mod.Registrable
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.resource.ResourceType

object RarityModule : Registrable {
    override fun register() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(RarityResourceListener())
    }
}

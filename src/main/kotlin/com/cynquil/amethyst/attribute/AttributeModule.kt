package com.cynquil.amethyst.attribute

import com.cynquil.amethyst.mod.Registrable
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.resource.ResourceType

object AttributeModule : Registrable {
    override fun register() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(AttributeResourceListener())
    }
}

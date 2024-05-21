package com.cynquil.amethyst

import com.cynquil.amethyst.attribute.AttributeModule
import com.cynquil.amethyst.effects.RespawnEffect
import com.cynquil.amethyst.rarity.RarityModule
import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

object Amethyst : ModInitializer {
    private val logger = LoggerFactory.getLogger("amethyst")

    override fun onInitialize() {
        // Registries
        AmRegistries

        // Modules
        AttributeModule.register()
        RarityModule.register()

        // Misc
        RespawnEffect.register()
    }
}

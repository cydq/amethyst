package com.cynquil.amethyst

import com.cynquil.amethyst.attribute.Attributes
import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

object Amethyst : ModInitializer {
    private val logger = LoggerFactory.getLogger("amethyst")

    override fun onInitialize() {
        Attributes

//        EntityAttributes
        logger.info("Hello Fabric world!")
    }
}

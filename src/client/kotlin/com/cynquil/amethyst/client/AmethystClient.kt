package com.cynquil.amethyst.client

import com.cynquil.amethyst.client.command.StatisticsCommand
import net.fabricmc.api.ClientModInitializer

object AmethystClient : ClientModInitializer {
    override fun onInitializeClient() {
        StatisticsCommand.register()
    }
}

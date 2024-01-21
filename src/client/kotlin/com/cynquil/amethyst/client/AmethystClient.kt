package com.cynquil.amethyst.client

import com.cynquil.amethyst.attribute.Attributes
import com.cynquil.amethyst.client.color.style
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text
import net.minecraft.util.Formatting

object AmethystClient : ClientModInitializer {
    override fun onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT
            .register { dispatcher, _ ->
                dispatcher.register(
                    ClientCommandManager
                        .literal("stats")
                        .executes { context ->
                            Attributes.values().forEach {
                                val value = context.source.player.getAttributeValue(it)

                                val icon = Text.translatable("attribute.symbol.${it.id.namespace}.${it.id.path}").setStyle(it.style)
                                val name = Text.translatable(it.translationKey).setStyle(it.style)

                                val display = Text.empty()
                                    .append(icon)
                                    .append(Text.literal(" "))
                                    .append(name)
                                    .append(
                                        Text.literal(": ")
                                            .styled { s -> s.withColor(Formatting.DARK_GRAY) }
                                    )
                                    .append(
                                        Text.literal(value.toString())
                                            .styled { s -> s.withColor(Formatting.GRAY) }
                                    )

                                context.source.sendFeedback(display)
                            }

                            1
                        }
                )
            }
    }
}

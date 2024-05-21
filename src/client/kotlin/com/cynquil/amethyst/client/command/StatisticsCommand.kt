package com.cynquil.amethyst.client.command

import com.cynquil.amethyst.attribute.Attribute
import com.cynquil.amethyst.attribute.Attributes
import com.cynquil.amethyst.client.attribute.resource
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.command.CommandSource
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.registry.Registries
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import java.util.concurrent.CompletableFuture

object StatisticsCommand {
    private val noAttributeException = SimpleCommandExceptionType(Text.literal("Could not find specified attribute!"))

    fun register() {
        ClientCommandRegistrationCallback.EVENT
            .register { dispatcher, _ ->
                dispatcher.register(
                    ClientCommandManager
                        .literal("attr")
                        .executes { context ->
                            Attributes.values().forEach {
                                val value = context.source.player.getAttributeValue(it)
                                context.source.sendFeedback(attributeOverview(it, value))
                            }

                            1
                        }
                        .then(
                            ClientCommandManager
                                .argument("stat", StringArgumentType.word())
                                .suggests(AttributeSuggestionProvider())
                                .executes { context ->
                                    val stat = StringArgumentType.getString(context, "stat")
                                    val attribute = Registries.ATTRIBUTE.get(Identifier("amethyst", stat)) as? Attribute
                                        ?: throw noAttributeException.create()

                                    val value = context.source.player.getAttributeInstance(attribute)
                                        ?: throw noAttributeException.create()

                                    context.source.sendFeedback(attributeOverview(attribute, value.value))

                                    value.modifiers.forEach {
                                        context.source.sendFeedback(attributeModifier(it))
                                    }

                                    1
                                }
                        )
                )
            }
    }

    private fun attributeOverview(attribute: Attribute, value: Double): Text {
        val icon = Text.translatable("attribute.symbol.${attribute.id.namespace}.${attribute.id.path}").setStyle(attribute.resource.style)
        val name = Text.translatable(attribute.translationKey).setStyle(attribute.resource.style)

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

        return display
    }

    private fun attributeModifier(modifier: EntityAttributeModifier): Text {
        val display = Text.empty()
            .append(
                Text.literal(modifier.name)
                    .styled { s -> s.withColor(Formatting.DARK_GRAY) }
            )
            .append(
                Text.literal(": ")
                    .styled { s -> s.withColor(Formatting.DARK_GRAY) }
            )
            .append(
                Text.literal(modifier.value.toString())
                    .styled { s -> s.withColor(Formatting.GRAY) }
            )
            .append(
                Text.literal(" (Operation ${modifier.operation})")
                    .styled { s -> s.withColor(Formatting.DARK_GRAY) }
            )

        return display
    }

    class AttributeSuggestionProvider : SuggestionProvider<FabricClientCommandSource> {
        override fun getSuggestions(
            context: CommandContext<FabricClientCommandSource>?,
            builder: SuggestionsBuilder?
        ): CompletableFuture<Suggestions> {
            if (builder == null)
                return Suggestions.empty()

            for (stat in Attributes.allValues())
                if (CommandSource.shouldSuggest(builder.remaining, stat.id.path))
                    builder.suggest(stat.id.path)

            return builder.buildFuture()
        }
    }
}

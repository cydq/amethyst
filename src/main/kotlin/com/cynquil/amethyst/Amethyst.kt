package com.cynquil.amethyst

import com.cynquil.amethyst.attribute.Attributes
import com.cynquil.amethyst.effects.RespawnEffect
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.network.ServerPlayerEntity
import org.slf4j.LoggerFactory

object Amethyst : ModInitializer {
    private val logger = LoggerFactory.getLogger("amethyst")

    override fun onInitialize() {
        Attributes

        RespawnEffect.register()
    }
}

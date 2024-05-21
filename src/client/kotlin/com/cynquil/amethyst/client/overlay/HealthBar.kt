package com.cynquil.amethyst.client.overlay

import com.cynquil.amethyst.extensions.id
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import kotlin.math.abs
import kotlin.math.ceil

// https://github.com/Lanfix8/SimpleHealthBar-Fabric/blob/main/src/main/java/fr/lanfix/simplehealthbar/overlays/HealthBar.java
// commit d1c79b7
object HealthBar {
    private val mc = MinecraftClient.getInstance()

    private val fullHealthBar = "amethyst:textures/gui/healthbars/full.png".id
    private val witherHealthBar = "amethyst:textures/gui/healthbars/wither.png".id
    private val poisonHealthBar = "amethyst:textures/gui/healthbars/poison.png".id
    private val frozenHealthBar = "amethyst:textures/gui/healthbars/frozen.png".id
    private val intermediateHealthBar = "amethyst:textures/gui/healthbars/intermediate.png".id
    private val emptyHealthBar = "amethyst:textures/gui/healthbars/empty.png".id
    private val absorptionBar = "amethyst:textures/gui/healthbars/absorption.png".id
    private val heartContainer = "minecraft:textures/gui/sprites/hud/heart/container.png".id
    private val absorptionHeart = "minecraft:textures/gui/sprites/hud/heart/absorbing_full.png".id

    private var currentBar = fullHealthBar
    private var intermediateHealth = 0.0

    fun render(context: DrawContext, tickDelta: Float) {
        val player = mc.cameraEntity
        if (player !is PlayerEntity || mc.options.hudHidden || mc.interactionManager?.hasStatusBars() != true) return

        val width = mc.window.scaledWidth
        val height = mc.window.scaledHeight

        val x = width.toFloat() / 2 - 91
        val y = (height - 39).toFloat()

        updateBarTextures(player)

        renderHealthValue(mc.textRenderer, context, x.toInt(), y.toInt(), player)
        renderHealthBar(context, tickDelta, x, y, player)

        if (player.absorptionAmount > 0) {
            renderAbsorptionValue(mc.textRenderer, context, x.toInt(), y.toInt(), player)
            renderAbsorptionBar(context, x, y, player)
        }
    }

    private fun updateBarTextures(player: PlayerEntity) {
        currentBar = when {
            player.hasStatusEffect(StatusEffects.WITHER) -> witherHealthBar
            player.hasStatusEffect(StatusEffects.POISON) -> poisonHealthBar
            player.isFrozen -> frozenHealthBar
            else -> fullHealthBar
        }
    }

    private fun renderHealthValue(
        textRenderer: TextRenderer,
        context: DrawContext,
        x: Int,
        y: Int,
        player: PlayerEntity
    ) {
        val health = ceil(player.health).toInt()
//        val text = health.toString() + "/" + player.maxHealth.toInt()
        val text = health.toString()
        context.drawText(textRenderer, text, x - textRenderer.getWidth(text) - 6, y + 1, 0xFF0000, false)
    }

    @Suppress("DuplicatedCode")
    private fun renderHealthBar(context: DrawContext, tickDelta: Float, x: Float, y: Float, player: PlayerEntity) {
        val health = player.health
        val maxHealth = player.maxHealth
        // Calculate bar proportions
        var healthProportion: Double
        var intermediateProportion: Double
        if (health < intermediateHealth) {
            healthProportion = (health / maxHealth).toDouble()
            intermediateProportion = (intermediateHealth - health) / maxHealth
        } else {
            healthProportion = intermediateHealth / maxHealth
            intermediateProportion = 0.0
        }
        if (healthProportion > 1) healthProportion = 1.0
        if (healthProportion + intermediateProportion > 1) intermediateProportion = 1 - healthProportion
        val healthWidth = ceil(80 * healthProportion).toInt()
        val intermediateWidth = ceil(80 * intermediateProportion).toInt()
        // Display full part
        context.drawTexture(
            currentBar,
            x.toInt(), y.toInt(),
            0f, 0f,
            healthWidth, 9,
            80, 9
        )
        // Display intermediate part
        context.drawTexture(
            intermediateHealthBar,
            x.toInt() + healthWidth, y.toInt(),
            healthWidth.toFloat(), 0f,
            intermediateWidth, 9,
            80, 9
        )
        // Display empty part
        context.drawTexture(
            emptyHealthBar,
            x.toInt() + healthWidth + intermediateWidth, y.toInt(),
            (healthWidth + intermediateWidth).toFloat(), 0f,
            80 - healthWidth - intermediateWidth, 9,
            80, 9
        )
        // Update intermediate health
        this.intermediateHealth += (health - intermediateHealth) * tickDelta * 0.08
        if (abs(health - intermediateHealth) <= 0.25) {
            this.intermediateHealth = health.toDouble()
        }
    }

    private fun renderAbsorptionValue(
        textRenderer: TextRenderer,
        context: DrawContext,
        x: Int,
        y: Int,
        player: PlayerEntity
    ) {
        val absorption = ceil(player.absorptionAmount.toDouble())
        var text = (absorption / 2).toString()
        text = text.replace(".0", "")
        context.drawText(textRenderer, text, x - textRenderer.getWidth(text) - 16, y - 9, 0xFFFF00, false)
        // blit heart container
        context.drawTexture(
            heartContainer,
            x - 16, y - 10,
            0f, 0f,
            9, 9,
            9, 9
        )
        // blit heart
        context.setShaderColor(127f, 127f, 0f, 0.5f)
        context.drawTexture(
            absorptionHeart,
            x - 16, y - 10,
            0f, 0f,
            9, 9,
            9, 9
        )
        context.setShaderColor(1f, 1f, 1f, 1f)
    }

    private fun renderAbsorptionBar(context: DrawContext, x: Float, y: Float, player: PlayerEntity) {
        val absorption = player.absorptionAmount
        val maxHealth = player.maxHealth
        // Calculate bar proportions
        var absorptionProportion = absorption / maxHealth
        if (absorptionProportion > 1) absorptionProportion = 1f
        val absorptionWidth = ceil((80 * absorptionProportion).toDouble()).toInt()
        // Display full part
        context.drawTexture(
            absorptionBar,
            x.toInt(), y.toInt() - 10,
            0f, 0f,
            absorptionWidth, 9,
            80, 9
        )
        // Display empty part
        context.drawTexture(
            emptyHealthBar,
            x.toInt() + absorptionWidth, y.toInt() - 10,
            absorptionWidth.toFloat(), 0f,
            80 - absorptionWidth, 9,
            80, 9
        )
    }
}

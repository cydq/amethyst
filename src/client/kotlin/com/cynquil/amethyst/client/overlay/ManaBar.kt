package com.cynquil.amethyst.client.overlay


import com.cynquil.amethyst.extensions.mana
import com.cynquil.amethyst.extensions.maxMana
import com.cynquil.amethyst.magic.ManaHolder
import com.cynquil.amethyst.model.LivingEntityMana
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Identifier
import kotlin.math.abs
import kotlin.math.ceil
object ManaBar {
    private val mc = MinecraftClient.getInstance()

    private val fullManaBar = Identifier("amethyst", "textures/gui/manabars/full.png")
    private val intermediateHealthBar = Identifier("amethyst", "textures/gui/manabars/intermediate.png")
    private val emptyManaBar = Identifier("amethyst", "textures/gui/manabars/empty.png")

    private var intermediateMana = 0.0

    fun render(context: DrawContext, tickDelta: Float) {
        val player = mc.cameraEntity
        if (player !is PlayerEntity || mc.options.hudHidden || mc.interactionManager?.hasStatusBars() != true) return

        val width = mc.window.scaledWidth
        val height = mc.window.scaledHeight

        val x = width.toFloat() / 2 + 91 - 80
        val y = (height - 39).toFloat()

        renderManaValue(mc.textRenderer, context, x.toInt(), y.toInt(), player)
        renderManaBar(context, tickDelta, x, y, player)
    }

    private fun renderManaValue(
        textRenderer: TextRenderer,
        context: DrawContext,
        x: Int,
        y: Int,
        player: PlayerEntity
    ) {
        val mana = ceil(player.mana).toInt()
//        val text = health.toString() + "/" + player.maxMana.toInt()
        val text = mana.toString()
        context.drawText(textRenderer, text, x + 80 + 6, y + 1, 0x0000FF, false)
    }

    @Suppress("DuplicatedCode")
    private fun renderManaBar(context: DrawContext, tickDelta: Float, x: Float, y: Float, player: PlayerEntity) {
        val mana = player.mana
        val maxMana = player.maxMana

        // Calculate bar proportions
        var healthProportion: Double
        var intermediateProportion: Double
        if (mana < intermediateMana) {
            healthProportion = (mana / maxMana).toDouble()
            intermediateProportion = (intermediateMana - mana) / maxMana
        } else {
            healthProportion = intermediateMana / maxMana
            intermediateProportion = 0.0
        }
        if (healthProportion > 1) healthProportion = 1.0
        if (healthProportion + intermediateProportion > 1) intermediateProportion = 1 - healthProportion
        val healthWidth = ceil(80 * healthProportion).toInt()
        val intermediateWidth = ceil(80 * intermediateProportion).toInt()
        // Display full part
        context.drawTexture(
            fullManaBar,
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
            emptyManaBar,
            x.toInt() + healthWidth + intermediateWidth, y.toInt(),
            (healthWidth + intermediateWidth).toFloat(), 0f,
            80 - healthWidth - intermediateWidth, 9,
            80, 9
        )
        // Update intermediate health
        this.intermediateMana += (mana - intermediateMana) * tickDelta * 0.08
        if (abs(mana - intermediateMana) <= 0.25) {
            this.intermediateMana = mana.toDouble()
        }
    }
}

package com.cynquil.amethyst.ability

import com.cynquil.amethyst.AmRegistries
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registry
import net.minecraft.text.Text
import net.minecraft.util.Identifier

abstract class Ability(val id: Identifier) {
    val name: Text
        get() = Text.translatable(id.toTranslationKey("ability"))

    val description: Text
        get() = Text.translatable(id.toTranslationKey("ability.description"))

    open fun manaCost(entity: PlayerEntity): Float = 0f

    open fun canUse(entity: PlayerEntity): Boolean = true

    open fun onUse(entity: PlayerEntity, item: ItemStack) {}

    fun register() {
        Registry.register(AmRegistries.Ability, id, this)
    }
}

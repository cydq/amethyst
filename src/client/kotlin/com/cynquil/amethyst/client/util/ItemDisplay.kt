package com.cynquil.amethyst.client.util

import com.cynquil.amethyst.AmRegistries
import com.cynquil.amethyst.attribute.Attribute
import com.cynquil.amethyst.client.rarity.resource
import com.cynquil.amethyst.client.rarity.tooltipText
import com.cynquil.amethyst.extensions.id
import com.cynquil.amethyst.rarity.HasRarity
import com.cynquil.amethyst.rarity.Rarity
import com.ibm.icu.text.DecimalFormat
import net.minecraft.client.item.TooltipContext
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.*
import net.minecraft.nbt.NbtElement
import net.minecraft.registry.Registries
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import kotlin.math.abs

object ItemDisplay {
    @JvmStatic
    fun getName(stack: ItemStack): Text {
        val item = stack.item
        val rarity = determineRarity(stack)

        val nbt = stack.getSubNbt(ItemStack.DISPLAY_KEY)
        if (nbt != null && nbt.contains(ItemStack.NAME_KEY, NbtElement.STRING_TYPE.toInt())) {
            try {
                val text = Text.Serialization.fromJson(nbt.getString(ItemStack.NAME_KEY))
                if (text != null) return text.copyContentOnly().setStyle(rarity.resource.style)

                nbt.remove(ItemStack.NAME_KEY)
            } catch (_: Exception) {
                nbt.remove(ItemStack.NAME_KEY)
            }
        }

        return item
            .getName(stack)
            .copyContentOnly()
            .styled { it.withColor(rarity.resource.style.color) }
//            .setStyle(if (stack.hasCustomName()) rarity.secondaryStyle else rarity.style)
    }

    @JvmStatic
    fun getTooltip(stack: ItemStack, player: PlayerEntity?, context: TooltipContext, advanced: Boolean = false): List<Text> {
        val list = mutableListOf<Text>()

        val item = stack.item
        val nbt = if (stack.hasNbt()) stack.nbt else null
        val flags: Int = stack.hideFlags

        val display =
            if (nbt?.contains("display", NbtElement.COMPOUND_TYPE.toInt()) == true) nbt.getCompound("display")
            else null

        // NAME
        val rarity = determineRarity(stack)
        list.add(stack.name.copy().styled { it.withColor(rarity.resource.style.color) })
//        list.add(stack.name.copy().setStyle(if (stack.hasCustomName()) rarity.secondaryStyle else rarity.style))

        // MAP ID SECTION
        if (!stack.hasCustomName() && stack.isOf(Items.FILLED_MAP)) {
            val integer = FilledMapItem.getMapId(stack)

            if (integer != null) {
                list.add(Text.literal("#$integer").formatted(Formatting.DARK_GRAY))
                list.add(Text.empty())
            }
        }

        // MODIFIER SECTION
        if (ItemStack.isSectionVisible(flags, ItemStack.TooltipSection.MODIFIERS)) {
            val slots = EquipmentSlot.entries.toTypedArray()

            for (equipmentSlot in slots) {
                val multimap = stack.getAttributeModifiers(equipmentSlot)
                if (multimap.isEmpty) continue

                multimap.entries().forEach { (key, value) ->
                    val modifier = value as EntityAttributeModifier
                    val modifierValue = modifier.value

                    if (key !is Attribute)
                        return@forEach

                    val amount = when (modifier.operation) {
                        EntityAttributeModifier.Operation.MULTIPLY_BASE -> modifierValue * 100.0
                        EntityAttributeModifier.Operation.MULTIPLY_TOTAL -> 1 + modifierValue
                        else -> modifierValue
                    }

                    val amountFmt = ItemStack.MODIFIER_FORMAT.format(abs(amount))
                    val decimalFmt = DecimalFormat("#.00").format(amount)
                    val sign = if (amount >= 0) "+" else "-"

                    list.add(
                        Text.empty()
                            .append(Text.translatable((key as EntityAttribute).translationKey).formatted(Formatting.GRAY))
                            .append(Text.literal(": ").formatted(Formatting.GRAY))
                            .append(
                                Text.literal(
                                    when (modifier.operation.id) {
                                        0 -> "$sign$amountFmt"
                                        1 -> "$sign$amountFmt%"
                                        2 -> "x$decimalFmt"
                                        else -> amountFmt
                                    }
                                ).formatted(Formatting.BLUE)
                            )
                    )
                }

                list.add(Text.empty())
            }
        }

        // ITEM TOOLTIP
        val added = mutableListOf<Text>()

        item.appendTooltip(stack, player?.world, added, context)
        list.addAll(added)
        list.add(Text.empty())

        // LORE SECTION
//        val lore =
//            if (display?.getType("Lore") == NbtElement.LIST_TYPE) {
//                val lore = display.getList("Lore", NbtElement.STRING_TYPE.toInt())
//
//                (0 until lore.size).mapNotNull {
//                    try {
//                        Text.Serializer.fromJson(lore.getString(it))?.string
//                    } catch (e: Exception) {
//                        display.remove("Lore")
//                        null
//                    }
//                }
//            }
//            else if (item is MaybeHasLore) item.lore
//            else null
//
//        if (lore != null) {
//            list.addAll(lore.map { Text.literal(it).formatted(Formatting.DARK_GRAY) })
//            list.add(Text.empty())
//        }

        // ENCHANTMENT
        if (nbt != null && ItemStack.isSectionVisible(flags, ItemStack.TooltipSection.ENCHANTMENTS)) {
            list.addAll(
                stack.enchantments.indices
                    .asSequence()
                    .map { stack.enchantments.getCompound(it) }
                    .map { it to Registries.ENCHANTMENT.get(EnchantmentHelper.getIdFromNbt(it)) }
                    .map { (compound, e) -> e?.getName(EnchantmentHelper.getLevelFromNbt(compound)) }
                    .filterNotNull()
                    .map { it.copy().formatted(Formatting.GRAY) }
                    .sortedBy { it.string }
            )

            list.add(Text.empty())
        }

        // UNBREAKABLE FLAG
        if (nbt != null && ItemStack.isSectionVisible(flags, ItemStack.TooltipSection.UNBREAKABLE) && nbt.getBoolean("Unbreakable"))
            list.add(Text.translatable("item.unbreakable").formatted(Formatting.RED))

        // RARITY FOOTER
        list.add(Text.empty())
        list.add(rarity.tooltipText)

        // Minecraft default advanced menu
        if (context.isAdvanced) {
            list.add(Text.empty())

            if (stack.isDamaged)
                list.add(Text.translatable("item.durability", stack.maxDamage - stack.damage, stack.maxDamage))

            list.add(Text.literal(Registries.ITEM.getId(stack.item).toString()).formatted(Formatting.DARK_GRAY))

            if (stack.hasNbt())
                list.add(Text.translatable("item.nbt_tags", stack.nbt?.keys?.size ?: 0).formatted(Formatting.DARK_GRAY))
        }

        // What the fuck?
        if (player != null && !stack.item.isEnabled(player.world.enabledFeatures))
            list.add(Text.literal("DISABLED").formatted(Formatting.RED, Formatting.BOLD))

        if (list.isNotEmpty())
            for (i in list.size - 1 downTo 1)
                if (list[i].string.isBlank() && list[i - 1].string.isBlank())
                    list.removeAt(i)

        return list
    }

    private fun determineRarity(stack: ItemStack): Rarity {
        val item = stack.item

        if (item is HasRarity)
            return item.rarity

        if (item is EnchantedBookItem) {
            val enchants = EnchantedBookItem.getEnchantmentNbt(stack)

            return enchants.indices
                .asSequence()
                .map { enchants.getCompound(it) }
                .map { Registries.ENCHANTMENT.get(EnchantmentHelper.getIdFromNbt(it)) }
                .mapNotNull { it as? HasRarity }
                .map { it.rarity }
                .maxByOrNull { it } ?: AmRegistries.Rarity.get("amethyst:common".id)!!
        }

        // Base Item
        val base = Rarity.fromItemRarity(stack.rarity)

        val enchantmentRarity = stack.enchantments.indices
            .asSequence()
            .map { Registries.ENCHANTMENT.get(EnchantmentHelper.getIdFromNbt(stack.enchantments.getCompound(it))) }
            .mapNotNull { it as? HasRarity }
            .map { it.rarity }
            .maxByOrNull { it } ?: AmRegistries.Rarity.get("amethyst:common".id)!!

        if (enchantmentRarity > base)
            return enchantmentRarity

        return base
    }
}

package com.cynquil.amethyst.mixin.vanilla;

import com.cynquil.amethyst.AmRegistries;
import com.cynquil.amethyst.attribute.Attributes;
import com.google.common.collect.ImmutableMultimap;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(MiningToolItem.class)
public abstract class MixinMiningToolItem {
    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMultimap$Builder;build()Lcom/google/common/collect/ImmutableMultimap;"))
    private void init(
        float attackDamage,
        float attackSpeed,
        ToolMaterial material,
        TagKey<?> effectiveBlocks,
        Item.Settings settings,
        CallbackInfo ci,
        @Local ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder
    ) {
        builder.put(
            Attributes.getDamage(),
            new EntityAttributeModifier(
                Item.ATTACK_DAMAGE_MODIFIER_ID,
                "Tool modifier",
                Math.floor(((MiningToolItem)(Object)this).getAttackDamage()) * 5f,
                EntityAttributeModifier.Operation.ADDITION
            )
        );
    }
}

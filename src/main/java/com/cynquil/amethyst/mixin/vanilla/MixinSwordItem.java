package com.cynquil.amethyst.mixin.vanilla;

import com.cynquil.amethyst.AmRegistries;
import com.cynquil.amethyst.attribute.Attributes;
import com.google.common.collect.ImmutableMultimap;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(SwordItem.class)
public abstract class MixinSwordItem {
    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMultimap$Builder;build()Lcom/google/common/collect/ImmutableMultimap;"))
    private void init(
        ToolMaterial toolMaterial,
        int attackDamage,
        float attackSpeed,
        Item.Settings settings,
        CallbackInfo ci,
        @Local ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder
    ) {
        builder.put(
            Attributes.getDamage(),
            new EntityAttributeModifier(
                Item.ATTACK_DAMAGE_MODIFIER_ID,
                "Weapon modifier",
                ((SwordItem)(Object)this).getAttackDamage() * 5f,
                EntityAttributeModifier.Operation.ADDITION
            )
        );
    }
}

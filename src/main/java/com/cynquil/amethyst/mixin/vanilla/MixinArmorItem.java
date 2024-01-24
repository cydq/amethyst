package com.cynquil.amethyst.mixin.vanilla;

import com.cynquil.amethyst.attribute.Attributes;
import com.google.common.collect.ImmutableMultimap;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;

@Mixin(ArmorItem.class)
public abstract class MixinArmorItem {
    @Unique
    private static final Map<ArmorMaterial, Double> defenseMap = Map.of(
        ArmorMaterials.TURTLE, 5.0,
        ArmorMaterials.LEATHER, 12.0 / 4,
        ArmorMaterials.IRON, 24.0 / 4,
        ArmorMaterials.CHAIN, 32.0 / 4,
        ArmorMaterials.GOLD, 40.0 / 4,
        ArmorMaterials.DIAMOND, 60.0 / 4,
        ArmorMaterials.NETHERITE, 100.0 / 4
    );

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMultimap$Builder;put(Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableMultimap$Builder;"))
    private void init(
        ArmorMaterial material,
        ArmorItem.Type type,
        Item.Settings settings,
        CallbackInfo ci,
        @Local UUID uuid,
        @Local ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder
    ) {
        builder.put(
            Attributes.INSTANCE.getDefense(),
            new EntityAttributeModifier(
                uuid,
                "Armor modifier",
                defenseMap.get(material),
                EntityAttributeModifier.Operation.ADDITION
            )
        );
    }
}

package com.cynquil.amethyst.mixin.attribute;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MixinAttributeHealth {
    @Inject(method = "getMaxHealth", at = @At("HEAD"), cancellable = true)
    public void getMaxHealth(CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue((float)((LivingEntity)(Object)this).getAttributeValue(
            Registries.ATTRIBUTE.get(new Identifier("amethyst", "health"))
        ));
    }

    @ModifyVariable(method = "updateAttribute", at = @At("HEAD"), argsOnly = true)
    public EntityAttribute updateAttribute(EntityAttribute value) {
        return value == EntityAttributes.GENERIC_MAX_HEALTH
            ? Registries.ATTRIBUTE.get(new Identifier("amethyst", "health"))
            : value;
    }
}

package com.cynquil.mixin;

import com.cynquil.amethyst.attribute.Attributes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class MixinGenericAttributes {
    @Inject(at = @At("RETURN"), method = "createLivingAttributes", cancellable = true)
    private static void createLivingAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
        var attributes = cir.getReturnValue();

        for (var attribute : Attributes.INSTANCE.values()) {
            attributes = attributes.add(attribute);
        }

        cir.setReturnValue(attributes);
    }
}

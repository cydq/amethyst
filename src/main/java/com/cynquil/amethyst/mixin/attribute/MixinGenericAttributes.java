package com.cynquil.amethyst.mixin.attribute;

import com.cynquil.amethyst.attribute.Attributes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MixinGenericAttributes {
    @Inject(at = @At("RETURN"), method = "createLivingAttributes", cancellable = true)
    private static void createLivingAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
        final var builder = cir.getReturnValue();

        for (var attribute : Attributes.allValues()) {
            var replaced = attribute.getReplaces();
            if (replaced == null) {
                builder.add(attribute);
                continue;
            }

            var replacedInstance = builder.instances.get(replaced.getReplaced());
            if (replacedInstance == null) continue;

            var transform = replaced.getTransform();

            if (transform == null)
                builder.add(attribute);
            else
                builder.add(attribute, transform.invoke(replacedInstance.getBaseValue()));

            builder.instances.remove(replaced.getReplaced());
        }

        cir.setReturnValue(builder);
    }
}

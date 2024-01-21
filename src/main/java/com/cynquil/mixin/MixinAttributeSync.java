package com.cynquil.mixin;

import com.cynquil.amethyst.attribute.Attribute;
import com.cynquil.amethyst.attribute.Attributes;
import com.cynquil.amethyst.attribute.compat.SyncedEntityAttributeInstance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(AttributeContainer.class)
public abstract class MixinAttributeSync {
    @Shadow @Final
    private DefaultAttributeContainer fallback;

    @Shadow @Final
    private Map<EntityAttribute, EntityAttributeInstance> custom;

    @Shadow
    protected abstract void updateTrackedStatus(EntityAttributeInstance instance);

    @Shadow @Nullable
    public abstract EntityAttributeInstance getCustomInstance(EntityAttribute attribute);


    @Inject(at = @At("HEAD"), method = "getCustomInstance(Lnet/minecraft/entity/attribute/EntityAttribute;)Lnet/minecraft/entity/attribute/EntityAttributeInstance;", cancellable = true)
    private void getCustomInstance(EntityAttribute attribute, CallbackInfoReturnable<EntityAttributeInstance> cir) {
        var target = Attribute.Companion.getSyncTarget(attribute);
        if (target == null) return;

        var targetInstance = this.getCustomInstance(target.getTarget());
        if (targetInstance == null) return;

        EntityAttributeInstance result = this.custom.computeIfAbsent(attribute, (attributex) -> {
            var copy = this.fallback.createOverride(this::updateTrackedStatus, attributex);
            if (copy == null) return null;

            var instance = new SyncedEntityAttributeInstance(attributex, this::updateTrackedStatus, targetInstance, target::transform);
            instance.setFrom(copy);
            return instance;
        });

        cir.setReturnValue(result);
    }
}

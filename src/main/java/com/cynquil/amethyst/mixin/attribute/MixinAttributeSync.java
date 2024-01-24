package com.cynquil.amethyst.mixin.attribute;

import com.cynquil.amethyst.attribute.Attribute;
import com.cynquil.amethyst.attribute.sync.SyncedEntityAttributeInstance;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.item.Items;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(AttributeContainer.class)
public abstract class MixinAttributeSync {
    @Shadow @Final
    private DefaultAttributeContainer fallback;

    @Shadow @Final
    private Map<EntityAttribute, EntityAttributeInstance> custom;

    @Inject(at = @At("HEAD"), method = "getCustomInstance(Lnet/minecraft/entity/attribute/EntityAttribute;)Lnet/minecraft/entity/attribute/EntityAttributeInstance;", cancellable = true)
    private void getCustomInstance(EntityAttribute attribute, CallbackInfoReturnable<EntityAttributeInstance> cir) {
        var replace = Attribute.getReplaceTarget(attribute);
        if (replace != null) {
            cir.setReturnValue(((AttributeContainer)(Object)this).getCustomInstance(replace.getTarget()));
            return;
        }

        var target = Attribute.getSyncTarget(attribute);
        if (target == null) return;

        var targetInstance = this.getCustomInstance(target.getTarget());
        if (targetInstance == null) return;

        EntityAttributeInstance result = this.custom.computeIfAbsent(attribute, (x) -> {
            var copy = this.fallback.createOverride(this::updateTrackedStatus, x);
            if (copy == null) return null;

            var instance = new SyncedEntityAttributeInstance(x, this::updateTrackedStatus, targetInstance, target);
            instance.setFrom(copy);

            return instance;
        });

        cir.setReturnValue(result);
    }

    @Inject(at = @At("HEAD"), method = "getValue", cancellable = true)
    private void getValue(EntityAttribute attribute, CallbackInfoReturnable<Double> cir) {
        var replace = Attribute.getReplaceTarget(attribute);
        if (replace != null) {
            cir.setReturnValue(((AttributeContainer)(Object)this).getValue(replace.getTarget()));
            return;
        }

        var target = Attribute.getSyncTarget(attribute);
        if (target == null) return;

        var instance = this.getCustomInstance(attribute);
        if (instance == null) return;

        cir.setReturnValue(instance.getValue());
    }

    @Inject(at = @At("HEAD"), method = "getBaseValue", cancellable = true)
    private void getBaseValue(EntityAttribute attribute, CallbackInfoReturnable<Double> cir) {
        var replace = Attribute.getReplaceTarget(attribute);
        if (replace != null) {
            cir.setReturnValue(((AttributeContainer)(Object)this).getBaseValue(replace.getTarget()));
            return;
        }

        var target = Attribute.getSyncTarget(attribute);
        if (target == null) return;

        var instance = this.getCustomInstance(attribute);
        if (instance == null) return;

        cir.setReturnValue(instance.getBaseValue());
    }

    @Shadow
    protected abstract void updateTrackedStatus(EntityAttributeInstance instance);

    @Shadow @Nullable
    public abstract EntityAttributeInstance getCustomInstance(EntityAttribute attribute);
}

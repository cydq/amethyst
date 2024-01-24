package com.cynquil.amethyst.mixin.combat;

import com.cynquil.amethyst.combat.MobDamage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public abstract class MixinMobAttack {
    @Inject(method = "tryAttack", at = @At("HEAD"), cancellable = true)
    public void tryAttack(Entity target, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(MobDamage.INSTANCE.attack((MobEntity)(Object)this, target));
    }
}

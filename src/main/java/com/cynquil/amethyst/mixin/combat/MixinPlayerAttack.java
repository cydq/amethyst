package com.cynquil.amethyst.mixin.combat;

import com.cynquil.amethyst.combat.PlayerDamage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerAttack {
    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    public void attack(Entity target, @NotNull CallbackInfo ci) {
        PlayerDamage.INSTANCE.attack((PlayerEntity)(Object)this, target);
        ci.cancel();
    }
}

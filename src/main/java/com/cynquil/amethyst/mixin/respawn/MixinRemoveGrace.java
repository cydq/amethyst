package com.cynquil.amethyst.mixin.respawn;

import com.cynquil.amethyst.effects.RespawnEffect;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class MixinRemoveGrace {
    @Inject(method = "damage", at = @At("RETURN"))
    private void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue())
            ((PlayerEntity)(Object)this).removeStatusEffect(RespawnEffect.INSTANCE);
    }
}

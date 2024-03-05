package com.cynquil.amethyst.mixin.vanilla;

import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HungerManager.class)
public abstract class MixinHungerHeal {
    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;heal(F)V"))
    private void heal(PlayerEntity instance, float v) {
        instance.heal(v * instance.getMaxHealth() / instance.defaultMaxHealth);
    }
}

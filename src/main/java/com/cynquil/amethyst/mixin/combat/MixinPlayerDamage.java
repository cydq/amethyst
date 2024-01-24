package com.cynquil.amethyst.mixin.combat;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerDamage {
    @ModifyVariable(method = "damageArmor", at = @At("HEAD"), argsOnly = true)
    public float damageArmor(float amount) {
        return amount / 5;
    }

    @ModifyVariable(method = "damageHelmet", at = @At("HEAD"), argsOnly = true)
    public float damageHelmet(float amount) {
        return amount / 5;
    }

    @ModifyVariable(method = "damageShield", at = @At("HEAD"), argsOnly = true)
    public float damageShield(float amount) {
        return amount / 5;
    }
}

package com.cynquil.amethyst.mixin.respawn;

import com.cynquil.amethyst.effects.RespawnEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerManager.class)
public abstract class MixinRespawnPlayer {
    @Redirect(method = "respawnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;setHealth(F)V"))
    public void respawnPlayer(ServerPlayerEntity player, float v) {
        player.addStatusEffect(new StatusEffectInstance(RespawnEffect.INSTANCE, 15));
    }
}

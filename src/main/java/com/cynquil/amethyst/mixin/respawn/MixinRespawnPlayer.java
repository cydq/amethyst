package com.cynquil.amethyst.mixin.respawn;

import com.cynquil.amethyst.effects.RespawnEffect;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

//@Mixin(PlayerManager.class)
@Mixin(LivingEntity.class)
public abstract class MixinRespawnPlayer {
    @Unique @Nullable
    private Float actualHealth = null;

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    private void readAdditionalSaveData(NbtCompound nbt, CallbackInfo ci) {
        if (!nbt.contains("Health", NbtElement.NUMBER_TYPE))
            return;

        final float savedHealth = nbt.getFloat("Health");

        if (savedHealth > getMaxHealth() && savedHealth > 0)
            actualHealth = savedHealth;
    }

    @Inject(method = "sendEquipmentChanges()V", at = @At("RETURN"))
    private void detectEquipmentUpdates(CallbackInfo ci) {
        if (actualHealth == null)
            return;

        if (actualHealth > 0 && actualHealth > getHealth())
            setHealth(actualHealth);

        actualHealth = null;
    }

    @Shadow public abstract float getMaxHealth();

    @Shadow public abstract float getHealth();

    @Shadow public abstract void setHealth(float health);

//    @Redirect(method = "respawnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;setHealth(F)V"))
//    public void respawnPlayer(ServerPlayerEntity player, float v) {
//        player.addStatusEffect(new StatusEffectInstance(RespawnEffect.INSTANCE, 15));
//    }
}

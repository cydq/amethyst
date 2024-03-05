package com.cynquil.amethyst.mixin.respawn;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class MixinSpawnPlayer {
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
}

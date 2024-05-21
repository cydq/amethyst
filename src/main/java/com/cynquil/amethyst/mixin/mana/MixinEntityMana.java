package com.cynquil.amethyst.mixin.mana;

import com.cynquil.amethyst.attribute.Attributes;
import com.cynquil.amethyst.model.LivingEntityMana;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
@SuppressWarnings("AddedMixinMembersNamePattern")
public abstract class MixinEntityMana implements LivingEntityMana {
    @Shadow
    public abstract double getAttributeValue(EntityAttribute attribute);

    @Unique
    private static final TrackedData<Float> MANA = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.FLOAT);

    @Inject(method = "<init>", at = @At("TAIL"))
    public void constructor(EntityType<?> entityType, World world, CallbackInfo ci) {
        setMana(getMaxMana());
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    public void initDataTracker(CallbackInfo ci) {
        ((LivingEntity)(Object)this).dataTracker.startTracking(MANA, 1.0F);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putFloat("Mana", getMana());
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("Health", 99)) {
            setMana(nbt.getFloat("Mana"));
        }
    }

    @Inject(method = "updateAttribute", at = @At("HEAD"))
    public void updateAttribute(EntityAttribute attribute, CallbackInfo ci) {
        if (attribute == Attributes.getIntelligence()) {
            float maxMana = getMaxMana();

            if (getMana() > maxMana) {
                setMana(maxMana);
            }
        }
    }

    @Override
    public float getMana() {
        return ((LivingEntity)(Object)this).dataTracker.get(MANA);
    }

    @Override
    public void setMana(float mana) {
        ((LivingEntity)(Object)this).dataTracker.set(MANA, MathHelper.clamp(mana, 0.0F, getMaxMana()));
    }

    @Override
    public float getMaxMana() {
        return 100f + (float)getAttributeValue(Attributes.getIntelligence());
    }

    @Override
    public void regenMana(float amount) {
        float mana = getMana();
        if (mana > 0.0F) setMana(mana + amount);
    }
}

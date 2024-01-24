package com.cynquil.amethyst.mixin.combat;

import com.cynquil.amethyst.combat.IncomingDamage;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MixinEntityDamage {
    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    public void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(IncomingDamage.INSTANCE.damage((LivingEntity)(Object)this, source, amount));
    }

    @Redirect(method = "applyDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;applyArmorToDamage(Lnet/minecraft/entity/damage/DamageSource;F)F"))
    public float applyArmorToDamage(LivingEntity instance, DamageSource source, float amount) {
        // Armor should not mitigate damage because that is handled by defense stat
        return amount;
    }

    @Redirect(method = "applyDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;modifyAppliedDamage(Lnet/minecraft/entity/damage/DamageSource;F)F"))
    public float modifyAppliedDamage(LivingEntity instance, DamageSource source, float amount) {
        return IncomingDamage.INSTANCE.modifyAppliedDamage(instance, source, amount);
    }
}

package com.cynquil.amethyst.mixin.client;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InGameHud.class)
public abstract class MixinItemHUD {
    @Redirect(method = "renderHeldItemTooltip", at =
    @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;hasCustomName()Z"))
    private boolean renderHeldItemTooltip(ItemStack stack) {
        return false;
    }
}

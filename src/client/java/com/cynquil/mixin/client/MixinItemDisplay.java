package com.cynquil.mixin.client;

import com.cynquil.amethyst.client.util.ItemDisplay;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class MixinItemDisplay {
    @Inject(at = @At("HEAD"), method = "getTooltip", cancellable = true)
    private void getTooltip(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir) {
        cir.setReturnValue(ItemDisplay.INSTANCE.getTooltip((ItemStack)(Object)this, player, context));
    }

    @Inject(at = @At("HEAD"), method = "getName", cancellable = true)
    private void getName(CallbackInfoReturnable<Text> cir) {
        cir.setReturnValue(ItemDisplay.INSTANCE.getName((ItemStack)(Object)this));
    }

    @Redirect(
        method = "toHoverableText()Lnet/minecraft/text/Text;",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;hasCustomName()Z")
    )
    private boolean straightenUpHoverText(ItemStack stack) {
        return false;
    }
}

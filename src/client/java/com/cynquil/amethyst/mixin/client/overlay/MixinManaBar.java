package com.cynquil.amethyst.mixin.client.overlay;

import com.cynquil.amethyst.client.overlay.ManaBar;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class MixinManaBar {
    @Shadow @Final private static Identifier FOOD_EMPTY_HUNGER_TEXTURE;
    @Shadow @Final private static Identifier FOOD_EMPTY_TEXTURE;
    @Shadow @Final private static Identifier FOOD_HALF_TEXTURE;
    @Shadow @Final private static Identifier FOOD_FULL_TEXTURE;
    @Unique
    private static final ManaBar manaBar = ManaBar.INSTANCE;

    @Inject(method = "render", at = @At(value = "HEAD"))
    public void renderHealthBar(DrawContext context, float tickDelta, CallbackInfo ci) {
        manaBar.render(context, tickDelta);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"), method = "renderStatusBars")
    public void moveHungerBar(DrawContext instance, Identifier texture, int x, int y, int width, int height) {
        if (texture == FOOD_EMPTY_HUNGER_TEXTURE || texture == FOOD_EMPTY_TEXTURE || texture == FOOD_HALF_TEXTURE || texture == FOOD_FULL_TEXTURE) {
            instance.drawGuiTexture(texture, x, y - 10, width, height);
            return;
        }

        instance.drawGuiTexture(texture, x, y, width, height);
    }

//    @ModifyVariable(at = @At(value = "STORE"), method = "renderStatusBars", ordinal = 15)
//    public int rowHeight(int value) {
//        return value - 10;
//    }

//    @Redirect(at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(FF)F"), method = "renderStatusBars")
//    public float fakeHealth(float a, float b) {
//        // The renderer should there is only one health row so the armor is displayed at the right place
//        return 20;
//    }
//
//    @Redirect(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getAbsorptionAmount()F"))
//    public float fakeAbsorption(PlayerEntity player) {
//        // The renderer should think there is only one absorption row if the player has absorption
//        // so the armor is displayed at the right place
//        return (player.getAbsorptionAmount() > 0) ? 20 : 0;
//    }
}

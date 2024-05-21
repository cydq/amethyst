package com.cynquil.amethyst.mixin.ability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class MixinItemAbility {
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void use(
        World world,
        PlayerEntity user,
        Hand hand,
        CallbackInfoReturnable<TypedActionResult<ItemStack>> cir
    ) {
        var stack = user.getStackInHand(hand);

        var ability = stack.getAbility();
        if (ability == null || !ability.canUse(user)) return;

        var cost = ability.manaCost(user);

        if (user.getMana() < cost) {
            user.sendMessage(Text.translatable("message.amethyst.not_enough_mana").formatted(Formatting.RED));

            cir.setReturnValue(TypedActionResult.fail(stack));
            return;
        }

        user.setMana(user.getMana() - cost);

        ability.onUse(user, stack);
        cir.setReturnValue(TypedActionResult.success(stack));
    }
}

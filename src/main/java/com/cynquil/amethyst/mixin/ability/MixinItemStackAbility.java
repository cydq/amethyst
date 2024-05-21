package com.cynquil.amethyst.mixin.ability;

import com.cynquil.amethyst.AmRegistries;
import com.cynquil.amethyst.ability.Ability;
import com.cynquil.amethyst.model.ItemStackAbility;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
@SuppressWarnings("AddedMixinMembersNamePattern")
public abstract class MixinItemStackAbility implements ItemStackAbility {
    @Shadow
    public abstract NbtCompound getOrCreateNbt();

    @Override
    public Ability getAbility() {
        var id = getOrCreateNbt().getString("ability");

        if (id == null)
            return null;

        return AmRegistries.INSTANCE.getAbility().get(new Identifier(id));
    }

    @Override
    public void setAbility(Ability ability) {
        if (ability == null) {
            getOrCreateNbt().remove("ability");
            return;
        }

        getOrCreateNbt().putString("ability", ability.getId().toString());
    }
}

package com.cynquil.amethyst.model;

import com.cynquil.amethyst.magic.ManaHolder;

public interface LivingEntityMana extends ManaHolder {
    @Override
    default float getMana() {
        return 0;
    }

    @Override
    default void setMana(float mana) {

    }

    @Override
    default float getMaxMana() {
        return 0;
    }

    @Override
    default void regenMana(float amount) {

    }
}

package com.cynquil.amethyst.model;

public interface LivingEntityMana {
    default float getMana() {
        return 0;
    }

    default void setMana(float mana) {

    }

    default float getMaxMana() {
        return 0;
    }

    default void regenMana(float amount) {

    }
}

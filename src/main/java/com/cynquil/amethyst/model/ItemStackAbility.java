package com.cynquil.amethyst.model;

import com.cynquil.amethyst.ability.Ability;
import com.cynquil.amethyst.ability.AbilityHolder;

public interface ItemStackAbility extends AbilityHolder {
    @Override
    default Ability getAbility() {
        return null;
    }

    @Override
    default void setAbility(Ability ability) {
    }
}

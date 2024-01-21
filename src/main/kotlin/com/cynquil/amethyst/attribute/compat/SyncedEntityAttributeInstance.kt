package com.cynquil.amethyst.attribute.compat;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance

import java.util.function.Consumer

class SyncedEntityAttributeInstance(
    type: EntityAttribute,
    updateCallback: Consumer<EntityAttributeInstance>,
    private val target: EntityAttributeInstance,
    private val transform: (Double, Double) -> Double
) : EntityAttributeInstance(type, updateCallback) {
    override fun getValue(): Double =
        transform(target.value, super.getValue())
}

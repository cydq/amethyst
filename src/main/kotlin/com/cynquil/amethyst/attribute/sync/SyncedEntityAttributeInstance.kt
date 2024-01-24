package com.cynquil.amethyst.attribute.sync

import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeInstance

import java.util.function.Consumer

class SyncedEntityAttributeInstance(
    type: EntityAttribute,
    updateCallback: Consumer<EntityAttributeInstance>,
    private val target: EntityAttributeInstance,
    private val sync: SyncTarget
) : EntityAttributeInstance(type, updateCallback) {
    override fun getValue(): Double =
        sync.transform(target.value, super.getValue())

    override fun getBaseValue(): Double =
        sync.transform(if (sync.syncBase) target.value else target.baseValue, super.getBaseValue())
}

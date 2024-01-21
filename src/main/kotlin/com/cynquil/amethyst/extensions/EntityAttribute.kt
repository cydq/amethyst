package com.cynquil.amethyst.extensions

import com.cynquil.amethyst.attribute.Attribute
import net.minecraft.entity.attribute.EntityAttribute

val EntityAttribute.syncTarget: Attribute.SyncTarget?
    get() = Attribute.getSyncTarget(this)

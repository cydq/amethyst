package com.cynquil.amethyst.extensions

import com.cynquil.amethyst.attribute.Attribute
import com.cynquil.amethyst.attribute.sync.SyncTarget
import net.minecraft.entity.attribute.EntityAttribute

val EntityAttribute.syncTarget: SyncTarget?
    get() = Attribute.getSyncTarget(this)

package com.cynquil.amethyst.extensions

import net.minecraft.util.Identifier

val String.id: Identifier
    get() = Identifier(this)

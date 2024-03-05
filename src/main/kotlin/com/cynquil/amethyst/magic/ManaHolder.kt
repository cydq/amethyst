package com.cynquil.amethyst.magic

interface ManaHolder {
    var mana: Float
    val maxMana: Float

    fun regenMana(amount: Float)
}

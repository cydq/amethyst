package com.cynquil.amethyst.extensions

import com.cynquil.amethyst.attribute.Attributes
import com.cynquil.amethyst.model.LivingEntityMana
import net.minecraft.entity.LivingEntity

val LivingEntity.amHealth: Double
    get() = getAttributeValue(Attributes.Health)

val LivingEntity.amAttackSpeed: Double
    get() = getAttributeValue(Attributes.AttackSpeed)

val LivingEntity.amSpeed: Double
    get() = getAttributeValue(Attributes.Speed)

val LivingEntity.amIntelligence: Double
    get() = getAttributeValue(Attributes.Intelligence)

val LivingEntity.amStrength: Double
    get() = getAttributeValue(Attributes.Strength)

val LivingEntity.amCritChance: Double
    get() = getAttributeValue(Attributes.CriticalChance)

val LivingEntity.amCritDamage: Double
    get() = getAttributeValue(Attributes.CriticalDamage)

val LivingEntity.amDefense: Double
    get() = getAttributeValue(Attributes.Defense)

val LivingEntity.amFerocity: Double
    get() = getAttributeValue(Attributes.Ferocity)

val LivingEntity.amMagicFind: Double
    get() = getAttributeValue(Attributes.MagicFind)

val LivingEntity.amDamage: Double
    get() = getAttributeValue(Attributes.Damage)

var LivingEntity.mana: Float
    get() = (this as LivingEntityMana).mana
    set(value) { (this as LivingEntityMana).mana = value }

val LivingEntity.maxMana: Float
    get() = (this as LivingEntityMana).maxMana

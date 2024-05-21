package com.cynquil.amethyst.extensions

import com.cynquil.amethyst.attribute.Attributes
import com.cynquil.amethyst.model.LivingEntityMana
import net.minecraft.entity.LivingEntity

val LivingEntity.amHealth: Double
    get() = getAttributeValue(Attributes.health)

val LivingEntity.amAttackSpeed: Double
    get() = getAttributeValue(Attributes.attackSpeed)

val LivingEntity.amSpeed: Double
    get() = getAttributeValue(Attributes.speed)

val LivingEntity.amIntelligence: Double
    get() = getAttributeValue(Attributes.intelligence)

val LivingEntity.amStrength: Double
    get() = getAttributeValue(Attributes.strength)

val LivingEntity.amCritChance: Double
    get() = getAttributeValue(Attributes.criticalChance)

val LivingEntity.amCritDamage: Double
    get() = getAttributeValue(Attributes.criticalDamage)

val LivingEntity.amDefense: Double
    get() = getAttributeValue(Attributes.defense)

val LivingEntity.amFerocity: Double
    get() = getAttributeValue(Attributes.ferocity)

val LivingEntity.amMagicFind: Double
    get() = getAttributeValue(Attributes.magicFind)

val LivingEntity.amDamage: Double
    get() = getAttributeValue(Attributes.damage)

var LivingEntity.mana: Float
    get() = (this as LivingEntityMana).mana
    set(value) { (this as LivingEntityMana).mana = value }

val LivingEntity.maxMana: Float
    get() = (this as LivingEntityMana).maxMana

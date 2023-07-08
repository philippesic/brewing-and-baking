package com.pp.item;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;

public class ModFoodComponents {
    public static final FoodComponent CHERRY = new FoodComponent.Builder().hunger(2).saturationModifier(0.3f).build();
    public static final FoodComponent CHERRY_PIE = new FoodComponent.Builder().hunger(12).saturationModifier(8.0f).build();
    public static final FoodComponent CHERRY_JAM = new FoodComponent.Builder().hunger(6).saturationModifier(2.0f).statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 40, 0), 0.5F).build();
}
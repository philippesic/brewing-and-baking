package com.pp.brewingandbaking;

import java.util.List;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    private ModItems() {}

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(BrewingandBaking.MODID);

    private static final FoodProperties CHERRY_FOOD = new FoodProperties.Builder()
            .nutrition(2)
            .saturationModifier(0.3f)
            .build();

    private static final FoodProperties CHERRY_PIE_FOOD = new FoodProperties.Builder()
            .nutrition(8)
            .saturationModifier(0.75f)
            .build();

    private static final FoodProperties CHERRY_JAM_FOOD = new FoodProperties.Builder()
            .nutrition(6)
            .saturationModifier(0.3f)
            .build();

    private static final FoodProperties CACAO_NIBS_FOOD = new FoodProperties.Builder()
            .nutrition(1)
            .saturationModifier(0.3f)
            .build();

    private static final FoodProperties CHOCOLATE_BAR_FOOD = new FoodProperties.Builder()
            .nutrition(4)
            .saturationModifier(0.2f)
            .build();

    // "effect(...)" moved off FoodProperties; use Consumable.onConsume(...) instead.
    private static final Consumable CHERRY_JAM_CONSUMABLE = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(
                    List.of(new MobEffectInstance(MobEffects.REGENERATION, 40, 0)),
                    0.5f
            ))
            .build();

    public static final DeferredItem<Item> CHERRY = ITEMS.registerSimpleItem(
            "cherry",
            props -> props.food(CHERRY_FOOD, Consumables.DEFAULT_FOOD)
    );

    public static final DeferredItem<Item> CHERRY_PIE = ITEMS.registerSimpleItem(
            "cherry_pie",
            props -> props.food(CHERRY_PIE_FOOD, Consumables.DEFAULT_FOOD)
    );

    public static final DeferredItem<Item> CHERRY_JAM = ITEMS.registerSimpleItem(
            "cherry_jam",
            props -> props.food(CHERRY_JAM_FOOD, CHERRY_JAM_CONSUMABLE)
    );
    
    public static final DeferredItem<Item> CACAO_NIBS = ITEMS.registerSimpleItem(
            "cacao_nibs",
            props -> props.food(CACAO_NIBS_FOOD, Consumables.DEFAULT_FOOD)
    );

    public static final DeferredItem<Item> CHOCOLATE_BAR = ITEMS.registerSimpleItem(
            "chocolate_bar",
            props -> props.food(CHOCOLATE_BAR_FOOD, Consumables.DEFAULT_FOOD)
    );

}

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

    private static final FoodProperties CHERRIES_FOOD = new FoodProperties.Builder()
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

    private static final Consumable CHERRY_JAM_CONSUMABLE = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(
                    List.of(new MobEffectInstance(MobEffects.REGENERATION, 40, 0)),
                    0.5f
            ))
            .build();

    private static final FoodProperties CHOCOLATE_FOOD = new FoodProperties.Builder()
            .nutrition(3)
            .saturationModifier(0.35f)
            .build();



    public static final DeferredItem<Item> CHERRIES = ITEMS.registerSimpleItem(
            "cherries",
            props -> props.food(CHERRIES_FOOD, Consumables.DEFAULT_FOOD)
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
            "cacao_nibs"
    );

    public static final DeferredItem<Item> CHOCOLATE = ITEMS.registerSimpleItem(
            "chocolate",
            props -> props.food(CHOCOLATE_FOOD, Consumables.DEFAULT_FOOD)
    );

    public static final DeferredItem<CoffeeBeansItem> COFFEE_BEANS = ITEMS.registerItem(
            "coffee_beans",
            CoffeeBeansItem::new
    );

    public static final DeferredItem<Item> ROASTED_COFFEE_BEANS = ITEMS.registerSimpleItem(
            "roasted_coffee_beans"
    );
}
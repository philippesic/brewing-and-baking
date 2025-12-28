package com.pp.brewingandbaking;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;


public final class ModItems {
    private ModItems() {}

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(BrewingandBaking.MODID);

    public static final DeferredItem<Item> CHERRY = ITEMS.registerSimpleItem(
            "cherry",
            new Item.Properties().food(new FoodProperties.Builder()
                    .nutrition(2)
                    .saturationModifier(0.3f)
                    .build())
    );

    public static final DeferredItem<Item> CHERRY_PIE = ITEMS.registerSimpleItem(
            "cherry_pie",
            new Item.Properties().food(new FoodProperties.Builder()
                    .nutrition(8)
                    .saturationModifier(0.75f)
                    .build())
    );

    public static final DeferredItem<Item> CHERRY_JAM = ITEMS.registerSimpleItem(
            "cherry_jam",
            new Item.Properties().food(new FoodProperties.Builder()
                    .nutrition(6)
                    .saturationModifier(0.3f)
                    .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 40, 0), 0.5f)
                    .build())
    );
}

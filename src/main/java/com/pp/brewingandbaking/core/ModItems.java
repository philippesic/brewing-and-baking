package com.pp.brewingandbaking.core;

import java.util.List;

import com.pp.brewingandbaking.CoffeeBeansItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    private ModItems() {}

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, BrewingandBaking.MODID);


    public static <T extends Block> DeferredHolder<Item, BlockItem> registerBlockItem(String name, DeferredHolder<Block, T> block) { // Block Item helper for future use
        return ITEMS.register(
                name,
                registryName -> new BlockItem(
                        block.get(),
                        new Item.Properties()
                                .setId(ResourceKey.create(Registries.ITEM, registryName))
                )
        );
    }

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



    // Item Registries

    public static final DeferredHolder<Item, Item> CHERRIES = ITEMS.register(
            "cherries",
            registryName -> new Item(new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, registryName))
                    .food(CHERRIES_FOOD, Consumables.DEFAULT_FOOD)
            )
    );

    public static final DeferredHolder<Item, Item> CHERRY_PIE = ITEMS.register(
            "cherry_pie",
            registryName -> new Item(new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, registryName))
                    .food(CHERRY_PIE_FOOD, Consumables.DEFAULT_FOOD)
            )
    );

    public static final DeferredHolder<Item, Item> CHERRY_JAM = ITEMS.register(
            "cherry_jam",
            registryName -> new Item(new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, registryName))
                    .food(CHERRY_JAM_FOOD, CHERRY_JAM_CONSUMABLE)
            )
    );

    public static final DeferredHolder<Item, Item> CACAO_NIBS = ITEMS.register(
            "cacao_nibs",
            registryName -> new Item(new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, registryName))
            )
    );

    public static final DeferredHolder<Item, Item> CHOCOLATE = ITEMS.register(
            "chocolate",
            registryName -> new Item(new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, registryName))
                    .food(CHOCOLATE_FOOD, Consumables.DEFAULT_FOOD)
            )
    );

    public static final DeferredHolder<Item, CoffeeBeansItem> COFFEE_BEANS = ITEMS.register(
            "coffee_beans",
            registryName -> new CoffeeBeansItem(new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, registryName))
            )
    );

    public static final DeferredHolder<Item, Item> ROASTED_COFFEE_BEANS = ITEMS.register(
            "roasted_coffee_beans",
            registryName -> new Item(new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, registryName))
            )
    );
}



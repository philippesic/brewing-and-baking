package com.pp.brewingandbaking.core;

import com.pp.brewingandbaking.worldgen.ModConfiguredFeatures;
import com.pp.brewingandbaking.worldgen.ModPlacedFeatures;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;

@Mod(BrewingandBaking.MODID)
public class BrewingandBaking {
    public static final String MODID = "brewingandbaking";

    public BrewingandBaking(IEventBus modBus) {
        ModItems.ITEMS.register(modBus);
        ModPotions.POTIONS.register(modBus);
        ModBlocks.BLOCKS.register(modBus);
       // NeoForge.EVENT_BUS.addListener(ModBrewing::onRegisterBrewingRecipes);
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.register(ModBrewing.class);
        modBus.addListener(BrewingandBaking::addCreative);

        ModConfiguredFeatures.CONFIGURED_FEATURES.register(modBus);
        ModPlacedFeatures.PLACED_FEATURES.register(modBus);
    }

private static void addCreative(BuildCreativeModeTabContentsEvent event) {

    // Food and Drinks tab
    if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
        event.insertAfter(
            new ItemStack(Items.GLOW_BERRIES),
            new ItemStack(ModItems.CHERRIES.get()),
            CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
        );

        event.insertAfter(
            new ItemStack(ModItems.CHERRIES.get()),
            new ItemStack(ModItems.CHERRY_JAM.get()),
            CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
        );

        event.insertAfter(
            new ItemStack(ModItems.CHERRY_JAM.get()),
            new ItemStack(ModItems.CHERRY_PIE.get()),
            CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
        );

        event.insertAfter(
            new ItemStack(Items.HONEY_BOTTLE),
            ModBrewing.makeCoffeePotionStack(),
            CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
        );
        
        event.insertAfter(new ItemStack(Items.COOKIE),
            new ItemStack(ModItems.CHOCOLATE.get()),
            CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
        );
    }

    // Ingredients tab
    if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {

        event.insertAfter(
            new ItemStack(Items.WHEAT),
            new ItemStack(ModItems.COFFEE_BEANS),
            CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
        );

        event.insertAfter(
            new ItemStack(ModItems.COFFEE_BEANS),
            new ItemStack(ModItems.ROASTED_COFFEE_BEANS.get()),
            CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
        );

        event.insertAfter(new ItemStack(ModItems.ROASTED_COFFEE_BEANS.get()),
            new ItemStack(ModItems.CACAO_NIBS.get()),
            CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
        );

    }

    // Nature Tab
    if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
        event.insertAfter(
            new ItemStack(Items.SWEET_BERRIES),
            new ItemStack(ModItems.COFFEE_BEANS),
            CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
        );
    }
}
}
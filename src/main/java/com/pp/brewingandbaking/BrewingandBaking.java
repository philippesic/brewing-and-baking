package com.pp.brewingandbaking;

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
        modBus.addListener(BrewingandBaking::addCreative);
    }

private static void addCreative(BuildCreativeModeTabContentsEvent event) {
    if (event.getTabKey() != CreativeModeTabs.FOOD_AND_DRINKS) return;

    // IMPORTANT: counts must be 1, and you must reference CreativeModeTab.TabVisibility (not event.TabVisibility)
    event.insertAfter(
        new ItemStack(Items.GLOW_BERRIES),
        new ItemStack(ModItems.CHERRY.get()),
        CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
    );

    // if you want jam/pie right after cherry:
    event.insertAfter(new ItemStack(ModItems.CHERRY.get()),     new ItemStack(ModItems.CHERRY_JAM.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
    event.insertAfter(new ItemStack(ModItems.CHERRY_JAM.get()), new ItemStack(ModItems.CHERRY_PIE.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
}
}

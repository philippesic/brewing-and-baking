package com.pp.brewingandbaking;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;

@Mod(BrewingandBaking.MODID)
public class BrewingandBaking {
    public static final String MODID = "brewingandbaking";

    public BrewingandBaking(IEventBus modBus) {
        ModDataComponents.COMPONENTS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModMeals.ITEMS.register(modBus);
        ModPotions.POTIONS.register(modBus);
        ModBlocks.BLOCKS.register(modBus);
        ModBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modBus);
        ModMenuTypes.MENU_TYPES.register(modBus);
        NeoForge.EVENT_BUS.register(ModBrewing.class);
        NeoForge.EVENT_BUS.register(HungerSystemHandler.class);
        NeoForge.EVENT_BUS.register(BrewingandBaking.class);
        modBus.addListener(BrewingandBaking::addCreative);
    }

    @SubscribeEvent
    public static void onAddReloadListeners(AddServerReloadListenersEvent event) {
        event.addListener(Identifier.fromNamespaceAndPath(MODID, "food_tags"), FoodTagRegistry.INSTANCE);
        event.addListener(Identifier.fromNamespaceAndPath(MODID, "meal_recipes"), MealRecipeRegistry.INSTANCE);
        event.addListener(Identifier.fromNamespaceAndPath(MODID, "luxury_items"), LuxuryRegistry.INSTANCE);
        event.addListener(Identifier.fromNamespaceAndPath(MODID, "uncookable"), UncookableRegistry.INSTANCE);
    }

    private static void addCreative(BuildCreativeModeTabContentsEvent event) {
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
            event.insertAfter(
                new ItemStack(Items.COOKIE),
                new ItemStack(ModItems.CHOCOLATE.get()),
                CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
            );
            for (var meal : ModMeals.ALL) {
                event.accept(new ItemStack(meal.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            }
        }

        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.insertAfter(
                new ItemStack(Items.WHEAT),
                new ItemStack(ModItems.COFFEE_BEANS.get()),
                CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
            );
            event.insertAfter(
                new ItemStack(ModItems.COFFEE_BEANS.get()),
                new ItemStack(ModItems.ROASTED_COFFEE_BEANS.get()),
                CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
            );
            event.insertAfter(
                new ItemStack(ModItems.ROASTED_COFFEE_BEANS.get()),
                new ItemStack(ModItems.CACAO_NIBS.get()),
                CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
            );
        }

        if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
            event.insertAfter(
                new ItemStack(Items.SWEET_BERRIES),
                new ItemStack(ModItems.COFFEE_BEANS.get()),
                CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
            );
        }

        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(
                new ItemStack(ModItems.COOKING_POT.get()),
                CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
            );
        }
    }
}
package com.pp.brewingandbaking.client;

import com.pp.brewingandbaking.core.BrewingandBaking;
import com.pp.brewingandbaking.core.ModBrewing;
import com.pp.brewingandbaking.core.ModPotions;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import java.util.ArrayList;

@EventBusSubscriber(
        modid = BrewingandBaking.MODID,
        value = Dist.CLIENT
)
public final class CoffeeCreativeTabFix {
    private CoffeeCreativeTabFix() {}

    @SubscribeEvent
    public static void onBuildTab(BuildCreativeModeTabContentsEvent event) {
        if (!event.getTabKey().equals(CreativeModeTabs.FOOD_AND_DRINKS)) {
            return;
        }

        removeCoffeeFromView(event, event.getParentEntries(), CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
        removeCoffeeFromView(event, event.getSearchEntries(), CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY);

        ItemStack anchor = new ItemStack(Items.HONEY_BOTTLE);

        // Make separate stacks for parent/search so we dont reuse the same instance
        ItemStack coffeeParent = ModBrewing.makeCoffeePotionStack();
        ItemStack coffeeSearch = ModBrewing.makeCoffeePotionStack();

        try {
            event.insertAfter(anchor, ModBrewing.makeCoffeePotionStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            return;
        } catch (IllegalArgumentException ignored) {
        }

        // Parent tab insertion
        try {
            event.insertAfter(anchor, coffeeParent, CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
        } catch (IllegalArgumentException ignored) {
            event.accept(coffeeParent, CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
        }

        // Search tab insertion
        try {
            event.insertAfter(anchor, coffeeSearch, CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY);
        } catch (IllegalArgumentException ignored) {
            event.accept(coffeeSearch, CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY);
        }
    }

    private static void removeCoffeeFromView(
            BuildCreativeModeTabContentsEvent event,
            Iterable<ItemStack> immutableView,
            CreativeModeTab.TabVisibility visibilityForThatView
    ) {
        // Copy first so we can call remove
        var copy = new ArrayList<ItemStack>();
        for (ItemStack s : immutableView) copy.add(s);

        for (ItemStack stack : copy) {
            if (isCoffeeVariant(stack)) {
                event.remove(stack, visibilityForThatView);
            }
        }
    }

    private static boolean isCoffeeVariant(ItemStack stack) {
        if (!(stack.is(Items.POTION)
                || stack.is(Items.SPLASH_POTION)
                || stack.is(Items.LINGERING_POTION)
                || stack.is(Items.TIPPED_ARROW))) {
            return false;
        }

        PotionContents pc = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);

        return pc.potion()
                .map(holder -> holder.value() == ModPotions.COFFEE.get())
                .orElse(false);
    }
}

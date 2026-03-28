package com.pp.brewingandbaking.client;

import com.pp.brewingandbaking.core.BrewingandBaking;
import com.pp.brewingandbaking.core.ModPotions;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import java.util.ArrayList;

@EventBusSubscriber(
    modid = BrewingandBaking.MODID,
    //bus = EventBusSubscriber.Bus.MOD,   // IMPORTANT (this event is on the mod bus)
    value = Dist.CLIENT
)
public final class CoffeeCreativeArrowHide {
    private CoffeeCreativeArrowHide() {}

    @SubscribeEvent(priority = EventPriority.LOWEST) // IMPORTANT: run after other listeners
    public static void onBuildTab(BuildCreativeModeTabContentsEvent event) {
        // We only care about tipped arrows, and only the coffee subtype
        removeCoffeeTippedArrows(event, CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
        removeCoffeeTippedArrows(event, CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY);

        if (event.getTabKey().equals(CreativeModeTabs.SEARCH)) {
            removeCoffeeTippedArrows(event, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }

    private static void removeCoffeeTippedArrows(BuildCreativeModeTabContentsEvent event, CreativeModeTab.TabVisibility vis) {
        var toRemove = new ArrayList<ItemStack>();

        // Parent entries
        for (ItemStack s : event.getParentEntries()) {
            if (isCoffeeTippedArrow(s)) toRemove.add(s);
        }
        // Search entries
        for (ItemStack s : event.getSearchEntries()) {
            if (isCoffeeTippedArrow(s)) toRemove.add(s);
        }

        for (ItemStack s : toRemove) {
            event.remove(s, vis);
        }
    }

    private static boolean isCoffeeTippedArrow(ItemStack stack) {
        if (!stack.is(Items.TIPPED_ARROW)) return false;

        PotionContents pc = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);

        return pc.potion()
                 .map(h -> h.is(ModPotions.COFFEE.getKey()))
                 .orElse(false);
    }
}

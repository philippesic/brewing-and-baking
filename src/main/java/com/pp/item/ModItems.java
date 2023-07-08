package com.pp.item;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.block.Block;
import java.util.Random;

import com.pp.Cherries;

import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
public class ModItems {
    public static final Item CHERRY = registerItem("cherry",
        new Item(new FabricItemSettings().food(ModFoodComponents.CHERRY)));

        public static final Item CHERRY_PIE = registerItem("cherry_pie",
        new Item(new FabricItemSettings().food(ModFoodComponents.CHERRY_PIE)));

        public static final Item CHERRY_JAM = registerItem("cherry_jam",
        new Item(new FabricItemSettings().food(ModFoodComponents.CHERRY_JAM)));

        public static void initializeGroups() {
            ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(content -> {
                content.addAfter(Items.CHORUS_FRUIT, ModItems.CHERRY);
            });
            ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(content -> {
                content.addAfter(ModItems.CHERRY, ModItems.CHERRY_PIE);
            });
            ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(content -> {
                content.addAfter(ModItems.CHERRY_PIE, ModItems.CHERRY_JAM);
            });
        }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier("cherries", name), item);
    }

    public static void registerModItems() {
        Cherries.LOGGER.info("Registering Mod Items for " + Cherries.MOD_ID);
    }

    public static void init() {
        Cherries.LOGGER.info("Initializing Mod Items for " + Cherries.MOD_ID);
    
        // Event handler for leaf break
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, entity) -> {
            if (state.getBlock() instanceof LeavesBlock && Block.getBlockFromItem(state.getBlock().asItem()) == Blocks.CHERRY_LEAVES) {
                dropCherry(world, pos);
            }
        });
    }
    
    private static void dropCherry(World world, BlockPos pos) {
        if (shouldDropCherry()) {
            ItemStack cherryStack = new ItemStack(ModItems.CHERRY);
            Block.dropStack(world, pos, cherryStack);
        }
    }
    
    // Helper method to determine if a cherry should drop
    private static boolean shouldDropCherry() {
        Random random = new Random();
        return random.nextInt(10) == 0; // 10% chance (1 in 10)
    }}
    

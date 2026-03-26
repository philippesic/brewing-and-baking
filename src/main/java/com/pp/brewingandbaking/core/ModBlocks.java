package com.pp.brewingandbaking.core;

import com.pp.brewingandbaking.CoffeePlantBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.world.level.block.SoundType;

public class ModBlocks {
public static final DeferredRegister<Block> BLOCKS =
        DeferredRegister.create(Registries.BLOCK, BrewingandBaking.MODID);

public static final DeferredHolder<Block, CoffeePlantBlock> COFFEE_PLANT = BLOCKS.register(
        "coffee_plant",
        registryName -> new CoffeePlantBlock(
            BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, registryName))
                    .noCollision()
                    .randomTicks()
                    .instabreak()
                    .sound(SoundType.SWEET_BERRY_BUSH)
        )
);




};

package com.pp.brewingandbaking;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    private ModBlocks() {}

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(BrewingandBaking.MODID);

    public static final DeferredBlock<CoffeePlantBlock> COFFEE_PLANT = BLOCKS.registerBlock(
            "coffee_plant",
            CoffeePlantBlock::new,
            BlockBehaviour.Properties.of()
                    .noCollission()
                    .randomTicks()
                    .instabreak()
                    .sound(SoundType.SWEET_BERRY_BUSH)
    );
}

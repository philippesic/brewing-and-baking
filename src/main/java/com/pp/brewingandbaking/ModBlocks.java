package com.pp.brewingandbaking;

import com.pp.brewingandbaking.block.CookingPotBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Block;

public class ModBlocks {
public static final DeferredRegister.Blocks BLOCKS =
        DeferredRegister.createBlocks(BrewingandBaking.MODID);

public static final DeferredBlock<CoffeePlantBlock> COFFEE_PLANT = BLOCKS.registerBlock(
        "coffee_plant",
        CoffeePlantBlock::new,
        props -> props
                .noCollision()
                .randomTicks()
                .instabreak()
                .sound(SoundType.SWEET_BERRY_BUSH)
);

    public static final DeferredBlock<Block> COOKING_POT = BLOCKS.registerBlock(
            "cooking_pot",
            CookingPotBlock::new,
            props -> props
                    .strength(2.0f, 3.0f)
                    .sound(SoundType.COPPER)
                    .noOcclusion()
    );
}


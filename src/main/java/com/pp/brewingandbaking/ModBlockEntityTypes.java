package com.pp.brewingandbaking;

import com.pp.brewingandbaking.block.entity.CookingPotBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, BrewingandBaking.MODID);

    public static final Supplier<BlockEntityType<CookingPotBlockEntity>> COOKING_POT_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
            "cooking_pot_block_entity",
            () -> new BlockEntityType<>(
                    CookingPotBlockEntity::new,
                    false,
                    ModBlocks.COOKING_POT.get()
            )
    );
}

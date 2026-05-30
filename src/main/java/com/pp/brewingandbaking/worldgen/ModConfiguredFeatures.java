package com.pp.brewingandbaking.worldgen;

import com.pp.brewingandbaking.BrewingandBaking;
import com.pp.brewingandbaking.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModConfiguredFeatures {
    private ModConfiguredFeatures() {}

    public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES =
            DeferredRegister.create(Registries.CONFIGURED_FEATURE, BrewingandBaking.MODID);

    public static final DeferredHolder<ConfiguredFeature<?, ?>, ConfiguredFeature<?, ?>> COFFEE_PATCH =
            CONFIGURED_FEATURES.register("coffee_plant", () ->
                    new ConfiguredFeature<>(
                            Feature.SIMPLE_BLOCK,
                            new SimpleBlockConfiguration(
                                    BlockStateProvider.simple(ModBlocks.COFFEE_PLANT.get().defaultBlockState())
                            )
                    )
            );
}

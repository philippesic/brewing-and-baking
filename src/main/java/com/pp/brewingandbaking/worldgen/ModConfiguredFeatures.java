package com.pp.brewingandbaking.worldgen;

import com.pp.brewingandbaking.core.BrewingandBaking;
import com.pp.brewingandbaking.core.ModBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModConfiguredFeatures {
    private ModConfiguredFeatures() {}

    public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES =
            DeferredRegister.create(Registries.CONFIGURED_FEATURE, BrewingandBaking.MODID);

    public static final DeferredHolder<ConfiguredFeature<?, ?>, ConfiguredFeature<?, ?>> COFFEE_PATCH =
            CONFIGURED_FEATURES.register("coffee_plant", () -> {

                ConfiguredFeature<SimpleBlockConfiguration, ?> coffeePlantConfigured =
                        new ConfiguredFeature<>(
                                Feature.SIMPLE_BLOCK,
                                new SimpleBlockConfiguration(
                                        SimpleStateProvider.simple(ModBlocks.COFFEE_PLANT.get().defaultBlockState())
                                )
                        );
                Holder<PlacedFeature> coffeePlantPlaced =
                        PlacementUtils.inlinePlaced(Holder.direct(coffeePlantConfigured));

                RandomPatchConfiguration patchConfig =
                        new RandomPatchConfiguration(
                                /* tries */ 2,
                                /* xzSpread */ 2,
                                /* ySpread */ 1,
                                coffeePlantPlaced
                        );

                return new ConfiguredFeature<>(Feature.RANDOM_PATCH, patchConfig);
            });
}
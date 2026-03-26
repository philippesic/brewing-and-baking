package com.pp.brewingandbaking.worldgen;

import com.pp.brewingandbaking.core.BrewingandBaking;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.levelgen.placement.*;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public final class ModPlacedFeatures {
    private ModPlacedFeatures() {}

    public static final DeferredRegister<PlacedFeature> PLACED_FEATURES =
            DeferredRegister.create(Registries.PLACED_FEATURE, BrewingandBaking.MODID);

    public static final DeferredHolder<PlacedFeature, PlacedFeature> COFFEE_PATCH_PLACED =
            PLACED_FEATURES.register("coffee_plant_placed", () ->
                    new PlacedFeature(
                            ModConfiguredFeatures.COFFEE_PATCH.getDelegate(),
                            List.of(
                                    RarityFilter.onAverageOnceEvery(32), // Edit for spawnrate
                                    InSquarePlacement.spread(),
                                    PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                                    BiomeFilter.biome()
                            )
                    )
            );
}
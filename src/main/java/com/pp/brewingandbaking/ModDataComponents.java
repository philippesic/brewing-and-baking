package com.pp.brewingandbaking;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModDataComponents {
    private ModDataComponents() {}

    public static final DeferredRegister<DataComponentType<?>> COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, BrewingandBaking.MODID);

    /**
     * Per-stack heal value in food points (1 point = 1/2 heart). Stored on the
     * stack so crafted meals can carry a value derived from their ingredients.
     */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Double>> FOOD_POINTS =
            COMPONENTS.register("food_points", () -> new DataComponentType.Builder<Double>()
                    .persistent(Codec.DOUBLE)
                    .networkSynchronized(ByteBufCodecs.DOUBLE)
                    .build());
}

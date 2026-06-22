package com.pp.brewingandbaking;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffectInstance;
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

    /**
     * Luxury points baked into a meal (0–5) from golden ingredients. Drives the
     * "Gold Specked / Dusted / Plated" display tier; only set when the meal's recipe
     * supports a gold variant.
     */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> LUXURY_POINTS =
            COMPONENTS.register("luxury_points", () -> new DataComponentType.Builder<Integer>()
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT)
                    .build());

    /** Status effect rolled onto a luxury meal at cook time; applied when the meal is eaten. */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<MobEffectInstance>> MEAL_EFFECT =
            COMPONENTS.register("meal_effect", () -> new DataComponentType.Builder<MobEffectInstance>()
                    .persistent(MobEffectInstance.CODEC)
                    .networkSynchronized(MobEffectInstance.STREAM_CODEC)
                    .build());

    /**
     * Convenience component for manually-given meals. Bundles food_points, gold_tier (0–3),
     * effect, and duration_modifier into one component for use with the vanilla /give command.
     * Takes precedence over the individual FOOD_POINTS, LUXURY_POINTS, and MEAL_EFFECT components.
     */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<MealModifiers>> MEAL_MODIFIERS =
            COMPONENTS.register("meal_modifiers", () -> new DataComponentType.Builder<MealModifiers>()
                    .persistent(MealModifiers.CODEC)
                    .networkSynchronized((StreamCodec<? super RegistryFriendlyByteBuf, MealModifiers>) MealModifiers.STREAM_CODEC)
                    .build());
}

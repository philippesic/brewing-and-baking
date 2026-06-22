package com.pp.brewingandbaking;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffect;

import java.util.Optional;

/**
 * A convenience component for manually given meals. Bundles food points,
 * gold tier, effect, and duration modifier into one component so the vanilla
 * /give command only needs a single component argument:
 *
 *   /give @s brewingandbaking:compote[brewingandbaking:meal_modifiers={food_points:5.0,gold_tier:2,effect:"minecraft:speed",duration_modifier:1.5}]
 *
 * Gold tier: 0=none, 1=specked (luxury 1), 2=dusted (luxury 3), 3=plated (luxury 5).
 * Duration modifier: multiplier on the base 600-tick effect duration.
 *
 * Any field may be omitted; omitted food_points falls back to the item's default FOOD_POINTS component.
 */
public record MealModifiers(
        Optional<Double> foodPoints,
        int goldTier,
        Optional<Holder<MobEffect>> effect,
        double durationModifier
) {
    private static final int[] TIER_TO_LUXURY = {0, 1, 3, 5};

    public static final Codec<MealModifiers> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.DOUBLE.optionalFieldOf("food_points").forGetter(MealModifiers::foodPoints),
            Codec.intRange(0, 3).optionalFieldOf("gold_tier", 0).forGetter(MealModifiers::goldTier),
            BuiltInRegistries.MOB_EFFECT.holderByNameCodec().optionalFieldOf("effect").forGetter(MealModifiers::effect),
            Codec.DOUBLE.optionalFieldOf("duration_modifier", 1.0).forGetter(MealModifiers::durationModifier)
    ).apply(i, MealModifiers::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, MealModifiers> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ByteBufCodecs.DOUBLE),
            MealModifiers::foodPoints,
            ByteBufCodecs.VAR_INT,
            MealModifiers::goldTier,
            ByteBufCodecs.optional(ByteBufCodecs.holderRegistry(Registries.MOB_EFFECT)),
            MealModifiers::effect,
            ByteBufCodecs.DOUBLE,
            MealModifiers::durationModifier,
            MealModifiers::new
    );

    /** Raw LUXURY_POINTS value for the given tier (0–5). */
    public int luxuryPoints() {
        return TIER_TO_LUXURY[goldTier];
    }
}

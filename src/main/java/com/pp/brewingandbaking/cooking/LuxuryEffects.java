package com.pp.brewingandbaking.cooking;

import com.pp.brewingandbaking.LuxuryRegistry;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * Luxury point accounting and the probabilistic status effect a luxury meal carries.
 * <p>
 * Golden ingredients contribute luxury points (capped at {@link #MAX_LUXURY} per meal). The first
 * point unlocks a 20% chance for a positive effect; each additional point rolls to either widen that
 * chance, raise the effect tier, or extend the duration, per the design in {@code food-tags.md}. The
 * concrete effect (if any) is rolled once at cook time and stored on the meal stack.
 */
public final class LuxuryEffects {
    private LuxuryEffects() {}

    public static final int MAX_LUXURY = 5;

    /** Positive effect pools, indexed by tier (1–3). Tier N draws only from its own pool. */
    private static final List<List<Holder<MobEffect>>> TIER_POOLS = List.of(
            List.of(MobEffects.NIGHT_VISION, MobEffects.WATER_BREATHING, MobEffects.SLOW_FALLING, MobEffects.INVISIBILITY),
            List.of(MobEffects.SPEED, MobEffects.JUMP_BOOST, MobEffects.FIRE_RESISTANCE, MobEffects.INSTANT_HEALTH),
            List.of(MobEffects.STRENGTH, MobEffects.REGENERATION, MobEffects.RESISTANCE)
    );

    /** Base duration (ticks) for a 1x effect; scaled by the rolled duration multiplier. */
    private static final int BASE_DURATION_TICKS = 600;

    public static int luxuryFor(Item item) {
        return LuxuryRegistry.INSTANCE.pointsFor(item);
    }

    /** Builds an effect instance with a duration scaled from the base cook-time duration. */
    public static MobEffectInstance makeEffect(Holder<MobEffect> effect, double durationMult) {
        int duration = effect.value().isInstantenous() ? 1 : (int) Math.round(BASE_DURATION_TICKS * durationMult);
        return new MobEffectInstance(effect, duration, 0);
    }

    /**
     * Rolls the status effect for a meal with the given luxury points, or returns {@code null} if the
     * meal rolls no effect (or has no luxury). The roll is resolved once, at cook time.
     */
    public static @Nullable MobEffectInstance rollEffect(int luxury, RandomSource random) {
        if (luxury < 1) {
            return null;
        }

        int appearChance = 20;   // percent
        int tier = 1;            // 1–3
        double durationMult = 1.0;

        for (int point = 2; point <= luxury; point++) {
            boolean appearOpen = appearChance < 100;
            boolean tierOpen = tier < TIER_POOLS.size();
            boolean durationOpen = durationMult < 2.0;

            int appearWeight = appearOpen ? 50 : 0;
            int tierWeight = tierOpen ? 25 : 0;
            int durationWeight = durationOpen ? 25 : 0;

            // A capped tier/duration hands its weight to the other of the pair; if both are capped the
            // weight falls to appear chance. A capped appear chance spills evenly onto whatever remains.
            if (!tierOpen && durationOpen) durationWeight += 25;
            if (!durationOpen && tierOpen) tierWeight += 25;
            if (!tierOpen && !durationOpen) appearWeight += 50;
            if (!appearOpen) {
                if (tierOpen && durationOpen) { tierWeight += 25; durationWeight += 25; }
                else if (tierOpen) tierWeight += 50;
                else if (durationOpen) durationWeight += 50;
            }

            int total = appearWeight + tierWeight + durationWeight;
            if (total <= 0) {
                break;
            }
            int roll = random.nextInt(total);
            if (roll < appearWeight) {
                appearChance = Math.min(100, appearChance + 20);
            } else if (roll < appearWeight + tierWeight) {
                tier = Math.min(TIER_POOLS.size(), tier + 1);
            } else {
                durationMult = Math.min(2.0, durationMult + 0.5);
            }
        }

        if (random.nextInt(100) >= appearChance) {
            return null;
        }

        List<Holder<MobEffect>> pool = TIER_POOLS.get(tier - 1);
        Holder<MobEffect> effect = pool.get(random.nextInt(pool.size()));
        int duration = effect.value().isInstantenous() ? 1 : (int) Math.round(BASE_DURATION_TICKS * durationMult);
        return new MobEffectInstance(effect, duration, 0);
    }
}

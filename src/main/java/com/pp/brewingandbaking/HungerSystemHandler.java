package com.pp.brewingandbaking;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.gamerules.GameRules;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Replaces the vanilla hunger system with direct health regeneration.
 *
 * - Eating a food queues health based on its {@link FoodPoints} value, drained
 *   gradually over the following ticks.
 * - The hunger bar is pinned full each tick (no starvation, no sprint lockout),
 *   and natural (food-based) health regeneration is disabled, so food points are
 *   the only source of passive healing.
 */
public final class HungerSystemHandler {
    private HungerSystemHandler() {}

    /** Pending health to drip into each player, keyed by UUID (server-side). */
    private static final Map<UUID, Float> PENDING_HEAL = new HashMap<>();

    /** Drip cadence: heal {@link #HEAL_PER_TICK_STEP} HP (half a heart) every this many ticks (0.5s). */
    private static final int HEAL_INTERVAL_TICKS = 10;
    private static final float HEAL_PER_TICK_STEP = 1.0F;

    /**
     * Food bar is held one point below full: {@code needsFood()} stays true so any
     * food is always edible, while still well above the starvation (0) and sprint
     * (6) thresholds. Natural regen is disabled separately via the gamerule.
     */
    private static final int PINNED_FOOD_LEVEL = 19;

    /** Below this health (3 hearts), the player is exhausted and cannot sprint. */
    private static final float EXHAUSTION_HEALTH_THRESHOLD = 6.0F;

    /** Food level that trips vanilla's sprint lockout ({@code hasEnoughFood()} requires {@code > 6}). */
    private static final int EXHAUSTED_FOOD_LEVEL = 6;

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        MinecraftServer server = event.getServer();
        server.overworld().getGameRules().set(GameRules.NATURAL_HEALTH_REGENERATION, false, server);
    }

    @SubscribeEvent
    public static void onFinishEating(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        double points = FoodPoints.pointsFor(event.getItem());
        if (points <= 0.0) {
            return;
        }
        float health = FoodPoints.rollHealth(points, player.getRandom());
        if (health > 0.0F) {
            PENDING_HEAL.merge(player.getUUID(), health, Float::sum);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        neutralizeHunger(player);
        dripHeal(player);
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        PENDING_HEAL.remove(event.getEntity().getUUID());
    }

    private static void neutralizeHunger(ServerPlayer player) {
        FoodData food = player.getFoodData();
        boolean exhausted = player.getHealth() < EXHAUSTION_HEALTH_THRESHOLD;
        food.setFoodLevel(exhausted ? EXHAUSTED_FOOD_LEVEL : PINNED_FOOD_LEVEL);
        food.setSaturation(20.0F);
    }

    private static void dripHeal(ServerPlayer player) {
        UUID id = player.getUUID();
        Float pending = PENDING_HEAL.get(id);
        if (pending == null) {
            return;
        }
        if (player.getHealth() >= player.getMaxHealth()) {
            PENDING_HEAL.remove(id);
            return;
        }
        if (player.tickCount % HEAL_INTERVAL_TICKS != 0) {
            return;
        }
        float amount = Math.min(HEAL_PER_TICK_STEP, pending);
        player.heal(amount);
        float remaining = pending - amount;
        if (remaining <= 0.0F) {
            PENDING_HEAL.remove(id);
        } else {
            PENDING_HEAL.put(id, remaining);
        }
    }
}

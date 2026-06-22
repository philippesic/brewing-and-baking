package com.pp.brewingandbaking.client;

import com.mojang.datafixers.util.Either;
import com.pp.brewingandbaking.BrewingandBaking;
import com.pp.brewingandbaking.FoodPoints;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

/**
 * Client HUD for the food-points health system:
 * <ul>
 *   <li>hides the vanilla hunger bar,</li>
 *   <li>appends a heart-value row to food tooltips, and</li>
 *   <li>previews the heal as blinking ghost hearts over the health bar while a
 *       food is held.</li>
 * </ul>
 */
@EventBusSubscriber(modid = BrewingandBaking.MODID, value = Dist.CLIENT)
public final class FoodHeartHud {
    private FoodHeartHud() {}

    /** Ghost preview hearts pulse their alpha between these bounds (fade in/out). */
    private static final float GHOST_MIN_ALPHA = 0.15F;
    private static final float GHOST_MAX_ALPHA = 0.7F;

    /** Standard y of the bottom health-heart row (hotbar + one bar height). */
    private static final int HEALTH_ROW_Y_FROM_BOTTOM = 39;

    /** Vanilla caps a heart row at 10 columns. */
    private static final int MAX_HEART_COLUMNS = 10;

    @SubscribeEvent
    public static void hideHungerBar(RenderGuiLayerEvent.Pre event) {
        if (event.getName().equals(VanillaGuiLayers.FOOD_LEVEL)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void appendFoodTooltip(RenderTooltipEvent.GatherComponents event) {
        double points = FoodPoints.pointsFor(event.getItemStack());
        if (points == 0.0) {
            return;
        }
        boolean poisonous = points < 0.0;
        double absPoints = Math.abs(points);
        event.getTooltipElements().add(Either.right(
                new FoodHeartsTooltip(FoodPoints.solidHalfHearts(absPoints), FoodPoints.hasChanceHalfHeart(absPoints), poisonous)));
    }

    @SubscribeEvent
    public static void previewHealOverHealth(RenderGuiLayerEvent.Post event) {
        if (!event.getName().equals(VanillaGuiLayers.PLAYER_HEALTH)) {
            return;
        }
        Player player = Minecraft.getInstance().player;
        if (player == null || player.isSpectator() || player.getAbilities().instabuild) {
            return;
        }
        ItemStack food = heldFood(player);
        if (food == null) {
            return;
        }
        double points = FoodPoints.pointsFor(food);
        if (points == 0.0) {
            return;
        }

        boolean poisonous = points < 0.0;
        double absPoints = Math.abs(points);
        int previewHalves = FoodPoints.solidHalfHearts(absPoints) + (FoodPoints.hasChanceHalfHeart(absPoints) ? 1 : 0);

        int maxHalves = Mth.ceil(player.getMaxHealth());
        int currentHalves = Mth.ceil(player.getHealth());

        GuiGraphicsExtractor graphics = event.getGuiGraphics();
        int baseX = graphics.guiWidth() / 2 - 91;
        int y = graphics.guiHeight() - HEALTH_ROW_Y_FROM_BOTTOM;
        float alpha = FoodHearts.pulseAlpha(GHOST_MIN_ALPHA, GHOST_MAX_ALPHA);

        if (poisonous) {
            int startSlot = Math.max(0, currentHalves - previewHalves);
            for (int k = 0; k < previewHalves; k++) {
                int slot = startSlot + k;
                if (slot >= currentHalves || slot / 2 >= MAX_HEART_COLUMNS) {
                    break;
                }
                FoodHearts.drawHalfSlot(graphics, baseX, y, slot, alpha, true);
            }
        } else {
            if (currentHalves >= maxHalves) {
                return;
            }
            for (int k = 0; k < previewHalves; k++) {
                int slot = currentHalves + k;
                if (slot >= maxHalves || slot / 2 >= MAX_HEART_COLUMNS) {
                    break;
                }
                FoodHearts.drawHalfSlot(graphics, baseX, y, slot, alpha);
            }
        }
    }

    private static ItemStack heldFood(Player player) {
        ItemStack main = player.getMainHandItem();
        if (FoodPoints.pointsFor(main) != 0.0) {
            return main;
        }
        ItemStack off = player.getOffhandItem();
        if (FoodPoints.pointsFor(off) != 0.0) {
            return off;
        }
        return null;
    }
}

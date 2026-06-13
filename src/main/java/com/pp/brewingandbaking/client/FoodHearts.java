package com.pp.brewingandbaking.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;

/**
 * Shared heart-icon drawing used by the food tooltip and the held-food health
 * overlay. Hearts are addressed by half-heart "slot" index from a base x: slot
 * {@code s} lives in heart column {@code s / 2}, drawn as a half on even slots
 * and a full (covering the whole heart) on odd slots.
 */
final class FoodHearts {
    private FoodHearts() {}

    static final int SIZE = 9;
    static final int SEPARATION = 8;

    private static final Identifier CONTAINER = Identifier.withDefaultNamespace("hud/heart/container");
    private static final Identifier FULL = Identifier.withDefaultNamespace("hud/heart/full");
    private static final Identifier HALF = Identifier.withDefaultNamespace("hud/heart/half");

    private static final long PULSE_CYCLE_MS = 1600L;

    /** Smooth shared pulse (1 -&gt; 0 -&gt; 1) so preview hearts fade in unison. */
    static float pulse01() {
        double cycle = (Util.getMillis() % PULSE_CYCLE_MS) / (double) PULSE_CYCLE_MS;
        return (float) ((Math.cos(cycle * 2.0 * Math.PI) + 1.0) / 2.0);
    }

    /** Eased alpha oscillating between {@code min} and {@code max}. */
    static float pulseAlpha(float min, float max) {
        return min + pulse01() * (max - min);
    }

    static int heartsForHalves(int halfHearts) {
        return Math.max(1, Mth.ceil(halfHearts / 2.0));
    }

    static int widthForHalves(int halfHearts) {
        return (heartsForHalves(halfHearts) - 1) * SEPARATION + SIZE;
    }

    static void drawContainer(GuiGraphicsExtractor graphics, int baseX, int y, int heartIndex) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, CONTAINER, baseX + heartIndex * SEPARATION, y, SIZE, SIZE);
    }

    static void drawHalfSlot(GuiGraphicsExtractor graphics, int baseX, int y, int slot, float alpha) {
        int x = baseX + (slot / 2) * SEPARATION;
        Identifier sprite = (slot % 2 == 0) ? HALF : FULL;
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, SIZE, SIZE, alpha);
    }
}

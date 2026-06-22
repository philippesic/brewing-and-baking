package com.pp.brewingandbaking.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

/**
 * Renders a food's heal value as a row of hearts under its tooltip: solid
 * hearts for the guaranteed heal, and a blinking half heart for the chance-based
 * portion.
 */
public final class FoodHeartsClientTooltip implements ClientTooltipComponent {
    private final int solidHalves;
    private final boolean chanceHalf;
    private final boolean poisonous;

    public FoodHeartsClientTooltip(FoodHeartsTooltip data) {
        this.solidHalves = data.solidHalves();
        this.chanceHalf = data.chanceHalf();
        this.poisonous = data.poisonous();
    }

    private int totalSlots() {
        return solidHalves + (chanceHalf ? 1 : 0);
    }

    @Override
    public int getHeight(Font font) {
        return FoodHearts.SIZE + 2;
    }

    @Override
    public int getWidth(Font font) {
        return FoodHearts.widthForHalves(totalSlots());
    }

    @Override
    public void extractImage(Font font, int x, int y, int w, int h, GuiGraphicsExtractor graphics) {
        int hearts = FoodHearts.heartsForHalves(totalSlots());
        for (int i = 0; i < hearts; i++) {
            FoodHearts.drawContainer(graphics, x, y, i);
        }
        for (int slot = 0; slot < solidHalves; slot++) {
            FoodHearts.drawHalfSlot(graphics, x, y, slot, 1.0F, poisonous);
        }
        if (chanceHalf) {
            FoodHearts.drawHalfSlot(graphics, x, y, solidHalves, FoodHearts.pulseAlpha(0.2F, 1.0F), poisonous);
        }
    }
}

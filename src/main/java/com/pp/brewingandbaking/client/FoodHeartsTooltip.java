package com.pp.brewingandbaking.client;

import net.minecraft.world.inventory.tooltip.TooltipComponent;

/**
 * Tooltip data carrier for a food's heal value: a number of guaranteed half
 * hearts plus an optional chance-based half heart (rendered blinking).
 */
public record FoodHeartsTooltip(int solidHalves, boolean chanceHalf) implements TooltipComponent {
}

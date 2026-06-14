package com.pp.brewingandbaking.client;

import com.pp.brewingandbaking.inventory.CookingPotMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.client.gui.GuiGraphicsExtractor;


public class CookingPotScreen extends AbstractContainerScreen<CookingPotMenu> {
    public CookingPotScreen(CookingPotMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
    }
    private static final Identifier BG_TEXTURE =
            Identifier.fromNamespaceAndPath("brewingandbaking", "textures/gui/cookingpot.png");

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, BG_TEXTURE, leftPos, topPos, 0.0F, 0.0F, imageWidth, imageHeight, 256, 256);

        // Progress indicator. Drawn as flat rects so it works before the GUI texture has dedicated
        // sprites. TODO: replace these fills with blits from cookingpot.png (a flame sprite + a
        // progress-arrow sprite) once the art is added, using menu.getCookProgressScaled for the arrow.
        int barX = leftPos + 78;
        int barY = topPos + 40;
        int barW = 24;
        int barH = 6;
        graphics.fill(barX, barY, barX + barW, barY + barH, 0xFF3A3A3A);
        int progress = menu.getCookProgressScaled(barW);
        if (progress > 0) {
            graphics.fill(barX, barY, barX + progress, barY + barH, 0xFFE08A1E);
        }
        if (menu.isHeated()) {
            graphics.fill(barX - 11, barY - 1, barX - 3, barY + barH + 1, 0xFFFF5520);
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        extractBackground(graphics, mouseX, mouseY, partialTick);
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        extractTooltip(graphics, mouseX, mouseY);
    }
}

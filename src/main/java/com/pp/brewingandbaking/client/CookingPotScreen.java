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
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        extractBackground(graphics, mouseX, mouseY, partialTick);
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        extractTooltip(graphics, mouseX, mouseY);
    }
}

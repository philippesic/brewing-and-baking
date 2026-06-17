package com.pp.brewingandbaking.client;

import com.pp.brewingandbaking.inventory.CookingPotMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class CookingPotScreen extends AbstractContainerScreen<CookingPotMenu> {

    private static final Identifier BG_TEXTURE =
            Identifier.fromNamespaceAndPath("brewingandbaking", "textures/gui/cookingpot.png");
    private static final Identifier FUEL_ON =
            Identifier.fromNamespaceAndPath("brewingandbaking", "container/cookingpot/fuel_on");
    private static final Identifier COOK_PROGRESS =
            Identifier.fromNamespaceAndPath("brewingandbaking", "container/cookingpot/cook_progress");

    public CookingPotScreen(CookingPotMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, BG_TEXTURE, leftPos, topPos, 0.0F, 0.0F, imageWidth, imageHeight, 256, 256);

        if (menu.isHeated()) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, FUEL_ON,
                    16, 16, 0, 0,
                    leftPos + 112, topPos + 62, 16, 16);
        }

        int progress = menu.getCookProgressScaled(24);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, COOK_PROGRESS,
                24, 16, 0, 0,
                leftPos + 108, topPos + 34, progress, 16);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        extractBackground(graphics, mouseX, mouseY, partialTick);
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        extractTooltip(graphics, mouseX, mouseY);
    }
}
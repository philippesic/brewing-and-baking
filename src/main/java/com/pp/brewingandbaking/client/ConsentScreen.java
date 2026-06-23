package com.pp.brewingandbaking.client;

import com.pp.brewingandbaking.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * First-run popup that explains local food logging and lets the player opt in or out.
 * Shown once on the title screen; can be re-triggered by setting consentShown=false in Mod Config.
 */
public class ConsentScreen extends Screen {

    private static final int BOX_W = 280;
    private static final int LINE_H = 10;
    private static final int PADDING = 16;
    private static final int INNER_W = BOX_W - PADDING * 2;

    private static final Component TITLE_TEXT =
            Component.literal("Brewing & Baking — Usage Data");
    private static final Component BODY_1 =
            Component.literal("Help improve the mod by enabling usage logging.");
    private static final Component BODY_2 =
            Component.literal("This records which foods you eat and when. Log data is collected anonymously, and you can view your log at any time in:");
    private static final Component BODY_3 =
            Component.literal("{minecraft}/logs/brewingandbaking-food.log");
    private static final Component BODY_4 =
            Component.literal("No personal data is collected. You can change this at any time via the Mod Config screen.");
    private static final Component OPT_IN_LABEL =
            Component.literal("Enable logging");

    private final Screen parent;
    private Checkbox checkbox;

    public ConsentScreen(Screen parent) {
        super(TITLE_TEXT);
        this.parent = parent;
    }

    @Override
    protected void init() {
        int boxH = computeBoxHeight();
        int boxX = (this.width - BOX_W) / 2;
        int boxY = (this.height - boxH) / 2;

        int contentX = boxX + PADDING;
        int contentY = boxY + PADDING;

        // Running cursor below title + separator
        contentY += LINE_H + 4 + 6; // title + gap + separator line

        contentY += wrappedHeight(BODY_1) + 6;
        contentY += wrappedHeight(BODY_2) + 6;
        contentY += wrappedHeight(BODY_3) + 10;
        contentY += wrappedHeight(BODY_4) + 6;

        checkbox = Checkbox.builder(OPT_IN_LABEL, this.font)
                .pos(contentX, contentY)
                .selected(Config.DATA_COLLECTION_ENABLED.get())
                .maxWidth(INNER_W)
                .build();
        this.addRenderableWidget(checkbox);

        int buttonY = boxY + boxH - PADDING - 20;
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, btn -> onClose())
                .pos(this.width / 2 - 60, buttonY)
                .size(120, 20)
                .build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractTransparentBackground(graphics);

        int boxH = computeBoxHeight();
        int boxX = (this.width - BOX_W) / 2;
        int boxY = (this.height - boxH) / 2;

        // Background box
        graphics.fill(boxX, boxY, boxX + BOX_W, boxY + boxH, 0xCC000000);
        graphics.fill(boxX, boxY, boxX + BOX_W, boxY + 1, 0xFFAAAAAA);
        graphics.fill(boxX, boxY + boxH - 1, boxX + BOX_W, boxY + boxH, 0xFFAAAAAA);
        graphics.fill(boxX, boxY, boxX + 1, boxY + boxH, 0xFFAAAAAA);
        graphics.fill(boxX + BOX_W - 1, boxY, boxX + BOX_W, boxY + boxH, 0xFFAAAAAA);

        int contentX = boxX + PADDING;
        int y = boxY + PADDING;

        // Title
        graphics.centeredText(this.font, TITLE_TEXT, this.width / 2, y, 0xFFFFFFFF);
        y += LINE_H + 4;

        // Separator
        graphics.fill(contentX, y, boxX + BOX_W - PADDING, y + 1, 0xFF555555);
        y += 6;

        y = drawWrapped(graphics, BODY_1, contentX, y, 0xFFCCCCCC);
        y += 6;
        y = drawWrapped(graphics, BODY_2, contentX, y, 0xFFAAAAAA);
        y += 6;
        y = drawWrapped(graphics, BODY_3, contentX, y, 0xFF888888);
        y += 6;
        y = drawWrapped(graphics, BODY_4, contentX, y, 0xFFAAAAAA);

        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        Config.DATA_COLLECTION_ENABLED.set(checkbox.selected());
        Config.CONSENT_SHOWN.set(true);
        Config.SPEC.save();
        Minecraft.getInstance().setScreen(parent);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    // -------------------------------------------------------------------------

    private int drawWrapped(GuiGraphicsExtractor graphics, Component text, int x, int y, int color) {
        List<net.minecraft.util.FormattedCharSequence> lines = this.font.split(text, INNER_W);
        for (var line : lines) {
            graphics.text(this.font, line, x, y, color);
            y += LINE_H;
        }
        return y;
    }

    private int wrappedHeight(Component text) {
        return this.font.split(text, INNER_W).size() * LINE_H;
    }

    private int computeBoxHeight() {
        // Title + sep + body lines + checkbox + button + padding
        int h = PADDING * 2;
        h += LINE_H + 4 + 6; // title + gap + separator
        h += wrappedHeight(BODY_1) + 6;
        h += wrappedHeight(BODY_2) + 6;
        h += wrappedHeight(BODY_3) + 10;
        h += 20 + 10; // checkbox height approx
        h += 20 + PADDING; // button
        return h;
    }
}

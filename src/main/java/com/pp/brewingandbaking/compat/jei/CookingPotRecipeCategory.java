package com.pp.brewingandbaking.compat.jei;

import com.pp.brewingandbaking.BrewingandBaking;
import com.pp.brewingandbaking.ModBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class CookingPotRecipeCategory implements IRecipeCategory<CookingPotJeiRecipe> {
    public static final IRecipeType<CookingPotJeiRecipe> RECIPE_TYPE = IRecipeType.create(
            BrewingandBaking.MODID, "cooking_pot", CookingPotJeiRecipe.class
    );

    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(BrewingandBaking.MODID, "textures/gui/cookingpot.png");
    private static final Identifier ARROW_TEXTURE =
            Identifier.fromNamespaceAndPath(BrewingandBaking.MODID,
                    "textures/gui/sprites/container/cookingpot/cook_progress.png");
    private static final Identifier FLAME_TEXTURE =
            Identifier.fromNamespaceAndPath(BrewingandBaking.MODID,
                    "textures/gui/sprites/container/cookingpot/fuel_on.png");

    private static final int[] INPUT_X = {12, 30, 48, 66, 84};
    private static final int INPUT_Y = 35;
    private static final int OUTPUT_X = 144;
    private static final int OUTPUT_Y = 35;
    private static final int ARROW_X = 108;
    private static final int ARROW_Y = 34;
    private static final int FLAME_X = 112;
    private static final int FLAME_Y = 62;
    private static final int BG_WIDTH = 176;
    private static final int BG_HEIGHT = 80;

    private final IDrawableStatic background;
    private final IDrawable icon;
    private final IDrawableAnimated arrow;
    private final IDrawableStatic flame;

    public CookingPotRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, BG_WIDTH, BG_HEIGHT);
        this.icon = helper.createDrawableItemLike(ModBlocks.COOKING_POT.get());
        this.arrow = helper.drawableBuilder(ARROW_TEXTURE, 0, 0, 24, 16)
                .setTextureSize(24, 16)
                .buildAnimated(40, IDrawableAnimated.StartDirection.LEFT, false);
        this.flame = helper.drawableBuilder(FLAME_TEXTURE, 0, 0, 16, 16)
                .setTextureSize(16, 16)
                .build();
    }

    @Override
    public IRecipeType<CookingPotJeiRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container.brewingandbaking.cooking_pot");
    }

    @Override
    public int getWidth() {
        return BG_WIDTH;
    }

    @Override
    public int getHeight() {
        return BG_HEIGHT;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void draw(CookingPotJeiRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics, 0, 0);
        arrow.draw(guiGraphics, ARROW_X, ARROW_Y);
        flame.draw(guiGraphics, FLAME_X, FLAME_Y);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CookingPotJeiRecipe recipe, IFocusGroup focuses) {
        List<List<ItemStack>> slots = recipe.inputSlots();
        for (int i = 0; i < INPUT_X.length; i++) {
            if (i < slots.size() && !slots.get(i).isEmpty()) {
                builder.addSlot(RecipeIngredientRole.INPUT, INPUT_X[i], INPUT_Y)
                        .addItemStacks(slots.get(i));
            }
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, OUTPUT_Y)
                .add(recipe.output());
    }
}

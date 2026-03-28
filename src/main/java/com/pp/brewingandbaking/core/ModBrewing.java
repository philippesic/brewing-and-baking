package com.pp.brewingandbaking.core;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.pp.brewingandbaking.CoffeeBrewingRecipe;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.brewing.IBrewingRecipe;
import net.neoforged.neoforge.event.brewing.PotionBrewEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;

@EventBusSubscriber(modid = BrewingandBaking.MODID)
public final class ModBrewing {
    private ModBrewing() {}

    public static final int COFFEE_COLOR = 0x6F4E37;

    @SubscribeEvent
    public static void onRegisterBrewingRecipes(RegisterBrewingRecipesEvent event) {
        event.getBuilder().addRecipe(new CoffeeBrewingRecipe());

        PotionBrewing.Builder builder = event.getBuilder();

        boolean removedAny = removeVanillaContainerConversions(builder);

        builder.addRecipe(new NoCoffeeContainerConversionsRecipe());
    }

    public static ItemStack makeCoffeePotionStack() {
        return makeCoffeeStackOf(Items.POTION);
    }

    public static ItemStack makeCoffeeStackOf(Item base) {
        ItemStack stack = new ItemStack(base);

        PotionContents contents = new PotionContents(
            Optional.of(ModPotions.COFFEE),
            Optional.of(COFFEE_COLOR),
            java.util.List.of(),
            Optional.empty()
        );

        stack.set(() -> DataComponents.POTION_CONTENTS, contents);
        return stack;
    }

    private static boolean isCoffeePotionStack(ItemStack stack) {
        PotionContents pc = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        return pc.potion().isPresent() && pc.potion().get().is(ModPotions.COFFEE.getKey());
    }

    @SubscribeEvent
    public static void onBrewPre(PotionBrewEvent.Pre event) {
        ItemStack ingredient = event.getItem(3);

        boolean isGunpowder = ingredient.is(Items.GUNPOWDER);
        boolean isDragonsBreath = ingredient.is(Items.DRAGON_BREATH);
        if (!isGunpowder && !isDragonsBreath) return;

        // Cancel Brew to avoid variants
        for (int i = 0; i < event.getLength(); i++) {
            ItemStack s = event.getItem(i);
            if (isCoffeePotionStack(s)) {
                event.setCanceled(true);
                return;
            }
        }
    }

    // Force Tinting
    @SubscribeEvent
    public static void onBrewPost(PotionBrewEvent.Post event) {
        for (int i = 0; i < event.getLength(); i++) {
            ItemStack stack = event.getItem(i);

            if (!stack.is(Items.POTION) && !stack.is(Items.SPLASH_POTION) && !stack.is(Items.LINGERING_POTION)) {
                continue;
            }

            PotionContents pc = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);

            if (pc.potion().isPresent() && pc.potion().get().is(ModPotions.COFFEE.getKey())) {
                PotionContents tinted = new PotionContents(
                    pc.potion(),
                    Optional.of(COFFEE_COLOR),
                    pc.customEffects(),
                    pc.customName()
                );

                stack.set(() -> DataComponents.POTION_CONTENTS, tinted);
                event.setItem(i, stack);
            }
        }
    }

private static boolean removeVanillaContainerConversions(PotionBrewing.Builder builder) {
    boolean removed1 = removeContainerMixReflect(builder, Items.POTION, Items.GUNPOWDER, Items.SPLASH_POTION);
    boolean removed2 = removeContainerMixReflect(builder, Items.SPLASH_POTION, Items.DRAGON_BREATH, Items.LINGERING_POTION);
    return removed1 || removed2;
}

private static boolean removeContainerMixReflect(PotionBrewing.Builder builder, Item from, Item reagent, Item to) {
    try {
        Field f = PotionBrewing.Builder.class.getDeclaredField("containerMixes");
        f.setAccessible(true);

        List<?> mixes = (List<?>) f.get(builder);
        if (mixes == null || mixes.isEmpty()) return false;

        ItemStack reagentStack = new ItemStack(reagent);
        boolean removedAny = false;

        Class<?> mixClass = mixes.get(0).getClass();
        var mFrom = mixClass.getMethod("from");
        var mTo = mixClass.getMethod("to");
        var mIngredient = mixClass.getMethod("ingredient");

        var holderValue = Class.forName("net.minecraft.core.Holder").getMethod("value");
        var ingredientTest = Class.forName("net.minecraft.world.item.crafting.Ingredient")
                .getMethod("test", ItemStack.class);

        for (Iterator<?> it = mixes.iterator(); it.hasNext();) {
            Object mix = it.next();

            Object fromHolder = mFrom.invoke(mix);
            Object toHolder = mTo.invoke(mix);
            Object ingredientObj = mIngredient.invoke(mix);

            Item fromItem = (Item) holderValue.invoke(fromHolder);
            Item toItem = (Item) holderValue.invoke(toHolder);

            boolean reagentMatches = (boolean) ingredientTest.invoke(ingredientObj, reagentStack);

            if (fromItem == from && toItem == to && reagentMatches) {
                it.remove();
                removedAny = true;
            }
        }

        return removedAny;
    } catch (Throwable t) {
        return false;
    }
}

    private static final class NoCoffeeContainerConversionsRecipe implements IBrewingRecipe {

        @Override
        public boolean isInput(ItemStack input) {
            return input.is(Items.POTION) || input.is(Items.SPLASH_POTION);
        }

        @Override
        public boolean isIngredient(ItemStack ingredient) {
            return ingredient.is(Items.GUNPOWDER) || ingredient.is(Items.DRAGON_BREATH);
        }

        @Override
        public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
            if (!isInput(input) || !isIngredient(ingredient)) return ItemStack.EMPTY;

            // Block Brew Timer
            if (isCoffeePotionStack(input)) return ItemStack.EMPTY;

            // Replacements for vanilla behavior
            if (ingredient.is(Items.GUNPOWDER) && input.is(Items.POTION)) {
                return input.transmuteCopy(Items.SPLASH_POTION, 1);
            }

            if (ingredient.is(Items.DRAGON_BREATH) && input.is(Items.SPLASH_POTION)) {
                return input.transmuteCopy(Items.LINGERING_POTION, 1);
            }

            return ItemStack.EMPTY;
        }
    }
}
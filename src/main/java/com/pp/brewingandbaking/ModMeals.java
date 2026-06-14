package com.pp.brewingandbaking;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.Consumables;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Stub meal items produced by the cooking system. Each carries a default
 * {@link ModDataComponents#FOOD_POINTS} of {@link #DEFAULT_FOOD_POINTS}; the
 * recipe system will later assign a value derived from the input ingredients.
 */
public final class ModMeals {
    private ModMeals() {}

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(BrewingandBaking.MODID);

    /** Placeholder heal value until recipe-driven values are implemented. */
    public static final double DEFAULT_FOOD_POINTS = 1.0;

    private static final FoodProperties MEAL_FOOD =
            new FoodProperties.Builder().nutrition(0).saturationModifier(0.0F).build();

    public static final String[] NAMES = {
            "meat_kebab", "glazed_tenderloin", "meat_omelette", "goulash", "sweet_and_sour_stir_fry",
            "burger", "grilled_fish_skewer", "glazed_filet", "grilled_fish_and_vegetables",
            "sweet_and_sour_filet", "fish_sandwich", "surf_and_turf", "glazed_surf_and_turf",
            "mixed_grill", "jambalaya", "paella", "roasted_vegetables", "glazed_roasted_vegetables",
            "veggie_omelette", "curry", "veggie_wrap", "breakfast_burrito", "fruit_salad", "compote",
            "smoothie", "fruit_tart", "fruit_cake", "fried_eggs", "omelette", "eggs_and_toast",
            "french_toast", "custard", "cheese", "ice_cream", "toast", "sweet_roll", "bread_pudding",
            "cake", "poisonous_porridge"
    };

    public static final List<DeferredItem<Item>> ALL = new ArrayList<>();
    private static final Map<String, DeferredItem<Item>> BY_NAME = new LinkedHashMap<>();

    static {
        for (String name : NAMES) {
            DeferredItem<Item> item = ITEMS.registerSimpleItem(name, props -> props
                    .food(MEAL_FOOD, Consumables.DEFAULT_FOOD)
                    .component(ModDataComponents.FOOD_POINTS.get(), DEFAULT_FOOD_POINTS));
            ALL.add(item);
            BY_NAME.put(name, item);
        }
    }

    public static DeferredItem<Item> get(String name) {
        return BY_NAME.get(name);
    }
}

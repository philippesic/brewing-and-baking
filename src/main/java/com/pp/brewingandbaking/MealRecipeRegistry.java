package com.pp.brewingandbaking;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.io.Reader;
import java.util.*;

public class MealRecipeRegistry extends SimplePreparableReloadListener<List<MealRecipe>> {
    public static final MealRecipeRegistry INSTANCE = new MealRecipeRegistry();
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final FileToIdConverter LISTER = FileToIdConverter.json("meal_recipes");

    private Map<EnumSet<FoodTag>, MealRecipe> byTags = new HashMap<>();
    private MealRecipe junkOverrideMeal = null;

    private MealRecipeRegistry() {}

    @Override
    protected List<MealRecipe> prepare(ResourceManager manager, ProfilerFiller profiler) {
        List<MealRecipe> result = new ArrayList<>();

        for (var entry : LISTER.listMatchingResources(manager).entrySet()) {
            Identifier id = LISTER.fileToId(entry.getKey());

            try (Reader reader = entry.getValue().openAsReader()) {
                var json = JsonParser.parseReader(reader).getAsJsonObject();

                String mealName = json.get("result").getAsString();
                boolean goldVariant = json.has("gold_variant") && json.get("gold_variant").getAsBoolean();
                boolean junkOverride = json.has("junk_override") && json.get("junk_override").getAsBoolean();

                List<EnumSet<FoodTag>> combinations = new ArrayList<>();
                if (json.has("combinations")) {
                    for (JsonElement combo : json.getAsJsonArray("combinations")) {
                        EnumSet<FoodTag> tagSet = EnumSet.noneOf(FoodTag.class);
                        for (JsonElement tagElem : combo.getAsJsonArray()) {
                            try {
                                tagSet.add(FoodTag.valueOf(tagElem.getAsString()));
                            } catch (IllegalArgumentException e) {
                                LOGGER.warn("Unknown tag {} in meal recipe {}", tagElem.getAsString(), mealName);
                            }
                        }
                        if (!tagSet.isEmpty()) {
                            combinations.add(tagSet);
                        }
                    }
                }

                result.add(new MealRecipe(mealName, combinations, goldVariant, junkOverride));
            } catch (Exception e) {
                LOGGER.error("Failed to load meal recipe {}", id, e);
            }
        }

        return result;
    }

    @Override
    protected void apply(List<MealRecipe> data, ResourceManager manager, ProfilerFiller profiler) {
        Map<EnumSet<FoodTag>, MealRecipe> newMap = new HashMap<>();
        MealRecipe newJunkMeal = null;

        for (MealRecipe recipe : data) {
            if (recipe.junkOverride()) {
                newJunkMeal = recipe;
                continue;
            }
            for (EnumSet<FoodTag> combo : recipe.combinations()) {
                if (newMap.putIfAbsent(EnumSet.copyOf(combo), recipe) != null) {
                    LOGGER.warn("Duplicate meal recipe for tag combination {}, ignoring {}", combo, recipe.result());
                }
            }
        }

        this.byTags = newMap;
        this.junkOverrideMeal = newJunkMeal;
        LOGGER.info("[BrewingAndBaking] Loaded {} meal recipes ({} tag combinations)", data.size(), newMap.size());
    }

    public Optional<MealRecipe> resolve(EnumSet<FoodTag> tags) {
        if (tags.contains(FoodTag.JUNK) && junkOverrideMeal != null) {
            return Optional.of(junkOverrideMeal);
        }
        return Optional.ofNullable(byTags.get(tags));
    }
}

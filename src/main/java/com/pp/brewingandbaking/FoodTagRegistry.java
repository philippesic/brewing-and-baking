package com.pp.brewingandbaking;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import org.slf4j.Logger;

import java.io.Reader;
import java.util.*;

public class FoodTagRegistry extends SimplePreparableReloadListener<Map<FoodTag, List<String>>> {
    public static final FoodTagRegistry INSTANCE = new FoodTagRegistry();
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final FileToIdConverter LISTER = FileToIdConverter.json("food_tags");

    private Map<Item, EnumSet<FoodTag>> itemToTags = new HashMap<>();

    private FoodTagRegistry() {}

    @Override
    protected Map<FoodTag, List<String>> prepare(ResourceManager manager, ProfilerFiller profiler) {
        Map<FoodTag, List<String>> result = new HashMap<>();

        for (var entry : LISTER.listMatchingResources(manager).entrySet()) {
            Identifier id = LISTER.fileToId(entry.getKey());
            String tagName = id.getPath();

            FoodTag tag;
            try {
                tag = FoodTag.valueOf(tagName.toUpperCase());
            } catch (IllegalArgumentException e) {
                LOGGER.warn("Skipping unknown food tag file: {}", tagName);
                continue;
            }

            try (Reader reader = entry.getValue().openAsReader()) {
                JsonElement json = JsonParser.parseReader(reader);
                List<String> items = new ArrayList<>();
                for (JsonElement elem : json.getAsJsonObject().getAsJsonArray("items")) {
                    items.add(elem.getAsString());
                }
                result.put(tag, items);
            } catch (Exception e) {
                LOGGER.error("Failed to load food tag {}", tagName, e);
            }
        }

        return result;
    }

    @Override
    protected void apply(Map<FoodTag, List<String>> data, ResourceManager manager, ProfilerFiller profiler) {
        Map<Item, EnumSet<FoodTag>> newMap = new HashMap<>();

        for (var entry : data.entrySet()) {
            FoodTag tag = entry.getKey();
            for (String itemId : entry.getValue()) {
                BuiltInRegistries.ITEM.getOptional(Identifier.parse(itemId)).ifPresentOrElse(
                    item -> newMap.computeIfAbsent(item, k -> EnumSet.noneOf(FoodTag.class)).add(tag),
                    () -> LOGGER.warn("Unknown item {} in food tag {}", itemId, tag)
                );
            }
        }

        this.itemToTags = newMap;
        LOGGER.info("[BrewingAndBaking] Loaded food tags for {} items", newMap.size());
    }

    public Set<FoodTag> getTagsFor(Item item) {
        return itemToTags.getOrDefault(item, EnumSet.noneOf(FoodTag.class));
    }

    public boolean hasTag(Item item) {
        return itemToTags.containsKey(item);
    }
}

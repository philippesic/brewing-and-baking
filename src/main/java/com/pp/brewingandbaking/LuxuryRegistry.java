package com.pp.brewingandbaking;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
import java.util.HashMap;
import java.util.Map;

/**
 * Data-driven luxury point values for cooking ingredients. Loads every
 * {@code data/&lt;namespace&gt;/luxury_items/*.json} file, each an object mapping item ids to point
 * values, e.g. {@code {"items": {"minecraft:golden_apple": 2}}}. Values from multiple files are merged,
 * so luxury items can be added or removed purely through data without touching code.
 */
public class LuxuryRegistry extends SimplePreparableReloadListener<Map<String, Integer>> {
    public static final LuxuryRegistry INSTANCE = new LuxuryRegistry();
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final FileToIdConverter LISTER = FileToIdConverter.json("luxury_items");

    private Map<Item, Integer> itemToPoints = new HashMap<>();

    private LuxuryRegistry() {}

    @Override
    protected Map<String, Integer> prepare(ResourceManager manager, ProfilerFiller profiler) {
        Map<String, Integer> result = new HashMap<>();

        for (var entry : LISTER.listMatchingResources(manager).entrySet()) {
            Identifier id = LISTER.fileToId(entry.getKey());
            try (Reader reader = entry.getValue().openAsReader()) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                JsonObject items = json.getAsJsonObject("items");
                for (Map.Entry<String, JsonElement> item : items.entrySet()) {
                    result.put(item.getKey(), item.getValue().getAsInt());
                }
            } catch (Exception e) {
                LOGGER.error("Failed to load luxury items {}", id, e);
            }
        }

        return result;
    }

    @Override
    protected void apply(Map<String, Integer> data, ResourceManager manager, ProfilerFiller profiler) {
        Map<Item, Integer> newMap = new HashMap<>();

        for (var entry : data.entrySet()) {
            BuiltInRegistries.ITEM.getOptional(Identifier.parse(entry.getKey())).ifPresentOrElse(
                item -> newMap.put(item, entry.getValue()),
                () -> LOGGER.warn("Unknown luxury item {}", entry.getKey())
            );
        }

        this.itemToPoints = newMap;
        LOGGER.info("[BrewingAndBaking] Loaded {} luxury items", newMap.size());
    }

    public int pointsFor(Item item) {
        return itemToPoints.getOrDefault(item, 0);
    }
}

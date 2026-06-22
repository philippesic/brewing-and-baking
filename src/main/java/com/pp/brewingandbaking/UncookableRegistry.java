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
import java.util.HashSet;
import java.util.Set;

/**
 * Data-driven blocklist of items barred from the cooking pot. Loads every
 * {@code data/&lt;namespace&gt;/uncookable/*.json} file, each an object with an {@code "items"} array of
 * item ids. An item here is rejected even if it carries a {@link FoodTag}, letting otherwise-cookable
 * foods be excluded without code changes. (Items simply absent from every food tag are already
 * uncookable and need not be listed.)
 */
public class UncookableRegistry extends SimplePreparableReloadListener<Set<String>> {
    public static final UncookableRegistry INSTANCE = new UncookableRegistry();
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final FileToIdConverter LISTER = FileToIdConverter.json("uncookable");

    private Set<Item> blocked = new HashSet<>();

    private UncookableRegistry() {}

    @Override
    protected Set<String> prepare(ResourceManager manager, ProfilerFiller profiler) {
        Set<String> result = new HashSet<>();

        for (var entry : LISTER.listMatchingResources(manager).entrySet()) {
            Identifier id = LISTER.fileToId(entry.getKey());
            try (Reader reader = entry.getValue().openAsReader()) {
                for (JsonElement item : JsonParser.parseReader(reader).getAsJsonObject().getAsJsonArray("items")) {
                    result.add(item.getAsString());
                }
            } catch (Exception e) {
                LOGGER.error("Failed to load uncookable list {}", id, e);
            }
        }

        return result;
    }

    @Override
    protected void apply(Set<String> data, ResourceManager manager, ProfilerFiller profiler) {
        Set<Item> newSet = new HashSet<>();

        for (String itemId : data) {
            BuiltInRegistries.ITEM.getOptional(Identifier.parse(itemId)).ifPresentOrElse(
                newSet::add,
                () -> LOGGER.warn("Unknown uncookable item {}", itemId)
            );
        }

        this.blocked = newSet;
        LOGGER.info("[BrewingAndBaking] Loaded {} uncookable items", newSet.size());
    }

    public boolean contains(Item item) {
        return blocked.contains(item);
    }
}

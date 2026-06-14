package com.pp.brewingandbaking.cooking;

import com.mojang.logging.LogUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class CookingIngredients {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String PREFIX = "cooking/";

    private static volatile Map<Item, TagKey<Item>> itemToTag = Map.of();

    private CookingIngredients() {
    }

    public static void onTagsUpdated(TagsUpdatedEvent event) {
        rebuild();
    }

    public static void rebuild() {
        Map<Item, TagKey<Item>> map = new HashMap<>();
        BuiltInRegistries.ITEM.getTags().forEach(named -> {
            TagKey<Item> key = named.key();
            if (!key.location().getPath().startsWith(PREFIX)) {
                return;
            }
            indexTag(map, key, named);
        });
        itemToTag = Map.copyOf(map);
        LOGGER.debug("Cooking ingredient index rebuilt: {} items across cooking/* tags", itemToTag.size());
    }

    private static void indexTag(Map<Item, TagKey<Item>> map, TagKey<Item> key, HolderSet.Named<Item> contents) {
        for (Holder<Item> holder : contents) {
            Item item = holder.value();
            TagKey<Item> existing = map.get(item);
            if (existing == null) {
                map.put(item, key);
            } else if (!existing.equals(key)) {
                if (key.location().compareTo(existing.location()) < 0) {
                    map.put(item, key);
                }
                LOGGER.warn("Item {} is in multiple cooking tags ({} and {}); using {}",
                        BuiltInRegistries.ITEM.getKey(item), existing.location(), key.location(),
                        map.get(item).location());
            }
        }
    }

    public static boolean isCookable(Item item) {
        return itemToTag.containsKey(item);
    }

    public static @Nullable TagKey<Item> tagFor(Item item) {
        return itemToTag.get(item);
    }

    public static Optional<List<Identifier>> canonicalKey(List<ItemStack> stacks) {
        List<Identifier> ids = new ArrayList<>();
        for (ItemStack stack : stacks) {
            if (stack.isEmpty()) {
                continue;
            }
            TagKey<Item> tag = itemToTag.get(stack.getItem());
            if (tag == null) {
                return Optional.empty();
            }
            ids.add(tag.location());
        }
        if (ids.isEmpty()) {
            return Optional.empty();
        }
        ids.sort(Comparator.naturalOrder());
        return Optional.of(ids);
    }
}

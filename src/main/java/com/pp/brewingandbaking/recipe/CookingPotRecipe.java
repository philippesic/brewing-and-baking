package com.pp.brewingandbaking.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pp.brewingandbaking.ModRecipes;
import com.pp.brewingandbaking.cooking.CookingIngredients;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A cooking pot recipe: 1–5 ingredient {@linkplain TagKey item tags} cook into a single meal.
 * <p>
 * Matching is by the <em>multiset of tags</em> rather than by slot or by item identity. Each input
 * item is resolved to its single cooking tag via {@link CookingIngredients}; the sorted list of those
 * tag ids is the recipe's canonical key, and {@link #matches} compares the two. Because lookups go
 * through {@code RecipeManager.getRecipeFor} (which pre-filters to this {@link RecipeType}), only the
 * handful of cooking pot recipes are ever tested, and each test is a couple of O(1) hashmap hits.
 * <p>
 * The result is stored as an (item, count) pair and the {@link ItemStack} is built lazily in
 * {@link #result()}. Decoding therefore never constructs an ItemStack, which would otherwise fail with
 * "item does not have components yet" when recipes parse before item components are attached.
 */
public class CookingPotRecipe implements Recipe<CookingPotRecipeInput> {
    public static final int MAX_INGREDIENTS = 5;

    /** Result item + count; the ItemStack is materialised lazily, never during codec decode. */
    public record Result(Holder<Item> item, int count) {
        static final Codec<Result> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Item.CODEC.fieldOf("id").forGetter(Result::item),
                        Codec.INT.optionalFieldOf("count", 1).forGetter(Result::count)
                ).apply(instance, Result::new)
        );
        static final StreamCodec<RegistryFriendlyByteBuf, Result> STREAM_CODEC = StreamCodec.composite(
                Item.STREAM_CODEC, Result::item,
                ByteBufCodecs.VAR_INT, Result::count,
                Result::new
        );

        ItemStack toStack() {
            return new ItemStack(item, count);
        }
    }

    public static final MapCodec<CookingPotRecipe> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    TagKey.codec(Registries.ITEM)
                            .listOf()
                            .fieldOf("ingredients")
                            .forGetter(CookingPotRecipe::inputTags),
                    Result.CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                    Codec.INT.optionalFieldOf("cookingtime", 100).forGetter(CookingPotRecipe::cookingTime)
            ).apply(instance, CookingPotRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, CookingPotRecipe> STREAM_CODEC = StreamCodec.composite(
            TagKey.streamCodec(Registries.ITEM).apply(ByteBufCodecs.list()),
            CookingPotRecipe::inputTags,
            Result.STREAM_CODEC,
            recipe -> recipe.result,
            ByteBufCodecs.VAR_INT,
            CookingPotRecipe::cookingTime,
            CookingPotRecipe::new
    );

    private final List<TagKey<Item>> inputTags;
    private final Result result;
    private final int cookingTime;

    public CookingPotRecipe(List<TagKey<Item>> inputTags, Result result, int cookingTime) {
        this.inputTags = List.copyOf(inputTags);
        this.result = result;
        this.cookingTime = cookingTime;
    }

    public List<TagKey<Item>> inputTags() {
        return inputTags;
    }

    public ItemStack result() {
        return result.toStack();
    }

    public int cookingTime() {
        return cookingTime;
    }

    /** Sorted tag-id list used as this recipe's lookup/match key. */
    public List<Identifier> canonicalKey() {
        List<Identifier> ids = new ArrayList<>(inputTags.size());
        for (TagKey<Item> tag : inputTags) {
            ids.add(tag.location());
        }
        ids.sort(Comparator.naturalOrder());
        return ids;
    }

    @Override
    public boolean matches(CookingPotRecipeInput input, Level level) {
        return CookingIngredients.canonicalKey(input.items())
                .map(key -> key.equals(canonicalKey()))
                .orElse(false);
    }

    @Override
    public ItemStack assemble(CookingPotRecipeInput input) {
        return result.toStack();
    }

    // Cooking pot recipes are matched through our own RecipeType lookup, never placed via the
    // recipe book. Marking special skips vanilla placement handling and its warnings.
    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.FURNACE_MISC;
    }

    @Override
    public RecipeSerializer<? extends Recipe<CookingPotRecipeInput>> getSerializer() {
        return ModRecipes.COOKING_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<CookingPotRecipeInput>> getType() {
        return ModRecipes.COOKING_TYPE.get();
    }
}

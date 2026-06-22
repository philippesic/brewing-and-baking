package com.pp.brewingandbaking;

import java.util.EnumSet;
import java.util.List;

public record MealRecipe(String result, List<EnumSet<FoodTag>> combinations, boolean goldVariant, boolean junkOverride) {}

Base Tags:

MEAT
FISH
VEGETABLE
FRUIT
JUNK    
EGG
MILK
SWEETENER
GRAIN

DO NOT TAG:
All soups, stew, suspicious stews
Anything not directly edible (like wheat) except eggs, milk, sugar
Cake, pumpkin pie, cooked foods like baked potato or steak, kelp


1 Food Point = 1/2 Heart Restored
<1 Food Point = % Chance to restore 1/2 heart
Luxury Points (Capped at 5 per recipe):
Glistening Melon: 1
Golden Carrot: 1
Golden Apple: 2
Notch Apple: 3

First Luxury point always increases the chance for a status effect from 0% -> 20%, only positive effects
Each additional luxury point has a:

50% Chance to add an additional 20% appear chance (eg 20% -> 40%), capped at 100%
25% Chance to increase the potion lootpool tier (eg tier 1 (low tier potions like night vision) -> tier 2 (no tier 1 potions, only tier 2, like speed or jump boost), capped at tier 3 (to clarify, all potions will be level 1 (not strength 2), i mean the general usefulness, like strength t3 vs water breathing t1)
25% Chance to increase the duration (1x -> 1.5x -> 2x), capped at 2x

If the cap for the tier is hit, then it is removed from the roll and the duration becomes a 50% chance (and vice versa)

Each luxury point also increases the food points of the resulting meal by 1 (regardless of the base food point value of the attached food), so 5 luxury points automatically adds 2.5 hearts to whatever the meal restores
Luxury points also influence the look and name of the meal:
1-2 Luxury Points: Gold Specked <meal-name> (Will have some gold in the texture)
3-4 Luxury Points: Gold Dusted <meal-name> (Will have more gold in the texture)
5 Luxury Points: Gold Plated <meal-name> (Will be fully golden)

---

## Food Items Reference

1 bar = 2 hunger points (1 drumstick icon). Melon Slice = 2 pts = 1.0 bars.

| Item | Hunger (bars) | Tags |
|:-----|:-------------:|------|
| Apple | 2.0 | | FRUIT
| Baked Potato | 2.5 | | N/A
| Beetroot | 0.5 | | VEGETABLE
| Beetroot Soup | 3.0 | | N/A
| Bread | 2.5 | | GRAIN
| Cake | 7.0 | | N/A
| Carrot | 1.5 | | VEGETABLE
| Chorus Fruit | 2.0 | | FRUIT
| Cookie | 1.0 | | N/A
| Cooked Chicken | 3.0 | | N/A
| Cooked Cod | 2.5 | | N/A
| Cooked Mutton | 3.0 | | N/A
| Cooked Porkchop | 4.0 | | N/A
| Cooked Rabbit | 2.5 | | N/A
| Cooked Salmon | 3.0 | | N/A
| Dried Kelp | 0.5 | | N/A
| Enchanted Golden Apple | 2.0 | | FRUIT
| Glow Berries | 1.0 | | FRUIT
| Golden Apple | 2.0 | | FRUIT
| Golden Carrot | 3.0 | | VEGETABLE
| Honey Bottle | 3.0 | | SWEETENER
| Melon Slice | 1.0 | | FRUIT
| Mushroom Stew | 3.0 | | N/A
| Poisonous Potato | 1.0 | | JUNK
| Potato | 0.5 | | VEGETABLE
| Pufferfish | 0.5 | | JUNK
| Pumpkin Pie | 4.0 | | N/A
| Rabbit Stew | 5.0 | | N/A
| Raw Beef | 1.5 | | MEAT
| Raw Chicken | 1.0 | | MEAT
| Raw Cod | 1.0 | | FISH
| Raw Mutton | 1.0 | | MEAT
| Raw Porkchop | 1.5 | | MEAT
| Raw Rabbit | 1.5 | | MEAT
| Raw Salmon | 1.0 | | FISH
| Rotten Flesh | 2.0 | | JUNK
| Spider Eye | 1.0 | | JUNK
| Steak | 4.0 | | N/A
| Suspicious Stew | 3.0 | | N/A
| Sweet Berries | 1.0 | | FRUIT
| Tropical Fish | 0.5 | | FISH

---

## Base Food Point Values

Direct-eat values for base items. `1.0 = guaranteed ½ heart`; fractions are a % chance
to restore ½ heart (`0.25` = 25%, `1.5` = ½ heart + 50% chance of another).
These govern eating the item raw only — meal values are derived separately (formula TBD).

Plain/furnaced/abundant foods are capped at 1.5. Cooked ≈ 1.5× its raw counterpart.
Golden foods sit in a separate luxury tier above the cap (gated by gold cost, not abundance).

| Item | Hunger (bars) | Food Points |
|:-----|:-------------:|:-----------:|
| Raw Beef | 1.5 | 1.0 |
| Raw Porkchop | 1.5 | 1.0 |
| Raw Rabbit | 1.5 | 0.5 |
| Raw Mutton | 1.0 | 0.5 |
| Raw Chicken | 1.0 | 0.5 |
| Steak | 4.0 | 1.5 |
| Cooked Porkchop | 4.0 | 1.5 |
| Cooked Rabbit | 2.5 | 0.75 |
| Cooked Mutton | 3.0 | 0.75 |
| Cooked Chicken | 3.0 | 0.75 |
| Raw Cod | 1.0 | 0.5 |
| Raw Salmon | 1.0 | 0.5 |
| Cooked Cod | 2.5 | 0.75 |
| Cooked Salmon | 3.0 | 0.75 |
| Tropical Fish | 0.5 | 0.25 |
| Carrot | 1.5 | 0.75 |
| Potato | 0.5 | 0.5 |
| Beetroot | 0.5 | 0.25 |
| Baked Potato | 2.5 | 0.75 |
| Apple | 2.0 | 0.5 |
| Chorus Fruit | 2.0 | 0.5 |
| Melon Slice | 1.0 | 0.25 |
| Sweet Berries | 1.0 | 0.25 |
| Glow Berries | 1.0 | 0.25 |
| Bread | 2.5 | 0.5 |
| Honey Bottle | 3.0 | 0.5 |
| Cookie | 1.0 | 0.25 |
| Dried Kelp | 0.5 | 0.25 |
| Rotten Flesh | 2.0 | 0.5 |
| Spider Eye | 1.0 | 0.25 |
| Poisonous Potato | 1.0 | 0.25 |
| Pufferfish | 0.5 | 0.25 |
| Rabbit Stew | 5.0 | 1.0 |
| Beetroot Soup | 3.0 | 0.75 |
| Mushroom Stew | 3.0 | 0.75 |
| Suspicious Stew | 3.0 | 0.75 |
| Pumpkin Pie | 4.0 | 0.75 |
| Cake | 7.0 | 0.25 / slice |
| Golden Carrot | 3.0 | 1.5 |
| Golden Apple | 2.0 | 2.0 |
| Enchanted Golden Apple | 2.0 | 3.0 |

Notes:
- Egg, Milk Bucket, and Sugar are pure ingredients (not directly edible) — no direct-eat value.
- Cake overlaps with the crafted `EGG+MILK+SWEETENER+GRAIN → Cake` meal; revisit when meal values are set.

---

## Tag Combinations

JUNK is listed once — anything + JUNK = junk food regardless of other tags.

Design logic for the resulting recipes:
- A dish is named after its dominant ingredient(s). Minor additions that wouldn't
  realistically change the meal are absorbed and leave the result unchanged.
- EGG, MILK, and SWEETENER are usually "accents" — they get absorbed into a heartier
  dish until enough of them accumulate to define a dish of their own (Custard, Cake, etc.).
- MEAT + FISH together is always a Surf and Turf family dish.
- Adding VEGETABLE + GRAIN to a protein builds it up into a rice/bread composite
  (Goulash, Jambalaya, Paella). FRUIT/SWEETENER on a protein make it "glazed" or "sweet and sour".

| Tag Combination | Resulting Recipe |
|:----------------|:-----------------|
| **— 1 tag —** | |
| MEAT | Meat Kebab |
| FISH | Grilled Fish Skewer |
| VEGETABLE | Roasted Vegetables |
| FRUIT | Compote |
| EGG | Fried Eggs |
| MILK | Cheese |
| GRAIN | Toast |
| JUNK | Poisonous Porridge |
| **— 2 tags —** | |
| MEAT + FISH | Surf and Turf |
| MEAT + VEGETABLE | Goulash |
| MEAT + FRUIT | Glazed Tenderloin |
| MEAT + EGG | Meat Omelette |
| MEAT + MILK | Meat Kebab |
| MEAT + SWEETENER | Glazed Tenderloin |
| MEAT + GRAIN | Burger |
| FISH + VEGETABLE | Grilled Fish and Vegetables |
| FISH + FRUIT | Glazed Filet |
| FISH + EGG | Grilled Fish Skewer |
| FISH + MILK | Grilled Fish Skewer |
| FISH + SWEETENER | Glazed Filet |
| FISH + GRAIN | Fish Sandwich |
| VEGETABLE + FRUIT | Fruit Salad |
| VEGETABLE + EGG | Veggie Omelette |
| VEGETABLE + MILK | Curry |
| VEGETABLE + SWEETENER | Glazed Roasted Vegetables |
| VEGETABLE + GRAIN | Veggie Wrap |
| FRUIT + EGG | Smoothie |
| FRUIT + MILK | Smoothie |
| FRUIT + SWEETENER | Smoothie |
| FRUIT + GRAIN | Fruit Tart |
| EGG + MILK | Omelette |
| EGG + SWEETENER | Omelette |
| EGG + GRAIN | Eggs and Toast |
| MILK + SWEETENER | Ice Cream |
| MILK + GRAIN | Bread Pudding |
| SWEETENER + GRAIN | Sweet Roll |
| **— 3 tags —** | |
| MEAT + FISH + VEGETABLE | Mixed Grill |
| MEAT + FISH + FRUIT | Glazed Surf and Turf |
| MEAT + FISH + EGG | Surf and Turf |
| MEAT + FISH + MILK | Surf and Turf |
| MEAT + FISH + SWEETENER | Glazed Surf and Turf |
| MEAT + FISH + GRAIN | Jambalaya |
| MEAT + VEGETABLE + FRUIT | Sweet and Sour Stir Fry |
| MEAT + VEGETABLE + EGG | Goulash |
| MEAT + VEGETABLE + MILK | Goulash |
| MEAT + VEGETABLE + SWEETENER | Sweet and Sour Stir Fry |
| MEAT + VEGETABLE + GRAIN | Goulash |
| MEAT + FRUIT + EGG | Meat Omelette |
| MEAT + FRUIT + MILK | Glazed Tenderloin |
| MEAT + FRUIT + SWEETENER | Glazed Tenderloin |
| MEAT + FRUIT + GRAIN | Burger |
| MEAT + EGG + MILK | Meat Omelette |
| MEAT + EGG + SWEETENER | Meat Omelette |
| MEAT + EGG + GRAIN | Burger |
| MEAT + MILK + SWEETENER | Glazed Tenderloin |
| MEAT + MILK + GRAIN | Burger |
| MEAT + SWEETENER + GRAIN | Burger |
| FISH + VEGETABLE + FRUIT | Sweet and Sour Filet |
| FISH + VEGETABLE + EGG | Grilled Fish and Vegetables |
| FISH + VEGETABLE + MILK | Grilled Fish and Vegetables |
| FISH + VEGETABLE + SWEETENER | Sweet and Sour Filet |
| FISH + VEGETABLE + GRAIN | Grilled Fish and Vegetables |
| FISH + FRUIT + EGG | Glazed Filet |
| FISH + FRUIT + MILK | Glazed Filet |
| FISH + FRUIT + SWEETENER | Glazed Filet |
| FISH + FRUIT + GRAIN | Fish Sandwich |
| FISH + EGG + MILK | Grilled Fish Skewer |
| FISH + EGG + SWEETENER | Glazed Filet |
| FISH + EGG + GRAIN | Fish Sandwich |
| FISH + MILK + SWEETENER | Glazed Filet |
| FISH + MILK + GRAIN | Fish Sandwich |
| FISH + SWEETENER + GRAIN | Fish Sandwich |
| VEGETABLE + FRUIT + EGG | Veggie Omelette |
| VEGETABLE + FRUIT + MILK | Curry |
| VEGETABLE + FRUIT + SWEETENER | Fruit Salad |
| VEGETABLE + FRUIT + GRAIN | Veggie Wrap |
| VEGETABLE + EGG + MILK | Veggie Omelette |
| VEGETABLE + EGG + SWEETENER | Veggie Omelette |
| VEGETABLE + EGG + GRAIN | Breakfast Burrito |
| VEGETABLE + MILK + SWEETENER | Curry |
| VEGETABLE + MILK + GRAIN | Curry |
| VEGETABLE + SWEETENER + GRAIN | Veggie Wrap |
| FRUIT + EGG + MILK | Smoothie |
| FRUIT + EGG + SWEETENER | Smoothie |
| FRUIT + EGG + GRAIN | Fruit Tart |
| FRUIT + MILK + SWEETENER | Smoothie |
| FRUIT + MILK + GRAIN | Fruit Tart |
| FRUIT + SWEETENER + GRAIN | Fruit Tart |
| EGG + MILK + SWEETENER | Custard |
| EGG + MILK + GRAIN | French Toast |
| EGG + SWEETENER + GRAIN | French Toast |
| MILK + SWEETENER + GRAIN | Bread Pudding |
| **— 4 tags —** | |
| MEAT + FISH + VEGETABLE + FRUIT | Mixed Grill |
| MEAT + FISH + VEGETABLE + EGG | Mixed Grill |
| MEAT + FISH + VEGETABLE + MILK | Mixed Grill |
| MEAT + FISH + VEGETABLE + SWEETENER | Mixed Grill |
| MEAT + FISH + VEGETABLE + GRAIN | Paella |
| MEAT + FISH + FRUIT + EGG | Glazed Surf and Turf |
| MEAT + FISH + FRUIT + MILK | Glazed Surf and Turf |
| MEAT + FISH + FRUIT + SWEETENER | Glazed Surf and Turf |
| MEAT + FISH + FRUIT + GRAIN | Jambalaya |
| MEAT + FISH + EGG + MILK | Surf and Turf |
| MEAT + FISH + EGG + SWEETENER | Glazed Surf and Turf |
| MEAT + FISH + EGG + GRAIN | Jambalaya |
| MEAT + FISH + MILK + SWEETENER | Glazed Surf and Turf |
| MEAT + FISH + MILK + GRAIN | Jambalaya |
| MEAT + FISH + SWEETENER + GRAIN | Jambalaya |
| MEAT + VEGETABLE + FRUIT + EGG | Sweet and Sour Stir Fry |
| MEAT + VEGETABLE + FRUIT + MILK | Sweet and Sour Stir Fry |
| MEAT + VEGETABLE + FRUIT + SWEETENER | Sweet and Sour Stir Fry |
| MEAT + VEGETABLE + FRUIT + GRAIN | Sweet and Sour Stir Fry |
| MEAT + VEGETABLE + EGG + MILK | Goulash |
| MEAT + VEGETABLE + EGG + SWEETENER | Sweet and Sour Stir Fry |
| MEAT + VEGETABLE + EGG + GRAIN | Goulash |
| MEAT + VEGETABLE + MILK + SWEETENER | Sweet and Sour Stir Fry |
| MEAT + VEGETABLE + MILK + GRAIN | Goulash |
| MEAT + VEGETABLE + SWEETENER + GRAIN | Sweet and Sour Stir Fry |
| MEAT + FRUIT + EGG + MILK | Meat Omelette |
| MEAT + FRUIT + EGG + SWEETENER | Meat Omelette |
| MEAT + FRUIT + EGG + GRAIN | Burger |
| MEAT + FRUIT + MILK + SWEETENER | Glazed Tenderloin |
| MEAT + FRUIT + MILK + GRAIN | Burger |
| MEAT + FRUIT + SWEETENER + GRAIN | Burger |
| MEAT + EGG + MILK + SWEETENER | Meat Omelette |
| MEAT + EGG + MILK + GRAIN | Burger |
| MEAT + EGG + SWEETENER + GRAIN | Burger |
| MEAT + MILK + SWEETENER + GRAIN | Burger |
| FISH + VEGETABLE + FRUIT + EGG | Sweet and Sour Filet |
| FISH + VEGETABLE + FRUIT + MILK | Sweet and Sour Filet |
| FISH + VEGETABLE + FRUIT + SWEETENER | Sweet and Sour Filet |
| FISH + VEGETABLE + FRUIT + GRAIN | Sweet and Sour Filet |
| FISH + VEGETABLE + EGG + MILK | Grilled Fish and Vegetables |
| FISH + VEGETABLE + EGG + SWEETENER | Sweet and Sour Filet |
| FISH + VEGETABLE + EGG + GRAIN | Grilled Fish and Vegetables |
| FISH + VEGETABLE + MILK + SWEETENER | Sweet and Sour Filet |
| FISH + VEGETABLE + MILK + GRAIN | Grilled Fish and Vegetables |
| FISH + VEGETABLE + SWEETENER + GRAIN | Sweet and Sour Filet |
| FISH + FRUIT + EGG + MILK | Glazed Filet |
| FISH + FRUIT + EGG + SWEETENER | Glazed Filet |
| FISH + FRUIT + EGG + GRAIN | Fish Sandwich |
| FISH + FRUIT + MILK + SWEETENER | Glazed Filet |
| FISH + FRUIT + MILK + GRAIN | Fish Sandwich |
| FISH + FRUIT + SWEETENER + GRAIN | Fish Sandwich |
| FISH + EGG + MILK + SWEETENER | Glazed Filet |
| FISH + EGG + MILK + GRAIN | Fish Sandwich |
| FISH + EGG + SWEETENER + GRAIN | Fish Sandwich |
| FISH + MILK + SWEETENER + GRAIN | Fish Sandwich |
| VEGETABLE + FRUIT + EGG + MILK | Veggie Omelette |
| VEGETABLE + FRUIT + EGG + SWEETENER | Veggie Omelette |
| VEGETABLE + FRUIT + EGG + GRAIN | Breakfast Burrito |
| VEGETABLE + FRUIT + MILK + SWEETENER | Curry |
| VEGETABLE + FRUIT + MILK + GRAIN | Curry |
| VEGETABLE + FRUIT + SWEETENER + GRAIN | Veggie Wrap |
| VEGETABLE + EGG + MILK + SWEETENER | Veggie Omelette |
| VEGETABLE + EGG + MILK + GRAIN | Breakfast Burrito |
| VEGETABLE + EGG + SWEETENER + GRAIN | Breakfast Burrito |
| VEGETABLE + MILK + SWEETENER + GRAIN | Curry |
| FRUIT + EGG + MILK + SWEETENER | Smoothie |
| FRUIT + EGG + MILK + GRAIN | Fruit Tart |
| FRUIT + EGG + SWEETENER + GRAIN | Fruit Tart |
| FRUIT + MILK + SWEETENER + GRAIN | Fruit Tart |
| EGG + MILK + SWEETENER + GRAIN | Cake |
| **— 5 tags —** | |
| MEAT + FISH + VEGETABLE + FRUIT + EGG | Mixed Grill |
| MEAT + FISH + VEGETABLE + FRUIT + MILK | Mixed Grill |
| MEAT + FISH + VEGETABLE + FRUIT + SWEETENER | Mixed Grill |
| MEAT + FISH + VEGETABLE + FRUIT + GRAIN | Paella |
| MEAT + FISH + VEGETABLE + EGG + MILK | Mixed Grill |
| MEAT + FISH + VEGETABLE + EGG + SWEETENER | Mixed Grill |
| MEAT + FISH + VEGETABLE + EGG + GRAIN | Paella |
| MEAT + FISH + VEGETABLE + MILK + SWEETENER | Mixed Grill |
| MEAT + FISH + VEGETABLE + MILK + GRAIN | Paella |
| MEAT + FISH + VEGETABLE + SWEETENER + GRAIN | Paella |
| MEAT + FISH + FRUIT + EGG + MILK | Glazed Surf and Turf |
| MEAT + FISH + FRUIT + EGG + SWEETENER | Glazed Surf and Turf |
| MEAT + FISH + FRUIT + EGG + GRAIN | Jambalaya |
| MEAT + FISH + FRUIT + MILK + SWEETENER | Glazed Surf and Turf |
| MEAT + FISH + FRUIT + MILK + GRAIN | Jambalaya |
| MEAT + FISH + FRUIT + SWEETENER + GRAIN | Jambalaya |
| MEAT + FISH + EGG + MILK + SWEETENER | Glazed Surf and Turf |
| MEAT + FISH + EGG + MILK + GRAIN | Jambalaya |
| MEAT + FISH + EGG + SWEETENER + GRAIN | Jambalaya |
| MEAT + FISH + MILK + SWEETENER + GRAIN | Jambalaya |
| MEAT + VEGETABLE + FRUIT + EGG + MILK | Sweet and Sour Stir Fry |
| MEAT + VEGETABLE + FRUIT + EGG + SWEETENER | Sweet and Sour Stir Fry |
| MEAT + VEGETABLE + FRUIT + EGG + GRAIN | Sweet and Sour Stir Fry |
| MEAT + VEGETABLE + FRUIT + MILK + SWEETENER | Sweet and Sour Stir Fry |
| MEAT + VEGETABLE + FRUIT + MILK + GRAIN | Sweet and Sour Stir Fry |
| MEAT + VEGETABLE + FRUIT + SWEETENER + GRAIN | Sweet and Sour Stir Fry |
| MEAT + VEGETABLE + EGG + MILK + SWEETENER | Sweet and Sour Stir Fry |
| MEAT + VEGETABLE + EGG + MILK + GRAIN | Goulash |
| MEAT + VEGETABLE + EGG + SWEETENER + GRAIN | Sweet and Sour Stir Fry |
| MEAT + VEGETABLE + MILK + SWEETENER + GRAIN | Sweet and Sour Stir Fry |
| MEAT + FRUIT + EGG + MILK + SWEETENER | Meat Omelette |
| MEAT + FRUIT + EGG + MILK + GRAIN | Burger |
| MEAT + FRUIT + EGG + SWEETENER + GRAIN | Burger |
| MEAT + FRUIT + MILK + SWEETENER + GRAIN | Burger |
| MEAT + EGG + MILK + SWEETENER + GRAIN | Burger |
| FISH + VEGETABLE + FRUIT + EGG + MILK | Sweet and Sour Filet |
| FISH + VEGETABLE + FRUIT + EGG + SWEETENER | Sweet and Sour Filet |
| FISH + VEGETABLE + FRUIT + EGG + GRAIN | Sweet and Sour Filet |
| FISH + VEGETABLE + FRUIT + MILK + SWEETENER | Sweet and Sour Filet |
| FISH + VEGETABLE + FRUIT + MILK + GRAIN | Sweet and Sour Filet |
| FISH + VEGETABLE + FRUIT + SWEETENER + GRAIN | Sweet and Sour Filet |
| FISH + VEGETABLE + EGG + MILK + SWEETENER | Sweet and Sour Filet |
| FISH + VEGETABLE + EGG + MILK + GRAIN | Grilled Fish and Vegetables |
| FISH + VEGETABLE + EGG + SWEETENER + GRAIN | Sweet and Sour Filet |
| FISH + VEGETABLE + MILK + SWEETENER + GRAIN | Sweet and Sour Filet |
| FISH + FRUIT + EGG + MILK + SWEETENER | Glazed Filet |
| FISH + FRUIT + EGG + MILK + GRAIN | Fish Sandwich |
| FISH + FRUIT + EGG + SWEETENER + GRAIN | Fish Sandwich |
| FISH + FRUIT + MILK + SWEETENER + GRAIN | Fish Sandwich |
| FISH + EGG + MILK + SWEETENER + GRAIN | Fish Sandwich |
| VEGETABLE + FRUIT + EGG + MILK + SWEETENER | Veggie Omelette |
| VEGETABLE + FRUIT + EGG + MILK + GRAIN | Breakfast Burrito |
| VEGETABLE + FRUIT + EGG + SWEETENER + GRAIN | Breakfast Burrito |
| VEGETABLE + FRUIT + MILK + SWEETENER + GRAIN | Curry |
| VEGETABLE + EGG + MILK + SWEETENER + GRAIN | Breakfast Burrito |
| FRUIT + EGG + MILK + SWEETENER + GRAIN | Fruit Cake |

---

## Unique Results
- Meat Kebab
- Glazed Tenderloin
- Meat Omelette
- Goulash
- Sweet and Sour Stir Fry
- Burger
- Grilled Fish Skewer
- Glazed Filet
- Grilled Fish and Vegetables
- Sweet and Sour Filet
- Fish Sandwich
- Surf and Turf
- Glazed Surf and Turf
- Mixed Grill
- Jambalaya
- Paella
- Roasted Vegetables
- Glazed Roasted Vegetables
- Veggie Omelette
- Curry
- Veggie Wrap
- Breakfast Burrito
- Fruit Salad
- Compote
- Smoothie
- Fruit Tart
- Fruit Cake
- Fried Eggs
- Omelette
- Eggs and Toast
- French Toast
- Custard
- Cheese
- Ice Cream
- Toast
- Sweet Roll
- Bread Pudding
- Cake
- Poisonous Porridge

---

## Positive Potion Effects (Brewed)

Every non-harmful status effect obtainable from a survival-brewable potion. (Luck is excluded — it has no survival brewing recipe. Resistance comes only from the Turtle Master potion, which also applies Slowness as a tradeoff.)

All potions are level 1 (no level II variants).

**Tier 1** — situational / low combat impact:
- Night Vision
- Water Breathing
- Slow Falling
- Invisibility

**Tier 2** — generally useful / moderate:
- Speed
- Jump Boost
- Fire Resistance
- Instant Health

**Tier 3** — powerful / high combat impact:
- Strength
- Regeneration
- Resistance (Turtle Master)

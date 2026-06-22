# /give meal_modifiers

```
/give @s brewingandbaking:<meal>[brewingandbaking:meal_modifiers={...}]
```

| Field | Type | Notes |
|---|---|---|
| `food_points` | double | negative = damage |
| `gold_tier` | int 0–3 | 0=none, 1=specked, 2=dusted, 3=plated |
| `effect` | resource ID | e.g. `"minecraft:speed"` |
| `duration_modifier` | double | multiplier on base 600-tick duration |

All fields optional.

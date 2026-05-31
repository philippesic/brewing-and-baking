package com.pp.brewingandbaking;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModPotions {
    private ModPotions() {}

    public static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(Registries.POTION, BrewingandBaking.MODID);

    public static final DeferredHolder<Potion, Potion> COFFEE = POTIONS.register(
            "coffee",
            () -> new Potion(
                    "coffee",
                    new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 400, 0),
                    new MobEffectInstance(MobEffects.JUMP, 400, 0)
            )
    );
}

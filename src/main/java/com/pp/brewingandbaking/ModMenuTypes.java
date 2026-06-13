package com.pp.brewingandbaking;

import com.pp.brewingandbaking.inventory.CookingPotMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(Registries.MENU, BrewingandBaking.MODID);

    public static final Supplier<MenuType<CookingPotMenu>> COOKING_POT_MENU =
            MENU_TYPES.register("cooking_pot_menu", () -> IMenuTypeExtension.create(CookingPotMenu::new));
}
package com.pp.brewingandbaking;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * A cooked meal. Display name gains a "Gold Specked / Dusted / Plated" prefix based on the
 * {@link ModDataComponents#LUXURY_POINTS} baked into the stack, and any rolled
 * {@link ModDataComponents#MEAL_EFFECT} is surfaced in the tooltip.
 */
public class MealItem extends Item {
    public MealItem(Properties props) {
        super(props);
    }

    @Override
    public Component getName(ItemStack stack) {
        Component base = super.getName(stack);
        int luxury = resolvedLuxury(stack);
        if (luxury <= 0) {
            return base;
        }
        String key = luxury >= 5 ? "gold_plated" : luxury >= 3 ? "gold_dusted" : "gold_specked";
        return Component.translatable("meal.brewingandbaking." + key, base);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
                                Consumer<Component> builder, TooltipFlag flag) {
        Optional<Holder<MobEffect>> effect = resolvedEffect(stack);
        effect.ifPresent(e ->
                builder.accept(e.value().getDisplayName().copy().withStyle(ChatFormatting.BLUE)));
    }

    private static int resolvedLuxury(ItemStack stack) {
        MealModifiers mod = stack.get(ModDataComponents.MEAL_MODIFIERS.get());
        if (mod != null) {
            return mod.luxuryPoints();
        }
        return stack.getOrDefault(ModDataComponents.LUXURY_POINTS.get(), 0);
    }

    private static Optional<Holder<MobEffect>> resolvedEffect(ItemStack stack) {
        MealModifiers mod = stack.get(ModDataComponents.MEAL_MODIFIERS.get());
        if (mod != null) {
            return mod.effect();
        }
        MobEffectInstance inst = stack.get(ModDataComponents.MEAL_EFFECT.get());
        return inst != null ? Optional.of(inst.getEffect()) : Optional.empty();
    }
}

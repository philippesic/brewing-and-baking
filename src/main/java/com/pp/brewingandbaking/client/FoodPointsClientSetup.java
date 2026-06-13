package com.pp.brewingandbaking.client;

import com.pp.brewingandbaking.BrewingandBaking;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;

/** Client-side mod-bus registration for the food heart tooltip renderer. */
@EventBusSubscriber(modid = BrewingandBaking.MODID, value = Dist.CLIENT)
public final class FoodPointsClientSetup {
    private FoodPointsClientSetup() {}

    @SubscribeEvent
    public static void registerTooltipFactories(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(FoodHeartsTooltip.class, FoodHeartsClientTooltip::new);
    }
}

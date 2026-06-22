package com.pp.brewingandbaking;

import com.pp.brewingandbaking.client.CookingPotScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = BrewingandBaking.MODID, dist = Dist.CLIENT)

@EventBusSubscriber(modid = BrewingandBaking.MODID, value = Dist.CLIENT)
public class BrewingandBakingClient {
    public BrewingandBakingClient(ModContainer container) {

        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.COOKING_POT_MENU.get(), CookingPotScreen::new);
    }
}

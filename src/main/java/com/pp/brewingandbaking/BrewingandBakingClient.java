package com.pp.brewingandbaking;

import com.pp.brewingandbaking.client.ConsentScreen;
import com.pp.brewingandbaking.client.CookingPotScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = BrewingandBaking.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = BrewingandBaking.MODID, value = Dist.CLIENT)
public class BrewingandBakingClient {
    public BrewingandBakingClient(ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        NeoForge.EVENT_BUS.register(FoodLogger.class);
    }

    @SubscribeEvent
    public static void onScreenOpening(ScreenEvent.Opening event) {
        if (event.getNewScreen() instanceof TitleScreen && !Config.CONSENT_SHOWN.get()) {
            event.setNewScreen(new ConsentScreen(event.getNewScreen()));
        }
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.COOKING_POT_MENU.get(), CookingPotScreen::new);
    }
}

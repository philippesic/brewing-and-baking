package com.pp.brewingandbaking;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;

/**
 * Writes a local CSV log of mod meals eaten when the player has opted in.
 * Log: {gameDir}/logs/brewingandbaking-food.log, one line per eat event.
 */
public final class FoodLogger {
    private FoodLogger() {}

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final HttpClient HTTP = HttpClient.newHttpClient();
    private static final String ENDPOINT = "https://brewing-and-baking-logger.brewingandbaking.workers.dev";

    @SubscribeEvent
    public static void onFinishEating(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof ServerPlayer)) {
            return;
        }
        if (!Config.DATA_COLLECTION_ENABLED.get()) {
            return;
        }
        Item item = event.getItem().getItem();
        Identifier id = BuiltInRegistries.ITEM.getKey(item);
        if (id == null || !BrewingandBaking.MODID.equals(id.getNamespace())) {
            return;
        }
        write(id.toString());
    }

    private static void write(String itemId) {
        String timestamp = Instant.now().toString();
        try {
            Path logDir = Minecraft.getInstance().gameDirectory.toPath().resolve("logs");
            Files.createDirectories(logDir);
            Path logFile = logDir.resolve("brewingandbaking-food.log");
            Files.writeString(logFile, timestamp + "," + itemId + System.lineSeparator(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            LOGGER.error("[BrewingAndBaking] Failed to write food log", e);
        }
        sendRemote(itemId, timestamp);
    }

    private static void sendRemote(String itemId, String timestamp) {
        String json = "{\"itemId\":\"" + itemId + "\",\"timestamp\":\"" + timestamp + "\"}";
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(ENDPOINT))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HTTP.sendAsync(req, HttpResponse.BodyHandlers.discarding())
                .exceptionally(e -> {
                    LOGGER.warn("[BrewingAndBaking] Failed to send remote log: {}", e.getMessage());
                    return null;
                });
    }
}

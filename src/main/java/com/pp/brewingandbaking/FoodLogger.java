package com.pp.brewingandbaking;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

/**
 * Accumulates food-eat events in memory and flushes them as a batch to the remote
 * analytics endpoint every 5 minutes and on server stop.
 * Only logs the local player's eating; other players on LAN are never tracked.
 * Local log: {gameDir}/logs/brewingandbaking-food.log (one line per eat, for player transparency).
 * Pending backup: {gameDir}/logs/brewingandbaking-pending.csv (cleared on successful flush).
 */
public final class FoodLogger {
    private FoodLogger() {}

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private static final String ENDPOINT = "https://brewing-and-baking-logger.brewingandbaking.workers.dev";
    // Injected at build time from the bnb_logger_key Gradle property; must match the AUTH_KEY Wrangler secret.
    private static final String AUTH_KEY = BuildConfig.LOGGER_KEY;

    private static final int MAX_PAYLOAD_BYTES = 65_536;

    private static final ConcurrentHashMap<String, LongAdder> COUNTS = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Boolean> IS_MEAL_MAP = new ConcurrentHashMap<>();
    private static volatile boolean backupLoaded = false;
    private static volatile String sessionId;

    private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "BnB-FoodLogger");
        t.setDaemon(true);
        return t;
    });

    public static void init() {
        sessionId = UUID.randomUUID().toString();
        SCHEDULER.scheduleAtFixedRate(FoodLogger::flush, 5, 5, TimeUnit.MINUTES);
    }

    @SubscribeEvent
    public static void onFinishEating(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof ServerPlayer)) return;
        if (!Config.DATA_COLLECTION_ENABLED.get()) return;
        if (!event.getItem().has(DataComponents.FOOD)) return;
        var local = Minecraft.getInstance().player;
        if (local == null || !event.getEntity().getUUID().equals(local.getUUID())) return;

        if (!backupLoaded) {
            synchronized (FoodLogger.class) {
                if (!backupLoaded) {
                    loadBackup();
                    backupLoaded = true;
                }
            }
        }

        Item item = event.getItem().getItem();
        Identifier id = BuiltInRegistries.ITEM.getKey(item);
        if (id == null) return;

        String itemId = id.toString();
        boolean meal = isMeal(item);

        COUNTS.computeIfAbsent(itemId, k -> new LongAdder()).increment();
        IS_MEAL_MAP.putIfAbsent(itemId, meal);
        writeLocal(itemId, meal);
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        flush();
    }

    // -------------------------------------------------------------------------

    private static boolean isMeal(Item item) {
        for (var deferred : ModMeals.ALL) {
            if (deferred.get() == item) return true;
        }
        return false;
    }

    private static void writeLocal(String itemId, boolean meal) {
        try {
            Path logDir = Minecraft.getInstance().gameDirectory.toPath().resolve("logs");
            Files.createDirectories(logDir);
            Path logFile = logDir.resolve("brewingandbaking-food.log");
            Files.writeString(logFile, itemId + "," + meal + System.lineSeparator(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            LOGGER.error("[BrewingAndBaking] Failed to write local food log", e);
        }
    }

    // -------------------------------------------------------------------------

    private static synchronized void flush() {
        if (COUNTS.isEmpty()) return;

        Map<String, Long> snapshot = new HashMap<>();
        for (var entry : COUNTS.entrySet()) {
            long count = entry.getValue().sumThenReset();
            if (count > 0) snapshot.put(entry.getKey(), count);
        }
        if (snapshot.isEmpty()) return;

        Path backup = backupPath();
        writeBackup(backup, snapshot);

        String json = buildJson(snapshot);
        byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);
        if (jsonBytes.length > MAX_PAYLOAD_BYTES) {
            LOGGER.warn("[BrewingAndBaking] Food log batch too large ({} bytes), skipping", jsonBytes.length);
            return;
        }

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(ENDPOINT))
                .timeout(Duration.ofSeconds(15))
                .header("Content-Type", "application/json")
                .header("X-BnB-Key", AUTH_KEY)
                .POST(HttpRequest.BodyPublishers.ofByteArray(jsonBytes))
                .build();
        try {
            HttpResponse<Void> res = HTTP.send(req, HttpResponse.BodyHandlers.discarding());
            if (res.statusCode() == 200) {
                Files.deleteIfExists(backup);
            } else {
                LOGGER.warn("[BrewingAndBaking] Remote log rejected with status {}", res.statusCode());
            }
        } catch (Exception e) {
            LOGGER.warn("[BrewingAndBaking] Failed to send food log batch: {}", e.getMessage());
        }
    }

    private static String buildJson(Map<String, Long> snapshot) {
        JsonArray entries = new JsonArray();
        for (var entry : snapshot.entrySet()) {
            String itemId = entry.getKey();
            JsonObject obj = new JsonObject();
            obj.addProperty("itemId", itemId);
            obj.addProperty("isMeal", IS_MEAL_MAP.getOrDefault(itemId, false));
            obj.addProperty("count", entry.getValue());
            entries.add(obj);
        }
        JsonObject root = new JsonObject();
        root.addProperty("userId", sessionId);
        root.add("entries", entries);
        return root.toString();
    }

    // -------------------------------------------------------------------------

    private static Path backupPath() {
        return Minecraft.getInstance().gameDirectory.toPath()
                .resolve("logs/brewingandbaking-pending.csv");
    }

    private static void writeBackup(Path path, Map<String, Long> snapshot) {
        try {
            StringBuilder sb = new StringBuilder();
            for (var entry : snapshot.entrySet()) {
                String itemId = entry.getKey();
                boolean meal = IS_MEAL_MAP.getOrDefault(itemId, false);
                sb.append(itemId).append(",").append(meal).append(",").append(entry.getValue())
                  .append(System.lineSeparator());
            }
            Files.writeString(path, sb.toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            LOGGER.error("[BrewingAndBaking] Failed to write pending backup", e);
        }
    }

    private static void loadBackup() {
        Path backup = backupPath();
        if (!Files.exists(backup)) return;
        try {
            for (String line : Files.readAllLines(backup)) {
                String[] parts = line.split(",");
                if (parts.length != 3) continue;
                String itemId = parts[0];
                boolean meal = Boolean.parseBoolean(parts[1]);
                long count = Long.parseLong(parts[2].trim());
                COUNTS.computeIfAbsent(itemId, k -> new LongAdder()).add(count);
                IS_MEAL_MAP.putIfAbsent(itemId, meal);
            }
            LOGGER.info("[BrewingAndBaking] Loaded pending food log backup");
        } catch (IOException | NumberFormatException e) {
            LOGGER.error("[BrewingAndBaking] Failed to load pending backup", e);
        }
    }
}

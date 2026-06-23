package com.pp.brewingandbaking;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue DATA_COLLECTION_ENABLED = BUILDER
            .comment("Allow Brewing & Baking to log meals eaten anonymously.")
            .define("dataCollectionEnabled", false);

    public static final ModConfigSpec.BooleanValue CONSENT_SHOWN = BUILDER
            .comment("Whether the first-run data consent screen has been shown. Set to false to show it again on next launch.")
            .define("consentShown", false);

    public static final ModConfigSpec SPEC = BUILDER.build();
}

package com.antibat.nobats;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Common configuration. Registered as {@link net.neoforged.fml.config.ModConfig.Type#COMMON}, so both
 * the client and a dedicated server read it from {@code <game directory>/config/nobats-common.toml}.
 */
public final class NoBatsConfig {
    public static final ModConfigSpec SPEC;

    private static final ModConfigSpec.BooleanValue REMOVE_BATS;
    private static final ModConfigSpec.BooleanValue REMOVE_EXISTING_BATS;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        REMOVE_BATS = builder
                .comment(
                        "Master switch. When true, bats are never allowed to enter a level.",
                        "This blocks natural spawns, structure and world generation spawns, spawners,",
                        "spawn eggs, the /summon command and bats loaded from a save file.")
                .define("remove_bats", true);

        REMOVE_EXISTING_BATS = builder
                .comment(
                        "When true, bats that already exist in a saved world are removed as well: a bat",
                        "restored from a chunk is dropped instead of being added to the level, and it",
                        "disappears from the save file the next time the chunk is written.",
                        "Set to false to keep bats that are already in the world and only block new ones.",
                        "Has no effect while remove_bats is false.")
                .define("remove_existing_bats", true);

        SPEC = builder.build();
    }

    private NoBatsConfig() {}

    public static boolean isRemoveBatsEnabled() {
        return REMOVE_BATS.get();
    }

    public static boolean isRemoveExistingBatsEnabled() {
        return REMOVE_EXISTING_BATS.get();
    }
}

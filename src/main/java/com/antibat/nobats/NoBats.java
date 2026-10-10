package com.antibat.nobats;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

/**
 * No Bats - keeps bats out of the world on NeoForge 1.21.1.
 * <p>
 * See {@link BatSpawnBlocker} for the blocking logic and {@link NoBatsConfig} for the settings.
 */
@Mod(NoBats.MOD_ID)
public final class NoBats {
    /** Must match {@code mod_id} in gradle.properties and the metadata template. */
    public static final String MOD_ID = "nobats";

    public static final Logger LOGGER = LogUtils.getLogger();

    public NoBats(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, NoBatsConfig.SPEC);
        NeoForge.EVENT_BUS.addListener(BatSpawnBlocker::onEntityJoinLevel);
        LOGGER.info("No Bats loaded: bats will not be allowed to enter any level");
    }
}

package com.antibat.nobats;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ambient.Bat;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

/**
 * Blocks bats from ever entering a level.
 * <p>
 * NeoForge fires {@link EntityJoinLevelEvent} from both {@code Level#addFreshEntity} and
 * {@code PersistentEntitySectionManager#addEntity}, which covers every way an entity can appear in a
 * level: natural spawns, structure and world generation, spawners, spawn eggs, commands and entities
 * loaded from a saved chunk. The event is cancellable and, when cancelled, the entity is simply never
 * added to the level, so a bat loaded from a save file is dropped as well.
 * <p>
 * The handler deliberately never touches the level (no block lookups, no chunk access): the event may
 * fire before the underlying chunk reaches {@code ChunkStatus.FULL}, and world access at that point can
 * deadlock chunk loading.
 */
public final class BatSpawnBlocker {
    private BatSpawnBlocker() {}

    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!NoBatsConfig.isRemoveBatsEnabled()) {
            return;
        }

        Entity entity = event.getEntity();
        if (!(entity instanceof Bat)) {
            return;
        }

        // Bats restored from a save file are only dropped when the user asked for a bat-free world.
        if (event.loadedFromDisk() && !NoBatsConfig.isRemoveExistingBatsEnabled()) {
            return;
        }

        event.setCanceled(true);

        if (NoBats.LOGGER.isDebugEnabled()) {
            NoBats.LOGGER.debug("Blocked a bat from joining {}", event.getLevel().dimension().location());
        }
    }
}

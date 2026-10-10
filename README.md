# No Bats

**English** | [简体中文](README_CN.md)

A NeoForge **1.21.1** mod that keeps bats **out of the world entirely**.

- Blocks every way a bat can enter a level: natural spawns, structure and world generation, spawners, spawn eggs and the `/summon` command.
- Bats that already exist in a saved world are dropped when their chunk loads and disappear from the save file the next time it is written.
- Pure runtime event handling: **no Mixin, no changes to vanilla data**, so it will not conflict with mods that alter mobs.
- Two switches you can flip in the config file at any time.

## Requirements

| | Version |
| --- | --- |
| Minecraft | 1.21.1 |
| NeoForge | any 21.1.x (tested on 21.1.1 and 21.1.256) |
| Java (to run the game) | 21 |

NeoForge compatibility: the mod is compiled against `21.1.1`, the first release of the 21.1 line, and declares its runtime dependency as `[21.1.1,)`. Any NeoForge build for Minecraft 1.21.1 therefore works, including older ones such as `21.1.250`; only 21.0.x and 21.2+ are rejected. To change the range, edit `neo_version_range` in `gradle.properties` (`neo_version` is just the compile target).

## Installation

1. Install Minecraft 1.21.1 and NeoForge 21.1.x.
2. Drop `build/libs/nobats-1.0.0.jar` into `.minecraft/mods/` (or the server's `mods/` folder).
3. Start the game or server. No further setup is required.

The mod works on both sides, but installing it on the **server** alone is enough, since the server is authoritative.

## Configuration

`config/nobats-common.toml` is generated on first launch (`.minecraft/config/` on a client, `<server folder>/config/` on a server):

```toml
#Master switch. When true, bats are never allowed to enter a level.
remove_bats = true

#When true, bats that already exist in a saved world are removed as well.
remove_existing_bats = true
```

| Option | Default | Description |
| --- | --- | --- |
| `remove_bats` | `true` | Master switch. When `false` the mod does not touch bats at all. |
| `remove_existing_bats` | `true` | When `true`, bats already stored in a save are removed as well when their chunk loads. When `false`, existing bats are kept and only newly spawned ones are blocked. |

Restart the client/server after changing the config. It uses NeoForge's standard configuration system (type `COMMON`) and does not rely on network syncing.

## How it works

NeoForge fires `EntityJoinLevelEvent` whenever an entity joins a level. It covers both `Level#addFreshEntity` and `PersistentEntitySectionManager#addEntity`, which is where natural spawns, world generation entities, spawners, spawn eggs, commands and entities loaded from disk all pass through. The event is cancellable, and **a cancelled entity is never added to the level at all**.

So the mod does exactly one thing:

```java
// BatSpawnBlocker#onEntityJoinLevel (simplified)
if (!NoBatsConfig.isRemoveBatsEnabled()) return;
if (!(event.getEntity() instanceof Bat)) return;
if (event.loadedFromDisk() && !NoBatsConfig.isRemoveExistingBatsEnabled()) return;
event.setCanceled(true);
```

The handler deliberately **never touches world data** (no block lookups, no chunk access): the event may fire before the underlying chunk reaches `ChunkStatus.FULL`, and accessing the world at that point can deadlock chunk loading.

## Building from source

**JDK 21** is required (Minecraft 1.21.1 mandates it). Example for this machine:

```powershell
# 1) Install JDK 21 to F:\JDK\JDK21 and Gradle 9.2.1 to F:\MCresource\gradle-9.2.1 (or use your own)
# 2) Build
cd 'F:\tmp\anti bats'
$env:JAVA_HOME = 'F:\JDK\JDK21'
& 'F:\MCresource\gradle-9.2.1\bin\gradle.bat' build
```

Output: `build/libs/nobats-1.0.0.jar`.

Alternative (without setting `JAVA_HOME`): set `jdk21_path` in `gradle.properties`, or pass `-Pjdk21_path=F:/JDK/JDK21`; the build script then uses that directory as the Java 21 toolchain.

You can also use the bundled Gradle Wrapper (the first run downloads Gradle 9.2.1 into `~/.gradle/wrapper/dists`, about 135 MB):

```powershell
$env:JAVA_HOME = 'F:\JDK\JDK21'
cd 'F:\tmp\anti bats'
.\gradlew.bat build
```

The first build downloads the NeoForge/Minecraft dependencies and decompiles them, which can take 10–40 minutes.

Common tasks:

```powershell
& 'F:\MCresource\gradle-9.2.1\bin\gradle.bat' runClient   # launch a client with the mod
& 'F:\MCresource\gradle-9.2.1\bin\gradle.bat' runServer   # launch a server with the mod (accept eula.txt on first run and set online-mode to false)
```

## License

MIT, see [LICENSE](LICENSE). All code here was written for this project and contains no third-party sources.

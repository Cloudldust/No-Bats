# No Bats

[English](README.md) | **简体中文**

一个 NeoForge **1.21.1** 模组：让蝙蝠**彻底不出现在世界里**。

- 拦截所有让蝙蝠进入世界的途径：自然生成、结构/世界生成、刷怪笼、刷怪蛋、`/summon` 指令。
- 存档里已经存在的蝙蝠在区块载入时也会被丢弃，下次保存后即从存档中消失。
- 纯运行时事件实现，**不使用 Mixin，不修改原版数据**，与改动生物的模组几乎不会冲突。
- 两个开关可随时在配置文件里调整。

## 环境要求

| 项目 | 版本 |
| --- | --- |
| Minecraft | 1.21.1 |
| NeoForge | 任意 21.1.x（测试于 21.1.1 与 21.1.256） |
| Java（运行游戏） | 21 |

NeoForge 兼容性：模组针对 21.1 线首个版本 `21.1.1` 编译，运行期依赖声明为 `[21.1.1,)`。因此 1.21.1 上的任何 NeoForge 版本（包括 `21.1.250` 这类较早的构建）都能加载，只有 21.0.x 或 21.2+ 会被拒绝。若要调整该范围，改 `gradle.properties` 的 `neo_version_range` 即可（`neo_version` 只是编译目标）。

## 安装

1. 安装 Minecraft 1.21.1 与 NeoForge 21.1.x。
2. 把 `build/libs/nobats-1.0.0.jar` 放进 `.minecraft/mods/`（服务端则放入服务器的 `mods/`）。
3. 启动游戏或服务器即可，无需额外设置。

模组为双端模组，但只安装在**服务端**也能生效（服务端才是权威判定方）。

## 配置

首次启动后会生成 `config/nobats-common.toml`（客户端在 `.minecraft/config/`，服务端在服务器根目录的 `config/`）：

```toml
#Master switch. When true, bats are never allowed to enter a level.
remove_bats = true

#When true, bats that already exist in a saved world are removed as well.
remove_existing_bats = true
```

| 配置项 | 默认 | 说明 |
| --- | --- | --- |
| `remove_bats` | `true` | 总开关。为 `false` 时本模组不干预任何蝙蝠。 |
| `remove_existing_bats` | `true` | 为 `true` 时，存档里已有的蝙蝠在区块载入时也会被清除。为 `false` 时保留旧蝙蝠，只拦截新生成的蝙蝠。 |

改完配置需要重启客户端/服务端；配置读取用的是 NeoForge 的标准配置系统（`COMMON` 类型），不依赖网络同步。

## 工作原理

NeoForge 在实体被加入世界时触发 `EntityJoinLevelEvent`，它同时覆盖 `Level#addFreshEntity` 和 `PersistentEntitySectionManager#addEntity` 两条路径——自然生成、世界生成区块实体、刷怪笼、刷怪蛋、指令、从存档载入的实体都经过这里。该事件可取消，**取消后实体根本不会被加入世界**。

模组只做一件事：

```java
// BatSpawnBlocker#onEntityJoinLevel（简化）
if (!NoBatsConfig.isRemoveBatsEnabled()) return;
if (!(event.getEntity() instanceof Bat)) return;
if (event.loadedFromDisk() && !NoBatsConfig.isRemoveExistingBatsEnabled()) return;
event.setCanceled(true);
```

处理函数**不访问世界数据**（不查方块、不加载区块）：该事件可能在区块达到 `ChunkStatus.FULL` 之前触发，此时访问世界会造成区块加载死锁。

## 从源码构建

需要 **JDK 21**（Minecraft 1.21.1 强制要求）。本机示例：

```powershell
# 1) 安装 JDK 21 到 F:\JDK\JDK21、Gradle 9.2.1 到 F:\MCresource\gradle-9.2.1（或使用你自己的）
# 2) 构建
cd 'F:\tmp\anti bats'
$env:JAVA_HOME = 'F:\JDK\JDK21'
& 'F:\MCresource\gradle-9.2.1\bin\gradle.bat' build
```

产物：`build/libs/nobats-1.0.0.jar`。

替代方式（不设置 `JAVA_HOME`）：在 `gradle.properties` 里设置 `jdk21_path`，或者用 `-Pjdk21_path=F:/JDK/JDK21` 传参，构建脚本会把它当作 Java 21 工具链。

也可以直接用仓库自带的 Gradle Wrapper（首次运行会下载 Gradle 9.2.1 到 `~/.gradle/wrapper/dists`，约 135 MB）：

```powershell
$env:JAVA_HOME = 'F:\JDK\JDK21'
cd 'F:\tmp\anti bats'
.\gradlew.bat build
```

首次构建会下载 NeoForge/Minecraft 依赖并反编译，耗时可能 10–40 分钟。

常用任务：

```powershell
& 'F:\MCresource\gradle-9.2.1\bin\gradle.bat' runClient   # 启动带模组的客户端
& 'F:\MCresource\gradle-9.2.1\bin\gradle.bat' runServer   # 启动带模组的服务端（首次需接受 eula.txt，并把 online-mode 设为 false）
```

## 许可证

MIT，见 [LICENSE](LICENSE)。所有代码为本项目独立编写，不包含第三方源码。

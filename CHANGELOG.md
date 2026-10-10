# Changelog

## 1.0.0

- 初始版本，适配 Minecraft 1.21.1 / NeoForge 21.1.x。
- 通过取消 `EntityJoinLevelEvent` 阻止蝙蝠进入世界，覆盖自然生成、结构/世界生成、刷怪笼、刷怪蛋与 `/summon`。
- 新增 `remove_bats` 与 `remove_existing_bats` 两个配置项，后者可清理存档中已存在的蝙蝠。
- 兼容性：改为针对 21.1 线首个版本 `21.1.1` 编译，运行期依赖放宽为 `[21.1.1,)`，任意 1.21.1 的 NeoForge 版本（如 21.1.250、21.1.256）都能加载，不再要求不低于编译版本。

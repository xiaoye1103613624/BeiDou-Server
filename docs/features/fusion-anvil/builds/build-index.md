# 融合外观 — 构建/资源索引

| 项 | 路径 | 备注 |
|----|------|------|
| 插件产物 | `E:\project\BeiDou-ijl15\out\Release\ijl15.dll` | 2026-09-17 PostBuild → 客户端 |
| 客户端插件 | `E:\MXD\BeiDou-Client_S9\ijl15.dll` | 与 out 同大小/时间戳 |
| Custom.wz | `E:\MXD\BeiDou-Client_S9\Custom.wz` | 含 `UI/UIWindow.img/Synthesizing`；旧版备份 `Custom.wz.old_small` |
| Sound | `E:\MXD\BeiDou-Client_S9\Data\Sound\UI.img` | 含 `anvil`；备份 `UI.img.bak_pre_anvil` |
| 参考 WZ | `E:\MXD\扩展改动\融合外观\Anvil WZ\` | UI/Sound/Item 源 |
| 服务端改动 | `UseCashItemHandler.refreshAnvilEquip` | 补发 `updateCharLook`；IDE 热更即可 |
| ijl15 工程修补 | `ezorsia.vcxproj` 补回 `weapontint.cpp` / `itemeff.cpp` | 解除链接失败（与幻化无关的既有缺口） |

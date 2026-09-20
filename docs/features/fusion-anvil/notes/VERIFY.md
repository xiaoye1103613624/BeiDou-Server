# 融合外观 — 验收清单

日期：2026-09-17  
客户端：`E:\MXD\BeiDou-Client_S9`  
插件：`ijl15.dll`（含还原按钮 GBK「还原」）

## 前置

- [ ] 服务端已含 `anvilItemId` 列与 `UseCashItemHandler` itemType 590（含 `updateCharLook` 自刷）
- [ ] 客户端 `Custom.wz` 含 `UI/UIWindow.img/Synthesizing`
- [ ] 客户端 `Data/Sound/UI.img/anvil` 存在
- [ ] 现金栏有 `5900000`；准备同类型两件装备（外观源未穿戴）

## 用例

1. **融合**：双击锤子 → 左放外观源、右放基底 → 确定  
   - 外观源消失；基底属性在；**自己立刻**变成外观源外形；他人同图可见。
2. **存档**：换频/重登后外观仍在。
3. **还原**：再开窗口，右槽放已幻化基底（可不放左槽）→「还原」  
   - 外形恢复；扣 1 锤；自己与他人立刻更新。
4. **无幻化还原**：右槽无 anvil → 点还原应提示且不扣锤（若按钮灰显则无法点）。
5. **选角进图**：有/无幻化装备均可进图，无无提示闪退。

## 本版不验收（已知限制）

- 装备 tip「外观 : xxx (幻化)」行（Tip soft-disable）
- 背包格子角标图标
- 交易/雇佣商店买家侧外观

## 构建记录

| 产物 | 路径 / 说明 |
|------|-------------|
| ijl15.dll | `BeiDou-ijl15\out\Release\ijl15.dll` → 已 PostBuild 到客户端 |
| Custom.wz | 客户端已换含 Synthesizing 版本（旧小文件备份 `Custom.wz.old_small`） |
| Sound UI.img | 已写入 `anvil`（备份 `UI.img.bak_pre_anvil`） |
| 服务端 | `refreshAnvilEquip` 补发 `updateCharLook`；IDE 热编译或带 JDK 的 `mvn compile` |

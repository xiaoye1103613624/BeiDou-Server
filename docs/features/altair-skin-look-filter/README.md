# 阿尔泰皮肤 AvatarLook 全覆盖过滤

> 状态：已闭环（含选频道闪退修复）  
> 适用客户端/协议版本：GMS v83 / BeiDou  
> 项目名称：BeiDou-Server_s9  
> 作者/日期：2026-09-15  
> 产物目录：docs/features/altair-skin-look-filter/

## 1. 背景与目标

- **问题**：111 款阿尔泰 Cap 皮肤（`1008900–1009999`）虽有全身立绘（`z=characterStart`），但 `vslot` 仅遮头发，AvatarLook 仍广播其它装备，导致武器/衣服等外观叠在皮肤上。
- **目标**：穿上阿尔泰皮肤后，角色外观仅为皮肤立绘；其它装备外观不显示；装备属性仍生效。
- **明确不做**：不接「动漫皮肤」鞋子包 `1076000+`；不再迁点装武器槽；不改插件；**不做幽灵卸下/清空装备栏**（纯外观遮罩）。

## 2. 影响范围

| 层级 | 路径/模块 | 变更类型 |
| --- | --- | --- |
| 服务端 | `AltairSkinItems.java` | 检测穿戴、配置开关 |
| 服务端 | `PacketCreator.addCharLook/addCharEquips` | AvatarLook 过滤 |
| 服务端 | `Character.equipChanged` | 自身也下发 UPDATE_CHAR_LOOK |
| DB | `V1.11.51__altair_skin_look_filter_config.sql` | `use_altair_skin_look_filter` |
| i18n | `log_zh_CN` / `log_en_US` | 过滤生效日志 |
| 客户端 IMG | `E:\MXD\BeiDou-Client_S9\Data\Character\Cap` | `islot=Cp` + 全身 `vslot`（111/111） |
| 服务端 WZ | `wz/Character.wz/Cap/010089xx–010099xx` | 同上 `islot`/`vslot` |

## 3. 最终实现逻辑

### 3.1 主流程（纯外观遮罩）

1. 角色装备点装帽子槽 `-101`（或装备栏中）存在 `AltairSkinItems` 名单内 Cap。
2. `GameConfig` 开关 `use_altair_skin_look_filter` 为 true（缺省 true）。
3. `addCharLook`：**下发真实 face/hair**（禁止写 `0`，避免 `00000000.img` 闪退）。
4. `addCharEquips`：仅写入 Cap 槽 `1` = 皮肤 itemId；masked 空；现金武器 `0`；宠物照常（**不改 EQUIPPED 库存**）。
5. Cap WZ：`islot=Cp`（非 `HrCp`）+ 全身 `vslot`（含 `Wp/Sr/Gl/Gw/Wc/...`），本地绘制也遮住武器/披风等部件。
6. `equipChanged` / `syncAltairSkinSelfLook`：向自身推送过滤后的 AvatarLook；装备栏 UI 保持完整。

### 3.2 选角 / 选频道展示

- **应显示完整阿尔泰皮肤立绘**，不是空白脸。
- 脸/发仍发真实 ID，客户端会加载，但 Cap 大画布 `z=characterStart` 盖住；其它装备不进 AvatarLook，故选角列表与进图后一致看到皮肤。

### 3.3 数据 / 协议

- 不改协议结构，只改变 AvatarLook 内容。
- 真实 `EQUIPPED` 库存不变，战斗属性仍按全套装备计算。

### 3.4 开关

- `server.Game Mechanics.use_altair_skin_look_filter`（Boolean，可热重载）。
- 关闭后行为回退为原全装备外观广播。

## 4. 选频道闪退根因与修复（2026-09-15）

| 项 | 内容 |
| --- | --- |
| 现象 | 选频道闪退，`error code : -2147287038 (0x80030002)` |
| 含义 | `STG_E_FILENOTFOUND`（文件未找到） |
| 证据 | 客户端 `beidou-wz-last.log`：`getobj_inflight=Character/00000000.img` |
| 根因 | `addCharLook` 将 face/hair 写成 `0`，客户端加载不存在的 `00000000.img` |
| 修复 | 恢复真实 face/hair；仅过滤装备列表，靠 Cap 立绘覆盖 |

## 4.1 进游戏闪退根因与修复（2026-09-16）

| 项 | 内容 |
| --- | --- |
| 现象 | 选角进服约 1s 内闪退（穿阿尔泰） |
| 证据 | `beidou-crash`：`TSecType::GetData` @`0x428743`，栈在 `OnInventoryOperation`；见 `evidence/enter-game-crash-inventory-op.md` |
| 根因 | 曾用幽灵 REMOVE 藏身体槽，与 CharInfo 不一致触发 `OnInventoryOperation` 崩溃 |
| 修复 | **撤销幽灵卸下**；仅 AvatarLook + Cap `islot`/`vslot` 纯外观遮罩 |

## 5. 资源与同步

- 服务端 WZ Cap XML 已存在；客户端 111 个 `.img` 与资源包 SHA256 全匹配（见 `resources/cap-verify.txt`）。
- **纯外观遮罩字段**（2026-09-16）：`islot=Cp`，`vslot=CpH1H2H3H4H5H6HfHbHxHsAfAyAsAeMaPnSoGlGwSrSiWpWoWc`；客户端 `DumpIslot` `VERIFY ok=111 bad=0`（见 `resources/cap-islot-vslot-verify.txt`）。
- 外观道具结论：**完整资源**（非仅图标）。

## 6. 编码与 i18n

- 日志：`AltairSkinItems.lookFilter.active` / `lookFilter.cleared`
- 配置文案：`lang_resources` zh-CN / en-US

## 7. 产物清单

| 类型 | 路径 | 说明 |
| --- | --- | --- |
| 文档 | README.md | 本文件 |
| 资源清单 | resources/cap-verify.txt | 111 Cap 校验结果 |
| 证据 | evidence/channel-select-crash.md | 闪退定位摘要 |

## 8. 验证记录

- 编译：`mvn -pl gms-server -DskipTests compile`（2026-09-16 通过）
- 客户端 Cap 资源：match=111；`islot`/`vslot`：`BatchAltairVslot skip=111` + `DumpIslot ok=111`
- 手工验收：
  1. 重启服务端与客户端后进服：**装备栏完整**（衣服/武器等图标都在）
  2. 选角/他人：只见皮肤立绘
  3. 进图自身：皮肤遮罩全身（含武器/披风）；属性仍吃真实装备
  4. 卸下皮肤 → 外观恢复

## 9. 预留、风险与回滚

- 风险：极少数动作帧若 Cap 未完全盖住，可能短暂露出真脸发；若出现再评估空白脸发资源或扩展 vslot（本版不做）。
- 回滚：关配置开关，或还原 `PacketCreator`/`Character.equipChanged` 相关改动。

## 10. 对外交流摘要

穿阿尔泰时：AvatarLook 只广播皮肤 Cap（保留真实脸/发）；Cap `islot=Cp` + 全身 `vslot` 做本地遮罩；装备栏与真实 EQUIPPED 不动；属性仍吃真实装备。

# V16.1 vs Client_1 (xiaoye) Data 对比摘要

- **REFERENCE (V16.1)**: `E:\mxd_soft\2.客户端\083\BeiDou-ClientV16.1\BeiDou-Client\Data`
- **TARGET (Client_1)**: `E:\mxd_soft\2.客户端\083\beidou_client_xiaoye\BeiDou-Client_1\Data`
- 生成时间: 2026-07-12

## 1. 顶层目录

- 共有 **15** 个: Character, Effect, Etc, Item, Map, Mob, Morph, Npc, Quest, Reactor, Skill, Sound, String, TamingMob, UI
- **仅 V16.1**: Base（Client_1 无此顶层目录）
- 结论: **基本一致**；V16.1 多 `Base/`，其余顶层与 Client_1 对齐。

## 2. 文件总数

| 指标 | 数量 |
|------|------|
| V16.1 Data 文件总数 | **89,304** |
| Client_1 Data 文件总数 | **86,491** |
| V16.1 有、Client_1 无 | **36,751** |
| Client_1 有、V16.1 无（反向） | **33,938** |
| 两侧共有（按相对路径） | **52,553** |

## 重要排除 / 定制资源

`UI/UIWindow.img`：V16.1 **203,321** 字节，Client_1 **12,240,903** 字节 — Client_1 为定制大版本，**不应**用 V16.1 覆盖。
该文件两侧均存在，未计入缺失列表。
- 与 CHECKPOINT 一致：追加时 **SKIP** `BasicEff`、`DamageSkin`、`0591/0592/590*` 及 Client_1 侧备份/`.bak` 文件。
- Client_1 反向独有中含大量 `Effect/BasicEff.img.*` 备份与 `Item/Cash/0591.img` 等定制内容，属预期。

## 3. Client_1 缺失 — 按类别

| 类别 | 缺失数 |
|------|--------|
| Map | 15,915 |
| Character | 14,469 |
| Npc | 3,339 |
| Mob | 2,913 |
| Item | 90 |
| Etc | 9 |
| Effect | 6 |
| Base | 4 |
| Morph | 4 |
| (root) | 2 |

## 4. 缺口最大的类别（Top）

1. **Map** — 15915
2. **Character** — 14469
3. **Npc** — 3339
4. **Mob** — 2913
5. **Item** — 90
6. **Etc** — 9
7. **Effect** — 6
8. **Base** — 4

## 5. Client_1 独有（反向 diff）

合计 **33,938** 个路径仅存在于 Client_1（含定制、备份、历史追加残留）。

| 类别 | 独有数 |
|------|--------|
| Character | 13,624 |
| Npc | 11,951 |
| Map | 5,572 |
| Mob | 1,943 |
| Reactor | 449 |
| Item | 177 |
| Morph | 75 |
| Skill | 64 |
| UI | 27 |
| Effect | 26 |
| String | 22 |
| Sound | 5 |

## 6. 与 V15 append 评估对照（2026-07-10）

- 历史 `append_candidates.txt`：**41,134** 条（相对 Data 的路径，无 `.wz` 后缀）。
- 2026-07-10 已对其中 **41,134** 条执行整文件 robocopy 到 xiaoye 客户端。
- 当前 **V16 有 / Client_1 无** 与 V15 候选交集：**0** → 说明 V15 批次候选在 Client_1 侧已齐。
- V16.1 仍包含的 V15 候选路径：**33,932** / 41,134（**82.5%**）。
- V15 候选中 **不在** V16.1 的路径：**7,202**（多为旧源独有，V16 分支已删改）。
- 相对 V15 候选清单，V16 相对 Client_1 **新增缺口**（在 V16 有、Client_1 无，且不在 V15 候选中）：**36,751**。

**是否超集？**
- 对「已 robocopy 的 V15 候选」：Client_1 已覆盖，V16 与 V15 源 **不是严格超集**（约 7.2k 条 V15 路径在 V16 中不存在）。
- 对「继续从 V16 补资源」：V16 相对 Client_1 仍有 **36,751** 条可追加路径，且与已完成 V15 批次 **不重叠**。

## 7. 建议

**值得从 V16.1 分批追加**，但应沿用 SKIP 策略：优先 **Map → Character → Npc → Mob**（占缺口约 96%），不要覆盖 Client_1 的 UIWindow/BasicEff/DamageSkin/现金皮肤相关文件。建议生成新的 `append_candidates_v16.txt`（即本次 `v16_vs_client1_missing.txt` 去掉 Base/日志/根目录杂项），并与历史 `conflicts.txt` 合并去重后再跑 merge/robocopy。

完整缺失列表: `v16_vs_client1_missing.txt`

## 附录：各类别缺失样例

### Map
- `Map/Back/2020Awake.img`
- `Map/Back/BM2_3.img`
- `Map/Back/BM3_2.img`
- `Map/Back/BM3_4_bossBlackMage.img`
- `Map/Back/BossKaringCY.img`

### Character
- `Character/00002006.img`
- `Character/00002007.img`
- `Character/00002008.img`
- `Character/00002012.img`
- `Character/00002013.img`

### Npc
- `Npc/0000000.img`
- `Npc/0002008.img`
- `Npc/0010300.img`
- `Npc/0010301.img`
- `Npc/0010302.img`

### Mob
- `Mob/0100000.img`
- `Mob/0100001.img`
- `Mob/0100002.img`
- `Mob/0100003.img`
- `Mob/0100004.img`

### Item
- `Item/Cash/0531.img`
- `Item/Cash/0532.img`
- `Item/Cash/0534.img`
- `Item/Cash/0535.img`
- `Item/Cash/0544.img`

### Etc
- `Etc/FreeCouponStock.img`
- `Etc/PredictCard.img`
- `Etc/SetItemInfo.img`
- `Etc/SetItemInfo.img.bak_20260704`
- `Etc/SpecialConditionItem.img`

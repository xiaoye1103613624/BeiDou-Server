# Extended Equipment Architecture (server mirror)

Canonical design document lives in the client plugin repo:

**[`E:\pro\BeiDou-ijl15\docs\ADDON_EXTEND_EQUIP_ARCHITECTURE.md`](../../../BeiDou-ijl15/docs/ADDON_EXTEND_EQUIP_ARCHITECTURE.md)**

**Real CD grow (2026-08-04):** [`ADDON_REAL_EQUIP_EXPAND_CLIENT.md`](./ADDON_REAL_EQUIP_EXPAND_CLIENT.md) → client [`ADDON_REAL_EQUIP_EXPAND_20260804.md`](file:///E:/pro/BeiDou-ijl15/docs/ADDON_REAL_EQUIP_EXPAND_20260804.md). Sidecar PreferSend on 52-slot CD is frozen; server keeps Aw/−62 + Si/−10.

Related: [`ADDON_095_IDA_BODY_PART_NOTES.md`](../../../BeiDou-ijl15/docs/ADDON_095_IDA_BODY_PART_NOTES.md) · [`ADDON_STATS_BASELINE_RECOVERY.md`](../../../BeiDou-ijl15/docs/ADDON_STATS_BASELINE_RECOVERY.md) · avoid-list process: STATS restore = no enter retest.

## Server-facing decisions (summary)

1. **Registry-driven** — `ExtendedEquipRegistry` drives AbleToWear prefix allow, equip fixed-dst, totem slot list, and blind seats (Phase 2 landed).
2. **Client-blind seats** — `Character.isClientBlindEquipSlot` → registry; seats: `−52/−53`, `−54…−61`, cash mirrors; **never** Si `−10` or pocket `−33`.
3. **Prefix-first** — 116/119/166/167/120/109|134|135 before WZ `islot` (119 must not hit `SHIELD`).
4. **Cash rings** — never persist `−152/−153`; classic cash ring slots only.
5. **Unequip** — server is sole bag grant for Addon/sidecar clears (client clear-only).
6. **Persist** — DB positions unchanged; no parallel inventory.

Avoid-list / enter policy: `BeiDou-ijl15/docs/ADDON_ENTER_CRASH_AVOID.md`.

Live client baseline (enter A/B): `ADDON_ENTER_STATS_WIREOFF_20260802` SHA `A6308A19…` — wire/REMAP/paint OFF. Restore bak: `STATS_UNEQUIP` `4AD0AA7A…`. Server `ExtendedEquipRegistry` already in tree; no server rebuild required for this A/B.

## FAQ — 为什么原红 9 能用，换成口袋 116 就不行？

**结论：** 不是 GetSlotXY 坏了，是 **显示 bodypart 类别** 不匹配。

- 历史红 9：座位本就在原生绘制范围（或不落 `bp>20 && bp<=48` 跳过带），坐标 park 到 `(104,200)` 即可。
- 口袋 116→BP33：落在原版 Equip 图标跳过带内；改 skip 上界或 BP33 cave → 进图 `0xC0000409`。
- 图标需 sidecar/overlay 或 Equip 打开后安全 paint；**134/135 → Addon Aw/−62**（与 109 Si/−10 真分槽，零耦合）。当前 live 仍 `GREEN_ENTER_OMIT_AUX62=true` 时 CharInfo 省略 −62，穿戴拒绝；见 [`ADDON_AUX_SERVER_PARITY.md`](ADDON_AUX_SERVER_PARITY.md)。

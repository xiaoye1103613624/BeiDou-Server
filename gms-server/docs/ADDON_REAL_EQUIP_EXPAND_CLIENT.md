# Client ↔ Server — Real Equip Expand (link)

Canonical client design (中文 + technical English):

**[`E:\pro\BeiDou-ijl15\docs\ADDON_REAL_EQUIP_EXPAND_20260804.md`](file:///E:/pro/BeiDou-ijl15/docs/ADDON_REAL_EQUIP_EXPAND_20260804.md)**

Also: [`ADDON_NATIVE_CD_GROW.md`](file:///E:/pro/BeiDou-ijl15/docs/ADDON_NATIVE_CD_GROW.md) · [`ADDON_AUX_SERVER_PARITY.md`](./ADDON_AUX_SERVER_PARITY.md) · [`ADDON_EXTEND_EQUIP_ARCHITECTURE.md`](./ADDON_EXTEND_EQUIP_ARCHITECTURE.md)

## Server posture (keep)

| Item | Status |
|------|--------|
| 109 → `Si`/−10 only | Keep |
| 134/135 → `Aw`/−62 | Keep; zero shield coupling |
| `canWearEquipment` level/job | Keep — same path as classic |
| CharInfo −54…−62 | Send when client can hold; `GREEN_ENTER_OMIT_AUX62=false` |
| Cash totem −156 alias | ENTERSAFE server alias; do not require TOTEM2 client |

## Gate

Omit / reject-aux forever is a **non-goal**. Real CD64 client holds native ZRefs −54…−62; until CD64 promotes to Client_1, do not invent PreferSend-style client hacks on live. Server already stores negative positions — no parallel inventory.

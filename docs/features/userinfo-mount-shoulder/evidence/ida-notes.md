# IDA / 证据摘要（2026-09-17）

目标：未改 GMS v083 `MapleStory.exe` / BeiDou 同源（image base `0x400000`）。

## SetAvatarInfo — BP20 → 坐骑页第三格

- 函数：`CUIUserInfo::SetAvatarInfo` @ `0x00903E24`
- `0x903E90`: `cmp dword ptr [ebp-10h], 14h`（bytes `83 7D F0 14`）
- 命中时写入 `this+0x6D4`（坐骑页 slot）
- 插件：改 imm @ `0x00903E93`：`14` → `7F`

## SetAvatarInfo — 预览门闩

| VA | 原指令 | 含义 |
| --- | --- | --- |
| `904044` | `cmp [esi+6CCh], ebx` / jz fail | 无坐骑则跳过（保留） |
| `904058` | `jz loc_9041A5`（`0F 84 47 01 00 00`） | 无鞍 → fail |
| `90406c` | `jz loc_9041A2`（`0F 84 30 01 00 00`） | 鞍 GetEquipItem 空 |
| `904081` | `jz loc_9041A2`（`0F 84 1B 01 00 00`） | IsItemSuited 失败 |
| `904087` | create preview + SetRidingVehicle | 目标落点 |

`IsItemSuitedForTamingMob` @ `0x004B1113`：`191xxxx` 鞍 bitmask 含 `mountId % 100`（1902242 → bit 42）。

## SetTamingMobInfo — 名称

- `0x00903DB7`：`GetEquipItem(mountId @ +0x6CC)` → 拷贝 name 到 `+0x6C4`
- 与背包同一 `CItemInfo` 源；差异仅因 **item id 对应的 String 节点不同**

## String 抽样（orange-wz，密钥 083-GMS / TSPHKw）

| 节点 | 改前 | 改后 |
| --- | --- | --- |
| `Eqp/Taming/1902242/name` | 坐骑1902242 | 小浣猪 |
| `Eqp/Taming/1902000/name` | 小浣猪 | 小浣猪（未改） |

## 占用说明

首次覆盖 live `Eqp.img` 失败：Restart Manager 显示锁在 `orange-wz` MCP 的 `java.exe`（映射会话）。`unload_all` + `clear_cache` 后 Copy 成功。

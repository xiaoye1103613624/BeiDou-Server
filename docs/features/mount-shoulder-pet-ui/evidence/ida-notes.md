# IDA 校准笔记（2026-09-16）

目标二进制：v083 MapleStory（user-ida-pro-mcp 当前库）。

## MountFail 红槽 @7FEDD9

技能等级失败后连续 `push` bodypart 并 `call sub_7FF006`：

| VA | 指令 | BP |
| --- | --- | --- |
| `7FEDE2` | `push 12h` | 18 坐骑 |
| `7FEE03` | `push 13h` | 19 鞍具 |
| `7FEE1B` | `push 14h` | 20 → 现为护肩 |

插件：`EnsureShoulderSkipMountFailRedBp20` 将 `7FEE1B` 起跳过 push-14h 块（期望原字节 `6A 14`）。

## CUIEquip 红 overlay @7FEF80

原字节：`83 7D E4 00 74 06 83 7D E8 00 74 19`  
`DrawClearRedBp20_cave`：若 `ebp+8==20` 清标志并跳到 `7FEFA5`。

## GetSlotXY @7FEFEA

序言：`83 39 00 ...`（`cmp dword ptr [ecx],0`）  
表：`BE23F0` / `BE2580`。  
静态只读样本：BP18=(5,233) BP19=(38,233) BP20=(71,233)。  
插件 hook：`bpIndex==19` → 强制 `(137,101)`。

## CUIPetEquip

| 符号 | VA | 备注 |
| --- | --- | --- |
| Draw | `801474` | 入口 |
| 红层 cmp | `8017EC` | `cmp [ebp-24], ebx; jnz skip`；随后 `push 40FF0000h` |
| 跳过红 | `80182C` | |
| BE2260[32] BP33 | `BE2360` | 静态 (13,44) |

插件 cave：`esi == 0xBE2360` 且 `g_petEquipPouchAliased` → 跳 `80182C`。

## get_bytes 抽样（实现日）

```
7FEE1B: 6A 14 51 8D 45 F0 8B CC
7FEFEA: 83 39 00 8B 44 24 04 ...
7FEF80: 83 7D E4 00 74 06 83 7D E8 00 74 19
8017EC: 39 5D DC 75 3B ...
BE2360: 0D 00 00 00 2C 00 00 00  (=13,44)
```

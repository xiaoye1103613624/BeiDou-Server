# Hook / 分发表（BeiDou-ijl15）

目标客户端：GMS v83 image base `0x400000`。原则：同一 VA 仅一所有者，棱镜逻辑插入既有 hook。

| VA | 用途 | 所有者 | 棱镜动作 |
| --- | --- | --- | --- |
| `0x004863D5` | cash consume type 门闸 | `fusionanvil.cpp` | `ColorPrism_IsPrismItem` → return 1 |
| `0x00A0A63F` / `0x00A1DC5B` | 现金使用发包 | `fusionanvil.cpp` | `ColorPrism_OnUse` 并吞包 |
| `0x004EF140` | 道具拖放 | `fusionanvil` OnDropped 链 | `ColorPrism_HandleItemDrop` |
| `0x004FAA22` | 技能拖放 | `AttachColoringPrismMod` | `ColorPrism_HandleSkillDrop` |
| `0x00453AD1` | PrepareActionLayer | `AttachWeaponTintMod` | 本体/发型/皮肤/装备特效层 |
| `0x00453696` | 脸部层 | `AttachWeaponTintMod` | Eyes |
| `0x00933990` | ShowSkillEffect | `AttachWeaponTintMod` | Skills（五参 `__thiscall`） |
| `0x0093BEB9` / `0x0093C218` | 现金特效 | `AttachWeaponTintMod` | 501 组 |
| `0x00435DF3` | CreateBullet | `AttachWeaponTintMod` | `WeaponTint_NoteBulletFlight` |
| （PacketDispatcher） | 收包路由 | `compat/PacketDispatcher` | `0x372F` → `WeaponTint_HandleSync`（禁止再 Detour `0x004965F1`） |

ItemEff：`AttachItemEffectMod` 包裹 `LoadLayer`；`ItemEff_Invalidate` 由 `weapontint` 本地/远程刷新路径调用。

Avatar：`AVATAR_EQUIP_SLOTS = 52`（`compat/wvs/avatar.h`）。

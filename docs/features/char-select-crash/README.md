# 选角进图无提示闪退

> 状态：须与 ijl15 Decode 钩子对齐——`addItemInfo` **必须**写尾部 `anvilItemId`  
> 日期：2026-09-15  
> 客户端：`E:\MXD\BeiDou-Client_S9`

## 现象

- 选角色进入游戏时**直接闪退，无错误弹窗**。
- 与早前「选频道 + `0x80030002` / `Character/00000000.img`」不同（那是阿尔泰 face/hair 写 0）。

## 根因（已校正）

当前 `ijl15` `GW_ItemSlotBase::Decode_hook`（`fusionanvil.cpp`）在原生解码后若 `offset+4 <= length` 就会 `Decode4` 读 `nAnvilItemID`。CharInfo 中装备后几乎总有后续字节，故钩子会**始终**多读 4 字节。

- 服务端**停写**尾字段 → 钩子偷读下一字段 → 库存后错位 → `CharacterData::Decode` @`0xE4EAD` 空读（`0xC0000005`）。
- 先前「停写止血」判断错误：对现行 DLL 反而会制造错位。19:54 停写重启后 19:55 仍同 EIP 闪退即证据。

外观另可由 `addCharEquips` 替换 itemId；`USER_INFO_EX` 仍带 anvil。

## 修复

- **恢复** `addItemInfo` 在 `writeInt(-1)` 后 `writeInt(equip.getAnvilItemId())`（无幻化写 0）。
- 勿再对现行插件 DLL 做「停写」；若要去尾字段须先改钩子为永不 Decode4（或真正帧定界），两端同发。

## 验证

1. IDE 重编译并重启 `ServerApplication`（确认 classpath 为 `gms-server/target/classes`）。  
2. 选角色进图：不应再无提示闪退。  
3. 有幻化的装备：外观与 tip 中 anvil 一致。

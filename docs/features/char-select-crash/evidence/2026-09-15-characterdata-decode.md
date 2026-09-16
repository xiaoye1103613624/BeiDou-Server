# 证据：CharacterData::Decode 空指针

## 客户端日志（2026-09-15 19:44 / 19:55）

- `beidou-crash.log` / `client_boot.log`：
  - `ExceptionCode=0xC0000005`
  - `EIP=0x004E4EAD`（`BeiDou.exe`）
  - `EAX/EBX/ECX/EDX/ESI/EDI=0`，读 `target=0x00000000`
  - **无** `0x80030002` / `getobj_inflight`（非 WZ 弹窗类）
- `beidou-wz-last.log`：末段为 WorldSelect / `GameIn` / `shadow`（进图过渡），非 `Character/00000000.img`
- 19:55 与 19:44 **同一 EIP**；19:54 服务端已用「停写 anvil」的 `target/classes` 重启后仍崩。

## IDA（BeiDou.exe）

| 地址 | 符号 |
|------|------|
| `0x4E4EAD` | 落在 `sub_4E4DEA` |
| 调用链 | `CharacterData::Decode`（`?Decode@CharacterData@@QAE_KAAVCInPacket@@H@Z` @ `0x4E66C6`）|

`sub_4E4DEA` 从 `CInPacket` 读 `Decode1/Decode2/DecodeBuffer` 后按位图遍历；`v21` 为空时在 `*(v21 + v4/8)` 空读 —— 与现场寄存器全 0 一致。

## 服务端 / 插件对照（校正）

- HEAD：`addItemInfo` 在 `writeInt(-1)` 后 `writeInt(equip.getAnvilItemId())` —— **与现行插件一致，应保留**。
- 工作区曾误删该尾字段；`ijl15` `Decode_hook` 在 `offset+4<=length` 时总会 Decode4，停写即偷读后续字段。
- 19:54 停写 + 重启 → 19:55 同址复现，证实「停写」方向错误。
- 已恢复 `writeInt(equip.getAnvilItemId())`。

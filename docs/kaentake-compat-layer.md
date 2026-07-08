# Kaentake 兼容层（compat/）

BeiDou-ijl15 的 Kaentake 风格兼容层：WZ 桥接、ProcessPacket 侧效、模块注册。

## 初始化

dllmain -> ModRegistry::Initialize -> WzBridge + RegisterBuiltins + PacketDispatcher::InstallHook

## PacketDispatcher

Hook 0x004965F1 (__thiscall trampoline)：
- 0x0178：吞包（WorldMapInfo）
- 0x3714/0x1000 等：侧效后仍转发原生 ProcessPacket
- 除 0x0178 外禁止吞包（换图 EH/stack 问题）

## 模块

| 模块 | 开关 | 说明 |
|------|------|------|
| HpMpAlert | - | 0x1000, SaveGlobal __thiscall |
| WorldMapInfo | disableWorldMap | 0x0178/0x0115 |
| DamageRank | - | 0x3714, F12, stage |

## config.ini [debug]

disablePacketHook / disableBossHP / disableWorldMap — 二分调试换图崩溃。

## BossHP

tooltip 只 init 一次，换图 ClearToolTip，防二次析构 crash。

## 扩展

RegisterBuiltins 添加 CompatModule + PacketDispatcher::RegisterHandler。

# 个人伤害统计（DPT / DamageRank）

> 适用：**GMS v083** 北斗服务端 + **BeiDou-ijl15** 客户端插件（`feature/dev_0.0.3`）
> 参考：Tengutake DamageRank（`UI_custom2.wz`）

## 1. 功能概述

| 能力 | 说明 |
|------|------|
| F12 | 开关伤害面板 |
| 玩家页 | 同地图玩家累计伤害排行 |
| 技能页 | 技能伤害、次数、最大/最小单次 |
| DoT | skillId = -1，显示 DoT Damage |

## 2. WZ 资源

从 Tengutake 合并 **UI_custom2.wz**，需包含 `UI/UIWindow.img/DamageRank/*` 与 `UI/Basic.img` 按钮/滚动条。

## 3. 封包

| 方向 | Opcode | 用途 |
|------|--------|------|
| C->S | 0x3713 | 控制：1=open 2=reset 3=close |
| S->C | 0x3714 | 追踪：mode 0=reset 1=player 2=skill |

服务端：CustomPacketHandler + Character.dptOnDamage + PacketCreator.dpt*

客户端：DamageRankBridge.cpp 解析 0x3714；SendDamageRankControl 发 0x3713。

## 4. 插件结构

- damagerank/DamageRankBridge.cpp — 封包与模块注册
- damagerank/DamageRankStage.cpp — set_stage hook
- damagerank/uiDamageRank.cpp — UI
- damagerank/F12test.cpp — F12
- compat/ModRegistry.cpp — 注册 DamageRank 模块
- compat/ClientAddresses.h — opcode 常量

## 5. F12

进 CField 后 F12 开关；Reset 按钮发 reset；首次打开 MenuUp 音效。

## 6. 地图切换

DamageRankStage hook set_stage (0x00777347)：
- CField：OnMapTransition() 重建 layer，保留可见状态与数据
- 离开字段：ResetOnStageChange + 清空 CDamageRankData
- 0x0178 由 PacketDispatcher 吞包，避免换图 crash

## 7. 构建

编译 ijl15 -> PostBuild BeiDou-Client_1；合并 WZ 后验证 F12 与字段内换图。

## 8. 参考

- docs/kaentake-compat-layer.md
- Tengutake damagerank_client_readme.md / damagerank_cosmic_readme.md

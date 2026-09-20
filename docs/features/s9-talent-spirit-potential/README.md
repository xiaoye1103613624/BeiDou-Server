# S9 · 天赋 / 灵韵觉醒 / 潜能·Hyper

> 状态：已闭环（装备 INSERT 占位符已校正）  
> 适用客户端/协议版本：GMS v83 + ijl15（装备包尾仅到 `anvilItemId`）  
> 项目名称：BeiDou-Server_s9  
> 日期：2026-09-17  
> 产物目录：`docs/features/s9-talent-spirit-potential/`  
> 计划：`.codebuddy/plans/S9-天赋-灵韵-潜能-缺失功能闭环修复_ed029984.md`

## 1. 背景与目标

S9 重构后，天赋整包缺失；灵韵与潜能为桩实现且缺 DB 列，导致匠人街 NPC / `9031014` 运行时报错、属性不生效。目标：按脚本契约补齐持久化与服务，不改动 `PacketCreator.addItemInfo` 包尾。

明确不做：改装备常规封包字段、重写战斗架构、改 NPC 脚本签名。

## 2. 影响范围

| 层级 | 路径/模块 | 变更类型 |
| --- | --- | --- |
| DB | `V1.11.60` 灵韵三列 · `V1.11.61` 潜能十五列 · `V1.11.62` `xy_character_talent` · `V1.11.63` `potential` 命令 | 新增 |
| 持久化 | `ItemFactory` INSERT/SELECT、`Equip` 字段 | 修改 |
| 灵韵 | `org.gms.spirit.*` | 补全 |
| 潜能 | `org.gms.potential.*`、`ScrollHandler`、`PotentialCommand` | 补全 |
| 天赋 | `org.gms.talent.*`、`Character`、`NPCConversationManager` | 新建/接线 |
| 战斗 | `PotentialStatProvider`、`TalentEffects` | 接入 |
| 脚本 | `9031014.js`、`xy/匠人街/灵韵觉醒.js` | 契约不变 |
| 插件/包尾 | `addItemInfo` | **未改**（红线） |

## 3. 最终实现逻辑

### 3.1 灵韵觉醒

1. NPC → `SpiritAwakenService.awaken/reset/describeEquip`
2. 成功写入 `equipSkillId/Level/Expire`，经 `ItemFactory` 落库
3. 穿戴刷新 → `SpiritWearSkills.sync` 注入 `setSkillBonusLevels`（跨职走 `SpiritSkillRemap`）
4. 交易可选清空：`CLEAR_SPIRIT_ON_TRADE`

### 3.2 潜能 / Hyper

1. 卷轴/魔方/放大镜 → `ScrollHandler` → `PotentialHyperService`
2. `computeBonus(equip, level)`：`ItemOptionProvider` 词条 + `HyperEnhanceTable` 星级，无 IO
3. 战斗平面属性 → `PotentialStatProvider`
4. GM：`!potential`（`command_info` / `V1.11.63`，经 `CommandService` 反射加载）
5. 客户端展示走既有 `USER_INFO_EX`，不扩装备包尾

### 3.3 天赋

1. 登录懒加载：`Character.getTalentManager()` → `load()`
2. 脚本门面：`TalentService` + `NPCConversationManager` 七个 `cm.*` API
3. 命中类效果在 `TalentEffects`；`TalentStatProvider.provide()` 恒空（设计如此）

### 3.4 持久化注意

`INSERT_EQUIP_SQL` 列数与 `bindEquipColumns` 下标 **必须同为 51**。曾出现 VALUES 多一个 `?` 导致装备存档 SQLException，已修正。

## 4. 资源与同步

- 词条池：服务端 `ItemOption.img`（含 `wz-zh-CN` 覆盖），启动加载进内存
- 无客户端 IMG/插件变更；无道具外观资源同步需求

## 5. 编码与 i18n

- 源码 / i18n / REST：UTF-8
- 服务端玩家可见文案：部分仍在 Java 字面量（NPC `message()` / GM 提示），后续应迁入 `message_*.properties`
- 游戏封包字符串仍按客户端语言（中文 GBK）

## 6. 产物清单

| 类型 | 路径 | 说明 |
| --- | --- | --- |
| 文档 | `README.md` | 本文件 |
| 计划 | `.codebuddy/plans/S9-天赋-灵韵-潜能-缺失功能闭环修复_ed029984.md` | 任务分解 |

## 7. 验证记录

- `mvn -pl gms-server -DskipTests compile`：见同目录 `notes/VERIFY.md`
- 手工：Flyway 60–63；灵韵 NPC 觉醒/重置；潜能卷/魔方；`9031014` 学天赋；存档重启后装备灵韵/潜能列不丢
- 包尾红线：`addItemInfo` 在 `writeInt(-1)` 后无新增字段

## 8. 预留、风险与回滚

| 项 | 说明 |
| --- | --- |
| i18n | GM/NPC 中文提示尚未全部资源化 |
| 回滚 | 删迁移 60–63 列/表前需备份；代码回退 `spirit/potential/talent` 包与 `ItemFactory` |
| 风险 | 错改 `addItemInfo` 会导致进游戏 Decode 闪退 |

## 9. 对外交流摘要

S9 已把天赋、灵韵觉醒、潜能/Hyper 从桩实现补到可跑：DB 列 + ItemFactory 读写、脚本契约服务、Scroll/GM/战斗接线齐全；装备常规封包未动，扩展数据走 DB 与 `USER_INFO_EX`。

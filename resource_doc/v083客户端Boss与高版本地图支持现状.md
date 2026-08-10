# v083 高版本 Boss 盘点与补齐记录（2026-07-28）

## 结论（根因）

玩家反馈「好多高版本 Boss 都没有了」——对照后：

1. **客户端 Live 主 Boss 本体大多仍在**（Mob 体积正常，非整包丢失）。
2. **服务端 `wz-zh-CN/Mob.wz` 缺大量高版本 Mob XML**：召唤时 `getMonsterLifeFactory` 失败 → 表现为「Boss 没了」。
3. **部分入口/阶段地图服务端缺失**（路西德/威尔/顿凯尔相关 Map）。
4. **少数客户端空壳/无图**：召唤物/阶段怪 `<2KB`；乌鲁斯官方 ID `8881000` Live 无图。

根因归类：**曾有资源后服务端 XML 缺口扩大（非“从来没移植”为主）**；入口脚本（匠人街/高级BOSS/远征）仍在。

## 检查清单

| Boss | Live Mob | Srv Mob | 入口 | 状态 |
|------|----------|---------|------|------|
| 进阶四傻 | 有 | 有 | 匠人街进阶 | OK |
| 斯乌 | 有 | 有 | 普通挑战 | OK |
| 戴米安 | 有 | 已补困难阶段 | 远征 `209000001` | OK |
| 路西德 | 有 | 已补阶段/召唤 | 远征 `450003740` | OK |
| 威尔 | 有 | 已补阶段 | 远征 `555000200` | OK |
| 觉醒希拉/炼狱 | 有 | 已补 `8880405` 等 | GM/高级BOSS | OK |
| 黑魔法师 | 有 | 已补阶段 | 高级BOSS/远征 | OK |
| 塞伦/绿水灵 | 有 | 有 | 高级BOSS | OK |
| 卡洛斯 | 有；`8880802` 已用兄弟壳补画布 | 有 + String | 高级BOSS `8880803` | OK（阶段 804~806 Live 仍无图） |
| 乌鲁斯 | `9303100/01` 有；`8881000` 无 | `9303100`+`8881000` XML | — | 官方 Live 无源 |
| 敦凯尔/至暗 | 有 | 有 | 远征/高级BOSS | OK |
| 麦格纳斯/希拉 | 有 | 有 | 团队挑战 | OK |
| 四凶远征 | 穷奇图正常；120/140/160 空壳 | — | **已改入口+补刷** | OK |

## 第一轮已补（服务端 WZ）

- Mob XML：约 **195**（Live→NONE）+ **77**（BMS，20 个修 `maxHP=??????`）
- Map XML：**8**（Live）+ **4**（BMS：`160000000/160010000/970072200/970072300`）
- String（服务端）：卡洛斯 `8880800~802` →「监视者卡洛斯」

## 第二轮已补（2026-07-28 续）

| 项 | 处理 |
|----|------|
| Live `8880802` 空壳 | MCP：从 `8880801` 复制节点→新 img→部署（2.0MB）；备份在 `_img_merge_backup/boss_shell*` |
| Live `8800200`（拉瓦那）空壳 | MCP：从 `8800201` 复制（393KB） |
| Live `8880412` / `8880001` | MCP：分别从 `8880411` / `8880000` 复制 |
| Live String 卡洛斯 | MCP `set_value` 写入 `8880800~802`（Mob.img 430347B） |
| 四凶远征空壳图 | `9031000_远征.js`：`511000120/140/160` → `105200800/900/100`，进场前 `spawnMonsterOnGroundBelow` 补刷 `8880831/832/837` |
| 扩大搜索乌鲁斯 `.img` | `beidou_client_xiaoye` / V16 / 079：**均无** `8881000/8881010` 可用二进制 |

## 「检查更多」新发现

| 类别 | 结果 |
|------|------|
| 脚本引用的高版本 Mob（≥8200000） | 服务端/Live **主 ID 已齐**；仅个别空壳（已处理上述） |
| Effect（相对 BMS） | Live 缺约 **75** 个（多为 `Direction*` 过场），非战斗刚需 |
| Sound（相对 BMS） | Live 缺约 **54** 个；**无**明确 Boss 专名缺口（Lotus/Damien 等关键字未命中） |
| Reactor | 双边约缺 **62**（BMS 有、Live+Srv 皆无），未整包导入 |
| Skill/MobSkill | Live/Srv 均有；BMS 为拆分目录（140 项），未强行对齐 |
| 卡洛斯 `8880804~806` | **Srv 已有 XML**；**Live 无 `.img`**（仅 BMS XML，无画布源） |
| 炎魔/自定义皮卡丘类 | 未发现独立于现有高级BOSS 列表的官方全家桶缺口 |

## 仍无法补（无源 / 协议）

| 项 | 说明 |
|----|------|
| 乌鲁斯 `8881000/8881010` Live | 本地合集无二进制；玩法可用 `9303100` |
| 卡洛斯阶段 `8880804~806` Live | 无画布源；Srv 可召唤但客户端可能看不见/异常 |
| 大量 Effect Direction* / 杂 Sound | 体量大、非 Boss 刚需；需单独批次 MCP 导入 |
| 完整官机多阶段机制 | v083 协议限制 |

## 召唤崩溃修复（2026-07-28 紧急）

**承认**：上一轮 BMS/Live 直拷 Mob XML **缺校验**，留下 `maxHP="??????"`；高版本 skill 未映射进 `MobSkillType` 时旧逻辑 `orElseThrow` 会炸。

### 根因
1. `maxHP="??????"`（真值在 `finalmaxHP`）→ `Long.parseLong` 崩（例 `8880101`）
2. 未知 MobSkill → `MobSkillType.from(...).orElseThrow()`（加载/施放）

### 代码（已进源码并 **rebuild BeiDou.jar**）
- `DataTool.getLong`：非法字符串返回 null/默认
- `LifeFactory`：maxHP 回退 `finalmaxHP`；未知 skill **skip+warn**；`getMonster` catch 返回 null
- `MapleMap` / `ReactorActionManager` / `cm.spawnMonster`：null 不召唤
- `MoveLifeHandler`：未知 skill 跳过施放

### 数据
- 已写回 **20** 个非法 `maxHP`（有 `finalmaxHP` 用其值，否则 `1000000000`）
- 888* 中未映射 skill（运行时跳过）：`146,170,176,201,214,215,217,227,232,234,238,242,246,247,248,267,268,274,276`

### 重启
- **必须用新 jar 重启服务端**后再召唤（旧 jar 时间早于本次修复无效）
- 客户端无需为本次 Java/XML 修复重进

## 重启要求（资源向）

- **必须重启服务端**（或至少 `!reloadevents` + 重载脚本；WZ XML 建议整服重启）
- **客户端重进**：已改 Live Mob 空壳 + String；热更散 img 后重登即可

## 工具备忘

- 服务端导出：`java -cp target/classes;dependency/* orange.wz.BatchImg2Xml <stage> <out>`（`MediaExportType.NONE`）
- 空壳补画布：MCP `create_img_file` + `copy_nodes`（兄弟 ID）+ `paste_nodes OVERWRITE` + `save_as`
- 禁止对 Live `.img` 做 robocopy/整包 Copy-Item「恢复」
- **移植后必须扫** `maxHP/maxMP` 非数字 + skill 是否在 `MobSkillType` 内，再 rebuild

# v083客户端 Boss 与高版本地图/Boss 支持现状调查（2026-06-20）

## 结论概览

- 当前 BeiDou 服务端基于 v083 客户端，**地图(Map)和怪物(Mob)系统完全是v083原版**，未做任何高版本移植。
- **唯一确认的高版本移植内容是装备/物品**：通过079中转秘钥把高版本装备的itemId数据写入083客户端的 `Character.wz` 和 `String.wz/Eqp.img.xml`（武器段集中在1200000~1299999）。地图和怪物没有类似移植。

## 高版本地图/Boss 排查结果（均未找到证据）

- `wz-zh-CN/String.wz/Map.img.xml`、`gms-server/wz/Map.wz` 中检索高版本特征地图名（魔王城、卡欧斯狱光、摩尔孔孔泪滴、地下监狱等）均无命中，地图ID分布全部落在v083原版常规范围（920xxxxx童话镇、922xxxxx、992/993xxxxx西丽贵/事件图）。
- `Mob.img.xml`、`org.gms.constants.id.MobId` 中没有Magnus、Lotus、Damien、Hilla、Von Leon、CygnusFM等post-BigBang boss的任何常量、数据或脚本。
- `scripts-zh-CN/` 中提到"高版本/083V2移植"的脚本（约22个文件，矿石仓库、抽奖、点歌、战力系统等）均为**功能系统**移植，与地图/boss无关。

## 当前v083客户端已实现的Boss清单

| Boss中文名 | mobId | Java专属逻辑 | 脚本支持 |
|---|---|---|---|
| 赞昆 Zakum | 8800000-02(本体)/8800003-10(8臂) | 有(MapleMap阻挡进场直到8臂全灭) | 有(reactor/2111001.js) |
| 暗黑龙王 Horntail | 8810000-09/8810010-17(死亡部位)/8810018(灵魂)/8810026(召唤体) | 有(spawnHorntailOnGroundBelow等) | 有(reactor/2401000.js) |
| 帕普拉图斯 Papulatus | 8500001(座钟)/8500002(本体，常量缺失) | 无专属特判 | 有(quest/7103.js) |
| 皮安纳斯 Pianus | 8510000(本体)/8510100(炸弹) | 有(MobSkill炸弹特判) | 未发现专属reactor |
| 粉豆 Pink Bean | 8820001 | 有(broadcastPinkBeanVictory) | 远征队类型存在 |
| 塔加 Targa | 9420541-44 | 复用通用召唤逻辑 | 有(reactor/5511000.js) |
| 斯卡隆 Scarlion | 9420546-49 | 复用通用召唤逻辑 | 有(reactor/5511001.js) |
| 巴洛古 Balrog | 8830000-06(常量缺失) | 有(broadcastBalrogVictory) | 有(event/BalrogBattle.js、BalrogQuest.js) |
| 武陵道场Boss系列 | 9300184-9300215 | 有(MobId.isDojoBoss范围判定) | 有 |

支撑文件：
- `gms-server/src/main/java/org/gms/constants/id/MobId.java`
- `gms-server/src/main/java/org/gms/server/maps/MapleMap.java`
- `gms-server/src/main/java/org/gms/server/life/Monster.java`、`MobSkill.java`
- `gms-server/scripts-zh-CN/reactor/2111001.js`、`2401000.js`、`5511000.js`、`5511001.js`
- `gms-server/scripts-zh-CN/quest/7103.js`
- `gms-server/scripts-zh-CN/event/BalrogBattle.js`、`BalrogQuest.js`

## 已知缺口（待二次核实，不可直接当作已支持）

1. Papulatus本体(8500002)、Balrog全系列(8830000-06)未在 `MobId.java` 登记常量。
2. Von Bon/Pierre/Crow/慕雷卡/熊设计师 — 未找到对应脚本与Java逻辑，疑似未实现。
3. Chaos Zakum/Chaos Horntail/Scarga/Showa/Ariant — 仅见于 `ExpeditionType` 枚举，实际战斗逻辑落地情况未确认。

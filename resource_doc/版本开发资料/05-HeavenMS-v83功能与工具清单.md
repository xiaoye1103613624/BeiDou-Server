# HeavenMS v83 功能特性与配套工具清单

> 资料整理日期：2026-06-17
> 来源：HeavenMS Wiki（rev203 功能列表）
> 用途：HeavenMS 是北斗上游 Cosmic 的前身，本清单可作为"一个完整 v83 服务端应具备哪些功能"的参照表，移植/补全功能时对照

---

## 一、组队副本(PQ) 与远征(Expedition)

- 经典 PQ：HPQ / KPQ / LPQ / LMPQ / OPQ / EllinPQ / PiratePQ / MagatiaPQ / HorntailPQ / AmoriaPQ / TreasurePQ / ElnathPQ。
- CWKPQ 作为"远征"类活动。
- 远征 Boss：Scarga / Horntail / Showa / Balrog / Zakum / Pinkbean。
- 公会副本 GuildPQ + 公会排队(多大厅系统)。
- 自定义 PQ：BossRushPQ、CafePQ。
- 武陵道场(Mu Lung Dojo)；拉提那斯队长(Capt. Latanica) 改为活动 Boss。

**PQ 底层机制：**
- 大厅系统(Lobby)：同频道多个 PQ 实例。
- 远征系统：多队伍可挑战同一实例（与大厅互斥）。
- 公会排队系统：公会注册进 GPQ 队列。
- EIM Pool：首个实例创建后预加载后续实例，优化加载。

---

## 二、技能(Skills)

- 修复异常技能：偷窃(Steal)、毒星/毒刺(Venomous)、神秘之门(Mystic Door)。
- 制作技能(Maker) 完整实现，用启发式算法计算费用/试剂误差。
- 新增被动技能：座椅精通(Chair Mastery, max lv1)——坐椅子时大幅提升 HP/MP 回复。

---

## 三、任务(Quests)

- 玩偶屋任务可用；任务奖励按职业匹配物品、随机奖励、可选奖励均正常。
- 开始/完成任务的金币(meso)要求会校验。
- 大量职业任务线（含技能奖励）补全；重做 Aran 任务线、4 转技能任务线。
- 三转测验(探险家)完整 40 题题库。
- 增强奖励系统：先查背包可叠加位再找新格子；改进任务过期系统。

---

## 四、社交系统

- 公会(Guild) 与联盟(Alliance) 完整可用。
- 从零实现婚姻(Marriage) 系统。
- 新手可建/加入"仅新手"队伍（≤10 级）。
- 玩家商店/雇佣商人交易实时通知店主。
- 小游戏(配对/五子棋) 半功能密码系统。
- 物品拾取冷却（非己方/非队伍物品）；每日排行榜涨跌显示；自动 Player NPC 与名人堂(Hall of Fame)。

---

## 五、现金道具与物品

- 经验/掉落/装扮 优惠券；经验/掉落券激活时显示为 buff。
- 防驱逐卷(antibanish)、鞋钉、Vega 卷轴、智慧女神之眼(Owl of Minerva)、宠物物品忽略、贺年卡、风筝、商城惊喜、枫叶人生(Maple Life)。
- 背包系统校验空格与归属；仓库"整理物品"功能。

---

## 六、怪物、地图与反应堆

- 每张怪物卡可被对应野怪掉落；补全几乎所有野怪的金币掉落数据。
- 每本技能/精通书可被怪物掉落；同一装备可多次掉落（掉落时按 DB 最小/最大数量随机）。
- 地图边界检查优化拾取；地图物品数量上限（智能过期最旧物品防刷屏）。
- 僵尸化(Zombify) 异常状态；数十个 Boss 的血条(需定制 WZ)；多 Boss 时客户端优先显示玩家目标的血条。
- 船/电梯等交通机制；地图随时间掉血及对抗机制；血色帝国巴洛古船只逼近特效。
- PQ/出租车/活动 随机出生点传送(仿 GMS)；部分反应堆喷洒物品。
- 更新天空之城/世界旅行/尼哈沙漠/未来都市(Neo City) 等区域可完成任务推进。

---

## 七、服务端运营级特性

- 多世界(Multi-worlds)。
- 背包自动收集/排序；增强自动吃药(宠物按阈值吃多瓶)；增强 buff 系统(智能选最佳 buff)；增强 AP 自动分配。
- 宠物/坐骑饥饿度按活跃时间计算；NPC 工匠条件不满足不收材料。
- 频道容量条与世界满员检查；异常状态/中毒伤害对他人可见。
- 自定义监狱系统(需定制 WZ)、自定义回购(buyback)、删除角色(需开 ENABLE_PIC)。
- 自动存档(Autosaver) 定期保存在线玩家状态。
- HP/MP 成长支持固定与随机两种(ServerConstants 切换)，预留洗点位。
- 重新分配 mapobjectid 使用，修复"NPC 神秘消失"问题。
- 登录不存在账号时自动创建账号(credits: shavit)；密码哈希用 **BCrypt** 替代旧 SHA(credits: shavit)。

> 北斗同样使用 BCrypt（`org.gms.util` 下），并有自动存档、多世界等机制。

---

## 八、已修复的漏洞(Exploits)

- 登录阶段认证后可访问/删除任意账号任意角色的漏洞。
- 可随意开始/完成任意任务的漏洞。
- 多个异步相关漏洞（重点是 Fredrick 与 Duey 相关）。

---

## 九、配套外部工具(Java tools)

HeavenMS 自带一批数据维护工具，思路对北斗维护 WZ/DB 数据有参考价值：

| 工具 | 作用 |
|------|------|
| MapleArrowFetcher | 按怪物等级/是否 Boss 更新所有箭矢的最小/最大掉落数量 |
| MapleBossHpBarFetcher | 扫 quest WZ，报告有 Boss 血条却缺 "boss" 标签的怪物 |
| MapleCashDropFetcher | 扫 DB 列出所有 CASH 掉落项 |
| MapleCouponInstaller | 从 WZ 取优惠券信息生成 SQL 表（倍率/时段） |
| MapleIdRetriever | 手册名↔id 互转，生成 SQL 表 |
| MapleInvalidItemIdFetcher | 列出 DB 中不存在的 itemid |
| MapleMapInfoRetriever | 检测地图 field 结构缺失的 info 节点(缺失是严重问题) |
| MapleMesoFetcher | 为多于 4 项掉落的怪(野怪)生成金币掉落 |
| MapleMobBookIndexer | 生成怪物图鉴 cardid↔mobid 关系表 |
| MapleMobBookUpdate | 用 DB 当前掉落数据更新 MonsterBook.wz.xml |
| MapleQuest* 系列 | 校验任务物品 count 标签、缺脚本的 questid、任务费用等 |
| MapleReactorDropFetcher | 报告有掉落数据但未编码的 reactorid |
| MapleSkillMaker* 系列 | 用 WZ 数据更新制作技能(Maker)相关 DB 表 |

---

## 十、localhost 客户端补丁(v83 通用改动)

HeavenMS/Cosmic 系常对 localhost.exe 做以下修改（"去除客户端限制"）：
- 修复 NPC 对话框的 `'n'` 问题。
- 移除 MATK/WDEF/MDEF/ACC/AVOID 上限；为 SPEED 设更高上限。
- 移除"AP 过剩"弹窗、"已升级"弹窗。
- 移除新手(≤10 级)的 AP 分配限制、组队限制。
- 移除在禁止换频/商城的地图上的"无法进入 MTS"弹窗，使回购可用。
- 移除对非武器装备使用攻击宝石的限制。

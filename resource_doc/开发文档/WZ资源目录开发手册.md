# WZ 资源目录开发手册

> **项目：** 北斗服务器（BeiDou Server）  
> **WZ版本：** GMS v083  
> **最后更新：** 2026-06-15  
> **用途：** 开发时快速查询 WZ 目录结构、IMG 文件内容、节点属性

---

## 目录

- [1. 目录总览](#1-目录总览)
- [2. Base.wz — 渲染基础](#2-basewz--渲染基础)
- [3. Character.wz — 装备外观](#3-characterwz--装备外观)
- [4. Effect.wz — 视觉特效](#4-effectwz--视觉特效)
- [5. Etc.wz — 游戏配置](#5-etcwz--游戏配置)
- [6. Item.wz — 物品数据](#6-itemwz--物品数据)
- [7. Map.wz — 地图系统](#7-mapwz--地图系统)
- [8. Mob.wz — 怪物数据](#8-mobwz--怪物数据)
- [9. Morph.wz — 变身动作帧](#9-morphwz--变身动作帧)
- [10. Npc.wz — NPC数据](#10-npcwz--npc数据)
- [11. Quest.wz — 任务系统](#11-questwz--任务系统)
- [12. Reactor.wz — 反应堆](#12-reactorwz--反应堆)
- [13. Skill.wz — 技能数据](#13-skillwz--技能数据)
- [14. Sound.wz — 音频](#14-soundwz--音频)
- [15. String.wz — 本地化字符串](#15-stringwz--本地化字符串)
- [16. TamingMob.wz — 坐骑](#16-tamingmobwz--坐骑)
- [17. UI.wz — 用户界面](#17-uiwz--用户界面)
- [18. 开发速查表](#18-开发速查表)

---

## 1. 目录总览

```
wz/（服务端 WZ XML 解析后的数据目录）
├── Base.wz/          (3)    渲染基础
├── Character.wz/     (37142) 装备外观精灵
├── Effect.wz/        (17)   视觉特效
├── Etc.wz/           (24)   杂项配置
├── Item.wz/          (155)  物品（6子目录）
├── Map.wz/           (5725) 地图（5子目录）
├── Mob.wz/           (2058) 怪物
├── Morph.wz/         (42)   变身
├── Npc.wz/           (7198) NPC
├── Quest.wz/         (6)    任务
├── Reactor.wz/       (421)  反应堆
├── Skill.wz/         (76)   技能
├── Sound.wz/         (45)   音频
├── String.wz/        (20)   字符串
├── TamingMob.wz/     (7)    坐骑
└── UI.wz/            (19)   UI
```

### WZ 节点基础类型

| 类型 | XML标签 | 说明 |
|------|---------|------|
| int | `<int name="price" value="100"/>` | 整数 |
| string | `<string name="islot" value="Cp"/>` | 字符串 |
| float | `<float name="mobRate" value="1.0"/>` | 浮点 |
| double | `<double name="unitPrice" value="0.3"/>` | 双精度 |
| vector | `<vector name="origin" x="0" y="32"/>` | 二维向量 |
| canvas | `<canvas name="icon" width="32" height="32">` | 精灵帧 |
| sound | `<sound name="Bgm00/FloralLife"/>` | 音频引用 |
| uol | `<uol name=".." value="../path"/>` | 节点链接 |
| imgdir | `<imgdir name="info">` | 容器目录 |
| null | `<null name="marker"/>` | 标记占位符 |

---

## 2. Base.wz — 渲染基础

**3文件：** StandardPDD.img, smap.img, zmap.img

- **StandardPDD.img**：6组职业 → `{等级: 物防值}` 映射
- **smap.img / zmap.img**：装备层级渲染Z序

| 部位 | 键 | z层值 |
|------|-----|-------|
| 武器 | Wp | weaponOverArm |
| 披风 | Sr | capeOverBody |
| 手套 | Gl/Gw | gloveOverBody |
| 身体 | Bd | body |
| 盾牌 | Si | shield |
| 帽子 | Cp | cap / capOverHair |
| 头发 | H1~H6 | hairOverHead / backHair / hairShade |
| 鞋 | So | shoes |
| 裤 | Pn | pantsOverShoesBelowMailChest |

---

## 3. Character.wz — 装备外观

**17子目录，37142文件**

| 子目录 | 文件数 | islot | ID范围 | 说明 |
|--------|--------|-------|--------|------|
| Hair | 10167 | Hr | 0003xxxx | 发型（最大目录） |
| Weapon | 4862 | Wp | 0120~0170 | 武器 |
| Face | 3238 | Fc | 0002xxxx | 脸型（含表情帧） |
| Dragon | 3007 | - | 多范围 | Evan龙装备 |
| Coat | 2995 | Ma | 0104xxxx | 上衣 |
| Cape | 2985 | Sr | 0110xxxx | 披风层 |
| Cap | 2466 | Cp | 0100xxxx | 帽子 |
| Accessory | 2308 | Af/Ay/Ae/Pe/Be/Me | 0101~0119 | 饰品 |
| Longcoat | 1648 | MaPn | 0105xxxx | 套服 |
| Shoes | 1019 | So | 0107xxxx | 鞋子 |
| Ring | 806 | Ri | 0111xxxx | 戒指 |
| Pants | 589 | Pn | 0106xxxx | 裤子 |
| Glove | 556 | Gv | 0108xxxx | 手套 |
| PetEquip | 236 | - | 0180xxxx | 宠物装备 |
| Shield | 134 | Si | 0109xxxx | 盾牌 |
| TamingMob | 93 | Tm | 0190xxxx | 坐骑 |
| Afterimage | 15 | - | 描述命名 | 武器残影 |

### 装备 info 属性

```
info/
├── islot           ← 装备栏位代码（见上表）
├── vslot           ← 可视栏位约束
├── cash            ← 0=普通, 1=现金
├── price           ← 售价(mesos)
├── tuc             ← 升级槽数
├── reqJob          ← 职业要求（位掩码）
├── reqLevel        ← 等级要求
├── reqSTR/DEX/INT/LUK     ← 属性要求
├── incSTR/DEX/INT/LUK     ← 属性加成
├── incPAD/MAD/PDD/MDD     ← 攻防加成
├── incMHP/MMP      ← HP/MP加成
├── incACC/EVA      ← 命中/回避
├── incSpeed/Jump   ← 速度/跳跃
└── tradeBlock      ← 1=不可交易
```

---

## 4. Effect.wz — 视觉特效

**17文件**

| 文件 | 条目 | 说明 |
|------|------|------|
| BasicEff.img | 201 | 通用基础特效（升级/连击/Buff/拾取/传送门） |
| CharacterEff.img | 79 | 装备发光特效（按物品ID） |
| Direction.img | 337 | 过场动画1（Cygnus/Noblesse/Aran转职） |
| Direction1.img | 949 | 技能过场（Brandish/Combo等大招） |
| Direction2.img | 103 | Aran教程过场 |
| Direction3.img | 302 | 新手教程过场 |
| ItemEff.img | 89 | 物品使用特效 |
| MapEff.img | 5 | 地图特效（Bubbling/Viewrange/NpcSummon/NpcReturn） |
| OnUserEff.img | 25 | 用户界面特效（教程箭头/聊天气泡） |
| PetEff.img | 102 | 宠物特效（按宠物ID） |
| SetEff.img | 631 | 套装特效（~35套） |
| SkillName1~4.img | 68 | 技能名弹窗（战/法/弓/飞） |
| Summon.img | 28 | 召唤袋特效(0~27) |
| Tomb.img | 3 | 墓碑（fall/land） |

---

## 5. Etc.wz — 游戏配置

**24文件**

| 文件 | 条目 | 关键属性 |
|------|------|----------|
| Commodity.img | 8948 | SN, ItemId, Count, Price, Period, Gender, OnSale |
| MapNeighbors.img | 5263 | 地图ID → 邻居地图ID列表 |
| ItemMake.img | 3743 | reqLevel, reqSkillLevel, recipe(材料), randomReward(产出) |
| NpcLocation.img | 1696 | NPC ID → 地图ID列表 |
| CashPackage.img | 871 | SN → 礼包内物品 |
| OXQuiz.img | 563 | q(问题), a(0/1), d(解析) |
| VegaSpell.img | 202 | item, prob(如[R8]0.1) |
| MakeCharInfo.img | 73 | 脸/发/肤色/初始装备（分男女） |
| Tips.img | 22 | levelMin/Max, tip |
| Swindle.img | 19 | word(触发词), warn(警告语) |
| BlockReason.img | 13 | type(HACK/BOT/AD...), msg |
| RecommendSkill.img | 12 | 职业ID → (等级→技能ID) |
| EmotionEffect.img | 11 | (仅oops有动画) |
| Category.img | 31 | 商城分类树 |
| ForbiddenName.img | ~180 | 角色名黑名单 |
| Curse.img | ~数千 | 脏话过滤 |
| ScriptInfo.img | - | 脚本名→描述 |
| DeveloperNpc.img | 9 | 开发者NPC(Ronan等) |
| ChatBlockReason.img | 7 | type, msg |
| MedalQuestCategory.img | 4 | Job/General/Challenge/Event |
| NPT_exception.img | - | NPT交易例外物品ID |
| Halloween.img | 120 | 万圣节物品ID列表 |
| QuestCategory.img | - | 任务分类名 |
| ScanBlock.img | 0 | 空 |

---

## 6. Item.wz — 物品数据

**6子目录，155文件**

### 6.1 Cash/（49文件，0501~0599）

| IMG | 说明 | 特征 |
|-----|------|------|
| 0501 | 现金特效/皮肤 | effect动画 |
| 0502 | 现金子弹/武器覆盖 | bullet+hit精灵 |
| 0503 | 雇佣店员 | employee动画 |
| 0505 | 纯视觉现金 | 仅图标 |
| 0550 | 宠物生命水 | addTime(秒), maxDays |

### 6.2 Consume/（28文件，0200~0245）

| IMG | 分类 | 特征属性 |
|-----|------|----------|
| 0200 | HP药水 | spec.hp |
| 0201 | HP食物 | spec.hp |
| 0202 | 混合药水 | spec.hp / spec.mp / spec.hpR / spec.mpR |
| 0203 | 传送卷轴 | spec.moveTo |
| 0204 | 装备卷轴 | incPAD/MAD/PDD/MDD + success |
| 0205 | 状态治愈 | spec.poison/darkness/weakness/curse/seal |
| 0206 | 子弹(火枪) | bullet + incPAD |
| 0207 | 飞镖(拳套) | bullet + incPAD + reqLevel |
| 0210 | 怪物召唤袋 | mob(怪物ID+概率) |
| 0212 | 魔法粉末 | spec.inc |
| 0216 | 限时物品 | timeLimited=1 |
| 0219 | 特殊消耗品 | 仅price |
| 0221 | 变身药水 | spec.morph=1 + spec.time |
| 0224 | 活动限定 | tradeBlock + notSale + only |
| 0226 | 疲劳恢复 | spec.incFatigue(负值) |
| 0227 | 怪物放置 | mob + create + 坐标范围 |
| 0228 | 技能书(70%/50%) | skill + masterLevel + success |
| 0229 | 技能书(带前置) | 同上 + reqSkillLevel |
| 0231 | 限时活动 | timeLimited |
| 0232 | 限时活动 | timeLimited |
| 0233 | 高级子弹 | bullet + incPAD + reqLevel |
| 0234 | 空壳物品 | 无spec |
| 0236 | 幽灵药水 | spec.ghost=1 + time |
| 0237 | 经验券 | spec.exp + maxLevel |
| 0238 | 怪物卡 | monsterBook=1 + mob |
| 0243 | 任务脚本物品 | spec.script + spec.npc + quest=1 |
| 0244 | 组队任务 | pquest=1 + randomMoveInFieldSet |
| 0245 | 经验Buff | spec.expBuff + time |

### 6.3 Etc/（18文件，0400~0431）

| IMG | 说明 | IMG | 说明 |
|-----|------|-----|------|
| 0400 | 加工材料（大文件） | 0416 | 宠物手册 |
| 0401 | 弹药（箭矢） | 0417 | 手册 |
| 0402 | 矿石/宝石 | 0421 | 任务限定掉落 |
| 0403 | 加工后材料 | 0422 | 材料 |
| 0405 | 限时活动物品 | 0425 | 材料 |
| 0408 | 廉价通用材料 | 0426 | 材料 |
| 0413 | 锻造产物 | 0428 | 简单图标物品 |
| 0414 | 组件材料 | 0429 | 带动画物品(effect) |
| 0430 | 限时活动 | 0431 | 代币/硬币(slotMax=1000) |

### 6.4 Install/（2文件）

| IMG | 说明 | 特征 |
|-----|------|------|
| 0301 | 椅子/沙发(恢复) | recoveryHP/MP, reqLevel, effect, distanceX/Y/direction |
| 0399 | 奖励兑换物 | iconReward, price=50~300, slotMax=100 |

### 6.5 Pet/（56文件，5000000~5000102）

- **info:** hungry(饥饿1~3), life(寿命90或0=永久), cash=1, permanent, chatBalloon, nameTag
- **interact:** 28个命令(c1~c28) → command/inc(亲密度)/prob(概率)/success/fail
- **food:** 4组食物反应
- **slang:** 4组宠物话语
- **动画:** stand0~1/hungry/move/jump/rest0/chat/angry/hang/rise/dung/stretch/prone/alert/cry/fly/nap/tedious

### 6.6 Special/（4文件）

| IMG | 说明 |
|-----|------|
| 0900 | 勋章 |
| 0910 | 商城套装礼包(~900个, name中英文) |
| 0911 | 背包扩容券(5个, delta=8) |
| MaplePoint | 抵用券面额(100~100000, 30个) |

---

## 7. Map.wz — 地图系统

**5子目录，5725文件**

| 子目录 | 文件 | 说明 |
|--------|------|------|
| Back | 111 | 背景图片（Amoria/Christmas/ShaolinCN等） |
| Obj | 111 | 物件精灵（acc1-12/tree/crystal等） |
| Tile | 108 | 图块集（basicTile/woodTile/iceTile等） |
| WorldMap | 28 | 世界地图（WorldMap000~230） |
| Map/Map0~9 | 5363 | 实际地图文件 |

### Map/子目录分布

| 子目录 | 文件 | 区域 |
|--------|------|------|
| Map0 | 43 | 登录/新手 |
| Map1 | 882 | 维多利亚岛 |
| Map2 | 1063 | 艾尔纳斯/水下世界 |
| Map3 | 198 | 路德斯湖 |
| Map5 | 57 | 武陵/尼哈沙漠 |
| Map6 | 204 | 神木村 |
| Map7 | 103 | 马斯特利亚 |
| Map8 | 62 | 时间神殿 |
| Map9 | 2751 | 活动/特殊/签到 |

### 地图 info 关键属性

```
info/
├── version           ← 地图版本
├── town              ← 1=城镇
├── bgm               ← 背景音乐路径
├── returnMap         ← 死亡回城地图
├── forcedReturn      ← 强制返回地图
├── mapMark           ← 小地图标记文字
├── mobRate           ← 怪物刷新倍率
├── swim              ← 1=游泳地图
├── fly               ← 1=飞行地图
├── moveLimit         ← 移动限制
├── fieldLimit        ← 场地限制(位掩码)
├── onUserEnter       ← 进入时执行的脚本
└── cloud             ← 1=有云雾
```

### 地图子节点

| 节点 | 说明 | 关键属性 |
|------|------|----------|
| back | 背景图层 | x/y/rx/ry/a(透明度)/bS(图片路径) |
| life | NPC/怪物生成 | type(n/m), id, x/y, fh, cy, rx0/rx1, mobTime |
| foothold | 立足点(3层) | x1/y1/x2/y2, prev/next(链式) |
| ladderRope | 梯子绳索 | l(层), uf(方向), x, y1/y2 |
| seat | 休息座位 | x, y |
| miniMap | 小地图 | width/height/centerX/Y/mag |
| portal | 传送门 | pn, pt(类型), x/y, tm(目标地图), tn(目标门) |

### 传送门类型(pt)

| pt | 说明 |
|----|------|
| 0 | 出生点 |
| 1 | 隐藏 |
| 2 | 常规(按↑) |
| 3 | 触碰 |
| 4 | 不可见触碰 |
| 7 | 脚本控制 |

### 场地限制(fieldLimit)位掩码

| 位 | 含义 | 位 | 含义 |
|----|------|----|------|
| 0x01 | 禁止跳跃 | 0x10 | 禁止丢物品 |
| 0x02 | 禁止技能 | 0x20 | 禁止传送 |
| 0x04 | 禁止召唤 | 0x40 | 禁止变身 |
| 0x08 | 禁止消耗品 | 0x80 | 禁止骑宠 |

---

## 8. Mob.wz — 怪物数据

**2058文件 + QuestCountGroup/（4文件）**

### 怪物ID范围

| ID | 区域 | 等级 |
|----|------|------|
| 0100100~ | 新手/维多利亚 | 1~30 |
| 1xxxxxx | 维多利亚深处 | 10~50 |
| 2xxxxxx | 艾尔纳斯/水下 | 30~70 |
| 3xxxxxx | 路德斯湖 | 50~90 |
| 5xxxxxx | 武陵/神木 | 70~120 |
| 6xxxxxx | 马斯特利亚 | 80~130 |
| 8xxxxxx | BOSS | BOSS |
| 9xxxxxx | 活动/特殊BOSS | 特殊 |

### 怪物 info 属性

```
info/
├── bodyAttack     ← 碰撞伤害
├── level          ← 等级
├── maxHP/maxMP    ← 最大HP/MP
├── speed          ← 移动速度(负数=慢)
├── PADamage       ← 物理攻击力
├── PDDamage       ← 物理防御力
├── MADamage       ← 魔法攻击力
├── MDDamage       ← 魔法防御力
├── acc/eva        ← 命中/回避
├── exp            ← 经验值
├── undead         ← 1=不死系
├── pushed         ← 可击退距离
├── fs             ← 击退力系数
├── summonType     ← 召唤类型(0不召唤/1小怪/2同族)
├── mobType        ← 怪物类型(0普通/1不死/2飞行/3BOSS)
├── boss           ← 1=BOSS
├── firstAttack    ← 先手攻击
└── publicReward   ← 1=公开奖励
```

### 动画帧通用属性

每帧(stand/move/hit1/die1/jump/fly等)：`origin`, `lt`/`rb`(碰撞盒), `head`(头部位置), `delay`, `a0`/`a1`(透明动画)

### 攻击节点 attackN

```
attackN/info/
├── type           ← 攻击类型
├── attackAfter    ← 攻击后摇(ms)
├── conMP          ← MP消耗
├── range          ← 攻击范围
├── knockback      ← 击退力度
├── deadlyAttack   ← 致命攻击概率
└── disease        ← 附加异常状态
```

---

## 9. Morph.wz — 变身动作帧

**42文件（0001~0045 + 1000~1103）**

```
info/
├── speed          ← 移速(%)
├── jump           ← 跳高(%)
├── fs             ← 摩擦系数
├── swim           ← 游泳速度
├── fatigue        ← 疲劳累积
├── morphEffect    ← 变身特效
└── superman       ← 飞行模式
```

基础动画：walk/stand/jump/prone/sit/ladder/rope/fly  
战斗动作（高级变身）：stabO1/stabO2/shoot1/shoot2/swing/doublefire/backspin/somersault/dragonstrike 等

---

## 10. Npc.wz — NPC数据

**7198文件，无子目录**

NPC ID：7位数字，`000xxxx`（系统功能）～ `9977777`（开发者）

```
info/
├── speak/         ← 对话行引用(n0/n1/n2...)
└── script/        ← 脚本名（可选，如"rithTeleport"）

动画：stand/move/fly/finger/wink/say
每帧：origin, z, delay, a0/a1
```

---

## 11. Quest.wz — 任务系统

**6文件**

| 文件 | 说明 | 核心结构 |
|------|------|----------|
| Act.img | 任务完成动作 | nextQuest, exp, npc, item(id/count) |
| Check.img | 任务条件 | npc, lvmin, lvmax, job, item需求 |
| QuestInfo.img | 任务元数据 | name, desc, area, order, autoStart |
| Say.img | 任务对话 | 对话树, yes/no分支 |
| PQuest.img | 组队任务 | 排名标准 |
| Exclusive.img | 勋章独占 | 任务ID列表 |

---

## 12. Reactor.wz — 反应堆

**421文件**

```
{ID}.img/
├── info → info(描述)
├── action         ← 动作字符串("mBoxItem0","oBoxItem0"等)
├── 0/             ← 状态0
│   ├── event → type, state(跳转目标)
│   └── hit → canvas帧(origin/ delay)
├── 1/             ← 状态1（中间）
└── 2/             ← 状态2（最终/掉落）
```

---

## 13. Skill.wz — 技能数据

**76文件**

### 职业文件快速对照

| 文件 | 职业 | 文件 | 职业 |
|------|------|------|------|
| 000 | 新手 | 310~312 | 神射手线 |
| 100~132 | 战士三系 | 320~322 | 箭神线 |
| 200~232 | 法师三系 | 400~422 | 飞侠双线 |
| 300~322 | 弓手双线 | 500~522 | 海盗双线 |
| 800 | GM | 900~910 | 贵族 |
| 1000~1112 | 龙神 | 1200~1212 | 战神 |
| 1300~1312 | 反抗者 | 1400~1412 | 双弩 |
| 1500~1512 | 幻影 | 2000~2112 | 恶魔 |
| BFSkill | BOSS技 | MobSkill | 怪物技能 |

### 技能 level 节点属性

```
level/{N}/
├── mpCon/hpCon    ← MP/HP消耗
├── damage         ← 伤害%
├── x/y            ← 通用参数
├── time           ← 持续时间/秒
├── cooltime       ← 冷却/秒
├── mobCount       ← 目标数
├── bulletCount    ← 弹道数
├── mastery        ← 熟练度%
├── fixdamage      ← 固定伤害
├── prop           ← 触发概率%
├── range          ← 范围
├── knockback      ← 击退距离
├── dot/dotTime    ← DOT伤害/时间
├── lt/rb          ← 攻击矩形
└── cr             ← 暴击率
```

---

## 14. Sound.wz — 音频

**45文件**

- **BGM:** Bgm00~Bgm21 + BgmCN/Event/GL/Jp/MY/SG/UI (22个)
- **音效:** CashEffect/ConsumeEffect/Field/Game/Item/Mob/Pet/Skill/UI/Weapon 等(23个)

格式：`<sound name="路径/文件名"/>`

---

## 15. String.wz — 本地化字符串

**20文件**

| 文件 | 内容 | 属性 |
|------|------|------|
| Cash.img | 现金物品 | name, desc |
| Consume.img | 消耗品 | name, desc |
| Eqp.img | 装备 | Eqp/{类别}/{ID} → name, desc |
| Etc.img | 其他物品 | name, desc |
| Map.img | 地图 | name, streetName |
| Mob.img | 怪物 | name |
| MonsterBook.img | 怪物卡 | name, desc |
| Npc.img | NPC | name |
| Pet.img | 宠物 | name, desc |
| Skill.img | 技能 | name, desc, bookName, h1~hN |
| 其他 | EULA/改名/转服/提示 | 文本 |

---

## 16. TamingMob.wz — 坐骑

**7文件（0001~0007）**

```
info/
├── speed          ← 移速
├── jump           ← 跳高
├── fs             ← 摩擦系数
├── swim           ← 游泳速度
└── fatigue        ← 疲劳累积
```

---

## 17. UI.wz — 用户界面

**19文件**

| 文件 | 说明 |
|------|------|
| Basic.img | 鼠标光标 |
| BuffIcon.img | Buff图标 |
| CashShop.img | 商城界面 |
| ChatBalloon.img | 聊天气泡 |
| DialogImage.img | 对话背景 |
| Login.img | 登录界面 |
| Logo.img | Logo |
| StatusBar.img | HUD(HP/MP/EXP条) |
| UIWindow.img | 通用窗口 |
| tutorial.img | 教程UI |

按钮状态：`normal/mouseOver/pressed/disabled/keyFocused`

---

## 18. 开发速查表

### 18.1 改端操作 → 涉及目录

| 操作 | 目录 | 关键节点 |
|------|------|----------|
| 新增装备 | Character + String(Eqp) | islot + 属性 + 动画帧 |
| 修改技能 | Skill + String(Skill) | level/N属性 |
| 新增消耗品 | Item(Consume) + String(Consume) | info + spec |
| 新增NPC | Npc + String(Npc) | speak + stand |
| 新增怪物 | Mob + String(Mob) | info属性 + 动画帧 |
| 修改地图 | Map | info/bgm + life + portal |
| 新增任务 | Quest(4文件) | Act/Check/QuestInfo/Say |
| 修改商城 | Etc(Commodity) | SN, ItemId, Price |
| 新增反应堆 | Reactor | action + 状态0→1→2 |

### 18.2 Canvas 通用属性

| 属性 | 类型 | 说明 |
|------|------|------|
| width/height | int | 图片尺寸 |
| origin | vector | 锚点 |
| z | int | Z序层级 |
| delay | int | 帧延迟(ms) |
| lt/rb | vector | 碰撞盒 |
| head | vector | 头部位置 |
| a0/a1 | int | 透明度动画 |

### 18.3 服务端 WZ 路径映射

```
客户端WZ文件                    服务端XML路径
Character.wz → Character/     → wz/Character.wz/
Item.wz      → Item/          → wz/Item.wz/Cash|Consume|Etc|Install|Pet|Special/
Map.wz       → Map/           → wz/Map.wz/Back|Map|Obj|Tile|WorldMap/
Mob.wz       → Mob/           → wz/Mob.wz/
Npc.wz       → Npc/           → wz/Npc.wz/
Skill.wz     → Skill/         → wz/Skill.wz/
String.wz    → String/        → wz/String.wz/
UI.wz        → UI/            → wz/UI.wz/
```

### 18.4 Java 中对应的 Provider 类

| WZ目录 | Java Provider | 包路径 |
|--------|---------------|--------|
| Item.wz(Consume/Etc/Cash) | ItemInformationProvider | org.gms.server |
| Skill.wz | SkillbookInformationProvider | org.gms.provider |
| Map.wz | MapleDataProvider("Map.wz") | org.gms.server.maps |
| Mob.wz | MapleDataProvider("Mob.wz") | org.gms.server.life |
| Quest.wz | MapleDataProvider("Quest.wz") | org.gms.server.quest |
| String.wz | StringDataProvider | org.gms.provider |

---

> **参考：**
> - 完整教学文档：`../冒险岛WZ资源目录完整参考.md`
> - 项目架构：`../北斗服务器-项目架构及开发手册.md`
> - 改端教程：`../冒险岛改端教学文档.md`

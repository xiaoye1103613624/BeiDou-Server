# 冒险岛 WZ 资源目录完整参考

> **适用版本：** GMS v083（北斗服务器）  
> **最后更新：** 2026-06-15  
> **用途：** 改端开发时的 WZ 资源查阅手册，涵盖全部 16 个一级目录、子目录、IMG 文件及节点属性

---

## 目录

1. [WZ 目录总览](#1-wz-目录总览)
2. [Base.wz — 角色渲染基础](#2-basewz--角色渲染基础)
3. [Character.wz — 角色装备外观](#3-characterwz--角色装备外观)
4. [Effect.wz — 视觉特效](#4-effectwz--视觉特效)
5. [Etc.wz — 杂项游戏配置](#5-etcwz--杂项游戏配置)
6. [Item.wz — 物品数据](#6-itemwz--物品数据)
7. [Map.wz — 地图系统](#7-mapwz--地图系统)
8. [Mob.wz — 怪物数据](#8-mobwz--怪物数据)
9. [Morph.wz — 变身动作帧](#9-morphwz--变身动作帧)
10. [Npc.wz — NPC 外观与对话](#10-npcwz--npc-外观与对话)
11. [Quest.wz — 任务系统](#11-questwz--任务系统)
12. [Reactor.wz — 地图反应堆](#12-reactorwz--地图反应堆)
13. [Skill.wz — 技能数据](#13-skillwz--技能数据)
14. [Sound.wz — 音频资源](#14-soundwz--音频资源)
15. [String.wz — 本地化字符串](#15-stringwz--本地化字符串)
16. [TamingMob.wz — 坐骑数据](#16-tamingmobwz--坐骑数据)
17. [UI.wz — 用户界面](#17-uiwz--用户界面)
18. [附录：常用属性速查](#18-附录常用属性速查)

---

## 1. WZ 目录总览

WZ 文件是冒险岛的客户端资源包，服务端通过解析 WZ XML 来获取游戏数据。BeiDou 项目中 WZ XML 存放于 `gms-server/wz/` 目录。

```
wz/
├── Base.wz/          # 3 文件    — 角色渲染基础（属性表、精灵层级）
├── Character.wz/     # 37,142 文件 — 角色装备外观（17个子目录）
├── Effect.wz/        # 17 文件   — 视觉特效（技能/过场/物品）
├── Etc.wz/           # 24 文件   — 杂项配置（商城/配方/题库）
├── Item.wz/          # 155 文件  — 物品数据（6个子目录）
├── Map.wz/           # 5,725 文件 — 地图系统（背景/物件/图块/地图）
├── Mob.wz/           # 2,058 文件 — 怪物数据
├── Morph.wz/         # 42 文件   — 变身/骑乘动作帧
├── Npc.wz/           # 7,198 文件 — NPC 外观与对话
├── Quest.wz/         # 6 文件    — 任务系统
├── Reactor.wz/       # 421 文件  — 地图交互对象
├── Skill.wz/         # 76 文件   — 职业技能
├── Sound.wz/         # 45 文件   — 音频/BGM/音效
├── String.wz/        # 20 文件   — 本地化字符串
├── TamingMob.wz/     # 7 文件    — 坐骑数据
└── UI.wz/            # 19 文件   — 用户界面图形
```

### 1.1 WZ 节点基础类型

| 节点类型 | XML 标签 | 说明 | 示例 |
|----------|----------|------|------|
| **属性节点** | `<int>`, `<string>`, `<float>`, `<double>`, `<vector>` | 存储单一键值对数据 | `<int name="price" value="100"/>` |
| **画布节点** | `<canvas>` | 存储图片（含宽高、原点、延迟） | `<canvas name="icon" width="32" height="32">` |
| **目录节点** | `<imgdir>` | 容器节点，可嵌套子节点 | `<imgdir name="info">...</imgdir>` |
| **音效节点** | `<sound>` | 音频文件引用 | `<sound name="Bgm00/FloralLife"/>` |
| **链接节点** | `<uol>` | 引用另一个节点的内容（避免重复） | `<uol name="stand" value="../00002000/stand"/>` |

### 1.2 IMG 文件命名规则

| 命名方式 | 目录 | 说明 |
|----------|------|------|
| **7位数字ID** | Mob, Npc, Reactor | 如 `0100100.img.xml` = 蜗牛 |
| **8位数字ID** | Character, Item | 如 `01040000.img.xml` = 上衣 |
| **4位前缀** | Item子目录 | 如 `0200.img.xml` = HP药水分类 |
| **3位职业码** | Skill | 如 `100.img.xml` = 战士系技能 |
| **描述性名称** | Effect, Sound, UI, Map子目录 | 如 `BasicEff.img.xml`, `Bgm00.img.xml` |

---

## 2. Base.wz — 角色渲染基础

**文件数：** 3  
**功能：** 控制角色属性的基础参数和装备渲染的精灵层级顺序

### 2.1 文件清单

| 文件名 | 大小 | 说明 |
|--------|------|------|
| `StandardPDD.img.xml` | 3.9 KB | 各职业等级-物防对照表 |
| `smap.img.xml` | 6.3 KB | 角色装备精灵渲染层级图（主Z序） |
| `zmap.img.xml` | 4.9 KB | 辅助Z序图（含坐骑层等） |

### 2.2 StandardPDD.img — 防御力对照表

**ID 节点：** `0` ~ `5`（6组职业曲线）

```
StandardPDD.img
├── 0/  ← 职业组0（如新手/战士基础）
│   ├── <int name="5"  value="2"/>
│   ├── <int name="10" value="4"/>
│   └── ...（17~28条等级→物防映射）
├── 1/  ← 职业组1
└── ...（共6组）
```

| 属性 | 类型 | 说明 |
|------|------|------|
| `{level}` | int | 键=等级(5/8/10/15/20...100)，值=物防值 |

### 2.3 smap.img / zmap.img — 精灵渲染层级

**ID 节点：** 字符串键（身体部位简称），值=渲染层级名称

| 部位键 | 含义 | z层值 |
|--------|------|-------|
| `Wp` | 武器 (Weapon) | `weaponOverGlove` / `weaponOverArm` |
| `Sr` | 披风 (Cape) | `capeOverBody` |
| `Gl` / `Gw` | 手套 / 手套腕部 | `gloveOverBody` |
| `Bd` | 身体 (Body/Hand/Arm) | `body` |
| `Si` | 盾牌 (Shield) | `shield` |
| `MaGw` | 上衣 + 手套腕部 | `mailArmOverGloveWrist` |
| `Cp` | 帽子 (Cap) | `cap` / `capOverHair` |
| `Fc` | 脸 (Face) | `face` |
| `Hd` | 头 (Head) | `head` |
| `So` | 鞋子 (Shoes) | `shoes` |
| `Pn` | 裤子 (Pants) | `pantsOverShoesBelowMailChest` |
| `H1~H6` | 头发各层 | `hairOverHead` / `backHair` / `hairShade` |
| `Ae/Ay/Af` | 耳环/眼饰/脸饰 | `accessoryEar` / `accessoryEye` / `accessoryFace` |

**渲染顺序标记（smap 中的 null 节点）：**
```
mobEquipFront → tamingMobFront → characterStart → emotionOverBody
→ characterEnd → tamingMobMid → saddleMid → mobEquipBelowAll
```

---

## 3. Character.wz — 角色装备外观

**文件数：** 37,142  
**功能：** 所有玩家可穿戴装备的外观精灵和动画数据

### 3.1 子目录一览

| 子目录 | 文件数 | 装备栏位 | ID范围 | 说明 |
|--------|--------|----------|--------|------|
| **Hair/** | 10,167 | Hr | 00030000~00039999 | 发型（最大目录，含前/后/阴影层） |
| **Weapon/** | 4,862 | Wp | 0120xxxx~0170xxxx | 武器（含残影类型、音效） |
| **Face/** | 3,238 | Fc | 00020000~00029999 | 脸型（含表情动画帧） |
| **Dragon/** | 3,007 | (龙) | 多范围 | Evan龙伙伴装备层 |
| **Coat/** | 2,995 | Ma | 0104xxxx为主 | 上衣/身体层 |
| **Cape/** | 2,985 | (披风) | 多范围 | 披风/背部渲染层 |
| **Cap/** | 2,466 | Cp | 01000000~01009999 | 帽子/头盔 |
| **Accessory/** | 2,308 | Af/Ay/Ae等 | 0101~0119 | 脸饰/眼饰/耳环/项链/腰带/勋章等 |
| **Longcoat/** | 1,648 | MaPn | 01050000~01059999 | 套服（同时占上衣+裤子） |
| **Shoes/** | 1,019 | So | 01070000~01079999 | 鞋子 |
| **Ring/** | 806 | Ri | 0111xxxx | 戒指 |
| **Pants/** | 589 | Pn | 01060000~01069999 | 裤子/裙子 |
| **Glove/** | 556 | Gv | 01080000~01089999 | 手套（左右手分离） |
| **PetEquip/** | 236 | - | 0180xxxx | 宠物装备（按宠物ID分组） |
| **Shield/** | 134 | Si | 01090000~01099999 | 盾牌 |
| **TamingMob/** | 93 | Tm | 0190xxxx~0198xxxx | 坐骑 |
| **Afterimage/** | 15 | - | 描述性命名 | 武器挥动残影 |
| **(根目录)** | 18 | Bd | 0000xxxx/0001xxxx | 男女基础身体模板 |

### 3.2 装备通用属性节点（info/）

每个装备 IMG 文件结构：

```
{8位装备ID}.img/
├── info/
│   ├── <canvas name="icon" width="32" height="32">      ← 背包图标
│   ├── <canvas name="iconRaw" width="32" height="32">    ← 原始图标
│   ├── <string name="islot" value="Cp"/>                 ← 装备栏位代码
│   ├── <string name="vslot" value="CpH1H5"/>             ← 可视栏位约束
│   ├── <int name="cash" value="0"/>                      ← 0=普通 1=现金物品
│   ├── <int name="price" value="1"/>                     ← 售价(mesos)
│   ├── <int name="tuc" value="7"/>                       ← 可升级次数(卷轴槽)
│   ├── <int name="reqJob" value="0"/>                    ← 职业要求(位掩码)
│   ├── <int name="reqLevel" value="30"/>                 ← 等级要求
│   ├── <int name="reqSTR" value="0"/>                    ← 力量要求
│   ├── <int name="reqDEX" value="0"/>                    ← 敏捷要求
│   ├── <int name="reqINT" value="0"/>                    ← 智力要求
│   ├── <int name="reqLUK" value="0"/>                    ← 运气要求
│   ├── <int name="incSTR" value="3"/>                    ← 力量加成
│   ├── <int name="incDEX" value="0"/>                    ← 敏捷加成
│   ├── <int name="incINT" value="0"/>                    ← 智力加成
│   ├── <int name="incLUK" value="0"/>                    ← 运气加成
│   ├── <int name="incPAD" value="0"/>                    ← 物理攻击加成
│   ├── <int name="incMAD" value="0"/>                    ← 魔法攻击加成
│   ├── <int name="incPDD" value="15"/>                   ← 物理防御加成
│   ├── <int name="incMDD" value="0"/>                    ← 魔法防御加成
│   ├── <int name="incMHP" value="0"/>                    ← 最大HP加成
│   ├── <int name="incMMP" value="0"/>                    ← 最大MP加成
│   ├── <int name="incACC" value="0"/>                    ← 命中加成
│   ├── <int name="incEVA" value="0"/>                    ← 回避加成
│   ├── <int name="incSpeed" value="0"/>                  ← 速度加成
│   ├── <int name="incJump" value="0"/>                   ← 跳跃加成
│   └── <int name="tradeBlock" value="1"/>                ← 1=不可交易
└── (动画帧)/
    ├── default/      ← 默认外观
    ├── walk1/        ← 行走帧1
    ├── stand1/       ← 站立帧1
    ├── jump/         ← 跳跃帧
    ├── prone/        ← 趴下帧
    ├── ladder/       ← 爬梯帧
    └── ...
```

### 3.3 装备栏位代码（islot）对照表

| islot | 栏位名称 | 客户端显示 | ID前缀 |
|-------|----------|-----------|--------|
| `Cp` | 帽子 | 帽子 | 0100xxxx |
| `Af` | 脸饰 | 脸饰 | 0101xxxx |
| `Ay` | 眼饰 | 眼饰 | 0102xxxx |
| `Ae` | 耳环 | 耳环 | 0103xxxx |
| `Ma` | 上衣 | 上衣 | 0104xxxx |
| `MaPn` | 套服 | 套服 | 0105xxxx |
| `Pn` | 裤子 | 裤子 | 0106xxxx |
| `So` | 鞋子 | 鞋子 | 0107xxxx |
| `Gv` | 手套 | 手套 | 0108xxxx |
| `Si` | 盾牌 | 盾牌 | 0109xxxx |
| `Sr` | 披风 | 披风 | 0110xxxx |
| `Ri` | 戒指 | 戒指 | 0111xxxx |
| `Pe` | 项链 | 项链 | 0112xxxx |
| `Be` | 腰带 | 腰带 | 0113xxxx |
| `Me` | 勋章 | 勋章 | 0114xxxx |
| `Wp` | 武器 | 武器 | 0120~0170 |
| `Tm` | 坐骑 | 坐骑 | 0190xxxx |
| `Fc` | 脸型 | - | 0002xxxx |
| `Hr` | 发型 | - | 0003xxxx |
| `Bd` | 皮肤 | - | 0000xxxx |

### 3.4 武器子目录详细

| ID前缀 | afterImage | 武器类型 | 说明 |
|--------|-----------|----------|------|
| 0120xxxx | swordOL | 单手剑 | 1H Sword |
| 0121xxxx | mace | 单手钝器 | 1H BW |
| 0122xxxx | swordOL | 单手斧 | 1H Axe |
| 0130xxxx | swordTS/TL | 双手剑 | 2H Sword |
| 0131xxxx | swordOL | 双手钝器 | 2H BW |
| 0132xxxx | mace | 枪/矛 | Spear/Polearm |
| 0133xxxx | poleArm | 矛 | Polearm |
| 0137xxxx | mace | 短杖 | Wand |
| 0138xxxx | mace | 长杖 | Staff |
| 0140xxxx | barehands | 拳套 | Knuckle |
| 0142xxxx | gun | 手枪 | Gun |
| 0145xxxx | bow | 弓 | Bow |
| 0146xxxx | crossBow | 弩 | Crossbow |
| 0147xxxx | swordOL | 短剑/双刀 | Dagger/Katara |
| 0170xxxx | 各种 | 现金武器 | Cash Weapon |

### 3.5 Afterimage（武器残影）

15 个文件，按武器类型命名：

| 文件 | 残影动画名 |
|------|-----------|
| `swordOL.img` / `swordOS.img` / `swordTL.img` / `swordTS.img` | 剑（单手/双手，左/右） |
| `axe.img` | 斧头 |
| `mace.img` | 钝器 |
| `spear.img` | 枪 |
| `poleArm.img` | 矛 |
| `bow.img` | 弓 |
| `crossBow.img` | 弩 |
| `gun.img` | 火枪 |
| `knuckle.img` | 拳套 |
| `barehands.img` | 空手 |
| `hit.img` | 打击特效 |
| `blank.img` | 空残影 |

每帧含 `lt`/`rb`（碰撞盒）和 `delay`。

---

## 4. Effect.wz — 视觉特效

**文件数：** 17  
**功能：** 游戏内所有视觉特效，包括技能特效、过场动画、物品效果等

### 4.1 文件清单

| 文件 | 条目数 | 说明 |
|------|--------|------|
| `BasicEff.img.xml` | 201 | **通用基础特效**：升级/连击/拾取/传送门/击中/冰冻等 |
| `CharacterEff.img.xml` | 79 | **装备发光特效**：按物品ID分组 |
| `Direction.img.xml` | 337 | **过场动画1**：Cygnus/Noblesse/Aran 转职动画 |
| `Direction1.img.xml` | 949 | **技能过场**：Brandish/Combo/Rush等大招动画 |
| `Direction2.img.xml` | 103 | **Aran教程过场**：Gasi场景动画 |
| `Direction3.img.xml` | 302 | **新手教程过场**：GoAdventure/GoLith/EvanTutorial |
| `ItemEff.img.xml` | 89 | **物品使用特效**：卷轴使用/药水使用动画 |
| `MapEff.img.xml` | 5 | **地图级特效**：Bubbling/Viewrange/NpcSummon/NpcReturn |
| `OnUserEff.img.xml` | 25 | **用户界面特效**：教程箭头/聊天气泡 |
| `PetEff.img.xml` | 102 | **宠物特效**：升级/进化/传送 |
| `SetEff.img.xml` | 631 | **套装特效**：雨衣套/矩阵套等(~35套) |
| `SkillName1.img.xml` | 22 | **技能名弹窗**：战士技能名 |
| `SkillName2.img.xml` | 20 | **技能名弹窗**：法师技能名 |
| `SkillName3.img.xml` | 14 | **技能名弹窗**：弓手技能名 |
| `SkillName4.img.xml` | 12 | **技能名弹窗**：飞侠技能名 |
| `Summon.img.xml` | 28 | **召唤特效**：召唤袋动画(0~27号) |
| `Tomb.img.xml` | 3 | **死亡特效**：墓碑落下(fall/land) |

### 4.2 BasicEff.img 子类别

| 类别 | 说明 |
|------|------|
| `LevelUp` | 升级光环特效 |
| `Combo` | 连击计数特效 |
| `Buff` | Buff施放特效 |
| `Hit` | 受击特效 |
| `Ice` | 冰冻特效 |
| `Portal` | 传送门特效 |
| `Quest` | 任务完成特效 |
| `Mob` | 怪物通用特效 |
| `Pet` | 宠物通用特效 |

### 4.3 过场动画属性

| 属性 | 类型 | 说明 |
|------|------|------|
| `type` / `start` | int | 动画类型/起始帧 |
| `visual` | int | 可视性标记 |
| `x` / `y` | int | 坐标位置 |
| `a0` / `a1` | int | 透明度动画（起始→结束） |
| `z` | int | 渲染层级 |
| `origin` | vector | 锚点 |
| `delay` | int | 帧延迟(ms) |
| `sound` | string | 关联音效 |

---

## 5. Etc.wz — 杂项游戏配置

**文件数：** 24  
**功能：** 商城商品、制作配方、地图邻接、反作弊、题库等杂项游戏数据

### 5.1 文件清单

| 文件 | 条目数 | 说明 |
|------|--------|------|
| `Commodity.img.xml` | 8,948 | **商城商品目录**（最大文件，3.1MB） |
| `MapNeighbors.img.xml` | 5,263 | **地图邻接关系** |
| `ItemMake.img.xml` | 3,743 | **制作配方**（草药/锻造/首饰） |
| `NpcLocation.img.xml` | 1,696 | **NPC所在地图** |
| `CashPackage.img.xml` | 871 | **商城礼包定义** |
| `OXQuiz.img.xml` | 563 | **OX问答题库** |
| `VegaSpell.img.xml` | 202 | **Vega卷轴概率** |
| `MakeCharInfo.img.xml` | 73 | **角色创建默认值** |
| `Tips.img.xml` | 22 | **加载画面提示**（按等级分段） |
| `Swindle.img.xml` | 19 | **反诈骗关键词**（含韩文） |
| `BlockReason.img.xml` | 13 | **封号原因定义** |
| `RecommendSkill.img.xml` | 12 | **推荐技能加点** |
| `EmotionEffect.img.xml` | 11 | **表情特效**（仅oops有数据） |
| `Category.img.xml` | 31 | **商城分类定义** |
| `ForbiddenName.img.xml` | ~180 | **角色名黑名单** |
| `Curse.img.xml` | ~数千 | **脏话过滤词库** |
| `ScriptInfo.img.xml` | - | **任务脚本描述** |
| `DeveloperNpc.img.xml` | 9 | **开发者NPC配置**（Ronan等） |
| `ChatBlockReason.img.xml` | 7 | **聊天封禁原因** |
| `MedalQuestCategory.img.xml` | 4 | **勋章分类名** |
| `QuestCategory.img.xml` | - | **任务分类名** |
| `NPT_exception.img.xml` | - | **NPT交易例外物品列表** |
| `Halloween.img.xml` | 120 | **万圣节物品列表** |
| `ScanBlock.img.xml` | 0 | 空文件（预留） |

### 5.2 Commodity.img — 商城商品属性

```
Commodity.img/
└── {商品ID}/
    ├── <int name="SN" value="10000000"/>       ← 序列号（唯一标识）
    ├── <int name="ItemId" value="1002000"/>    ← 物品ID
    ├── <int name="Count" value="1"/>           ← 购买数量
    ├── <int name="Price" value="3000"/>        ← NX价格
    ├── <int name="Period" value="90"/>         ← 有效期(天)
    ├── <int name="Priority" value="10"/>       ← 排序优先级
    ├── <int name="Gender" value="2"/>          ← 0=男 1=女 2=通用
    ├── <int name="OnSale" value="1"/>          ← 0=下架 1=在售
    └── <int name="Bonus" value="0"/>           ← 赠品标记
```

### 5.3 ItemMake.img — 制作配方属性

```
ItemMake.img/
└── 0/
    └── {物品ID}/
        ├── <int name="reqLevel" value="1"/>              ← 角色等级要求
        ├── <int name="reqSkillLevel" value="1"/>          ← 专业技能等级要求
        ├── <int name="itemNum" value="1"/>                ← 产出数量
        ├── <int name="tuc" value="0"/>                    ← 产出品升级槽
        ├── <int name="meso" value="1000"/>                ← 制作费用
        ├── recipe/
        │   ├── <int name="0" value="itemId"/>             ← 材料1 ID
        │   ├── <int name="1" value="count"/>              ← 材料1 数量
        │   └── ...
        └── randomReward/
            ├── <int name="0" value="itemId"/>             ← 可能产出1
            ├── <int name="1" value="prob"/>               ← 产出1 概率
            └── ...
```

### 5.4 其他重要配置

**ForbiddenName.img（角色名黑名单）：**
- 管理员类：admin, administrator, gm, gamemaster, helper
- 官方类：Nexon, Wizet, MapleStory, and their leet变体
- 服务器类：Scania, Bera, Broa, etc.
- 脏话类：fuck, shit, bitch, asshole, etc.
- 其他游戏：Warcraft, Lineage, Ragnarok, etc.

**BlockReason.img（封号原因）：**
HACK, BOT, AD, HARASS, CURSE, SCAM, MISCONDUCT, SELL, ICASH, TEMP, GM, IPROGRAM, MEGAPHONE

**OXQuiz.img（答题格式）：**
```
<imgdir name="1">                          ← 题目编号
  <string name="q" value="问题内容"/>
  <int name="a" value="0"/>                ← 0=假(X) 1=真(O)
  <string name="d" value="答案解析"/>
</imgdir>
```

---

## 6. Item.wz — 物品数据

**文件数：** 155  
**子目录：** 6 个（Cash, Consume, Etc, Install, Pet, Special）

### 6.1 子目录总览

| 子目录 | 文件数 | ID范围 | 说明 |
|--------|--------|--------|------|
| **Cash/** | 49 | 0501~0599 | 现金商城物品 |
| **Consume/** | 28 | 0200~0245 | 消耗品 |
| **Etc/** | 18 | 0400~0431 | 其他物品（材料/矿石/代币） |
| **Install/** | 2 | 0301, 0399 | 可放置物品（椅子/奖励） |
| **Pet/** | 56 | 5000000~5000102 | 宠物数据 |
| **Special/** | 4 | 0900, 0910~0911 | 特殊物品（勋章/套装/扩容） |

### 6.2 Consume/（消耗品）完整 ID 表

| IMG | 物品数 | 中文分类 | 关键特征属性 |
|-----|--------|----------|-------------|
| **0200** | 55 | HP 药水（红/橙/白） | `spec.hp` — 固定值恢复 |
| **0201** | 17 | HP 食物（苹果/烤肉/蛋糕等） | `spec.hp` — 食物恢复 |
| **0202** | 505 | 混合药水（HP+MP，含清晨/黄昏之露） | `spec.hp` 或 `spec.mp` 或 `spec.hpR`/`spec.mpR`（百分比） |
| **0203** | 25 | 传送卷轴（各村回城卷/魔法种子） | `spec.moveTo` — 目标地图ID |
| **0204** | 754 | 装备强化卷轴（10%/60%/100% 卷） | `incPAD/MAD/PDD/MDD` + `success` 成功率 |
| **0205** | 9 | 状态治愈药水（万能疗伤药） | `spec.poison`/`darkness`/`weakness`/`curse`/`seal` |
| **0206** | 12 | 子弹（火枪弹药） | `bullet` 动画节点 + `incPAD` |
| **0207** | 17 | 飞镖（海星镖/雪花镖/齿轮镖等） | `bullet` + `incPAD` + `reqLevel` |
| **0210** | 283 | 怪物召唤袋 | `mob` 节点（怪物ID + 概率） |
| **0212** | 2 | 魔法粉末（制造/合成材料） | `spec.inc` + 关联装备ID |
| **0216** | 1 | 限时特殊物品 | `timeLimited=1` |
| **0219** | 1 | 特殊消耗品 | 仅价格 |
| **0221** | 30 | 变身药水 | `spec.morph=1` + `spec.time` |
| **0224** | 4 | 活动限定物品 | `tradeBlock` + `notSale` + `only` |
| **0226** | 1 | 疲劳恢复剂 | `spec.incFatigue`（负值=恢复） |
| **0227** | 13 | 怪物放置道具 | `mob` + `create` + 坐标范围 |
| **0228** | 26 | 技能书（4转，70%/50%） | `skill` ID + `masterLevel` + `success` |
| **0229** | 139 | 技能书（4转，带前置技能要求） | 同上 + `reqSkillLevel` |
| **0231** | 1 | 限时活动物品 | `timeLimited` + `tradeBlock` + `only` |
| **0232** | 1 | 限时活动物品 | 同上 |
| **0233** | 9 | 胶囊/高级子弹（有等级要求） | `bullet` + `incPAD` + `reqLevel` |
| **0234** | 1 | 空壳物品（仅图标） | 无 spec 节点 |
| **0236** | 3 | 幽灵药水（透明/隐身） | `spec.ghost=1` + `spec.time` |
| **0237** | 13 | 经验值道具（经验券） | `spec.exp` + `maxLevel` |
| **0238** | 343 | 怪物卡（怪物图鉴收集） | `monsterBook=1` + `mob` |
| **0243** | 27 | 任务脚本物品（双击触发NPC对话） | `spec.script` + `spec.npc` + `quest=1` |
| **0244** | 1 | 组队任务物品 | `pquest=1` + `randomMoveInFieldSet` |
| **0245** | 1 | 经验加成道具（双倍经验Buff） | `spec.expBuff=200` + `spec.time` |

### 6.3 Consume 属性详解

| 属性 | 类型 | 出现在 | 说明 |
|------|------|--------|------|
| `spec/hp` | int | 0200~0202 | 固定HP恢复量 |
| `spec/mp` | int | 0200~0202 | 固定MP恢复量 |
| `spec/hpR` | int | 0200~0202 | 百分比HP恢复 |
| `spec/mpR` | int | 0200~0202 | 百分比MP恢复 |
| `spec/moveTo` | int | 0203 | 传送目标地图ID |
| `spec/poison/darkness/weakness/curse/seal` | int | 0205 | 解除对应状态 |
| `spec/morph` | int | 0221 | 变身ID |
| `spec/ghost` | int | 0236 | 1=幽灵化（透明） |
| `spec/time` | int | 0221/0236/0245 | Buff持续时间(ms) |
| `spec/exp` | int | 0237 | 经验值获得量 |
| `spec/expBuff` | int | 0245 | 经验加成%（200=双倍） |
| `spec/incFatigue` | int | 0226 | 疲劳值变化（负数=恢复） |
| `spec/script` | string | 0243 | 使用后执行的JS脚本名 |
| `spec/npc` | int | 0243 | 关联NPC ID |
| `incPAD/MAD/PDD/MDD/STR/DEX/INT/LUK` | int | 0204 | 卷轴属性加成 |
| `success` | int | 0204/0228/0229 | 成功率(%) |
| `masterLevel` | int | 0228/0229 | 技能精通等级上限 |
| `reqSkillLevel` | int | 0229 | 前置技能等级要求 |
| `bullet` 节点 | imgdir | 0206/0207/0233 | 弹道动画精灵 |
| `mob` 节点 | imgdir | 0210/0227/0238 | 怪物ID + 概率/坐标 |
| `monsterBook` | int | 0238 | 1=怪物图鉴卡 |
| `slotMax` | int | 大多数 | 每格最大堆叠数 |
| `reqLevel` | int | 0207/0233 | 使用等级要求 |

### 6.4 Etc/（其他物品）ID 表

| IMG | 说明 | 特点 |
|-----|------|------|
| 0400 | 加工材料（大文件） | 各种基础材料 |
| 0401 | 弹药（箭矢） | `price` 100~8000 |
| 0402 | 矿石/宝石 | `price` 150~10000 |
| 0403 | 加工后金属/珠宝 | 怪物掉落物 |
| 0405 | 限时活动物品 | `timeLimited=1` |
| 0408 | 廉价通用材料 | `price=1` |
| 0413 | 锻造产物 | 钢铁/珠宝 |
| 0416 | 宠物手册 | 含 `book` 节点（宠物指令说明） |
| 0421 | 任务限定掉落 | `only=1`, `tradeBlock=1` |
| 0428 | 简单图标物品 | 无特殊属性 |
| 0429 | 带动画效果物品 | 含 `effect/stand1` 帧动画 |
| 0430 | 限时活动物品 | `timeLimited=1` |
| 0431 | 代币/硬币 | `slotMax=1000` |

### 6.5 Install/（可放置物品）

| IMG | 说明 | 关键属性 |
|-----|------|----------|
| 0301 | 椅子/沙发（恢复类） | `recoveryHP`, `recoveryMP`, `reqLevel`, `effect`动画, `distanceX/Y/direction`（可定向） |
| 0399 | 奖励兑换物品 | `iconReward`多帧动画, `price=50~300`, `slotMax=100` |

### 6.6 Pet/（宠物）属性结构

```
{宠物ID}.img/
├── info/
│   ├── <int name="hungry" value="2"/>            ← 饥饿速度(1~3)
│   ├── <int name="life" value="90"/>             ← 寿命(天，0=永久)
│   ├── <int name="cash" value="1"/>              ← 现金标记
│   ├── <int name="permanent" value="1"/>         ← 永久宠物标记
│   ├── <int name="chatBalloon" value="25"/>      ← 聊天气泡样式ID
│   └── <int name="nameTag" value="27"/>          ← 名称标签样式ID
├── interact/  ← 28个命令槽(c1~c28)
│   └── c1/
│       ├── <string name="command" value="sit"/>  ← 命令词
│       ├── <int name="inc" value="1"/>           ← 亲密度增加
│       ├── <int name="prob" value="100"/>        ← 成功概率(%)
│       └── l0/l1 → success/fail 动画
├── food/      ← 4个食物反应（按宠物等级分组）
├── slang/     ← 4个宠物话语（按等级分组）
└── (动画): stand0~1/hungry/move/jump/rest0/chat/angry/hang/rise/dung/stretch/prone/alert/cry/fly/nap/tedious
```

### 6.7 Special/（特殊物品）

| IMG | 说明 | 关键属性 |
|-----|------|----------|
| 0900 | 勋章/提醒物品 | 仅 `iconRaw`（4帧动画） |
| 0910 | 商城套装礼包(~900个) | `name` 套装名(中英文混合)，UOL链接复用图标 |
| 0911 | 背包扩容券(5个) | `delta=8`（每次+8格），分仓库/装备/消耗/安装/其他 |
| MaplePoint | 抵用券面额(30个) | 100~100000面额 |

---

## 7. Map.wz — 地图系统

**文件数：** 5,725  
**子目录：** Back, Map(Map0~Map9), Obj, Tile, WorldMap

### 7.1 子目录总览

| 子目录 | 文件数 | 说明 |
|--------|--------|------|
| **Back/** | 111 | 背景图片（按区域/主题命名） |
| **Obj/** | 111 | 地图物件精灵（acc1-12, tree, lava, crystal等） |
| **Tile/** | 108 | 地图图块集（basicTile, woodTile, iceTile等） |
| **WorldMap/** | 28 | 世界地图展示（WorldMap000~230） |
| **Map/Map0** | 43 | 登录/新手地图 |
| **Map/Map1** | 882 | 维多利亚岛 |
| **Map/Map2** | 1,063 | 艾尔纳斯/水下世界 |
| **Map/Map3** | 198 | 路德斯湖 |
| **Map/Map5** | 57 | 武陵/尼哈沙漠 |
| **Map/Map6** | 204 | 神木村 |
| **Map/Map7** | 103 | 马斯特利亚 |
| **Map/Map8** | 62 | 时间神殿 |
| **Map/Map9** | 2,751 | 活动/特殊/签到地图 |

### 7.2 Back/ 背景文件（按区域）

| 文件 | 区域 |
|------|------|
| `Amoria.img` | 阿莫利亚（婚礼） |
| `Christmas.img` | 圣诞节 |
| `EventCN.img` | 中国活动 |
| `Rien.img` | 瑞恩（Aran出生地） |
| `ShanghaiCN.img` | 上海 |
| `ShaolinCN.img` | 少林寺 |
| `aquaRoad.img` | 水下世界 |
| `ariantCastle.img` | 阿里安特城堡 |
| `darkCave.img` | 黑暗洞穴 |
| `desert.img` | 沙漠 |
| `dragonRoad.img` | 龙之路 |
| `ereb.img` | 圣地（Cygnus出生地） |
| `iceCave.img` | 冰洞 |
| `lava.img` | 熔岩 |
| `nightTree.img` | 夜树 |
| `pirate.img` | 海盗 |
| `ruins.img` | 遗迹 |
| `shipyard.img` | 造船厂 |
| `snowMountain.img` | 雪山 |
| `swamp.img` | 沼泽 |
| `treeMap.img` | 树林 |
| `undead.img` | 亡灵 |
| `winter*.img` | 冬季主题 |
| `yacht.img` | 游艇 |

### 7.3 地图文件完整结构

以 `100000000.img.xml`（赫里西安）为例：

```
{地图ID}.img/
├── info/                                        ← 地图基础信息
│   ├── <int name="version" value="10"/>          ← 地图版本
│   ├── <int name="town" value="1"/>              ← 1=城镇地图
│   ├── <string name="bgm" value="Bgm00/FloralLife"/>  ← 背景音乐
│   ├── <int name="returnMap" value="100000000"/>  ← 死亡回城地图
│   ├── <int name="forcedReturn" value="999999999"/>  ← 强制返回地图
│   ├── <string name="mapMark" value="Henesys"/>   ← 地图标记文字
│   ├── <float name="mobRate" value="1.0"/>        ← 怪物刷新率倍率
│   ├── <int name="swim" value="0"/>               ← 1=游泳地图
│   ├── <int name="fly" value="0"/>                ← 1=飞行地图
│   ├── <int name="moveLimit" value="0"/>           ← 移动限制
│   ├── <int name="fieldLimit" value="0"/>          ← 场地限制标志位
│   ├── <string name="onUserEnter" value="script"/> ← 进入时执行的脚本
│   └── <int name="cloud" value="0"/>              ← 1=有云雾
├── back/                                        ← 背景图层
│   ├── 0/  ← 背景层0
│   │   ├── <int name="x" value="0"/>
│   │   ├── <int name="y" value="0"/>
│   │   ├── <int name="rx" value="-5"/>           ← X轴移动速率
│   │   ├── <int name="ry" value="0"/>            ← Y轴移动速率
│   │   ├── <int name="a" value="255"/>           ← 透明度(0=全透明,255=不透明)
│   │   ├── <string name="bS" value="Back/grassySoil.img/back/0"/>
│   │   └── ...
│   └── 1/ ... 2/ ...                             ← 更多层
├── life/                                        ← NPC和怪物生成
│   ├── <imgdir name="0">
│   │   ├── <string name="type" value="n"/>       ← "n"=NPC "m"=怪物
│   │   ├── <string name="id" value="1012000"/>
│   │   ├── <int name="x" value="332"/>
│   │   ├── <int name="y" value="-28"/>
│   │   ├── <int name="fh" value="14"/>           ← 立足点ID
│   │   ├── <int name="cy" value="-185"/>         ← 碰撞体Y偏移
│   │   ├── <int name="rx0" value="-271"/>        ← 移动范围左
│   │   ├── <int name="rx1" value="271"/>         ← 移动范围右
│   │   ├── <int name="mobTime" value="0"/>       ← 刷新时间(秒)
│   │   ├── <int name="hide" value="0"/>          ← 隐藏标记
│   │   └── <int name="f" value="0"/>             ← 朝向(0=右 1=左)
│   └── ...
├── foothold/                                    ← 立足点（物理碰撞）
│   ├── 0/  ← 层0（底层）
│   │   ├── <imgdir name="0">
│   │   │   ├── <int name="x1" value="-812"/>     ← 起点X
│   │   │   ├── <int name="y1" value="50"/>       ← 起点Y
│   │   │   ├── <int name="x2" value="-811"/>     ← 终点X
│   │   │   ├── <int name="y2" value="50"/>       ← 终点Y
│   │   │   ├── <int name="prev" value="0"/>      ← 前一段ID(0=起始)
│   │   │   └── <int name="next" value="1"/>      ← 后一段ID
│   │   └── ...
│   ├── 1/  ← 层1（中层）
│   └── 2/  ← 层2（顶层）
├── ladderRope/                                  ← 梯子和绳索
│   ├── <imgdir name="0">
│   │   ├── <int name="l" value="0"/>             ← 层
│   │   ├── <int name="uf" value="0"/>            ← 方向标记
│   │   ├── <int name="x" value="-598"/>
│   │   ├── <int name="y1" value="24"/>           ← 顶端Y
│   │   └── <int name="y2" value="-239"/>          ← 底端Y
│   └── ...
├── seat/                                        ← 休息座位
│   └── <vector name="0" x="-78" y="-58"/>
├── miniMap/                                     ← 小地图
│   ├── <canvas name="miniMap" .../>
│   ├── <int name="width" value="794"/>
│   ├── <int name="height" value="422"/>
│   ├── <int name="centerX" value="0"/>
│   ├── <int name="centerY" value="-3"/>
│   └── <int name="mag" value="2"/>              ← 缩放倍数
└── portal/                                      ← 传送门
    ├── <imgdir name="0">
    │   ├── <string name="pn" value="sp"/>         ← 传送门名称
    │   ├── <int name="pt" value="0"/>            ← 类型(0=出生点,1=隐藏,2=常规,3=触摸,7=脚本)
    │   ├── <int name="x" value="-199"/>
    │   ├── <int name="y" value="18"/>
    │   ├── <int name="tm" value="100000001"/>    ← 目标地图ID
    │   └── <string name="tn" value="tp1"/>       ← 目标传送门名称
    └── ...
```

### 7.4 传送门类型（pt）枚举

| pt值 | 类型 | 说明 |
|------|------|------|
| 0 | 出生点 | 玩家进入地图时的初始位置 |
| 1 | 隐藏 | 不可见的传送点 |
| 2 | 常规 | 按↑键进入的传送门 |
| 3 | 触摸 | 碰到就传送 |
| 4 | 不可见触发 | 触碰触发 |
| 5 | 计时 | 定时触发 |
| 6 | 不可见计时 | 定时+不可见 |
| 7 | 脚本 | 由JS脚本控制 |

### 7.5 场地限制标志（fieldLimit）位掩码

| 位 | 含义 |
|----|------|
| 0x01 | 禁止跳跃 |
| 0x02 | 禁止技能 |
| 0x04 | 禁止召唤 |
| 0x08 | 禁止使用消耗品 |
| 0x10 | 禁止丢物品 |
| 0x20 | 禁止传送 |
| 0x40 | 禁止变身 |
| 0x80 | 禁止骑宠 |

---

## 8. Mob.wz — 怪物数据

**文件数：** 2,058 + 子目录 `QuestCountGroup/`（4文件）  
**功能：** 所有怪物的属性、动画、掉落生成点数据

### 8.1 怪物ID命名规则

7位数字：`0XYYZZZZ`

| ID范围 | 区域 | 等级范围 |
|--------|------|----------|
| 0100100~0199999 | 新手/维多利亚 | Lv1~30 |
| 100xxxx~1999999 | 维多利亚深处 | Lv10~50 |
| 200xxxx~2999999 | 艾尔纳斯/水下 | Lv30~70 |
| 300xxxx~3999999 | 路德斯湖 | Lv50~90 |
| 400xxxx~4999999 | 玩具城 | Lv60~100 |
| 500xxxx~5999999 | 武陵/神木 | Lv70~120 |
| 600xxxx~6999999 | 马斯特利亚 | Lv80~130 |
| 700xxxx~7999999 | (预留) | - |
| 800xxxx~8999999 | BOSS系列 | BOSS |
| 900xxxx~9999999 | 活动/特殊BOSS | 特殊 |

### 8.2 怪物节点完整结构

```
{怪物ID}.img/
├── info/                                        ← 基础属性
│   ├── <int name="bodyAttack" value="1"/>        ← 碰撞伤害
│   ├── <int name="level" value="1"/>             ← 等级
│   ├── <int name="maxHP" value="8"/>             ← 最大HP
│   ├── <int name="maxMP" value="0"/>             ← 最大MP
│   ├── <int name="speed" value="-65"/>           ← 移动速度(负数=慢)
│   ├── <int name="PADamage" value="12"/>         ← 物理攻击力
│   ├── <int name="PDDamage" value="0"/>          ← 物理防御力
│   ├── <int name="MADamage" value="0"/>          ← 魔法攻击力
│   ├── <int name="MDDamage" value="0"/>          ← 魔法防御力
│   ├── <int name="acc" value="20"/>              ← 命中
│   ├── <int name="eva" value="0"/>               ← 回避
│   ├── <int name="exp" value="3"/>               ← 经验值
│   ├── <int name="undead" value="0"/>            ← 1=不死系
│   ├── <int name="pushed" value="1"/>            ← 可被击退距离
│   ├── <int name="fs" value="10"/>               ← 击退力系数
│   ├── <int name="summonType" value="1"/>        ← 召唤类型
│   ├── <int name="mobType" value="0"/>           ← 怪物类型
│   ├── <int name="boss" value="0"/>              ← 1=BOSS（免疫击退等）
│   ├── <int name="firstAttack" value="0"/>       ← 先手攻击
│   ├── <int name="publicReward" value="1"/>      ← 公开奖励
│   ├── <int name="explosiveReward" value="1"/>   ← 爆炸奖励
│   ├── <int name="hpTagColor" value="0"/>        ← HP条颜色编号
│   ├── <int name="hpTagBgcolor" value="0"/>      ← HP条背景色编号
│   └── <int name="buff" value="0"/>              ← 自带Buff
├── attackN/                                     ← 攻击技能(N=1,2,3...)
│   ├── info/
│   │   ├── <int name="type" value="0"/>           ← 攻击类型
│   │   ├── <int name="attackAfter" value="0"/>    ← 攻击后摇(ms)
│   │   ├── <int name="conMP" value="0"/>          ← MP消耗
│   │   ├── <int name="range" value="100"/>        ← 攻击范围
│   │   ├── <int name="knockback" value="100"/>    ← 击退力度
│   │   ├── <int name="deadlyAttack" value="0"/>   ← 致命攻击概率
│   │   └── <int name="disease" value="0"/>        ← 附加异常状态
│   └── (攻击动画帧)
├── stand/                                       ← 站立帧
├── move/                                        ← 移动帧
├── hit1/                                        ← 受击帧
├── die1/                                        ← 死亡帧（含a0→a1淡出）
├── jump/                                        ← 跳跃帧
├── fly/                                         ← 飞行帧
└── (每帧):
    ├── <vector name="origin" x="..." y="..."/>   ← 锚点
    ├── <vector name="lt" x="..." y="..."/>       ← 碰撞盒左上
    ├── <vector name="rb" x="..." y="..."/>       ← 碰撞盒右下
    ├── <vector name="head" x="..." y="..."/>     ← 头部位置
    └── <int name="delay" value="..."/>           ← 帧延迟(ms)
```

### 8.3 mobType 与 summonType 枚举

**mobType（怪物类型）：**
| 值 | 类型 | 说明 |
|----|------|------|
| 0 | 普通 | 最常见的怪物 |
| 1 | 不死 | 对治疗技能敏感 |
| 2 | 飞行 | 空中移动 |
| 3 | BOSS | 免疫击退/控制 |

**summonType（召唤类型）：**
| 值 | 类型 |
|----|------|
| 0 | 不召唤 |
| 1 | 召唤小怪 |
| 2 | 召唤同族 |

### 8.4 QuestCountGroup（任务怪物组）

```
QuestCountGroup/
├── 9101000.img  →  info/ { <int name="0" value="1110100"/>, ...}  ← 怪物ID列表
├── 9101001.img
├── 9101002.img
└── 9101003.img
```

记录特定任务物品掉落的怪物组。

---

## 9. Morph.wz — 变身动作帧

**文件数：** 42  
**功能：** 角色变身/骑乘时使用的替代动画帧

### 9.1 文件清单

| ID范围 | 说明 |
|--------|------|
| 0001~0045 | 基础变身（香肠/雪人/南瓜/装饰性变身等） |
| 1000~1103 | 现金/特殊变身（含完整战斗动作） |

### 9.2 属性结构

```
{变身ID}.img/
├── info/
│   ├── <int name="speed" value="80"/>            ← 移动速度(%)
│   ├── <int name="jump" value="100"/>            ← 跳跃力(%)
│   ├── <float name="fs" value="10.0"/>           ← 摩擦系数
│   ├── <float name="swim" value="100.0"/>        ← 游泳速度
│   ├── <int name="fatigue" value="0"/>           ← 疲劳累积速度(骑宠)
│   ├── <int name="morphEffect" value="0"/>       ← 变身特效
│   └── <int name="superman" value="0"/>          ← 超人模式(可飞行)
├── (基础动画): walk/stand/jump/prone/sit/ladder/rope/fly
└── (战斗动作 - 高级变身): stabO1/stabO2/shoot1/shoot2/swingO1/swingO2/
                           somersault/doublefire/backspin/doubleupper/
                           screw/dragonstrike/fist/straight/wave
```

每帧含 `origin`, `lt`, `rb`, `head`, `delay`。

---

## 10. Npc.wz — NPC 外观与对话

**文件数：** 7,198  
**功能：** NPC 的精灵图、对话数据和关联脚本

### 10.1 NPC ID 命名规则

7位数字：`XXYYYYYY`

| ID范围 | 区域 | 说明 |
|--------|------|------|
| 000xxxx | 系统/功能NPC | 仓库管理员、传送员 |
| 001xxxx | 新手村 | 枫叶岛NPC |
| 100xxxx~106xxxx | 维多利亚岛 | 赫里西安/勇士/废弃/林中之城等 |
| 200xxxx~213xxxx | 高级区域 | 艾尔纳斯/水下/神木/时间神殿 |
| 900xxxx~990xxxx | 活动/特殊 | 活动NPC、商城NPC |
| 997xxxx | 开发者 | Ronan等开发者NPC |

### 10.2 NPC 节点结构

```
{NPC_ID}.img/
├── info/
│   ├── <imgdir name="speak">                     ← 对话内容引用
│   │   ├── <string name="0" value="n0"/>          ← 对话行引用名
│   │   ├── <string name="1" value="n1"/>
│   │   └── ...
│   └── <imgdir name="script">                    ← 脚本引用（可选）
│       └── <string name="0" value="rithTeleport"/>  ← 执行脚本名
├── stand/                                       ← 站立帧
│   └── <canvas name="0" width="52" height="68">
│       ├── <vector name="origin" x="22" y="68"/>
│       └── <int name="z" value="0"/>
├── move/                                        ← 移动帧（可选，4帧）
├── fly/                                         ← 飞行帧（可选）
├── finger/                                      ← 手指动画（可选）
├── wink/                                        ← 眨眼动画（可选）
└── say/                                         ← 说话动画（可选）
```

---

## 11. Quest.wz — 任务系统

**文件数：** 6  
**功能：** 完整的任务数据，包括接取条件、完成动作、奖励、对话

### 11.1 文件清单

| 文件 | 说明 | 结构 |
|------|------|------|
| `Act.img.xml` | **任务完成动作** | 任务ID → 奖励物品/经验/下一任务 |
| `Check.img.xml` | **任务接取/完成条件** | 任务ID → NPC/等级/物品/职业要求 |
| `QuestInfo.img.xml` | **任务元数据** | 任务ID → 名称/描述/区域/排序 |
| `Say.img.xml` | **任务对话树** | 任务ID → NPC对话/选项/yes-no分支 |
| `PQuest.img.xml` | **组队任务数据** | 含排名标准 |
| `Exclusive.img.xml` | **勋章任务独占** | 任务ID列表 |

### 11.2 属性详解

**Act.img（任务完成动作）：**
```
{任务ID}/
├── {阶段号}/
│   ├── <int name="nextQuest" value="1001"/>      ← 下一任务ID
│   ├── <int name="exp" value="10000"/>           ← 经验奖励
│   ├── <int name="npc" value="2100"/>            ← 完成NPC
│   ├── <int name="lvmin" value="10"/>            ← 最低等级
│   └── item/
│       └── {序号}/
│           ├── <int name="id" value="4031701"/>   ← 物品ID
│           └── <int name="count" value="-5"/>     ← 数量(负数=扣除)
```

**Check.img（任务条件）：**
```
{任务ID}/
├── 0/  ← 开始条件
│   ├── <int name="npc" value="2101"/>            ← 接任务NPC
│   ├── <int name="lvmin" value="10"/>            ← 最低等级
│   ├── <int name="lvmax" value="200"/>           ← 最高等级
│   ├── job/ { <int name="0" value="0"/> }        ← 职业要求(0=全部)
│   └── item/ { <int name="id"/> <int name="count"/> }  ← 物品需求
└── 1/  ← 完成条件
```

**QuestInfo.img（任务信息）：**
```
{任务ID}/
├── <string name="name" value="任务名称（英文）"/>
├── <string name="desc" value="任务描述"/>
├── <int name="area" value="10"/>                 ← 所属区域
├── <int name="order" value="1"/>                 ← 排序顺序
├── <int name="autoStart" value="0"/>             ← 自动接取
└── <int name="autoPreComplete" value="0"/>       ← 自动完成
```

---

## 12. Reactor.wz — 地图反应堆

**文件数：** 421  
**功能：** 地图上可交互的物体（箱子/药草/矿石/机关等）

### 12.1 节点结构

```
{反应堆ID}.img/
├── info/
│   └── <string name="info" value="韩文描述"/>
├── <string name="action" value="mBoxItem0"/>     ← 动作类型字符串
├── 0/  ← 状态0（初始外观）
│   ├── event/
│   │   └── 0/
│   │       ├── <int name="type" value="0"/>       ← 事件类型
│   │       └── <int name="state" value="1"/>      ← 切换到的状态
│   └── hit/  ← 受击动画帧
│       ├── <canvas name="0" width="48" height="34">
│       │   ├── <vector name="origin" x="24" y="17"/>
│       │   └── <int name="delay" value="150"/>
│       └── ...
├── 1/  ← 状态1（中间状态）
├── 2/  ← 状态2（最终状态/掉落）
└── (更多状态)...
```

### 12.2 动作类型字符串

| action | 说明 |
|--------|------|
| `mBoxItem0` | 普通箱子（打开掉落物品） |
| `oBoxItem0` | 特殊箱子 |
| `s4hitmanMap0` | 受击触发 |

---

## 13. Skill.wz — 技能数据

**文件数：** 76  
**功能：** 所有职业技能的属性、动画、特效

### 13.1 职业文件对照表

| 文件 | 职业 | 说明 |
|------|------|------|
| `000.img` | 新手 | Beginner |
| `100.img` | 剑客 | Swordsman |
| `110.img` | 勇士 | Fighter |
| `111.img` | 十字军 | Crusader |
| `112.img` | 英雄 | Hero |
| `120.img` | 准骑士 | Page |
| `121.img` | 骑士 | White Knight |
| `122.img` | 圣骑士 | Paladin |
| `130.img` | 枪战士 | Spearman |
| `131.img` | 龙骑士 | Dragon Knight |
| `132.img` | 黑骑士 | Dark Knight |
| `200.img` | 魔法师 | Magician |
| `210.img` | 火毒法师 | FP Wizard |
| `211.img` | 火毒魔导士 | FP Mage |
| `212.img` | 火毒大魔导士 | FP ArchMage |
| `220.img` | 冰雷法师 | IL Wizard |
| `221.img` | 冰雷魔导士 | IL Mage |
| `222.img` | 冰雷大魔导士 | IL ArchMage |
| `230.img` | 牧师 | Cleric |
| `231.img` | 祭司 | Priest |
| `232.img` | 主教 | Bishop |
| `300.img` | 弓箭手 | Bowman |
| `310.img` | 猎人 | Hunter |
| `311.img` | 射手 | Ranger |
| `312.img` | 神射手 | Bowmaster |
| `320.img` | 弩弓手 | Crossbowman |
| `321.img` | 游侠 | Sniper |
| `322.img` | 箭神 | Marksman |
| `400.img` | 飞侠 | Rogue |
| `410.img` | 刺客 | Assassin |
| `411.img` | 隐士 | Hermit |
| `412.img` | 暗杀者 | Night Lord |
| `420.img` | 侠客 | Bandit |
| `421.img` | 独行客 | Chief Bandit |
| `422.img` | 隐忍 | Shadower |
| `500.img` | 海盗 | Pirate |
| `510.img` | 拳击手 | Infighter |
| `511.img` | 冲锋队长 | Buccaneer |
| `512.img` | 毒蛇 | Viper |
| `520.img` | 枪手 | Gunslinger |
| `521.img` | 突击手 | Outlaw |
| `522.img` | 船长 | Corsair |
| `800.img` | 管理员 | Super GM |
| `900~910.img` | 贵族 | Noblesse |
| `1000~1112.img` | 龙神 | Evan |
| `1200~1212.img` | 战神 | Aran |
| `1300~1312.img` | 反抗者 | Resistance |
| `1400~1412.img` | 双弩 | Mercedes |
| `1500~1512.img` | 幻影 | Phantom |
| `2000~2112.img` | 恶魔 | Demon |
| `BFSkill.img` | BOSS技能 | Boss Final Attack |
| `ItemSkill.img` | 物品技能 | 卷轴/药水触发的技能 |
| `MCGuardian.img` | 怪物嘉年华 | 守护者技能 |
| `MCSkill.img` | 怪物嘉年华 | 比赛技能 |
| `MobSkill.img` | 怪物技能 | 怪物使用的技能 |

### 13.2 技能 ID 编码规则

6~7位数字：`JOB` + `SKILL_NUMBER`

| 职业码 | 职业 |
|--------|------|
| 000, 1000 | 新手 |
| 100, 110, 111, 112 | 英雄系 |
| 120, 121, 122 | 圣骑系 |
| 130, 131, 132 | 黑骑系 |
| 200, 210, 211, 212 | 火毒系 |
| 220, 221, 222 | 冰雷系 |
| 230, 231, 232 | 主教系 |
| 300, 310, 311, 312 | 神射系 |
| 320, 321, 322 | 箭神系 |
| 400, 410, 411, 412 | 暗杀者系 |
| 420, 421, 422 | 隐忍系 |
| 500, 510, 511, 512 | 冲锋队长系 |
| 520, 521, 522 | 船长系 |

### 13.3 单个技能节点完整结构

```
{技能ID}/
├── <canvas name="icon">                          ← 技能图标
├── <canvas name="iconMouseOver">                 ← 悬停图标
├── <canvas name="iconDisabled">                  ← 不可用图标
├── level/
│   └── {技能等级}/
│       ├── <string name="hs" value="h1"/>         ← 效果描述名
│       ├── <int name="mpCon" value="4"/>          ← MP消耗
│       ├── <int name="hpCon" value="0"/>          ← HP消耗
│       ├── <int name="damage" value="165"/>       ← 伤害%
│       ├── <int name="x" value="1"/>              ← 通用参数
│       ├── <int name="y" value="0"/>              ← 通用参数
│       ├── <int name="time" value="300"/>         ← 持续时间(秒)
│       ├── <int name="cooltime" value="0"/>       ← 冷却时间(秒)
│       ├── <int name="mobCount" value="6"/>       ← 最大目标数
│       ├── <int name="bulletCount" value="1"/>    ← 弹道数
│       ├── <int name="mastery" value="60"/>       ← 熟练度%
│       ├── <int name="fixdamage" value="0"/>      ← 固定伤害
│       ├── <int name="prop" value="50"/>          ← 触发概率%
│       ├── <int name="subProp" value="0"/>        ← 子概率
│       ├── <int name="range" value="200"/>        ← 攻击范围
│       ├── <int name="knockback" value="100"/>    ← 击退距离
│       ├── <int name="dot" value="0"/>            ← 持续伤害
│       ├── <int name="dotTime" value="0"/>        ← 持续伤害间隔
│       ├── <int name="dotInterval" value="0"/>    ← DOT间隔
│       ├── <int name="dotSuperpos" value="0"/>    ← DOT叠加
│       ├── <int name="hpCon" value="0"/>          ← HP消耗/转换
│       ├── <int name="morph" value="0"/>          ← 变身ID
│       ├── <int name="itemCon" value="0"/>        ← 消耗物品ID
│       ├── <int name="itemConNo" value="0"/>      ← 消耗物品数量
│       ├── <vector name="lt" x=".." y=".."/>      ← 攻击矩形左上
│       ├── <vector name="rb" x=".." y=".."/>      ← 攻击矩形右下
│       ├── <int name="cr" value="0"/>             ← 暴击率
│       └── <int name="er" value="0"/>             ← 异常状态抗性
├── req/                                          ← 前置技能
│   ├── <int name="{skillId}" value="{level}"/>
│   └── ...
├── common/                                       ← 多级共用属性
│   └── ...
├── effect/                                       ← 施法特效
├── hit/                                          ← 命中特效
├── ball/                                         ← 弹道特效
├── action/                                       ← 角色动作
├── finalAttack/                                  ← 终极攻击链接
├── CharLevel/                                    ← 按角色等级变化的效果
├── <int name="invisible" value="0"/>             ← 1=隐藏技能
├── <int name="disable" value="0"/>               ← 1=禁用
├── <int name="timeLimited" value="0"/>           ← 1=限时技能
├── <int name="weapon" value="0"/>                ← 武器要求位掩码
├── <int name="masterLevel" value="10"/>          ← 可加最高等级
└── <int name="skillType" value="0"/>             ← 技能类型(0=主动 1=被动)
```

---

## 14. Sound.wz — 音频资源

**文件数：** 45  
**功能：** BGM 和音效的引用（不含实际音频文件，仅存储路径引用）

### 14.1 BGM 文件（22个）

| 文件 | 说明 |
|------|------|
| `Bgm00.img` ~ `Bgm21.img` | 各区域BGM（`<sound name="Bgm00/FloralLife"/>` 格式） |
| `BgmCN.img` / `BgmEvent.img` / `BgmGL.img` / `BgmJp.img` / `BgmMY.img` / `BgmSG.img` / `BgmUI.img` | 特定区域/语言/UI的BGM |

### 14.2 音效文件（23个）

| 文件 | 说明 |
|------|------|
| `CashEffect.img` | 现金物品音效 |
| `ConsumeEffect.img` | 消耗品使用音效 |
| `Field.img` | 场景环境音效 |
| `Game.img` | 游戏通用音效（升级/死亡等） |
| `Item.img` | 物品掉落/拾取音效 |
| `Jukebox.img` | 点唱机音乐 |
| `MiniGame.img` | 小游戏音效(OMOK等) |
| `Mob.img` | 怪物音效(死亡/受击等) |
| `Object.img` | 地图物件音效 |
| `Pet.img` | 宠物音效 |
| `Radio.img` | 电台音效 |
| `Reactor.img` | 反应堆触发音效 |
| `Skill.img` | 技能音效 |
| `Summon.img` | 召唤音效 |
| `UI.img` | UI点击/打开音效 |
| `Weapon.img` | 武器挥动音效 |

---

## 15. String.wz — 本地化字符串

**文件数：** 20  
**功能：** 所有游戏实体的名称、描述、提示文本

### 15.1 文件清单

| 文件 | 内容 | 结构 |
|------|------|------|
| `Cash.img` | 现金物品名 | `{ID} → name, desc` |
| `Consume.img` | 消耗品名 | `{ID} → name, desc` |
| `Eqp.img` | 装备名 | `Eqp/{类别}/{ID} → name, desc` |
| `Etc.img` | 其他物品名 | `{ID} → name, desc` |
| `Ins.img` | 安装物品名 | `{ID} → name, desc` |
| `Map.img` | 地图/区域名 | `{ID} → name, streetName` |
| `Mob.img` | 怪物名 | `{ID} → name` |
| `MonsterBook.img` | 怪物卡片名 | `{ID} → name, desc` |
| `Npc.img` | NPC名 | `{ID} → name` |
| `Pet.img` | 宠物名 | `{ID} → name, desc` |
| `PetDialog.img` | 宠物对话文本 | `{ID} → 对话内容` |
| `Skill.img` | 技能名+各级描述 | `{ID} → name, desc, h1~hN(各级效果)` |
| `EULA.img` | 用户协议 | EULA文本 |
| `GuestEULA.img` | 游客协议 | 协��文本 |
| `TrialEULA.img` | 试玩协议 | 协议文本 |
| `TestEULA.img` | 测试协议 | 协议文本 |
| `NameChange.img` | 改名相关 | 改名提示文本 |
| `ToolTipHelp.img` | 工具栏提示 | 帮助文本 |
| `TransferWorld.img` | 转服相关 | 转服提示文本 |
| `GLcloneC.img` | 全球服克隆 | 角色克隆字符串 |

### 15.2 字符串格式

```
{实体ID}/
├── <string name="name" value="中文名称"/>
├── <string name="desc" value="详细描述（换行用\\n）"/>
└── (技能特有):
    ├── <string name="bookName" value="技能书名称"/>
    ├── <string name="h1" value="等级1效果描述"/>
    ├── <string name="h2" value="等级2效果描述"/>
    └── ...
```

---

## 16. TamingMob.wz — 坐骑数据

**文件数：** 7（0001~0007）  
**功能：** 坐骑的移动属性和动画

### 16.1 属性结构

```
{坐骑ID}.img/
├── info/
│   ├── <int name="speed" value="80"/>            ← 移动速度
│   ├── <int name="jump" value="100"/>            ← 跳跃高度
│   ├── <float name="fs" value="10.0"/>           ← 摩擦系数
│   ├── <float name="swim" value="80.0"/>         ← 游泳速度
│   └── <int name="fatigue" value="5"/>           ← 疲劳累积速率
└── (动画帧): 同 Morph.wz
```

---

## 17. UI.wz — 用户界面

**文件数：** 19  
**功能：** 游戏所有UI元素的图形资源

### 17.1 文件清单

| 文件 | 说明 | 包含内容 |
|------|------|----------|
| `Basic.img` | 鼠标光标 | 默认/点击动画光标 |
| `BuffIcon.img` | Buff图标 | HP/MP/状态Buff小图标 |
| `CashShop.img` | 商城界面 | 背景/标签/按钮 |
| `CashShopPreview.img` | 商城预览 | 角色试穿界面 |
| `ChatBalloon.img` | 聊天气泡 | NPC/玩家聊天气泡框 |
| `DialogImage.img` | 对话背景 | NPC对话框背景图 |
| `GuildBBS.img` | 公会公告板 | 公会BBS界面 |
| `GuildMark.img` | 公会徽章 | 徽章底图/图案 |
| `ITC.img` | 交易中心 | 自由市场寄售界面 |
| `ITCPreview.img` | 交易预览 | 物品预览界面 |
| `Login.img` | 登录界面 | 开始按钮/选服/选角 |
| `Logo.img` | 游戏Logo | 启动画面Logo |
| `MapLogin.img` | 选服地图 | 世界选择地图背景 |
| `MapleTV.img` | 广播界面 | 全服广播叠加层 |
| `NameTag.img` | 名称标签 | 玩家/NPC名字标签 |
| `Npt_GuideLine.img` | 导航线 | NPC任务导航引导线 |
| `StatusBar.img` | 状态栏 | HP/MP/EXP条/快捷栏/聊天框 |
| `UIWindow.img` | 通用窗口 | 带关闭按钮的窗口框架 |
| `tutorial.img` | 教程UI | 新手引导箭头/高亮 |
| `Ribbon.img` | 缎带 | UI装饰元素 |

### 17.2 UI 按钮状态

```
{按钮名}/
├── normal/      ← 正常状态
├── mouseOver/   ← 鼠标悬停
├── pressed/     ← 按下状态
├── disabled/    ← 禁用状态
└── keyFocused/  ← 键盘焦点
```

每帧含 `origin`, `z`, `delay`。

---

## 18. 附录：常用属性速查

### 18.1 属性类型速查

| XML标签 | 数据类型 | 示例 |
|---------|----------|------|
| `<int>` | 整数 | `<int name="price" value="100"/>` |
| `<string>` | 字符串 | `<string name="islot" value="Cp"/>` |
| `<float>` | 浮点数 | `<float name="mobRate" value="1.0"/>` |
| `<double>` | 双精度浮点 | `<double name="unitPrice" value="0.3"/>` |
| `<vector>` | 二维向量 | `<vector name="origin" x="0" y="32"/>` |
| `<canvas>` | 图片帧 | `<canvas name="icon" width="32" height="32">` |
| `<sound>` | 音频引用 | `<sound name="Bgm00/FloralLife"/>` |
| `<uol>` | 链接引用 | `<uol name="stand" value="../00002000/stand"/>` |
| `<null>` | 空标记 | `<null name="characterStart"/>` |

### 18.2 Canvas（画布）通用属性

| 属性 | 类型 | 说明 |
|------|------|------|
| `width` / `height` | int | 图片宽高(px) |
| `origin` | vector | 锚点(渲染原点) |
| `z` | int | Z序层级 |
| `delay` | int | 帧延迟(ms) |
| `lt` / `rb` | vector | 碰撞盒(左上/右下) |
| `head` | vector | 头部位置 |
| `a0` / `a1` | int | 透明度动画(起始→结束) |

### 18.3 改端常用操作对照

| 操作 | 涉及目录 | 涉及节点 |
|------|----------|----------|
| **新增装备** | Character.wz + String.wz(Eqp) + Item.wz | `info/islot`, 属性加成, 动画帧 |
| **修改技能** | Skill.wz + String.wz(Skill) | `level/*` 属性, `effect/hit/ball` |
| **新增消耗品** | Item.wz(Consume) + String.wz(Consume) | `info/` + `spec/` |
| **新增NPC** | Npc.wz + String.wz(Npc) | `info/speak`, `stand` 帧 |
| **新增怪物** | Mob.wz + String.wz(Mob) | `info/level/hp/exp/attack`, 动画帧 |
| **修改地图** | Map.wz | `info/bgm/returnMap`, `life/`, `portal/` |
| **新增任务** | Quest.wz(Act+Check+QuestInfo+Say) | 4个文件协同配合 |
| **修改商城** | Etc.wz(Commodity) | SN, ItemId, Price, Period |
| **新增反应堆** | Reactor.wz | `action`, 状态0→1→2 |
| **新增变身** | Morph.wz | 动作帧 + `info/speed/jump` |

---

> **参考链接：**
> - 北斗项目架构：`北斗服务器-项目架构及开发手册.md`
> - 改端教程：`冒险岛改端教学文档.md`
> - 开发记录：`开发文档/开发记录.md`
> - 数据库说明：`参考文档/数据库说明文档.md`

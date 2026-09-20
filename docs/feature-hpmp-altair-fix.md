# 修复：套装 HP/MP 显示口径 + 阿尔泰皮肤遮挡

> 关联计划：修复阿尔泰皮肤遮挡与套装HPMP显示口径
> 规范：xiaoye-MapleStory-dev（完整中文注释、回退开关、编译校验、WZ 走 MCP）

## 一、背景

1. **套装 HP/MP 药水加不满**：套装增加 HP/MP 后，服用药水（含百分比药水）无法补满血条。
2. **阿尔泰皮肤仍显示装备外观**：穿戴完整任务动画皮肤时，应只显示皮肤动画，实际自己仍看到披风、武器等装备外观。

## 二、根因

### HP/MP（已修复）
客户端会自行把"经典装备"的 `incMHP/incMMP` 叠加到 `MAXHP` 上（v83 原生行为：服务端 `MAXHP` 不含装备血，客户端本地自加）。因此服务端下发的 `MAXHP` 必须是"不含经典装备血"的值，客户端叠加后才等于真实上限 `localMaxHp`。

- **登录 / 升级 / 池加点**路径发 `clientMaxHp`（裸基础值）→ 客户端自加装备+套装 → 正确（用户实测"装备没问题"）。
- **唯独套装刷新 / 穿脱 / forceSync** 路径发 `localMaxHp`（已含装备血）→ 客户端再叠加一次装备血 → 上限虚高 → 套装那部分永远补不满。即"只有套装不行"的病根。

`Character.java` 四维属性 `computeClientDisplayBaseFourStats()` 已刻意排除经典装备平坦值，HP/MP 此前却没做同样处理，故装备血被算两遍。

### 皮肤（按用户决定不处理）
- 用户明确：不遮挡就算了，皮肤遮挡问题不再处理。
- 现状：别人视角已干净（`addCharEquips` 只发帽子）；自己视角仍显示披风/武器（受 v83 本地背包渲染机制限制，需透明遮挡件 WZ 才能解决，暂不实施）。
- 服务端过滤方案（`addInventoryInfo` 仅发皮肤本体）已回退，因其会清空装备栏，属错误方案。

## 三、改动

### 1. HP/MP 客户端显示口径（已实施，编译通过）
- `Character.recalcEquipStats`：按 `isClientBlindEquipSlot(eq.getPosition())` 分流累计经典槽装备血 `classicEquipMaxHp/Mp`（含 `eq.getHp()+潜能base`）。
- `Character.reapplyLocalStats`：计算 `clientDisplayMaxHp = localMaxHp - classicEquipMaxHp`（MP 同理）。
- `AbstractCharacterObject`：新增 `getClientDisplayMaxHp/Mp()`，统一五处下发（登录 `addCharStats`、升级、套装刷新、forceSync、加点）。
- 回退开关：`use_client_display_maxhp_legacy=true` 回退为旧基础值口径 `clientMaxHp/Mp`。
- 服务端权威不变：药水 `StatEffect` 仍以 `localMaxHp` 截断，逻辑不动。

### 2. 皮肤（已回退，待 WZ）
- `addInventoryInfo` 的"皮肤玩家仅下发皮肤本体"过滤**已回退**：会清空装备栏，不可取。
- 自视角遮挡必须由**透明披风/武器遮挡件**实现（见第六节），无 WZ 则服务端无解。

## 四、关键文件
- `gms-server/.../client/Character.java`（recalcEquipStats / reapplyLocalStats / 五处下发）
- `gms-server/.../client/AbstractCharacterObject.java`（clientDisplayMaxHp/Mp 字段与 getter）
- `gms-server/.../util/PacketCreator.java`（addCharStats / addInventoryInfo 已还原）
- `gms-server/.../server/cashshop/AltairSkinItems.java`（isAltairLookActive / resolveAltairVisual）

## 五、验证
- 编译：`mvn -o -pl gms-server compile -DskipTests` 通过。
- HP/MP：穿脱带 `incMHP` 装备 + 激活套装，客户端血条上限与服务端 `localMaxHp` 差值应为 0；100% 药水应能补满（重点测套装刷新路径）。
- 皮肤：别人视角只显示皮肤（已正确）；自己视角在未加透明遮挡件 WZ 前仍会显示披风/武器。
- 回退：配置 `use_client_display_maxhp_legacy=true` 可一键回到旧口径。

## 六、待办（需 orange-wz MCP）
- 透明披风（110xxxx）与透明现金武器（170xxxx）四份 WZ 副本（Character.wz Cape/Weapon、Item.wz 图标、String.wz 名称），随皮肤自动装备到 `-109`/`-111` 遮住原披风/武器，同时保持装备栏有该件（不空）。
- 本会话无 orange-wz MCP，无法创建 WZ；该方案待工具可用时实施。

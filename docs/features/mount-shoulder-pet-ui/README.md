# 坐骑/护肩错位与宠物拾取红槽

> 状态：已闭环（手工进游验收待用户确认）  
> 适用客户端/协议版本：GMS v083 / BeiDou S9  
> 项目：BeiDou-Server_s9 + BeiDou-ijl15  
> 日期：2026-09-16  
> 产物目录：`docs/features/mount-shoulder-pet-ui/`

## 1. 背景与目标

- **问题**：坐骑预览无图；护肩（115，如 1152206）画进坐骑栏；2 号宠自动拾取槽红显。
- **目标**：护肩仍占 BP20/−20；坐骑窗不再画/点到 BP20；补齐 1902242 资源与名称；PetEquip tab「2」拾取袋不因 −133 别名假红。
- **不做**：不迁护肩存档槽；不改 `PET_EQUIP_SLOTS` 常量。

## 2. 影响范围

| 层级 | 路径/模块 | 变更类型 |
| --- | --- | --- |
| 服务端 Java | 槽位常量 | 未改 |
| WZ/XML | `Character.wz/TamingMob/01902242.img.xml`、`wz-zh-CN`/`wz` `String.wz/Eqp.img.xml` | 资源/文案 |
| 客户端 Data | `Character/TamingMob/01902242.img`（已有）、`ijl15.dll` | 校验 + 插件覆盖 |
| 插件 | `shoulders.cpp`、`equipaddon.cpp` | GetSlotXY 强制 / PetEquip 禁红 |

## 3. 最终实现逻辑

### 3.1 护肩离坐骑行（1A）

- 原生 BP18/19/20 同在 Y=233 坐骑行；115 占用 BP20 后图标进坐骑区。
- `PatchShoulderUiCoords` 将 BP20 表坐标改为经典红 8 `(137,101)`。
- `GetSlotXy_ForceShoulder_hook` @`0x7FEFEA`：凡 `bpIndex==19` 一律返回强制坐标，避免表被其它路径写回 `(71,233)`。
- 保留 `EnsureShoulderSkipMountFailRedBp20`（跳过 MountFail `push 14h`）与 `DrawClearRedBp20_cave`。

### 3.2 2 号宠拾取袋禁红

- Vanilla PetEquip 画 BP33；−133 为 Pet1ItemPouch，与口袋现金镜像冲突。
- `PetEquipDraw_NoPocketGhost_hook`：隐藏非宠鬼影，将 −133 宠袋别名到 −33 再 Draw。
- **20260916b**：若 −33 在鬼影隐藏后已是 `181xxxx`，同样置 `g_petEquipPouchAliased`（不依赖 −133 别名成功）。
- `PetEquipSkipRedBp33_cave` @`0x8017EC`：当 `esi == &BE2260[32]` 且别名中，跳过 `0x40FF0000` 红层。
- `g_petEquipPouchAliased` 为 **非 TLS 的 `volatile bool`**（naked cave 按绝对地址读）。

### 3.3 坐骑 1902242 资源

- 服务端 XML 与 V095 同源已落地；`TamingMob.wz/0010` 已存在（`tamingMob=10`）。
- Client_S9 已有完整 `01902242.img`（140647 bytes）。
- String：zh=`飞天坐骑1902242`；en=`Sky Mount 1902242`（MXD/客户端 Eqp.img 无更优真名）。

## 4. 资源与同步

| 源 | 目标 |
| --- | --- |
| `E:\MXD\BeiDou-Client_S9\Data\Character\TamingMob\01902242.img` | live 客户端（已存在） |
| V095 `01902242.img.xml` | `gms-server/wz/Character.wz/TamingMob/` |
| 文案 | `wz-zh-CN` + `wz` `String.wz/Eqp.img.xml` |
| `out\Release\ijl15.dll` | `E:\MXD\BeiDou-Client_S9\ijl15.dll`（PostBuild xcopy） |

客户端 `Data/String/Eqp.img` 内未写入明文名（WZ 压缩/工具链缺口）——**游戏内名称**若仍占位，需另用 Wz 工具改客户端 String（预留）。图标/模型以 Character img 为准。

## 5. 编码与 i18n

- 服务端 String XML：UTF-8。
- 插件 UI 无新增中文窄字面量。

## 6. 产物清单

| 类型 | 路径 |
| --- | --- |
| 文档 | `README.md` |
| 证据 | `evidence/ida-20260916.md`、`evidence/ida-notes.md` |
| 资源 | `resources/resource-list.md`、`fix_mount_string.py` |
| 构建 | `builds/build-index.md` |

## 7. 验证记录

- IDA：`0x7FEE1B` MountFail BP20、`0x7FEFEA` GetSlotXY、`0x8017EC` PetEquip 红比较、静态表 BP18/19/20 Y=233、BP33=(13,44)。
- 构建：`MSBuild ezorsia.sln /p:Configuration=Release /p:Platform=x86` 成功；stamp `MOUNT_BP20_OFF_ROW_PET_RED_20260916b` 已同步至 Client_S9。
- 服务端：`mvn -pl gms-server -DskipTests package`（JBR 25）成功 → `gms-server/target/BeiDou.jar`。
- 手工进游（请确认）：
  1. 穿 1152206 → 主栏护肩正常；坐骑窗右侧无护肩。
  2. 装备 1902242 → 预览有图；服务端名为「飞天坐骑1902242」。
  3. 2 号宠 + 自动拾取袋 → tab「2」不红。

## 8. 预留、风险与回滚

- 客户端 String 真名未写入 `.img`——显示名可能仍占位，不影响模型。
- 回滚：还原 `ijl15.dll`；还原 Eqp 字符串与 TamingMob XML。
- 风险：GetSlotXY 全局强制 BP20 坐标——主栏护肩依赖同一坐标，勿改回 `(71,233)`。

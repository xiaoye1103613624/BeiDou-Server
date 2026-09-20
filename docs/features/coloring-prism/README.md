# Coloring Prism（七彩棱镜 / weapontint）

> 状态：已闭环（工程侧）  
> 适用客户端/协议版本：GMS v83 / BeiDou S9 + BeiDou-ijl15  
> 项目：BeiDou-Server_s9 + BeiDou-ijl15 + BeiDou-Client_S9  
> 日期：2026-09-17  
> 产物目录：`docs/features/coloring-prism/`  
> 决策：1A 废弃旧 HSL 七彩棱镜；2A 全功能（装备双层 / 发型瞳色肤色 / 技能 / 多段弹道 / ItemEff）

## 1. 背景与目标

用上游 GMS Coloring Prism（道具 `5782000`，协议 `0x372E`/`0x372F`）替换本仓库旧版 HSL EquipDye（`0x11D`/`0x184`、`coloring_prism_dye`），实现预览染色、持久化、地图同步与技能/特效染色闭环。

明确不做：

- 不保留双协议长期并存
- 不把 Liquibase `coloring-prism.xml` 接入 BeiDou（已用 Flyway）
- 不迁移旧 `coloring_prism_dye` 行（语义不兼容，接受清档）

## 2. 影响范围

| 层级 | 路径/模块 | 变更类型 |
| --- | --- | --- |
| 服务端 | `WeaponTintHandler`、`ColorPrismPackets`、`Character`/`Equip`/`Item`/`ItemFactory` | 保留并复核 weapontint |
| 服务端 | 旧 `org.gms.server.coloring.*`、`ColoringPrismHandler`、opcode `COLORING_PRISM*` | 已拆除 |
| DB | `V1.11.19` tint 列 + `skilltints`；`V1.11.64` DROP `coloring_prism_dye` | Flyway |
| 插件 | `BeiDou-ijl15/ezorsia/coloringprism/`（`weapontint`/`coloringprism`/`itemeff`） | 新协议实现 |
| 宿主分发 | `fusionanvil` 门闸/用道具/拖放；`PacketDispatcher` 收 `0x372F` | 一所有者分发 |
| WZ 服务端 | `wz`/`wz-zh-CN`：`Item/.../0578`、`String/Cash` 5782000 | 双语对齐 |
| WZ 客户端 | `Data/UI/UIWindow.img/ColorPrism`；`Data`/`EN` `String/Cash`；`Item/Cash/0578.img` | 合并/文案 |

## 3. 最终实现逻辑

### 3.1 主流程

1. 玩家双击 Cash 栏 `5782000` → 门闸 `get_consume_cash_item_type` 放行 → 发包路径吞包并 `ColorPrism_OnUse` 开窗。
2. 窗口预览本地改色；确认后 C→S `WEAPON_TINT_ACTION(0x372E)`（apply/restore/look/skill）。
3. 服务端校验棱镜、写入装备列 / 角色外观列 / `skilltints`，消耗棱镜，`syncWeaponTint` 推送。
4. S→C `WEAPON_TINT_SYNC(0x372F)` 快照/地图表；DLL `WeaponTint_HandleSync` + 每帧 `WeaponTint_Tick` 刷新层。
5. 渲染：`PrepareActionLayer` / 脸部 builder / ShowSkillEffect / 现金特效 / ItemEff 包裹 / CreateBullet→`NoteBulletFlight`。

### 3.2 协议与存档

| 方向 | Opcode | 用途 |
| --- | --- | --- |
| C→S | `0x372E` | request / apply / restore / applyLook / restoreLook / applySkill / restoreSkill |
| S→C | `0x372F` | 单人快照 + 地图 tint 表 |

- 装备：`inventoryequipment.tint*` + `tintfx*`
- 现金特效道具：`inventoryitems.efftint*`
- 外观：`characters.hairtint*` / `facetint*` / `skintint*`
- 技能：表 `skilltints(characterid, skillid, …)`
- 色相符号语义：正=旋转、负=绝对色相、0=不动（见 `normalizeTintHue`）

### 3.3 模块与扩展点

- 服务端包：`org.gms.server.colorprism`
- 插件入口：`ColoringPrism::EnsureHooks` / `RegisterPacketHandler` / `ColoringPrism_OnTick`
- ItemEff：`AttachItemEffectMod` + `ItemEff_Invalidate`
- 旧 HSL 备份：`ezorsia/coloringprism/_legacy_hsl_backup/`（仅存档，不编入）

## 4. 资源与同步

| 资源 | 路径 | 说明 |
| --- | --- | --- |
| UI | `BeiDou-Client_S9/Data/UI/UIWindow.img` → `ColorPrism/*` | 含 `LayerBt`、`backgrndLook` |
| 独立旧文件 | `ColorPrism.img.unused_standalone` | 已迁出，窗口不读此路径 |
| 道具 | `Data/Item/Cash/0578.img` / `05782000` | icon + `cash=1` |
| 中文名 | `Data/String/Cash.img/5782000` = 七彩棱镜 | 与 `wz-zh-CN` 一致 |
| 英文名 | `EN/String/Cash.img/5782000` + 服务端 `wz/String` | Coloring Prism |
| 密钥 | orange-wz `083-GMS` / IV `TSPHKw==` | 仅本机工具 |

## 5. 编码与 i18n

- 游戏封包字符串仍按客户端 GBK；窗口烘焙标题在 canvas 内。
- 服务端玩家提示走 `I18nUtil`（handler 既有键）。
- 服务端基础 `wz` 英文、覆盖 `wz-zh-CN` 中文。

## 6. 产物清单

见同目录 `resources/`、`evidence/`、`builds/`、`notes/VERIFY.md`。

## 7. 验证记录

- 服务端：`mvn -pl gms-server -am -DskipTests package` → **BUILD SUCCESS**（`builds/mvn-package.log`）
- 插件：`BeiDou-ijl15/build_once.bat` → 已部署 `BeiDou-Client_S9/ijl15.dll`（与 `out/Release` 同 SHA256）
- WZ：reload 校验 `UIWindow.img/ColorPrism`、Data/EN String 5782000
- 游戏内 §5 冒烟清单：`notes/VERIFY.md`（需实机点验）

## 8. 预留、风险与回滚

- **风险**：共享 Detour 冲突会导致静默无染色；必须保持一所有者分发。
- **风险**：`AvatarLook` 槽位须为 52；勿改回 60。
- **旧数据**：`coloring_prism_dye` 已 DROP，不恢复旧色。
- **回滚**：恢复 `_legacy_hsl_backup` + 旧 opcode；客户端可用 `UIWindow.img.bak_before_colorprism_20260917`；DLL 回退上一构建。

## 9. 对外交流摘要

七彩棱镜改为 GMS 风格 weapontint：双击 5782000 开窗，可染装备本体/特效、发型瞳色肤色与技能；服务端 `0x372E/F` 持久化并地图同步；旧 HSL 表与协议已移除。

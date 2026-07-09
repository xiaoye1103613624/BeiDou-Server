# 伤害皮肤（Damage Skin）实现说明

北斗 v083 全栈伤害皮肤功能，协议与 Kaentake/Cosmic v83 参考实现一致。

## 功能概览

| 能力 | 说明 |
|------|------|
| 商城目录 | 服务端从 WZ 扫描 BasicEff.img/damageSkin 写入 damageskin_catalog |
| 个人库存 | damageskin_inventory 记录已拥有皮肤 |
| 当前装备 | characters.activeDamageSkin（0 = 默认数字） |
| 打开选择器 | 使用现金道具 5910000（itemType 591，不消耗） |
| 客户端渲染 | 插件 hook Effect_HP / Effect_Miss，按皮肤 ID 替换伤害数字贴图 |
| 地图同步 | 进图/换图时广播当前皮肤给其他玩家 |

## 封包协议

### 客户端 -> 服务端

| Opcode | 值 | 说明 |
|--------|-----|------|
| DAMAGE_SKIN_APPLY | 0x0110 | 装备皮肤（int skinId） |
| DAMAGE_SKIN_PURCHASE | 0x0111 | 购买皮肤（int skinId） |

### 服务端 -> 客户端

| Opcode | 值 | 说明 |
|--------|-----|------|
| DAMAGE_SKIN_CATALOG | 0x0170 | 商店目录 |
| DAMAGE_SKIN_INVENTORY | 0x0171 | 库存 |
| DAMAGE_SKIN_RESULT | 0x0172 | 操作结果 |
| DAMAGE_SKIN_BROADCAST | 0x0173 | 广播 charId + skinId |

客户端插件通过 PacketDispatcher 仅吞掉 0x170-0x173（及已有的 0x178 世界地图）。

## 服务端改动（gms-server）

### 数据库

gms-server/src/main/resources/db/migration/V1.11.4__damage_skin.sql

### 新增类

- org.gms.client.DamageSkinCatalog
- org.gms.client.DamageSkinInventory
- DamageSkinApplyHandler / DamageSkinPurchaseHandler

### 修改

RecvOpcode, SendOpcode, PacketProcessor, PacketCreator, Character, Server, PlayerLoggedinHandler, UseCashItemHandler, MapleMap, WZFiles

### 构建

cd E:\pro\BeiDou-Server_xy
mvn package -pl gms-server -DskipTests

产物: gms-server/target/BeiDou.jar

## 客户端插件（ijl15.dll）

目录: E:\pro\BeiDou-ijl15\ezorsia\damageskin\

构建 Release Win32 后自动部署到 BeiDou-Client_1\ijl15.dll

需 detours.lib (x86) 放在 ezorsia\detours\

## 客户端资源（BeiDou 083 IMG 节点合并）

**重要：北斗 083 从 `Data/` 目录加载 `.img`，不要在客户端根目录部署 `Effect.wz` / `UI.wz` / `Item.wz` / `String.wz` 补丁包。**

### 路径映射（参考 `WZ needed.zip`）

| 参考 WZ | 目标 IMG 路径 | 合并节点 |
|---------|---------------|----------|
| Effect.wz → BasicEff.img | `Data/Effect/BasicEff.img` | `damageSkin/*`（597 皮肤） |
| UI.wz → UIWindow.img | `Data/UI/UIWindow.img` | `DamageSkin/*`（选择器 UI） |
| Item.wz → 0591.img | `Data/Item/Cash/0591.img` | `05910000` |
| String.wz → Cash.img | `Data/String/Cash.img` | `5910000` |
| Sound.wz | 通常不需要（选择器用 `UI/UIWindow.img/Shop/meso`） |

参考包路径：`E:\资料\xiaoye\mxd学习\伤害皮肤\WZ needed.zip`

### 工具链

1. **WzImg-MCP-Server**（`E:\pro\WzImg-MCP-Server`，HTTP 端口 13339）
   - `init_data_source` → 指向 `BeiDou-Client_1/Data`
   - `delete_property` / `add_property` / `copy_property` / `save_image`
   - 用于验证：`get_property_count`、`get_canvas_info`、`search_by_name`
2. **orange-wz OrzRepacker**（`E:\pro\orange-wz\target\OrzRepacker.jar`）
   - 从 PKG1 参考 WZ 导出 IMG：`ExportRefWz.java`（tools/_dmg_skin_tmp）
   - 节点合并：`MergeDamageSkinFromRef`（替换 damageSkin 子树）、`MergeOneFile`（追加缺失节点）
3. **WzImg XmlToImg / ImgToXml**（验证与中间 XML 处理）

### 标准流程

```text
1. taskkill /F /IM BeiDou.exe
2. 停止占用 Data/*.img 的 WzImgMCP 进程
3. 解压 WZ needed.zip → 用 ExportRefWz 导出 ref_effect/Effect/BasicEff.img、ref_ui/UI/UIWindow.img
4. BasicEff.img：MODIFY — 删除空壳 damageSkin，从 ref BasicEff.img deepClone 完整 damageSkin 子树
5. UIWindow.img：APPEND — MergeOneFile(ref UIWindow.img → client UIWindow.img)，仅补充 DamageSkin 等缺失节点
6. String/Cash.img、Item/Cash/0591.img：已存在则 MergeOneFile 追加；缺失则跳过（本次已验证节点存在）
7. 删除客户端根目录误部署的 Item.wz、Sound.wz、String.wz、UI.wz（保留 List.wz）
8. 验证 manifest.json encryption=GMS；MCP 验证 canvas Format2、DamageSkin 节点
```

### 2026-07-09 合并结果

| IMG | 操作 | 合并前 | 合并后 | 节点验证 |
|-----|------|--------|--------|----------|
| BasicEff.img | MODIFY damageSkin | 4,052,603 B（597 空壳） | 78,703,167 B | 641 皮肤；`damageSkin/1/NoRed0/0` canvas 32×37 Format2 |
| UIWindow.img | APPEND DamageSkin | 11,822,909 B | 12,160,040 B | `DamageSkin` 10 子节点 |
| Item/Cash/0591.img | 已存在 | 1,193 B | 1,193 B | `05910000` OK |
| String/Cash.img | 已存在 | 287,095 B | 287,095 B | `5910000` OK |

**已删除根目录 WZ：** `Item.wz`、`Sound.wz`、`String.wz`、`UI.wz` → 重命名为 `*.removed`

**manifest.json：** `encryption: "GMS"`，`version: "083"`

> **体积说明：** 参考 `Effect.wz` 内 BasicEff.img 约 76MB，含完整 canvas 位图。节点级合并后客户端 `BasicEff.img` 约 75MB（含 597+ 皮肤 canvas），**不是**在根目录部署 74MB WZ 补丁的错误做法。若需压回 ~4MB 量级，需走 `_hash` 轻量 canvas 导入（orange-wz XmlImport 对 `_hash` 格式待完善）；当前以可渲染的完整 canvas 为准。

### 服务端 WZ（只读，与客户端 IMG 分离）

1. 服务端 catalog 读取 `gms-server/wz/Effect.wz/BasicEff.img.xml` 的 `damageSkin`（已合并 ~597）
2. 客户端渲染读取 `Data/Effect/BasicEff.img/damageSkin/<id>/NoRed0/0` 等 canvas

未合并 WZ 时 catalog 为空，可用 SQL 手动插入库存测试。

### 辅助脚本

- `gms-server/tools/merge_damage_skin_xml.py` — 服务端 XML 提取 damageSkin + WzImg MCP 合并（需 MCP 13339 运行）
- `gms-server/tools/_dmg_skin_tmp/ExportRefWz.java` — 从 PKG1 参考 WZ 导出 IMG
- `gms-server/tools/_dmg_skin_tmp/MergeDamageSkinFromRef.java` — 替换 client BasicEff.img 的 damageSkin 子树

## 测试

1. 启动服务端确认 Flyway V1.11.4
2. 登录看 DamageSkinCatalog 日志
3. !item 5910000 或 SQL 插入 damageskin_inventory
4. 打怪看伤害数字；换图/传送确认不崩
5. F12 伤害排行仍可用

## 参考

E:\资料\xiaoye\mxd学习\伤害皮肤\DAMAGE_SKIN_SERVER.md

---

## BeiDou 适配补充 (2026-07-09)

### 参考逻辑
- Client-v83: damageskin.cpp Effect_HP splice + damageskinpicker 5910000 + SEH 延迟 AttachDamageSkinMod
- Server: DAMAGE_SKIN_SERVER.md 全套 handler/DB/opcodes

### 插件修复 (E_FAIL)
- 问题: DamageSkinBridge 与 DamageRankStage 重复 detour set_stage @ 0x777347
- 修复: DamageSkin::AttachHooks() 空操作; DamageRankStage 进 CField 后调用 DamageSkin::EnsureHooks()
- 文件: damageskin/DamageSkinBridge.cpp, damagerank/DamageRankStage.cpp, damageskin/DamageSkinApi.h

### IDA 验证 (BeiDou.exe)
- 0x437DA2 push [esi+174h] OK
- 0x437D8C push [esi+18Ch] OK  
- 0x438166 jz loc_4382F6 OK
- 0x668B83 CMob::OnHit OK

### WZ/IMG 状态（2026-07-09 更新）
- 服务端 BasicEff.img.xml: 已合并 damageSkin (~597)
- 客户端 BasicEff.img: 已合并完整 damageSkin canvas（641 皮肤，MCP 验证 Format2）
- Item 0591.img / String 5910000: 客户端已存在
- UI UIWindow.img/DamageSkin: 已从参考 UI.wz 追加合并
- 根目录误部署 WZ 补丁: 已移除

### 部署
- **必须使用 Release Win32 构建**：`ezorsia/out/Release/ijl15.dll`（520192 B）
- **禁止部署 Debug 构建**（约 1.35MB）——会在启动阶段 E_FAIL
- PostBuild 会自动 xcopy 到 `BeiDou-Client_1/`；若误用 Debug 配置编译，立即改回 Release 并重部署

### 2026-07-09 E_FAIL 全面排查结论

| 测试组合 | dll 大小 | BasicEff | UIWindow | 10s 存活 |
|----------|----------|----------|----------|----------|
| 无插件 | 0 | 78MB | 11.8MB | FAIL（缺 ijl15） |
| **520KB Release + 合并 IMG** | 520192 | 78MB | 12.1MB | **PASS** |
| 1.35MB 错误 dll + 合并 IMG | 1352192 | 78MB | 12.1MB | **FAIL E_FAIL** |
| 467KB 旧版 + 合并 IMG | 467456 | 78MB | 12.1MB | 不稳定（无 EnsureHooks 链） |

**根因（按优先级）：**
1. **插件版本错误**：`ijl15.dll` 被替换为 1,352,192 B 的非 Release 构建（2026-07-09 10:19），含启动期 picker hook 路径，触发 COM E_FAIL
2. **IMG 合并本身不是启动阻断项**：78MB `BasicEff.img`（完整 Format2 canvas）+ 12.1MB `UIWindow.img/DamageSkin` 在 520KB Release dll 下可正常启动
3. v083 ResMan **不会**在启动时全量加载 78MB BasicEff；`damageSkin` 由插件在首次进图（`DamageSkin::EnsureHooks`）后懒加载

**Data/ 与参考客户端差异（关键项）：**
| 文件 | Client_1 | 参考 BeiDou-Client | 说明 |
|------|----------|-------------------|------|
| Effect/BasicEff.img | 78,703,167 | 3,992,551 | 已合并 damageSkin canvas |
| Effect/DamageSkin.img | 无 | 70,058,716 | 参考端用独立 IMG；Client_1 合并进 BasicEff |
| UI/UIWindow.img | 12,160,040（.bak） | 11,806,696 | 需部署含 DamageSkin 的合并版 |
| Item/Cash/0591.img | 1,193 | 无 | Client_1 独有 |
| String/Cash.img | 287,095 | 286,973 | 5910000 名称 |
| manifest.json | 156 B | 176 B | 均为 GMS/083 |

**正确 083 IMG 合并流程（修订）：**
1. `taskkill /F /IM BeiDou.exe`
2. 备份 `Data/Effect/BasicEff.img`、`Data/UI/UIWindow.img`
3. 从 `WZ needed.zip` 导出参考 IMG（ExportRefWz）
4. **BasicEff.img**：删除空壳 `damageSkin`，deepClone 参考完整子树（含 Format2 canvas）→ 约 75–79MB
5. **UIWindow.img**：APPEND 缺失的 `DamageSkin/*` 节点（勿整文件替换）→ 约 12.1MB
6. 验证 `Item/Cash/0591.img`、`String/Cash.img` 含 5910000
7. 删除根目录误部署的 `*.wz` 补丁
8. 部署 **520192 B Release** `ijl15.dll`（非 Debug、非 467KB 旧版）
9. 启动验证：进程存活 ≥10s，登录进图，打怪看伤害数字，F12 使用 5910000 打开选择器

### Case C 安全部署清单（bisect PASS，2026-07-09）

> **工作基线快照：** 见 `docs/working-baseline-20260709.md`

矩阵二分验证 **Case C（commit 2a6dbc2a 组合）** 可启动且功能在首次进图后懒加载。**禁止** 同时部署 78MB `BasicEff.img` 与独立 `DamageSkin.img`。

| 文件 | 目标大小 | 来源 | 说明 |
|------|----------|------|------|
| `ijl15.dll` | **520,704** | `ijl15.full.lazy.dll` 或 `E:\pro\BeiDou-ijl15\out\Release\ijl15.dll` | lazy init，首次进图才挂 hook |
| `Data/Effect/BasicEff.img` | **3,992,551** | **保持原版，勿替换** | 不要用 78MB 合并版 |
| `Data/Effect/DamageSkin.img` | **70,058,716** | `_removed_for_baseline_reset` 或 `DamageSkin.img.bisect_src` | 独立 IMG 承载皮肤 canvas |
| `Data/UI/UIWindow.img` | **12,160,040** | `UIWindow.merged.img` 或 `_efail_bisect` 备份 | 含 DamageSkin 选择器 UI |
| `Data/String/Cash.img` | **287,095** | `_efail_bisect_20260709` | 含 `5910000` 名称（原版 63,983 不够） |
| `Data/Item/Cash/0591.img` | **1,193** | `_efail_bisect_20260709` | 5910000 道具定义 |
| `Data/manifest.json` | **不存在** | — | 勿部署 |

**一键部署（仅复制，不启动客户端）：**

```bat
cd /d E:\mxd_soft\2.客户端\083\beidou_client_xiaoye\BeiDou-Client_1
deploy_features.bat
```

**回滚到 baseline（原版 300544 dll + 原版 IMG）：**

```bat
rollback_baseline.bat
REM 或手动：
copy /Y ijl15.ultra.dll ijl15.dll
copy /Y Data\Effect\BasicEff.img.premerge Data\Effect\BasicEff.img
copy /Y Data\UI\UIWindow.img.bak Data\UI\UIWindow.img
del Data\Effect\DamageSkin.img
del Data\Item\Cash\0591.img
```

**部署后验证清单：**

1. 各文件字节数与上表一致（`deploy_features.bat` 会自动校验）
2. `BasicEff.img` ≤ 4MB 且 `DamageSkin.img` ≈ 70MB（安全组合）
3. 启动 → 选角 → **进任意地图**（lazy hook 触发点）
4. 打怪看伤害数字是否换皮
5. 世界地图（M）正常
6. F12 伤害排行仍可用
7. 使用道具 **5910000** 打开伤害皮肤选择器

**危险组合（已知 FAIL）：**

- 78,703,167 B `BasicEff.img` + 70,058,716 B `DamageSkin.img` 同时存在
- 1.35MB Debug `ijl15.dll`
- 根目录误部署 `Effect.wz` / `UI.wz` 补丁包

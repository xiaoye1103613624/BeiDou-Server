# 北斗 GMS083 — 高版本现金外观装备导入

> **状态（2026-07-10）**：**试点已撤销** — 01102900 等试点项已从服务端 `wz/` 与客户端 `Data/` 回滚；**工具链保留**供后续导入使用。

基于 [ImportHighVersionCashItems.py](E:/资料/xiaoye/mxd学习/导入高版本现金物品/ImportHighVersionCashItems.py) 适配北斗双端 WZ 工作流。

### 试点撤销原因

1. **079 整合包 XML 不含 canvas bytedata** — 服务端结构可导入，客户端 icon/sprite 须另备二进制 `.img`
2. **01102900（特效披风）** — 缺 `Effect.wz` 雪花动画；1×1 占位 + 缺 icon 导致 tooltip 风险
3. **推荐下一试点**：01102899（完整 sprite + `_outlink`）；确保 `CLIENT_IMG_FALLBACK_ROOTS` 有对应 `.img`

## 目录

| 路径 | 说明 |
|------|------|
| `import_high_version_cash.py` | 主脚本（CLI） |
| `config_beidou.py` | 北斗默认路径 |
| `output/{Type}/` | 处理结果（部署前检查） |

## 用户需准备的高版本导出

用 **HaRepacker**（或 WzComparerR2 + 导出）从高版本/079 整合客户端导出 XML：

| 变量 | 含义 | 推荐路径（本机已验证） |
|------|------|------------------------|
| **SOURCE_DIR** | `Character.wz/{Type}/*.img.xml`，含动作帧坐标，可有 `_outlink` | `E:\mxd_soft\079\wz\Character.wz\{Type}\` |
| **REFERENCE_DIR** | 含 canvas `bytedata` 的 XML；可与 SOURCE 相同，或单独 `_Canvas` 目录 | 同上，或 `{Type}\_Canvas\` |
| **STRINGS_SOURCE** | `String.wz/Eqp.img.xml` 导出（名称/描述） | `E:\资料\xiaoye\mxd学习\导入高版本现金物品\SOURCE_Eqp.img.xml` |

> **说明**：当前 079 整合包导出的 Cape XML 多数**不含** `bytedata`（仅结构+坐标+`_outlink`/`_hash`）。**服务端**只需要结构 XML；**客户端**需要二进制 `.img`（PNG 内嵌），须从 079 客户端 `Data/` 另取，或重新导出含 `bytedata` 的 `_Canvas` XML。

## 「完整导入」到底包含什么？

| 层级 | 079 `wz/*.img.xml` 是否够用 | 脚本 `--deploy` 行为 |
|------|------------------------------|----------------------|
| 服务端 Character XML | ✅ 够用（结构/坐标/cash 槽位） | 自动写入 `gms-server/wz/Character.wz/` |
| String 名称 | ✅ 需另备 `SOURCE_Eqp.img.xml` | 自动合并 EN + zh-CN |
| 客户端 `.img`（图标/穿戴 sprite） | ❌ **079 wz XML 不含 PNG** | 从 `CLIENT_IMG_FALLBACK_ROOTS`（079 客户端 Data 等）**复制**已有二进制；或 `--build-client-img` 从含 bytedata 的 XML 生成 |
| 特效披风视觉效果 | ❌ 不在 Character.wz | 需另移植 `Effect.wz`（如 01102900 雪花） |

**结论**：脚本导入的是「服务端 + 字符串 + 客户端结构」；**不是**把 079 文件夹里所有视觉资源一次性搬完。079 `E:\mxd_soft\079\wz\` 只有 XML 骨架，真实 PNG 在客户端 `Data/Character/.../*.img` 或需 HaRepacker 勾选导出 bytedata。

### 01102900 为何看起来「不完整」？

1. **079 源 XML**：`info/icon` 只有 `width/height`，无 `bytedata`、无 `_outlink`；`default` 为 **1×1 透明占位**（特效型披风）
2. **无 `_Canvas` 目录**：本机 079 导出未含 icon PNG
3. **穿戴无特效**：`setItemID=587` 关联 `Effect.wz` 雪花动画，Character 里 intentionally 无 sprite
4. **客户端 img**：079 wz 无法生成，故 `--deploy` 从 `BeiDou-Client\Data\Character\Cape\01102900.img`（7609B）复制到 `BeiDou-Client_1`

### 01102899 能否从 079 完整导入？

- **服务端**：可以（结构与 01102883 通过 `_outlink` 引用）
- **客户端 sprite**：079 XML 同样无 bytedata；01102899 的 canvas 全部 `_outlink` → `01102883.img`，而 01102883 也只有 `_hash` 无 PNG
- **更好试点**：01102899 + 确保 `CLIENT_IMG_FALLBACK_ROOTS` 有对应 `.img`，或导出 `_Canvas` 含 bytedata 后再 `--build-client-img`

### 一条命令尽量完整

```powershell
# 1) 结构 + String + 从 079/北斗客户端 Data 复制 .img
python import_high_version_cash.py --item-type Cape --ids 01102899 --deploy

# 2) 若已导出含 bytedata 的 REFERENCE（或 _Canvas），可生成 .img
python import_high_version_cash.py --item-type Cape --ids 01102899 --deploy --build-client-img
```

**仍无法自动完成**：特效披风的 `Effect.wz`、商城 `Commodity.img` 上架。

### 其他可参考源

- `E:\mxd_soft\2.客户端\083\20大陆_079整合版\20dalu\wz\Character.wz\{Type}\`
- `F:\BaiduNetdiskDownload\20大陆_079整合版\...\客户端`（079 高版本内容）

## 脚本能力（P0）

1. 扫描 `SOURCE_DIR` 中 `cash=1` 的装备，跳过服务端已有 (`wz/Character.wz/{Type}/`)
2. 从 `REFERENCE_DIR` 复制 `bytedata`（若有 `_outlink` 则解析引用）
3. 删除 `_outlink` 节点
4. **剥离 v083 不兼容节点**：`potential`、`starForce`、`setEffect`、`equipTradeBlock`、`setItemID` 等
5. **服务端模式**：去除所有 canvas `bytedata`
6. **双 String 合并**：`wz/String.wz/Eqp.img.xml`（EN）+ `wz-zh-CN/String.wz/Eqp.img.xml`（ZH 占位）
7. UTF-8 无 BOM 输出

## 用法

```powershell
cd E:\pro\BeiDou-Server_xy\gms-server\tools\import_high_version_cash

# 单件试点（披风 01102900）
python import_high_version_cash.py --item-type Cape --ids 01102900 --deploy

# 仅生成到 output，不写入 wz
python import_high_version_cash.py --item-type Cape --ids 01102900 --dry-run

# 按 test_output.txt 批量（示例限 5 件）
python import_high_version_cash.py --item-type Cape --ids-file "E:\资料\xiaoye\mxd学习\导入高版本现金物品\test_output.txt" --limit 5

# 自定义源目录
python import_high_version_cash.py --item-type Coat ^
  --source-dir "D:\export\Character.wz\Coat" ^
  --reference-dir "D:\export\Character.wz\Coat\_Canvas" ^
  --deploy
```

## P1 试点：披风 01102900（Lumpy Snowflakes）

### 诊断结论（2026-07-10）

| 现象 | 根因 |
|------|------|
| 背包图标透明 | `BeiDou-Client_1` **缺少** `Data/Character/Cape/01102900.img`；079 导出 XML 无 icon bytedata |
| 鼠标悬停崩溃 | 客户端 tooltip 读取 `Character/Cape/01102900.img` 的 icon/预览，文件缺失导致崩溃 |
| 穿戴无视觉效果 | 该件为**特效型披风**：`default` canvas 为 1×1 透明占位，实际雪花特效依赖 `Effect.wz`（未移植） |

079 源 XML 的 `info/icon` 仅有 `width/height`，**无** `bytedata`/`_outlink`。但 `BeiDou-Client\Data\Character\Cape\01102900.img`（7609 字节）内嵌完整 icon PNG（38×37）。

**不推荐继续以 01102900 作为批量导入试点**：特效披风缺 Effect 资源。更好试点：`01102899`（完整 sprite + _outlink 到 01102883）。

| 步骤 | 状态 | 路径 |
|------|------|------|
| 高版本 SOURCE | ✅ | `E:\mxd_soft\079\wz\Character.wz\Cape\01102900.img.xml` |
| 脚本处理 + 部署服务端 Character | ✅ | `gms-server/wz/Character.wz/Cape/01102900.img.xml` |
| String EN | ✅ | `gms-server/wz/String.wz/Eqp.img.xml` → `1102900` |
| String ZH | ✅ | `gms-server/wz-zh-CN/String.wz/Eqp.img.xml` → `1102900` |
| 客户端散 img（Case C） | ✅ 已修复 | `BeiDou-Client_1\Data\Character\Cape\01102900.img`（7609B，含 icon） |

### 游戏内测试

```
!item 1102900 1
```

检查清单：
- [ ] 背包图标显示雪花披风图标（非透明）
- [ ] 鼠标悬停 tooltip **不崩溃**，显示名称 "Lumpy Snowflakes"
- [ ] 穿戴后披风槽位有装备（本体为 1×1 占位，无雪花特效属正常，需另移植 Effect.wz）

`--deploy` 现会自动从 `CLIENT_IMG_FALLBACK_ROOTS` 复制二进制 `.img` 到 `CLIENT_DATA_ROOT`。

### 客户端 Xml → Img（若需重新打包）

当 `output/Cape/*.img.xml` **含 bytedata** 时，用 orange-wz 转二进制：

```powershell
cd E:\pro\orange-wz
mvn -q package -DskipTests

java -cp target/classes orange.wz.MyXml2Img ^
  "E:\pro\BeiDou-Server_xy\gms-server\tools\import_high_version_cash\output\Cape\01102900.img.xml" ^
  "E:\mxd_soft\2.客户端\083\BeiDou-ClientV16.1\BeiDou-Client\Data\Character\Cape\01102900.img" ^
  "01102900.img"
```

**铁律（Case C）**：仅 APPEND 到 `Data/Character.wz/{Type}/{id}.img`，**不要**直接改 PKG1 内 `.wz`。

## 北斗约束 checklist

- [x] 服务端 `wz/Character.wz` + `wz` / `wz-zh-CN` String 双写
- [x] 客户端 `Data/Character.wz/...` 散文件热更（ijl15）
- [x] 无 Hook（纯外观现金装备）
- [x] UTF-8 XML 无 BOM
- [x] v083 不兼容字段已剥离
- [ ] 可选：`Etc.wz/Commodity.img.xml` 商城上架（本试点未加）

## 已知限制

1. **079 wz XML 无 bytedata**：整包 Cape 目录 0 个 canvas 含 PNG；客户端依赖 `Data/*.img` 回退复制或 REFERENCE 含图导出。
2. **_outlink 解析**：脚本已修复为扫描所有 `_outlink` canvas；但若 REFERENCE 也无 bytedata（本机 079 即如此），仍无法 inline 图片。
3. **中文名占位**：`SOURCE_Eqp` 仅英文时，zh-CN String 会加「（高版本）」后缀，可手改。
4. **特效披风**（如 01102900）：`setItemID` 已删除；缺 Effect.wz 时穿戴仅 1×1 占位，属正常。

## 相关文档

- `C:\Users\Administrator\.claude\skills\xiaoye_maple_dev\SKILL.md`
- `E:\资料\xiaoye\mxd学习\导入高版本现金物品\导入高版本现金物品-试点指南.md`

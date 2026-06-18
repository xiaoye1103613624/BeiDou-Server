# 冒险岛 WZ / IMG 工具链完整参考

> 生成日期：2026-06-17  
> 来源：对 7 个仓库源码的深度研读，结合 BeiDou-Server_xy 项目实际架构总结

---

## 1. 总体工具链地图

```
┌─ 浏览 / 查值 / 版本对比 ─────────────────────────────────────┐
│  WzComparerR2（C#，Kagamia）                                  │
│  • String WZ 搜索（ID/名称）                                   │
│  • 两个客户端版本 diff（升级 wz 时找改动点）                     │
│  • 装备模拟器 / 地图仿真器 / 纸娃娃 Avatar 预览                  │
└──────────────────────────────────────────────────────────────┘

┌─ 可视化编辑 + 导出 img ──────────────────────────────────────┐
│  Harepacker-resurrected（C#/.NET 8，lastbattle）               │
│  HaRepacker = .wz 编辑器；HaCreator = 地图/关卡编辑器          │
│  底层 MapleLib（同 WzImg-MCP-Server 共用）                     │
│  ★ 唯一能导出 .img + manifest.json（供 wzimg MCP 消费）        │
└──────────────────────────────────────────────────────────────┘

┌─ AI 批量读写 WZ/IMG ─────────────────────────────────────────┐
│  WzImg-MCP-Server（C#/.NET，lastbattle）        74 MCP 工具   │
│  orange-wz / OrzRepacker（Java/Spring，leevccc）22 MCP 工具   │
└──────────────────────────────────────────────────────────────┘

┌─ 客户端修改 / 加载散 img ────────────────────────────────────┐
│  BeiDou-ijl15（C++，BeiDouMS）    ★ 直接可用的成品 ijl15.dll  │
│  ClientImageLoader（C++，MapleMyth）教学版，原理参考           │
│  kaentake（C++，iw2d）            另一个 v83 启动器参考        │
└──────────────────────────────────────────────────────────────┘

┌─ 服务端运维 CLI ─────────────────────────────────────────────┐
│  beidou-cli（Java/GraalVM，BeiDouMS）  封装 8686 REST API      │
└──────────────────────────────────────────────────────────────┘
```

### 关键数据流

```
二进制 .wz
  │ HaRepacker 导出 .img + manifest.json
  ▼
散 .img 目录（WZIMGMCP_DATA_PATH）────→ wzimg MCP（74 工具）
  │                                      AI 读/写/导出 PNG
  │ orange-wz 直接吃二进制 .wz/.img
  │ Xml2Img 互转
  ▼
服务端 XML（wz-zh-CN/*.wz/ 目录）←──── 北斗服务端消费
  XMLWZFile 读 *.xml，ItemInformationProvider 等
  （与客户端 img 是两套独立的格式，互不自动同步）
  ▼
客户端 ./Data/ 散 img ────────────── BeiDou-ijl15 加载
（覆盖原 .wz，免重打包热更）
```

---

## 2. BeiDou-Server_xy 服务端 WZ 数据格式（重要）

**服务端读的是 XML 格式，不是二进制 .wz。**

- 入口类：`org.gms.provider.wz.XMLWZFile`
- `getData(path)` → `root.resolve(path + ".xml")` + `XMLDomMapleData` DOM 解析
- `wz/Item.wz/` 等实际上是**目录**（含 `.xml` 文件和 `.img` 子目录），不是二进制文件
- 语言优先：`wz-zh-CN/` 优先，fallback `wz/`（`WZFiles.getFile()`）
- 客户端版本：GMS v83，OdinMS/Cosmic 祖传格式

**加自定义内容必须同时改两侧：**

| 侧 | 格式 | 改什么 | 工具 |
|---|---|---|---|
| 服务端 | XML（wz-zh-CN/ 目录）| 属性/价格/装备条件 | 直接编辑 xml 文件 / orange-wz 的 Xml2Img |
| 客户端 | .img 散文件（./Data/）| 图标/动画 Canvas | HaRepacker / orange-wz GUI |

---

## 3. WzImg-MCP-Server（C#/.NET）

**本 session 挂载的 `wzimg` MCP 的源码，本地：`E:\pro\WzImg-MCP-Server`**

### 前置条件
1. 用 HaRepacker 把 .wz 导出为 .img 目录（含 manifest.json）
2. 设环境变量 `WZIMGMCP_DATA_PATH` 指向该目录

### manifest.json 字段
```json
{
  "version": "v83",
  "displayName": "GMS v83",
  "isPreBB": true,
  "is64Bit": false
}
```

### 架构（三层）
```
传输层：stdio（默认）或 --http（端口 13339）
会话层：WzSessionManager（单例）
  - ImgFileSystemDataSource 管理 .img 目录
  - 懒解析：img.ParseImage() 按需触发，UnparseImage() 释放内存
工具层：ToolBase → Execute<T>()（统一会话校验+异常捕获）
```

### 74 个工具分类速查

| 分类 | 代表工具 | 说明 |
|---|---|---|
| 文件 | `init_data_source` `list_categories` `scan_img_directories` | 初始化、扫描 |
| 导航 | `list_properties` `get_tree_structure` `search_by_name` `search_by_value` | 路径导航/搜索（支持 `*` 通配） |
| 属性读取 | `get_string` `get_int` `get_float` `get_vector` `resolve_uol` `get_properties_batch` | 按类型取值 |
| Canvas | `get_canvas_bitmap`（base64 PNG）`get_canvas_info` `get_animation_frames` `resolve_canvas_link` | 图像操作 |
| 音频 | `get_sound_info` `get_sound_data` | MP3/WAV 元数据/data |
| 导出 | `export_png` `export_mp3` `export_to_md` | 导出到文件 |
| 分析 | `get_statistics` `find_broken_uols` `compare_properties` | 统计/校验 |
| 修改 | `set_string` `set_int` `add_property` `delete_property` `save_image` `discard_changes` | 读写保存 |
| 批量 | `extract_to_img` `pack_to_wz` `batch_search` | 转格式/批量搜 |
| 生命周期 | `parse_image` `unparse_image` `preload_category` | 内存管理 |

### 三种链接解析（重要）

| 类型 | 字段 | 解析工具 | 说明 |
|---|---|---|---|
| UOL（软链接）| `WzUOLProperty` | `resolve_uol` | 同 img 内跳转 |
| inlink（内联）| `_inlink` 字符串属性 | `resolve_canvas_link` | 同 img 内 canvas 引用 |
| outlink（外联）| `_outlink` 字符串属性 | ❌ **不支持** | 跨 img 引用，工具会抛异常 |

`_outlink` 跨 img 引用需用 **orange-wz** 或 **HaRepacker** 处理。

### Canvas 取图流程
```csharp
// WzDataConverter.GetCanvasBase64()
var bitmap = canvas.GetLinkedWzCanvasBitmap(); // 自动跟随链接
bitmap.Save(ms, ImageFormat.Png);
return Convert.ToBase64String(ms.ToArray());
```

### 响应大小控制（appsettings.json）
```json
{
  "ResponseLimits": {
    "MaxMarkdownResponseKB": 512,
    "MaxBase64ImageKB": 256,
    "DefaultAnimationPageSize": 5,
    "DefaultPropertyPageSize": 50
  }
}
```
- 默认 `compact=true`（只返回 name/type/childCount）
- 动画帧默认分页 5 帧，支持 `offset`/`limit`/`has_more`

---

## 4. orange-wz / OrzRepacker（Java/Spring）

**本地未克隆，需要时从 GitHub 获取：https://github.com/leevccc/orange-wz**

### 特点（相比 WzImg-MCP-Server）
- 自带完整 WZ **二进制读写库**（自行实现，非依赖 MapleLib）
- 直接吃二进制 .wz 和 .img，**无需 HaRepacker 预导出**
- 支持 `_outlink` 跨 img 引用（完整库，非 MCP 层限制）
- 带 **Swing GUI**（FlatLaf）可视化编辑
- **Xml2Img** 支持 .wz/.img ↔ XML 互转（是连接客户端 img 与服务端 XML 的桥）
- 代码用 classfinal 加密，无法直接修改源码打补丁
- MCP 端口：`10002`，路径 `/mcp`，`web-application-type=servlet` 时开启

### WZ 密钥体系（WzAESConstant）
```java
WZ_GMS_IV    = {0x4D, 0x23, 0xC7, 0x2B}  // 国际服 v83（你的版本）
WZ_CMS_IV    = {0xB9, 0x7D, 0x63, 0xE9}  // 亚洲/中服低版本
WZ_LATEST_IV = {0x00, 0x00, 0x00, 0x00}  // 新版本无加密
DEFAULT_KEY  = 32 字节，见源码
```

密钥存储：`keys.dat`，GUI 里可增删，`WzKeyStorage` 管理。

### WzMutableKey 密钥流生成原理
```java
// AES/ECB/NoPadding，批量 4096 字节生成
// iv == 0 时跳过加密（LATEST_IV 场景）
Cipher cipher = AES/ECB; 
for (i = startIndex; i < size; i += 16) {
    block = (i==0) ? expand(iv) : prev_16_bytes_of_keys;
    keys[i..i+16] = cipher.update(block);
}
```

### 批量改密钥为什么慢（根因）

**调用链**：`changeKey()` → `rebuildCompressedForPngBelongListWz()` → **每个 Canvas** → `rebuildCompressedBytesUseNewWzKey()`

```java
// WzPngProperty.java:404 作者自己的注释：
// 该方法在处理CMS079的Map.wz时要额外花费123秒，只是为了List.wz的图片
byte[] compressedBytes = getCompressedBytes(false); // 读磁盘
byte[] rawBytes = decompress(compressedBytes);       // inflate 解压（无条件）
if (listWzUsed) {
    compressBytes(rawBytes, wzMutableKey);           // deflate 压缩 + XOR 加密
}
```

**三个慢点**：
1. `decompress()` **无条件对每个 Canvas 执行 inflate**，不管是否 List.wz
2. `save()` 设了 `setChanged(true)`，强制全量 deflate 重写，无法复用原始字节
3. Map.wz 等大文件 Canvas 数量上万，IO + CPU 双重瓶颈

**解决方案**：
- 服务端读 XML 格式，**根本不需要改 WZ 密钥**，日常编辑保持原密钥即可
- 确实需要跨区移植（GMS→CMS）：只对实际编辑过的少量 img 改，别对整个 Map.wz 批量操作
- 一次性做完，结果存盘，不要反复改密钥

### 22 个 MCP 工具（会话模型）
```
create_session → load_files → 
  get_node_detail / list_children / find_node / search_node /
  query_nodes / mutate_nodes / batch_update_nodes / batch_find_nodes /
  create_child_node / copy_nodes / paste_nodes / delete_node /
  save_node / save_node_as / create_wz_file / create_img_file /
  unload_node / unload_all / list_loaded_roots / get_node_tree_json
```

---

## 5. Harepacker-resurrected（C#/.NET 8）

**本地：`E:\pro\Harepacker-resurrected`**

### 模块
| 模块 | 说明 |
|---|---|
| HaRepacker | .wz 文件编辑器（GUI + MapleLib） |
| HaCreator | 地图/关卡编辑器，可导出 .img + manifest.json |
| MapleLib（子模块）| WZ 读写核心库，同 WzImg-MCP-Server 共用 |
| WzImg-MCP-Server（子模块）| Codex/AI MCP 集成 |
| Real-ESRGAN | AI 图像 2x 放大（ncnn-vulkan） |
| spine-csharp 2.1.25 | Spine 2D 动画播放 |

### 关键文档（`docs/wz-format/`，本地真实存在）
- `README.md` — WZ 文件格式总览
- `wz-file-overview.md` — WZ/IMG 文件结构、加密、格式历史
- `wz-format-history.md` — 各版本格式变化
- `WzFileManager.md` — 中央 WZ 文件加载管理类
- `canvas-outlink-system.md` — `_Canvas` 目录、`_outlink`/`_inlink` 解析机制

### 构建要求
- VS 2022 + .NET 8 SDK
- `git submodule update --init --recursive`（含 MapleLib、WzImg-MCP-Server）
- `dotnet publish WzImg-MCP-Server\WzImgMCP\WzImgMCP.csproj` 产出 wzimg MCP 可执行文件

---

## 6. BeiDou-ijl15（C++，北斗客户端补丁）

**本地：`E:\pro\BeiDou-ijl15`，branch BeiDou**

### 使用方式
1. 原 `ijl15.dll` → 重命名为 `2ijl15.dll`
2. 编译好的 `BeiDou-ijl15.dll` → 重命名为 `ijl15.dll` 放入客户端目录
3. `ezorsia/config.ini` 复制到客户端目录

### config.ini 完整配置项

```ini
[general]
imeType=1              ; IME 修复方案：1（更完美）/0（备用，遇卡门用）
width=1280             ; 分辨率（推荐 1280x720 / 1366x768）
height=720
ServerIP_Address=127.0.0.1
serverIP_Port=8484     ; 登录端口，对应服务端 gms.service.login-port
SwitchChinese=true     ; 加载文字汉化
EzorsiaV2WzIncluded=true  ; UI.wz 是否含 MapleEzorsiaV2wzfiles.img
CustomLoginFrame=true
bigLoginFrame=true     ; 登录界面大边框布局
WindowedMode=true
RemoveLogos=true       ; 跳过开头动画
UseVirtuProtect=true   ; CRC 校验（无 CRC bypass 的客户端必须 true）

[optional]
setDamageCap=199999    ; 物理攻击面板上限
setMAtkCap=1999        ; 魔攻/魔防上限
setAccCap=999          ; 命中上限
setAvdCap=999          ; 回避上限
setAtkOutCap=199999    ; 真实输出上限
speedMovementCap=140   ; 移速上限
jumpCap=123            ; 跳跃上限
climbSpeedAuto=false   ; 爬绳速度自适应
climbSpeed=1.0
useTubi=true           ; 防止升级/宠物捡取时停顿
```

### 相比原版 ijl15 的增强（v1 基础上，BeiDou 分支）
- 中文输入 / 中文角色名（修复卡门问题）
- 修复滚轮乱飞、ToolTip 超出窗口
- 长键盘快捷键支持
- 交易中心居中
- 中文汉化（UI 文字）
- 魔攻/魔防/命中/回避/跳跃上限突破
- **BossHp 百分比显示**（Boss 血条头像下方）
- **免密模式**（服务端需配合）
- 装备 tooltip / 道具有效期字体大小调整
- 修复有效期日期顺序
- 远征队菜单汉化
- **无需改 wz 即可解决 Eqp/Etc 汉化后游戏崩溃、Use 汉化后吃药没声音**（重要）

### 编译环境
- VS 2019，SDK 10，工具集 v142，**Release x86**
- 产物：`out/Release/ijl15.dll`

---

## 7. ClientImageLoader（C++，MapleMyth）

**原理教学版，BeiDou-ijl15 是其成品化版本**

### 核心：Resman.cpp Hook 原理

```
DllMain → Hook_InitializeResMan(true)
       → Microsoft Detours DetourAttach(CWvsApp::InitializeResMan)
       → 替换为 lambda：
           1. 创建 ResMan（AUTO_REPARSE | AUTO_SERIALIZE）
           2. 创建根命名空间
           3. 挂载游戏目录 → "/"（.wz 的基础）
           4. 挂载 ./Data/ → "/"（追加，覆盖同名 .wz 内容）
```

第二个 `IWZNameSpace::Mount` 让 `./Data/` 的 img **优先于** 原 .wz，实现热更。

### v83 关键函数地址（`functions/GMS_v83.txt`）
```
CWvsApp__InitializeResMan = 0x009F7159
g_rm = 0x00BF14E8    g_root = 0x00BF14E0    pNameSpace = 0x00BF0CD0
PcCreateObject_IWzResMan = 0x009FAF55
PcCreateObject_IWzNameSpace = 0x009FAFBA
IWZNameSpace__Mount = 0x009F790A
PcCreateObject_IWzFileSystem = 0x009FB01F
IWzFileSystem__Init = 0x009F7964
bstr_constructor = 0x00406301
```

### 注入方式（APIDummy 导出法）
1. CFF Explorer 打开客户端目录的 `ijl15.dll`
2. Import Adder → Add → 选编译好的 DLL
3. 导入 `APIDummy` 导出
4. Rebuild Import Table → 保存

---

## 8. kaentake（C++，iw2d）

**本地：`E:\pro\kaentake`**  
另一个 v83 客户端启动器/加载器参考，含 Custom.wz（扩展分辨率下拉框）

```ini
[config]
host=127.0.0.1
port=8484
```

命令行优先：`Kaentake.exe 127.0.0.1 8484`

---

## 9. WzComparerR2（C#，Kagamia）

**本地未克隆。https://github.com/Kagamia/WzComparerR2**  
目前"深度维护"状态，功能完整可用。

### 核心功能
- **String WZ 搜索**：按名称/ID 找物品、技能、地图、NPC
- **客户端版本对比（diff）**：两个 .wz 版本之间的改动对比（升级版本时用）
- **装备模拟器**：纸面属性预览
- **MapRender 地图仿真器**：地图视觉预览（SharpDX/DirectX 11）
- **Avatar 纸娃娃**：角色穿戴预览（Spine 动画支持）
- **Lua 控制台**（可选插件）

### 技术栈
- C# latest + .NET 4.62 / .NET 8
- SharpDX / Monogame（地图渲染）
- BassLibrary（音频）
- IMEHelper（输入法）
- Spine-Runtime 2D 动画

---

## 10. flwmxd/WzTools（C++）

**https://github.com/flwmxd/WzTools**  
极简 C++ WZ 只读库，适合学习 C++ 侧如何解析 WZ 树，或嵌入自己的 C++ 工具。

```cpp
// API 示例
WzNode node = WzFiles::character["Hair"]["0000000.img"]["info"];
```

构建：CMake，`build.sh` 或 `cmake && make`。

---

## 11. 工具选型速查

| 场景 | 推荐工具 | 备注 |
|---|---|---|
| 找物品/技能 ID 或名称 | WzComparerR2 | 最快 |
| 版本 wz 升级对比 | WzComparerR2 | diff 功能 |
| 可视化编辑 .wz | HaRepacker | GUI 编辑器 |
| 导出 img 供 AI 用 | HaCreator → manifest.json | wzimg MCP 前置步骤 |
| AI 批量读 img 数据 | wzimg MCP（74 工具）| 本 session 已挂载 |
| 直接读二进制 .wz/跨区移植 | orange-wz GUI | Xml2Img 连接两侧格式 |
| 改服务端属性数值 | 编辑 wz-zh-CN/*.xml | XMLWZFile 读取 |
| 客户端加载散 img | **BeiDou-ijl15**（推荐成品） | config.ini 丰富配置 |
| 学习客户端 Hook 原理 | ClientImageLoader | 教学版，原理清晰 |
| C++ 嵌入 WZ 读取 | WzTools / kaentake | 轻量，只读 |
| 服务端运维/AI 发道具 | beidou-cli | 封装 8686 API |

---

## 12. WZ 文件格式速览（来自 Harepacker docs）

```
.wz 文件结构（二进制）：
  Header：4 字节 magic ("PKG1") + version + hash + copyright string
  目录区：WZDirectory → WZEntry（file/dir）树
  数据区：.img 二进制块，key-stream XOR 加密

.img 文件（独立或嵌在 .wz 中）：
  IMG Header → WzImageProperty 树
  属性类型：Short/Int/Long/Float/Double/String/Sub/Canvas/Vector/Sound/UOL/Lua/Convex/Null
  Canvas：PNG 像素数据，zlib 压缩后 XOR key-stream
  UOL：字符串路径，运行时解引用
  _inlink：同 img 内 canvas 引用（路径字符串）
  _outlink：跨 img canvas 引用（路径含 img 文件名）

加密 IV（v83 GMS）：{0x4D, 0x23, 0xC7, 0x2B}
密钥流：AES/ECB 批量生成（WzMutableKey，4096 字节一批）
```

---

*本文档由 Claude Code 自动生成，存储路径：`resource_doc/冒险岛WZ工具链完整参考.md`*

# Kaentake 头顶动态聊天表情（Chat Emoticon）

> 状态：服务端已闭环 | 客户端资源已落盘 | 客户端插件接入为**预留项**（见第 8 节）
> 适用客户端/协议版本：MapleStory GMS083（`E:\MXD\BeiDou-Client_S9\BeiDou.exe`，ImageBase `0x00400000`）
> 项目名称：BeiDou-Server_s9（GMS083 服务端）
> 作者/日期：2026-09-18
> 相关提交：07666ad86b（`✨ feat(server): 新增 Kaentake 头顶动态聊天表情（0x11C/0x17F）`）
> 产物目录：`docs/features/chat-emoticon/`

## 1. 背景与目标

把 Kaentake 的「头顶动态聊天表情包」移植到 BeiDou GMS083：玩家点聊天栏表情按钮 → 弹出 4 分类表情网格 →
选中后客户端上行表情 ID → 服务端校验并同地图广播 → 所有客户端在角色头顶显示气泡 + 动画约 3 秒。

**本次做到的事**

- 服务端协议闭环：收包 `0x11C`、广播 `0x17F`、ID 白名单、1500ms 冷却、i18n 日志。
- 客户端静态校验：用 PE 解析 + capstone 反汇编核对移植包全部硬编码地址与特征码（等效 IDA 证据）。
- 客户端资源落盘：`Effect/ChatEmoticon.img` 导入、`UIWindow.img` 合并 `ChatExpression` 子树，并做前后节点级校验。

**明确不做的事**

- 不改动原版脸部表情（`FACE_EXPRESSION 0x33` / 物品 5160000–5160014），两者资源与协议独立。
- 不覆盖客户端 `Basic.img`（已确认其自带 `BtClose2` / `VScr`，无需导入）。
- 不在服务端 `wz/Effect.wz` 放 `ChatEmoticon.img.xml`（服务端只读 ID 广播，不读贴图）。
- 未修改任何与聊天表情无关的既有功能。

## 2. 影响范围

| 层级 | 路径/模块 | 变更类型 |
| --- | --- | --- |
| 服务端 | `gms-server/src/main/java/org/gms/net/opcodes/RecvOpcode.java` | 新增 `CHAT_EMOTICON(0x11C)` |
| 服务端 | `gms-server/src/main/java/org/gms/net/opcodes/SendOpcode.java` | 新增 `CHAT_EMOTICON(0x17F)` |
| 服务端 | `gms-server/src/main/java/org/gms/net/PacketProcessor.java` | 注册 `ChatEmoticonHandler` |
| 服务端 | `gms-server/src/main/java/org/gms/util/PacketCreator.java` | 新增 `chatEmoticon(cid, id)` |
| 服务端 | `gms-server/src/main/java/org/gms/net/server/channel/handlers/ChatEmoticonHandler.java` | 新增处理器 |
| i18n | `gms-server/src/main/resources/i18n/log_{zh_CN,en_US}.properties` | 新增 3 条日志文案 |
| 客户端资源 | `E:\MXD\BeiDou-Client_S9\Data\Effect\ChatEmoticon.img` | 新增（9,438,350 B） |
| 客户端资源 | `E:\MXD\BeiDou-Client_S9\Data\UI\UIWindow.img` | 合并 `ChatExpression` 子树（12,476,680 → 12,485,763 B） |
| 备份 | `Data\UI\UIWindow.img.bak_chatemoticon_20260918_065215` | 回滚用 |
| 脚本 | 无 | 无 |
| 管理端/前端 | 无 | 无 |
| DB/配置 | 无（冷却与白名单为代码常量，未引入 GameConfig） | 无 |

## 3. 最终实现逻辑

### 3.1 主流程

```text
客户端（按钮 → 弹窗 → 选中）
   │ 上行 0x11C：int emoticonId
   ▼
ChatEmoticonHandler
   ├─ 角色/地图为空 → 丢弃
   ├─ 载荷 != 4 字节 → WARN 丢弃
   ├─ ID 不在白名单 → WARN 丢弃（绝不广播任意整数）
   ├─ 上次成功时间距今 < 1500ms → DEBUG 忽略（不刷新时间戳）
   └─ 记录时间戳 → 同地图广播 0x17F（含发起者）
   ▼
同地图所有客户端：解析 cid + id → 从 CUserPool 取角色 →
   头顶挂 DialogBox 气泡（GA_STOP, z=2）+ 表情动画层（GA_REPEAT, z=3）→ 3000ms 后 Sweep 隐藏
```

### 3.2 数据 / 协议 / 节点路径与语义

| 项 | 值 | 说明 |
| --- | --- | --- |
| 收包 opcode | `0x011C` | `RecvOpcode.CHAT_EMOTICON`，载荷 `int emoticonId` |
| 发包 opcode | `0x017F` | `SendOpcode.CHAT_EMOTICON`，载荷 `int characterId` + `int emoticonId` |
| 表情 ID 分类 | 1xxxxx→Dynamic0，2xxxxx→Dynamic1，300001–300120→Dynamic2，301000–301002→Dynamic3 | 与客户端 `CategoryForId()` 一致 |
| 动画资源路径 | `Effect/ChatEmoticon.img/Dynamic{分类}/{ID}/effect/{帧}` | 客户端资源管理器读取 |
| 窗口资源路径 | `UI/UIWindow.img/ChatExpression/{backgrnd,DialogBox,open,Tab,Dynamic,ltSlotDisplay,scroll:emote,lbBtOpenOffset,ptWindowOffset}` | 按钮/弹窗/页签 |
| 依赖基础控件 | `UI/Basic.img/BtClose2`、`UI/Basic.img/VScr` | 客户端 Data 已自带，未导入 |
| 冷却 | 1500 ms（`COOLDOWN_MILLIS`） | 与客户端 3 秒动画时长配套 |
| 广播范围 | `broadcastMessage(player, packet, true)` | `repeatToSource=true` → 发起者本人也看到 |

ID 白名单（与客户端 `kDynamic0~kDynamic3` 等价，各段内连续无空洞）：

```text
100001-100010  101001-101010  102001-102024  103001-103007  104001-104005
105001-105004  106001-106006  107001-107006  108000-108027
200001-200005  201001-201004  202001-202004  203001-203006  204001-204006
205001-205006  206001-206006  207001-207006  208000-208003
300001-300120  301000-301002
```

### 3.3 模块与扩展点

- 处理器无状态，冷却表为 `ConcurrentHashMap<Integer, Long>`，条目上限≈在线角色数。
- 若后续需要运营期调冷却/开关，建议改为 `GameConfig` 热配置；当前保持常量以避免牵动既有配置体系。
- 客户端侧收包通道：Kaentake 用 `RegisterCustomPacketHandler(0x17F, ...)`；当前 BeiDou 插件（ijl15/ezorsia）
  已有等价的 `PacketDispatcher::RegisterHandler(opcode, handler)`，可直接注册 `0x17F`（见第 8 节）。

## 4. 资源与同步

- **多语言 / 多端目录**：本次为表情贴图（语言无关），只进客户端 `Data/`；EN 覆盖层只含文本类 img，无需同步。
- **服务端 wz**：未新增 `Effect.wz/ChatEmoticon.img.xml`（服务端不读贴图），与移植文档第 6.2 节一致。
- **密钥（仅名称）**：orange-wz MCP 使用既有的 `GMS` 密钥（iv/userKey 来自本机既有 wz 工具脚本，**不入库**）。
- **格式兼容**：移植包 img 与客户端 Data 内 img 的文件头魔数完全一致（`73 F8 6C 77 FC 79 83 27 19 58 00 00`），
  因此 `ChatEmoticon.img` 可直接拷贝，`ChatExpression` 子树可跨文件 paste。
- **编码**：本次无中文进封包；日志文案走 i18n 资源文件（UTF-8），符合仓库编码分层约定。

## 5. 编码与 i18n

| Key | zh_CN | en_US |
| --- | --- | --- |
| `ChatEmoticon.warn.badLength` | [聊天表情] 收包长度异常 cid={} bytes={} | [ChatEmoticon] invalid payload length cid={} bytes={} |
| `ChatEmoticon.warn.unsupportedId` | [聊天表情] 已忽略未登记的表情ID cid={} id={} | [ChatEmoticon] dropped unregistered emoticon id cid={} id={} |
| `ChatEmoticon.debug.cooldown` | [聊天表情] 冷却期内已忽略 cid={} id={} intervalMs={} | [ChatEmoticon] ignored during cooldown cid={} id={} intervalMs={} |

冷却拒绝属高频正常行为 → `DEBUG`；长度异常/非法 ID 属可审计异常 → `WARN`。

## 6. 产物清单

| 类型 | 路径 | 说明 |
| --- | --- | --- |
| 文档 | `README.md` | 本文件 |
| 笔记 | `notes/IMPLEMENTATION.md` | 实现要点、地址表、决策记录 |
| 证据 | `evidence/ida_static_check.py` / `.out.txt` | PE+capstone 静态校验脚本与输出（可复现） |
| 证据 | `evidence/wz-resource-verify.md` | orange-wz MCP 资源导入前后校验记录 |
| 资源清单 | `resources/RESOURCE_MANIFEST.md` | 源/目标路径、大小、SHA256、备份位置 |

## 7. 验证记录

- **构建**：以 IDEA jbr（JDK 21）作为 `JAVA_HOME`：
  `mvn -o -pl gms-server -Dmaven.test.skip=true package` → **BUILD SUCCESS**，产物 `gms-server/target/BeiDou.jar`。
  （本机默认 `mvn` 挂在 JRE 1.8 上，无法编译本项目的 Java 21 源码，需显式指定 JDK。）
- **未跑 `mvn test`**：仓库 `src/test/java` 的 `XmlSort.java` 引用了 `XmlNode.XmlChild` 上不存在的
  `setX/setY/getType/setValue`，属**既有**失败（本次未触碰 test 源码，`git status` 仅显示一个未跟踪的
  `test/.../doll/` 目录）；且按 `CLAUDE.md`，`CodeGen`/`Xml*` 是开发工具而非 CI 单测，需要本地 MySQL 才能跑。
- **opcode 冲突检查**：`0x11C`（收）与 `0x17F`（发）在改动前的 `RecvOpcode`/`SendOpcode` 中均无占用。
- **IDA/静态校准**（详见 `evidence/ida_static_check.out.txt`）：
  - `0x004965F1` 前 8 字节 == `B8 B0 12 A8 00 E8 9D A5`，与 `kProcessPacketSignature` 完全一致；
  - 7 个硬编码函数入口反汇编均为「`mov eax,const; call 0xA60B98` wrapper + 标准 prolog」或标准 prolog，
    其中 `0x009716ED` 为 `__thiscall` + `ret 4`，与 `GetUserFn` 声明吻合；
  - 4 个全局地址在 `.text` 中均有 ≥8 处立即数交叉引用。
- **资源导入校验**（详见 `evidence/wz-resource-verify.md`）：
  - `UIWindow.img` 一级节点 119 → 120，`added=['ChatExpression']`、`removed=[]`（零丢失）；
  - `ChatEmoticon.img`：`Dynamic0=100 / Dynamic1=47 / Dynamic2=120 / Dynamic3=3`，抽样
    `Dynamic0/100001/effect/0`、`Dynamic2/300001/effect/0`、`Dynamic3/301000/effect/0` 均为 `CANVAS_PROPERTY`；
  - `Basic.img` 自带 `BtClose2` 与 `VScr`。
- **客户端游戏内验证**：待插件接入后执行（步骤见第 8 节与移植文档第 7.2 节）。

## 8. 预留、风险与回滚

**预留项（关键）**：当前 `E:\MXD\BeiDou-Client_S9` 目录内装载的是 BeiDou 自研插件 `ijl15.dll`（工程
`E:\project\BeiDou-ijl15`），**不是 Kaentake DLL**。本次移植包给出的是 Kaentake 的 C++ 源码，因此：

- 服务端与资源已就绪，但「按钮 UI / 上行 0x11C / 渲染 0x17F」这段客户端逻辑尚无人承载；
- 接入路径二选一：
  1. 在 `ezorsia` 新增 `chatemoticon` 模块（`PacketDispatcher::RegisterHandler(0x17F, ...)` +
     状态栏挂按钮 + `Effect/ChatEmoticon.img` 动画层），沿用现有模块化结构；
  2. 换用 Kaentake DLL 时，直接按移植文档合并 9 个 C++ 文件（地址与签名已校验通过）。
- 未做此接入前，玩家不会看到表情按钮与头顶动画；服务端收到 `0x11C` 才会广播，功能无副作用。

**风险与回滚**

| 风险 | 处理 |
| --- | --- |
| `UIWindow.img` 合并破坏既有 UI | 已备份 `UIWindow.img.bak_chatemoticon_20260918_065215`；校验显示一级节点零丢失，回滚即 `copy /Y` 备份 |
| 客户端运行时占用导致写入失败 | 导入前已确认 `BeiDou.exe` 未运行；采用 `save_as` 到 `.new` 再 `os.replace` 原子替换 |
| ID 白名单与客户端数组不一致 | 白名单按客户端 `kDynamic0~kDynamic3` 逐段对齐；资源侧数量已校验一致 |
| 冷却被刷包绕过 | 冷却在白名单校验之后生效，且非法 ID 不刷新时间戳 |
| 冷却表内存增长 | 条目上限≈在线角色数，无清理钩子；如需严格回收可改为角色级字段（未做，避免牵动 Character） |

**回滚步骤**：删除 `Data\Effect\ChatEmoticon.img`；用备份覆盖 `Data\UI\UIWindow.img`；
服务端回退本次 5 个文件改动即可（`ChatEmoticonHandler.java` 为新增文件，直接删除）。

## 9. 对外交流摘要

- 头顶动态聊天表情：服务端收 `0x11C`、广播 `0x17F`，只校验 ID 与 1500ms 冷却后同地图转发，不读贴图。
- 客户端地址与特征码已用静态反汇编逐项核对，与 GMS083 客户端完全吻合（含收包 hook 点 8 字节特征码）。
- 资源：`Effect/ChatEmoticon.img` 直接落盘，`UIWindow.img` 只追加 `ChatExpression` 子树，原有 119 个一级节点零丢失。
- `Basic.img` 已自带 `BtClose2` / `VScr`，无需替换。
- 待补：客户端插件侧接入（当前客户端跑的是 ijl15 而非 Kaentake）。

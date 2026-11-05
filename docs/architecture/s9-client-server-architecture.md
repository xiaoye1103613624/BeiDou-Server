# BeiDou 服务端-客户端一体化架构方案（v0.3 实证稿）

> 取代 v0.2。本版所有关键断言均经代码核验，带 文件:行号；无法核验的标注"待验证"。
> 取证方式：客户端研究员报告（c829e59e，已复核其核心断言）+ 协调者对 s9/S10 仓库的独立核验（研究员A/B报告未送达，其取证目标由直接命令复核替代，见 §12）。
> 三份仓库快照：s9 HEAD=44d0d5d9（branch feature/integration_client）；S10 HEAD=ac5eca0c，merge-base(s9,S10)=42f03863，S10 领先 59 提交 / s9 领先 2；客户端 HEAD 工作区含他人在途未提交修改（Configuration.cpp/.h、Constants.h），行号以当前工作区为准。

---

## 0. 假设清单（同 v0.2，仍有效）

| # | 假设 | 翻案条件 |
|---|------|----------|
| A1 | CCU 约 500 以内单机为主，集群只留接口，第一期不引入 Redis/MQ | 已有多节点需求 |
| A2 | web 管理与游戏服同进程（8686），热更路线，预留 IPC | 要求管理进程独立 |
| A3 | 先定架构，S10 功能分批移植到扩展点 | 个别功能急用 |
| A4 | 本轮交付文档 | 确认后开工 |
| A5 | 客户端允许小侵入（登录包尾字段/分发器/复用内置更新器） | 要求客户端零改动 |
| A6 | 双语结构维持 wz/+wz-zh-CN/ ↔ 客户端资源目录映射 | 语言机制重构 |

> 注（实证修正 A6）：CLAUDE.md 描述的客户端 Data/+EN/ 双层属于 **BeiDou-Client** 仓库；本方案对接的 MapleStory-Client（HeavenClient 系）只有部署位 data.dir 的 16 根资源树（默认 "Data"，Configuration.h:398-400），无 EN/ 覆盖层。双语由服务端侧 wz/+wz-zh-CN/ 合并后打给客户端，客户端无需语言分层。

---

## 1. 实证现状

### 1.1 服务端 s9（核验后）
- 双引擎同进程；REST controller 共 16 个（Account/Auth/AutobanConfig/CashShop/Character/Command/Common/Config/Drop/File/Gachapon/Give/Inventory/Server/Shop…，org/gms/controller）。
- 线程：LoginServer/ChannelServer 用默认参数 NioEventLoopGroup（LoginServer.java:22-23，未限核数）；全局调度走 ThreadManager（Server.java:735 启动/:1698 停止）；初始化用虚拟线程执行器（Server.java:680）；存档有集中点 CharacterAutosaverTask（net/server/task/），saveCharToDB 调用点分布：Character.java×6、Client.java×4、HiredMerchant×2、WeddingHandler×1 等（grep 计数 22 处）——散点触发属实，周期兜底已存在。
- 遗留耦合：getApplicationContext().getBean 反向依赖共 56 处，热点 Server.java×13、Character.java×6、I18nUtil×4、CashShop×4。
- 日志：log4j2.xml 仅 Console+RollingFile（Time+Size 触发），**无 Async appender**——磁盘抖动会阻塞业务线程，v0.1 的"全异步"不是可选项而是补漏。
- 自定义 opcode 先例已存在：SendOpcode.DPT_TRACKER(0x3714)（s9 SendOpcode.java:358），客户端 extended_handlers 已承接 0x37xx（PacketSwitch.h:60,87-96）——**扩展 opcode 的双端通道是现成的**，新特性包不必挤 0..499 号段。
- 版本混淆约束：客户端头混淆含 MAPLEVERSION=83（Cryptography.cpp:55,57），登录包本身无版本字段（LoginPackets.h:25-54 仅账号/密码/skip/hwid 派生）→ 版本协商不能改混淆常数，只能走包内字段。

### 1.2 S10 增量（核验后，59 提交 / java 文件 376 新增+115 修改 / Flyway V1.11.7~V1.11.64 约 58 个新迁移）
按提交日志与新增文件聚类的功能簇：
| 簇 | 证据 |
|----|------|
| 战力排行/装备评分 | org/gms/combat/power/*（CombatPowerCalculator/EquipScoreCalculator）+ V1.11.30；排行即"latestRank"方向 |
| 战斗数值重构 | org/gms/combat/damage|stat|provider/*（CombatStatResolver/Provider 体系，20+ 类）|
| 炼金/锻造/重铸后台 | config/Alchemist*|Forge*|Alchemy* + V1.11.16/35 |
| 活动管理系统 | constants/activity/* + controller/ActivityController + V1.11.27/28 |
| 每日签到+账号体力 | gm0/CheckinCommand + config/StaminaManager + V1.11.14/24/34 |
| 伤害皮肤商城 | client/DamageSkin* + V1.11.12/25/26/41 |
| 武器幻化/Altair 皮肤 | V1.11.39/42/51 + docs/features/altair-skin-look-filter |
| 染色棱镜 | V1.11.18/19/64 |
| 套装 set-item（双语多套） | V1.11.10/53 + docs/features/set-item-admin |
| 宠物成长后台 | config/PetGrowthManager + V1.11.36 |
| 转职/变身器（dice+AP） | creator/novice/AdventurerDiceStats + V1.11.23 |
| 聊天表情（新 opcode 0x11C 上行/0x17F 广播） | S10 RecvOpcode.java:212、SendOpcode.java:367、ChatEmoticonHandler + PacketProcessor.java:281 |
| 美型/潜力/超潜力/天赋 | V1.11.13/15/60/61/62/63 + gm4/PotentialCommand、gm0/BeautyCommand、Pendant2Command |
| 藏包/大数 meso | V1.11.20/21 |
| 限破 | V1.11.29 + 客户端 extended LIMIT_BREAK_SYNC=0x3730 |
| 侧边栏/脚本/系统菜单后台 | V1.11.37/38 + docs/features/sidebar |
| 管理 RBAC/菜单重构/资产中枢 | V1.11.45/46/48/52 —— **S10 已把"web 管理"做出大半** |
| 任务/技能 Web 编辑器 | V1.11.54/55/56 + config/QuestEditor|SkillEditorProperties |
| 商城 xy 窗/坐骑分类/椅子姿势 | V1.11.7/9/31/43/57/59 |
| 工作台监控（服务器+MySQL 图表） | gms-ui workplace monitor（S10 提交 1130a0c6）|

> 移植策略不变（A3）：这些簇是"内容"，本方案的模块 SPI 是"容器"——P2 逐簇落容器，不回填式硬合。注意 S10 与 s9 分叉 59 提交且 s9 自身领先 2（44d0d5d9 等），移植是 cherry-pick+适配而非 merge，冲突集中在 Character/CashShop 热点类（getBean 热点同名类）。

### 1.3 客户端 MapleStory-Client（研究员C报告 + 协调者复核）
- **内置 HTTP 差异更新器（重大利好，v0.2 §4.2 的 launcher 线可直接删掉）**：Util/Patch/{PatchUpdater,Manifest,HttpClient(WinHttp),Sha256}；MapleStory::init() 内、资源装载前同步执行（MapleStory.cpp:104-110）；manifest 契约 {clientMinVersion, channel, baseUrl, files[{path,size,sha256,format}]}（Manifest.h:22-28）；.part→校验→原子 rename；patch.url 空则跳过、失败 allow_skip 可进游戏（xy-config patch.enabled/url/channel/timeout_ms/retries/allow_skip 热读键）。HTTP 走 WinHttp 明文+可配（HttpClient.cpp:92-106）。
- 未知 s2c opcode 行为（**已复核，兼容性好**）：PacketSwitch::forward 无 handler 时仅 warn 不吞包不断连（PacketSwitch.cpp:804-830；Release LOG 为 no-op）→ 新服发扩展段特性包，旧客户端天然静默。仅**畸形长度**才主动断线（Session.cpp:125-131），正常包不受影响。
- 热通道：reload_if_changed() 确在每帧（MapleStory.cpp:240，mtime stat，Configuration.cpp:609-623）；但端到端即时生效仅 ui.language→Locale(gen 重绑, Locale.cpp:78-95 + LocaleBindings.h:65-76) 与 debug.perf_log；server.ip/window.*/data.* 等虽热改值但消费方启动一次性读取，须重连/重启才落行为（C 报告分层结论，采信）。
- 资源加载=16 根目录递归扫描+按 id 惰性寻址（ImgFiles.cpp:41-44,92-113；ImgSource.cpp:241-265,326-393；RootTable.cpp:36-63），新地图/道具/怪物零代码改动，前提落在既有根与前缀类别；例外须重编：新武器大类前缀（WeaponData.cpp:34-37）、MakerRecipes.h 兜底表、StatCaps.h、新 opcode 处理。
- 稳定性：换图 LRU 已修（MAP_KEEP=3，ImgSource.cpp:35,186-225，4 换图 WS 零增长实测）；Template/Cache 无淘汰但有种类天花板；全根预热常驻 ~3.3GB 无自动降级；8h 长跑未做；packet_dump.log / atlas_dbg.log 两个无上限 append（Session.cpp:52-55 等）——生产保持开关关闭并把 append 加上限列入工单。

---

## 2. 架构决策（相对 v0.2 的实证修订）

### 2.1 分发层：不做 launcher，补服务端 manifest 服务
删除 v0.2 §4.2 的"C3 新 launcher"。改为：
1. 服务端新增 PatchController（8686，/patch/v1/manifest）：对 wz 补丁流水线产物生成/签名 manifest（files[{path,size,sha256,format}]+clientMinVersion+channel），静态托管补丁文件（现有 FileController + static 资源体系可承载）。
2. 客户端**零改动**即消费：把 xy-config 的 patch.url 指向该端点即可（字段契约已存在，客户端已实现）。channel 字段可用于测试频道灰度。
3. 发布动作纳入 AdminCommand：PublishPatch（生成 manifest→原子替换→WS 通知在线管理端），wz ADD 子树预检（客户端目标 img 同名节点存在性检查）固化进生成步骤。
4. 遗留核对项：manifest.files.path 相对基准与客户端 data.dir 落盘规则的映射（读 Util/Patch/Manifest.cpp 确认，P1 第一天做）；WinHttp 默认 http 明文，内网可用，公网发布走反代 TLS（部署项，非代码项）。
- 游戏内热替换（文本类 img 空闲重载）保留为 C2 增量，范围按 §1.3 消费端核实结果定，收益排序降级到 manifest 通道之后。

### 2.2 契约层：protocol/ 仍做，但握手方案改
- 因 MAPLEVERSION=83 参与头混淆（不动），且登录包无版本字段：clientBuild + capBitmap 作为**登录包尾追加字段**（LoginPackets.h 写侧追加，服务端 InPacket 顺序读、旧服读不到尾字段即忽略——双向兼容由读取语义天然保证）。
- 特性包一律走扩展段（≥0x37xx，先例 DPT_TRACKER/LIMIT_BREAK_SYNC）：客户端 extended_handlers 现成、未知即静默（§1.3 已证），能力协商从"兼容必需"降级为"服务端省带宽的开关"。
- protocol/ 目录 + 双端 codegen 不变（opcode.json/version.json/capability.json/errorcode.json）；s9 的 Recv/SendOpcode.java 与客户端 PacketSwitch 枚举表就是抽取源。
- s9 侧待验证一项：PacketProcessor（服务端入站分发）对未注册 opcode 的行为（断连/忽略），影响旧客户端连新服的健壮性——P0 第一天读该文件补上（本会话核验到 import 层，未到 default 分支）。

### 2.3 服务端核心：v0.1 全部维持，优先级按实证微调
- 保留：内存权威/单写者 Command Bus/journal+write-behind/资产白名单/登录装载批量化/断线清理幂等链。
- 新增确定项（原为空位）：
  - CharacterAutosaverTask 已是集中周期存档点 → write-behind 改造以此为切入（先合并其逐角色 save 为批量快照，再上 dirty 位图），试点风险比预估低。
  - log4j2 无 Async：P0-4 明确为"加 AsyncAppender + RingBuffer"，非新建观测体系。
  - NioEventLoopGroup 默认参数：P0-1 顺手钉死核数并禁止 handler 内阻塞（配 BlockHound 或指标验收）。
  - getBean 56 处/热点 Server.java：模块 SPI 首批解耦对象即这 4 个热点类。
- 管理面：gms-ui 侧 S10 已有 RBAC/菜单/监控台雏形（V1.11.38/45/46/52 + workplace monitor），s9 的 web 管理**不必从零**：P1 把 S10 后台簇作为第一批移植对象，与其玩法簇解耦（它们依赖的 combat/power 等可后行）。

### 2.4 长期稳定（双端工单化）
- 服务端：MemoryRegistry（getBean 热点类与 static 集合审计）、Async 日志、GraalVM per-script Context+超时（Server.java:91 已关警告，Context 复用策略待 P0-4 读 scripting 层定案）、micrometer 指标（executor 队列深度/命令 P99/journal 水位/快照耗时/GC）。
- 客户端：manifest 通道的回滚=保留上一版本文件（PatchUpdater 已原子替换，补保留逻辑一处）；packet_dump/atlas_dbg append 加上限；8h 长跑与 GPU 内存曲线列为 P1 验收项（perf README §9.2 自述未跑项）；3.3GB 全根预热的 LRU 退路按机型内存分档（低配机型只预热 boot+charselect 根，ensure 时补）。

---

## 3. 修订后分阶段路线

**P0 地基（1~2 周）** —— 与 v0.2 一致，切入顺序按实证更新：
1. protocol/ 抽取（双端常量逆向整理，不改行为）；同日补两项核验：s9 PacketProcessor 未注册 opcode 行为、Manifest.cpp 路径基准。
2. Command Bus + map/player 执行器骨架（试点=交易/背包 + 以 CharacterAutosaverTask 为切入的批量快照合并）。
3. journal + write-behind + 资产白名单；登录装载批量化；断线清理幂等链。
4. log4j2 Async + micrometer + MemoryRegistry 静态集合审计 + EventLoop 参数钉死。
- 验收：500 机器人在线 30 分钟，DB 写 QPS 降 ≥80%；逻辑线程零同步 DB 等待；RSS 走平；protocol 双端 codegen CI 绿。

**P1 热更与分发（2~3 周）**：
1. GameConfig 回调矩阵补齐；AdminCommand+审计+WS；
2. ReloadScript/ReloadWz/Flyway 在线迁移；引擎滚动产品化（JVM 不重启，Netty 引擎拆建——现成 startServer/stopServer 补优雅下线序列）；
3. **PatchController manifest 服务 + wz 补丁流水线 CI（ADD 预检）+ xy-config patch.url 对接**（替代 launcher 主线）；
4. 登录包尾字段（clientBuild/capBitmap/locale）双端 codegen；S10 web 管理簇（RBAC/菜单/监控）第一批移植。
- 验收：改参数/脚本/加表零重启零分发；wz 提交→manifest 发布→客户端下次启动自动更新全链无人肉拷贝；旧客户端连新服回归通过（含未知扩展包静默用例）。

**P2 模块 SPI + S10 玩法簇移植（按 §1.2 簇表排批次）**：
基础设施簇先行（排行/签到/体力/活动管理/后台编辑器）→ 玩法簇（战力/炼金锻造/皮肤幻化/表情/转职/潜力天赋）→ 商城簇。每簇一张任务书 + capability 声明 + 对应 C5 客户端 UI 批次（表情等需客户端接收实现的除外，走 manifest 不可达，需客户端版本）。
- 验收：每簇合入后核心 org.gms 目录 diff 仅注册点；能力位灰度秒级隐身回滚。

**P3 可选集群**：Redis 会话/广播 → world 分片 → 独立网关（触发才做）。

---

## 4. 风险表（实证更新）

| 风险 | 状态 | 处置 |
|------|------|------|
| 旧客户端遇未知包断连 | **已排除**：PacketSwitch.cpp:804-830 warn-only；仅畸形长度断线（Session.cpp:125-131） | 扩展段发新包即安全；P1 回归仍加此用例 |
| 服务端对未知入站包行为不明 | 待验证（P0 第一天） | 读 PacketProcessor default 分支；若断连改为忽略+计数 |
| manifest path 基准/落盘规则与客户端 data.dir 映射 | 待验证（读 Manifest.cpp） | P0 核验，P1 对接 |
| 热更覆盖假象：xy-config 大多数键热改不生效 | 已坐实（消费方启动一次性读取） | 方案不依赖客户端配置热生效；只依赖 PatchUpdater 启动前同步 |
| 客户端 3.3GB 常驻/8h 长跑未证 | 已记录（perf §9.1/§9.2） | P1 客户端验收项：长跑+分档预热 |
| append 日志无上限（packet_dump/atlas_dbg） | 已定位（Session.cpp:52-55 等） | 小补丁工单，P1 前完成 |
| S10 移植的 Character/CashShop 冲突 | 分叉 59 提交、热点同名类 | cherry-pick+适配，不 merge；模块 SPI 收敛写路径后再动热点 |
| write-behind 崩溃丢快照 | 同 v0.2 | 资产白名单同步+触发点快照 |
| 假设 A1/A2/A5 翻案 | 同 v0.2 | 只动 P1/P2 边界，不动 P0 |

---

## 5. 取证账本（R6/R7 留痕）

- 研究员C（c829e59e）：报告送达，A/B 两断言经协调者独立复核推翻或修正——"无 HTTP/TLS 通道"不成立（Util/Patch/HttpClient.cpp WinHttp 存在）；"Data/EN/ 双层"属 BeiDou-Client 而非本仓库。其余核心断言（PatchUpdater、每帧 reload、warn-only 分发、MAP_KEEP）经 文件:行号 复核通过，**采信**。
- 研究员A/B：完成通知未送达本会话（派发跨越中断，可能未起或结果丢失）。其取证目标（S10 差异清单、s9 热点计数）已由协调者直接命令复核替代，§1.1/§1.2 即其成果；未复核完的两项转入 P0 待验清单（见 §4）。按 R6 不为"未收到的报告"重派——事实已在手。
- v0.1 文档继续作为服务端内部设计细则（线程/存储/命令层），本 v0.3 为双端总纲；冲突处以 v0.3 实证结论为准。

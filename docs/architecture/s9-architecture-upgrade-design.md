# BeiDou s9 架构升级设计方案（v0.1 草案）

> 状态：待确认稿。访谈未回收的决策按【假设清单】推进，翻案条件逐条标注。
> 范围：gms-server（Java 21 + Spring Boot 3 + Netty 双引擎同进程）。前端 gms-ui 仅涉及对接协议，不改技术栈。

---

## 0. 假设清单（本轮推进基准）

| # | 假设 | 影响 | 翻案条件 |
|---|------|------|----------|
| A1 | CCU 约 500 以内，集群只留接口、第一期不引入 Redis/MQ 依赖 | 决定 P0/P1 全部进程内实现 | 你已有或确定要有多节点部署 |
| A2 | web 管理与游戏服同进程（沿用 8686），走配置/脚本/资源热更路线，预留 IPC 抽象 | 决定少重启的实现方式 | 你要求管理后台与游戏进程独立存活 |
| A3 | 先定架构，S10 功能按清单分批移植到新扩展点 | 决定移植节奏 | 某几个功能急用，需单独通道 |
| A4 | 本轮交付文档，不动代码 | — | 你确认后立即开工 P0 |

---

## 1. 现状与问题定位

双引擎同进程：Spring Boot（REST 8686，管理面）+ Netty 游戏服（Login/Channel，业务面），ServerManager 桥接，遗留 org.gms.net.server.Server 单例经 getApplicationContext().getBean(...) 反向取 Spring 依赖（分布密度待研究员B给出计数证据）。

对应你提出的四个诉求，痛点归因：

1. **数据库查询压力 / 响应慢** → 游戏逻辑与 DB 之间没有分层缓冲：角色存档走散点 UPDATE（实际路径是 UPDATE 版 saveCharToDB），触发时机多、粒度碎；静态数据虽常驻内存但缺统一生命周期管理。
2. **修改数据影响客户端 / 不一致** → 管理端与游戏逻辑是两条写路径（web 直写库、游戏内存里还有一份活的），没有单一写者约束，改了库不知道、内存覆盖回库、客户端收不到同步包。
3. **重启次数多** → 热更覆盖面只有 GameConfig 的一部分参数；脚本、wz、DB 结构变更、新 bean 都会升级为整 JVM 重启；/server/v1/restartServer 只重启 Netty 引擎这一层没有被产品化利用。
4. **长期运行卡顿 / 内存问题** → 静态集合只增不减（channel map、cooldown、监听注册等待核实）、GraalVM Context 复用方式、WZ 缓存无淘汰策略，缺指标与巡检手段。

---

## 2. 目标架构总览

```text
+------------------------ 单一 JVM（P0/P1）------------------------+
|  Spring Boot 管理面(8686)               Netty 游戏面            |
|  [REST/WS 管理API][Auth/限流/审计]      [LoginServer][Channel]  |
|            \                        /  编解码/加密后即投递命令     |
|             v                       v                          |
|  +------------------ Command Bus（唯一写入口，单写者）----------+ |
|  |  PlayerCmd / MapCmd / WorldCmd / AdminCmd（web 也走这里）   | |
|  +----------+-----------------------------------+-------------+ |
|             v                                   v                 |
|  [游戏逻辑执行层]                     [ModuleRegistry 模块注册中心]  |
|   每 map 一个逻辑域(单线程队列)          玩法/商城/社交/活动 = Module |
|   权威状态全在内存                     (handler+config+脚本+迁移)  |
|             v                                                      |
|  [Persistence 层]                        [HotReload 层]             |
|   journal(同步内存记账)                   Config热更/脚本热替换/      |
|   write-behind 快照+合并(异步批量)         wz重载/Flyway在线迁移      |
|             v                                                      |
+-------------|------------------------------------------------------+
              v
          MySQL 8 (beidou)   <- P2 集群期插入: Redis(会话/广播)+消息总线适配
```

三条铁律（整个方案的地基）：

- **内存权威**：在线玩家/地图对象是唯一真相，DB 是投影。读路径 = 内存命中优先，web 读走只读缓存。
- **单写者**：一切变更（游戏包、定时器、GM 命令、web 后台）收敛为 Command，按玩家/map 串行执行；web 永远不直接 UPDATE 在线实体。
- **异步落库、同步记账**：变更先写内存 journal（追加，微秒级），快照批量异步刷库；崩溃最多丢一个 flush 周期的快照，journal 可重放校验。

---

## 3. 核心设计

### 3.1 线程模型：map 为最小并发单元

- Netty EventLoop 只做**编解码 + 命令入队**，不做业务（现状若 EventLoop 上有业务 handler，改为投递后即返回）。
- 逻辑执行：MapExecutor（每个活跃地图一个单线程队列）+ PlayerExecutor（玩家个人级命令，如背包/商店），避免地图内并发锁。跨 map 交互（组队、交易、跨图传送）经 WorldCoordinator 交换快照，不共享可变对象。
- 全局限时任务（tick、回城、活动）挂 GameScheduler，任务体只发 Command，不直接摸对象。
- 与遗留 Server 单例的关系：Server 保留为壳，init/destroy 分别装配/卸载上述执行器；遗留代码逐步把散落的直接调用替换为命令投递（按模块灰度，不要求一次到位）。

### 3.2 读路径：单节点减少 DB 查询

| 数据 | 策略 | 备注 |
|------|------|------|
| 在线玩家 | 纯内存，0 查询 | 登录一次性全量装载（扩展表合并为 2~3 条批量 SQL） |
| 离线玩家（web 查看） | Caffeine 只读缓存 + 版本号失效 | TTL 30~120s，管理场景可接受最终一致 |
| 静态数据（map/wz/skill/item 定义） | 启动常驻 + 分代 LRU | 见 3.5 资源重载 |
| 全局配置 | GameConfig 内存树（已有） | 扩大热更覆盖面，见 3.4 |

登录装载优化：角色相关表（character / birthday / summon_skills / 各 ExtendValue...）合并为按 characterid 的 IN 批量装载，登录查询次数大幅下降。

### 3.3 写路径：journal + write-behind，一致性与低查询兼得

```java
// 变更即记入内存 journal（对象级 dirty 字段位图），O(1)
player.markDirty(CharacterField.EXP, CharacterField.MESO);
journal.append(new FieldDelta(charId, EXP, newValue, seq++));

// PersistenceWorker（独立线程池）周期性做三件事：
// 1) 合并：同一字段多次变更取末值（一个快照周期内练级 200 次 = 1 条 UPDATE）
// 2) 批量：按表攒批 multi-row UPDATE（rewriteBatchedStatements=true）
// 3) 落库：写成功后截断对应 journal 段
```

- **强制即时落库白名单**（不走快照、直接同步事务）：现金交易、商城购买、寄售成交、公会资金、账号充值——钱和不可替代资产永远同步一致。
- **触发点**：登出、切频道、传送跨图、被踢，各触发一次该玩家快照；按 dirty 位图生成列级 UPDATE（比现状全字段大 UPDATE 更省）。
- 一致性验证：journal 序号 + 快照水位线；崩溃恢复时比对 journal 尾段与库值，差量重放（文件级 WAL 留 P1/P2，P0 的内存 journal + 登出即快照已保证正常运维零丢失）。
- 效果预估：500 在线常规练级，角色表写入从每次状态变化一条降到**每角色每 30s 不超过 5 条列级 UPDATE**（再按批合并 DB 往返），写 QPS 下降一个数量级。

### 3.4 修改通道：web 管理经 AdminCommand，客户端无感

web 不再直写在线实体，统一发命令：

```java
public record AdminCommand<T>(
    long id, String issuer,        // GM/审计
    CommandTarget target,          // PLAYER(charId) | MAP | WORLD | CONFIG
    AdminAction<T> action,       // SetExpRate / GiveItem / BanPlayer / ReloadScript...
    long issuedAt
) {}
```

- 目标玩家**在线** → 命令路由进其 PlayerExecutor/所在 MapExecutor，与游戏逻辑同队列串行执行，天然无竞态；执行完内存生效 + dirty 标记 + journal，快照照常落库；需要玩家可见时由命令处理器顺手发包（属性变化、公告），客户端全程无感、无需重连。
- 目标玩家**离线** → Persistence 层直接写库 + 缓存版本号失效，下次登录装载拿到新值。
- **参数类修改**（倍率、掉率、商城折扣）→ 只发 CONFIG 命令，走 GameConfig 热重载回调矩阵（3.5），根本不碰玩家行数据。
- 管理端拿执行回执：ResultBody 扩展 appliedAt / executorState，WS 推送长任务进度（见 3.6）。

### 3.5 热更新矩阵（少重启的核心交付）

| 变更类型 | 现状 | 目标 | 机制 |
|----------|------|------|------|
| game_config 运营参数 | 部分热更 | **全量热更** | GameConfig.update + 订阅回调 ConfigChangeListener；世界倍率类即时写回 World（已有），补齐 NPC 商店价、任务奖励、活动参数等消费端订阅 |
| NPC/quest/portal 等 .js 脚本 | 改动需重启或全量重载 | **按脚本热替换** | ScriptEngine 缓存 key 加版本戳；ReloadScript AdminCommand 失效该 key、下次调用重建 Context（GraalVM per-script Context）；重载失败自动回滚旧版 |
| wz/XML 资源 | 启动全量 | **按 img 重载** | provider/wz 加 LRU + 失效接口；ReloadWz(imgPath) 命令清对应缓存项，下次访问重解析 |
| DB 表结构 | Flyway 启动跑 | **在线迁移** | expand-contract：加列/加表允许运行时 Flyway migrate（管理端触发，带备份检查点）；删列拆两步发版；MyBatis-Flex 实体对新列默认忽略 |
| Java 代码 | 整 JVM 重启 | **引擎级滚动** | 利用现有 startServer/stopServer：JVM 不重启、只拆建 Netty 引擎 + 模块重装。优雅下线：广播 -> 禁止登录 -> 限时快照 -> 停引擎 -> 换模块 -> 起引擎 -> 放开登录。诚实边界：Java 类热替换不做（classloader 魔法在这个规模是负债），多进程滚动留给 P2 |

预期：日常运维（调参数、改脚本、改资源、加表）从零重启解决；Java 发版从直接 kill 降为一次受控引擎滚动（分钟级、玩家有预告、资产零丢失）。

### 3.6 Web 管理对接层（对接更多客户端）

- REST（现有 8686 + JWT）保持，新增：
  - **WebSocket /admin/v1/ws**：在线人数、引擎状态、命令回执、告警推送；GM 工具、web 面板、移动端共用，管理端不需轮询。
  - **OpenAPI 契约**：管理接口带版本段（沿用 ApiConstant 机制），供第三方工具对接。
- 管理面与游戏面资源隔离：REST 线程池/连接数独立；慢查询（日志检索、统计报表）走只读缓存视图，禁止摸在线对象内部。
- 审计：AdminCommand 全量入 admin_command_log 表（who/when/target/参数/结果），这是多客户端接入后的安全底线。
- IPC 预留（按假设 A2 第一期不实现）：CommandBus 接口与实现分离，将来独立管理进程只需新增一种 RemoteCommandTransport，命令模型不变。

### 3.7 长期稳定：内存与卡顿治理清单

1. **静态集合审计落地**（P0 即做，研究员B清单为输入）：每张常驻 Map 注册进 MemoryRegistry，声明容量上限与淘汰策略；无主集合直接删。
2. 断线清理链路：channel 关闭 -> PlayerExecutor 注销 -> 地图对象移除 -> journal 终快照，四步做成**幂等 finally 链**，任何一步失败进补偿队列而不是静默吞掉。
3. GraalVM：per-script Context + 执行超时选项，脚本内长任务禁止；Context close 时机绑定脚本重载。
4. WZ：常驻上限 + LRU；怪物 stats、掉落表这类高频小对象用原始类型数组/紧凑结构（视 GC 数据决定）。
5. 观测：micrometer 指标（各 executor 队列深度、命令延迟 P99、journal 长度、快照耗时、DB 批大小、GC 暂停）-> 现有 web 面板出图；log4j2 全异步 appender + 按天/按大小滚动。
6. 卡顿定位：队列深度指标天然暴露哪个 map/哪类命令堵了，比事后 thread dump 便宜得多。

---

## 4. 集群演进路径（假设 A1：只留接口，不提前建设）

| 阶段 | 形态 | 改动 |
|------|------|------|
| S0（本方案 P0/P1） | 单 JVM 双引擎 | 见上 |
| S1 | 单 JVM + 外置会话 | CommandBus 加 Redis 实现：登录票据、跨节点广播（公告/好友状态）走 pub/sub；其余不变 |
| S2 | 多节点分片 | **world = 分片单元**：每 world 权威于一个节点（单写者原则跨进程仍成立，不需要分布式事务）；Gateway/LoginServer 独立进程做接入与票据签发；节点间只走命令与事件，不共享对象 |

内存权威 + 命令总线 + world 单主，是这套设计能平滑走到 S2 的全部前提——所以 P0 就要守住两条线：web 不直写在线实体、逻辑不跨 map 直接引用。其余都可后补。

---

## 5. 分阶段实施路线

**P0 地基（不动功能，1~2 周量级）**
1. Command Bus + Map/Player Executor 骨架（先在交易/背包两个模块试点，包处理器投递）
2. journal + write-behind 快照 + dirty 位图（替换散点 saveCharToDB 触发；保留资产白名单同步事务）
3. 登录装载批量化；断线清理幂等链
4. MemoryRegistry 静态集合审计 + micrometer 指标 + 异步日志
- 验收：500 假机器人在线 30 分钟，DB 写入 QPS 较基线下降 80% 以上；逻辑线程 0 次同步 DB 等待（指标证明）；RSS 曲线走平。

**P1 热更与扩展（2~3 周量级）**
1. GameConfig 订阅回调矩阵补齐；AdminCommand 通道 + 审计表 + WS 推送
2. 脚本按文件热替换 + 失败回滚；wz 按 img 重载；Flyway 在线迁移入口
3. ModuleRegistry：功能模块化 SPI（handler + config schema + 脚本包 + 迁移 + API 声明为一个模块）
- 验收：调倍率/改 NPC 脚本/加一张运营表，全程零重启且客户端无感；引擎滚动一次实测资产零丢失。

**P2 S10 功能移植 + 集群预留**
1. 按研究员A清单，将 S10 增量逐个落进模块 SPI（每个功能一张任务书，先基础设施类，再玩法类）
2. 可选：Redis 会话/广播（触发条件：需要多节点或独立管理进程）
- 验收：每移植一个功能，核心 org.gms 目录 diff 为零或仅注册点。

---

## 6. 风险与回滚

| 风险 | 缓解 |
|------|------|
| write-behind 崩溃丢最后一段快照 | 资产白名单同步落库 + 触发点快照；journal 文件化留后续，正常运维登出即快照零丢失 |
| 遗留代码命令化改造不彻底，新旧写路径并存 | 试点模块先收口，未收口模块保持现状原样跑（执行器只接管投递过命令的路径），灰度推进 |
| 热替换脚本引发运行时异常 | 双版本 Context，重载失败秒回旧版；AdminCommand 有回执与回滚命令 |
| 引擎级滚动期间玩家掉线 | 预告 + 限时快照 + 客户端自动重连指引；滚动窗口目标 60s 内 |
| 假设 A1/A2 与真实意图不符 | 0 节逐条给翻案条件；任何一条改动只影响 P1/P2 边界，不动 P0 地基 |

---

## 7. 待研究员证据回填的空位

- [ ] S10 功能清单与架构差异（研究员A）-> 回填 5-P2 移植批次表
- [ ] s9 EventLoop 上业务调用点计数（研究员B）-> 决定 P0-1 试点模块选择
- [ ] saveCharToDB 实际触发点清单 -> 基线 QPS 估算
- [ ] getBean 反向依赖热点类 -> 模块 SPI 首批解耦对象
- [ ] 疑似只增不减的静态集合清单 -> P0-4 审计工单

# BeiDou Server 架构深度优化方案

> 基于 2026-06-27 全量代码审查（Netty网络层、数据库访问层、并发模型、内存管理、启动流程），
> 覆盖十大维度，含具体代码行号和实施步骤。

---

## 执行摘要

**当前最大瓶颈排序**：Netty 缓冲区无池化 > 数据库缺索引+N+1查询 > ScriptEngine 无池化 > 包处理阻塞 I/O 线程 > Druid 连接池默认配置

**预期效果**：内存占用降低 50-70%，网络延迟降低 50%，数据库查询减少 60%+，STW 暂停降低 60%，支持 7×24 长时间运行无需重启。

---

## 一、Netty 网络层深度优化

### 1.1 问题总览

当前 Netty 层存在 **11 个可优化点**，其中 4 个为高优先级：

| 优先级 | 问题 | 影响 |
|--------|------|------|
| **P0** | 全链路 `Unpooled.buffer()` — 零池化 | 每个数据包触发堆内分配 + GC |
| **P0** | `TCP_NODELAY` 未设置 — Nagle 算法生效 | 小包延迟增加 40ms |
| **P0** | `sendPacket()` 公平锁序列化所有写 | 并发发包被强制排队 |
| **P0** | 数据包构造无容量提示 | buffer 反复扩容复制 |
| P1 | 加密循环内 `multiplyBytes()` 分配 | per-crypt GC 负载 |
| P1 | 每个字符串 I/O 扫描 Charset 枚举 | CPU 浪费 |
| P1 | 编码路径 `getBytes()` 额外复制 | 每包多一次 byte[] 分配 |

### 1.2 P0-1: 启用 ByteBuf 池化

**现状** (`ByteBufOutPacket.java:26-27`):
```java
// 所有出站包的起点 — 无池化
this.byteBuf = Unpooled.buffer();
```

**问题链**：
1. `PacketCreator` 483 个方法全部调用 `OutPacket.create()` → `new ByteBufOutPacket()` → `Unpooled.buffer()`
2. `GMSV83PacketProtocol.encode()` 从 ByteBuf 提取 `byte[]` → 再写入输出 ByteBuf
3. 每个包经历：创建 Unpooled ByteBuf → 写入数据 → 提取 byte[] → 加密 → 写进输出 ByteBuf → 释放
4. 高频操作（移动、攻击、拾取）每秒可能产生几十个包

**修复** (`ByteBufOutPacket.java`):
```java
public ByteBufOutPacket() {
    // 使用池化分配器，100字节初始容量（多数包在此范围内）
    this.byteBuf = PooledByteBufAllocator.DEFAULT.buffer(100);
}

public ByteBufOutPacket(SendOpcode op, int initialCapacity) {
    this.byteBuf = PooledByteBufAllocator.DEFAULT.buffer(initialCapacity);
    writeShortLE(op.getValue());
}
```

**修复** (`LoginServer.java:28` 和 `ChannelServer.java:31`):
```java
ServerBootstrap bootstrap = new ServerBootstrap()
    .group(parentGroup, childGroup)
    .channel(NioServerSocketChannel.class)
    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)  // ← 新增
    .childOption(ChannelOption.TCP_NODELAY, true)   // ← 新增，禁用 Nagle
    .childOption(ChannelOption.SO_KEEPALIVE, true)   // ← 新增
    .childHandler(...);
```

### 1.3 P0-2: sendPacket 锁优化

**现状** (`Client.java:1542-1549`):
```java
public void sendPacket(Packet packet) {
    announcerLock.lock();       // ← 公平锁！强制 FIFO 排队
    try {
        ioChannel.writeAndFlush(packet);
    } finally {
        announcerLock.unlock();
    }
}
```

**问题**: Netty 的 `Channel.writeAndFlush()` 已是线程安全的（内部排队到 EventLoop），外层再加重入公平锁是完全多余的。公平锁在竞争时触发线程切换，开销极大。

**修复**: 直接移除锁
```java
public void sendPacket(Packet packet) {
    // Netty Channel 内部已是线程安全的，无需额外加锁
    ioChannel.writeAndFlush(packet);
}
```
或者在极端情况下需要保证包顺序时，改用 `Channel.write()` + `Channel.flush()` 合并：
```java
public void sendPacket(Packet packet) {
    ioChannel.write(packet);          // 不立即 flush
    // 在适当时机批量 flush，如 enableActions() 中
}
```

### 1.4 P0-3: 包容量预分配

**现状** (`PacketCreator.java` 483个方法无一指定初始容量):
```java
OutPacket p = OutPacket.create(SendOpcode.SET_FIELD);  // ← 默认 256 字节
// addCharInfo 可能写入数千字节，触发 5-10 次扩容+复制
```

**修复** — 大包指定容量:
```java
// SET_FIELD — 角色数据，通常 2-8KB
OutPacket p = OutPacket.create(SendOpcode.SET_FIELD, 8192);

// SHOW_FOREIGN_EFFECT — 小包
OutPacket p = OutPacket.create(SendOpcode.SHOW_FOREIGN_EFFECT, 64);
```

将 `PacketCreator` 中 20 个最频繁调用的方法加上容量提示即可覆盖 80% 流量。

### 1.5 P1-4: 加密循环去分配

**现状** (`MapleAESOFB.java:100-107,114`):
```java
// crypt() 方法的 while 循环内每次迭代
byte[] myIv = multiplyBytes(this.iv, 4, 4);  // ← 分配 new byte[16]

private static byte[] multiplyBytes(byte[] in, int count, int mul) {
    byte[] ret = new byte[count * mul];       // ← 每次分配
    for (int x = 0; x < ret.length; x++) {
        ret[x] = in[x % count];
    }
    return ret;
}
```

**修复** — 预分配复用:
```java
// 类字段，一次性分配
private final byte[] myIvBuffer = new byte[16];

// crypt() 中
System.arraycopy(this.iv, 0, myIvBuffer, 0, 4);
System.arraycopy(this.iv, 0, myIvBuffer, 4, 4);
System.arraycopy(this.iv, 0, myIvBuffer, 8, 4);
System.arraycopy(this.iv, 0, myIvBuffer, 12, 4);
// 直接用 myIvBuffer 替代 multiplyBytes() 返回值
```

### 1.6 P1-5: Charset 查找缓存

**现状** (`CharsetConstants.java:27`):
```java
public static Charset getCharset(int language) {
    return Charset.forName(Language.fromLang(language).getCharset());
    // Language.fromLang() 遍历所有枚举值 — 每个字符串调用一次！
}
```
每个带字符串的数据包调用此方法 1-10 次。一秒内可能调用数百次。

**修复**:
```java
// 启动时预计算
private static final Charset[] CHARSET_CACHE = new Charset[Language.values().length];
static {
    for (Language lang : Language.values()) {
        CHARSET_CACHE[lang.ordinal()] = Charset.forName(lang.getCharset());
    }
}

public static Charset getCharset(int language) {
    return CHARSET_CACHE[language];  // O(1) 数组查找
}
```

### 1.7 其他 Netty 优化

- **ThreadLocalUtil 去 Optional** (`ThreadLocalUtil.java:45`): `Optional.ofNullable(...).map(...).orElse(0)` → 直接 null 检查
- **LoggingUtil 去 ByteBuf** (`LoggingUtil.java:19`): `Unpooled.wrappedBuffer(bytes).readShortLE()` → 直接位运算
- **PacketProcessor 数组改 Map**: `new PacketHandler[14100]` (90%+ 为 null) → `Map<Short, PacketHandler>`
- **ReplayingDecoder 改 ByteToMessageDecoder** (`PacketDecoder.java:11`): 消除 Signal 异常开销

---

## 二、数据库层深度优化

### 2.1 问题总览

| 优先级 | 问题 | 文件:行号 |
|--------|------|-----------|
| **P0** | `CharacterService.saveCharToDB()` 用 INSERT 而非 UPDATE | `CharacterService.java:435` |
| **P0** | `Pet.saveToDb()` 在事务内部打开新连接 | `Character.java:8123` |
| **P0** | `ItemFactory` 全量逐行 INSERT 无批处理 | `ItemFactory.java:319-361` |
| **P0** | 15+ 个子表缺索引 | 多个 Flyway 迁移文件 |
| P1 | 商人物品加载 N+1 查询 | `ItemFactory.java:396-404` |
| P1 | `CashShop.save()` 心愿单逐行插入 (有 TODO) | `CashShop.java:651` |
| P1 | `CharacterAutosaverTask` 串行保存 | `CharacterAutosaverTask.java:50-54` |
| P1 | 每次登录 ~25 个独立查询 | `Character.java:6716-6850` |

### 2.2 P0-1: saveCharToDB INSERT/UPDATE 确认

**现状** (`CharacterService.java:435`):
```java
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_UNCOMMITTED)
public void saveCharToDB(Character player, boolean notAutosave) {
    CharactersDO cdo = Character.toCharactersDO(player);
    charactersMapper.insertSelective(cdo);   // ← 这是 INSERT！
}
```

需要确认 MyBatis-Flex 的 `insertSelective` 是否有 `ON DUPLICATE KEY UPDATE` 行为。如果没有，应该改为 `updateById` 或明确使用 `insertOrUpdate`。

### 2.3 P0-2: ItemFactory 批处理改造

**现状** (`ItemFactory.java:286-364`):
```
DELETE 所有物品 → for (每个物品) { psItem.executeUpdate() }  // 100 次网络往返
                 → for (每个装备) { psEquip.executeUpdate() } // 50 次网络往返
```

**修复**:
```java
// saveItemsCommon() 改造
try (PreparedStatement psItem = con.prepareStatement(INSERT_ITEM_SQL)) {
    for (Item item : items) {
        psItem.setInt(1, itemId);
        // ... set all params
        psItem.addBatch();              // ← 收集到批次
    }
    psItem.executeBatch();              // ← 一次网络往返
}
// 装备插入同理
```

预期效果：100 个物品的保存从 150 次网络往返降到 2 次。

### 2.4 P0-3: 缺失索引补充

从 Flyway 迁移文件分析，以下表缺少高频查询需要的索引：

```sql
-- 物品相关 (每次保存/加载都用到)
ALTER TABLE inventoryitems ADD INDEX idx_type_charid (type, characterid);

-- 宠物 (每次登录加载)
ALTER TABLE pets ADD INDEX idx_petid (petid);

-- 角色子表 (每次登录都查)
ALTER TABLE skills ADD INDEX idx_characterid (characterid);
ALTER TABLE keymap ADD INDEX idx_characterid (characterid);
ALTER TABLE skillmacros ADD INDEX idx_characterid (characterid);
ALTER TABLE savedlocations ADD INDEX idx_characterid (characterid);
ALTER TABLE trocklocations ADD INDEX idx_characterid (characterid);

-- 任务 (登录+保存都用到)
ALTER TABLE queststatus ADD INDEX idx_characterid (characterid);
ALTER TABLE questprogress ADD INDEX idx_queststatusid (queststatusid);
ALTER TABLE questprogress ADD INDEX idx_characterid (characterid);

-- 好友
ALTER TABLE buddies ADD INDEX idx_characterid (characterid);
ALTER TABLE famelog ADD INDEX idx_characterid_date (characterid, famelog_when);

-- 其它
ALTER TABLE area_info ADD INDEX idx_charid (charid);
ALTER TABLE eventstats ADD INDEX idx_characterid (characterid);
ALTER TABLE cooldowns ADD INDEX idx_charid (charid);
ALTER TABLE playerdiseases ADD INDEX idx_charid (charid);
ALTER TABLE characters ADD UNIQUE INDEX idx_name (name);  -- 名字查重
```

### 2.5 P1-4: 商人物品 N+1 修复

**现状** (`ItemFactory.java:394-404`):
```java
while (rs.next()) {
    // 主查询加载每个物品后...
    try (PreparedStatement psBundle = con.prepareStatement(
            "SELECT bundles FROM inventorymerchant WHERE inventoryitemid = ?")) {
        // ← 每个物品一次额外查询！
    }
}
```

**修复** — 合并为 JOIN:
```java
// 主查询改为
SELECT i.*, m.bundles 
FROM inventoryitems i 
LEFT JOIN inventorymerchant m ON i.inventoryitemid = m.inventoryitemid
WHERE ...
```

### 2.6 P1-5: 登录查询合并

当前登录加载角色执行 **~25 个独立查询**。优化方案：
- 将 `skills`, `keymap`, `skillmacros`, `savedlocations`, `trocklocations` 五个表用 `UNION ALL` 合并为 1 个查询
- 将 `queststatus` + `questprogress` + `medalmaps` 用 JOIN 合并
- 将 `cooldowns` + `playerdiseases` 延迟到进入游戏后加载
- `famelog` 延迟加载（非关键路径）

预期效果：25 查询 → 8-10 查询。

### 2.7 P1-6: Pet.saveToDb() 连接泄漏修复

**现状** (`Character.java:8122-8124`):
```java
for (Pet pet : petList) {
    pet.saveToDb();  // pet.saveToDb() 内部打开新 Connection，不在事务内！
}
```

**修复** (`Pet.java`):
```java
// 新增重载
public void saveToDb(Connection con) throws SQLException {
    // 使用传入的连接，不自己获取
}
// Character.java 中改为
for (Pet pet : petList) {
    pet.saveToDb(con);  // 复用主事务连接
}
```

---

## 三、连接池与 Druid 配置

### 3.1 现状

`application.yml` 仅指定了 `type: com.alibaba.druid.pool.DruidDataSource`，其余全部使用默认值。Druid 默认参数偏小（initial-size=0, max-active=8）。

### 3.2 推荐配置

```yaml
mybatis-flex:
  datasource:
    mysql:
      type: com.alibaba.druid.pool.DruidDataSource
      driver-class-name: com.mysql.cj.jdbc.Driver
      url: jdbc:mysql://localhost:3306/beidou_dev_v1?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&cachePrepStmts=true&useServerPrepStmts=true&prepStmtCacheSize=250&prepStmtCacheSqlLimit=2048&rewriteBatchedStatements=true
      username: root
      password: root
      # === Druid 池化配置 ===
      initial-size: 10
      min-idle: 10
      max-active: 50
      max-wait: 3000
      # === 连接健康检查 ===
      validation-query: SELECT 1
      test-while-idle: true
      test-on-borrow: false
      test-on-return: false
      time-between-eviction-runs-millis: 60000
      min-evictable-idle-time-millis: 300000
      keep-alive: true
      keep-alive-between-time-millis: 120000
      # === 连接泄漏检测 ===
      remove-abandoned: true
      remove-abandoned-timeout: 1800
      log-abandoned: true
      # === 监控 ===
      filters: stat,wall,slf4j
      filter:
        stat:
          log-slow-sql: true
          slow-sql-millis: 2000
```

关键配置说明：
- `rewriteBatchedStatements=true` — MySQL JDBC 驱动层将 `addBatch()` 重写为多值 INSERT，性能提升 5-10x
- `prepStmtCacheSize=250` — 服务端预编译语句缓存
- `max-active: 50` — 考虑到可能 100+ 在线玩家同时操作

---

## 四、ScriptEngine 池化与脚本优化

### 4.1 问题核心

当前每个 `(Client, scriptPath)` 组合创建一个独立的 GraalJS ScriptEngine。每个引擎包含：
- 独立的 Polyglot Context (~200KB 基线)
- JIT 编译的代码缓存
- Bindings 对象

**内存模型**: 100 在线玩家 × 平均 30 个独特脚本 = 3000 个 ScriptEngine = **~600MB GraalJS 开销**

### 4.2 分层优化方案

**第1层 — 无状态脚本全局共享（立即执行）**:

Portal、Map、Event (init/setup)、Reactor 脚本天然无状态 → 引擎全局共享：

```java
// AbstractScriptManager.java 新增
private static final Map<String, SynchronizedInvocable> GLOBAL_ENGINES = new ConcurrentHashMap<>();

public static SynchronizedInvocable getGlobalEngine(String path) {
    return GLOBAL_ENGINES.computeIfAbsent(path, p -> {
        ScriptEngine engine = sef.getScriptEngine();
        // evaluate script
        return SynchronizedInvocable.of((Invocable) engine);
    });
}
```

适用脚本：
- `PortalScriptManager` — 传送门脚本，每个传送门一个全局引擎
- `MapScriptManager` — 地图脚本，每个地图一个全局引擎
- `EventScriptManager` — 事件脚本已使用 `SynchronizedInvocable`，改为全局
- `ReactorScriptManager` — 反应器脚本

**第2层 — NPC/Quest 脚本参数传递重构（中期）**:

当前模式：`cm` 作为全局变量注入引擎 → 引擎不能共享

改造为参数传递：
```javascript
// 旧: cm.sendNext("你好");
// 新: function start(cm) { cm.sendNext("你好"); }
```
Java 侧：
```java
// 旧: engine.put("cm", cm); invocable.invokeFunction("start");
// 新: invocable.invokeFunction("start", cm);  // cm 作为参数
```
这样引擎本身不再绑定 cm，可以跨玩家共享。NPC 对话脚本可复用同一引擎。

**第3层 — ScriptEngine 对象池（中期）**:

对于必须绑定 cm 的复杂脚本，使用 Apache Commons Pool：
```java
GenericObjectPool<ScriptEngine> npcEnginePool = new GenericObjectPool<>(
    new ScriptEnginePooledObjectFactory()
);
```

### 4.3 GraalJS 编译缓存

当前每次创建新引擎都要重新解析和编译 JS 源码。GraalJS 支持预编译：

```java
// 启动时预编译所有脚本
for (File scriptFile : scriptDir.listFiles()) {
    Source source = Source.newBuilder("js", scriptFile).build();
    // GraalJS 内部会自动缓存编译结果
    engine.eval(source);
}
```

---

## 五、并发模型重构

### 5.1 问题根源

```
Netty Worker Thread (16 threads)
  └─ Client.channelRead()
       └─ handler.handlePacket()
            ├─ DB 查询（阻塞！）
            ├─ ScriptEngine.eval()（阻塞！）
            └─ 复杂计算（阻塞！）
```

所有业务逻辑同步运行在 Netty I/O 线程上 → 一个玩家的慢操作拖慢同线程所有玩家。

### 5.2 修复：引入业务虚拟线程池

```java
// Client.java 新增
private static final ExecutorService GAME_EXECUTOR = 
    Executors.newVirtualThreadPerTaskExecutor();

@Override
public void channelRead(ChannelHandlerContext ctx, Object msg) {
    Packet packet = (Packet) msg;
    RecvOpcode opcode = packet.getOpcode();
    
    // 高频轻操作保持在 I/O 线程处理
    if (isLightweightOpcode(opcode)) {
        processPacket(opcode, packet);
        return;
    }
    
    // 重操作提交到虚拟线程
    GAME_EXECUTOR.submit(() -> {
        try {
            ThreadLocalUtil.setCurrentClient(this);
            processPacket(opcode, packet);
        } catch (Exception e) {
            log.error("Packet processing error for opcode: {}", opcode, e);
        } finally {
            ThreadLocalUtil.clear();
        }
    });
}
```

**轻操作白名单**（保留在 I/O 线程）：
- `MOVE_PLAYER`, `CLOSE_RANGE_ATTACK`, `TAKE_DAMAGE` — 高频移动/战斗
- `PONG` — 心跳响应
- `CHANGE_KEYMAP` — 按键映射

**注意事项**：
- 需要确保 handler 内部没有假设运行在特定线程
- Client 的锁机制已存在（`chrLock`, `announcerLock`），线程安全有基础
- 建议先在测试环境验证，确认包时序不受影响

### 5.3 Client/Character 锁拆分

当前热点锁：
- `Client.announcerLock` — 发包用公平锁（已建议移除，见 §1.3）
- `Client.lock` — 通用客户端锁，覆盖状态变更、NPC对话、交易等
- `Character.chrLock` — 覆盖物品操作、技能释放、任务等

拆分为细粒度锁：
```java
// Character.java
private final Object inventoryLock = new Object();  // 物品增删改
private final Object skillLock = new Object();      // 技能操作
private final Object questLock = new Object();      // 任务状态
// chrLock 保留用于 saveCharToDB() 等需要全局一致性的操作
```

---

## 六、启动流程优化

### 6.1 现状

启动时间分布估计（单世界单频道）：
```
数据库迁移检查      < 1s
Spring 上下文初始化   3-5s  (含 17 个 @PostConstruct 配置加载)
WZ 并行加载          5-10s (Skill + Item + Quest + Cash)
Server.init()        3-5s  (全表 UPDATE + BossLog + EventScript)
Netty 绑定           < 1s
─────────────────────────
总计                 12-22s
```

### 6.2 优化项

**启动时全表 UPDATE 异步化** (`Server.java:915-916`):
```java
// 这两行阻塞启动
accountService.resetAllLoggedIn();   // UPDATE accounts SET loggedin=0
characterService.resetMerchant();    // UPDATE characters SET hasMerchant=0

// 改为：带有 WHERE loggedin != 0 / hasMerchant != 0 的条件更新
// 如果大部分行已经是 0，几乎不需要做任何工作
```

**BossLogTask + ExtendValueTask 延迟执行** (`Server.java:942-943`):
```java
// 当前：启动时立即同步执行
new BossLogTask().run();
new ExtendValueTask().run();

// 改为：延迟到服务器标记 online 后异步执行
ThreadManager.getInstance().newTask(() -> {
    new BossLogTask().run();
    new ExtendValueTask().run();
});
```

**EquipPowerRankingManager 延迟** (`Server.java:946`):
```java
// 当前 startRankingRefresh() 立即执行首次排名 + 定时
// 改为：先注册定时器，首次计算延迟 30 秒
equipPowerRankingManager.scheduleWithInitialDelay(30, TimeUnit.SECONDS);
```

**WZ 文件实例缓存** (`DataProviderFactory.java:50-52`):
```java
// 当前：每次调用 new XMLWZFile() → 重建整个目录树
// 修复：
private static final Map<WZFiles, XMLWZFile> cache = new ConcurrentHashMap<>();

public static DataProvider getDataProvider(WZFiles file) {
    return cache.computeIfAbsent(file, f -> {
        return new XMLWZFile(f.getFile());  // 目录树只遍历一次
    });
}
```

**合并 @PostConstruct 配置加载**：
17 个服务各自在 `@PostConstruct` 中执行 SELECT。可合并为一个启动批处理查询，减少数据库连接获取/释放开销。

**合并 Flyway 基线**：
146 个迁移文件 → 将 V1.0.x (初始建表) 合并为 1 个基线迁移。将 V1.8.x (BeiDou 自定义) 稳定后也合并。

**消除 EventScriptManager 双重创建**：
`Channel` 构造函数中先用占位脚本，`Server.init()` 末尾再重新加载 → 直接在构造函数中加载真实事件脚本。

---

## 七、GC 与内存调优

### 7.1 推荐 JVM 参数

```bash
java \
  -Xms2G -Xmx4G \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=100 \
  -XX:G1HeapRegionSize=4M \
  -XX:ConcGCThreads=2 \
  -XX:InitiatingHeapOccupancyPercent=45 \
  -XX:+ParallelRefProcEnabled \
  -XX:+UseStringDeduplication \
  -XX:StringDeduplicationAgeThreshold=3 \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath=./heap_dump.hprof \
  -XX:+ExitOnOutOfMemoryError \
  -XX:MaxMetaspaceSize=256M \
  -XX:+ExplicitGCInvokesConcurrent \
  -XX:+ScavengeBeforeFullGC \
  -jar BeiDou.jar
```

### 7.2 对象池化

**Packet 对象池** — 高频创建/销毁，适合 ThreadLocal 缓存：
```java
public class PacketWriterPool {
    private static final ThreadLocal<PacketWriter> CACHE = ThreadLocal.withInitial(PacketWriter::new);
    
    public static PacketWriter borrow() {
        PacketWriter pw = CACHE.get();
        pw.reset();
        return pw;
    }
    // 无需归还 — ThreadLocal 自动管理
}
```

**MapItem 对象池** — 掉落物高频创建：
```java
private static final Queue<MapItem> DROP_POOL = new ConcurrentLinkedQueue<>();

public static MapItem borrow() {
    MapItem item = DROP_POOL.poll();
    if (item != null) return item;
    return new MapItem();
}

public static void recycle(MapItem item) {
    if (DROP_POOL.size() < 200) {
        item.reset();
        DROP_POOL.offer(item);
    }
}
```

### 7.3 集合优化

**Monster.takenDamage** — `HashMap<Integer, AtomicLong>` 改为 fastutil：
```java
// 旧：每个攻击产生 Long 装箱 + AtomicLong 对象
private final Map<Integer, AtomicLong> takenDamage = new HashMap<>();

// 新：原始类型 Map，无装箱
private final Int2LongOpenHashMap takenDamage = new Int2LongOpenHashMap();
// 使用 addTo(key, delta) 替代 AtomicLong
```

**Character.visibleMapObjects** — ConcurrentHashMap 开销大：
```java
// 如果频繁遍历，考虑改用
private final Set<MapObject> visibleMapObjects = 
    Collections.newSetFromMap(new ConcurrentHashMap<>(32));
// 改为预分配容量的
private final Set<MapObject> visibleMapObjects = 
    Collections.newSetFromMap(new ConcurrentHashMap<>(64, 0.75f, 1));
```

### 7.4 内存泄漏防护

**EventLoopGroup 泄漏修复** — `LoginServer.java` 和 `ChannelServer.java`：
```java
// stop() 方法当前只关闭 Channel，不关闭 EventLoopGroup
public void stop() {
    try {
        if (channel != null) {
            channel.close().syncUninterruptibly();
        }
    } finally {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully(2, 5, TimeUnit.SECONDS);
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully(2, 5, TimeUnit.SECONDS);
        }
    }
}
```

**孤儿事件地图清理** — `MapManager.java` 新增：
```java
@Scheduled(fixedRate = 600000)  // 每 10 分钟
public void cleanOrphanEventMaps() {
    List<Integer> toRemove = new ArrayList<>();
    for (Map.Entry<Integer, MapleMap> entry : maps.entrySet()) {
        if (entry.getValue().isEventMap() && !hasActiveEIM(entry.getKey())) {
            toRemove.add(entry.getKey());
        }
    }
    toRemove.forEach(this::dispose);
    if (!toRemove.isEmpty()) {
        log.info("清理了 {} 个孤儿事件地图", toRemove.size());
    }
}
```

---

## 八、可观测性

### 8.1 Spring Actuator 集成

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,info,heapdump,threaddump
  endpoint:
    health:
      show-details: always
```

### 8.2 自定义健康检查

```java
@Component
public class GameServerHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        Server server = Server.getInstance();
        if (!server.isOnline()) {
            return Health.down().withDetail("reason", "server offline").build();
        }
        
        Runtime rt = Runtime.getRuntime();
        long freeMB = rt.freeMemory() / (1024 * 1024);
        int playerCount = Server.getInstance().getWorld(0)
            .getPlayerStorage().getAllCharacters().size();
        
        Health.Builder builder = freeMB < 100 
            ? Health.status("WARN") 
            : Health.up();
        
        return builder
            .withDetail("players", playerCount)
            .withDetail("freeMemoryMB", freeMB)
            .withDetail("online", server.isOnline())
            .build();
    }
}
```

### 8.3 定期内存报告

```java
@Component
public class MemoryMonitor {
    @Scheduled(fixedRate = 300000)
    public void report() {
        Runtime rt = Runtime.getRuntime();
        long used = rt.totalMemory() - rt.freeMemory();
        double pct = (double) used / rt.maxMemory() * 100;
        
        log.info("[内存] {}MB / {}MB ({:.1f}%)", 
            used >> 20, rt.maxMemory() >> 20, pct);
        
        if (pct > 85) {
            log.warn("[内存告警] 使用率超过 85%");
        }
    }
}
```

---

## 九、代码级微观优化

### 9.1 内联 byte[] 文字复用

`PacketCreator` 中大量 `new byte[]{0, 0, ...}` 模式（至少 15 处）：
```java
// 改为类常量
private static final byte[] ZERO_5 = {0, 0, 0, 0, 0};
private static final byte[] ZERO_6 = {0, 0, 0, 0, 0, 0};
// p.writeBytes(ZERO_5)
```

### 9.2 writeFixedString 优化

`ByteBufOutPacket.writeFixedString()` + `StringUtil.getRightPaddedStr()` 产生 3 次中间数组：
```java
// 合并为单次分配
public void writeFixedPaddedString(String value, int fixed) {
    int start = byteBuf.writerIndex();
    byteBuf.writerIndex(start + fixed);  // 预留空间
    byte[] bytes = value.getBytes(charset);
    int len = Math.min(bytes.length, fixed);
    byteBuf.setBytes(start, bytes, 0, len);
    // 剩余部分已经是 0（ByteBuf 初始化为 0）
}
```

### 9.3 Map 预分配容量

避免 HashMap 扩容导致的 rehash：
```java
// 旧
Map<Integer, Item> items = new HashMap<>();  // 默认 16，扩容多次

// 新
Map<Integer, Item> items = new HashMap<>((int)(expectedSize / 0.75f) + 1);
```

---

## 十、实施路线图

### 第一梯队（立即，1-2 天，低风险高收益）

| # | 项目 | 文件 | 风险 |
|---|------|------|------|
| 1 | JVM GC 参数 + 内存监控 | `launch.bat`, 新建 `MemoryMonitor` | 无 |
| 2 | Druid 连接池完整配置 | `application.yml` | 低 |
| 3 | Netty TCP_NODELAY + ChannelOption | `LoginServer.java`, `ChannelServer.java` | 低 |
| 4 | ByteBuf 池化 (PooledByteBufAllocator) | `ByteBufOutPacket.java` | 低 |
| 5 | Charset 查找缓存 | `CharsetConstants.java` | 无 |
| 6 | EventLoopGroup 泄漏修复 | `LoginServer.java`, `ChannelServer.java` | 低 |
| 7 | 缺失数据库索引补充 | 新建 Flyway 迁移 | 低 |
| 8 | Spring Actuator 集成 | `pom.xml`, `application.yml` | 无 |

### 第二梯队（本周，3-5 天，中等风险）

| # | 项目 | 风险 |
|---|------|------|
| 9 | sendPacket 锁移除 | ⚠️ 需验证包时序 |
| 10 | Client/Character 锁拆分 | ⚠️ 需验证并发安全 |
| 11 | ItemFactory 批处理 INSERT | ⚠️ 需测试事务回滚 |
| 12 | TimerManager 调度拆分 | 低 |
| 13 | WZ XML 文件缓存 + DataProviderFactory 缓存 | 低 |
| 14 | 登录查询合并 | 低 |
| 15 | 商人 N+1 查询修复 | 低 |
| 16 | Pet.saveToDb() 连接泄漏修复 | 低 |
| 17 | @PostConstruct 配置加载合并 | 低 |
| 18 | 启动异步化 (BossLog/ExtendValue) | 低 |

### 第三梯队（下周，1-2 周，需充分测试）

| # | 项目 | 风险 |
|---|------|------|
| 19 | 包处理异步化 (虚拟线程) | ⚠️⚠️ 高风险，需充分测试 |
| 20 | ScriptEngine 全局共享 (无状态脚本) | ⚠️ 需验证引擎线程安全 |
| 21 | NPC cm 参数传递重构 | ⚠️⚠️ 改动量较大 |
| 22 | PacketWriter 线程本地缓存 | 低 |
| 23 | MapItem 对象池 | ⚠️ 需验证池化正确性 |
| 24 | Monster.takenDamage fastutil 替换 | 低 |
| 25 | Map 预分配容量 (批量改造) | 无 |

### 第四梯队（长期迭代）

| # | 项目 | 说明 |
|---|------|------|
| 26 | Caffeine 二级缓存引入 | MyBatis 查询缓存 |
| 27 | 乐观锁版本号 (防脏写) | 数据一致性增强 |
| 28 | Flyway 迁移合并 | 减少启动时间 |
| 29 | GraalJS 预编译 | 脚本加载加速 |
| 30 | Redis 集成 | 如果需分布式/跨服务器共享状态 |

---

## 十一、回滚策略

每项改动独立提交。高风险改动先上测试服务器验证。

| 改动类型 | 回滚方法 |
|----------|----------|
| JVM 参数 | 恢复原始 `launch.bat` |
| `application.yml` | Git revert |
| Java 代码 | Git revert + `mvn clean package` |
| 数据库索引 | 新建的索引不会影响旧查询，可不回滚 |
| 新 Maven 依赖 | 移除 `pom.xml` 中 dependency |

---

> 文档版本: v2.0  
> 最后更新: 2026-06-27  
> 审查范围: gms-server/src/main/java 全量 + application.yml + launch 脚本 + Flyway 迁移文件

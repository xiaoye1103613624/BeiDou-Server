# 掉落特效（DropItemAura）开发计划

> 前置：[EVALUATION.md](EVALUATION.md)（方案评估与取证）
> 状态：**实现中（v1 联调）** —— 服务端 B 组已落地；客户端 v1 已定稿（A2 IDA 校准 + 4 档 + C4 包尾读取 + C3 夹层框架，`kAuraLayerZ=0` 占位），待 E2 实测反推 A3/A4。

---

## 一、目标与非目标

**目标**：给掉落的**装备**按品质档位叠加光环特效（**带光柱版**），特效**真正挂载在道具上**（跟随镜头与掉落动画），**层级正确**（道具夹在 back 与 front 之间），且**不越到界面 UI 之上**。

**非目标**：
- 不做非装备（金币/消耗品等）的光环（一律无特效）。
- 不改掉落概率、掉落表、拾取逻辑等既有玩法。
- 不做光环外观的自定义配置（本版固定 4 档映射）。

---

## 二、已确认决策

| # | 决策点 | 结论 |
| --- | --- | --- |
| 1 | 挂载方式 | **A 逐帧跟随**（每帧取 CDrop 世界坐标 − 相机偏移同步图层） |
| 2 | z 值 / 层级策略 | **A `back = dropZ - 1`，`front = dropZ + 1`**（道具夹中间） |
| 3 | 是否改协议 | **确认改**：掉落封包新增 grade 字节（服务端 + 客户端双向） |
| 4 | 档位数 | **4 档**（Rare / Epic / Unique / Legendary；资源无 Mythic，按决策 A 放弃） |
| 5 | 资源形态 | **带光柱版** |

---

## 三、协议设计（提案，待前置 A2 确认）

- 在 `dropItemFromMapObject` 与 `updateMapItemObject` 两个封包的**尾部追加** 1 字节 `grade`：`0` = 无光环，`1..4` = Rare→Legendary。
- **该字节无条件追加**（即便 `DROP_AURA_ENABLED=false` 也写，仅值=0）。故旧客户端不兼容，必须配套新客户端（C）部署；开关关闭 = 光环不显示（grade 0），即"视觉完全回退"。
- 客户端 hook **不再依赖 `Decode1` 流位置**，改为从 `CInPacket` 缓冲**包尾**读取 grade（更稳，且与客户端自身解析解耦）。
- **不插入中间字段**：避免打乱客户端既有解析顺序；尾部追加对客户端解析无影响（待实测确认客户端忽略尾部字节、无崩溃/无错位）。
- 客户端 hook **不再依赖 `Decode1` 的流位置**读档位，改为从 `CInPacket` 缓冲按**包尾偏移**读取（更稳，且与客户端自身解析解耦）。
- 服务端把 `-1` / `0` **统一归一化成 `0`** 再写入，协议只暴露 `0..5`。

---

## 四、前置项状态

| ID | 阻塞项 | 状态 | 结果 |
| --- | --- | --- | --- |
| **P1** | IDA 校准 | ✅ **已解决**（地址部分） | 已接入本地 `ida-pro-mcp`（v83 客户端 `` `Angel.exe ``）。**7 个硬编码地址全部命中**，见 [evidence/ida-notes.md](../evidence/ida-notes.md) |
| **P2** | 资源实测 | ✅ **已解决** | 已用 `orange-wz` 解析带光柱版 `Effect.wz`，见 [evidence/wz-effect-audit.md](../evidence/wz-effect-audit.md) |

### ✅ P2 引出的档位冲突 —— 已解决（决策 A）

实测资源**只有 4 档**（Rare / Epic / Unique / Legendary），**无 Mythic**；决策 ④ 已改为 **4 档**（见 §二④）：`getDropItemGrade` 返回 `0..4`，客户端 `g_gradeNames[4]`。已按方案 A 落地，直接用现成资源，放弃 Mythic 档。

> P1 中「协议插入点 / 坐标跟随 / z 取值」三项：
> - **协议插入点（包尾读 grade）已定稿** —— A2 用 IDA 复核完成：`CInPacket::Decode1` 反编译确认 `m_nLen=*(this+0x0C)`、`m_pData=*(this+0x08)`；客户端 `ReadGradeFromTail` 已据此次确认修正（原推测 `+0x04` 错，已改 `+0x0C`）。
> - **坐标跟随 / z 取值（A3 / A4）** —— 已取得静态证据：`OnDropEnterField` 反编译给出 DROP 成员偏移（x/y 为世界坐标方向）；但 **C2 逐帧跟随是否必要、C3 真实夹层 z** 取决于 `IWzGr2D` 图层 z 坐标系（静态无法定），**留待 E2 实测评测**（镜头脱钩 / front·back 夹住 / 是否被 UI 遮挡）。当前维持占位实现（`kAuraLayerZ=0`）。

> 按门禁：偏移/签名/封包一律以 IDA 证据为准，**不臆造、不沿用未校验的 v83 地址**。

---

## 五、任务分解

### A. 前置校准（阻塞，依赖 P1/P2）

| ID | 任务 | 产出 |
| --- | --- | --- |
| A1 | IDA 复核 7 个地址：`0x00506385` `0x0050638A` `0x004065F3` `0x0043EEFC` `0x00426C7E` `0x0050ACA8` `0x0045144A` | 地址对照表（含函数名/签名依据） |
| A2 | 定 `CInPacket` 缓冲基址与长度字段 → 确定 grade 字节读取方式 | 封包偏移笔记 + 抓包样例 |
| A3 | 定 `CDrop` 世界坐标字段 + 相机偏移地址 → 支撑逐帧跟随 | 结构笔记 |
| A4 | 实测掉落层 `dropZ` 与 UI 层边界 → 定 `back/front` z 值 | z 取值结论 |
| A5 | 实测带光柱版 `Effect.wz`：帧数、尺寸、origin、front/back | 资源清单 + `MAX_FRAMES` 结论（>16 则上调） |

### B. 服务端（本仓库，可独立推进）

| ID | 任务 | 涉及文件 |
| --- | --- | --- |
| B1 | 实现 `getDropItemGrade(MapItem)`：4 档阈值（Rare/Epic/Unique/Legendary，配置可覆盖）；仅装备有光环，非装备/关闭一律 0；按 `getEquipStats` 加成属性总分划分 | `org/gms/util/PacketCreator.java` |
| B2 | `dropItemFromMapObject` 包尾写 grade 字节 | 同上 |
| B3 | `updateMapItemObject` **同步**写 grade 字节（mod=2 重发路径）——否则重新进图 / 组队归属变更时光环丢失 | 同上 |
| B4 | 加 `GameConfig` 开关（可关可回滚） | `game_config` 记录 + 读取处 |
| B5 | 上线级节点日志（掉落档位决策、协议开关状态），临时调试日志完成后清理 | 同上 |
| B6 | 性能评估：按 `itemId` 缓存白板属性，避免每次掉落都走 WZ 查表 | `PacketCreator` / 缓存处 |

> 阈值用装备**加成属性总分**（`getEquipStats` 的 `inc*` 之和，已缓存）：内置默认占位 `T1=40→Epic`、`T2=90→Unique`、`T3=160→Legendary`，可用 `DROP_AURA_T1/T2/T3` 覆盖；**需按本服装备属性分布校准**。

### C. 客户端插件（外部仓库 / 产物，依赖 A）

| ID | 任务 |
| --- | --- |
| C1 | 以 **`itemaura.cpp` 为基线**（5 档），废弃 `dropitemaura.cpp`，避免双份分叉 |
| C2 | **逐帧跟随**：位置同步挂渲染循环 / `CDrop::Update`（**不能用 120ms 定时器**——镜头移动时会产生可见拖影抖动）；帧动画仍用 120ms 时间累加驱动 |
| C3 | **z 策略**：`back = dropZ - 1`、`front = dropZ + 1`，取消统一 `z = 0` |
| C4 | grade 读取改为包尾偏移（见协议设计） |
| C5 | 并发光环上限（池化 / LRU），防高密度刷怪图图层堆积（现状约 48 图层/掉落物） |
| C6 | 析构清理改用 A3 校准后的真实 `CDrop` 偏移，替换 `{pEntry, pEntry+0x10}` 猜测；保留 TTL 兜底 |
| C7 | 保留「预加载 + `Putcolor` 切帧」优点；删除魔法偏移，补齐注释与逆向依据；`CreateLayer` 的 z 覆盖改 RAII，避免全局标志泄漏污染无关图层 |

#### C 组进度（2026-09-18）

| ID | 状态 | 说明 |
| --- | --- | --- |
| A2 | ✅ | CInPacket 布局 IDA 确认：`len=*(this+0x0C)`、`buf=*(this+0x08)`（Decode1 反编译）；客户端 `ReadGradeFromTail` 已据正（原推测 `+0x04` 错→改 `+0x0C`） |
| C1 | ✅ | 以 `itemaura.cpp` 为唯一基线；`dropitemaura.cpp` 废弃（仅保留不编） |
| 4 档 | ✅ | `g_gradeNames[4]` 去 Mythic；grade 1..4 映射 Rare..Legendary（决策 A） |
| C4 | ✅ | grade 改**包尾读取**（`buf[len-1]`），删除脆弱的 `Decode1` 首字节读取；不再消耗 `CInPacket` 读指针 |
| C3 | ⚠️框架/待实测 | `front=dropZ+1` / `back=dropZ-1` 已接线；`dropZ` 暂 0 占位（A4：WZ 图层 z 语义静态无法定），**待 E2 实测**确认是否夹住道具 |
| C2 | ✅静态方案 | 沿用原 mod 静态世界坐标传参；A3 已确认 DROP x/y 为世界坐标方向。是否需每帧跟随取决于 WZ 图层坐标系，**待 E2 实测**（镜头移动脱钩则改每帧读 DROP 坐标，需先解决 pDrop 身份=C6） |
| C5 | ⏳ | 并发上限（池化/LRU）未实现（原 mod 无上限），建议接受，可后续补 |
| C6 | ⏳ | dtor 候选偏移 `{pEntry,pEntry+0x10}` 依赖 A3，保留 TTL 兜底 |
| C7 | ⏳ | `CreateLayer` z 覆盖暂保留全局标志方案，RAII 重构待定 |

### D. 资源

| ID | 任务 |
| --- | --- |
| D1 | 带光柱版 `Effect.wz` 合入客户端 `Data/`（**先 dry-run**：按 `CLAUDE.md`，patch 的 ADD 是合并不是覆盖，防重复子树脏数据） |
| D2 | 确认 `EN/` 不同步（Effect 属语言无关数据，中英文客户端共用 `Data/`） |

### E. 验证门禁

| ID | 项目 |
| --- | --- |
| E1 | 服务端 `mvn clean package` 构建通过（本仓库改动） |
| E2 | 客户端实测清单：① 移动镜头光环是否脱钩 ② front/back 是否夹住道具 ③ 是否被 UI 遮挡 / 是否盖住 UI ④ 高密度图性能与内存 ⑤ 掉落消失后图层是否回收 |
| E3 | 玩法回归：掉落 / 拾取 / 换图重发（mod=2）/ 组队归属变更 / 宠物拾取 / 金币掉落 |
| E4 | 关卡：无客户端崩溃、无封包错位、关闭开关后行为完全回退 |

#### E2 联调实测清单（v1 = A2 校准版）

> v1 代码已含 A2（IDA 确认 `len=+0x0C`）+ 4 档 + C4 包尾读取 + C3 夹层框架（`kAuraLayerZ=0`）。
> A3（逐帧跟随）/ A4（真实夹层 z）仍为占位，**本清单用于实测反推**。每项：观察点 → 预期 → 故障现象 → 对应修正。

| # | 观察点 | 预期（假设成立时） | 故障现象 | 指向待定项 / 修正 |
| --- | --- | --- | --- | --- |
| 1 | **光环位置对准道具** | 光环中心对齐掉落物脚下 | 光环偏移/错位/在错误位置 | `buf[21]/[23]` 偏移需按服务端 `dropItemFromMapObject` 字段顺序校准（见下备注）；或落点需减 origin |
| 2 | **镜头移动是否脱钩** | 移动镜头时光环随道具一起移动 | 光环留在原地（屏幕坐标不跟随） | C2：改每帧跟随（挂 CDrop/Pool 的 Update，读 DROP.x/y − 相机偏移）；前提先解决 pDrop 身份（C6） |
| 3 | **front/back 是否夹住道具** | 道具在 back 之上、front 之下，三层正确 | front 跑到道具后 / back 盖住道具 / 整组被埋 | C3/A4：把 `dropZ` 从 0 改真实层序值（疑似 `DROP.z≈dropY`），据实测调 `kAuraLayerZ` |
| 4 | **是否被 UI 遮挡 / 盖住 UI** | 光环在游戏世界层，不盖聊天/血条/小地图 | 浮在 UI 之上 或 被 UI 完全遮住 | C3：z 取值错误；需确认 `CreateLayer` z 坐标系（是否层序、是否含 UI 层） |
| 5 | **掉落消失后回收** | 拾取/消失瞬间光环消失 | 光环残留数秒~200s | C6：dtor 候选 `{pEntry,pEntry+0x10}` 与 `pDrop=CDropPool*` 不匹配，靠 TTL 兜底；需校准析构 hook |
| 6 | **档位与开关** | 装备按品质显 Rare/Epic/Unique/Legendary；非装备/关闭=无 | 档位错 / 无光环 / 越界崩溃 | 协议 grade 映射；`DROP_AURA_ENABLED=false` 时 grade=0 |
| 7 | **稳定性** | 长时间不掉崩溃、不内存暴涨 | 崩溃 / 内存泄漏 | CreateLayer z 全局标志污染（C7）；图层未释放 |

**备注（观察点 1 —— 已在 v1 对齐，非阻塞）**：mod 原 `buf[6]/[21]/[23]/[33]` 偏移经核对**与 BeiDou `dropItemFromMapObject` 错位**（原值落在 itemId / dropperOid / dropfrom / expiry 区）。已据服务端源码 + 客户端 `OnDropEnterField` Decode 顺序交叉验证，改为 `mod@0`、`dropX@15`、`dropY@17`，delay 改 dropfrom→dropto 距离估算（见 `itemaura.cpp` 注释）。E2 观察点 1 现为**验证**项而非阻塞项。

**反馈机制**：实测后把观察点 1~7 的现象（截图/描述）反馈，我据以：
- 位置错位 → 校准 buf 偏移（对齐服务端封包顺序）。
- 镜头脱钩 → 实现 C2 逐帧跟随（先 C6 解决 pDrop 身份）。
- 夹层/遮挡 → 定稿 C3/A4 真实 `dropZ`。
- 回收慢 → 校准 C6 dtor 偏移。

### F. 产物与文档

| ID | 任务 |
| --- | --- |
| F1 | `docs/features/drop-item-aura/`：`README.md`（最终实现逻辑）、`notes/`（评估 + 计划）、`evidence/`（IDA 地址笔记、封包样例，脱敏）、`patches/`（dry-run 日志）、`resources/`（资源清单） |
| F2 | 评估文档标注决策已确认并指向本计划 |

---

## 六、涉及文件预估

**服务端（本仓库）**
- `gms-server/src/main/java/org/gms/util/PacketCreator.java`（B1/B2/B3/B5/B6）
- `game_config` 配置记录（B4）
- 可能触及 `org/gms/server/maps/MapItem.java`（若需缓存/取装备）

**客户端（外部仓库 BeiDou-Client）**
- 插件源码（由 `itemaura.cpp` 演化）→ 构建产物
- `Data/Effect.wz`（D1）

---

## 七、风险与回滚

| 风险 | 应对 |
| --- | --- |
| 协议改动影响所有掉落流程 | `GameConfig` 开关，关闭即完全回退旧行为；E3 全量回归 |
| 客户端 hook 崩溃 / 封包错位 | 插件可单独禁用；E4 崩溃项必测 |
| `dropZ` 取值不准导致被埋或被 UI 覆盖 | A4 先实测，C3 小范围验证后再全量 |
| 高密度图图层堆积 | C5 并发上限（**建议接受**，原实现无上限） |
| 尾部追加字节客户端不忽略 | A2/E4 优先验证；不忽略则改为新增独立 opcode 方案（需再确认） |

---

## 八、执行顺序建议

```text
P1/P2 提供 → A1~A5 校准
                ├─→ B1~B6 服务端（可与 A2 并行推进到可联调状态）
                └─→ C1~C7 客户端 → 联调
D1/D2 资源 → E1~E4 验证 → F1/F2 归档
```

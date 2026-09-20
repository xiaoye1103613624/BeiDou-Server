# 掉落特效（DropItemAura）方案评估

> 来源：`E:\MXD\扩展改动\掉落特效`（Discord v83 DropItemAura 发布帖 + 源码）
> 评估范围：仅**评估**，未落地实现。
> 评估关注点（用户指定）：① 特效挂载到道具上 ② 层级关系 ③ 不要超过界面 UI 上显示

---

## 一、方案构成

三件套，链路为「服务端算档位 → 封包带档位 → 客户端 hook 渲染光环」：

| 组成 | 文件 | 作用 |
| --- | --- | --- |
| 客户端插件 | `dropitemaura.cpp`、`itemaura.cpp` | v83 客户端内存注入，hook `OnDropEnterField` / `IWzGr2D::CreateLayer` / `CDrop` 析构，`SetTimer` 驱动 |
| 服务端 | `PacketCreator.java`（`getDropItemGrade`） | 按装备属性与白板属性差值算「品质档位」 |
| 资源 | `Effect.wz` | `Effect/BasicEff.img/dropItemAura/{Rare,Epic,Unique,Legendary[,Mythic]}/{front,back}/{0..N}` + `Effect/BasicEff.img/dropItemEffect/{0..N}` |

插件主流程：`OnDropEnterField` 读档位 → 入 pending 队列 → 120ms 定时器 flush → `SpawnAura` 预加载全部帧 → 定时器靠 `Putcolor` 显隐切帧循环播放；落地特效播一次即释放；`CDrop` 析构或 200s TTL 兜底回收。

**可取之处**（后续实现应保留）：
- 帧**预加载 + `Putcolor` 显隐切帧**，动画期间不新建图层，避免每帧建层的开销。
- `Putcolor(0)` = ARGB alpha 0 = 全透明，用作「隐藏」是正确且轻量的技巧。
- 200s TTL 兜底，防止掉落物析构未回调导致图层泄漏。

---

## 二、逐条评估

### 要求 ① 特效挂载到道具上 —— ❌ 未做到，当前是「钉」在生成瞬间的坐标

坐标只在 `SpawnAura` 里写一次，之后定时器仅做显隐切换，**从不更新坐标**：

```cpp
SetNextZ(kAuraLayerZ); SafeLoadLayer(&pF, pathF, dropX, dropY); ClearNextZ();
```

而 `dropX/dropY` 来自 `OnDropEnterField` 时从封包读到的值。后果：

1. **镜头一移动就脱钩（最致命）**：v83 视野随角色滚动，掉落物在世界坐标里不动，光环却被钉在 WzGr2D 画布的固定屏幕位置 → 立刻与道具分离，甚至飘到别的掉落物身上。
2. **不跟随掉落物浮动/弹跳动画**：v83 掉落物有上下浮动，光环静止。
3. **落地时机靠猜**：`readyTick = now + delay + 1000`，`delay` 取封包 `buf+33` 的 short，再硬加 1000ms 近似落地时间，落早落晚都会先错位。
4. **落地特效偏移是魔法数**：`int effY = dropY - 16;`

**要真正挂载**，必须每帧（或至少每个动画 tick）从 `CDrop` 取当前世界坐标 → 减相机偏移 → 同步到光环图层；或把特效挂成 `CDrop` 子节点由引擎带。两者都需 IDA 定位 `CDrop` 位置字段 / 相机偏移 / 附加入口。

### 要求 ② 注意层级关系 —— ⚠️ 当前实现恰好破坏了层级关系

两份 cpp 都把所有光环图层（front、back 一视同仁）通过 hook `IWzGr2D::CreateLayer` 强制成同一个 z：

```cpp
static const int kAuraLayerZ = 0;
...
if (g_nextLayerZ != kNoZOverride) z = g_nextLayerZ;   // front / back 都改成 0
```

问题：

1. **front / back 被压成同层，这正是层级关系的崩坏点**。WZ 资源刻意拆分 `front`（应压在道具**前面/上层**）与 `back`（应垫在道具**后面/下层**），道具夹在中间才有「光环环绕道具」的立体感。现在两者同为 z=0，前后关系退化成**取决于创建顺序**——循环里 `pF` 先建、`pB` 后建，同 z 下后建者后绘制，`back` 反而可能盖住道具与 `front`，与资源设计意图相反（需实测确认）。
2. **z=0 大概率在地图背景之下**，等于把光环埋进地里，很可能直接不可见。作者是为躲开「WZ 内嵌 z 太高会盖到 UI」才压到 0，属于从一个极端跳到另一个极端。
3. 正确取值应**相对掉落物所在层**：`back = dropZ - 1`、`front = dropZ + 1`，三者都远低于 UI 层。`dropZ` 需 IDA/WZ 实测确认。
4. 附带风险：`CreateLayer` 是引擎**全局** hook，`g_nextLayerZ` 是裸全局量（无 RAII）。单线程下 `SetNextZ/ClearNextZ` 成对调用基本安全，但 `SafeLoadLayer` 内部若触发额外建层或走异常路径，标志会泄漏并污染无关图层。建议改 RAII + 仅在确认是自身调用时改写。

### 要求 ③ 不要超过界面 UI 上显示 —— 需分清两种口径，当前只（歪打正着）满足其一

实测资源画布（解析 webp 文件头，VP8L）：

| 文件 | 尺寸 (px) |
| --- | --- |
| `0..5.webp`（纯光环） | 61~73 × 33~38 |
| `0..5_光柱.webp`（带光柱） | 53~62 × 46~48 |

- **口径 A：z 序上不能盖住 UI（必做）**。强制 z=0 确实满足「不盖 UI」，但代价是埋到背景之下（见 ②）。正确解是**把 z 夹在 `[dropZ-1, dropZ+1]`**，天然低于 UI 层 —— 这才是既「挂载在道具上」又「不越 UI」的正解。
- **口径 B：视觉上不能侵入 UI 区域（可选）**。实测光柱最高仅 48px（比纯光环高约 10~15px），v83 画面 800×600，该体量**基本不会顶到 UI 区域**，风险很低。⚠️ 但 webp 可能是帖子里的缩略预览，**真实 WZ 帧尺寸须以 `Effect.wz` 实测为准**再下最终结论。

---

## 三、其它必须一并处理的风险

| # | 风险 | 说明 |
| --- | --- | --- |
| 1 | **协议要改（双向）** | BeiDou 现有 `PacketCreator.dropItemFromMapObject` **不含** grade 字节，这套插件当前读不到档位。需在掉落封包新增字节，属**协议改动**，会动到所有掉落流程，须回归验证 |
| 2 | **档位数不一致 → 静默丢档** | 服务端 `getDropItemGrade` 返回 `1..5`（5 档，`0`/`-1` 为无光环）；`dropitemaura.cpp` 仅 `g_gradeNames[4]`（Rare~Legendary）且只接受 `1..4` → **第 5 档 Mythic 静默无光环**；`itemaura.cpp` 含 Mythic 接受 `1..5` → 与服务端匹配。**应以 `itemaura.cpp` 为基线，或把服务端削到 4 档，两端必须对齐** |
| 3 | **两份 cpp 是同 mod 的 v1/v2 近重复** | 不要都留，否则必然分叉 |
| 4 | **地址全为 v83 硬编码，本环境无 IDA MCP 可校** | 7 个绝对地址（`0x00506385` / `0x0050638A` / `0x004065F3` / `0x0043EEFC` / `0x00426C7E` / `0x0050ACA8` / `0x0045144A`）+ 封包魔法偏移（`buf+6 / +21 / +23 / +33`），均来自原作者针对某个 v83 客户端的逆向。BeiDou-Client 是改过的客户端，基址与函数布局很可能不同，**必须逐个 IDA 复核**。按门禁不臆造/沿用未校验偏移 |
| 5 | **析构 hook 脆弱 + 图层堆积** | `candidates[2] = { pEntry, pEntry + 0x10 }` 是猜 `CDrop` 相对 `CEntry` 的偏移；猜错就清不掉，只能等 200s TTL。每掉落物预载 16 front + 16 back + 16 effect ≈ 48 个 Wz 图层常驻，高密度刷怪图（数百掉落）= 上万个图层，内存与每帧开销吃紧。建议加并发上限（池化/LRU） |
| 6 | **定时器驱动不牢** | `SetTimer` 120ms 依赖窗口消息泵；拖拽/最小化/切图时消息滞后 → 动画卡住。挂渲染循环更稳 |
| 7 | **资源落地位置** | 按 `CLAUDE.md`：Effect 属语言无关数据，中英文客户端共用 `Data/`，**不需要**同步 `EN/`。合并方式需明确（整包替换 or 把 `BasicEff.img` 子树合进客户端现有 `Effect.wz`）；按 `CLAUDE.md`「patch 的 ADD 是合并不是覆盖」，务必先 dry-run 防重复子树脏数据 |
| 8 | **服务端转型细节** | BeiDou `ItemInformationProvider.getEquipById(int)` 返回 **`Item` 而非 `Equip`**（见 `ItemInformationProvider.java:1304`）。给的片段 `(Equip) ...getEquipById(...)` 实现上确为 `new Equip(...)`，向下转型可行，但需确认白板装备未被额外加成污染 |

---

## 四、结论

**创意可行、观感方向正确，但当前实现不能直接落地。**

三条用户要求中：

- ①「挂载到道具上」——**没做到**（固定坐标，不跟随镜头与动画）
- ②「层级关系」——**被当前实现主动破坏**（front/back 压成同 z=0）
- ③「不越 UI」——**仅靠把 z 压到 0 歪打正着**，代价是光环被埋到背景之下

两个最大硬伤：**不跟随镜头**、**front/back 同层**。

**建议路径**：以 `itemaura.cpp` 为基线 → IDA 校准地址与 `CDrop`/相机字段 → 改为逐帧跟随 + `back=dropZ-1 / front=dropZ+1` → 服务端补 grade 字节并对齐 5 档 → 资源入 `Data/`（先 dry-run）→ 客户端实测（重点：移动镜头看是否脱钩、看是否被 UI 遮挡/遮挡 UI）。

---

## 五、决策项 —— 已确认（见 [PLAN.md](PLAN.md)）

> 用户已拍板：① 挂载方式 **A 逐帧跟随** ② z 策略 **A `back=dropZ-1 / front=dropZ+1`** ③ **确认改协议**（掉落封包新增 grade 字节）④ **5 档**（含 Mythic）⑤ 资源 **带光柱版**。
> 仍阻塞：**P1** IDA 环境（BeiDou-Client 路径）、**P2** `Effect.wz` 实测。详见 [PLAN.md](PLAN.md) 第四、五节。

原待决策项如下（留档）：

| # | 决策点 | 选项 | 建议 |
| --- | --- | --- | --- |
| 1 | 挂载方式 | A 逐帧跟随（每 tick 取 CDrop 世界坐标 − 相机偏移同步图层）／B 挂成 CDrop 子节点由引擎带／C 维持固定坐标 | A（B 若 IDA 找到 attach 入口更优） |
| 2 | z 值/层级策略 | A `back=dropZ-1, front=dropZ+1`（夹住道具）／B front、back 同层／C 全 z=0（现状） | A，需先实测 dropZ 与 UI 层边界 |
| 3 | 「不越 UI」口径 | 仅保证 z 序不盖 UI（必做）／还要限制光柱高度做视觉裁剪 | 先做 z 序；裁剪按实测 48px 评估为低风险，可选 |
| 4 | 是否改协议 | 确认在掉落封包新增 grade 字节（服务端+客户端双向）并接受掉落流程回归成本 | 需确认 |
| 5 | 档位数 | 5 档（含 Mythic，与服务端一致）／4 档（服务端削到 4） | 5 档 |
| 6 | 资源形态 | 采用「带光柱」版 or 「纯光环」版；webp 是否即最终帧 | 需以 `Effect.wz` 实测帧数与尺寸为准 |
| 7 | 验证环境 | BeiDou-Client 路径 + IDA 环境（本会话无 IDA MCP，地址校准需用户侧提供） | 需提供 |
| 8 | 性能上限 | 是否接受加并发光环上限（池化/LRU）防高密度图堆积 | 建议加 |

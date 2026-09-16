# 窗口商城 · 坐骑二级分类灌货

## 目标

在窗口现金商城中，以一级「坐骑」为树标注，二级「坐骑 / 鞍具 / 坐骑道具」挂载客户端全部 `190*` / `191*` / `226*`，固定点券价出售。不改经典 Commodity。

## 分类树

| 节点 | name | parent | legacy_tab:cat | 商品 |
|------|------|--------|----------------|------|
| L1 | 坐骑 | NULL | 11:0 | 无（仅标注） |
| L2 | 坐骑 | L1 | 11:1 | `190xxxx` |
| L2 | 鞍具 | L1 | 11:2 | `191xxxx` |
| L2 | 坐骑道具 | L1 | 11:3 | `226xxxx` |

不纳入 TamingMob 目录内的 `193/198/199` 等非卖资源。

## 固定点券价

| 类型 | 价格 (NX) |
|------|-----------|
| 坐骑 `190*` | 5000 |
| 鞍具 `191*` | 1000 |
| 坐骑道具 `226*` | 500 |

常量：`CashShopTaxonomy.MOUNT_PRICE` / `MOUNT_EQ_PRICE` / `MOUNT_USE_PRICE`。

## 实现要点

1. **Taxonomy** — [`CashShopTaxonomy.java`](../../../gms-server/src/main/java/org/gms/server/cashshop/CashShopTaxonomy.java)：`forItemId` 映射 190/191/226；`characterFolders` 含 `TamingMob`。
2. **Flyway** — [`V1.11.57__cashshop_mount_categories.sql`](../../../gms-server/src/main/resources/db/migration/V1.11.57__cashshop_mount_categories.sql)：幂等纠偏/创建分类树。
3. **灌货** — `WindowCashShopService.seedMountCatalog()`：扫服务端 `wz-zh-CN`/`wz` 与已配置客户端 `Data/Character/TamingMob`，Consume/226 走 `ItemInformationProvider.listIdsInItemPack`；从误挂分类卸下后挂到二级。
4. **触发** — 管理端「种子分类」(`seedDefaults`) 会 ensure 树 + 灌货；「从客户端同步」也会先 ensure 坐骑树，且 cashOnly 时仍扫 TamingMob + 并入 226。

## 客户端（必须）

窗口商城一级 Tab **仍由插件静态 `kTabs`/`kCats` 绘制**，不解析 `RESP_TAXONOMY`。坐骑需在 `BeiDou-ijl15/ezorsia/cashshop/cashshopwnd.cpp` 增加：

- `kTabs`：`{11, 坐骑}`
- `kCats`：`11:1 坐骑` / `11:2 鞍具` / `11:3 坐骑道具`（**不含**空标注 `11:0`）
- 8 个一级 Tab：`kTabW=88` / `kTabPitch=90`

构建：`E:\project\BeiDou-ijl15\build_once.bat` → PostBuild 覆盖 `E:\MXD\BeiDou-Client_S9\ijl15.dll`。

## 启用步骤

1. 服务启动跑完 Flyway `V1.11.57`。
2. 管理端「新商城数据」→ **灌入坐骑商品**（或「种子分类」/`POST /windowCashShop/v1/seedMountCatalog`）。
3. 换上新 `ijl15.dll` 后重启客户端；一级应出现「坐骑」，二级为坐骑/鞍具/坐骑道具。
4. 点二级分类应看到商品；一级标注 `11:0` 无货属预期。
5. 单测：`mvn -pl gms-server test -Dtest=CashShopTaxonomyTest`。

## 管理端层级

分类列表按 `legacyTab` 分组，组内按 `parentId` 缩进：一级「坐骑」(11:0) 下挂二级 11:1/2/3。

## 边界

- 仅窗口商城上架；骑乘技能/疲劳等玩法规则不变。
- `ItemConstants.isTaming` 仍限 `1902/1912`，未放宽。
- 无名 String 条目会以 ID 字符串入库；内存目录加载仍会跳过无法 resolve 名称的项时，以 `saveItem` 落库名为准。
- 工作目录可为仓库根或 `gms-server`；`seedMountCatalog` 会同时探测两处下的 `wz-zh-CN`/`wz`。

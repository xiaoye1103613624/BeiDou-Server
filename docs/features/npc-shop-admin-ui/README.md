# NPC 商店双视角管理

> 状态：已闭环  
> 适用客户端/协议版本：GMS v83  
> 项目名称：BeiDou-Server  
> 作者/日期：2026-09-15  
> 相关提交：  
> 产物目录：docs/features/npc-shop-admin-ui/

## 1. 背景与目标

- 原管理页是「商店列表 → 点查看整表换成商品」钻取，无法同时看到 NPC 与商品，也不能从商品反查 NPC，且不能新建/改绑/删除商店。
- 目标：按 NPC 左右主从管理商店与商品；按商品反查售卖该物品的 NPC；支持商店绑定 NPC 的增删改。
- 明确不做：游戏内购买协议、窗口现金商城、批量导入导出。

## 2. 影响范围

| 层级 | 路径/模块 | 变更类型 |
| --- | --- | --- |
| 服务端 | `ShopController` / `ShopService` | 新增商店 CRUD、物品反查 |
| 管理端/前端 | `gms-ui/src/views/game/npcShop` | 双 Tab 主从 UI |
| DB/配置 | `shops` / `shopitems` | 无迁移，沿用现表 |

## 3. 最终实现逻辑

### 3.1 主流程

- **按 NPC**：左侧分页商店列表（可按 shopId / npcId / npcName / 店内物品筛选），点击选中；右侧该店 `shopitems` 行内增删改。
- **按商品**：填写 itemId 或名称，列出售卖行（NPC、商店、价格等），可改价/删除，并可跳转到对应商店。
- 商店弹窗：新建可空 `shopId`（自增）或指定；`npcId` 必填；编辑只改绑 NPC。

### 3.2 数据 / 约束

- 表：`shops(shopid, npcid)`、`shopitems(shopitemid, shopid, itemid, price, pitch, position)`。
- 运行时 `ShopFactory.getShopForNPC` 按 NPC 取店，**同一 NPC 只允许一家商店**。
- 删除商店先删全部 `shopitems` 再删 `shops`，然后 `ShopFactory.reloadShops()`。
- NPC 名称解析为 `MISSINGNO` 视为不存在，拒绝绑定。

### 3.3 API（`/shop/v1`）

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/getShopList` | 商店分页（原有） |
| POST | `/getShopItemList` | 某店商品分页（原有） |
| POST | `/getItemShopList` | 按物品反查售卖行 |
| PUT | `/addShop` | 新建商店 |
| POST | `/updateShop` | 改绑 NPC |
| DELETE | `/deleteShop/{shopId}` | 级联删除商店 |
| PUT/POST/DELETE | `*ShopItem*` | 商品 CRUD（原有） |

## 4. 资源与同步

- 无 WZ / 客户端资源变更。
- 后台文案走 vue-i18n；服务端异常与日志走 `I18nUtil`。

## 5. 编码与 i18n

- 异常键：`ShopService.npcId.required` / `shop.notExist` / `npc.occupied` / `shopId.exists` / `npc.unknown` / `itemQuery.required`
- 日志键：`ShopService.addShop.info` / `updateShop.info` / `deleteShop.info`

## 6. 产物清单

| 类型 | 路径 | 说明 |
| --- | --- | --- |
| 文档 | README.md | 本文件 |

## 7. 验证记录

- `mvn -pl gms-server -DskipTests compile`：BUILD SUCCESS（JDK 21）
- `yarn type:check`：本页无错误；仓库仍有既有问题 `src/components/global-setting/block.vue` TS2418

## 8. 预留、风险与回滚

- 历史数据若同一 NPC 已有多家店，改绑/新建会按查询到的第一条占用店拦截；需人工清理重复行。
- 回滚：还原 Shop API 与 `npcShop/index.vue` 即可，无表结构变更。

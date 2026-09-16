# 管理端资源中心与 UI 统一

## 目标

- 管理端图标**只消费**服务端 `static/game-assets/{category}/{id}.png`
- 补齐/同步集中在 **客户端 → 游戏资源中心**（`/client/assetHub`）
- 各业务页不再自带「同步图标」按钮
- 展示组件统一：`ItemIcon` / `ItemIdCell`；版式：`PageContainer` → alert → `ProCard` → toolbar → 主区

## 解析链

1. 本地 `game-assets`（含从遗留 `/item-icons` promote）
2. 客户端 Data 旁已有 PNG（`web_png` / `item-icons`，一期不解码 IMG）
3. maplestory.io GMS/83（`gms.assets.cdn`）
4. 小册子镜像模板（`gms.assets.booklet`，默认 `static.mapleartale.com`）

写入**仅** `game-assets`；`/item-icons` 只读迁移。`ItemIconFiles.writePng` 已转发到 `SharedIconFiles`。

## API

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/asset/v1/info` | 落盘目录、Provider、客户端路径 |
| GET | `/asset/v1/resolve/{cat}/{id}` | 解析优先 URL |
| POST | `/asset/v1/ensure` | 批量补齐/强制刷新 |
| POST | `/asset/v1/ensureMissing` | ensure 且 force=false |
| POST | `/asset/v1/cache` | 兼容旧 IconCache 批量写法 |
| POST | `/icon/v1/cache` | 兼容旧懒缓存（内部转 AssetService） |

配置见 `application.yml` → `gms.assets`。

遗留兼容（勿再从管理端调用）：

- `POST /windowCashShop/v1/syncIcons`、`POST /clientAsset/v1/syncIcons` — 仅更新新商城 `icon_url`，PNG 经 AssetService 写入 game-assets

## 版式约定

```
PageContainer(title, description?)
  a-alert 说明写库/热重载/生效时机
  ProCard
    bd-page-toolbar 主操作（primary）/ 次要 / 危险二次确认
    主表或主区
```

物品列优先用 `ItemIdCell`（图标 + ID 输入），避免纯数字宽表。

组件：

- `gms-ui/src/components/item-icon` — 全局 `ItemIcon`
- `gms-ui/src/components/item-id-cell` — 全局 `ItemIdCell`

## 改造页清单

| 页 | 状态 |
| --- | --- |
| 游戏资源中心 `/client/assetHub` | 已落地 |
| InventoryUI | 先本地 game-assets，缺失再 CDN + `/icon/v1/cache` |
| 新商城（原窗口商城） | 展示名「新商城」；去图标同步按钮；点击类型仅 SHOW_ITEMS/OPEN_WINDOW |
| 签到 | ItemIdCell |
| 活动 | 奖励表/表单 ItemIdCell |
| 套装预览 | ItemIcon |
| 掉落 / NPC店 / 扭蛋 / 配方 / 宠物成长 / 经典商城 | 已用 ItemIcon |

## 新商城改名

仅展示文案「窗口商城」→「新商城」；路由/API/Java/表名不变。

DB：`V1.11.52__admin_asset_hub_menu.sql` 增加资源中心菜单，并更新 remark。

## 二期预留

客户端 IMG→PNG 抽取（orange-wz / 离线工具）接入 `AssetService.tryCopyClientPng` 之上。

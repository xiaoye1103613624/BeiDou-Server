# 套装管理（双语名称 + 一装多套）

## 决策摘要

| 代号 | 含义 |
|------|------|
| **1B** | 双语名称：`set_name_zh` + `set_name_en`；展示/客户端提示优先中文，无中文回退英文 |
| **2A** | 一件装备可同时属于多个套装；穿戴时对每个所属套装分别计件数并叠加加成 |

## 数据与运行时

- 表 `xy_set_item`（迁移 `V1.11.53__set_item_bilingual_multiset.sql`）
  - `set_name_zh` / `set_name_en`
  - `set_name` = 展示主名 `COALESCE(zh, en)`（兼容旧读路径）
  - `item_ids` 扩为 `TEXT`
- `SetDefinition.displayName()`：中文优先
- WZ 导入：英文 `setItemName` → `setNameEn`，**不覆盖**已有 `setNameZh`
- 中文名来源：客户端 `Data/Etc/SetItemInfo.img` 导出 → `classpath:setitem/set_item_names_zh.tsv`；启动/热重载回填空缺
- **套装数量（1B+2A）**：服务端保留原有 GMS 套装不动；从中文客户端追加**服务端没有的 setId** 到 [`gms-server/wz/Etc.wz/SetItemInfo.img.xml`](../../gms-server/wz/Etc.wz/SetItemInfo.img.xml)（约 61 → 600+）。「从 WZ 导入」只读服务端 WZ，不会读客户端路径
- 合并脚本：[`tools/merge_setitem_client_append.py`](../../tools/merge_setitem_client_append.py)；片段备份：`setitem-client-append.xml`
- `SetItemManager.itemToSets`：`Map<itemId, Set<setId>>`；`countEquippedSets` 对每个所属 set 各计 1（同 set 内同 itemId 去重）

## 管理端操作

1. 列表：主显中文、副显英文；搜索匹配 ID / 中英文名
2. 详情：分别编辑中文名、英文名
3. 装备清单：「批量添加」按分类 / ID / 名称模糊搜索（`informationSearch` types=`eqp`），多选可反复添加；已属其他套装显示 Tag，不拦截
4. 预览：图标 + ID + 名称 + 部位；布局 `flex-start`（修复原先 `space-between` 错位）

## 手工验收

1. 重启服务端使 Flyway 与热重载生效
2. 打开「强化 / 套装管理」，确认列表有中文优先显示
3. 编辑套装：填中英文名并保存；游戏内 tooltip 标题为中文（有中文时）
4. 批量添加装备：搜索多选 → 再次搜索再添；预览出现图标与中文名
5. 将同一装备加入两个启用套装 → 穿戴后两套件数均增加、加成叠加

## 风险

- 多套叠加会变强，无数值封顶（产品选择 2A）
- WZ 中本就有重复归属的套装，2A 后会真正双计

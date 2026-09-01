-- 阿尔泰皮肤：装备部位 Cap(1008900-1009999) → 点装武器(1708900-1709999)
-- 映射：new_id = old_id + 700000
-- 幂等：仅更新仍落在旧 Cap 区间的行；已迁到 170xxxx 的不会再改。
-- xy_cashshop_category_item.item_id 外键引用 xy_cashshop_item，需临时关闭 FK 校验后两边一起改。

SET FOREIGN_KEY_CHECKS = 0;

-- 角色背包 / 仓库 / 商城栏位中的旧皮肤 ID
UPDATE inventoryitems
SET itemid = itemid + 700000
WHERE itemid BETWEEN 1008900 AND 1009999;

-- 点券商城自定义商品表（若已种子导入过旧 Cap ID）
UPDATE xy_cashshop_item
SET item_id = item_id + 700000
WHERE item_id BETWEEN 1008900 AND 1009999;

UPDATE xy_cashshop_category_item
SET item_id = item_id + 700000
WHERE item_id BETWEEN 1008900 AND 1009999;

SET FOREIGN_KEY_CHECKS = 1;

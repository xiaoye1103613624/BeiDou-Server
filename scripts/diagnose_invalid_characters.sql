-- 诊断脚本：检查数据库中的无效角色
-- 用途：找出可能导致NPE的角色数据问题

USE beidou_dev_v1;

-- 1. 查找在characters表中但缺少必要字段的角色
-- 这可能导致Character.loadCharFromDB()返回null
SELECT
    c.id,
    c.accountid,
    c.name,
    '缺少必要字段' as issue_type
FROM characters c
WHERE
    c.id IS NULL
    OR c.name IS NULL
    OR c.accountid IS NULL
    OR c.level IS NULL
    OR c.job IS NULL;

-- 2. 查找孤立的角色（accountid不存在于accounts表）
SELECT
    c.id,
    c.accountid,
    c.name,
    '孤立角色' as issue_type
FROM characters c
LEFT JOIN accounts a ON c.accountid = a.id
WHERE a.id IS NULL;

-- 3. 查找inventory_items表中缺少对应角色的物品
SELECT
    DISTINCT ii.characterid,
    c.name,
    '物品孤立' as issue_type
FROM inventoryitems ii
LEFT JOIN characters c ON ii.characterid = c.id
WHERE c.id IS NULL
LIMIT 10;

-- 4. 查找inventory_equipment表中缺少对应物品的装备数据
SELECT
    DISTINCT ie.inventoryitemid,
    ii.characterid,
    c.name,
    '装备孤立' as issue_type
FROM inventoryequipment ie
LEFT JOIN inventoryitems ii ON ie.inventoryitemid = ii.id
LEFT JOIN characters c ON ii.characterid = c.id
WHERE ii.id IS NULL
LIMIT 10;

-- 5. 统计无效角色数量
SELECT
    'characters表中无效角色总数' as check_type,
    COUNT(*) as count
FROM characters c
WHERE
    c.id IS NULL
    OR c.name IS NULL
    OR c.accountid IS NULL
    OR c.level IS NULL
    OR c.job IS NULL
UNION ALL
SELECT
    '孤立角色总数',
    COUNT(*)
FROM characters c
LEFT JOIN accounts a ON c.accountid = a.id
WHERE a.id IS NULL
UNION ALL
SELECT
    '总角色数',
    COUNT(*)
FROM characters;

-- 6. 查看具体的账号及其角色数
SELECT
    a.name as account_name,
    COUNT(c.id) as character_count,
    GROUP_CONCAT(c.name SEPARATOR ', ') as character_names
FROM accounts a
LEFT JOIN characters c ON a.id = c.accountid
GROUP BY a.id, a.name
ORDER BY a.name;

-- 7. 查看admin账号的所有角色详情
SELECT
    c.id,
    c.name,
    c.level,
    c.job,
    c.world,
    c.map as map_id,
    c.gm as gm_level,
    c.creationdate,
    c.lastlogintime
FROM characters c
INNER JOIN accounts a ON c.accountid = a.id
WHERE a.name = 'admin'
ORDER BY c.world, c.creationdate;

-- 8. 数据一致性检查报告
SELECT
    '数据一致性检查报告' as report_type,
    CASE
        WHEN EXISTS (SELECT 1 FROM characters WHERE id IS NULL)
        THEN 'ERROR: characters表中存在NULL id'
        ELSE 'OK: characters表id完整'
    END as check_result
UNION ALL
SELECT
    '数据一致性检查报告',
    CASE
        WHEN EXISTS (SELECT 1 FROM characters WHERE name IS NULL OR name = '')
        THEN 'ERROR: characters表中存在空name'
        ELSE 'OK: characters表name完整'
    END
UNION ALL
SELECT
    '数据一致性检查报告',
    CASE
        WHEN EXISTS (
            SELECT 1 FROM characters c
            LEFT JOIN accounts a ON c.accountid = a.id
            WHERE a.id IS NULL
        )
        THEN 'ERROR: 存在孤立角色（accountid无效）'
        ELSE 'OK: 所有角色的accountid都有效'
    END
UNION ALL
SELECT
    '数据一致性检查报告',
    CASE
        WHEN EXISTS (
            SELECT 1 FROM inventoryitems ii
            LEFT JOIN characters c ON ii.characterid = c.id
            WHERE c.id IS NULL
        )
        THEN 'ERROR: 存在孤立物品（characterid无效）'
        ELSE 'OK: 所有物品的characterid都有效'
    END;

-- 9. 如果需要清理，可以使用以下查询（不要自动执行！）
-- 列出需要删除的无效角色
-- SELECT 'DELETE FROM characters WHERE id = ' || c.id || ';'
-- FROM characters c
-- LEFT JOIN accounts a ON c.accountid = a.id
-- WHERE a.id IS NULL
-- LIMIT 10;

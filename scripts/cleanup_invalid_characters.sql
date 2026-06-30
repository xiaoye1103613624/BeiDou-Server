-- 清理脚本：删除数据库中的无效角色
-- 警告：这是破坏性操作，请先备份数据库！

USE beidou_dev_v1;

-- ============================================================
-- 阶段1：备份无效数据（可选但强烈建议）
-- ============================================================

-- 创建备份表（如果不存在）
CREATE TABLE IF NOT EXISTS characters_backup_invalid AS
SELECT * FROM characters WHERE 1=0;

-- 备份无效角色及其关联数据
INSERT INTO characters_backup_invalid
SELECT c.*
FROM characters c
WHERE
    c.id IS NULL
    OR c.name IS NULL
    OR c.accountid IS NULL
    OR c.level IS NULL
    OR c.job IS NULL;

INSERT INTO characters_backup_invalid
SELECT c.*
FROM characters c
LEFT JOIN accounts a ON c.accountid = a.id
WHERE a.id IS NULL;

-- ============================================================
-- 阶段2：删除关联的物品数据
-- ============================================================

-- 查找并删除孤立的物品（物品对应的角色不存在）
DELETE FROM inventoryitems
WHERE characterid NOT IN (SELECT id FROM characters WHERE id IS NOT NULL);

-- 查找并删除孤立的装备数据（装备对应的物品不存在）
DELETE FROM inventoryequipment
WHERE inventoryitemid NOT IN (SELECT id FROM inventoryitems WHERE id IS NOT NULL);

-- ============================================================
-- 阶段3：删除无效的角色数据
-- ============================================================

-- 删除缺少必要字段的角色
DELETE FROM characters
WHERE
    id IS NULL
    OR name IS NULL
    OR accountid IS NULL
    OR level IS NULL
    OR job IS NULL;

-- 删除accountid无效的角色（孤立角色）
DELETE FROM characters c
WHERE NOT EXISTS (
    SELECT 1 FROM accounts a WHERE a.id = c.accountid
);

-- ============================================================
-- 阶段4：清理其他关联数据
-- ============================================================

-- 删除无效角色的技能数据
DELETE FROM skills
WHERE characterid NOT IN (SELECT id FROM characters WHERE id IS NOT NULL);

-- 删除无效角色的任务数据
DELETE FROM queststatus
WHERE characterid NOT IN (SELECT id FROM characters WHERE id IS NOT NULL);

-- 删除无效角色的好友数据
DELETE FROM buddies
WHERE characterid NOT IN (SELECT id FROM characters WHERE id IS NOT NULL);

-- 删除无效角色的冷却数据
DELETE FROM cooldowns
WHERE charid NOT IN (SELECT id FROM characters WHERE id IS NOT NULL);

-- ============================================================
-- 阶段5：验证和统计
-- ============================================================

-- 验证数据一致性
SELECT
    'admin账号的角色数' as check_type,
    COUNT(*) as count
FROM characters c
INNER JOIN accounts a ON c.accountid = a.id AND a.name = 'admin';

SELECT
    '总角色数' as check_type,
    COUNT(*) as count
FROM characters;

SELECT
    '有效账户数' as check_type,
    COUNT(*) as count
FROM accounts;

SELECT
    '孤立物品数（应该为0）' as check_type,
    COUNT(*) as count
FROM inventoryitems ii
LEFT JOIN characters c ON ii.characterid = c.id
WHERE c.id IS NULL;

SELECT
    '孤立装备数（应该为0）' as check_type,
    COUNT(*) as count
FROM inventoryequipment ie
LEFT JOIN inventoryitems ii ON ie.inventoryitemid = ii.id
WHERE ii.id IS NULL;

-- ============================================================
-- 恢复备份（如果需要）
-- ============================================================

-- 如果清理后发现问题，可以从备份恢复：
-- TRUNCATE TABLE characters;
-- INSERT INTO characters SELECT * FROM characters_backup_invalid;

-- ============================================================
-- 清理步骤说明
-- ============================================================

/*
使用说明：
1. 首先运行 diagnose_invalid_characters.sql 来检查问题
2. 备份数据库（通过MySQL备份工具）
3. 分阶段执行此脚本（不要一次性执行所有内容）
4. 每个阶段后检查结果
5. 如有问题，恢复备份

危险操作：
- 阶段2/3删除是不可逆的，请确保备份完整
- 不要在生产环境高峰期执行
- 建议在维护窗口执行

验证步骤：
1. 运行阶段5中的验证查询
2. 确保所有关联数据都有效
3. 重启游戏服务器进行完整测试
4. 监控日志是否还有NPE错误

回滚方案：
- 使用 characters_backup_invalid 表恢复数据
- 或从之前的完整数据库备份恢复

常见问题：
Q: 删除后其他表有数据不一致怎么办？
A: 运行阶段4来清理所有关联的孤立数据

Q: 如何避免这个问题再次发生？
A: 添加数据库外键约束来强制引用完整性
*/

-- ============================================================
-- 添加外键约束（防止未来出现同样问题）
-- ============================================================

-- 注意：这些操作可能需要禁用外键检查
-- SET FOREIGN_KEY_CHECKS = 0;

-- characters.accountid -> accounts.id
ALTER TABLE characters
ADD CONSTRAINT fk_characters_accountid
FOREIGN KEY (accountid) REFERENCES accounts(id)
ON DELETE CASCADE
ON UPDATE CASCADE;

-- inventoryitems.characterid -> characters.id
ALTER TABLE inventoryitems
ADD CONSTRAINT fk_inventoryitems_characterid
FOREIGN KEY (characterid) REFERENCES characters(id)
ON DELETE CASCADE
ON UPDATE CASCADE;

-- 其他关键外键约束...
-- （根据实际情况添加）

-- SET FOREIGN_KEY_CHECKS = 1;

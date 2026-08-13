-- 全局爆率启用/停用（列表可切换；停用后不参与游戏掉落加载）
ALTER TABLE `drop_data_global`
    ADD COLUMN `enabled` TINYINT NOT NULL DEFAULT 1 COMMENT '1启用 0停用' AFTER `comments`;

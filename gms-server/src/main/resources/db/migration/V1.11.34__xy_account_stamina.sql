-- 账号级体力（炼金/炼药等共用）
CREATE TABLE IF NOT EXISTS `xy_account_stamina` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `account_id` int NOT NULL COMMENT '账号ID',
  `stamina` int NOT NULL DEFAULT '100' COMMENT '当前体力，上限1000',
  `last_refill_date` datetime DEFAULT NULL COMMENT '上次每日体力发放时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_account_stamina_account` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账号级体力';

-- 角色天赋等级持久化
CREATE TABLE IF NOT EXISTS `xy_character_talent` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `character_id` INT NOT NULL,
  `talent_id` INT NOT NULL,
  `level` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_char_talent` (`character_id`, `talent_id`),
  KEY `idx_xy_character_talent_cid` (`character_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

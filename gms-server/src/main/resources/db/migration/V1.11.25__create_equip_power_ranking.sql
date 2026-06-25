-- 装备战力排行榜：每小时由后台定时任务重新计算并覆盖写入(仅保留前10名)
CREATE TABLE IF NOT EXISTS `equip_power_ranking`
(
    `id`            INT(11)      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `rank_no`       INT(11)      NOT NULL COMMENT '排名(1~10，数值越小排名越靠前)',
    `character_id`  INT(11)      NOT NULL COMMENT '角色ID',
    `character_name` VARCHAR(13) NOT NULL COMMENT '角色名字(快照，避免改名后历史排名显示错乱)',
    `power`         BIGINT(20)   NOT NULL DEFAULT '0' COMMENT '装备战力分数(由装备属性加权汇总计算得出)',
    `hat_item`      INT(11)      NOT NULL DEFAULT '0' COMMENT '帽子槽位装备ID(0=未装备)',
    `face_item`     INT(11)      NOT NULL DEFAULT '0' COMMENT '脸饰槽位装备ID(0=未装备)',
    `eye_item`      INT(11)      NOT NULL DEFAULT '0' COMMENT '眼饰槽位装备ID(0=未装备)',
    `earring_item`  INT(11)      NOT NULL DEFAULT '0' COMMENT '耳饰槽位装备ID(0=未装备)',
    `top_item`      INT(11)      NOT NULL DEFAULT '0' COMMENT '上衣槽位装备ID(0=未装备)',
    `pants_item`    INT(11)      NOT NULL DEFAULT '0' COMMENT '裤裙槽位装备ID(0=未装备)',
    `shoes_item`    INT(11)      NOT NULL DEFAULT '0' COMMENT '鞋子槽位装备ID(0=未装备)',
    `gloves_item`   INT(11)      NOT NULL DEFAULT '0' COMMENT '手套槽位装备ID(0=未装备)',
    `cape_item`     INT(11)      NOT NULL DEFAULT '0' COMMENT '披风槽位装备ID(0=未装备)',
    `pendant_item`  INT(11)      NOT NULL DEFAULT '0' COMMENT '项链槽位装备ID(0=未装备)',
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '本条排名数据的计算/更新时间',
    PRIMARY KEY (`id`),
    KEY `RANK_NO` (`rank_no`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '装备战力排行榜(每小时由后台定时任务重新计算，仅保留前10名快照)';

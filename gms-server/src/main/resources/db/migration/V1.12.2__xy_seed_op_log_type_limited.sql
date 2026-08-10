-- 初始化「限购」操作日志类型：材料商人限购兑换，广播样式为白底粉字(serverNotice type=5)
INSERT INTO `xy_op_log_type` (`op_type`, `name`, `notice_tag`, `chat_type`, `broadcast`, `enabled`, `sort_order`, `remark`) VALUES
(11, '限购', '限购系统', 5, 1, 1, 11, '材料商人限购兑换, 公告样式白底粉字')
ON DUPLICATE KEY UPDATE
    `name` = VALUES(`name`),
    `notice_tag` = VALUES(`notice_tag`),
    `chat_type` = VALUES(`chat_type`),
    `broadcast` = VALUES(`broadcast`),
    `enabled` = VALUES(`enabled`),
    `sort_order` = VALUES(`sort_order`),
    `remark` = VALUES(`remark`);
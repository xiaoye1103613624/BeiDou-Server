-- 初始化「装备注能」操作日志类型：⚡注能，广播样式白底蓝字(serverNotice type=6)
INSERT INTO `xy_op_log_type` (`op_type`, `name`, `notice_tag`, `chat_type`, `broadcast`, `enabled`, `sort_order`, `remark`) VALUES
(12, '注能', '注能系统', 6, 1, 1, 12, '装备注能(⚡), 公告样式白底蓝字')
ON DUPLICATE KEY UPDATE
    `name` = VALUES(`name`),
    `notice_tag` = VALUES(`notice_tag`),
    `chat_type` = VALUES(`chat_type`),
    `broadcast` = VALUES(`broadcast`),
    `enabled` = VALUES(`enabled`),
    `sort_order` = VALUES(`sort_order`),
    `remark` = VALUES(`remark`);
-- 初始化「宝石镶嵌」操作日志类型：宝X 镶嵌，广播样式白底粉字(serverNotice type=5)
INSERT INTO `xy_op_log_type` (`op_type`, `name`, `notice_tag`, `chat_type`, `broadcast`, `enabled`, `sort_order`, `remark`) VALUES
(13, '宝石镶嵌', '宝石镶嵌', 5, 1, 1, 13, '梅兹 宝石镶嵌(宝X)')
ON DUPLICATE KEY UPDATE
    `name` = VALUES(`name`),
    `notice_tag` = VALUES(`notice_tag`),
    `chat_type` = VALUES(`chat_type`),
    `broadcast` = VALUES(`broadcast`),
    `enabled` = VALUES(`enabled`),
    `sort_order` = VALUES(`sort_order`),
    `remark` = VALUES(`remark`);
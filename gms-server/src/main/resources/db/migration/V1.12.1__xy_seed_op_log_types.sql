-- 初始化操作日志类型样式绑定
INSERT INTO `xy_op_log_type` (`op_type`, `name`, `notice_tag`, `chat_type`, `broadcast`, `enabled`, `sort_order`, `remark`) VALUES
(0, '其他', '', 0, 0, 1, 0, '未绑定类型的兜底(默认白底黑字, 不广播)'),
(1, '兑换', '兑换系统', 6, 1, 1, 1, '材料/xx兑换, 公告样式白底蓝字'),
(2, '打造', '打造系统', 6, 1, 1, 2, '打造装备/道具'),
(3, '锻造', '锻造系统', 6, 1, 1, 3, '锻造/重铸'),
(4, '强化', '强化系统', 6, 1, 1, 4, '装备强化/潜能'),
(5, '炼金', '炼金系统', 6, 1, 1, 5, '炼金合成'),
(6, '回收', '回收系统', 6, 1, 1, 6, '道具回收'),
(7, '商店', '', 6, 0, 1, 7, '商店购买/卖出(一般不广播)'),
(8, '赞助', '赞助系统', 6, 1, 1, 8, '赞助到账'),
(9, '管理', '', 0, 0, 1, 9, '管理操作(不广播)'),
(10, 'GM指令', '', 0, 0, 1, 10, 'GM指令(不广播)')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`), `notice_tag` = VALUES(`notice_tag`), `chat_type` = VALUES(`chat_type`), `broadcast` = VALUES(`broadcast`), `enabled` = VALUES(`enabled`), `sort_order` = VALUES(`sort_order`), `remark` = VALUES(`remark`);
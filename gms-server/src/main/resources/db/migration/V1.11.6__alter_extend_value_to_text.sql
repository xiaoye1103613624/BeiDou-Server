-- 扩展字段值由 VARCHAR(255) 改为 TEXT，用于存储副本进度等较大 JSON 数据
ALTER TABLE `extend_value`
    MODIFY COLUMN `extend_value` TEXT COMMENT '扩展字段值';

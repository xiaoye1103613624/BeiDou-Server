-- 仓库配置表增加排序字段
ALTER TABLE xy_warehouse_config
    ADD COLUMN sort_order INT DEFAULT 200 COMMENT '排序号（升序，数字越小越靠前，默认200）'
    AFTER enabled;

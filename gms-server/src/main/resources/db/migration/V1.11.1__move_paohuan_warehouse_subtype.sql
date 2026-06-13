-- 将跑环和仓库相关参数从独立分类移入"游戏机制"分类，统一参数归类
UPDATE `game_config` SET `config_sub_type` = 'Game Mechanics' WHERE `config_sub_type` = 'Paohuan';
UPDATE `game_config` SET `config_sub_type` = 'Game Mechanics' WHERE `config_sub_type` = 'Warehouse';

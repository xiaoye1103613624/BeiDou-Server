-- 客户端 Data 根目录：语义从「仅窗口商城」扩展为全局共用（技改同步等）
-- config_code 保持 window_cashshop_client_data_path 以兼容既有配置与 API

UPDATE `game_config`
SET `config_sub_type` = 'Client',
    `config_desc`    = '客户端 Data 根目录（窗口商城校验/技改客户端同步等共用；绝对路径；空=跳过需客户端路径的功能）',
    `update_time`    = NOW()
WHERE `config_code` = 'window_cashshop_client_data_path';

UPDATE `lang_resources`
SET `lang_value` = '客户端 Data 根目录（窗口商城/技改等共用；绝对路径；空=跳过）'
WHERE `lang_type` = 'zh-CN'
  AND `lang_base` = 'game_config'
  AND `lang_code` = 'window_cashshop_client_data_path';

UPDATE `lang_resources`
SET `lang_value` = 'Client Data root (shared by window cash-shop, skill-tech sync, etc.; absolute path; empty skips)'
WHERE `lang_type` = 'en-US'
  AND `lang_base` = 'game_config'
  AND `lang_code` = 'window_cashshop_client_data_path';

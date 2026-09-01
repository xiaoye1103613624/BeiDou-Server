-- window_cashshop_client_data_path 原属「Cash Shop」子类，管理端无对应 i18n，显示为裸 key；归入 GM
UPDATE `game_config`
SET `config_sub_type` = 'GM',
    `update_time`    = NOW()
WHERE `config_code` = 'window_cashshop_client_data_path'
  AND `config_sub_type` IN ('Cash Shop', 'CashShop');

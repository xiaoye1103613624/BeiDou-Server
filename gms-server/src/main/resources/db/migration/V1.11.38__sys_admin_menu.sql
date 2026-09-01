-- 管理后台系统菜单（侧栏可配置）
CREATE TABLE IF NOT EXISTS `sys_admin_menu` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT,
    `parent_id`     BIGINT       NOT NULL DEFAULT 0 COMMENT '父菜单ID，0为根',
    `name`          VARCHAR(64)  NOT NULL COMMENT '路由 name，全局唯一',
    `path`          VARCHAR(255) NOT NULL DEFAULT '' COMMENT '路由 path（子级为相对路径，外链为完整 URL）',
    `locale_key`    VARCHAR(128) NOT NULL DEFAULT '' COMMENT '前端 i18n key',
    `icon`          VARCHAR(64)           DEFAULT NULL,
    `sort_order`    INT          NOT NULL DEFAULT 0,
    `menu_type`     TINYINT      NOT NULL DEFAULT 1 COMMENT '0目录 1菜单 2外链',
    `roles`         VARCHAR(128)          DEFAULT 'admin' COMMENT '逗号分隔角色',
    `requires_auth` TINYINT      NOT NULL DEFAULT 1,
    `hide_in_menu`  TINYINT      NOT NULL DEFAULT 0,
    `enabled`       TINYINT      NOT NULL DEFAULT 1,
    `remark`        VARCHAR(255)          DEFAULT NULL,
    `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sys_admin_menu_name` (`name`),
    KEY `idx_sys_admin_menu_parent_sort` (`parent_id`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理后台系统菜单';

-- 种子：现有侧栏分类 + 菜单管理页本身
INSERT INTO `sys_admin_menu`
(`id`, `parent_id`, `name`, `path`, `locale_key`, `icon`, `sort_order`, `menu_type`, `roles`, `requires_auth`, `hide_in_menu`, `enabled`, `remark`)
VALUES
(1,  0, 'dashboard',          '/dashboard',                                          'menu.dashboard',                   'icon-dashboard',  0, 0, 'admin', 1, 0, 1, '仪表盘'),
(2,  1, 'Workplace',          'workplace',                                           'menu.dashboard.workplace',         NULL,              0, 1, 'admin', 1, 0, 1, NULL),
(3,  1, 'informationSearch',  'informationSearch',                                   'menu.dashboard.informationSearch', NULL,              1, 1, 'admin', 1, 0, 1, NULL),

(4,  0, 'daily',              '/daily',                                              'menu.daily',                       'icon-calendar',   1, 0, 'admin', 1, 0, 1, '日常系统'),
(5,  4, 'DailyWeather',       'weather',                                             'menu.game.weather',                NULL,              0, 1, 'admin', 1, 0, 1, NULL),
(6,  4, 'DailyCommandInfo',   'commandInfo',                                         'menu.game.command',                NULL,              1, 1, 'admin', 1, 0, 1, NULL),
(7,  4, 'DailyAutoban',       'autoban',                                             'menu.game.autoban',                NULL,              2, 1, 'admin', 1, 0, 1, NULL),
(8,  4, 'DailyFile',          'file',                                                'menu.game.file',                   NULL,              3, 1, 'admin', 1, 0, 1, NULL),
(9,  4, 'DailyCheckin',       'dailyCheckin',                                        'menu.game.dailyCheckin',           NULL,              4, 1, 'admin', 1, 0, 1, NULL),
(10, 4, 'DailyActivity',      'activity',                                            'menu.game.activity',               NULL,              5, 1, 'admin', 1, 0, 1, NULL),

(11, 0, 'growth',             '/growth',                                             'menu.growth',                      'icon-rise',       2, 0, 'admin', 1, 0, 1, '成长系统'),
(12, 11,'GrowthSetItem',      'setItem',                                             'menu.game.setItem',                NULL,              0, 1, 'admin', 1, 0, 1, NULL),
(13, 11,'GrowthDrop',         'drop',                                                'menu.game.drop',                   NULL,              1, 1, 'admin', 1, 0, 1, NULL),
(14, 11,'GrowthGlobalDrop',   'drop/global',                                         'menu.game.drop.global',            NULL,              2, 1, 'admin', 1, 0, 1, NULL),
(15, 11,'GrowthGachapon',     'gachapon',                                            'menu.game.gachapon',               NULL,              3, 1, 'admin', 1, 0, 1, NULL),
(16, 11,'GrowthPetGrowth',    'petGrowth',                                           'menu.game.petGrowth',              NULL,              4, 1, 'admin', 1, 0, 1, NULL),

(17, 0, 'member',             '/member',                                             'menu.member',                      'icon-user-group', 3, 0, 'admin', 1, 0, 1, '会员中心'),
(18, 17,'MemberAccountList',  'list',                                                'menu.account.list',                NULL,              0, 1, 'admin', 1, 0, 1, NULL),
(19, 17,'MemberPlayerList',   'player',                                              'menu.account.player',              NULL,              1, 1, 'admin', 1, 0, 1, NULL),
(20, 17,'MemberCashShop',     'cashShop',                                            'menu.game.cashShop',               NULL,              2, 1, 'admin', 1, 0, 1, NULL),
(21, 17,'MemberInventory',    'inventory',                                           'menu.game.inventory',              NULL,              3, 1, 'admin', 1, 0, 1, NULL),
(22, 17,'MemberRanking',      'ranking',                                             'menu.member.ranking',              NULL,              4, 1, 'admin', 1, 0, 1, NULL),

(23, 0, 'game',               '/game',                                               'menu.game',                        'icon-dice',       4, 0, 'admin', 1, 0, 1, '游戏管理'),
(24, 23,'Config',             'config',                                              'menu.game.config',                 NULL,              0, 1, 'admin', 1, 0, 1, NULL),
(25, 23,'NpcShop',            'npcShop',                                             'menu.game.npcShop',                NULL,              1, 1, 'admin', 1, 0, 1, NULL),
(26, 23,'windowCashShop',     'windowCashShop',                                      'menu.game.windowCashShop',         NULL,              2, 1, 'admin', 1, 0, 1, NULL),
(27, 23,'SysMenu',            'sysMenu',                                             'menu.game.sysMenu',                NULL,              3, 1, 'admin', 1, 0, 1, '菜单管理'),

(28, 0, 'arcoWebsite',        'https://arco.design/vue/docs/start',                  'menu.arco',                        'icon-link',       8, 2, 'admin', 1, 0, 1, '外链'),
(29, 0, 'beiDou',             'https://github.com/BeiDouMS/BeiDou-Server',            'menu.beiDou',                      'icon-github',    99, 2, 'admin', 1, 0, 1, '外链');

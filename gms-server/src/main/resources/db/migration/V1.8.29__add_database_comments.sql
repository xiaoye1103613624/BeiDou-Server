-- =====================================================
-- 批量添加数据库表和字段注释
-- 为所有缺少注释的表和字段添加中文注释
-- 已含注释的表（game_config, xy_* 系列等）跳过不处理
-- =====================================================

-- ==========================================
-- 1. accounts - 账号表
-- ==========================================
ALTER TABLE `accounts`
    COMMENT '账号表',
    MODIFY COLUMN `id`             INT(11)      NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `name`           VARCHAR(13)  NOT NULL DEFAULT '' COMMENT '账号名',
    MODIFY COLUMN `password`       VARCHAR(128) NOT NULL DEFAULT '' COMMENT '密码（BCrypt加密）',
    MODIFY COLUMN `pin`            VARCHAR(10)  NOT NULL DEFAULT '' COMMENT 'PIN码',
    MODIFY COLUMN `pic`            VARCHAR(26)  NOT NULL DEFAULT '' COMMENT 'PIC码',
    MODIFY COLUMN `loggedin`       TINYINT(4)   NOT NULL DEFAULT '0' COMMENT '在线状态（0=离线 1=登录服 2=游戏服）',
    MODIFY COLUMN `lastlogin`      TIMESTAMP    NULL     DEFAULT NULL COMMENT '最后登录时间',
    MODIFY COLUMN `createdat`      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    MODIFY COLUMN `birthday`       DATE         NOT NULL DEFAULT '2005-05-11' COMMENT '生日',
    MODIFY COLUMN `banned`         TINYINT(1)   NOT NULL DEFAULT '0' COMMENT '是否封禁（0=否 1=是）',
    MODIFY COLUMN `banreason`      TEXT         COMMENT '封禁原因',
    MODIFY COLUMN `macs`           TINYTEXT     COMMENT 'MAC地址列表',
    MODIFY COLUMN `nxCredit`       INT(11)      DEFAULT NULL COMMENT '点卷',
    MODIFY COLUMN `maplePoint`     INT(11)      DEFAULT NULL COMMENT '抵用券',
    MODIFY COLUMN `nxPrepaid`      INT(11)      DEFAULT NULL COMMENT '信用券',
    MODIFY COLUMN `characterslots` TINYINT(2)   NOT NULL DEFAULT '3' COMMENT '角色槽位数',
    MODIFY COLUMN `gender`         TINYINT(2)   NOT NULL DEFAULT '10' COMMENT '性别（0=男 1=女 10=未设置）',
    MODIFY COLUMN `tempban`        TIMESTAMP    NOT NULL DEFAULT '2005-05-11 00:00:00' COMMENT '临时封禁到期时间',
    MODIFY COLUMN `greason`        TINYINT(4)   NOT NULL DEFAULT '0' COMMENT '封禁原因码',
    MODIFY COLUMN `tos`            TINYINT(1)   NOT NULL DEFAULT '0' COMMENT '是否同意服务条款（0=否 1=是）',
    MODIFY COLUMN `sitelogged`     TEXT         COMMENT '网站登录记录',
    MODIFY COLUMN `webadmin`       INT(1)       DEFAULT '0' COMMENT '是否网站管理员（0=否 1=是）',
    MODIFY COLUMN `nick`           VARCHAR(20)  DEFAULT NULL COMMENT '昵称',
    MODIFY COLUMN `mute`           INT(1)       DEFAULT '0' COMMENT '是否禁言（0=否 1=是）',
    MODIFY COLUMN `email`          VARCHAR(45)  DEFAULT NULL COMMENT '邮箱',
    MODIFY COLUMN `ip`             TEXT         COMMENT 'IP地址记录',
    MODIFY COLUMN `rewardpoints`   INT(11)      NOT NULL DEFAULT '0' COMMENT '奖励积分',
    MODIFY COLUMN `votepoints`     INT(11)      NOT NULL DEFAULT '0' COMMENT '投票积分',
    MODIFY COLUMN `hwid`           VARCHAR(12)  NOT NULL DEFAULT '' COMMENT '硬件ID',
    MODIFY COLUMN `language`       INT(1)       NOT NULL DEFAULT '3' COMMENT '语言偏好（1=en-US 2=en-US 3=zh-CN）';

-- ==========================================
-- 2. alliance - 家族联盟表
-- ==========================================
ALTER TABLE `alliance`
    COMMENT '家族联盟表',
    MODIFY COLUMN `id`       INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `name`     VARCHAR(13) NOT NULL COMMENT '联盟名称',
    MODIFY COLUMN `capacity` INT(10) UNSIGNED NOT NULL DEFAULT '2' COMMENT '最大容纳公会数',
    MODIFY COLUMN `notice`   VARCHAR(20) NOT NULL DEFAULT '' COMMENT '联盟公告',
    MODIFY COLUMN `rank1`    VARCHAR(11) NOT NULL DEFAULT 'Master' COMMENT '联盟第1级称号',
    MODIFY COLUMN `rank2`    VARCHAR(11) NOT NULL DEFAULT 'Jr. Master' COMMENT '联盟第2级称号',
    MODIFY COLUMN `rank3`    VARCHAR(11) NOT NULL DEFAULT 'Member' COMMENT '联盟第3级称号',
    MODIFY COLUMN `rank4`    VARCHAR(11) NOT NULL DEFAULT 'Member' COMMENT '联盟第4级称号',
    MODIFY COLUMN `rank5`    VARCHAR(11) NOT NULL DEFAULT 'Member' COMMENT '联盟第5级称号';

-- ==========================================
-- 3. allianceguilds - 家族联盟-公会关联表
-- ==========================================
ALTER TABLE `allianceguilds`
    COMMENT '家族联盟-公会关联表',
    MODIFY COLUMN `id`         INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `allianceid` INT(10) NOT NULL DEFAULT '-1' COMMENT '联盟ID（关联alliance.id）',
    MODIFY COLUMN `guildid`    INT(10) NOT NULL DEFAULT '-1' COMMENT '公会ID（关联guilds.guildid）';

-- ==========================================
-- 4. area_info - 区域信息表
-- ==========================================
ALTER TABLE `area_info`
    COMMENT '区域信息表（记录角色在各区域的状态）',
    MODIFY COLUMN `id`     INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `charid` INT(11) NOT NULL COMMENT '角色ID（关联characters.id）',
    MODIFY COLUMN `area`   INT(11) NOT NULL COMMENT '区域ID',
    MODIFY COLUMN `info`   VARCHAR(200) NOT NULL COMMENT '区域状态信息';

-- ==========================================
-- 5. bbs_replies - 留言板回复表
-- ==========================================
ALTER TABLE `bbs_replies`
    COMMENT '留言板回复表（公会BBS）',
    MODIFY COLUMN `replyid`   INT(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '回复ID',
    MODIFY COLUMN `threadid`  INT(10) unsigned NOT NULL COMMENT '帖子ID（关联bbs_threads.threadid）',
    MODIFY COLUMN `postercid` INT(10) unsigned NOT NULL COMMENT '回复者角色ID',
    MODIFY COLUMN `TIMESTAMP` BIGINT(20) unsigned NOT NULL COMMENT '回复时间戳',
    MODIFY COLUMN `content`   VARCHAR(26) NOT NULL DEFAULT '' COMMENT '回复内容';

-- ==========================================
-- 6. bbs_threads - 留言板帖子表
-- ==========================================
ALTER TABLE `bbs_threads`
    COMMENT '留言板帖子表（公会BBS）',
    MODIFY COLUMN `threadid`      INT(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
    MODIFY COLUMN `postercid`     INT(10) unsigned NOT NULL COMMENT '发帖者角色ID',
    MODIFY COLUMN `name`          VARCHAR(26) NOT NULL DEFAULT '' COMMENT '帖子标题',
    MODIFY COLUMN `TIMESTAMP`     BIGINT(20) unsigned NOT NULL COMMENT '发帖时间戳',
    MODIFY COLUMN `icon`          SMALLINT(5) unsigned NOT NULL COMMENT '帖子图标',
    MODIFY COLUMN `replycount`    SMALLINT(5) unsigned NOT NULL DEFAULT '0' COMMENT '回复数',
    MODIFY COLUMN `startpost`     TEXT NOT NULL COMMENT '帖子正文',
    MODIFY COLUMN `guildid`       INT(10) unsigned NOT NULL COMMENT '公会ID（关联guilds.guildid）',
    MODIFY COLUMN `localthreadid` INT(10) unsigned NOT NULL COMMENT '公会内帖子编号';

-- ==========================================
-- 7. bosslog_daily - 每日BOSS击杀记录表
-- ==========================================
ALTER TABLE `bosslog_daily`
    COMMENT '每日BOSS击杀记录表',
    MODIFY COLUMN `id`          INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `characterid` INT(11) NOT NULL COMMENT '角色ID（关联characters.id）',
    MODIFY COLUMN `bosstype`    ENUM('ZAKUM','HORNTAIL','PINKBEAN','SCARGA','PAPULATUS') NOT NULL COMMENT 'BOSS类型',
    MODIFY COLUMN `attempttime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '击杀时间';

-- ==========================================
-- 8. bosslog_weekly - 每周BOSS击杀记录表
-- ==========================================
ALTER TABLE `bosslog_weekly`
    COMMENT '每周BOSS击杀记录表',
    MODIFY COLUMN `id`          INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `characterid` INT(11) NOT NULL COMMENT '角色ID（关联characters.id）',
    MODIFY COLUMN `bosstype`    ENUM('ZAKUM','HORNTAIL','PINKBEAN','SCARGA','PAPULATUS') NOT NULL COMMENT 'BOSS类型',
    MODIFY COLUMN `attempttime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '击杀时间';

-- ==========================================
-- 9. buddies - 好友表
-- ==========================================
ALTER TABLE `buddies`
    COMMENT '好友表',
    MODIFY COLUMN `id`          INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `characterid` INT(11) NOT NULL COMMENT '角色ID（关联characters.id）',
    MODIFY COLUMN `buddyid`     INT(11) NOT NULL COMMENT '好友角色ID',
    MODIFY COLUMN `pending`     TINYINT(4) NOT NULL DEFAULT '0' COMMENT '是否待确认（0=已添加 1=待确认）',
    MODIFY COLUMN `group`       VARCHAR(17) DEFAULT '0' COMMENT '好友分组';

-- ==========================================
-- 10. characters - 角色表
-- ==========================================
ALTER TABLE `characters`
    COMMENT '角色表',
    MODIFY COLUMN `id`                   INT(11)             NOT NULL AUTO_INCREMENT COMMENT '自增主键（角色ID）',
    MODIFY COLUMN `accountid`            INT(11)             NOT NULL DEFAULT '0' COMMENT '账号ID（关联accounts.id）',
    MODIFY COLUMN `world`                INT(11)             NOT NULL DEFAULT '0' COMMENT '所在大区',
    MODIFY COLUMN `name`                 VARCHAR(13)         NOT NULL DEFAULT '' COMMENT '角色名',
    MODIFY COLUMN `level`                INT(11)             NOT NULL DEFAULT '1' COMMENT '等级',
    MODIFY COLUMN `exp`                  INT(11)             NOT NULL DEFAULT '0' COMMENT '经验值',
    MODIFY COLUMN `gachaexp`             INT(11)             NOT NULL DEFAULT '0' COMMENT '装备经验',
    MODIFY COLUMN `str`                  INT(11)             NOT NULL DEFAULT '12' COMMENT '力量',
    MODIFY COLUMN `dex`                  INT(11)             NOT NULL DEFAULT '5' COMMENT '敏捷',
    MODIFY COLUMN `luk`                  INT(11)             NOT NULL DEFAULT '4' COMMENT '运气',
    MODIFY COLUMN `int`                  INT(11)             NOT NULL DEFAULT '4' COMMENT '智力',
    MODIFY COLUMN `hp`                   INT(11)             NOT NULL DEFAULT '50' COMMENT '当前血量',
    MODIFY COLUMN `mp`                   INT(11)             NOT NULL DEFAULT '5' COMMENT '当前蓝量',
    MODIFY COLUMN `maxhp`                INT(11)             NOT NULL DEFAULT '50' COMMENT '最大血量',
    MODIFY COLUMN `maxmp`                INT(11)             NOT NULL DEFAULT '5' COMMENT '最大蓝量',
    MODIFY COLUMN `meso`                 INT(11)             NOT NULL DEFAULT '0' COMMENT '金币',
    MODIFY COLUMN `hpMpUsed`             INT(11) UNSIGNED    NOT NULL DEFAULT '0' COMMENT '已使用的洗血点数',
    MODIFY COLUMN `job`                  INT(11)             NOT NULL DEFAULT '0' COMMENT '职业ID',
    MODIFY COLUMN `skincolor`            INT(11)             NOT NULL DEFAULT '0' COMMENT '皮肤颜色',
    MODIFY COLUMN `gender`               INT(11)             NOT NULL DEFAULT '0' COMMENT '性别（0=男 1=女）',
    MODIFY COLUMN `fame`                 INT(11)             NOT NULL DEFAULT '0' COMMENT '人气',
    MODIFY COLUMN `fquest`               INT(11)             NOT NULL DEFAULT '0' COMMENT '已完成的任务数量',
    MODIFY COLUMN `hair`                 INT(11)             NOT NULL DEFAULT '0' COMMENT '发型ID',
    MODIFY COLUMN `face`                 INT(11)             NOT NULL DEFAULT '0' COMMENT '脸型ID',
    MODIFY COLUMN `ap`                   INT(11)             NOT NULL DEFAULT '0' COMMENT '可用属性点',
    MODIFY COLUMN `sp`                   VARCHAR(128)        NOT NULL DEFAULT '0,0,0,0,0,0,0,0,0,0' COMMENT '可用技能点（逗号分隔各职业群）',
    MODIFY COLUMN `map`                  INT(11)             NOT NULL DEFAULT '0' COMMENT '当前地图ID',
    MODIFY COLUMN `spawnpoint`           INT(11)             NOT NULL DEFAULT '0' COMMENT '出生点ID',
    MODIFY COLUMN `gm`                   TINYINT(1)          NOT NULL DEFAULT '0' COMMENT 'GM等级（0=普通玩家 >0=GM等级）',
    MODIFY COLUMN `party`                INT(11)             NOT NULL DEFAULT '0' COMMENT '所在队伍ID',
    MODIFY COLUMN `buddyCapacity`        INT(11)             NOT NULL DEFAULT '25' COMMENT '好友栏容量',
    MODIFY COLUMN `createdate`           TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    MODIFY COLUMN `rank`                 INT(10) UNSIGNED    NOT NULL DEFAULT '1' COMMENT '全区排名',
    MODIFY COLUMN `rankMove`             INT(11)             NOT NULL DEFAULT '0' COMMENT '排名变化（正=上升 负=下降）',
    MODIFY COLUMN `jobRank`              INT(10) UNSIGNED    NOT NULL DEFAULT '1' COMMENT '职业排名',
    MODIFY COLUMN `jobRankMove`          INT(11)             NOT NULL DEFAULT '0' COMMENT '职业排名变化',
    MODIFY COLUMN `guildid`              INT(10) UNSIGNED    NOT NULL DEFAULT '0' COMMENT '公会ID（关联guilds.guildid）',
    MODIFY COLUMN `guildrank`            INT(10) UNSIGNED    NOT NULL DEFAULT '5' COMMENT '公会内等级（1=会长 2=副会长 3~5=成员）',
    MODIFY COLUMN `messengerid`          INT(10) UNSIGNED    NOT NULL DEFAULT '0' COMMENT '信使ID',
    MODIFY COLUMN `messengerposition`    INT(10) UNSIGNED    NOT NULL DEFAULT '4' COMMENT '信使位置',
    MODIFY COLUMN `mountlevel`           INT(9)              NOT NULL DEFAULT '1' COMMENT '坐骑等级',
    MODIFY COLUMN `mountexp`             INT(9)              NOT NULL DEFAULT '0' COMMENT '坐骑经验',
    MODIFY COLUMN `mounttiredness`       INT(9)              NOT NULL DEFAULT '0' COMMENT '坐骑疲劳度',
    MODIFY COLUMN `omokwins`             INT(11)             NOT NULL DEFAULT '0' COMMENT '五子棋胜场',
    MODIFY COLUMN `omoklosses`           INT(11)             NOT NULL DEFAULT '0' COMMENT '五子棋负场',
    MODIFY COLUMN `omokties`             INT(11)             NOT NULL DEFAULT '0' COMMENT '五子棋平局',
    MODIFY COLUMN `matchcardwins`        INT(11)             NOT NULL DEFAULT '0' COMMENT '记忆卡片胜场',
    MODIFY COLUMN `matchcardlosses`      INT(11)             NOT NULL DEFAULT '0' COMMENT '记忆卡片负场',
    MODIFY COLUMN `matchcardties`        INT(11)             NOT NULL DEFAULT '0' COMMENT '记忆卡片平局',
    MODIFY COLUMN `MerchantMesos`        INT(11)                      DEFAULT '0' COMMENT '雇佣商店出售所得金币',
    MODIFY COLUMN `HasMerchant`          TINYINT(1)                   DEFAULT '0' COMMENT '是否有雇佣商店（0=否 1=是）',
    MODIFY COLUMN `equipslots`           INT(11)             NOT NULL DEFAULT '24' COMMENT '装备栏容量',
    MODIFY COLUMN `useslots`             INT(11)             NOT NULL DEFAULT '24' COMMENT '消耗栏容量',
    MODIFY COLUMN `setupslots`           INT(11)             NOT NULL DEFAULT '24' COMMENT '设置栏容量',
    MODIFY COLUMN `etcslots`             INT(11)             NOT NULL DEFAULT '24' COMMENT '其他栏容量',
    MODIFY COLUMN `familyId`             INT(11)             NOT NULL DEFAULT '-1' COMMENT '学院ID（-1=无学院）',
    MODIFY COLUMN `monsterbookcover`     INT(11)             NOT NULL DEFAULT '0' COMMENT '怪物图鉴封面',
    MODIFY COLUMN `allianceRank`         INT(10)             NOT NULL DEFAULT '5' COMMENT '联盟内等级',
    MODIFY COLUMN `vanquisherStage`      INT(11) UNSIGNED    NOT NULL DEFAULT '0' COMMENT '武陵道场征服者阶段',
    MODIFY COLUMN `ariantPoints`         INT(11) UNSIGNED    NOT NULL DEFAULT '0' COMMENT '阿里安特竞技场点数',
    MODIFY COLUMN `dojoPoints`           INT(11) UNSIGNED    NOT NULL DEFAULT '0' COMMENT '武陵道场点数',
    MODIFY COLUMN `lastDojoStage`        INT(10) UNSIGNED    NOT NULL DEFAULT '0' COMMENT '最后挑战的道场阶段',
    MODIFY COLUMN `finishedDojoTutorial` TINYINT(1) UNSIGNED NOT NULL DEFAULT '0' COMMENT '是否完成道场教程（0=未完成 1=已完成）',
    MODIFY COLUMN `vanquisherKills`      INT(11) UNSIGNED    NOT NULL DEFAULT '0' COMMENT '武陵征服者击杀数',
    MODIFY COLUMN `summonValue`          INT(11) UNSIGNED    NOT NULL DEFAULT '0' COMMENT '召唤值',
    MODIFY COLUMN `partnerId`            INT(11)             NOT NULL DEFAULT '0' COMMENT '伴侣角色ID',
    MODIFY COLUMN `marriageItemId`       INT(11)             NOT NULL DEFAULT '0' COMMENT '结婚戒指物品ID',
    MODIFY COLUMN `reborns`              INT(5)              NOT NULL DEFAULT '0' COMMENT '转生次数',
    MODIFY COLUMN `PQPoints`             INT(11)             NOT NULL DEFAULT '0' COMMENT '组队任务点数',
    MODIFY COLUMN `dataString`           VARCHAR(64)         NOT NULL DEFAULT '' COMMENT '自定义数据字符串',
    MODIFY COLUMN `lastLogoutTime`       TIMESTAMP           NOT NULL DEFAULT '2015-01-01 05:00:00' COMMENT '最后登出时间',
    MODIFY COLUMN `lastExpGainTime`      TIMESTAMP           NOT NULL DEFAULT '2015-01-01 05:00:00' COMMENT '最后获取经验时间',
    MODIFY COLUMN `partySearch`          TINYINT(1)          NOT NULL DEFAULT '1' COMMENT '是否开启组队搜索（0=关闭 1=开启）',
    MODIFY COLUMN `jailexpire`           bigint(20)          NOT NULL DEFAULT '0' COMMENT '监狱到期时间戳';

-- ==========================================
-- 11. characterexplogs - 角色经验日志表
-- ==========================================
ALTER TABLE `characterexplogs`
    COMMENT '角色经验获取日志表',
    MODIFY COLUMN `id`             bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `world_exp_rate` int NULL DEFAULT NULL COMMENT '大区倍率',
    MODIFY COLUMN `exp_coupon`     int NULL DEFAULT NULL COMMENT '双倍卡倍率',
    MODIFY COLUMN `gained_exp`     bigint NULL DEFAULT NULL COMMENT '获取经验值',
    MODIFY COLUMN `current_exp`    bigint NULL DEFAULT NULL COMMENT '当前总经验值',
    MODIFY COLUMN `exp_gain_time`  timestamp NULL DEFAULT NULL COMMENT '经验获取时间',
    MODIFY COLUMN `charid`         int NULL DEFAULT NULL COMMENT '角色ID（关联characters.id）';

-- ==========================================
-- 12. cooldowns - 技能冷却时间表
-- ==========================================
ALTER TABLE `cooldowns`
    COMMENT '技能冷却时间表',
    MODIFY COLUMN `id`        INT(11)             NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `charid`    INT(11)             NOT NULL COMMENT '角色ID（关联characters.id）',
    MODIFY COLUMN `SkillID`   INT(11)             NOT NULL COMMENT '技能ID',
    MODIFY COLUMN `length`    BIGINT(20) UNSIGNED NOT NULL COMMENT '冷却时长（毫秒）',
    MODIFY COLUMN `StartTime` BIGINT(20) UNSIGNED NOT NULL COMMENT '冷却开始时间戳';

-- ==========================================
-- 13. drop_data - 怪物掉落数据表
-- ==========================================
ALTER TABLE `drop_data`
    COMMENT '怪物掉落数据表',
    MODIFY COLUMN `id`               BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `dropperid`        INT(11)    NOT NULL COMMENT '掉落者ID（怪物ID或任务ID）',
    MODIFY COLUMN `itemid`           INT(11)    NOT NULL DEFAULT '0' COMMENT '掉落物品ID',
    MODIFY COLUMN `minimum_quantity` INT(11)    NOT NULL DEFAULT '1' COMMENT '最小掉落数量',
    MODIFY COLUMN `maximum_quantity` INT(11)    NOT NULL DEFAULT '1' COMMENT '最大掉落数量',
    MODIFY COLUMN `questid`          INT(11)    NOT NULL DEFAULT '0' COMMENT '关联任务ID（0=非任务掉落）',
    MODIFY COLUMN `chance`           INT(11)    NOT NULL DEFAULT '0' COMMENT '掉落概率';

-- ==========================================
-- 14. drop_data_global - 全局掉落数据表
-- ==========================================
ALTER TABLE `drop_data_global`
    COMMENT '全局掉落数据表（跨地图通用掉落）',
    MODIFY COLUMN `id`               BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `continent`        TINYINT(1) NOT NULL DEFAULT '-1' COMMENT '大陆类型（-1=全局）',
    MODIFY COLUMN `itemid`           INT(11)    NOT NULL DEFAULT '0' COMMENT '掉落物品ID',
    MODIFY COLUMN `minimum_quantity` INT(11)    NOT NULL DEFAULT '1' COMMENT '最小掉落数量',
    MODIFY COLUMN `maximum_quantity` INT(11)    NOT NULL DEFAULT '1' COMMENT '最大掉落数量',
    MODIFY COLUMN `questid`          INT(11)    NOT NULL DEFAULT '0' COMMENT '关联任务ID（0=非任务掉落）',
    MODIFY COLUMN `chance`           INT(11)    NOT NULL DEFAULT '0' COMMENT '掉落概率',
    MODIFY COLUMN `comments`         VARCHAR(45) DEFAULT NULL COMMENT '备注';

-- ==========================================
-- 15. dueyitems - 快递物品表
-- ==========================================
ALTER TABLE `dueyitems`
    COMMENT '快递物品表',
    MODIFY COLUMN `id`              INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `PackageId`       INT(10) UNSIGNED NOT NULL DEFAULT '0' COMMENT '包裹ID（关联dueypackages.PackageId）',
    MODIFY COLUMN `inventoryitemid` INT(10) UNSIGNED NOT NULL DEFAULT '0' COMMENT '物品栏物品ID（关联inventoryitems.inventoryitemid）';

-- ==========================================
-- 16. dueypackages - 快递包裹表
-- ==========================================
ALTER TABLE `dueypackages`
    COMMENT '快递包裹表',
    MODIFY COLUMN `PackageId`   INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '包裹ID',
    MODIFY COLUMN `ReceiverId`  INT(10) UNSIGNED NOT NULL COMMENT '接收者角色ID',
    MODIFY COLUMN `SenderName`  VARCHAR(13) NOT NULL COMMENT '发送者角色名',
    MODIFY COLUMN `Mesos`       INT(10) UNSIGNED DEFAULT '0' COMMENT '附带金币',
    MODIFY COLUMN `TIMESTAMP`   TIMESTAMP NOT NULL DEFAULT '2015-01-01 05:00:00' COMMENT '发送时间',
    MODIFY COLUMN `Message`     VARCHAR(200) NULL COMMENT '留言内容',
    MODIFY COLUMN `Checked`     TINYINT(1) UNSIGNED DEFAULT '1' COMMENT '是否已查看（0=已查看 1=未查看）',
    MODIFY COLUMN `Type`        TINYINT(1) UNSIGNED DEFAULT '0' COMMENT '包裹类型（0=普通 1=点券）';

-- ==========================================
-- 17. eventstats - 活动统计表
-- ==========================================
ALTER TABLE `eventstats`
    COMMENT '活动统计表',
    MODIFY COLUMN `characterid` INT(11) UNSIGNED NOT NULL COMMENT '角色ID（关联characters.id）',
    MODIFY COLUMN `name`        VARCHAR(11) NOT NULL DEFAULT '0' COMMENT '活动名称',
    MODIFY COLUMN `info`        INT(11) NOT NULL COMMENT '活动统计值';

-- ==========================================
-- 18. famelog - 人气记录表
-- ==========================================
ALTER TABLE `famelog`
    COMMENT '人气记录表',
    MODIFY COLUMN `famelogid`     INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `characterid`   INT(11) NOT NULL DEFAULT '0' COMMENT '操作者角色ID',
    MODIFY COLUMN `characterid_to` INT(11) NOT NULL DEFAULT '0' COMMENT '目标角色ID',
    MODIFY COLUMN `when`          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间';

-- ==========================================
-- 19. family_character - 学院角色表
-- ==========================================
ALTER TABLE `family_character`
    COMMENT '学院角色表',
    MODIFY COLUMN `cid`             INT(11) NOT NULL COMMENT '角色ID（关联characters.id）',
    MODIFY COLUMN `familyid`        INT(11) NOT NULL COMMENT '学院ID',
    MODIFY COLUMN `seniorid`        INT(11) NOT NULL COMMENT '上级角色ID（0=创始人）',
    MODIFY COLUMN `reputation`      INT(11) NOT NULL DEFAULT '0' COMMENT '当前声望值',
    MODIFY COLUMN `todaysrep`       INT(11) NOT NULL DEFAULT '0' COMMENT '今日获得声望',
    MODIFY COLUMN `totalreputation` INT(11) NOT NULL DEFAULT '0' COMMENT '累计声望值',
    MODIFY COLUMN `reptosenior`     INT(11) NOT NULL DEFAULT '0' COMMENT '贡献给上级的声望',
    MODIFY COLUMN `precepts`        VARCHAR(200) DEFAULT NULL COMMENT '学院训言',
    MODIFY COLUMN `lastresettime`   BIGINT(20) NOT NULL DEFAULT '0' COMMENT '最后重置时间';

-- ==========================================
-- 20. family_entitlement - 学院特权表
-- ==========================================
ALTER TABLE `family_entitlement`
    COMMENT '学院特权表',
    MODIFY COLUMN `id`            INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `charid`        INT(11) NOT NULL COMMENT '角色ID（关联characters.id）',
    MODIFY COLUMN `entitlementid` INT(11) NOT NULL COMMENT '特权ID',
    MODIFY COLUMN `TIMESTAMP`     BIGINT(20) NOT NULL DEFAULT '0' COMMENT '获取时间戳';

-- ==========================================
-- 21. fredstorage - 弗雷德仓库表
-- ==========================================
ALTER TABLE `fredstorage`
    COMMENT '弗雷德仓库表（弗雷德活动存储）',
    MODIFY COLUMN `id`        INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `cid`       INT(10) UNSIGNED NOT NULL COMMENT '角色ID（关联characters.id）',
    MODIFY COLUMN `daynotes`  INT(4) UNSIGNED NOT NULL COMMENT '当日笔记数量',
    MODIFY COLUMN `TIMESTAMP` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间';

-- ==========================================
-- 22. gachapon - 转蛋机配置表（补表级注释）
-- ==========================================
ALTER TABLE `gachapon`
    COMMENT '转蛋机配置表';

-- ==========================================
-- 23. gachapon_reward - 转蛋机奖励表（补表级注释）
-- ==========================================
ALTER TABLE `gachapon_reward`
    COMMENT '转蛋机奖励配置表';

-- ==========================================
-- 24. gachapon_reward_pool - 转蛋机奖池表（补表级注释）
-- ==========================================
ALTER TABLE `gachapon_reward_pool`
    COMMENT '转蛋机奖池配置表';

-- ==========================================
-- 25. gifts - 礼物表
-- ==========================================
ALTER TABLE `gifts`
    COMMENT '礼物表',
    MODIFY COLUMN `id`      INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `to`      INT(11) NOT NULL COMMENT '接收者角色ID',
    MODIFY COLUMN `from`    VARCHAR(13) NOT NULL COMMENT '发送者角色名',
    MODIFY COLUMN `message` TINYTEXT NOT NULL COMMENT '礼物寄语',
    MODIFY COLUMN `sn`      INT(10) UNSIGNED NOT NULL COMMENT '商城物品SN',
    MODIFY COLUMN `ringid`  INT(10) NOT NULL COMMENT '戒指ID（关联rings.id，-1=非戒指）';

-- ==========================================
-- 26. guilds - 公会表
-- ==========================================
ALTER TABLE `guilds`
    COMMENT '公会表',
    MODIFY COLUMN `guildid`     INT(10) UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '公会ID',
    MODIFY COLUMN `leader`      INT(10) UNSIGNED     NOT NULL DEFAULT '0' COMMENT '会长角色ID',
    MODIFY COLUMN `GP`          INT(10) UNSIGNED     NOT NULL DEFAULT '0' COMMENT '公会积分',
    MODIFY COLUMN `logo`        INT(10) UNSIGNED     DEFAULT NULL COMMENT '公会Logo图案ID',
    MODIFY COLUMN `logoColor`   SMALLINT(5) UNSIGNED NOT NULL DEFAULT '0' COMMENT 'Logo图案颜色',
    MODIFY COLUMN `name`        VARCHAR(45)          NOT NULL COMMENT '公会名称',
    MODIFY COLUMN `rank1title`  VARCHAR(45)          NOT NULL DEFAULT 'Master' COMMENT '1级称号（会长）',
    MODIFY COLUMN `rank2title`  VARCHAR(45)          NOT NULL DEFAULT 'Jr. Master' COMMENT '2级称号（副会长）',
    MODIFY COLUMN `rank3title`  VARCHAR(45)          NOT NULL DEFAULT 'Member' COMMENT '3级称号（成员）',
    MODIFY COLUMN `rank4title`  VARCHAR(45)          NOT NULL DEFAULT 'Member' COMMENT '4级称号（成员）',
    MODIFY COLUMN `rank5title`  VARCHAR(45)          NOT NULL DEFAULT 'Member' COMMENT '5级称号（成员）',
    MODIFY COLUMN `capacity`    INT(10) UNSIGNED     NOT NULL DEFAULT '10' COMMENT '最大容纳人数',
    MODIFY COLUMN `logoBG`      INT(10) UNSIGNED     DEFAULT NULL COMMENT 'Logo背景图案ID',
    MODIFY COLUMN `logoBGColor` SMALLINT(5) UNSIGNED NOT NULL DEFAULT '0' COMMENT 'Logo背景颜色',
    MODIFY COLUMN `notice`      VARCHAR(101)         DEFAULT NULL COMMENT '公会公告',
    MODIFY COLUMN `signature`   INT(11)              NOT NULL DEFAULT '0' COMMENT '公会签名',
    MODIFY COLUMN `allianceId`  INT(11) UNSIGNED     NOT NULL DEFAULT '0' COMMENT '所属联盟ID（关联alliance.id，0=无联盟）';

-- ==========================================
-- 27. hp_mp_alert - HP/MP警戒表
-- ==========================================
ALTER TABLE `hp_mp_alert`
    COMMENT 'HP/MP警戒配置表（宠物自动吃药阈值）',
    MODIFY COLUMN `id`   INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `c_id` INT(11) UNSIGNED NOT NULL COMMENT '角色ID（关联characters.id）',
    MODIFY COLUMN `hp`   TINYINT UNSIGNED NOT NULL DEFAULT 10 COMMENT 'HP警戒线百分比',
    MODIFY COLUMN `mp`   TINYINT UNSIGNED NOT NULL DEFAULT 10 COMMENT 'MP警戒线百分比';

-- ==========================================
-- 28. hwidaccounts - 硬件绑定账号表
-- ==========================================
ALTER TABLE `hwidaccounts`
    COMMENT '硬件绑定账号表',
    MODIFY COLUMN `accountid` INT(11) NOT NULL DEFAULT '0' COMMENT '账号ID（关联accounts.id）',
    MODIFY COLUMN `hwid`      VARCHAR(40) NOT NULL DEFAULT '' COMMENT '硬件ID',
    MODIFY COLUMN `relevance` TINYINT(2) NOT NULL DEFAULT '0' COMMENT '关联度',
    MODIFY COLUMN `expiresat` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '过期时间';

-- ==========================================
-- 29. hwidbans - 硬件封禁表
-- ==========================================
ALTER TABLE `hwidbans`
    COMMENT '硬件封禁表',
    MODIFY COLUMN `hwidbanid` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `hwid`      VARCHAR(30) NOT NULL COMMENT '被封禁的硬件ID';

-- ==========================================
-- 30. inventoryequipment - 装备物品表
-- ==========================================
ALTER TABLE `inventoryequipment`
    COMMENT '装备物品表（存储装备的详细属性）',
    MODIFY COLUMN `inventoryequipmentid` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '装备物品ID',
    MODIFY COLUMN `inventoryitemid`      INT(10) UNSIGNED NOT NULL DEFAULT '0' COMMENT '物品ID（关联inventoryitems.inventoryitemid）',
    MODIFY COLUMN `upgradeslots`         INT(11)          NOT NULL DEFAULT '0' COMMENT '剩余可砸卷次数',
    MODIFY COLUMN `level`                INT(11)          NOT NULL DEFAULT '0' COMMENT '装备等级',
    MODIFY COLUMN `str`                  INT(11)          NOT NULL DEFAULT '0' COMMENT '力量加成',
    MODIFY COLUMN `dex`                  INT(11)          NOT NULL DEFAULT '0' COMMENT '敏捷加成',
    MODIFY COLUMN `int`                  INT(11)          NOT NULL DEFAULT '0' COMMENT '智力加成',
    MODIFY COLUMN `luk`                  INT(11)          NOT NULL DEFAULT '0' COMMENT '运气加成',
    MODIFY COLUMN `hp`                   INT(11)          NOT NULL DEFAULT '0' COMMENT 'HP加成',
    MODIFY COLUMN `mp`                   INT(11)          NOT NULL DEFAULT '0' COMMENT 'MP加成',
    MODIFY COLUMN `watk`                 INT(11)          NOT NULL DEFAULT '0' COMMENT '物理攻击加成',
    MODIFY COLUMN `matk`                 INT(11)          NOT NULL DEFAULT '0' COMMENT '魔法攻击加成',
    MODIFY COLUMN `wdef`                 INT(11)          NOT NULL DEFAULT '0' COMMENT '物理防御加成',
    MODIFY COLUMN `mdef`                 INT(11)          NOT NULL DEFAULT '0' COMMENT '魔法防御加成',
    MODIFY COLUMN `acc`                  INT(11)          NOT NULL DEFAULT '0' COMMENT '命中加成',
    MODIFY COLUMN `avoid`                INT(11)          NOT NULL DEFAULT '0' COMMENT '回避加成',
    MODIFY COLUMN `hands`                INT(11)          NOT NULL DEFAULT '0' COMMENT '手技加成',
    MODIFY COLUMN `speed`                INT(11)          NOT NULL DEFAULT '0' COMMENT '速度加成',
    MODIFY COLUMN `jump`                 INT(11)          NOT NULL DEFAULT '0' COMMENT '跳跃加成',
    MODIFY COLUMN `locked`               INT(11)          NOT NULL DEFAULT '0' COMMENT '是否锁定（0=未锁定 1=已锁定）',
    MODIFY COLUMN `vicious`              INT(11) UNSIGNED NOT NULL DEFAULT '0' COMMENT '砸卷失败计数',
    MODIFY COLUMN `itemlevel`            INT(11)          NOT NULL DEFAULT '1' COMMENT '物品等级',
    MODIFY COLUMN `itemexp`              INT(11) UNSIGNED NOT NULL DEFAULT '0' COMMENT '物品经验',
    MODIFY COLUMN `ringid`               INT(11)          NOT NULL DEFAULT '-1' COMMENT '戒指ID（关联rings.id，-1=非戒指）',
    MODIFY COLUMN `enhance_level`        INT              NOT NULL DEFAULT 0 COMMENT '强化等级（0=未强化）';

-- ==========================================
-- 31. inventoryitems - 物品栏表
-- ==========================================
ALTER TABLE `inventoryitems`
    COMMENT '物品栏表（玩家背包中所有物品）',
    MODIFY COLUMN `inventoryitemid` INT(10) UNSIGNED    NOT NULL AUTO_INCREMENT COMMENT '物品唯一ID',
    MODIFY COLUMN `type`            TINYINT(3) UNSIGNED NOT NULL COMMENT '存储类别（ItemFactory枚举值：1=普通背包 2=仓库 3=冒险家现金 4=骑士团现金 5=战神现金 6=雇佣商店 7=通用现金 8=婚礼礼物 9=快递）',
    MODIFY COLUMN `characterid`     INT(11)             DEFAULT NULL COMMENT '所属角色ID',
    MODIFY COLUMN `accountid`       INT(11)             DEFAULT NULL COMMENT '所属账号ID（现金物品）',
    MODIFY COLUMN `itemid`          INT(11)             NOT NULL DEFAULT '0' COMMENT '物品ID（对应WZ数据）',
    MODIFY COLUMN `inventorytype`   INT(11)             NOT NULL DEFAULT '0' COMMENT '背包类型（-1=已装备 0=未定义 1=装备 2=消耗 3=装饰 4=其他 5=现金 6=容器，参考InventoryType枚举）',
    MODIFY COLUMN `position`        INT(11)             NOT NULL DEFAULT '0' COMMENT '背包位置（负数=装备栏位）',
    MODIFY COLUMN `quantity`        INT(11)             NOT NULL DEFAULT '0' COMMENT '堆叠数量',
    MODIFY COLUMN `owner`           TINYTEXT            NOT NULL COMMENT '所有者名称（用于交易限制）',
    MODIFY COLUMN `petid`           INT(11)             NOT NULL DEFAULT '-1' COMMENT '宠物ID（关联pets.petid，-1=非宠物）',
    MODIFY COLUMN `flag`            INT(11)             NOT NULL COMMENT '物品标记',
    MODIFY COLUMN `expiration`      BIGINT(20)          NOT NULL DEFAULT '-1' COMMENT '过期时间戳（-1=永不过期）',
    MODIFY COLUMN `giftFrom`        VARCHAR(26)         NOT NULL COMMENT '赠送者角色名';

-- ==========================================
-- 32. inventorymerchant - 雇佣商店物品表
-- ==========================================
ALTER TABLE `inventorymerchant`
    COMMENT '雇佣商店物品表',
    MODIFY COLUMN `inventorymerchantid` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `inventoryitemid`     INT(10) UNSIGNED NOT NULL DEFAULT '0' COMMENT '物品ID（关联inventoryitems.inventoryitemid）',
    MODIFY COLUMN `characterid`         INT(11)          DEFAULT NULL COMMENT '所有者角色ID',
    MODIFY COLUMN `bundles`             INT(10)          NOT NULL DEFAULT '0' COMMENT '物品捆绑数量';

-- ==========================================
-- 33. ipbans - IP封禁表
-- ==========================================
ALTER TABLE `ipbans`
    COMMENT 'IP封禁表',
    MODIFY COLUMN `ipbanid` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `ip`      VARCHAR(40) NOT NULL DEFAULT '' COMMENT '被封禁的IP地址',
    MODIFY COLUMN `aid`     VARCHAR(40) DEFAULT NULL COMMENT '关联账号';

-- ==========================================
-- 34. keymap - 按键映射表
-- ==========================================
ALTER TABLE `keymap`
    COMMENT '按键映射表（玩家自定义快捷键）',
    MODIFY COLUMN `id`          INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `characterid` INT(11) NOT NULL DEFAULT '0' COMMENT '角色ID（关联characters.id）',
    MODIFY COLUMN `key`         INT(11) NOT NULL DEFAULT '0' COMMENT '按键码',
    MODIFY COLUMN `type`        INT(11) NOT NULL DEFAULT '0' COMMENT '映射类型（1=技能 2=物品 3=表情 4=宏）',
    MODIFY COLUMN `action`      INT(11) NOT NULL DEFAULT '0' COMMENT '映射动作ID';

-- ==========================================
-- 35. macbans - MAC封禁表
-- ==========================================
ALTER TABLE `macbans`
    COMMENT 'MAC封禁表',
    MODIFY COLUMN `macbanid` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `mac`      VARCHAR(30) NOT NULL COMMENT '被封禁的MAC地址',
    MODIFY COLUMN `aid`      VARCHAR(40) DEFAULT NULL COMMENT '关联账号';

-- ==========================================
-- 36. macfilters - MAC过滤表
-- ==========================================
ALTER TABLE `macfilters`
    COMMENT 'MAC过滤表（MAC允许列表）',
    MODIFY COLUMN `macfilterid` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `filter`      VARCHAR(30) NOT NULL COMMENT '过滤的MAC地址';

-- ==========================================
-- 37. makercreatedata - 制造创建数据表
-- ==========================================
ALTER TABLE `makercreatedata`
    COMMENT '制造创建数据表（制作装备基础配置）',
    MODIFY COLUMN `id`               TINYINT(3) UNSIGNED NOT NULL COMMENT '制作ID',
    MODIFY COLUMN `itemid`           INT(11) NOT NULL COMMENT '产出的装备ID',
    MODIFY COLUMN `req_level`        TINYINT(3) UNSIGNED NOT NULL COMMENT '要求角色等级',
    MODIFY COLUMN `req_maker_level`  TINYINT(3) UNSIGNED NOT NULL COMMENT '要求制作等级',
    MODIFY COLUMN `req_meso`         INT(11) NOT NULL COMMENT '金币消耗',
    MODIFY COLUMN `req_item`         INT(11) NOT NULL COMMENT '所需材料物品ID',
    MODIFY COLUMN `req_equip`        INT(11) NOT NULL COMMENT '所需基础装备ID',
    MODIFY COLUMN `catalyst`         INT(11) NOT NULL COMMENT '催化剂物品ID',
    MODIFY COLUMN `quantity`         SMALLINT(6) NOT NULL COMMENT '产出数量',
    MODIFY COLUMN `tuc`              TINYINT(3) NOT NULL COMMENT '装备可砸卷次数';

-- ==========================================
-- 38. makerrecipedata - 制造配方数据表
-- ==========================================
ALTER TABLE `makerrecipedata`
    COMMENT '制造配方数据表（制作所需材料）',
    MODIFY COLUMN `itemid`   INT(11) NOT NULL COMMENT '产出的装备ID',
    MODIFY COLUMN `req_item` INT(11) NOT NULL COMMENT '所需材料物品ID',
    MODIFY COLUMN `count`    SMALLINT(6) NOT NULL COMMENT '所需材料数量';

-- ==========================================
-- 39. makerreagentdata - 制造试剂数据表
-- ==========================================
ALTER TABLE `makerreagentdata`
    COMMENT '制造试剂数据表（制作属性加成）',
    MODIFY COLUMN `itemid` INT(11) NOT NULL COMMENT '装备ID',
    MODIFY COLUMN `stat`   VARCHAR(20) NOT NULL COMMENT '加成的属性名（如str/dex/int/luk）',
    MODIFY COLUMN `value`  SMALLINT(6) NOT NULL COMMENT '加成值';

-- ==========================================
-- 40. makerrewarddata - 制造奖励数据表
-- ==========================================
ALTER TABLE `makerrewarddata`
    COMMENT '制造奖励数据表（制作随机产出）',
    MODIFY COLUMN `itemid`   INT(11) NOT NULL COMMENT '制作物品ID',
    MODIFY COLUMN `rewardid` INT(11) NOT NULL COMMENT '奖励物品ID',
    MODIFY COLUMN `quantity` SMALLINT(6) NOT NULL COMMENT '奖励数量',
    MODIFY COLUMN `prob`     TINYINT(3) UNSIGNED NOT NULL DEFAULT '100' COMMENT '产出概率';

-- ==========================================
-- 41. marriages - 婚姻表
-- ==========================================
ALTER TABLE `marriages`
    COMMENT '婚姻表（结婚记录）',
    MODIFY COLUMN `marriageid` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '婚姻ID',
    MODIFY COLUMN `husbandid`  INT(10) UNSIGNED NOT NULL DEFAULT '0' COMMENT '丈夫角色ID',
    MODIFY COLUMN `wifeid`     INT(10) UNSIGNED NOT NULL DEFAULT '0' COMMENT '妻子角色ID';

-- ==========================================
-- 42. medalmaps - 勋章地图表
-- ==========================================
ALTER TABLE `medalmaps`
    COMMENT '勋章地图表（角色勋章获取地图记录）',
    MODIFY COLUMN `id`            INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `characterid`   INT(11) NOT NULL COMMENT '角色ID（关联characters.id）',
    MODIFY COLUMN `queststatusid` INT(11) UNSIGNED NOT NULL COMMENT '任务状态ID（关联queststatus.queststatusid）',
    MODIFY COLUMN `mapid`         INT(11) NOT NULL COMMENT '地图ID';

-- ==========================================
-- 43. modified_cash_item - 商城物品修改表（补完整字段注释）
-- ==========================================
ALTER TABLE `modified_cash_item`
    MODIFY COLUMN `class`    int(11) COMMENT '职业限制',
    MODIFY COLUMN `limit`    int(11) COMMENT '限购数量',
    MODIFY COLUMN `pb_cash`  int(11) COMMENT 'PB点卷',
    MODIFY COLUMN `pb_point` int(11) COMMENT 'PB积分',
    MODIFY COLUMN `pb_gift`  int(11) COMMENT 'PB礼物';

-- ==========================================
-- 44. monsterbook - 怪物图鉴表
-- ==========================================
ALTER TABLE `monsterbook`
    COMMENT '怪物图鉴表（角色已收集的怪物卡片）',
    MODIFY COLUMN `charid` INT(11) NOT NULL COMMENT '角色ID（关联characters.id）',
    MODIFY COLUMN `cardid` INT(11) NOT NULL COMMENT '卡片ID',
    MODIFY COLUMN `level`  INT(1)  NOT NULL DEFAULT '1' COMMENT '卡片等级';

-- ==========================================
-- 45. monstercarddata - 怪物卡片数据表
-- ==========================================
ALTER TABLE `monstercarddata`
    COMMENT '怪物卡片数据表（卡片与怪物对应关系）',
    MODIFY COLUMN `id`     INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `cardid` INT(11) NOT NULL DEFAULT '0' COMMENT '卡片ID',
    MODIFY COLUMN `mobid`  INT(11) NOT NULL DEFAULT '0' COMMENT '怪物ID';

-- ==========================================
-- 46. mts_cart - 拍卖购物车表
-- ==========================================
ALTER TABLE `mts_cart`
    COMMENT '拍卖行购物车表',
    MODIFY COLUMN `id`     INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `cid`    INT(11) NOT NULL COMMENT '角色ID（关联characters.id）',
    MODIFY COLUMN `itemid` INT(11) NOT NULL COMMENT '拍卖物品ID（关联mts_items.id）';

-- ==========================================
-- 47. mts_items - 拍卖物品表
-- ==========================================
ALTER TABLE `mts_items`
    COMMENT '拍卖行物品表',
    MODIFY COLUMN `id`          INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '物品ID',
    MODIFY COLUMN `tab`         INT(11) NOT NULL DEFAULT '0' COMMENT '标签页（0=装备 1=消耗 2=设置 3=其他 4=现金）',
    MODIFY COLUMN `type`        INT(11) NOT NULL DEFAULT '0' COMMENT '物品类型',
    MODIFY COLUMN `itemid`      INT(10) UNSIGNED NOT NULL DEFAULT '0' COMMENT '物品ID（对应WZ数据）',
    MODIFY COLUMN `quantity`    INT(11) NOT NULL DEFAULT '1' COMMENT '数量',
    MODIFY COLUMN `seller`      INT(11) NOT NULL DEFAULT '0' COMMENT '卖家角色ID',
    MODIFY COLUMN `price`       INT(11) NOT NULL DEFAULT '0' COMMENT '起拍价格',
    MODIFY COLUMN `bid_incre`   INT(11) DEFAULT '0' COMMENT '最低加价',
    MODIFY COLUMN `buy_now`     INT(11) DEFAULT '0' COMMENT '一口价',
    MODIFY COLUMN `position`    INT(11) DEFAULT '0' COMMENT '显示排序',
    MODIFY COLUMN `upgradeslots` INT(11) DEFAULT '0' COMMENT '剩余可砸卷次数',
    MODIFY COLUMN `level`       INT(11) DEFAULT '0' COMMENT '装备等级',
    MODIFY COLUMN `itemlevel`   INT(11) NOT NULL DEFAULT '1' COMMENT '物品等级',
    MODIFY COLUMN `itemexp`     INT(11) UNSIGNED NOT NULL DEFAULT '0' COMMENT '物品经验',
    MODIFY COLUMN `ringid`      INT(11) NOT NULL DEFAULT '-1' COMMENT '戒指ID（关联rings.id，-1=非戒指）',
    MODIFY COLUMN `str`         INT(11) DEFAULT '0' COMMENT '力量加成',
    MODIFY COLUMN `dex`         INT(11) DEFAULT '0' COMMENT '敏捷加成',
    MODIFY COLUMN `int`         INT(11) DEFAULT '0' COMMENT '智力加成',
    MODIFY COLUMN `luk`         INT(11) DEFAULT '0' COMMENT '运气加成',
    MODIFY COLUMN `hp`          INT(11) DEFAULT '0' COMMENT 'HP加成',
    MODIFY COLUMN `mp`          INT(11) DEFAULT '0' COMMENT 'MP加成',
    MODIFY COLUMN `watk`        INT(11) DEFAULT '0' COMMENT '物理攻击加成',
    MODIFY COLUMN `matk`        INT(11) DEFAULT '0' COMMENT '魔法攻击加成',
    MODIFY COLUMN `wdef`        INT(11) DEFAULT '0' COMMENT '物理防御加成',
    MODIFY COLUMN `mdef`        INT(11) DEFAULT '0' COMMENT '魔法防御加成',
    MODIFY COLUMN `acc`         INT(11) DEFAULT '0' COMMENT '命中加成',
    MODIFY COLUMN `avoid`       INT(11) DEFAULT '0' COMMENT '回避加成',
    MODIFY COLUMN `hands`       INT(11) DEFAULT '0' COMMENT '手技加成',
    MODIFY COLUMN `speed`       INT(11) DEFAULT '0' COMMENT '速度加成',
    MODIFY COLUMN `jump`        INT(11) DEFAULT '0' COMMENT '跳跃加成',
    MODIFY COLUMN `locked`      INT(11) DEFAULT '0' COMMENT '是否锁定（0=未锁定 1=已锁定）',
    MODIFY COLUMN `isequip`     INT(1) DEFAULT '0' COMMENT '是否为装备（0=否 1=是）',
    MODIFY COLUMN `owner`       VARCHAR(16) DEFAULT '' COMMENT '所有者名称',
    MODIFY COLUMN `sellername`  VARCHAR(16) NOT NULL COMMENT '卖家角色名',
    MODIFY COLUMN `sell_ends`   VARCHAR(16) NOT NULL COMMENT '拍卖结束时间',
    MODIFY COLUMN `transfer`    INT(2) DEFAULT '0' COMMENT '是否允许转移（0=不允许 1=允许）',
    MODIFY COLUMN `vicious`     INT(2) UNSIGNED NOT NULL DEFAULT '0' COMMENT '砸卷失败计数',
    MODIFY COLUMN `flag`        INT(2) UNSIGNED NOT NULL DEFAULT '0' COMMENT '物品标记',
    MODIFY COLUMN `expiration`  BIGINT(20) NOT NULL DEFAULT '-1' COMMENT '过期时间戳（-1=永不过期）',
    MODIFY COLUMN `giftFrom`    VARCHAR(26) NOT NULL COMMENT '赠送者角色名';

-- ==========================================
-- 48. namechanges - 改名记录表
-- ==========================================
ALTER TABLE `namechanges`
    COMMENT '改名记录表',
    MODIFY COLUMN `id`             INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `characterid`    INT(11) NOT NULL COMMENT '角色ID（关联characters.id）',
    MODIFY COLUMN `old`            VARCHAR(13) NOT NULL COMMENT '旧角色名',
    MODIFY COLUMN `new`            VARCHAR(13) NOT NULL COMMENT '新角色名',
    MODIFY COLUMN `requestTime`    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    MODIFY COLUMN `completionTime` TIMESTAMP NULL COMMENT '改名完成时间';

-- ==========================================
-- 49. newyear - 新年贺卡表
-- ==========================================
ALTER TABLE `newyear`
    COMMENT '新年贺卡表',
    MODIFY COLUMN `id`              INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `senderid`        INT(10) NOT NULL DEFAULT '-1' COMMENT '发送者角色ID',
    MODIFY COLUMN `sendername`      VARCHAR(13) DEFAULT '' COMMENT '发送者角色名',
    MODIFY COLUMN `receiverid`      INT(10) NOT NULL DEFAULT '-1' COMMENT '接收者角色ID',
    MODIFY COLUMN `receivername`    VARCHAR(13) DEFAULT '' COMMENT '接收者角色名',
    MODIFY COLUMN `message`         VARCHAR(120) DEFAULT '' COMMENT '贺卡内容',
    MODIFY COLUMN `senderdiscard`   TINYINT(1) NOT NULL DEFAULT '0' COMMENT '发送者是否丢弃（0=否 1=是）',
    MODIFY COLUMN `receiverdiscard` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '接收者是否丢弃（0=否 1=是）',
    MODIFY COLUMN `received`        TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否已接收（0=否 1=是）',
    MODIFY COLUMN `timesent`        BIGINT(20) UNSIGNED NOT NULL COMMENT '发送时间戳',
    MODIFY COLUMN `timereceived`    BIGINT(20) UNSIGNED NOT NULL COMMENT '接收时间戳';

-- ==========================================
-- 50. notes - 留言板表（非公会BBS）
-- ==========================================
ALTER TABLE `notes`
    COMMENT '留言板表',
    MODIFY COLUMN `id`        INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `to`        VARCHAR(13) NOT NULL DEFAULT '' COMMENT '接收者角色名',
    MODIFY COLUMN `from`      VARCHAR(13) NOT NULL DEFAULT '' COMMENT '发送者角色名',
    MODIFY COLUMN `message`   TEXT NOT NULL COMMENT '留言内容',
    MODIFY COLUMN `TIMESTAMP` BIGINT(20) UNSIGNED NOT NULL COMMENT '发送时间戳',
    MODIFY COLUMN `fame`      INT(11) NOT NULL DEFAULT '0' COMMENT '附带人气值',
    MODIFY COLUMN `deleted`   INT(2) NOT NULL DEFAULT '0' COMMENT '是否已删除（0=否 1=是）';

-- ==========================================
-- 51. nxcode - 点券兑换码表
-- ==========================================
ALTER TABLE `nxcode`
    COMMENT '点券兑换码表',
    MODIFY COLUMN `id`         INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `code`       VARCHAR(17) NOT NULL UNIQUE COMMENT '兑换码',
    MODIFY COLUMN `retriever`  VARCHAR(13) DEFAULT NULL COMMENT '领取者角色名',
    MODIFY COLUMN `expiration` BIGINT(20) UNSIGNED NOT NULL DEFAULT '0' COMMENT '过期时间戳';

-- ==========================================
-- 52. nxcode_items - 兑换码物品表
-- ==========================================
ALTER TABLE `nxcode_items`
    COMMENT '兑换码物品表（每个兑换码包含的物品列表）',
    MODIFY COLUMN `id`       INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `codeid`   INT(11) NOT NULL COMMENT '兑换码ID（关联nxcode.id）',
    MODIFY COLUMN `type`     INT(11) NOT NULL DEFAULT '5' COMMENT '物品类型（5=点券类）',
    MODIFY COLUMN `item`     INT(11) NOT NULL DEFAULT '4000000' COMMENT '物品ID',
    MODIFY COLUMN `quantity` INT(11) NOT NULL DEFAULT '1' COMMENT '数量';

-- ==========================================
-- 53. nxcoupons - 点券商城优惠券表
-- ==========================================
ALTER TABLE `nxcoupons`
    COMMENT '点券优惠券表（商城双倍卡等）',
    MODIFY COLUMN `id`        INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `couponid`  INT(11) NOT NULL DEFAULT '0' COMMENT '优惠券物品ID',
    MODIFY COLUMN `rate`      INT(11) NOT NULL DEFAULT '0' COMMENT '倍率',
    MODIFY COLUMN `activeday` INT(11) NOT NULL DEFAULT '0' COMMENT '有效天数',
    MODIFY COLUMN `starthour` INT(11) NOT NULL DEFAULT '0' COMMENT '每日生效起始小时',
    MODIFY COLUMN `endhour`   INT(11) NOT NULL DEFAULT '0' COMMENT '每日生效结束小时';

-- ==========================================
-- 54. petignores - 宠物忽略物品表
-- ==========================================
ALTER TABLE `petignores`
    COMMENT '宠物忽略物品表（宠物不拾取的物品列表）',
    MODIFY COLUMN `id`     INT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `petid`  INT(11) UNSIGNED NOT NULL COMMENT '宠物ID（关联pets.petid）',
    MODIFY COLUMN `itemid` INT(10) UNSIGNED NOT NULL COMMENT '忽略的物品ID';

-- ==========================================
-- 55. pets - 宠物表
-- ==========================================
ALTER TABLE `pets`
    COMMENT '宠物表',
    MODIFY COLUMN `petid`     INT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '宠物ID',
    MODIFY COLUMN `name`      VARCHAR(13) DEFAULT NULL COMMENT '宠物名称',
    MODIFY COLUMN `level`     INT(10) UNSIGNED NOT NULL COMMENT '宠物等级',
    MODIFY COLUMN `closeness` INT(10) UNSIGNED NOT NULL COMMENT '亲密度',
    MODIFY COLUMN `fullness`  INT(10) UNSIGNED NOT NULL COMMENT '饱食度',
    MODIFY COLUMN `summoned`  TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否已召唤（0=否 1=是）',
    MODIFY COLUMN `flag`      INT(10) UNSIGNED NOT NULL DEFAULT '0' COMMENT '宠物标记';

-- ==========================================
-- 56. playerdiseases - 玩家异常状态表
-- ==========================================
ALTER TABLE `playerdiseases`
    COMMENT '玩家异常状态表（怪物技能debuff等）',
    MODIFY COLUMN `id`         INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `charid`     INT(11) NOT NULL COMMENT '角色ID（关联characters.id）',
    MODIFY COLUMN `disease`    INT(11) NOT NULL COMMENT '异常状态类型',
    MODIFY COLUMN `mobskillid` INT(11) NOT NULL COMMENT '来源怪物技能ID',
    MODIFY COLUMN `mobskilllv` INT(11) NOT NULL COMMENT '来源怪物技能等级',
    MODIFY COLUMN `length`     INT(11) NOT NULL DEFAULT '1' COMMENT '异常状态持续时间';

-- ==========================================
-- 57. playernpcs - 玩家NPC表
-- ==========================================
ALTER TABLE `playernpcs`
    COMMENT '玩家NPC表（名人堂）',
    MODIFY COLUMN `id`           INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `name`         VARCHAR(13) NOT NULL COMMENT '角色名',
    MODIFY COLUMN `hair`         INT(11) NOT NULL COMMENT '发型ID',
    MODIFY COLUMN `face`         INT(11) NOT NULL COMMENT '脸型ID',
    MODIFY COLUMN `skin`         INT(11) NOT NULL COMMENT '皮肤颜色',
    MODIFY COLUMN `gender`       INT(11) NOT NULL DEFAULT '0' COMMENT '性别（0=男 1=女）',
    MODIFY COLUMN `x`            INT(11) NOT NULL COMMENT 'X坐标',
    MODIFY COLUMN `cy`           INT(11) NOT NULL DEFAULT '0' COMMENT 'Y坐标',
    MODIFY COLUMN `world`        INT(11) NOT NULL DEFAULT '0' COMMENT '所在大区',
    MODIFY COLUMN `map`          INT(11) NOT NULL DEFAULT '0' COMMENT '所在地图ID',
    MODIFY COLUMN `dir`          INT(11) NOT NULL DEFAULT '0' COMMENT '朝向',
    MODIFY COLUMN `scriptid`     INT(10) UNSIGNED NOT NULL DEFAULT '0' COMMENT '脚本ID',
    MODIFY COLUMN `fh`           INT(11) NOT NULL DEFAULT '0' COMMENT '站立足点',
    MODIFY COLUMN `rx0`          INT(11) NOT NULL DEFAULT '0' COMMENT '边界框左',
    MODIFY COLUMN `rx1`          INT(11) NOT NULL DEFAULT '0' COMMENT '边界框右',
    MODIFY COLUMN `worldrank`    INT(11) NOT NULL DEFAULT '0' COMMENT '全区排名',
    MODIFY COLUMN `overallrank`  INT(11) NOT NULL DEFAULT '0' COMMENT '综合排名',
    MODIFY COLUMN `worldjobrank` INT(11) NOT NULL DEFAULT '0' COMMENT '全区职业排名',
    MODIFY COLUMN `job`          INT(11) NOT NULL DEFAULT '0' COMMENT '职业ID';

-- ==========================================
-- 58. playernpcs_equip - 玩家NPC装备表
-- ==========================================
ALTER TABLE `playernpcs_equip`
    COMMENT '玩家NPC装备表',
    MODIFY COLUMN `id`       INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `npcid`    INT(11) NOT NULL DEFAULT '0' COMMENT 'NPC ID（关联playernpcs.id）',
    MODIFY COLUMN `equipid`  INT(11) NOT NULL COMMENT '装备物品ID',
    MODIFY COLUMN `type`     INT(11) NOT NULL DEFAULT '0' COMMENT '装备类型',
    MODIFY COLUMN `equippos` INT(11) NOT NULL COMMENT '装备位置（负数=对应equip位置）';

-- ==========================================
-- 59. playernpcs_field - 玩家NPC布局表
-- ==========================================
ALTER TABLE `playernpcs_field`
    COMMENT '玩家NPC布局表（名人堂地图布局）',
    MODIFY COLUMN `id`     INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `world`  INT(11) NOT NULL COMMENT '大区ID',
    MODIFY COLUMN `map`    INT(11) NOT NULL COMMENT '地图ID',
    MODIFY COLUMN `step`   TINYINT(1) NOT NULL DEFAULT '0' COMMENT '布局步进',
    MODIFY COLUMN `podium` SMALLINT(8) NOT NULL DEFAULT '0' COMMENT '站台编号';

-- ==========================================
-- 60. plife - 地图生物表
-- ==========================================
ALTER TABLE `plife`
    COMMENT '地图生物表（自定义NPC/怪物布置）',
    MODIFY COLUMN `id`      INT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `world`   INT(11) NOT NULL DEFAULT '-1' COMMENT '大区ID',
    MODIFY COLUMN `map`     INT(11) NOT NULL DEFAULT '0' COMMENT '地图ID',
    MODIFY COLUMN `life`    INT(11) NOT NULL DEFAULT '0' COMMENT '生物ID（怪物/NPC ID）',
    MODIFY COLUMN `type`    VARCHAR(1) NOT NULL DEFAULT 'n' COMMENT '生物类型（n=NPC m=怪物）',
    MODIFY COLUMN `cy`      INT(11) NOT NULL DEFAULT '0' COMMENT 'Y坐标',
    MODIFY COLUMN `f`       INT(11) NOT NULL DEFAULT '0' COMMENT '是否翻转（0=正常 1=翻转）',
    MODIFY COLUMN `fh`      INT(11) NOT NULL DEFAULT '0' COMMENT '站立足点',
    MODIFY COLUMN `rx0`     INT(11) NOT NULL DEFAULT '0' COMMENT '移动范围左',
    MODIFY COLUMN `rx1`     INT(11) NOT NULL DEFAULT '0' COMMENT '移动范围右',
    MODIFY COLUMN `x`       INT(11) NOT NULL DEFAULT '0' COMMENT 'X坐标',
    MODIFY COLUMN `y`       INT(11) NOT NULL DEFAULT '0' COMMENT 'Y坐标（地图层级）',
    MODIFY COLUMN `hide`    INT(11) NOT NULL DEFAULT '0' COMMENT '是否隐藏（0=显示 1=隐藏）',
    MODIFY COLUMN `mobtime` INT(11) NOT NULL DEFAULT '0' COMMENT '怪物重生时间',
    MODIFY COLUMN `team`    INT(11) NOT NULL DEFAULT '0' COMMENT '所属队伍（-1=无）';

-- ==========================================
-- 61. questactions - 任务动作表
-- ==========================================
ALTER TABLE `questactions`
    COMMENT '任务动作表（任务完成时的奖励动作）',
    MODIFY COLUMN `questactionid` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `questid`       INT(11) NOT NULL DEFAULT '0' COMMENT '任务ID',
    MODIFY COLUMN `status`        INT(11) NOT NULL DEFAULT '0' COMMENT '任务状态（0=开始 1=完成）',
    MODIFY COLUMN `data`          BLOB NOT NULL COMMENT '动作数据（序列化的物品/经验等奖励）';

-- ==========================================
-- 62. questprogress - 任务进度表
-- ==========================================
ALTER TABLE `questprogress`
    COMMENT '任务进度表（角色各任务的详细进度）',
    MODIFY COLUMN `id`            INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `characterid`   INT(11) NOT NULL COMMENT '角色ID（关联characters.id）',
    MODIFY COLUMN `queststatusid` INT(10) UNSIGNED NOT NULL DEFAULT '0' COMMENT '任务状态ID（关联queststatus.queststatusid）',
    MODIFY COLUMN `progressid`    INT(11) NOT NULL DEFAULT '0' COMMENT '进度步骤ID',
    MODIFY COLUMN `progress`      VARCHAR(15) NOT NULL DEFAULT '' COMMENT '当前进度值';

-- ==========================================
-- 63. questrequirements - 任务需求表
-- ==========================================
ALTER TABLE `questrequirements`
    COMMENT '任务需求表（接任务的前置条件）',
    MODIFY COLUMN `questrequirementid` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `questid`            INT(11) NOT NULL DEFAULT '0' COMMENT '任务ID',
    MODIFY COLUMN `status`             INT(11) NOT NULL DEFAULT '0' COMMENT '任务状态（0=开始 1=完成）',
    MODIFY COLUMN `data`               BLOB NOT NULL COMMENT '需求数据（序列化信息）';

-- ==========================================
-- 64. queststatus - 任务状态表
-- ==========================================
ALTER TABLE `queststatus`
    COMMENT '任务状态表（角色各任务的当前状态）',
    MODIFY COLUMN `queststatusid` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `characterid`   INT(11) NOT NULL DEFAULT '0' COMMENT '角色ID（关联characters.id）',
    MODIFY COLUMN `quest`         INT(11) NOT NULL DEFAULT '0' COMMENT '任务ID',
    MODIFY COLUMN `status`        INT(11) NOT NULL DEFAULT '0' COMMENT '任务状态（0=未开始 1=进行中 2=已完成）',
    MODIFY COLUMN `time`          INT(11) NOT NULL DEFAULT '0' COMMENT '接受任务时间',
    MODIFY COLUMN `expires`       BIGINT(20) NOT NULL DEFAULT '0' COMMENT '任务过期时间戳',
    MODIFY COLUMN `forfeited`     INT(11) NOT NULL DEFAULT '0' COMMENT '放弃次数',
    MODIFY COLUMN `completed`     INT(11) NOT NULL DEFAULT '0' COMMENT '完成次数',
    MODIFY COLUMN `info`          TINYINT(3) NOT NULL DEFAULT '0' COMMENT '额外信息标记';

-- ==========================================
-- 65. quickslotkeymapped - 快捷栏映射表
-- ==========================================
ALTER TABLE `quickslotkeymapped`
    COMMENT '快捷栏映射表（按账号保存的快捷栏配置）',
    MODIFY COLUMN `accountid` INT NOT NULL COMMENT '账号ID（关联accounts.id）',
    MODIFY COLUMN `keymap`    BIGINT NOT NULL DEFAULT 0 COMMENT '快捷栏按键映射位图';

-- ==========================================
-- 66. reactordrops - 反应堆掉落表
-- ==========================================
ALTER TABLE `reactordrops`
    COMMENT '反应堆掉落表（打破地图反应堆的掉落物）',
    MODIFY COLUMN `reactordropid` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `reactorid`     INT(11) NOT NULL COMMENT '反应堆ID',
    MODIFY COLUMN `itemid`        INT(11) NOT NULL COMMENT '掉落物品ID',
    MODIFY COLUMN `chance`        INT(11) NOT NULL COMMENT '掉落概率',
    MODIFY COLUMN `questid`       INT(5) NOT NULL DEFAULT '-1' COMMENT '关联任务ID（-1=非任务掉落）';

-- ==========================================
-- 67. reports - 举报表
-- ==========================================
ALTER TABLE `reports`
    COMMENT '举报表',
    MODIFY COLUMN `id`          INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `reporttime`  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '举报时间',
    MODIFY COLUMN `reporterid`  INT(11) NOT NULL COMMENT '举报者角色ID',
    MODIFY COLUMN `victimid`    INT(11) NOT NULL COMMENT '被举报者角色ID',
    MODIFY COLUMN `reason`      TINYINT(4) NOT NULL COMMENT '举报原因码',
    MODIFY COLUMN `chatlog`     TEXT NOT NULL COMMENT '聊天记录',
    MODIFY COLUMN `description` TEXT NOT NULL COMMENT '举报描述';

-- ==========================================
-- 68. responses - 自动回复表
-- ==========================================
ALTER TABLE `responses`
    COMMENT '自动回复表（助手/帮助系统）',
    MODIFY COLUMN `id`       INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `chat`     TEXT COMMENT '触发消息',
    MODIFY COLUMN `response` TEXT COMMENT '自动回复内容';

-- ==========================================
-- 69. rings - 戒指表
-- ==========================================
ALTER TABLE `rings`
    COMMENT '戒指表（友情/爱情/结婚戒指等）',
    MODIFY COLUMN `id`            INT(11) NOT NULL AUTO_INCREMENT COMMENT '戒指ID',
    MODIFY COLUMN `partnerRingId` INT(11) NOT NULL DEFAULT '0' COMMENT '配对戒指ID',
    MODIFY COLUMN `partnerChrId`  INT(11) NOT NULL DEFAULT '0' COMMENT '伴侣角色ID',
    MODIFY COLUMN `itemid`        INT(11) NOT NULL DEFAULT '0' COMMENT '戒指物品ID',
    MODIFY COLUMN `partnername`   VARCHAR(255) NOT NULL COMMENT '伴侣角色名';

-- ==========================================
-- 70. savedlocations - 保存位置表
-- ==========================================
ALTER TABLE `savedlocations`
    COMMENT '保存位置表（角色各类型的保存位置）',
    MODIFY COLUMN `id`           INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `characterid`  INT(11) NOT NULL COMMENT '角色ID（关联characters.id）',
    MODIFY COLUMN `locationtype` ENUM('FREE_MARKET','WORLDTOUR','FLORINA','INTRO','SUNDAY_MARKET','MIRROR','EVENT','BOSSPQ','HAPPYVILLE','DEVELOPER','MONSTER_CARNIVAL','JAIL') NOT NULL COMMENT '位置类型',
    MODIFY COLUMN `map`          INT(11) NOT NULL COMMENT '保存地图ID',
    MODIFY COLUMN `portal`       INT(11) NOT NULL COMMENT '保存传送门ID';

-- ==========================================
-- 71. server_queue - 服务队列表
-- ==========================================
ALTER TABLE `server_queue`
    COMMENT '服务队列表（服务器重启/维护后的待处理队列）',
    MODIFY COLUMN `id`          INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `accountid`   INT(11) NOT NULL DEFAULT '0' COMMENT '账号ID（关联accounts.id）',
    MODIFY COLUMN `characterid` INT(11) NOT NULL DEFAULT '0' COMMENT '角色ID（关联characters.id）',
    MODIFY COLUMN `type`        TINYINT(2) NOT NULL DEFAULT '0' COMMENT '队列类型（0=改名 1=转区 2=世界转移）',
    MODIFY COLUMN `value`       INT(10) NOT NULL DEFAULT '0' COMMENT '队列参数',
    MODIFY COLUMN `message`     VARCHAR(128) NOT NULL COMMENT '队列消息',
    MODIFY COLUMN `createTime`  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';

-- ==========================================
-- 72. shopitems - 商店物品表
-- ==========================================
ALTER TABLE `shopitems`
    COMMENT '商店物品表',
    MODIFY COLUMN `shopitemid` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '商店物品ID',
    MODIFY COLUMN `shopid`     INT(10) UNSIGNED NOT NULL COMMENT '商店ID（关联shops.shopid）',
    MODIFY COLUMN `itemid`     INT(11) NOT NULL COMMENT '物品ID',
    MODIFY COLUMN `price`      INT(11) NOT NULL COMMENT '出售价格',
    MODIFY COLUMN `pitch`      INT(11) NOT NULL DEFAULT '0' COMMENT '物品间距',
    MODIFY COLUMN `position`   INT(11) NOT NULL COMMENT '显示排序（104起始，每次+4以便插入新物品）';

-- ==========================================
-- 73. shops - 商店表
-- ==========================================
ALTER TABLE `shops`
    COMMENT '商店表（NPC商店定义）',
    MODIFY COLUMN `shopid` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '商店ID',
    MODIFY COLUMN `npcid`  INT(11) NOT NULL DEFAULT '0' COMMENT '隶属于哪个NPC（0=不隶属于任何NPC）';

-- ==========================================
-- 74. skillmacros - 技能宏表
-- ==========================================
ALTER TABLE `skillmacros`
    COMMENT '技能宏表（玩家自定义技能宏）',
    MODIFY COLUMN `id`          INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `characterid` INT(11) NOT NULL DEFAULT '0' COMMENT '角色ID（关联characters.id）',
    MODIFY COLUMN `position`    TINYINT(1) NOT NULL DEFAULT '0' COMMENT '宏位置（0~4）',
    MODIFY COLUMN `skill1`      INT(11) NOT NULL DEFAULT '0' COMMENT '技能1',
    MODIFY COLUMN `skill2`      INT(11) NOT NULL DEFAULT '0' COMMENT '技能2',
    MODIFY COLUMN `skill3`      INT(11) NOT NULL DEFAULT '0' COMMENT '技能3',
    MODIFY COLUMN `name`        VARCHAR(13) DEFAULT NULL COMMENT '宏名称',
    MODIFY COLUMN `shout`       TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否包含喊话（0=否 1=是）';

-- ==========================================
-- 75. skills - 技能表
-- ==========================================
ALTER TABLE `skills`
    COMMENT '技能表（角色已学习的技能）',
    MODIFY COLUMN `id`          INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `skillid`     INT(11) NOT NULL DEFAULT '0' COMMENT '技能ID',
    MODIFY COLUMN `characterid` INT(11) NOT NULL DEFAULT '0' COMMENT '角色ID（关联characters.id）',
    MODIFY COLUMN `skilllevel`  INT(11) NOT NULL DEFAULT '0' COMMENT '当前技能等级',
    MODIFY COLUMN `masterlevel` INT(11) NOT NULL DEFAULT '0' COMMENT '最大掌握等级',
    MODIFY COLUMN `expiration`  BIGINT(20) NOT NULL DEFAULT '-1' COMMENT '过期时间戳（-1=永久技能）';

-- ==========================================
-- 76. specialcashitems - 特殊商城物品表
-- ==========================================
ALTER TABLE `specialcashitems`
    COMMENT '特殊商城物品表（限时/限定商城物品）',
    MODIFY COLUMN `id`       INT(11) NOT NULL COMMENT '特殊物品ID',
    MODIFY COLUMN `sn`       INT(11) NOT NULL COMMENT '商城物品SN（关联modified_cash_item.sn）',
    MODIFY COLUMN `modifier` INT(11) NOT NULL COMMENT '修改标记（1024=新增/移除）',
    MODIFY COLUMN `info`     INT(1) NOT NULL COMMENT '信息标记';

-- ==========================================
-- 77. storages - 仓库表
-- ==========================================
ALTER TABLE `storages`
    COMMENT '仓库表（玩家个人仓库）',
    MODIFY COLUMN `storageid` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '仓库ID',
    MODIFY COLUMN `accountid` INT(11) NOT NULL DEFAULT '0' COMMENT '账号ID（关联accounts.id）',
    MODIFY COLUMN `world`     INT(2) NOT NULL COMMENT '大区ID',
    MODIFY COLUMN `slots`     INT(11) NOT NULL DEFAULT '0' COMMENT '仓库容量',
    MODIFY COLUMN `meso`      INT(11) NOT NULL DEFAULT '0' COMMENT '仓库存储的金币';

-- ==========================================
-- 78. trocklocations - 岩石传送位置表
-- ==========================================
ALTER TABLE `trocklocations`
    COMMENT '岩石传送位置表（回归/传送石保存的位置）',
    MODIFY COLUMN `trockid`     INT(11) NOT NULL AUTO_INCREMENT COMMENT '传送位置ID',
    MODIFY COLUMN `characterid` INT(11) NOT NULL COMMENT '角色ID（关联characters.id）',
    MODIFY COLUMN `mapid`       INT(11) NOT NULL COMMENT '保存的地图ID',
    MODIFY COLUMN `vip`         INT(2) NOT NULL COMMENT '是否VIP（0=普通 1=VIP）';

-- ==========================================
-- 79. wishlists - 愿望清单表
-- ==========================================
ALTER TABLE `wishlists`
    COMMENT '愿望清单表（商城愿望单）',
    MODIFY COLUMN `id`     INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `charid` INT(11) NOT NULL COMMENT '角色ID（关联characters.id）',
    MODIFY COLUMN `sn`     INT(11) NOT NULL COMMENT '商城物品SN';

-- ==========================================
-- 80. worldtransfers - 世界转移表
-- ==========================================
ALTER TABLE `worldtransfers`
    COMMENT '世界转移表（角色跨大区转服申请）',
    MODIFY COLUMN `id`             INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    MODIFY COLUMN `characterid`    INT(11) NOT NULL COMMENT '角色ID（关联characters.id）',
    MODIFY COLUMN `from`           TINYINT(3) NOT NULL COMMENT '源大区ID',
    MODIFY COLUMN `to`             TINYINT(3) NOT NULL COMMENT '目标大区ID',
    MODIFY COLUMN `requestTime`    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    MODIFY COLUMN `completionTime` TIMESTAMP NULL COMMENT '转移完成时间';

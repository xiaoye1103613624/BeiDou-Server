-- ============================================================
-- 全库表/字段备注补全（单文件，覆盖 88 张表 / 675 列）
-- 备注按 gms-server 实际业务与读写位置填写；已有中文备注沿用或补全。
-- 本脚本只补备注、不改结构：每条 MODIFY COLUMN 均完整重复原列定义
--   （类型 / NOT NULL / DEFAULT / AUTO_INCREMENT / 字符集），不丢约束。
-- 范围：只覆盖数据库中已存在的列；095 潜能列（potential1..bonusPotential2）
--   由独立迁移 V1.11.7__add_inventoryequipment_potential.sql 创建并自带备注，
--   不在本脚本范围，避免对尚未建列的库报 Unknown column。
-- 风险：MODIFY COLUMN 可能触发 InnoDB 表重建；执行前请备份，
--   characters / inventoryitems / mts_items 等大表建议低峰期执行。
-- 结构依据：db/migration 全部建表与 ALTER 脚本（按 Flyway 版本序）。
-- ============================================================


-- ---- accounts：账号主表：登录凭据、封禁状态、点券/抵用券、角色槽与硬件指纹绑定 ----
ALTER TABLE `accounts` COMMENT = '账号主表：登录凭据、封禁状态、点券/抵用券、角色槽与硬件指纹绑定';
ALTER TABLE `accounts`
  MODIFY COLUMN `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键 ID',
  MODIFY COLUMN `name` VARCHAR(13) NOT NULL DEFAULT '' COMMENT '名称',
  MODIFY COLUMN `password` VARCHAR(128) NOT NULL DEFAULT '' COMMENT '登录密码(服务端加盐哈希后的值)',
  MODIFY COLUMN `pin` VARCHAR(10) NOT NULL DEFAULT '' COMMENT '游戏内 PIN 码(二次密码)，默认 enable_pin=false',
  MODIFY COLUMN `pic` VARCHAR(26) NOT NULL DEFAULT '' COMMENT '角色二次密码 PIC，默认 enable_pic=false',
  MODIFY COLUMN `loggedin` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '在线状态：0=离线，>0=在线(登录服/频道服状态位)',
  MODIFY COLUMN `lastlogin` TIMESTAMP NULL DEFAULT NULL COMMENT '上次登录时间',
  MODIFY COLUMN `createdat` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '账号注册时间',
  MODIFY COLUMN `birthday` DATE NOT NULL DEFAULT '2005-05-11' COMMENT '出生日期(YYYYMMDD)，用于年龄/生日校验',
  MODIFY COLUMN `banned` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否封禁：0=正常，1=封禁',
  MODIFY COLUMN `banreason` TEXT COMMENT '封禁原因',
  MODIFY COLUMN `macs` TINYTEXT COMMENT '登录时上报的 MAC 地址列表',
  MODIFY COLUMN `nxCredit` INT(11) DEFAULT NULL COMMENT '点券(Credit)余额',
  MODIFY COLUMN `maplePoint` INT(11) DEFAULT NULL COMMENT '抵用券(Maple Point)余额',
  MODIFY COLUMN `nxPrepaid` INT(11) DEFAULT NULL COMMENT '预备点券(Prepaid)余额',
  MODIFY COLUMN `characterslots` TINYINT(2) NOT NULL DEFAULT '3' COMMENT '角色栏位上限',
  MODIFY COLUMN `gender` TINYINT(2) NOT NULL DEFAULT '10' COMMENT '性别',
  MODIFY COLUMN `tempban` TIMESTAMP NOT NULL DEFAULT '2005-05-11 00:00:00' COMMENT '临时封禁截止时间',
  MODIFY COLUMN `greason` TINYINT(4) NOT NULL DEFAULT '0' COMMENT 'GM 备注/封禁操作等级',
  MODIFY COLUMN `tos` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否已同意用户协议(EULA)',
  MODIFY COLUMN `sitelogged` TEXT COMMENT '网站(管理后台)登录状态',
  MODIFY COLUMN `webadmin` INT(1) DEFAULT '0' COMMENT '是否为网站管理员',
  MODIFY COLUMN `nick` VARCHAR(20) DEFAULT NULL COMMENT '账号昵称',
  MODIFY COLUMN `mute` INT(1) DEFAULT '0' COMMENT '是否禁言',
  MODIFY COLUMN `email` VARCHAR(45) DEFAULT NULL COMMENT '邮箱地址',
  MODIFY COLUMN `ip` TEXT COMMENT 'IP 地址',
  MODIFY COLUMN `rewardpoints` INT(11) NOT NULL DEFAULT '0' COMMENT '奖励积分',
  MODIFY COLUMN `votepoints` INT(11) NOT NULL DEFAULT '0' COMMENT '投票积分',
  MODIFY COLUMN `hwid` VARCHAR(12) NOT NULL DEFAULT '' COMMENT '硬件指纹(HWID)',
  MODIFY COLUMN `language` INT(1) NOT NULL DEFAULT '3' COMMENT '语言偏好(zh-CN/en-US)';

-- ---- alliance：公会联盟表：联盟名称、容量、公告与五级职位称谓 ----
ALTER TABLE `alliance` COMMENT = '公会联盟表：联盟名称、容量、公告与五级职位称谓';
ALTER TABLE `alliance`
  MODIFY COLUMN `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键 ID',
  MODIFY COLUMN `name` VARCHAR(13) NOT NULL COMMENT '名称',
  MODIFY COLUMN `capacity` INT(10) UNSIGNED NOT NULL DEFAULT '2' COMMENT '联盟可容纳公会数',
  MODIFY COLUMN `notice` VARCHAR(20) NOT NULL DEFAULT '' COMMENT '公告内容',
  MODIFY COLUMN `rank1` VARCHAR(11) NOT NULL DEFAULT 'Master' COMMENT '联盟 1 级职位称谓',
  MODIFY COLUMN `rank2` VARCHAR(11) NOT NULL DEFAULT 'Jr. Master' COMMENT '联盟 2 级职位称谓',
  MODIFY COLUMN `rank3` VARCHAR(11) NOT NULL DEFAULT 'Member' COMMENT '联盟 3 级职位称谓',
  MODIFY COLUMN `rank4` VARCHAR(11) NOT NULL DEFAULT 'Member' COMMENT '联盟 4 级职位称谓',
  MODIFY COLUMN `rank5` VARCHAR(11) NOT NULL DEFAULT 'Member' COMMENT '联盟 5 级职位称谓';

-- ---- allianceguilds：联盟-公会关联表：记录联盟下属公会 ----
ALTER TABLE `allianceguilds` COMMENT = '联盟-公会关联表：记录联盟下属公会';
ALTER TABLE `allianceguilds`
  MODIFY COLUMN `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键 ID',
  MODIFY COLUMN `allianceid` INT(10) NOT NULL DEFAULT '-1' COMMENT '联盟 ID，关联 alliance.id',
  MODIFY COLUMN `guildid` INT(10) NOT NULL DEFAULT '-1' COMMENT '公会 ID，关联 guilds.guildid';

-- ---- area_info：角色区域信息表：按区域存档的自定义数据 ----
ALTER TABLE `area_info` COMMENT = '角色区域信息表：按区域存档的自定义数据';
ALTER TABLE `area_info`
  MODIFY COLUMN `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键 ID',
  MODIFY COLUMN `charid` INT(11) NOT NULL COMMENT '角色 ID，关联 characters.id',
  MODIFY COLUMN `area` INT(11) NOT NULL COMMENT '区域编号',
  MODIFY COLUMN `info` VARCHAR(200) NOT NULL COMMENT '附加信息';

-- ---- autoban_config：自动封禁规则表：按违规类型配置积分、阈值与过期时间 ----
ALTER TABLE `autoban_config` COMMENT = '自动封禁规则表：按违规类型配置积分、阈值与过期时间';
ALTER TABLE `autoban_config`
  MODIFY COLUMN `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  MODIFY COLUMN `type` VARCHAR(32) NOT NULL COMMENT '封禁类型，对应AutobanFactory枚举名',
  MODIFY COLUMN `disabled` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否禁用，1=禁用该类型封禁检测',
  MODIFY COLUMN `points` INT(11) NULL COMMENT '触发封禁所需积分，NULL使用枚举默认值',
  MODIFY COLUMN `expire_time` BIGINT NULL COMMENT '积分过期时间(毫秒)，NULL使用枚举默认值',
  MODIFY COLUMN `description` VARCHAR(128) NULL COMMENT '描述说明',
  MODIFY COLUMN `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  MODIFY COLUMN `update_time` TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';

-- ---- bbs_replies：公会 BBS 回帖表 ----
ALTER TABLE `bbs_replies` COMMENT = '公会 BBS 回帖表';
ALTER TABLE `bbs_replies`
  MODIFY COLUMN `replyid` INT(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '回帖 ID',
  MODIFY COLUMN `threadid` INT(10) unsigned NOT NULL COMMENT '主题帖 ID',
  MODIFY COLUMN `postercid` INT(10) unsigned NOT NULL COMMENT '发帖人角色 ID',
  MODIFY COLUMN `TIMESTAMP` BIGINT(20) unsigned NOT NULL COMMENT '时间戳',
  MODIFY COLUMN `content` VARCHAR(26) NOT NULL DEFAULT '' COMMENT '回帖正文';

-- ---- bbs_threads：公会 BBS 主题帖表 ----
ALTER TABLE `bbs_threads` COMMENT = '公会 BBS 主题帖表';
ALTER TABLE `bbs_threads`
  MODIFY COLUMN `threadid` INT(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主题帖 ID',
  MODIFY COLUMN `postercid` INT(10) unsigned NOT NULL COMMENT '发帖人角色 ID',
  MODIFY COLUMN `name` VARCHAR(26) NOT NULL DEFAULT '' COMMENT '名称',
  MODIFY COLUMN `TIMESTAMP` BIGINT(20) unsigned NOT NULL COMMENT '时间戳',
  MODIFY COLUMN `icon` SMALLINT(5) unsigned NOT NULL COMMENT '主题帖图标编号',
  MODIFY COLUMN `replycount` SMALLINT(5) unsigned NOT NULL DEFAULT '0' COMMENT '回帖数',
  MODIFY COLUMN `startpost` TEXT NOT NULL COMMENT '首帖正文',
  MODIFY COLUMN `guildid` INT(10) unsigned NOT NULL COMMENT '公会 ID，关联 guilds.guildid',
  MODIFY COLUMN `localthreadid` INT(10) unsigned NOT NULL COMMENT '公会内本地帖号';

-- ---- bosslog_daily：BOSS 每日挑战记录表 ----
ALTER TABLE `bosslog_daily` COMMENT = 'BOSS 每日挑战记录表';
ALTER TABLE `bosslog_daily`
  MODIFY COLUMN `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键 ID',
  MODIFY COLUMN `characterid` INT(11) NOT NULL COMMENT '角色 ID，关联 characters.id',
  MODIFY COLUMN `bosstype` ENUM ('ZAKUM','HORNTAIL','PINKBEAN','SCARGA','PAPULATUS') NOT NULL COMMENT 'BOSS 类型',
  MODIFY COLUMN `attempttime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '挑战时间';

-- ---- bosslog_weekly：BOSS 每周挑战记录表 ----
ALTER TABLE `bosslog_weekly` COMMENT = 'BOSS 每周挑战记录表';
ALTER TABLE `bosslog_weekly`
  MODIFY COLUMN `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键 ID',
  MODIFY COLUMN `characterid` INT(11) NOT NULL COMMENT '角色 ID，关联 characters.id',
  MODIFY COLUMN `bosstype` ENUM ('ZAKUM','HORNTAIL','PINKBEAN','SCARGA','PAPULATUS') NOT NULL COMMENT 'BOSS 类型',
  MODIFY COLUMN `attempttime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '挑战时间';

-- ---- buddies：好友关系表：好友角色、待确认状态与分组 ----
ALTER TABLE `buddies` COMMENT = '好友关系表：好友角色、待确认状态与分组';
ALTER TABLE `buddies`
  MODIFY COLUMN `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键 ID',
  MODIFY COLUMN `characterid` INT(11) NOT NULL COMMENT '角色 ID，关联 characters.id',
  MODIFY COLUMN `buddyid` INT(11) NOT NULL COMMENT '好友角色 ID',
  MODIFY COLUMN `pending` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '是否待对方确认，1=待确认',
  MODIFY COLUMN `group` VARCHAR(17) DEFAULT '0' COMMENT '好友分组名称';

-- ---- characterexplogs：经验获取日志表：记录每次获得经验的倍率、倍率券与累计值 ----
ALTER TABLE `characterexplogs` COMMENT = '经验获取日志表：记录每次获得经验的倍率、倍率券与累计值';
ALTER TABLE `characterexplogs`
  MODIFY COLUMN `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '自增主键 ID',
  MODIFY COLUMN `world_exp_rate` float COMMENT '本次获得经验时的大区经验倍率',
  MODIFY COLUMN `exp_coupon` int(0) NULL DEFAULT NULL COMMENT '经验券倍率',
  MODIFY COLUMN `gained_exp` bigint(0) NULL DEFAULT NULL COMMENT '本次获得经验',
  MODIFY COLUMN `current_exp` bigint(0) NULL DEFAULT NULL COMMENT '获得后的角色累计经验',
  MODIFY COLUMN `exp_gain_time` timestamp(0) NULL DEFAULT NULL COMMENT '经验获得时间',
  MODIFY COLUMN `charid` int(0) NULL DEFAULT NULL COMMENT '角色 ID，关联 characters.id';

-- ---- characters：角色主表：账号下角色存档、属性、等级经验、背包容量与社会关系 ----
ALTER TABLE `characters` COMMENT = '角色主表：账号下角色存档、属性、等级经验、背包容量与社会关系';
ALTER TABLE `characters`
  MODIFY COLUMN `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键 ID',
  MODIFY COLUMN `accountid` INT(11) NOT NULL DEFAULT '0' COMMENT '账号 ID，关联 accounts.id',
  MODIFY COLUMN `world` INT(11) NOT NULL DEFAULT '0' COMMENT '大区(世界) ID',
  MODIFY COLUMN `name` VARCHAR(13) NOT NULL DEFAULT '' COMMENT '名称',
  MODIFY COLUMN `level` INT(11) NOT NULL DEFAULT '1' COMMENT '等级',
  MODIFY COLUMN `exp` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '角色当前等级已累积经验；999 级突破后为 BIGINT，封包 8 字节',
  MODIFY COLUMN `gachaexp` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '扭蛋/经验券累积的待兑换经验池，兑换后转入 exp，非等级经验',
  MODIFY COLUMN `str` INT(11) NOT NULL DEFAULT '12' COMMENT '力量加成',
  MODIFY COLUMN `dex` INT(11) NOT NULL DEFAULT '5' COMMENT '敏捷加成',
  MODIFY COLUMN `luk` INT(11) NOT NULL DEFAULT '4' COMMENT '运气加成',
  MODIFY COLUMN `int` INT(11) NOT NULL DEFAULT '4' COMMENT '智力加成',
  MODIFY COLUMN `hp` INT(11) NOT NULL DEFAULT '50' COMMENT 'HP 值/加成',
  MODIFY COLUMN `mp` INT(11) NOT NULL DEFAULT '5' COMMENT 'MP 值/加成',
  MODIFY COLUMN `maxhp` INT(11) NOT NULL DEFAULT '50' COMMENT '最大 HP',
  MODIFY COLUMN `maxmp` INT(11) NOT NULL DEFAULT '5' COMMENT '最大 MP',
  MODIFY COLUMN `meso` INT(11) NOT NULL DEFAULT '0' COMMENT '金币(冒险币)数量',
  MODIFY COLUMN `hpMpUsed` INT(11) UNSIGNED NOT NULL DEFAULT '0' COMMENT 'HP/MP 自动补给已使用次数',
  MODIFY COLUMN `job` INT(11) NOT NULL DEFAULT '0' COMMENT '职业 ID',
  MODIFY COLUMN `skincolor` INT(11) NOT NULL DEFAULT '0' COMMENT '肤色 ID',
  MODIFY COLUMN `gender` INT(11) NOT NULL DEFAULT '0' COMMENT '性别',
  MODIFY COLUMN `fame` INT(11) NOT NULL DEFAULT '0' COMMENT '人气(声誉)值',
  MODIFY COLUMN `fquest` INT(11) NOT NULL DEFAULT '0' COMMENT '已完成的新手/引导任务标记',
  MODIFY COLUMN `hair` INT(11) NOT NULL DEFAULT '0' COMMENT '发型 ID',
  MODIFY COLUMN `face` INT(11) NOT NULL DEFAULT '0' COMMENT '脸型 ID',
  MODIFY COLUMN `ap` INT(11) NOT NULL DEFAULT '0' COMMENT '可分配能力点(AP)',
  MODIFY COLUMN `sp` VARCHAR(128) NOT NULL DEFAULT '0,0,0,0,0,0,0,0,0,0' COMMENT '可分配技能点(SP)',
  MODIFY COLUMN `map` INT(11) NOT NULL DEFAULT '0' COMMENT '地图 ID',
  MODIFY COLUMN `spawnpoint` INT(11) NOT NULL DEFAULT '0' COMMENT '复活/出生传送点 ID',
  MODIFY COLUMN `gm` TINYINT(1) NOT NULL DEFAULT '0' COMMENT 'GM 等级，0=普通玩家；登录包仅下发 gmLevel>1 的布尔值',
  MODIFY COLUMN `party` INT(11) NOT NULL DEFAULT '0' COMMENT '所属队伍 ID，-1 表示无队伍',
  MODIFY COLUMN `buddyCapacity` INT(11) NOT NULL DEFAULT '25' COMMENT '好友栏位上限',
  MODIFY COLUMN `createdate` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  MODIFY COLUMN `rank` INT(10) UNSIGNED NOT NULL DEFAULT '1' COMMENT '全服排名名次',
  MODIFY COLUMN `rankMove` INT(11) NOT NULL DEFAULT '0' COMMENT '排名升降幅度',
  MODIFY COLUMN `jobRank` INT(10) UNSIGNED NOT NULL DEFAULT '1' COMMENT '职业排名名次',
  MODIFY COLUMN `jobRankMove` INT(11) NOT NULL DEFAULT '0' COMMENT '职业排名升降幅度',
  MODIFY COLUMN `guildid` INT(10) UNSIGNED NOT NULL DEFAULT '0' COMMENT '公会 ID，关联 guilds.guildid',
  MODIFY COLUMN `guildrank` INT(10) UNSIGNED NOT NULL DEFAULT '5' COMMENT '公会内职位等级(1=会长)',
  MODIFY COLUMN `messengerid` INT(10) UNSIGNED NOT NULL DEFAULT '0' COMMENT '所属聊天室(小屋) ID',
  MODIFY COLUMN `messengerposition` INT(10) UNSIGNED NOT NULL DEFAULT '4' COMMENT '聊天室内座位号',
  MODIFY COLUMN `mountlevel` INT(9) NOT NULL DEFAULT '1' COMMENT '坐骑等级',
  MODIFY COLUMN `mountexp` INT(9) NOT NULL DEFAULT '0' COMMENT '坐骑经验',
  MODIFY COLUMN `mounttiredness` INT(9) NOT NULL DEFAULT '0' COMMENT '坐骑疲劳度',
  MODIFY COLUMN `omokwins` INT(11) NOT NULL DEFAULT '0' COMMENT '五子棋胜场',
  MODIFY COLUMN `omoklosses` INT(11) NOT NULL DEFAULT '0' COMMENT '五子棋负场',
  MODIFY COLUMN `omokties` INT(11) NOT NULL DEFAULT '0' COMMENT '五子棋平局',
  MODIFY COLUMN `matchcardwins` INT(11) NOT NULL DEFAULT '0' COMMENT '翻牌配对胜场',
  MODIFY COLUMN `matchcardlosses` INT(11) NOT NULL DEFAULT '0' COMMENT '翻牌配对负场',
  MODIFY COLUMN `matchcardties` INT(11) NOT NULL DEFAULT '0' COMMENT '翻牌配对平局',
  MODIFY COLUMN `MerchantMesos` INT(11) DEFAULT '0' COMMENT '雇佣商店待领取金币',
  MODIFY COLUMN `HasMerchant` TINYINT(1) DEFAULT '0' COMMENT '是否拥有已开启的雇佣商店',
  MODIFY COLUMN `equipslots` INT(11) NOT NULL DEFAULT '24' COMMENT '装备栏格数',
  MODIFY COLUMN `useslots` INT(11) NOT NULL DEFAULT '24' COMMENT '消耗栏格数',
  MODIFY COLUMN `setupslots` INT(11) NOT NULL DEFAULT '24' COMMENT '设置(安置)栏格数',
  MODIFY COLUMN `etcslots` INT(11) NOT NULL DEFAULT '24' COMMENT '其他栏格数',
  MODIFY COLUMN `familyId` INT(11) NOT NULL DEFAULT '-1' COMMENT '家族 ID',
  MODIFY COLUMN `monsterbookcover` INT(11) NOT NULL DEFAULT '0' COMMENT '怪物图鉴封面卡片 ID',
  MODIFY COLUMN `allianceRank` INT(10) NOT NULL DEFAULT '5' COMMENT '联盟内职位等级',
  MODIFY COLUMN `vanquisherStage` INT(11) UNSIGNED NOT NULL DEFAULT '0' COMMENT '武陵道场/征服者当前层数',
  MODIFY COLUMN `ariantPoints` INT(11) UNSIGNED NOT NULL DEFAULT '0' COMMENT '阿里安特竞技场积分',
  MODIFY COLUMN `dojoPoints` INT(11) UNSIGNED NOT NULL DEFAULT '0' COMMENT '武陵道场积分',
  MODIFY COLUMN `lastDojoStage` INT(10) UNSIGNED NOT NULL DEFAULT '0' COMMENT '上次道场到达层数',
  MODIFY COLUMN `finishedDojoTutorial` TINYINT(1) UNSIGNED NOT NULL DEFAULT '0' COMMENT '是否完成道场新手引导',
  MODIFY COLUMN `vanquisherKills` INT(11) UNSIGNED NOT NULL DEFAULT '0' COMMENT '征服者击杀数',
  MODIFY COLUMN `summonValue` INT(11) UNSIGNED NOT NULL DEFAULT '0' COMMENT '召唤兽相关数值',
  MODIFY COLUMN `partnerId` INT(11) NOT NULL DEFAULT '0' COMMENT '配偶/配对角色 ID',
  MODIFY COLUMN `marriageItemId` INT(11) NOT NULL DEFAULT '0' COMMENT '结婚戒指物品 ID',
  MODIFY COLUMN `reborns` INT(5) NOT NULL DEFAULT '0' COMMENT '转生次数',
  MODIFY COLUMN `PQPoints` INT(11) NOT NULL DEFAULT '0' COMMENT '组队任务(PQ)积分',
  MODIFY COLUMN `dataString` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '角色扩展序列化数据',
  MODIFY COLUMN `lastLogoutTime` TIMESTAMP NOT NULL DEFAULT '2015-01-01 05:00:00' COMMENT '上次登出时间',
  MODIFY COLUMN `lastExpGainTime` TIMESTAMP NOT NULL DEFAULT '2015-01-01 05:00:00' COMMENT '上次获得经验时间',
  MODIFY COLUMN `partySearch` TINYINT(1) NOT NULL DEFAULT '1' COMMENT '是否开启组队招募',
  MODIFY COLUMN `jailexpire` bigint(20) NOT NULL DEFAULT '0' COMMENT '监禁/禁闭到期时间';

-- ---- command_info：GM 指令配置表：指令语法、可用等级与后端实现类 ----
ALTER TABLE `command_info` COMMENT = 'GM 指令配置表：指令语法、可用等级与后端实现类';
ALTER TABLE `command_info`
  MODIFY COLUMN `id` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  MODIFY COLUMN `syntax` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '指令',
  MODIFY COLUMN `level` int NOT NULL COMMENT '指令等级0-6',
  MODIFY COLUMN `enabled` tinyint(1) UNSIGNED ZEROFILL NOT NULL DEFAULT 1 COMMENT '0不启用 1启用',
  MODIFY COLUMN `clazz` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '指令对应后端java类名',
  MODIFY COLUMN `default_level` int NOT NULL COMMENT '默认指令等级0-6（该字段不可被修改）';

-- ---- cooldowns：技能冷却表：角色技能冷却时长与开始时间 ----
ALTER TABLE `cooldowns` COMMENT = '技能冷却表：角色技能冷却时长与开始时间';
ALTER TABLE `cooldowns`
  MODIFY COLUMN `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键 ID',
  MODIFY COLUMN `charid` INT(11) NOT NULL COMMENT '角色 ID，关联 characters.id',
  MODIFY COLUMN `SkillID` INT(11) NOT NULL COMMENT '技能 ID',
  MODIFY COLUMN `length` BIGINT(20) UNSIGNED NOT NULL COMMENT '持续时长',
  MODIFY COLUMN `StartTime` BIGINT(20) UNSIGNED NOT NULL COMMENT '冷却开始时间';

-- ---- drop_data：怪物掉落表：按掉落源配置物品与爆率 ----
ALTER TABLE `drop_data` COMMENT = '怪物掉落表：按掉落源配置物品与爆率';
ALTER TABLE `drop_data`
  MODIFY COLUMN `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键 ID',
  MODIFY COLUMN `dropperid` INT(11) NOT NULL COMMENT '掉落源 ID(怪物/反应器)',
  MODIFY COLUMN `itemid` INT(11) NOT NULL DEFAULT '0' COMMENT '物品 ID',
  MODIFY COLUMN `minimum_quantity` INT(11) NOT NULL DEFAULT '1' COMMENT '最小掉落/产出数量',
  MODIFY COLUMN `maximum_quantity` INT(11) NOT NULL DEFAULT '1' COMMENT '最大掉落/产出数量',
  MODIFY COLUMN `questid` INT(11) NOT NULL DEFAULT '0' COMMENT '任务 ID，0 表示无任务条件',
  MODIFY COLUMN `chance` INT(11) NOT NULL DEFAULT '0' COMMENT '掉落概率(百万分比)';

-- ---- drop_data_global：全服通用掉落表：按大区(大陆)配置全局掉落 ----
ALTER TABLE `drop_data_global` COMMENT = '全服通用掉落表：按大区(大陆)配置全局掉落';
ALTER TABLE `drop_data_global`
  MODIFY COLUMN `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键 ID',
  MODIFY COLUMN `continent` TINYINT(1) NOT NULL DEFAULT '-1' COMMENT '大区/大陆编号，用于区分全局掉落适用范围',
  MODIFY COLUMN `itemid` INT(11) NOT NULL DEFAULT '0' COMMENT '物品 ID',
  MODIFY COLUMN `minimum_quantity` INT(11) NOT NULL DEFAULT '1' COMMENT '最小掉落/产出数量',
  MODIFY COLUMN `maximum_quantity` INT(11) NOT NULL DEFAULT '1' COMMENT '最大掉落/产出数量',
  MODIFY COLUMN `questid` INT(11) NOT NULL DEFAULT '0' COMMENT '任务 ID，0 表示无任务条件',
  MODIFY COLUMN `chance` INT(11) NOT NULL DEFAULT '0' COMMENT '掉落概率(百万分比)',
  MODIFY COLUMN `comments` VARCHAR(45) DEFAULT NULL COMMENT '备注';

-- ---- dueyitems：快递包裹内物品表：包裹与物品实例的关联 ----
ALTER TABLE `dueyitems` COMMENT = '快递包裹内物品表：包裹与物品实例的关联';
ALTER TABLE `dueyitems`
  MODIFY COLUMN `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键 ID',
  MODIFY COLUMN `PackageId` INT(10) UNSIGNED NOT NULL DEFAULT '0' COMMENT '快递包裹 ID，关联 dueypackages.PackageId',
  MODIFY COLUMN `inventoryitemid` INT(10) UNSIGNED NOT NULL DEFAULT '0' COMMENT '物品实例 ID，关联 inventoryitems.inventoryitemid';

-- ---- dueypackages：快递包裹表：收发人、附带金币、留言与领取状态 ----
ALTER TABLE `dueypackages` COMMENT = '快递包裹表：收发人、附带金币、留言与领取状态';
ALTER TABLE `dueypackages`
  MODIFY COLUMN `PackageId` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '快递包裹 ID，关联 dueypackages.PackageId',
  MODIFY COLUMN `ReceiverId` INT(10) UNSIGNED NOT NULL COMMENT '收件人角色 ID',
  MODIFY COLUMN `SenderName` VARCHAR(13) NOT NULL COMMENT '寄件人名称',
  MODIFY COLUMN `Mesos` INT(10) UNSIGNED DEFAULT '0' COMMENT '包裹附带金币',
  MODIFY COLUMN `TIMESTAMP` TIMESTAMP NOT NULL DEFAULT '2015-01-01 05:00:00' COMMENT '时间戳',
  MODIFY COLUMN `Message` VARCHAR(200) NULL COMMENT '留言内容',
  MODIFY COLUMN `Checked` TINYINT(1) UNSIGNED DEFAULT '1' COMMENT '是否已被领取',
  MODIFY COLUMN `Type` TINYINT(1) UNSIGNED DEFAULT '0' COMMENT '包裹类型';

-- ---- eventstats：活动统计表：角色参与活动的名称与进度数据 ----
ALTER TABLE `eventstats` COMMENT = '活动统计表：角色参与活动的名称与进度数据';
ALTER TABLE `eventstats`
  MODIFY COLUMN `characterid` INT(11) UNSIGNED NOT NULL COMMENT '角色 ID，关联 characters.id',
  MODIFY COLUMN `name` VARCHAR(11) NOT NULL DEFAULT '0' COMMENT '活动名称',
  MODIFY COLUMN `info` INT(11) NOT NULL COMMENT '活动进度/统计值';

-- ---- extend_value：扩展字段表：账号/角色自定义扩展值与日清周清周期 ----
ALTER TABLE `extend_value` COMMENT = '扩展字段表：账号/角色自定义扩展值与日清周清周期';
ALTER TABLE `extend_value`
  MODIFY COLUMN `extend_id` varchar(50) not null COMMENT '扩展字段id',
  MODIFY COLUMN `extend_type` int not null COMMENT '扩展字段类型，11-账号，12-账号日清，13-账号周清；21-角色，22-角色日清，23-角色周清',
  MODIFY COLUMN `extend_name` varchar(50) not null COMMENT '扩展字段名称',
  MODIFY COLUMN `extend_value` varchar(255) COMMENT '扩展字段值',
  MODIFY COLUMN `create_time` datetime not null default current_timestamp COMMENT '创建时间',
  MODIFY COLUMN `update_time` datetime COMMENT '更新时间';

-- ---- famelog：人气(声誉)变动日志表 ----
ALTER TABLE `famelog` COMMENT = '人气(声誉)变动日志表';
ALTER TABLE `famelog`
  MODIFY COLUMN `famelogid` INT(11) NOT NULL AUTO_INCREMENT COMMENT '人气日志 ID',
  MODIFY COLUMN `characterid` INT(11) NOT NULL DEFAULT '0' COMMENT '角色 ID，关联 characters.id',
  MODIFY COLUMN `characterid_to` INT(11) NOT NULL DEFAULT '0' COMMENT '被给/扣人气的角色 ID',
  MODIFY COLUMN `when` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '人气变动时间';

-- ---- family_character：家族成员表：角色所属家族、声望与族谱上级 ----
ALTER TABLE `family_character` COMMENT = '家族成员表：角色所属家族、声望与族谱上级';
ALTER TABLE `family_character`
  MODIFY COLUMN `cid` INT(11) NOT NULL COMMENT '角色 ID，关联 characters.id',
  MODIFY COLUMN `familyid` INT(11) NOT NULL COMMENT '家族 ID',
  MODIFY COLUMN `seniorid` INT(11) NOT NULL COMMENT '族谱上级(长辈)角色 ID',
  MODIFY COLUMN `reputation` INT(11) NOT NULL DEFAULT '0' COMMENT '当前家族声望',
  MODIFY COLUMN `todaysrep` INT(11) NOT NULL DEFAULT '0' COMMENT '今日获得声望',
  MODIFY COLUMN `totalreputation` INT(11) NOT NULL DEFAULT '0' COMMENT '累计家族声望',
  MODIFY COLUMN `reptosenior` INT(11) NOT NULL DEFAULT '0' COMMENT '向上级贡献的声望',
  MODIFY COLUMN `precepts` VARCHAR(200) DEFAULT NULL COMMENT '家训/家族格言',
  MODIFY COLUMN `lastresettime` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '上次日清重置时间';

-- ---- family_entitlement：家族特权领取记录表 ----
ALTER TABLE `family_entitlement` COMMENT = '家族特权领取记录表';
ALTER TABLE `family_entitlement`
  MODIFY COLUMN `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键 ID',
  MODIFY COLUMN `charid` INT(11) NOT NULL COMMENT '角色 ID，关联 characters.id',
  MODIFY COLUMN `entitlementid` INT(11) NOT NULL COMMENT '家族特权 ID',
  MODIFY COLUMN `TIMESTAMP` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '时间戳';

-- ---- fredstorage：弗雷德储物/每日留言表 ----
ALTER TABLE `fredstorage` COMMENT = '弗雷德储物/每日留言表';
ALTER TABLE `fredstorage`
  MODIFY COLUMN `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键 ID',
  MODIFY COLUMN `cid` INT(10) UNSIGNED NOT NULL COMMENT '角色 ID，关联 characters.id',
  MODIFY COLUMN `daynotes` INT(4) UNSIGNED NOT NULL COMMENT '每日留言内容',
  MODIFY COLUMN `TIMESTAMP` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '时间戳';

-- ---- gachapon：扭蛋机物品池表：扭蛋机产出物品与概率 ----
ALTER TABLE `gachapon` COMMENT = '扭蛋机物品池表：扭蛋机产出物品与概率';
ALTER TABLE `gachapon`
  MODIFY COLUMN `id` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  MODIFY COLUMN `gachaponId` int NULL DEFAULT NULL COMMENT '扭蛋机 ID',
  MODIFY COLUMN `itemId` int NULL DEFAULT NULL COMMENT '道具ID',
  MODIFY COLUMN `stack` int NULL DEFAULT 1 COMMENT '产出物品堆叠数量',
  MODIFY COLUMN `probability` decimal(5, 4) NULL DEFAULT NULL COMMENT '概率',
  MODIFY COLUMN `createDate` datetime NULL DEFAULT NULL COMMENT '配置创建时间',
  MODIFY COLUMN `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '配置备注';

-- ---- gachapon_reward：扭蛋奖池物品表：奖池内物品与数量 ----
ALTER TABLE `gachapon_reward` COMMENT = '扭蛋奖池物品表：奖池内物品与数量';
ALTER TABLE `gachapon_reward`
  MODIFY COLUMN `id` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  MODIFY COLUMN `pool_id` int NOT NULL COMMENT '奖池 ID，关联 gachapon_reward_pool.id',
  MODIFY COLUMN `item_id` int NOT NULL COMMENT '奖励物品 ID',
  MODIFY COLUMN `quantity` int NOT NULL DEFAULT 1 COMMENT '单次抽取数量',
  MODIFY COLUMN `create_time` datetime NULL DEFAULT NULL COMMENT '创建日期',
  MODIFY COLUMN `comment` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注';

-- ---- gachapon_reward_pool：扭蛋奖池表：奖池名称、权重、公示概率与生效时段 ----
ALTER TABLE `gachapon_reward_pool` COMMENT = '扭蛋奖池表：奖池名称、权重、公示概率与生效时段';
ALTER TABLE `gachapon_reward_pool`
  MODIFY COLUMN `id` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  MODIFY COLUMN `name` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '转蛋机奖池名称',
  MODIFY COLUMN `gachapon_id` int NOT NULL COMMENT '对应扭蛋机 ID',
  MODIFY COLUMN `weight` int NOT NULL COMMENT '权重',
  MODIFY COLUMN `is_public` tinyint(1) UNSIGNED ZEROFILL NOT NULL DEFAULT 0 COMMENT '是否公共奖池 0为否 1为是',
  MODIFY COLUMN `prob` int NOT NULL DEFAULT 0 COMMENT '公示概率',
  MODIFY COLUMN `start_time` datetime NOT NULL COMMENT '奖池的启用日期',
  MODIFY COLUMN `end_time` datetime NULL DEFAULT NULL COMMENT '奖池的结束日期',
  MODIFY COLUMN `notification` tinyint(1) UNSIGNED ZEROFILL NOT NULL DEFAULT 0 COMMENT '是否喇叭通知 0为否 1为是',
  MODIFY COLUMN `comment` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注';

-- ---- game_config：游戏参数表：可热重载的运营参数(type/subType/code/value) ----
ALTER TABLE `game_config` COMMENT = '游戏参数表：可热重载的运营参数(type/subType/code/value)';
ALTER TABLE `game_config`
  MODIFY COLUMN `id` bigint auto_increment COMMENT '自增id',
  MODIFY COLUMN `config_type` varchar(32) not null COMMENT '参数类型',
  MODIFY COLUMN `config_sub_type` varchar(32) COMMENT '参数子类型',
  MODIFY COLUMN `config_clazz` varchar(256) COMMENT '参数值java类型',
  MODIFY COLUMN `config_code` varchar(64) not null COMMENT '参数名',
  MODIFY COLUMN `config_value` varchar(256) not null COMMENT '参数值',
  MODIFY COLUMN `config_desc` varchar(512) COMMENT '参数描述，中英文，关联i18n表lang_resources',
  MODIFY COLUMN `update_time` timestamp COMMENT '最后更新时间';

-- ---- gifts：礼物赠送记录表 ----
ALTER TABLE `gifts` COMMENT = '礼物赠送记录表';
ALTER TABLE `gifts`
  MODIFY COLUMN `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键 ID',
  MODIFY COLUMN `to` INT(11) NOT NULL COMMENT '收件人',
  MODIFY COLUMN `from` VARCHAR(13) NOT NULL COMMENT '发件人',
  MODIFY COLUMN `message` TINYTEXT NOT NULL COMMENT '留言内容',
  MODIFY COLUMN `sn` INT(10) UNSIGNED NOT NULL COMMENT '物品唯一序列号(SN)',
  MODIFY COLUMN `ringid` INT(10) NOT NULL COMMENT '戒指配对 ID，-1 表示无';

-- ---- guilds：公会表：会长、徽章、五级职位称谓、容量、公告与所属联盟 ----
ALTER TABLE `guilds` COMMENT = '公会表：会长、徽章、五级职位称谓、容量、公告与所属联盟';
ALTER TABLE `guilds`
  MODIFY COLUMN `guildid` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '公会 ID，关联 guilds.guildid',
  MODIFY COLUMN `leader` INT(10) UNSIGNED NOT NULL DEFAULT '0' COMMENT '会长角色 ID',
  MODIFY COLUMN `GP` INT(10) UNSIGNED NOT NULL DEFAULT '0' COMMENT '公会积分(GP)',
  MODIFY COLUMN `logo` INT(10) UNSIGNED DEFAULT NULL COMMENT '公会徽章图案 ID',
  MODIFY COLUMN `logoColor` SMALLINT(5) UNSIGNED NOT NULL DEFAULT '0' COMMENT '徽章颜色 ID',
  MODIFY COLUMN `name` VARCHAR(45) NOT NULL COMMENT '名称',
  MODIFY COLUMN `rank1title` VARCHAR(45) NOT NULL DEFAULT 'Master' COMMENT '公会 1 级职位称谓',
  MODIFY COLUMN `rank2title` VARCHAR(45) NOT NULL DEFAULT 'Jr. Master' COMMENT '公会 2 级职位称谓',
  MODIFY COLUMN `rank3title` VARCHAR(45) NOT NULL DEFAULT 'Member' COMMENT '公会 3 级职位称谓',
  MODIFY COLUMN `rank4title` VARCHAR(45) NOT NULL DEFAULT 'Member' COMMENT '公会 4 级职位称谓',
  MODIFY COLUMN `rank5title` VARCHAR(45) NOT NULL DEFAULT 'Member' COMMENT '公会 5 级职位称谓',
  MODIFY COLUMN `capacity` INT(10) UNSIGNED NOT NULL DEFAULT '10' COMMENT '容量上限',
  MODIFY COLUMN `logoBG` INT(10) UNSIGNED DEFAULT NULL COMMENT '徽章背景图案 ID',
  MODIFY COLUMN `logoBGColor` SMALLINT(5) UNSIGNED NOT NULL DEFAULT '0' COMMENT '徽章背景颜色 ID',
  MODIFY COLUMN `notice` VARCHAR(101) DEFAULT NULL COMMENT '公告内容',
  MODIFY COLUMN `signature` INT(11) NOT NULL DEFAULT '0' COMMENT '公会签名/简介',
  MODIFY COLUMN `allianceId` INT(11) UNSIGNED NOT NULL DEFAULT '0' COMMENT '联盟 ID，关联 alliance.id';

-- ---- hp_mp_alert：HP/MP 自动补给提醒阈值表 ----
ALTER TABLE `hp_mp_alert` COMMENT = 'HP/MP 自动补给提醒阈值表';
ALTER TABLE `hp_mp_alert`
  MODIFY COLUMN `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键 ID',
  MODIFY COLUMN `c_id` INT(11) UNSIGNED NOT NULL COMMENT '角色 ID，关联 characters.id',
  MODIFY COLUMN `hp` TINYINT UNSIGNED NOT NULL DEFAULT 10 COMMENT 'HP 值/加成',
  MODIFY COLUMN `mp` TINYINT UNSIGNED NOT NULL DEFAULT 10 COMMENT 'MP 值/加成';

-- ---- hwidaccounts：账号-硬件指纹绑定表 ----
ALTER TABLE `hwidaccounts` COMMENT = '账号-硬件指纹绑定表';
ALTER TABLE `hwidaccounts`
  MODIFY COLUMN `accountid` INT(11) NOT NULL DEFAULT '0' COMMENT '账号 ID，关联 accounts.id',
  MODIFY COLUMN `hwid` VARCHAR(40) NOT NULL DEFAULT '' COMMENT '硬件指纹(HWID)',
  MODIFY COLUMN `relevance` TINYINT(2) NOT NULL DEFAULT '0' COMMENT '硬件指纹与账号的关联类型',
  MODIFY COLUMN `expiresat` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '绑定过期时间';

-- ---- hwidbans：硬件指纹封禁表 ----
ALTER TABLE `hwidbans` COMMENT = '硬件指纹封禁表';
ALTER TABLE `hwidbans`
  MODIFY COLUMN `hwidbanid` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '硬件封禁记录 ID',
  MODIFY COLUMN `hwid` VARCHAR(30) NOT NULL COMMENT '硬件指纹(HWID)';

-- ---- inventoryequipment：装备扩展属性表：强化次数、附加属性、装备等级、戒指与 095 潜能 ----
ALTER TABLE `inventoryequipment` COMMENT = '装备扩展属性表：强化次数、附加属性、装备等级、戒指与 095 潜能';
ALTER TABLE `inventoryequipment`
  MODIFY COLUMN `inventoryequipmentid` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '装备扩展记录自增 ID',
  MODIFY COLUMN `inventoryitemid` INT(10) UNSIGNED NOT NULL DEFAULT '0' COMMENT '物品实例 ID，关联 inventoryitems.inventoryitemid',
  MODIFY COLUMN `upgradeslots` INT(11) NOT NULL DEFAULT '0' COMMENT '剩余可升级(卷轴)次数',
  MODIFY COLUMN `level` INT(11) NOT NULL DEFAULT '0' COMMENT '等级',
  MODIFY COLUMN `str` INT(11) NOT NULL DEFAULT '0' COMMENT '力量加成',
  MODIFY COLUMN `dex` INT(11) NOT NULL DEFAULT '0' COMMENT '敏捷加成',
  MODIFY COLUMN `int` INT(11) NOT NULL DEFAULT '0' COMMENT '智力加成',
  MODIFY COLUMN `luk` INT(11) NOT NULL DEFAULT '0' COMMENT '运气加成',
  MODIFY COLUMN `hp` INT(11) NOT NULL DEFAULT '0' COMMENT 'HP 值/加成',
  MODIFY COLUMN `mp` INT(11) NOT NULL DEFAULT '0' COMMENT 'MP 值/加成',
  MODIFY COLUMN `watk` INT(11) NOT NULL DEFAULT '0' COMMENT '物理攻击力加成',
  MODIFY COLUMN `matk` INT(11) NOT NULL DEFAULT '0' COMMENT '魔法攻击力加成',
  MODIFY COLUMN `wdef` INT(11) NOT NULL DEFAULT '0' COMMENT '物理防御力加成',
  MODIFY COLUMN `mdef` INT(11) NOT NULL DEFAULT '0' COMMENT '魔法防御力加成',
  MODIFY COLUMN `acc` INT(11) NOT NULL DEFAULT '0' COMMENT '命中率加成',
  MODIFY COLUMN `avoid` INT(11) NOT NULL DEFAULT '0' COMMENT '回避率加成',
  MODIFY COLUMN `hands` INT(11) NOT NULL DEFAULT '0' COMMENT '握持手数',
  MODIFY COLUMN `speed` INT(11) NOT NULL DEFAULT '0' COMMENT '移动速度加成',
  MODIFY COLUMN `jump` INT(11) NOT NULL DEFAULT '0' COMMENT '跳跃力加成',
  MODIFY COLUMN `locked` INT(11) NOT NULL DEFAULT '0' COMMENT '是否锁定，0=未锁定',
  MODIFY COLUMN `vicious` INT(11) UNSIGNED NOT NULL DEFAULT '0' COMMENT '恶霸(诅咒)属性标记',
  MODIFY COLUMN `itemlevel` INT(11) NOT NULL DEFAULT '1' COMMENT '装备等级',
  MODIFY COLUMN `itemexp` INT(11) UNSIGNED NOT NULL DEFAULT '0' COMMENT '装备经验',
  MODIFY COLUMN `ringid` INT(11) NOT NULL DEFAULT '-1' COMMENT '戒指配对 ID，-1 表示无';

-- ---- inventoryitems：背包/仓库物品表：物品类型、归属、位置、数量与有效期 ----
ALTER TABLE `inventoryitems` COMMENT = '背包/仓库物品表：物品类型、归属、位置、数量与有效期';
ALTER TABLE `inventoryitems`
  MODIFY COLUMN `inventoryitemid` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '物品实例 ID，关联 inventoryitems.inventoryitemid',
  MODIFY COLUMN `type` TINYINT(3) UNSIGNED NOT NULL COMMENT '类型',
  MODIFY COLUMN `characterid` INT(11) DEFAULT NULL COMMENT '角色 ID，关联 characters.id',
  MODIFY COLUMN `accountid` INT(11) DEFAULT NULL COMMENT '账号 ID，关联 accounts.id',
  MODIFY COLUMN `itemid` INT(11) NOT NULL DEFAULT '0' COMMENT '物品 ID',
  MODIFY COLUMN `inventorytype` INT(11) NOT NULL DEFAULT '0' COMMENT '背包栏位类型(装备/消耗/设置/其他/仓库)',
  MODIFY COLUMN `position` INT(11) NOT NULL DEFAULT '0' COMMENT '位置/排序序号',
  MODIFY COLUMN `quantity` INT(11) NOT NULL DEFAULT '0' COMMENT '数量',
  MODIFY COLUMN `owner` TINYTEXT NOT NULL COMMENT '物品所有者名称',
  MODIFY COLUMN `petid` INT(11) NOT NULL DEFAULT '-1' COMMENT '宠物 ID',
  MODIFY COLUMN `flag` INT(11) NOT NULL COMMENT '标志位',
  MODIFY COLUMN `expiration` BIGINT(20) NOT NULL DEFAULT '-1' COMMENT '到期时间，NULL 表示永久',
  MODIFY COLUMN `giftFrom` VARCHAR(26) NOT NULL COMMENT '赠送者名称';

-- ---- inventorymerchant：雇佣商店物品表：角色上架物品与捆数 ----
ALTER TABLE `inventorymerchant` COMMENT = '雇佣商店物品表：角色上架物品与捆数';
ALTER TABLE `inventorymerchant`
  MODIFY COLUMN `inventorymerchantid` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '雇佣商店物品记录 ID',
  MODIFY COLUMN `inventoryitemid` INT(10) UNSIGNED NOT NULL DEFAULT '0' COMMENT '物品实例 ID，关联 inventoryitems.inventoryitemid',
  MODIFY COLUMN `characterid` INT(11) DEFAULT NULL COMMENT '角色 ID，关联 characters.id',
  MODIFY COLUMN `bundles` INT(10) NOT NULL DEFAULT '0' COMMENT '捆数(雇佣商店上架数量)';

-- ---- ipbans：IP 封禁表 ----
ALTER TABLE `ipbans` COMMENT = 'IP 封禁表';
ALTER TABLE `ipbans`
  MODIFY COLUMN `ipbanid` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'IP 封禁记录 ID',
  MODIFY COLUMN `ip` VARCHAR(40) NOT NULL DEFAULT '' COMMENT 'IP 地址',
  MODIFY COLUMN `aid` VARCHAR(40) DEFAULT NULL COMMENT '关联账号 ID';

-- ---- keymap：角色键位设置表：按键与对应动作 ----
ALTER TABLE `keymap` COMMENT = '角色键位设置表：按键与对应动作';
ALTER TABLE `keymap`
  MODIFY COLUMN `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键 ID',
  MODIFY COLUMN `characterid` INT(11) NOT NULL DEFAULT '0' COMMENT '角色 ID，关联 characters.id',
  MODIFY COLUMN `key` INT(11) NOT NULL DEFAULT '0' COMMENT '键位码(按键编号)',
  MODIFY COLUMN `type` INT(11) NOT NULL DEFAULT '0' COMMENT '类型',
  MODIFY COLUMN `action` INT(11) NOT NULL DEFAULT '0' COMMENT '该键绑定的动作 ID';

-- ---- lang_resources：数据库 i18n 文案表：多语言键值对 ----
ALTER TABLE `lang_resources` COMMENT = '数据库 i18n 文案表：多语言键值对';
ALTER TABLE `lang_resources`
  MODIFY COLUMN `id` bigint auto_increment COMMENT '自增id',
  MODIFY COLUMN `lang_type` varchar(32) COMMENT '语言类型，zh-CN，en-US',
  MODIFY COLUMN `lang_base` varchar(32) COMMENT '预留，当存在2个一样的code，不一样的value，需要用base来区分',
  MODIFY COLUMN `lang_code` varchar(128) not null COMMENT 'i18n编码',
  MODIFY COLUMN `lang_value` varchar(512) not null COMMENT 'i18n值',
  MODIFY COLUMN `lang_extend` varchar(512) COMMENT '预留扩展字段';

-- ---- macbans：MAC 地址封禁表 ----
ALTER TABLE `macbans` COMMENT = 'MAC 地址封禁表';
ALTER TABLE `macbans`
  MODIFY COLUMN `macbanid` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'MAC 封禁记录 ID',
  MODIFY COLUMN `mac` VARCHAR(30) NOT NULL COMMENT 'MAC 地址',
  MODIFY COLUMN `aid` VARCHAR(40) DEFAULT NULL COMMENT '关联账号 ID';

-- ---- macfilters：MAC 地址过滤表 ----
ALTER TABLE `macfilters` COMMENT = 'MAC 地址过滤表';
ALTER TABLE `macfilters`
  MODIFY COLUMN `macfilterid` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'MAC 过滤记录 ID',
  MODIFY COLUMN `filter` VARCHAR(30) NOT NULL COMMENT 'MAC 过滤表达式';

-- ---- makercreatedata：制作(锻造)成品配方表：成品、需求等级、制作等级、金币与材料 ----
ALTER TABLE `makercreatedata` COMMENT = '制作(锻造)成品配方表：成品、需求等级、制作等级、金币与材料';
ALTER TABLE `makercreatedata`
  MODIFY COLUMN `id` TINYINT(3) UNSIGNED NOT NULL COMMENT '自增主键 ID',
  MODIFY COLUMN `itemid` INT(11) NOT NULL COMMENT '物品 ID',
  MODIFY COLUMN `req_level` TINYINT(3) UNSIGNED NOT NULL COMMENT '需求角色等级',
  MODIFY COLUMN `req_maker_level` TINYINT(3) UNSIGNED NOT NULL COMMENT '需求制作等级',
  MODIFY COLUMN `req_meso` INT(11) NOT NULL COMMENT '需求金币',
  MODIFY COLUMN `req_item` INT(11) NOT NULL COMMENT '需求材料物品 ID',
  MODIFY COLUMN `req_equip` INT(11) NOT NULL COMMENT '需求装备 ID',
  MODIFY COLUMN `catalyst` INT(11) NOT NULL COMMENT '催化剂物品 ID',
  MODIFY COLUMN `quantity` SMALLINT(6) NOT NULL COMMENT '数量',
  MODIFY COLUMN `tuc` TINYINT(3) NOT NULL COMMENT '成品可升级次数';

-- ---- makerreagentdata：制作(锻造)试剂属性表 ----
ALTER TABLE `makerreagentdata` COMMENT = '制作(锻造)试剂属性表';
ALTER TABLE `makerreagentdata`
  MODIFY COLUMN `itemid` INT(11) NOT NULL COMMENT '物品 ID',
  MODIFY COLUMN `stat` VARCHAR(20) NOT NULL COMMENT '试剂影响的属性类型',
  MODIFY COLUMN `value` SMALLINT(6) NOT NULL COMMENT '值';

-- ---- makerrecipedata：制作(锻造)材料需求表 ----
ALTER TABLE `makerrecipedata` COMMENT = '制作(锻造)材料需求表';
ALTER TABLE `makerrecipedata`
  MODIFY COLUMN `itemid` INT(11) NOT NULL COMMENT '物品 ID',
  MODIFY COLUMN `req_item` INT(11) NOT NULL COMMENT '需求材料物品 ID',
  MODIFY COLUMN `count` SMALLINT(6) NOT NULL COMMENT '材料需求数量';

-- ---- makerrewarddata：制作(锻造)奖励产出表 ----
ALTER TABLE `makerrewarddata` COMMENT = '制作(锻造)奖励产出表';
ALTER TABLE `makerrewarddata`
  MODIFY COLUMN `itemid` INT(11) NOT NULL COMMENT '物品 ID',
  MODIFY COLUMN `rewardid` INT(11) NOT NULL COMMENT '奖励物品 ID',
  MODIFY COLUMN `quantity` SMALLINT(6) NOT NULL COMMENT '数量',
  MODIFY COLUMN `prob` TINYINT(3) UNSIGNED NOT NULL DEFAULT '100' COMMENT '奖励产出概率';

-- ---- marriages：婚姻登记表：夫妻双方角色 ----
ALTER TABLE `marriages` COMMENT = '婚姻登记表：夫妻双方角色';
ALTER TABLE `marriages`
  MODIFY COLUMN `marriageid` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '婚姻记录 ID',
  MODIFY COLUMN `husbandid` INT(10) UNSIGNED NOT NULL DEFAULT '0' COMMENT '男方角色 ID',
  MODIFY COLUMN `wifeid` INT(10) UNSIGNED NOT NULL DEFAULT '0' COMMENT '女方角色 ID';

-- ---- medalmaps：勋章任务地图记录表 ----
ALTER TABLE `medalmaps` COMMENT = '勋章任务地图记录表';
ALTER TABLE `medalmaps`
  MODIFY COLUMN `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键 ID',
  MODIFY COLUMN `characterid` INT(11) NOT NULL COMMENT '角色 ID，关联 characters.id',
  MODIFY COLUMN `queststatusid` INT(11) UNSIGNED NOT NULL COMMENT '任务状态记录 ID，关联 queststatus.queststatusid',
  MODIFY COLUMN `mapid` INT(11) NOT NULL COMMENT '地图 ID';

-- ---- modified_cash_item：商城物品改价/属性覆盖表 ----
ALTER TABLE `modified_cash_item` COMMENT = '商城物品改价/属性覆盖表';
ALTER TABLE `modified_cash_item`
  MODIFY COLUMN `sn` int(11) not null COMMENT 'sn码',
  MODIFY COLUMN `item_id` int(11) COMMENT '商城物品 ID',
  MODIFY COLUMN `count` int(11) COMMENT '数量',
  MODIFY COLUMN `price` int(11) COMMENT '价格',
  MODIFY COLUMN `bonus` int(11) COMMENT '属性奖励',
  MODIFY COLUMN `priority` int(11) COMMENT '优先级',
  MODIFY COLUMN `period` bigint(20) COMMENT '有效期(天/毫秒)',
  MODIFY COLUMN `maple_point` int(11) COMMENT '抵用券价格',
  MODIFY COLUMN `meso` int(11) COMMENT '金币价格',
  MODIFY COLUMN `for_premium_user` int(11) COMMENT '是否仅高级用户可购',
  MODIFY COLUMN `commodity_gender` int(11) COMMENT '适用性别',
  MODIFY COLUMN `on_sale` int(1) COMMENT '是否上架销售',
  MODIFY COLUMN `class` int(11) COMMENT '适用职业分类',
  MODIFY COLUMN `limit` int(11) COMMENT '限购数量',
  MODIFY COLUMN `pb_cash` int(11) COMMENT '点券支付标记',
  MODIFY COLUMN `pb_point` int(11) COMMENT '抵用券支付标记',
  MODIFY COLUMN `pb_gift` int(11) COMMENT '是否可赠送',
  MODIFY COLUMN `package_sn` int(11) COMMENT '礼包 SN';

-- ---- monsterbook：角色怪物图鉴收集表 ----
ALTER TABLE `monsterbook` COMMENT = '角色怪物图鉴收集表';
ALTER TABLE `monsterbook`
  MODIFY COLUMN `charid` INT(11) NOT NULL COMMENT '角色 ID，关联 characters.id',
  MODIFY COLUMN `cardid` INT(11) NOT NULL COMMENT '怪物卡片 ID',
  MODIFY COLUMN `level` INT(1) NOT NULL DEFAULT '1' COMMENT '等级';

-- ---- monstercarddata：怪物卡片数据表：卡片与怪物映射 ----
ALTER TABLE `monstercarddata` COMMENT = '怪物卡片数据表：卡片与怪物映射';
ALTER TABLE `monstercarddata`
  MODIFY COLUMN `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键 ID',
  MODIFY COLUMN `cardid` INT(11) NOT NULL DEFAULT '0' COMMENT '怪物卡片 ID',
  MODIFY COLUMN `mobid` INT(11) NOT NULL DEFAULT '0' COMMENT '怪物 ID';

-- ---- mts_cart：自由市场购物车表 ----
ALTER TABLE `mts_cart` COMMENT = '自由市场购物车表';
ALTER TABLE `mts_cart`
  MODIFY COLUMN `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键 ID',
  MODIFY COLUMN `cid` INT(11) NOT NULL COMMENT '角色 ID',
  MODIFY COLUMN `itemid` INT(11) NOT NULL COMMENT '物品 ID';

-- ---- mts_items：自由市场挂售物品表：含装备完整属性快照 ----
ALTER TABLE `mts_items` COMMENT = '自由市场挂售物品表：含装备完整属性快照';
ALTER TABLE `mts_items`
  MODIFY COLUMN `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键 ID',
  MODIFY COLUMN `tab` INT(11) NOT NULL DEFAULT '0' COMMENT '自由市场页签分类',
  MODIFY COLUMN `type` INT(11) NOT NULL DEFAULT '0' COMMENT '类型',
  MODIFY COLUMN `itemid` INT(10) UNSIGNED NOT NULL DEFAULT '0' COMMENT '物品 ID',
  MODIFY COLUMN `quantity` INT(11) NOT NULL DEFAULT '1' COMMENT '数量',
  MODIFY COLUMN `seller` INT(11) NOT NULL DEFAULT '0' COMMENT '卖家角色 ID',
  MODIFY COLUMN `price` INT(11) NOT NULL DEFAULT '0' COMMENT '价格',
  MODIFY COLUMN `bid_incre` INT(11) DEFAULT '0' COMMENT '竞拍加价幅度',
  MODIFY COLUMN `buy_now` INT(11) DEFAULT '0' COMMENT '一口价',
  MODIFY COLUMN `position` INT(11) DEFAULT '0' COMMENT '位置/排序序号',
  MODIFY COLUMN `upgradeslots` INT(11) DEFAULT '0' COMMENT '剩余可升级(卷轴)次数',
  MODIFY COLUMN `level` INT(11) DEFAULT '0' COMMENT '等级',
  MODIFY COLUMN `itemlevel` INT(11) NOT NULL DEFAULT '1' COMMENT '装备等级',
  MODIFY COLUMN `itemexp` INT(11) UNSIGNED NOT NULL DEFAULT '0' COMMENT '装备经验',
  MODIFY COLUMN `ringid` INT(11) NOT NULL DEFAULT '-1' COMMENT '戒指配对 ID，-1 表示无',
  MODIFY COLUMN `str` INT(11) DEFAULT '0' COMMENT '力量加成',
  MODIFY COLUMN `dex` INT(11) DEFAULT '0' COMMENT '敏捷加成',
  MODIFY COLUMN `int` INT(11) DEFAULT '0' COMMENT '智力加成',
  MODIFY COLUMN `luk` INT(11) DEFAULT '0' COMMENT '运气加成',
  MODIFY COLUMN `hp` INT(11) DEFAULT '0' COMMENT 'HP 值/加成',
  MODIFY COLUMN `mp` INT(11) DEFAULT '0' COMMENT 'MP 值/加成',
  MODIFY COLUMN `watk` INT(11) DEFAULT '0' COMMENT '物理攻击力加成',
  MODIFY COLUMN `matk` INT(11) DEFAULT '0' COMMENT '魔法攻击力加成',
  MODIFY COLUMN `wdef` INT(11) DEFAULT '0' COMMENT '物理防御力加成',
  MODIFY COLUMN `mdef` INT(11) DEFAULT '0' COMMENT '魔法防御力加成',
  MODIFY COLUMN `acc` INT(11) DEFAULT '0' COMMENT '命中率加成',
  MODIFY COLUMN `avoid` INT(11) DEFAULT '0' COMMENT '回避率加成',
  MODIFY COLUMN `hands` INT(11) DEFAULT '0' COMMENT '握持手数',
  MODIFY COLUMN `speed` INT(11) DEFAULT '0' COMMENT '移动速度加成',
  MODIFY COLUMN `jump` INT(11) DEFAULT '0' COMMENT '跳跃力加成',
  MODIFY COLUMN `locked` INT(11) DEFAULT '0' COMMENT '是否锁定，0=未锁定',
  MODIFY COLUMN `isequip` INT(1) DEFAULT '0' COMMENT '是否为装备(1=装备)',
  MODIFY COLUMN `owner` VARCHAR(16) DEFAULT '' COMMENT '物品所有者名称',
  MODIFY COLUMN `sellername` VARCHAR(16) NOT NULL COMMENT '卖家角色名',
  MODIFY COLUMN `sell_ends` VARCHAR(16) NOT NULL COMMENT '挂售结束时间',
  MODIFY COLUMN `transfer` INT(2) DEFAULT '0' COMMENT '转移/交付标记',
  MODIFY COLUMN `vicious` INT(2) UNSIGNED NOT NULL DEFAULT '0' COMMENT '恶霸(诅咒)属性标记',
  MODIFY COLUMN `flag` INT(2) UNSIGNED NOT NULL DEFAULT '0' COMMENT '标志位',
  MODIFY COLUMN `expiration` BIGINT(20) NOT NULL DEFAULT '-1' COMMENT '到期时间，NULL 表示永久',
  MODIFY COLUMN `giftFrom` VARCHAR(26) NOT NULL COMMENT '赠送者名称';

-- ---- namechanges：角色改名申请记录表 ----
ALTER TABLE `namechanges` COMMENT = '角色改名申请记录表';
ALTER TABLE `namechanges`
  MODIFY COLUMN `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键 ID',
  MODIFY COLUMN `characterid` INT(11) NOT NULL COMMENT '角色 ID，关联 characters.id',
  MODIFY COLUMN `old` VARCHAR(13) NOT NULL COMMENT '原角色名',
  MODIFY COLUMN `new` VARCHAR(13) NOT NULL COMMENT '新角色名',
  MODIFY COLUMN `requestTime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  MODIFY COLUMN `completionTime` TIMESTAMP NULL COMMENT '完成时间';

-- ---- newyear：新年贺卡/祝福留言表 ----
ALTER TABLE `newyear` COMMENT = '新年贺卡/祝福留言表';
ALTER TABLE `newyear`
  MODIFY COLUMN `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键 ID',
  MODIFY COLUMN `senderid` INT(10) NOT NULL DEFAULT '-1' COMMENT '发送者角色 ID',
  MODIFY COLUMN `sendername` VARCHAR(13) DEFAULT '' COMMENT '发送者角色名',
  MODIFY COLUMN `receiverid` INT(10) NOT NULL DEFAULT '-1' COMMENT '接收者角色 ID',
  MODIFY COLUMN `receivername` VARCHAR(13) DEFAULT '' COMMENT '接收者角色名',
  MODIFY COLUMN `message` VARCHAR(120) DEFAULT '' COMMENT '留言内容',
  MODIFY COLUMN `senderdiscard` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '发送者是否已删除',
  MODIFY COLUMN `receiverdiscard` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '接收者是否已删除',
  MODIFY COLUMN `received` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否已领取',
  MODIFY COLUMN `timesent` BIGINT(20) UNSIGNED NOT NULL COMMENT '发送时间',
  MODIFY COLUMN `timereceived` BIGINT(20) UNSIGNED NOT NULL COMMENT '领取时间';

-- ---- notes：留言/便笺表 ----
ALTER TABLE `notes` COMMENT = '留言/便笺表';
ALTER TABLE `notes`
  MODIFY COLUMN `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键 ID',
  MODIFY COLUMN `to` VARCHAR(13) NOT NULL DEFAULT '' COMMENT '收件人',
  MODIFY COLUMN `from` VARCHAR(13) NOT NULL DEFAULT '' COMMENT '发件人',
  MODIFY COLUMN `message` TEXT NOT NULL COMMENT '留言内容',
  MODIFY COLUMN `TIMESTAMP` BIGINT(20) UNSIGNED NOT NULL COMMENT '时间戳',
  MODIFY COLUMN `fame` INT(11) NOT NULL DEFAULT '0' COMMENT '随留言附带的人气变更',
  MODIFY COLUMN `deleted` INT(2) NOT NULL DEFAULT '0' COMMENT '是否删除';

-- ---- nxcode：点券兑换码表 ----
ALTER TABLE `nxcode` COMMENT = '点券兑换码表';
ALTER TABLE `nxcode`
  MODIFY COLUMN `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键 ID',
  MODIFY COLUMN `code` VARCHAR(17) NOT NULL COMMENT '兑换码字符串',
  MODIFY COLUMN `retriever` VARCHAR(13) DEFAULT NULL COMMENT '兑换人账号/角色 ID',
  MODIFY COLUMN `expiration` BIGINT(20) UNSIGNED NOT NULL DEFAULT '0' COMMENT '到期时间，NULL 表示永久';

-- ---- nxcode_items：兑换码所含物品表 ----
ALTER TABLE `nxcode_items` COMMENT = '兑换码所含物品表';
ALTER TABLE `nxcode_items`
  MODIFY COLUMN `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键 ID',
  MODIFY COLUMN `codeid` INT(11) NOT NULL COMMENT '兑换码 ID，关联 nxcode.id',
  MODIFY COLUMN `type` INT(11) NOT NULL DEFAULT '5' COMMENT '类型',
  MODIFY COLUMN `item` INT(11) NOT NULL DEFAULT '4000000' COMMENT '兑换所得物品 ID',
  MODIFY COLUMN `quantity` INT(11) NOT NULL DEFAULT '1' COMMENT '数量';

-- ---- nxcoupons：点券优惠券/倍率活动表 ----
ALTER TABLE `nxcoupons` COMMENT = '点券优惠券/倍率活动表';
ALTER TABLE `nxcoupons`
  MODIFY COLUMN `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键 ID',
  MODIFY COLUMN `couponid` INT(11) NOT NULL DEFAULT '0' COMMENT '优惠券 ID',
  MODIFY COLUMN `rate` INT(11) NOT NULL DEFAULT '0' COMMENT '倍率',
  MODIFY COLUMN `activeday` INT(11) NOT NULL DEFAULT '0' COMMENT '生效星期/日期',
  MODIFY COLUMN `starthour` INT(11) NOT NULL DEFAULT '0' COMMENT '生效起始小时',
  MODIFY COLUMN `endhour` INT(11) NOT NULL DEFAULT '0' COMMENT '生效结束小时';

-- ---- petignores：宠物忽略拾取物品表 ----
ALTER TABLE `petignores` COMMENT = '宠物忽略拾取物品表';
ALTER TABLE `petignores`
  MODIFY COLUMN `id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键 ID',
  MODIFY COLUMN `petid` INT(11) UNSIGNED NOT NULL COMMENT '宠物 ID',
  MODIFY COLUMN `itemid` INT(10) UNSIGNED NOT NULL COMMENT '物品 ID';

-- ---- pets：宠物表：名称、等级、亲密度、饱食度与召唤状态 ----
ALTER TABLE `pets` COMMENT = '宠物表：名称、等级、亲密度、饱食度与召唤状态';
ALTER TABLE `pets`
  MODIFY COLUMN `petid` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '宠物 ID',
  MODIFY COLUMN `name` VARCHAR(13) DEFAULT NULL COMMENT '名称',
  MODIFY COLUMN `level` INT(10) UNSIGNED NOT NULL COMMENT '等级',
  MODIFY COLUMN `closeness` INT(10) UNSIGNED NOT NULL COMMENT '亲密度',
  MODIFY COLUMN `fullness` INT(10) UNSIGNED NOT NULL COMMENT '饱食度',
  MODIFY COLUMN `summoned` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否处于召唤状态',
  MODIFY COLUMN `flag` INT(10) UNSIGNED NOT NULL DEFAULT '0' COMMENT '标志位';

-- ---- playerdiseases：角色异常状态表：疾病/怪物技能 debuff 与剩余时长 ----
ALTER TABLE `playerdiseases` COMMENT = '角色异常状态表：疾病/怪物技能 debuff 与剩余时长';
ALTER TABLE `playerdiseases`
  MODIFY COLUMN `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键 ID',
  MODIFY COLUMN `charid` INT(11) NOT NULL COMMENT '角色 ID，关联 characters.id',
  MODIFY COLUMN `disease` INT(11) NOT NULL COMMENT '异常状态类型',
  MODIFY COLUMN `mobskillid` INT(11) NOT NULL COMMENT '来源怪物技能 ID',
  MODIFY COLUMN `mobskilllv` INT(11) NOT NULL COMMENT '来源怪物技能等级',
  MODIFY COLUMN `length` INT(11) NOT NULL DEFAULT '1' COMMENT '持续时长';

-- ---- playernpcs：玩家 NPC 立牌表：外观、坐标、地图与排名 ----
ALTER TABLE `playernpcs` COMMENT = '玩家 NPC 立牌表：外观、坐标、地图与排名';
ALTER TABLE `playernpcs`
  MODIFY COLUMN `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键 ID',
  MODIFY COLUMN `name` VARCHAR(13) NOT NULL COMMENT '名称',
  MODIFY COLUMN `hair` INT(11) NOT NULL COMMENT '发型 ID',
  MODIFY COLUMN `face` INT(11) NOT NULL COMMENT '脸型 ID',
  MODIFY COLUMN `skin` INT(11) NOT NULL COMMENT '肤色 ID',
  MODIFY COLUMN `gender` INT(11) NOT NULL DEFAULT '0' COMMENT '性别',
  MODIFY COLUMN `x` INT(11) NOT NULL COMMENT 'X 坐标',
  MODIFY COLUMN `cy` INT(11) NOT NULL DEFAULT '0' COMMENT 'Y 坐标(平台高度)',
  MODIFY COLUMN `world` INT(11) NOT NULL DEFAULT '0' COMMENT '大区(世界) ID',
  MODIFY COLUMN `map` INT(11) NOT NULL DEFAULT '0' COMMENT '地图 ID',
  MODIFY COLUMN `dir` INT(11) NOT NULL DEFAULT '0' COMMENT '朝向',
  MODIFY COLUMN `scriptid` INT(10) UNSIGNED NOT NULL DEFAULT '0' COMMENT '关联 NPC 脚本 ID',
  MODIFY COLUMN `fh` INT(11) NOT NULL DEFAULT '0' COMMENT '平台(Foothold) ID',
  MODIFY COLUMN `rx0` INT(11) NOT NULL DEFAULT '0' COMMENT '移动范围左边界',
  MODIFY COLUMN `rx1` INT(11) NOT NULL DEFAULT '0' COMMENT '移动范围右边界',
  MODIFY COLUMN `worldrank` INT(11) NOT NULL DEFAULT '0' COMMENT '本大区排名',
  MODIFY COLUMN `overallrank` INT(11) NOT NULL DEFAULT '0' COMMENT '全服排名',
  MODIFY COLUMN `worldjobrank` INT(11) NOT NULL DEFAULT '0' COMMENT '本大区职业排名',
  MODIFY COLUMN `job` INT(11) NOT NULL DEFAULT '0' COMMENT '职业 ID';

-- ---- playernpcs_equip：玩家 NPC 立牌装备表 ----
ALTER TABLE `playernpcs_equip` COMMENT = '玩家 NPC 立牌装备表';
ALTER TABLE `playernpcs_equip`
  MODIFY COLUMN `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键 ID',
  MODIFY COLUMN `npcid` INT(11) NOT NULL DEFAULT '0' COMMENT 'NPC ID',
  MODIFY COLUMN `equipid` INT(11) NOT NULL COMMENT '装备物品 ID',
  MODIFY COLUMN `type` INT(11) NOT NULL DEFAULT '0' COMMENT '类型',
  MODIFY COLUMN `equippos` INT(11) NOT NULL COMMENT '装备槽位';

-- ---- playernpcs_field：玩家 NPC 立牌展台/领奖台位置表 ----
ALTER TABLE `playernpcs_field` COMMENT = '玩家 NPC 立牌展台/领奖台位置表';
ALTER TABLE `playernpcs_field`
  MODIFY COLUMN `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键 ID',
  MODIFY COLUMN `world` INT(11) NOT NULL COMMENT '大区(世界) ID',
  MODIFY COLUMN `map` INT(11) NOT NULL COMMENT '地图 ID',
  MODIFY COLUMN `step` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '展台阶位',
  MODIFY COLUMN `podium` SMALLINT(8) NOT NULL DEFAULT '0' COMMENT '领奖台编号';

-- ---- plife：地图自定义生命体(装饰/怪物)摆放表 ----
ALTER TABLE `plife` COMMENT = '地图自定义生命体(装饰/怪物)摆放表';
ALTER TABLE `plife`
  MODIFY COLUMN `id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键 ID',
  MODIFY COLUMN `world` INT(11) NOT NULL DEFAULT '-1' COMMENT '大区(世界) ID',
  MODIFY COLUMN `map` INT(11) NOT NULL DEFAULT '0' COMMENT '地图 ID',
  MODIFY COLUMN `life` INT(11) NOT NULL DEFAULT '0' COMMENT '生命体 ID(怪物/装饰物)',
  MODIFY COLUMN `type` VARCHAR(1) NOT NULL DEFAULT 'n' COMMENT '类型',
  MODIFY COLUMN `cy` INT(11) NOT NULL DEFAULT '0' COMMENT 'Y 坐标(平台高度)',
  MODIFY COLUMN `f` INT(11) NOT NULL DEFAULT '0' COMMENT '是否面向左(flip)',
  MODIFY COLUMN `fh` INT(11) NOT NULL DEFAULT '0' COMMENT '平台(Foothold) ID',
  MODIFY COLUMN `rx0` INT(11) NOT NULL DEFAULT '0' COMMENT '移动范围左边界',
  MODIFY COLUMN `rx1` INT(11) NOT NULL DEFAULT '0' COMMENT '移动范围右边界',
  MODIFY COLUMN `x` INT(11) NOT NULL DEFAULT '0' COMMENT 'X 坐标',
  MODIFY COLUMN `y` INT(11) NOT NULL DEFAULT '0' COMMENT 'Y 坐标',
  MODIFY COLUMN `hide` INT(11) NOT NULL DEFAULT '0' COMMENT '是否隐藏',
  MODIFY COLUMN `mobtime` INT(11) NOT NULL DEFAULT '0' COMMENT '怪物重生时间',
  MODIFY COLUMN `team` INT(11) NOT NULL DEFAULT '0' COMMENT '所属队伍/阵营';

-- ---- questactions：任务动作记录表 ----
ALTER TABLE `questactions` COMMENT = '任务动作记录表';
ALTER TABLE `questactions`
  MODIFY COLUMN `questactionid` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '任务动作记录 ID',
  MODIFY COLUMN `questid` INT(11) NOT NULL DEFAULT '0' COMMENT '任务 ID，0 表示无任务条件',
  MODIFY COLUMN `status` INT(11) NOT NULL DEFAULT '0' COMMENT '状态',
  MODIFY COLUMN `data` BLOB NOT NULL COMMENT '序列化数据';

-- ---- questprogress：任务进度表：任务内各进度项的字符串值 ----
ALTER TABLE `questprogress` COMMENT = '任务进度表：任务内各进度项的字符串值';
ALTER TABLE `questprogress`
  MODIFY COLUMN `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键 ID',
  MODIFY COLUMN `characterid` INT(11) NOT NULL COMMENT '角色 ID，关联 characters.id',
  MODIFY COLUMN `queststatusid` INT(10) UNSIGNED NOT NULL DEFAULT '0' COMMENT '任务状态记录 ID，关联 queststatus.queststatusid',
  MODIFY COLUMN `progressid` INT(11) NOT NULL DEFAULT '0' COMMENT '进度项编号',
  MODIFY COLUMN `progress` VARCHAR(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '进度值(字符串)';

-- ---- questrequirements：任务需求记录表 ----
ALTER TABLE `questrequirements` COMMENT = '任务需求记录表';
ALTER TABLE `questrequirements`
  MODIFY COLUMN `questrequirementid` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '任务需求记录 ID',
  MODIFY COLUMN `questid` INT(11) NOT NULL DEFAULT '0' COMMENT '任务 ID，0 表示无任务条件',
  MODIFY COLUMN `status` INT(11) NOT NULL DEFAULT '0' COMMENT '状态',
  MODIFY COLUMN `data` BLOB NOT NULL COMMENT '序列化数据';

-- ---- queststatus：角色任务状态表：进行中/完成/过期/放弃 ----
ALTER TABLE `queststatus` COMMENT = '角色任务状态表：进行中/完成/过期/放弃';
ALTER TABLE `queststatus`
  MODIFY COLUMN `queststatusid` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '任务状态记录 ID，关联 queststatus.queststatusid',
  MODIFY COLUMN `characterid` INT(11) NOT NULL DEFAULT '0' COMMENT '角色 ID，关联 characters.id',
  MODIFY COLUMN `quest` INT(11) NOT NULL DEFAULT '0' COMMENT '任务 ID',
  MODIFY COLUMN `status` INT(11) NOT NULL DEFAULT '0' COMMENT '状态',
  MODIFY COLUMN `time` INT(11) NOT NULL DEFAULT '0' COMMENT '任务状态记录时间',
  MODIFY COLUMN `expires` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '过期时间',
  MODIFY COLUMN `forfeited` INT(11) NOT NULL DEFAULT '0' COMMENT '是否已放弃',
  MODIFY COLUMN `completed` INT(11) NOT NULL DEFAULT '0' COMMENT '完成时间',
  MODIFY COLUMN `info` TINYINT(3) NOT NULL DEFAULT '0' COMMENT '附加信息';

-- ---- quickslotkeymapped：账号快捷栏键位映射表 ----
ALTER TABLE `quickslotkeymapped` COMMENT = '账号快捷栏键位映射表';
ALTER TABLE `quickslotkeymapped`
  MODIFY COLUMN `accountid` INT NOT NULL COMMENT '账号 ID，关联 accounts.id',
  MODIFY COLUMN `keymap` BIGINT NOT NULL DEFAULT 0 COMMENT '快捷栏键位序列化数据';

-- ---- reactordrops：反应器掉落表 ----
ALTER TABLE `reactordrops` COMMENT = '反应器掉落表';
ALTER TABLE `reactordrops`
  MODIFY COLUMN `reactordropid` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '反应器掉落记录 ID',
  MODIFY COLUMN `reactorid` INT(11) NOT NULL COMMENT '反应器 ID',
  MODIFY COLUMN `itemid` INT(11) NOT NULL COMMENT '物品 ID',
  MODIFY COLUMN `chance` INT(11) NOT NULL COMMENT '掉落概率(百万分比)',
  MODIFY COLUMN `questid` INT(5) NOT NULL DEFAULT '-1' COMMENT '任务 ID，0 表示无任务条件';

-- ---- reports：玩家举报表：举报人/被举报人、原因、聊天记录与处理结果 ----
ALTER TABLE `reports` COMMENT = '玩家举报表：举报人/被举报人、原因、聊天记录与处理结果';
ALTER TABLE `reports`
  MODIFY COLUMN `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键 ID',
  MODIFY COLUMN `reporttime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '举报时间',
  MODIFY COLUMN `reporterid` INT(11) NOT NULL COMMENT '举报人角色 ID',
  MODIFY COLUMN `victimid` INT(11) NOT NULL COMMENT '被举报人角色 ID',
  MODIFY COLUMN `reason` TINYINT(4) NOT NULL COMMENT '举报原因',
  MODIFY COLUMN `chatlog` TEXT NOT NULL COMMENT '举报附带的聊天记录',
  MODIFY COLUMN `description` TEXT NOT NULL COMMENT '描述';

-- ---- responses：自动回复/聊天响应表 ----
ALTER TABLE `responses` COMMENT = '自动回复/聊天响应表';
ALTER TABLE `responses`
  MODIFY COLUMN `chat` TEXT COMMENT '触发的关键字/聊天内容',
  MODIFY COLUMN `response` TEXT COMMENT '自动回复内容',
  MODIFY COLUMN `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键 ID';

-- ---- rings：戒指(情侣/好友)配对表 ----
ALTER TABLE `rings` COMMENT = '戒指(情侣/好友)配对表';
ALTER TABLE `rings`
  MODIFY COLUMN `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键 ID',
  MODIFY COLUMN `partnerRingId` INT(11) NOT NULL DEFAULT '0' COMMENT '配对戒指 ID',
  MODIFY COLUMN `partnerChrId` INT(11) NOT NULL DEFAULT '0' COMMENT '配对角色 ID',
  MODIFY COLUMN `itemid` INT(11) NOT NULL DEFAULT '0' COMMENT '物品 ID',
  MODIFY COLUMN `partnername` VARCHAR(255) NOT NULL COMMENT '配对角色名';

-- ---- savedlocations：角色保存地点表：传送石/记录点 ----
ALTER TABLE `savedlocations` COMMENT = '角色保存地点表：传送石/记录点';
ALTER TABLE `savedlocations`
  MODIFY COLUMN `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键 ID',
  MODIFY COLUMN `characterid` INT(11) NOT NULL COMMENT '角色 ID，关联 characters.id',
  MODIFY COLUMN `locationtype` ENUM ( 'FREE_MARKET', 'WORLDTOUR', 'FLORINA', 'INTRO', 'SUNDAY_MARKET', 'MIRROR', 'EVENT', 'BOSSPQ', 'HAPPYVILLE', 'DEVELOPER', 'MONSTER_CARNIVAL', 'JAIL') NOT NULL COMMENT '地点类型(如传送石/记录点)',
  MODIFY COLUMN `map` INT(11) NOT NULL COMMENT '地图 ID',
  MODIFY COLUMN `portal` INT(11) NOT NULL COMMENT '传送点(portal) ID';

-- ---- server_prop：服务器属性表：全局键值参数 ----
ALTER TABLE `server_prop` COMMENT = '服务器属性表：全局键值参数';
ALTER TABLE `server_prop`
  MODIFY COLUMN `id` bigint auto_increment COMMENT '配置id',
  MODIFY COLUMN `prop_type` int COMMENT '配置分类',
  MODIFY COLUMN `prop_code` varchar(32) COMMENT '配置编码',
  MODIFY COLUMN `prop_class` varchar(32) COMMENT '配置值数据类型',
  MODIFY COLUMN `prop_value` varchar(255) COMMENT '配置值',
  MODIFY COLUMN `prop_desc` varchar(500) COMMENT '配置描述';

-- ---- server_queue：服务器队列/待处理请求表 ----
ALTER TABLE `server_queue` COMMENT = '服务器队列/待处理请求表';
ALTER TABLE `server_queue`
  MODIFY COLUMN `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键 ID',
  MODIFY COLUMN `accountid` INT(11) NOT NULL DEFAULT '0' COMMENT '账号 ID，关联 accounts.id',
  MODIFY COLUMN `characterid` INT(11) NOT NULL DEFAULT '0' COMMENT '角色 ID，关联 characters.id',
  MODIFY COLUMN `type` TINYINT(2) NOT NULL DEFAULT '0' COMMENT '类型',
  MODIFY COLUMN `value` INT(10) NOT NULL DEFAULT '0' COMMENT '值',
  MODIFY COLUMN `message` VARCHAR(128) NOT NULL COMMENT '留言内容',
  MODIFY COLUMN `createTime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '入队时间';

-- ---- shopitems：NPC 商店商品表 ----
ALTER TABLE `shopitems` COMMENT = 'NPC 商店商品表';
ALTER TABLE `shopitems`
  MODIFY COLUMN `shopitemid` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '商店商品记录 ID',
  MODIFY COLUMN `shopid` INT(10) UNSIGNED NOT NULL COMMENT '商店 ID',
  MODIFY COLUMN `itemid` INT(11) NOT NULL COMMENT '物品 ID',
  MODIFY COLUMN `price` INT(11) NOT NULL COMMENT '价格',
  MODIFY COLUMN `pitch` INT(11) NOT NULL DEFAULT '0' COMMENT '商品音调/展示参数',
  MODIFY COLUMN `position` INT(11) NOT NULL COMMENT 'sort is an arbitrary field designed to give leeway when modifying shops. The lowest number is 104 and it increments by 4 for each item to allow decent space for swapping/inserting/removing items.';

-- ---- shops：NPC 商店表：商店与 NPC 绑定 ----
ALTER TABLE `shops` COMMENT = 'NPC 商店表：商店与 NPC 绑定';
ALTER TABLE `shops`
  MODIFY COLUMN `shopid` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '商店 ID',
  MODIFY COLUMN `npcid` INT(11) NOT NULL DEFAULT '0' COMMENT 'NPC ID';

-- ---- skillmacros：技能宏表：角色技能宏与喊话 ----
ALTER TABLE `skillmacros` COMMENT = '技能宏表：角色技能宏与喊话';
ALTER TABLE `skillmacros`
  MODIFY COLUMN `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键 ID',
  MODIFY COLUMN `characterid` INT(11) NOT NULL DEFAULT '0' COMMENT '角色 ID，关联 characters.id',
  MODIFY COLUMN `position` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '位置/排序序号',
  MODIFY COLUMN `skill1` INT(11) NOT NULL DEFAULT '0' COMMENT '宏内第 1 个技能 ID',
  MODIFY COLUMN `skill2` INT(11) NOT NULL DEFAULT '0' COMMENT '宏内第 2 个技能 ID',
  MODIFY COLUMN `skill3` INT(11) NOT NULL DEFAULT '0' COMMENT '宏内第 3 个技能 ID',
  MODIFY COLUMN `name` VARCHAR(13) DEFAULT NULL COMMENT '名称',
  MODIFY COLUMN `shout` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '宏喊话内容';

-- ---- skills：角色技能表：技能等级、熟练度上限与到期时间 ----
ALTER TABLE `skills` COMMENT = '角色技能表：技能等级、熟练度上限与到期时间';
ALTER TABLE `skills`
  MODIFY COLUMN `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键 ID',
  MODIFY COLUMN `skillid` INT(11) NOT NULL DEFAULT '0' COMMENT '技能 ID',
  MODIFY COLUMN `characterid` INT(11) NOT NULL DEFAULT '0' COMMENT '角色 ID，关联 characters.id',
  MODIFY COLUMN `skilllevel` INT(11) NOT NULL DEFAULT '0' COMMENT '当前技能等级',
  MODIFY COLUMN `masterlevel` INT(11) NOT NULL DEFAULT '0' COMMENT '技能熟练度上限',
  MODIFY COLUMN `expiration` BIGINT(20) NOT NULL DEFAULT '-1' COMMENT '到期时间，NULL 表示永久';

-- ---- specialcashitems：特殊现金道具标记表 ----
ALTER TABLE `specialcashitems` COMMENT = '特殊现金道具标记表';
ALTER TABLE `specialcashitems`
  MODIFY COLUMN `id` INT(11) NOT NULL COMMENT '自增主键 ID',
  MODIFY COLUMN `sn` INT(11) NOT NULL COMMENT '物品唯一序列号(SN)',
  MODIFY COLUMN `modifier` INT(11) NOT NULL COMMENT '现金道具修改标记(1024=新增/删除)',
  MODIFY COLUMN `info` INT(1) NOT NULL COMMENT '附加信息';

-- ---- storages：账号仓库表：按大区的格子数与金币 ----
ALTER TABLE `storages` COMMENT = '账号仓库表：按大区的格子数与金币';
ALTER TABLE `storages`
  MODIFY COLUMN `storageid` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '仓库记录 ID',
  MODIFY COLUMN `accountid` INT(11) NOT NULL DEFAULT '0' COMMENT '账号 ID，关联 accounts.id',
  MODIFY COLUMN `world` INT(2) NOT NULL COMMENT '大区(世界) ID',
  MODIFY COLUMN `slots` INT(11) NOT NULL DEFAULT '0' COMMENT '仓库格数',
  MODIFY COLUMN `meso` INT(11) NOT NULL DEFAULT '0' COMMENT '金币(冒险币)数量';

-- ---- temp_data：掉落数据迁移临时表（V1.0.51 数据搬迁用） ----
ALTER TABLE `temp_data` COMMENT = '掉落数据迁移临时表（V1.0.51 数据搬迁用）';
ALTER TABLE `temp_data`
  MODIFY COLUMN `dropperid` INT(11) NOT NULL COMMENT '掉落源 ID(怪物/反应器)',
  MODIFY COLUMN `itemid` INT(11) NOT NULL DEFAULT '0' COMMENT '物品 ID',
  MODIFY COLUMN `minimum_quantity` INT(11) NOT NULL DEFAULT '1' COMMENT '最小掉落/产出数量',
  MODIFY COLUMN `maximum_quantity` INT(11) NOT NULL DEFAULT '1' COMMENT '最大掉落/产出数量',
  MODIFY COLUMN `questid` INT(11) NOT NULL DEFAULT '0' COMMENT '任务 ID，0 表示无任务条件',
  MODIFY COLUMN `chance` INT(11) NOT NULL DEFAULT '0' COMMENT '掉落概率(百万分比)';

-- ---- trocklocations：角色传送石记录表 ----
ALTER TABLE `trocklocations` COMMENT = '角色传送石记录表';
ALTER TABLE `trocklocations`
  MODIFY COLUMN `trockid` INT(11) NOT NULL AUTO_INCREMENT COMMENT '传送石记录 ID',
  MODIFY COLUMN `characterid` INT(11) NOT NULL COMMENT '角色 ID，关联 characters.id',
  MODIFY COLUMN `mapid` INT(11) NOT NULL COMMENT '地图 ID',
  MODIFY COLUMN `vip` INT(2) NOT NULL COMMENT '是否 VIP 传送石';

-- ---- wishlists：角色心愿(收藏)清单表 ----
ALTER TABLE `wishlists` COMMENT = '角色心愿(收藏)清单表';
ALTER TABLE `wishlists`
  MODIFY COLUMN `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键 ID',
  MODIFY COLUMN `charid` INT(11) NOT NULL COMMENT '角色 ID，关联 characters.id',
  MODIFY COLUMN `sn` INT(11) NOT NULL COMMENT '物品唯一序列号(SN)';

-- ---- world_prop：大区属性表：频道数、经验/金币/掉落/任务倍率与公告 ----
ALTER TABLE `world_prop` COMMENT = '大区属性表：频道数、经验/金币/掉落/任务倍率与公告';
ALTER TABLE `world_prop`
  MODIFY COLUMN `id` bigint not null COMMENT '大区id',
  MODIFY COLUMN `flag` tinyint default 0 COMMENT '大区状态标志位',
  MODIFY COLUMN `server_message` varchar(255) COMMENT '顶部滚动信息',
  MODIFY COLUMN `event_message` varchar(255) COMMENT '大区描述',
  MODIFY COLUMN `recommend_message` varchar(255) COMMENT '大区推荐信息',
  MODIFY COLUMN `channel_size` int COMMENT '频道数',
  MODIFY COLUMN `exp_rate` decimal(40, 3) COMMENT '经验倍率',
  MODIFY COLUMN `meso_rate` decimal(40, 3) COMMENT '金币倍率',
  MODIFY COLUMN `drop_rate` decimal(40, 3) COMMENT '掉落倍率',
  MODIFY COLUMN `boss_drop_rate` decimal(40, 3) COMMENT 'BOSS掉落倍率',
  MODIFY COLUMN `quest_rate` decimal(40, 3) COMMENT '任务倍率',
  MODIFY COLUMN `fishing_rate` decimal(40, 3) COMMENT '钓鱼倍率',
  MODIFY COLUMN `travel_rate` decimal(40, 3) COMMENT '旅行倍率',
  MODIFY COLUMN `level_exp_rate` decimal(40, 3) COMMENT '等级经验倍率，0为不启用',
  MODIFY COLUMN `quick_level` decimal(40, 3) COMMENT '冲刺等级，0为不启用',
  MODIFY COLUMN `quick_level_exp_rate` decimal(40, 3) COMMENT '冲刺等级经验倍率',
  MODIFY COLUMN `enabled` tinyint default 0 COMMENT '大区是否启用，0-不启用，1-启用';

-- ---- worldtransfers：角色转区申请记录表 ----
ALTER TABLE `worldtransfers` COMMENT = '角色转区申请记录表';
ALTER TABLE `worldtransfers`
  MODIFY COLUMN `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键 ID',
  MODIFY COLUMN `characterid` INT(11) NOT NULL COMMENT '角色 ID，关联 characters.id',
  MODIFY COLUMN `from` TINYINT(3) NOT NULL COMMENT '原大区 ID',
  MODIFY COLUMN `to` TINYINT(3) NOT NULL COMMENT '目标大区 ID',
  MODIFY COLUMN `requestTime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  MODIFY COLUMN `completionTime` TIMESTAMP NULL COMMENT '完成时间';

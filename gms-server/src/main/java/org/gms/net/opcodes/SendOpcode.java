/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as
 published by the Free Software Foundation version 3 as published by
 the Free Software Foundation. You may not use, modify or distribute
 this program under any other version of the GNU Affero General Public
 License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.gms.net.opcodes;

/**
 * 发送操作码
 */
public enum SendOpcode {
    /**
     * 登录状态
     */
    LOGIN_STATUS(0x00),
    /**
     * 游客ID登录
     */
    GUEST_ID_LOGIN(0x01),
    /**
     * 账户信息
     */
    ACCOUNT_INFO(0x02),
    /**
     * 服务器状态（CHECK_USER_LIMIT_RESULT）
     */
    SERVER_STATUS(0x03),
    /**
     * 性别设置结果（SET_ACCOUNT_RESULT）
     */
    GENDER_DONE(0x04),
    /**
     * EULA确认结果
     */
    CONFIRM_EULA_RESULT(0x05),
    /**
     * 检查PIN码
     */
    CHECK_PIN_CODE(0x06),
    /**
     * 更新PIN码
     */
    UPDATE_PIN_CODE(0x07),
    /**
     * 查看所有角色
     */
    VIEW_ALL_CHAR(0x08),
    /**
     * 通过VAC选择角色
     */
    SELECT_CHARACTER_BY_VAC(0x09),
    /**
     * 服务器列表
     */
    SERVER_LIST(0x0A),
    /**
     * 角色列表
     */
    CHAR_LIST(0x0B),
    /**
     * 服务器IP
     */
    SERVER_IP(0x0C),
    /**
     * 角色名称响应
     */
    CHAR_NAME_RESPONSE(0x0D),
    /**
     * 添加新角色条目
     */
    ADD_NEW_CHAR_ENTRY(0x0E),
    /**
     * 删除角色响应
     */
    DELETE_CHAR_RESPONSE(0x0F),
    /**
     * 更改频道
     */
    CHANGE_CHANNEL(0x10),
    /**
     * 心跳检测
     */
    PING(0x11),
    /**
     * 韩国互联网咖啡无关紧要的内容，忽略
     */
    KOREAN_INTERNET_CAFE_SHIT(0x12),
    /**
     * 频道已选择
     */
    CHANNEL_SELECTED(0x14),
    /**
     * 可能是RELOG_RESPONSE，无所谓
     */
    HACKSHIELD_REQUEST(0x15),
    /**
     * 重新登录响应
     */
    RELOG_RESPONSE(0x16),
    /**
     * CRC检查结果
     */
    CHECK_CRC_RESULT(0x19),
    /**
     * 上次连接的世界
     */
    LAST_CONNECTED_WORLD(0x1A),
    /**
     * 推荐世界消息
     */
    RECOMMENDED_WORLD_MESSAGE(0x1B),
    /**
     * SPW检查结果
     */
    CHECK_SPW_RESULT(0x1C),
    /**
     * CWvsContext::OnPacket
     * 物品栏操作
     */
    INVENTORY_OPERATION(0x1D),
    /**
     * 扩展物品栏
     */
    INVENTORY_GROW(0x1E),
    /**
     * 状态改变
     */
    STAT_CHANGED(0x1F),
    /**
     * 施加增益效果
     */
    GIVE_BUFF(0x20),
    /**
     * 移除增益效果
     */
    CANCEL_BUFF(0x21),
    /**
     * 强制设置状态
     */
    FORCED_STAT_SET(0x22),
    /**
     * 强制重置状态
     */
    FORCED_STAT_RESET(0x23),
    /**
     * 更新技能
     */
    UPDATE_SKILLS(0x24),
    /**
     * 技能使用结果
     */
    SKILL_USE_RESULT(0x25),
    /**
     * 声望响应
     */
    FAME_RESPONSE(0x26),
    /**
     * 显示状态信息
     */
    SHOW_STATUS_INFO(0x27),
    /**
     * 打开完整客户端下载链接
     */
    OPEN_FULL_CLIENT_DOWNLOAD_LINK(0x28),
    /**
     * 备忘录结果
     */
    MEMO_RESULT(0x29),
    /**
     * 地图转移结果
     */
    MAP_TRANSFER_RESULT(0x2A),
    /**
     * 结婚照片（ANTI_MACRO_RESULT在某些版本可能是这个）
     */
    WEDDING_PHOTO(0x2B),
    /**
     * 领取结果
     */
    CLAIM_RESULT(0x2D),
    /**
     * 领取可用时间
     */
    CLAIM_AVAILABLE_TIME(0x2E),
    /**
     * 领取状态改变
     */
    CLAIM_STATUS_CHANGED(0x2F),
    /**
     * 设置驯服怪物信息
     */
    SET_TAMING_MOB_INFO(0x30),
    /**
     * 任务完成
     */
    QUEST_CLEAR(0x31),
    /**
     * 委托商店检查结果
     */
    ENTRUSTED_SHOP_CHECK_RESULT(0x32),
    /**
     * 学习技能物品结果
     */
    SKILL_LEARN_ITEM_RESULT(0x33),
    /**
     * 收集物品结果
     */
    GATHER_ITEM_RESULT(0x34),
    /**
     * 整理物品结果
     */
    SORT_ITEM_RESULT(0x35),
    /**
     * 控诉角色结果
     */
    SUE_CHARACTER_RESULT(0x37),
    /**
     * 交易金钱限制
     */
    TRADE_MONEY_LIMIT(0x39),
    /**
     * 设置性别
     */
    SET_GENDER(0x3A),
    /**
     * 公会公告板数据包
     */
    GUILD_BBS_PACKET(0x3B),
    /**
     * 角色信息
     */
    CHAR_INFO(0x3D),
    /**
     * 组队操作
     */
    PARTY_OPERATION(0x3E),
    /**
     * 好友列表
     */
    BUDDY_LIST(0x3F),
    /**
     * 公会操作
     */
    GUILD_OPERATION(0x41),
    /**
     * 联盟操作
     */
    ALLIANCE_OPERATION(0x42),
    /**
     * 生成传送门
     */
    SPAWN_PORTAL(0x43),
    /**
     * 服务器消息
     */
    SERVER_MESSAGE(0x44),
    /**
     * 孵化器结果
     */
    INCUBATOR_RESULT(0x45),
    /**
     * 商店扫描结果
     */
    SHOP_SCANNER_RESULT(0x46),
    /**
     * 商店链接结果
     */
    SHOP_LINK_RESULT(0x47),
    /**
     * 结婚请求
     */

    MARRIAGE_REQUEST(0x48),
    /**
     * 结婚结果
     */
    MARRIAGE_RESULT(0x49),
    /**
     * 结婚礼物结果
     */
    WEDDING_GIFT_RESULT(0x4A),
    /**
     * 通知结婚伴侣地图转移
     */
    NOTIFY_MARRIED_PARTNER_MAP_TRANSFER(0x4B),
    /**
     * 宠物食物结果
     */

    CASH_PET_FOOD_RESULT(0x4C),
    /**
     * 设置周活动消息
     */
    SET_WEEK_EVENT_MESSAGE(0x4D),
    /**
     * 设置药水折扣率
     */
    SET_POTION_DISCOUNT_RATE(0x4E),
    /**
     * 鞍具捕捉怪物失败
     */

    BRIDLE_MOB_CATCH_FAIL(0x4F),
    /**
     * 仿冒NPC结果
     */
    IMITATED_NPC_RESULT(0x50),
    /**
     * 仿冒NPC数据
     */
    IMITATED_NPC_DATA(0x51),
    /**
     * 限时NPC禁用信息
     */
    LIMITED_NPC_DISABLE_INFO(0x52),
    /**
     * 怪物图鉴设置卡片
     */
    MONSTER_BOOK_SET_CARD(0x53),
    /**
     * 怪物图鉴设置封面
     */
    MONSTER_BOOK_SET_COVER(0x54),
    /**
     * 时间变化
     */
    HOUR_CHANGED(0x55),
    /**
     * 小地图开关
     */
    MINIMAP_ON_OFF(0x56),
    /**
     * 咨询认证密钥更新
     */
    CONSULT_AUTH_KEY_UPDATE(0x57),
    /**
     * 竞技场认证密钥更新
     */
    CLASS_COMPETITION_AUTH_KEY_UPDATE(0x58),
    /**
     * 网络论坛认证密钥更新
     */
    WEB_BOARD_AUTH_KEY_UPDATE(0x59),
    /**
     * 会话值
     */
    SESSION_VALUE(0x5A),
    /**
     * 组队值
     */
    PARTY_VALUE(0x5B),
    /**
     * 字段设置变量
     */
    FIELD_SET_VARIABLE(0x5C),
    /**
     * 奖励经验值变化（猜测，不确定v83中的opcode）
     */
    BONUS_EXP_CHANGED(0x5D),
    /**
     * 家族图表结果
     */

    FAMILY_CHART_RESULT(0x5E),
    /**
     * 家族信息结果
     */
    FAMILY_INFO_RESULT(0x5F),
    /**
     * 家族结果
     */
    FAMILY_RESULT(0x60),
    /**
     * 家族加入请求
     */
    FAMILY_JOIN_REQUEST(0x61),
    /**
     * 家族加入请求结果
     */
    FAMILY_JOIN_REQUEST_RESULT(0x62),
    /**
     * 家族加入接受
     */
    FAMILY_JOIN_ACCEPTED(0x63),
    /**
     * 家族权限列表
     */
    FAMILY_PRIVILEGE_LIST(0x64),
    /**
     * 家族声望获得
     */
    FAMILY_REP_GAIN(0x65),
    /**
     * 通知家族成员登录或登出
     */
    FAMILY_NOTIFY_LOGIN_OR_LOGOUT(0x66),
    /**
     * 设置家族权限
     */
    FAMILY_SET_PRIVILEGE(0x67),
    /**
     * 家族召唤请求
     */
    FAMILY_SUMMON_REQUEST(0x68),
    /**
     * 通知等级提升
     */

    NOTIFY_LEVEL_UP(0x69),
    /**
     * 通知结婚
     */
    NOTIFY_MARRIAGE(0x6A),
    /**
     * 通知职业变更
     */
    NOTIFY_JOB_CHANGE(0x6B),
    /**
     * 可能是额外的饰品插槽，用于其他版本？
     */
    SET_BUY_EQUIP_EXT(0x6C),
    /**
     * Maple TV使用结果（不是空白，是一个弹窗）
     */
//
    MAPLE_TV_USE_RES(0x6D),
    /**
     * 头像喇叭结果（机器人无用）
     */
    AVATAR_MEGAPHONE_RESULT(0x6E),
    /**
     * 设置头像喇叭
     */
    SET_AVATAR_MEGAPHONE(0x6F),
    /**
     * 清除头像喇叭
     */
    CLEAR_AVATAR_MEGAPHONE(0x70),
    /**
     * 取消更改名字结果
     */
    CANCEL_NAME_CHANGE_RESULT(0x71),
    /**
     * 取消转移世界结果
     */
    CANCEL_TRANSFER_WORLD_RESULT(0x72),
    /**
     * 销毁商店结果
     */
    DESTROY_SHOP_RESULT(0x73),
    /**
     * 假GM通知（坏家伙）
     */
    FAKE_GM_NOTICE(0x74),
    /**
     * 成功使用扭蛋机箱
     */
    SUCCESS_IN_USE_GACHAPON_BOX(0x75),
    /**
     * 新年贺卡结果
     */
    NEW_YEAR_CARD_RES(0x76),
    /**
     * 随机变身结果
     */
    RANDOM_MORPH_RES(0x77),
    /**
     * 由他人取消更改名字
     */
    CANCEL_NAME_CHANGE_BY_OTHER(0x78),
    /**
     * 设置额外饰品插槽
     */
    SET_EXTRA_PENDANT_SLOT(0x79),
    /**
     * 脚本进度消息
     */
    SCRIPT_PROGRESS_MESSAGE(0x7A),
    /**
     * 数据CRC检查失败
     */
    DATA_CRC_CHECK_FAILED(0x7B),
    /**
     * 宏系统数据初始化
     */
    MACRO_SYS_DATA_INIT(0x7C),
    /**
     * CStage::OnPacket
     * 设置字段
     */

    /**/
    SET_FIELD(0x7D),
    /**
     * 设置ITC
     */
    SET_ITC(0x7E),
    /**
     * 设置现金商店
     */
    SET_CASH_SHOP(0x7F),
    /**
     * CField::OnPacket
     * 设置背景特效
     */

    /**/
//
    SET_BACK_EFFECT(0x80),
    /**
     * 设置地图对象可见性
     */
    SET_MAP_OBJECT_VISIBLE(0x81),
    /**
     * 清除背景特效
     */
    CLEAR_BACK_EFFECT(0x82),
    /**
     * 被阻止的地图
     */
    BLOCKED_MAP(0x83),
    /**
     * 被阻止的服务器
     */
    BLOCKED_SERVER(0x84),
    /**
     * 强制地图装备
     */
    FORCED_MAP_EQUIP(0x85),
    /**
     * 多人聊天
     */
    MULTI_CHAT(0x86),
    /**
     * 密语
     */
    WHISPER(0x87),
    /**
     * 配偶聊天
     */
    SPOUSE_CHAT(0x88),
    /**
     * 在此地图无法使用召唤物品
     */
    SUMMON_ITEM_INAVAILABLE(0x89),
    /**
     * 场景效果
     */

    FIELD_EFFECT(0x8A),
    /**
     * 场景障碍物开关
     */
    FIELD_OBSTACLE_ONOFF(0x8B),
    /**
     * 场景障碍物开关列表
     */
    FIELD_OBSTACLE_ONOFF_LIST(0x8C),
    /**
     * 重置所有场景障碍物
     */
    FIELD_OBSTACLE_ALL_RESET(0x8D),
    /**
     * 吹风天气效果
     */
    BLOW_WEATHER(0x8E),
    /**
     * 播放点唱机
     */
    PLAY_JUKEBOX(0x8F),
    /**
     * 管理员结果
     */

    ADMIN_RESULT(0x90),
    /**
     * QUIZ（OX问答）
     */
    OX_QUIZ(0x91),
    /**
     * DESC（游戏事件说明）
     */
    GM_EVENT_INSTRUCTIONS(0x92),
    /**
     * 时钟
     */
    CLOCK(0x93),
    /**
     * 连续移动
     */
    CONTI_MOVE(0x94),
    /**
     * 连续状态
     */
    CONTI_STATE(0x95),
    /**
     * 设置任务完成
     */
    SET_QUEST_CLEAR(0x96),
    /**
     * 设置任务时间
     */
    SET_QUEST_TIME(0x97),
    /**
     * thanks lrenex // ARIANT结果
     */
    ARIANT_RESULT(0x98),
    /**
     * 设置物体状态
     */
    SET_OBJECT_STATE(0x99),
    /**
     * 停止时钟
     */
    STOP_CLOCK(0x9A),
    /**
     * ARIANT竞技场显示结果
     */
    ARIANT_ARENA_SHOW_RESULT(0x9B),
    /**
     * 金字塔计数器
     */
    PYRAMID_GAUGE(0x9D),
    /**
     * 金字塔分数
     */
    PYRAMID_SCORE(0x9E),
    /**
     * LP_QuickslotMappedInit // 快捷栏初始化
     */
    QUICK_SLOT_INIT(0x9F),
    /**
     * 生成玩家
     */
    SPAWN_PLAYER(0xA0),
    /**
     * 从地图移除玩家
     */
    REMOVE_PLAYER_FROM_MAP(0xA1),
    /**
     * 聊天文本（类型0）
     */
    CHAT_TEXT(0xA2),
    /**
     * 聊天文本（类型1）
     */
    CHAT_TEXT1(0xA3),
    /**
     * 黑板
     */
    CHALKBOARD(0xA4),
    /**
     * 更新角色盒子
     */
    UPDATE_CHAR_BOX(0xA5),
    /**
     * 显示消耗效果
     */
    SHOW_CONSUME_EFFECT(0xA6),
    /**
     * 显示卷轴效果
     */
    SHOW_SCROLL_EFFECT(0xA7),
    /**
     * 生成宠物
     */

    SPAWN_PET(0xA8),
    /**
     * 移动宠物
     */
    MOVE_PET(0xAA),
    /**
     * 宠物对话
     */
    PET_CHAT(0xAB),
    /**
     * 更改宠物名字
     */
    PET_NAME_CHANGE(0xAC),
    /**
     * 宠物异常列表
     */
    PET_EXCEPTION_LIST(0xAD),
    /**
     * 宠物命令
     */
    PET_COMMAND(0xAE),
    /**
     * 生成特殊地图对象
     */
    SPAWN_SPECIAL_MAP_OBJECT(0xAF),
    /**
     * 移除特殊地图对象
     */
    REMOVE_SPECIAL_MAP_OBJECT(0xB0),
    /**
     * 移动召唤兽
     */
    MOVE_SUMMON(0xB1),
    /**
     * 召唤兽攻击
     */
    SUMMON_ATTACK(0xB2),
    /**
     * 召唤兽受到伤害
     */
    DAMAGE_SUMMON(0xB3),
    /**
     * 召唤兽技能
     */
    SUMMON_SKILL(0xB4),
    /**
     * 生成龙
     */
    SPAWN_DRAGON(0xB5),
    /**
     * 移动龙
     */
    MOVE_DRAGON(0xB6),
    /**
     * 移除龙
     */
    REMOVE_DRAGON(0xB7),
    /**
     * 移动玩家
     */
    MOVE_PLAYER(0xB9),
    /**
     * 近战攻击
     */
    CLOSE_RANGE_ATTACK(0xBA),
    /**
     * 远程攻击
     */
    RANGED_ATTACK(0xBB),
    /**
     * 魔法攻击
     */
    MAGIC_ATTACK(0xBC),
    /**
     * 能量攻击
     */
    ENERGY_ATTACK(0xBD),
    /**
     * 技能效果
     */
    SKILL_EFFECT(0xBE),
    /**
     * 取消技能效果
     */
    CANCEL_SKILL_EFFECT(0xBF),
    /**
     * 玩家受到伤害
     */
    DAMAGE_PLAYER(0xC0),
    /**
     * 表情符号
     */
    FACIAL_EXPRESSION(0xC1),
    /**
     * 显示物品效果
     */
    SHOW_ITEM_EFFECT(0xC2),
    /**
     * 显示椅子
     */
    SHOW_CHAIR(0xC4),
    /**
     * 更新角色外观
     */
    UPDATE_CHAR_LOOK(0xC5),
    /**
     * 显示远程效果
     */
    SHOW_FOREIGN_EFFECT(0xC6),
    /**
     * 给予远程增益效果
     */
    GIVE_FOREIGN_BUFF(0xC7),
    /**
     * 取消远程增益效果
     */
    CANCEL_FOREIGN_BUFF(0xC8),
    /**
     * 更新组队成员HP
     */
    UPDATE_PARTY_MEMBER_HP(0xC9),
    /**
     * 公会名称改变
     */
    GUILD_NAME_CHANGED(0xCA),
    /**
     * 公会标志改变
     */
    GUILD_MARK_CHANGED(0xCB),
    /**
     * 抛掷手榴弹
     */
    THROW_GRENADE(0xCC),
    /**
     * 取消椅子
     */
    CANCEL_CHAIR(0xCD),
    /**
     * 在聊天中显示获得物品
     */
    SHOW_ITEM_GAIN_IN_CHAT(0xCE),
    /**
     * 武道馆传送准备
     */
    DOJO_WARP_UP(0xCF),
    /**
     * 幸运袋成功
     */
    LUCKSACK_PASS(0xD0),
    /**
     * 幸运袋失败
     */
    LUCKSACK_FAIL(0xD1),
    /**
     * 金币背包消息
     */
    MESO_BAG_MESSAGE(0xD2),
    /**
     * 更新任务信息
     */
    UPDATE_QUEST_INFO(0xD3),
    /**
     * 通知字段减少HP
     */
    ON_NOTIFY_HP_DEC_BY_FIELD(0xD4),
    /**
     * 玩家提示
     */
    PLAYER_HINT(0xD6),
    /**
     * 制作器结果
     */
    MAKER_RESULT(0xD9),
    /**
     * 韩国活动
     */
    KOREAN_EVENT(0xDB),
    /**
     * 打开UI
     */
    OPEN_UI(0xDC),
    /**
     * 锁定UI
     */
    LOCK_UI(0xDD),
    /**
     * 禁用UI
     */
    DISABLE_UI(0xDE),
    /**
     * 生成引导者
     */
    SPAWN_GUIDE(0xDF),
    /**
     * 引导者对话
     */
    TALK_GUIDE(0xE0),
    /**
     * 显示连击
     */
    SHOW_COMBO(0xE1),
    /**
     * 冷却时间
     */
    COOLDOWN(0xEA),
    /**
     * 生成怪物
     */
    SPAWN_MONSTER(0xEC),
    /**
     * 击杀怪物
     */
    KILL_MONSTER(0xED),
    /**
     * 控制生成怪物
     */
    SPAWN_MONSTER_CONTROL(0xEE),
    /**
     * 移动怪物
     */
    MOVE_MONSTER(0xEF),
    /**
     * 移动怪物响应
     */
    MOVE_MONSTER_RESPONSE(0xF0),
    /**
     * 应用怪物状态
     */
    APPLY_MONSTER_STATUS(0xF2),
    /**
     * 取消怪物状态
     */
    CANCEL_MONSTER_STATUS(0xF3),
    /**
     * LOL? o.o // 重置怪物动画
     */
    RESET_MONSTER_ANIMATION(0xF4),
    /**
     * 怪物受到伤害
     */

    DAMAGE_MONSTER(0xF6),
    /**
     * ARIANT相关操作
     */
    ARIANT_THING(0xF9),
    /**
     * 显示怪物HP
     */
    SHOW_MONSTER_HP(0xFA),
    /**
     * 捕捉怪物
     */
    CATCH_MONSTER(0xFB),
    /**
     * 使用物品捕捉怪物
     */
    CATCH_MONSTER_WITH_ITEM(0xFC),
    /**
     * 显示磁铁效果
     */
    SHOW_MAGNET(0xFD),
    /**
     * 生成NPC
     */
    SPAWN_NPC(0x101),
    /**
     * 移除NPC
     */
    REMOVE_NPC(0x102),
    /**
     * 请求控制NPC生成
     */
    SPAWN_NPC_REQUEST_CONTROLLER(0x103),
    /**
     * NPC动作
     */
    NPC_ACTION(0x104),
    /**
     * 设置NPC可脚本化
     */
    SET_NPC_SCRIPTABLE(0x107),
    /**
     * 生成雇佣商人
     */
    SPAWN_HIRED_MERCHANT(0x109),
    /**
     * 销毁雇佣商人
     */
    DESTROY_HIRED_MERCHANT(0x10A),
    /**
     * 更新雇佣商人
     */
    UPDATE_HIRED_MERCHANT(0x10B),
    /**
     * 从地图对象掉落物品
     */
    DROP_ITEM_FROM_MAPOBJECT(0x10C),
    /**
     * 从地图移除物品
     */
    REMOVE_ITEM_FROM_MAP(0x10D),
    /**
     * 无法生成风筝
     */
    CANNOT_SPAWN_KITE(0x10E),
    /**
     * 生成风筝
     */
    SPAWN_KITE(0x10F),
    /**
     * 移除风筝
     */
    REMOVE_KITE(0x110),
    /**
     * 生成迷雾
     */
    SPAWN_MIST(0x111),
    /**
     * 移除迷雾
     */
    REMOVE_MIST(0x112),
    /**
     * 生成门
     */
    SPAWN_DOOR(0x113),
    /**
     * 移除门
     */
    REMOVE_DOOR(0x114),
    /**
     * 反应堆被击中
     */
    REACTOR_HIT(0x115),
    /**
     * 生成反应堆
     */
    REACTOR_SPAWN(0x117),
    /**
     * 销毁反应堆
     */
    REACTOR_DESTROY(0x118),
    /**
     * 雪球状态
     */
    SNOWBALL_STATE(0x119),
    /**
     * 击中雪球
     */
    HIT_SNOWBALL(0x11A),
    /**
     * 雪球消息
     */
    SNOWBALL_MESSAGE(0x11B),
    /**
     * 左侧击退
     */
    LEFT_KNOCK_BACK(0x11C),
    /**
     * 击中椰子
     */
    COCONUT_HIT(0x11D),
    /**
     * 椰子得分
     */
    COCONUT_SCORE(0x11E),
    /**
     * 公会长老移动
     */
    GUILD_BOSS_HEALER_MOVE(0x11F),
    /**
     * 公会长老滑轮状态改变
     */
    GUILD_BOSS_PULLEY_STATE_CHANGE(0x120),
    /**
     * 怪物嘉年华开始
     */
    MONSTER_CARNIVAL_START(0x121),
    /**
     * 获得怪物嘉年华CP
     */
    MONSTER_CARNIVAL_OBTAINED_CP(0x122),
    /**
     * 怪物嘉年华队伍CP
     */
    MONSTER_CARNIVAL_PARTY_CP(0x123),
    /**
     * 怪物嘉年华召唤
     */
    MONSTER_CARNIVAL_SUMMON(0x124),
    /**
     * 怪物嘉年华消息
     */
    MONSTER_CARNIVAL_MESSAGE(0x125),
    /**
     * 怪物嘉年华死亡
     */
    MONSTER_CARNIVAL_DIED(0x126),
    /**
     * 离开怪物嘉年华
     */
    MONSTER_CARNIVAL_LEAVE(0x127),
    /**
     * ARIANT竞技场用户得分
     */

    ARIANT_ARENA_USER_SCORE(0x129),
    /**
     * 羊牧场信息
     */
    SHEEP_RANCH_INFO(0x12B),
    /**
     * 羊牧场服装
     */
    SHEEP_RANCH_CLOTHES(0x12C),
    /**
     * thanks lrenex // 巫师塔得分更新
     */
    WITCH_TOWER_SCORE_UPDATE(0x12D),
    /**
     * 角龙头洞
     */
    HORNTAIL_CAVE(0x12E),
    /**
     * 泽库姆神殿
     */
    ZAKUM_SHRINE(0x12F),
    /**
     * NPC对话
     */
    NPC_TALK(0x130),
    /**
     * 打开NPC商店
     */
    OPEN_NPC_SHOP(0x131),
    /**
     * 确认商店交易
     */
    CONFIRM_SHOP_TRANSACTION(0x132),
    /**
     * lame :P // 管理员商店消息
     */
    ADMIN_SHOP_MESSAGE(0x133),
    /**
     * 管理员商店
     */
    ADMIN_SHOP(0x134),
    /**
     * 仓库
     */
    STORAGE(0x135),
    /**
     * Fredrick消息
     */
    FREDRICK_MESSAGE(0x136),
    /**
     * Fredrick操作
     */
    FREDRICK(0x137),
    /**
     * 石头剪刀布游戏
     */
    RPS_GAME(0x138),
    /**
     * 消息传递
     */
    MESSENGER(0x139),
    /**
     * 玩家互动
     */
    PLAYER_INTERACTION(0x13A),
    /**
     * 锦标赛
     */

    TOURNAMENT(0x13B),
    /**
     * 锦标赛匹配表
     */
    TOURNAMENT_MATCH_TABLE(0x13C),
    /**
     * 设置锦标赛奖品
     */
    TOURNAMENT_SET_PRIZE(0x13D),
    /**
     * 锦标赛未知功能
     */
    TOURNAMENT_UEW(0x13E),
    /**
     * they never coded this :| // 锦标赛角色（他们从未实现这个功能）
     */
    TOURNAMENT_CHARACTERS(0x13F),
    /**
     * byte step, int groomid, int brideid // 结婚进度
     */
    WEDDING_PROGRESS(0x140),
    /**
     * 结婚仪式结束
     */
    WEDDING_CEREMONY_END(0x141),
    /**
     * 礼包
     */

    PARCEL(0x142),
    /**
     * 充值参数结果
     */

    CHARGE_PARAM_RESULT(0x143),
    /**
     * 查询现金结果
     */
    QUERY_CASH_RESULT(0x144),
    /**
     * 现金商店操作
     */
    CASH_SHOP_OPERATION(0x145),
    /**
     * found thanks to Arnah (Vertisy) // 现金商店购买经验变化
     */
    CASH_SHOP_PURCHASE_EXP_CHANGED(0x146),
    /**
     * 现金商店礼物信息结果
     */
    CASH_SHOP_GIFT_INFO_RESULT(0x147),
    /**
     * 检查现金商店姓名更改
     */
    CASH_SHOP_CHECK_NAME_CHANGE(0x148),
    /**
     * 检查现金商店姓名更改可能性结果
     */
    CASH_SHOP_CHECK_NAME_CHANGE_POSSIBLE_RESULT(0x149),
    /**
     * 注册新角色结果
     */
    CASH_SHOP_REGISTER_NEW_CHARACTER_RESULT(0x14A),
    /**
     * 检查转移世界可能性结果
     */
    CASH_SHOP_CHECK_TRANSFER_WORLD_POSSIBLE_RESULT(0x14B),
    /**
     * 现金商店扭蛋印章结果
     */
    CASH_SHOP_GACHAPON_STAMP_RESULT(0x14C),
    /**
     * 现金商店现金物品扭蛋结果
     */
    CASH_SHOP_CASH_ITEM_GACHAPON_RESULT(0x14D),
    /**
     * 现金商店现金扭蛋打开结果
     */
    CASH_SHOP_CASH_GACHAPON_OPEN_RESULT(0x14E),
    /**
     * 键盘映射
     */

    KEYMAP(0x14F),
    /**
     * 自动使用HP药水
     */
    AUTO_HP_POT(0x150),
    /**
     * 自动使用MP药水
     */
    AUTO_MP_POT(0x151),
    /**
     * 发送电视
     */
    SEND_TV(0x155),

    /**
     * 移除电视
     */
    REMOVE_TV(0x156),

    /**
     * 启用电视
     */
    ENABLE_TV(0x157),

    /**
     * MTS操作2
     */
    MTS_OPERATION2(0x15B),

    /**
     * MTS操作
     */
    MTS_OPERATION(0x15C),

    /**
     * MapleLife结果
     */
    MAPLELIFE_RESULT(0x15D),

    /**
     * MapleLife错误
     */
    MAPLELIFE_ERROR(0x15E),

    /**
     * 恶毒锤子
     */
    VICIOUS_HAMMER(0x162),

    /**
     * VEGA卷轴
     */
    VEGA_SCROLL(0x166),

    /**
     * 更新HP/MP/EXP警报
     */
    UPDATE_HP_MP_ALERT(0x1000);

    private int code = -2;

    SendOpcode(int code) {
        this.code = code;
    }

    public int getValue() {
        return code;
    }
}

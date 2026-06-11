/*
    This file is part of the HeavenMS MapleStory Server, commands OdinMS-based
    Copyleft (L) 2016 - 2019 RonanLana

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

/*
   @Author: Arthur L - Refactored command content into modules
*/
package org.gms.client.command;

import lombok.Getter;
import org.gms.client.Client;
import org.gms.client.command.commands.gm0.*;
import org.gms.client.command.commands.gm1.*;
import org.gms.client.command.commands.gm2.*;
import org.gms.client.command.commands.gm3.*;
import org.gms.client.command.commands.gm4.*;
import org.gms.client.command.commands.gm5.*;
import org.gms.client.command.commands.gm6.*;
import org.gms.config.GameConfig;
import org.gms.constants.id.MapId;
import org.gms.manager.ServerManager;
import org.gms.service.CommandService;
import org.gms.util.I18nUtil;
import org.gms.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 命令执行器（单例）
 * 管理所有命令的注册、查找和执行
 * 支持玩家命令（@开头）和GM命令（!开头），按GM等级分级
 * 从数据库加载命令配置，支持动态重载
 *
 * @Author: Arthur L - Refactored command content into modules
 */
public class CommandsExecutor {
    private static final Logger log = LoggerFactory.getLogger(CommandsExecutor.class);
    /** 单例实例 */
    @Getter
    private static final CommandsExecutor instance = new CommandsExecutor();
    /** 玩家命令前缀 */
    private static final char USER_HEADING = '@';
    /** GM命令前缀 */
    private static final char GM_HEADING = '!';

    /** 已注册命令映射（命令名 -> 命令对象） */
    @Getter
    private final HashMap<String, Command> registeredCommands = new HashMap<>();
    /** 命令名称和描述列表，按等级分组 */
    @Getter
    private final List<Pair<List<String>, List<String>>> commandsNameDesc = new ArrayList<>();
    /** 当前等级命令组游标 */
    private Pair<List<String>, List<String>> levelCommandsCursor;

    private static final CommandService commandService = ServerManager.getApplicationContext().getBean(CommandService.class);

    /**
     * 判断输入内容是否为命令
     *
     * @param client  客户端对象
     * @param content 输入内容
     * @return true=是命令，false=不是命令
     */
    public static boolean isCommand(Client client, String content) {
        char heading = content.charAt(0);
        if (client.getPlayer().isGM()) {
            return heading == USER_HEADING || heading == GM_HEADING;
        }
        return heading == USER_HEADING;
    }

    /**
     * 加载所有命令（从数据库）
     * 原始硬编码注册方法已注释保留，当前使用数据库动态配置
     */
    public void loadCommandsExecutor() {
//        registeredCommands.clear();
//        commandsNameDesc.clear();
//        registerLv0Commands();
//        registerLv1Commands();
//        registerLv2Commands();
//        registerLv3Commands();
//        registerLv4Commands();
//        registerLv5Commands();
//        registerLv6Commands();

        commandService.loadCommands(registeredCommands, commandsNameDesc);
    }

    /**
     * 处理客户端发来的命令消息
     * 获取客户端锁后调用内部处理方法，保证线程安全
     *
     * @param client  客户端对象
     * @param message 命令消息文本
     */
    public void handle(Client client, String message) {
        if (client.tryacquireClient()) {
            try {
                handleInternal(client, message);
            } finally {
                client.releaseClient();
            }
        } else {
            client.getPlayer().dropMessage(5, I18nUtil.getMessage("CommandsExecutor.handle.message1"));
        }
    }

    /**
     * 内部处理命令：解析命令名、参数，权限校验，执行命令
     *
     * @param client  客户端对象
     * @param message 命令消息文本
     */
    private void handleInternal(Client client, String message) {
        // 监狱地图中非GM玩家禁止使用命令
        if (client.getPlayer().getMapId() == MapId.JAIL && !client.getPlayer().isGM()) {
            client.getPlayer().yellowMessage(I18nUtil.getMessage("CommandsExecutor.handleInternal.message1"));
            return;
        }
        // 禁止非GM玩家使用玩家命令（配置开关）
        char heading = message.charAt(0);
        if (!client.getPlayer().isGM() && heading == USER_HEADING && GameConfig.getServerBoolean("deterred_player_command")) {
            client.getPlayer().yellowMessage(I18nUtil.getMessage("CommandsExecutor.handleInternal.message4"));
            return;
        }
        final String splitRegex = "[ ]";
        String[] splitedMessage = message.substring(1).split(splitRegex, 2);
        if (splitedMessage.length < 2) {
            splitedMessage = new String[]{splitedMessage[0], ""};
        }

        // 保存最后一条命令参数，保持小写方便匹配
        client.getPlayer().setLastCommandMessage(splitedMessage[1]);
        final String commandName = splitedMessage[0].toLowerCase();
        final String[] lowercaseParams = splitedMessage[1].toLowerCase().split(splitRegex);

        // 命令不存在
        final Command command = registeredCommands.get(commandName);
        if (command == null) {
            client.getPlayer().yellowMessage(I18nUtil.getMessage("CommandsExecutor.handleInternal.message2", commandName));
            return;
        }
        // GM等级不足
        if (client.getPlayer().gmLevel() < command.getRank()) {
            client.getPlayer().yellowMessage(I18nUtil.getMessage("CommandsExecutor.handleInternal.message3"));
            return;
        }
        // 构建参数数组
        String[] params;
        if (lowercaseParams.length > 0 && !lowercaseParams[0].isEmpty()) {
            params = Arrays.copyOfRange(lowercaseParams, 0, lowercaseParams.length);
        } else {
            params = new String[]{};
        }

        // 执行命令并记录日志
        command.execute(client, params);
        log.info(I18nUtil.getLogMessage("CommandsExecutor.handleInternal.info1"), client.getPlayer().getName(), command.getClass().getSimpleName());
    }

    /**
     * 添加命令信息到当前等级分组（名称和描述）
     *
     * @param name         命令名称
     * @param commandClass 命令类
     */
    private void addCommandInfo(String name, Class<? extends Command> commandClass) {
        try {
            levelCommandsCursor.getRight().add(commandClass.getDeclaredConstructor().newInstance().getDescription());
            levelCommandsCursor.getLeft().add(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 批量添加多个命令（等级0）
     */
    private void addCommand(String[] syntaxs, Class<? extends Command> commandClass) {
        for (String syntax : syntaxs) {
            addCommand(syntax, commandClass);
        }
    }

    /**
     * 添加单个命令（等级0）
     */
    private void addCommand(String syntax, Class<? extends Command> commandClass) {
        addCommand(syntax, 0, commandClass);
    }

    /**
     * 批量添加多个命令到指定等级
     */
    private void addCommand(String[] surtaxes, int rank, Class<? extends Command> commandClass) {
        for (String syntax : surtaxes) {
            addCommand(syntax, rank, commandClass);
        }
    }

    /**
     * 添加单个命令到指定等级
     * 反射实例化命令对象，每个命令只注册一次
     *
     * @param syntax       命令名称
     * @param rank         GM等级要求
     * @param commandClass 命令类
     */
    private void addCommand(String syntax, int rank, Class<? extends Command> commandClass) {
        if (registeredCommands.containsKey(syntax.toLowerCase())) {
            log.warn(I18nUtil.getLogMessage("CommandsExecutor.addCommand.warn1"), syntax);
            return;
        }

        String commandName = syntax.toLowerCase();
        addCommandInfo(commandName, commandClass);

        try {
            // 反射实例化命令对象，每次注册只实例化一次
            Command commandInstance = commandClass.getDeclaredConstructor().newInstance();
            commandInstance.setRank(rank);

            registeredCommands.put(commandName, commandInstance);
        } catch (Exception e) {
            log.warn(I18nUtil.getLogMessage("CommandsExecutor.addCommand.warn2"), e);
        }
    }

    /**
     * 注册等级0命令（玩家命令，所有人可用）
     */
    private void registerLv0Commands() {
        levelCommandsCursor = new Pair<>(new ArrayList<String>(), new ArrayList<String>());

        // 查看所有可用命令
        addCommand(new String[]{"help", "commands"}, HelpCommand.class);
        // 查看掉落限制
        addCommand("droplimit", DropLimitCommand.class);
        // 查看当前服务器时间
        addCommand("time", TimeCommand.class);
        // 查看服务器制作人员名单
        addCommand("credits", StaffCommand.class);
        // 查看服务器运行时长
        addCommand("uptime", UptimeCommand.class);
        // 扭蛋机抽奖
        addCommand("gacha", GachaCommand.class);
        // 解除卡号状态
        addCommand("dispose", DisposeCommand.class);
        // 切换客户端语言
        addCommand("changel", ChangeLanguageCommand.class);
        // 查看装备等级
        addCommand("equiplv", EquipLvCommand.class);
        // 查看当前服务器倍率
        addCommand("showrates", ShowRatesCommand.class);
        // 查看个人倍率信息
        addCommand("rates", RatesCommand.class);
        // 查看在线人数
        addCommand("online", OnlineCommand.class);
        // 查看在线GM列表
        addCommand("gm", GmCommand.class);
        // 提交Bug报告
        addCommand("reportbug", ReportBugCommand.class);
        // 查看个人积分
        addCommand("points", ReadPointsCommand.class);
        // 加入活动
        addCommand("joinevent", JoinEventCommand.class);
        // 离开活动
        addCommand("leaveevent", LeaveEventCommand.class);
        // 查看排行榜
        addCommand("ranks", RanksCommand.class);
        // 力量加点
        addCommand("str", StatStrCommand.class);
        // 敏捷加点
        addCommand("dex", StatDexCommand.class);
        // 智力加点
        addCommand("int", StatIntCommand.class);
        // 运气加点
        addCommand("luk", StatLukCommand.class);
        // 启用设备认证
        addCommand("enableauth", EnableAuthCommand.class);
        // 开关经验获取
        addCommand("toggleexp", ToggleExpCommand.class);
        // 查看房屋归属
        addCommand("mylawn", MapOwnerClaimCommand.class);

        commandsNameDesc.add(levelCommandsCursor);
    }


    /**
     * 注册等级1命令（初级GM，如查询信息）
     */
    private void registerLv1Commands() {
        levelCommandsCursor = new Pair<>(new ArrayList<String>(), new ArrayList<String>());

        // 查看Boss血量
        addCommand("bosshp", 1, BossHpCommand.class);
        // 查看怪物血量
        addCommand("mobhp", 1, MobHpCommand.class);
        // 查看怪物掉落物品
        addCommand("whatdropsfrom", 1, WhatDropsFromCommand.class);
        // 查看物品由谁掉落
        addCommand("whodrops", 1, WhoDropsCommand.class);
        // 给自己加满Buff
        addCommand("buffme", 1, BuffMeCommand.class);
        // 传送到指定玩家或地图
        addCommand("goto", 1, GotoCommand.class);

        commandsNameDesc.add(levelCommandsCursor);
    }


    /**
     * 注册等级2命令（中级GM，如物品、传送、属性）
     */
    private void registerLv2Commands() {
        levelCommandsCursor = new Pair<>(new ArrayList<String>(), new ArrayList<String>());

        // 为玩家充值
        addCommand("recharge", 2, RechargeCommand.class);
        // 查看当前所在地图
        addCommand("whereami", 2, WhereaMiCommand.class);
        // 管理员隐身
        addCommand("hide", 2, HideCommand.class);
        // 取消管理员隐身
        addCommand("unhide", 2, UnHideCommand.class);
        // 增加技能点
        addCommand("sp", 2, SpCommand.class);
        // 增加能力点
        addCommand("ap", 2, ApCommand.class);
        // 给自己满技能/属性
        addCommand("empowerme", 2, EmpowerMeCommand.class);
        // 给全图玩家加Buff
        addCommand("buffmap", 2, BuffMapCommand.class);
        // 给指定玩家加Buff
        addCommand("buff", 2, BuffCommand.class);
        // 投掷炸弹（范围伤害）
        addCommand("bomb", 2, BombCommand.class);
        // 踢玩家下线
        addCommand("dc", 2, DcCommand.class);
        // 清除地面掉落物
        addCommand("cleardrops", 2, ClearDropsCommand.class);
        // 清除指定装备栏位
        addCommand("clearslot", 2, ClearSlotCommand.class);
        // 清除保存的传送位置
        addCommand("clearsavelocs", 2, ClearSavedLocationsCommand.class);
        // 传送到指定地图
        addCommand("warp", 2, WarpCommand.class);
        // 召唤玩家到当前位置
        addCommand(new String[]{"warphere", "summon"}, 2, SummonCommand.class);
        // 传送到指定玩家身边
        addCommand(new String[]{"warpto", "reach", "follow"}, 2, ReachCommand.class);
        // 打开GM商店
        addCommand("gmshop", 2, GmShopCommand.class);
        // 恢复HP/MP
        addCommand("heal", 2, HealCommand.class);
        // 创建物品
        addCommand("item", 2, ItemCommand.class);
        // 掉落物品到地面
        addCommand("drop", 2, ItemDropCommand.class);
        // 设置玩家等级
        addCommand("level", 2, LevelCommand.class);
        // 专业等级调整
        addCommand("levelpro", 2, LevelProCommand.class);
        // 设置装备栏位数量
        addCommand("setslot", 2, SetSlotCommand.class);
        // 设置属性值
        addCommand("setstat", 2, SetStatCommand.class);
        // 属性值全满（999）
        addCommand("maxstat", 2, MaxStatCommand.class);
        // 技能等级全满
        addCommand("maxskill", 2, MaxSkillCommand.class);
        // 重置技能
        addCommand("resetskill", 2, ResetSkillCommand.class);
        // 搜索物品/怪物/NPC
        addCommand("search", 2, SearchCommand.class);
        // 将玩家关进监狱
        addCommand("jail", 2, JailCommand.class);
        // 将玩家释放出狱
        addCommand("unjail", 2, UnJailCommand.class);
        // 更改玩家职业
        addCommand("job", 2, JobCommand.class);
        // 解除玩家Bug状态
        addCommand("unbug", 2, UnBugCommand.class);
        // 根据ID查询物品/怪物信息
        addCommand("id", 2, IdCommand.class);
        // 查看扭蛋机配置列表
        addCommand("gachalist", 2, GachaListCommand.class);
        // 拾取地面物品
        addCommand("loot", 2, LootCommand.class);
        // 查看怪物技能
        addCommand("mobskill", 2, MobSkillCommand.class);
        // 全图传送
        addCommand("warpmap", 2, WarpMapCommand.class);
        // 区域传送
        addCommand("warparea", 2, WarpAreaCommand.class);

        commandsNameDesc.add(levelCommandsCursor);
    }

    /**
     * 注册等级3命令（高级GM，如活动、封禁、广播）
     */
    private void registerLv3Commands() {
        levelCommandsCursor = new Pair<>(new ArrayList<String>(), new ArrayList<String>());

        // 给目标解除Buff
        addCommand("debuff", 3, DebuffCommand.class);
        // 开启飞行模式
        addCommand("fly", 3, FlyCommand.class);
        // 召唤怪物
        addCommand("spawn", 3, SpawnCommand.class);
        // 禁言当前地图
        addCommand("mutemap", 3, MuteMapCommand.class);
        // 查看伤害统计
        addCommand("checkdmg", 3, CheckDmgCommand.class);
        // 查看当前地图所有玩家
        addCommand("inmap", 3, InMapCommand.class);
        // 重载事件配置
        addCommand("reloadevents", 3, ReloadEventsCommand.class);
        // 重载掉落配置
        addCommand("reloaddrops", 3, ReloadDropsCommand.class);
        // 重载传送门
        addCommand("reloadportals", 3, ReloadPortalsCommand.class);
        // 重载地图数据
        addCommand("reloadmap", 3, ReloadMapCommand.class);
        // 重载商店数据
        addCommand("reloadshops", 3, ReloadShopsCommand.class);
        // 设置HP/MP值
        addCommand("hpmp", 3, HpMpCommand.class);
        // HP/MP全满
        addCommand("maxhpmp", 3, MaxHpMpCommand.class);
        // 播放指定BGM
        addCommand("music", 3, MusicCommand.class);
        // 开始监控指定玩家
        addCommand("monitor", 3, MonitorCommand.class);
        // 查看监控中玩家列表
        addCommand("monitors", 3, MonitorsCommand.class);
        // 忽略指定玩家（聊天屏蔽）
        addCommand("ignore", 3, IgnoreCommand.class);
        // 查看已忽略玩家列表
        addCommand("ignored", 3, IgnoredCommand.class);
        // 查看玩家坐标
        addCommand("pos", 3, PosCommand.class);
        // 开关优惠券功能
        addCommand("togglecoupon", 3, ToggleCouponCommand.class);
        // 开关白字聊天频道
        addCommand("togglewhitechat", 3, ChatCommand.class);
        // 设置玩家人气值
        addCommand("fame", 3, FameCommand.class);
        // 给予点券
        addCommand("givenx", 3, GiveNxCommand.class);
        // 给予投票点数
        addCommand("givevp", 3, GiveVpCommand.class);
        // 给予金币
        addCommand("givems", 3, GiveMesosCommand.class);
        // 给予声望
        addCommand("giverp", 3, GiveRpCommand.class);
        // 远征队相关操作
        addCommand("expeds", 3, ExpedsCommand.class);
        // 杀死指定玩家
        addCommand("kill", 3, KillCommand.class);
        // 种子系统
        addCommand("seed", 3, SeedCommand.class);
        // 能量值全满
        addCommand("maxenergy", 3, MaxEnergyCommand.class);
        // 杀死当前地图所有怪物
        addCommand("killall", 3, KillAllCommand.class);
        // 发送全服公告
        addCommand("notice", 3, NoticeCommand.class);
        // 强制杀死玩家
        addCommand("rip", 3, RipCommand.class);
        // 开启传送门
        addCommand("openportal", 3, OpenPortalCommand.class);
        // 关闭传送门
        addCommand("closeportal", 3, ClosePortalCommand.class);
        // 查看角色详细属性
        addCommand("pe", 3, PeCommand.class);
        // 开始活动
        addCommand("startevent", 3, StartEventCommand.class);
        // 结束活动
        addCommand("endevent", 3, EndEventCommand.class);
        // 开始地图活动
        addCommand("startmapevent", 3, StartMapEventCommand.class);
        // 停止地图活动
        addCommand("stopmapevent", 3, StopMapEventCommand.class);
        // 查看详细在线列表
        addCommand("online2", 3, OnlineTwoCommand.class);
        // 封禁玩家账号
        addCommand("ban", 3, BanCommand.class);
        // 解封玩家账号
        addCommand("unban", 3, UnBanCommand.class);
        // 治疗当前地图所有玩家
        addCommand("healmap", 3, HealMapCommand.class);
        // 治疗指定玩家
        addCommand("healperson", 3, HealPersonCommand.class);
        // 对玩家造成伤害
        addCommand("hurt", 3, HurtCommand.class);
        // 杀死当前地图所有玩家
        addCommand("killmap", 3, KillMapCommand.class);
        // 切换黑夜效果
        addCommand("night", 3, NightCommand.class);
        // 生成NPC
        addCommand("npc", 3, NpcCommand.class);
        // 更换角色脸型
        addCommand("face", 3, FaceCommand.class);
        // 更换角色发型
        addCommand("hair", 3, HairCommand.class);
        // 开始指定任务
        addCommand("startquest", 3, QuestStartCommand.class);
        // 完成指定任务
        addCommand("completequest", 3, QuestCompleteCommand.class);
        // 重置指定任务
        addCommand("resetquest", 3, QuestResetCommand.class);
        // 设置定时器
        addCommand("timer", 3, TimerCommand.class);
        // 设置地图定时器
        addCommand("timermap", 3, TimerMapCommand.class);
        // 设置全局定时器
        addCommand("timerall", 3, TimerAllCommand.class);

        commandsNameDesc.add(levelCommandsCursor);
    }

    /**
     * 注册等级4命令（超级GM，如服务器倍率、Boss、NPC管理）
     */
    private void registerLv4Commands() {
        levelCommandsCursor = new Pair<>(new ArrayList<String>(), new ArrayList<String>());

        // 设置服务器滚动消息
        addCommand("servermessage", 4, ServerMessageCommand.class);
        // 创建带额外属性的高级物品
        addCommand("proitem", 4, ProItemCommand.class);
        // 设置装备额外属性
        addCommand("seteqstat", 4, SetEqStatCommand.class);
        // 设置经验倍率
        addCommand("exprate", 4, ExpRateCommand.class);
        // 设置金币倍率
        addCommand("mesorate", 4, MesoRateCommand.class);
        // 设置掉落倍率
        addCommand("droprate", 4, DropRateCommand.class);
        // 设置Boss掉落倍率
        addCommand("bossdroprate", 4, BossDropRateCommand.class);
        // 设置任务奖励倍率
        addCommand("questrate", 4, QuestRateCommand.class);
        // 设置旅行倍率
        addCommand("travelrate", 4, TravelRateCommand.class);
        // 设置钓鱼倍率
        addCommand("fishrate", 4, FishingRateCommand.class);
        // 物品吸怪（将物品吸到身边）
        addCommand("itemvac", 4, ItemVacCommand.class);
        // 强制全域吸怪
        addCommand("forcevac", 4, ForceVacCommand.class);
        // 召唤扎昆Boss
        addCommand("zakum", 4, ZakumCommand.class);
        // 召唤黑龙Boss
        addCommand("horntail", 4, HorntailCommand.class);
        // 召唤品克缤Boss
        addCommand("pinkbean", 4, PinkbeanCommand.class);
        // 召唤蝙蝠魔Boss
        addCommand("pap", 4, PapCommand.class);
        // 召唤鱼王Boss
        addCommand("pianus", 4, PianusCommand.class);
        // 召唤蛋糕Boss
        addCommand("cake", 4, CakeCommand.class);
        // 创建玩家NPC
        addCommand("playernpc", 4, PlayerNpcCommand.class);
        // 移除玩家NPC
        addCommand("playernpcremove", 4, PlayerNpcRemoveCommand.class);
        // 创建永久NPC
        addCommand("pnpc", 4, PnpcCommand.class);
        // 移除永久NPC
        addCommand("pnpcremove", 4, PnpcRemoveCommand.class);
        // 创建永久怪物
        addCommand("pmob", 4, PmobCommand.class);
        // 移除永久怪物
        addCommand("pmobremove", 4, PmobRemoveCommand.class);
        // 传送到最近的生物身边
        addCommand("warptolife", 4, WarpToLifeCommand.class);

        commandsNameDesc.add(levelCommandsCursor);
    }

    /**
     * 注册等级5命令（调试级GM，如调试、会话监控）
     */
    private void registerLv5Commands() {
        levelCommandsCursor = new Pair<>(new ArrayList<String>(), new ArrayList<String>());

        // 开启调试模式
        addCommand("debug", 5, DebugCommand.class);
        // 设置系统变量
        addCommand("set", 5, SetCommand.class);
        // 显示客户端收发包
        addCommand("showpackets", 5, ShowPacketsCommand.class);
        // 显示生物移动轨迹
        addCommand("showmovelife", 5, ShowMoveLifeCommand.class);
        // 显示所有活跃会话
        addCommand("showsessions", 5, ShowSessionsCommand.class);
        // 查看在线玩家IP列表
        addCommand("iplist", 5, IpListCommand.class);

        commandsNameDesc.add(levelCommandsCursor);
    }

    /**
     * 注册等级6命令（管理员级GM，如服务器管理、世界迁移）
     */
    private void registerLv6Commands() {
        levelCommandsCursor = new Pair<>(new ArrayList<String>(), new ArrayList<String>());

        // 设置其他玩家的GM等级
        addCommand("setgmlevel", 6, SetGmLevelCommand.class);
        // 跨世界传送
        addCommand("warpworld", 6, WarpWorldCommand.class);
        // 强制保存所有玩家数据
        addCommand("saveall", 6, SaveAllCommand.class);
        // 踢所有玩家下线
        addCommand("dcall", 6, DCAllCommand.class);
        // 统计各地图玩家数量
        addCommand("mapplayers", 6, MapPlayersCommand.class);
        // 通过角色名获取账号信息
        addCommand("getacc", 6, GetAccCommand.class);
        // 关闭服务器
        addCommand("shutdown", 6, ShutdownCommand.class);
        // 清除任务缓存
        addCommand("clearquestcache", 6, ClearQuestCacheCommand.class);
        // 清除玩家任务数据
        addCommand("clearquest", 6, ClearQuestCommand.class);
        // 发放倍率优惠券
        addCommand("supplyratecoupon", 6, SupplyRateCouponCommand.class);
        // 生成所有永久NPC
        addCommand("spawnallpnpcs", 6, SpawnAllPNpcsCommand.class);
        // 移除所有永久NPC
        addCommand("eraseallpnpcs", 6, EraseAllPNpcsCommand.class);
        // 添加频道
        addCommand("addchannel", 6, ServerAddChannelCommand.class);
        // 添加世界
        addCommand("addworld", 6, ServerAddWorldCommand.class);
        // 移除频道
        addCommand("removechannel", 6, ServerRemoveChannelCommand.class);
        // 移除世界
        addCommand("removeworld", 6, ServerRemoveWorldCommand.class);
        // 开发者测试命令
        addCommand("devtest", 6, DevtestCommand.class);

        commandsNameDesc.add(levelCommandsCursor);
    }

}
/*
 * BOSS传送脚本
 * 作者：Claude AI
 * 版本：3.0
 * 功能：传送玩家到BOSS地图，显示BOSS存活状态，支持多频道查看，使用图标显示BOSS
 */

// BOSS图标定义
var 蜗牛王图标 = "#fUI/UIWindow.img/MobGage/Mob/2220000#";
var 树妖王图标 = "#fUI/UIWindow.img/MobGage/Mob/3220000#";
var 大宇图标 = "#fUI/UIWindow.img/MobGage/Mob/3220001#";
var 歇尔夫图标 = "#fUI/UIWindow.img/MobGage/Mob/4220000#";
var 战甲鱼图标 = "#fUI/UIWindow.img/MobGage/Mob/9300182#";
var 浮士德图标 = "#fUI/UIWindow.img/MobGage/Mob/5220002#";
var 冰海螺图标 = "#fUI/UIWindow.img/MobGage/Mob/5220000#";
var 提莫图标 = "#fUI/UIWindow.img/MobGage/Mob/5220003#";
var 蘑菇王图标 = "#fUI/UIWindow.img/MobGage/Mob/6130101#";
var 多尔图标 = "#fUI/UIWindow.img/MobGage/Mob/6220000#";
var 僵尸蘑菇图标 = "#fUI/UIWindow.img/MobGage/Mob/6300005#";
var 朱诺图标 = "#fUI/UIWindow.img/MobGage/Mob/6220001#";
var 九尾狐图标 = "#fUI/UIWindow.img/MobGage/Mob/7220001#";
var 肯德熊图标 = "#fUI/UIWindow.img/MobGage/Mob/7220000#";
var 妖怪禅师图标 = "#fUI/UIWindow.img/MobGage/Mob/7220002#";
var 蝙蝠怪图标 = "#fUI/UIWindow.img/MobGage/Mob/8130100#";
var 艾利杰图标 = "#fUI/UIWindow.img/MobGage/Mob/8220000#";
var 吉米啦图标 = "#fUI/UIWindow.img/MobGage/Mob/8220002#";
var 蓝蘑菇王图标 = "#fUI/UIWindow.img/MobGage/Mob/9400205#";
var 天鹰图标 = "#fUI/UIWindow.img/MobGage/Mob/8180001#";
var 火焰龙图标 = "#fUI/UIWindow.img/MobGage/Mob/8180000#";
var 大海兽图标 = "#fUI/UIWindow.img/MobGage/Mob/8220003#";
var 多多图标 = "#fUI/UIWindow.img/MobGage/Mob/8220004#";
var 独角兽图标 = "#fUI/UIWindow.img/MobGage/Mob/8220005#";
var 雷卡图标 = "#fUI/UIWindow.img/MobGage/Mob/8220006#";
var 鱼王图标 = "#fUI/UIWindow.img/MobGage/Mob/8510000#";
var 树精图标 = "#fUI/UIWindow.img/MobGage/Mob/9420521#";
var 妖僧图标 = "#fUI/UIWindow.img/MobGage/Mob/9600025#";
var 品克宾图标 = "#fUI/UIWindow.img/MobGage/Mob/8820001#";
var 黑龙图标 = "#fUI/UIWindow.img/MobGage/Mob/8810018#";
var 闹钟图标 = "#fUI/UIWindow.img/MobGage/Mob/8500001#";
var 熊狮图标 = "#fUI/UIWindow.img/MobGage/Mob/9420542#";
var 扎昆图标 = "#fUI/UIWindow.img/MobGage/Mob/8800001#";

var 提示 = "#fUI/CN_Chat/ChattingRoom/BtVolUp/0/normal/0#";
var 分割线 = "#fUI/Login.img/WorldSelect/channel/chgauge1#";
var status = -1;
var selectedBoss = -1;
var currentChannel; // 当前查看的频道
var BOSS图标 = [
    蜗牛王图标, 树妖王图标, 大宇图标, 歇尔夫图标, 战甲鱼图标, 浮士德图标, 冰海螺图标, 提莫图标, 蘑菇王图标, 多尔图标,
    僵尸蘑菇图标, 朱诺图标, 九尾狐图标, 肯德熊图标, 妖怪禅师图标, 蝙蝠怪图标, 艾利杰图标, 吉米啦图标, 蓝蘑菇王图标, 天鹰图标,
    火焰龙图标, 大海兽图标, 多多图标, 独角兽图标, 雷卡图标, 鱼王图标, 
	树精图标, 妖僧图标, 闹钟图标, 熊狮图标, 扎昆图标, 黑龙图标, 品克宾图标, 
];

var bossData = [
    // [名称, 地图ID, 等级要求, 费用, 怪物ID, 图标变量名]
    ["[蜗 牛 王]", 104000400, 20, 100, 2220000, 蜗牛王图标],
    ["[树 妖 王]", 101030404, 35, 100, 3220000, 树妖王图标],
    ["[ 大  宇 ]", 260010201, 38, 100, 3220001, 大宇图标],
    ["[歇 尔 夫]", 230020100, 45, 100, 4220000, 歇尔夫图标],
    ["[战 甲 鱼]", 221020701, 47, 100, 4130103, 战甲鱼图标],
    ["[浮 士 德]", 100040106, 50, 100, 5220002, 浮士德图标],
    ["[冰 海 螺]", 110040000, 55, 100, 5220001, 冰海螺图标],
    ["[ 提  莫 ]", 220050100, 59, 100, 5220003, 提莫图标],
    ["[蘑 菇 王]", 100000005, 60, 100, 6130101, 蘑菇王图标],
    ["[ 多  尔 ]", 107000300, 65, 100, 6220000, 多尔图标],
    ["[僵尸蘑菇]", 105070002, 65, 100, 6300005, 僵尸蘑菇图标],
    ["[ 朱  诺 ]", 221040301, 65, 100, 6220001, 朱诺图标],
    ["[九 尾 狐]", 222010310, 70, 100, 7220001, 九尾狐图标],
    ["[肯 德 熊]", 250010304, 71, 100, 7220000, 肯德熊图标],
    ["[妖怪禅师]", 250010503, 77, 100, 7220002, 妖怪禅师图标],
    ["[蝙 蝠 怪]", 105090900, 80, 100, 8130100, 蝙蝠怪图标],
    ["[艾 利 杰]", 200010300, 83, 100, 8220000, 艾利杰图标],
	["[吉 米 啦]", 261030000, 83, 100, 8220002, 吉米啦图标],
    ["[蓝蘑菇王]", 800010100, 90, 100, 9400205, 蓝蘑菇王图标],
    ["[ 天  鹰 ]", 240020101, 105, 100, 8180001, 天鹰图标],
    ["[火 焰 龙]", 240020401, 105, 100, 8180000, 火焰龙图标],
    ["[大 海 兽]", 240040401, 120, 100, 8220003, 大海兽图标],
    ["[ 多  多 ]", 270010500, 121, 100, 8220004, 多多图标],
    ["[独 角 兽]", 270020500, 131, 100, 8220005, 独角兽图标],
    ["[ 雷  卡 ]", 270030500, 141, 100, 8220006, 雷卡图标],
	["[ 鱼  王 ]", 230040420, 141, 100, 8510000, 鱼王图标],
	
	["[ 树  精 ]", 541020700, 141, 100, 9420521, 树精图标],
	["[ 妖  僧 ]", 702070400, 141, 100, 9600025, 妖僧图标],
	["[ 闹  钟 ]", 220080000, 141, 100, 8500001, 闹钟图标],
	["[ 熊  狮 ]", 551030100, 141, 100, 9420542, 熊狮图标],
	["[ 扎  昆 ]", 211042300, 141, 100, 8800001, 扎昆图标],
	["[ 黑  龙 ]", 240050400, 141, 100, 8810018, 黑龙图标],
	["[品 克 宾]", 270050000, 141, 100, 8820001, 品克宾图标]
	
];

// 存储各频道BOSS状态
var channelBossStatus = {};

// 频道列表
var channelList = [
    { id: 1, name: "频道1" },
    { id: 2, name: "频道2" },
    { id: 3, name: "频道3" },
    { id: 4, name: "频道4" },
	{ id: 5, name: "频道5" },
    { id: 6, name: "频道6" },
    { id: 7, name: "频道7" },
    { id: 8, name: "频道8" }
];

// 定义全局变量存储跨频道传送信息
var pendingBossWarpMap = {};

function start() {
    // 检查是否有待处理的跨频道传送
    var playerId = cm.getPlayer().getId();
    var pendingMapId = pendingBossWarpMap[playerId];

    if (pendingMapId) {
        // 清除传送信息
        delete pendingBossWarpMap[playerId];

        // 执行传送
        cm.warp(pendingMapId, 0);
        cm.dispose();
        return;
    }

    // 默认使用当前频道
    if (!currentChannel) {
        currentChannel = cm.getClient().getChannel();
    }

    // 检查当前频道BOSS状态
    checkBossStatus(currentChannel);

    action(1, 0, 0);
}

// 检查指定频道BOSS是否存活
// 检查指定频道BOSS是否存活
function checkBossStatus(channel) {
    try {
        // 确保状态数组已初始化
        if (!channelBossStatus[channel]) {
            channelBossStatus[channel] = [];
            for (var i = 0; i < bossData.length; i++) {
                channelBossStatus[channel][i] = 0; // 默认为死亡
            }
        }

        // 遍历所有频道和地图，获取真实的BOSS状态
        var xmcserv = Packages.handling.channel.ChannelServer.getAllInstances().iterator();
        while (xmcserv.hasNext()) {
            var xmfwq = xmcserv.next();
            var chnId = xmfwq.getChannel();

            // 只处理要查询的频道
            if (chnId == channel) {
                // 遍历该频道的所有BOSS
                for (var i = 0; i < bossData.length; i++) {
                    try {
                        var mapId = bossData[i][1]; // 地图ID
                        var mobId = bossData[i][4]; // 怪物ID

                        // 获取地图实例
                        var map = xmfwq.getMapFactory().getMap(mapId);
                        if (!map) {
                            // 地图不存在，默认为死亡状态
                            channelBossStatus[channel][i] = 0;
                            continue;
                        }

                        var monsters = map.getAllMonstersThreadsafe();
                        if (!monsters) {
                            // 地图中没有怪物，默认为死亡状态
                            channelBossStatus[channel][i] = 0;
                            continue;
                        }

                        var alive = false;
                        for (var j = 0; j < monsters.size(); j++) {
                            if (monsters.get(j).getId() == mobId) {
                                alive = true;
                                break;
                            }
                        }

                        // 更新BOSS状态
                        channelBossStatus[channel][i] = alive ? 1 : 0;
                    } catch (e) {
                        // 出错时保持原状态不变
                        channelBossStatus[channel][i] = 0; // 默认为死亡状态
                    }
                }
                break; // 找到对应频道后跳出循环
            }
        }
    } catch (e) {
        // 如果处理出错，显示错误信息并保持原状态不变
        for (var i = 0; i < bossData.length; i++) {
            channelBossStatus[channel][i] = 0; // 默认为死亡状态
        }
    }
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            cm.dispose();
            return;
        }
        status--;
    }

    if (status == 0) {
        // 默认使用当前频道
        if (!currentChannel) {
            currentChannel = cm.getClient().getChannel();
        }

        // 更新当前查看频道的BOSS状态
        checkBossStatus(currentChannel);

        var text = "#d\r\n";
        text += " #k┏━#rBOSS传送系统#k━━━━━━━━━━━━━━━━┓\r\n";
        text += "\t#d" + 提示 + ":欢迎来到 [#rBOSS传送#d] 当前所在频道:#r" + cm.getClient().getChannel() + "#k\r\n";
		text += "\t#d" + 提示 + ":#r会员专享#d可点击下列频道查看BOSS刷新情况#k\r\n";
        // 频道选择
        text += "\t";
        for (var i = 0; i < channelList.length; i++) {
            if (currentChannel == channelList[i].id) {
                text += "#L" + (1000 + channelList[i].id) + "#[#r" + channelList[i].name + "#d]#l";
            } else {
                text += "#L" + (1000 + channelList[i].id) + "#[#b" + channelList[i].name + "#d]#l";
            }
            if ((i + 1) % 4 == 0) {
                text += "\r\n\t";
            }
        }
		text += "\r\n #k┗━━━━━━━━━━━━━━━━━━━━━━━┛#k\r\n";
		text += "\t#dBOSS名字为#g绿色#d代表#g已刷新#d;#d名字为#r红色#d代表#r未刷新#d;#k\r\n";
    //    text += 分割线 + 分割线 + 分割线 + "\r\n\r\n";

        // BOSS列表
        for (var i = 0; i < bossData.length; i++) {
            if (i > 0 && i % 3 == 0) {
                text += "\r\n\r\n";
            }

            var boss = bossData[i];
            var isAlive = channelBossStatus[currentChannel][i];
            var statusText = isAlive ? "#g" + boss[0] + "#k" : "#r" + boss[0] + "#k";

            // 使用BOSS图标和状态
            text += "#L" + i + "#" + BOSS图标[i] + statusText + "#l";
        }

     //   text += "\r\n\r\n #k┗━━━━━━━━━━━━━━━━━━━━━━━┛#k\r\n";

        cm.sendSimple(text);
    } else if (status == 1) {
        // 处理频道切换
        if (selection >= 1000 && selection <= 1008) {
            currentChannel = selection - 1000;
            checkBossStatus(currentChannel);
            status = -1;
            action(1, 0, 0);
            return;
        }

        // 返回上一页
        if (selection == 999) {
            cm.dispose();
            return;
        }

        // 选择BOSS
        selectedBoss = selection;
        var boss = bossData[selectedBoss];
        var isAlive = channelBossStatus[currentChannel][selectedBoss];

        var text = "#d\r\n";
        text += " #k┏━#r" + boss[0] + " 详细信息#k━━━━━━━━━━━━┓\r\n\r\n";

        text += "\t#dBOSS名称：#r" + boss[0] + "#k\r\n";
        text += "\t#d等级要求：#b" + boss[2] + "#k 级\r\n";
        text += "\t#d传送费用：#b" + boss[3] + "#k 抵用券\r\n";
        text += "\t#d当前状态：" + (isAlive ? "#g存活#k" : "#r死亡#k") + "\r\n";
        text += "\t#d显示频道：#b" + currentChannel + "#k\r\n";
        text += "\t#d当前频道：#b" + cm.getClient().getChannel() + "#k\r\n\r\n";

        if (cm.getPlayer().getLevel() < boss[2]) {
            text += "\t#r您的等级不足，无法传送！#k\r\n";
            cm.sendOk(text);
            cm.dispose();
            return;
        }

        if (cm.getPlayer().getCSPoints(2) < boss[3]) {
            text += "\t#r您的抵用券不足，无法传送！#k\r\n";
            cm.sendOk(text);
            cm.dispose();
            return;
        }

        if (!isAlive) {
            text += "\t#r该BOSS已经被击杀，是否仍然传送？#k\r\n\r\n";
        }

        if (currentChannel != cm.getClient().getChannel()) {
            text += "\t您当前在 #r" + cm.getClient().getChannel() + "#k 频道，将会切换到 #b" + currentChannel + "#k 频道。\r\n";
        }

        text += "\t是否花费 #r" + boss[3] + "#k 抵用券传送到#b" + boss[0] + "#k？\r\n";
        text += " #k┗━━━━━━━━━━━━━━━━━━━━━━━┛#k\r\n";

        cm.sendYesNo(text);
    } else if (status == 2) {
        var boss = bossData[selectedBoss];
    // 如果目标频道是8，检测背包中是否有道具ID：5220002
    if (currentChannel == 8) {
        if (!cm.haveItem(5220002)) {
            cm.sendOk("您背包中没有#b#v5220002##z5220002##k，无法传送！");
            cm.dispose();
            return;
        }
    }
        // 扣除费用
        cm.gainDY(-boss[3]);

        // 如果需要切换频道
        if (currentChannel != cm.getClient().getChannel()) {
            // 先记录玩家的传送信息
            var playerId = cm.getPlayer().getId();
            pendingBossWarpMap[playerId] = boss[1]; // 记录目标地图ID

            // 切换频道
			cm.warp(boss[1], 0);
            cm.getPlayer().changeChannel(currentChannel);
            cm.dispose();
        } else {
            // 如果不需要切换频道，直接传送
            cm.warp(boss[1], 0);
            cm.dispose();
        }
    }
}
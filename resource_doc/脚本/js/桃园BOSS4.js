var cishu = 1; // 总共挑战次数
var jdHP = 5000000000000;
var zdHP = 200000;
var knHP = 2000000;
var cnHP = 20000000;
var 蓝色箭头 = "#fUI/UIWindow/Quest/icon2/7#";
var sj;
var 皇冠白 = "#fUI/GuildMark/Mark/Etc/00009004/15#";
var 彩虹1 = "#fUI/ChatBalloon/122/n#";
var 彩虹上1 = "#fUI/ChatBalloon/122/ne#";
var 彩虹上2 = "#fUI/ChatBalloon/122/nw#";
var 彩1 = "#fUI/ChatBalloon/122/e#";
var 彩2 = "#fUI/ChatBalloon/122/w#";
var status;
var minLevel = 50;
var maxLevel = 255;
var minPartySize = 1;
var maxPartySize = 1;
var 彩虹下 = "#fUI/ChatBalloon/122/s#";
var 彩虹下1 = "#fUI/ChatBalloon/122/se#";
var 彩虹下2 = "#fUI/ChatBalloon/122/sw#";
var 彩虹中 = "#fUI/ChatBalloon/122/head#";
var 梅花 = "#fUI/GuildMark/Mark/Animal/00002008/14#";
var 蝴蝶 = "#fUI/GuildMark/Mark/Animal/00002020/14#";
var fubendt = 811000400; // 定义副本地图ID变量

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1)
        status++;
    else {
        cm.dispose();
        return;
    }
    if(cm.判断物品(3700069)){
        var 次数 = 6;
    } else if(cm.判断物品(3700070)){
        var 次数 = 3;
    } else {
        var 次数 = 1;
    }
    if (status == 0) {
        var text = "";
        text += "                  #e#k" + 皇冠白 + " #r团体BOSS挑战#n#k " + 皇冠白 + "\r\n\r\n";
        text += "       " + 彩虹上2 + "" + 彩虹1 + 彩虹1 + 彩虹1 + 彩虹1 + 彩虹1 + 彩虹1 + 彩虹1 + 彩虹1 + 彩虹中 + 彩虹1 + 彩虹1 + 彩虹1 + 彩虹1 + 彩虹1 + 彩虹1 + 彩虹1 + 彩虹1 + "" + 彩虹上1 + "\r\n";
        text += "        [挑战对象]：[马里蒂亚]\r\n";
        text += "        [血量]：[5万E]\r\n";
        //text += "        [BOSS难度]：[不一般]  [队伍]：[1-6人]\r\n";
		text += "        [BOSS难度]：[不一般]  [单人挑战]：[1人]\r\n";
        text += "        [限时30分钟]\r\n";
        text += "        #b#k[每天进入次数]：#r" + cm.getPlayer().getBossLog('马里蒂亚') + "#k/"+次数+"#l [等级要求]：120\r\n";
        text += "       " + 彩虹下2 + "" + 彩虹下 + 彩虹下 + 彩虹下 + 彩虹下 + 彩虹下 + 彩虹下 + 彩虹下 + 彩虹下 + 彩虹下 + 彩虹下 + 彩虹下 + 彩虹下 + 彩虹下 + 彩虹下 + 彩虹下 + 彩虹下 + 彩虹下 + 彩虹下 + "" + 彩虹下1 + "\r\n";
        text += "         #L0##r" + 蝴蝶 + " #k普通挑战#k（#v4031227#x5 + #v3994789#x1）" + 蝴蝶 + "#l\r\n";
        text += "         #L1##r" + 蝴蝶 + " #k高级挑战#k（#v3700070#x1，可挑战3次）" + 蝴蝶 + "#l\r\n";
        text += "         #L2##r" + 蝴蝶 + " #k特级挑战#k（#v3700069#x1，可挑战6次）" + 蝴蝶 + "#l\r\n";

        cm.sendSimple(text);
    } else if (status == 1) {
        if (cm.getPlayerCount(fubendt) > 0) {
            cm.sendOk("有人正在挑战，请稍等一会儿再来");
            cm.dispose();
            return;
        }

        if (cm.getParty() == null) {
            cm.sendOk("你没有队伍无法进入！");
            cm.dispose();
            return;
        }

        if (!cm.isLeader()) {
            cm.sendOk("请让你的队长和我说话~");
            cm.dispose();
            return;
        }

        var party = cm.getParty().getMembers();
        var inMap = cm.partyMembersInMap();
        var levelValid = 0;
        for (var i = 0; i < party.size(); i++) {
            if (party.get(i).getLevel() >= minLevel && party.get(i).getLevel() <= maxLevel)
                levelValid++;
        }

        if (inMap < minPartySize || inMap > maxPartySize) {
            cm.sendOk("你的队伍人数不足" + minPartySize + "人.请把你的队伍人员召集到BOSS大厅在进入副本.");
            cm.dispose();
            return;
        }

        if (levelValid != inMap) {
            cm.sendOk("请确保你的队伍人员最小等级在 " + minLevel + " 和 " + maxLevel + "之间.或者你有队员处于离线状态,请退出下线的队员");
            cm.dispose();
            return;
        }

        if (cm.getPlayerCount(811000400) <= 0) {
            // 根据选择的挑战类型处理
            switch (selection) {
                case 0: // 普通挑战
                    if (cm.getPlayer().getBossLog("马里蒂亚") >= 1) {
                        cm.sendOk("你今天的普通挑战次数已用完！");
                        cm.dispose();
                        return;
                    }
                    if (!cm.判断团队物品(4031227, 5) || !cm.判断团队物品(3994789, 1)) {
                        cm.sendOk("队伍中有人#v4031227#或#v3994789#不足，无法进入");
                        cm.dispose();
                        return;
                    }
                    cm.收团队道具(4031227, -5);
                    cm.收团队道具(3994789, -1);
                    cm.给团队每日("马里蒂亚");
                    break;

                case 1: // 高级挑战
                    if (cm.getPlayer().getBossLog("马里蒂亚") >= 3) {
                        cm.sendOk("你今天的高级挑战次数已用完！");
                        cm.dispose();
                        return;
                    }
                    if (!cm.判断团队物品(3700070, 1)|| !cm.判断团队物品(2381048, 1)|| !cm.判断团队物品(2381048, 1)) {
                        cm.sendOk("队伍中有人道具不足，无法进入");
                        cm.dispose();
                        return;
                    }
                    cm.收团队道具(4031227, -5);
                    cm.收团队道具(2381050, -1);                    
                    cm.给团队每日("马里蒂亚"); // 增加2次挑战次数
                    break;

                case 2: // 特级挑战
                    if (cm.getPlayer().getBossLog("马里蒂亚") >= 6) {
                        cm.sendOk("你今天的特级挑战次数已用完！");
                        cm.dispose();
                        return;
                    }
                    if (!cm.判断团队物品(3700069, 1)|| !cm.判断团队物品(2381048, 1)|| !cm.判断团队物品(2381048, 1)) {
                        cm.sendOk("队伍中有人道具不足，无法进入");
                        cm.dispose();
                        return;
                    }
                    cm.收团队道具(4031227, -5);
                    cm.收团队道具(2381050, -1);                    
                    cm.给团队每日("马里蒂亚"); // 增加3次挑战次数
                    break;

                default:
                    cm.sendOk("无效选择");
                    cm.dispose();
                    return;
            }
			cm.getPlayer().getMap().removeDrops();
            cm.warpParty(811000400, 0);
            cm.killAllMob(); // 清理副本怪物
            cm.getPlayer().startMapTimeLimitTask(1800, cm.getChannelServer().getMapFactory().getMap(910000000));
            cm.spawnMobOnMap(9602112,1,-26, 146, 811000400, jdHP);
            cm.喇叭(1, "困难BOSS：[" + cm.getPlayer().getName() + "]带领他的队伍前往挑战桃园BOSS了！");
        } else {
            cm.sendOk("请稍等...任务正在进行中.");
        }
        cm.dispose();
    }
}
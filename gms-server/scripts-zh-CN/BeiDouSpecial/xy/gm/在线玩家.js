/*
 ZEVMS冒险岛(079)游戏服务端
 脚本：巡查面板
 */

var 心 = "#fUI/GuildMark.img/Mark/Etc/00009001/14#";
var xx;
var xxx;

function start() {
    status = -1;
    action(1, 0, 0);
}

var nowchannel;
var nowmap;
var nowplayer;

function action(mode, type, selection) {
    if (status == 0 && mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
    // 检查是否为管理员
    if (cm.getPlayer().getGMLevel() < 100) {
        cm.sendOk("你没有权限使用这个功能！");
        cm.dispose();
        return;
    }
    // 开始
    if (status == 0) {
        var selStr = "    " + 心 + " " + 心 + "  " + 心 + "  " + 心 + " #r#e < 巡查面板 > #k#n " + 心 + "  " + 心 + "  " + 心 + " " + 心 + "\r\n\r\n";
        var ChannelAll = Packages.handling.channel.ChannelServer.getAllInstances().toArray();
        var onlineCount = 0; // 在线人数计数器

        for (var cs = 0; cs < ChannelAll.length; cs++) {
            var Player = ChannelAll[cs].getPlayerStorage().getAllCharacters().toArray();
            onlineCount += Player.length; // 累加在线人数
            for (var x = 0; x < Player.length; x++) {
                selStr += "#L" + Player[x].getId() + "# 玩家:#b" + Player[x].getName() + "#k 等级:#r" + Player[x].getLevel() + "#k 频道:#r" + Player[x].getClient().getChannel() + "#k 地图:#b" + Player[x].getMap().getMapName() + "#k#l\r\n";
            }
        }
        selStr = "\t\t\t\t\t#r#e当前在线人数：" + onlineCount + "#k\r\n#n" + selStr; // 在列表顶部显示在线人数
        cm.sendSimple(selStr);
    } else if (status == 1) {
        xx = selection;
        var xdx = 0;
        var ChannelAll = Packages.handling.channel.ChannelServer.getAllInstances().toArray();
        for (var cs = 0; cs < ChannelAll.length; cs++) {
            if (xdx == 1) break;
            var PlayerList = ChannelAll[cs].getPlayerStorage().getAllCharacters().toArray();
            for (var x = 0; x < PlayerList.length; x++) {
                Player = PlayerList[x];
                if (Player.getId() == xx) {
                    nowplayer = Player;
                    nowchannel = Player.getClient().getChannel();
                    nowmap = Player.getMapId();
                    xdx = 1;
                    break;
                }
            }
        }
        var selStr2 = "    " + 心 + " " + 心 + "  " + 心 + "  " + 心 + " #r#e < 巡查面板 > #k#n " + 心 + "  " + 心 + "  " + 心 + " " + 心 + "\r\n\r\n";
        selStr2 += "\t\t#d玩家 I D:  #r" + Player.getId() + "#k\r\n";
        selStr2 += "\t\t#d玩家名字:  #r" + Player.getName() + "#k\r\n";
        selStr2 += "\t\t#d玩家等级:  #r" + Player.getLevel() + "#k\r\n";
        selStr2 += "\t\t#d玩家职业:  #r" + Player.getJob() + "#k\r\n";
        selStr2 += "\t\t#d入狱次数:  #r" + Player.getOneTimeLogcs("入狱次数") + "#k\r\n";
        selStr2 += "\t\t#d所在地图:  #r" + Player.getMap().getMapName() + "#k\r\n\r\n";
        selStr2 += "\t\t#b#L0#[跟踪玩家]#l \t\t\t#L5#[加满技能]#l\r\n\r\n";
        selStr2 += "\t\t#k#L2#[关小黑屋]#l \t\t\t#L77#[解封黑屋]#l \r\n\r\n";
        selStr2 += "\t\t#r#L3#[封停账号]#l \t\t\t#L4#[踢出下线]#l\r\n\r\n";
        selStr2 += "\t\t\t\t\t\t\t\t  #b#L1#[返回]#l\r\n\r\n";
        selStr2 += "\r\n\r\n \t\t注释: 如果需要解封, !解封 +空格 游戏名字 #l";
        cm.sendSimple(selStr2);
    } else if (status == 2) {
        if (selection == 0) {
            cm.getPlayer().changeMap(nowmap);
            if (cm.getPlayer().getClient().getChannel() != nowchannel) {
                cm.getPlayer().changeChannel(nowchannel);
            }
            cm.dispose();
            return;
        } else if (selection == 1) {
            status = 0;
            var selStr = "    " + 心 + " " + 心 + "  " + 心 + "  " + 心 + " #r#e < 巡查面板 > #k#n " + 心 + "  " + 心 + "  " + 心 + " " + 心 + "\r\n\r\n";
            var ChannelAll = Packages.handling.channel.ChannelServer.getAllInstances().toArray();
            var onlineCount = 0; // 在线人数计数器
            for (var cs = 0; cs < ChannelAll.length; cs++) {
                var Player = ChannelAll[cs].getPlayerStorage().getAllCharacters().toArray();
                onlineCount += Player.length; // 累加在线人数
                for (var x = 0; x < Player.length; x++) {
                    selStr += "#L" + Player[x].getId() + "# 玩家:#b" + Player[x].getName() + "#k 等级:#r" + Player[x].getLevel() + "#k 频道:#r" + Player[x].getClient().getChannel() + "#k 地图:#b" + Player[x].getMap().getMapName() + "#k#l\r\n";
                }
            }
            selStr = "#b在线人数: #r" + onlineCount + "#k\r\n" + selStr; // 在列表顶部显示在线人数
            cm.sendSimple(selStr);
        } else if (selection == 2) {
            nowplayer.changeMap(180000001);
            nowplayer.gainOneTimeLogcs("监狱禁令");
            nowplayer.gainOneTimeLogcs("入狱次数");
            nowplayer.dropMessage(-1, "由于你不规范操作被拉进了小黑屋。");
            nowplayer.dropMessage(-1, "由于你不规范操作被拉进了小黑屋。");
            nowplayer.dropMessage(-1, "由于你不规范操作被拉进了小黑屋。");
            cm.dispose();
            return;
        } else if (selection == 77) {
            nowplayer.gainOneTimeLogcs("监狱禁令", -nowplayer.getOneTimeLogcs("监狱禁令"));
            nowplayer.changeMap(910000000);
            nowplayer.dropMessage(-1, "解封监狱了 ");
            nowplayer.dropMessage(1, "解封监狱了 ");
            cm.dispose();
            return;
        } else if (selection == 3) {
            var selStr2 = "";
            selStr2 += "\t\t#d玩家ID:  #r" + nowplayer.getId() + "#k\r\n";
            selStr2 += "\t\t#d玩家名字:  #r" + nowplayer.getName() + "#k\r\n";
            selStr2 += "\t\t#d玩家等级:  #r" + nowplayer.getLevel() + "#k\r\n";
            selStr2 += "\t\t#d玩家职业:  #r" + nowplayer.getJob() + "#k\r\n";
            selStr2 += "\t\t#d所在地图:  #r" + nowplayer.getMap().getMapName() + "#k\r\n\r\n";
            cm.sendYesNo("目标玩家:\r\n" + selStr2 + "你确定要封禁该玩家吗?");
        } else if (selection == 4) {
            // 踢玩家下线
            nowplayer.getClient().getSession().close();
            nowplayer.dropMessage(-1, "你被管理员踢下线了！");
            cm.sendOk("玩家 " + nowplayer.getName() + " 已被踢下线。");
            cm.dispose();
            return;
        } else if (selection == 5) {
            // 满技能
            nowplayer.maxSkillsByJob();
            nowplayer.dropMessage(1, "管理员已为你满技能！");
            cm.sendOk("已为玩家 " + nowplayer.getName() + " 满技能。");
            cm.dispose();
            return;
        }
    } else if (status == 3) {
        if (cm.getPlayer().getGMLevel() != 100) {
            cm.sendOk("不是超级管理员不能用这个功能!");
            status = -1;
        } else {
            cm.封号(nowplayer.getName());
            cm.sendOk("已经成功将玩家[" + nowplayer.getName() + "]封号下线!");
            status = -1;
        }
    }
}
var 心 = "#fUI/GuildMark.img/Mark/Etc/00009001/14#";
var xx;
var xxx;

function start() {
    status = -1;
    action(1, 0, 0);
    //cm.spawnMobOnMap(100100,1,-812, 34,910000000,3000000000);
}

var nowmap = 0, nowchannel = 0;
var now

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
    //开始
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
            if (xdx == 1) {
                break;
            }
            var PlayerList = ChannelAll[cs].getPlayerStorage().getAllCharacters().toArray();
            for (var x = 0; x < PlayerList.length; x++) {
                Player = PlayerList[x];
                if (Player.getId() == xx) {
                    nowchannel = Player.getClient().getChannel();
                    nowmap = Player.getMap();
                    xdx = 1
                    break;
                }
            }

        }
        var selStr2 = "    " + 心 + " " + 心 + "  " + 心 + "  " + 心 + " #r#e < 巡查面板 > #k#n " + 心 + "  " + 心 + "  " + 心 + " " + 心 + "\r\n\r\n";
        selStr2 += "\t\t#dXXID:  #r" + selection + "#k\r\n";
        selStr2 += "\t\t#d玩家ID:  #r" + Player.getId() + "#k\r\n";
        selStr2 += "\t\t#d玩家名字:  #r" + Player.getName() + "#k\r\n";
        selStr2 += "\t\t#d玩家等级:  #r" + Player.getLevel() + "#k\r\n";
        selStr2 += "\t\t#d玩家职业:  #r" + Player.getJob() + "#k\r\n";
        selStr2 += "\t\t#d所在地图:  #r" + Player.getMap().getMapName() + "#k\r\n\r\n";
        selStr2 += "\t\t  #b#L0#[跟踪]#l #L1#[返回]#l "
        cm.sendSimple(selStr2);
    } else if (status == 2) {
        if (selection == 0) {
            cm.getPlayer().changeMap(nowmap, nowmap.getPortal(0));
            cm.getPlayer().changeChannel(nowchannel);
            cm.dispose();
        } else if (selection == 1) {
            status = 0;
            var selStr = "    " + 心 + " " + 心 + "  " + 心 + "  " + 心 + " #r#e < 巡查面板 > #k#n " + 心 + "  " + 心 + "  " + 心 + " " + 心 + "\r\n\r\n";
            var ChannelAll = Packages.handling.channel.ChannelServer.getAllInstances().toArray();
            for (var cs = 0; cs < ChannelAll.length; cs++) {
                var Player = ChannelAll[cs].getPlayerStorage().getAllCharacters().toArray();
                for (var x = 0; x < Player.length; x++) {
                    selStr += "#L" + Player[x].getId() + "# 玩家:#b" + Player[x].getName() + "#k 等级:#r" + Player[x].getLevel() + "#k 频道:#r" + Player[x].getClient().getChannel() + "#k 地图:#b" + Player[x].getMap().getMapName() + "#k#l\r\n";
                }
            }
            ndSimple(selStr);
        } else {
            cm.sendOk("暂未开启");
            cm.dispose();
            return;
        }
    }
}
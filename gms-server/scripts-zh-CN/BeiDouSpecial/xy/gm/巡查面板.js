/**
 * 巡查面板 — GM管理工具：查看在线玩家、跟踪、封禁、踢下线
 */
var Server = Java.type("org.gms.net.server.Server");

function start() {
    status = -1;
    action(1, 0, 0);
}

var selectedPlayer;
var selectedChannel;

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

    // GM权限检查
    if (cm.getPlayer().getGMLevel() < 1) {
        cm.sendOk("你没有权限使用这个功能！");
        cm.dispose();
        return;
    }

    if (status == 0) {
        // 在线玩家列表
        var text = "#e巡查面板#n\r\n\r\n";
        var worlds = Server.getInstance().getWorlds();
        var onlineCount = 0;
        for (var w = 0; w < worlds.size(); w++) {
            var world = worlds.get(w);
            var channels = world.getChannels();
            for (var c = 0; c < channels.size(); c++) {
                var channel = channels.get(c);
                var players = channel.getPlayerStorage().getAllCharacters();
                for (var i = 0; i < players.size(); i++) {
                    var player = players.get(i);
                    onlineCount++;
                    text += "#L" + player.getId() + "# 玩家:#b" + player.getName()
                        + "#k 等级:#r" + player.getLevel()
                        + "#k 频道:#r" + channel.getId()
                        + "#k 地图:#b" + player.getMap().getMapName() + "#k#l\r\n";
                }
            }
        }
        text = "#r当前在线人数：" + onlineCount + "#k\r\n" + text;
        if (onlineCount == 0) {
            text += "\r\n暂无在线玩家";
        }
        cm.sendSimple(text);

    } else if (status == 1) {
        // 查找选中的玩家
        var targetId = selection;
        var found = false;
        var worlds = Server.getInstance().getWorlds();
        for (var w = 0; w < worlds.size() && !found; w++) {
            var world = worlds.get(w);
            var channels = world.getChannels();
            for (var c = 0; c < channels.size() && !found; c++) {
                var channel = channels.get(c);
                var players = channel.getPlayerStorage().getAllCharacters();
                for (var i = 0; i < players.size() && !found; i++) {
                    var player = players.get(i);
                    if (player.getId() == targetId) {
                        selectedPlayer = player;
                        selectedChannel = channel.getId();
                        found = true;
                    }
                }
            }
        }

        if (!found) {
            cm.sendOk("该玩家已下线。");
            cm.dispose();
            return;
        }

        var text = "#e巡查面板 - 玩家详情#n\r\n\r\n";
        text += "玩家ID: #r" + selectedPlayer.getId() + "#k\r\n";
        text += "玩家名字: #r" + selectedPlayer.getName() + "#k\r\n";
        text += "玩家等级: #r" + selectedPlayer.getLevel() + "#k\r\n";
        text += "玩家职业: #r" + selectedPlayer.getJob() + "#k\r\n";
        text += "所在地图: #r" + selectedPlayer.getMap().getMapName() + "#k\r\n\r\n";
        text += "#L0##b[跟踪玩家]#l      #L1#[返回列表]#l\r\n\r\n";
        text += "#L2##r[踢出下线]#l\r\n";
        cm.sendSimple(text);

    } else if (status == 2) {
        if (selection == 0) {
            // 跟踪玩家
            var targetMap = selectedPlayer.getMapId();
            var targetCh = selectedPlayer.getClient().getChannel();
            cm.getPlayer().changeMap(targetMap);
            if (cm.getPlayer().getClient().getChannel() != targetCh) {
                cm.getPlayer().changeChannel(targetCh);
            }
            cm.sendOk("已跟踪到玩家 #b" + selectedPlayer.getName() + "#k 所在位置。");
            cm.dispose();
        } else if (selection == 1) {
            // 返回列表
            status = -1;
            action(1, 0, 0);
        } else if (selection == 2) {
            // 踢下线
            selectedPlayer.getClient().getSession().close();
            cm.sendOk("玩家 #r" + selectedPlayer.getName() + "#k 已被踢下线。");
            cm.dispose();
        }
    }
}

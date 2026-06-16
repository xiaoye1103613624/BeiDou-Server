/**
 * 在线玩家列表 — 列出所有在线玩家，可选中查看详情及管理操作（跟踪玩家）
 */
var Server = Java.type("org.gms.net.server.Server");
var status = 0;
var selectedPlayer; // 选中的玩家对象
var selectedChannel; // 选中玩家所在频道

function start() {
    status = -1;
    action(1, 0, 0);
}

/**
 * 构建在线玩家列表文本，供多处复用
 * 使用 .toArray() 将 Java 集合转为 JS 数组，避免 GraalJS 下标访问兼容问题
 */
function buildPlayerListText() {
    var text = "#e在线玩家列表#n\r\n\r\n";
    var onlineCount = 0;
    // Server.getInstance().getWorlds() 返回 Java List，通过 toArray 转为 JS 数组安全遍历
    var worlds = Server.getInstance().getWorlds().toArray();
    for (var w = 0; w < worlds.length; w++) {
        var channels = worlds[w].getChannels().toArray();
        for (var c = 0; c < channels.length; c++) {
            var players = channels[c].getPlayerStorage().getAllCharacters().toArray();
            for (var i = 0; i < players.length; i++) {
                var player = players[i];
                onlineCount++;
                text += "#L" + player.getId() + "# 玩家:#b" + player.getName()
                    + "#k 等级:#r" + player.getLevel()
                    + "#k 频道:#r" + channels[c].getId()
                    + "#k 地图:#b" + player.getMap().getMapName() + "#k#l\r\n";
            }
        }
    }
    text = "#r当前在线人数：" + onlineCount + "#k\r\n" + text;
    if (onlineCount == 0) {
        text += "\r\n暂无在线玩家";
    }
    return text;
}

function action(mode, type, selection) {
    // 玩家关闭对话框
    if (mode == -1) {
        cm.dispose();
        return;
    }
    // 玩家点击结束/否
    if (mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    } else {
        status--;
    }

    if (status == 0) {
        // GM权限检查
        if (cm.getPlayer().gmLevel() < 1) {
            cm.sendOk("你没有权限使用这个功能！");
            cm.dispose();
            return;
        }
        cm.sendSimple(buildPlayerListText());

    } else if (status == 1) {
        // 查找选中的玩家
        var targetId = selection;
        var found = false;
        var worlds = Server.getInstance().getWorlds().toArray();
        for (var w = 0; w < worlds.length && !found; w++) {
            var channels = worlds[w].getChannels().toArray();
            for (var c = 0; c < channels.length && !found; c++) {
                var players = channels[c].getPlayerStorage().getAllCharacters().toArray();
                for (var i = 0; i < players.length && !found; i++) {
                    var player = players[i];
                    if (player.getId() == targetId) {
                        selectedPlayer = player;
                        selectedChannel = channels[c].getId();
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

        var text = "#e玩家详情#n\r\n\r\n";
        text += "玩家ID: #r" + selectedPlayer.getId() + "#k\r\n";
        text += "玩家名字: #r" + selectedPlayer.getName() + "#k\r\n";
        text += "玩家等级: #r" + selectedPlayer.getLevel() + "#k\r\n";
        text += "玩家职业: #r" + selectedPlayer.getJob() + "#k\r\n";
        text += "所在地图: #r" + selectedPlayer.getMap().getMapName() + "#k\r\n";
        text += "所在频道: #r" + selectedChannel + "#k\r\n\r\n";
        text += "#L0##b[跟踪玩家]#l\r\n";
        text += "#L1##b[返回列表]#l\r\n";
        cm.sendSimple(text);

    } else if (status == 2) {
        if (selection == 0) {
            // 跟踪玩家：跳转到目标玩家所在地图
            var targetMap = selectedPlayer.getMapId();
            var targetChannel = selectedPlayer.getClient().getChannel();
            cm.getPlayer().changeMap(targetMap);
            if (cm.getPlayer().getClient().getChannel() != targetChannel) {
                cm.getPlayer().changeChannel(targetChannel);
            }
            cm.sendOk("已跟踪到玩家 " + selectedPlayer.getName() + " 所在位置。");
            cm.dispose();
        } else if (selection == 1) {
            // 返回列表
            cm.sendSimple(buildPlayerListText());
        }
    }
}

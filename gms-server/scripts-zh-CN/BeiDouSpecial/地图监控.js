/*
 * ==================
 * 脚本类型: 地图监控NPC (GM专用)
 * 脚本作者：北斗项目组
 * 功能说明：
 *   1. 查看各频道在线玩家分布
 *   2. 查看指定地图的玩家列表和怪物刷新状态
 * ==================
 */

var status = -1;
var Server = Java.type('org.gms.server.Server');

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1) {
        cm.dispose();
        return;
    }
    if (mode === 0 && status === 0) {
        cm.dispose();
        return;
    }

    if (!cm.getPlayer().isGM()) {
        cm.sendOk("该功能仅GM可用。");
        cm.dispose();
        return;
    }

    status++;

    if (status === 0) {
        var text = "#e#b=== 地图监控 ===#k#n\r\n\r\n";

        var worlds = Server.getInstance().getWorlds();
        for (var w = 0; w < worlds.size(); w++) {
            var world = worlds.get(w);
            var channels = world.getChannels();

            text += "#d" + "".padStart(26, "——") + "#k\r\n";
            text += "世界 #b" + world.getId() + "#k  (经验倍率: #r" + world.getExpRate() + "x#k)\r\n";

            for (var c = 0; c < channels.size(); c++) {
                var channel = channels.get(c);
                var playerStorage = channel.getPlayerStorage();
                var playerCount = playerStorage.getSize();
                var allPlayers = playerStorage.getAllCharacters().toArray();

                text += "\r\n  频道 #b" + (c + 1) + "#k: #r" + playerCount + "#k 人在线\r\n";

                if (playerCount > 0) {
                    var mapCounts = {};
                    for (var p = 0; p < allPlayers.length; p++) {
                        var mapId = allPlayers[p].getMapId();
                        if (!mapCounts[mapId]) mapCounts[mapId] = [];
                        mapCounts[mapId].push(allPlayers[p].getName());
                    }

                    var mapKeys = Object.keys(mapCounts).sort();
                    for (var m = 0; m < mapKeys.length; m++) {
                        var mId = mapKeys[m];
                        text += "    地图 #b" + mId + "#k: #r" + mapCounts[mId].length + "#k 人\r\n";
                    }
                }
            }
        }

        cm.sendOk(text);
        cm.dispose();
    }
}

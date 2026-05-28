/*
 * ==================
 * 脚本类型: 世界合服工具 (GM专用)
 * 脚本作者：北斗项目组
 * 功能说明：
 *   1. 查看所有世界/频道信息
 *   2. 显示当前角色所在世界
 * ==================
 */

var status = -1;
var Server = Java.type('org.gms.net.server.Server');

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

    if (status === 0) {
        var player = cm.getPlayer();
        var currentWorld = player.getWorld();
        var currentChannel = player.getClient().getChannel();

        var text = "#e#b=== 世界合服工具 ===#k#n\r\n\r\n";
        text += "#d" + "".padStart(30, "——") + "#k\r\n";
        text += "当前世界：#b" + currentWorld + "#k\r\n";
        text += "当前频道：#b" + currentChannel + "#k\r\n";
        text += "#d" + "".padStart(30, "——") + "#k\r\n\r\n";

        var worlds = Server.getInstance().getWorlds();

        for (var w = 0; w < worlds.size(); w++) {
            var world = worlds.get(w);
            var worldId = world.getId();
            var channels = world.getChannels();
            var playerCount = world.getPlayerStorage().getSize();
            var channelsSize = world.getChannelsSize();

            var marker = (worldId === currentWorld) ? "  #r← 当前#k" : "";
            text += "#b世界 " + worldId + "#k" + marker + "\r\n";
            text += "  频道数：" + channelsSize + " | 在线：" + playerCount + "\r\n";
            text += "  经验：" + world.getExpRate() + "x | 掉率：" + world.getDropRate() + "x\r\n";
            text += "\r\n";
        }

        text += "#d" + "".padStart(30, "——") + "#k\r\n";
        text += "#r合服操作请在后台管理面板执行。#k\r\n";

        cm.sendOk(text);
        cm.dispose();
    }
}

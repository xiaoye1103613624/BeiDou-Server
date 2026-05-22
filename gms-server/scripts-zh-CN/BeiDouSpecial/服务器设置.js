/*
 * ==================
 * 脚本类型: 服务器设置控制面板 (GM专用)
 * 脚本作者：北斗项目组
 * 功能说明：
 *   1. 查看当前服务器倍率设置
 *   2. 快速查看关键配置参数
 * ==================
 */

var status = -1;
var GameConfig = Java.type('org.gms.config.GameConfig');
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

    if (status === 0) {
        var text = "#e#b=== 服务器设置 ===#k#n\r\n\r\n";

        var worlds = Server.getInstance().getWorlds();
        for (var w = 0; w < worlds.size(); w++) {
            var world = worlds.get(w);
            var worldId = world.getId();

            text += "#d" + "".padStart(30, "——") + "#k\r\n";
            text += "世界 #b" + worldId + "#k\r\n\r\n";

            text += "经验倍率：#r" + world.getExpRate() + "x#k\r\n";
            text += "金币倍率：#r" + world.getMesoRate() + "x#k\r\n";
            text += "掉率倍率：#r" + world.getDropRate() + "x#k\r\n";
            text += "Boss掉率：#r" + world.getBossDropRate() + "x#k\r\n";
            text += "\r\n";

            text += "服务器消息：\r\n";
            var msg = world.getServerMessage();
            text += "#b" + (msg == null ? "(无)" : msg) + "#k\r\n";
            text += "\r\n";

            text += "活动消息：\r\n";
            var emsg = world.getEventMessage();
            text += "#b" + (emsg == null ? "(无)" : emsg) + "#k\r\n";
            text += "\r\n";

            text += "总频道数：#b" + world.getChannelsSize() + "#k\r\n";
            text += "在线人数：#b" + world.getPlayerStorage().getSize() + "#k\r\n";
        }

        cm.sendOk(text);
        cm.dispose();
    }
}

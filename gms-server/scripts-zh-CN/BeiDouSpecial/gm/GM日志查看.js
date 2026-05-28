/*
 * ==================
 * 脚本类型: GM日志查看器 (GM专用)
 * 脚本作者：北斗项目组
 * 功能说明：
 *   1. 查看服务端近期活动日志信息
 *   2. 显示服务器运行状态
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
        var text = "#e#b=== GM日志查看器 ===#k#n\r\n\r\n";

        text += "GM角色：#b" + player.getName() + "#k\r\n";
        text += "GM等级：#b" + player.getGMLevel() + "#k\r\n";
        text += "#d" + "".padStart(30, "——") + "#k\r\n\r\n";

        // 显示服务器整体状态
        var worlds = Server.getInstance().getWorlds();
        for (var w = 0; w < worlds.size(); w++) {
            var world = worlds.get(w);
            var channels = world.getChannels();

            text += "世界 #b" + world.getId() + "#k:\r\n";
            for (var c = 0; c < channels.size(); c++) {
                var channel = channels.get(c);
                var pCount = channel.getPlayerStorage().getSize();
                text += "  频道#" + (c + 1) + ": " + pCount + " 人在线";
                if (channel.isRunning()) {
                    text += " #b[运行中]#k\r\n";
                } else {
                    text += " #r[已停止]#k\r\n";
                }
            }
        }

        text += "\r\n#d" + "".padStart(30, "——") + "#k\r\n";
        text += "详细GM命令日志请查询数据库 #bgm_command_log#k 表。\r\n";

        cm.sendOk(text);
        cm.dispose();
    }
}

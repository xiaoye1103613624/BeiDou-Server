// 吸怪管理NPC - 地图级别吸怪管理机器人

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
        return;
    }

    if (status >= 0 && mode == 0) {
        cm.dispose();
        return;
    }

    if (mode == 1) {
        status++;
    } else {
        status--;
    }

    if (status == 0) {
        var map = cm.getMap();
        var mapId = map.getMapId();
        var playerId = cm.getPlayer().getId();
        var playerName = cm.getPlayer().getName();

        // 检查地图吸怪状态
        var xgActive = map.getProperty("xg_active");
        var xgOwner = map.getProperty("xg_owner");
        var xgOwnerName = map.getProperty("xg_owner_name");
        var xgStartTime = map.getProperty("xg_start_time");

        var text = "#e#b=== 吸怪管理系统 ===#k#n\r\n\r\n";
        text += "当前地图: #r[" + mapId + "]#k\r\n";
        text += "你的账号: #b[" + playerName + "]#k\r\n\r\n";

        if (xgActive == "true" && xgOwner != null) {
            // 吸怪已启用
            text += "#r【✓ 吸怪已启用】#k\r\n";
            text += "启用者: #b" + xgOwnerName + "#k\r\n";

            if (xgStartTime != null) {
                var elapsed = (java.lang.System.currentTimeMillis() - parseInt(xgStartTime)) / 1000;
                text += "已启用: #b" + Math.floor(elapsed) + " 秒#k\r\n";
            }
            text += "\r\n";

            if (xgOwner == playerId) {
                // 当前玩家是启用者
                text += "#L1##b【关闭吸怪】#k - 停止此地图的吸怪效果#l\r\n";
                text += "#r说明：#k仅当前启用者可关闭。#n\r\n";
            } else {
                // 其他玩家启用了吸怪
                text += "#b此地图吸怪已被其他玩家启用#k\r\n";
                text += "#r无法再次启用，请等待启用者关闭。#n\r\n";
            }
        } else {
            // 吸怪未启用
            text += "#g【✗ 吸怪未启用】#k\r\n\r\n";
            text += "#L2##b【开启吸怪】#k - 启用此地图的吸怪效果#l\r\n";
            text += "#r说明：#k\r\n";
            text += "  • 启用后，地图掉落物品吸到脚下\r\n";
            text += "  • 同时只有1位玩家可启用吸怪\r\n";
            text += "  • 5分钟无伤害将自动停止\r\n";
            text += "  • 启用者离开地图会自动关闭#n\r\n";
        }

        text += "\r\n#b─────────────────────────#k\r\n";
        text += "#L0##k【返回】#l\r\n";

        cm.sendSimple(text, 3);
    } else if (status == 1) {
        var map = cm.getMap();
        var playerId = cm.getPlayer().getId();
        var playerName = cm.getPlayer().getName();
        var xgOwner = map.getProperty("xg_owner");

        if (selection == 0) {
            cm.dispose();
        } else if (selection == 1) {
            // 关闭吸怪
            cm.getPlayer().stopMobVacuum();
            cm.dropMessage(1, "吸怪效果已关闭。");
            cm.dispose();
        } else if (selection == 2) {
            // 开启吸怪
            var result = cm.getPlayer().startMobVacuum();
            if (result) {
                cm.dropMessage(1, "吸怪效果已启用！");
            } else {
                cm.dropMessage(1, "吸怪已启用，请勿重复启用。");
            }
            cm.dispose();
        }
    }
}

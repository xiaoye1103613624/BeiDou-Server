// 吸怪管理脚本 - 地图级别吸怪管理，同时只允许一个玩家开启吸怪
// 物品脚本上下文变量名为 im（由 ItemScriptManager 注入）

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        im.dispose();
        return;
    }

    if (status >= 0 && mode == 0) {
        im.dispose();
        return;
    }

    if (mode == 1) {
        status++;
    } else {
        status--;
    }

    if (status == 0) {
        var map = im.getMap();
        var mapId = map.getMapId();
        var playerId = im.getPlayer().getId();
        var playerName = im.getPlayer().getName();

        var xgActive = map.getProperty("xg_active");
        var xgOwner = map.getProperty("xg_owner");
        var xgOwnerName = map.getProperty("xg_owner_name");

        var text = "#e#b=== 吸怪管理系统 ===#k#n\r\n\r\n";
        text += "当前地图: #r" + mapId + "#k\r\n";
        text += "你的ID: #b" + playerId + " (" + playerName + ")#k\r\n\r\n";
        text += "#b#e─────────────────────────#k#n\r\n";

        if (xgActive == "true" && xgOwner != null) {
            text += "#r【吸怪已启用】#k\r\n";
            text += "启用者: #b" + xgOwnerName + "#k\r\n\r\n";

            if (xgOwner == playerId) {
                text += "#L1##b【关闭吸怪】#k - 停止此地图的吸怪效果#l\r\n\r\n";
                text += "#r说明：#k关闭后，其他玩家可以开启吸怪。\r\n";
            } else {
                text += "#b此地图吸怪已被其他玩家启用#k\r\n";
                text += "#r无法再次启用吸怪，请稍候...\r\n";
            }
        } else {
            text += "#g【吸怪未启用】#k\r\n\r\n";
            text += "#L2##b【开启吸怪】#k - 启用此地图的吸怪效果#l\r\n\r\n";
            text += "#r说明：#k启用后，此地图的所有掉落物品会吸到你脚下。\r\n";
            text += "#r      如果5分钟内没有造成伤害，吸怪会自动停止。\r\n";
        }

        text += "\r\n#b#e─────────────────────────#k#n\r\n";
        text += "#L0##k【返回】#l\r\n";

        im.sendSimple(text, 3);
    } else if (status == 1) {
        var map = im.getMap();
        var playerId = im.getPlayer().getId();
        var playerName = im.getPlayer().getName();
        var xgOwner = map.getProperty("xg_owner");

        if (selection == 0) {
            im.dispose();
        } else if (selection == 1) {
            if (xgOwner == playerId) {
                im.getPlayer().cancelXg();
                map.setProperty("xg_active", "false");
                map.setProperty("xg_owner", null);
                map.setProperty("xg_owner_name", null);
                map.setProperty("xg_last_damage_time", null);
                im.dropMessage(1, "吸怪效果已关闭。");
                im.dispose();
            } else {
                im.sendOk("你没有权限关闭吸怪。");
                im.dispose();
            }
        } else if (selection == 2) {
            var xgActive = map.getProperty("xg_active");
            if (xgActive == "true") {
                im.sendOk("此地图吸怪已被启用，请稍候...");
                im.dispose();
            } else {
                im.getPlayer().starXg();
                map.setProperty("xg_active", "true");
                map.setProperty("xg_owner", playerId);
                map.setProperty("xg_owner_name", playerName);
                map.setProperty("xg_last_damage_time", java.lang.System.currentTimeMillis());
                im.dropMessage(1, "吸怪效果已启用！（5分钟无伤害会自动停止）");
                im.dispose();
            }
        }
    }
}

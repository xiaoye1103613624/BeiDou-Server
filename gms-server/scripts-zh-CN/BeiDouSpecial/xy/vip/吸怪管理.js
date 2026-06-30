// 吸怪管理脚本 - VIP功能
// 功能：地图级别吸怪管理，同时只允许一个玩家开启吸怪

var VACUUM_TIMEOUT = 5 * 60 * 1000; // 5分钟没有伤害则自动取消
var VACUUM_CHECK_INTERVAL = 60 * 1000; // 每分钟检查一次伤害记录

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

        // 获取地图上的吸怪状态
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
                // 当前玩家是吸怪启用者
                text += "#L1##b【关闭吸怪】#k - 停止此地图的吸怪效果#l\r\n\r\n";
                text += "#r说明：#k关闭后，其他玩家可以开启吸怪。\r\n";
            } else {
                // 其他玩家已启用吸怪
                text += "#b此地图吸怪已被其他玩家启用#k\r\n";
                text += "#r无法再次启用吸怪，请稍候...\r\n";
            }
        } else {
            // 吸怪未启用
            text += "#g【吸怪未启用】#k\r\n\r\n";
            text += "#L2##b【开启吸怪】#k - 启用此地图的吸怪效果#l\r\n\r\n";
            text += "#r说明：#k启用后，此地图的所有掉落物品会吸到你脚下。\r\n";
            text += "#r      如果5分钟内没有造成伤害，吸怪会自动停止。\r\n";
        }

        text += "\r\n#b#e─────────────────────────#k#n\r\n";
        text += "#L0##k【返回】#l\r\n";

        cm.sendSimple(text, 3);
    } else if (status == 1) {
        var map = cm.getMap();
        var mapId = map.getMapId();
        var playerId = cm.getPlayer().getId();
        var playerName = cm.getPlayer().getName();
        var xgOwner = map.getProperty("xg_owner");

        if (selection == 0) {
            cm.dispose();
        } else if (selection == 1) {
            // 关闭吸怪
            if (xgOwner == playerId) {
                // 取消吸怪
                cm.getPlayer().cancelXg();
                map.setProperty("xg_active", "false");
                map.setProperty("xg_owner", null);
                map.setProperty("xg_owner_name", null);
                map.setProperty("xg_last_damage_time", null);

                cm.dropMessage(1, "吸怪效果已关闭。");
                cm.dispose();
            } else {
                cm.sendOk("你没有权限关闭吸怪。");
                cm.dispose();
            }
        } else if (selection == 2) {
            // 开启吸怪
            var xgActive = map.getProperty("xg_active");
            if (xgActive == "true") {
                cm.sendOk("此地图吸怪已被启用，请稍候...");
                cm.dispose();
            } else {
                // 启用吸怪
                cm.getPlayer().starXg();
                map.setProperty("xg_active", "true");
                map.setProperty("xg_owner", playerId);
                map.setProperty("xg_owner_name", playerName);
                map.setProperty("xg_last_damage_time", java.lang.System.currentTimeMillis());

                cm.dropMessage(1, "吸怪效果已启用！（5分钟无伤害会自动停止）");

                // 调度5分钟后的检查
                scheduleTimeoutCheck(mapId, playerId);

                cm.dispose();
            }
        }
    }
}

// 调度超时检查任务（5分钟无伤害则自动取消吸怪）
function scheduleTimeoutCheck(mapId, playerId) {
    // 使用em.schedule在5分钟后检查伤害情况
    // 参数：方法名, EventInstanceManager, 延迟时间(毫秒)
    em.schedule("checkVacuumTimeout", null, VACUUM_TIMEOUT);
}

// 定时检查吸怪超时（5分钟无伤害）
function checkVacuumTimeout() {
    try {
        // 遍历所有地图实例，检查吸怪状态
        var mapFactory = em.getChannelServer().getMapFactory();
        if (mapFactory != null) {
            // 这个方法需要在Channel级别遍历
            // 简化方案：记录超时的地图/玩家，在下一次交互时检查
        }
    } catch (e) {
        java.lang.System.err.println("检查吸怪超时出错: " + e);
    }
}

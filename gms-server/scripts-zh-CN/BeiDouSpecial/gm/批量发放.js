/*
 * ==================
 * 脚本类型: GM批量发放工具
 * 功能说明：
 *   1. 选择目标范围：全体在线玩家 / 当前地图玩家
 *   2. 选择发放类型：点券 / 抵用券 / 信用券 / 金币 / 人气
 *   3. 输入数量并确认发放
 * ==================
 */

var Server = Java.type('org.gms.net.server.Server');
var CashShop = Java.type('org.gms.server.CashShop');

var status = -1;
var awardType = -1;      // 0=点券 1=抵用券 2=信用券 3=金币 4=人气
var awardAmount = 0;
var targetScope = -1;    // 0=全体在线 1=当前地图

var awardTypeNames = ["点券", "抵用券", "信用券", "金币", "人气"];
var awardTypeCashMap = [CashShop.NX_CREDIT, CashShop.MAPLE_POINT, CashShop.NX_PREPAID];

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1) {
        cm.dispose();
        return;
    }
    if (mode === 0) {
        cm.dispose();
        return;
    }

    if (!cm.getPlayer().isGM()) {
        cm.sendOk("该功能仅GM可用。");
        cm.dispose();
        return;
    }

    if (mode === 1) {
        status++;
    }

    // ========================================
    // status 0: 选择目标范围
    // ========================================
    if (status === 0) {
        var text = "#e#b=== 批量发放 ===#k#n\r\n\r\n";
        text += "请选择发放目标：\r\n\r\n";
        text += "#L0##b全体在线玩家#k  向所有频道在线玩家发放#l\r\n";
        text += "#L1##b当前地图玩家#k  仅向当前地图内玩家发放#l\r\n";
        cm.sendSimple(text);

    // ========================================
    // status 1: 选择发放类型
    // ========================================
    } else if (status === 1) {
        targetScope = selection;

        var scopeText = targetScope === 0 ? "全体在线玩家" : "当前地图玩家";
        var text = "#e#b=== 批量发放 ===#k#n\r\n\r\n";
        text += "发放目标：#b" + scopeText + "#k\r\n\r\n";
        text += "请选择发放类型：\r\n";
        text += "#L0##b点券#k (Cash Shop 点券)#l\r\n";
        text += "#L1##b抵用券#k (Maple Points)#l\r\n";
        text += "#L2##b信用券#k (NX Prepaid)#l\r\n";
        text += "#L3##b金币#k (Mesos)#l\r\n";
        text += "#L4##b人气#k (Fame)#l\r\n";
        cm.sendSimple(text);

    // ========================================
    // status 2: 输入数量
    // ========================================
    } else if (status === 2) {
        awardType = selection;

        var typeName = awardTypeNames[awardType];
        var scopeText = targetScope === 0 ? "全体在线玩家" : "当前地图玩家";
        var unit = (awardType <= 2) ? "点" : (awardType === 3 ? "金币" : "点");

        cm.sendGetNumber(
            "发放目标：#b" + scopeText + "#k\r\n发放类型：#b" + typeName + "#k\r\n\r\n请输入每人获得数量：",
            1000,
            1,
            2000000000
        );

    // ========================================
    // status 3: 确认发放
    // ========================================
    } else if (status === 3) {
        awardAmount = selection;

        var typeName = awardTypeNames[awardType];
        var scopeText = targetScope === 0 ? "全体在线玩家" : "当前地图玩家";
        var unit = (awardType <= 2) ? "点" : (awardType === 3 ? "金币" : "点");
        var playerCount = getTargetPlayerCount();

        var text = "#e#b=== 确认发放 ===#k#n\r\n\r\n";
        text += "发放目标：#b" + scopeText + "#k\r\n";
        text += "发放类型：#b" + typeName + "#k\r\n";
        text += "每人获得：#b" + awardAmount.toLocaleString() + " " + unit + "#k\r\n";
        text += "预计影响：#b" + playerCount + "#k 名玩家\r\n\r\n";
        text += "#r确认执行？#k";

        cm.sendYesNo(text);

    // ========================================
    // status 4: 执行发放并显示结果
    // ========================================
    } else if (status === 4) {
        var result = doBatchAward();
        var typeName = awardTypeNames[awardType];
        var unit = (awardType <= 2) ? "点" : (awardType === 3 ? "金币" : "点");

        var text = "#e#b=== 发放完成 ===#k#n\r\n\r\n";
        text += "发放类型：#b" + typeName + "#k\r\n";
        text += "每人获得：#b" + awardAmount.toLocaleString() + " " + unit + "#k\r\n";
        text += "成功：#g" + result.success + "#k 人\r\n";
        if (result.fail > 0) {
            text += "失败：#r" + result.fail + "#k 人\r\n";
        }
        text += "总计影响：#b" + result.total + "#k 人";

        cm.sendOk(text);
        cm.dispose();
    }
}

// ==================== 辅助函数 ====================

function getTargetPlayerCount() {
    if (targetScope === 0) {
        return getOnlinePlayers().length;
    } else {
        return cm.getPlayer().getMap().getAllPlayers().size();
    }
}

function getOnlinePlayers() {
    var allPlayers = [];
    var worlds = Server.getInstance().getWorlds();
    for (var w = 0; w < worlds.size(); w++) {
        var world = worlds.get(w);
        var players = world.getPlayerStorage().getAllCharacters().toArray();
        for (var p = 0; p < players.length; p++) {
            allPlayers.push(players[p]);
        }
    }
    return allPlayers;
}

function doBatchAward() {
    var players;
    if (targetScope === 0) {
        players = getOnlinePlayers();
    } else {
        players = cm.getPlayer().getMap().getAllPlayers().toArray();
    }

    var successCount = 0;
    var failCount = 0;

    for (var i = 0; i < players.length; i++) {
        var p = players[i];
        try {
            if (awardType <= 2) {
                // 点券/抵用券/信用券
                p.getCashShop().gainCash(awardTypeCashMap[awardType], awardAmount);
            } else if (awardType === 3) {
                // 金币
                p.gainMeso(awardAmount, true);
            } else if (awardType === 4) {
                // 人气
                p.gainFame(awardAmount);
            }
            successCount++;
        } catch (e) {
            failCount++;
        }
    }

    // 全服公告
    try {
        var typeName = awardTypeNames[awardType];
        var unit = (awardType <= 2) ? "点" : (awardType === 3 ? "金币" : "点");
        var scopeText = targetScope === 0 ? "全体在线玩家" : "当前地图玩家";
        var gmName = cm.getPlayer().getName();

        var worlds = Server.getInstance().getWorlds();
        for (var w = 0; w < worlds.size(); w++) {
            var PacketCreator = Java.type('org.gms.util.PacketCreator');
            Server.getInstance().broadcastMessage(worlds.get(w).getId(),
                PacketCreator.serverNotice(6,
                    "[GM公告] " + gmName + " 向" + scopeText + "发放了每人 " + awardAmount + " " + unit + "！"
                )
            );
        }
    } catch (e) {}

    return {
        total: players.length,
        success: successCount,
        fail: failCount
    };
}

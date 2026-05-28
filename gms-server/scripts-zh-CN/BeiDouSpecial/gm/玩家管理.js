/*
 * ==================
 * 脚本类型: GM玩家管理工具
 * 脚本作者：北斗项目组
 * 功能说明：
 *   1. 查看所有在线玩家（按世界/频道/地图分组）
 *   2. 点击玩家查看详细信息（属性、装备、背包、所在线路）
 *   3. 追踪玩家（传送到目标玩家地图）
 *   4. 召唤玩家（拉到当前地图）
 *   5. 踢玩家下线
 *   6. 关入监牢
 *   7. 封禁玩家
 *   8. 给予物品/金币
 *   9. 发送消息
 * ==================
 */

var Server = Java.type('org.gms.net.server.Server');
var InventoryType = Java.type('org.gms.client.inventory.InventoryType');
var InventoryManipulator = Java.type('org.gms.client.inventory.manipulator.InventoryManipulator');
var ItemInformationProvider = Java.type('org.gms.server.ItemInformationProvider');
var Job = Java.type('org.gms.client.Job');
var MapId = Java.type('org.gms.constants.id.MapId');
var PacketCreator = Java.type('org.gms.util.PacketCreator');
var ExpTable = Java.type('org.gms.constants.game.ExpTable');

var status = -1;
var selectionList = [];
var selectedWorld = -1;
var selectedChannelIdx = -1;
var selectedPlayerName = "";
var actionType = "";
var pendingData = {};

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1) {
        // 返回上一级
        if (status >= 2) { status -= 2; action(1, 0, 0); return; }
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

    if (mode === 1) { status++; }

    // ========================================
    // status 0: 世界/频道选择
    // ========================================
    if (status === 0) {
        var text = "#e#b=== 玩家管理 ===#k#n\r\n\r\n";
        text += "请选择要查看的频道：\r\n";
        text += "#d" + "".padStart(30, "——") + "#k\r\n\r\n";

        var worlds = Server.getInstance().getWorlds();
        var channelCount = 0;

        for (var w = 0; w < worlds.size(); w++) {
            var world = worlds.get(w);
            var channels = world.getChannels();

            for (var c = 0; c < channels.size(); c++) {
                var channel = channels.get(c);
                var playerCount = channel.getPlayerStorage().getSize();
                var encode = w * 10000 + c;
                text += "#L" + encode + "#";
                text += "世界#b" + world.getId() + "#k 频道#b" + (c + 1) + "#k  ";
                text += "在线: #r" + playerCount + "#k 人";
                text += "#l\r\n";
                channelCount++;
            }
        }

        if (channelCount === 0) {
            text += "#r没有可用频道#k\r\n";
        }

        text += "\r\n#d" + "".padStart(30, "——") + "#k\r\n";
        text += "#L99999##r关闭#k#l\r\n";
        cm.sendSimple(text);

    // ========================================
    // status 1: 玩家列表
    // ========================================
    } else if (status === 1) {
        if (selection === 99999) { cm.dispose(); return; }

        selectedWorld = Math.floor(selection / 10000);
        selectedChannelIdx = selection % 10000;

        var world = Server.getInstance().getWorlds().get(selectedWorld);
        var channel = world.getChannels().get(selectedChannelIdx);
        var playerStorage = channel.getPlayerStorage();
        var allPlayers = playerStorage.getAllCharacters().toArray();

        selectionList = [];

        var text = "#e#b=== 世界 " + world.getId() + " 频道 " + (selectedChannelIdx + 1) + " ===#k#n\r\n\r\n";

        if (allPlayers.length === 0) {
            text += "#r该频道无在线玩家#k\r\n\r\n";
            text += "#L0##b返回上级#k#l\r\n";
        } else {
            text += "#d" + "".padStart(30, "——") + "#k\r\n\r\n";
            for (var i = 0; i < allPlayers.length; i++) {
                var p = allPlayers[i];
                selectionList.push(p.getName());
                var pJob = Job.getById(p.getJob().getId());
                var gmTag = p.isGM() ? " #r[GM]#k" : "";

                text += "#L" + i + "#";
                text += "#b" + (i + 1) + ".#k ";
                text += "#b" + p.getName() + "#k" + gmTag + "  ";
                text += "Lv." + p.getLevel() + "  " + pJob.getName() + "  ";
                text += "地图:" + p.getMapId();
                text += "#l\r\n";
            }
            text += "\r\n#d" + "".padStart(30, "——") + "#k\r\n";
            text += "#L99999##b返回上级#k#l\r\n";
        }

        cm.sendSimple(text);

    // ========================================
    // status 2: 玩家详情 + 操作菜单
    // ========================================
    } else if (status === 2) {
        if (selection === 99999) { status = -1; action(1, 0, 0); return; }

        selectedPlayerName = selectionList[selection];

        // 重新获取目标玩家（确保状态最新）
        var world = Server.getInstance().getWorlds().get(selectedWorld);
        var channel = world.getChannels().get(selectedChannelIdx);
        var victim = channel.getPlayerStorage().getCharacterByName(selectedPlayerName);

        if (victim === null) {
            cm.sendOk("#r玩家 " + selectedPlayerName + " 已不在线。#k");
            cm.dispose();
            return;
        }

        var victimJob = Job.getById(victim.getJob().getId());
        var mapId = victim.getMapId();
        var mapName = "";
        try { mapName = victim.getMap().getMapName(); } catch (e) { mapName = "未知"; }

        // ===== 玩家详细信息 =====
        var text = "#e#b=== 角色信息卡 ===#k#n\r\n\r\n";
        text += "#d" + "".padStart(30, "——") + "#k\r\n";
        text += "角色名：#b" + victim.getName() + "#k";
        if (victim.isGM()) { text += "  #r[GM Lv." + victim.gmLevel() + "]#k"; }
        text += "\r\n";
        text += "等级：  #b" + victim.getLevel() + "#k  职业：#b" + victimJob.getName() + "#k (ID:" + victim.getJob().getId() + ")\r\n";
        text += "经验：  #b" + victim.getExp().toLocaleString() + "#k / #b" + ExpTable.getExpNeededForLevel(victim.getLevel()).toLocaleString() + "#k\r\n";
        text += "金币：  #b" + victim.getMeso().toLocaleString() + "#k  人气：#b" + victim.getFame() + "#k\r\n";
        text += "\r\n";
        text += "HP：#b" + victim.getHp() + "#k / #b" + victim.getCurrentMaxHp() + "#k  ";
        text += "MP：#b" + victim.getMp() + "#k / #b" + victim.getCurrentMaxMp() + "#k\r\n";
        text += "力量:#b" + victim.getStr() + "#k  敏捷:#b" + victim.getDex() + "#k  智力:#b" + victim.getInt() + "#k  运气:#b" + victim.getLuk() + "#k\r\n";
        text += "AP：#b" + victim.getRemainingAp() + "#k  SP：#b" + victim.getRemainingSp() + "#k\r\n";
        text += "\r\n";
        text += "#d" + "".padStart(30, "——") + "#k\r\n";
        text += "所在世界：#b" + world.getId() + "#k  频道：#b" + (selectedChannelIdx + 1) + "#k\r\n";
        text += "所在地图：#b" + mapId + "#k (#b" + mapName + "#k)\r\n";
        try { text += "IP地址：  #b" + victim.getClient().getRemoteAddress() + "#k\r\n"; } catch (e) {}
        try { text += "账号ID：  #b" + victim.getClient().getAccID() + "#k\r\n"; } catch (e) {}
        text += "账号名：  #b" + victim.getAccountName() + "#k\r\n";
        text += "\r\n";
        text += "#d" + "".padStart(30, "——") + "#k\r\n";
        text += "#e#r=== 玩家操作 ===#k#n\r\n\r\n";
        text += "#L0##b[背包]#k  查看背包物品#l";
        text += "  #L1##b[装备]#k  查看装备栏#l\r\n";
        text += "#L2##b[召唤]#k  拉到当前地图#l";
        text += "  #L3##b[追踪]#k  传送到目标地图#l\r\n";
        text += "#L4##b[踢下线]#r  强制断开连接#k#l";
        text += "  #L5##b[监牢]#r  关入监牢#k#l\r\n";
        text += "#L6##r[封禁]#r  封禁玩家#k#l";
        text += "  #L7##b[给物品]#k  给予物品#l\r\n";
        text += "#L8##b[给金币]#k  给予金币#l";
        text += "  #L9##b[发消息]#k  发送私聊#l\r\n";
        text += "\r\n#L99999##b返回玩家列表#k#l\r\n";

        cm.sendSimple(text);

    // ========================================
    // status 3: 执行操作
    // ========================================
    } else if (status === 3) {
        if (selection === 99999) { status = 0; action(1, 0, 0); return; }

        actionType = selection;

        if (selection === 0) {
            // 查看背包
            showInventory();
        } else if (selection === 1) {
            // 查看装备栏
            showEquipment();
        } else if (selection === 2) {
            // 召唤到当前地图
            cm.sendYesNo("确认将 #b" + selectedPlayerName + "#k 召唤到当前地图？\r\n\r\n#r注意：跨频道召唤会自动切换玩家频道#k");
        } else if (selection === 3) {
            // 传送到目标玩家
            cm.sendYesNo("确认传送到玩家 #b" + selectedPlayerName + "#k 所在的 #b地图 " + getVictimMapId() + "#k？");
        } else if (selection === 4) {
            // 踢下线
            cm.sendYesNo("#r确认将玩家 #b" + selectedPlayerName + "#r 强制断开连接？#k");
        } else if (selection === 5) {
            // 关入监牢
            cm.sendGetNumber("请输入关押 #r" + selectedPlayerName + "#k 的时长（分钟）：", 5, 1, 1440);
        } else if (selection === 6) {
            // 封禁
            cm.sendGetText("#r封禁玩家 #b" + selectedPlayerName + "#r\r\n请输入封禁原因：");
        } else if (selection === 7) {
            // 给予物品
            cm.sendGetText("请输要给予 #b" + selectedPlayerName + "#k 的物品ID：");
        } else if (selection === 8) {
            // 给予金币
            cm.sendGetNumber("请输入给予 #b" + selectedPlayerName + "#k 的金币数量：", 1000000, -2000000000, 2000000000);
        } else if (selection === 9) {
            // 发送消息
            cm.sendGetText("请输入要发给 #b" + selectedPlayerName + "#k 的消息：");
        }

    // ========================================
    // status 4: 执行确认操作
    // ========================================
    } else if (status === 4) {
        if (actionType === 2) {
            // 召唤玩家
            if (selection === 1) {
                doSummonPlayer();
            } else {
                doBackToDetail();
            }
        } else if (actionType === 3) {
            // 追踪玩家
            if (selection === 1) {
                doTrackPlayer();
            } else {
                doBackToDetail();
            }
        } else if (actionType === 4) {
            // 踢下线
            if (selection === 1) {
                doKickPlayer();
            } else {
                doBackToDetail();
            }
        } else if (actionType === 5) {
            // 关入监牢 - 输入了分钟数
            doJailPlayer(selection);
        } else if (actionType === 6) {
            // 封禁 - 输入了原因
            doBanPlayer(cm.getText());
        } else if (actionType === 7) {
            // 给予物品 - 输入了物品ID，现在输入数量
            pendingData.itemId = parseInt(cm.getText());
            var ii = ItemInformationProvider.getInstance();
            if (ii.getName(pendingData.itemId) === null) {
                cm.sendOk("#r物品ID " + pendingData.itemId + " 无效，请重新操作。#k");
                cm.dispose();
                return;
            }
            status--;
            cm.sendGetNumber("物品 #b" + ii.getName(pendingData.itemId) + "#k (ID:" + pendingData.itemId + ")\r\n请输入数量：", 1, 1, 200);
        } else if (actionType === 8) {
            // 给予金币
            doGiveMeso(selection);
        } else if (actionType === 9) {
            // 发送消息
            doSendMessage(cm.getText());
        }

    // ========================================
    // status 5: 物品数量输入后的给予
    // ========================================
    } else if (status === 5) {
        if (actionType === 7) {
            doGiveItem(pendingData.itemId, selection);
        }
    }
}

// ==================== 辅助函数 ====================

function getVictim() {
    var world = Server.getInstance().getWorlds().get(selectedWorld);
    var channel = world.getChannels().get(selectedChannelIdx);
    return channel.getPlayerStorage().getCharacterByName(selectedPlayerName);
}

function getVictimMapId() {
    var v = getVictim();
    return v !== null ? v.getMapId() : 0;
}

function doBackToDetail() {
    cm.sendOk("操作已取消。");
    cm.dispose();
}

// ==================== 查看背包 ====================
function showInventory() {
    var victim = getVictim();
    if (victim === null) { cm.sendOk("#r玩家已下线#k"); cm.dispose(); return; }

    var typeNames = ["装备", "消耗", "设置", "其他", "现金"];
    var invTypes = [InventoryType.EQUIP, InventoryType.USE, InventoryType.SETUP, InventoryType.ETC, InventoryType.CASH];
    var ii = ItemInformationProvider.getInstance();

    var text = "#e#b=== " + selectedPlayerName + " 的背包 ===#k#n\r\n\r\n";

    for (var t = 0; t < invTypes.length; t++) {
        var inv = victim.getInventory(invTypes[t]);
        var items = inv.list().toArray();
        text += "#d" + "".padStart(30, "——") + "#k\r\n";
        text += "#b[" + typeNames[t] + "]#k  物品数: " + items.length + "\r\n\r\n";

        if (items.length === 0) {
            text += "  #r(空)#k\r\n";
        } else {
            for (var i = 0; i < items.length && i < 50; i++) {
                var item = items[i];
                var itemName = ii.getName(item.getItemId());
                if (itemName === null) itemName = "未知物品";
                text += "  #bID:" + item.getItemId() + "#k " + itemName;
                text += " x" + item.getQuantity() + "\r\n";
            }
            if (items.length > 50) {
                text += "  ... 还有 #r" + (items.length - 50) + "#k 件物品\r\n";
            }
        }
        text += "\r\n";
    }

    cm.sendOk(text);
    cm.dispose();
}

// ==================== 查看装备栏 ====================
function showEquipment() {
    var victim = getVictim();
    if (victim === null) { cm.sendOk("#r玩家已下线#k"); cm.dispose(); return; }

    var inv = victim.getInventory(InventoryType.EQUIPPED);
    var items = inv.list().toArray();
    var ii = ItemInformationProvider.getInstance();

    var text = "#e#b=== " + selectedPlayerName + " 的装备栏 ===#k#n\r\n\r\n";
    text += "#d" + "".padStart(30, "——") + "#k\r\n\r\n";

    if (items.length === 0) {
        text += "#r(无装备)#k\r\n";
    } else {
        for (var i = 0; i < items.length; i++) {
            var item = items[i];
            var itemName = ii.getName(item.getItemId());
            if (itemName === null) itemName = "未知装备";
            text += "#bID:" + item.getItemId() + "#k " + itemName;
            text += "  槽位:" + item.getPosition() + "\r\n";
        }
    }

    cm.sendOk(text);
    cm.dispose();
}

// ==================== 召唤玩家 ====================
function doSummonPlayer() {
    var victim = getVictim();
    if (victim === null) { cm.sendOk("#r玩家 " + selectedPlayerName + " 已不在线。#k"); cm.dispose(); return; }

    var player = cm.getPlayer();

    // 跨频道切换
    if (player.getClient().getChannel() !== victim.getClient().getChannel()) {
        victim.dropMessage("[GM] 你被GM召唤，正在切换频道...");
        victim.getClient().changeChannel(player.getClient().getChannel());
        try { java.lang.Thread.sleep(2000); } catch (e) {}
    }

    var targetMap = player.getMap();
    victim.saveLocationOnWarp();
    victim.forceChangeMap(targetMap, targetMap.findClosestPortal(player.getPosition()));

    cm.sendOk("已将 #b" + selectedPlayerName + "#k 召唤到当前地图。");
    cm.dispose();
}

// ==================== 追踪玩家 ====================
function doTrackPlayer() {
    var victim = getVictim();
    if (victim === null) { cm.sendOk("#r玩家 " + selectedPlayerName + " 已不在线。#k"); cm.dispose(); return; }

    var player = cm.getPlayer();

    // 跨频道切换
    if (player.getClient().getChannel() !== victim.getClient().getChannel()) {
        player.getClient().changeChannel(victim.getClient().getChannel());
        try { java.lang.Thread.sleep(2000); } catch (e) {}
    }

    var targetMap = victim.getMap();
    player.saveLocationOnWarp();
    player.forceChangeMap(targetMap, targetMap.findClosestPortal(victim.getPosition()));

    cm.sendOk("已传送到 #b" + selectedPlayerName + "#k 所在的 #b" + victim.getMap().getMapName() + "#k。");
    cm.dispose();
}

// ==================== 踢下线 ====================
function doKickPlayer() {
    var victim = getVictim();
    if (victim === null) { cm.sendOk("#r玩家 " + selectedPlayerName + " 已不在线。#k"); cm.dispose(); return; }

    try {
        victim.getClient().disconnect(false, false);
        cm.sendOk("已将 #b" + selectedPlayerName + "#k 强制断开连接。");
    } catch (e) {
        cm.sendOk("#r断开连接失败：" + e.message + "#k");
    }
    cm.dispose();
}

// ==================== 关入监牢 ====================
function doJailPlayer(minutes) {
    var victim = getVictim();
    if (victim === null) { cm.sendOk("#r玩家 " + selectedPlayerName + " 已不在线。#k"); cm.dispose(); return; }

    if (victim.isGM()) {
        cm.sendOk("#r不能将GM关入监牢。#k");
        cm.dispose();
        return;
    }

    victim.addJailExpirationTime(minutes * 60 * 1000);

    if (victim.getMapId() !== MapId.JAIL) {
        var jaileMap = cm.getClient().getChannelServer().getMapFactory().getMap(MapId.JAIL);
        victim.saveLocation("JAIL");
        victim.changeMap(jaileMap, jaileMap.getPortal(0));
    }

    var gmName = cm.getPlayer().getName();
    victim.yellowMessage("你已被GM " + gmName + " 关入监牢，关押时长: " + minutes + " 分钟");

    cm.sendOk("已将 #b" + selectedPlayerName + "#k 关入监牢 #r" + minutes + "#k 分钟。");
    cm.dispose();
}

// ==================== 封禁玩家 ====================
function doBanPlayer(reason) {
    var victim = getVictim();
    if (victim === null) {
        // 尝试离线封禁
        var banned = Java.type('org.gms.client.Character').ban(selectedPlayerName, reason, false);
        if (banned) {
            var worlds = Server.getInstance().getWorlds();
            for (var w = 0; w < worlds.size(); w++) {
                Server.getInstance().broadcastMessage(worlds.get(w).getId(), PacketCreator.serverNotice(6, "[封禁公告] " + selectedPlayerName + " 已被GM " + cm.getPlayer().getName() + " 封禁！原因：" + reason));
            }
            cm.sendOk("#r已封禁玩家 " + selectedPlayerName + "（离线封禁）\r\n原因：" + reason + "#k");
        } else {
            cm.sendOk("#r封禁失败：玩家不存在。#k");
        }
        cm.dispose();
        return;
    }

    if (victim.isGM()) {
        cm.sendOk("#r不能封禁GM。#k");
        cm.dispose();
        return;
    }

    var ip = victim.getClient().getRemoteAddress();
    var gmName = cm.getPlayer().getName();

    // IP封禁
    try {
        var DatabaseConnection = Java.type('org.gms.util.DatabaseConnection');
        var con = DatabaseConnection.getConnection();
        var ps = con.prepareStatement("INSERT INTO ipbans VALUES (DEFAULT, ?, ?)");
        ps.setString(1, ip);
        ps.setString(2, String(victim.getClient().getAccID()));
        ps.executeUpdate();
        ps.close();
        con.close();
    } catch (e) {
        // IP封禁失败不阻止账号封禁
    }

    victim.getClient().banMacs();
    var fullReason = "GM " + gmName + " 封禁 " + victim.getName() + " | 原因: " + reason + " | IP: " + ip;
    victim.ban(fullReason);
    victim.yellowMessage("你已被GM " + gmName + " 封禁！原因：" + reason);
    victim.yellowMessage("封禁详情：" + fullReason);
    cm.getClient().sendPacket(PacketCreator.getGMEffect(4, 0));

    // 5秒后断开
    var rip = victim;
    Java.type('org.gms.server.TimerManager').getInstance().schedule(function() {
        try { rip.getClient().disconnect(false, false); } catch (e) {}
    }, 5000);

    // 广播公告
    var worlds = Server.getInstance().getWorlds();
    for (var w = 0; w < worlds.size(); w++) {
        Server.getInstance().broadcastMessage(worlds.get(w).getId(), PacketCreator.serverNotice(6, "[封禁公告] " + selectedPlayerName + " 已被GM " + gmName + " 封禁！"));
    }

    cm.sendOk("#r已封禁玩家 " + selectedPlayerName + "\r\n原因：" + reason + "\r\nIP：" + ip + "#k");
    cm.dispose();
}

// ==================== 给予物品 ====================
function doGiveItem(itemId, quantity) {
    var victim = getVictim();
    if (victim === null) { cm.sendOk("#r玩家 " + selectedPlayerName + " 已不在线。#k"); cm.dispose(); return; }

    var ii = ItemInformationProvider.getInstance();
    var itemName = ii.getName(itemId);

    try {
        InventoryManipulator.addById(victim.getClient(), itemId, quantity, victim.getName(), -1, 0, -1);
        victim.yellowMessage("GM " + cm.getPlayer().getName() + " 给予你 " + itemName + " x" + quantity);
        cm.sendOk("已给予 #b" + selectedPlayerName + "#k 物品 #b" + itemName + "#k (ID:" + itemId + ") x" + quantity);
    } catch (e) {
        cm.sendOk("#r给予物品失败：" + e.message + "#k");
    }
    cm.dispose();
}

// ==================== 给予金币 ====================
function doGiveMeso(amount) {
    var victim = getVictim();
    if (victim === null) { cm.sendOk("#r玩家 " + selectedPlayerName + " 已不在线。#k"); cm.dispose(); return; }

    var meso = parseInt(amount);
    if (meso > 2147483647) meso = 2147483647;
    if (meso < -2147483647) meso = -2147483647;

    victim.gainMeso(meso, true);
    if (meso >= 0) {
        victim.yellowMessage("GM " + cm.getPlayer().getName() + " 给予你 " + meso.toLocaleString() + " 金币");
    }
    cm.sendOk("已给予 #b" + selectedPlayerName + "#k 金币 #b" + meso.toLocaleString() + "#k。");
    cm.dispose();
}

// ==================== 发送消息 ====================
function doSendMessage(msg) {
    var victim = getVictim();
    if (victim === null) { cm.sendOk("#r玩家 " + selectedPlayerName + " 已不在线。#k"); cm.dispose(); return; }

    victim.yellowMessage("[GM消息] " + msg);
    cm.sendOk("已向 #b" + selectedPlayerName + "#k 发送消息：\r\n#b\"" + msg + "\"#k");
    cm.dispose();
}

/**
 * 巡查面板 — GM管理工具
 * 功能：查看在线玩家完整属性信息、给予资源（金币/点卷/抵用/赞助/物品）、
 *       跟踪玩家、拉到身边、关闭小黑屋（全服广播）、踢下线
 */
var Server = Java.type("org.gms.net.server.Server");
var PacketCreator = Java.type("org.gms.util.PacketCreator");
var MentorService = Java.type("org.gms.service.MentorService");
var InventoryManipulator = Java.type("org.gms.client.inventory.manipulator.InventoryManipulator");
var ServerManager = Java.type("org.gms.manager.ServerManager");
var ItemInfoProvider = Java.type("org.gms.server.ItemInformationProvider");
var Integer = Java.type("java.lang.Integer"); // 用于 JS Number → Java Short 转换

var status = 0;
var selectedPlayer;       // 选中的玩家对象
var selectedChannel;      // 选中玩家所在频道
var pendingAction = "";   // 当前给予操作类型："meso"|"nxCredit"|"nxPrepaid"|"sponsor"|"item"
var pendingItemId = 0;    // 待给予的物品ID
var lastResultMsg = "";   // 上次操作结果消息，刷新详情时展示

function start() {
    status = -1;
    action(1, 0, 0);
}

/**
 * 构建在线玩家列表文本，供多处复用
 * 使用 .toArray() 将 Java 集合转为 JS 数组，避免 GraalJS 下标访问兼容问题
 */
function buildPlayerListText() {
    var text = "#e巡查面板#n\r\n\r\n";
    var onlineCount = 0;
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

/**
 * 验证玩家是否仍在在线，并刷新 selectedPlayer 引用
 * 防止操作时目标已离线导致空指针
 */
function isPlayerStillOnline(charId) {
    var worlds = Server.getInstance().getWorlds().toArray();
    for (var w = 0; w < worlds.length; w++) {
        var channels = worlds[w].getChannels().toArray();
        for (var c = 0; c < channels.length; c++) {
            var players = channels[c].getPlayerStorage().getAllCharacters().toArray();
            for (var i = 0; i < players.length; i++) {
                if (players[i].getId() == charId) {
                    selectedPlayer = players[i];     // 刷新引用
                    selectedChannel = channels[c].getId();
                    return true;
                }
            }
        }
    }
    return false;
}

/**
 * 构建玩家详情文本 + 操作菜单
 * @param {*} player    目标玩家对象
 * @param {string} resultMsg 操作结果提示（可选）
 */
function buildDetailText(player, resultMsg) {
    var text = "#e巡查面板 - 玩家详情#n\r\n\r\n";

    // 操作结果提示
    if (resultMsg && resultMsg.length > 0) {
        text += "#b[操作结果]#k " + resultMsg + "\r\n\r\n";
    }

    // ===== 基本信息 =====
    text += "========== #r基本信息#k ==========\r\n";
    text += "玩家ID: #r" + player.getId() + "#k\r\n";
    text += "玩家名字: #r" + player.getName() + "#k\r\n";
    text += "等级: #r" + player.getLevel() + "#k";
    // getJob().getName() 返回 I18n 中文职业名，如"新手""战士""魔法师"等
    var jobName = "未知职业";
    try {
        var jn = player.getJob().getName();
        if (jn != null && jn.length() > 0) {
            jobName = jn;
        }
    } catch (e) {}
    text += "\t职业: #r" + jobName + "#k (ID:" + player.getJob().getId() + ")\r\n";
    text += "所在地图: #r" + player.getMap().getMapName() + "#k";
    text += " (ID:" + player.getMapId() + ")\r\n";
    text += "所在频道: #r" + player.getClient().getChannel() + "#k\r\n\r\n";

    // ===== 属性面板 =====
    text += "========== #r属性面板#k ==========\r\n";
    text += "力量(STR): #b" + player.getStr() + "#k\t";
    text += "敏捷(DEX): #b" + player.getDex() + "#k\r\n";
    text += "智力(INT): #b" + player.getInt() + "#k\t";
    text += "运气(LUK): #b" + player.getLuk() + "#k\r\n";
    text += "HP: #d" + player.getHp() + "#k/#r" + player.getMaxHp() + "#k\t";
    text += "MP: #d" + player.getMp() + "#k/#r" + player.getMaxMp() + "#k\r\n";
    text += "AP: #r" + player.getRemainingAp() + "#k\t";
    text += "SP: #r" + player.getRemainingSp() + "#k\t";
    text += "人气: #r" + player.getFame() + "#k\r\n";
    text += "金币: #r" + player.getMeso() + "#k 金币\r\n\r\n";

    // ===== 师门信息 =====
    text += "========== #r师门信息#k ==========\r\n";
    try {
        var relationship = MentorService.getMyMaster(player.getId());
        if (relationship != null) {
            var masterName = MentorService.getCharacterName(relationship.getMasterCharacterId());
            text += "师父: #b" + masterName + "#k";
            // relationship.getStatus(): 0=在师门中, 1=已出师, 2=已退出
            var statusMap = {0: "#b在师门中#k", 1: "#d已出师#k", 2: "#r已退出#k"};
            text += "\t状态: " + (statusMap[relationship.getStatus()] || "未知") + "\r\n";
        } else {
            text += "师父: #d无#k\r\n";
        }
    } catch (e) {
        text += "师父: #d查询失败#k\r\n";
    }
    text += "\r\n";

    // ===== 家族信息 =====
    text += "========== #r家族信息#k ==========\r\n";
    var guild = player.getGuild();
    if (guild != null) {
        text += "家族名: #b" + guild.getName() + "#k\r\n";
        var rankNames = ["", "#r族长#k", "#b副族长#k", "长老", "元老", "成员"];
        var rank = player.getGuildRank();
        text += "职位: " + (rankNames[rank] || "成员") + " (Rank:" + rank + ")\r\n";
        text += "家族GP: #b" + guild.getGP() + "#k\r\n";
        // 族长：显示ID + 名称
        var leaderId = guild.getLeaderId();
        var leaderName = "未知";
        try {
            var ln = MentorService.getCharacterName(leaderId);
            if (ln != null && ln.length() > 0) {
                leaderName = ln;
            }
        } catch (e) {}
        text += "族长: #b" + leaderName + "#k (ID:" + leaderId + ")\r\n";
    } else {
        text += "家族: #d无#k\r\n";
    }
    text += "\r\n";

    // ===== 小黑屋状态 =====
    var jailTimeLeft = player.getJailExpirationTimeLeft();
    if (jailTimeLeft > 0) {
        var jailSec = Math.floor(jailTimeLeft / 1000);
        var jailMin = Math.floor(jailSec / 60);
        var jailRemainSec = jailSec % 60;
        text += "#r[小黑屋]#k 剩余: #r" + jailMin + "分" + jailRemainSec + "秒#k\r\n\r\n";
    }

    // ===== 操作菜单（固定ID，不受条件显示影响） =====
    text += "========== #r操作菜单#k ==========\r\n";
    text += "#L0##b[跟踪玩家]#l\t\t";
    text += "#L6##b[拉到身边]#l\r\n";
    text += "#L1##d[给予金币]#l\t\t";
    text += "#L2##d[给予点卷]#l\r\n";
    text += "#L3##d[给予抵用]#l\t\t";
    text += "#L4##d[给予赞助]#l\r\n";
    text += "#L5##d[给予物品]#l\r\n";
    if (jailTimeLeft > 0) {
        text += "#L7##r[关闭小黑屋]#l\r\n";
    }
    text += "#L8##r[踢出下线]#l\t\t";
    text += "#L9##b[返回列表]#l\r\n";

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

    // ===== status 0：玩家列表 =====
    if (status == 0) {
        // GM权限检查
        if (cm.getPlayer().gmLevel() < 1) {
            cm.sendOk("你没有权限使用这个功能！");
            cm.dispose();
            return;
        }
        lastResultMsg = ""; // 重置操作结果
        cm.sendSimple(buildPlayerListText());

    // ===== status 1：玩家详情 + 操作菜单 =====
    } else if (status == 1) {
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

        cm.sendSimple(buildDetailText(selectedPlayer, lastResultMsg));
        lastResultMsg = ""; // 显示后清除

    // ===== status 2：操作路由 =====
    } else if (status == 2) {
        // 重新验证目标玩家在线
        if (!isPlayerStillOnline(selectedPlayer.getId())) {
            cm.sendOk("该玩家已下线，操作取消。");
            cm.dispose();
            return;
        }

        if (selection == 0) {
            // 跟踪玩家：GM跳转到目标玩家所在地图
            var targetMap = selectedPlayer.getMapId();
            var targetCh = selectedPlayer.getClient().getChannel();
            cm.getPlayer().changeMap(targetMap);
            if (cm.getPlayer().getClient().getChannel() != targetCh) {
                cm.getPlayer().changeChannel(targetCh);
            }
            cm.sendOk("已跟踪到玩家 #b" + selectedPlayer.getName() + "#k 所在位置。");
            cm.dispose();

        } else if (selection >= 1 && selection <= 5) {
            // 给予操作：设置 pendingAction，弹出输入框
            var giveTypes = ["meso", "nxCredit", "nxPrepaid", "sponsor", "item"];
            pendingAction = giveTypes[selection - 1];
            var prompts = [
                "当前玩家：#b" + selectedPlayer.getName() + "#k\r\n请输入要给予的 #r金币#k 数量：",
                "当前玩家：#b" + selectedPlayer.getName() + "#k\r\n请输入要给予的 #r点卷#k 数量：",
                "当前玩家：#b" + selectedPlayer.getName() + "#k\r\n请输入要给予的 #r抵用#k 数量：",
                "当前玩家：#b" + selectedPlayer.getName() + "#k\r\n请输入要给予的 #r赞助#k 金额：",
                "当前玩家：#b" + selectedPlayer.getName() + "#k\r\n请输入要给予的 #r物品ID#k："
            ];
            cm.sendGetText(prompts[selection - 1]);

        } else if (selection == 6) {
            // 拉到身边：将目标玩家传送到GM所在位置
            var gmMapId = cm.getPlayer().getMapId();
            selectedPlayer.changeMap(gmMapId);
            selectedPlayer.dropMessage(5, "[GM通知] 你已被管理员 " + cm.getPlayer().getName() + " 拉到了身边。");
            lastResultMsg = "已将 #b" + selectedPlayer.getName() + "#k 拉到身边。";
            // 刷新详情
            if (!isPlayerStillOnline(selectedPlayer.getId())) {
                cm.sendOk("操作已完成，但玩家已下线。");
                cm.dispose();
                return;
            }
            status = 1;
            cm.sendSimple(buildDetailText(selectedPlayer, lastResultMsg));

        } else if (selection == 7) {
            // 关闭小黑屋：解除监禁 + 传送到自由市场 + 全服白底广播
            var jailLeft = selectedPlayer.getJailExpirationTimeLeft();
            if (jailLeft <= 0) {
                lastResultMsg = "该玩家未被关小黑屋，无需操作。";
                status = 1;
                cm.sendSimple(buildDetailText(selectedPlayer, lastResultMsg));
                return;
            }

            selectedPlayer.removeJailExpirationTime();
            selectedPlayer.changeMap(910000000); // 传送到自由市场入口
            selectedPlayer.dropMessage(5, "[GM通知] 你已被管理员 " + cm.getPlayer().getName() + " 解除了小黑屋，你已恢复自由。");

            // 全服白底广播（type=6 浅蓝色文字）
            var broadcastMsg = "[GM通知] 玩家 #b" + selectedPlayer.getName()
                + "#k 已被管理员从 #r小黑屋#k 释放。";
            cm.getPlayer().getWorldServer().broadcastPacket(
                PacketCreator.serverNotice(6, broadcastMsg)
            );

            lastResultMsg = "已关闭 #b" + selectedPlayer.getName() + "#k 的小黑屋，已传送至自由市场。";
            // 刷新详情
            if (!isPlayerStillOnline(selectedPlayer.getId())) {
                cm.sendOk("操作已完成，但玩家已下线。");
                cm.dispose();
                return;
            }
            status = 1;
            cm.sendSimple(buildDetailText(selectedPlayer, lastResultMsg));

        } else if (selection == 8) {
            // 踢下线：关闭目标玩家的网络连接
            selectedPlayer.getClient().disconnectSession();
            cm.sendOk("玩家 #r" + selectedPlayer.getName() + "#k 已被踢下线。");
            cm.dispose();

        } else if (selection == 9) {
            // 返回列表：status=0 确保下次点击进入 status=1 处理玩家选择
            lastResultMsg = "";
            status = 0;
            cm.sendSimple(buildPlayerListText());
        }

    // ===== status 3：金额/物品ID 输入处理 =====
    } else if (status == 3) {
        var inputText = cm.getText();
        var amount = parseInt(inputText);

        if (isNaN(amount) || amount <= 0) {
            cm.sendOk("无效的输入！请输入正整數。");
            cm.dispose();
            return;
        }

        if (pendingAction == "item") {
            // 物品ID验证阶段：用 cm.itemExists() 验证物品是否存在（避免 GraalJS 链式调用问题）
            if (!cm.itemExists(amount)) {
                cm.sendOk("物品ID #r" + amount + "#k 不存在，请确认后重试。");
                cm.dispose();
                return;
            }
            pendingItemId = amount;

            // 获取物品名用于展示
            var itemDisplayName = "物品ID:" + amount;
            try {
                var provider = ItemInfoProvider.getInstance();
                var name = provider.getName(amount);
                if (name != null && name.length() > 0) {
                    itemDisplayName = name;
                }
            } catch (e) {}

            cm.sendGetText("物品：#b#i" + pendingItemId + "# " + itemDisplayName + "#k\r\n请输入数量（1~9999）：");

        } else {
            // 金额类给予操作（金币/点卷/抵用/赞助）
            // 重新验证在线
            if (!isPlayerStillOnline(selectedPlayer.getId())) {
                cm.sendOk("该玩家已下线，操作取消。");
                cm.dispose();
                return;
            }

            var pname = selectedPlayer.getName();

            var gmName = cm.getPlayer().getName();
            if (pendingAction == "meso") {
                selectedPlayer.gainMeso(amount, true);
                selectedPlayer.dropMessage(5, "[GM通知] 管理员 " + gmName + " 给予了你 " + amount + " 金币。");
                lastResultMsg = "已给予 #b" + pname + "#k 金币 x #r" + amount + "#k";
            } else if (pendingAction == "nxCredit") {
                selectedPlayer.getCashShop().gainCash(1, amount);
                selectedPlayer.dropMessage(5, "[GM通知] 管理员 " + gmName + " 给予了你 " + amount + " 点卷。");
                lastResultMsg = "已给予 #b" + pname + "#k 点卷 x #r" + amount + "#k";
            } else if (pendingAction == "nxPrepaid") {
                selectedPlayer.getCashShop().gainCash(4, amount);
                selectedPlayer.dropMessage(5, "[GM通知] 管理员 " + gmName + " 给予了你 " + amount + " 抵用。");
                lastResultMsg = "已给予 #b" + pname + "#k 抵用 x #r" + amount + "#k";
            } else if (pendingAction == "sponsor") {
                try {
                    var sponsorService = ServerManager.getApplicationContext().getBean("sponsorService");
                    sponsorService.addSponsorAmount(
                        selectedPlayer.getId(),
                        selectedPlayer.getName(),
                        selectedPlayer.getAccountId(),
                        "",
                        amount,
                        2, // type=2 管理员手动添加
                        "管理员手动添加"
                    );
                    selectedPlayer.dropMessage(5, "[GM通知] 管理员 " + gmName + " 给予了你 " + amount + " 赞助点。");
                    lastResultMsg = "已给予 #b" + pname + "#k 赞助 x #r" + amount + "#k";
                } catch (e) {
                    cm.sendOk("赞助系统操作失败，请检查赞助服务是否正常运行。");
                    cm.dispose();
                    return;
                }
            }

            // 刷新详情
            if (!isPlayerStillOnline(selectedPlayer.getId())) {
                cm.sendOk("操作已完成，但玩家已下线。\r\n" + lastResultMsg);
                cm.dispose();
                return;
            }
            status = 1;
            cm.sendSimple(buildDetailText(selectedPlayer, lastResultMsg));
        }

    // ===== status 4：物品数量输入处理 =====
    } else if (status == 4) {
        var qty = parseInt(cm.getText());

        if (isNaN(qty) || qty <= 0 || qty > 9999) {
            cm.sendOk("无效的数量！请输入 1~9999 之间的数字。");
            cm.dispose();
            return;
        }

        // 重新验证在线
        if (!isPlayerStillOnline(selectedPlayer.getId())) {
            cm.sendOk("该玩家已下线，操作取消。");
            cm.dispose();
            return;
        }

        try {
            InventoryManipulator.addById(selectedPlayer.getClient(), pendingItemId, new Integer(qty).shortValue(), "", -1);

            var itemName = "物品ID:" + pendingItemId;
            try {
                var provider = ItemInfoProvider.getInstance();
                var name = provider.getName(pendingItemId);
                if (name != null && name.length() > 0) {
                    itemName = name;
                }
            } catch (e) {}
            selectedPlayer.dropMessage(5, "[GM通知] 管理员 " + cm.getPlayer().getName() + " 给予了你 " + itemName + " x " + qty + "。");
            lastResultMsg = "已给予 #b" + selectedPlayer.getName() + "#k #b" + itemName + "#k x #r" + qty + "#k";
        } catch (e) {
            cm.sendOk("给予物品失败！可能背包已满或物品不可交易。");
            cm.dispose();
            return;
        }

        // 刷新详情
        if (!isPlayerStillOnline(selectedPlayer.getId())) {
            cm.sendOk("操作已完成，但玩家已下线。\r\n" + lastResultMsg);
            cm.dispose();
            return;
        }
        status = 1;
        cm.sendSimple(buildDetailText(selectedPlayer, lastResultMsg));
    }
}
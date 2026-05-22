/*
 * ==================
 * 脚本类型: 结婚NPC扩展
 * 脚本作者：北斗项目组
 * 功能说明：
 *   1. 查看结婚相关信息和状态
 *   2. 查看伴侣信息
 *   3. 结婚戒指相关说明
 * ==================
 */

var status = -1;
var MARRIAGE_KEY = "marriageInfo";

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

    status++;

    if (status === 0) {
        var player = cm.getPlayer();
        var text = "#e#b=== 结婚信息 ===#k#n\r\n\r\n";
        text += "#d" + "".padStart(30, "——") + "#k\r\n\r\n";

        // 检查婚姻状态
        var married = false;
        var partnerName = "";

        try {
            var ringInv = player.getInventory(Java.type('org.gms.client.inventory.InventoryType').EQUIP);
            if (ringInv != null) {
                var items = ringInv.list();
                for (var i = 0; i < items.size(); i++) {
                    var item = items.get(i);
                    var itemId = item.getItemId();
                    // 结婚戒指ID范围: 1112000-1112999
                    if (itemId >= 1112000 && itemId <= 1112999) {
                        married = true;
                        partnerName = "伴侣 (戒指ID: " + itemId + ")";
                        break;
                    }
                }
            }
        } catch (e) {}

        if (married) {
            text += "状态：#b已婚#k\r\n";
            text += partnerName + "\r\n";
            text += "\r\n#d" + "".padStart(30, "——") + "#k\r\n\r\n";
            text += "结婚戒指：#b#i1112000# 结婚金戒指#k\r\n";
            text += "\r\n结婚地点：\r\n";
            text += "#L0##b前往结婚教堂#k (圣地图)#l\r\n";
            text += "#L1##b前往结婚大厅#k (等待室)#l\r\n";
        } else {
            text += "状态：#r未婚#k\r\n\r\n";
            text += "结婚流程说明：\r\n";
            text += "1. 前往 #b圣地图(130000000)#k 的婚礼教堂\r\n";
            text += "2. 与婚礼司仪对话，佩戴结婚戒指\r\n";
            text += "3. 邀请伴侣组队后完成婚礼仪式\r\n";
            text += "\r\n";
            text += "#L0##b前往结婚教堂#k#l\r\n";
        }

        cm.sendSimple(text);
    } else if (status === 1) {
        switch (selection) {
            case 0:
                cm.getPlayer().saveLocationOnWarp();
                cm.warp(130000000); // 圣地
                break;
            case 1:
                cm.getPlayer().saveLocationOnWarp();
                cm.warp(680000000); // 结婚大厅
                break;
        }
        cm.dispose();
    }
}

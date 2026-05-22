/*
 * ==================
 * 脚本类型: 成就/收集中心
 * 脚本作者：北斗项目组
 * 功能说明：
 *   1. 统一查看各收集系统进度
 *   2. 快速跳转到各收集系统
 * ==================
 */

var status = -1;

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

    if (status === 0) {
        var text = "#e#b=== 成就/收集中心 ===#k#n\r\n\r\n";
        text += "#d" + "".padStart(30, "——") + "#k\r\n\r\n";

        // 卡片收集
        var cardData = cm.getCharacterExtendValue("cardCollection");
        var cardCount = 0;
        if (cardData != null && cardData !== "") {
            try {
                var cards = JSON.parse(cardData);
                cardCount = Object.keys(cards).length;
            } catch (e) {}
        }
        text += "#L0##b卡片收集#k  (已收集: #r" + cardCount + "#k 种)#l\r\n";

        // 玩具收集
        var toyData = cm.getCharacterExtendValue("toyCollection");
        var toyCount = 0;
        if (toyData != null && toyData !== "") {
            try {
                var toys = JSON.parse(toyData);
                toyCount = Object.keys(toys).length;
            } catch (e) {}
        }
        text += "#L1##b玩具收集#k  (已收集: #r" + toyCount + "#k 件)#l\r\n";

        // 城镇任务
        var townData = cm.getCharacterExtendValue("townQuestCollection");
        var townCount = 0;
        if (townData != null && townData !== "") {
            try {
                var towns = JSON.parse(townData);
                townCount = Object.keys(towns).length;
            } catch (e) {}
        }
        text += "#L2##b城镇任务#k  (已完成: #r" + townCount + "#k 个)#l\r\n";

        // Boss挑战
        var bossData = cm.getCharacterExtendValue("bossCooldown");
        text += "#L3##bBoss入口#k  (查看Boss挑战)#l\r\n";

        text += "\r\n#L4##b角色信息卡#k#l\r\n";

        cm.sendSimple(text);
    } else if (status === 1) {
        switch (selection) {
            case 0:
                cm.dispose();
                cm.openNpc(9900001, "卡片收集");
                break;
            case 1:
                cm.dispose();
                cm.openNpc(9900001, "玩具收集");
                break;
            case 2:
                cm.dispose();
                cm.openNpc(9900001, "城镇任务");
                break;
            case 3:
                cm.dispose();
                cm.openNpc(9900001, "Boss入口");
                break;
            case 4:
                cm.dispose();
                cm.openNpc(9900001, "角色信息卡");
                break;
        }
    }
}

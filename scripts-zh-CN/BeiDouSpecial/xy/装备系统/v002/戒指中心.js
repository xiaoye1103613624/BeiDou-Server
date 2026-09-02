/*
 * ==================
 * 脚本类型: NPC导航
 * 脚本作者：北斗项目组
 * 功能说明：戒指中心 — 戒指升级、戒指强化的统一入口
 * ==================
 */
var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
        return;
    }
    if (mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    } else {
        status--;
    }

    if (status == 0) {
        var text = "#e#d戒指中心#n#k\r\n\r\n";
        text += "请选择服务：\r\n\r\n";
        text += "#L0##b戒指升级#k — 十字旅团戒指逐级升级，属性随等级成长#l\r\n\r\n";
        text += "#L1##b戒指强化#k — 强化戒指属性（开发中）#l\r\n";
        cm.sendSimple(text);

    } else if (status == 1) {
        if (selection == 0) {
            // 戒指升级
            cm.dispose();
            cm.openNpc(9900001, "xy/装备系统/v002/戒指升级");
        } else if (selection == 1) {
            // 戒指强化（开发中）
            cm.sendOk("戒指强化功能开发中，敬请期待！");
            cm.dispose();
        } else {
            cm.dispose();
        }
    }
}

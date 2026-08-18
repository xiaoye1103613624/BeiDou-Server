/*
 * ==================
 * 脚本类型: NPC导航
 * 脚本作者：北斗项目组
 * 功能说明：武器中心 — 初始武器领取、武器进阶的统一入口
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
        var text = "#e#d武器中心#n#k\r\n\r\n";
        text += "请选择服务：\r\n\r\n";
        text += "#L0##b购买初始武器#k#l\r\n\r\n";
        text += "#L1##b武器进阶#k#l\r\n";
        cm.sendSimple(text);

    } else if (status == 1) {
        if (selection == 0) {
            // 购买初始武器
            cm.dispose();
            cm.openNpc(9900001, "xy/装备系统/v002/购买初始武器");
        } else if (selection == 1) {
            // 武器进阶（不可领取初始武器）
            cm.dispose();
            cm.openNpc(9900001, "xy/装备系统/v002/武器进阶");
        } else {
            cm.dispose();
        }
    }
}

/**
 * 北斗助手 - 侧边栏快捷键入口
 * 功能: 一键打开9大门户工具, 可绑定键盘快捷键
 * 使用: 在自由市场放置NPC, 脚本指向本文件
 *       按NPC交谈键(空格) → 弹出工具菜单
 *
 * @author 萧曵
 * @date 2026-07-30
 */

var ICON = "#fUI/UIWindow/Quest/icon3/6#";

var MENUS = [
    { id: 1,  name: "便民工具",   desc: "仓库/时装/发型/答题/宠物", script: "xy/portal/便民工具" },
    { id: 2,  name: "装备中心",   desc: "装备强化/洗练/升星/卷轴", script: "xy/portal/装备中心" },
    { id: 3,  name: "兑换中心",   desc: "物品/金币/枫叶兑换",       script: "xy/portal/兑换中心" },
    { id: 4,  name: "VIP会员",    desc: "会员特权/商店/吸怪",       script: "xy/portal/VIP会员" },
    { id: 5,  name: "成长系统",   desc: "转生/属性/技能",           script: "xy/portal/成长系统" },
    { id: 6,  name: "每日任务",   desc: "日常/副本/Boss/跑环",      script: "xy/portal/每日任务" },
    { id: 7,  name: "社交系统",   desc: "师徒/组队/公会/好友",      script: "xy/portal/社交系统" },
    { id: 8,  name: "收集系统",   desc: "卡片/玩具/成就",           script: "xy/portal/收集系统" },
    { id: 9,  name: "GM工具",     desc: "管理员专用工具箱",         script: "xy/portal/GM工具" }
];

var status = 0;

function start() {
    showMainMenu();
}

function showMainMenu() {
    var text = "";
    text += "\t#b★══ 北斗助手 ══★#k\r\n";
    text += ICON.repeat(6) + "\r\n\r\n";

    for (var i = 0; i < MENUS.length; i++) {
        var m = MENUS[i];
        var num = i + 1;
        text += "#L" + m.id + "##b" + m.name + "#k  #d" + m.desc + "#l\r\n";
    }

    text += "\r\n";
    text += "#L0##r关闭菜单#k#l\r\n";
    text += "\r\n" + ICON.repeat(6) + "\r\n";
    text += "#e提示: 在键盘设置中给NPC交谈键绑定方便快捷键#n";

    cm.sendSimple(text);
}

function action(mode, type, selection) {
    if (mode == -1) { cm.dispose(); return; }
    if (mode == 0) { cm.dispose(); return; }

    if (selection == 0) {
        cm.dispose();
        return;
    }

    for (var i = 0; i < MENUS.length; i++) {
        if (MENUS[i].id == selection) {
            cm.dispose();
            cm.openNpc(9900001, MENUS[i].script);
            return;
        }
    }

    cm.dispose();
}

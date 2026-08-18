/**
 * @description 每日任务系统（占位脚本）
 * 后续可扩展为真正的每日任务系统：随机任务池、完成条件、奖励等
 * 入口：9900001.js case 200
 */

function start() { levelMain(); }

function levelMain() {
    var text = "#e每日任务#n\r\n\r\n";
    text += "该功能正在开发中，敬请期待！\r\n\r\n";
    text += "当前已开放的每日功能：\r\n";
    text += "  · 每日跑环 - 随机收集任务\r\n";
    text += "  · 每日副本 - 挑战副本BOSS\r\n";
    text += "  · 每日BOSS - 挑战世界BOSS\r\n\r\n";
    text += "#L0##g返回首页#k#l\r\n";

    cm.sendNextSelectLevel("HandleSelect", text);
}

function levelHandleSelect(selection) {
    cm.dispose();
}

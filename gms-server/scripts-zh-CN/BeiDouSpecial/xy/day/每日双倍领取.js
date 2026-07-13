/**
 * @description 每日双倍领取（经验双倍券 + 爆率双倍券）
 * 功能：每天可领取经验双倍券(2450000)最多2次、爆率双倍券(5360014)最多1次
 * 领取的道具本身自带使用效果（经验券为限时buff道具，爆率券为持有生效的倍率券），
 * 本脚本只负责"每日限量发放"，不涉及道具的使用效果逻辑。
 * 持久化：使用 CharacterExtendValue(daily=true)，午夜自动清空次数
 */

// ==================== 配置 ====================
var 经验券ID = 2450000;
var 经验券每日上限 = 2;
var 爆率券ID = 5360014;
var 爆率券每日上限 = 1;

// 持久化Key（与 每日Boss.js 同样的 daily 扩展值用法）
var EXTEND_KEY_EXP = "每日双倍领取_经验";
var EXTEND_KEY_DRP = "每日双倍领取_爆率";

// 菜单选项
var SEL_RETURN = 0;
var SEL_CLAIM_EXP = 1; // 领取经验双倍券
var SEL_CLAIM_DRP = 2; // 领取爆率双倍券

// ==================== 数据持久化 ====================

function getClaimedCount(extendKey) {
    var raw = cm.getCharacterExtendValue(extendKey, true);
    var n = parseInt(raw, 10);
    return isNaN(n) ? 0 : n;
}

function setClaimedCount(extendKey, count) {
    cm.saveOrUpdateCharacterExtendValue(extendKey, String(count), true);
}

// ==================== 入口 ====================

function start() {
    levelMain();
}

function levelMain() {
    var expClaimed = getClaimedCount(EXTEND_KEY_EXP);
    var drpClaimed = getClaimedCount(EXTEND_KEY_DRP);

    var text = "#e每日双倍领取#n\r\n\r\n";

    text += "经验双倍券 今日：#r" + expClaimed + "#k / " + 经验券每日上限 + " 次\r\n";
    if (expClaimed < 经验券每日上限) {
        text += "#L" + SEL_CLAIM_EXP + "##r领取经验双倍券#k#l\r\n\r\n";
    } else {
        text += "#r今日已领满，请明天再来#k\r\n\r\n";
    }

    text += "爆率双倍券 今日：#r" + drpClaimed + "#k / " + 爆率券每日上限 + " 次\r\n";
    if (drpClaimed < 爆率券每日上限) {
        text += "#L" + SEL_CLAIM_DRP + "##r领取爆率双倍券#k#l\r\n\r\n";
    } else {
        text += "#r今日已领满，请明天再来#k\r\n\r\n";
    }

    text += "#L" + SEL_RETURN + "##g返回上一页#k#l\r\n";

    cm.sendNextSelectLevel("HandleMain", text);
}

// ==================== 操作路由 ====================

function levelHandleMain(selection) {
    if (selection === SEL_RETURN) {
        cm.dispose();
        cm.openNpc(9900001);
        return;
    }
    if (selection === SEL_CLAIM_EXP) {
        claimExp();
        return;
    }
    if (selection === SEL_CLAIM_DRP) {
        claimDrop();
        return;
    }
    levelMain();
}

function claimExp() {
    var claimed = getClaimedCount(EXTEND_KEY_EXP);
    if (claimed >= 经验券每日上限) {
        cm.sendOkLevel("Main", "今日经验双倍券已领完，请明天再来。");
        return;
    }
    if (!cm.canHold(经验券ID, 1)) {
        cm.sendOkLevel("Main", "背包空间不足，请清理背包后再来领取。");
        return;
    }

    cm.gainItem(经验券ID, 1);
    setClaimedCount(EXTEND_KEY_EXP, claimed + 1);

    cm.sendOkLevel("Main", "领取成功！获得经验双倍券 ×1\r\n今日已领取：#r" + (claimed + 1) + "#k / " + 经验券每日上限 + " 次");
}

function claimDrop() {
    var claimed = getClaimedCount(EXTEND_KEY_DRP);
    if (claimed >= 爆率券每日上限) {
        cm.sendOkLevel("Main", "今日爆率双倍券已领完，请明天再来。");
        return;
    }
    if (!cm.canHold(爆率券ID, 1)) {
        cm.sendOkLevel("Main", "背包空间不足，请清理背包后再来领取。");
        return;
    }

    cm.gainItem(爆率券ID, 1);
    setClaimedCount(EXTEND_KEY_DRP, claimed + 1);

    cm.sendOkLevel("Main", "领取成功！获得爆率双倍券 ×1\r\n今日已领取：#r" + (claimed + 1) + "#k / " + 爆率券每日上限 + " 次");
}

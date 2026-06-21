/**
 * @description 口令礼包系统
 * 功能：输入口令领取礼包道具，每个口令每账号只能兑换一次
 * 口令存储在CharacterExtendValue中，key="redeemed_codes"，value为已兑换口令的逗号分隔字符串
 */

var EXTEND_KEY = "redeemed_codes";

// 口令配置表：{ code: "口令字符串", items: [[itemId, qty], ...], msg: "提示语" }
var CODE_TABLE = [
    {
        code: "萧曳开服",
        items: [[2290138, 1], [1112000, 1]],
        msg: "感谢参与开服活动！获得2倍经验卡和精灵戒指！"
    },
    {
        code: "新年快乐",
        items: [[2022179, 100], [2022180, 100]],
        msg: "新年快乐！获得高级药水大礼包！"
    },
    {
        code: "周年庆典",
        items: [[2049100, 3], [2290138, 2]],
        msg: "周年庆快乐！获得混沌卷轴和2倍经验卡！"
    }
];

var status = -1;
var inputCode = "";

function start() {
    status = -1;
    inputCode = "";
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === 1) { status++; }
    else { cm.dispose(); return; }

    if (status === 0) {
        cm.sendGetText("欢迎来到口令礼包兑换！\r\n\r\n请输入口令：\r\n#d（每个口令每个角色只能使用一次）#k");
    } else if (status === 1) {
        inputCode = cm.getText().trim();
        redeemCode(inputCode);
    } else {
        cm.dispose();
    }
}

function redeemCode(code) {
    if (!code || code === "") {
        cm.sendOk("#r口令不能为空！#k");
        cm.dispose();
        return;
    }

    // 查找口令
    var found = null;
    for (var i = 0; i < CODE_TABLE.length; i++) {
        if (CODE_TABLE[i].code === code) {
            found = CODE_TABLE[i];
            break;
        }
    }

    if (found === null) {
        cm.sendOk("#r口令错误！#k 请检查后重新输入。");
        cm.dispose();
        return;
    }

    // 检查是否已兑换
    if (hasRedeemed(code)) {
        cm.sendOk("#r此口令已使用过！#k 每个口令每个角色只能使用一次。");
        cm.dispose();
        return;
    }

    // 检查背包空间
    for (var j = 0; j < found.items.length; j++) {
        var itemId = found.items[j][0];
        var qty = found.items[j][1];
        if (!cm.canHold(itemId, qty)) {
            cm.sendOk("#r背包空间不足！#k 请先整理背包再兑换。");
            cm.dispose();
            return;
        }
    }

    // 发放奖励
    for (var k = 0; k < found.items.length; k++) {
        cm.gainItem(found.items[k][0], found.items[k][1]);
    }

    // 记录已兑换
    markRedeemed(code);

    cm.getPlayer().dropMessage(6, "[" + cm.getPlayer().getName() + "玩家] 成功兑换口令礼包！");
    cm.sendOk("口令兑换成功！\r\n\r\n" + found.msg + "\r\n\r\n奖励已放入背包，请注意查收！");
    cm.dispose();
}

/** 检查口令是否已兑换 */
function hasRedeemed(code) {
    var val = cm.getCharacterExtendValue(EXTEND_KEY);
    if (val == null || val === "" || val === "null") return false;
    var codes = val.split(",");
    for (var i = 0; i < codes.length; i++) {
        if (codes[i] === code) return true;
    }
    return false;
}

/** 记录口令为已兑换 */
function markRedeemed(code) {
    var val = cm.getCharacterExtendValue(EXTEND_KEY);
    var newVal = (val == null || val === "" || val === "null") ? code : (val + "," + code);
    cm.saveOrUpdateCharacterExtendValue(EXTEND_KEY, newVal, false);
}

/**
 * 物品兑换（拍卖 → 兑换中心）
 * 修复：#v 空格导致客户端解析异常；选项未闭合 #l；扣材料前校验背包与下标。
 */

var itemSet = [
    [2049100, 4001006, 1],
    [2040920, 4001006, 1],
    [2040816, 4001006, 1],
    [2040915, 4001006, 10],
    [2340000, 4001126, 3],
    [5150040, 4001126, 100],
    [4250000, 4021007, 10],
    [4250001, 4250000, 10],
    [4250002, 4250001, 10],
    [4250800, 4005000, 10],
    [4250801, 4250800, 10],
    [4250802, 4250801, 10],
    [4250900, 4005001, 10],
    [4250901, 4250900, 10],
    [4250902, 4250901, 10],
    [4251000, 4005003, 10],
    [4251001, 4251000, 10],
    [4251002, 4251001, 10],
    [4251100, 4005002, 10],
    [4251101, 4251100, 10],
    [4251102, 4251101, 10],
    [4251200, 4000313, 2],
    [4251201, 4251200, 10],
    [4251202, 4251201, 10],
    [4251300, 4021008, 10],
    [4251301, 4251300, 10],
    [4251302, 4251301, 10],
    [4251400, 4005004, 10],
    [4251401, 4251400, 10],
    [4251402, 4251401, 10],
    [4310000, 4021010, 1]
];

var status = 0;
var selectedItem = -1;
var item;
var req;
var co;
var qty;
var cost;

function start() {
    status = 0;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode !== 1) {
        cm.dispose();
        return;
    }
    status++;

    if (status === 1) {
        var add = "请选择你想兑换的物品\r\n";
        for (var i = 0; i < itemSet.length; i++) {
            // 用文字+ID，避免个别自定义道具 #v 缺图闪退；常用卷轴仍可用 #t
            add += "\r\n#L" + i + "##b#t" + itemSet[i][0] + "##k (" + itemSet[i][0] + ")";
            add += "  需 #t" + itemSet[i][1] + "# ×" + itemSet[i][2] + "#l";
        }
        cm.sendSimple(add);
    } else if (status === 2) {
        if (selection < 0 || selection >= itemSet.length) {
            cm.sendOk("选项无效。");
            cm.dispose();
            return;
        }
        selectedItem = selection;
        item = itemSet[selectedItem][0];
        req = itemSet[selectedItem][1];
        co = itemSet[selectedItem][2];
        var bdd = "你确定要兑换：\r\n";
        bdd += "#b#t" + item + "##k (" + item + ")\r\n";
        bdd += "材料：#t" + req + "# ×" + co + " / 个\r\n\r\n";
        bdd += "请输入兑换数量：";
        cm.sendGetNumber(bdd, 1, 1, 100);
    } else if (status === 3) {
        qty = selection > 0 ? selection : 1;
        cost = co * qty;
        if (!cm.haveItem(req, cost)) {
            cm.sendOk("材料不足：需要 #t" + req + "# ×" + cost + "。");
            cm.dispose();
            return;
        }
        if (!cm.canHold(item, qty)) {
            cm.sendOk("背包空间不足，无法放入兑换物品。");
            cm.dispose();
            return;
        }
        cm.gainItem(req, -cost);
        cm.gainItem(item, qty);
        cm.sendOk("兑换成功：#t" + item + "# ×" + qty);
        cm.dispose();
    } else {
        cm.dispose();
    }
}

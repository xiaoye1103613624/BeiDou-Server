// 蝙蝠魔皮碎片兑换（挑战大厅）
var status = -1;

var EXCHANGES = [
    {cost: 5, item: 2040739, label: "蝙蝠怪的鞋子强化卷轴5%"},
    {cost: 10, item: 2340000, label: "祝福卷轴"},
    {cost: 10, item: 2049000, label: "白医卷轴"},
    {cost: 20, item: 2049100, label: "混沌卷轴60%"},
    {cost: 30, item: 2439101, label: "时装强化卷"},
    {cost: 30, item: 2439102, label: "装备强化卷"},
    {cost: 30, item: 2439103, label: "全能强化卷"}
];

var FRAGMENT = 4001261;

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode !== 1) {
        cm.dispose();
        return;
    }
    status++;

    if (status === 0) {
        var text = "你好，#h0#。使用蝙蝠魔皮碎片可以兑换以下物品：\r\n\r\n#b";
        for (var i = 0; i < EXCHANGES.length; i++) {
            var e = EXCHANGES[i];
            text += "#L" + i + "#" + e.cost + " 个碎片 兑换 " + e.label + "#l\r\n";
        }
        cm.sendSimple(text);
    } else if (status === 1) {
        var pick = EXCHANGES[selection];
        if (!cm.canHold(pick.item, 1)) {
            cm.sendOk("请先清出背包空间。");
        } else if (!cm.haveItem(FRAGMENT, pick.cost)) {
            cm.sendOk("需要 #b" + pick.cost + "#k 个蝙蝠魔皮碎片，数量不足。");
        } else {
            cm.gainItem(FRAGMENT, -pick.cost);
            cm.gainItem(pick.item, 1);
            cm.sendOk("兑换成功！");
        }
        cm.dispose();
    }
}

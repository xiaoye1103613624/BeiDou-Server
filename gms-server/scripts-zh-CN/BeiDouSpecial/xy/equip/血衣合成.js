var itemSet = new Array
(1012187,                                                               //（血衣代码）
    1012188,
    1012189,
    1012190,
    1041091
);


var matSet = new Array
(new Array(4000017, 4021000, 4021006, 4021002), //（材料代码）
    new Array(4021008, 4021001, 4021003, 4021007, 4000313, 1012187),
    new Array(4021008, 4021001, 4021003, 4021007, 4000313, 1012188),
    new Array(4021008, 4021001, 4021003, 4021007, 4000313, 1012189),
    new Array(4000313, 4000152, 4000151, 1012190)
);

var matQtySet = new Array
(new Array(2, 10, 10, 10),                                            //（材料个数）
    new Array(15, 15, 15, 15, 300, 1),
    new Array(15, 15, 15, 15, 300, 1),
    new Array(15, 15, 15, 15, 300, 1),
    new Array(300, 50, 50, 1)
);

var costSet = new Array
(12000000,                                                            //（需要金币）
    12000000,
    12000000
);

var status = 0;
var selectedItem;
var item;
var req;
var cost;


function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    status++;
    if (mode == -1) {
        cm.dispose();
        return;
    } else if (mode == 0) {
        cm.sendOk("go away!.");
        cm.dispose();
        return;
    }
    if (status == 1) {
        var add = "选择你要的装备\r\n";
        for (var i = 0; i < itemSet.length; i++) {
            add += "\r\n#L" + i + "##v " + itemSet[i] + "##z";
            add += itemSet[i] + "#\r\n";
        }
        ;

        cm.sendSimple(add, 2);
    } else if (status == 2) {

        selectedItem = selection;

        item = itemSet[selectedItem];
        mats = matSet[selectedItem];
        matQty = matQtySet[selectedItem];
        cost = costSet[selectedItem];

        var bdd = "你想要     ";
        bdd += "#i" + item + "# " + " #t" + item + "#";
        bdd += "\r\n 你需要 :\r\n";

        if (mats instanceof Array) {
            for (var i = 0; i < mats.length; i++) {
                bdd += "\r\n#i" + mats[i] + "# " + matQty[i] + " #t" + mats[i] + "#";
            }
        } else {
            bdd += "\r\n#i" + mats + "# " + matQty + " #t" + mats + "#";
        }
        if (cost > 0)
            bdd += "\r\n#i4031138# " + cost + " meso";

        cm.sendYesNo(bdd);
    } else if (status == 3) {
        var complete = true;

        if (!cm.canHold(item, 1)) {
            cm.sendOk("检查背包.");
            cm.dispose();
            return;
        } else if (cm.getMeso() < cost) {
            complete = false;
            cm.sendOk("#b 没钱走开.");
            cm.dispose();
            return;
        } else {
            if (mats instanceof Array) {
                for (var i = 0; complete && i < mats.length; i++)
                    if (!cm.haveItem(mats[i], matQty[i]))
                        complete = false;
            } else if (!cm.haveItem(mats, matQty))
                complete = false;
        }

        if (!complete) {
            cm.sendOk("物品不对.");
        } else {
            if (mats instanceof Array) {
                for (var i = 0; i < mats.length; i++) {
                    cm.gainItem(mats[i], -matQty[i]);
                }
            } else {
                cm.gainItem(mats, -matQty);
            }
            cm.gainMeso(-cost);
            cm.gainItem(item);
            cm.sendOk("#b 祝贺你");
            cm.dispose();
        }
        cm.dispose();
    }
}
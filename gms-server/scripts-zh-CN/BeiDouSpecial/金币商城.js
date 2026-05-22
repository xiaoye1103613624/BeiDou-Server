/*
 * ==================
 * 脚本类型: 金币商城
 * 脚本作者：北斗项目组
 * 功能说明：
 *   1. 使用金币购买各种道具
 *   2. 分类：消耗品、装备、材料、特殊道具
 * ==================
 */

var status = -1;
var currentCategory = -1;

var categories = [
    { name: "消耗品", items: [
        { id: 2000000, name: "红色药水", price: 500 },
        { id: 2000001, name: "橙色药水", price: 1500 },
        { id: 2000002, name: "白色药水", price: 4000 },
        { id: 2000003, name: "蓝色药水", price: 5000 },
        { id: 2000006, name: "特殊药水", price: 8000 },
        { id: 2002000, name: "万能疗伤药", price: 2000 },
        { id: 2002003, name: "矿泉水", price: 10000 },
        { id: 2022178, name: "召唤包", price: 50000 },
        { id: 2060000, name: "弓箭矢", price: 100 }
    ]},
    { name: "材料/矿石", items: [
        { id: 4000000, name: "蜗牛壳", price: 100 },
        { id: 4000001, name: "蘑菇盖", price: 200 },
        { id: 4000003, name: "树妖木块", price: 300 },
        { id: 4000012, name: "钢铁", price: 5000 },
        { id: 4000013, name: "银矿石", price: 3000 },
        { id: 4000014, name: "金矿石", price: 8000 },
        { id: 4000015, name: "钻石", price: 15000 },
        { id: 4000010, name: "蓝宝石", price: 5000 },
        { id: 4000011, name: "红宝石", price: 6000 },
        { id: 4000134, name: "制作宝石", price: 20000 }
    ]},
    { name: "卷轴", items: [
        { id: 2040000, name: "头盔防御卷轴10%", price: 50000 },
        { id: 2040001, name: "头盔体力卷轴10%", price: 60000 },
        { id: 2040003, name: "耳环防御卷轴10%", price: 50000 },
        { id: 2040100, name: "上衣防御卷轴10%", price: 50000 },
        { id: 2040300, name: "裤/裙防御卷轴10%", price: 50000 },
        { id: 2040500, name: "手套攻击卷轴10%", price: 80000 },
        { id: 2040600, name: "单手剑攻击卷轴10%", price: 70000 },
        { id: 2040700, name: "单手斧攻击卷轴10%", price: 70000 },
        { id: 2040800, name: "枪攻击卷轴10%", price: 70000 }
    ]},
    { name: "特殊道具", items: [
        { id: 4006000, name: "放大镜", price: 10000 },
        { id: 4006001, name: "道具探测器", price: 30000 },
        { id: 4030000, name: "速度光环卷", price: 5000 },
        { id: 4030001, name: "跳跃光环卷", price: 5000 },
        { id: 4031000, name: "女神的祝福", price: 100000 }
    ]}
];

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

    status++;

    if (status === 0) {
        var text = "#e#b=== 金币商城 ===#k#n\r\n\r\n";
        text += "当前金币：#b" + cm.getPlayer().getMeso().toLocaleString() + "#k\r\n";
        text += "#d" + "".padStart(26, "——") + "#k\r\n\r\n";
        text += "选择商品分类：\r\n\r\n";
        for (var i = 0; i < categories.length; i++) {
            text += "#L" + i + "##b" + categories[i].name + "#k (" + categories[i].items.length + "件)#l\r\n";
        }
        cm.sendSimple(text);
    } else if (status === 1) {
        currentCategory = selection;
        showItems(selection);
    } else if (status === 2) {
        buyItem(currentCategory, selection);
    }
}

function showItems(catIdx) {
    var cat = categories[catIdx];
    var text = "#e#b=== " + cat.name + " ===#k#n\r\n\r\n";
    text += "当前金币：#b" + cm.getPlayer().getMeso().toLocaleString() + "#k\r\n";
    text += "#d" + "".padStart(26, "——") + "#k\r\n\r\n";

    for (var i = 0; i < cat.items.length; i++) {
        var item = cat.items[i];
        text += "#L" + i + "#";
        text += "#i" + item.id + "# #b" + item.name + "#k ";
        text += "#r" + item.price.toLocaleString() + "金币#k";
        text += "#l\r\n";
    }

    cm.sendSimple(text);
}

function buyItem(catIdx, itemIdx) {
    var item = categories[catIdx].items[itemIdx];
    var player = cm.getPlayer();

    if (player.getMeso() < item.price) {
        cm.sendOk("金币不足！需要 #b" + item.price.toLocaleString() + "#k 金币。");
        cm.dispose();
        return;
    }

    player.gainMeso(-item.price);
    cm.gainItem(item.id, 1);
    cm.sendOk("购买成功！获得 #b#i" + item.id + "# " + item.name + "#k");
    cm.dispose();
}

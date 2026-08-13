/*
	快乐百宝箱 — 抽奖（快乐百宝券）+ 查看奖池
 */

var status;
var ticketId = 5220000;
var mapName = ["射手村", "魔法密林", "勇士部落", "废弃都市", "林中之城", "蘑菇神社", "昭和澡堂（男）", "昭和澡堂（女）", "玩具城", "新叶城", "冰峰雪域", "诺特勒斯号"];
var curMapName = "";

function start() {
    status = -1;
    curMapName = mapName[(cm.getNpc() != 9100117 && cm.getNpc() != 9100109) ? (cm.getNpc() - 9100100) : cm.getNpc() == 9100109 ? 9 : 11];
    action(1, 0, 0);
}

function showGachaPool() {
    try {
        var ServerManager = Java.type("org.gms.manager.ServerManager");
        var gachaponService = ServerManager.getApplicationContext().getBean("gachaponService");
        var rewards = gachaponService.getRewardsByNpcId(cm.getNpc());
        var text = "#e" + curMapName + "快乐百宝箱 · 奖池#n\r\n\r\n";
        if (rewards == null || rewards.size() === 0) {
            text += "#d当前奖池暂无配置，请联系管理员。#k";
        } else {
            var max = Math.min(rewards.size(), 80);
            for (var i = 0; i < max; i++) {
                var r = rewards.get(i);
                text += "#v" + r.getItemId() + "# #z" + r.getItemId() + "#\r\n";
            }
            if (rewards.size() > max) {
                text += "\r\n#d……共 " + rewards.size() + " 种，仅显示前 " + max + " 种#k";
            }
        }
        cm.sendOk(text);
    } catch (e) {
        cm.sendOk("奖池读取失败，请稍后重试。\r\n" + e);
    }
    cm.dispose();
}

function action(mode, type, selection) {
    if (mode < 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    } else {
        status--;
    }

    if (status == 0 && mode == 1) {
        if (cm.haveItem(ticketId)) {
            cm.sendSimple("欢迎来到" + curMapName + "快乐百宝箱！\r\n你持有 #b快乐百宝券#k。\r\n\r\n#L10#开始抽奖（消耗1张券）#l\r\n#L11#查看奖池#l\r\n#L12#什么是快乐百宝箱？#l");
        } else {
            cm.sendSimple("欢迎来到" + curMapName + "快乐百宝箱。我可以为您做些什么呢？\r\n\r\n#L11#查看奖池#l\r\n#L12#什么是快乐百宝箱？#l\r\n#L13#在哪里可以购买快乐百宝券？#l");
        }
    } else if (status == 1) {
        if (selection == 10) {
            if (!cm.haveItem(ticketId)) {
                cm.sendOk("你没有快乐百宝券。");
                cm.dispose();
                return;
            }
            if (cm.canHold(1302000) && cm.canHold(2000000) && cm.canHold(3010001) && cm.canHold(4000000)) {
                cm.gainItem(ticketId, -1);
                cm.doGachapon();
            } else {
                cm.sendOk("请确保你的#r装备、消耗、设置#k和#r其他#k物品栏中至少有一个空位。");
            }
            cm.dispose();
        } else if (selection == 11) {
            showGachaPool();
        } else if (selection == 12) {
            cm.sendNext("玩转快乐百宝箱，赢得稀有卷轴、装备、椅子、熟练书和其他酷炫物品！你只需要一张 #b快乐百宝券#k 就有机会成为随机物品的幸运获得者。");
        } else if (selection == 13) {
            cm.sendNext("快乐百宝券可以在#r现金商店#k购买，可以使用NX或枫叶点购买。点击屏幕右下角的红色商店图标进入#r现金商店#k，就能购买快乐百宝券。");
        } else {
            cm.dispose();
        }
    } else if (status == 2) {
        cm.dispose();
    } else {
        cm.dispose();
    }
}

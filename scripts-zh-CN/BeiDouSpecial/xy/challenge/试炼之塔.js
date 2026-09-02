/**
 * 试炼之塔 NPC脚本 - 北斗冒险岛
 * 功能: 选择Boss难度层级, 付费进入试炼副本
 * NPC映射: 可在自由市场放置, 调用本脚本
 *
 * 使用方式: 在自由市场设置NPC, 脚本指向 BeiDouSpecial/xy/challenge/试炼之塔.js
 * 或在portal中通过 cm.openNpc(npcId, "BeiDouSpecial/xy/challenge/试炼之塔") 调用
 *
 * @author 萧曵
 * @date 2026-07-30
 */

var TIERS = [
    { name: "初级试炼 (Lv50+)", boss: "初级试炼Boss", fee: 100000, reqLevel: 50, tierKey: "初级试炼" },
    { name: "中级试炼 (Lv100+)", boss: "中级试炼Boss", fee: 500000, reqLevel: 100, tierKey: "中级试炼" },
    { name: "高级试炼 (Lv150+)", boss: "高级试炼Boss", fee: 2000000, reqLevel: 150, tierKey: "高级试炼" },
    { name: "终极试炼 (Lv200+)", boss: "终极试炼Boss", fee: 10000000, reqLevel: 200, tierKey: "终极试炼" }
];

var status = 0;

function start() {
    cm.sendNext(
        "#b⚔ 试炼之塔 - 北斗冒险岛#k\r\n\r\n" +
        "这里是为勇者准备的Boss挑战场!\r\n" +
        "从186版移植的强大Boss等你来挑战。\r\n\r\n" +
        "#r规则:#k\r\n" +
        "- 单人进入, 限时30分钟\r\n" +
        "- 击败Boss获得丰厚奖励\r\n" +
        "- 超时或死亡则挑战失败\r\n" +
        "- 需支付入场费(不退还)"
    );
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
        return;
    }

    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }

    if (status == 1) {
        var text = "#b请选择试炼难度:#k\r\n\r\n";
        for (var i = 0; i < TIERS.length; i++) {
            var t = TIERS[i];
            var canEnter = cm.getLevel() >= t.reqLevel;
            var color = canEnter ? "#b" : "#r";
            text += "#L" + i + "#" + color + t.name + " | Boss: " + t.boss + " | 入场费: " + t.fee.toLocaleString() + "金币#k";
            if (!canEnter) text += " #r(等级不足)#k";
            text += "#l\r\n";
        }
        text += "\r\n#L" + TIERS.length + "##r离开#k#l";
        cm.sendSimple(text);
    } else if (status == 2) {
        if (selection >= TIERS.length) {
            cm.sendOk("期待你的挑战!");
            cm.dispose();
            return;
        }

        var tier = TIERS[selection];

        if (cm.getLevel() < tier.reqLevel) {
            cm.sendOk("你的等级不足 #b" + tier.reqLevel + "#k，无法进入此试炼!");
            cm.dispose();
            return;
        }

        if (cm.getMeso() < tier.fee) {
            cm.sendOk("金币不足! 需要 #b" + tier.fee.toLocaleString() + "金币#k。");
            cm.dispose();
            return;
        }

        // 检查是否已在事件中
        if (cm.getEventInstance() != null) {
            cm.sendOk("你已经在其他副本中了!");
            cm.dispose();
            return;
        }

        // 扣费并进入
        cm.gainMeso(-tier.fee);
        cm.getClient().getChannelServer().getEventSM().getEventManager("TrialTower")
            .startInstance(tier.tierKey, cm.getPlayer());
        cm.dispose();
    }
}

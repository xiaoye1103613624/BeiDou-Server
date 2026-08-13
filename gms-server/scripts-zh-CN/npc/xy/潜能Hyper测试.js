var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
        return;
    }
    if (mode == 0 && status == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) status++;
    else status--;

    if (status == 0) {
        cm.sendSimple(
            "#e潜能 / Hyper（Phase4）#n\r\n" +
            "含附加潜能、灵魂宝珠、魔方、品阶、星岩。\r\n\r\n" +
            "#L0#查看：!potential#l\r\n" +
            "#L1#领取 Phase1~3 卷#l\r\n" +
            "#L2#领取 Phase4 卷（魔方/灵魂/星岩）#l\r\n" +
            "#L3#说明#l"
        );
    } else if (status == 1) {
        if (selection == 0) {
            cm.getPlayer().dropMessage(5, "请用 GM 命令: !potential");
            cm.dispose();
        } else if (selection == 1) {
            cm.gainItem(2049402, 10);
            cm.gainItem(2049300, 10);
            cm.gainItem(2049902, 10);
            cm.sendOk("已发放潜能/Hyper/附加潜能卷各×10。");
            cm.dispose();
        } else if (selection == 2) {
            // Phase4 自研 ID（勿用经典 2049700=A级潜能卷轴 / 2049800=神奇魔方）
            cm.gainItem(2049910, 10);
            cm.gainItem(2049911, 10);
            cm.gainItem(2049912, 10);
            cm.gainItem(2049913, 10);
            cm.gainItem(2049914, 10);
            cm.gainItem(2049915, 5);
            cm.sendOk(
                "已发放：\r\n" +
                "#v2049910# 潜能魔方 ×10\r\n" +
                "#v2049911# 附加魔方 ×10\r\n" +
                "#v2049912# 品阶提升 ×10\r\n" +
                "#v2049913# 星岩 ×10\r\n" +
                "#v2049914# 灵魂附加 ×10 / #v2049915# 清除 ×5"
            );
            cm.dispose();
        } else {
            cm.sendOk(
                "1) 20494* 主潜能 / 2049902 附加潜能 / 20493* Hyper\r\n" +
                "2) 2049910 魔方 / 2049911 附加魔方 / 2049912 品阶\r\n" +
                "3) 2049914 灵魂 / 2049915 清灵魂 / 2049913 星岩\r\n" +
                "4) 经典 2049700=A级潜能卷轴（非魔方）\r\n" +
                "5) GM：!potential cube/grade/soul/socket …\r\n" +
                "6) 服务端+ijl15 须同更（封包 0x13C）"
            );
            cm.dispose();
        }
    }
}

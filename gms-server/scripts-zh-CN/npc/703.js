// NPC 703 · 米拉 · 遗忘山谷任务链 10603-10604
var status = -1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode !== 1) {
        cm.dispose();
        return;
    }
    status++;

    if (status === 0) {
        var text = "我的同伴……他们都死了。\r\n";
        text += "遗忘之谷里潜伏着可怕的东西。\r\n\r\n";
        if (cm.getQuestStatus(10603) === 0) {
            text += "#L0#接受任务：亡者的愿望#l\r\n";
        } else if (cm.getQuestStatus(10603) === 1) {
            text += "#L1#汇报讨伐进度#l\r\n";
        } else if (cm.getQuestStatus(10604) === 0) {
            text += "#L2#接受任务：亡者的遗言#l\r\n";
        } else if (cm.getQuestStatus(10604) === 1) {
            text += "#L3#关于腐朽树枝#l\r\n";
        } else {
            text += "谢谢你……愿他们安息。";
        }
        text += "\r\n#L9#离开#l";
        cm.sendSimple(text);
        return;
    }

    if (selection === 9) {
        cm.dispose();
        return;
    }

    if (selection === 0) {
        cm.forceStartQuest(10603);
        cm.sendOk("请帮同伴讨伐安眠：打倒 #r#o61##k 与 #r#o63##k。");
    } else if (selection === 1) {
        var k61 = cm.getQuestProgressInt(10603, 61);
        var k63 = cm.getQuestProgressInt(10603, 63);
        if (k61 >= 30 && k63 >= 30) {
            cm.forceCompleteQuest(10603);
            cm.gainExp(10000);
            cm.sendOk("谢谢……还有一件事，想拜托你。");
        } else {
            cm.sendOk("进度：#o61# " + k61 + "/30，#o63# " + k63 + "/30。\r\n请继续讨伐。");
        }
    } else if (selection === 2) {
        cm.forceStartQuest(10604);
        cm.sendOk(
            "请把 #b#t4032901##k 交给 #b#m101000003##k 的 #b#p1032001##k。\r\n" +
            "没有的话可从 #r#o63##k 身上取得。"
        );
    } else if (selection === 3) {
        if (cm.haveItem(4032901, 1)) {
            cm.sendOk("带着树枝去找 #p1032001# 吧，他就在魔法密林图书馆。");
        } else {
            cm.sendOk("请先从 #o63# 身上取得 #t4032901#。");
        }
    }
    cm.dispose();
}

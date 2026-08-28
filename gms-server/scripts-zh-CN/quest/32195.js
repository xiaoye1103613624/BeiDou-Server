var status = -1;

function start(mode, type, selection) {
    if (mode == -1) {
        qm.sendOk("在你决定是否要进行这个任务之后再跟我说。如果你决定不做，你不会错过任何机会。");
        qm.dispose();
    } else {
        if (mode == 0 && type > 0) {
            qm.sendOk("在你决定是否要进行这个任务之后再跟我说。如果你决定不做，你不会错过任何机会。");
            qm.dispose();
            return;
        }

        if (mode == 1) {
            status++;
        } else {
            status--;
        }

        if (status == 0) {
            qm.sendYesNo("我听说了你的出色表现，便来见见你。你在这地方立了大功呢？");
        } else if (status == 1) {
            qm.sendNext("你对这地方有什么感想？\r\n\r\n企鹅族、阿拉斯加犬族、海象族它们曾经彼此憎恨。尽管现在它们已经一笑泯恩仇，可要是它们从一开始就没有互相争斗，维持着合作关系，也许就可以阻止今天这样的事情发生。");
        } else if (status == 2) {
            qm.sendOk("大家没必要非得思想一致，可如果在共同的敌人面前，有想要守护的东西的话，至少就应该齐心协力来和敌人对抗。作为冒险岛世界的一员，希望你要牢牢记住这句话。\r\n\r\n #i1142629# #b#t1142629##k");
            qm.forceStartQuest();
            qm.forceCompleteQuest();
            qm.gainExp(38805);
            qm.gainItem(1142629, 1);
            qm.dispose();
        }
    }
}

function end(mode, type, selection) {
    qm.dispose();
}

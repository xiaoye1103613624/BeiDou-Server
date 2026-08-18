function start() {
    if(cm.getLevel() >= 30) {
    cm.sendSimple("想要看看你的努力成果吗？所有兼职工作的帮助都由我来处理，#bMs. Appropriation#k。\r\n#b#e#L0# 接受兼职工作奖励。#l");
} else {
cm.sendOk("你好。我是Miss Appropriation，负责兼职工作。很抱歉，在达到 #e30级#n之前，我不能给你任何工作，但是当你达到这个等级时，请来找我。");
    }

function action(mode, type, selection) {
    if (mode != 1) {
        cm.dispose();
        return;
    }
    if (cm.getPlayer().getPartTime(cm.getPlayer().getId()).getJob() > 0) {
        cm.sendNext("The fruits of labors are always sweet. I hope to see you again.");
        //cm.partTimeReward();
    } else {
        cm.sendOk("Hmm... Are you sure you completed the Part-Time Job? There are no rewards available right now.");
    }
    cm.dispose();
  }
 }
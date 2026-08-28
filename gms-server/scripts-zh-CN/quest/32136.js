/* Dawnveil
    [主题地下城] 艾利涅尔仙女学院
    与巴尔洛戈共舞
    由丹妮莉丝制作
*/
var status = -1;

function start(mode, type, selection) {
    if (mode == 1)
        status++;
    else
        status--;
    if (status == 0) {
        qm.sendAcceptDecline("你看起来状态不错。要再接一个任务吗？我收到了来自#b艾利涅尔仙女学院#的紧急请求。");
    } else if (status == 1) {
        qm.sendNext("一个年轻的人类闯进了#b艾利涅尔仙女学院#k，引起了很大的骚动。");
    } else if (status == 2) {
        qm.sendNextPrev("我不知道所有的细节，但我知道我们与仙女们的关系已经足够紧张了。你愿意去艾林尼亚附近的北森林与#b范茨#k见面吗？");
    } else if (status == 3) {
        qm.sendYesNo("范茨会带你进入仙女之地。如果你愿意，我可以直接把你送到他那里。");
    } else if (status == 4) {
        qm.warp(101020000, 0);
        qm.forceStartQuest();
        qm.dispose();
    }
}

function end(mode, type, selection) {
    if (mode == 0 && type == 0) {
        status--;
    } else if (mode == -1) {
        qm.dispose();
        return;
    } else {
        status++;
    }
    if (status == 0) {
        qm.sendNext("你就是我邀请来帮助解决艾利涅尔仙女学院骚动的人吗？");
    } else if (status == 1) {
        qm.sendNextPrev("嗯，当然是我了。", 15);
    } else if (status == 2) {
        qm.sendNextPrev("你看起来没有我期望的那么强大。但是，你很有名，所以我就交给你了。");
    } else if (status == 3) {
        qm.forceCompleteQuest();
        qm.dispose();
    }
}
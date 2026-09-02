var status = -1;

function start(mode, type, selection) {
    if (mode == -1) {
        qm.sendOk("在你决定是否要进行这个任务之后再跟我说。如果你决定不做，你不会错过任何机会。");
        qm.dispose();
    } else {
        if (mode == 0 && type > 0 || selection == 1) {
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
            qm.sendNext("我研究的是森林里的各种生物。在森林中，需要精确捕捉所需生命体的声音时，有时也会用上这个工具。只要捕捉到声音的话，就能大致知道方向和距离。\r\n\r\n#i4033830##b#t4033830##k");
        } else if (status == 1) {
            qm.sendAcceptDecline("虽然不知道是否能派上用场，但总比没有强啊。请把那些不幸的孩子拯救出来吧。\r\n\r\n（接受后，移动到妖精学院艾利涅。）");
        } else if (status == 2) {
            qm.gainItem(4033830, 1);
            qm.warp(101071300, 0);
            qm.forceStartQuest();
            qm.dispose();
        }
    }
}

function end(mode, type, selection) {
    if (mode == -1) {
        qm.dispose();
    } else {
        if (mode == 1) {
            status++;
        } else {
            status--;
        }
        if (status == 0) {
            qm.sendNext("你来啦。有收获吗？");
        } else if (status == 1) {
            qm.sendNextPrev("(给妖精们看巴缇博士的东西，并说明其功能。)");
        } else if (status == 2) {
            qm.sendNextPrev("……那么，现在要让我们使用这不纯洁的人类的东西？不行！绝对不行！死也不行！");
        } else if (status == 3) {
            qm.sendNextPrev("可现在别无他法，副校长先生。");
        } else if (status == 4) {
            qm.sendNextPrev("罗雯的话没错。现在最首要的任务就是要找到那些孩子，不是吗？");
        } else if (status == 5) {
            qm.sendNextPrev("我虽不赞同，但目前只有这个方法。");
        } else if (status == 6) {
            qm.sendNextPrev("……呃……只好那样了，不过只此一回……不，不管怎么说……");
        } else if (status == 7) {
            qm.sendNextPrev("我来启动试试。请大家暂时安静。");
        } else if (status == 8) {
            qm.sendNextPrev("好像捕捉到了森林里的各种声音……");
        } else if (status == 9) {
            qm.sendNext("#b吱吱吱…#k");
        } else if (status == 10) {
            qm.sendNext("？？？");
        } else if (status == 11) {
            qm.sendNext("#b嘭……嘭……#k");
        } else if (status == 12) {
            qm.sendNext("什么嘛，除了杂音什么也听不见。");
        } else if (status == 13) {
            qm.sendNextPrev("嘘……安静。");
        } else if (status == 14) {
            qm.sendNext("#b呜呜……救命啊……呜呜。#k");
        } else if (status == 15) {
            qm.sendNext("！！这声音是！");
        } else if (status == 16) {
            qm.sendNextPrev("是后院的方向。");
        } else if (status == 17) {
            qm.sendNextPrev("等着我，孩子们！我副校长去救你们啦！");
        } else if (status == 18) {
            qm.sendNextPrev("艾温，我们也一起去寻找孩子们吧！");
        } else if (status == 19) {
            qm.sendNextPrev("各位，请等一下……！");
        } else if (status == 20) {
            qm.gainExp(8409);
            qm.forceCompleteQuest(32120);
            if (qm.haveItem(4033830)) {
                qm.gainItem(4033830, -1);
            }
            qm.dispose();
        }
    }
}

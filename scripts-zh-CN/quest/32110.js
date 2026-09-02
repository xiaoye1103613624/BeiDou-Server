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
            qm.sendNext("你来啦，#h0#。那么开始搜索吧？");
        } else if (status == 1) {
            // 原脚本 sendNextPrevS：玩家说话
            qm.sendNextPrev("从哪里开始入手呢？");
        } else if (status == 2) {
            qm.sendNextPrev("你知道孩子们最喜欢什么吗？就是他们之间的秘密。我也常常背着师傅，偷偷和其他弟子交换纸条，然后偷偷地笑呢。有时也在秘密的地方藏东西。那种事情可是非常有趣的呢。");
        } else if (status == 3) {
            qm.sendSimple("凭我的直觉，孩子们之间有个共同的秘密。那个秘密应该就是关键。不过现在的问题是，要怎样做才能找到孩子们的秘密呢？\r\n\r\n#L1##b先找到孩子们之后，再询问秘密。#l\r\n#L2##b寻找一下写有秘密的纸条如何？#l\r\n#L3##b我也不知道，你怎么看？#l");
        } else if (status == 4) {
            if (selection == 1) {
                qm.sendNext("#h0#，清醒一点。我不是说过嘛，我们得先掌握那个秘密，才能找到孩子。你前后颠倒啦！");
                status -= 2;
            } else if (selection == 2) {
                qm.sendNext("这个想法不错。最好寻找一下孩子们在上课时偷偷交换的纸条。");
            } else {
                qm.sendNext("我也是没有什么好办法所以才问的。说说你的看法吧。");
                status -= 2;
            }
        } else if (status == 5) {
            qm.sendAcceptDecline("这些#r下级魔法书#k身上肯定有纸条。据我刚刚观察，你好像身手不错。那你应该能消灭#r下级魔法书#k，并找出#b男生们的纸条#k吧？");
        } else if (status == 6) {
            qm.sendNext("查看纸条时，上面会有有用的信息，当然也会有没用的信息。\r\n所以每次获得纸条的时候，必须对内容进行确认！\r\n（消灭#r下级魔法书#k，搜寻#b男生们的纸条#k，并确认内容。）");
        } else if (status == 7) {
            qm.forceStartQuest();
            qm.dispose();
        }
    }
}

function end(mode, type, selection) {
    qm.dispose();
}

/*
 * 任务 32102 - 妖精学院艾利涅（进入学院）
 * 由 Kmst/HeavenMS 写法(cm) 改写为 北斗GMS083 风格(qm)
 *
 * 改写对照：
 *   cm.sendNormalTalk(msg,0,npcId,false,true)  -> qm.sendNext(msg)      首句，只有下一页
 *   cm.sendNormalTalk(msg,0/2,npcId,true,true)  -> qm.sendNextPrev(msg)  中间对话，有上一页+下一页
 *   cm.sendNormalTalk(msg,0,npcId,false,true)   -> qm.sendOk(msg)        末句+启动任务
 *   cm.askAcceptDecline(msg,0,npcId)             -> qm.sendAcceptDecline(msg)
 *   cm.askYesNo(msg)                             -> qm.sendYesNo(msg)
 *   cm.forceStartQuest(questId,"")               -> qm.forceStartQuest()
 *   cm.forceCompleteQuest()                      -> qm.forceCompleteQuest()
 *   speakerType 0/2 (NPC/玩家) 在GMS083中不区分，由客户端自动处理
 *   selectionLog 数组在GMS083中不需要
 *   stage0 函数在GMS083中没有对应概念，跳过
 */

var status = -1;

function start(mode, type, selection) {
    if (mode == -1) {
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
            // NPC说话，首句，只有下一页
            qm.sendNext("你问这里是哪里？你连这里是哪里都不知道就跟来了吗？\r\n这里是通往#b妖精学院艾利涅#k的森林深处。");
        } else if (status == 1) {
            // 玩家说话，有上一页+下一页
            qm.sendNextPrev("妖精学院艾利涅？");
        } else if (status == 2) {
            // NPC说话
            qm.sendNextPrev("没错，#b艾利涅#k是多年来教导妖精孩童们魔法的一种教育机构。");
        } else if (status == 3) {
            // 玩家说话
            qm.sendNextPrev("那为什么要藏在这种森林深处啊？");
        } else if (status == 4) {
            // NPC说话
            qm.sendNextPrev("看你的表情，还真是一无所知的样子，你知道#b魔法密林#k原本曾是妖精的村庄吗？在数百年前和黑魔法师爆发过一场大型战争之后，人类进来开垦了村庄，便有了现如今的#b魔法密林#k。\r\n");
        } else if (status == 5) {
            // 玩家说话
            qm.sendNextPrev("那么魔法密林外面也有妖精生活的地方咯。");
        } else if (status == 6) {
            // NPC说话
            qm.sendNextPrev("虽然也有一些愿意接受人类的妖精，但还是有很多并非如此相对保守的妖精，#b妖精学院艾利涅#k也曾如此。他们拒绝与人类为伍，独自消失在夜的领域中，而且为了拒绝外人的出入，他们还建在了湖的对面。\r\n");
        } else if (status == 7) {
            // 玩家说话
            qm.sendNextPrev("你的意思是说魔法师库迪被那些艾利涅的妖精生擒了吗？");
        } else if (status == 8) {
            // NPC说话，接受/拒绝对话框
            qm.sendAcceptDecline("没错，我也完全没有搞明白这到底是怎么一回事，虽然#b汉斯#k和我曾经多次尝试去联系，但他们很讨厌我们，并不听我们说话。所以我们需要你的帮忙，\r\n不过#b#h0##k，你游泳游得好吗？");
        } else if (status == 9) {
            // NPC说话，末句 + 启动任务
            qm.sendOk("你先游到湖对面怎么样？相信你应该能做到！鼓起勇气跃入水中吧！\r\n#b(度过右边的湖。)#k");
            qm.forceStartQuest();
            qm.dispose();
        }
    }
}

function end(mode, type, selection) {
    if (mode == -1) {
        qm.dispose();
    } else {
        if (mode == 0 && type > 0 || selection == 1) {
            qm.sendOk("在你决定是否要完成这个任务之后再跟我说。");
            qm.dispose();
            return;
        }

        if (mode == 1) {
            status++;
        } else {
            status--;
        }

        if (status == 0) {
            // 原Kmst脚本为未修复占位，此处简化
            qm.sendYesNo("这个任务的结束脚本还没有修复哦。你要立刻完成这个任务吗？");
        } else if (status == 1) {
            qm.forceCompleteQuest();
            qm.dispose();
        }
    }
}

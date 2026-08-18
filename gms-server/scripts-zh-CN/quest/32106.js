/*
 * 任务 32106 - 妖精学院艾利涅（听取双方意见）
 * 由 Kmst/HeavenMS 写法(cm) 改写为 北斗GMS083 风格(qm)
 *
 * 改写对照：
 *   cm.sendNormalTalk(msg,4,npcId,false,true)  -> qm.sendNext(msg)       首句只有下一页
 *   cm.sendNormalTalk(msg,4/2,npcId,true,true)  -> qm.sendNextPrev(msg)   中间对话有上一页+下一页
 *   末句对话+forceStartQuest                      -> qm.sendOk(msg) + forceStartQuest + dispose
 *   cm.sendNext(msg)                              -> qm.sendNext(msg)
 *   cm.sendNextPrev(msg)                          -> qm.sendNextPrev(msg)
 *   cm.forceStartQuest(32106,"")                  -> qm.forceStartQuest()
 *   cm.forceCompleteQuest(32106)                  -> qm.forceCompleteQuest()
 *   cm.gainExp(3630)                              -> qm.gainExp(3630)
 *   speakerType 4/2 在GMS083中不区分，由客户端自动处理
 *   NPC ID 1500001/1500002 不需要传给GMS083对话API
 *
 * 对话角色说明（原脚本 speakerType）：
 *   speakerType 4, npcId 1500001 → 校长（妖精）
 *   speakerType 4, npcId 1500002 → 妖精（激进派）
 *   speakerType 2, npcId 1500001 → 玩家（内心独白/回应）
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
            qm.sendNext("你还没回去啊。还有什么要说的吗？");
        } else if (status == 1) {
            qm.sendNextPrev("哼，显而易见。他的辩解实在是令人难以信服。异邦人的话不能相信，校长先生。");
        } else if (status == 2) {
            qm.sendNextPrev("#b#h0#：#k\r\n#b我也只是受人之托来化解误会的。因此，我得先掌握清楚互相之间正确的事实关系，不是吗？#k");
        } else if (status == 3) {
            qm.sendNextPrev("哼，别废话了！我们5个孩子一下子消失了。这不是绑架是什么？");
        } else if (status == 4) {
            qm.sendNextPrev("#b#h0#：#k\r\n#b有证据能够证明是魔法师库迪犯下的罪行吗？#k");
        } else if (status == 5) {
            qm.sendNextPrev("那个叫库迪的魔法师在我们森林附近出没也不是一两次了。虽然把他赶跑很多次，但他还是偷偷地干着什么奇怪的勾当。");
        } else if (status == 6) {
            qm.sendNextPrev("原来，为了干坏事，他已经事先考察过现场了吧。他想趁着放假，老师们全部去休假的空当，对孩子们下手。不过犯人总归会再次出现在现场。所以当他在这附近游荡的时候，被我抓了个正着。");
        } else if (status == 7) {
            qm.sendNextPrev("#b#h0#：#k\r\n#b(5个孩子消失，此事的确非同寻常。但果真是库迪所为吗？)#k");
        } else if (status == 8) {
            qm.sendNextPrev("你说需要冷静思考问题，我也理解。但作为我们来讲，首先必须得对有嫌疑的人进行训问。");
        } else if (status == 9) {
            qm.sendOk("#b#h0#：#k\r\n#b(他们因为担心孩子，现在正处于非常兴奋的状态。看来最好听听魔法师库迪本人的话。)#k");
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
            qm.sendNext("你是来救我的吗？");
        } else if (status == 1) {
            qm.sendOk("你来的正好，请听我说。我根本不是犯人。我为何要绑架那些妖精呢？");
            qm.gainExp(3630);
            qm.forceCompleteQuest();
            qm.dispose();
        }
    }
}

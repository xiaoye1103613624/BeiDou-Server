// NPC 1514003 对话脚本 — 配合 enter141040002 使用
// 饲养者与小家伙们的对话，最终 warp 到 141040003
//
// 对话内容（括号标注原版说话人）：
//   0: "小家伙们……肚子饿不？"                    (1514003 饲养者)
//   1: "汪汪！"                                    (1514005)
//   2: "呱呱呱呱！"                                (1514004)
//   3: "唔唔！"                                    (1514006)
//   4: "什么，你们说话我听不懂啊。唉……"            (1514003)
//   5: "这些稚嫩的小家伙那么渴望生存……"            (1514003)
//   6: "汪汪！"                                    (1514005)
//   7: "叽叽……"                                    (1514004)
//   8: "嗡嗡？"                                    (1514006)
//   9: "好，好，知道了。我这就给你们找吃的……"      (1514003)

var status = -1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode < 0) {
		cm.forceStartQuest(32192);
        cm.warp(141040003, 0);
        cm.dispose();
        return;
    }
    status++;

    if (status === 0) {
        cm.sendNext("小家伙们……肚子饿不？\r\n#r（后续剧情如果没有触发请点击 #p1514003# 继续，或者和 #p1510006# 对话返回总部交任务）#k");
    } else if (status === 1) {
        cm.sendNext("#b#p1514005#：#k\r\n汪汪！");
    } else if (status === 2) {
        cm.sendNext("#b#p1514004#：#k\r\n呱呱呱呱！");
    } else if (status === 3) {
        cm.sendNext("#b#p1514006#：#k\r\n唔唔！");
    } else if (status === 4) {
        cm.sendNext("什么，你们说话我听不懂啊。唉……");
    } else if (status === 5) {
        cm.sendNext("这些稚嫩的小家伙那么渴望生存……都大张着嘴等着食物。");
    } else if (status === 6) {
        cm.sendNext("#b#p1514005#：#k\r\n汪汪！");
    } else if (status === 7) {
        cm.sendNext("#b#p1514004#：#k\r\n叽叽……");
    } else if (status === 8) {
        cm.sendNext("#b#p1514006#：#k\r\n嗡嗡？");
    } else if (status === 9) {
        cm.sendNext("好，好，知道了。我这就给你们找吃的。我想想，这附近有没有好钓到鱼的地方呢？");
    } else {
		cm.forceStartQuest(32192);
        cm.warp(141040003, 0);
        cm.dispose();
    }
}

// NPC 1510008 对话脚本 — 配合 enter141040003 使用
// 文件部署名: 1510008_reunion.js (放 scripts/npc/ 目录)
// 三个种族与魔女芭芭拉重逢和解的剧情，最终 forceStartQuest(32192) + warp 到 141000000
//
// 对话内容（括号标注原版说话人）：
//   0: "真不敢相信……你竟是小时候救了我们的大恩人啊！"  (1510000)
//   1: "我隐隐约约能记起来……但没想到她竟是人类……"        (1510003)
//   2: "你在这里做这样的善事已经至少有数十年了……"       (1510005)
//   3: "真让人感动啊，老奶奶。"                          (1510007)
//   4: "少肉麻，好久不见，吃顿饭再走吧。"                (1510008 芭芭拉)
//   5: "还有，你们这帮家伙，别再相互斗了……"             (1510008 芭芭拉)
//   6: "…？"                                            (1510000)
//   7: "#r#e见到你们那样，我这老婆子都要伤心死了！#n#k"   (1510008 芭芭拉)
//   8: "呃……知道了！"                                   (1510000)
//   9: "太好了。今后三个种族之间再不会互相斗了吧？"        (1510007)
//  10: "哈哈，这全要归功于航海士啊。"                    (1510006)

var status = -1;
var jq=0;
function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode < 0) {
        // 玩家跳过/关闭对话 — 仍然传送，避免卡图
		cm.forceStartQuest(32192);
        cm.warp(141000000, 0);
        cm.dispose();
        return;
    }
    status++;

    if (status === 0) {
        cm.sendNext("#b#p1510000#：#k\r\n真不敢相信……我都想不起来了，你竟是小时候救了我们的大恩人啊！\r\n#r（后续剧情如果没有触发可以直接和 #p1510006# 对话返回总部交任务）#k");
		cm.forceStartQuest(32192);
    } else if (status === 1) {
        cm.sendNext("#b#p1510003#：#k\r\n我隐隐约约能记起来，被温暖的大手照料的那种感觉……但没想到她竟是人类，甚至还被我们称作魔女。");
    } else if (status === 2) {
        cm.sendNext("#b#p1510005#：#k\r\n这么说，你在这里做这样的善事已经至少有数十年了。天啊……对我们种族来说，你是像母亲一样的恩人啊！");
    } else if (status === 3) {
        cm.sendNext("#b#p1510007#：#k\r\n真让人感动啊，老奶奶。");
    } else if (status === 4) {
        cm.sendNext("少肉麻，好久不见，吃顿饭再走吧。");
    } else if (status === 5) {
        cm.sendNext("还有，你们这帮家伙，别再相互斗了……");
    } else if (status === 6) {
        cm.sendNext("#b#p1510000#：#k\r\n…？");
    } else if (status === 7) {
        cm.sendNext("#r#e见到你们那样，我这老婆子都要伤心死了！#n#k");
    } else if (status === 8) {
        cm.sendNext("#b#p1510000#：#k\r\n呃……知道了！");
    } else if (status === 9) {
        cm.sendNext("#b#p1510007#：#k\r\n太好了。今后三个种族之间再不会互相斗了吧？");
    } else if (status === 10) {
        cm.sendNext("#b#p1510006#：#k\r\n哈哈，这全要归功于航海士啊。");
    } else {
        cm.forceStartQuest(32192);
        cm.warp(141000000, 0);
        cm.dispose();
    }
}

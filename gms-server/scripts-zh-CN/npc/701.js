// NPC 701 · 泽莉亚 · 遗忘山谷任务链 10600-10602
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
        var text = "你好，旅行者。我是泽莉亚。\r\n";
        text += "露玛太粗心了……有一次，她把地图丢到洞穴深处害我无家可归。\r\n\r\n";
        if (cm.getQuestStatus(10600) === 0) {
            text += "#L0#接受任务：归家之路（收集地图碎片）#l\r\n";
        } else if (cm.getQuestStatus(10600) === 1) {
            text += "#L1#交还地图碎片（#c4032900#/25）#l\r\n";
        } else if (cm.getQuestStatus(10601) === 0) {
            text += "#L2#接受任务：拼合地图#l\r\n";
        } else if (cm.getQuestStatus(10601) === 1) {
            text += "#L3#交付黏合材料（#c4000901#/25）#l\r\n";
        } else if (cm.getQuestStatus(10602) === 0) {
            text += "#L4#接受任务：清扫归途#l\r\n";
        } else if (cm.getQuestStatus(10602) === 1) {
            text += "#L5#汇报清扫进度#l\r\n";
        } else {
            text += "谢谢你……我已经能看清回家的路了。\r\n";
            text += "深处还有米拉，她或许也需要帮助。";
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
        cm.forceStartQuest(10600);
        cm.sendOk("请帮我找回 #b25个地图碎片#k（#t4032900#）。\r\n发光蘑菇仔身上可能会掉落。");
    } else if (selection === 1) {
        if (cm.haveItem(4032900, 25)) {
            cm.gainItem(4032900, -25);
            cm.forceCompleteQuest(10600);
            cm.gainExp(6000);
            cm.sendOk("拼起来了！不过还缺黏合材料……能再帮我一次吗？");
        } else {
            cm.sendOk("还不够 25 片……请继续收集 #t4032900#。");
        }
    } else if (selection === 2) {
        cm.forceStartQuest(10601);
        cm.sendOk("据说 #r#o55##k 身上可以弄到黏合剂。\r\n请带回 #b25个 #t4000901##k。");
    } else if (selection === 3) {
        if (cm.haveItem(4000901, 25)) {
            cm.gainItem(4000901, -25);
            cm.forceCompleteQuest(10601);
            cm.gainExp(6000);
            cm.sendOk("地图拼好了！可归途被怪物堵住了……");
        } else {
            cm.sendOk("黏合材料还不够，需要 #b25个 #t4000901##k。");
        }
    } else if (selection === 4) {
        cm.forceStartQuest(10602);
        cm.sendOk("请各打倒 #b50只#k #r#o57##k 与 #r#o58##k，好让我安全归家。");
    } else if (selection === 5) {
        // Quest WZ tracks mob kills under progress keys = mobId
        var k57 = cm.getQuestProgressInt(10602, 57);
        var k58 = cm.getQuestProgressInt(10602, 58);
        if (k57 >= 50 && k58 >= 50) {
            cm.forceCompleteQuest(10602);
            cm.gainExp(8000);
            cm.gainItem(2040803, 1);
            cm.sendOk("谢谢你！这条路清出来了。手套卷轴就当作谢礼吧。");
        } else {
            cm.sendOk("进度：#o57# " + k57 + "/50，#o58# " + k58 + "/50。\r\n请继续讨伐。");
        }
    }
    cm.dispose();
}

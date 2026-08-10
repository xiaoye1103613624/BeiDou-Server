// 9031000 希梅尔 → 收藏成就（图鉴+博学）
var status = -1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1 || mode === 0) { cm.dispose(); return; }
    status++;

    if (status === 0) {
        var t = "#e#b<收藏成就>#k#n\r\n\r\n";
        t += "#L1##b📖 收藏图鉴#k - 怪物卡/装备收集，属性加成+积分兑换#l\r\n";
        t += "#L3##b🏅 成长勋章#k - 地区/野外Boss/远征卡注入 + 勋章池幻化#l\r\n";
        t += "#L2##b🧠 博学大师#k - 知识问答，答题赢奖励#l\r\n";
        t += "\r\n#L0#离开#l";
        cm.sendSimple(t);
    } else if (status === 1) {
        if (selection === 0) { cm.dispose(); return; }
        cm.dispose();
        switch (selection) {
            case 1: cm.openNpc(9031000, "xy/匠人街/收藏图鉴"); break;
            case 2: cm.openNpc(9031000, "xy/匠人街/博学大师"); break;
            case 3: cm.openNpc(9031000, "xy/collect/成长勋章"); break;
        }
    }
}

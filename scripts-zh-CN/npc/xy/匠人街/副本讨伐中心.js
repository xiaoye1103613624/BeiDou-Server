// 9031000 希梅尔 → 副本讨伐中心（统一入口）
// 挑战副本 · 世界Boss · 每日任务 · 混沌扎昆

var status = -1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1 || mode === 0) { cm.dispose(); return; }
    status++;

    if (status === 0) {
        var t = "#e#b<希梅尔 · 副本讨伐中心>#k#n\r\n\r\n";
        t += "挑战强敌，获取稀有材料！\r\n\r\n";
        t += "#L1##b📋 普通挑战#k - 6个Boss，基础材料+宝石#l\r\n";
        t += "#L2##b👥 团队挑战#k - 6个Boss，高级材料+A/S宝石#l\r\n";
        t += "#L3##b💀 进阶挑战#k - 8个Boss，套装散件+SS宝石#l\r\n";
        t += "#L4##b🔮 混沌扎昆#k - 远征版终极挑战#l\r\n";
        t += "#L5##r👑 世界Boss#k - 全服讨伐·排名奖励#l\r\n";
        t += "#L6##b📅 每日任务#k - 讨伐/收集/Boss 3任务#l\r\n";
        t += "#L7##b📖 掉落查询#k#l\r\n";
        t += "\r\n#L0#离开#l";
        cm.sendSimple(t);
    } else if (status === 1) {
        if (selection === 0) { cm.dispose(); return; }
        cm.dispose();
        switch (selection) {
            case 1: cm.openNpc(9031000, "xy/匠人街/普通挑战副本"); break;
            case 2: cm.openNpc(9031000, "xy/匠人街/团队挑战副本"); break;
            case 3: cm.openNpc(9031000, "xy/匠人街/进阶挑战副本"); break;
            case 4: cm.openNpc(9031000, "xy/匠人街/混沌扎昆"); break;
            case 5: cm.openNpc(9031000, "xy/匠人街/世界Boss"); break;
            case 6: cm.openNpc(9031000, "xy/匠人街/每日任务"); break;
            case 7: cm.openNpc(9031000, "xy/匠人街/挑战掉落查询"); break;
        }
    }
}

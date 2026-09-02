// 9031000 希梅尔 → 成长辅助（师徒+修炼）
var status = -1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1 || mode === 0) { cm.dispose(); return; }
    status++;

    if (status === 0) {
        var t = "#e#b<成长辅助>#k#n\r\n\r\n";
        t += "#L1##b🧘 修炼大师#k - 挂机修炼获取经验#l\r\n";
        t += "#L2##b👨‍🏫 师徒系统#k - 拜师收徒，经验加成+师徒币#l\r\n";
        t += "\r\n#L0#离开#l";
        cm.sendSimple(t);
    } else if (status === 1) {
        if (selection === 0) { cm.dispose(); return; }
        cm.dispose();
        switch (selection) {
            case 1: cm.openNpc(9031000, "xy/匠人街/修炼大师"); break;
            case 2: cm.openNpc(9031000, "xy/匠人街/师徒系统"); break;
        }
    }
}

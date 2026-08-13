// 9031003 装备打造师 → 装备铸造中心（统一入口）
// 武器进阶 · 套服进阶 · 戒指进阶 · 装备锻造

var status = -1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1 || mode === 0) { cm.dispose(); return; }
    status++;

    if (status === 0) {
        var t = "#e#b<装备打造师 · 铸造中心>#k#n\r\n\r\n";
        t += "在这里打造和升级你的装备！\r\n\r\n";
        t += "#L1##b🗡 武器进阶#k - 19阶链式升级，从枫叶到创世#l\r\n";
        t += "#L2##b👘 套服进阶#k - 19阶血衣进化，3种方向可选#l\r\n";
        t += "#L3##b💍 戒指进阶#k - 15阶成长戒指，属性不断提升#l\r\n";
        t += "#L4##b🔨 装备锻造#k - 按配方打造装备，随机属性区间#l\r\n";
        t += "#L5##b🔮 装备破界#k - 破界之力，随机激活13种属性#l\r\n";
        t += "\r\n#L0#离开#l";
        cm.sendSimple(t);
    } else if (status === 1) {
        if (selection === 0) { cm.dispose(); return; }
        cm.dispose();
        switch (selection) {
            case 1: cm.openNpc(9031003, "装备打造师"); break;
            case 2: cm.openNpc(9031003, "xy/匠人街/套服进阶"); break;
            case 3: cm.openNpc(9031003, "xy/匠人街/戒指中心"); break;
            case 4: cm.openNpc(9031003, "xy/匠人街/锻造师"); break;
            case 5: cm.openNpc(9031003, "xy/匠人街/装备破界"); break;
        }
    }
}

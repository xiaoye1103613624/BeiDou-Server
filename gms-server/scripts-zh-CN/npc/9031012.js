// 匠人街 · 装备制作铁砧 → 统一入口：装备强化大师
// 灵韵 / 星之力 / 潜能 / 洗炼 / 灵魂 / 星岩
var status = -1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1 || mode === 0) {
        cm.dispose();
        return;
    }
    status++;

    if (status === 0) {
        cm.dispose();
        cm.openNpc(9031012, "xy/匠人街/装备强化大师");
    }
}

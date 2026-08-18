// 匠人街 · 拉菲纳特 · 配方商人
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
        cm.openNpc(9031006, "xy/匠人街/配方商人");
    }
}

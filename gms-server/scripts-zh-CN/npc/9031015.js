// 匠人街 · 大当家 · 修炼大师
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
        cm.openNpc(9031015, "xy/匠人街/修炼大师");
    }
}

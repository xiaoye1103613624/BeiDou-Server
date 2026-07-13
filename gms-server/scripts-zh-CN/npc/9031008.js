// 匠人街 · 希梅尔 · 博学大师
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
        cm.openNpc(9031008, "xy/匠人街/博学大师");
    }
}

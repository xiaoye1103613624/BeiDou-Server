// NPC 704 · 洞穴精灵的仓库
function start() {
    cm.getPlayer().getStorage().sendStorage(cm.getClient(), 704);
    cm.dispose();
}

function action(mode, type, selection) {
    cm.dispose();
}

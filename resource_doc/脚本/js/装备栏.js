var status = 0;
function start() {
    cm.sendYesNo("是否清理掉背包#r装备栏#k24格后的物品？此次清空后无法恢复。");
}

function action(mode, type, selection) {
    if (mode != 1) {
        if (mode == 0)
        cm.dispose();
		cm.打开NPC(9900004,5);
        return;
    }
    status++;
    if (status == 1) {
		for (var i = 25; i <= 96; i++) {
			if (cm.getInventory(1).getItem(i) != null) {
				cm.removeAll(cm.getInventory(1).getItem(i).getItemId());
			}
		}
        cm.dispose();
	}
}

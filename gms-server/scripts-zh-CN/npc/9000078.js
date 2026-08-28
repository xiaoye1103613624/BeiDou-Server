var status = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode != 1) {
		cm.dispose();
	} else {
		status++;
		if(status == 0){
			cm.sendSimple("欢迎来到金庙！我可以发给你一个金票.\r\n\r\n#b#L0#金票为500万观（一次性使用）#l\r\n#L1#50,000,000观溢价金票#l#k");
		} else if (status == 1) {
			if (selection == 0) {
				if (cm.getMeso() < 5000000) {
					cm.sendOk("您没有足够的金币.");
				} else if (!cm.canHold(4001431) || cm.haveItem(4001431)) {
					cm.sendOk("Either you have this already or can't hold it.");
				} else {
					cm.gainMeso(-5000000);
					cm.gainItem(4001431,1);
					cm.sendOk("谢谢.");
				}
			} else {
				if (cm.getMeso() < 50000000) {
					cm.sendOk("您没有足够的金币.");
				} else if (!cm.canHold(4001432) || cm.haveItem(4001432)) {
					cm.sendOk("要么你有这个已经或无法容纳它.");
				} else {
					cm.gainMeso(-50000000);
					cm.gainItem(4001432,1);
					cm.sendOk("谢谢.");
				}
			}
			cm.dispose();
		}
	}
}
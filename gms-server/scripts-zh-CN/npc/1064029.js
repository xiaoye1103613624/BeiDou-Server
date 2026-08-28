/* RED 1st impact
    Sugar (Maple Return skill)
    Made by Daenerys
*/

var status = -1;

function action(mode, type, selection) {
    if (mode == 1)
	status++;
    else
	status--;
        if (status == 0) {
            cm.sendNext("果然有出口。");
        } else if (status == 1) {
            cm.sendYesNo("快去吧这件事告诉少女！");
        } else if (status == 2) {
			cm.forceCompleteQuest(30002);
		cm.dispose();
    }
}


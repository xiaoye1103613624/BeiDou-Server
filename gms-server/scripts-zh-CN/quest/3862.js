/*黄金寺院
Made by Jessefjxm
 */
var status = -1;

function start(mode, type, selection) {
	status++;
	var i = -1;
	if (status <= i++) {
		qm.dispose();
	} else if (status === i++) {
	    qm.forceCompleteQuest();
		qm.dispose();
	}
}

function end(mode, type, selection) {
	status++;
	var i = -1;
	if (status <= i++) {
		qm.dispose();
	} else if (status === i++ && qm.getQuestStatus(3862)==0) {
		//qm.EnableUI(1);
		qm.sendNext("敲打位于#b#m252020700##k的#b试炼之门#k，挑战第二次试炼吧。");
	} else if (status === i++) {
		qm.sendNext("声音再次在脑海中回响.");
	} else if (status === i++) {
        qm.sendNext("第二个考研是待会太阳火焰,为在恶魔之力下的守护灵魂,在拉瓦那祭坛供奉祭物.将寺院中鬼怪的太阳火焰带回来吧.....");
	} else if (status === i++) {	
		qm.sendNext("找到#t4033176#后再返回试炼之门所在地吧。");
	} else if (status === i++) {	
		//qm.EnableUI(0);
		qm.forceCompleteQuest(3862);
		//qm.warp(252030000);
		qm.dispose();
	} else {
	    qm.sendOkS("敲打位于#b#m252020700##k的#b试炼之门#k，找到了#t4033176#再次返回了试炼之门所在地。");
		qm.forceCompleteQuest();
		qm.dispose();
	}
}
	

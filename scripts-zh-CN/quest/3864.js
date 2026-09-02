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
	    qm.forceStartQuest();
		qm.dispose();
	}
}

function end(mode, type, selection) {
	status++;
	var i = -1;
	if (status <= i++) {
		qm.dispose();
	} else if (status === i++ && qm.getQuestStatus(3864)==0) {
		qm.sendNextS("通过试炼之门！");
	} else if (status === i++) {
        qm.forceStartQuest();
		qm.dispose();
	} else {
	    qm.sendOkS("敲打#b#m252020700##k的#b试炼之门#k，找到了#t4033176#再次返回了试炼之门所在地。#e#r提示：该任务无需完成，为永久完成状态。#n#k",2);
		qm.removeAll(4001684);//销毁背包太阳火焰
		qm.warp(252030000,0);//传送到荒废寺院
		qm.forceCompleteQuest();
		qm.forceStartQuest();
		qm.dispose();
	}
}
	

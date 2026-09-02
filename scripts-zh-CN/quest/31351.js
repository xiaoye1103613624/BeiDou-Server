/**
-- 枫叶岛作者：烁电  ---------------------------------------------------------------------------
任务 ID：[31351]
任务名称：[拯救岩壁巨人作战7]
任务开始NPC ID：[] 
任务开始NPC 名称：[] 
-- 更新日志 ---------------------------------------------------------------------------------------
2020-03-16 第一次更新
---------------------------------------------------------------------------------------------------
 **/

// 全局变量
var status = -1; // status: 当前聊天交互轮数
var selectionLog = new Array(); // 记录每一轮的选择

// 开头
function start(mode, type, selection) {
	if (status == 0 && mode == 0) {
		qm.dispose();
		return;
	}
	(mode == 1) ? status++ : status--;
	selectionLog[status] = selection;
	var i = -1;
	if (status <= i++) {
		qm.dispose();
	} else if (status == i++) {
		//qm.EnableUI(1);
		qm.sendNext("奇诺说有事情要告诉我。再去和奇诺对话吧。");
	} else if (status === i++) {
		qm.sendNext("奇诺说在岩壁巨人的头上发现了一些奇怪的人在活动。到岩壁巨人躯干上方3去看看吧。");
	} else if (status === i++) {
		//qm.EnableUI(0);
		qm.forceStartQuest();
		qm.dispose();
	}
}

function end(mode, type, selection) {
	if (status == 0 && mode == 0) {
		qm.dispose();
		return;
	}
	(mode == 1) ? status++ : status--;
	selectionLog[status] = selection;
	var i = -1;
	if (status <= i++) {
		qm.dispose();
	} else if (status == i++ && qm.getQuestStatus(31351)==0) {
		//qm.EnableUI(1);
		qm.sendNext("必须找到根本的原因，从源头开始解决才行。接受岩壁巨人最后的委托吧。");
	} else if (status === i++) {
		qm.sendNext("岩壁巨人让我到他身体的最深处去寻找元凶。到#b岩壁巨人心脏#k去消灭#r蜘蛛女王#k吧。");
	} else if (status === i++) {
		//qm.EnableUI(0);
		qm.forceStartQuest();
		qm.dispose();
	} else {
	    qm.sendOkS("岩壁巨人让我到他身体的最深处去寻找元凶。到岩壁巨人心脏去消灭掉了蜘蛛女王之后，污染停止了。这下岩壁巨人应该恢复正常了。");
		qm.forceCompleteQuest(); 
		qm.dispose();
	}
}

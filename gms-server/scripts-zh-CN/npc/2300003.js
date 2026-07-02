var status = -1;
var keys = Array(8, 9, 10, 11, 12, 13);
var keynames = Array("#fEffect/UIWindow/KeyConfig/key/8#", "#fEffect/UIWindow/KeyConfig/key/9#", "#fEffect/UIWindow/KeyConfig/key/10#", "#fEffect/UIWindow/KeyConfig/key/11#", "#fEffect/UIWindow/KeyConfig/key/12#", "#fEffect/UIWindow/KeyConfig/key/13#"); //just as reference

var skill = [
//技能代码，名字，所需元宝
[1321002,"稳如泰山",388],
[5121009,"急速领域",888],
//[4111006,"二段跳",88],
//[2301001,"快速移动",88],
[9001002,"圣化之力",388],
//[9001006,"超级龙咆",88],
[9001005,"复活",58],
]
function start() {
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode != 1) {
		cm.dispose();
		return;
	}
	status++;
	if (status == 0) {
		sel = selection;
		var selStr = "请选择你所需要的技能：\r\n当前拥有元宝：" + cm.getChar().getmoneyb() + "\r\n";
		for (var i = 0; i < skill.length; i++) {
			selStr += "#L" + i + "##s" + skill[i][0] + "#" + skill[i][1] + " 需要 #e" + skill[i][2] + "#n 元宝#n#l\r\n";
		}
		cm.sendSimple(selStr + "#k");

	} else if (status == 1) {

		itt = selection;
		var selStr = "请选择所放置的技能位置：#b\r\n";
		for (var i = 0; i < keys.length; i++) {
			selStr += "#L" + i + "#" + keynames[i] + "#l\r\n";
		}
		cm.sendSimple(selStr + "#k");

	} else if (status == 2) {
		var hadSkill = true;
		if (cm.getPlayer().getSkillLevel(skill[itt]) <= 0) {
			hadSkill = false;
			if (cm.getChar().getmoneyb()< skill[itt][2]) {
				cm.sendOk("你没有拥有 " + skill[itt][2] + "元宝，无法学习.");
				cm.dispose();
				return;
			} else {
				cm.teachSkill(skill[itt][0], 31, 31);
				cm.setmoneyb(-skill[itt][2]);
				//cm.getPlayer().setAccountLog("累计赞助积分", 1, - skillsp[itt]);
			}
		}
		cm.getPlayer().changeKeybinding(keys[selection], 1, skill[itt][0]);
		cm.刷新状态();
		cm.getPlayer().dropMessage(1,"学习成功,请换线打开键盘查看。");
		cm.dispose();
	}
}
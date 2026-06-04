var status = -1;
var keys = Array(8, 9, 10, 11, 12, 13);
var keynames = Array("#fUI/UIWindow/KeyConfig/key/8#", "#fUI/UIWindow/KeyConfig/key/9#", "#fUI/UIWindow/KeyConfig/key/10#", "#fUI/UIWindow/KeyConfig/key/11#", "#fUI/UIWindow/KeyConfig/key/12#", "#fUI/UIWindow/KeyConfig/key/13#"); //just as reference

var skill = [
//技能代码，名字，所需元宝
//[1321002,"稳如泰山",18],
//[5121009,"急速领域",38],
[4111006,"二段跳",8],
[2301001,"快速移动",8],
[4101004,"轻功",8],
[5121009,"急速领域",38],
//[4111006,"二段跳",8],
//[2301001,"快速移动",8],
[9001005,"复活",58],
[1321002,"稳如泰山",88],
//[9001005,"复活",58],
]
// 定义不允许使用此功能的地图ID数组
var restrictedMapIds = [410000123, 9101000000]; // 示例地图ID

function start() {
    if (isInRestrictedMap()) {
        cm.sendOk("不能再此地图使用此功能。");
        cm.dispose();
        return;
    }
    action(1, 0, 0);
}

function isInRestrictedMap() {
    var currentMapId = cm.getMapId();
    for (var i = 0; i < restrictedMapIds.length; i++) {
        if (currentMapId == restrictedMapIds[i]) {
            return true;
        }
    }
    return false;
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
var status = -1;
var keys = Array(8, 9, 10, 11, 12, 13);
var keynames = Array("#fUI/UIWindow/KeyConfig/key/8#", "#fUI/UIWindow/KeyConfig/key/9#", "#fUI/UIWindow/KeyConfig/key/10#", "#fUI/UIWindow/KeyConfig/key/11#", "#fUI/UIWindow/KeyConfig/key/12#", "#fUI/UIWindow/KeyConfig/key/13#"); //just as reference

var skill = [
//技能代码、技能名字、所学元宝、需要仙级等级
    [14101004, "二段跳", 8, 0], // 凡人期-飞侠的 4111006 - 14101004
    [2301001, "快速移动", 8, 0], // 凡人期
    [4101004, "轻功", 8, 0], // 凡人期
	//[9001005, "复活", 58, 0], // 凡人期
	[9001000,"治愈+魔法无效",88, 0],// 凡人期
	//[9001002,"圣化之力",88, 0], // 凡人期
	[9001006,"超级龙咆",88, 0],// 凡人期
	//[1321002, "稳如泰山", 188, 0], // 凡人期
    //[5121009, "急速领域", 288, 0], // 筑基期
	//[5121003,"超级变身",888, 0],// 筑基期
	
];

// 定义不允许使用此功能的地图ID数组
var restrictedMapIds = [910022000, 105040321, 105040322, 105040323, 105040324, 105040325, 105040326, 105040327, 105040328, 105040329, 9101000000,926010001]; // 示例地图ID

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
        var 当前仙级 = getxmwnjlc("XM飞升系统_仙级");
        var selStr = "根据你的飞升阶段你可选择如下技能进行偷学。\r\n当前拥有元宝：" + cm.getChar().getmoneyb() + "\r\n";
        for (var i = 0; i < skill.length; i++) {
            if (当前仙级 >= skill[i][3]) { // 检查玩家当前仙级是否达到技能要求
                selStr += "#L" + i + "##s" + skill[i][0] + "#" + skill[i][1] + " 需要 #e" + skill[i][2] + "#n 元宝#n#l\r\n";
            }
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
    // 检查玩家是否有足够的元宝
		if (cm.getChar().getmoneyb() < skill[itt][2]) {
			cm.sendOk("你没有拥有 " + skill[itt][2] + "元宝，无法学习.");
			cm.dispose();
        return;
		} else {
        // 玩家有足够的元宝，扣除所需元宝
			cm.setmoneyb(- skill[itt][2]);
        // 显示扣除元宝的提示
			cm.getPlayer().dropMessage(5, "学习成功，扣除 " + skill[itt][2] + " 元宝。");
        // 学习技能
			cm.teachSkill(skill[itt][0], 31, 31);
        // 设置技能快捷键
			cm.getPlayer().changeKeybinding(keys[selection], 1, skill[itt][0]);
        // 刷新状态
			cm.刷新状态();
        // 提示学习成功
			cm.getPlayer().dropMessage(1, "学习成功，请换线打开键盘查看。");
		}
		cm.dispose();
	}
}

function getxmwnjlc(log) {
    return getxmwnjljsc(log);
}

function getxmwnjljsc(jiluid) {
    var xmsjfh = 0;
    zhjsid = cm.getPlayer().getId();
    var conn = getConnection();
    var sql = "SELECT * FROM xmwnjl WHERE characterid = " + zhjsid + " AND bossid = '" + jiluid + "' ;";
    var pstmt = conn.prepareStatement(sql);
    var result = pstmt.executeQuery();
    if (result.next()) {
        xmsjfh = result.getInt("count");
    }
    result.close();
    pstmt.close();
    conn.close();
    return xmsjfh;
}

function getConnection() {
    return cm.getConnection();
}
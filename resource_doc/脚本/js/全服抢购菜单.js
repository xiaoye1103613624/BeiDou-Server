var 感叹号0 = "#fUI/UIWindow/Quest/icon0#";
var 感叹号1 = "#fUI/UIWindow/Quest/icon1#";
var 开 = "#fUI/Basic/CheckBox/0#";   //有框框 无√
var 关 = "#fUI/Basic/CheckBox/1#";   //有框框 有√
var xx = "#fItem/Etc/0427/04270001/Icon9/0#";  //小黄星
var 分割线 = "#fUI/Login.img/WorldSelect/channel/chgauge#";
var 广播 = "#fUI/CN_Chat/ChattingRoom/BtVolUp/0/normal/0#";
var 功能名称 = "全服抢购";

var 列表 = [
	{ 代码: 1404005, 连接: "第一期全服抢购-扫荡月卡",   标题: "第一期全服抢购（扫 荡 卡）" },
//	{ 代码: 1404005, 连接: "第二期全服抢购-幻彩裂空剑", 标题: "第二期全服抢购（裂 空 剑）" },
	
//	{ 代码: 1404005, 连接: "第三期全服抢购-时装戒指",	标题: "第三期全服抢购（时装戒指）" },
//	{ 代码: 1404005, 连接: "第四期全服抢购-暗影币",		标题: "第四期全服抢购（暗 影 币）" },
//	{ 代码: 1404005, 连接: "第五期全服抢购-超级混沌", 	标题: "第五期全服抢购（超级混沌）" },
//	{ 代码: 1404005, 连接: "第六期全服抢购-积分币", 	标题: "第六期全服抢购（积 分 币）" },
//	{ 代码: 1404005, 连接: "第七期全服抢购-宠物项圈", 	标题: "第七期全服抢购（宠物项圈）" },
]

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (status >= 0 && mode == 0) {
			cm.dispose();
			return;
		}
		if (mode == 1) {
			status++;
		} else {
			status--;
		}
		if (status == 0) {
			var text = "#d\r\n";
			text += " #k┏━#r冒险岛提示#k━━━━━━━━━━━━━━━━━┓\r\n";
			text += "\t#d" + 广播 + " 欢迎来到:[#r" + 功能名称 + "#d]\r\n";
			text += "\t#d" + 广播 + " 想要我为您提供什么服务呢？\r\n";
			text += " #k┗━━━━━━━━━━━━━━━━━━━━━━━┛#d\r\n";
			var itemsPerRow = 1; // 每行显示几个项目
			for (var i = 0; i < 列表.length; i++) {
				if (i % itemsPerRow == 0 && i != 0) {
					text += "#d\r\n\r\n"; 
				}
					text += "\t\t #L" + i + "#" + xx + "" + 列表[i].标题 + "" + xx + "#l#d";
			}
			cm.sendYesNo(text);
		} else if (status == 1) {
			if (selection == null || selection < 0 || selection >= 列表.length) {
				cm.sendOk("选择异常，请重新操作。");
				Packages.tools.FileoutputUtil.log("log\\玩家相关\\强开非法记录.log", "[" + cm.getName() + "] 脚本-6大陆功能系统【1404005】强开非法记录 selection=" + selection);
				cm.dispose();
				return;
			}
			
			var isGM = cm.getPlayer().isGM(); 
			if (!isGM) { 
				if (selection == 0) {
					var 当前仙级 = getxmwnjljsc("XM飞升系统_仙级");
					if (当前仙级 < 12) {          // 12 = 仙帝
						cm.sendOk("仙级不足！只有达到#r仙帝#k级别才能参与第一期全服抢购。");
						cm.dispose();
						return;
					}
				}
			}
			
			cm.dispose();
			cm.openNpc(列表[selection].代码, 列表[selection].连接);
			return;
		}
	}
}

function getxmwnjljsc(bossid) {
	var ret = 0;
	try {
		var conn = cm.getConnection();                  // 数据库连接
		var ps   = conn.prepareStatement(
			"SELECT `count` FROM `xmwnjl` WHERE `characterid` = ? AND `bossid` = ?");
		ps.setInt(1, cm.getPlayer().getId());
		ps.setString(2, bossid);
		var rs = ps.executeQuery();
		if (rs.next()) {
			ret = rs.getInt("count");
		}
		rs.close();
		ps.close();
		conn.close();
	} catch (e) {
		cm.getPlayer().dropMessage(5, "读取仙级失败：" + e);
	}
	return ret;
}
/* ============================================== */
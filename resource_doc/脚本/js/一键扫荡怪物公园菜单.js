

/*
	作者：狗哥
	QQ联系：1418181168
	制作时间：2022年/7月/1日
*/

var 感叹号0 = "#fUI/UIWindow/Quest/icon0#";
var 感叹号1 = "#fUI/UIWindow/Quest/icon1#";
var 开 = "#fUI/Basic/CheckBox/0#";   //有框框 无√
var 关 = "#fUI/Basic/CheckBox/1#";   //有框框 有√
var xx = "#fItem/Etc/0427/04270001/Icon9/0#";  //小黄星
var 分割线 = "#fUI/Login.img/WorldSelect/channel/chgauge#";
var 广播 = "#fUI/CN_Chat/ChattingRoom/BtVolUp/0/normal/0#";
var 功能名称 = "一键扫荡怪物公园";

var 列表 = [
	{ 代码: 1404005, 连接: "扫荡怪物公园_简单", 标题: "扫荡怪物公园_简单" },
	{ 代码: 1404005, 连接: "扫荡怪物公园_普通", 标题: "扫荡怪物公园_普通" },
	{ 代码: 1404005, 连接: "扫荡怪物公园_困难", 标题: "扫荡怪物公园_困难" },
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
					text += "#d\r\n\r\n"; // 每3个项目后换行
				}
					text += "\t\t\t #L" + i + "#" + xx + "" + 列表[i].标题 + "" + xx + "#l#d";
			}
			cm.sendYesNo(text);
		} else if (status == 1) {
			if (selection == null || selection < 0 || selection >= 列表.length) {
				cm.sendOk("选择异常，请重新操作。");
				Packages.tools.FileoutputUtil.log("log\\玩家相关\\强开非法记录.log", "[" + cm.getName() + "] 脚本-6大陆功能系统【1404005】强开非法记录 selection=" + selection);
				cm.dispose();
				return;
			}
			cm.dispose();
			cm.openNpc(列表[selection].代码, 列表[selection].连接);
			return;
		}
	}
}


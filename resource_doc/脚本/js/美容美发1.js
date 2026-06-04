

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
var 功能名称 = "美容美发系统";

var 列表 = [
	{ 代码: 1540107, 连接: "美容护肤", 标题: "美容护肤" },
	{ 代码: 1540108, 连接: "明星美发", 标题: "明星美发" }
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
			text += "\t#d" + 广播 + " 想要变得更漂亮吗？\r\n";
			text += " #k┗━━━━━━━━━━━━━━━━━━━━━━━┛#d\r\n";
			for (var i = 0; i < 列表.length; i++) {
				text += "     #L" + i + "#" + xx + "" + 列表[i].标题 + "" + xx + "#l#d  ";
			}
			cm.sendYesNo(text);
		} else if (status == 1) {
			cm.dispose();
			cm.openNpc(列表[selection].代码, 列表[selection].连接);
			return;
		}
	}
}


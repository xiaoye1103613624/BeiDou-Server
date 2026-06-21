

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
var 功能名称 = "怪物公园饰品系统";

var 列表 = [
	{ 代码: 2111000, 连接: "怪物公园-面饰", 标题: "制作面饰" },
	{ 代码: 2111000, 连接: "怪物公园-眼镜", 标题: "制作眼镜" },
	{ 代码: 2111000, 连接: "怪物公园-耳环", 标题: "制作耳环" },
	{ 代码: 2111000, 连接: "怪物公园-项链", 标题: "制作项链" }
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
			//text += "\t#d" + 广播 + " 埃苏武器一件激活:[#r附加额外伤害40%#d]\r\n";
			text += "\t#d" + 广播 + " 想要制作一些什么呢？\r\n";
			text += " #k┗━━━━━━━━━━━━━━━━━━━━━━━┛#d\r\n";
			var itemsPerRow = 2; // 每行显示几个项目
			for (var i = 0; i < 列表.length; i++) {
				if (i % itemsPerRow == 0 && i != 0) {
					text += "#d\r\n\r\n"; // 每3个项目后换行
				}
					text += "  \t  #L" + i + "#" + xx + "" + 列表[i].标题 + "" + xx + "#l#d";
			}
			cm.sendYesNo(text);
		} else if (status == 1) {
			cm.dispose();
			cm.openNpc(列表[selection].代码, 列表[selection].连接);
			return;
		}
	}
}


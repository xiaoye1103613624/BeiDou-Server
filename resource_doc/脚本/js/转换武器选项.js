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
var 功能名称 = "武器互换系统";

// 仙级名称列表
var 仙级名称 = [
    "凡人", "筑基", "金丹", "元婴", "出窍", "分神", "合体", "渡劫", 
    "大乘", "天仙", "仙君", "玄仙", "仙帝", "神人", "神将", "神君", 
    "神帝", "神皇", "神尊", "圣人", "至尊", "主宰", "永恒", "创世", 
	"超脱"
];

// 检测玩家当前仙级
function getxmwnjlc(log) {
    return getxmwnjljsc(log);
}

var 列表 = [
	{ 代码: 1540943, 连接: "T5武器互换", 标题: "T5武器互换" },
	{ 代码: 1540943, 连接: "T4武器互换", 标题: "T4武器互换" },
	{ 代码: 1540943, 连接: "T3武器互换", 标题: "T3武器互换" },
	{ 代码: 1540943, 连接: "T2武器互换", 标题: "T2武器互换" },
	{ 代码: 1540943, 连接: "T1武器互换", 标题: "T1武器互换" },
	{ 代码: 1540943, 连接: "T0武器互换", 标题: "T0武器互换" }
];

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
			var 当前仙级 = getxmwnjlc("XM飞升系统_仙级");
			var 当前仙级名称 = 仙级名称[当前仙级];
			var text = "#d\r\n";
			text += " #k┏━#r冒险岛提示#k━━━━━━━━━━━━━━━━━┓\r\n";
			text += "\t#d" + 广播 + " 欢迎来到:[#r" + 功能名称 + "#d]\r\n";
			text += "\t#d" + 广播 + " 当前仙级：[#r" + 当前仙级名称 + "#d]\r\n";
			text += "\t#d" + 广播 + " 想要互换什么武器呢？\r\n";
			text += " #k┗━━━━━━━━━━━━━━━━━━━━━━━┛#d\r\n";
			var itemsPerRow = 2; // 每行显示几个项目
			for (var i = 0; i < 列表.length; i++) {
				if (i % itemsPerRow == 0 && i != 0) {
					text += "#d\r\n\r\n"; // 每2个项目后换行
				}
				text += "\t #L" + i + "#" + xx + "" + 列表[i].标题 + "" + xx + "#l#d";
			}
			if (当前仙级 >= 12) { // 只有达到仙帝级别的玩家才能看到以下选项
				text += "#d\r\n\r\n";
				text += "\t #L" + 列表.length + "#" + xx + "<神器>互换" + xx + "#l#d";
				text += "\t #L" + (列表.length + 1) + "#" + xx + "<史诗>互换" + xx + "#l#d\r\n\r\n";
				text += "\t #L" + (列表.length + 2) + "#" + xx + "<上古>互换" + xx + "#l#d";
			//	text += "\t #L" + (列表.length + 3) + "#" + xx + "<传承>互换" + xx + "#l#d\r\n\r\n";
			}
			cm.sendYesNo(text);
		} else if (status == 1) {
			if (selection < 列表.length) {
				cm.dispose();
				cm.openNpc(列表[selection].代码, 列表[selection].连接);
			} else {
				var 高级选项 = ["神器武器互换", "史诗武器互换", "上古武器互换"];
				var 当前仙级 = getxmwnjlc("XM飞升系统_仙级");
				if (当前仙级 >= 12) { // 检查玩家是否达到仙帝级别
					cm.dispose();
					cm.openNpc(2111000, 高级选项[selection - 列表.length]);
				} else {
					cm.sendOk("你尚未达到仙帝级别，无法进行此操作！");
					cm.dispose();
				}
			}
		}
	}
}

function getxmwnjljsc(jiluid) {
    var xmsjfh = 0;
    zhjsid = cm.getPlayer().getId();
    var conn = cm.getConnection();
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
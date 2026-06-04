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
var 功能名称 = "一键分解装备系统";

var 列表 = [
	{ 代码: 9031002, 连接: "传说系列分解", 标题: "传说系列( #b1积分#d)" },
	{ 代码: 9031002, 连接: "紫金系列分解", 标题: "紫金系列( #b2积分#d)" },
	{ 代码: 9031002, 连接: "运动系列分解", 标题: "运动系列( #b3积分#d)" },
	{ 代码: 9031002, 连接: "风暴系列分解", 标题: "风暴系列( #b4积分#d)" },
	{ 代码: 9031002, 连接: "终极系列分解", 标题: "终极系列( #b5积分#d)" },
	{ 代码: 9031002, 连接: "外星系列分解", 标题: "外星系列( #b6积分#d)" },
	{ 代码: 9031002, 连接: "C级戒指分解", 标题: "Ｃ级戒指(#b10积分#d)" },
	{ 代码: 9031002, 连接: "B级戒指分解", 标题: "Ｂ级戒指(#b20积分#d)" },
	{ 代码: 9031002, 连接: "A级戒指分解", 标题: "Ａ级戒指(#b40积分#d)" },
	{ 代码: 9031002, 连接: "S级戒指分解", 标题: "Ｓ级戒指(#b80积分#d)" },
	{ 代码: 9031002, 连接: "抽奖系列分解", 标题: "抽奖系列(#b10积分#d)" },
	{ 代码: 9031002, 连接: "其他系列分解", 标题: "其他系列(#bxx积分#d)" },
	{ 代码: 9031002, 连接: "一键分解所有装备", 标题: "#r一键分解所有" }
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
			text += "\t#d" + 广播 + " 部分道具可点击查看回收价格。\r\n";
			text += "\t#d" + 广播 + " 【VIP.1】回收倍率1.05   【VIP.2】回收倍率1.10\r\n";
			text += "\t#d" + 广播 + " 【VIP.3】回收倍率1.15   【VIP.4】回收倍率1.20\r\n";
			text += "\t#d" + 广播 + " 【VIP.5】回收倍率1.30   【VIP.6】回收倍率1.40\r\n";
			text += "\t#d" + 广播 + " 【VIP.7】回收倍率1.50   【VIP.8】回收倍率1.60\r\n";
			text += "\t#d" + 广播 + " 【VIP.9】回收倍率1.70   【VIP10】回收倍率1.80\r\n";
			text += "\t#d" + 广播 + " 【VIP11】回收倍率2.0   \r\n";
			text += "\t#d" + 广播 + " 当前积分：#r" + 查询当前积分() + "#k\r\n"; // 显示当前积分
			text += " #k┗━━━━━━━━━━━━━━━━━━━━━━━┛#d\r\n";
            // 显示普通分解选项
            for (var i = 0; i < 列表.length - 1; i += 2) { // 每次循环处理两个选项，排除最后一个“一键分解所有”
                text += " #L" + i + "#" + xx + "" + 列表[i].标题 + "" + xx + "#l";
                if (i + 1 < 列表.length - 1) { // 如果存在第二个选项，且不是“一键分解所有”
                    text += "  #L" + (i + 1) + "#" + xx + "" + 列表[i + 1].标题 + "" + xx + "#l";
                }
                text += "#d\r\n\r\n"; // 换行
            }

            // 显示“一键分解所有”选项
            text += "               #L" + (列表.length - 1) + "#" + xx + "" + 列表[列表.length - 1].标题 + "" + xx + "#l";
            text += "#d\r\n\r\n"; // 换行
			cm.sendYesNo(text);
		} else if (status == 1) {
			cm.dispose();
			cm.openNpc(列表[selection].代码, 列表[selection].连接);
			return;
		}
	}
}


function 查询当前积分() {
    var chr = cm.getPlayer();
    var con = cm.getConnection();
    var ps = con.prepareStatement("SELECT ps FROM characters WHERE id = ?");
    ps.setInt(1, chr.getId());
    var rs = ps.executeQuery();
    if (rs.next()) {
        var 当前积分 = rs.getInt("ps");
        rs.close();
        ps.close();
        con.close();
        return 当前积分;
    } else {
        rs.close();
        ps.close();
        con.close();
        return 0;
    }
}
/*
function sqlMultiPurpose(sql) {
    var con = cm.getConnection();
    var ps = con.prepareStatement(sql);
    ps.executeUpdate();
    ps.close();
    con.close();
}*/
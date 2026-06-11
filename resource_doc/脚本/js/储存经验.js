
var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";//红桃心
var 感叹号0 = "#fUI/UIWindow/Quest/icon0#";
var 感叹号1 = "#fUI/UIWindow/Quest/icon1#";
var 开 = "#fUI/Basic/CheckBox/0#";   //有框框 无√
var 关 = "#fUI/Basic/CheckBox/1#";   //有框框 有√
var xx = "#fItem/Etc/0427/04270001/Icon9/0#";  //小黄星
var 分割线 = "#fUI/Login.img/WorldSelect/channel/chgauge#";
var 广播 = "#fUI/CN_Chat/ChattingRoom/BtVolUp/0/normal/0#";
var 功能名称 = "经验存储系统";

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
			text += "\t#d" + 粉心 + " 欢迎来到:[#r" + 功能名称 + "#d]\r\n";
			text += "\t#d" + 粉心 + " 该功能主要是为了打造#v1113131:#使用。\r\n\r\n";
			text += "\t#d" + 粉心 + " 已存储经验有：[#r" + getExp() + "#d / #b2147483647#d]\r\n\r\n";
			text += "\t#d" + 粉心 + " 当前还可储存经验值：[#r" + (2147483647 - getExp()) + "#d]\r\n\r\n";
			text += "\t#d" + 粉心 + " 当前您的角色拥有经验：[#r" + cm.getPlayer().getExp()+ "#d]\r\n\r\n";//cm.getPlayer().getExp()
			text += "\t#d" + 粉心 + " 如果要存储经验，请在下方输入要存入多少经验！\r\n";
			text += " #k┗━━━━━━━━━━━━━━━━━━━━━━━┛#d\r\n";
			cm.sendGetNumber(text, 1, 1, cm.getPlayer().getExp());
		} else if (status == 1) {
			if (getExp() >= 2147483647){
				cm.sendOk("当前储存经验值已达最高限制，无法继续储存。");
				cm.dispose();
			} else if (2147483647 - getExp() < selection){
				cm.sendOk("当前储存经验值超过最高限制。\r\n当前可储存经验值为#r"+(2147483647 - getExp())+"");
				cm.dispose();
			} else{
			cm.gainExp(-selection);
			gainExp(+selection);
			cm.getPlayer().dropMessage(5, "角色储存经验 (+" + selection + ")");
			cm.sendOk("#e#d经验存储完毕！");
			cm.dispose();
		}
	}
}
}


function sqlMultiPurpose(sql) {//
	var con = cm.getConnection();
	var ps = con.prepareStatement(sql);
	ret = ps.executeUpdate();
	ps.close();
	con.close();
	return ret;
}

function gainExp(sum) {
	var id = cm.getPlayer().getId();
	return sqlMultiPurpose("UPDATE characters SET 储存经验 = 储存经验 + " + sum + " WHERE id = " + id + "");
}

function getExp() {
	var exp = 0;
	var id = cm.getPlayer().getId();
	var con = cm.getConnection();
	var ps = con.prepareStatement("SELECT * FROM characters WHERE id = " + id + "");
	var rs = ps.executeQuery();
	while (rs.next()) {
		exp = rs.getString("储存经验");
	}
	rs.close();
	ps.close();
	con.close();
	return exp;
}

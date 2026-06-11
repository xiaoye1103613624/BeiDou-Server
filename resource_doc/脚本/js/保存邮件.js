var maxs = new Array();
var itemArr = new Array();
var 存放时间 = 7;//单位：以天计
var 筛选;
function start() {
	扫描超时道具();
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
			邮件包 = 读取邮件();
			var 检测 = true;
			var text = "#d";
			if (邮件包.length != 0) {
				text += "   呐~~~邮件的东西都在这里哦！\r\n ";
			} else {
				检测 = false;
				text += "#d此功能会在背包满时触发\r\n#k当挑战#r远征队#k的#rBOSS#k获得#r物品#k时如果#r背包#k满了,就会以#r邮件#k的方式将道具存入此功能\r\n";
			}
			for (var i = 0; i < 邮件包.length; i++) {
				text += "#L" + i + "##v" + 邮件包[i]["物品代码"] + ":#数量#r" + 邮件包[i]["数量"] + "#d个(距离删除时间：" + 呈现时间(邮件包[i]["倒计时"] - 读取时间()) + ")#l\r\n";
			}
			text += "\r\n";
			if (检测 == true) {
				cm.sendOk(text);
			} else {
				cm.sendOk(text);
				cm.dispose();
				return;
			}
		} else if (status == 1) {
			sele = selection;
			if (!cm.canHold(邮件包[sele]["物品代码"], 邮件包[sele]["数量"])) {
				cm.sendOk("#e#d您背包放不下哦！");
				status = -1;
				return;
			} else {
				cm.gainItem(邮件包[sele]["物品代码"], 邮件包[sele]["数量"]);
				删除邮件(邮件包[sele]["顺序"]);
				start();
				return;
			}
		}
	}
}

function 呈现时间(time) {//呈现制作物品需要花费的时间
	var text = "";
	/*if (time >= 0) {
		text += "" + parseInt(time / (60 * 60 * 24)) + "#r天 #d";
		time = time % (60 * 60 * 24);
	}*/
	if (time >= 0) {
		text += "" + parseInt(time / (60 * 60)) + "#r时 #d";
		time = time % (60 * 60);
	}
	if (time >= 0) {
		text += "" + parseInt(time / 60) + "#r分 #d";
		time = time % 60;
	}
	if (time >= 0) {
		text += "" + time + "#r秒#d";
	}
	return text;
}

function 删除邮件(顺序) {
	sqlMultiPurpose("DELETE FROM 泉哥_邮件 WHERE 顺序 = " + 顺序 + "");
}

function 读取邮件() {
	var 角色id = cm.getPlayer().getId();
	var ret = sqlSelect("SELECT * FROM 泉哥_邮件 WHERE 角色id = " + 角色id + " ORDER BY `倒计时` asc");
	return ret;
}
function 转至邮件(物品代码, 物品名称, 数量) {//
	var 角色id = cm.getPlayer().getId();
	var 玩家名称 = cm.getPlayer().getName();
	var 倒计时 = 读取时间() + 60 * 60 * 24 * 存放时间;
	sqlMultiPurpose("INSERT INTO 泉哥_邮件 (玩家名称, 角色id, 倒计时, 物品代码, 物品名称, 数量) VALUES ('" + 玩家名称 + "'," + 角色id + "," + 倒计时 + "," + 物品代码 + ",'" + 物品名称 + "'," + 数量 + ")");
}

function 扫描超时道具() {
	var ret = sqlSelect("SELECT * FROM 泉哥_邮件 WHERE 倒计时 < " + 读取时间() + "");
	for (var i = 0; i < ret.length; i++) {
		sqlMultiPurpose("DELETE FROM 泉哥_邮件 WHERE 顺序 = " + ret[i]["顺序"] + "");
	}
}

function 读取时间() {//读取秒钟
	var ca = new Date();
	var 秒 = Math.ceil(ca / 1000);//
	return 秒;
}

function 物品名称(代码) {
	var name = Packages.server.MapleItemInformationProvider;
	return name.getInstance().getName(代码);
}
function 字符串转换为组(str) {
	var tempString = str.toString().split("");
	return tempString;
}

function 提取字符串数值(str) {
	var num = str.replace(/[^0-9]/ig, "");//提取字符串中的数值  返回 = 字符串数值(较为精准)
	var num = str.match(/\d+(.\d+)?/g);//提取字符串中的数值  返回 = 字符串数值(不太精准)
	return num;
}

function 判断背包空间_素组(list) {
	var text = "#e#d";
	var 检测背包 = true;
	var k1 = 0; var k2 = 0; var k3 = 0; var k4 = 0; var k5 = 0;
	for (var i = 0; i < list.length; i++) {
		var is = list[i];
		if (is.代码 >= 1000000 && is.代码 <= 1999999) { k1++; };
		if (is.代码 >= 2000000 && is.代码 <= 2999999) { k2++; };
		if (is.代码 >= 3000000 && is.代码 <= 3999999) { k3++; };
		if (is.代码 >= 4000000 && is.代码 <= 4999999) { k4++; };
		if (is.代码 >= 5000000 && is.代码 <= 5999999) { k5++; };
	}
	var 装备栏空位 = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.EQUIP).getNumFreeSlot();
	var 消耗栏空位 = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.USE).getNumFreeSlot();
	var 设置栏空位 = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.SETUP).getNumFreeSlot();
	var 其他栏空位 = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.ETC).getNumFreeSlot();
	var 现金栏空位 = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.CASH).getNumFreeSlot();
	if (装备栏空位 < k1) { 检测背包 = false; text += " 请确保#r装备#d栏有 #r" + k1 + "#d 空间以上！\r\n"; };
	if (消耗栏空位 < k2) { 检测背包 = false; text += " 请确保#r消耗#d栏有 #r" + k2 + "#d 空间以上！\r\n"; };
	if (设置栏空位 < k3) { 检测背包 = false; text += " 请确保#r设置#d栏有 #r" + k3 + "#d 空间以上！\r\n"; };
	if (其他栏空位 < k4) { 检测背包 = false; text += " 请确保#r其他#d栏有 #r" + k4 + "#d 空间以上！\r\n"; };
	if (现金栏空位 < k5) { 检测背包 = false; text += " 请确保#r现金#d栏有 #r" + k5 + "#d 空间以上！\r\n"; };
	return ret = { bool: 检测背包, text: text };
}

function 判断背包空间_单个(itemid) {
	var text = "#e#d";
	var 检测背包 = true;
	var k1 = 0; var k2 = 0; var k3 = 0; var k4 = 0; var k5 = 0;
	if (itemid >= 1000000 && itemid <= 1999999) { k1++; };
	if (itemid >= 2000000 && itemid <= 2999999) { k2++; };
	if (itemid >= 3000000 && itemid <= 3999999) { k3++; };
	if (itemid >= 4000000 && itemid <= 4999999) { k4++; };
	if (itemid >= 5000000 && itemid <= 5999999) { k5++; };
	var 装备栏空位 = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.EQUIP).getNumFreeSlot();
	var 消耗栏空位 = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.USE).getNumFreeSlot();
	var 设置栏空位 = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.SETUP).getNumFreeSlot();
	var 其他栏空位 = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.ETC).getNumFreeSlot();
	var 现金栏空位 = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.CASH).getNumFreeSlot();
	if (装备栏空位 < k1) { 检测背包 = false; text += " 请确保#r装备#d栏有 #r" + k1 + "#d 空间以上！\r\n"; };
	if (消耗栏空位 < k2) { 检测背包 = false; text += " 请确保#r消耗#d栏有 #r" + k2 + "#d 空间以上！\r\n"; };
	if (设置栏空位 < k3) { 检测背包 = false; text += " 请确保#r设置#d栏有 #r" + k3 + "#d 空间以上！\r\n"; };
	if (其他栏空位 < k4) { 检测背包 = false; text += " 请确保#r其他#d栏有 #r" + k4 + "#d 空间以上！\r\n"; };
	if (现金栏空位 < k5) { 检测背包 = false; text += " 请确保#r现金#d栏有 #r" + k5 + "#d 空间以上！\r\n"; };
	return ret = { bool: 检测背包, text: text };
}

function 分割线() {
	var text = " ";
	var list = [
		//{ UI: "#fEffect/CharacterEff.img/1022223/3/0#", length: 21 },
		//{ UI: "#fEffect/CharacterEff.img/1022223/4/0#", length: 21 },
		//{ UI: "#fEffect/CharacterEff.img/1022223/6/0#", length: 31 },
		{ UI: "1", length: 21 },
		{ UI: "1", length: 22 },
		{ UI: "1", length: 24 },
	];
	var random = Math.floor(Math.random() * list.length);
	for (var i = 0; i < list[random].length; i++) {
		//var random = Math.floor(Math.random() * list.length);
		text += list[random].UI;
	}
	text += "\r\n";
	return text;
}

function 更改呈现奖励(类型, 数量) {
	switch (类型) {
		case "点券":
			if (数量 != 0 && 数量 != null) { cm.gainNX(数量) };
			break;
		case "抵用":
			if (数量 != 0 && 数量 != null) { cm.gainDY(数量) };
			break;
		case "金币":
			if (数量 != 0 && 数量 != null) { cm.gainMeso(数量) };
			break;
		case "经验":
			if (数量 != 0 && 数量 != null) { cm.gainExp(数量) };
			break;
		default:
			break;
	}
}

function 呈现奖励货币(类型, 数量) {
	var 章鱼 = "1";
	var 蘑菇 = "1";
	var 绿水 = "1";
	var 猪猪 = "1";
	var text = "";
	switch (类型) {
		case "点券":
			text += (数量 != 0 && 数量 != null ? "" + 章鱼 + "奖励: #r" + 数量 + "#d点券" : "");
			break;
		case "抵用":
			text += (数量 != 0 && 数量 != null ? "" + 蘑菇 + "奖励: #r" + 数量 + "#d抵用" : "");
			break;
		case "金币":
			text += (数量 != 0 && 数量 != null ? "" + 绿水 + "奖励: #r" + 数量 + "#d金币" : "");
			break;
		case "经验":
			text += (数量 != 0 && 数量 != null ? "" + 猪猪 + "奖励: #r" + 数量 + "#d经验" : "");
			break;
		default:
			break;
	}
	return text;
}

function sqlSelect(sql) {
	var con = cm.getConnection();
	var ret = new Array();
	var ps = con.prepareStatement(sql);
	var rs = ps.executeQuery();
	var metaData = ps.getMetaData();
	while (rs.next()) {
		var rsdata = new java.util.HashMap();
		for (var j = 1; j <= metaData.getColumnCount(); j++) {
			columnLabel = metaData.getColumnLabel(j);
			rsdata.put(columnLabel, rs.getObject(columnLabel));
		}
		if (!rsdata.isEmpty()) {
			ret.push(rsdata);
		}
	}
	rs.close();
	ps.close();
	con.close();
	return ret;
}

function sqlMultiPurpose(sql) {//
	var con = cm.getConnection();
	var ps = con.prepareStatement(sql);
	ret = ps.executeUpdate();
	ps.close();
	con.close();
	return ret;
}
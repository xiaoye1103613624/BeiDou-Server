

/*
	作者：狗哥
	QQ联系：1418181168
	制作时间：2022年/7月/9日
*/
var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";//红桃心
var 分割线 =""+粉心+粉心+粉心+粉心+粉心+粉心+"";
//获取 = #fUI/UIWindow.img/QuestIcon/4/0#;
var 开 = "#fUI/Basic/CheckBox/0#";   //有框框 无√
var 关 = "#fUI/Basic/CheckBox/1#";   //有框框 有√
var xx = "#fItem/Etc/0427/04270001/Icon9/0#";  //小黄星
//var 分割线 = "#fUI/Login.img/WorldSelect/channel/chgauge#";
var 广播 = "#fUI/CN_Chat/ChattingRoom/BtVolUp/0/normal/0#";
var 未完成 = "#fUI/Basic/CheckBox/0#";   //有框框 无√
var 已完成 = "#fUI/Basic/CheckBox/1#";   //有框框 有√
var 感叹 = "#fUI/UIWindow/Quest/icon0#";
var 上拉 = "#fUI/UIWindow.img/PvP/Scroll/enabled/prev2#";
var 下拉 = "#fUI/UIWindow.img/PvP/Scroll/enabled/next2#";
//cm.喇叭(4, "[冲级活动]", "恭喜 ");//红色黄底 喇叭
//cm.全服漂浮喇叭("【星星提高】：恭喜玩家 [" + cm.getPlayer().getName() + "] ！", 5121009);
var Sl;
var 检测条件 = true;
var 功能介绍展示 = false;
var 功能名称 = "玩具组队副本";

var 事件配置 = "LudiPQ";
var 人数 = { min: 1, max: 6 };
var 等级 = { min: 35, max: 255 };
var 队长奖励加倍等级 = 65;
var 限制次数 = 8;
var 限制招募 = 5;
var 扫荡物品 = 4321029;
var 地图控制 = { 领奖: 922011100 };

var 介绍编辑 = [
	{ 内容: "[#b提示#k]:玩具倒数第二关优化无需跳箱子直接通关" },
	{ 内容: "#r带队福利#k:" },
	{ 内容: "#k队长等级大于等于#r65#k级即可领取双倍奖励~" },
]
var 奖励列表 = [
	{ 金币: 0, 点卷: 0, 抵用: 0, 经验: 50000 },
	{ 道具: 4170005, 数量: 1 },
	{ 道具: 2711003, 数量: 1 },
	{ 道具: 4321010, 数量: 10 },
	{ 道具: 4011007, 数量: 3 },
	{ 道具: 4021009, 数量: 3 },
	{ 道具: 4001165, 数量: 150 },
]

function start() {
	副本次数 = cm.getPlayer().getBossLog(功能名称);
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
			if (cm.getMapId() == 地图控制.领奖) {
				通关奖励("常规");
				cm.dispose();
				return;
			}
			var text = "#d";
			text += " #k┏━#r冒险岛提示#k━━━━━━━━━━━━━━━━━━┓\r\n";
			if (功能介绍展示 == true) {
				text += " #L666#" + 上拉 + "||------------[#r关闭#d]讯息介绍------------||" + 上拉 + "#l\r\n\r\n";
				for (var i = 0; i < 介绍编辑.length; i++) {
					text += "\t#d" + 广播 + " " + 介绍编辑[i].内容 + "\r\n";
				}
			} else {
				text += " #L666#" + 下拉 + "||------------[#r展开#d]讯息介绍------------||" + 下拉 + "#l\r\n\r\n";
				text += "\t#d" + 广播 + " 欢迎来到 [#r" + 功能名称 + "#d]\r\n";
				text += "\t#d" + 广播 + " 限制人数：#r" + 人数.min + "#d ~ #r" + 人数.max + "#d\r\n";
				text += "\t#d" + 广播 + " 限制等级：#r" + 等级.min + "#d ~ #r" + 等级.max + "#d\r\n";
				text += "\t#d" + 广播 + " 入场限制：#b每天#r" + 限制次数 + "#d次\r\n";
				text += "\t#d" + 广播 + " 当前完成：#b当天#r" + 副本次数 + "#d次\r\n";
				if (cm.getParty() == null) { // 没有组队
					检测条件 = false;
					text += "\t#d" + 广播 + " 您还没有创建队伍！\r\n";
				} else {
					text += "\t#d" + 广播 + " 队员详情情况：\r\n";
					text += "        #k Name    Level   Limit    MapName#d\r\n";
					var leaderMap = cm.getPlayer().getMapId();
					var party = cm.getParty().getMembers();
					if (party.size() < 人数.min || party.size() > 人数.max) {
						检测条件 = false;
					}
					var it = party.iterator();
					while (it.hasNext()) {
						var cPlayer = it.next();
						var 玩家状态 = "";
						var chr = 状态(cPlayer.getId());
						if (chr == null) {
							检测条件 = false;
							var NaNChr = palayerContent(cPlayer.getId());
							玩家状态 += "" + 美化(15, "" + NaNChr["name"], false) + "" + 美化(6, "" + NaNChr["level"], false) + "   " + 感叹 + "#r该玩家不在线！#d\r\n";
						} else {
							var chrLog = chr.getBossLog(功能名称);
							var chrMap = chr.getMapId();
							var chrLv = chr.getLevel();
							if (chrLog >= 限制次数) {
								检测条件 = false;
							}
							if (chrMap != leaderMap) {
								检测条件 = false;
							}
							if (chrLv < 等级.min || chrLv > 等级.max) {
								检测条件 = false;
							}
							var Nm = 美化(15, chr.getName(), false);
							var Lv = 美化(6, "" + chrLv, false);
							var Lg = 美化(18, "[#b" + 美化(2, "" + chrLog, false) + "#d/#r" + 美化(2, "" + 限制次数, false) + "#d]", false);
							var Map = "#m" + chrMap + "##d";
							玩家状态 += "" + Nm + "" + Lv + "" + Lg + "  " + Map + "\r\n";
						}
						text += 玩家状态;
					}
				}
			}
			text += "\r\n #k┗━━━━━━━━━━━━━━━━━━━━━━━━┛#d\r\n";
			if (检测条件 == false) {
				text += "\t\t\t  条件不足，请检测队员状态！\r\n";
			} else {
				text += "\t\t\t  #L1#" + xx + "开始执行任务" + xx + "#l\r\n\r\n";
			}
			var 副本招募 = cm.getPlayer().getBossLog("副本招募");
			text += " #L2#" + xx + "副本招募[#b" + 副本招募 + "#d/#r" + 限制招募 + "#d]" + xx + "#l #L3#" + xx + "副本扫荡(#r需要#d:#v" + 扫荡物品 + ":#)" + xx + "#l\r\n";
			text += "\r\n          " + 分割线 + "  #k奖励.清单#d  " + 分割线 + "\r\n";
			Sl = 奖励列表[0];
			if (Sl.金币 != 0 && Sl.金币 != null) {
				text += "\t\t奖励：+#r" + Sl.金币 + "#d 金币\r\n";
			}
			if (Sl.点卷 != 0 && Sl.点卷 != null) {
				text += "\t\t奖励：+#r" + Sl.点卷 + "#d 点卷\r\n";
			}
			if (Sl.抵用 != 0 && Sl.抵用 != null) {
				text += "\t\t奖励：+#r" + Sl.抵用 + "#d 抵用\r\n";
			}
			text += "┏                                                 ┓\r\n";
			if (奖励列表.length >= 2) {
				for (var i = 1; i < 奖励列表.length; i++) {
					text += "    #k道具：#v" + 奖励列表[i].道具 + ":##z" + 奖励列表[i].道具 + "# * " + 奖励列表[i].数量 + "\r\n";
				}
			}
			if (Sl.经验 != 0 && Sl.经验 != null) {
				text += "    经验：" + Sl.经验 + " * 人物等级\r\n";
			}
			text += "\r\n#k┗                                                 ┛\r\n";
			cm.sendYesNo(text);
		} else if (status == 1) {
			switch (selection) {
				case 666:
					if (功能介绍展示 == false) {
						功能介绍展示 = true;
					} else {
						功能介绍展示 = false;
					}
					start();
					return;
				case 1:
					var em = cm.getEventManager(事件配置);
					if (em == null) {
						cm.sendOk("#e#d副本出错，请联系作者修复！\r\n作者QQ：1418181168");
					} else {
						var prop = em.getProperty("state");
						if (prop.equals("0") || prop == null) {
							var party = cm.getParty().getMembers();
							var it = party.iterator();
							while (it.hasNext()) {
								var cPlayer = it.next();
								var chr = cm.getPlayer().getMap().getCharacterById(cPlayer.getId());
								if (限制次数 != 0) {
									//chr.setBossLog(功能名称);
								}
							}
							em.startInstance(cm.getParty(), cm.getMap());
						} else {
							cm.sendOk("#e#d该频道已有人在挑战...请等待或者换个频道尝试一下。");
						}
					}
					cm.dispose();
					break;
				case 2:
					var 副本招募 = cm.getPlayer().getBossLog("副本招募");
					if (副本招募 >= 限制招募) {
						cm.sendOk("#e#d每天只能使用#r" + 限制招募 + "#d次！");
					} else {
						cm.getPlayer().setBossLog("副本招募");
						cm.喇叭(2,"[副本招募] 玩家:[" + cm.getPlayer().getName() + "] 在" + 功能名称 + " 召唤群英！");//红色黄底 喇叭
					}
					cm.dispose();
					break;
				case 3:
					if (物品数量(扫荡物品) >= 1 && cm.getPlayer().getBossLog(功能名称) < 限制次数) {
						cm.gainItem(扫荡物品, -1);
						通关奖励("扫荡");
						cm.sendOk("#e#d恭喜您，使用了一次扫荡！");
						cm.喇叭(1,"[副本扫荡] 玩家:[" + cm.getPlayer().getName() + "] 使用了扫荡器完成了 【" + 功能名称 + "】 任务！");//红色黄底 喇叭
					} else {
						cm.sendOk("#e#d你没有#v" + 扫荡物品 + ":# 或者 已经达到副本挑战上限！！");
					}
					cm.dispose();
					break;
				default:
					break;
			}
		}
	}
}

function 物品数量(itemid) {
	return cm.getPlayer().getItemQuantity(itemid, false);
}

function 通关奖励(type) {//
	var 倍率 = 1;
	if (type == "常规") {
		if (cm.isLeader() == true && cm.getPlayer().getLevel() >= 队长奖励加倍等级) {
			倍率 = 2;
		}
	}
	cm.getPlayer().setBossLog1("累计通关副本",1);
	cm.getPlayer().setBossLog(功能名称,1);
	Sl = 奖励列表[0];
	if (Sl.金币 != 0 && Sl.金币 != null) {
		cm.gainMeso(+Sl.金币 * 倍率);
	}
	if (Sl.点卷 != 0 && Sl.点卷 != null) {
		cm.gainNX(+Sl.点卷 * 倍率);
	}
	if (Sl.抵用 != 0 && Sl.抵用 != null) {
		cm.gainDY(Sl.抵用 * 倍率);
	}
	if (Sl.经验 != 0 && Sl.经验 != null) {
		cm.gainExp(+(Sl.经验 * cm.getPlayer().getLevel()) * 倍率);
	}
	for (var i = 1; i < 奖励列表.length; i++) {
		cm.gainItem(奖励列表[i].道具, +奖励列表[i].数量 * 倍率);
	}
}

function palayerContent(id) {//
	var ret = sqlSelect("SELECT * FROM characters WHERE id = " + id + "");
	return ret[0];
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

function 状态(id) {//
	var cchr;
	var 循环 = true;
	if (循环 == true) {
		var channels = Packages.handling.channel.ChannelServer.getAllInstances().iterator();
		while (channels.hasNext()) {
			var channel = channels.next();
			var chrs = channel.getPlayerStorage().getAllCharacters().iterator();
			while (chrs.hasNext()) {
				var chr = chrs.next();
				if (chr.getId() == id) {
					cchr = chr;
					循环 = false;
					break;
				}
			}
		}
	}
	return cchr;
}

function 美化(length, content, bool) {
	var bool = false;
	var str = "";
	var cs = "";
	if (content.length > length) {
		str = content;
	} else {
		for (var j = 0; j < length - content.getBytes("GB2312").length; j++) {
			cs += " ";
		}
	}
	if (bool == true) {
		str = content + cs;
	} else {
		str = cs + content;
	}
	return str;
}

var 蓝星 = "#fEffect/CharacterEff.img/1052203/1/0#";
var 红星 = "#fEffect/CharacterEff.img/1052203/2/0#";
var 大蓝星 = "#fEffect/CharacterEff.img/1022223/2/0#";
var 大红星 = "#fEffect/CharacterEff.img/1022223/1/0#";
var 蓝点 = "#fEffect/CharacterEff.img/1022223/6/0#";
var 红点 = "#fEffect/CharacterEff.img/1022223/7/0#";

var 拉瓦那 = "#fUI/UIWindow.img/MobGage/Mob/8800200#";
var 艾里葛斯 = "#fUI/UIWindow.img/MobGage/Mob/9300028#";
var 蜘蛛女王 = "#fUI/UIWindow.img/MobGage/Mob/8800400#";
var 狂暴威尔 = "#fUI/UIWindow.img/MobGage/Mob/8880302#";
var 三头犬 = "#fUI/UIWindow.img/MobGage/Mob/9400897#";
var 蟾蜍怪 = "#fUI/UIWindow.img/MobGage/Mob/6500012#";
var 火狐 = "#fUI/UIWindow.img/MobGage/Mob/9700043#";
var 黑暗恶狼 = "#fUI/UIWindow.img/MobGage/Mob/8220109#";
var 皇帝 = "#fUI/UIWindow.img/MobGage/Mob/9410224#";
var 敦凯尔 = "#fUI/UIWindow.img/MobGage/Mob/8645009#";
var 戴米安 = "#fUI/UIWindow.img/MobGage/Mob/8880100#";
var 调和精灵 = "#fUI/UIWindow.img/MobGage/Mob/8644011#";
var 穷奇 = "#fUI/UIWindow.img/MobGage/Mob/8880830#";
var 铸杌 = "#fUI/UIWindow.img/MobGage/Mob/8880831#";
var 混沌 = "#fUI/UIWindow.img/MobGage/Mob/8880832#";
var 卡琳 = "#fUI/UIWindow.img/MobGage/Mob/8880837#";
var 黑魔法师 = "#fUI/UIWindow.img/MobGage/Mob/8880503#";
var 路西德 = "#fUI/UIWindow.img/MobGage/Mob/8880141#";
var 黑水灵王 = "#fUI/UIWindow.img/MobGage/Mob/8220104#";
var 阿勒玛 = "#fUI/UIWindow.img/MobGage/Mob/8641011#";

var 感叹 = "#fUI/UIWindow/Quest/icon0#";
var 开 = "#fUI/Basic/CheckBox/0#";
var 关 = "#fUI/Basic/CheckBox/1#";
var xx = "#fItem/Etc/0427/04270001/Icon9/0#";
var 广播 = "#fUI/CN_Chat/ChattingRoom/BtVolUp/0/normal/0#";

var 功能名称 = "大型远征队入口";

var 列表 = [
	{ 地图: 252030000, 战力要求: 0,  标题: ""+拉瓦那+"#k拉瓦那      进入需要:#r10万#k战力以上可挑战" },
	{ 地图: 910540100, 战力要求: 30,  标题: ""+艾里葛斯+"#k艾里葛斯    进入需要:#r30万#k战力以上可挑战" },
	{ 地图: 240093300, 战力要求: 50,  标题: ""+蜘蛛女王+"#k蜘蛛女王    进入需要:#r50万#k战力以上可挑战" },
	{ 地图: 555000200, 战力要求: 70,  标题: ""+狂暴威尔+"#k狂暴威尔    进入需要:#r70万#k战力以上可挑战" },
	{ 地图: 510101300, 战力要求: 100, 标题: ""+三头犬+"#k三头犬      进入需要:#r100万#k战力以上可挑战" },
	{ 地图: 910025200, 战力要求: 130, 标题: ""+蟾蜍怪+"#k蟾蜍怪      进入需要:#r130万#k战力以上可挑战" },
	{ 地图: 910141000, 战力要求: 150, 标题: ""+火狐+"#k烈焰火狐    进入需要:#r150万#k战力以上可挑战" },
	{ 地图: 910142080, 战力要求: 170, 标题: ""+黑暗恶狼+"#k黑暗恶狼    进入需要:#r170万#k战力以上可挑战" },
	{ 地图: 745010500, 战力要求: 200, 标题: ""+皇帝+"#k皇帝        进入需要:#r200万#k战力以上可挑战" },
	{ 地图: 803100000, 战力要求: 300, 标题: ""+敦凯尔+"#k敦凯尔      进入需要:#r300万#k战力以上可挑战" },
	{ 地图: 209000001, 战力要求: 400, 标题: ""+戴米安+"#k戴米安      进入需要:#r400万#k战力以上可挑战" },
	{ 地图: 910142100, 战力要求: 500, 标题: ""+调和精灵+"#k调和精灵    进入需要:#r500万#k战力以上可挑战" },
	{ 地图: 511000100, 战力要求: 700, 标题: ""+穷奇+"#k穷奇        进入需要:#r700万#k战力以上可挑战" },
	// 511000120/140/160 官方空壳(~387B)：改走可用庭院；进场后由本脚本补刷（非 Event 实例）
	{ 地图: 105200800, 战力要求: 800, 标题: ""+铸杌+"#k铸杌        进入需要:#r800万#k战力以上可挑战" },
	{ 地图: 105200900, 战力要求: 1000,标题: ""+混沌+"#k混沌        进入需要:#r1000万#k战力以上可挑战" },
	// 不用 105200110（会触发半半 banban_Summon）；105200100 无 onFirstUserEnter
	{ 地图: 105200100, 战力要求: 1200,标题: ""+卡琳+"#k卡琳        进入需要:#r1200万#k战力以上可挑战" },
	{ 地图: 450013850, 战力要求: 1600,标题: ""+黑魔法师+"#k黑魔法师    进入需要:#r1600万#k战力以上可挑战" },
	{ 地图: 450003740, 战力要求: 1800,标题: ""+路西德+"#k路西德      进入需要:#r1800万#k战力以上可挑战" },
	{ 地图: 450001219, 战力要求: 2300,标题: ""+黑水灵王+"#k黑水灵王    进入需要:#r2300万#k战力以上可挑战" },
];

var BossKey = {
	252030000 : "拉瓦那远征队",
	910540100 : "艾里葛斯远征队",
	240093300 : "蜘蛛女王远征队",
	555000200 : "狂暴威尔远征队",
	510101300 : "三头犬远征队",
	910025200 : "蟾蜍怪远征队",
	910141000 : "火狐远征队",
	910142080 : "黑暗恶狼远征队",
	745010500 : "始皇帝远征队",
	803100000 : "敦凯尔远征队",
	209000001 : "戴米安远征队",
	910142100 : "调和精灵远征队",
	511000100 : "穷奇远征队",
	105200800 : "铸杌远征队",
	105200900 : "混沌远征队",
	105200100 : "卡琳远征队",
	450013850 : "黑魔法师远征队",
	450003740 : "路西德远征队",
	450001219 : "黑水灵王征队",
};

// 空壳改图后需脚本补刷（穷奇仍走 cowUserEnter）
var SpawnOnEnter = {
	105200800: { mob: 8880831, x: 1105, y: 180 },
	105200900: { mob: 8880832, x: 1105, y: 180 },
	105200100: { mob: 8880837, x: 2450, y: 221 }
};

var status;
var sele1;

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
		if (mode == 1) status++;
		else status--;

		if (status == 0) {
			/* ========== 1. 检查是否全部打完 ========== */
			var allDone = true;
			for (var i = 0; i < 列表.length; i++) {
				var mapId  = 列表[i].地图;
				var fubenm = BossKey[mapId];
				var used   = cm.getPlayer().getBossLog(fubenm);
				var ext    = cm.getPlayer().getBossLog("远征挑战扩充");
				var max    = 2 + ext;
				if (used < max) { allDone = false; break; }
			}
			if (allDone) {
				cm.sendOk("#d#k┏━#r冒险岛提示#k━━━━━━━━━━━━━━━━━━━┓\r\n\r\n"
						+ "\t#d" + 广播 + " 今日所有远征队已挑战完成！\r\n"
						+ "\t#d" + 广播 + " 请明日再来，或购买额外次数。#k\r\n\r\n"
						+ "#k┗━━━━━━━━━━━━━━━━━━━━━━━━━┛");
				cm.dispose();
				return;
			}

			/* ========== 2. 列出还能打的 ========== */
			var text = "#d\r\n";
			text += "#k┏━#r冒险岛提示#k━━━━━━━━━━━━━━━━━━━┓\r\n";
			text += "\t#d" + 广播 + " 欢迎来到:[#r" + 功能名称 + "#d]\r\n";
			text += "\t#d" + 广播 + " 选择您今日还可参与的远征队副本：\r\n";
			for (var i = 0; i < 列表.length; i++) {
				var mapId  = 列表[i].地图;
				var fubenm = BossKey[mapId];
				var used   = cm.getPlayer().getBossLog(fubenm);
				var ext    = cm.getPlayer().getBossLog("远征挑战扩充");
				var max    = 2 + ext;
				if (used >= max) continue;
				text += "#L" + i + "#" + 列表[i].标题 + "#l\r\n";
			}
			text += "\r\n\r\n#k┗━━━━━━━━━━━━━━━━━━━━━━━━━┛#d\r\n";
			text += "\t\t\t  #L999##b"+ 红星 + ""+ 大红星 + ""+ 红点 + "#b购买门票"+ 红点 + ""+ 大红星 + ""+ 红星 + "#l\r\n\r\n";
			text += "\t\t\t\t #d当前战力:#r " + (cm.getPlayer().getCombatPower()/10000).toFixed(0) + " #k万\r\n";
			cm.sendSimple(text);
		} else if (status == 1) {
			if (selection == 999) {
				cm.dispose();
				cm.openNpc(9900004, "远征副本门票商店");
				return;
			}
			sele1 = selection;
			var text = "#d\r\n";
			text += "\t#d" + 广播 + " 您确定要前往该副本吗？\r\n";
			cm.sendYesNo(text);
		} else if (status == 2) {
			var mapId = 列表[sele1].地图;
			var player = cm.getChar();
			var party = player.getParty();
			var map = cm.getMap(mapId);
			var playersInMap = map.getAllPlayers();

			if (party == null) {
				cm.sendOk("请组队再来找我....");
				cm.dispose();
				return;
			}
			if (party.getMembers().size() < 1) {
				cm.sendOk("需要 1 人及以上的组队才能进入！");
				cm.dispose();
				return;
			}

			/* ---- 战力统一判断 ---- */
			var need = 列表[sele1].战力要求 * 10000;
			if (cm.getPlayer().getCombatPower() < need) {
				cm.sendOk("战力需要达到" + 列表[sele1].战力要求 + "万，才可以进入此地图！");
				cm.dispose();
				return;
			}

			/* ---- 地图占用检查 ---- */
			var hasTeamMember = false;
			var hasAnyPlayer = false;
			for (var i = 0; i < playersInMap.size(); i++) {
				var chr = playersInMap.get(i);
				hasAnyPlayer = true;
				if (chr != player && chr.getParty() == party) {
					hasTeamMember = true;
					break;
				}
			}
			if (hasAnyPlayer && !hasTeamMember) {
				cm.sendOk("该地图已经有其他队伍的玩家存在，无法进入。");
				cm.dispose();
				return;
			}

			/* ---- 必要时先在目标图补刷，再传送 ---- */
			var fubenm = BossKey[mapId];
		//	cm.getPlayer().setBossLog(fubenm, 1);  //扣次数
			var spawn = SpawnOnEnter[mapId];
			if (spawn != null) {
				var targetMap = cm.getMap(mapId);
				if (targetMap != null && targetMap.getMonsterById(spawn.mob) == null) {
					var LifeFactory = Java.type("org.gms.server.life.LifeFactory");
					var Point = Java.type("java.awt.Point");
					targetMap.spawnMonsterOnGroundBelow(LifeFactory.getMonster(spawn.mob), new Point(spawn.x, spawn.y));
				}
			}
			cm.warp(mapId, 0);
			cm.dispose();
		}
	}
}
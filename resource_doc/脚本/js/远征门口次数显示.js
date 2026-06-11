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

var 感叹 = "#fUI/UIWindow/Quest/icon0#";
var 开 = "#fUI/Basic/CheckBox/0#";   //有框框 无√
var 关 = "#fUI/Basic/CheckBox/1#";   //有框框 有√
var xx = "#fItem/Etc/0427/04270001/Icon9/0#";  //小黄星
var 广播 = "#fUI/CN_Chat/ChattingRoom/BtVolUp/0/normal/0#";


var 功能名称 = "大型远征队入口";

var 列表 = [
	{ 地图: 252030000, 标题: ""+拉瓦那+"#k拉瓦那      进入需要:#r10万#k战力以上可挑战" },
	{ 地图: 910540100, 标题: ""+艾里葛斯+"#k艾里葛斯    进入需要:#r30万#k战力以上可挑战" },
	{ 地图: 240093300, 标题: ""+蜘蛛女王+"#k蜘蛛女王    进入需要:#r50万#k战力以上可挑战" },
	{ 地图: 555000200, 标题: ""+狂暴威尔+"#k狂暴威尔    进入需要:#r70万#k战力以上可挑战" },
	{ 地图: 510101300, 标题: ""+三头犬+"#k三头犬      进入需要:#r100万#k战力以上可挑战" },
	{ 地图: 910025200, 标题: ""+蟾蜍怪+"#k蟾蜍怪      进入需要:#r130万#k战力以上可挑战" },
	{ 地图: 910141000, 标题: ""+火狐+"#k烈焰火狐    进入需要:#r150万#k战力以上可挑战" },
	{ 地图: 910142080, 标题: ""+黑暗恶狼+"#k黑暗恶狼    进入需要:#r170万#k战力以上可挑战" },
	{ 地图: 745010500, 标题: ""+皇帝+"#k皇帝        进入需要:#r200万#k战力以上可挑战" },
	{ 地图: 803100000, 标题: ""+敦凯尔+"#k敦凯尔      进入需要:#r300万#k战力以上可挑战" },
	{ 地图: 209000001, 标题: ""+戴米安+"#k戴米安      进入需要:#r400万#k战力以上可挑战" },
	{ 地图: 910142100, 标题: ""+调和精灵+"#k调和精灵    进入需要:#r500万#k战力以上可挑战" },
	
	{ 地图: 511000100, 标题: ""+穷奇+"#k穷奇#k        进入需要:#r700万#k战力以上可挑战" },
	{ 地图: 511000120, 标题: ""+铸杌+"#k铸杌#k        进入需要:#r800万#k战力以上可挑战" },
	{ 地图: 511000140, 标题: ""+混沌+"#k混沌#k        进入需要:#r1000万#k战力以上可挑战" },
	{ 地图: 511000160, 标题: ""+卡琳+"#k卡琳#k        进入需要:#r1200万#k战力以上可挑战" },
	
	{ 地图: 450013850, 标题: ""+黑魔法师+"#k黑魔法师#k#b    #k进入需要:#r1600万#k战力以上可挑战" }
	
]

// 地图 ID → 远征队名字
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
    511000120 : "铸杌远征队",
    511000140 : "混沌远征队",
    511000160 : "卡琳远征队",
    450013850 : "黑魔法师远征队"
};

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
			//cm.getMap(910025200).resetFully();
			var text = "#d\r\n";
			text += "#k┏━#r冒险岛提示#k━━━━━━━━━━━━━━━━━━━┓\r\n";
			text += "\t#d" + 广播 + " 欢迎来到:[#r" + 功能名称 + "#d]\r\n";
			text += "\t#d" + 广播 + " 请选择您要参与那个远征队副本：\r\n";
for (var i = 0; i < 列表.length; i++) {
    var mapId  = 列表[i].地图;
    var fubenm = BossKey[mapId];
    var used   = cm.getPlayer().getBossLog(fubenm);
    var ext    = cm.getPlayer().getBossLog("远征挑战扩充");
    var max    = 2 + ext;

    if (used == max) continue;   // ← 已刷满，跳过这一行

    text += "#L" + i + "#" +
            "#g" + used + "#k/#b" + max + "#k次 " +
            列表[i].标题 + "#l\r\n";
}
			text += "\r\n\r\n#k┗━━━━━━━━━━━━━━━━━━━━━━━━━┛#d\r\n";
			text += "\t\t\t  #L999##b"+ 红星 + ""+ 大红星 + ""+ 红点 + "#b购买门票"+ 红点 + ""+ 大红星 + ""+ 红星 + "#l\r\n\r\n"; // 添加购买门票选项
			text += "\t\t\t\t #d当前战力:#r " + cm.getPlayer().GetCombat() / 10000 + " #k万\r\n";
			cm.sendYesNo(text);
		} else if (status == 1) {
            if (selection == 999) {
				cm.dispose();
				cm.openNpc(9900004,"远征副本门票商店");
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
            var map = cm.getMapFactory().getMap(mapId);
            var playersInMap = map.getCharactersThreadsafe();
            var canEnter = true;
			
			if (party == null) {
				cm.sendOk("请组队再来找我....");
				cm.dispose();
				return;
			} else if (party.getMembers().size() < 1) { // 需要至少1人组队
				cm.sendOk("需要 1 人及以上的组队才能进入！");
				cm.dispose();
				return;
			}
			
			if (列表[sele1].地图 == 252030000 ) {
				if (cm.getPlayer().GetCombat() < 100000 ){cm.sendOk("战力需要达到10万,才可以进入此地图");cm.dispose();return;}
			} else if (列表[sele1].地图 == 910540100 ) {if (cm.getPlayer().GetCombat() < 300000 ){cm.sendOk("战力需要达到30万,才可以进入此地图");cm.dispose();return;}
			} else if (列表[sele1].地图 == 240093300 ) {if (cm.getPlayer().GetCombat() < 500000 ){cm.sendOk("战力需要达到50万,才可以进入此地图");cm.dispose();return;}
			} else if (列表[sele1].地图 == 555000200 ) {if (cm.getPlayer().GetCombat() < 700000 ){cm.sendOk("战力需要达到70万,才可以进入此地图");cm.dispose();return;}
			} else if (列表[sele1].地图 == 510101300 ) {if (cm.getPlayer().GetCombat() < 1000000 ){cm.sendOk("战力需要达到100万,才可以进入此地图");cm.dispose();return;}
			} else if (列表[sele1].地图 == 910025200 ) {if (cm.getPlayer().GetCombat() < 1300000 ){cm.sendOk("战力需要达到130万,才可以进入此地图");cm.dispose();return;}
			} else if (列表[sele1].地图 == 910141000 ) {if (cm.getPlayer().GetCombat() < 1500000 ){cm.sendOk("战力需要达到150万,才可以进入此地图");cm.dispose();return;}
			} else if (列表[sele1].地图 == 910142080 ) {if (cm.getPlayer().GetCombat() < 1700000 ){cm.sendOk("战力需要达到170万,才可以进入此地图");cm.dispose();return;}
			} else if (列表[sele1].地图 == 745010500 ) {if (cm.getPlayer().GetCombat() < 2000000 ){cm.sendOk("战力需要达到200万,才可以进入此地图");cm.dispose();return;}
			} else if (列表[sele1].地图 == 803100000 ) {if (cm.getPlayer().GetCombat() < 3000000 ){cm.sendOk("战力需要达到300万,才可以进入此地图");cm.dispose();return;}
			} else if (列表[sele1].地图 == 209000001 ) {if (cm.getPlayer().GetCombat() < 4000000 ){cm.sendOk("战力需要达到400万,才可以进入此地图");cm.dispose();return;}
			} else if (列表[sele1].地图 == 910142100 ) {if (cm.getPlayer().GetCombat() < 5000000 ){cm.sendOk("战力需要达到500万,才可以进入此地图");cm.dispose();return;}
			} else if (列表[sele1].地图 == 511000100 ) {if (cm.getPlayer().GetCombat() < 7000000 ){cm.sendOk("战力需要达到700万,才可以进入此地图");cm.dispose();return;}
			} else if (列表[sele1].地图 == 511000120 ) {if (cm.getPlayer().GetCombat() < 8000000 ){cm.sendOk("战力需要达到800万,才可以进入此地图");cm.dispose();return;}
			} else if (列表[sele1].地图 == 511000140 ) {if (cm.getPlayer().GetCombat() < 10000000 ){cm.sendOk("战力需要达到1000万,才可以进入此地图");cm.dispose();return;}
			} else if (列表[sele1].地图 == 511000160 ) {if (cm.getPlayer().GetCombat() < 12000000 ){cm.sendOk("战力需要达到1200万,才可以进入此地图");cm.dispose();return;}
			} else if (列表[sele1].地图 == 450013850 ) {if (cm.getPlayer().GetCombat() < 16000000 ){cm.sendOk("战力需要达到1600万,才可以进入此地图");cm.dispose();return;}
			
			}
			//cm.warp(列表[sele1].地图, 0);
// 检查地图中的玩家情况
var hasTeamMember = false; // 用于标记是否有当前队伍的玩家
var hasAnyPlayer = false; // 用于标记地图中是否有任何玩家

for (var i = 0; i < playersInMap.size(); i++) {
    var chr = playersInMap.get(i);
    hasAnyPlayer = true; // 标记地图中至少有一个玩家
    if (chr != player && chr.getParty() == party) { // 检查该玩家是否属于当前队伍
        hasTeamMember = true; // 找到属于当前队伍的玩家
        break; // 找到一个即可，退出循环
    }
}

if (!hasAnyPlayer || hasTeamMember) {
    // 如果地图中没有人，或者有属于当前队伍的玩家，允许进入
    var requiredCombat = 列表[sele1].战力要求;
    if (player.getLevel() < requiredCombat) {
        cm.sendOk("战力需要达到" + requiredCombat + ", 才可以进入此地图");
    } else {
        cm.warp(mapId, 0); // 传送玩家到目标地图
    }
} else {
    // 如果地图中有玩家，但没有属于当前队伍的玩家，不允许进入
    cm.sendOk("该地图已经有其他队伍的玩家存在，无法进入。");
}
cm.dispose();
		}
	}
}

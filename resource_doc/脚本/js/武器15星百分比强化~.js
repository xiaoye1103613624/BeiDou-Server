var x = "#fUI/UIWindow.img/PartySearch/check0#";
var w = "#fUI/UIWindow.img/PartySearch/check1#";
var 花草 ="#fEffect/SetEff/208/effect/walk2/4#";
var 草丛2 = "#fEffect/SetEff/208/effect/walk2/3#";
var 蓝爱心 = "#fEffect/CharacterEff/1112905/0/1#";
var 红爱心 = "#fEffect/CharacterEff/1022223/3/0#";
var 星星 = "#fMap/MapHelper/weather/witch/3#";
var 强化中心 = "#fEffect/CharacterEff1.img/QQ1408745/0/0#";
var dd = " ";
var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";
var 群粉心 = ""+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+"\r\n";
var cc = null;
var cc1 = null;
var 上限 = 15;
var 概率 = 0;
var 当前强化次数 = 0;
var 当前强化 = 0;

var qhst = 4251202;


var 强化表 = Array(
	Array(0),//不要删掉他
//Array(星级次数, 概率，4维百分比，攻魔百分比,失败掉几级，失败掉级概率),

	Array(1, 100, 5, 5, 0, 0),
	Array(2, 90, 5, 5, 0, 0),
	Array(3, 80, 5, 5, 0, 0),
	Array(4, 70, 10, 10, 0, 0),
	Array(5, 60, 10, 10, 1, 50/*概率掉级*/),
	Array(6, 55, 15, 15, 1, 55),
	Array(7, 50, 20, 20, 1, 60),
	Array(8, 45, 30, 30, 2, 65),
	Array(9, 40, 40, 40, 2, 70),
	Array(10,35, 50, 50, 2, 75),
	Array(11,30, 60, 60, 3, 80),
	Array(12,25, 70, 70, 4, 85),
	Array(13,20, 80, 80, 5, 90),
	Array(14,15, 90, 90, 6, 95),
	Array(15,10, 100, 100, 7, 100)
	/*Array(16,30, 100, 100, 7, 100)
	Array(17,30, 100, 100, 7, 100)
	Array(18,30, 100, 100, 7, 100)
	Array(19,30, 100, 100, 7, 100)
	Array(20,30, 100, 100, 7, 100)*/
);
//------------------------------------------------------
var 材料表1 = Array(

	//Array(隶属强化次数, 物品代码，物品数量),

	//物品代码 1 代表 金币
	Array(1,3700288,1),

	Array(2,3700288,2),

	Array(3,3700288,3),

	Array(4,3700288,4),

	Array(5,3700288,5),

	Array(6,3700288,6),

	Array(7,3700288,7),

	Array(8,3700288,8),

	Array(9,3700288,9),

   Array(10,3700288,10),

	Array(11,3700288,11),

	Array(12,3700288,12),

	Array(13,3700288,13),

	Array(14,3700288,14),

	Array(15,3700288,15)
)


var 直通材料表 = Array(

	//Array(隶属强化次数, 物品代码，物品数量),

	//物品代码 1 代表 金币
	Array(1,3991000,1),

	Array(2,3991001,1),

	Array(3,3991002,1),

	Array(4,3991003,1),

	Array(5,3991004,1),

	Array(6,3991005,1),

	Array(7,3991006,1),

	Array(8,3991007,1),

	Array(9,3991008,1),

   Array(10,3991009,1),

	Array(11,3991010,1),

	Array(12,3991011,1),

	Array(13,3991012,1),


	Array(14,3991013,1),

	Array(15,4000414,1)
)
var 材料表 ;
var cz = "#fItem/Cash/0557.img/05570000/info/icon#"
function start() {
	status = -1;
	action(1, 0, 0);
}
function 强化成功(a) {
	//Array(强化次数, 概率，属性最小值，属性最大值),
	概率 = 强化表[a][1];

}
function 判断材料(a) { //失败-3
	var 材料是否足够 = true

	var txt2 = "----------------------------------------------\r\n"

	for (var j = 0; j < 材料表.length; j++) {
		if (a == 材料表[j][0]) {

			if (材料表[j][1] == 1) {
				txt2 += "金币 * " + 材料表[j][2] + "  [#r" + cm.getPlayer().getMeso() + "#k/" + 材料表[j][2] + "]\r\n\r\n";
				if (cm.getPlayer().getMeso() < 材料表[j][2]) {
					材料是否足够 = false;
				}

			} else if (材料表[j][1] == 2) {
				txt2 += "抵用券 * " + 材料表[j][2] + "  [#r" + cm.getPlayer().getMaplePoints() + "#k/" + 材料表[j][2] + "]\r\n\r\n";
				if (cm.getPlayer().getMaplePoints() < 材料表[j][2]) {
					材料是否足够 = false;
				}
			} else {
				txt2 += "#v" + 材料表[j][1] + "# * " + 材料表[j][2] + "  [#r" + cm.getPlayer().getItemQuantity(材料表[j][1], false) + "#k/" + 材料表[j][2] + "]\r\n\r\n"
				if (cm.getPlayer().getItemQuantity(材料表[j][1], false) < 材料表[j][2]) {
					材料是否足够 = false;
				}

			}

		}

	}
	txt2 += "----------------------------------------------\r\n"
	if (材料是否足够 == false) {
		cm.sendOk("以下材料不足\r\n" + txt2);
		return 材料是否足够;
	} else {

		for (var j = 0; j < 材料表.length; j++) {
			if (a == 材料表[j][0]) {

				if (材料表[j][1] == 1) {
					cm.gainMeso(-材料表[j][2]);
				} else if (材料表[j][1] == 2) {
					cm.getPlayer().gainMaplePoints(-材料表[j][2]);
				} else {
					cm.gainItem(材料表[j][1], -材料表[j][2]); //扣除物品


				}

			}

		}

	}
	return 材料是否足够;
}

function patch(str) {
	if(str ==""){
		return 0;
	}
	return parseInt(str);


}
var next = false;
var sel 
function patch2(cs) {

	return cs + "星";
}
function action(mode, type, selection) {
	cc = cm.getInventory(1).getItem(1);

	if (mode <= 0) {
		cm.dispose();
	} else {
		if (mode == 1)
			status++;
		else
			status--;
		if (selection == 99999) {
			next = !next;
			status = 1
		}
		if(status ==3){
			status =1;
		}
		if (status == 0) {
			var txt = ""+dd+"\r\n\t\t\t"+强化中心+"\r\n"+群粉心+""
			txt +="\t\t\t\t#r#e#L888#我要强化武器#l\r\n\r\n"
			//txt += "#r装备强化系统!#k\r\n"
			//txt += "#k只能强化没有升级次数的装备，";
			txt += "#k强化说明： #n#b只能强化#r[武器]#b，不能强化#r[点装]#b和#r[装备]#b\r\n\t\t\t可以通过#r[暗影之力]#b将武器强化到最高15星\r\n\r\n"
			/*cm.addbyItem(cm.getItem(1,1).copy())
			cm.addbyItem(cm.getItem(1,1).copy())
			cm.addbyItem(cm.getItem(1,1).copy())
			cm.addbyItem(cm.getItem(1,1).copy())
			cm.addbyItem(cm.getItem(1,1).copy())
			cm.addbyItem(cm.getItem(1,1).copy())
			cm.addbyItem(cm.getItem(1,1).copy())
			cm.addbyItem(cm.getItem(1,1).copy())
			cm.addbyItem(cm.getItem(1,1).copy())
			cm.addbyItem(cm.getItem(1,1).copy())*/


				//		txt +="#L888#正常强化#l\t\t#L999#直通卷轴强化#l\r\n\r\n"

			for (var i = 1; i < 强化表.length; i++) {
				var n = i %2 ==0 ? 粉心:粉心
				var xx ="";
				if(i<10){
					xx = " ";
				}
				txt += "#r"+n+xx+强化表[i][0]  +"星#b成功率#r" + getString1(强化表[i][1]) + "% #b四维#r+" + 强化表[i][2] + "%" + " #b攻魔+#r" + 强化表[i][3]  + "%#k"+(强化表[i][5] >0 ? " #b失败#r"+getString1(强化表[i][5])+"%#b掉星" : "")+"\r\n";
			}
			txt += "#k";
			cm.sendNext(txt);
		} else if (status == 1) {
			if (cc == null) {
				cm.sendOk("你的装备栏第一格没有装备!");
				cm.dispose();
				return;
			}
			if (cm.isCash(cc.getItemId())) {
				cm.sendOk("现金装备不能强化")
				cm.dispose()
				return
			}
            if (cm.getPlayer().getCSPoints(1)<30000) {
                cm.sendOk("你的点券不足30000");
                cm.dispose();
				return
			}
			sel =selection
			if(sel == 888){
				材料表 = 材料表1;
			}else if(sel == 999){
				材料表 = 直通材料表;
			}
         //    if (cm.haveItem(2460005, 1)==false) {
          //      cm.sendOk("强化道具不足 #i2460005# *1");
          //      cm.dispose();
		//		return
		//	}

			var cs = 0//cc.getUpgradeSlots()
			if (cs != 0) {
				cm.sendOk("你的第一格子的装备还有升级次数，请升级完再来强化");
				cm.dispose();
				return;
			} else if (!(isWeapon(cc.getItemId()))) {
				cm.sendOk("#e你的装备不符合要求,只能是武器");
				cm.dispose();
				return
			} else if (cc.getHands() >= 上限) {
				cm.sendOk("#e你的已经突破次数上限,无法在继续突破!");
				cm.dispose();
			} else if (cc.getExpiration() != -1) {
				cm.sendOk("限时装备不能进行强化.");
				cm.dispose();
			} else if (cm.isCash(cc.getItemId())) {
				cm.sendOk("现金装备无法强化。");
				cm.dispose();

			} else {
				当前强化 = cc.getHands()
				
				当前强化次数 = 当前强化 + 1;
				概率 = 强化表[当前强化次数][1];
				四维 = 强化表[当前强化次数][2];
				攻魔 = 强化表[当前强化次数][3];
				var txt2 = "你要强化的装备:#v" + cc.getItemId() + "#\t"
				txt2 += "强化星级：#r" + patch2(当前强化) + " → " + patch2(当前强化次数) + "#k\r\n"
				

				txt2 += "----------------------------------------------------\r\n"
				for (var j = 0; j < 材料表.length; j++) {
					if (当前强化次数 == 材料表[j][0]) {

						if (材料表[j][1] == 1) {
							txt2 += "#v2140002#金币 * " + 材料表[j][2] + "  [#r" + 材料表[j][2] + "#k/" + cm.getPlayer().getMeso() + "]\r\n"
						} else if(材料表[j][1] == 2){
							txt2 += "抵用券 * " + 材料表[j][2] + "  [#r" + cm.getPlayer().getMaplePoints() + "#k/" + 材料表[j][2] + "]\r\n"
						}else{	txt2 += "#v" + 材料表[j][1] + "##t" + 材料表[j][1] + "#    [#k消耗:#r" +  材料表[j][2] + "#k个 / 拥有 #r" + cm.getPlayer().getItemQuantity(材料表[j][1], false)+ "#k个]\r\n"

						}

					}

				}
				txt2 += "----------------------------------------------------\r\n"
				txt2 += "#b强化成功概率：#r" + 概率 + "%#k\t"
				txt2 += "\r\n#b属性突破：#r四维 + " + 四维 + "% 攻魔 + " + 攻魔 + "%\r\n"
				if (强化表[当前强化次数][5] > 0 && !next) {
					txt2 += "#b温馨提示：#r当前强化失败有几率掉["+强化表[当前强化次数][4]+"]级#k\r\n"
				}
				
				
				txt2 += "\r\n\t\t\t#e#L88888#开始强化武器#l#n \t"
				//txt2 +="#L666666#使用直升圈#l"
     if (!isWeapon(cc.getItemId()) && sel == 888) {
			 
				txt2 += "#L99999#" + (next ? w : x) + "#v2531000#保护装备#k#l\r\n" 
}
			//	txt2 += "#L99999#" + (next ? w : x) + "#v2531000#保护装备#k#l\r\n" 
				//txt2 += "#L999999#" + (next ? w : x) + "#v"+aweir1+"#保护装备#k#l\r\n" 

				//txt2 += "#r*随便输入即可下一步强化 ，或者点 【确认】 \r\n"
				cm.sendNext(txt2);
			}
		} else if (status == 2) {
			当前强化次数 = parseInt(当前强化次数)
			if (!cm.haveItem(2531000, 1) && next) {
				cm.sendOk("你的#v" + 2531000 + "##t" + 2531000 + "#不足，不能对装备进行保护");
				cm.dispose();
				return;
			}
			
				if (判断材料(当前强化 + 1) == false) {
					cm.dispose();
					return;
				}
			
			if (next) {
				cm.gainItem(2531000, -1)
			}

			var roll点 = selection == 999 ? 0 : Math.round(Math.random() * 100);
			if (roll点 <= 概率) { //成功
				var citem = cc.copy();
				var siwei =  四维/100 +1;
				var gongm = 攻魔/100 +1;
				
				citem.setStr((citem.getStr()*siwei)); //给力量
				citem.setDex((citem.getDex()*siwei)); //给敏捷
				citem.setInt((citem.getInt()*siwei)); //给智力
				citem.setLuk((citem.getLuk()*siwei)); //给运气
				citem.setMatk((citem.getMatk()*gongm)); //攻击
				citem.setWatk((citem.getWatk()*gongm)); //魔法力
				citem.setHands(当前强化+1)
				citem.setOwner((当前强化+1)+"☆")
				cm.刷新装备(citem);
				cm.playerMessage(5, "发出一道闪光，在装备上添加了某种神秘的力量!");

				//cm.gainItem(2460005, -1)//扣去点卷不需要点卷或者物品的时候注释即可
                //cm.gainNX(-30000);//扣去点卷不需要点卷或者物品的时候注释即可
				cm.setmoneyb(-当前强化次数)

				//cm.itemlaba("装备强化", "恭喜【"+cm.getName()+"】强化到了"+(当前强化+1)+"星", citem, 15)
				cm.ShowWZEffect("Effect/BasicEff.img/SkillBook/Success/0"); //成功效果
				cm.sendNext("强化成功，如果需要，请点击确认继续强化，不是请点击结束对话")
				if(selection == 999){
					cm.dispose()
					return
				}
			} else { //失败
				
				var channce = Math.round(Math.random() * 0);
				var txt ="";
				if (强化表[当前强化次数][5] > 0) {
					//cm.playerMessage(channce +"  "+ 强化表[当前强化次数][5])
					if (channce < 强化表[当前强化次数][5]) {
						
						var dj = 强化表[当前强化次数][4];
						var qhcs = parseInt(当前强化);
						if (next) {
							txt ="强化失败,因为使用了装备保护卷，装备没有任何变化!"
							cm.playerMessage(5, "发出一道闪光，因为使用了装备保护卷，装备没有任何变化!");

						} else {
							var item = cc.copy();
							for(var i =0;i<dj;i++){
							
							
							//当前强化 = parseInt(当前强化) - i
							四维 =强化表[qhcs][2];
							攻魔 =强化表[qhcs][3];
							item.setStr(Math.ceil(item.getStr()/ ((四维/100)+1))); //给力量
							item.setDex(Math.ceil(item.getDex()/((四维/100)+1))); //给敏捷
							item.setInt(Math.ceil(item.getInt()/((四维/100)+1))); //给智力
							item.setLuk(Math.ceil(item.getLuk()/((四维/100)+1))); //给运气
							item.setMatk(Math.ceil(item.getMatk()/((攻魔/100)+1))); //攻击
							item.setWatk(Math.ceil(item.getWatk()/((攻魔/100)+1))); //魔法力
							qhcs--;
							item.setHands(qhcs)
							item.setOwner((qhcs)+"☆")
							
							}
							txt = "强化失败，装备掉了" + dj + "个等级!";
							cm.playerMessage(5, "发出一道闪光，装备掉了" + dj + "个等级!");
							//cm.removeSlot(1, 1, 1);
							cm.刷新装备(item);
				//cm.gainItem(2460005, -1)//扣去道具不需要道具或者物品的时候注释即可
                //cm.gainNX(-30000);//扣去点卷不需要点卷或者物品的时候注释即可
				cm.setmoneyb(-当前强化次数)
						}
						cm.ShowWZEffect("Effect/BasicEff.img/SkillBook/Failure/0"); //卷轴失败效果
					}else{
						txt ="强化失败，装备没有任何变化"
					}
				} else {
					txt ="强化失败，装备没有任何变化"
					cm.playerMessage(5, "发出一道闪光，装备没有任务变化!");
					cm.ShowWZEffect("Effect/BasicEff.img/SkillBook/Failure/0"); //卷轴失败效果
				}
				cm.sendNext(txt+"如果需要继续强化，请点击下项,不是请点击结束对话")
			}
			
			//cm.dispose();

		}
	}
}
function getString1(num) {
	var sss = num + "";
	var cs = sss
	//cm.playerMessage(sss.length)
	for (var i = 0; i < (2 - sss.length); i++) {
		cs = " "+cs;
	}
	return cs;
}
function isWeapon(itemid){
	return itemid >=1300000 && itemid <1500000 && itemid ;
}

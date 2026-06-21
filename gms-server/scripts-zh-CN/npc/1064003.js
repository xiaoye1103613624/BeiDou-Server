 /* ==================
 脚本类型: NPC	    
 脚本作者：TTL-唯一   
 联系方式qq：504603558
 =====================
 */

var status = 0;
var 开 = "#fUI/Basic/CheckBox/0#";   //有框框 无√
var 关 = "#fUI/Basic/CheckBox/1#";   //有框框 有√
var 正方箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";
var 感叹号 = "#fUI/UIWindow/Quest/icon0#";
var 元宝图标 = "#fUI/UIWindow/QuestIcon/7/0#";
var 章鱼 = "#fUI/UIWindow/Minigame/Omok/stone/3/white/0#";
var 蘑菇 = "#fUI/UIWindow/Minigame/Omok/stone/0/black/0#";
var 绿水 = "#fUI/UIWindow/Minigame/Omok/stone/1/white/0#";
var 猪猪 = "#fUI/UIWindow/Minigame/Omok/stone/2/black/0#";
var 可以 = 1000;
var 装备 = "#fUI/UIWindow/MaplepointShop/Tab/enabled/0#";

var 配套物品 = [
//强化物品，材料物品1，数量，材料物品2，数量，
[1004492,2049302,1,4310088,5,2048708,5], //首饰
[1052929,2049302,1,4310088,5,2048708,5],
[1082647,2049302,1,4310088,5,2048708,5],
[1073057,2049302,1,4310088,5,2048708,5],
[1102828,2049302,1,4310088,5,2048708,5],
[1132287,2049302,1,4310088,5,2048708,5],

[1302276,2049302,1,4310088,10,2048708,10], //武器
[1402197,2049302,1,4310088,10,2048708,10],
[1432168,2049302,1,4310088,10,2048708,10],
[1442224,2049302,1,4310088,10,2048708,10],
[1382209,2049302,1,4310088,10,2048708,10],
[1452206,2049302,1,4310088,10,2048708,10],
[1462194,2049302,1,4310088,10,2048708,10],
[1472215,2049302,1,4310088,20,2048708,30],
[1332226,2049302,1,4310088,10,2048708,10],
[1482169,2049302,1,4310088,10,2048708,10],
[1492180,2049302,1,4310088,10,2048708,10],

[1099003,2049302,1,4310088,15,2048708,15], //盾牌

]

var 部位 = 0;
var 系数 = 1;
var 强化增加属性 =["四维","攻魔"];
var 强化上线 = 10;
var 成功率=[90,85,80,75,70,65,60,55,50,45];

var 属性 = [10, 10, 10, 10, 10, 10, 10, 10, 10, 10];

var 攻魔 = [10, 10, 10, 10, 10, 10, 10, 10, 10, 10];

var 元宝 = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];

var 点券 = [500, 1000, 1500, 2000, 2500, 3000, 3500, 4000, 4500, 5000];

var 抵用 = [1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000];


var 皇冠白 ="#fUI/GuildMark/Mark/Etc/00009004/16#";
var sels;
var status = -1;

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {

	if(cm.getInventory(1).getItem(1)== null ){
		cm.sendOk("请把 #r#e需要强化的#d#n装备放在第#r#e 1 #d#n格才能进行。");
		cm.dispose();
		return;
	}
	var ii = Packages.server.MapleItemInformationProvider.getInstance();
	
	var statup = new java.util.ArrayList();
	var item = cm.getInventory(1).getItem(1).copy();
	var 属性17 = item.getHands();//手技
	var dmID = cm.getInventory(1).getItem(1).getItemId();
	var 需求数量 = (属性17+1);
	
	for(var i =0;i<配套物品.length;i++){
		if(dmID==配套物品[i][0]){
			可以=i;
			break;
		}
	}
	if(可以==1000){
		var text1="";
		text1 += "对不起装备栏第一格,#v"+dmID+"#，不是可强化物品。\r\n";
		text1 += "只有指定道具可以使用该功能：\r\n";
		text1 += "#r#e指定道具：\r\n";
		for(var i =0;i<配套物品.length;i++){
			text1 += "#n#v"+配套物品[i][0]+"##z"+配套物品[i][0]+"#";
		}
		cm.sendOk(text1);
		cm.dispose();
		return;
	}

	if(属性17>=强化上线){
		cm.sendOk("该装备已经满级"+强化上线+"");
		cm.dispose();
		return;
		
	}
    if (mode == 1) {
        status++;
    } else if (mode == 0) {
        status--;
    } else {
        cm.dispose();
        return;
    }
    if (status == 0) {
			
			
	
			var 是否0_1 = "#r未满足"+开+"#d"; 
			var 是否0_2 = "#r未满足"+开+"#d"; 
			var 是否0_3 = "#r未满足"+开+"#d"; // 第三种材料物品的检查
			var 是否2_元宝 = "#r未满足"+开+"#d"; 
			var 是否2_点券 = "#r未满足"+开+"#d"; 
			var 是否2_抵用 = "#r未满足"+开+"#d"; 

			if (cm.getPlayer().getItemQuantity(配套物品[可以][1], false) >= 配套物品[可以][2]){
				是否0_1 = "#g已满足"+关+"#d";
			}
			if (cm.getPlayer().getItemQuantity(配套物品[可以][3], false) >= 配套物品[可以][4]){
				是否0_2 = "#g已满足"+关+"#d";
			}
			if (cm.getPlayer().getItemQuantity(配套物品[可以][5], false) >= 配套物品[可以][6]){
				是否0_3 = "#g已满足"+关+"#d";
			}
			if ((cm.getmoneyb()) >= 元宝[需求数量-1]){
				是否2_元宝 = "#g已满足"+关+"#d";
			}
			if ((cm.getPlayer().getCSPoints(1)) >= 点券[需求数量-1]){
				是否2_点券 = "#g已满足"+关+"#d";
			}
			if ((cm.getPlayer().getCSPoints(2)) >= 抵用[需求数量-1]){
				是否2_抵用 = "#g已满足"+关+"#d";
			}
			var text = "";
			text += "" + 感叹号 + "[#v"+dmID+"#][#e#b#t"+dmID+"#]#n#r[可强化的装备]\r\n"
			//text += "" + 感叹号 + "主装备[#v"+dmID+"#][#e#b#t"+dmID+"#]#n#d一定要放在第一格。\r\n"
			text += "" + 感叹号 + "#d本次强化增加 [四维 #r#e+"+属性[需求数量-1]+"#d#n]、[攻魔 #r#e+"+(攻魔[需求数量-1])+"#d#n]\r\n"
			text += "" + 感叹号 + "#d总共可以强化[ #r"+强化上线+"#d ]次\r\n"
			text += "" + 感叹号 + "#d当前进行第[ #r"+需求数量+"#d ]次强化：强化需要以下材料\r\n"
			text += "----------------------------------------------------\r\n"
			text += "" + 感叹号 + "[  #v" + 配套物品[可以][1] + "# #e#r#z" + 配套物品[可以][1] + "##n  #r" + cm.getPlayer().getItemQuantity(配套物品[可以][1], false) + "#d/"+ 配套物品[可以][2]+" ]["+是否0_1+"] #l \r\n\r\n"
			text += "" + 感叹号 + "[  #v" + 配套物品[可以][3] + "# #e#r#z" + 配套物品[可以][3] + "##n  #r" + cm.getPlayer().getItemQuantity(配套物品[可以][3], false) + "#d/"+ 配套物品[可以][4]+" ]["+是否0_2+"] #l \r\n\r\n"
			text += "" + 感叹号 + "[  #v" + 配套物品[可以][5] + "# #e#r#z" + 配套物品[可以][5] + "##n  #r" + cm.getPlayer().getItemQuantity(配套物品[可以][5], false) + "#d/"+ 配套物品[可以][6]+" ]["+是否0_3+"] #l \r\n\r\n"; // 第三种材料物品的UI显示
			text += "" + 感叹号 + "[  "+章鱼+" #r 元宝 #r" + (cm.getmoneyb()) + "#d/"+(元宝[需求数量-1])+" ]["+是否2_元宝+"] #l \r\n\r\n"
			text += "" + 感叹号 + "[  "+蘑菇+" #r 点券 #r" + (cm.getPlayer().getCSPoints(1)) + "#d/"+(点券[需求数量-1])+" ]["+是否2_点券+"] #l \r\n\r\n"
			text += "" + 感叹号 + "[  "+绿水+" #r 抵用 #r" + (cm.getPlayer().getCSPoints(2)) + "#d/"+(抵用[需求数量-1])+" ]["+是否2_抵用+"] #l \r\n\r\n"
			text += "" + 感叹号 + "#d成功率：[ #r"+成功率[需求数量-1]+"%#d ]\r\n"
			text += "----------------------------------------------------\r\n"
			text += "" + 感叹号 + "#d请收集指定物品进行强化\r\n"
			text += "" + 感叹号 + "#d请无论成功与否[#r材料道具都会消失#d]\r\n"
		//	text += "" + 感叹号 + "#d强化成功后：成功率[#r递减5%#d]，不会减到50%以下\r\n"
			text += "" + 感叹号 + "#d每次强化成功后#r四维+10#d、#r双攻+10#d\r\n"
				
        cm.sendSimple(text);
    } else if (status == 1) {
		var statup = new java.util.ArrayList();
		var item = cm.getInventory(1).getItem(1).copy();
		var 属性17 = item.getHands();//手技
		var dmID = cm.getInventory(1).getItem(1).getItemId();
		var 需求数量 = (属性17+1);		
        sels = selection;
		if(cm.getInventory(1).getItem(1)== null ){
		    cm.sendOk("请把 #r#e需要强化的#d#n #v"+dmID+"#放在第#r#e 1 #d#n格才能进行。");
			cm.dispose();
			return;
		}
		if(!cm.haveItem(配套物品[可以][1],配套物品[可以][2])){
				cm.sendOk("#v"+配套物品[可以][1]+"# #d材料不足,#r当前拥有:#c"+配套物品[可以][1]+"# 个");
				cm.dispose();
				return;
		}
		if(!cm.haveItem(配套物品[可以][3],配套物品[可以][4])){
				cm.sendOk("#v"+配套物品[可以][3]+"# #d材料不足,#r当前拥有:#c"+配套物品[可以][3]+"# 个");
				cm.dispose();
				return;
		} 
		if(!cm.haveItem(配套物品[可以][5],配套物品[可以][6])){
				cm.sendOk("#v"+配套物品[可以][5]+"# #d材料不足,#r当前拥有:#c"+配套物品[可以][5]+"# 个");
				cm.dispose();
				return;
		} 
		if(cm.getInventory(1).getItem(1).getItemId()!= dmID ){
		    cm.sendOk("请把 #r#e需要强化的#d#n #v"+dmID+"#放在第#r#e 1 #d#n格才能进行f。");
			cm.dispose();
			return;
		}
	///////////////////////////////////////////		
		/*if(cm.getmoneyb() < dang[n][1]){
			cm.sendOk("元宝不足" + dang[n][1] + "，无法融合！");
			cm.dispose();
			return;
		}*/
		if(cm.getmoneyb()<((元宝[需求数量-1]))){
			cm.sendOk("元宝不足 "+((元宝[需求数量-1]))+"");
			cm.dispose();
			return;
		}
		if(cm.getPlayer().getCSPoints(1)<((点券[需求数量-1]))){
			cm.sendOk("点券不足 "+((点券[需求数量-1]))+"");
			cm.dispose();
			return;
		}
		if(cm.getPlayer().getCSPoints(2)<((抵用[需求数量-1]))){
			cm.sendOk("抵用不足 "+((抵用[需求数量-1]))+"");
			cm.dispose();
			return;
		}
			//////////////////////////////////////////
		
		
		
		
		
        var text1 ="";
			text1 +="当前装备栏第#r#e 1 #d#n格#i" + dmID + "#强化等级为：#r#e"+属性17+"\r\n";
			text1 +="#d#n是否要进行#r#r #i" + dmID + "##d#n的第 #r#e"+(属性17+1)+"#d#n 次强化? \r\n";
			text1 +="成功率：[ #r"+成功率[需求数量-1]+"%#d ]\r\n";
			//text1 +="将会按顺序从装备栏第#r#e 2 #d#n格扣除#v"+配套物品[可以][1]+"#\r\n";
			cm.sendYesNo(text1);
    } else if (status == 2) {
		随机数 = Math.floor((Math.random()*100));
		var statup = new java.util.ArrayList();
		var item = cm.getInventory(1).getItem(1).copy();
		var 属性17 = item.getHands();//手技
		var dmID = cm.getInventory(1).getItem(1).getItemId();
		var 需求数量 = (属性17+1);	
		function getItemName(itemId) {
        var ii = Packages.server.MapleItemInformationProvider.getInstance();
        return ii.getName(itemId);
    }
			if(随机数<=成功率[需求数量-1]){
				cm.gainNX(-(点券[需求数量-1])*系数);
				cm.gainDY(-(抵用[需求数量-1])*系数);
				cm.setmoneyb(-(元宝[需求数量-1])*系数);
				var statup = new java.util.ArrayList();
				var item = cm.getInventory(1).getItem(1).copy();
				var itemName = getItemName(dmID); // 获取装备名称
				var 属性1 = item.getStr();//给力量
				var 属性2 = item.getDex();//给【 #r敏捷#
				var 属性3 = item.getInt();//给智力
				var 属性4 = item.getLuk();//给运气
				var 属性5 = item.getWatk();//攻击
				var 属性6 = item.getMatk();//魔法力
				var 属性7 = item.getWdef();//物理防御
				var 属性8 = item.getMdef();//魔法防御
				var 属性9 = item.getHp();//给HP
				var 属性10 = item.getMp();//给MP
				var 属性11 = item.getAcc();//命中
				var 属性12 = item.getAvoid();//回避
				var 属性13 = item.getJump();//跳跃
				var 属性14 = item.getSpeed();//移动
				var 属性15 = item.getLevel();//已升级次数 （装备+几）
				var 属性16 = item.getUpgradeSlots();//剩余升级次数
				var 属性17 = item.getHands();//手技
				item.setStr(  item.getStr() + (属性[需求数量-1]));
				item.setInt(  item.getInt() + (属性[需求数量-1]));
				item.setLuk(  item.getLuk() + (属性[需求数量-1]));
				item.setDex(  item.getDex() + (属性[需求数量-1]));
				item.setWatk( item.getWatk()+攻魔[需求数量-1]);//攻击
				item.setMatk( item.getMatk()+攻魔[需求数量-1]);//魔法力
				item.setHands(item.getHands()+1);
				Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
				cm.gainItem(配套物品[可以][1], -配套物品[可以][2]);	
				cm.gainItem(配套物品[可以][3], -配套物品[可以][4]);	
				cm.gainItem(配套物品[可以][5], -配套物品[可以][6]); // 扣除第三种材料物品
				Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);
				cm.sendNext("#b已经强化好了，请前往背包查看");
			   // cm.喇叭(1,"恭喜玩家:[" + cm.getPlayer().getName() + "]  强化 [" + itemName + "] 第["+需求数量+"]次 成功！");//白色 喇叭
				cm.喇叭(1, "恭喜玩家:[" + cm.getPlayer().getName() + "] 强化 " + itemName + " 第[" + 需求数量 + "]次 成功！");
				cm.喇叭(1, "恭喜玩家:[" + cm.getPlayer().getName() + "] 强化 " + itemName + " 第[" + 需求数量 + "]次 成功！");
				cm.喇叭(1, "恭喜玩家:[" + cm.getPlayer().getName() + "] 强化 " + itemName + " 第[" + 需求数量 + "]次 成功！");
				//cm.ShowWZEffect("Effect/BasicEff.img/SkillBook/Success/0"); //卷轴成功效果
				cm.dispose();
				return;
			}else{
				cm.gainNX(-(点券[需求数量-1])*系数);
				cm.gainDY(-(抵用[需求数量-1])*系数);
				cm.setmoneyb(-(元宝[需求数量-1])*系数);
				cm.gainItem(配套物品[可以][1], -配套物品[可以][2]);	
				cm.gainItem(配套物品[可以][3], -配套物品[可以][4]);	
				cm.gainItem(配套物品[可以][5], -配套物品[可以][6]); // 扣除第三种材料物品
				cm.sendNext("#b强化失败,再接再厉。");
				cm.dispose();
				return;
				
			}
    } else {
		cm.sendOk("未知错误，请联系管理员。");
		cm.dispose();
		return;
    }
}
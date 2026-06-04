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
var 金币图标 = "#fUI/UIWindow/QuestIcon/7/0#";
var 可以 = 1000;
var 装备 = "#fUI/UIWindow/MaplepointShop/Tab/enabled/0#";

var 配套物品 = [
//强化物品，材料物品，数量
[1302197,1302023,1],
[1312103,1312015,1],
[1322143,1322029,1],
[1402135,1402016,1],
[1412091,1412010,1],
[1422094,1422013,1],
[1432122,1432010,1],
[1442126,1442020,1],
[1372124,1372010,1],
[1382149,1382035,1],
[1452153,1452017,1],
[1462143,1462018,1],
[1472165,1472033,1],
[1332173,1332027,1],
]

var 部位 = 0;
var 系数 = 1;
var 强化增加属性 =["四维","攻魔"];
var 强化上线 = 10;
var 成功率=[100,90,80,70,60,50,40,30,20,10];
var 属性 = [1,1,1,2,2,2,3,3,3,4];
var 攻魔 = [1,1,1,2,2,2,3,3,3,4];
var 金币 = [100000,200000,300000,400000,500000,600000,700000,800000,900000,700000];


var 皇冠白 ="#fUI/GuildMark/Mark/Etc/00009004/16#";
var sels;
var status = -1;

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {

	if(cm.getInventory(1).getItem(1)== null ){
		cm.sendOk("请把 #r#e需要强化的#k#n装备放在第#r#e 1 #k#n格才能进行。");
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
		text1 += "对不起,#v"+dmID+"#，不是可强化物品。\r\n";
		text1 += "只有指定道具可以使用该功能：\r\n";
		text1 += "#r#e指定道具：\r\n";
		for(var i =0;i<配套物品.length;i++){
			text1 += "#n#v"+配套物品[i][0]+"##z"+配套物品[i][0]+"#  ";
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
			
			
		
			
			var 是否0 = "#r未满足"+开+"#k"; 
			var 是否1 = "#r未满足"+开+"#k"; 
			var 是否2 = "#r未满足"+开+"#k"; 
			var text = "";
			if (cm.getPlayer().getItemQuantity(配套物品[可以][1], false)>=配套物品[可以][2]){var 是否0 = "#g已满足"+关+"#k";}
			if ((cm.getMeso())>=金币[需求数量-1]){var 是否2 = "#g已满足"+关+"#k";}
			var text = "";
			text += "" + 感叹号 + "[#v"+dmID+"#][#e#b#t"+dmID+"#][#n#r可强化的装备#k]\r\n"
			text += "" + 感叹号 + "主装备[#v"+dmID+"#][#e#b#t"+dmID+"#]#n#k一定要放在第一格。\r\n"
			text += "" + 感叹号 + "强化增加 [四维 #r#e+"+属性[需求数量-1]+"#k#n]、[攻魔 #r#e+"+(攻魔[需求数量-1])+"#k#n]\r\n"
			text += "" + 感叹号 + "进行第[ #r"+需求数量+"#k ]次强化：强化需要以下材料\r\n"
			text += "----------------------------------------------\r\n"
			text += "" + 感叹号 + "[  #v" + 配套物品[可以][1] + "# #e#r#z" + 配套物品[可以][1] + "##n  #r" + cm.getPlayer().getItemQuantity( 配套物品[可以][1], false) + "#k/"+ 配套物品[可以][2]+" ]["+是否0+"] #l \r\n\r\n"
			text += "" + 感叹号 + "[  "+金币图标+"  #r" + (cm.getMeso()) + "#k/"+(金币[需求数量-1])+" ]["+是否2+"] #l \r\n\r\n"
			text += "" + 感叹号 + "成功率：[ #r"+成功率[需求数量-1]+"%#k ]\r\n"
			text += "----------------------------------------------\r\n"
			text += "" + 感叹号 + "请收集指定装备进行强化\r\n"
			text += "" + 感叹号 + "请把这几件套放进背包,强化后[#r装备会消失#k]\r\n"
			text += "" + 感叹号 + "第一格放需要强化的装备，第二格子放材料\r\n"
				
        cm.sendSimple(text);
    } else if (status == 1) {
		var statup = new java.util.ArrayList();
		var item = cm.getInventory(1).getItem(1).copy();
		var 属性17 = item.getHands();//手技
		var dmID = cm.getInventory(1).getItem(1).getItemId();
		var 需求数量 = (属性17+1);		
        sels = selection;
		if(cm.getInventory(1).getItem(1)== null ){
		    cm.sendOk("请把 #r#e需要强化的#k#n #v"+dmID+"#放在第#r#e 1 #k#n格才能进行。");
			cm.dispose();
			return;
		}
		for(var i=2;i<((配套物品[可以][2])+2);i++){
			if(cm.getInventory(1).getItem(i)== null ){
				cm.sendOk("请把 #r#e作为材料的#k#n #v"+dmID+"#放在第#r#e "+i+" #k#n格才能进行。");
				cm.dispose();
				return;
			}
		}
		if(cm.getInventory(1).getItem(1).getItemId()!= dmID ){
		    cm.sendOk("请把 #r#e需要强化的#k#n #v"+dmID+"#放在第#r#e 1 #k#n格才能进行f。");
			cm.dispose();
			return;
		}
	///////////////////////////////////////////		
		for(var i=2;i<((配套物品[可以][2])+2);i++){
			if(cm.getInventory(1).getItem(i).getItemId()!= 配套物品[可以][1] ){
				cm.sendOk("请把 #r#e作为材料的#k#n #v"+配套物品[可以][1]+"#放在第#r#e "+i+" #k#n格才能进行。");
				cm.dispose();
				return;
			}
		}
		if(cm.getMeso()<((金币[需求数量-1]))){
			cm.sendOk("金币不足 "+((金币[需求数量-1]))+"");
			cm.dispose();
			return;
		}
			//////////////////////////////////////////
		
		
		
		
		
        var text1 ="";
			text1 +="当前装备栏第#r#e 1 #k#n格#i" + dmID + "#强化等级为：#r#e"+属性17+"\r\n";
			text1 +="#k#n是否要进行#r#r #i" + dmID + "##k#n的第 #r#e"+(属性17+1)+"#k#n 次强化? \r\n";
			text1 +="成功率：[ #r"+成功率[需求数量-1]+"%#k ]\r\n";
			text1 +="将会按顺序从装备栏第#r#e 2 #k#n格扣除#v"+配套物品[可以][1]+"#\r\n";
			cm.sendYesNo(text1);
    } else if (status == 2) {
		随机数 = Math.floor((Math.random()*100));
		var statup = new java.util.ArrayList();
		var item = cm.getInventory(1).getItem(1).copy();
		var 属性17 = item.getHands();//手技
		var dmID = cm.getInventory(1).getItem(1).getItemId();
		var 需求数量 = (属性17+1);	
			if(随机数<=成功率[需求数量-1]){
				cm.gainMeso(-(金币[需求数量-1])*系数);
				var statup = new java.util.ArrayList();
				var item = cm.getInventory(1).getItem(1).copy();
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
				/*item.setStr(  item.getStr());
				item.setInt(  item.getInt());
				item.setLuk(  item.getLuk());
				item.setDex(  item.getDex());
				item.setWatk( item.getWatk());//攻击
				item.setMatk( item.getMatk());//魔法力
				item.setWdef( item.getWdef());//物理防御
				item.setMdef( item.getMdef());//魔法防御
				item.setHp(   item.getHp());//给HP
				item.setMp(   item.getMp());//给MP
				item.setAcc(  item.getAcc());//命中
				item.setAvoid(item.getAvoid());//回避
				item.setJump( item.getJump());//跳跃
				item.setSpeed(item.getSpeed());//移动
				item.setOwner(item.getOwner());
				item.setHands(item.getHands()+1);
				item.setLevel(item.getLevel());//已升级次数 （装备+几）
				item.setUpgradeSlots(item.getUpgradeSlots());//剩余升级次数*/
				Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
				cm.gainItem(配套物品[可以][1], -配套物品[可以][2]);	
				Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);

				//cm.道具喇叭(item,"【装备进阶】 : 玩家 [" + cm.getPlayer().getName() + " ]进阶装备成功");
				cm.sendNext("#b已经强化好了，请前往背包查看");
			    cm.getItemMegaphone("[装备吞噬]：恭喜 "+cm.getPlayer().getName()+" 吞噬成功!",item);
				cm.dispose();
				return;
			}else{
				cm.gainMeso(-(金币[需求数量-1])*系数);
				cm.gainItem(配套物品[可以][1], -配套物品[可以][2]);	
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

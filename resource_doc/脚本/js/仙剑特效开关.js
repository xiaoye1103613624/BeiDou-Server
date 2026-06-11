//importPackage(java.lang);
//importPackage(Packages.tools);
//importPackage(Packages.client);
//importPackage(Packages.server);
//importPackage(Packages.tools.packet);
var status = -1;
var selected = null;
var 力量A = 0;
var 敏捷A = 0;
var 运气A = 0;
var 智力A = 0;
var 攻击A = 0;
var 魔力A = 0;
var 开 = "#fUI/Basic/CheckBox/0#";   //有框框 无√
var 关 = "#fUI/Basic/CheckBox/1#";   //有框框 有√
var 制作 = "#fUI/UIWindow/Maker/BtStart/mouseOver/0#";
var 锁头 = "#fUI/UIWindow/ItemProtector/Icon/0#";
var 小lv数字0 ="#fUI/Basic/ShowLevel/0/0#";
var 小lv数字1 ="#fUI/Basic/ShowLevel/0/1#";
var 小lv数字2 ="#fUI/Basic/ShowLevel/0/2#";
var 小lv数字3 ="#fUI/Basic/ShowLevel/0/3#";
var 小lv数字4 ="#fUI/Basic/ShowLevel/0/4#";
var 小lv数字5 ="#fUI/Basic/ShowLevel/0/5#";
var 小lv数字6 ="#fUI/Basic/ShowLevel/0/6#";
var 小lv数字7 ="#fUI/Basic/ShowLevel/0/7#";
var 小lv数字8 ="#fUI/Basic/ShowLevel/0/8#";
var 小lv数字9 ="#fUI/Basic/ShowLevel/0/9#";
var 小lv ="#fUI/Basic/ShowLevel/0/left#";
var 黄条上 = "#fUI/ChatBalloon.img/pet/25/head#";
var 黄条下 = "#fUI/ChatBalloon.img/pet/25/s#";
var 黄条下左 = "#fUI/ChatBalloon.img/pet/25/sw#";
var 黄条下右 = "#fUI/ChatBalloon.img/pet/25/se#";
var 黄条左 = "#fUI/ChatBalloon.img/pet/25/nw#";
var 黄条右 = "#fUI/ChatBalloon.img/pet/25/ne#";
var 五子棋 = "#fUI/ChatBalloon.img/miniroom/Omok#";
var 斜金币 = "#fUI/ChatBalloon.img/miniroom/PersonalShop#";
var 熊猫 = "#fUI/ChatBalloon.img/pet/1/nw#";
var 毛球 = "#fUI/ChatBalloon.img/pet/12/nw#";
var 金冠 = "#fUI/UIWindow.img/UserInfo/bossPetCrown#";
var 红蓝点 = "#fEffect/CharacterEff.img/1032054/0/0#";
var 蓝星 = "#fEffect/CharacterEff.img/1052203/1/0#";
var 红星 = "#fEffect/CharacterEff.img/1052203/2/0#";
var 大蓝星 = "#fEffect/CharacterEff.img/1022223/2/0#";
var 大红星 = "#fEffect/CharacterEff.img/1022223/1/0#";
var 蓝点 = "#fEffect/CharacterEff.img/1022223/6/0#";
var 红点 = "#fEffect/CharacterEff.img/1022223/7/0#";
var 右边框 ="#fUI/Basic/ShowLevel/Bracket/right#";
var 美化1 = "#fUI/ChatBalloon.img/pet/218/nw#";//选择道具
var 美化3 = "#fUI/ChatBalloon.img/pet/218/ne#";//选择道具
var 美化2 = "#fUI/ChatBalloon.img/pet/218/n#";//选择道具
var 美化4 = "#fUI/ChatBalloon.img/pet/218/sw#";//选择道具
var 美化5 = "#fUI/ChatBalloon.img/pet/218/se#";//选择道具
var 美化6 = "#fUI/ChatBalloon.img/pet/218/s#";//选择道具
var 美化7 = "#fUI/ChatBalloon.img/156/arrow#";//选择道具
var 装备 = "#fUI/UIWindow/MaplepointShop/Tab/enabled/0#";
var 红心 = "#fUI/UIWindow/Megaphone/0#";
var 小蘑菇 = "#fUI/UIWindow/MinigameTable/BtUP/normal/0#";
var 制作 = "#fUI/UIWindow/Maker/BtStart/mouseOver/0#";
var 推荐 = "#fUI/UIWindow/Shop/TabBuy/enabled/1#";
var 右边 = "#fUI/UIWindow/UserList/Guild/GuildRank/BtRight/normal/0#";
var 金锤子 = "#fUI/UIWindow/ViciousHammer/EffectP/1#";
var 向下 = "#fUI/UIWindow/MinigameTable/BtDown/mouseOver/0#";
var 红枫 = "#fUI/UIWindow/MonsterCarnival/icon0#";
var 蓝枫 = "#fUI/UIWindow/MonsterCarnival/icon1#";
var 上升 = "#fUI/StatusBar/QuickSlot/ani/1#";
var 金币 = "#fUI/UIWindow/TradingRoom/BtCoin/normal/0#";
var 窗口名称="特效开/关互换";


var itemObjS = {
//	"1116049": [[4310143, 1],['进阶', 1116052],['属性', 0,0,0,0,0,0]], //帽子
//	"1116052": [[4310143, 1],['进阶', 1116049],['属性', 0,0,0,0,0,0]], //帽子
	"1115234": [[4310143, 1],['进阶', 1115434],['属性', 0,0,0,0,0,0]], //帽子
	"1115434": [[4310143, 1],['进阶', 1115234],['属性', 0,0,0,0,0,0]], //帽子
	
	"1112575": [[4310143, 1],['进阶', 1112542],['属性', 0,0,0,0,0,0]], //仙剑
	"1112542": [[4310143, 1],['进阶', 1112575],['属性', 0,0,0,0,0,0]], //仙剑
	
	"1112543": [[4310143, 1],['进阶', 1112576],['属性', 0,0,0,0,0,0]], //仙剑
	"1112576": [[4310143, 1],['进阶', 1112543],['属性', 0,0,0,0,0,0]], //仙剑
	
}
function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1)
        status++;
    else {
        cm.dispose();
        return;
    }
    if (status == 0) {
        var 
	    text = "\t#r#e   	     "+ 红星 + ""+ 大红星 + ""+ 红点 + "" + cm.开服名称() + ""+ 红蓝点 + ""+ 蓝点 + ""+ 大蓝星 + ""+ 蓝星 + "#k \r\n";
		text += ""+ 黄条左 + ""+ 黄条上 + ""+ 黄条上 + ""+ 黄条上 + ""+ 黄条上 + ""+ 黄条上 + ""+ 黄条上 + ""+ 金冠 + "#b#e#r"+窗口名称+"#b#n"+ 金冠 + ""+ 黄条上 + ""+ 黄条上 + ""+ 黄条上 + ""+ 黄条上 + ""+ 黄条上 + ""+ 黄条上 + ""+ 黄条右 + "#k  \r\n";
		for(var oKey in itemObjS) {
			text += "#b#L"+ oKey +"    #使用 #v"+ oKey +"##z"+ oKey +"#  互换 \r\n\r\n";
		}
			text += ""+ 黄条下左 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下右 + "#k  ";
        cm.sendSimple(text);
    } else if (status == 1) {
		if(!itemObjS[selection]) {
			cm.sendOk("数据错误！");
            cm.dispose();
            return;
		}
		selected = selection;
		var 
	    text = ""+美化1+""+美化2+""+美化2+""+美化2+""+美化2+"『特效装备开/关互换』"+美化2+""+美化2+""+美化2+""+美化2+""+美化3+"#k\r\n\r\n";
		// text += "    "+制作+"  [ #v"+ selection +"##r#z"+ selection +"# "+小lv+""+小lv数字9+""+小lv数字0+""+右边框+"  ]#k\r\n";
		text += " "+红心+" #b特别说明：互换后所有属性依然在！#k\r\n";
		text += " "+红心+" 如果你拥有以下材料可以为你提供互换服务：\r\n";
		var needItems = itemObjS[selection];
		for(var i = 0; i < needItems.length; i++) {
		var 是否 = "#r未满足 "+开+"#k"; 
		var 是否100 = "#r未满足 "+开+"#k"; 
		var 是否101 = "#r未满足 "+开+"#k"; 
			if('金币' == needItems[i][0]) {
				if(cm.getPlayer().getMeso() > needItems[i][1]) {
					var 是否100 = "#g已满足"+关+"#k";
				}
				text += "\r\n "+红心+" [ "+金币+" 金币 x "+ needItems[i][1]/10000 +"万  / #r"+cm.getPlayer().getMeso()/10000+"#k 万 ] "+是否100+"\r\n";
			} else if ('属性' == needItems[i][0]) {
			text += "#r" +美化4+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化5+"#k#k\r\n";
			} else if ('点券' == needItems[i][0]) {
				if(cm.getPlayer().getCSPoints(1) > needItems[i][1]) {
					var 是否101 = "#g已满足"+关+"#k";
				}
				text += "点券 x "+ needItems[i][1] +""+是否101+"\r\n";
			}else if ('进阶' == needItems[i][0]) {
				// text += "进阶 x #v"+ needItems[i][1] +"#\r\n";

		        text += "\r\n        使用[#d #v"+ selection +":#]  #r→#k 换 #r→#k  [ #v"+needItems[i][1]+":# ]#k\r\n";
			} else {
				if(cm.haveItem(needItems[i][0], needItems[i][1])) {
				var 是否 = "#g已满足"+关+"#k";
			}
				text += " "+红心+" [ #v"+ needItems[i][0] +"# ]#k × [ "+ needItems[i][1]+ " / #r#c"+ needItems[i][0] +"##k 个 ] [ "+是否+" ]\r\n";
			}
		}
		cm.sendSimple(text);
    } else if (status == 2) {
		var item1 = cm.getInventory(1).getItem(1);
		var ii = Packages.server.MapleItemInformationProvider.getInstance();
		
		
		var msg = "";
			if (item1 == null) {
					msg += "装备栏第一格没有物品，请放入继承装备。\r\n";
			} else if (item1.getItemId() != selected) {
					msg += "装备栏第一格需要放入继承装备\r\n"
			} else if (cm.getInventory(1).isFull(0)){//判断第一个也就是装备栏的装备栏是否有一个空格
					msg += "您的背包已满，无法存放新装备。请清理背包空间后再试。\r\n";
			} else {
			var 手技17 = item1.getHands(); // 获取装备的手技属性
		//	if (手技17 != 0) {
		//			msg += "该装备没有强化满级，无法进行操作。\r\n请在#b枫叶城堡#r装备强化大师#k处强化满级才可神铸！\r\n";
		//		}
			}
		
		var needItems = itemObjS[selected];
		for(var i = 0; i < needItems.length; i++) {
			if('金币' == needItems[i][0]) {
				if(cm.getPlayer().getMeso() < needItems[i][1]) {
					msg += "金币 不足 "+ needItems[i][1] +"\r\n"
				}
			} else if ('点券' == needItems[i][0]) {
				if(cm.getPlayer().getCSPoints(1) < needItems[i][1]) {
					msg += "点券 不足 "+ needItems[i][1] +"\r\n"
				}
			} else if ('属性' == needItems[i][0]) {
			} else if ('进阶' == needItems[i][0]) {
			} else if(!cm.haveItem(needItems[i][0], needItems[i][1])) {
				msg += "#v"+ needItems[i][0] +"##b#z"+ needItems[i][0] +"##k x "+ needItems[i][1] + " 不足\r\n"
			}
		}
		if('' !== msg) {
			cm.sendOk(msg);
            cm.dispose();
            return;
		}
		for(var i = 0; i < needItems.length; i++) {
			if('金币' == needItems[i][0]) {
				cm.gainMeso(-needItems[i][1]);
			} else if ('点券' == needItems[i][0]) {
				cm.gainNX(-needItems[i][1]);
			} else if ('属性' == needItems[i][0]) {
				 力量A = needItems[i][1];
				 敏捷A = needItems[i][2];
				 运气A = needItems[i][3];
				 智力A = needItems[i][4];
				 攻击A = needItems[i][5];
				 魔力A = needItems[i][6];
			}else if ('进阶' == needItems[i][0]) {
				var 进阶武器 = needItems[i][1];
				var item2 = ii.randomizeStats(ii.getEquipById(进阶武器)).copy(); 
			}else {
				cm.gainItem(needItems[i][0], -needItems[i][1]);
			}
		}
		for(var i = 0; i < needItems.length; i++) {
			//	item2.setFlag(1);//上锁
				item2.setStr(item1.getStr()+0);  //力量
                item2.setDex(item1.getDex()+0);  //敏捷
                item2.setInt(item1.getInt()+0);  //智力
                item2.setLuk(item1.getLuk()+0); //运气----四维
                item2.setHp(item1.getHp());
                item2.setMp(item1.getMp());
                item2.setWatk(item1.getWatk()+0);//攻击力
                item2.setMatk(item1.getMatk()+0);//魔法力
                item2.setWdef(item1.getWdef()); //物防
                item2.setMdef(item1.getMdef()); //模仿
				item2.setAcc(item1.getAcc()); //命中
                item2.setAvoid(item1.getAvoid()); //回避
                item2.setHands(item1.getHands()); //手技
		//		item2.setHands(0);//因为要强化后面的装备用到手技，这里把手技等于0
                item2.setSpeed(item1.getSpeed()); //移速
                item2.setJump(item1.getJump()); //跳跃
				item2.setOwner(item1.getOwner());
				//item2.setLevel(item1.getLevel()); //已经提升的 砸卷显示的次数
				item2.setUpgradeSlots(item2.getUpgradeSlots());
				cm.addFromDrop(item2);
				cm.gainItem(item1.getItemId(),-1);
				cm.sendOk("装备属性转移成功");
				cm.dispose();
				return;
		}
	}
}
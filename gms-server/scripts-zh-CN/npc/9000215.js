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
var 制作 = "#fEffect/UIWindow/Maker/BtStart/mouseOver/0#";
var 锁头 = "#fEffect/UIWindow/ItemProtector/Icon/0#";
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
var 右边框 ="#fUI/Basic/ShowLevel/Bracket/right#";
var 美化1 = "#fUI/ChatBalloon.img/pet/218/nw#";//选择道具
var 美化3 = "#fUI/ChatBalloon.img/pet/218/ne#";//选择道具
var 美化2 = "#fUI/ChatBalloon.img/pet/218/n#";//选择道具
var 美化4 = "#fUI/ChatBalloon.img/pet/218/sw#";//选择道具
var 美化5 = "#fUI/ChatBalloon.img/pet/218/se#";//选择道具
var 美化6 = "#fUI/ChatBalloon.img/pet/218/s#";//选择道具
var 美化7 = "#fUI/ChatBalloon.img/156/arrow#";//选择道具
var 装备 = "#fEffect/UIWindow/MaplepointShop/Tab/enabled/0#";
var 红心 = "#fEffect/UIWindow/Megaphone/0#";
var 小蘑菇 = "#fEffect/UIWindow/MinigameTable/BtUP/normal/0#";
var 制作 = "#fEffect/UIWindow/Maker/BtStart/mouseOver/0#";
var 推荐 = "#fEffect/UIWindow/Shop/TabBuy/enabled/1#";
var 右边 = "#fEffect/UIWindow/UserList/Guild/GuildRank/BtRight/normal/0#";
var 金锤子 = "#fEffect/UIWindow/ViciousHammer/EffectP/1#";
var 向下 = "#fEffect/UIWindow/MinigameTable/BtDown/mouseOver/0#";
var 红枫 = "#fEffect/UIWindow/MonsterCarnival/icon0#";
var 蓝枫 = "#fEffect/UIWindow/MonsterCarnival/icon1#";
var 上升 = "#fUI/StatusBar/QuickSlot/ani/1#";
var 金币 = "#fEffect/UIWindow/TradingRoom/BtCoin/normal/0#";


var itemObjS = {
	"1005004": [[2048401, 1],[1352975, 1],[4310038, 100], [4001245, 188],[4001268, 38],[4001269, 38],['金币', 2000000000],['进阶', 1003624],['属性', 100,100,100,100,100,100]], //帽子
	"1053309": [[2048401, 1],[1352975, 1],[4310038, 100], [4001245, 188],[4001268, 38],[4001269, 38],['金币', 2000000000],['进阶', 1052532],['属性', 100,100,100,100,100,100]], //衣服
	"1072637": [[2048401, 1],[1352975, 1],[4310038, 100], [4001245, 188],[4001268, 38],[4001269, 38],['金币', 2000000000],['进阶', 1072695],['属性', 100,100,100,100,100,100]], //靴子
	"1082671": [[2048401, 1],[1352975, 1],[4310038, 100], [4001245, 188],[4001268, 38],[4001269, 38],['金币', 2000000000],['进阶', 1082490],['属性', 100,100,100,100,100,100]], //手套
	"1102946": [[2048401, 1],[1352975, 1],[4310038, 100], [4001245, 188],[4001268, 38],[4001269, 38],['金币', 2000000000],['进阶', 1102948],['属性', 100,100,100,100,100,100]], //披风
	"1132142": [[2048401, 1],[1352975, 1],[4310038, 100], [4001245, 188],[4001268, 38],[4001269, 38],['金币', 2000000000],['进阶', 1132187],['属性', 100,100,100,100,100,100]], //腰带
	
	"1098009": [[2048401, 1],[1352975, 1],[4310038, 300], [4001245, 388],[4001268, 58],[4001269, 58],['金币', 2000000000],['进阶', 1098008],['属性', 100,100,100,100,100,100]], //盾牌
	
	"1402334": [[2048401, 1],[1352975, 1],[4310038, 200], [4001245, 288],[4001268, 48],[4001269, 48],['金币', 2000000000],['进阶', 1402332],['属性', 100,100,100,100,100,100]], //双手剑
	"1432308": [[2048401, 1],[1352975, 1],[4310038, 200], [4001245, 288],[4001268, 48],[4001269, 48],['金币', 2000000000],['进阶', 1432306],['属性', 100,100,100,100,100,100]], //枪
	"1442116": [[2048401, 1],[1352975, 1],[4310038, 200], [4001245, 288],[4001268, 48],[4001269, 48],['金币', 2000000000],['进阶', 1442122],['属性', 100,100,100,100,100,100]], //长矛
	"1382269": [[2048401, 1],[1352975, 1],[4310038, 200], [4001245, 288],[4001268, 48],[4001269, 48],['金币', 2000000000],['进阶', 1382267],['属性', 100,100,100,100,100,100]], //长杖
	"1452309": [[2048401, 1],[1352975, 1],[4310038, 200], [4001245, 288],[4001268, 48],[4001269, 48],['金币', 2000000000],['进阶', 1452307],['属性', 100,100,100,100,100,100]], //弓
	"1462247": [[2048401, 1],[1352975, 1],[4310038, 200], [4001245, 288],[4001268, 48],[4001269, 48],['金币', 2000000000],['进阶', 1462245],['属性', 100,100,100,100,100,100]], //弩
	"1472269": [[2048401, 1],[1352975, 1],[4310038, 200], [4001245, 288],[4001268, 48],[4001269, 48],['金币', 2000000000],['进阶', 1472267],['属性', 100,100,100,100,100,100]], //拳套
	"1332283": [[2048401, 1],[1352975, 1],[4310038, 200], [4001245, 288],[4001268, 48],[4001269, 48],['金币', 2000000000],['进阶', 1332281],['属性', 100,100,100,100,100,100]], //短刀
	"1482206": [[2048401, 1],[1352975, 1],[4310038, 200], [4001245, 288],[4001268, 48],[4001269, 48],['金币', 2000000000],['进阶', 1482204],['属性', 100,100,100,100,100,100]], //拳套
	"1492239": [[2048401, 1],[1352975, 1],[4310038, 200], [4001245, 288],[4001268, 48],[4001269, 48],['金币', 2000000000],['进阶', 1492237],['属性', 100,100,100,100,100,100]], //短刀

	
	
	
	
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
	    text = ""+美化1+""+美化2+""+美化2+""+美化2+""+美化2+""+美化2+""+美化2+"『装备神铸』"+美化2+""+美化2+""+美化2+""+美化2+""+美化2+""+美化3+"#k\r\n\r\n";
		for(var oKey in itemObjS) {
			text += "#L"+ oKey +"#"+制作+"     #v"+ oKey +"##b#z"+ oKey +"# "+小lv+""+小lv数字1+""+小lv数字0+""+小lv数字0+"#k\r\n\r\n";
		}
			//text += "#r" +美化4+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化5+"#k#k\r\n";
        cm.sendSimple(text);
    } else if (status == 1) {
		if(!itemObjS[selection]) {
			cm.sendOk("数据错误！");
            cm.dispose();
            return;
		}
		selected = selection;
		var 
	    text = ""+美化1+""+美化2+""+美化2+""+美化2+""+美化2+""+美化2+""+美化2+"『装备打造』"+美化2+""+美化2+""+美化2+""+美化2+""+美化2+""+美化3+"#k\r\n\r\n";
		// text += "    "+制作+"  [ #v"+ selection +"##r#z"+ selection +"# "+小lv+""+小lv数字9+""+小lv数字0+""+右边框+"  ]#k\r\n";
		text += " "+红心+" 如果你拥有以下材料：\r\n";
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
				text += "             #d额外附加 ["+上升+" #r力量 + "+ needItems[i][1] +"#k]\r\n";
				text += "             #d额外附加 ["+上升+" #r敏捷 + "+ needItems[i][2] +"#k]\r\n";
				text += "             #d额外附加 ["+上升+" #r智力 + "+ needItems[i][3] +"#k]\r\n";
				text += "             #d额外附加 ["+上升+" #r运气 + "+ needItems[i][4] +"#k]\r\n";
				text += "             #d额外附加 ["+上升+" #r攻击 + "+ needItems[i][5] +"#k]\r\n";
				text += "             #d额外附加 ["+上升+" #r魔力 + "+ needItems[i][6] +"#k]\r\n";
				text += "\r\n                     #b【特别说明】\r\n";
				text += "          #r首饰6件 #d触发套装属性#r20%#d额外附加伤害 #n#k\r\n";
				text += "          #r武器1件 #d触发套装属性#r20%#d额外附加伤害 #n#k\r\n";
				text += "          #r盾牌1件 #d触发套装属性#r20%#d额外附加伤害 #n#k\r\n";
			text += "#r" +美化4+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化5+"#k#k\r\n";
			} else if ('点券' == needItems[i][0]) {
				if(cm.getPlayer().getCSPoints(1) > needItems[i][1]) {
					var 是否101 = "#g已满足"+关+"#k";
				}
				text += "点券 x "+ needItems[i][1] +""+是否101+"\r\n";
			}else if ('进阶' == needItems[i][0]) {
				// text += "进阶 x #v"+ needItems[i][1] +"#\r\n";
		        text += "         "+蓝枫+"[#d #v"+ selection +"##b#z"+ selection +"# "+小lv+""+小lv数字1+""+小lv数字0+""+小lv数字0+""+右边框+" "+装备+" ]#k\r\n\r\n";
		// text += "\r\n     "+向下+" "+向下+" "+向下+" "+向下+"  "+向下+" "+向下+" "+向下+" "+向下+" "+向下+" "+向下+"#n#k\r\n";
		        text += "       [ "+向下+" #d所有属性将会继承到#r新装备上#d"+向下+" ] #n#k\r\n";
		        text += "       [ "+向下+" #d[可强化次数]恢复为#r全新次数#d"+向下+" ] #n#k\r\n";
		        text += "         "+红枫+"[ #v"+needItems[i][1]+"##r#z"+needItems[i][1]+"# "+小lv+""+小lv数字0+""+右边框+" "+装备+" #k]#k\r\n";
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
			if (手技17 != 10) {
					msg += "该装备没有强化满级，无法进行操作。\r\n请在#b大神殿#r装备强化大师#k处强化满级才可神铸！\r\n";
				}
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
				item2.setFlag(1);//上锁
				item2.setStr(item1.getStr()+100);  //力量
                item2.setDex(item1.getDex()+100);  //敏捷
                item2.setInt(item1.getInt()+100);  //智力
                item2.setLuk(item1.getLuk()+100); //运气----四维
                item2.setHp(item1.getHp());
                item2.setMp(item1.getMp());
                item2.setWatk(item1.getWatk()+100);//攻击力
                item2.setMatk(item1.getMatk()+100);//魔法力
                item2.setWdef(item1.getWdef()+300); //物防
                item2.setMdef(item1.getMdef()+300); //模仿
				item2.setAcc(item1.getAcc()+30); //命中
                item2.setAvoid(item1.getAvoid()+30); //回避
                //item2.setHands(item1.getHands()); //手技
				item2.setHands(0);//因为要强化后面的装备用到手技，这里把手技等于0
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
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


var itemObjS = {
	"1004492": [[4170016, 15],[4000464, 30], [4310088, 300], [4021009, 300],[4011007, 300],['金币', 500000000],['进阶', 1004234],['属性', 30,30,30,30,30,30]], //帽子
	"1052929": [[4170016, 15],[4000464, 30], [4310088, 300], [4021009, 300],[4011007, 300],['金币', 500000000],['进阶', 1052804],['属性', 30,30,30,30,30,30]], //衣服
	"1073057": [[4170016, 15],[4000464, 30], [4310088, 300], [4021009, 300],[4011007, 300],['金币', 500000000],['进阶', 1072972],['属性', 30,30,30,30,30,30]], //靴子
	"1082647": [[4170016, 15],[4000464, 30], [4310088, 300], [4021009, 300],[4011007, 300],['金币', 500000000],['进阶', 1082613],['属性', 30,30,30,30,30,30]], //手套
	"1102828": [[4170016, 15],[4000464, 30], [4310088, 300], [4021009, 300],[4011007, 300],['金币', 500000000],['进阶', 1102713],['属性', 30,30,30,30,30,30]], //披风
	"1132287": [[4170016, 15],[4000464, 30], [4310088, 300], [4021009, 300],[4011007, 300],['金币', 500000000],['进阶', 1132169],['属性', 30,30,30,30,30,30]], //腰带
	
	"1099003": [[4170016, 15],[4000464, 30], [4310088, 300], [4021009, 300],[4011007, 300],['金币', 1500000000],['进阶', 1099011],['属性', 30,30,30,30,30,30]], //盾牌
	
	"1302276": [[4170016, 15],[4000464, 30], [4310088, 300], [4021009, 300],[4011007, 300],['金币', 1000000000],['进阶', 1302285],['属性', 30,30,30,30,30,30]], //单手剑
	"1402197": [[4170016, 15],[4000464, 30], [4310088, 300], [4021009, 300],[4011007, 300],['金币', 1000000000],['进阶', 1402204],['属性', 30,30,30,30,30,30]], //双手剑
	"1432168": [[4170016, 15],[4000464, 30], [4310088, 300], [4021009, 300],[4011007, 300],['金币', 1000000000],['进阶', 1432176],['属性', 30,30,30,30,30,30]], //枪
	"1442224": [[4170016, 15],[4000464, 30], [4310088, 300], [4021009, 300],[4011007, 300],['金币', 1000000000],['进阶', 1442232],['属性', 30,30,30,30,30,30]], //矛
	"1382209": [[4170016, 15],[4000464, 30], [4310088, 300], [4021009, 300],[4011007, 300],['金币', 1000000000],['进阶', 1382220],['属性', 30,30,30,30,30,30]], //长杖
	"1452206": [[4170016, 15],[4000464, 30], [4310088, 300], [4021009, 300],[4011007, 300],['金币', 1000000000],['进阶', 1452214],['属性', 30,30,30,30,30,30]], //弓
	"1462194": [[4170016, 15],[4000464, 30], [4310088, 300], [4021009, 300],[4011007, 300],['金币', 1000000000],['进阶', 1462202],['属性', 30,30,30,30,30,30]], //弩
	"1472215": [[4170016, 15],[4000464, 30], [4310088, 300], [4021009, 300],[4011007, 300],['金币', 1000000000],['进阶', 1472223],['属性', 30,30,30,30,30,30]], //拳套
	"1332226": [[4170016, 15],[4000464, 30], [4310088, 300], [4021009, 300],[4011007, 300],['金币', 1000000000],['进阶', 1332235],['属性', 30,30,30,30,30,30]], //短刀
	"1482169": [[4170016, 15],[4000464, 30], [4310088, 300], [4021009, 300],[4011007, 300],['金币', 1000000000],['进阶', 1482177],['属性', 30,30,30,30,30,30]], //指节
	"1492180": [[4170016, 15],[4000464, 30], [4310088, 300], [4021009, 300],[4011007, 300],['金币', 1000000000],['进阶', 1492188],['属性', 30,30,30,30,30,30]], //短枪
	
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
			text += "#L"+ oKey +"#"+制作+"     #v"+ oKey +"##b#z"+ oKey +"# "+小lv+""+小lv数字1+""+小lv数字2+""+小lv数字0+"#k\r\n\r\n";
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
			text += "#r" +美化4+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化5+"#k#k\r\n";
			} else if ('点券' == needItems[i][0]) {
				if(cm.getPlayer().getCSPoints(1) > needItems[i][1]) {
					var 是否101 = "#g已满足"+关+"#k";
				}
				text += "点券 x "+ needItems[i][1] +""+是否101+"\r\n";
			}else if ('进阶' == needItems[i][0]) {
				// text += "进阶 x #v"+ needItems[i][1] +"#\r\n";
		        text += "         "+蓝枫+"[#d #v"+ selection +"##b#z"+ selection +"# "+小lv+""+小lv数字1+""+小lv数字2+""+小lv数字0+""+右边框+" "+装备+" ]#k\r\n\r\n";
		// text += "\r\n     "+向下+" "+向下+" "+向下+" "+向下+"  "+向下+" "+向下+" "+向下+" "+向下+" "+向下+" "+向下+"#n#k\r\n";
		        text += "       [ "+向下+" #d所有属性将会继承到#r新装备上#d"+向下+" ] #n#k\r\n";
		        text += "       [ "+向下+" #d[可强化次数]恢复为#r全新次数#d"+向下+" ] #n#k\r\n";
		        text += "         "+红枫+"[ #v"+needItems[i][1]+"##r#z"+needItems[i][1]+"# "+小lv+""+小lv数字1+""+小lv数字5+""+小lv数字0+""+右边框+" "+装备+" #k]#k\r\n";
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
					msg += "该装备没有强化满级，无法进行操作。\r\n请在#b世界之树#r装备强化大师#k处强化满级才可神铸！\r\n";
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
				item2.setStr(item1.getStr()+30);  //力量
                item2.setDex(item1.getDex()+30);  //敏捷
                item2.setInt(item1.getInt()+30);  //智力
                item2.setLuk(item1.getLuk()+30); //运气----四维
                item2.setHp(item1.getHp());
                item2.setMp(item1.getMp());
                item2.setWatk(item1.getWatk()+30);//攻击力
                item2.setMatk(item1.getMatk()+30);//魔法力
                item2.setWdef(item1.getWdef()+30); //物防
                item2.setMdef(item1.getMdef()+30); //模仿
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
				// 获取装备名称
				var 进阶前 = Packages.server.MapleItemInformationProvider.getInstance().getName(item1.getItemId());
				var 进阶后 = Packages.server.MapleItemInformationProvider.getInstance().getName(item2.getItemId());
				cm.喇叭(1, "恭喜玩家:[" + cm.getPlayer().getName() + "] 使用 [" + 进阶前 + "] 成功进阶 [" + 进阶后 + "] ！");
				cm.喇叭(1, "恭喜玩家:[" + cm.getPlayer().getName() + "] 使用 [" + 进阶前 + "] 成功进阶 [" + 进阶后 + "] ！");
				cm.喇叭(1, "恭喜玩家:[" + cm.getPlayer().getName() + "] 使用 [" + 进阶前 + "] 成功进阶 [" + 进阶后 + "] ！");
				cm.dispose();
				return;
		}
	}
}
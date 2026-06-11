var LJ = "#fEffect/CharacterEff/1082565/2/0#";  //蓝兔子
var mi0 = "┏━━━━━━━━━━━┓";
var mi1 = "┃     - XiaoMiMS -     ┃";
var mi2 = "┃ 脚本仿制  　定制脚本 ┃";
var mi3 = "┃ 技术支持 　 游戏顾问 ┃";
var mi4 = "┃ ＷＺ添加　  地图制作 ┃";
var mi5 = "┣━━━━━━━━━━━┫";
var mi6 = "┃　唯一QQ:1408745    ┃";
var mi7 = "┗━━━━━━━━━━━┛";
var 抽奖中心 = "#fEffect/CharacterEff1.img/QQ1408745/0/5#";
var xiaomi = {        	
	开关: 0, 玩家: "南山" , 变量: "暗操中奖记录_01" , 物品: 1112666 ,
	喇叭开关:1 ,
	
};

status = -1;
var itemList = new Array(
/*[1050598,1000,1,1],
[1050532,1000,1,1],
[1050603,1000,1,1],
[1051213,1000,1,1],
[1051220,1000,1,1],
[1051294,1000,1,1],
[1051329,1000,1,1],
[1051391,1000,1,1],
[1051455,1000,1,1],
[1051461,1000,1,1],
[1051503,1000,1,1],
[1051672,1000,1,1],
[1051692,1000,1,1],
[1051695,1000,1,1],
[1052041,1000,1,1],
[1052083,1000,1,1],
[1052293,1000,1,1],
[1052294,1000,1,1],
[1052327,1000,1,1],
[1052332,1000,1,1],
[1052360,1000,1,1],
[1052372,1000,1,1],
[1052410,1000,1,1],
[1052415,1000,1,1],
[1052425,1000,1,1],
[1052426,1000,1,1],
[1052456,1000,1,1],
[1052531,1000,1,1],
[1052574,1000,1,1],
[1052597,1000,1,1],
[1052604,1000,1,1],
[1052644,1000,1,1],
[1052662,1000,1,1],
[1052676,1000,1,1],
[1052686,1000,1,1],
[1052697,1000,1,1],
[1052709,1000,1,1],
[1052749,1000,1,1],
[1052750,1000,1,1],
[1052757,1000,1,1],
[1052765,1000,1,1],
[1052770,1000,1,1],
[1052773,1000,1,1],
[1052779,1000,1,1],
[1052836,1000,1,1],
[1052875,1000,1,1],
[1052894,1000,1,1],
[1052904,1000,1,1],
[1052916,1000,1,1],
[1052923,1000,1,1],*/
[1052924,1000,1,1],
[1052925,1000,1,1],
[1052946,1000,1,1],
[1052949,1000,1,1],
[1052967,1000,1,1],
[1052972,1000,1,1],
[1053024,1000,1,1],
[1053025,1000,1,1],
[1053031,1000,1,1],
[1053034,1000,1,1],
[1053041,1000,1,1],
[1053042,1000,1,1],
[1053054,1000,1,1],
[1053084,1000,1,1],
[1053085,1000,1,1],
[1053091,1000,1,1],
[1053092,1000,1,1],
[1053103,1000,1,1],
[1053104,1000,1,1],
[1053107,1000,1,1],
[1053114,1000,1,1],
[1053124,1000,1,1],
[1053125,1000,1,1],
[1053138,1000,1,1],
[1053143,1000,1,1],
[1053146,1000,1,1],
[1053149,1000,1,1],
[1053151,1000,1,1],
[1053152,1000,1,1],
[1053154,1000,1,1],
[1053155,1000,1,1],
[1053164,1000,1,1],
[1053171,1000,1,1],
[1053173,1000,1,1],
[1053176,1000,1,1],
[1053177,1000,1,1],
[1053194,1000,1,1],
[1053209,1000,1,1],
[1053210,1000,1,1],
[1053219,1000,1,1],
[1053222,1000,1,1],
[1053225,1000,1,1],
[1053226,1000,1,1],
[1053228,1000,1,1],
[1053229,1000,1,1],
[1053234,1000,1,1],
[1053277,1000,1,1],
[1053278,1000,1,1],
[1053279,1000,1,1],
[1053295,1000,1,1],
[1053297,1000,1,1],
[1053298,1000,1,1],
[1053321,1000,1,1],
[1053323,1000,1,1],
[1053324,1000,1,1],
[1053325,1000,1,1],
[1053338,1000,1,1],
[1053349,1000,1,1],
[1053356,1000,1,1],
[1053357,1000,1,1],
[1053367,1000,1,1],
[1053387,1000,1,1],
[1053397,1000,1,1],
[1053399,1000,1,1],
[1053422,1000,1,1],
[1053423,1000,1,1],
[1053429,1000,1,1],
[1053433,1000,1,1],
[1053434,1000,1,1],
[1053435,1000,1,1],
[1053436,1000,1,1],
[1053437,1000,1,1],
[1053439,1000,1,1],
[1053444,1000,1,1],
[1053446,1000,1,1],
[1053448,1000,1,1],
[1053468,1000,1,1],
[1053474,1000,1,1],
[1053475,1000,1,1],
[1053482,1000,1,1],
[1053515,1000,1,1],
[1053518,1000,1,1],
[1053519,1000,1,1],
[1053521,1000,1,1],
[1053522,1000,1,1],
[1053548,1000,1,1],
[1053549,1000,1,1],
[1053563,1000,1,1],
[1053579,1000,1,1],
[1053598,1000,1,1],
[1053617,1000,1,1],
[1053619,1000,1,1],
[1053634,1000,1,1],
[1053640,1000,1,1],
[1053656,1000,1,1],
[1053657,1000,1,1],
[1053658,1000,1,1],
[1053659,1000,1,1],
[1053660,1000,1,1],
[1053670,1000,1,1],
[1053671,1000,1,1],
[1053688,1000,1,1],
[1053728,1000,1,1],
[1053729,1000,1,1],
[1053734,1000,1,1],
[1053735,1000,1,1],
[1053759,1000,1,1],
[1053761,1000,1,1],
[1053790,1000,1,1],
[1053797,1000,1,1],
[1053812,1000,1,1],
[1053813,1000,1,1],
[1053814,1000,1,1],
[1053826,1000,1,1],
[1053849,1000,1,1],
[1053850,1000,1,1],
[1053863,1000,1,1],
[1053864,1000,1,1],
[1053909,1000,1,1],
[1053910,1000,1,1],
[1053933,1000,1,1],
[1053938,1000,1,1],
[1053951,1000,1,1],
[1053952,1000,1,1],
[1053966,1000,1,1],
[1053986,1000,1,1],
[1053987,1000,1,1],
[1056016,1000,1,1],
[1056017,1000,1,1]

);



var 喇叭开关 = 1;//0为关1为开

var 点卷抽奖 = 10000;
//var 概率暴击 = cm.getBossRankCount3("概率暴击");
function start() {
	/*
	if (!cm.getPlayer().isGM()){
            cm.sendOk("飞天猪抽奖调整中.....");
            cm.dispose();		
	}	*/
	
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            cm.sendOk("不想使用吗？…我的肚子里有各类#b奇特座椅或卷轴、装备、新奇道具#k哦！");
            cm.dispose();
        }
        status--;
    }
    if (status == 0) {
		MapleItemInformationProvider = Packages.server.MapleItemInformationProvider;
		ii = MapleItemInformationProvider.getInstance();
           var text = "";
		   for (var i = 0; i < itemList.length;  i++){
                   text += "#i"+itemList[i][0]+":#";//
           }		
          // cm.sendYesNo("暂时关闭");
          // cm.dispose();    
        //  cm.getPlayer().dropTopMsg("暂时关闭抽奖 维护抽奖, 点解卡图标进行解卡暂");//cm.sendOk("背包所有栏需要留空位,当前检测位置 装备栏");
		 // if (cm.getPlayer().isGM() ) {          #L2#"+LJ+"点卷十连抽"+LJ+"#l
		    cm.sendYesNo("#r#r冒险岛一条龙服务#k★#bQQ:1408745#k★\r\n\t\t\t"+抽奖中心+"\r\n#k┌--------------------------------------------------┐\r\n\t\t\t\t#r#L1#"+LJ+"开始抽奖"+LJ+"#l\r\n #k\r\n└--------------------------------------------------┘\r\n#d单次抽奖需要消耗#r10000点卷#d，奖池物品如下：\r\n\r\n"+text);



        //  }   
    } else if (status == 1) {  
        
		var cjcs = 1;
		if (selection == 1) {
			if (cm.getChar().getCSPoints(1)<=点卷抽奖) {
				cm.sendOk("需要的物品不足，无法进行抽奖");
				cm.dispose();
                return;
			}	
    if (cm.getInventory(1).isFull()) {
        cm.getPlayer().dropTopMsg("请你确认在背包的装备，消耗，其他窗口中是否有2格以上的空间");//cm.sendOk("背包所有栏需要留空位,当前检测位置 装备栏");
        cm.dispose();
        return;
			}	
    if (cm.getInventory(2).isFull()) {
        cm.getPlayer().dropTopMsg("请你确认在背包的装备，消耗，其他窗口中是否有2格以上的空间");//cm.sendOk("背包所有栏需要留空位,当前检测位置 消耗栏");
         cm.dispose();
        return;
			}	
    if (cm.getInventory(3).isFull()) {
        cm.getPlayer().dropTopMsg("请你确认在背包的装备，消耗，其他窗口中是否有2格以上的空间");//cm.sendOk("背包所有栏需要留空位,当前检测位置 设置栏");
         cm.dispose();
        return;
			}	
    if (cm.getInventory(4).isFull()) {
        cm.getPlayer().dropTopMsg("请你确认在背包的装备，消耗，其他窗口中是否有2格以上的空间");//cm.sendOk("背包所有栏需要留空位,当前检测位置 其他栏");
         cm.dispose();
        return;
			}	
    if (cm.getInventory(5).isFull()) {
       cm.getPlayer().dropTopMsg("请你确认在背包的装备，消耗，其他窗口中是否有2格以上的空间");// cm.sendOk("背包所有栏需要留空位,当前检测位置 特殊栏");
         cm.dispose();
        return;
			}	
   // } else if (cm.getPlayer().getName()==0) {//黑名单名字
    //    cm.sendOk("由于你被判断为交易行黑名单无法使用交易行");
    //     cm.dispose();
    //    return;
   // }
//}else{
		var chance = Math.floor(Math.random() * 1000);
        var finalitem = Array();
		     
        for (var i = 0; i < itemList.length; i++) {
            if (itemList[i][1] >= chance) {
                finalitem.push(itemList[i]);
            }
        }
        if (finalitem.length != 0) {
			
            var item;
            var random = new java.util.Random();
            var finalchance = random.nextInt(finalitem.length);
            var itemId = finalitem[finalchance][0];
            var quantity = finalitem[finalchance][2];
            var notice = finalitem[finalchance][3];
			//item = cm.gainGachaponItem(itemId, quantity,  "刮刮乐");
			
			
            if (cm.canHold(itemId,1)) {
				cm.gainNX(-点卷抽奖);
cm.gainGachaponItem(itemId, quantity, "元宝抽奖");
				//cm.setmoneyb(-点卷抽奖) 
				//if (xiaomi.开关 == 1 && cm.getPlayer().getName() == xiaomi.玩家 && cm.getPlayer().getxmdailyloga(xiaomi.变量) == 0) {
				////	itemId = xiaomi.物品;
			//		cm.getPlayer().gainxmdailyloga(xiaomi.变量,+1);
			//	}
			//	cm.gainGachaponItem(itemId, quantity);
				//item = cm.gainGachaponItem(itemId, quantity,  "刮刮乐");item = cm.gainGachaponItem(itemId, quantity, "元宝抽奖", notice);//cm.gainGachaponItem(itemId, quantity, "元宝抽奖");
				//gainxmwnjlc("小米_抽奖积分",1);
				//cm.setBossRank2("小米_抽奖积分",1,+1);
				//gainxmwnjlc("小米_抽奖积分",1);
				cm.sendOk("你获得了 #b#v" + itemId + "##k " + quantity + "个。");
				
				
					
				
				
				
            } else {
                cm.getPlayer().dropTopMsg("请你确认在背包的装备，消耗，其他窗口中是否有1格以上的空间");
                cm.getPlayer().dropTopMsg("请你确认在背包的装备，消耗，其他窗口中是否有1格以上的空间");
 cm.showInstruction("#r★★温馨提示★★\r\n\r\n#b请你确认在背包的\r\n装备，消耗，其他窗口中\r\n是否有1格以上的空间#r\r\n",300,1);
	cm.dispose();
            }
            //cm.safeDispose();
	status = -1;
        //} else {
            //cm.sendOk("今天的运气可真差，什么都没有拿到。");
           // cm.gainItem(抽奖需要物品, -点卷抽奖*cjcs);
//cm.setmoneyb(-点卷抽奖) 
            // cm.gainItem(4001322, 1);
            cm.safeDispose();
        }
/*		
		} else if (selection == 2) {
			if (cm.getChar().getCSPoints(1)<=10000) {
                cm.getPlayer().dropTopMsg("需要的物品不足，无法进行10连抽");
                cm.getPlayer().dropTopMsg("需要的物品不足，无法进行10连抽");
//cm.setmoneyb(9999) 
                cm.showInstruction("#r★★温馨提示★★\r\n\r\n#b需要的物品不足，无法进行10连抽\r\n",300,1);
				cm.dispose();
                return;
			}
    if (cm.getInventory(1).isFull(9)) {
        cm.getPlayer().dropTopMsg("请你确认在背包的装备，消耗，其他窗口中是否有10格以上的空间");//cm.sendOk("背包所有栏需要留空位,当前检测位置 装备栏");
        cm.showInstruction("#r★★温馨提示★★\r\n\r\n#b需要的物品不足，无法进行10连抽\r\n",300,1);
        cm.dispose();
        return;
			}	
    if (cm.getInventory(2).isFull(9)) {
        cm.getPlayer().dropTopMsg("请你确认在背包的装备，消耗，其他窗口中是否有10格以上的空间");//cm.sendOk("背包所有栏需要留空位,当前检测位置 消耗栏");
cm.showInstruction("#r★★温馨提示★★\r\n\r\n#b需要的物品不足，无法进行10连抽\r\n",300,1);
         cm.dispose();
        return;
			}	
    if (cm.getInventory(3).isFull(9)) {
        cm.getPlayer().dropTopMsg("请你确认在背包的装备，消耗，其他窗口中是否有10格以上的空间");//cm.sendOk("背包所有栏需要留空位,当前检测位置 设置栏");
cm.showInstruction("#r★★温馨提示★★\r\n\r\n#b需要的物品不足，无法进行10连抽\r\n",300,1);
         cm.dispose();
        return;
			}	
    if (cm.getInventory(4).isFull(9)) {
        cm.getPlayer().dropTopMsg("请你确认在背包的装备，消耗，其他窗口中是否有10格以上的空间");//cm.sendOk("背包所有栏需要留空位,当前检测位置 其他栏");
cm.showInstruction("#r★★温馨提示★★\r\n\r\n#b需要的物品不足，无法进行10连抽\r\n",300,1);
         cm.dispose();
        return;
			}	
    if (cm.getInventory(5).isFull(9)) {
       cm.getPlayer().dropTopMsg("请你确认在背包的装备，消耗，其他窗口中是否有10格以上的空间");// cm.sendOk("背包所有栏需要留空位,当前检测位置 特殊栏");
cm.showInstruction("#r★★温馨提示★★\r\n\r\n#b需要的物品不足，无法进行10连抽\r\n",300,1);
         cm.dispose();
        return;
			}	
			if (!canHoldSlots(10)) {
                cm.getPlayer().dropTopMsg("请你确认在背包的装备，消耗，其他窗口中是否有10格以上的空间");
                cm.getPlayer().dropTopMsg("请你确认在背包的装备，消耗，其他窗口中是否有10格以上的空间");
cm.showInstruction("#r★★温馨提示★★\r\n\r\n#b请你确认在背包的\r\n装备，消耗，其他窗口中\r\n是否有10格以上的空间#r\r\n",300,1);

				cm.dispose();
                return;
				
			}*/
			
			cjcs = 10;
			
		

		var slcwpjl = "";
		for (var C = 0; C < cjcs; C++) {
			
		var chance = Math.floor(Math.random() * 100);
        var finalitem = Array();
		     
        for (var i = 0; i < itemList.length; i++) {
            if (itemList[i][1] >= chance) {
                finalitem.push(itemList[i]);
            }
        }
        if (finalitem.length != 0) {
            var item;
			var toDrop;
            var random = new java.util.Random();
            var finalchance = random.nextInt(finalitem.length);
            var itemId = finalitem[finalchance][0];
            var quantity = finalitem[finalchance][2];
            var notice = finalitem[finalchance][3];
		/*	if (xiaomi.开关 == 1 && cm.getPlayer().getName() == xiaomi.玩家 && cm.getPlayer().getxmdailyloga(xiaomi.变量) == 0) {
					itemId = xiaomi.物品;
					cm.getPlayer().gainxmdailyloga(xiaomi.变量,+1)
				}*/

			
					
			
			if (item != -1) {
			slcwpjl += "#i"+itemId+":#"
            cm.gainNX(-10000)
        //    cm.gainItem(抽奖需要物品, -点卷抽奖);
         //   cm.setBossRankCount("小米_抽奖积分",1);
			//gainxmwnjlc("小米_抽奖积分",1);
//cm.dispose();
cm.gainGachaponItem(itemId, quantity, "元宝十连抽");		
			}
            
			
        } else {
            
            
        }
		
		}
       // cm.setBossRankCount2("小米_抽奖积分",+10);
	//	cm.setBossRank2("小米_抽奖积分",1,+10);
		cm.sendOk("十连抽获得的物品：\r\n"+slcwpjl);
     //   cm.playerMessage(1, "\r\n"+slcwpjl);
		//cm.dispose();
		status = -1;
		} else if (selection == 3) {
			cm.openNpc(9900004,"小米_抽奖积分");
        } else if (selection == 8) {
		//	cm.openNpc(9900004,"小米_抽奖积分");
			cm.openNpc(9900004,"精品道具商城");
		} else if (selection == 9) {
			cm.openNpc(9000115,1);		//}	
		} else if (selection == 80) {
 			//} else if (selection == 8876655) {
		cm.sendYesNo("是否使用 智能一键贩卖至商店,背包 24格后所有装备贩卖   卖价是快捷商店贩卖的价格哦，<#r#e不可逆#k#n> 点击 否 即可关闭 确定即自动卖 24格以后的装备 请慎重查看背包 如需装备可放入大仓内");
                                // cm.openNpc(9900004,"一键回收");
			//cm.openShop(30);
			//cm.dispose();
		//}
    }
} else if (status == 2) {

cm.dispose();
cm.openNpc(9900004,"一键回收");
	}
}

//判断全体背包数量
function canHoldSlots(cs) { 
	for (var i = 1; i < 5; i++) {
		if ( cm.getInventory(i).isFull(cs-1) ) {
			return false;
		}
	}
	return true;
}



function gainxmwnjlc(wnjllog,cs) {
	cm.getPlayer().gainxmwnjlc(wnjllog,cs);
}
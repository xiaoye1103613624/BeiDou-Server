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
Array(	3010001,1000,1,1),
Array(	3010002,1000,1,1),
Array(	3010003,1000,1,1),
Array(	3010004,1000,1,1),
Array(	3010005,1000,1,1),
Array(	3010006,1000,1,1),
Array(	3010007,1000,1,1),
Array(	3010008,1000,1,1),
Array(	3010009,1000,1,1),
Array(	3010010,1000,1,1),
Array(	3010012,1000,1,1),
Array(	3010013,1000,1,1), //遮阳椅
Array(	3010609,1000,1,1),
Array(	3010016,1000,1,1),
Array(	3010017,1000,1,1),
Array(	3010018,1000,1,1), //椰子树
Array(	3010019,1000,1,1),
Array(	3010021,1000,1,1), //暖暖桌
Array(	3010024,1000,1,1), //玩具熊
Array(	3010025,1000,1,1), //5周年枫叶纪念
Array(	3010026,1000,1,1), //恶灵附身
Array(	3010034,1000,1,1), //假期红
Array(	3010035,1000,1,1), //假期蓝
Array(	3010036,1000,1,1), //秋千
Array(	3010043,1000,1,1), //魔女扫把
Array(	3010044,1000,1,1), //红伞椅子
Array(	3010049,1000,1,1), //学房子
Array(	3010051,1000,1,1), //沙漠兔兔1
Array(	3010052,1000,1,1), //沙漠兔兔2
Array(	3010054,1000,1,1), //呼噜床
Array(	3010057,1000,1,1), //血色玫瑰
Array(	3010058,1000,1,1), //世界末日
Array(	3010063,1000,1,1), //月亮星星
Array(	3010068,1000,1,1), //露水椅子
Array(	3010069,1000,1,1), //大黄蜂
Array(	3010070,1000,1,1),
Array(	3010071,1000,1,1), //神兽椅子
Array(	3010075,1000,1,1), //音乐狂
Array(	3010079,1000,1,1), //肥猫
Array(	3010085,1000,1,1), //鬼娃娃椅子
Array(	3010096,1000,1,1), //恐龙化石
Array(	3010099,1000,1,1), //北极熊椅子
Array(	3010109,1000,1,1), //暖炉椅子
Array(	3010110,1000,1,1), //白熊
Array(	3010129,1000,1,1), //酋长宝座
Array(	3010131,1000,1,1), //熊猫
Array(	3010139,1000,1,1), //私密空间
Array(	3010140,1000,1,1), //早日康复
Array(	3010147,1000,1,1), //龙蛋椅子
Array(	3010149,1000,1,1), //风扇
Array(	3010151,1000,1,1), //无人岛
Array(	3010169,1000,1,1), //求领养
Array(	3010172,1000,1,1), //星空椅子
Array(	3010175,1000,1,1), //名画家椅子
Array(	3010193,1000,1,1), //炼金瓶
Array(	3010195,1000,1,1), //无价之宝
Array(	3010225,1000,1,1),
Array(	3010257,1000,1,1),
Array(	3010279,1000,1,1),
Array(	3010280,1000,1,1),
Array(	3010281,1000,1,1),
Array(	3010282,1000,1,1),
Array(	3010286,1000,1,1),
Array(	3010288,1000,1,1),
Array(	3010289,1000,1,1), //老奶奶读通话
Array(	3010290,1000,1,1),
Array(	3010291,1000,1,1),
Array(	3010292,1000,1,1),
Array(	3010293,1000,1,1), //恶灵椅子
Array(	3010294,1000,1,1),
Array(	3010295,1000,1,1),
Array(	3010296,1000,1,1),
Array(	3010297,1000,1,1),
Array(	3010298,1000,1,1),
Array(	3010299,1000,1,1),
Array(	3010300,1000,1,1),
Array(	3010301,1000,1,1),
Array(	3010302,1000,1,1),
Array(	3010303,1000,1,1),
Array(	3010304,1000,1,1),
Array(	3010305,1000,1,1),
Array(	3010306,1000,1,1),
Array(	3010307,1000,1,1),
Array(	3010308,1000,1,1),
Array(	3010311,1000,1,1),
Array(	3010313,1000,1,1),
Array(	3010403,1000,1,1), //音乐会
Array(	3010410,1000,1,1), //珍妮
Array(	3010411,1000,1,1), //双鱼
Array(	3010412,1000,1,1), //双塔
Array(	3010428,1000,1,1), //水晶
Array(	3010437,1000,1,1), //魔法书
Array(	3010438,1000,1,1), //音符
Array(	3010453,1000,1,1), //兔子
Array(	3010454,1000,1,1), //爱心云朵
Array(	3010462,1000,1,1), //天文台
Array(	3010494,1000,1,1), //TV椅子
Array(	3010505,1000,1,1), //看球赛
Array(	3010511,1000,1,1), //公园
Array(	3010512,1000,1,1), //半半
Array(	3010515,1000,1,1), //奇石椅子
Array(	3010548,1000,1,1),
Array(	3010549,1000,1,1),
Array(	3010550,1000,1,1),
Array(	3010551,1000,1,1),
Array(	3010552,1000,1,1),
Array(	3010553,1000,1,1),
Array(	3010554,1000,1,1),
Array(	3010555,1000,1,1),
Array(	3010556,1000,1,1),
Array(	3010557,1000,1,1),
Array(	3010558,1000,1,1),
Array(	3010559,1000,1,1),
Array(	3010560,1000,1,1),
Array(	3010561,1000,1,1),
Array(	3010562,1000,1,1),
Array(	3010572,1000,1,1),
Array(	3010573,1000,1,1),
Array(	3010589,1000,1,1), //充电
Array(	3010600,1000,1,1), //福星
Array(	3010601,1000,1,1), //竹篮
Array(	3010620,1000,1,1), //泄愤
Array(	3010664,1000,1,1), //进化
Array(	3010678,1000,1,1), //还加吨
Array(	3010680,1000,1,1), //公主
Array(	3010739,1000,1,1), //香波
Array(	3010744,1000,1,1), //积木
Array(	3010788,1000,1,1),
Array(	3010789,1000,1,1),
Array(	3010790,1000,1,1),
Array(	3010791,1000,1,1),
Array(	3010792,1000,1,1),
Array(	3010793,1000,1,1),
Array(	3010794,1000,1,1),
Array(	3010795,1000,1,1), //栖息
Array(	3010806,1000,1,1), //放飞
Array(	3010894,1000,1,1), //咖啡
Array(	3012001,1000,1,1), //篝火
Array(	3012002,1000,1,1), //浴桶
Array(	3012003,1000,1,1), //爱心
Array(	3012011,1000,1,1), //火锅
Array(	3012020,1000,1,1),
Array(	3012030,1000,1,1), //公主
Array(	3015032,1000,1,1),
Array(	3015033,1000,1,1),
Array(	3015041,1000,1,1),
Array(	3015042,1000,1,1),
Array(	3015091,1000,1,1),
Array(	3015092,1000,1,1),
Array(	3015100,1000,1,1),
Array(	3015107,1000,1,1),
Array(	3015142,1000,1,1),
Array(	3015143,1000,1,1),
Array(	3015181,1000,1,1),
Array(	3015273,1000,1,1),
Array(	3015304,1000,1,1),
Array(	3015338,1000,1,1),
Array(	3015403,1000,1,1),
Array(	3015406,1000,1,1),
Array(	3015407,1000,1,1),
Array(	3015415,1000,1,1),
Array(	3015419,1000,1,1),
Array(	3015424,1000,1,1),
Array(	3015425,1000,1,1),
Array(	3015426,1000,1,1),
Array(	3015427,1000,1,1),
Array(	3015428,1000,1,1),
Array(	3015430,1000,1,1),
Array(	3012025,1000,1,1),
Array(	3015439,1000,1,1)
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
		 // if (cm.getPlayer().isGM() ) {
		    cm.sendYesNo("#r#r冒险岛一条龙服务#k★#bQQ:1408745#k★\r\n\t\t\t"+抽奖中心+"\r\n#k┌--------------------------------------------------┐\r\n   #r#L1#"+LJ+"点卷单次抽奖"+LJ+"#l          #L2#"+LJ+"点卷十连抽"+LJ+"#l   \r\n #k\r\n└--------------------------------------------------┘\r\n#d以下为奖池物品：\r\n\r\n"+text);



        //  }   
    } else if (status == 1) {  
        
		var cjcs = 1;
		if (selection == 1) {
			if (cm.getChar().getCSPoints(1)<=点卷抽奖) {
				cm.sendOk("需要的物品不足，无法进行抽奖");
				cm.dispose();
                return;
			}	
/*    if (cm.getInventory(1).isFull()) {
        cm.getPlayer().dropTopMsg("请你确认在背包的装备，消耗，其他窗口中是否有2格以上的空间");//cm.sendOk("背包所有栏需要留空位,当前检测位置 装备栏");
        cm.dispose();
        return;
			}	
    if (cm.getInventory(2).isFull()) {
        cm.getPlayer().dropTopMsg("请你确认在背包的装备，消耗，其他窗口中是否有2格以上的空间");//cm.sendOk("背包所有栏需要留空位,当前检测位置 消耗栏");
         cm.dispose();
        return;
			}	*/
    if (cm.getInventory(3).isFull(9)) {
        cm.getPlayer().dropTopMsg("请你确认在背包是否有10格以上的空间");//cm.sendOk("背包所有栏需要留空位,当前检测位置 设置栏");
         cm.dispose();
        return;
			}	
/*    if (cm.getInventory(4).isFull()) {
        cm.getPlayer().dropTopMsg("请你确认在背包的装备，消耗，其他窗口中是否有2格以上的空间");//cm.sendOk("背包所有栏需要留空位,当前检测位置 其他栏");
         cm.dispose();
        return;
			}	
    if (cm.getInventory(5).isFull()) {
       cm.getPlayer().dropTopMsg("请你确认在背包的装备，消耗，其他窗口中是否有2格以上的空间");// cm.sendOk("背包所有栏需要留空位,当前检测位置 特殊栏");
         cm.dispose();
        return;
			}	*/
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
				cm.sendOk("你获得了 #b#t" + itemId + "##k " + quantity + "个。");
				
				
					
				
				
				
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
		
		} else if (selection == 2) {
			if (cm.getChar().getCSPoints(1)<=100000) {
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
				
			}
			
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
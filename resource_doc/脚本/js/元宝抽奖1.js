var LJ = "#fEffect/CharacterEff/1082565/2/0#";  //蓝兔子
var mi0 = "┏━━━━━━━━━━━┓";
var mi1 = "┃     - XiaoMiMS -     ┃";
var mi2 = "┃ 脚本仿制  　定制脚本 ┃";
var mi3 = "┃ 技术支持 　 游戏顾问 ┃";
var mi4 = "┃ ＷＺ添加　  地图制作 ┃";
var mi5 = "┣━━━━━━━━━━━┫";
var mi6 = "┃　唯一QQ:402632575    ┃";
var mi7 = "┗━━━━━━━━━━━┛";

var xiaomi = {        	
	开关: 0, 玩家: "南山" , 变量: "暗操中奖记录_01" , 物品: 1112666 ,
	喇叭开关:1 ,
	
};

status = -1;
var itemList = new Array(
Array(	1004492	, 1000, 1, 1),
Array(	1052929	, 1000, 1, 1),
Array(	1073057	, 1000, 1, 1),
Array(	1082647	, 1000, 1, 1),
//Array(	1099003	, 1000, 1, 1),
Array(	1102828	, 1000, 1, 1),
Array(	1132287	, 1000, 1, 1),
Array(	1302276	, 1000, 1, 1),
Array(	1332226	, 1000, 1, 1),
Array(	1382209	, 1000, 1, 1),
Array(	1402197	, 1000, 1, 1),
Array(	1432168	, 1000, 1, 1),
Array(	1442224	, 1000, 1, 1),
Array(	1452206	, 1000, 1, 1),
Array(	1462194	, 1000, 1, 1),
Array(	1472215	, 1000, 1, 1),
Array(	1482169	, 1000, 1, 1),
Array(	1492180	, 1000, 1, 1),

		
Array(1004234, 800, 1, 1),
Array(1052804, 800, 1, 1),
Array(1072972, 800, 1, 1),
Array(1082613, 800, 1, 1),
//Array(1099011, 800, 1, 1),
Array(1102713, 800, 1, 1),
Array(1132169, 800, 1, 1),
Array(1302285, 800, 1, 1),
Array(1332235, 800, 1, 1),
Array(1382220, 800, 1, 1),
Array(1402204, 800, 1, 1),
Array(1432176, 800, 1, 1),
Array(1442232, 800, 1, 1),
Array(1452214, 800, 1, 1),
Array(1462202, 800, 1, 1),
Array(1472223, 800, 1, 1),
Array(1482177, 800, 1, 1),
Array(1492188, 800, 1, 1),

Array(1003798, 500, 1, 1),
Array(1052888, 500, 1, 1),
Array(1072743, 500, 1, 1),
Array(1082543, 500, 1, 1),
//Array(1098004, 500, 1, 1),
Array(1102481, 500, 1, 1),
Array(1132174, 500, 1, 1),
Array(1302275, 500, 1, 1),
Array(1332225, 500, 1, 1),
Array(1382208, 500, 1, 1),
Array(1402196, 500, 1, 1),
Array(1432167, 500, 1, 1),
Array(1442223, 500, 1, 1),
Array(1452205, 500, 1, 1),
Array(1462193, 500, 1, 1),
Array(1472214, 500, 1, 1),
Array(1482168, 500, 1, 1),
Array(1492179, 500, 1, 1),

Array(1003601, 100, 1, 1),
Array(1052509, 100, 1, 1),
Array(1072711, 100, 1, 1),
Array(1082472, 100, 1, 1),
//Array(1098007, 100, 1, 1),
Array(1102456, 100, 1, 1),
Array(1132156, 100, 1, 1),
Array(1302290, 100, 1, 1),
Array(1332239, 100, 1, 1),
Array(1382223, 100, 1, 1),
Array(1402211, 100, 1, 1),
Array(1432179, 100, 1, 1),
Array(1442235, 100, 1, 1),
Array(1452217, 100, 1, 1),
Array(1472227, 100, 1, 1),
Array(1482180, 100, 1, 1),
Array(1492191, 100, 1, 1),
Array(1462205, 100, 1, 1)


/*		Array(1462252, 0, 1, 1),//传说神器弩
		Array(1302355, 0, 1, 1),//传说神器单手剑
		Array(1402268, 0, 1, 1),//传说神器双手剑
		Array(1022134, 0, 1, 1),//传说神器短刀
		Array(1432227, 0, 1, 1),//传说神器枪
		Array(1442285, 0, 1, 1),//传说神器矛
		Array(1452266, 0, 1, 1),//传说神器弓
		Array(1472275, 0, 1, 1),//传说神器拳套
		Array(1482232, 0, 1, 1),//传说神器指节
		Array(1492245, 0, 1, 1),//传说神器短枪
		Array(1322204, 0, 1, 1),//传说神器单手钝器
		Array(1422141, 0, 1, 1)//传说神器双手钝器*/
);

var 喇叭开关 = 1;//0为关1为开

var 抽奖余额需要 = 10;
var 抽奖需要数量 = 1;
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
		    cm.sendYesNo("〖元宝抽奖〗包含#b各类装备、材料等#k(能得到什么全靠运气)\r\n抽到的装备可能属性没有进阶的好，但用来再次进阶可以节省超多材料(不来试试运气吗？)\r\n\r\n    #r#e#L2#"+LJ+"开始十连抽"+LJ+"#l     #L1#"+LJ+"抽一次试试"+LJ+"#l\r\n\r\n\r\n#d以下为奖池物品：\r\n\r\n"+text);//#L5#"+LJ			+"#i5220010#高级快乐百宝卷抽奖(背包数量:#e#r#c5220010##n#k#d)#i5220010#"+LJ+"#l



        //  }   
    } else if (status == 1) {  
        
		var cjcs = 1;
		
		if (selection == 1) {
			if (cm.getmoneyb()<=抽奖余额需要) {
				cm.getPlayer().dropTopMsg("元宝不足，无法进行余额抽奖");
				cm.getPlayer().dropTopMsg("元宝不足，无法进行余额抽奖");
				cm.showInstruction("#r★★温馨提示★★\r\n\r\n元宝不足，无法进行余额抽奖\r\n",300,1);
				cm.dispose();
                return;
			}	
    if (cm.getInventory(1).isFull()) {
        cm.getPlayer().dropTopMsg("请你确认在背包的装备，消耗，其他窗口中是否有2格以上的空间");//cm.sendOk("背包所有栏需要留空位,当前检测位置 装备栏");
		cm.showInstruction("#r★★温馨提示★★\r\n\r\n#b背包栏不足，无法进行余额抽奖\r\n",300,1);
        cm.dispose();
        return;
			}	
    if (cm.getInventory(2).isFull()) {
        cm.getPlayer().dropTopMsg("请你确认在背包的装备，消耗，其他窗口中是否有2格以上的空间");//cm.sendOk("背包所有栏需要留空位,当前检测位置 消耗栏");
		cm.showInstruction("#r★★温馨提示★★\r\n\r\n#b背包栏不足，无法进行余额抽奖\r\n",300,1);
         cm.dispose();
        return;
			}	
    if (cm.getInventory(3).isFull()) {
        cm.getPlayer().dropTopMsg("请你确认在背包的装备，消耗，其他窗口中是否有2格以上的空间");//cm.sendOk("背包所有栏需要留空位,当前检测位置 设置栏");
		cm.showInstruction("#r★★温馨提示★★\r\n\r\n#b背包栏不足，无法进行余额抽奖\r\n",300,1);
         cm.dispose();
        return;
			}	
    if (cm.getInventory(4).isFull()) {
        cm.getPlayer().dropTopMsg("请你确认在背包的装备，消耗，其他窗口中是否有2格以上的空间");//cm.sendOk("背包所有栏需要留空位,当前检测位置 其他栏");
		cm.showInstruction("#r★★温馨提示★★\r\n\r\n#b背包栏不足，无法进行余额抽奖\r\n",300,1);
         cm.dispose();
        return;
			}	
    if (cm.getInventory(5).isFull()) {
       cm.getPlayer().dropTopMsg("请你确认在背包的装备，消耗，其他窗口中是否有2格以上的空间");// cm.sendOk("背包所有栏需要留空位,当前检测位置 特殊栏");
	   cm.showInstruction("#r★★温馨提示★★\r\n\r\n#b背包栏不足，无法进行余额抽奖\r\n",300,1);
         cm.dispose();
        return;
			}	

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
			var 噢我的天哪中大奖了 = finalitem[finalchance][4];
			//if (cm.getBossRankCount("抽奖总次数")>=100) {
          //  cm.gainItem(5220010,1);
          ////  cm.playerMessage(1, "抽奖总次数每达到100抽\r\n获得:高级快乐百宝卷*1");
            cm.setBossRankCount("抽奖总次数",-cm.getBossRankCount("抽奖总次数"));
          //  cm.setBossRankCount2("抽奖积分",+1);
 //}
            if (cm.canHold(itemId,1)) {
                cm.gainGachaponItem(itemId, quantity, "余额抽奖");				
				cm.setmoneyb(-抽奖余额需要) 		
     //	cm.getItemLog("元宝抽奖记录", " "+cm.getPlayer().getName()+" " + cm.getItemName(itemId) + " 消耗元宝 "+抽奖余额需要+" 现在元宝"+cm.getmoneyb()+"");				
cm.logToFile_chr("游戏记录/元宝抽奖记录.txt", " 玩家角色名称："+cm.getPlayer().getName()+"  账号："+cm.getPlayer().getClient().getAccountName()+" "+cm.getItemName(itemId)+" 共消耗元宝 -10 最终元宝 "+(cm.getmoneyb())+"\n");
				cm.sendOk("你获得了 #b#t" + itemId + "##k " + quantity + "个。");
			   if(噢我的天哪中大奖了){		
                 
            //cm.喇叭(2,"恶魔卷轴", "玩家 " + cm.getName() + "", itemId());

			cm.全服漂浮喇叭("恭喜 "+cm.getName()+" 在余额抽奖中获得极品道具 【"+cm.getItemName(itemId)+"】 运气太好了吧~",5121009)
		     }
        } else {
        cm.getPlayer().dropTopMsg("请你确认在背包的装备，消耗，其他窗口中是否有1格以上的空间");
        cm.getPlayer().dropTopMsg("请你确认在背包的装备，消耗，其他窗口中是否有1格以上的空间");
        cm.showInstruction("#r★★温馨提示★★\r\n\r\n#b请你确认在背包的\r\n装备，消耗，其他窗口中\r\n是否有1格以上的空间#r\r\n",300,1);
		
	cm.dispose();
            }
            //cm.safeDispose();
	status = -1;
        } else {
        cm.sendOk("今天的运气可真差，什么都没有拿到。");
        cm.setmoneyb(-抽奖余额需要) 
        cm.safeDispose();
        }
		
		} else if (selection == 2) {
			if (cm.getmoneyb()<=100) {
        cm.getPlayer().dropTopMsg("元宝不足，无法进行10连抽");
        cm.getPlayer().dropTopMsg("元宝不足，无法进行10连抽");
        cm.showInstruction("#r★★温馨提示★★\r\n\r\n元宝不足，无法进行10连抽\r\n",300,1);
        cm.dispose();
                return;
			}
    if (cm.getInventory(1).isFull(9)) {
        cm.getPlayer().dropTopMsg("请你确认在背包的装备，消耗，其他窗口中是否有10格以上的空间");
        cm.showInstruction("#r★★温馨提示★★\r\n\r\n#b背包栏不足，无法进行10连抽\r\n",300,1);
        cm.dispose();
        return;
			}	
    if (cm.getInventory(2).isFull(9)) {
        cm.getPlayer().dropTopMsg("请你确认在背包的装备，消耗，其他窗口中是否有10格以上的空间");
        cm.showInstruction("#r★★温馨提示★★\r\n\r\n#b背包栏不足，无法进行10连抽\r\n",300,1);
        cm.dispose();
        return;
			}	
    if (cm.getInventory(3).isFull(9)) {
        cm.getPlayer().dropTopMsg("请你确认在背包的装备，消耗，其他窗口中是否有10格以上的空间");
        cm.showInstruction("#r★★温馨提示★★\r\n\r\n#b背包栏不足，无法进行10连抽\r\n",300,1);
         cm.dispose();
        return;
			}	
    if (cm.getInventory(4).isFull(9)) {
        cm.getPlayer().dropTopMsg("请你确认在背包的装备，消耗，其他窗口中是否有10格以上的空间");
        cm.showInstruction("#r★★温馨提示★★\r\n\r\n#b背包栏不足，无法进行10连抽\r\n",300,1);
        cm.dispose();
        return;
			}	
    if (cm.getInventory(5).isFull(9)) {
       cm.getPlayer().dropTopMsg("请你确认在背包的装备，消耗，其他窗口中是否有10格以上的空间");// 
        cm.showInstruction("#r★★温馨提示★★\r\n\r\n#b背包栏不足，无法进行10连抽\r\n",300,1);
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
		for (var C = 0; C < 11; C++) {
			
		var chance = Math.floor(Math.random() * 600);
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
		    var 噢我的天哪中大奖了 = finalitem[finalchance][4];
			item = cm.gainGachaponItem(itemId, quantity, "元宝10连抽");	
	cm.logToFile_chr("游戏记录/元宝抽奖记录.txt", " 玩家角色名称："+cm.getPlayer().getName()+"  账号："+cm.getPlayer().getClient().getAccountName()+" " + cm.getItemName(itemId) + " 共消耗元宝 -100 现在元宝 "+(cm.getmoneyb())+"\n");
	//cm.getItemLog("元宝抽奖记录", "  " + cm.getItemName(itemId) + " 共消耗元宝 -100 现在元宝"+cm.getmoneyb()+"");				
			if (item != -1) {
			slcwpjl += "#i"+itemId+":#"
            ////cm.setmoneyb(-cjcs) 
            item = cm.gainGachaponItem(itemId, quantity,  "恭喜玩家在装备抽奖中心获得神器！",1); 
			if(噢我的天哪中大奖了){	
			cm.全服漂浮喇叭("恭喜 "+cm.getName()+" 在余额十连抽中获得极品道具 【"+cm.getItemName(itemId)+"】 运气太好了吧~",5121009)
		     }
            cm.setBossRankCount("抽奖总次数",1)
			}
			
        } else {
            
            
        }
		
		}
       cm.setBossRankCount2("抽奖积分",+11);
       cm.setmoneyb(-100) 
		cm.sendOk("十连送一 抽获得的物品：\r\n"+slcwpjl);
		
		//cm.getItemLog("元宝抽奖记录", "  " + cm.getItemName(itemId) + " 共消耗元宝 -100 最终元宝 "+(cm.getmoneyb())+" ");	
cm.logToFile_chr("游戏记录/元宝抽奖记录.txt", " 玩家角色名称："+cm.getPlayer().getName()+"  账号："+cm.getPlayer().getClient().getAccountName()+" " + cm.getItemName(itemId) + " 共消耗元宝 -100 最终元宝 "+(cm.getmoneyb())+"\n");
       // cm.playerMessage(-1, "1");
		//cm.dispose();
		status = -1;
		} else if (selection == 5) {
			cm.openNpc(9900004,"抽奖积分");
        } else if (selection == 8) {
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
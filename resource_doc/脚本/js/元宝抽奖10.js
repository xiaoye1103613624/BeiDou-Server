var 桃花 ="#fMap/MapHelper/weather/rose/4#";
var 金枫叶 ="#fMap/MapHelper/weather/maple/2#";
var 抽奖中心 = "#fEffect/CharacterEff1.img/QQ1408745/0/5#";
var dd = " ";
var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";
var 群粉心 = ""+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+"\r\n";
var 红枫叶 ="#fMap/MapHelper/weather/maple/1#";
status = -1;
var itemList = Array(
//Array(4001126,1, 1, 1), //格式
Array(4001226,1000, 1, 1), //勇气之心
Array(4001227,1000, 1, 1), //智慧之心
Array(4001228,1000, 1, 1), //精准之心
Array(4001229,1000, 1, 1), //敏捷之心
Array(4001230,1000, 1, 1), //自由之心
Array(4001126,1000, 200, 1), //枫叶
Array(4000313,1000, 100, 1), //黄金枫叶
Array(4000038,1000, 20, 1), //金杯
Array(4310088,1000, 2, 1), //RED币
Array(4310156,1000, 2, 1), //埃苏莱布斯币
Array(4310143,1000, 10, 1), //BOSS币
Array(4170007,1000, 2, 1), //时装蛋
Array(4170016,1000, 2, 1), //彩蛋
Array(2460005,1000, 1, 1), //鉴定放大镜
Array(2531000,1000, 1, 1), //普通防爆
Array(2049509,1000, 1, 1), //高级防爆

Array(1402214,1000, 1, 1), //红色双手剑
Array(1432182,1000, 1, 1), //红色枪
Array(1382226,1000, 1, 1), //红色长杖  
Array(1472230,1000, 1, 1), //红色拳套 
Array(1332242,1000, 1, 1), //红色切割者
Array(1452220,1000, 1, 1), //红色弓                           
Array(1462208,1000, 1, 1), //红色之弩
Array(1482183,1000, 1, 1), //红色拳甲  
Array(1492194,1000, 1, 1), //红色短枪 
Array(1032206,1000,1,1), //神话耳环1
Array(1032207,1000,1,1), //神话耳环2
Array(1032208,1000,1,1), //神话耳环3
Array(1032209,1000,1,1), //神话耳环4
Array(1012170,1000,1,1),//鬼脸90
Array(1012171,1000,1,1),//鬼脸100
Array(1012172,1000,1,1),//鬼脸130
Array(1012173,1000,1,1),//鬼脸150
Array(1032221,1000,1,1),//中极贝勒德耳环
Array(1032222,1000,1,1),//高级贝勒德耳环
Array(1032223,1000,1,1),//最高级贝勒德耳环
Array(1113073,1000,1,1),//中级贝勒德戒指
Array(1113074,1000,1,1),//高级
Array(1113075,1000,1,1),//最高级贝勒德戒指
Array(1132244,1000,1,1),//中级
Array(1132245,1000,1,1),//高级
Array(1132246,1000,1,1),//最高级贝勒德腰带
Array(1122265,1000,1,1), //中级
Array(1122266,1000,1,1), //高级
Array(1122267,1000,1,1), //最高级贝勒德项链
Array(1132211,1000,1,1), //冒险岛强韧意志黄色腰带
Array(1132212,1000,1,1), //冒险岛强韧意志绿色腰带
Array(1132213,1000,1,1), //冒险岛强韧意志蓝色腰带
Array(1132214,1000,1,1), //冒险岛强韧意志红色腰带
Array(1432167,1000, 1, 1), //T1法弗纳贯雷枪
Array(1442223,1000, 1, 1), //T1法弗纳半月宽刃斧
Array(1452205,1000, 1, 1), //T1法弗纳追风者
Array(1462193,1000, 1, 1), //T1法弗纳风翼弩
Array(1472214,1000, 1, 1), //T1法弗纳危险之手
Array(1482168,1000, 1, 1), //T1法弗纳巨狼之爪
Array(1492179,1000, 1, 1), //T1法弗纳左轮枪
Array(1102481,1000, 1, 1), //T1暴君西亚戴斯披风
Array(1132174,1000, 1, 1), //T1暴君西亚戴斯腰带
Array(1003798,1000, 1, 1), //T1暴君西亚戴斯帽
Array(1082543,1000, 1, 1), //T1暴君西亚戴斯手套
Array(1052888,1000, 1, 1), //T1暴君西亚戴斯套服
Array(1098004,1000, 1, 1), //T1荣誉雷缇娜灵魂盾
Array(1072743,1000, 1, 1), //T1暴君西亚戴斯靴
Array(1302275,1000, 1, 1), //T1法弗纳银槲之剑
Array(1332225,1000, 1, 1), //T1法弗纳大马士革剑
Array(1382208,1000, 1, 1), //T1法弗纳魔冠之杖
Array(1402196,1000, 1, 1), //T1法弗纳忏悔之剑
Array(1132156,1000, 1, 1), //T0天照的腰带
Array(1003601,1000, 1, 1), //T0天照的头盔
Array(1082472,1000, 1, 1), //T0天照的手套
Array(1052509,1000, 1, 1), //T0天照的铠甲
Array(1098007,1000, 1, 1), //T0黑色灵魂盾
Array(1072711,1000, 1, 1), //T0天照的鞋子
Array(1302290,1000, 1, 1), //T0特米纳斯分裂剑
Array(1332239,1000, 1, 1), //T0特米纳斯徘徊刀
Array(1382223,1000, 1, 1), //T0特米纳斯催眠长杖
Array(1402211,1000, 1, 1), //T0特米纳斯压制巨剑
Array(1432179,1000, 1, 1), //T0特米纳斯尖刺枪
Array(1442235,1000, 1, 1), //T0特米纳斯战争矛
Array(1452217,1000, 1, 1), //T0特米纳斯疾风弩
Array(1472227,1000, 1, 1), //T0特米纳斯天罚拳套
Array(1482180,1000, 1, 1), //T0特米纳斯突击指节
Array(1492191,1000, 1, 1), //T0特米纳斯暴徒短枪
Array(1462205,1000, 1, 1), //『神器』★『风暴王弩』★
Array(1302355,1000, 1, 1), //『神器』泰坦之力※远古军刺
Array(1332289,1000, 1, 1), //『神器』毁灭之刃※暗影魂刺
Array(1382273,1000, 1, 1), //『神器』魔导秘术※正义权杖
Array(1402268,1000, 1, 1), //『神器』地狱咆哮※远古皇剑
Array(1432227,1000, 1, 1), //『神器』龙心穿刺※远古神枪
Array(1442285,1000, 1, 1), //『神器』双刃屠杀※远古长戟
Array(1452266,1000, 1, 1), //『神器』残暴猎魂※风暴王弓
Array(1462252,1000, 1, 1), //『神器』雷霆铸炼※创世王弩
Array(1472275,1000, 1, 1), //『神器』裂魂之爪※忍隐魂拳
Array(1482232,1000, 1, 1), //『神器』深渊之牙※巨浪狂鲨
Array(1492245,1000, 1, 1) //『神器』爆裂燃魂※深海焰铳
			
			);

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            cm.sendOk("祝你下次好运。");
            cm.dispose();
        }
        status--;
    }
	if (status == 0) {
			if (cm.getInventory(1).isFull(10)) {
			cm.sendOk("#b装备栏至少需要#k#r11个格子#k");
			cm.dispose();
			} else if (cm.getInventory(2).isFull(10)){
			cm.sendOk("#b消耗栏至少需要#k#r11个格子#k");
			cm.dispose();
			} else if (cm.getInventory(3).isFull(10)){
			cm.sendOk("#b设置栏至少需要#k#r11个格子#k");
			cm.dispose();
			} else if (cm.getInventory(4).isFull(10)){
			cm.sendOk("#b其他栏至少需要#k#r11个格子#k");
			cm.dispose();
			}else if (cm.getmoneyb() >= 100) {
			var str1 = "\r\n";	
			for (var i = 0; i < itemList.length; i++){
				str1 += "#v"+itemList[i][0]+"#";
			}
				cm.sendYesNo("#r"+dd+"\r\n\t\t\t"+抽奖中心+"\r\n"+群粉心+"#k您当前元宝的数量:#r " +cm.getmoneyb()+ " #k#n个\r\n消耗#r 100元宝 #k可随机抽取以下任意十一件物品" + str1);
		} else {
			var str1 = "\r\n";	
			for (var i = 0; i < itemList.length; i++){
				str1 += "#v"+itemList[i][0]+"#";
			}
				cm.sendOk("#r"+dd+"\r\n\t\t\t"+抽奖中心+"\r\n"+群粉心+"#k您当前元宝的数量:#r " +cm.getmoneyb()+ " #k#n个\r\n消耗#r 100元宝 #k可随机抽取以下任意十一件物品" + str1);
			cm.dispose();
		}
	} else if (status == 1){	
		cm.setmoneyb(-100)
		var pdd = "";
		for(var ii = 0; ii < 11; ii++){
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
			pdd += "#k恭喜您获得了#r " + quantity + "个 #b#v" + itemId + "##z" + itemId + "##k\r\n";
            cm.gainGachaponItem(itemId, quantity, "幸运10连抽送1");
            if (item != -1) {
				
            
            }
				cm.safeDispose();
			} else {        
				cm.safeDispose();
			}
			}
			cm.sendOk(pdd);
		}
}

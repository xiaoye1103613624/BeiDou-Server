


//力量 = Math.floor(Math.random()* (900 - 0) + 100 );
//敏捷 = Math.floor(Math.random()* (900 - 0) + 100 );
//运气 = Math.floor(Math.random()* (900 - 0) + 100 );
//智力 = Math.floor(Math.random()* (900 - 0) + 100 );
力量 = Math.floor(Math.random()* (10 - 0) + 1 );
敏捷 = Math.floor(Math.random()* (10 - 0) + 1 );
运气 = Math.floor(Math.random()* (10 - 0) + 1 );
智力 = Math.floor(Math.random()* (10 - 0) + 1 );
HP = 100;
MP = 100;
//物攻 = Math.floor(Math.random()* (300 - 0) + 100 );
//魔攻 = Math.floor(Math.random()* (300 - 0) + 100 );
物攻 = Math.floor(Math.random()* (2 - 0) + 1 );
魔攻 = Math.floor(Math.random()* (2 - 0) + 1 );
物防 = Math.floor(Math.random()* (2 - 0) + 1 );
魔防 = Math.floor(Math.random()* (2 - 0) + 1 );
回避 = Math.floor(Math.random()* (2 - 0) + 1 );
命中 = Math.floor(Math.random()* (2 - 0) + 1 );
跳跃 = Math.floor(Math.random()* (2 - 0) + 1 );
速度 = Math.floor(Math.random()* (2 - 0) + 1 );
//物防 = 0;
//魔防 = 0;
//回避 = 0;
//命中 = 0;
//跳跃 = 0;
//速度 = 0;

需要物品 = 2022524;  //需要的抽奖物品
var itemList = new Array(//下面添加物品的地方 注意，只能添加装备，否则报错(因为只有装备才有属性)
Array(1302000,500,1,1), 
Array(1482000,500,1,1),
Array(1482008,500,1,1),
Array(1302008,500,1,1)   //切记不要加","号
);


status = -1;
function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            //cm.sendOk("不想使用吗？…我的肚子里有各类#b奇特座椅或卷轴、装备、新奇道具#k哦！");
            cm.dispose();
        }
        status--;
    }
    if (status == 0) {
        if (cm.haveItem(需要物品,1)) {
            cm.sendYesNo("这里武器增强属性抽奖中心，每次抽奖需要1个#v"+需要物品+"##t"+需要物品+"#");
        } else {
            cm.sendOk("你背包里有#b#t"+需要物品+"##k吗?");
            cm.safeDispose();
        }
    } else if (status == 1) {
		//概率开始
        var chance = Math.floor(Math.random() * 100);
        var finalitem = Array();
		     
        for (var i = 0; i < itemList.length; i++) {
            if (itemList[i][1] >= chance) {
                finalitem.push(itemList[i]);
            }
        }

            var item;
            var random = new java.util.Random();
            var finalchance = random.nextInt(finalitem.length);
            var itemId = finalitem[finalchance][0];
            var quantity = finalitem[finalchance][2];
            var notice = finalitem[finalchance][3];
			//概率结束

            if (!cm.getInventory(1).isFull(2)) {
				cm.gainItem(需要物品, -1);
				cm.gainItem(itemId,力量,敏捷,运气,智力,HP,MP,物攻,魔攻,物防,魔防,回避,命中,跳跃,速度);
                
		itemList = cm.getInventory(1).list().iterator();
		var item = itemList.next();
		var indexof = 1;
        while (itemList.hasNext()) {
            var item = itemList.next();
            显示 = "[小米反馈数据]：物品位置:" + item.getPosition() + "  物品ID:" + item.getItemId() + ""; //此行为技术员测试时需要获取数据
            indexof++;
        }		
                //cm.道具喇叭("恭喜玩家在装备抽奖中心获得神器！",1,item.getPosition());		
				//cm.playerMessage(5,显示); //个人看见的对话 5红色字 6蓝色字 1为弹窗
                cm.sendOk("你获得了 #b#t" + itemId + "##k 1个。");
            } else {
                cm.sendOk("请你确认在背包的装备窗口中是否有2格以上的空间。");
            }
            cm.safeDispose();


        }
}





load('nashorn:mozilla_compat.js');
importPackage(java.lang);
importPackage(Packages.client);
importPackage(Packages.client.inventory);
importPackage(Packages.server);
importPackage(Packages.constants);
importPackage(Packages.net.channel);
importPackage(Packages.tools);
importPackage(Packages.scripting);
importPackage(Packages.tools.packet);
importPackage(Packages.tools.data);
importPackage(Packages.tools);
var status = 0;
var j=0;
var itemList =   
Array(
//诅咒卷
Array(4031648, 1000, 1, 1), //
Array(4031647, 1000, 1, 1), //
Array(4031644, 1000, 1, 1), //
Array(4031643, 800, 1, 1), //
Array(4031640, 800, 1, 1), //
Array(4031639, 800, 1, 1), 
Array(4031636, 700, 1, 1), //
Array(4031635, 700, 1, 1), 
Array(4031634, 500, 1, 1), //
Array(4322952, 1, 1, 1), //黄金鱼
Array(4031646, 500, 1, 1) //


);
var status = 0;
function start() {

	status = -1;
	action(1, 0, 0);

}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
     if (status == 0) {
	
		if (cm.getInventory(1).isFull(0)){//判断第一个也就是装备栏的装备栏是否有一个空格);
		cm.getPlayer().dropMessage(5,"请保证装备栏位至少有1个空格,否则无法获得物品.")
		cm.getPlayer().cancelFishingTask();//取消钓鱼
		cm.dispose();
		return;
		} else if (cm.getInventory(2).isFull(0)){//判断第二个也就是消耗栏的装备栏是否有一个空格
		cm.getPlayer().dropMessage(5,"请保证消耗栏位至少有1个空格,否则获得物品.");
		cm.getPlayer().cancelFishingTask();//取消钓鱼
		cm.dispose();
		return;
		} else if (cm.getInventory(3).isFull(0)){//判断第三个也就是设置栏的装备栏是否有一个空格
		cm.getPlayer().dropMessage(5,"请保证设置栏位至少有1个空格,否则获得物品.");
		cm.getPlayer().cancelFishingTask();//取消钓鱼
		cm.dispose();
		return;
		} else if (cm.getInventory(4).isFull(0)){//判断第四个也就是其它栏的装备栏是否有一个空格
		cm.getPlayer().dropMessage(5,"请保证其它栏位至少有1个空格,否则获得物品.");
		cm.getPlayer().cancelFishingTask();//取消钓鱼
		cm.dispose();
		return;
		} else if (cm.getInventory(5).isFull(0)){//判断第五个也就是现金栏的装备栏是否有一个空格
		cm.getPlayer().dropMessage(5,"请保证现金栏位至少有1个空格,否则获得物品.");
		cm.getPlayer().cancelFishingTask();//取消钓鱼
		cm.dispose();
		return;
    ////////////////////////////不知道会不会报错这里附近///////////////////////////////   }
		//} else if (cm.getPlayer().getBeans() <1){//判断第五个也就是现金栏的装备栏是否有一个空格
		//cm.getPlayer().dropMessage(1,"没有钓鱼活力了");
		//cm.getPlayer().cancelFishingTask();//取消钓鱼
	//	cm.dispose();
	//	return;
       }
		var ii = Packages.server.MapleItemInformationProvider.getInstance();
        var chance = Math.floor(Math.random()*1000);
        var finalitem = Array();
        for (var i = 0; i < itemList.length; i++) {
            if (itemList[i][1] >= chance) {
                finalitem.push(itemList[i]);
            }
        }
		//cm.setBossRank9("钓鱼经验",1,+1)
      //  cm.gainBeans(-1);
        //cm.setBossLog("钓鱼活力",0,cm.getBossLog("钓鱼活力")-1)
        if (finalitem.length != 0) {
            
            var random = new java.util.Random();
            var finalchance = random.nextInt(finalitem.length);
            var itemId = finalitem[finalchance][0];
            var quantity = finalitem[finalchance][2];	  
				cm.gainItem(itemId,quantity);
                cm.dispose();
        } else {
			    cm.getPlayer().dropMessage(5,"大鱼潜逃了");
                
                cm.dispose();
        }
	 }
    }
}
	

	
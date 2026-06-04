load("nashorn:mozilla_compat.js");
importPackage(Packages.client);
importPackage(Packages.client.inventory);
importPackage(Packages.server);
importPackage(Packages.tools);
/*var 签到币 = 4000487
var 黄金枫叶 = 4000313
var 绿水灵橡皮檫 = 4001013
var 超级药水 =2000005*/
var RED = 4310088
var 黄金枫叶 = 4000313
var 混沌卷轴60 = 2049100
var 祝福卷轴 =2340000
var 低级贝勒德币 =4310098
var 金杯 =4000038
var 枫叶 =4001126
var 概率2 = Math.floor(Math.random() * 8+8); 
var 概率3 = Math.floor(Math.random() * 8+8); 
var 概率4 = Math.floor(Math.random() * 8+8); 
var 概率5 = Math.floor(Math.random() * 8+8); 
var 概率6 = Math.floor(Math.random() * 8+8); 
var 概率7 = Math.floor(Math.random() * 50+50); 

var itemSet = new Array(4020000);


var rand = Math.floor(Math.random() * itemSet.length);
var suliang = Math.floor(Math.random() * 2) + 1;
var rand2 = Math.floor(Math.random() * 5);


	function start() {
		status = -1;
		action(1, 0, 0);
		}

	function action(mode, type, selection) {
		if (mode == -1) {
		cm.dispose();
		} else {
		if (status >= 2 && mode == 0) {
		cm.dispose();
		return;
		}
		if (mode == 1)
		status++;
		else
		status--;


	if (status == 0) {
		for(var i = 1;i<=5;i++){
		if(cm.getPlayer().getInventory(MapleInventoryType.getByType(i)).isFull()){
		cm.sendOk("您至少应该让所有包裹都空出一格");
		cm.dispose();
		return;
		}
		}
		var ii = MapleItemInformationProvider.getInstance();		                
		var type = ii.getInventoryType(itemSet[rand]);
		if(ii.getInventoryType(itemSet[rand]).getType() == 1){	//装备类	 		
		var toDrop = ii.randomizeStats(ii.getEquipById(itemSet[rand])).copy();
		var cishu = Math.floor(Math.random() * 3) + toDrop.getUpgradeSlots();	    				
		toDrop.setUpgradeSlots(cishu);				
		if(itemSet[rand] == 1402014 || itemSet[rand] == 1442039 || itemSet[rand] == 1432046 || itemSet[rand] == 1122017){
		if(rand2 == 0){
		var temptime = new java.sql.Timestamp(java.lang.System.currentTimeMillis() + 1 * 24 * 60 * 60 * 1000);					
		}else if(rand2 == 1){
		var temptime = new java.sql.Timestamp(java.lang.System.currentTimeMillis() + 3 * 24 * 60 * 60 * 1000);
		}else if(rand2 == 2){
		var temptime = new java.sql.Timestamp(java.lang.System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000);
		}else if(rand2 == 3){
		var temptime = new java.sql.Timestamp(java.lang.System.currentTimeMillis() + 10 * 24 * 60 * 60 * 1000);
		}
		toDrop.setExpiration(temptime);											
		}	
							
		}else if(ii.getInventoryType(itemSet[rand]).getType() == 3){ //椅子
		var toDrop = new Item(itemSet[rand],0,1).copy();
		if(itemSet[rand] == 3010070){ //PB只给一天的
		var temptime = new java.sql.Timestamp(java.lang.System.currentTimeMillis() + 1 * 24 * 60 * 60 * 1000);
		}else{
		if(rand2 == 0){
		var temptime = new java.sql.Timestamp(java.lang.System.currentTimeMillis() + 1 * 24 * 60 * 60 * 1000);					
		}else if(rand2 == 1){
		var temptime = new java.sql.Timestamp(java.lang.System.currentTimeMillis() + 3 * 24 * 60 * 60 * 1000);
		}else if(rand2 == 2){
		var temptime = new java.sql.Timestamp(java.lang.System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000);
		}else if(rand2 == 3){
		var temptime = new java.sql.Timestamp(java.lang.System.currentTimeMillis() + 10 * 24 * 60 * 60 * 1000);
		}
		}
		toDrop.setExpiration(temptime);						
		}else{

		if(Math.floor(itemSet[rand] / 10000) == 202){ //其它一些东西
		var toDrop = new Item(itemSet[rand],0,suliang).copy();	
		}else{
		var toDrop = new Item(itemSet[rand],0,1).copy();				
		}
		}
var 物品数量 =0; 
var 经验 = 0; 
var 金币 = 0; 
if (cm.getPlayer().getLevel() > 120 ) {  
var 物品数量 = Math.floor(Math.random() * 400); 
var 经验 = Math.floor(Math.random() * 500000+300000); 
var 金币 = Math.floor(Math.random() * 500000+300000); 
} else if (cm.getPlayer().getLevel() > 70 ) { 
var 物品数量 = Math.floor(Math.random() * 400); 
var 经验 = Math.floor(Math.random() * 200000+100000); 
var 金币 = Math.floor(Math.random() * 200000+100000); 
} else if (cm.getPlayer().getLevel() > 30 ) { 
var 物品数量 = Math.floor(Math.random() * 399); 
var 经验 = Math.floor(Math.random() * 100000+50000); 
var 金币 = Math.floor(Math.random() * 100000+50000); 
} else {
                    
var 物品数量 = Math.floor(Math.random() * 399); 
var 经验 = Math.floor(Math.random() * 50000+10000); 
var 金币 = Math.floor(Math.random() * 50000+10000); 
} 
                 cm.gainItem(cm.getPlayer().get怪物ID(),-cm.getPlayer().get怪物数量());
                 cm.gainExp(cm.getPlayer().getLevel()*8000);//百分之20经验
                 //cm.gainMeso(cm.getPlayer().getLevel()*100000);
                 cm.gainItem(RED,10);
                 cm.gainItem(黄金枫叶,概率2);
                 cm.gainItem(混沌卷轴60,概率3);
                 cm.gainItem(祝福卷轴,概率4);
                 cm.gainItem(低级贝勒德币,概率5);
                 cm.gainItem(金杯,概率6);
                 cm.gainItem(枫叶,概率7);
				 cm.gainMeso(5000000);//金币
                 cm.喇叭(1," "+cm.getChar().getName()+" 完成今日的的收集任务,获得了丰厚的奖励")
                 cm.getPlayer().setBossLog('sk123');
                 cm.getPlayer().取消怪物ID();
                 cm.getPlayer().取消怪物数量();
                 cm.sendOk("恭喜你获得了：\r\n1.#i"+RED+"##z"+RED+"# *10\r\n2.#i"+黄金枫叶+"##z"+黄金枫叶+"# *8-16\r\n3.#i"+混沌卷轴60+"#.#z"+混沌卷轴60+"# *8-16\r\n4.#i"+祝福卷轴+"##z"+祝福卷轴+"# *8-16\r\n5.#i"+低级贝勒德币+"##z"+低级贝勒德币+"# *8-16\r\n6.#i"+金杯+"##z"+金杯+"# *8-16\r\n7.#i"+枫叶+"##z"+枫叶+"# *50-100\r\n8.#i4031138# 500W\r\n9.等级 * 8000点经验 获得="+cm.getPlayer().getLevel()*8000+"");
		         cm.dispose();
		}
	}
}

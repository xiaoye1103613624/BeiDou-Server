load("nashorn:mozilla_compat.js");
importPackage(Packages.client);
importPackage(Packages.client.inventory);
importPackage(Packages.server);
importPackage(Packages.tools);
var 签到币 = 4000487
var 黄金枫叶 = 4000313
var 绿水灵橡皮檫 = 4001013
var 超级药水 =2000005
var 概率2 = Math.floor(Math.random() * 8+8); 
var 概率3 = Math.floor(Math.random() * 3+3); 
var 概率4 = Math.floor(Math.random() * 6+6); 

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
		//cm.getPlayer().getInventory(type).addItem(toDrop);
		//cm.getC().getSession().write(MaplePacketCreator.addInventorySlot(type, toDrop));		
              
               // cm.gainMeso(100000);
            //     cm.gainItem(4251200,3);
            //       cm.gainItem(4310081,5);//cm.getPlayer().get怪物ID() == 0 && cm.getPlayer().get怪物数量()
            //      cm.getPlayer().setBossLog('sk1');

                 cm.gainItem(cm.getPlayer().get怪物ID(),-cm.getPlayer().get怪物数量());
                 cm.gainExp(cm.getPlayer().getLevel()*8000);//百分之20经验
                 cm.gainMeso(cm.getPlayer().getLevel()*1000);
                 cm.gainItem(签到币,1);
                 cm.gainItem(黄金枫叶,概率2);
                 cm.gainItem(绿水灵橡皮檫,概率3);
                 cm.gainItem(超级药水,概率4);
//1.#z"+签到币+"# *1\r\n2.#z"+黄金枫叶+"# *8-16\r\n3.#z"+绿水灵橡皮檫+"# *3-6\r\n4.#z"+超级药水+"# *3-6\r\n5.等级 * 1000金币\r\n6.等级 * 8000点经验
            //   cm.喇叭(1,"成功完成野外BOSS悬赏任务获得了『下等五彩水晶』x3个+『osss币』x5个3088W金币")
                 cm.喇叭(1," "+cm.getChar().getName()+" 完成今日的1次狩猎任务,获得了丰厚的奖励")
                 cm.getPlayer().setBossLog('sk1');
                 cm.getPlayer().取消怪物ID();
                 cm.getPlayer().取消怪物数量();
                 cm.sendOk("恭喜你获得了：\r\n1.#z"+签到币+"# *1\r\n2.#z"+黄金枫叶+"# *8-16 随机获得="+概率2+"\r\n3.#z"+绿水灵橡皮檫+"# *3-6 随机获得="+概率3+"\r\n4.#z"+超级药水+"# *3-6 随机获得="+概率4+"\r\n5.等级 * 1000金币 获得="+cm.getPlayer().getLevel()*1000+"\r\n6.等级 * 8000点经验 获得="+cm.getPlayer().getLevel()*8000+"");
		         cm.dispose();
		}
	}
}

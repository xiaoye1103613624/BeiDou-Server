function action(mode, type, selection) {
	var chance = Math.ceil(Math.random()* 4);
    if (cm.getMapId() == 910010100) { // 领奖地图
		// 4001095 - 4001099 种子 910010300 910010100
        for (var i = 4001095; i < 4001099; i++) {
            cm.removeAll(i);
        }
		cm.setBossLog('月妙副本');
		cm.gainItem(4001028,1); //加血材料
		if(chance == 1){
			cm.gainItem(4001101,1);
			cm.warp(910010200,0); // 骚地图
			// cm.warp(100000200,0);
			cm.喇叭(4,"["+cm.getPlayer().getName()+"]带领他的队伍,完成了[月妙副本],获取丰厚奖励,大家恭喜TA吧!!!");
			cm.dispose();
		}else if (chance == 2){
			cm.gainItem(4001101,2);
			cm.warp(910010200,0); // 骚地图
			// cm.warp(100000200,0);
			cm.喇叭(4,"["+cm.getPlayer().getName()+"]带领他的队伍,完成了[月妙副本],获取丰厚奖励,大家恭喜TA吧!!!");
			cm.dispose();
		}else if (chance == 3){
			cm.gainItem(4001101,3);
			//cm.warp(910010200,0); // 骚地图
			cm.warp(100000200,0);
			cm.dispose();
		}else if (chance == 4){
			cm.gainItem(4001101,1);
			// cm.warp(910010200,0); // 骚地图
			cm.喇叭(4,"["+cm.getPlayer().getName()+"]带领他的队伍,完成了[月妙副本],获取丰厚奖励,大家恭喜TA吧!!!");
			cm.warp(100000200,0);
			cm.dispose();
		}
     
	 // 一个是特殊奖励地图(杀怪的,就是怪多,没别的)  另一个是掉线出来的地图
    } else if (cm.getMapId() == 910010200 || cm.getMapId() == 910010300){
       cm.warp(100000200,0);
	   cm.dispose();
    } else {
       cm.sendOk("在这里我没功能");
	   cm.dispose();
    }
}
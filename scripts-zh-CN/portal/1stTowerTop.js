/*
	名称:	狮子王城
	地图:	第一座塔
	描述:	211060200
*/

function enter(pi) {
	if (pi.getQuestStatus(3164) == 1) {
		if (pi.getPlayerCount(921140100) == 0) {
			pi.getMap(921140100).resetFully();//地图刷新
			pi.warp(921140100,1);  //危险的第一座塔楼
			pi.spawnMobOnMap(8210010,1,1171,-183,921140100);//召唤怪物
			pi.getPlayer().startMapTimeLimitTask(1200, pi.getPlayer().getMap().getReturnMap());
		} else {
			pi.playerMessage("[White]任务正在执行中，请尝试其他频道");
		}
	} else if (pi.haveItem(4032832)) {
		if (pi.getPlayerCount(211060201) == 0) {
			var em = pi.getEventManager("tower_First");
			em.startInstance(pi.getPlayer());
		} else {
			pi.playerMessage("[White]任务正在执行中，请尝试其他频道");
		}
	} else {
		pi.playerMessage("[White]需要第一座塔的钥匙才能看开启传送点");
	}
}
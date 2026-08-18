function enter(pi) {
	if (pi.getQuestStatus(31347)==2 && pi.getQuestStatus(31348)==1) {
	    pi.warp(240092400,0);//传送到岩壁巨人的躯干上方3
        pi.spawnMonster(9100044,318,-480);//地图召唤任务怪物黑暗艾利杰
		pi.spawnMonster(9100044,318,-216);//地图召唤任务怪物黑暗艾利杰
		pi.spawnMonster(9100044,246,59);//地图召唤任务怪物黑暗艾利杰
		pi.spawnMonster(9100044,1094,-59);//地图召唤任务怪物黑暗艾利杰
		pi.spawnMonster(9100044,1089,-212);//地图召唤任务怪物黑暗艾利杰
		pi.spawnMonster(9100044,1016,-480);//地图召唤任务怪物黑暗艾利杰
	    pi.playerMessage(-1,"奇怪的怪物出现了。");
	    pi.playerMessage("地图出现了奇怪的怪物。");
	} else {
		pi.playerMessage(-1,"现在不能通过。");
	    pi.playerMessage("你未执行任务，现在还无法通过这里。");
	}    
}

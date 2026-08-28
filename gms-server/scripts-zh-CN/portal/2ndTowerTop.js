/*
	名字:	狮子王城
	地图:	第二座塔
	描述:	211060400
*/

function enter(pi) {
	if (pi.haveItem(4032833)) { // 检查玩家是否拥有第二座塔楼的钥匙
		if (pi.getPlayerCount(211060401) == 0) { // 检查第二座塔楼的下一层地图是否有玩家
			var em = pi.getEventManager("tower_Second"); // 获取事件管理器
			em.startInstance(pi.getPlayer()); // 开始事件副本
		} else {
			pi.playerMessage("[White] 任务正在进行中，请尝试其他频道。"); // 如果下一层地图有玩家，提示玩家尝试其他频道
		}
	} else {
		pi.playerMessage("[White] 需要第二座塔楼的钥匙才能开启传送点。"); // 如果玩家没有钥匙，提示玩家需要钥匙才能进入下一层地图
	}
}
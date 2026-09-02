/*
	名字:	獅子王城
	地圖:	第三座塔
	描述:	211060600
*/

function enter(pi) {
	if (pi.haveItem(4032834)) {
	if (pi.getPlayerCount(211060601) == 0) {
		var em = pi.getEventManager("tower_Third");
		em.startInstance(pi.getPlayer());
	} else {
		pi.playerMessage("[White]任务正在执行中，请尝试其它频道");
		}
	} else {
		pi.playerMessage("[White]需要第三座塔的钥匙才能看开启传送点");
}
}
/*
	名字:	獅子王城
	地圖:	第五座塔
	描述:	211061000
*/

function enter(pi) {
	if (pi.getQuestStatus(3142) == 1 || pi.getQuestStatus(3147) == 1) {
	if (pi.getPlayerCount(211061100) == 0) {
		pi.resetMap(211061100);   //地图刷新
		pi.warp(211061100,1);  //亞尼的禁閉室
		pi.spawnMobOnMap(8210013,1,117,-580,211061100);//召唤怪物
		pi.getPlayer().startMapTimeLimitTask(1800, pi.getPlayer().getMap().getReturnMap());
	} else {
		pi.playerMessage("[White]任務正在執行中，請嘗試其它頻道");
		}
	} else {
		pi.playerMessage("[White]需要接受相关任務");
}
}

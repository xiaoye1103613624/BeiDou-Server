/*
	名字:	依菲雅
	地圖:	第五座塔樓
	描述:	211061001
*/

function start() {
	if (cm.getQuestStatus(3173) == 1 || cm.getQuestStatus(3175) == 1) {
		cm.warp(211070200, 3);
	} else {
		cm.sendOk("呜……呜………呜呜……。");
		}
		cm.dispose();
}
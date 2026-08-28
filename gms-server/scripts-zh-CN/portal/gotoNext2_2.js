/*
	名字:	獅子王城
	地圖:	第二座塔
	描述:	211060400
*/

function enter(pi) {
	if (pi.getQuestStatus(3143) == 2) {
		pi.warp(211060410, 1);  //矮城牆1
	} else {
		pi.playerMessage("[White]未完成相关任务");
		}
	return true;
}
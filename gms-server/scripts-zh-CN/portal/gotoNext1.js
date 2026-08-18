/*
	名字:	獅子王城
	地圖:	第一座塔
	描述:	211060200
*/

function enter(pi) {
	if (pi.getQuestStatus(3139) == 1) {
		pi.openNpc(2161002, 1);
	} else if (pi.getQuestStatus(3139) == 2) {
		pi.warp(211060300, 2);  //城牆下2
	} else {
		pi.openNpc(2161002);
	}
	return true;
}
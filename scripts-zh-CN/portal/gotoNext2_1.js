/*
	名字:	獅子王城
	地圖:	第二座塔
	描述:	211060400
*/

function enter(pi) {
	if (pi.getQuestStatus(3140) == 1) {
		pi.openNpc(2161002, 3);
	} else if (pi.getQuestStatus(3140) == 2) {
		pi.warp(211060500, 1);  //城牆下3
	} else {
		pi.openNpc(2161002, 2);
		}
	return true;
}
/*
	名字:	獅子王城
	地圖:	第三座塔
	描述:	211060600
*/

function enter(pi) {
	if (pi.getQuestStatus(3141) == 1) {
		if (pi.getQuestStatus(3167) == 1) pi.openNpc(2161002, 5);
		else pi.openNpc(2161002, 6);
	} else if (pi.getQuestStatus(3141) == 2) {
		pi.warp(211060700, 1);   //城牆下4
	} else {
		pi.openNpc(2161002, 4);
		}
	return true;
}
/*
	名字:	獅子王城
	地圖:	秘密走道
	描述:	921140000
*/

function enter(pi) {
	if (!pi.haveMonster(9300296)) {
		pi.warp(921140001, 3);  //陰鬱的見面室
	} else {
		pi.playerMessage(-7,"[Portal]消滅地圖中所有怪物");
}
}
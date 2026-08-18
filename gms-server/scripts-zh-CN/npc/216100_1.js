/*
	名字:	盧頓
	地圖:	第四座塔
	描述:	211060800
*/

var status;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	switch (mode) {
	case 0:
		status--;
		break;
	case 1:
		status++;
		break;
		}
	switch (status) {
	case 0:
		if (cm.getInfoQuest(3139).equals("clear")) {
			cm.sendNext("你解開了第一個封印，好像比我想像的更强，但是後面還需要解開兩個這樣的封印，才能到達我所在的地方。現在回頭還來得及，怎麼樣？");
		} else {
			cm.sendOk("要想穿過這扇門，必須拿到第一座塔的鑰匙#v4032832:#，進去把怪物的全部消滅掉，才能解開封印。");
			cm.dispose();
			}
			break;
	case 1:
		cm.sendNextPrev("#b聽你這麼一說，我反而更有鬥志了，你等著，我馬上過去。");
		break;
	case 2:
		cm.sendPrev("那我就祝你能够獲勝，希望你能打敗那幫邪惡的傢伙。");
		break;
	case 3:
		cm.forceCompleteQuest(3139);
		cm.gainItem(4032832, -1);
		cm.dispose();
}
}
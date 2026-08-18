/* Andre
	Kerning Random Hair/Hair Color Change.
 */
var status = -1;
var beauty = 0;
var hair_Colo_new;

function start() {
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == 0) {
		cm.dispose();
		return;
	} else {
		status++;
	}

	if (status == 0) {
		var txt  = "";
			txt += "你好，我是魔法师库迪，为了学习更多的魔法，我就来到了妖精学院做魔法研究。\r\n\r\n";
			if(cm.getQuestStatus(10107016) != 2 && cm.getQuestStatus(10107015) == 2 &&cm.getMapId()==101073000){
				//洋葱种植园
				txt += "#L1##v4031025##b进入洋葱种植园深处#k#l\r\n";
			}else if(cm.getQuestStatus(10107018) != 2 && cm.getQuestStatus(10107017) == 2 && cm.getMapId()==101073100){
				txt += "#L2##v4031025##b进入萝卜种植园深处#k#l\r\n";
			}else if(cm.getMapId()==101073200){
				//后院空地
				txt += "#L3##b请送我回到萝卜田#k#l\r\n";
			}else{
				cm.dispose();
			}
		cm.sendSimple(txt);
	} else if (status == 1) {
		if (selection == 1) {
			cm.openNpc(1500013,100);
		}else if(selection == 2){
			cm.openNpc(1500013,200);
		}else if(selection == 3){
			cm.warp(101073100,1);
			cm.dispose();
		}
	}
	
}

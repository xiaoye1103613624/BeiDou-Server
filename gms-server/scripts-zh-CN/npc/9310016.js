/* 青瑕 ID:9310016
	豫园大道NPC 剪发+染发
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
	cm.sendSimple("您好，我是#p9310016#. 如果你有 #b#t5150015##k 或者有 #b#t5151011##k 请允许我把你的头发护理。请选择一个你想要的.\r\n#L0#使用 #i5150015##t5150015##l\r\n#L1#使用 #i5151011##t5151011##l");
    } else if (status == 1) {
	if (selection == 0) {
	    var hair = cm.getPlayerStat("HAIR");
	    hair_Colo_new = [];
	    beauty = 1;

	    if (cm.getPlayerStat("GENDER") == 0) {
		hair_Colo_new = [30030, 30040, 30000, 30060, 30110, 30120, 30160, 30260, 30270, 30420, 30550, 30340, 30300];
	    } else {
		hair_Colo_new = [31000, 31420, 31290, 31490, 30420, 31480, 31810, 31080, 31880, 31030, 31850, 31700, 34000];
	    }
	    for (var i = 0; i < hair_Colo_new.length; i++) {
		hair_Colo_new[i] = hair_Colo_new[i] + (hair % 10);
	    }
	    cm.askAvatar("选择一个你想要的。",5150025, hair_Colo_new);
	} else if (selection == 1) {
	    var currenthaircolo = Math.floor((cm.getPlayerStat("HAIR") / 10)) * 10;
	    hair_Colo_new = [];
	    beauty = 2;

	    for (var i = 0; i < 8; i++) {
		hair_Colo_new[i] = currenthaircolo + i;
	    }
	    cm.askAvatar("选择一个你想要的。",5151020, hair_Colo_new);
	}
    } else if (status == 2){
	if (beauty == 1) {
	    if (cm.setAvatar(5150025, hair_Colo_new[selection]) == 1) {
		cm.sendOk("享受!");
	    } else {
		cm.sendOk("痾...似乎没有#b#t5150025##k");
	    }
	} else {
	    if (cm.setAvatar(5151020, hair_Colo_new[selection]) == 1) {
		cm.sendOk("享受!");
	    } else {
		cm.sendOk("痾...似乎没有#b#t5151020##k");
	    }
	}
	cm.dispose();
    }
}
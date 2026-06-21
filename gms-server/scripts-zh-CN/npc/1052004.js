
       /* Grandpa Luo
	Mu Lung VIP face/face Color Change.
*/
var status = -1;
var beauty = 0;
var face_Colo_new;

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
	cm.sendSimple("我是黛玛院长. 如果你有 #b#t5152001##k 那么我就可以帮你整形 \r\n\#L0#使用 #b#t5152001##k");
    } else if (status == 1) {
	if (selection == 0) {
	    var face = cm.getPlayerStat("face");
	    face_Colo_new = [];
	    beauty = 1;

	    if (cm.getPlayerStat("GENDER") == 0) {
		face_Colo_new = [20044,20037,20046,20038,20052,20051,20047,20001,20002,20003,20004,20005,20006,20007,20008,20009,20010,20011,20012,20013,20014,20016,20017,20018,20019,20020,20021,20022,20023,20024,20025,20026,20027];
	    } else {
		face_Colo_new = [21031,21082,21044,21042,21036,21050,21149,21045,21001,21002,21003,21004,21005,21006,21007,21008,21009,21010,21011,21012,21013,21014,21015,21016,21017,21018,21019,21020,21021,21022,21023,21024,21025,21026,21027];
	    }
	    for (var i = 0; i < face_Colo_new.length; i++) {
		face_Colo_new[i] = face_Colo_new[i] + (face % 10);
	    }
	    cm.askAvatar("选择一个你想要的。",5152001, face_Colo_new);
	} else if (selection == 1) {
	    var currentfacecolo = Math.floor((cm.getPlayerStat("face") / 10)) * 10;
	    face_Colo_new = [];
	    beauty = 2;

	    for (var i = 0; i < 8; i++) {
		face_Colo_new[i] = currentfacecolo + i;
	    }
	    cm.askAvatar("选择一个你想要的。",5152001, face_Colo_new);
	}
    } else if (status == 2){
	if (beauty == 1) {
	    if (cm.setAvatar(5152001, face_Colo_new[selection]) == 1) {
		cm.sendOk("完工!");
	    } else {
		cm.sendOk("你似乎没有#b#t5152001##k");
	    }
	} else {
	    if (cm.setAvatar(5152001, face_Colo_new[selection]) == 1) {
		cm.sendOk("完工!");
	    } else {
		cm.sendOk("你似乎没有#b#t5152001##k");
	    }
	}
	cm.dispose();
    }
}
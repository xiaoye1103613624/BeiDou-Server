var bigHeart = [3994731];
var req = [
    [4001226, 1],
    [4001227, 1],
    [4001228, 1],
    [4001229, 1],
	[4001230, 1]
];

var sels;
var status = -1;

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else if (mode == 0) {
        status--;
    } else {
        cm.dispose();
        return;
    }
    if (status == 0) {
        var msg = "";
        msg += "\r\n#d需要:#b ";
        msg += "\r\n\r\n";
        for (var ii = 0; ii < req.length; ii++) {
            msg += "#i" + req[ii][0] + "##t" + req[ii][0] + "#x" + req[ii][1];
            if (ii+1 % 3 == 0) {
                msg += "\r\n";
            }
        }
        msg += "\r\n";
        msg += "#g----------------------------------------------\r\n";
        for (var i = 0; i < bigHeart.length; i++) {
            msg += "#r#L" + i + "#";
            msg += "#i" + bigHeart[i] + "##t" + bigHeart[i];
        }
        cm.sendSimple("#d利琳的情人节金币制作......#k！#l\r\n\r\n" + msg + "");
    } else if (status == 1) {
        sels = selection;
        if (!cm.canHold(bigHeart[sels])) {
            cm.sendNext("#r背包空间不足");
            cm.dispose();
            return;
        }
        for (var i = 0; i < req.length; i++) {
            if (!cm.haveItem(req[i][0], req[i][1])) {
                cm.sendNext("#b你身上没有#r足够的材料#k，继续收集材料去吧！");
                cm.dispose();
                return;
            }
        }
        cm.sendYesNo("#b是否要兑换#r #i" + bigHeart[sels] + "# \r\n");
    } else if (status == 2) {
        for (var i = 0; i < req.length; i++) {
            cm.gainItem(req[i][0], -req[i][1]);
        }
		cm.gainItem(bigHeart[sels],60,60,60,60,0,0,50,50,0,0,0,0,0,0);
        cm.sendNext("#b已经兑换了 #i" + bigHeart[sels] + "#");
        cm.dispose();
    } else {
        cm.sendNext("#r发生错误: mode : " + mode + " status : " + status);
        cm.dispose();
    }
}
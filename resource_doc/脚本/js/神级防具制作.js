var status = -1;
var selected = null;

var itemObjS = {
	"1003172": [[1003601, 1],[4000244, 200],[4000245, 200],[4021009, 30],[4011007, 30],[4000038, 50],[4001126, 5000],[3990000, 500],[4170016, 10],[4000464, 10], ['金币', 100000000], ['点券', 10000]],
	"1042258": [[1052509, 1],[4000244, 200],[4000245, 200],[4021009, 30],[4011007, 30],[4000038, 50],[4001126, 5000],[3990000, 500],[4170016, 10],[4000464, 10], ['金币', 100000000], ['点券', 10000]],
	"1062169": [[1052509, 1],[4000244, 200],[4000245, 200],[4021009, 30],[4011007, 30],[4000038, 50],[4001126, 5000],[3990000, 500],[4170016, 10],[4000464, 10], ['金币', 100000000], ['点券', 10000]],
	"1072485": [[1072711, 1],[4000244, 200],[4000245, 200],[4021009, 30],[4011007, 30],[4000038, 50],[4001126, 5000],[3990000, 500],[4170016, 10],[4000464, 10], ['金币', 100000000], ['点券', 10000]],
	"1082295": [[1082472, 1],[4000244, 200],[4000245, 200],[4021009, 30],[4011007, 30],[4000038, 50],[4001126, 5000],[3990000, 500],[4170016, 10],[4000464, 10], ['金币', 100000000], ['点券', 10000]],
	"1102275": [[1102456, 1],[4000244, 200],[4000245, 200],[4021009, 30],[4011007, 30],[4000038, 50],[4001126, 5000],[3990000, 500],[4170016, 10],[4000464, 10], ['金币', 100000000], ['点券', 10000]],
	"1132143": [[1132156, 1],[4000244, 200],[4000245, 200],[4021009, 30],[4011007, 30],[4000038, 50],[4001126, 5000],[3990000, 500],[4170016, 10],[4000464, 10], ['金币', 100000000], ['点券', 10000]],
}
function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1)
        status++;
    else {
        cm.dispose();
        return;
    }
    if (status == 0) {
        var text = "你想制作什么？\r\n";
		for(var oKey in itemObjS) {
			text += "#L"+ oKey +"##v"+ oKey +"##b#z"+ oKey +"##k\r\n";
		}
        cm.sendSimple(text);
    } else if (status == 1) {
		if(!itemObjS[selection]) {
			cm.sendOk("数据错误！");
            cm.dispose();
            return;
		}
		selected = selection;
		var text = "制作#v"+ selection +"##b#z"+ selection +"##k\r\n";
		text += "需求如下：\r\n";
		var needItems = itemObjS[selection];
		for(var i = 0; i < needItems.length; i++) {
			if('金币' == needItems[i][0]) {
				text += "金币 x "+ needItems[i][1] +"\r\n";
			} else if ('点券' == needItems[i][0]) {
				text += "点券 x "+ needItems[i][1] +"\r\n";
			} else {
				text += "#v"+ needItems[i][0] +"##b#z"+ needItems[i][0] +"##k x "+ needItems[i][1]+ " 你有 #r#c"+ needItems[i][0] +"##k 个\r\n";
			}
		}
		cm.sendSimple(text);
    } else if (status == 2) {
		var msg = "";
		var needItems = itemObjS[selected];
		for(var i = 0; i < needItems.length; i++) {
			if('金币' == needItems[i][0]) {
				if(cm.getPlayer().getMeso() < needItems[i][1]) {
					msg += "金币 不足 "+ needItems[i][1] +"\r\n"
				}
			} else if ('点券' == needItems[i][0]) {
				if(cm.getPlayer().getCSPoints(1) < needItems[i][1]) {
					msg += "点券 不足 "+ needItems[i][1] +"\r\n"
				}
			} else if(!cm.haveItem(needItems[i][0], needItems[i][1])) {
				msg += "#v"+ needItems[i][0] +"##b#z"+ needItems[i][0] +"##k x "+ needItems[i][1] + " 不足\r\n"
			}
		}
		if('' !== msg) {
			cm.sendOk(msg);
            cm.dispose();
            return;
		}
		for(var i = 0; i < needItems.length; i++) {
			if('金币' == needItems[i][0]) {
				cm.gainMeso(-needItems[i][1]);
			} else if ('点券' == needItems[i][0]) {
				cm.gainNX(-needItems[i][1]);
			}  else {
				cm.gainItem(needItems[i][0], -needItems[i][1]);
			}
		}
		cm.gainItem(selected, 1);
		cm.sendOk("制作 #v"+ selected +"##b#z"+ selected +"##k x 1 成功");
		cm.dispose();
	}
}



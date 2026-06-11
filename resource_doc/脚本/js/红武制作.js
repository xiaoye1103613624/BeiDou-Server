/******************
 079 085脚本
 QQ:870074996
 作者:小猫
********************/
var status = -1;
var selected = null;

var itemObjS = {
	"1422156": [[4310088, 1888],['金币', 1]],
	"1402214": [[4310088, 1888],['金币', 1]],
	"1432182": [[4310088, 1888],['金币', 1]],
	"1382226": [[4310088, 1888],['金币', 1]],
	"1472230": [[4310088, 1888],['金币', 1]],
	"1332242": [[4310088, 1888],['金币', 1]],
	"1452220": [[4310088, 1888],['金币', 1]],
	"1462208": [[4310088, 1888],['金币', 1]],
	"1482183": [[4310088, 1888],['金币', 1]],
	"1492194": [[4310088, 1888],['金币', 1]],
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
			}  else if(!cm.haveItem(needItems[i][0], needItems[i][1])) {
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
			}  else {
				cm.gainItem(needItems[i][0], -needItems[i][1]);
			}
		}
		cm.gainItem(selected, 1);
		cm.sendOk("制作 #v"+ selected +"##b#z"+ selected +"##k x 1 成功");
		cm.dispose();
	}
}



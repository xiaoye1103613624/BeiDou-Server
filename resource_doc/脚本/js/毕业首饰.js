var status = -1;
var selected = null;

var itemObjS = {
	"1032219": [[1032205, 1], [1032206, 1], [1032207, 1], [1032208, 1], [1032209, 1], [4310097, 20], [4310098, 20], [4310156, 20]],
	"1012174": [[1012170, 1], [1012171, 1], [1012172, 1], [1012173, 1], [4310097, 20], [4310098, 20], [4310156, 20]],
	"1132215": [[1132211, 1], [1132212, 1], [1132213, 1], [1132214, 1], [4310097, 20], [4310098, 20], [4310156, 20]],
	
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
        var text = "你想制作什么？\r\n#r显示不准确，实际属性为全属性100\r\n";
		for(var oKey in itemObjS) {
			text += "#L"+ oKey +"# #v"+ oKey +"# #b#z"+ oKey +"# #k\r\n";
		}
        cm.sendSimple(text);
    } else if (status == 1) {
		if(!itemObjS[selection]) {
			cm.sendOk("数据错误！");
            cm.dispose();
            return;
		}
		selected = selection;
		var text = "#v"+ selection +"##b#z"+ selection +"##r全属性100#k\r\n";
		text += "需求如下：\r\n";
		var needItems = itemObjS[selection];
		for(var i = 0; i < needItems.length; i++) {
				text += "#v"+ needItems[i][0] +"##b#z"+ needItems[i][0] +"##k x "+ needItems[i][1]+ " 你有 #r#c"+ needItems[i][0] +"##k 个\r\n";
		}
		cm.sendSimple(text);
    } else if (status == 2) {
		var msg = "";
		var needItems = itemObjS[selected];
		for(var i = 0; i < needItems.length; i++) {
			 if(!cm.haveItem(needItems[i][0], needItems[i][1])) {
				msg += "#v"+ needItems[i][0] +"##b#z"+ needItems[i][0] +"##k x "+ needItems[i][1] + " 不足\r\n"
			}
		}
		if('' !== msg) {
			cm.sendOk(msg);
            cm.dispose();
            return;
		}
		for(var i = 0; i < needItems.length; i++) {
				cm.gainItem(needItems[i][0], -needItems[i][1]);
		}
		cm.给属性装备(selected,0,0,100,100,100,100,0,0,100,100,0,0,0,0,0,0,0);
		cm.sendOk("制作 #v"+ selected +"##b#z"+ selected +"##k x 1 成功");
		cm.dispose();
	}
}
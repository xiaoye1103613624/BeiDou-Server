
/**
 079 085脚本
 QQ:870074996
 作者:小猫
**/
var status = -1;
var selected = null;

var itemObjS = {
	//"1032219": [[4003005, 500],[4003004, 500],[4000064, 500],[4001006, 1],[4032056, 1],[4003004, 1], ['金币', 100000000], ['点券', 10000]],
	"1102074": [[4003005, 500],[4003004, 500],[4000064, 500],[4001006, 10], [4170007, 2], ['金币', 100000000], ['点券', 10000]],
	"1102153": [[4003005, 500],[4003004, 500],[4000064, 500],[4001006, 10], [4170007, 2],['金币', 100000000], ['点券', 10000]],
	"1102389": [[4003005, 500],[4003004, 500],[4000064, 500],[4001006, 10], [4170007, 2],['金币', 100000000], ['点券', 10000]],
	"1102390": [[4003005, 500],[4003004, 500],[4000064, 500],[4001006, 10], [4170007, 2],['金币', 100000000], ['点券', 10000]],
	
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
        var text = "你想拥有翅膀吗???听说在本岛帅气的人都拥有属于自己的翅膀哦~!\r\n";
            text += "当前翅膀等阶:[#r未激活#k]\r\n";
            text += "请选择你需要制作点初级翅膀[#r全属性+10#k]\r\n";
		for(var oKey in itemObjS) {
			text += "#L"+ oKey +"#兑换#v"+ oKey +"##b说明:#k[#r全属性+10#k]#k\r\n";
		}
        cm.sendSimple(text);
    } else if (status == 1) {
		if(!itemObjS[selection]) {
			cm.sendOk("数据错误！");
            cm.dispose();
            return;
		}
		selected = selection;
		var text = "看样子你还没有翅膀吧?听说在本岛上帅气的人都会拥有属于自己的翅膀哟^ !第一次获取翅膀只需要以下材料就可以制作哟#k\r\n";
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
		if (cm.getSpace(1) < 5 || cm.getSpace(2) < 5 || cm.getSpace(4) < 5|| cm.getSpace(3) < 5) {
            cm.sendOk("请把装备栏,消耗栏,其他栏，空出5个格子，设置栏空出5个格子出来");
            cm.dispose();
            return;
        }
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
		cm.setBossRankPoints("翅膀",selected);
		cm.setBossRankPoints("翅膀等阶",1);
		cm.给属性装备(selected,0,0,10,10,10,10,0,0,10,10,0,0,0,0,0,0,0);
		cm.sendOk("进阶 #v"+ selected +"##b#z"+ selected +"##k成功。");
		cm.getPlayer().指定喇叭("高质地喇叭", "系统公告", "恭喜[" + cm.getPlayer().getName() + "]成功完成翅膀1阶!");
		cm.dispose();
	}
}
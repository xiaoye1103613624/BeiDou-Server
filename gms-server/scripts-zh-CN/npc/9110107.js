/*
	NPC Name: 		Palakeen
	Map(s): 		Zipangu - Mushroom Shrine
	Description: 		Kaede Castle teleporter
*/

var status = -1;
var cost = 3000;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 1 || status == 0 && mode == -1) {
	    cm.sendNext("等等，你不打算坐这个吗？");
	    cm.dispose();
	    return;
	}
	status--;
    }
    switch (cm.getMapId()) {
	case 800040000: {
	    if (status == 0) {
		cm.sendNext("我们是轿夫~！ 让轿夫带你去任何地方，甚至是被樱花包围的古代神社！ 一次 " + cost + "金币就好了。");
		} else if (status == 1) {
		cm.sendYesNo("您真的要去参观古代神社吗??");
	    } else if (status == 2) {
		cm.sendNext("好的，我明白了！ 只需让我们来完成工作，您就会在眨眼之间到达目的地！");
	    } else if (status == 3) {
		cm.gainMeso(-cost);
		cm.warp(800000000, 0);
		cm.dispose();
	    }
	    break;
	}
	default: {
	    if (status == 0) {
		cm.sendNext("我们是轿夫~！ 让轿夫带你去任何地方，甚至是被樱花包围的枫城~！ 一次 " + cost + "金币就好了。");
		} else if (status == 1) {
		cm.sendYesNo("您真的要去参观枫城吗??");	
	    } else if (status == 2) {
		cm.sendNext("好的，我明白了！ 只需让我们来完成工作，您就会在眨眼之间到达目的地！");
	    } else if (status == 3) {
		cm.gainMeso(-cost);
		cm.warp(800040000, 0);
		cm.dispose();
	    }
	    break;
	}
    }
}
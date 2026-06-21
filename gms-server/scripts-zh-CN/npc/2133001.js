var status = -1;
 
 
 
function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
    switch(cm.getPlayer().getMapId()) {
	case 930000000:
	    cm.sendNext("欢迎，请进入。");
	    break;
	case 930000100:
	    cm.sendNext("我们必须消除所有这些怪物的污染！");
	    break;
	case 930000200:
	    cm.sendNext("请把怪物引到中间的#r桥上打死#k,获得稀释的毒素,一共要#r4瓶#k哦！然后到最右边的绿树一瓶瓶的扔出来通过任务!");
	    break;
	case 930000300:
	    cm.warpParty(930000400);
	    break;
	case 930000400:
	    if (cm.haveItem(4001169,10)) {
        cm.warpParty(930000500, 0);
		cm.gainItem(4001169,-10);
	    } else if (!cm.haveItem(2270004)) {
		//cm.gainItem(2270004,10);
		
		cm.warp(930000600);// 恢复原来的就删除这两条上下一条注释
		cm.gainItem(4001163,1);// 恢复原来点就删除这两条与上下一条注释
		
		//cm.sendOk("你或者你的队员拿了10个#v4001169#,请净化这些怪物的好运气!");
	    } else {
		cm.sendOk("我们要净化这些被污染的怪物！让我从他们怪物大理石!");
	    }
	    break;
	case 930000600:
	    cm.sendNext("就是#v4001163#这个！放置在魔岩在坛！");
	    break;
	case 930000700:
            if (cm.canHold(4001198,1)) {
				cm.getPlayer().setBossLog("毒雾组队",0);
	            /*cm.gainGP(+20);//给家族GP点				
                cm.gainExpR(1400000);
				cm.gainItem(4002001,1);
				cm.gainItem(3992025,1);//muyao邮票*/
	        cm.getPlayer().endPartyQuest(1206);
	        cm.removeAll(4001161);
	        cm.removeAll(4001162);
	        cm.removeAll(4001163);
	        cm.removeAll(4001164);
	        cm.removeAll(4001169);
	        cm.removeAll(2270004);
	   // cm.gainItem(4310014,2);
		cm.喇叭(1,"[组队副本] 玩家:<"+cm.getName()+">带领他的队伍完成毒雾组队副本,大家恭喜吧！！！");
		cm.dispose();
        cm.openNpc(2133000,"毒雾组队副本");
	    cm.warp(910001000,0);
	} else {
		cm.getPlayer().dropMessage(5, "请确认你的其他栏有没有满");
	}
	    break;
    }
    cm.dispose();
}



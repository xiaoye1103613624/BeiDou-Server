var mapid,em;

function start() {
   status = -1;
   action(1, 0, 0);
}

function action(mode, type, selection) {

    if (status == 0 && mode == 0) {
        cm.dispose();
        return;
    } else if (mode == 0 && selection == -1) {
		cm.dispose();
        return;
	}
	
    if (mode == 1) {
        status++;
    } else {
        status--;
		cm.dispose();
        return;
    }

    if (status == 0) {
		em = cm.getEventManager("CrimsonPQ");
		if (em == null) {
			cm.sendSimple("事件已被关闭。");
			cm.dispose();
			return;
		}
		if (cm.getParty() == null) {
			cm.warp(803001400,0);
			cm.sendSimple("队伍已解散。");
			cm.dispose();
			return;
		}
			
		var text="这里是绯红要塞，都是一些非常强力的怪物。\r\n";
		mapid=cm.getMapId();
		if(mapid==803000700){
			text="寻找隐藏的入口，并且一定要小心这些绯红卫士。#l\r\n";
			if (cm.isLeader()) {
				if (em.getProperty("glpq1").equals("0")) {
					em.setProperty("glpq1", "1");
				}
			}
		}else if(mapid==803000800){
			text="攻击圣坛解除封印，每个圣坛只能用特定的技能才能解除封印，只有解除了封印开启圣坛信号，才能通往下一关。\r\n";
		}else if(mapid==803000900){
			text="解除圣坛的封印并小心巨大齿轮，巨大齿轮的伤害非常高，一不小心可能就嘎了。#k\r\n";
		}else if(mapid==803001000){
			text="试着去往上方的平台寻找暗影，不过你要注意这些火焰蝙蝠和火柱，它们会阻挡你。#k\r\n";
		}else if(mapid==803001100){
			text="每个职业前往专属的通道，寻找大师遗留的武器碎片来修复大师雕像吧，它们是#b#t4001256:#，#t4001257:#，#t4001258:#，#t4001259:#，#t4001260:#。#k\r\n";
		}else if(mapid==803001200){
			text="击败这些强力的怪物吧，击败后可以前往军械库哦，前往军械库就在我的身后那个门。\r\n";
		}else if(mapid==803001300){
			text="这里是军械库，从这些宝箱中可以获得宝物。\r\n";
		}
		
		text+="\r\n#L0##b离开这里#k#l\r\n";
		cm.sendSimple(text);
    } else if (status == 1) {
        a = selection;
		if(a==0){
			cm.sendYesNo("你确定要离开这里吗？");
		}else{
			cm.dispose();
		}
	} else if (status == 2) {
		if(a==0){
			cm.warp(803001400,0);
			cm.dispose();
		}else{
			cm.dispose();
		}
	} 
}








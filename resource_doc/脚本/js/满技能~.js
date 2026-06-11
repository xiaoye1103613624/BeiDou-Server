
/*

var status = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
			cm.sendYesNo("是否满技能? 使用后技能蛋扣除");
		} else if (status == 1) {
			if(cm.getLevel() < 120){
				cm.sendOk("120级后才能使用该功能");
				cm.dispose();
			}else {
				if (cm.getPlayer().haveItem(2022514,1)){
				
			//GM skill updated by Maple4U Start
			/*cm.teachSkill(9001000,1,1);
			cm.teachSkill(9001001,1,1);
			cm.teachSkill(9001002,1,1);
			cm.teachSkill(9101000,1,1);
			cm.teachSkill(9101001,1,1);
			cm.teachSkill(9101002,1,1);
			cm.teachSkill(9101003,1,1);
			cm.teachSkill(9101004,1,1);
			cm.teachSkill(9101005,1,1);
			cm.teachSkill(9101006,1,1);
			cm.teachSkill(9101007,1,1);
			cm.teachSkill(9101008,1,1);*/
			//GM skill updated by Maple4U End
			//cm.teachSkill(1003,1,1);
			cm.teachSkill(8,1,1);
			cm.teachSkill(1004,1,1);
			cm.teachSkill(1005,1,1);
			

			cm.gainItem(2022514,-1);
			cm.喇叭(3, "玩家 "+cm.getName()+" 使用了一键满技能蛋功能。");
			cm.sendOk("已为您满技能,请换线1次");
			cm.dispose();
			}else {
				cm.sendOk("您没有#v2022514#");
				cm.dispose();
			}
			}
		}
	}
	}
	*/
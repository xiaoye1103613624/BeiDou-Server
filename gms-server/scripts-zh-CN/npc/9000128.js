var status = -1;
var job = 0;
var type = -1;
var skill = [[8, 1004, 1007, 1013],[10000018, 10001004, 10001007,],[20000024, 20001004, 20001007]];

function start(){
	action(1, 0, 0);
}

function action(mode, type ,selection) {
	if(mode == 0 && status == 0) {
		status --;
	} else if(mode == 1) {
		status ++;
	} else {
		cm.dispose();
		return;
	}
	
	if (status == 0) {
		cm.sendYesNo("到达等级30，在我这里可以帮你一键学习 骑宠-群宠-锻造-皇家骑宠-匠人之魂技能");
	} else if (status == 1){
		if(cm.getPlayer().getLevel() < 30){
			cm.sendNext("你的等级没有达到30级");
			cm.dispose();
			return;
		}
			cm.teachSkill(8,1,1);
			cm.teachSkill(1004,1,1);
			cm.teachSkill(1007,1,1);
			cm.teachSkill(1013,1,1);
			cm.teachSkill(1003,1,1);
			cm.teachSkill(20000024, 3, 3);
			cm.teachSkill(20001007, 1, 1);	
			cm.teachSkill(20001003, 1, 1);	
			cm.teachSkill(20001004, 1, 1);
			cm.teachSkill(10001007, 3, 3);
			cm.teachSkill(10000018, 1, 1);
			cm.teachSkill(10001003, 1, 1);
			cm.teachSkill(10001004, 1, 1);		
			cm.sendNext("技能已经学习成功");
			cm.dispose();
		} else {
			cm.dispose();
	   }
	
			
}
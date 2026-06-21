var removeAll = [2020001, 4001122, 4001121, 4001120, 2022132, 2022131, 4001119, 4031437, 4031438, 4001118, 4001117, 4001347, 4001348];
var 功能名称 = "海盗组队副本";

function action(mode, type, selection) {
	for (var i = 0; i < removeAll.length; i++) {
		cm.removeAll(removeAll[i]); //清楚物品所有数目
	}
	cm.gainExp(20000);//个人给经验
	cm.喇叭(1, "[组队副本]玩家:<" + cm.getName() + ">队伍完成海盗组队副本,大家恭喜吧！！！");
	cm.dispose();
	cm.openNpc(2094000, "海盗组队副本");
	cm.warp(910001000);
}



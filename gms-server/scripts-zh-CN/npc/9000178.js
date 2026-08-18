// 测试用，直接触发型

function start() {
    cm.useItem(2022458);
	cm.useItem(2022070);
	cm.useItem(2022071);
	cm.useItem(2022423);
	cm.useItem(2022461);//双倍金币掉落
	cm.useItem(2022463);//双倍物品掉落
	cm.useItem(2022093);
    cm.sendOk("领取新手BUFF，祝你一臂之力");
}

function action(mode, type, selection) {
    cm.dispose();
}
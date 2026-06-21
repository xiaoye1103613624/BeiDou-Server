
var beauty;
var 出现问题的地图1 = 970032500;//你一会把这里的地图代码填上
var 出现问题的地图2 = 970030600;
var 出现问题的地图3 = 970031400;
var 出现问题的地图4 = 970032000;
var 出现问题的地图5 = 970032700;

function start() {
	status = -1;
	action(1, 0, 0);
	

}

function action(mode, type, selection) {
    
	if (mode == -1) {
		cm.dispose();
	} else {
		if (status >= 0 && mode == 0) {
			cm.sendOk("感谢您的光临！");
			cm.dispose();
			return;
		}
		if (mode == 1) {
			status++;
		} else {
			status--;
		}
		if (status == 0) {
	
				cm.sendYesNo("你想现在出去吗?");
				beauty = 0;
		//	}

		} else if (status == 1) {
			switch (beauty) {
				case 0 ://你想现在出去吗?
cm.dispose();
					cm.warp(555000400, 0);
					cm.dispose();
					break;
					
				case 1 ://出现问题的地图1
					if (selection == 0) {
						if (cm.haveMonster(9500361) == false) {//这个地图的BOSSid  你填一下
							cm.getMap().killAllMonsters(true);
							cm.sendOk("恭喜，本地图的小怪已经被清空，请进入下一关！\r\n\r\n");
							cm.dispose();
						} else {
							cm.sendOk("请先消灭 BOSS ");
							cm.dispose();
						}
					} else {
						cm.warp(555000400, 0); //回到起点
						cm.dispose();
					}
					break;
					
				case 2 ://出现问题的地图2
					if (selection == 0) {
						if (cm.haveMonster(9500342) == false) {//根据上面  修改
							cm.getMap().killAllMonsters(true);
							cm.sendOk("恭喜，本地图的小怪已经被清空，请进入下一关！\r\n\r\n");
							cm.dispose();
						} else {
							cm.sendOk("请先消灭 BOSS ");
							cm.dispose();
						}
					} else {
						cm.warp(555000400, 0); //回到起点
						cm.dispose();
					}
					break;
				case 3 ://出现问题的地图2
					if (selection == 0) {
						if (cm.haveMonster(9500350) == false) {//根据上面  修改
							cm.getMap().killAllMonsters(true);
							cm.sendOk("恭喜，本地图的小怪已经被清空，请进入下一关！\r\n\r\n");
							cm.dispose();
						} else {
							cm.sendOk("请先消灭 BOSS ");
							cm.dispose();
						}
					} else {
						cm.warp(555000400, 0); //回到起点
						cm.dispose();
					}
					break;	
	            case 4 ://出现问题的地图2
					if (selection == 0) {
						if (cm.haveMonster(9500356) == false) {//根据上面  修改
							cm.getMap().killAllMonsters(true);
							cm.sendOk("恭喜，本地图的小怪已经被清空，请进入下一关！\r\n\r\n");
							cm.dispose();
						} else {
							cm.sendOk("请先消灭 BOSS ");
							cm.dispose();
						}
					} else {
						cm.warp(555000400, 0); //回到起点
						cm.dispose();
					}
					break;	
                case 5 ://出现问题的地图2
					if (selection == 0) {
						if (cm.haveMonster(9500363) == false) {//根据上面  修改
							cm.getMap().killAllMonsters(true);
							cm.sendOk("恭喜，本地图的小怪已经被清空，请进入下一关！\r\n\r\n");
							cm.dispose();
						} else {
							cm.sendOk("请先消灭 BOSS ");
							cm.dispose();
						}
					} else {
						cm.warp(555000400, 0); //回到起点
						cm.dispose();
					}
					break;						
										
					
					
			}
		}
	}
		
	
    
}
// 定义项目数组，存储每个项目的当前地图ID和重返地图ID
var projects = [
    { 当前地图ID: 252030001, 重返地图ID: 252030100 }, //10万 
	{ 当前地图ID: 211040401, 重返地图ID: 910540200 }, //30万
	{ 当前地图ID: 555000100, 重返地图ID: 555000201 }, //50万 - 2030006
	{ 当前地图ID: 803000505, 重返地图ID: 803200000 }, //300万 - 2030006
	{ 当前地图ID: 209000000, 重返地图ID: 209000002 }  //500万
];

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (status >= 0 && mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        } else {
            status--;
        }

        if (status == 0) {
            var text = "请选择操作：\r\n";
            text += "#b#L0#【点击重返BOSS战场】#l\r\n\r\n";
            cm.sendSimple(text);
        } else if (status == 1) {
            if (selection == 0) {
                // === 新增：每日次数检测 ===
                var dailyKey = "远征副本每日重返";   // 自定义 key，可随意改
                var todayUsed = cm.getPlayer().getBossLog(dailyKey); // 读今日已用次数
                if (todayUsed >= 3) {
                    cm.sendOk("今天你已经重返战场 3 次，请明天再来！");
                    cm.dispose();
                    return;
                }
                var 当前地图ID = cm.getMapId(); // 获取当前地图ID

                // 遍历项目数组，检查每个项目
                for (var i = 0; i < projects.length; i++) {
                    if (当前地图ID == projects[i].当前地图ID) {
                        var 重返地图 = cm.getMap(projects[i].重返地图ID);
                        if (重返地图) {
                            var 怪物列表 = 重返地图.getAllMonstersThreadsafe();
                            var 怪物数量 = 怪物列表.size();
                            if (怪物数量 > 0) {
                                cm.warp(projects[i].重返地图ID, 0); // 传送玩家到指定的重返地图
								// === 新增：记录次数 ===
								cm.getPlayer().setBossLog(dailyKey);
								var dailyKey = "远征副本每日重返";
								var todayUsed = cm.getPlayer().getBossLog(dailyKey);
								cm.getPlayer().dropMessage(5, "远征副本每日重返 " + todayUsed + "/3次。");   //红字私聊提示
                                cm.dispose();
                                return;
                            } else {
                                cm.sendOk("当前频道重返地图没有怪物，无法进行传送。");
                                cm.dispose();
                                return;
                            }
                        } else {
                            cm.sendOk("无法找到重返地图，请检查地图ID是否正确。");
                            cm.dispose();
                            return;
                        }
                    }
                }

                // 如果没有匹配的项目
                cm.sendOk("你当前不在指定的地图，无法进行检测。");
                cm.dispose();
            }
        }
    }
}
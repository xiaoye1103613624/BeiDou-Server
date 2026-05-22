/*
 * ==================
 * 脚本类型: 自动活动管理器
 * 脚本作者：北斗项目组
 * 功能说明：
 *   1. 查看当前可用的活动
 *   2. 快速传送至活动地图
 * ==================
 */

var status = -1;

var events = [
    { name: "椰子活动", mapId: 109080000, desc: "打椰子比赛，获得北斗纪念币", icon: 4000000 },
    { name: "冰地活动", mapId: 109080010, desc: "冰上生存挑战", icon: 4000001 },
    { name: "高地跳跳", mapId: 109040001, desc: "跳跳地图挑战", icon: 4000003 },
    { name: "上楼活动", mapId: 109030001, desc: "楼梯攀爬挑战", icon: 4000000 },
    { name: "滚雪球", mapId: 109060000, desc: "雪球滚动大赛", icon: 4000001 },
    { name: "寻宝活动", mapId: 109010000, desc: "地图寻宝挑战", icon: 4000003 },
    { name: "森林跳跳", mapId: 105040316, desc: "森林跳跳地图", icon: 4000000 },
    { name: "地铁跳跳", mapId: 103000900, desc: "地铁跳跳挑战", icon: 4000001 },
    { name: "火山跳跳", mapId: 280020000, desc: "火山区域跳跳", icon: 4000003 },
    { name: "忍苦跳跳", mapId: 101000100, desc: "忍耐跳跃训练", icon: 4000000 },
    { name: "武陵道场", mapId: 925020000, desc: "武陵道场挑战", icon: 4000010 }
];

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1) {
        cm.dispose();
        return;
    }
    if (mode === 0 && status === 0) {
        cm.dispose();
        return;
    }

    status++;

    if (status === 0) {
        var text = "#e#b=== 活动管理器 ===#k#n\r\n\r\n";
        text += "#d" + "".padStart(30, "——") + "#k\r\n\r\n";
        text += "选择要参与的活动：\r\n\r\n";

        for (var i = 0; i < events.length; i++) {
            var e = events[i];
            text += "#L" + i + "#";
            text += "#i" + e.icon + "# ";
            text += "#b" + e.name + "#k\r\n";
            text += "  #d" + e.desc + "#k";
            text += "#l\r\n";
        }

        cm.sendSimple(text);
    } else if (status === 1) {
        var idx = selection;
        if (idx < 0 || idx >= events.length) {
            cm.dispose();
            return;
        }

        var event = events[idx];
        cm.getPlayer().saveLocationOnWarp();
        cm.warp(event.mapId);
        cm.dispose();
    }
}

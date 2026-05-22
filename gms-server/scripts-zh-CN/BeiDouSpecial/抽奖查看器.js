/*
 * ==================
 * 脚本类型: 抽奖池查看器
 * 脚本作者：北斗项目组
 * 功能说明：
 *   1. 查看各地图抽奖机的内容
 *   2. 显示各抽奖机的奖品列表
 * ==================
 */

var status = -1;

var gachaponMachines = [
    {
        name: "射手村抽奖机",
        mapId: 100000000,
        npcId: 9100100,
        desc: "维多利亚岛风格道具",
        sampleItems: [
            { id: 1032000, name: "灰色条纹帽" },
            { id: 1040000, name: "白色短袖" },
            { id: 1050000, name: "蓝色牛仔裤" },
            { id: 1072000, name: "棕色休闲鞋" },
            { id: 1302000, name: "长剑" }
        ]
    },
    {
        name: "魔法密林抽奖机",
        mapId: 101000000,
        npcId: 9100101,
        desc: "魔法森林风格道具",
        sampleItems: [
            { id: 1032001, name: "蓝色尖帽" },
            { id: 1040001, name: "蓝色法袍" },
            { id: 1382000, name: "短法杖" },
            { id: 1002002, name: "蓝色头巾" }
        ]
    },
    {
        name: "勇士部落抽奖机",
        mapId: 102000000,
        npcId: 9100102,
        desc: "勇士部落风格道具",
        sampleItems: [
            { id: 1002003, name: "红色头巾" },
            { id: 1322000, name: "双刃斧" },
            { id: 1402000, name: "长矛" },
            { id: 1092000, name: "小盾牌" }
        ]
    },
    {
        name: "废弃都市抽奖机",
        mapId: 103000000,
        npcId: 9100103,
        desc: "废弃都市风格道具",
        sampleItems: [
            { id: 1002004, name: "黑色眼罩" },
            { id: 1332000, name: "飞镖" },
            { id: 1472000, name: "拳套" },
            { id: 1040002, name: "黑色短袖" }
        ]
    },
    {
        name: "明珠港抽奖机",
        mapId: 104000000,
        npcId: 9100104,
        desc: "明珠港风格道具",
        sampleItems: [
            { id: 1002005, name: "绿色头巾" },
            { id: 1452000, name: "短弓" },
            { id: 1462000, name: "弩" },
            { id: 1072001, name: "黄色雨鞋" }
        ]
    },
    {
        name: "天空之城抽奖机",
        mapId: 200000000,
        npcId: 9100105,
        desc: "天空之城风格道具",
        sampleItems: [
            { id: 1002006, name: "天空之帽" },
            { id: 1082000, name: "羽毛手套" },
            { id: 1102000, name: "白云披风" }
        ]
    },
    {
        name: "玩具城抽奖机",
        mapId: 220000000,
        npcId: 9100106,
        desc: "玩具城风格道具",
        sampleItems: [
            { id: 1002007, name: "玩具帽" },
            { id: 1050001, name: "玩具衫" },
            { id: 1322001, name: "玩具锤" }
        ]
    },
    {
        name: "新叶城抽奖机",
        mapId: 600000000,
        npcId: 9100107,
        desc: "新叶城风格道具",
        sampleItems: [
            { id: 1002008, name: "未来帽" },
            { id: 1040003, name: "霓虹衫" }
        ]
    },
    {
        name: "神木村抽奖机",
        mapId: 240000000,
        npcId: 9100108,
        desc: "神木村风格道具",
        sampleItems: [
            { id: 1002009, name: "龙鳞帽" },
            { id: 1050002, name: "龙纹战甲" }
        ]
    },
    {
        name: "武陵抽奖机",
        mapId: 250000000,
        npcId: 9100109,
        desc: "武陵风格道具",
        sampleItems: [
            { id: 1002010, name: "功夫帽" },
            { id: 1050003, name: "武道服" }
        ]
    },
    {
        name: "百草堂抽奖机",
        mapId: 251000000,
        npcId: 9100110,
        desc: "百草堂风格道具",
        sampleItems: [
            { id: 1002011, name: "草药帽" },
            { id: 1050004, name: "草衣" }
        ]
    },
    {
        name: "诺特勒斯抽奖机",
        mapId: 120000000,
        npcId: 9100111,
        desc: "海盗风格道具",
        sampleItems: [
            { id: 1002012, name: "海盗帽" },
            { id: 1492000, name: "火枪" }
        ]
    }
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
        var text = "#e#b=== 抽奖池查看器 ===#k#n\r\n\r\n";
        text += "#d" + "".padStart(30, "——") + "#k\r\n\r\n";
        text += "选择抽奖机查看奖品池：\r\n\r\n";

        for (var i = 0; i < gachaponMachines.length; i++) {
            var m = gachaponMachines[i];
            text += "#L" + i + "##b" + m.name + "#k - " + m.desc + "#l\r\n";
        }

        cm.sendSimple(text);
    } else if (status === 1) {
        var machine = gachaponMachines[selection];
        var text = "#e#b=== " + machine.name + " ===#k#n\r\n\r\n";
        text += "位置：地图 #b" + machine.mapId + "#k\r\n";
        text += "NPC ID：#b" + machine.npcId + "#k\r\n";
        text += "描述：#b" + machine.desc + "#k\r\n";
        text += "#d" + "".padStart(30, "——") + "#k\r\n\r\n";
        text += "奖品示例 (完整奖品池请在数据库查看)：\r\n\r\n";

        for (var j = 0; j < machine.sampleItems.length; j++) {
            var item = machine.sampleItems[j];
            text += "#i" + item.id + "# #b" + item.name + "#k (ID:" + item.id + ")\r\n";
        }

        text += "\r\n#b注意：实际抽奖结果将从完整奖品池中随机抽取。#k\r\n";
        text += "每抽一次消耗 #r1000#k 金币(与NPC对话时扣除)。\r\n";

        cm.sendOk(text);
        cm.dispose();
    }
}

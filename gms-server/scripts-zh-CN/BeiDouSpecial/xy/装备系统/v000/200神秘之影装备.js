// ============== 【可自行修改】合成列表 ==============
// 格式：Array(新装备ID, 旧装备ID)
var weapon = new Array(
    Array(1004808, 1004422),  // 战士帽子升级
    Array(1053063, 1052882),  // 战士衣服升级
    Array(1073158, 1073030),  // 战士鞋子升级
    Array(1082695, 1082636),  // 战士手套升级
    Array(1102940, 1102775),  // 战士披风升级

    Array(1004809, 1004423),  // 法师帽子升级
    Array(1053064, 1052887),  // 法师衣服升级
    Array(1073159, 1073032),  // 法师鞋子升级
    Array(1082696, 1082637),  // 法师手套升级
    Array(1102941, 1102794),  // 法师披风升级

    Array(1004810, 1004424),  // 弓手帽子升级
    Array(1053065, 1052888),  // 弓手衣服升级
    Array(1073160, 1073033),  // 弓手鞋子升级
    Array(1082697, 1082638),  // 弓手手套升级
    Array(1102942, 1102795),  // 弓手披风升级

    Array(1004811, 1004425),  // 飞侠帽子升级
    Array(1053066, 1052889),  // 飞侠衣服升级
    Array(1073161, 1073034),  // 飞侠鞋子升级
    Array(1082698, 1082639),  // 飞侠手套升级
    Array(1102943, 1102796),  // 飞侠披风升级

    Array(1004812, 1004426),  // 海盗帽子升级
    Array(1053067, 1052890),  // 海盗衣服升级
    Array(1073162, 1073035),  // 海盗鞋子升级
    Array(1082699, 1082640),  // 海盗手套升级
    Array(1102944, 1102797),  // 海盗披风升级

    Array(1132308, 1132085),  // 腰带升级
    Array(1113225, 1113224),  // 戒指升级	英雄珍贵戒指

    Array(1302343, 1302344),  // 单手剑升级
    Array(1312203, 1312204),  // 单手斧升级
    Array(1322255, 1322256),  // 单手钝器升级
    Array(1332279, 1332280),  // 短刀升级
    Array(1372228, 1372229),  // 短杖升级
    Array(1382265, 1382266),  // 长杖升级
    Array(1402259, 1402260),  // 双手剑升级
    Array(1412181, 1412182),  // 双手战斧升级
    Array(1422189, 1422190),  // 双手钝器升级
    Array(1432218, 1432219),  // 枪升级
    Array(1442274, 1442276),  // 矛升级
    Array(1452257, 1452258),  // 弓升级
    Array(1462243, 1462244),  // 弩升级
    Array(1472265, 1472266),  // 拳套升级
    Array(1482221, 1482222),  // 指节升级
    Array(1492235, 1492236) // 短枪升级

);

// ============== 【可自行修改】所需材料 ==============
var req = [

    [4001126, 2],  // 枫叶
    [4000313, 20],   // 黄金枫叶
    [4000460, 3],   // 时间神殿多多
    [4000461, 3],    // 时间神殿独角兽
    [4000462, 3],    // 时间神殿雷卡
    [1122000, 1],    //黑龙项链
    [4310009, 1],    //狮子王贵族勋章
    [4310010, 1],    //狮子王皇家勋章

    [4020009, 18],    //时间碎片
    [4021009, 18],     // 星石
    [4011007, 18],     // 月石
    [4251202, 18],     //高级五彩水晶
    [4251402, 18],     //高级黑暗水晶

];

// ============== 【可自行修改】消耗金币 ==============
var rem = 10000000;  // 1000万金币

// ============== 【可自行修改】合成概率 ==============
var gailv = 60;  // 100% 成功

// ============== 固定脚本结构（不动）==============
var 金币图标 = "#fUI/UIWindow.img/QuestIcon/7/0#";
var sels;
var status = -1;

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else if (mode == 0) {
        status--;
    } else {
        cm.dispose();
        return;
    }

    if (status == 0) {
        var msg = "";
        msg += "\r\n#d需要:#b ";
        msg += "\r\n";
        for (var ii = 0; ii < req.length; ii++) {
            msg += "#i" + req[ii][0] + ":##z" + req[ii][0] + "#x" + req[ii][1];
            if (ii % 2 == 0 && ii != 0) {
                msg += "\r\n";
            }
        }

        msg += " " + 金币图标 + ":#r一千万#k";
        msg += "\r\n";
        msg += "#g----------------------------------------------\r\n";
        for (var i = 0; i < weapon.length; i++) {
            msg += "#r#L" + i + "#";
            msg += "#i" + weapon[i][1] + ":##z" + weapon[i][1] + "# #b→→→#r #i" + weapon[i][0] + ":##z" + weapon[i][0] + "##l\r\n";
        }
        cm.sendSimple("#d装备合成系统 \t\t成功率: #r#e" + gailv + "%#d#n\r\n温馨提示：失败材料不返还，请选择：" + msg + "");
    } else if (status == 1) {
        sels = selection;
        if (!cm.canHold(weapon[sels][0])) {
            cm.sendOk("#r背包空间不足,固有装备只能持有一个。");
            cm.dispose();
            return;
        }
        for (var i = 0; i < req.length; i++) {
            if (!cm.haveItem(req[i][0], req[i][1])) {
                cm.sendOk("#b缺少材料：#r#i" + req[i][0] + ":##z" + req[i][0] + "#x" + req[i][1] + "");
                cm.dispose();
                return;
            }
        }
        if (!cm.haveItem(weapon[sels][1], 1)) {
            cm.sendOk("#b缺少旧装备：#r#i" + weapon[sels][1] + ":##z" + weapon[sels][1] + "#");
            cm.dispose();
            return;
        }
        if (cm.getMeso() < rem) {
            cm.sendOk("#b金币不足，需要 #r" + rem + " 金币");
            cm.dispose();
            return;
        }
        cm.sendYesNo("#b确定要合成：#r #i" + weapon[sels][0] + "##z" + weapon[sels][0] + "# 吗？\r\n");

    } else if (status == 2) {
        var s1 = Math.floor(Math.random() * 100 + 1);
        if (s1 <= gailv) {
            for (var i = 0; i < req.length; i++) cm.gainItem(req[i][0], -req[i][1]);
            cm.gainMeso(-rem);
            cm.gainItem(weapon[sels][1], -1);
            cm.gainItem(weapon[sels][0], 1);
            cm.sendOk("#b合成成功！获得：#i" + weapon[sels][0] + "##z" + weapon[sels][0] + "#");
        } else {
            for (var i = 0; i < req.length; i++) cm.gainItem(req[i][0], -req[i][1]);
            cm.gainMeso(-rem);
            cm.sendOk("#r合成失败，材料已消失。");
        }
        cm.dispose();

    } else {
        cm.dispose();
    }
}
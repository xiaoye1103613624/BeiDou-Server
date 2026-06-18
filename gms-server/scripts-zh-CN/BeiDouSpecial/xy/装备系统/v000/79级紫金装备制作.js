// ============== 【可自行修改】合成列表 ==============
// 格式：Array(新装备ID, 旧装备ID)
var weapon = new Array(
	Array(1003529, 1003242),  // 帽子升级
	Array(1052457, 1052357),  // 衣服升级
	Array(1072660, 1072521),  // 鞋子升级
	Array(1082433, 1082314),  // 手套升级
	Array(1102394, 1102294),  // 披风升级
	Array(1132151, 1132092),  // 腰带升级
	Array(1113035, 1112422),  // 戒指升级

	Array(1302212, 1302169),  // 单手剑升级
	Array(1312114, 1312068),  // 单手斧升级
	Array(1322154, 1322099),  // 单手钝器升级
	Array(1332186, 1332144),  // 短刀升级
	Array(1372131, 1372096),  // 短杖升级
	Array(1382160, 1382120),  // 长杖升级（45级宝石长杖，若有bug可改为其他低级长杖）
	Array(1402145, 1402106),  // 双手剑升级
	Array(1412102, 1412067),  // 双手战斧升级
	Array(1422105, 1422069),  // 双手钝器升级
	Array(1432135, 1432095),  // 枪升级
	Array(1442173, 1442132),  // 矛升级
	Array(1452165, 1452125),  // 弓升级
	Array(1462156, 1462113),  // 弩升级
	Array(1472177, 1472136),  // 拳套升级
	Array(1482138, 1482098),  // 指节升级
	Array(1492138, 1492097)   // 短枪升级
);

// ============== 【可自行修改】所需材料 ==============
var req = [
	[4001126, 300],  // 枫叶
	[4000313, 3],    // 黄金枫叶
	[2210006, 2],    // 彩虹色蜗牛壳儿
	[4000040, 2],    // 蘑菇王芽孢
	[4000176, 2],    // 毒菇
	[4021009, 1],    // 星石
	[4011007, 1]     // 月石
];

// ============== 【可自行修改】消耗金币 ==============
var rem = 1000000;  // 100万金币

// ============== 【可自行修改】合成概率 ==============
var gailv = 90;

// ============== 固定脚本结构 ==============
var 金币图标 = "#fUI/UIWindow.img/QuestIcon/7/0#";
var 返回图标 = "#fUI/UIWindow.img/itemSearch/BtBack/normal/0#";
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
        var msg = "\r\n#d需要:#b \r\n";
        for (var ii = 0; ii < req.length; ii++) {
            msg += "#i" + req[ii][0] + ":##z" + req[ii][0] + "#x" + req[ii][1];
            if (ii % 2 == 0 && ii != 0) msg += "\r\n";
        }
        msg += " " + 金币图标 + ":#r1百万#k\r\n";
        msg += "#g----------------------------------------------\r\n";
        for (var i = 0; i < weapon.length; i++) {
            msg += "#r#L" + i + "##i" + weapon[i][1] + ":##z" + weapon[i][1] + "# #b→→→#r #i" + weapon[i][0] + ":##z" + weapon[i][0] + "##l\r\n";
        }
        msg += "#g----------------------------------------------\r\n";
        msg += "#L999#" + 返回图标 + "#l\r\n";
        cm.sendSimple("#d装备合成系统 \t\t成功率: #r#e" + gailv + "%#d#n\r\n温馨提示：失败材料不返还，请选择：" + msg);

    } else if (status == 1) {
        if (selection == 999) { cm.dispose(); cm.openNpc(9900001, "xy/装备系统/v000/套装制作升级"); return; }
        sels = selection;
        if (!cm.canHold(weapon[sels][0])) {
            cm.sendOk("#r背包空间不足，固有装备只能持有一个。");
            cm.dispose();
            return;
        }
        for (var i = 0; i < req.length; i++) {
            if (!cm.haveItem(req[i][0], req[i][1])) {
                cm.sendOk("#b缺少材料：#r#i" + req[i][0] + ":##z" + req[i][0] + "#x" + req[i][1]);
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

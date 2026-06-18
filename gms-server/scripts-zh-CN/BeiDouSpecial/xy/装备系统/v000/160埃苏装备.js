// ============== 【可自行修改】合成列表 ==============
// 格式：Array(新装备ID, 旧装备ID)
var weapon = new Array(
	Array(1004422, 1003172),  // 战士帽子升级
	Array(1052882, 1052314),  // 战士衣服升级
	Array(1073030, 1072485),  // 战士鞋子升级
	Array(1082636, 1082295),  // 战士手套升级
	Array(1102775, 1102275),  // 战士披风升级
	
	Array(1004423, 1003173),  // 法师帽子升级
	Array(1052887, 1052315),  // 法师衣服升级
	Array(1073032, 1072486),  // 法师鞋子升级
	Array(1082637, 1082296),  // 法师手套升级
	Array(1102794, 1102276),  // 法师披风升级
	
	Array(1004424, 1003174),  // 弓手帽子升级
	Array(1052888, 1052316),  // 弓手衣服升级
	Array(1073033, 1072487),  // 弓手鞋子升级
	Array(1082638, 1082297),  // 弓手手套升级
	Array(1102795, 1102277),  // 弓手披风升级
	
	Array(1004425, 1003175),  // 飞侠帽子升级
	Array(1052889, 1052317),  // 飞侠衣服升级
	Array(1073034, 1072488),  // 飞侠鞋子升级
	Array(1082639, 1082298),  // 飞侠手套升级
	Array(1102796, 1102278),  // 飞侠披风升级

	Array(1004426, 1003176),  // 海盗帽子升级
	Array(1052890, 1052318),  // 海盗衣服升级
	Array(1073035, 1072489),  // 海盗鞋子升级
	Array(1082640, 1082299),  // 海盗手套升级
	Array(1102797, 1102279),  // 海盗披风升级
	
	Array(1132085, 1132311),  // 腰带升级	
	Array(1113224, 1113223),  // 戒指升级
	
	Array(1302344, 1302285),  // 单手剑升级
	Array(1312204, 1312162),  // 单手斧升级
	Array(1322256, 1322213),  // 单手钝器升级	
	Array(1332280, 1332235),  // 短刀升级	
	Array(1372229, 1372186),  // 短杖升级
	Array(1382266, 1382220),  // 长杖升级	
	Array(1402260, 1402204),  // 双手剑升级
	Array(1412182, 1412144),  // 双手战斧升级
	Array(1422190, 1422149),  // 双手钝器升级	
	Array(1432219, 1432176),  // 枪升级
	Array(1442276, 1442232),  // 矛升级
	Array(1452258, 1452214),  // 弓升级
	Array(1462244, 1462202),  // 弩升级
	Array(1472266, 1472223),  // 拳套升级
	Array(1482222, 1482177),  // 指节升级
	Array(1492236, 1492188)   // 短枪升级
);

// ============== 【可自行修改】所需材料 ==============
var req = [

	[4001126, 2000],  // 枫叶
	[4000313, 15],   // 黄金枫叶
	[4001242, 1],   // 心疤狮
	[4001241, 1],    // 暴力熊
	[4001083, 1],    // 扎昆
	[4001085, 1],    // 鱼王
	[4001084, 1],    // 闹钟
	[4021009, 12],     // 星石
	[4011007, 12],     // 月石
	[4250802, 8],     //高级力量水晶
	[4251102, 8],     //高级敏捷水晶
	[4250902, 8],     //高级智慧水晶
	[4251002, 8],     //高级幸运水晶
];

// ============== 【可自行修改】消耗金币 ==============
var rem = 8000000;  // 800万金币

// ============== 【可自行修改】合成概率 ==============
var gailv = 65;  // 100% 成功

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
		
		msg += " "+金币图标+":#r8百万#k";
        msg += "\r\n";
        msg += "#g----------------------------------------------\r\n";
        for (var i = 0; i < weapon.length; i++) {
            msg += "#r#L" + i + "#";
            msg += "#i" + weapon[i][1] + ":##z" + weapon[i][1] + "# #b→→→#r #i" + weapon[i][0] + ":##z" + weapon[i][0] + "##l\r\n";
        }
        cm.sendSimple("#d装备合成系统 \t\t成功率: #r#e"+gailv+"%#d#n\r\n温馨提示：失败材料不返还，请选择：" + msg + "");
    } 
    else if (status == 1) {
        sels = selection;
        if (!cm.canHold(weapon[sels][0])) {
            cm.sendOk("#r背包空间不足，固有装备只能持有一个。");
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
		if (!cm.haveItem(weapon[sels][1],1)) {
            cm.sendOk("#b缺少旧装备：#r#i" + weapon[sels][1] + ":##z" + weapon[sels][1] + "#");
            cm.dispose();
            return;
        }
		if(cm.getMeso() < rem){
			cm.sendOk("#b金币不足，需要 #r" + rem + " 金币");
            cm.dispose();
			return;
		}
        cm.sendYesNo("#b确定要合成：#r #i" + weapon[sels][0] + "#吗? \r\n");
    } 
    else if (status == 2) {
		s1 = Math.floor(Math.random() * 100 + 1);
		if(s1 <= gailv){
			// 扣除材料
			for (var i = 0; i < req.length; i++) {
				cm.gainItem(req[i][0], -req[i][1]);
			}
			// 扣除金币
			cm.gainMeso(-rem);
			// 扣除旧装备
			cm.gainItem(weapon[sels][1], -1);
			// 发放新装备
			cm.gainItem(weapon[sels][0], 1);

			cm.sendOk("#b合成成功！获得：#i" + weapon[sels][0] + "#");
			cm.dispose();
		} 
		else {
			// 失败只扣材料不扣装备
			for (var i = 0; i < req.length; i++) {
				cm.gainItem(req[i][0], -req[i][1]);
			}
			cm.gainMeso(-rem);
			cm.sendOk("#r合成失败，材料已消失。");
			cm.dispose();
		}
    } 
    else {
		//cm.sendOk("#r好的，谢谢惠顾，欢迎下次光临！");
        cm.dispose();
    }
}
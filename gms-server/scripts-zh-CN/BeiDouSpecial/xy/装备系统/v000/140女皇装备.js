// ============== 【可自行修改】合成列表 ==============
// 格式：Array(新装备ID, 旧装备ID)
var weapon = new Array(
	Array(1003172, 1003946),  // 战士帽子升级
	Array(1052314, 1052647),  // 战士衣服升级
	Array(1072485, 1072853),  // 战士鞋子升级
	Array(1082295, 1082540),  // 战士手套升级
	Array(1102275, 1102612),  // 战士披风升级
	
	Array(1003173, 1003946),  // 法师帽子升级
	Array(1052315, 1052647),  // 法师衣服升级
	Array(1072486, 1072853),  // 法师鞋子升级
	Array(1082296, 1082540),  // 法师手套升级
	Array(1102276, 1102612),  // 法师披风升级
	
	Array(1003174, 1003946),  // 弓手帽子升级
	Array(1052316, 1052647),  // 弓手衣服升级
	Array(1072487, 1072853),  // 弓手鞋子升级
	Array(1082297, 1082540),  // 弓手手套升级
	Array(1102277, 1102612),  // 弓手披风升级
	
	Array(1003175, 1003946),  // 飞侠帽子升级
	Array(1052317, 1052647),  // 飞侠衣服升级
	Array(1072488, 1072853),  // 飞侠鞋子升级
	Array(1082298, 1082540),  // 飞侠手套升级
	Array(1102278, 1102612),  // 飞侠披风升级

	Array(1003176, 1003946),  // 海盗帽子升级
	Array(1052318, 1052647),  // 海盗衣服升级
	Array(1072489, 1072853),  // 海盗鞋子升级
	Array(1082299, 1082540),  // 海盗手套升级
	Array(1102279, 1102612),  // 海盗披风升级
	
	Array(1132311, 1132242),  // 腰带升级	
	Array(1113223, 1113222),  // 戒指升级
	
	Array(1302285, 1302289),  // 单手剑升级
	Array(1312162, 1312165),  // 单手斧升级
	Array(1322213, 1322215),  // 单手钝器升级	
	Array(1332235, 1332238),  // 短刀升级	
	Array(1372186, 1372188),  // 短杖升级
	Array(1382220, 1382222),  // 长杖升级	
	Array(1402204, 1402210),  // 双手剑升级
	Array(1412144, 1412147),  // 双手战斧升级
	Array(1422149, 1422152),  // 双手钝器升级	
	Array(1432176, 1432178),  // 枪升级
	Array(1442232, 1442234),  // 矛升级
	Array(1452214, 1452216),  // 弓升级
	Array(1462202, 1462204),  // 弩升级
	Array(1472223, 1472226),  // 拳套升级
	Array(1482177, 1482179),  // 指节升级
	Array(1492188, 1492190)   // 短枪升级
);

// ============== 【可自行修改】所需材料 ==============
var req = [

	[4001126, 1000],  // 枫叶
	[4000313, 10],   // 黄金枫叶
	[4000235, 3],   // 喷火龙尾巴	
	[4000243, 3],    // 天鹰的角

	[4021009, 5],     // 星石
	[4011007, 5],     // 月石
	[4250801, 8],     //中级力量水晶
	[4251101, 8],     //中级敏捷水晶
	[4250901, 8],     //中级智慧水晶
	[4251001, 8],     //中级幸运水晶
];

// ============== 【可自行修改】消耗金币 ==============
var rem = 5000000;  // 500万金币

// ============== 【可自行修改】合成概率 ==============
var gailv = 70;  // 100% 成功

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
		
		msg += " "+金币图标+":#r5百万#k";
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
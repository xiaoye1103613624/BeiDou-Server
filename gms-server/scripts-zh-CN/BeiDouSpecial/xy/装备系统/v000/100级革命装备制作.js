// ============== 【可自行修改】合成列表 ==============
// 格式：Array(新装备ID, 旧装备ID)
var weapon = new Array(
	Array(1003946, 1003529),  // 帽子升级
	Array(1052647, 1052457),  // 衣服升级
	Array(1072853, 1072660),  // 鞋子升级
	Array(1082540, 1082433),  // 手套升级
	Array(1102612, 1102394),  // 披风升级
	Array(1132242, 1132151),  // 腰带升级	
	Array(1113222, 1113035),  // 戒指升级
	
	Array(1302289, 1302212),  // 单手剑升级
	Array(1312165, 1312114),  // 单手斧升级
	Array(1322215, 1322154),  // 单手钝器升级	
	Array(1332238, 1332186),  // 短刀升级	
	Array(1372188, 1372131),  // 短杖升级
	Array(1382222, 1382160),  // 长杖升级	
	Array(1402210, 1382160),  // 双手剑升级
	Array(1412147, 1412102),  // 双手战斧升级
	Array(1422152, 1422105),  // 双手钝器升级	
	Array(1432178, 1432135),  // 枪升级
	Array(1442234, 1442173),  // 矛升级
	Array(1452216, 1452165),  // 弓升级
	Array(1462204, 1462156),  // 弩升级
	Array(1472226, 1472177),  // 拳套升级
	Array(1482179, 1482138),  // 指节升级
	Array(1492190, 1492138)   // 短枪升级
);

// ============== 【可自行修改】所需材料 ==============
var req = [

	[4001126, 500],  // 枫叶
	[4000313, 5],   // 黄金枫叶
	[4000082, 10],   // 僵尸弄丢的金齿	
	[4032474, 2],    // 歇尔夫的珍珠
	[4000124, 2],     // 战甲吹泡泡鱼的内存卡
	[4021009, 2],     // 星石
	[4011007, 2],     // 月石
	[4005000, 8],     //力量水晶
	[4005002, 8],     //敏捷水晶
	[4005001, 8],     //智慧水晶
	[4005003, 8],     //幸运水晶
];

// ============== 【可自行修改】消耗金币 ==============
var rem = 3000000;  // 300万金币

// ============== 【可自行修改】合成概率 ==============
var gailv = 80;  // 100% 成功

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
		
		msg += " "+金币图标+":#r3百万#k";
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
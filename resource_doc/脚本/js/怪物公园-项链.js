var 金币图标 = "#fUI/UIWindow/QuestIcon/7/0#";
var 广播 = "#fUI/CN_Chat/ChattingRoom/BtVolUp/0/normal/0#";
var weapon = {
	"1122163": { // 合成物品ID
        materials: [ // 合成所需的材料
            [1122162, 1], // 前一个装备ID和数量
            [4310020, 40], // 公共材料1
            [4170016, 5], // 公共材料2
            ["金币", 50000000] // 所需金币
        ],
        attributes: [ // 合成物品的属性
		//1是升级次数、2是锁、3456是四维、9 10是双攻
            10, 1, 10, 10, 10, 10, 0, 0, 10, 10, 0, 0, 0, 0, 0 // 示例属性值
        ],
        minXianLevel: 0 // 达到出窍（仙级6）才能看见
    },
    "1122164": {
        materials: [
            [1122163, 1],
            [4310020, 50],
            [4170016, 10],
            ["金币", 60000000]
        ],
        attributes: [
            10, 1, 20, 20, 20, 20, 0, 0, 20, 20, 0, 0, 0, 0, 0 // 示例属性值
        ],
        minXianLevel: 0 // 达到出窍（仙级6）才能看见
    },
    "1122165": {
        materials: [
            [1122164, 1],
            [4310020, 60],
            [4170016, 15],
            ["金币", 70000000]
        ],
        attributes: [
            10, 1, 40, 40, 40, 40, 0, 0, 40, 40, 0, 0, 0, 0, 0, 0
        ],
        minXianLevel: 0 // 达到出窍（仙级6）才能看见
    },
	"1122166": {
        materials: [
            [1122165, 1],
            [4310020, 70],
            [4170016, 20],
            ["金币", 80000000]
        ],
        attributes: [
            10, 1, 60, 60, 60, 60, 0, 0, 60, 60, 0, 0, 0, 0, 0, 0
        ],
        minXianLevel: 1 // 达到出窍（仙级6）才能看见
    },
	"1122167": {
        materials: [
            [1122166, 1],
            [4310020, 80],
            [4170016, 25],
            ["金币", 90000000]
        ],
        attributes: [
            10, 1, 80, 80, 80, 80, 0, 0, 80, 80, 0, 0, 0, 0, 0, 0
        ],
        minXianLevel: 1 // 达到出窍（仙级6）才能看见
    },
	"1122168": {
        materials: [
            [1122167, 1],
            [4310020, 90],
            [4170016, 30],
            ["金币", 100000000]
        ],
        attributes: [
            10, 1, 100, 100, 100, 100, 0, 0, 100, 100, 0, 0, 0, 0, 0, 0
        ],
        minXianLevel: 1 // 达到出窍（仙级6）才能看见
    },  
	"1122169": {
        materials: [
            [1122168, 1],
            [4310020, 100],
            [4170016, 35],
            ["金币", 200000000]
        ],
        attributes: [
            10, 1, 150, 150, 150, 150, 0, 0, 150, 150, 0, 0, 0, 0, 0, 0
        ],
        minXianLevel: 10 // 达到出窍（仙级6）才能看见
    }
/*	"1122170": {
        materials: [
            [1122169, 1],
            [4310020, 200],
            [4170016, 40],
            ["金币", 300000000]
        ],
        attributes: [
            10, 1, 250, 250, 250, 250, 0, 0, 250, 250, 0, 0, 0, 0, 0, 0
        ],
        minXianLevel: 20 // 达到出窍（仙级6）才能看见
    }
	"1122171": {
        materials: [
            [1122170, 1],
            [4310020, 300],
            [4170016, 50],
            ["金币", 500000000]
        ],
        attributes: [
            10, 1, 500, 500, 500, 500, 0, 0, 500, 500, 0, 0, 0, 0, 0, 0
        ],
        minXianLevel: 20 // 达到出窍（仙级6）才能看见
    },
    "1122172": {
        materials: [
            [1122171, 1],
            [4310020, 400],
            [4170016, 100],
            ["金币", 1000000000]
        ],
        attributes: [
            10, 1, 1000, 1000, 1000, 1000, 0, 0, 1000, 1000, 0, 0, 0, 0, 0, 0
        ],
        minXianLevel: 20 // 达到出窍（仙级6）才能看见
    },
    "1122173": {
        materials: [
            [1122172, 1],
            [4310020, 500],
            [4170016, 200],
            ["金币", 1000000000]
        ],
        attributes: [
            10, 1, 1500, 1500, 1500, 1500, 0, 0, 1500, 1500, 0, 0, 0, 0, 0, 0
        ],
        minXianLevel: 20 // 达到出窍（仙级6）才能看见
    },
	*/
};

var gailv = 40; // 合成概率（百分比）
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
        msg += " #g┏━#r冒险岛提示#g━━━━━━━━━━━━━━━━━━┓\r\n";
        msg += "\t#d" + 广播 + "  欢迎来到 [#r副本饰品合成中心#d]\r\n";
        msg += "\t#d" + 广播 + ": 目前合成概率为: #r#e" + gailv + "%#d#n\r\n";
        msg += "\t#d" + 广播 + ": #d失败扣除所有材料！#r（保留主材料） #d#n\r\n";
        msg += " #g┗━━━━━━━━━━━━━━━━━━━━━━┛#d\r\n";
        var 当前仙级 = getxmwnjlc("XM飞升系统_仙级");
        for (var key in weapon) {
            if (当前仙级 >= weapon[key].minXianLevel) {
                var 加多少全属性 = weapon[key].attributes[3]; // 获取全属性的值--取的第三个数字
                msg += "#r#L" + key + "#";
                msg += "#b合成#r #i" + key + ":##z" + key + "#[全属性+" + 加多少全属性 + "]#l\r\n\r\n"; // 显示全属性值
                msg += "#d需要以下物品:\r\n";
                for (var i = 0; i < weapon[key].materials.length; i++) {
                    if (weapon[key].materials[i][0] == "金币") {
                        msg += "#z" + weapon[key].materials[i][0] + "#" + 金币图标 + " x " + weapon[key].materials[i][1] + "\r\n\r\n";
                    } else {
                        msg += "#i" + weapon[key].materials[i][0] + ":# #z" + weapon[key].materials[i][0] + "# x " + weapon[key].materials[i][1] + "\r\n";
                    }
                }
                msg += "#g-------------------------------------------------\r\n";
            }
        }
        cm.sendSimple(msg);
    } else if (status == 1) {
        sels = selection;
        if (!cm.canHold(sels)) {
            cm.sendNext("#r背包空间不足");
            cm.dispose();
            return;
        }
        for (var i = 0; i < weapon[sels].materials.length; i++) {
            if (weapon[sels].materials[i][0] == "金币") {
                if (cm.getMeso() < weapon[sels].materials[i][1]) {
                    cm.sendNext("#b身上没有足够的金币");
                    cm.dispose();
                    return;
                }
            } else {
                if (!cm.haveItem(weapon[sels].materials[i][0], weapon[sels].materials[i][1])) {
                    cm.sendNext("#b身上没有#r#i" + weapon[sels].materials[i][0] + ":##z" + weapon[sels].materials[i][0] + "#x" + weapon[sels].materials[i][1] + "");
                    cm.dispose();
                    return;
                }
            }
        }
        cm.sendYesNo("#b是否要兑换#r #i" + sels + "#? \r\n");
    } else if (status == 2) {
        var s1 = Math.floor(Math.random() * (100 - 1) + 1);
        if (s1 <= gailv) {
            for (var i = 0; i < weapon[sels].materials.length; i++) {
                if (weapon[sels].materials[i][0] == "金币") {
                    cm.gainMeso(-weapon[sels].materials[i][1]);
                } else {
                    cm.gainItem(weapon[sels].materials[i][0], -weapon[sels].materials[i][1]);
                }
            }
            // 合成成功，赋予属性
            cm.gainItem(
                sels,
                weapon[sels].attributes[0], weapon[sels].attributes[1], weapon[sels].attributes[2],
                weapon[sels].attributes[3], weapon[sels].attributes[4], weapon[sels].attributes[5],
                weapon[sels].attributes[6], weapon[sels].attributes[7], weapon[sels].attributes[8],
                weapon[sels].attributes[9], weapon[sels].attributes[10], weapon[sels].attributes[11],
                weapon[sels].attributes[12], weapon[sels].attributes[13], weapon[sels].attributes[14],
                weapon[sels].attributes[15]
            );
            cm.道具喇叭(sels," [" + cm.getChar().getName() + "] 在怪物公园成功合成！恭喜！恭喜！");
            cm.sendNext("#b已经兑换了 #i" + sels + "#");
        } else {
            for (var i = 1; i < weapon[sels].materials.length; i++) { // 从第二个材料开始扣除 var i = 0;就是扣除全部
                if (weapon[sels].materials[i][0] == "金币") {
                    cm.gainMeso(-weapon[sels].materials[i][1]);
                } else {
                    cm.gainItem(weapon[sels].materials[i][0], -weapon[sels].materials[i][1]);
                }
            }
            cm.sendNext("#b合成失败,你投入的材料消失了~!");
            cm.喇叭(2," [" + cm.getChar().getName() + "] 在怪物公园成功合成眼镜失败了，真惨啊！");
        }
        cm.dispose();
    } else {
        cm.dispose();
    }
}

function getConnection() {
    return cm.getConnection();
}

function getxmwnjljsc(jiluid) {
    var xmsjfh = 0;
    zhjsid = cm.getPlayer().getId();
    var conn = getConnection();
    var sql = "SELECT * FROM xmwnjl WHERE characterid = " + zhjsid + " AND bossid = '" + jiluid + "' ;";
    var pstmt = conn.prepareStatement(sql);
    var result = pstmt.executeQuery();		
    if (result.next()) {
        xmsjfh = result.getInt("count");
    } 
    result.close();
    pstmt.close();
    conn.close();
    return xmsjfh;
}

function getxmwnjlc(log) {
    return getxmwnjljsc(log);
}
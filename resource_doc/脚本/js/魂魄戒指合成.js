var 提示 = "#fUI/CN_Chat/ChattingRoom/BtVolUp/0/normal/0#";
//T4 第一个合成的物品  第二个需求T5d的武器 第3456是思维 78 是攻击魔法
var weapon = new Array(
Array(1115204,1115203,40,40,40,40,40,40),
Array(1115205,1115204,50,50,50,50,50,50)
//Array(1115206,1115205,60,60,60,60,60,60),
//Array(1115207,1115206,70,70,70,70,70,70),
//Array(1115208,1115207,80,80,80,80,80,80),
//Array(1115209,1115208,90,90,90,90,90,90),
//Array(1115210,1115209,100,100,100,100,100,100)
/*
Array(1115211,1115210,550,550,550,550,550,550),
Array(1115212,1115211,600,600,600,600,600,600),
Array(1115213,1115212,650,650,650,650,650,650),
Array(1115214,1115213,700,700,700,700,700,700),
Array(1115215,1115214,800,800,800,800,800,800)

Array(1115216,1115215,850,850,850,850,850,850),
Array(1115217,1115216,900,900,900,900,900,900),
Array(1115218,1115217,950,950,950,950,950,950),
Array(1115219,1115218,1000,1000,1000,1000,1000,1000),
Array(1115220,1115219,1100,1100,1100,1100,1100,1100),

Array(1115221,1115220,1200,1200,1200,1200,1200,1200),
Array(1115222,1115221,1300,1300,1300,1300,1300,1300),
Array(1115223,1115222,1400,1400,1400,1400,1400,1400),
Array(1115224,1115223,1500,1500,1500,1500,1500,1500),
Array(1115225,1115224,1600,1600,1600,1600,1600,1600),

Array(1115226,1115225,1700,1700,1700,1700,1700,1700),
Array(1115227,1115226,1800,1800,1800,1800,1800,1800),
Array(1115228,1115227,1900,1900,1900,1900,1900,1900),
Array(1115229,1115228,2000,2000,2000,2000,2000,2000),
Array(1115230,1115229,2100,2100,2100,2100,2100,2100),

Array(1115231,1115230,2200,2200,2200,2200,2200,2200),
Array(1115232,1115231,2400,2400,2400,2400,2400,2400),
Array(1115233,1115232,2600,2600,2600,2600,2600,2600),
Array(1115234,1115233,3000,3000,3000,3000,3000,3000)*/

);
//需求的材料和数量
var req = [
  /*[3605015, 10],*/
  [4021009, 5],
  [4011007, 5],
  [4000464, 1],
  [4000313, 99],
  [4001126, 999]
];
//需求的金币
var rem = 100000000;
var gailv = 80;//输入百分之几不要输入百分号只要数字在里面就行了!!
var sels;
var status = -1;
/* 
测试概率代码 
		var aa = "";
		for (var i = 0; i < 1000; i++) {
			s1 = Math.floor(Math.random() * (100 - 1) + 1);
			if(s1 <= gailv){
				aa +="#g"+s1+"#k ";
			} else {
				aa +="#r"+s1+"#k ";
			}
		}
		cm.sendOk(aa);
*/
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
        msg += "\r\n\r\n#r"+提示+" 进阶材料:#b ";
        msg += "\r\n";
        for (var ii = 0; ii < req.length; ii++) {
            msg += " #i" + req[ii][0] + "# * #r" + req[ii][1] + "#b个 " ;
            if (ii % 2 == 0 && ii !=0) {
                msg += "\r\n";
            }
        }
		msg += ""+提示+"#k #b金币消耗：#r"+rem+" #b金币\r\n";
		msg += "#g-----------------------------------------------------\r\n";
		msg += ""+提示+"#k #b当前拥有：#r"+cm.getMeso()+" #b金币\r\n";
        msg += ""+提示+"#k #b进阶说明：#r成功率#r"+gailv+"%，进阶失败材料将会消失\r\n";
		//msg += "#b每次升级的成功率：#r"+gailv+"%，升级失败投入的材料将会消失\r\n";
        msg += "#g-----------------------------------------------------\r\n";
        for (var i = 0; i < weapon.length; i++) {
            msg += "#r#L" + i + "#";
            msg += "#i" + weapon[i][1] + ":##b进阶#r#i" + weapon[i][0] + "##k:全属性+ #r" + weapon[i][3] + "#k#l\r\n";
        }
       cm.sendSimple("\t\t\t\t#r#e魂魄戒指进阶中心#d#n\r\n" + msg + "");
    } else if (status == 1) {
        sels = selection;
        if (!cm.canHold(weapon[sels][0])) {
            cm.sendNext("#r背包空间不足");
            cm.dispose();
            return;
        }
        for (var i = 0; i < req.length; i++) {
            if (!cm.haveItem(req[i][0], req[i][1])) {
                cm.sendNext("#b身上没有#r#i" + req[i][0] + ":##z" + req[i][0] + "#x" + req[i][1] + "");
                cm.dispose();
                return;
            }
        }
		if (!cm.haveItem(weapon[sels][1],1)) {
            cm.sendNext("#b身上没有#r#i" + weapon[sels][1] + ":##z" + weapon[sels][1] + "#");
            cm.dispose();
            return;
        }
		if(cm.getMeso() < rem){
			cm.sendNext("#b身上没有#r"+rem+"金币");
            cm.dispose();
            return;
		}
        cm.sendYesNo("#b是否要兑换#r #i" + weapon[sels] + ":#? \r\n");
    } else if (status == 2) {
		s1 = Math.floor(Math.random() * (100 - 1) + 1);
		if(s1 <= gailv){
			for (var i = 0; i < req.length; i++) {
				cm.gainItem(req[i][0], -req[i][1]);
			}
			cm.gainMeso(-rem);
			cm.gainItem(weapon[sels][1],-1);
			cm.gainItem(weapon[sels][0],weapon[sels][2],weapon[sels][3],weapon[sels][4],weapon[sels][5],0,0,weapon[sels][6],weapon[sels][7],0,0,0,0,0,0);
			
			cm.sendNext("#b已经兑换了 #i" + weapon[sels] + "#");
			var itemName = cm.getItemName(weapon[sels][0]);
			var itemName1 = cm.getItemName(weapon[sels][1]);
			// 发送喇叭广播
			cm.喇叭(2, "" + cm.getName() + ":成功将 [" + itemName1 + "] 升级为 [" + itemName + "] ！");
			cm.dispose();
		} else {
			for (var i = 0; i < req.length; i++) {
				cm.gainItem(req[i][0], -req[i][1]);
			}
			cm.gainMeso(-rem);
			//cm.gainItem(weapon[sels][1],-2);
			cm.sendNext("#b合成失败,你投入的材料消失了~!");
			var itemName = cm.getItemName(weapon[sels][0]);
			var itemName1 = cm.getItemName(weapon[sels][1]);
			cm.喇叭(3, "" + cm.getName() + ": 升级 [" + itemName + "] 失败了，好惨啊！");
			cm.dispose();
		}
    } else {
        //cm.sendNext("#r发生错误: mode : " + mode + " status : " + status);
        cm.dispose();
    }
}
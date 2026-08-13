var weapon =[
1003622,//	布莱克缤帽
1022232,//	布莱克缤瞳印
1052527,//	布莱克缤大衣
1302070,//布莱克缤单手剑
1472205, //布莱克缤拳套	
1492170, //布莱克缤短枪	
1402185, //布莱克缤双手剑	
1452196,//布莱克缤弓
1462184,//布莱克缤弩
1322100,//布莱克缤单手钝器
1312142,//布莱克缤单手斧	
1332214,//布莱克缤短刀	
1412126,//布莱克缤双手斧
1422129,//布莱克缤双手钝器
1482159,//布莱克缤指节
1372168,//布莱克缤短杖
1382199,//布莱克缤长杖
1442209,//布莱克缤长矛
1432158//布莱克缤长枪

];
var req = [
	 [4001126, 888],//枫叶
	 [4000313, 88],//黄金枫叶	
	 //[4011008, 5],//锂	 
	 [4021009, 5],//星石
	 [4011007, 5],//月石
 
     [4000407, 88],//铜心
     [4000402, 88],//银心
     [4000406, 88],//金心

	 [4001083, 1],//扎昆象征
	 [4001084, 1],//闹钟象征
	 [4001085, 1]//皮亚努斯的象征		
	
    
];
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
        msg += "\r\n\r\n";
        for (var ii = 0; ii < req.length; ii++) {
            msg += "#i" + req[ii][0] + "##z" + req[ii][0] + "#x" + req[ii][1];
            if (ii % 3 == 0) {
                msg += "\r\n";
            }
        }
        msg += "\r\n";
        msg += "#g----------------------------------------------\r\n";
        for (var i = 0; i < weapon.length; i++) {
            msg += "#r#L" + i + "#";
            msg += "#i" + weapon[i] + "##z" + weapon[i] + "##l\r\n";
        }
        msg += "#g----------------------------------------------\r\n";
        msg += "#L999#" + 返回图标 + "#l\r\n";
        cm.sendSimple("#b#e您好，制作#r布莱克武器#b需要以下材料，没有材料可不行哦\r\n\r\n" + msg + "");
    } else if (status == 1) {
        if (selection == 999) { cm.dispose(); cm.openNpc(9900001, "xy/装备系统/v000/套装制作升级"); return; }
        sels = selection;
        if (!cm.canHold(weapon[sels])) {
            cm.sendNext("#r背包空间不足，固有装备只能持有一个。");
            cm.dispose();
            return;
        }
        for (var i = 0; i < req.length; i++) {
            if (!cm.haveItem(req[i][0], req[i][1])) {
                cm.sendNext("#b你身上没有#r足够的材料#k，继续收集材料去吧！");
                cm.dispose();
                return;
            }
        }
        cm.sendYesNo("#b是否要兑换#r布莱克武器系列#r #i" + weapon[sels] + "##z" + weapon[sels] + "#? \r\n");
    } else if (status == 2) {
        for (var i = 0; i < req.length; i++) {
            cm.gainItem(req[i][0], -req[i][1]);
        }
        cm.gainItem(weapon[sels], 1);
        cm.sendNext("#b已经兑换好了，请前往背包查看 #i" + weapon[sels] + "##z" + weapon[sels] + "#");
        cm.dispose();
    } else {
        //cm.sendNext("#r发生错误: mode : " + mode + " status : " + status);
        cm.dispose();
    }
}
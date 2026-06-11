//T5 第一个合成的物品  第二个忽略 第3456是思维 78 是攻击魔法
var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";
var weapon = new Array(

	Array(1082102,10,10,10,10,10,10,10),//四属+双功
	Array(1002186,10,10,10,10,10,10,10),
	Array(1012289,10,10,10,10,10,10,10),
	Array(1022048,10,10,10,10,10,10,10),
	Array(1032024,10,10,10,10,10,10,10),
	Array(1102039,10,10,10,10,10,10,10),
	Array(1072153,10,10,10,10,10,10,10),
	Array(1802100,10,10,10,10,10,10,10)



);

var req = [
 
];
var rem = 50;//价格
var gailv = 100;//输入百分之几不要输入百分号只要数字在里面就行了!!
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
        //msg += "#b" + 粉心 + "所有装备全属性:#r+10#b";
        for (var ii = 0; ii < req.length; ii++) {
            msg += "#i" + req[ii][0] + ":##z" + req[ii][0] + "#x" + req[ii][1];
            if (ii % 2 == 0 && ii !=0) {
                msg += "\r\n";
            }
        }
		//msg += "\t\t";
		//msg += "";
        //msg += "";
        //msg += "#g-----------------------------------------------------\r\n";
		
        for (var i = 0; i < weapon.length; i++) {
            msg += "#r#L" + i + "#";
            msg += "#b" + 粉心 + "购买#r #i" + weapon[i][0] + "##b\t全属性:#r+10#b点#b\t价格:#r"+rem+"#b元宝#l\r\n\r\n";
        }
       cm.sendSimple("  \t\t\t\t" + 粉心 + "当前元宝余额：#r "+cm.getmoneyb()+"\r\n " + msg + "");
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
		/*if (!cm.haveItem(weapon[sels][1],3)) {
            cm.sendNext("#b身上没有#r#i" + weapon[sels][1] + ":##z" + weapon[sels][1] + "#");
            cm.dispose();
            return;
        }*/
		if(cm.getMeso() < rem){
			cm.sendNext("#b身上没有#r"+rem+"元宝");
            cm.dispose();
            return;
		}
        cm.sendYesNo("#b是否要购买#r #i" + weapon[sels] + "##z" + weapon[sels] + "# \r\n");
    } else if (status == 2) {
		s1 = Math.floor(Math.random() * (100 - 1) + 1);
		if(s1 <= gailv){
			for (var i = 0; i < req.length; i++) {
				cm.gainItem(req[i][0], -req[i][1]);
			}
			cm.setmoneyb(-rem);
			//cm.gainItem(weapon[sels][1],-1);
			cm.gainItem(weapon[sels][0],weapon[sels][2],weapon[sels][3],weapon[sels][4],weapon[sels][5],0,0,weapon[sels][6],weapon[sels][7],0,0,0,0,0,0);
			
			cm.sendNext("#b已经购买了 #i" + weapon[sels] + "##z" + weapon[sels] + "#");
			cm.dispose();
		} else {
			for (var i = 0; i < req.length; i++) {
				cm.gainItem(req[i][0], -req[i][1]);
			}
			cm.setmoneyb(-rem);
			//cm.gainItem(weapon[sels][1],-2);
			cm.sendNext("#b合成失败,你投入的材料消失了~!");
			cm.dispose();
		}
    } else {
        //cm.sendNext("#r发生错误: mode : " + mode + " status : " + status);
        cm.dispose();
    }
}
var 强化中心 = "#fEffect/CharacterEff1.img/QQ1408745/0/1#";
var dd = " ";
var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";
var 群粉心 = ""+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+"\r\n";
var weapon = new Array(
    Array(1003172,1003601,200,200,200,200,200,200,2000,2000,200,200,0,0,0,0),
	Array(1102275,1102456,200,200,200,200,200,200,2000,2000,200,200,0,0,0,0),
	Array(1082295,1082472,200,200,200,200,200,200,2000,2000,200,200,0,0,0,0),
	Array(1052314,1052509,200,200,200,200,200,200,2000,2000,200,200,0,0,0,0),
	Array(1132143,1132156,200,200,200,200,200,200,2000,2000,200,200,0,0,0,0),
	Array(1072485,1072711,200,200,200,200,200,200,2000,2000,200,200,0,0,0,0)
);

var req = [
  [1003601, 1],
  [1102456, 1],
  [1082472, 1],
  [1052509, 1],
  [1132156, 1],
  [1072711, 1],

  [2048403, 10]
];
for (var i = 0; i < weapon.length; i++) {
var 四属性 = weapon[i][2] //1-4属性
var 双攻 = weapon[i][6] //5-6双功
var 血蓝量 = weapon[i][8] //7-8血蓝装备+
var 双防 = weapon[i][10] //9-10双防装备+
        }
var rem = 100000000;
var gailv = 70;//输入百分之几不要输入百分号只要数字在里面就行了!!
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
        msg += "\r\n#d合成材料需要如下:#b ";
        msg += "\r\n";
        for (var ii = 0; ii < req.length; ii++) {
        msg += " #b#i" + req[ii][0] + "##z" + req[ii][0] + "# #r* " + req[ii][1] + " #b个 #k【拥有#r #c" + req[ii][0] + "# #k个】\r\n";
        /*if (ii % 2 == 0 && ii !=0) {
        msg += "\r\n";
            }*/
        }
		msg += "#b金币消耗：#r"+rem+" #b金币";
		msg += "\r\n#b当前拥有：#r"+cm.getMeso()+" #b金币";
        msg += "\r\n";
        msg += "#k------------------------------------------------------\r\n";
        msg += "#k请选择需要合成的装备(装备合成概率 #r#e"+gailv+"%#n#k)\r\n装备属性#b【四属性+ #r"+四属性+"#b;双功+ #r"+双攻+"#b;双防+ #r"+双防+"#b;血蓝+ #r"+血蓝量+"#b】\r\n";
        for (var i = 0; i < weapon.length; i++) {
            msg += "#r#L" + i + "#";
            msg += "#i" + weapon[i][0] + "##l";
        }
       cm.sendSimple(""+dd+"\r\n\t\t\t"+强化中心+"#d" + msg + "");
    } else if (status == 1) {
        sels = selection;
        if (!cm.canHold(weapon[sels][0])) {
            cm.sendNext("#r背包空间不足");
            cm.dispose();
            return;
        }
        for (var i = 0; i < req.length; i++) {
            if (!cm.haveItem(req[i][0], req[i][1])) {
                cm.sendNext("#b您的背包里没有 #r#i" + req[i][0] + ":##z" + req[i][0] + "# x " + req[i][1] + "\r\n\r\n #b请先将 #r#i" + req[i][0] + ":##z" + req[i][0] + "##b放进背包再来");
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
			//cm.gainItem(weapon[sels][1],-1);
			cm.gainItem(weapon[sels][0],weapon[sels][2],weapon[sels][3],weapon[sels][4],weapon[sels][5],weapon[sels][8],weapon[sels][9],weapon[sels][6],weapon[sels][7],weapon[sels][10],weapon[sels][11],weapon[sels][12],weapon[sels][13],weapon[sels][14],weapon[sels][15]);
			
			cm.sendNext("#b已经兑换了 #i" + weapon[sels] + "#");
			cm.dispose();
		} else {
			for (var i = 0; i < req.length; i++) {
				cm.gainItem(req[i][0], -req[i][1]);
			}
			cm.gainMeso(-rem);
			//cm.gainItem(weapon[sels][1],-2);
			cm.sendNext("#b合成失败,你投入的材料消失了~!");
			cm.dispose();
		}
    } else {
        //cm.sendNext("#r发生错误: mode : " + mode + " status : " + status);
        cm.dispose();
    }
}
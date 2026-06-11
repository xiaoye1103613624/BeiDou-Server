var W = "#fUI/UIWindow.img/PartySearch/check0#";
var X = "#fUI/UIWindow.img/PartySearch/check1#";
var 银杏叶 ="#fMap/MapHelper/weather/maple/3#";
var weapon = new Array(

	Array(2044303,2044302),
	Array(2044403,2044402),
	Array(2043803,2043802),
	Array(2044703,2044702),
	Array(2043303,2043302),
	Array(2044503,2044502),
	Array(2044603,2044602),
	Array(2044815,2044802),
	Array(2044908,2044902),
	Array(2043003,2043002),
	Array(2044003,2044002)

	


);

var req = [
  
];
var rem = 10000;
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
		金币余额 = cm.getPlayer().getMeso();
	  var  msg = "#k\r\n#d┍========================================= "+银杏叶+" ====#d┑#k\r\n";
        msg += "                    #r合成需要以下物品:#n#b ";
        msg += "\r\n";
		
    if (cm.getBossRank("满足条件",2) > 0) {
			
			var 开关 = ""+X+"#l";
		} else {
			var 满足条件 = 0
			var 开关 = ""+W+"#l";
		}	
        for (var i = 0; i < weapon.length; i++) {
            msg += "#r#L" + i + "#";
            msg += "#d合成#i"+weapon[i][0]+"##z"+weapon[i][1]+":##r要100个#n#d#i"+weapon[i][1]+"##z"+weapon[i][1]+"##n#k\r\n";
        }	

		msg += "#k\r\n#d┕==== "+银杏叶+" =========================================#d┙#k\r\n";		
       cm.sendSimple("#d" + msg + "");
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
		if (!cm.haveItem(weapon[sels][1],100)) {
            cm.sendNext("#b你身上不足#r100个#i" +  weapon[sels][1] + "##z" +  weapon[sels][1] + "##n#k，请凑齐之后再来兑换！");
            cm.dispose();
            return;
        }
        cm.sendYesNo("#b是否要兑换#r #i" + weapon[sels][1] + ":##z" +  weapon[sels][1] + "# \r\n");
    } else if (status == 2) {
		s1 = Math.floor(Math.random() * (100 - 1) + 1);
		if(s1 <= gailv){
			for (var i = 0; i < req.length; i++) {
				cm.gainItem(req[i][0], -req[i][1]);
			}
			
			cm.gainItem(weapon[sels][1],-100);
			cm.gainItem(weapon[sels][0],1);
			
			cm.sendNext("#b已经兑换了 #i" + weapon[sels] + "##z" +  weapon[sels] + "#");
			cm.dispose();
		} else {
			for (var i = 0; i < req.length; i++) {
				cm.gainItem(req[i][0], -req[i][1]);
			}
			//cm.gainItem(weapon[sels][1],-2);
			cm.sendNext("#b合成失败,你投入的材料消失了~!");
			cm.dispose();
		}
    } else {
        //cm.sendNext("#r发生错误: mode : " + mode + " status : " + status);
        cm.dispose();
    }
}
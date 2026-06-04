//T5 第一个合成的物品  第二个忽略 第3456是思维 78 是攻击魔法
var W = "#fUI/UIWindow.img/PartySearch/check0#";
var X = "#fUI/UIWindow.img/PartySearch/check1#";
var 银杏叶 ="#fMap/MapHelper/weather/maple/3#";
var KaixinMs462110111 ="#fUi/CashShop.img/CashItem/0#"
var weapon = new Array(

	Array(4005002,1102207,1,1,1,1,1,1)

);

var req = [

  [4004002, 10]

];
var rem = 100000;
var gailv = 100;//输入百分之几不要输入百分号只要数字在里面就行了!!
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
		
        for (var ii = 0; ii < req.length; ii++) {

       if(cm.haveItem(req[ii][0], req[ii][1])){
            msg += "#d#i" + req[ii][0] + ":##z" + req[ii][0] + "#  [#r#c" + req[ii][0] + "##k/"+ req[ii][1]+"]#r 已满足 " + X + " #n#k\r\n";
    }else{
            msg += "#d#i" + req[ii][0] + ":##z" + req[ii][0] +"#  [#r#c" + req[ii][0] + "##k/"+ req[ii][1]+"]#d 未满足 " + W + " #n#k\r\n";
    }
            if (ii % 2 == 0 && ii !=0) {
              //msg += "";
            }
        }

		//msg += "\t\t";
       if(cm.getMeso() < rem){
		msg += "\r\n "+KaixinMs462110111+" 金币：[#r"+金币余额+"#k/"+rem+"]#d 未满足 " + W + " #n#k\r\n";
    }else{
		msg += "\r\n "+KaixinMs462110111+" 金币：[#r"+金币余额+"#k/"+rem+"#r 已满足 " + X + " #n#k\r\n";
    }
        msg += "#k\r\n#d┕==== "+银杏叶+" =========================================#d┙#k\r\n";	
        for (var i = 0; i < weapon.length; i++) {
            msg += "#r#L" + i + "#";
            msg += "#d点击制作#r #i" + weapon[i][0] + ":##z" + weapon[i][0] + "##l\r\n";
        }
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
                cm.sendNext("#b你身上没有#r足够的#i" + req[i][0] + "##z" + req[i][0] + "##n#k，继续收集材料去吧！");
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
			cm.sendNext("#b身上没有#r"+rem+"金币");
            cm.dispose();
            return;
		}
        cm.sendYesNo("#b是否要兑换#r #i" + weapon[sels][0] + ":##z" + weapon[sels][0] + "# \r\n");
    } else if (status == 2) {
		s1 = Math.floor(Math.random() * (100 - 1) + 1);
		if(s1 <= gailv){
			for (var i = 0; i < req.length; i++) {
				cm.gainItem(req[i][0], -req[i][1]);
			}
			cm.gainMeso(-rem);
			//cm.gainItem(weapon[sels][1],-1);
			cm.gainItem(weapon[sels][0],weapon[sels][2],weapon[sels][3],weapon[sels][4],weapon[sels][5],0,0,weapon[sels][6],weapon[sels][7],0,0,0,0,0,0);
			
			cm.sendNext("#b已经兑换了 #i" + weapon[sels] + "##z" + weapon[sels]+ "#");
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
var 提示 = "#fUI/CN_Chat/ChattingRoom/BtVolUp/0/normal/0#";
//T4 第一个合成的物品  第二个需求T5d的武器 第3456是思维 78 是攻击魔法
var weapon = new Array(
//Array(1115211,1115210,120,120,120,120,120,120),
//Array(1115212,1115211,140,140,140,140,140,140),
Array(1115213,1115212,160,160,160,160,160,160),
Array(1115214,1115213,180,180,180,180,180,180),
Array(1115215,1115214,200,200,200,200,200,200)
);
//需求的材料和数量
var req = [
  /*[3605015, 10],*/
  [4021009, 15],
  [4011007, 15],
  [4000464, 5],
  [3605006, 99],
  [4001126, 1999]
];
//需求的金币
var rem = 100000000;
var dianquan = 5000; // 每次进阶所需的点券数量
var gailv = 75;//成功率 输入百分之几不要输入百分号只要数字在里面就行了!!-----下面写了假显示
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
		msg += ""+提示+"#k #b点券消耗：#r"+dianquan+" #b点券\r\n";
		msg += "#g-----------------------------------------------------\r\n";
		msg += ""+提示+"#k #b当前拥有：#r"+cm.getMeso()+" #b金币\r\n";
		msg += ""+提示+"#k #b当前拥有：#r" + cm.getPlayer().getCSPoints(1) + " #b点券\r\n";
        msg += ""+提示+"#k #b进阶说明：#r成功率#r75%，进阶失败材料将会消失\r\n";
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
		if (cm.getPlayer().getCSPoints(1) < dianquan) {
            cm.sendNext("#b身上没有#r" + dianquan + "点券");
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
			cm.gainNX(-dianquan); // 扣除点券
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
			cm.gainNX(-dianquan); // 扣除点券
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
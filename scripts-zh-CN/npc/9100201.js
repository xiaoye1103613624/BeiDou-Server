var FY0 = "┏━━━━━━━━━━━┓";
var FY1 = "┃       - 枫叶 -       ┃";
var FY2 = "┃ 脚本仿制  　定制脚本 ┃";
var FY3 = "┃ 技术支持 　 游戏顾问 ┃";
var FY4 = "┃ ＷＺ添加　  地图制作 ┃";
var FY5 = "┃ 加盾防御　  售登陆器 ┃";
var FY6 = "┣━━━━━━━━━━━┫";
var FY7 = "┃ 唯一QQ:1848350048    ┃";
var FY8 = "┗━━━━━━━━━━━┛";
var 红色箭头 = "#fUI/UIWindow/Quest/icon6/7#";
var 蓝色箭头 = "#fUI/UIWindow/Quest/icon2/7#";
var 正方箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";
var status = 0;

//普通奖池
var itemList1 = [
	//物品id，几率，数字越大概率越大，数量

[1402051, 1, 1, 0], //超级龙背
[4031141, 2, 1, 0],
[4031097, 3, 1, 0],
[4031142, 4, 1, 0],
[4031095, 10, 1, 0],
[4031096, 10, 1, 0],
[2614000, 10, 1, 0],
[4000089, 10, 1, 0],
[4001126, 10, 1, 0],
[2430402, 10, 1, 0],
[2430026, 10, 1, 0],
[2430047, 5, 1, 0],
[2430051, 1, 1, 0],
[4251200, 50, 1, 0],
[4251201, 50, 1, 0],
[4251202, 50, 1, 0],
[2028074, 50, 1, 0],
[4000081, 50, 1, 0],
[4000403, 50, 1, 0],
[4001028, 50, 1, 0],
[4021009, 50, 1, 0],
[4031997, 10, 1, 0],
[4000703, 10, 1, 0],
[4000313, 50, 1, 0],
[2049401, 10, 1, 0],
[4001485, 20, 100,0],
[4031549, 10, 1, 0],
[5150038, 10, 1, 0],
[5150040, 10, 1, 0],
[5121015, 20, 1, 0],
[5121016, 20, 1, 0],
[5120000, 20, 1, 0],
[5120012, 20, 1, 0],
[5390006, 20, 1, 0],
[5390004, 20, 1, 0],
[5390005, 20, 1, 0],
[5121020, 20, 1, 0],
[5211060, 20, 1, 0],
[5360015, 20, 1, 0],
[4011000, 20, 1, 0],
[4011001, 20, 1, 0],
[4011002, 20, 1, 0],
[4011003, 20, 1, 0],
[4011004, 20, 1, 0],
[4011005, 20, 1, 0],
[4011006, 20, 1, 0],
[4020008, 20, 1, 0],
[4021000, 20, 1, 0],
[4021001, 20, 1, 0],
[4021002, 20, 1, 0],
[4021003, 20, 1, 0],
[4021004, 20, 1, 0],
[4021005, 20, 1, 0],
[4021006, 20, 1, 0],
[4021007, 20, 1, 0],
[4021008, 20, 1, 0],
[4007000, 20, 5, 0],
[4007001, 20, 5, 0],
[4007002, 20, 5, 0],
[4007003, 20, 5, 0],
[4007004, 20, 5, 0],
[4007005, 20, 5, 0],
[4007006, 20, 5, 0],
[4007007, 20, 5, 1],
[4260000, 20, 5, 0],
[4260001, 20, 5, 0],
[4260002, 20, 5, 0],
[4260004, 20, 5, 1],
[4260005, 20, 5, 0],
[4260006, 2, 2, 0],
[4260007, 1, 1, 0],
[4260008, 1, 1, 1]

];

var useNx = 10000000;
var sel0 = -1;
function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (status == 0 && mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    } else {
        status--;
    }

    if (status == 0) {
    	var txt = "#d使用点券进行抽奖，您将随机获得下面的奖励#n#k\r\n";
		txt += "#d当前点券剩余：#b"+cm.getPlayer().getCSPoints(1)+"\r\n";
		txt += "#d每次抽奖消耗：#b1000点券\r\n";
		txt += "#d今日已抽次数：#b"+cm.getBossLog("点券抽奖次数")+"\r\n\r\n";
    	txt += "#d#L1##r"+红色箭头+蓝色箭头+" 开始抽奖！冲！#l\r\n\r\n\r\n\r\n";
		txt += "#b"+正方箭头+" 奖品展示： \r\n";
		
		var txt2 = "";
		for (var i = 0; i < itemList1.length;  i++){
			txt2 += "#i"+itemList1[i][0]+":#";
		}
    	cm.sendSimple(txt + txt2);
    } else if (status == 1) {
		var jobid=cm.getPlayer().getJob();
		//if (jobid==112||jobid==122||jobid==132||jobid==212||jobid==222||jobid==232||jobid==312||jobid==322||jobid==412||jobid==422||jobid==512||jobid==522||jobid==2112||jobid==1112||jobid==1111||jobid==1211||jobid==1311||jobid==1411||jobid==1511){
		sel0 = selection;
		cm.sendGetNumber(""+正方箭头+"#d 请输入抽奖次数\r\n"
		+"#d每次抽奖需要消耗1000点券",
		1, 1, 99999
		);
		//}else {
         //   cm.sendOk("您好，金币抽奖需要您先完成所有转职后才可以进行抽奖哦！");
         //   cm.dispose();
         //   }
	} else if(status == 2) {
		if (!cm.checkNumSpace(0, selection)) {
			cm.sendOk("背包空间不足"+selection+"格");
			cm.dispose();
			return;
		}
    	switch (sel0) {
			case 1:
                if (cm.getPlayer().getCSPoints(1)< 1000*selection) 
				{
					cm.sendOk("点券数量不足#r "+(1000*selection)+" #k，无法抽奖");
					cm.dispose();
					return;
				} 
				else
				{
					cm.gainMeso(-(1000*selection));
					cm.setBossLog("点券抽奖次数",1,selection);
				}
				break;
			case 2:
                if (cm.getMeso() < (useNx*selection)) {
					cm.sendOk("金币不足"+(useNx*selection)+"，无法抽奖");
					cm.dispose();
					return;
				} else {
					cm.gainMeso(-useNx*selection);
				}
				break;
			default:
				cm.sendOk("脚本出错，请联系管理员");
				cm.dispose();
				return;
        }
		var txt = "恭喜你获得道具：\r\n";
		for (var i = 0; i < selection; i++) {
			var item;
			var ran = Math.floor(Math.random() * 100);
			var ran1 = null;
			ran1 = finalGift(itemList1);
			if(cm.getBossRankCount("屏蔽"+ran1[0]) > 0){
				cm.gainGachaponItem2(4001126, 1, "枫叶", ran1[3]);
			}
			else
			{
				var sjpp = Math.floor(Math.random()*20)+10;
				var suiji1 = Math.floor(Math.random()*10)+1;
				var suiji2 = Math.floor(Math.random()*10)+1;
				var suiji3 = Math.floor(Math.random()*10)+1;
				var suiji4 = Math.floor(Math.random()*10)+1;
				var suijigg = Math.floor(Math.random()*20)+sjpp+(100-ran1[2]);
				var suijigg2 = Math.floor(Math.random()*20)+sjpp+(100-ran1[2]);
				var suiji5 = Math.floor(Math.random()*200);
				cm.gainGachaponItem2(ran1[0], ran1[2], "枫叶", 1);
				cm.setBossLog("活跃度");
				cm.gainItem(ran1[0],1);
			}
			cm.gainItem(4000313, 1);
			//cm.gainItem(ran1[0] ,ran1[2])
			//cm.worldMessage("『抽奖捷报』：恭喜玩家."+ cm.getChar().getName() +"  获得["+ Packages.server.MapleItemInformationProvider.getInstance().getName(ran1[0]) +"]让我们热烈的祝福他/她吧！");
			txt += "#v" + ran1[0] + "#\r\n";
			//var result = cm.setBossRankCount("随机奖池抽奖");
			/*
			var ran2 = null;
			if (result%10 == 0) {//十连抽保底
				ran2 = finalGift(itemList1);
				cm.gainItem(ran2[0] ,ran2[2])
				txt += "额外道具：#v" + ran2[0] + "#\r\n";
			}
			*/
		}
		cm.dispose();
		return;
    }
}

function finalGift(lists) {
	var maxChance = 0;
	for (var i in lists) {
		if (lists[i][1] > maxChance) {
			maxChance = lists[i][1];
		}
	}
	var chance = Math.floor(Math.random() * maxChance);
	var finalitem = Array();
	for (var i = 0; i < lists.length; i++) {
		if (lists[i][1] >= chance) {
			finalitem.push(lists[i]);
		}
	}
	var ran1 = Math.floor(Math.random() * finalitem.length);
	return finalitem[ran1];
}
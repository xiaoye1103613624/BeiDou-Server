var 点券图标 = "#fUI/CashShop/CashItem/0#";
var status = 0;
//普通奖池
var itemList1 = [
	//物品id，几率，数字越大概率越大，数量
[	1912000	,50,1,1],
[	1912001	,50,1,1],
[	1912002	,50,1,1],
[	1912003	,50,1,1],
[	1912004	,50,1,1],
[	1912005	,50,1,1],
[	1912006	,50,1,1],
[	1912007	,50,1,1],
[	1912008	,50,1,1],
[	1912009	,50,1,1],
[	1912010	,50,1,1],
[	1912011	,50,1,1],
[	1912012	,50,1,1],
[	1912013	,50,1,1],
[	1912014	,50,1,1],
[	1912015	,50,1,1],
[	1912016	,50,1,1],
[	1912021	,50,1,1],
[	1912024	,50,1,1],
[	1912025	,50,1,1],
[	1912026	,50,1,1],
[	1912027	,50,1,1],
[	1912028	,50,1,1],
[	1912029	,50,1,1],
[	1912030	,50,1,1],
[	1912031	,50,1,1],
[	1912032	,50,1,1],
[	1912033	,50,1,1],
[	1912034	,50,1,1],
[	1912035	,50,1,1],
[	1912036	,50,1,1],
[	1912037	,50,1,1],
[	1912038	,50,1,1],
[	1912039	,50,1,1],
[	1912040	,50,1,1],
[	1912041	,50,1,1],
[	1912042	,50,1,1],
[	1912043	,50,1,1],


];

var useNx = 5000;
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
    	var txt = "#d\t\t\t#b欢迎来到『冒险岛』抽奖中心#n#k\r\n";
    	txt += "#r#L1##b点券抽奖#l\r\n\r\n\r\n";
		//txt += "#d#L2##b枫叶抽奖#l\r\n\r\n\r\n";
		txt += "";
		
		var txt2 = "";
		for (var i = 0; i < itemList1.length;  i++){
			txt2 += "#i"+itemList1[i][0]+":#";
		}
    	cm.sendSimple(txt + txt2);
    } else if (status == 1) {
		sel0 = selection;
		cm.sendGetNumber("#d请输入抽奖次数\r\n"
		+"#d点卷抽奖5000一次\r\n"
		//+"#r抽奖有保底，请看群文件"
		+"#r当前拥有点卷数量"+点券图标+": "+cm.getPlayer().getCSPoints(1)+"#k\r\n#r " ,
		1, 1, 99999
		);
	} else if(status == 2) {
    	switch (sel0) {
			case 1:
                if (cm.getPlayer().getCSPoints(1) < (useNx*selection)) {
					cm.sendOk("枫叶不足"+(useNx*selection)+"，无法抽奖");
					cm.dispose();
					return;
				} else {
					cm.getPlayer().modifyCSPoints(1, -useNx*selection);
				}
				break;
			case 2:
                if (!cm.haveItem(4001126,5000*selection)) {
					cm.sendOk("#v4001126#数量不足#r "+(5000*selection)+" #k，无法抽奖");
					cm.dispose();
					return;
				} else {
					cm.gainItem(4001126, -(5000*selection));
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
				cm.gainGachaponItem2(4001126, 1, "自由金猪", ran1[3]);
			//}else{
			//	cm.gainGachaponItem2(ran1[0], ran1[2], "自由金猪", 1);
			}
			//cm.gainItem(2000005, 1);
			cm.gainItem(ran1[0] ,ran1[2])
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
		cm.sendOk(txt);
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
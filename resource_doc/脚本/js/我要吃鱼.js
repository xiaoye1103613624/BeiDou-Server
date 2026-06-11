
var 倍率 =5000;
var itemList = new Array(
//兑换物品ID,兑换数量,获得经验
	Array(4031627,10,(5*倍率)),
	Array(4031633,10,(6*倍率)),
	Array(4031634,10,(7*倍率)),
	Array(4031635,10,(8*倍率)),
	Array(4031636,10,(9*倍率)),
	Array(4031630,10,(10*倍率)),
	Array(4031637,10,(11*倍率)),
	Array(4031638,10,(12*倍率)),
	Array(4031639,10,(13*倍率)),
	Array(4031640,10,(14*倍率)),
	Array(4031628,10,(15*倍率)),
	Array(4031641,10,(16*倍率)),
	Array(4031642,10,(17*倍率)),
	Array(4031643,10,(18*倍率)),
	Array(4031644,10,(19*倍率)),
	Array(4031631,10,(20*倍率)),
	Array(4031645,10,(21*倍率)),
	Array(4031646,10,(22*倍率)),
	Array(4031647,10,(23*倍率)),
	Array(4031648,10,(24*倍率))
	);
/*Array(4031648, 1000, 1, 1), //
Array(4031647, 1000, 1, 1), //
Array(4031644, 1000, 1, 1), //
Array(4031643, 1000, 1, 1), //
Array(4031640, 1000, 1, 1), //
Array(4031639, 1000, 1, 1), 
Array(4031636, 1000, 1, 1), //
Array(4031635, 1000, 1, 1), 
Array(4031634, 1000, 1, 1), //
//Array(4020008, 1000, 1, 1), //
Array(4031646, 1000, 1, 1) /*/
var sels;
var status = -1;
var 物品数量;
var 可换经验;
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
        msg += "吃掉各种钓到的鱼后可以增加经验哦\r\n#b(吃鱼前请查看当前升级还需要多少经验)\r\n\r\n";
		//msg += "#r吃鱼系统已升级(可一键增加)#k\r\n";
        //msg += "#b------------------------------------------------------\r\n";
        for (var i = 0; i < itemList.length; i++) {
			var 物品数量 = cm.getPlayer().getItemQuantity(itemList[i][0], false);
			var 可换经验 = cm.getPlayer().getItemQuantity(itemList[i][0], false)*itemList[i][2];
            msg += "#k#L" + i + "#";
            msg += "吃掉#r " + 物品数量 + " #k条 #i" + itemList[i][0] + "#  可以增加#r " + 可换经验 + " #k点经验#l\r\n";
        }
        cm.sendSimple("" + msg + "");
		
    } else if (status == 1) {
        sels = selection;
	 物品数量 = cm.getPlayer().getItemQuantity(itemList[sels][0], false);
	 可换经验 = cm.getPlayer().getItemQuantity(itemList[sels][0], false)*itemList[sels][2];
        if (!cm.haveItem(itemList[sels][0],1)) {
			cm.sendOk("#b你根本就没鱼，你吃个铲铲");
            status = -1;
			//cm.dispose();
            //return;
        }
        cm.sendYesNo("#b是否要吃掉#r#i" + itemList[sels][0] + "#"+物品数量+"条鱼  增加" + 可换经验 + " 经验? \r\n");
  
   } else if (status == 2) {
	 物品数量 = cm.getPlayer().getItemQuantity(itemList[sels][0], false);
	 可换经验 = cm.getPlayer().getItemQuantity(itemList[sels][0], false)*itemList[sels][2];		
        cm.gainItem(itemList[sels][0],-物品数量);
        cm.gainExp(+可换经验); //经验		
        cm.sendOk("#b已经兑换了 #i" + itemList[sels][0] + "# × "+物品数量+"  获得:"+可换经验+" 经验");
		cm.worldMessage(6,"【我要吃鱼】["+cm.getName()+"]这吃货吃掉了一些鱼，增加了"+可换经验+" 经验");
        
		//cm.dispose();
    
	status = -1;
	} else {
        cm.sendNext("#r发生错误: mode : " + mode + " status : " + status);
        cm.dispose();
    }
	
}
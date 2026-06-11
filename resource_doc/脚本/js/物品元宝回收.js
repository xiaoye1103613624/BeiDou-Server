

var status = -1;
var itemss;
var slot = Array();
var itemid = 4001245;
var isRec = Array(
Array(2460005,1),
Array(2531000,5)
//Array(2049509,15),
//Array(3990000,5)
);
var isRec1 = Array(
Array(2460005,1),
Array(2531000,5)
//Array(2049509,15),
//Array(3990000,5)
);

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
	
    if (mode == 1) {
        status++;
    } else if (mode == 0 && status != 0) {
        status--;
    } else {
        cm.dispose();
        return;
    }

    if (status == 0) {
		var avail = "";
		  for (var i = 0; i < 96; i++) {
			  for(var s=0;s < isRec.length;s++){
				if (cm.getInventory(2).getItem(i) != null && cm.getInventory(2).getItem(i).getItemId() == isRec[s][0]) {
					avail += "#L" + cm.getInventory(2).getItem(i).getItemId() + "#"+cm.显示物品(cm.getInventory(2).getItem(i).getItemId())+"[#r#c" + cm.getInventory(2).getItem(i).getItemId() + "##k]  元宝单价:"+isRec[s][1]+"#l\r\n";
				}
			  }
				//slot.push(i);
		}
		if(avail == ""){
			cm.sendOk("没有可分解的物品");
			cm.dispose();
			return;
		}
		cm.sendSimple("可分解的物品和单价如下,请确认是否分解？\r\n" + avail);
	 
    } else if (status == 1) {
        itemss = selection;
		var shul = cm.getPlayer().getItemQuantity(itemss, false);
			  for(var s=0;s < isRec1.length;s++){
				  if(isRec1[s][0] == itemss){
		cm.removeAll(itemss);
		cm.setmoneyb(shul*isRec1[s][1]);
		Ok("我已经将你背包里的 #d#i" + itemss + ":# #t" + itemss + ":# 数量：#e#r" + shul + "#n#b\r\n从你的背包分解获得:#r"+shul*isRec1[s][1]+"元宝！");
	//	cm.getItemLog("物品元宝回收", " " + cm.getItemName(itemss) + " "+shul+" 从你的背包分解获得:#r"+shul*isRec1[s][1]+"元宝  最终元宝"+cm.getmoneyb()+"");	  
cm.logToFile_chr("游戏记录/物品元宝回收.txt", " 玩家角色名称："+cm.getPlayer().getName()+"  账号："+cm.getPlayer().getClient().getAccountName()+" " + cm.getItemName(itemss) + " "+shul+" 从你的背包分解获得:#r"+shul*isRec1[s][1]+"元宝  最终元宝"+cm.getmoneyb()+"·\n");
				  }
			  }
		status = -1;
		//return;
    } else {
        cm.dispose();
    }//status
}// function

function Ok(text) {
    cm.sendOk(text);
}
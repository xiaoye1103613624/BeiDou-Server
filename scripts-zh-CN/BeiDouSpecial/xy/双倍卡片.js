

var l = ["日","一","二","三","四","五","六"];
var d = new Date().getDay();
var str = "星期" + l[d];

var 双倍卡代码 = 5211047;
var 精灵项链代码 = 1122017;
var 双爆卡代码 = 5360015;
var 双倍点券 = 1888;
var 精灵项链点券 = 9999;
var 双爆点券 = 1888;
var 双倍卡时间 = 3;//小时
var 精灵项链时间 = 12;//小时
var 双爆卡时间 = 3;//小时
var 双倍次数 = 8;
var 精灵项链次数 = 2;
var 双爆次数 = 8;

function start() {
    status = -1;

    action(1, 0, 0);
}

function action(mode, type, selection) {

    if (mode == -1) {
        cm.dispose();
    } else {
        if (status >= 0 && mode == 0) {

            cm.sendOk("感谢你的光临！");
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        } else {
            status--;
        }
        if (status == 0) {
			var tex2 = "";
            var text = "";
            for (i = 0; i < 10; i++) {
                text += "";
            }
			text += "#k欢迎来到双倍商城！\t#r注：购买后换线生效#k\r\n\r\n"
			
			text += ""
			text += "#L5##d购买"+精灵项链时间+"小时#v"+精灵项链代码+"#(#r"+精灵项链点券+"点券)#k今天已买:#r["+cm.getPlayer().getBossLog("精灵项链记录")+"#k/2]#r\r\n\r\n"
				
			text += "#L1##d购买"+双倍卡时间+"小时#v"+双倍卡代码+"#(#r"+双倍点券+"点券)#k今天已买:#r["+cm.getPlayer().getBossLog("双倍记录")+"#k/8]#r\r\n\r\n"	
			
			text += "#L2##d购买"+双爆卡时间+"小时#v"+双爆卡代码+"#(#r"+双爆点券+"点券)#k今天已买:#r["+cm.getPlayer().getBossLog("双爆记录")+"#k/8]#r\r\n\r\n"
			
			
			
			
			cm.sendOk(text); 
        } else if (selection == 1) {
			if (cm.getInventory(5).isFull(0)){//判断第五个也就是现金栏的装备栏是否有一个空格
            cm.sendOk("#b请保证现金栏位至少有1个空格,否则无法购买！");		
            cm.dispose();
            } else if (cm.getPlayer().getCSPoints(1) < 双倍点券){
			cm.sendOk("点券不足"+双倍点券+"点!");
      	    cm.dispose();
			} else if(cm.haveItem(双倍卡代码,1)){
            cm.sendOk("你的#v"+双倍卡代码+"#还未到期!");
            cm.dispose();
			} else if(cm.getPlayer().getBossLog("双倍记录") >= 双倍次数){//判断一天几次
			cm.sendOk("一天只能购买"+双倍次数+"次，请明天在来!");
            cm.dispose();
			} else {
			cm.getPlayer().setBossLog('双倍记录');//给一天次数记录
			cm.gainNX(-双倍点券);
			cm.gainItem(双倍卡代码,1,3);//扣除物品
			cm.sendOk("购买成功，换频道或者进入商城后才生效!");
			cm.dispose();
				}
        } else if (selection == 2) {
			if (cm.getInventory(5).isFull(0)){//判断第五个也就是现金栏的装备栏是否有一个空格
            cm.sendOk("#b请保证现金栏位至少有1个空格,否则无法购买！");		
            cm.dispose();
            } else if (cm.getPlayer().getCSPoints(1) < 双爆点券){
			cm.sendOk("点券不足"+双爆点券+"点!");
      	    cm.dispose();
			} else if(cm.haveItem(双爆卡代码,1)){
            cm.sendOk("你的#v"+双爆卡代码+"#还未到期!");
            cm.dispose();
			} else if(cm.getPlayer().getBossLog("双爆记录") >= 双爆次数){//判断一天几次
			cm.sendOk("一天只能购买"+双爆次数+"次，请明天在来!");
            cm.dispose();
			} else {
			cm.getPlayer().setBossLog('双爆记录');//给一天次数记录
			cm.gainNX(-双爆点券);
			cm.gainItem(双爆卡代码,1,3);//扣除物品
			cm.sendOk("购买成功，换频道或者进入商城后才生效!");
			cm.dispose();
				}
		} else if (selection == 3) {
			if (cm.getInventory(3).isFull(0)){//判断第五个也就是现金栏的装备栏是否有一个空格
            cm.sendOk("#b请保证设置栏位至少有1个空格,否则无法购买！");		
            cm.dispose();
            } else if (cm.getPlayer().getCSPoints(1) < 宠吸点券){
			cm.sendOk("点券不足"+宠吸点券+"点!");
      	    cm.dispose();
			} else if(cm.haveItem(宠吸卡代码,1)){
            cm.sendOk("你的#v"+宠吸卡代码+"#还未到期!");
            cm.dispose();
			} else if(cm.getPlayer().getBossLog("宠吸记录") >= 宠吸次数){//判断一天几次
			cm.sendOk("一天只能购买"+宠吸次数+"次，请明天在来!");
            cm.dispose();
			} else {
			cm.getPlayer().setBossLog('宠吸记录');//给一天次数记录
			cm.gainNX(-宠吸点券);
			cm.gainItem(宠吸卡代码,1,3);//扣除物品
			cm.sendOk("购买成功，换频道或者进入商城后才生效!");
			cm.dispose();
				}
		} else if (selection == 4) {
			if (cm.getInventory(3).isFull(0)){//判断第五个也就是现金栏的装备栏是否有一个空格
            cm.sendOk("#b请保证设置栏位至少有1个空格,否则无法购买！");		
            cm.dispose();
            } else if (cm.getPlayer().getCSPoints(1) < 宠吸天卡点券){
			cm.sendOk("点券不足"+宠吸天卡点券+"点!");
      	    cm.dispose();
			} else if(cm.haveItem(宠吸卡代码,1)){
            cm.sendOk("你的#v"+宠吸卡代码+"#还未到期!");
            cm.dispose();
			} else if(cm.getPlayer().getBossLog("宠吸记录") >= 宠吸天卡次数){//判断一天几次
			cm.sendOk("一天只能购买"+宠吸天卡次数+"次，请明天在来!");
            cm.dispose();
			} else {
			cm.getPlayer().setBossLog('宠吸天卡记录');//给一天次数记录
			cm.gainNX(-宠吸天卡点券);
			cm.gainItem(宠吸卡代码,1,24);//扣除物品
			cm.sendOk("购买成功，换频道或者进入商城后才生效!");
			cm.dispose();
				}
		} else if (selection == 5) {
			if (cm.getInventory(1).isFull(0)){//判断第五个也就是现金栏的装备栏是否有一个空格
            cm.sendOk("#b请保证装备栏位至少有1个空格,否则无法购买！");		
            cm.dispose();
            } else if (cm.getPlayer().getCSPoints(1) < 精灵项链点券){
			cm.sendOk("点券不足"+精灵项链点券+"点!");
      	    cm.dispose();
			} else if(cm.haveItem(精灵项链代码,1)){
            cm.sendOk("你的#v"+精灵项链代码+"#还未到期!");
            cm.dispose();
			} else if(cm.getPlayer().getBossLog("精灵项链记录") >= 精灵项链次数){//判断一天几次
			cm.sendOk("一天只能购买"+精灵项链次数+"次，请明天在来!");
            cm.dispose();
			} else {
			cm.getPlayer().setBossLog('精灵项链记录');//给一天次数记录
			cm.gainNX(-精灵项链点券);
			cm.gainItem(精灵项链代码,0,0,0,0,0,0,0,0,0,0,0,0,0,0,12);//扣除物品
			cm.sendOk("购买成功，换频道或者进入商城后才生效!");
			cm.dispose();
				}
				}
        }
    }

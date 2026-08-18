/* ==================
 脚本类型: NPC	    
 脚本作者: 一线海团队-维多利亚 
 联系扣扣: 462110111
 =====================
 */

var l = ["日","一","二","三","四","五","六"];
var d = new Date().getDay();
var str = "星期" + l[d];

var 双倍卡代码 = 5210002;
var 精灵项链代码 = 1122017;
var 双爆卡代码 = 5360015;
var 宠吸卡代码 = 3700124;
var 金币数 = 1000000;
var 双倍点卷 = 1888;
var 双倍点卷1 = 4000;
var 双倍点卷2 = 10000;
var 精灵项链点卷 = 9999;
var 双爆点卷 = 18888;
var 宠吸点卷 = 1888;
var 宠吸天卡点卷 = 18888;
var 双倍卡时间 = 3;//小时
var 双倍卡时间1 = 6;//小时
var 双倍卡时间2 = 1;//小时
var 精灵项链时间 = 12;//小时
var 双爆卡时间 = 1;//小时
var 宠吸卡时间 = 3;
var 宠吸天卡时间 = 24;
var 双倍次数 = 1;
var 双倍次数1 = 1;
var 双倍次数2 = 1;
var 精灵项链次数 = 1;
var 双爆次数 = 1;
var 宠吸次数 = 4;
var 宠吸天卡次数 = 3;

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
			text += "#k您当前拥有 #r"+cm.getPlayer().getCSPoints(1)+" #k点卷 \t#b注：购买后换线生效#k\r\n\r\n"
           // if (d == 1 ||d == 3 ||d == 5){//判断礼拜1  3  5
			/*text += "#L1##e#d购买"+双倍卡时间+"小时#v"+双倍卡代码+"##n#r("+双倍点卷+"点卷+"+金币数+"金币)#l#n\r\n\r\n"
			text += "#k每天最多允许购买:#r"+双倍次数+"#k次#b#r\t"
			text += "#k今天已经购买了:#r"+cm.getBossLog("双倍记录")+"#k次#b#r\r\n\r\n"

			text += "#L10##e#d购买"+双倍卡时间1+"小时#v"+双倍卡代码+"##n#r("+双倍点卷1+"点卷+"+金币数+"金币)#l#n\r\n\r\n"
			text += "#k每天最多允许购买:#r"+双倍次数1+"#k次#b#r\t"
			text += "#k今天已经购买了:#r"+cm.getBossLog("双倍记录1")+"#k次#b#r\r\n\r\n"*/

			text += "#L11##e#d购买"+双倍卡时间2+"天#v"+双倍卡代码+"##n#r("+双倍点卷2+"点卷+"+金币数+"金币)#l#n\r\n\r\n"
			text += "#k每天最多允许购买:#r"+双倍次数2+"#k次#b#r\t"
			text += "#k今天已经购买了:#r"+cm.getBossLog("双倍记录2")+"#k次#b#r\r\n\r\n"

			/*text += "#L5##e#d购买"+精灵项链时间+"小时#v"+精灵项链代码+"##n#r("+精灵项链点卷+"点卷+"+金币数+"金币)#l\r\n\r\n"
			text += "#n#k每天最多允许购买:#r"+精灵项链次数+"#k次#b#r\t"
			text += "#k今天已经购买了:#r"+cm.getBossLog("精灵项链记录")+"#k次#b#r\r\n\r\n"*/

			//cm.sendOk(text); 
			//} else if (d == 2 ||d == 4 ||d == 6){//判断礼拜2  4  6
			text += "#L2##e#d购买"+双爆卡时间+"天#v"+双爆卡代码+"##n#r("+双爆点卷+"点卷+"+金币数+"金币)#l\r\n\r\n"
			text += "#n#k每天最多允许购买:#r"+双爆次数+"#k次#b#r\t"
			text += "#k今天已经购买了:#r"+cm.getBossLog("双爆记录")+"#k次#b#r\r\n"
			text += "#L22##e#d购买#v5030000##n#r(10000点卷+"+金币数+"金币)#l\r\n\r\n"
			//text += "#n#k每天最多允许购买:#r"+精灵项链次数+"#k次#b#r\t"
			//text += "#k今天已经购买了:#r"+cm.getBossLog("精灵项链记录")+"#k次#b#r\r\n\r\n"
			cm.sendOk(text); 

        } else if (selection == 1) {
			if (cm.getInventory(5).isFull(0)){//判断第五个也就是现金栏的装备栏是否有一个空格
            cm.sendOk("#b请保证现金栏位至少有1个空格,否则无法购买！");		
            cm.dispose();
            } else if (cm.getPlayer().getCSPoints(1) < 双倍点卷){
			cm.sendOk("点卷不足"+双倍点卷+"点!");
      	    cm.dispose();
            } else if (cm.getPlayer().getMeso() < 金币数) {
			cm.sendOk("金币不足"+双倍点卷+"!");
      	    cm.dispose();
			} else if(cm.haveItem(双倍卡代码,1)){
            cm.sendOk("你的#v"+双倍卡代码+"#还未到期!");
            cm.dispose();
			} else if(cm.getBossLog("双倍记录") >= 双倍次数){//判断一天几次
			cm.sendOk("一天只能购买"+双倍次数+"次，请明天在来!");
            cm.dispose();
			} else {
			cm.getPlayer().setBossLog('双倍记录');//给一天次数记录
			cm.gainNX(-双倍点卷);
			cm.gainMeso(-金币数);
			cm.gainItem(双倍卡代码,1,3);//扣除物品
			cm.sendOk("购买成功，换频道或者进入商城后才生效!");
			cm.dispose();
				}
        } else if (selection == 10) {
			if (cm.getInventory(5).isFull(0)){//判断第五个也就是现金栏的装备栏是否有一个空格
            cm.sendOk("#b请保证现金栏位至少有1个空格,否则无法购买！");		
            cm.dispose();
            } else if (cm.getPlayer().getCSPoints(1) < 双倍点卷1){
			cm.sendOk("点卷不足"+双倍点卷1+"点!");
      	    cm.dispose();
            } else if (cm.getPlayer().getMeso() < 金币数) {
			cm.sendOk("金币不足"+双倍点卷+"!");
      	    cm.dispose();
			} else if(cm.haveItem(双倍卡代码,1)){
            cm.sendOk("你的#v"+双倍卡代码+"#还未到期!");
            cm.dispose();
			} else if(cm.getBossLog("双倍记录1") >= 双倍次数1){//判断一天几次
			cm.sendOk("一天只能购买"+双倍次数1+"次，请明天在来!");
            cm.dispose();
			} else {
			cm.getPlayer().setBossLog('双倍记录1');//给一天次数记录
			cm.gainNX(-双倍点卷1);
			cm.gainMeso(-金币数);
			cm.gainItem(双倍卡代码,1,6);//扣除物品
			cm.sendOk("购买成功，换频道或者进入商城后才生效!");
			cm.dispose();
				}
        } else if (selection == 11) {
			if (cm.getInventory(5).isFull(0)){//判断第五个也就是现金栏的装备栏是否有一个空格
            cm.sendOk("#b请保证现金栏位至少有1个空格,否则无法购买！");		
            cm.dispose();
            } else if (cm.getPlayer().getCSPoints(1) < 双倍点卷2){
			cm.sendOk("点卷不足"+双倍点卷2+"点!");
      	    cm.dispose();
            } else if (cm.getPlayer().getMeso() < 金币数) {
			cm.sendOk("金币不足"+双倍点卷+"!");
      	    cm.dispose();
			} else if(cm.haveItem(双倍卡代码,1)){
            cm.sendOk("你的#v"+双倍卡代码+"#还未到期!");
            cm.dispose();
			} else if(cm.getBossLog("双倍记录2") >= 双倍次数2){//判断一天几次
			cm.sendOk("一天只能购买"+双倍次数2+"次，请明天在来!");
            cm.dispose();
			} else {
			cm.getPlayer().setBossLog('双倍记录2');//给一天次数记录
			cm.gainNX(-双倍点卷2);
			cm.gainMeso(-金币数);
			cm.gainItem(双倍卡代码,1,1);//扣除物品
			cm.sendOk("购买成功，换频道或者进入商城后才生效!");
			cm.dispose();
				}
        } else if (selection == 2) {
			if (cm.getInventory(5).isFull(0)){//判断第五个也就是现金栏的装备栏是否有一个空格
            cm.sendOk("#b请保证现金栏位至少有1个空格,否则无法购买！");		
            cm.dispose();
            } else if (cm.getPlayer().getCSPoints(1) < 双爆点卷){
			cm.sendOk("点卷不足"+双爆点卷+"点!");
      	    cm.dispose();
            } else if (cm.getPlayer().getMeso() < 金币数) {
			cm.sendOk("金币不足"+双倍点卷+"!");
      	    cm.dispose();
			} else if(cm.haveItem(双爆卡代码,1)){
            cm.sendOk("你的#v"+双爆卡代码+"#还未到期!");
            cm.dispose();
			} else if(cm.getBossLog("双爆记录") >= 双爆次数){//判断一天几次
			cm.sendOk("一天只能购买"+双爆次数+"次，请明天在来!");
            cm.dispose();
			} else {
			cm.getPlayer().setBossLog('双爆记录');//给一天次数记录
			cm.gainNX(-双爆点卷);
			cm.gainMeso(-金币数);
			cm.gainItem(双爆卡代码,1,1);//扣除物品
			cm.sendOk("购买成功，换频道或者进入商城后才生效!");
			cm.dispose();
				}
        } else if (selection == 22) {
			if (cm.getInventory(5).isFull(0)){//判断第五个也就是现金栏的装备栏是否有一个空格
            cm.sendOk("#b请保证现金栏位至少有1个空格,否则无法购买！");		
            cm.dispose();
            } else if (cm.getPlayer().getCSPoints(1) < 10000){
			cm.sendOk("点卷不足10000点!");
      	    cm.dispose();
            } else if (cm.getPlayer().getMeso() < 金币数) {
			cm.sendOk("金币不足"+双倍点卷+"!");
      	    cm.dispose();
			/*} else if(cm.haveItem(双爆卡代码,1)){
            cm.sendOk("你的#v"+双爆卡代码+"#还未到期!");
            cm.dispose();*/
			/*} else if(cm.getBossLog("双爆记录") >= 双爆次数){//判断一天几次
			cm.sendOk("一天只能购买"+双爆次数+"次，请明天在来!");
            cm.dispose();*/
			} else {
			//cm.getPlayer().setBossLog('双爆记录');//给一天次数记录
			cm.gainNX(-10000);
			cm.gainMeso(-金币数);
			cm.gainItem(5030000,1);//扣除物品
			cm.sendOk("购买成功，换频道或者进入商城后才生效!");
			cm.dispose();
				}
/*		} else if (selection == 3) {
			if (cm.getInventory(3).isFull(0)){//判断第五个也就是现金栏的装备栏是否有一个空格
            cm.sendOk("#b请保证设置栏位至少有1个空格,否则无法购买！");		
            cm.dispose();
            } else if (cm.getPlayer().getCSPoints(1) < 宠吸点卷){
			cm.sendOk("点卷不足"+宠吸点卷+"点!");
      	    cm.dispose();
			} else if(cm.haveItem(宠吸卡代码,1)){
            cm.sendOk("你的#v"+宠吸卡代码+"#还未到期!");
            cm.dispose();
			} else if(cm.getBossLog("宠吸记录") >= 宠吸次数){//判断一天几次
			cm.sendOk("一天只能购买"+宠吸次数+"次，请明天在来!");
            cm.dispose();
			} else {
			cm.getPlayer().setBossLog('宠吸记录');//给一天次数记录
			cm.gainNX(-宠吸点卷);
			cm.gainMeso(-金币数);
			cm.gainItem(宠吸卡代码,1,3);//扣除物品
			cm.sendOk("购买成功，换频道或者进入商城后才生效!");
			cm.dispose();
				}
		} else if (selection == 4) {
			if (cm.getInventory(3).isFull(0)){//判断第五个也就是现金栏的装备栏是否有一个空格
            cm.sendOk("#b请保证设置栏位至少有1个空格,否则无法购买！");		
            cm.dispose();
            } else if (cm.getPlayer().getCSPoints(1) < 宠吸天卡点卷){
			cm.sendOk("点卷不足"+宠吸天卡点卷+"点!");
      	    cm.dispose();
			} else if(cm.haveItem(宠吸卡代码,1)){
            cm.sendOk("你的#v"+宠吸卡代码+"#还未到期!");
            cm.dispose();
			} else if(cm.getBossLog("宠吸记录") >= 宠吸天卡次数){//判断一天几次
			cm.sendOk("一天只能购买"+宠吸天卡次数+"次，请明天在来!");
            cm.dispose();
			} else {
			cm.getPlayer().setBossLog('宠吸天卡记录');//给一天次数记录
			cm.gainNX(-宠吸天卡点卷);
			cm.gainMeso(-金币数);
			cm.gainItem(宠吸卡代码,1,24);//扣除物品
			cm.sendOk("购买成功，换频道或者进入商城后才生效!");
			cm.dispose();
				}*/
		} else if (selection == 5) {
			if (cm.getInventory(1).isFull(0)){//判断第五个也就是现金栏的装备栏是否有一个空格
            cm.sendOk("#b请保证装备栏位至少有1个空格,否则无法购买！");		
            cm.dispose();
            } else if (cm.getPlayer().getCSPoints(1) < 精灵项链点卷){
			cm.sendOk("点卷不足"+精灵项链点卷+"点!");
      	    cm.dispose();
            } else if (cm.getPlayer().getMeso() < 金币数) {
			cm.sendOk("金币不足"+双倍点卷+"!");
      	    cm.dispose();
			} else if(cm.haveItem(精灵项链代码,1)){
            cm.sendOk("你的#v"+精灵项链代码+"#还未到期!");
            cm.dispose();
			} else if(cm.getBossLog("精灵项链记录") >= 精灵项链次数){//判断一天几次
			cm.sendOk("一天只能购买"+精灵项链次数+"次，请明天在来!");
            cm.dispose();
			} else {
			cm.getPlayer().setBossLog('精灵项链记录');//给一天次数记录
			cm.gainNX(-精灵项链点卷);
			cm.gainMeso(-金币数);
			cm.gainItem(精灵项链代码,0,0,0,0,0,0,0,0,0,0,0,0,0,0,12);//扣除物品
			cm.sendOk("购买成功，换频道或者进入商城后才生效!");
			cm.dispose();
				}
				}
        }
    }

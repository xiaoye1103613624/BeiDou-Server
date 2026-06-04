/* ==================
 脚本类型: NPC	    
 脚本作者: 一线海团队-维多利亚 
 联系扣扣: 297870163
 =====================
 */


var l = ["日","一","二","三","四","五","六"];
var d = new Date().getDay();
var str = "星期" + l[d];

var 双倍卡代码 = 5211047;
var 双爆卡代码 = 5360015;
var 双爆卡代码1 = 5360015;
var 双倍点券 = 200;
var 双爆点券 = 200;
var 双爆点券1 = 300;
var 双倍卡时间 = 3;//小时
var 双爆卡时间 = 1;//小时
var 双爆卡时间1 = 1;//小时
var 双倍次数 = 3;
var 双爆次数 = 3;
var 双爆次数1 = 1;

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
			text += "#k欢迎来到双倍商城！\t#r\r\n"
			//text += "#k#v"+双倍卡代码+"#双倍经验卡最多允许购买:#r"+双倍次数+"#k张#b#r\r\n"
			//text += "#k今天天已经购买了:#r"+cm.getPlayer().getBossLog("双倍1")+"#k次#b#r\r\n"
			text += "#L1##r领取,#k#d"+双倍卡时间+"小时#v"+双倍卡代码+"#[#r"+cm.getBossLog("免费双倍")+"#k/1]#l\r\n#L2##r购买,#d"+双倍卡时间+"小时#v"+双倍卡代码+"#("+双倍点券+"枫叶)#l\r\n\r\n"

			//text += "#n#k#v"+双爆卡代码+"#双倍爆率卡最多允许购买:#r"+双爆次数+"#k张#b#r\r\n"
			//text += "#k今天天已经购买了:#r"+cm.getPlayer().getAcDayLog("双倍2")+"#k次#b#r\r\n"
			//text += "#L2##e#d购买"+双爆卡时间+"小时#v"+双爆卡代码+"#("+双爆点券+"抵用)#l\r\n\r\n"
			
			/*text +="#k#v"+双爆卡代码1+"#最多允许购买:#r"+双爆次数1+"#k张#b#r\r\n"
			text += "#k今天天已经购买了:#r"+cm.getPlayer().getBossDayLog("双爆11")+"#k次#b#r\r\n"
			text += "#L3##e#d购买"+双爆卡时间1+"小时#v"+双爆卡代码1+"#("+双爆点券1+"点券)#l\r\n\r\n"*/
			cm.sendOk(text); 

        } else if (selection == 1) {
			if (cm.getInventory(5).isFull(0)){//判断第五个也就是现金栏的装备栏是否有一个空格
            cm.sendOk("#b请保证现金栏位至少有1个空格,否则无法购买！");		
            cm.dispose();
         //   } else if (cm.getPlayer().getDY <= 双倍点券){
		//	cm.sendOk("抵用不足"+双倍点券+"点!");
      	 //   cm.dispose();
			} else if(cm.haveItem(双倍卡代码,1)){
            cm.sendOk("你的#v"+双倍卡代码+"#还未到期!");
            cm.dispose();
			} else if(cm.getPlayer().getBossLog("免费双倍") >0){//判断一天几次
			cm.sendOk("今日已经领取过一次了!");
            cm.dispose();
			} else {
			cm.getPlayer().setBossLog("免费双倍");//给一天次数记录
			//cm.gainDY(-双倍点券);
			cm.gainItem(双倍卡代码,1,双倍卡时间,true);//扣除物品
			cm.sendOk("领取今日份免费双倍卡 3小时成功");
			cm.dispose();
				}
        } else if (selection == 2) {
			if (cm.getInventory(5).isFull(0)){//判断第五个也就是现金栏的装备栏是否有一个空格
            cm.sendOk("#b请保证现金栏位至少有1个空格,否则无法购买！");		
            cm.dispose();
           } else if (cm.haveItem(4001126,200) ==false){
	    	cm.sendOk("枫叶不足 200!");
      	    cm.dispose();
			} else if(cm.haveItem(双爆卡代码,1)){
            cm.sendOk("你的#v"+双爆卡代码+"#还未到期!");
            cm.dispose();
		//	} else if(cm.getPlayer().getAcDayLog("双倍2") >= 双爆次数){//判断一天几次
		//	cm.sendOk("一天只能购买"+双爆次数+"次，请明天在来!");
         //   cm.dispose();
			} else {
			//cm.getPlayer().isetAcDayLog("双倍2");//给一天次数记录
		//	cm.gainDY(-双爆点券);
            cm.gainItem(4001126,-200);
			cm.gainItem(双倍卡代码,1,双倍卡时间,true);//扣除物品
			cm.sendOk("购买成功，换频道或者进入商城后才生效!");
			cm.dispose();
				}
		} else if (selection == 3) {
			if (cm.getInventory(5).isFull(0)){//判断第五个也就是现金栏的装备栏是否有一个空格
            cm.sendOk("#b请保证现金栏位至少有1个空格,否则无法购买！");		
            cm.dispose();
            } else if (cm.getPlayer().getCSPoints(1) < 双爆点券1){
			cm.sendOk("点券不足"+双爆点券1+"点!");
      	    cm.dispose();
			} else if(cm.haveItem(双爆卡代码1,1)){
            cm.sendOk("你的#v"+双爆卡代码1+"#还未到期!");
            cm.dispose();
			} else if(cm.getPlayer().getAcDayLog("双爆11") >= 双爆次数1){//判断一天几次
			cm.sendOk("一天只能购买"+双爆次数1+"次，请明天在来!");
            cm.dispose();
			} else {
			cm.getPlayer().setBossLog("双爆11");//给一天次数记录
			cm.gainNX(-双爆点券1);
			cm.gainItem(双爆卡代码1,1,双爆卡时间1,true);//扣除物品
			cm.sendOk("购买成功，换频道或者进入商城后才生效!");
			cm.dispose();
				}		
		
				}
        }
    }

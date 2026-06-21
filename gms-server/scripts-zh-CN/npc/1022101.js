

function start() { 
 cm.sendOk("管理员还没有发放福利！！！"); 
 	cm.dispose();
} 

/*
//启用就开下面符号



var status = -1;
var tknow=0;
var typed=0;
var ca = java.util.Calendar.getInstance();
var year = ca.get(java.util.Calendar.YEAR); //获得年份
var month = ca.get(java.util.Calendar.MONTH) + 1; //获得月份
var day = ca.get(java.util.Calendar.DATE);//获取日
var hour = ca.get(java.util.Calendar.HOUR_OF_DAY); //获得小时
var minute = ca.get(java.util.Calendar.MINUTE);//获得分钟
var second = ca.get(java.util.Calendar.SECOND); //获得秒

var 领取补偿 = "补偿第2次";      //以后只需要改这里的数字-第1次

var 道具1 = 2049100;             //混沌卷轴60%  2张
var 数量1 = 3;

var 道具2 = 2049122;             //正向混沌卷轴 2张
var 数量2 = 4;

var 道具3 = 2460004;             //鉴定魔方 10个
var 数量3 = 1;

var 道具4 = 2614000;             //破攻2个
var 数量4 = 4;

var 道具5 = 2022530;             //迎春花语 4个
var 数量5 = 8;

var 元宝 = 0;                    //领取数量
var 点券 = 88888;
var 金币 = 0;
var 抵用 = 88888;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
    // 获取当前时间
    var ca = java.util.Calendar.getInstance();
    var hour = ca.get(java.util.Calendar.HOUR_OF_DAY); // 24小时制的小时数

    // 检查是否在20:00至22:00之间
    if (hour < 1 || hour >= 24) {
        cm.sendOk("【#r新年福利#k】领取时间为#r 00:00 至 01:00#k。\r\n\r\n当前不是领取时间！！！\r\n\r\n服务器当前时间：#r" + hour +":" + minute + ":" + second + "#n#k");
        cm.dispose();
        return;
		}
	if (mode == -1) {
		cm.dispose();
	} else {
		if ((mode == 0 && status == 2) || (mode == 0 && status == 13)) {
			cm.dispose();
			return;
		}
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
		  var text = " #k你好 ,#r ["+cm.getName()+"] #k 这里是福利中心\r\n\r\n";
		      text += " #k 领取福利可以找我\r\n\r\n";
			  text += "#r#e#L0#领取福利#n#l\r\n"
			  cm.sendSimple(text,2);
	    } else if (status == 1) {
			if (selection == 0) {
		   var text = "#r#e本次福利道具列表如下:#n#l\r\n\r\n";
		       text += "#k#v"+道具1+"##z"+道具1+"# 数量：#r["+数量1+"]#l\r\n\r\n";
			   text +="#k#v"+道具2+"##z"+道具2+"# 数量：#r["+数量2+"]#l\r\n\r\n";
			   text +="#k#v"+道具3+"##z"+道具3+"# 数量：#r["+数量3+"]#l\r\n\r\n";
			   text +="#k#v"+道具4+"##z"+道具4+"# 数量：#r["+数量4+"]#l\r\n\r\n";
			   text +="#k#v"+道具5+"##z"+道具5+"# 数量：#r["+数量5+"]#l\r\n\r\n";
			   text += "#k 点券： #r["+点券+"] #k点#l\r\n\r\n";
			   text += "#k 抵用： #r["+抵用+"] #k点#l\r\n\r\n";
			   //text += "#k 金币： #r["+金币+"] #k元#l\r\n\r\n";
			   //text += "#k 元宝： #r["+元宝+"] #k个#l\r\n\r\n\r\n\r\n";
			   text +="\t\t\t #r#e[是否要领取以上福利?请注意背包空位！]#n#l\r\n\r\n"
			   cm.sendYesNoS(text,2);
			}
		} else if (status == 2) {
			if (cm.getPlayer().getPrizeLog(""+领取补偿+"") >= 1) { 
			cm.sendOk("该账号下已经领取过福利了");
			cm.dispose();
			} else if (cm.getInventory(1).isFull(3)){
			cm.sendOk("#b请保证装备栏位至少有4个空格,否则无法领取.");
			cm.dispose();
			} else if (cm.getInventory(2).isFull(3)){
			cm.sendOk("#b请保证消耗栏位至少有4个空格,否则无法领取.");
			cm.dispose();
			} else if (cm.getInventory(3).isFull(3)){
			cm.sendOk("#b请保证设置栏位至少有4个空格,否则无法领取.");
			cm.dispose();
			} else if (cm.getInventory(4).isFull(3)){
			cm.sendOk("#b请保证其它栏位至少有4个空格,否则无法领取.");
			cm.dispose();
			} else if (cm.getInventory(5).isFull(3)){
			cm.sendOk("#b请保证现金栏位至少有4个空格,否则无法领取.");
			cm.dispose();
			return;
		} else {
			cm.getPlayer().setPrizeLog(""+领取补偿+""); 
			cm.gainNX(点券);
			cm.gainDY(抵用);
			//cm.gainMeso(金币);
			//cm.setmoneyb(+元宝);
			cm.gainItem(道具1,数量1);
			cm.gainItem(道具2,数量2);
			cm.gainItem(道具3,数量3);
			cm.gainItem(道具4,数量4);
			cm.gainItem(道具5,数量5);
			cm.sendOk("恭喜你 领取补偿成功 请打开背包查看");
		    cm.喇叭(1,"[福利中心] 玩家 ["+cm.getName()+"] 在【市场相框-活动中心】领取了老G发放的【新年福利】");
			cm.喇叭(1,"[福利中心] 玩家 ["+cm.getName()+"] 在【市场相框-活动中心】领取了老G发放的【新年福利】");
			cm.喇叭(1,"[福利中心] 玩家 ["+cm.getName()+"] 在【市场相框-活动中心】领取了老G发放的【新年福利】");
			cm.dispose();
		    }
		}
	}
}	

*/
/*
	脚本:拍卖
	by:Rice
*/


//先创建全局变量
var status;
var a1 = "#fEffect/CharacterEff/1112905/0/1#"; //小红心
var a2 = "#fEffect/CharacterEff/1051294/0/0#"; //紫色爱心
var a3 = "#fUI/UIWindowBT.img/WorldMap/BtHome/normal/0#";//黄房子
var a4 = "#fUI/UIWindowBT.img/WorldMap/Btnavi/normal/0#";//蓝→
var a5 = "#fUI/Basic.img/BtCoin/normal/0#";//金币

//模板
function start(){
    status = -1;
    action(1, 0, 0);
}
function action(mode, type, selection) {
    if (mode == 0) {
		cm.dispose();
		return;
    } else if (mode == 1){
		status++;
    } else {
		status--;
    }

	if (cm.getMapId() == 180000001) {//判断是否在小黑屋.防止越狱(一般都封号.可不写)
		cm.sendOk("很遗憾，您因为违反用户守则被禁止游戏活动，如有异议请联系管理员.");
        cm.dispose();
	} else if (status == 0) {//第1个对话
		//拍卖界面:创建变量用拼接的方式写.方便修改查看
		var selStr = a5 + "现金:#r" + cm.getMeso() + "\r\n";
			selStr += a5 + "#k点卷:#r" + cm.getPlayer().getCSPoints(1) + "\r\n";
			selStr += a5 + "#k抵用卷:#r" + cm.getPlayer().getCSPoints(2) + "\r\n#k";
			for(var a=0;a<27;a++){selStr += a1}//分割线:循环红心.减少重复代码
			selStr += "\r\n";
			selStr += "#L1#" + a3 + "#e返回市场" + "#L2#" + a4 + "万能传送" + "#L3#" + a4 + "快速转职\r\n";
			selStr += "\r\n";
			for(var a=0;a<12;a++){selStr += a2}//分割线:循环紫心.减少重复代码

		cm.sendSimple(selStr);//对话框.把变量内容传给函数
	} else if (status == 1) {//第2个对话
		switch (selection) {
			case 1://返回市场
				cm.dispose();
                cm.warp(910000000,0);//跳转地图
				break;
			case 2://万能传送
				cm.dispose();
                cm.openNpc(9900003);//跳转NPC
				break;
			case 3://快速转职
				cm.dispose();
                cm.openNpc(9900003,2);
				break;
		}
	}
}

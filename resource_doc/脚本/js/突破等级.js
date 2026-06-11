/*
漫步岛屿079
QQ870074996
 */

importPackage(Packages.server);
var status = 0;
var fee;
var chance = Math.floor(Math.random() * 1);
var juanzs =Array(
Array(100,200000),
Array(105,200000),
Array(110,200000),
Array(115,200000),
Array(120,200000),
Array(125,200000),
Array(130,200000),
Array(135,200000),
Array(140,200000),
Array(145,200000),
Array(150,200000),
Array(155,250000),
Array(160,300000),
Array(165,350000),
Array(170,400000),
Array(175,450000),
Array(180,500000),
Array(185,550000),
Array(190,5000000),
Array(195,5000000),
Array(200,5000000),
Array(205,5000000),
Array(210,20000000),
Array(215,20000000),
Array(220,20000000),
Array(225,20000000),
Array(230,20000000),
Array(235,20000000),
Array(240,20000000),
Array(245,20000000),
Array(250,20000000),
Array(255,99999999999999999999)

);
function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0) {
            cm.sendOk("你没有？");
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
            cm.sendGetText("请输入提交的数量；");
        } else if (status == 1) {
            fee = cm.getText();
			if (isNaN(cm.getText())) {
				cm.playerMessage(1, "抱歉,只能输入数字。");
                cm.dispose();
                return;
			}
			if(fee<1){
				cm.playerMessage(1, "请输入正确的数量。");
                cm.dispose();
                return;
			}
			if(fee>30000){
				cm.playerMessage(1, "一次最多提交 19999 堆火药。");
                cm.dispose();
                return;
			}
            if (!cm.判断物品数量(4001128, fee)) {
                cm.playerMessage(1, "你没有这么多强大的火药。");
                cm.dispose();
                return;
            }
            var 解除封印进度 = cm.GetPiot("解除封印进度", "1");
            cm.GainPiot("解除封印进度", "1", fee);
			cm.setBossRank("捐献数量",2,fee);
            cm.收物品(4001128, fee);
			cm.给抵用券(fee);
			//cm.给点券(fee);
			cm.worldMessage(6,"玩家：["+cm.getName()+"]捐献了"+fee+"个强大的火药，为解开等级封印之路做出了贡献获得"+fee+" 抵用券。");
			for(var i=0;i<juanzs.length;i++){
				var shuliang = 0;
				if(juanzs[i][0] == (cm.getMaxLevel()+5)){
					shuliang = juanzs[i][1];
            if (解除封印进度 >= shuliang) {
                cm.GainPiot("解除封印进度", "1", -解除封印进度);
				cm.setMaxLevel((cm.getMaxLevel()+5));
                cm.全服黄色喇叭("[全服通报] : 恭喜勇士们冲破结界，解除等级的封印！");
				cm.全服黄色喇叭("[全服通报] : 恭喜勇士们冲破结界，解除等级的封印！");
				cm.全服黄色喇叭("[全服通报] : 恭喜勇士们冲破结界，解除等级的封印！");
				cm.全服黄色喇叭("[全服通报] : 恭喜勇士们冲破结界，解除等级的封印！");
				cm.全服黄色喇叭("[全服通报] : 恭喜勇士们冲破结界，解除等级的封印！");
					}
				}
			}
            cm.dispose();
        }
    }
}
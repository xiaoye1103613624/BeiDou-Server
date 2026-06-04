/* ==================
 脚本类型: NPC	    
 脚本作者: 游戏盒团队-维多利亚 
 联系扣扣: 297870163
 =====================
 */
var status = 0;
var zones = 0;
var ItemId = Array(
    //Array(1114200, 50, 1, 1), //玛瑙1
	//Array(1114219, 50, 1, 1), //玛瑙2
	//Array(1114206, 50, 1, 1), //玛瑙3
        //物品1         物品2    货币    数量
	Array(1102968,1102723,1,2590018,1,"0")
);


function start() {
	 status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
cm.dispose();
    } else {
if (status >= 0 && mode == 0) {
    cm.dispose();
    return;
}
if (mode == 1)
    status++;
else
    status--;
if (status == 0) {
var selStr = "您好，请选择您需要兑换的物品\r\n#r★角色剩余:#v1102723# x #c1102723#个!\r\n★#r角色剩余:#v2590018# x #c2590018#枚!!\r\n";
for (var i = 0; i < ItemId.length; i++) {
    selStr += "\r\n#L" + i + "##v" + ItemId[i][0] + "#冰封之翼 四维+40 双攻+40 HPMP+458  #k\r\n\t = #r#e" + ItemId[i][2] + "#n个#v " + ItemId[i][1] + " #+#e" + ItemId[i][4] + "#n个#v " + ItemId[i][3] + " + # + 18888点券#l";
   }
cm.sendSimple(selStr);
    } else if (status == 1) {
	 if (zones == 1) {
cm.sendNext("你让我帮你做什么呢？", 2);
zones = 2;
    } else if (zones == 0) {
if (cm.getInventory(1).isFull(0)){//判断第2个也就是装备栏是否有一个空格	
	 cm.sendOk("#b请保证装备栏位至少有1个空格,否则无法购买.");	
	 cm.dispose();
} else if (!cm.haveItem(ItemId[selection][1], ItemId[selection][2])) {
	 cm.sendOk("你没有#r"+ItemId[selection][2]+"#k个#v"+ItemId[selection][1]+"##z"+ItemId[selection][1]+"#兑换。");
     cm.dispose();
} else if (!cm.haveItem(ItemId[selection][3], ItemId[selection][4])) {
	 cm.sendOk("你没有#r"+ItemId[selection][4]+"#k个#v"+ItemId[selection][3]+"##z"+ItemId[selection][3]+"#兑换。");
     cm.dispose();
// } else if (!cm.haveItem(ItemId[selection][5], ItemId[selection][6])) {
	 // cm.sendOk("你没有#r"+ItemId[selection][6]+"#k个#v"+ItemId[selection][5]+"##z"+ItemId[selection][5]+"#兑换。");
     // cm.dispose();
// } else if (!cm.haveItem(ItemId[selection][7], ItemId[selection][8])) {
	 // cm.sendOk("你没有#r"+ItemId[selection][8]+"#k个#v"+ItemId[selection][7]+"##z"+ItemId[selection][7]+"#兑换。");
     // cm.dispose();
}else if (cm.getPlayer().getCSPoints(1) < 18888) {
cm.sendOk("你没有18888点券。");
cm.dispose();
}	  else {
	 cm.gainItem(ItemId[selection][1], -ItemId[selection][2]);
	 cm.gainItem(ItemId[selection][3], -ItemId[selection][4]);
	 // cm.gainItem(ItemId[selection][5], -ItemId[selection][6]);
	 cm.gainNX(-18888);//改为扣除点券
	 // cm.gainItem(ItemId[selection][0], 1);
	 cm.给属性装备(ItemId[selection][0], 5, 0, 40, 40, 40, 40, 458, 458, 40, 40, 10, 10, 0, 0, 0, 0);
	 cm.getPlayer().saveToDB(true, true);
	 cm.sendOk("兑换成功,商品#i" + ItemId[selection][0] + ":# #b#t" + ItemId[selection][0] + "##k已送往背包。");
	 status = -1;
}
    }}}}

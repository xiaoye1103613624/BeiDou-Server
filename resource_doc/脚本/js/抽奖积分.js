/* ==================
 脚本类型: NPC	    
 脚本作者: 游戏盒团队-维多利亚 
 联系扣扣: 297870163
 =====================
 */
var status = 0;
var zones = 0;
var ItemId = Array(
	//如需其它道具兑换，请按照此格式自行添置。
	//代码,价格,介绍
	//多少数量    物品	兑换数量  要兑换的物品id 
	
	//100
	Array(   1,4310129,1,4310088),
	Array(   1,4310129,1,2049100),
	Array(   4,4310129,1,2049124),
	Array(   5,4310129,1,2049104),
	Array(  10,4310129,10,4310088),
	Array(  10,4310129,10,2049100),
	Array(  40,4310129,10,2049124),
	Array(  40,4310129,1,1112426),
	Array(  40,4310129,1,1022066),
	Array(  40,4310129,1,1032061),
	Array( 50,4310129,1,1122265),
	Array( 50,4310129,1,2022522),
	Array( 50,4310129,10,2049104),
	Array( 100,4310129,1,2079995),
	Array( 100,4310129,1,1022132),
	Array( 100,4310129,1,1032222),
	Array( 100,4310129,1,1122266),
	Array( 300,4310129,1,1113075),
	Array( 300,4310129,1,1112672),
	Array(1000,4310129,1,1032129),
	Array(1000,4310129,1,1122185),
	Array(1000,4310129,1,1132135)
	
	
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
	sl = cm.getBossRankCount2("抽奖积分");
var selStr = "您好，请选择您需要兑换的物品\r\n\r\n";
	selStr += "你有"+sl+"积分";
for (var i = 0; i < ItemId.length; i++) {
    selStr += "\r\n#L" + i + " ##k" + ItemId[i][0] + "积分#d兑换#r" + ItemId[i][2] + "个#v" + ItemId[i][3] + "##z" + ItemId[i][3] + "##k#l";
   }
cm.sendSimple(selStr);
    } else if (status == 1) {
		选中 = selection;
		 cm.sendYesNo("你确认你要使用" +ItemId[选中][0] + "积分#d兑换#r" + ItemId[选中][2] + "个#v" + ItemId[选中][3] + "#吗?");
	} else if (status == 2) {

					if (cm.getInventory(1).isFull(0)){//判断第2张也就是装备栏是否有一张空格	
										cm.sendOk("#b请保证装备栏位至少有1张空格,否则无法兑换.");	
										cm.dispose();
					} else if (cm.getInventory(2).isFull(0)){//判断第2张也就是装备栏是否有一张空格	
										cm.sendOk("#b请保证消耗栏位至少有1张空格,否则无法兑换.");	
										cm.dispose();
					} else if (cm.getInventory(3).isFull(0)){//判断第2张也就是装备栏是否有一张空格	
										cm.sendOk("#b请保证设置栏位至少有1张空格,否则无法兑换.");	
										cm.dispose();
					} else if (cm.getInventory(4).isFull(0)){//判断第2张也就是装备栏是否有一张空格	
										cm.sendOk("#b请保证其它栏位至少有1张空格,否则无法兑换.");	
										cm.dispose();
					} else if (cm.getInventory(5).isFull(0)){//判断第2张也就是装备栏是否有一张空格	
										cm.sendOk("#b请保证现金栏位至少有1张空格,否则无法兑换.");	
										cm.dispose();
					}else if (sl<ItemId[选中][0]) {
						cm.sendOk("你没有"+ItemId[选中][0]+"积分");
						cm.dispose();
					return;
					 } else {
                            var thisItemId = ItemId[选中][3];
							//var ii = MapleItemInformationProvider.getInstance();
							//var toDrop = ii.randomizeStats(ii.getEquipById(ItemId[选中][3])).copy();
							cm.setBossRankCount2("抽奖积分",-ItemId[选中][0]);
							// if(thisItemId == 2000005){
						      // cm.gainItem(ItemId[选中][3], ItemId[选中][2]);
							// }else{	
							  cm.gainItem(ItemId[选中][3],  ItemId[选中][2], true);
							// }
							//MapleInventoryManipulator.addFromDrop(cm.getC(), toDrop, false);
							
							//cm.gainItem(ItemId[选中][1], -ItemId[选中][0]);
							//cm.gainItem(ItemId[选中][3], ItemId[选中][2]);
							
							cm.getPlayer().saveToDB(true, true);
							cm.sendOk("兑换成功," + ItemId[选中][2] + "个#v" + ItemId[选中][3] +  "##k已送往背包。");
							status = -1;
					 }
                   
                   
}
    }}

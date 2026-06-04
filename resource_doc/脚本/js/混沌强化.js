load("nashorn:mozilla_compat.js");
importPackage(Packages.client);
importPackage(Packages.client.inventory);
importPackage(Packages.server);
importPackage(Packages.tools);


var status = 0;
var job;
var DJ = "15000"; //扣除的点卷
var 高等五彩水晶 = "4251202"; //扣除的点卷

var ttt = "#fUI/UIWindow.img/Quest/icon9/0#";
var xxx = "#fUI/UIWindow.img/Quest/icon8/0#";
var sss = "#fUI/UIWindow.img/QuestIcon/3/0#";




//function start(){
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

            var textz = "#e#b            #v4310174#进阶混沌系统#v4310174#\r\n\r\n";
            
           textz += "#r强混介绍:每件装备最多强化120次,成功几率50,失败扣除材料,属性不变\r\n";
		   textz += "#k强混需求:#v4310174#2枚,装备可砸卷需求次数1次\r\n\r\n";
		   textz += "#k强混范围:除武器外所有装备(不包括时装)\r\n\r\n";
		 
		   textz += "#r#L3#『我要强混装备(武器除外)』\r\n";
		  //textz += "#r#L1#『我要减少一次装备砸卷次数#v"+cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getItemId()+"#』\r\n";

            //     textz += "#r#L1#提高装备攻击力 #k+1需要#r1#b个#z4251200#\r\n";
            cm.sendSimple(textz);


        } else if (status == 1) {

            if (selection == 0) { //材料进阶在装备上
               if (!cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy().getLevel() !=0) {
cm.sendOk("你的装备已砸卷次数为0");
cm.dispose();
                } else if (cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1) == null) {
                    cm.sendOk("请把要进阶的装备放在第一格才能进行.");
                    cm.dispose();
					//} else if (getItemType(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getItemId()) == 7) {
                  // cm.sendOk("武器不在进阶范围内.");
                  // cm.dispose();
              } else if(!cm.haveItem(4000463,2)){
					cm.sendOk("#v4000463#物品数量不足2个！");
					cm.dispose();
                } else {
                    var statup = new java.util.ArrayList();
                    cm.gainItem(4000463, -2);
                    var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                    item.setUpgradeSlots(item.getLevel() - 1);
                 item.setStr(item.getStr()+0);//力量
		 item.setDex(item.getDex()+0);//敏捷
		 item.setInt(item.getInt()+0);//智力
                 item.setLuk(item.getLuk()+0);//运气
                 item.setWatk(item.getWatk()+0);//攻击力
                 item.setMatk(item.getMatk()+0);//魔法力
                 item.setHp(item.getHp() + 0); //血量
                 item.setMp(item.getMp() + 0); //蓝量

                    MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                    MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
                    cm.喇叭(4, "" + cm.getPlayer().getName() + ":★★装备已砸券次数-1,继续混沌吧★★>");

                    //	cm.sendOk("#r#e进阶成功,祝您游戏愉快!#k");
                    cm.dispose();
                }
              } else if (selection == 2) {

			  if (cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1) == null) {
                    cm.sendOk("请把要进阶的装备放在第一格才能进行.");
                    cm.dispose();
             
  } else if (cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getExpiration() == 1) {
                    cm.sendOk("限时装备不能使用该功能.");
                    cm.dispose();

					} else if (getItemType(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getItemId()) == 7) {
                    cm.sendOk("武器不在进阶范围内.");
                    cm.dispose();
					}  else  if (cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy().getUpgradeSlots() ==0) {
cm.sendOk("你的装备可升级次数不够");
cm.dispose();
					} else if(!cm.haveItem(4310174,2)){
					cm.sendOk("#v4310174#物品数量不足2个！");
					cm.dispose();
				} else {
	            var statup = new java.util.ArrayList();
                   
                    var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
					var itemId = item.getItemId();
                    var level = cm.getItemLevel(itemId);
                    var ii = MapleItemInformationProvider.getInstance();
					sj = Math.floor(Math.random()*100);

                    if(sj <= 100){//随机成功
						cm.gainItem(4310174, -2);
                  var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                 item.setUpgradeSlots(item.getUpgradeSlots() - 1);
                 item.setStr(item.getStr()+5);//力量
		         item.setDex(item.getDex()+5);//敏捷
		         item.setInt(item.getInt()+5);//智力
                 item.setLuk(item.getLuk()+5);//运气
                 item.setWatk(item.getWatk()+5);//攻击力
                 item.setMatk(item.getMatk()+5);//魔法力
				 item.setLevel((item.getLevel() + 1));
                    MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                    MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
					cm.sendOk("成功了！");
                    cm.喇叭(1, "" + cm.getPlayer().getName() + ":     ★★进阶混沌成功,全属性+5★★>");
					cm.dispose();
					} else {//随机失败
						cm.gainItem(4310174, -2);
                 
					cm.sendOk("强混失败了！");
                    cm.喇叭(1,  "" + cm.getPlayer().getName() + ":     ★★进阶混沌失败,材料扣除★★>");
						cm.dispose();
					}}
					 } else if (selection == 3) {

               if (cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getLevel() >=125) {
cm.sendOk("你的装备已砸卷次数大于125");
cm.dispose();

                } else if (cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1) == null) {
                    cm.sendOk("请把要进阶的装备放在第一格才能进行.");
                    cm.dispose();
					} else if (cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getExpiration() == 1) {
                    cm.sendOk("限时装备不能使用该功能.");
                    cm.dispose();

					} else if (getItemType(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getItemId()) == 7) {
                    cm.sendOk("武器不在进阶范围内.");
                    cm.dispose();
					} else if(!cm.haveItem(4000463,2)){
					cm.sendOk("#v4310174#物品数量不足2个！");
					cm.dispose();
					}  else  if (cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy().getUpgradeSlots() ==0) {
cm.sendOk("你的装备可升级次数不够");
cm.dispose();
				} else {
	            var statup = new java.util.ArrayList();
                   
                    var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
					var itemId = item.getItemId();
                    var level = cm.getItemLevel(itemId);
                    var ii = MapleItemInformationProvider.getInstance();
					sj = Math.floor(Math.random()*100);

                    if(sj <= 100){//随机成功
						cm.gainItem(4310174, -2);
                  var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                 item.setUpgradeSlots(item.getUpgradeSlots() - 1);
                 item.setStr(item.getStr()+5);//力量
		         item.setDex(item.getDex()+5);//敏捷
		         item.setInt(item.getInt()+5);//智力
                 item.setLuk(item.getLuk()+5);//运气
                 item.setWatk(item.getWatk()+5);//攻击力
                 item.setMatk(item.getMatk()+5);//魔法力
				 item.setLevel((item.getLevel() + 1));
                    MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                    MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
                    cm.喇叭(1, "" + cm.getPlayer().getName() + ":     ★★进阶混沌成功，次数-１,全属性+5★★>");
						cm.sendOk("成功了！");
						 cm.dispose();
					} else {//随机失败
						cm.gainItem(4310174, -2);
                  var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                 //item.setUpgradeSlots(item.getUpgradeSlots() - 1);
                 item.setStr(item.getStr()-0);//力量
		         item.setDex(item.getDex()-0);//敏捷
		         item.setInt(item.getInt()-0);//智力
                 item.setLuk(item.getLuk()-0);//运气
                 item.setWatk(item.getWatk()-0);//攻击力
                 item.setMatk(item.getMatk()-0);//魔法力
				// item.setLevel((item.getLevel() + 1));
				
                    MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                    MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
                    cm.喇叭(1,  "" + cm.getPlayer().getName() + ":     ★★进阶混沌失败，,全属性-0★★>");
					cm.sendOk("失败了！");
						cm.dispose();
					}
                }



            }
        }
    }
}

//获取装备类型
function getItemType(itemid) {
    var type = Math.floor(itemid / 10000);
    switch (type) {
        case 100:
            return 0; //帽子
        case 104:
            return 1; //上衣
        case 105:
            return 2; //套装
        case 106:
            return 3; //裤裙
        case 107:
            return 4; //鞋子
        case 108:
            return 5; //手套
        case 110:
            return 6; //披风
        case 115:
            return 8; //护肩
        case 111:
            return 9; //ring
        default:
            if (type == 120) return -1; //图腾
            if (type == 135) return -1; //副手
            var type = Math.floor(type / 10);
            if (type == 12 || type == 13 || type == 14 || type == 15 || type == 17) {
                return 7; //武器
            }
            return -1;
    }
}

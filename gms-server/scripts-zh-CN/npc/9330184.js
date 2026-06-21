load("nashorn:mozilla_compat.js");
importPackage(Packages.client);
importPackage(Packages.client.inventory);
importPackage(Packages.server);
importPackage(Packages.tools);
var 黄左括号 ="#fUI/UIWindow/createCygnus/BtLeft/mouseOver/0#" 
var 黑左括号 ="#fUI/UIWindow/createCygnus/BtLeft/disabled/0#" 
var 黄右括号 ="#fUI/UIWindow/createCygnus/BtRight/mouseOver/0#"
var 黑右括号 ="#fUI/UIWindow/createCygnus/BtRight/disabled/0#"
var status = 0;
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
			var ii = Packages.server.MapleItemInformationProvider.getInstance();
 if(cm.getInventory(1).getItem(1)==null || cm.isCash(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getItemId())) {
            cm.sendOk("对不起,你的装备栏第一个格子里面没有装备,或者是时装");
            cm.dispose();
            return;
		}
		
             var textz = "   当前装备等级:" + cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getHands() + "      可强化至等级15\r\n\r\n";

if (cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getHands() < 10 ){
		   textz += "#r#L1##e#r"+黄右括号+"增加概率#v2460005#       #L2##e#r"+黄右括号+"取出卷轴#v2460005##l \r\n";
              // textz += "#r#L4##e#r开始强化#v" + cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getItemId() + "#[当前几率60%+额外几率(" + cm.getPlayer(). get输出Log('概率')*5 + ")%]\r\n\r\n";
	
			} else {	
              // textz += "#r#L7##e#r开始强化#v" + cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getItemId() + "#[几率50%(固定几率不能提升)]\r\n\r\n";
		
			}	
	//	textz += "#r#L1##e#r"+黄右括号+"增加概率       #L2##e#r"+黄右括号+"取出卷轴#l \r\n";
		if (cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getHands() < 10 ){
               textz += "#r#L4##e#r开始强化#v" + cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getItemId() + "#[当前几率10%+额外几率(" + cm.getPlayer(). get输出Log('概率')*5 + ")%]\r\n\r\n";
	
			} else {	
               textz += "#r#L7##e#r强化#v" + cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getItemId() + "#[11-15星星成功几率50%40%30%20%10%]\r\n\r\n";
			 // textz += "#k强化需求材料#v4001126#*10\r\n\r\n";
		
		
		textz += "#r#L3##e#r"+黄右括号+"普通防爆#v2531000#[#b" + cm.getPlayer(). get输出Log('普通防爆') + "]   #L6##e#r"+黄右括号+"高级防爆#v2531000#[#b" + cm.getPlayer(). get输出Log('高级防爆') + "]\r\n";
		}
		//textz += "#r#L4##e#r开始强化#v" + cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getItemId() + "#[当前几率60%+额外几率(" + (60+cm.getPlayer(). get输出Log('概率')*5) + ")%]\r\n\r\n";

		textz += "#r#L5##e#r【强化说明】        #L10##e#r【强化介绍】#l\r\n\r\n";

		textz += "#k--------------------------------------\r\n";
		textz += "#k手续费:1-10星3万点券 10-15星5万点券\r\n\r\n";
		textz += "#k普通防爆:#v2531000# 高级防爆:#v2531000#\r\n\r\n";



	

		cm.sendSimple (textz);  
        } else if (status == 1) {
            if (selection == 1) {
                if (cm.haveItem(2460005, 1)) {
                    cm.gainItem(2460005, -1);
			 cm.getPlayer().set输出Log('概率',1,1)	;
 cm.sendOk("使用#v2460005#增加5%概率！");
  	 status = -1; 
                  
                } else {
                    cm.sendOk("您的材料不足！");
					 status = -1; 
                  
                }
            } else if (selection == 2) { 
                if (cm.getPlayer(). get输出Log('概率')>=1) {
                    cm.getPlayer().set输出Log("概率",1,-1);
					 cm.gainItem(2460005, 1);
 cm.sendOk("取出#v2460005#成功！");
                   	 status = -1; 
                } else {
                    cm.sendOk("您没有放入#v2460005#！");
                  	 status = -1; 
                }
            } else if (selection == 3) {//防爆
                 //if (cm.haveItem(2531000, 1) && cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getHands()>=10 && cm.getPlayer(). get输出Log('普通防爆')<1) {
				if (cm.haveItem(2531000, 1) && cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getHands()<10 && cm.getPlayer(). get输出Log('普通防爆')<1) {
                    cm.gainItem(2531000, -1);
					 cm.getPlayer().set输出Log('普通防爆',1,1)	
					  cm.sendOk("使用防爆成功！");
 status = -1; 
 
                } else {
                    //cm.sendOk("您的材料不足,或者装备等级没有达到10级,或者已经添加过一次！");
					cm.sendOk("您的材料不足,或者装备强化等级大于等于10级,或者已经添加过一次！");
                     status = -1; 
                }
            } else if (selection == 4) {
                if (cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getHands()==0) {
                    if (cm.getPlayer().getCSPoints(1)<30000) {
                    cm.sendOk("你的点券不足30000");
                    cm.dispose();
					
					 } else {
                    var statup = new java.util.ArrayList();
                   
					cm.gainNX(-30000);
					sj = Math.floor(Math.random()*100);
					几率 = (10+cm.getPlayer(). get输出Log('概率')*5) ;
                     if(sj <=几率){//随机成功
					  if (getItemType(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getItemId()) == 7) {
                   
                   var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                   item.setStr(item.getStr()+5);//力量
		           item.setDex(item.getDex()+5);//敏捷
		           item.setInt(item.getInt()+5);//智力
                   item.setLuk(item.getLuk()+5);//运气
				   item.setHands(item.getHands()+1);//手记
				   item.setWatk(item.getWatk()+5);//攻击
                   item.setMatk(item.getMatk()+5);//魔法力
				  item.setOwner("1☆");
                  MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                  MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
				  cm.getPlayer().set输出Log("概率",1,-cm.getPlayer(). get输出Log("概率",1));
				  cm.sendOk("升星成功了！");
				  cm.喇叭(1," 恭喜玩家:<"+cm.getName()+">成功将装备升为1☆,大家为他鼓掌吧!");
                  cm.dispose();
				   } else {
					    var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                   item.setStr(item.getStr()+5);//力量
		           item.setDex(item.getDex()+5);//敏捷
		           item.setInt(item.getInt()+5);//智力
                   item.setLuk(item.getLuk()+5);//运气
				   item.setHands(item.getHands()+1);//手记
				   item.setWatk(item.getWatk()+5);//攻击
                   item.setMatk(item.getMatk()+5);//魔法力
				   item.setWdef(item.getWdef()+1);//物理防御
				  item.setOwner("1☆");
                  MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                  MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
				  cm.getPlayer().set输出Log("概率",1,-cm.getPlayer(). get输出Log("概率",1));
				  cm.sendOk("升星成功了！");
				   cm.喇叭(1," 恭喜玩家:<"+cm.getName()+">成功将装备升为1☆,大家为他鼓掌吧!");
                  cm.dispose();
				  }
				  } else {
                    cm.sendOk("升星失败了！");
					cm.getPlayer().set输出Log("概率",1,-cm.getPlayer(). get输出Log("概率",1));
                   
						cm.dispose();
					}}
				} else if(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getHands()==1) {
                     if (cm.getPlayer().getCSPoints(1)<30000) {
                    cm.sendOk("你的点券不足30000");
                    cm.dispose();
					 
					 } else {
                    var statup = new java.util.ArrayList();
                   
					cm.gainNX(-30000);
					sj = Math.floor(Math.random()*100);
					几率 = (10+cm.getPlayer(). get输出Log('概率')*5) ;
                     if(sj <=几率){//随机成功
					 if (getItemType(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getItemId()) == 7) {
                   var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                   item.setStr(item.getStr()+5);//力量
		           item.setDex(item.getDex()+5);//敏捷
		           item.setInt(item.getInt()+5);//智力
                   item.setLuk(item.getLuk()+5);//运气
				   item.setHands(item.getHands()+1);//手记
				   item.setWatk(item.getWatk()+5);//攻击
                   item.setMatk(item.getMatk()+5);//魔法力
				   
				  item.setOwner("2☆");
                  MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                  MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
				  cm.sendOk("升星成功了！");
				   cm.getPlayer().set输出Log("概率",1,-cm.getPlayer(). get输出Log("概率",1));
				    cm.喇叭(1," 恭喜玩家:<"+cm.getName()+">成功将装备升为2☆,大家为他鼓掌吧!");
                  cm.dispose();
				   } else {
					    var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                   item.setStr(item.getStr()+5);//力量
		           item.setDex(item.getDex()+5);//敏捷
		           item.setInt(item.getInt()+5);//智力
                   item.setLuk(item.getLuk()+5);//运气
				   item.setHands(item.getHands()+1);//手记
				   item.setWatk(item.getWatk()+5);//攻击
                   item.setMatk(item.getMatk()+5);//魔法力
				  item.setOwner("2☆");
                  MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                  MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
				  cm.sendOk("升星成功了！");
				   cm.喇叭(1," 恭喜玩家:<"+cm.getName()+">成功将装备升为2☆,大家为他鼓掌吧!");
				   cm.getPlayer().set输出Log("概率",1,-cm.getPlayer(). get输出Log("概率",1));
                  cm.dispose();
				  }
				  } else {
                    cm.sendOk("升星失败了！");
                     cm.getPlayer().set输出Log("概率",1,-cm.getPlayer(). get输出Log("概率",1));
					  cm.喇叭(2," 玩家:<"+cm.getName()+">升2☆失败,请再接再厉!");
						cm.dispose();
					}
					}
				} else	if (cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getHands()==2) {
                    if (cm.getPlayer().getCSPoints(1)<30000) {
                    cm.sendOk("你的点券不足30000");
                    cm.dispose();
					 
					 } else {
                    var statup = new java.util.ArrayList();
                   
					cm.gainNX(-30000);
					sj = Math.floor(Math.random()*100);
					几率 = (10+cm.getPlayer(). get输出Log('概率')*5) ;
                     if(sj <=几率){//随机成功
					 if (getItemType(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getItemId()) == 7) {
                   var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                  item.setStr(item.getStr()+5);//力量
		           item.setDex(item.getDex()+5);//敏捷
		           item.setInt(item.getInt()+5);//智力
                   item.setLuk(item.getLuk()+5);//运气
				   item.setHands(item.getHands()+1);//手记
				   item.setWatk(item.getWatk()+5);//攻击
                   item.setMatk(item.getMatk()+5);//魔法力
				  
				  item.setOwner("3☆");
                  MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                  MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
				  cm.sendOk("升星成功了！");
				    cm.喇叭(1," 恭喜玩家:<"+cm.getName()+">成功将装备升为3☆,大家为他鼓掌吧!");
				   cm.getPlayer().set输出Log("概率",1,-cm.getPlayer(). get输出Log("概率",1));
                  cm.dispose();
				   } else {
					    var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                   item.setStr(item.getStr()+5);//力量
		           item.setDex(item.getDex()+5);//敏捷
		           item.setInt(item.getInt()+5);//智力
                   item.setLuk(item.getLuk()+5);//运气
				   item.setHands(item.getHands()+1);//手记
				   item.setWatk(item.getWatk()+5);//攻击
                   item.setMatk(item.getMatk()+5);//魔法力
				  item.setOwner("3☆");
                  MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                  MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
				  cm.sendOk("升星成功了！");
				   cm.喇叭(1," 恭喜玩家:<"+cm.getName()+">成功将装备升为3☆,大家为他鼓掌吧!");
				   cm.getPlayer().set输出Log("概率",1,-cm.getPlayer(). get输出Log("概率",1));
                  cm.dispose();
				   } 
				  } else {
                    cm.sendOk("升星失败了！");
					 cm.喇叭(2," 玩家:<"+cm.getName()+">升3☆失败,请再接再厉!");
                     cm.getPlayer().set输出Log("概率",1,-cm.getPlayer(). get输出Log("概率",1));
						cm.dispose();
					}
					}
				} else	if (cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getHands()==3) {
                    if (cm.getPlayer().getCSPoints(1)<30000) {
                    cm.sendOk("你的点券不足30000");
                    cm.dispose();
					 
					 } else {
                    var statup = new java.util.ArrayList();
                   
					cm.gainNX(-30000);
					sj = Math.floor(Math.random()*100);
					几率 = (10+cm.getPlayer(). get输出Log('概率')*5) ;
                     if(sj <=几率){//随机成功
					 if (getItemType(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getItemId()) == 7) {
                   var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                   item.setStr(item.getStr()+5);//力量
		           item.setDex(item.getDex()+5);//敏捷
		           item.setInt(item.getInt()+5);//智力
                   item.setLuk(item.getLuk()+5);//运气
				   item.setHands(item.getHands()+1);//手记
				   item.setWatk(item.getWatk()+5);//攻击
                   item.setMatk(item.getMatk()+5);//魔法力
				   
				  item.setOwner("4☆");
                  MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                  MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
				  cm.sendOk("升星成功了！");
				   cm.喇叭(1," 恭喜玩家:<"+cm.getName()+">成功将装备升为4☆,大家为他鼓掌吧!");
				   cm.getPlayer().set输出Log("概率",1,-cm.getPlayer(). get输出Log("概率",1));
                  cm.dispose();
				  } else {
					  var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                   item.setStr(item.getStr()+5);//力量
		           item.setDex(item.getDex()+5);//敏捷
		           item.setInt(item.getInt()+5);//智力
                   item.setLuk(item.getLuk()+5);//运气
				   item.setHands(item.getHands()+1);//手记
				   item.setWatk(item.getWatk()+5);//攻击
                   item.setMatk(item.getMatk()+5);//魔法力
				  item.setOwner("4☆");
                  MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                  MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
				  cm.sendOk("升星成功了！");
				   cm.喇叭(1," 恭喜玩家:<"+cm.getName()+">成功将装备升为4☆,大家为他鼓掌吧!");
				   cm.getPlayer().set输出Log("概率",1,-cm.getPlayer(). get输出Log("概率",1));
                  cm.dispose();
					  }
				  } else {
                    cm.sendOk("升星失败了！");
					 cm.喇叭(2," 玩家:<"+cm.getName()+">升4☆失败,请再接再厉!");
                     cm.getPlayer().set输出Log("概率",1,-cm.getPlayer(). get输出Log("概率",1));
						cm.dispose();
					}
					}
				} else	if (cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getHands()==4) {
                    if (cm.getPlayer().getCSPoints(1)<30000) {
                    cm.sendOk("你的点券不足30000");
                    cm.dispose();
					 
					 } else {
                    var statup = new java.util.ArrayList();
                   
					cm.gainNX(-30000);
					sj = Math.floor(Math.random()*100);
					几率 = (10+cm.getPlayer(). get输出Log('概率')*5) ;
                     if(sj <=几率){//随机成功
					  if (getItemType(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getItemId()) == 7) {
                   var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                   item.setStr(item.getStr()+5);//力量
		           item.setDex(item.getDex()+5);//敏捷
		           item.setInt(item.getInt()+5);//智力
                   item.setLuk(item.getLuk()+5);//运气
				   item.setHands(item.getHands()+1);//手记
				   item.setWatk(item.getWatk()+5);//攻击
                   item.setMatk(item.getMatk()+5);//魔法力
				  item.setOwner("5☆");
                  MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                  MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
				  cm.sendOk("升星成功了！");
				   cm.喇叭(1," 恭喜玩家:<"+cm.getName()+">成功将装备升为5☆,大家为他鼓掌吧!");
				   cm.getPlayer().set输出Log("概率",1,-cm.getPlayer(). get输出Log("概率",1));
                  cm.dispose();
				   } else {
					   var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                   item.setStr(item.getStr()+5);//力量
		           item.setDex(item.getDex()+5);//敏捷
		           item.setInt(item.getInt()+5);//智力
                   item.setLuk(item.getLuk()+5);//运气
				   item.setHands(item.getHands()+1);//手记
				   item.setWatk(item.getWatk()+5);//攻击
                   item.setMatk(item.getMatk()+5);//魔法力
				  item.setOwner("5☆");
                  MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                  MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
				  cm.sendOk("升星成功了！");
				   cm.喇叭(1," 恭喜玩家:<"+cm.getName()+">成功将装备升为2☆,大家为他鼓掌吧!");
				   cm.getPlayer().set输出Log("概率",1,-cm.getPlayer(). get输出Log("概率",1));
                  cm.dispose();
					   }
				  } else {
                    cm.sendOk("升星失败了！");
					 cm.喇叭(2," 玩家:<"+cm.getName()+">升5☆失败,请再接再厉!");
                     cm.getPlayer().set输出Log("概率",1,-cm.getPlayer(). get输出Log("概率",1));
						cm.dispose();
					}
					}
				} else	if (cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getHands()==5) {
                     if (cm.getPlayer().getCSPoints(1)<30000) {
                    cm.sendOk("你的点券不足30000");
                    cm.dispose();
					 
					 } else {
                    var statup = new java.util.ArrayList();
                   
					cm.gainNX(-30000);
					sj = Math.floor(Math.random()*100);
					几率 = (10+cm.getPlayer(). get输出Log('概率')*5) ;
                     if(sj <=几率){//随机成功
					 if (getItemType(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getItemId()) == 7) {
                   var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                   item.setStr(item.getStr()+10);//力量
		           item.setDex(item.getDex()+10);//敏捷
		           item.setInt(item.getInt()+10);//智力
                   item.setLuk(item.getLuk()+10);//运气
				   item.setHands(item.getHands()+1);//手记
				   item.setWatk(item.getWatk()+10);//攻击
                   item.setMatk(item.getMatk()+10);//魔法力
				   item.setOwner("6☆");
                  MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                  MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
				  cm.sendOk("升星成功了！");
				  cm.喇叭(1," 恭喜玩家:<"+cm.getName()+">成功将装备升为6☆,大家为他鼓掌吧!");
				   cm.getPlayer().set输出Log("概率",1,-cm.getPlayer(). get输出Log("概率",1));
                  cm.dispose();
				  } else {
					  var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                  item.setStr(item.getStr()+10);//力量
		           item.setDex(item.getDex()+10);//敏捷
		           item.setInt(item.getInt()+10);//智力
                   item.setLuk(item.getLuk()+10);//运气
				   item.setHands(item.getHands()+1);//手记
				   item.setWatk(item.getWatk()+10);//攻击
                   item.setMatk(item.getMatk()+10);//魔法力
				   item.setOwner("6☆");
                  MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                  MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
				  cm.sendOk("升星成功了！");
				  cm.喇叭(1," 恭喜玩家:<"+cm.getName()+">成功将装备升为6☆,大家为他鼓掌吧!");
				   cm.getPlayer().set输出Log("概率",1,-cm.getPlayer(). get输出Log("概率",1));
                  cm.dispose();
					  }
				  } else {
					  var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                   item.setStr(item.getStr()-5);//力量
		           item.setDex(item.getDex()-5);//敏捷
		           item.setInt(item.getInt()-5);//智力
                   item.setLuk(item.getLuk()-5);//运气
				   item.setHands(item.getHands()-1);//手记
				   item.setWatk(item.getWatk()-5);//攻击
                   item.setMatk(item.getMatk()-5);//魔法力

				   item.setOwner("4☆");
                  MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                  MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
                    cm.sendOk("升星失败了！");
					 cm.喇叭(2," 玩家:<"+cm.getName()+">升6☆失败,退回4☆请再接再厉!");
                     cm.getPlayer().set输出Log("概率",1,-cm.getPlayer(). get输出Log("概率",1));
						cm.dispose();
					}
					}
				} else	if (cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getHands()==6) {
                     if (cm.getPlayer().getCSPoints(1)<30000) {
                    cm.sendOk("你的点券不足30000");
                    cm.dispose();
					 
					 } else {
                    var statup = new java.util.ArrayList();
                   
					cm.gainNX(-30000);
					sj = Math.floor(Math.random()*100);
					几率 = (10+cm.getPlayer(). get输出Log('概率')*5) ;
                     if(sj <=几率){//随机成功
					 if (getItemType(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getItemId()) == 7) {
                   var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
               item.setStr(item.getStr()+10);//力量
		           item.setDex(item.getDex()+10);//敏捷
		           item.setInt(item.getInt()+10);//智力
                   item.setLuk(item.getLuk()+10);//运气
				   item.setHands(item.getHands()+1);//手记
				   item.setWatk(item.getWatk()+10);//攻击
                   item.setMatk(item.getMatk()+10);//魔法力
				    
				  item.setOwner("7☆");
                  MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                  MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
				  cm.sendOk("升星成功了！");
				  cm.喇叭(1," 恭喜玩家:<"+cm.getName()+">成功将装备升为7☆,大家为他鼓掌吧!");
				   cm.getPlayer().set输出Log("概率",1,-cm.getPlayer(). get输出Log("概率",1));
                  cm.dispose();
				  } else {
					   var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
             item.setStr(item.getStr()+10);//力量
		           item.setDex(item.getDex()+10);//敏捷
		           item.setInt(item.getInt()+10);//智力
                   item.setLuk(item.getLuk()+10);//运气
				   item.setHands(item.getHands()+1);//手记
				   item.setWatk(item.getWatk()+10);//攻击
                   item.setMatk(item.getMatk()+10);//魔法力
				  item.setOwner("7☆");
                  MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                  MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
				  cm.sendOk("升星成功了！");
				   cm.喇叭(1," 恭喜玩家:<"+cm.getName()+">成功将装备升为7☆,大家为他鼓掌吧!");
				   cm.getPlayer().set输出Log("概率",1,-cm.getPlayer(). get输出Log("概率",1));
                  cm.dispose();
					  }
				  } else {
					    var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                   item.setStr(item.getStr()-10);//力量
		           item.setDex(item.getDex()-10);//敏捷
		           item.setInt(item.getInt()-10);//智力
                   item.setLuk(item.getLuk()-10);//运气
				   item.setHands(item.getHands()-1);//手记
				   item.setWatk(item.getWatk()-10);//攻击
                   item.setMatk(item.getMatk()-10);//魔法力
			
				   item.setOwner("5☆");
                  MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                  MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
                    cm.sendOk("升星失败了！");
					cm.喇叭(2," 玩家:<"+cm.getName()+">升7☆失败,退回5☆请再接再厉!");
                     cm.getPlayer().set输出Log("概率",1,-cm.getPlayer(). get输出Log("概率",1));
						cm.dispose();
					}
					};
					
					} else if (cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getHands()==7) {
                     if (cm.getPlayer().getCSPoints(1)<30000) {
                    cm.sendOk("你的点券不足30000");
                    cm.dispose();
					 
					 } else {
                    var statup = new java.util.ArrayList();
                   
					cm.gainNX(-30000);
					sj = Math.floor(Math.random()*100);
					几率 = (10+cm.getPlayer(). get输出Log('概率')*5) ;
                     if(sj <=几率){//随机成功
					  if (getItemType(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getItemId()) == 7) {
                   var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                   item.setStr(item.getStr()+10);//力量
		           item.setDex(item.getDex()+10);//敏捷
		           item.setInt(item.getInt()+10);//智力
                   item.setLuk(item.getLuk()+10);//运气
				   item.setHands(item.getHands()+1);//手记
				   item.setWatk(item.getWatk()+10);//攻击
                   item.setMatk(item.getMatk()+10);//魔法力
				  item.setOwner("8☆");
                  MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                  MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
				  cm.sendOk("升星成功了！");
				   cm.喇叭(1," 恭喜玩家:<"+cm.getName()+">成功将装备升为8☆,大家为他鼓掌吧!");
				   cm.getPlayer().set输出Log("概率",1,-cm.getPlayer(). get输出Log("概率",1));
                  cm.dispose();
				  } else {
					 var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                   item.setStr(item.getStr()+10);//力量
		           item.setDex(item.getDex()+10);//敏捷
		           item.setInt(item.getInt()+10);//智力
                   item.setLuk(item.getLuk()+10);//运气
				   item.setHands(item.getHands()+1);//手记
				   item.setWatk(item.getWatk()+10);//攻击
                   item.setMatk(item.getMatk()+10);//魔法力
				  item.setOwner("8☆");
                  MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                  MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
				   cm.喇叭(1," 恭喜玩家:<"+cm.getName()+">成功将装备升为8☆,大家为他鼓掌吧!");
				  cm.sendOk("升星成功了！");
				   cm.getPlayer().set输出Log("概率",1,-cm.getPlayer(). get输出Log("概率",1));
                  cm.dispose();
					  }
				  } else {
					    var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                   item.setStr(item.getStr()-10);//力量
		           item.setDex(item.getDex()-10);//敏捷
		           item.setInt(item.getInt()-10);//智力
                   item.setLuk(item.getLuk()-10);//运气
				   item.setHands(item.getHands()-1);//手记
				   item.setWatk(item.getWatk()-10);//攻击
                   item.setMatk(item.getMatk()-10);//魔法力
				  
				   item.setOwner("6☆");
                  MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                  MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
                    cm.sendOk("升星失败了！");
					cm.喇叭(2," 玩家:<"+cm.getName()+">升8☆失败,退回6☆请再接再厉!");
                     cm.getPlayer().set输出Log("概率",1,-cm.getPlayer(). get输出Log("概率",1));
						cm.dispose();
					}
					}
					
				} else	if (cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getHands()==8) {
                     if (cm.getPlayer().getCSPoints(1)<30000) {
                    cm.sendOk("你的点券不足30000");
                    cm.dispose();
					 
					 } else {
                    var statup = new java.util.ArrayList();
                   
					cm.gainNX(-30000);
					sj = Math.floor(Math.random()*100);
					几率 = (10+cm.getPlayer(). get输出Log('概率')*5) ;
                     if(sj <=几率){//随机成功
					  if (getItemType(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getItemId()) == 7) {
                   var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                   item.setStr(item.getStr()+10);//力量
		           item.setDex(item.getDex()+10);//敏捷
		           item.setInt(item.getInt()+10);//智力
                   item.setLuk(item.getLuk()+10);//运气
				   item.setHands(item.getHands()+1);//手记
				   item.setWatk(item.getWatk()+10);//攻击
                   item.setMatk(item.getMatk()+10);//魔法力
				  item.setOwner("9☆");
                  MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                  MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
				   cm.喇叭(1," 恭喜玩家:<"+cm.getName()+">成功将装备升为9☆,大家为他鼓掌吧!");
				  cm.sendOk("升星成功了！");
				   cm.getPlayer().set输出Log("概率",1,-cm.getPlayer(). get输出Log("概率",1));
                  cm.dispose();
				  } else {
					  var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                   item.setStr(item.getStr()+10);//力量
		           item.setDex(item.getDex()+10);//敏捷
		           item.setInt(item.getInt()+10);//智力
                   item.setLuk(item.getLuk()+10);//运气
				   item.setHands(item.getHands()+1);//手记
				   item.setWatk(item.getWatk()+10);//攻击
                   item.setMatk(item.getMatk()+10);//魔法力
				  item.setOwner("9☆");
                  MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                  MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
				  cm.喇叭(1," 恭喜玩家:<"+cm.getName()+">成功将装备升为9☆,大家为他鼓掌吧!");
				  cm.sendOk("升星成功了！");
				   cm.getPlayer().set输出Log("概率",1,-cm.getPlayer(). get输出Log("概率",1));
                  cm.dispose();
					  }
				  } else {
					    var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                   item.setStr(item.getStr()-10);//力量
		           item.setDex(item.getDex()-10);//敏捷
		           item.setInt(item.getInt()-10);//智力
                   item.setLuk(item.getLuk()-10);//运气
				   item.setHands(item.getHands()-1);//手记
				   item.setWatk(item.getWatk()-10);//攻击
                   item.setMatk(item.getMatk()-10);//魔法力
				    
				   item.setOwner("7☆");
                  MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                  MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
                    cm.sendOk("升星失败了！");
					cm.喇叭(2," 玩家:<"+cm.getName()+">升9☆失败,退回7☆请再接再厉!");
                     cm.getPlayer().set输出Log("概率",1,-cm.getPlayer(). get输出Log("概率",1));
						cm.dispose();
					}
					}
					
				} else	if (cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getHands()==9) {
                     if (cm.getPlayer().getCSPoints(1)<30000) {
                    cm.sendOk("你的点券不足30000");
                    cm.dispose();
					 
					 } else {
                    var statup = new java.util.ArrayList();
                   
					cm.gainNX(-30000);
					sj = Math.floor(Math.random()*100);
					几率 = (10+cm.getPlayer(). get输出Log('概率')*5) ;
                     if(sj <=几率){//随机成功
					  if (getItemType(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getItemId()) == 7) {
                   var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                   item.setStr(item.getStr()+10);//力量
		           item.setDex(item.getDex()+10);//敏捷
		           item.setInt(item.getInt()+10);//智力
                   item.setLuk(item.getLuk()+10);//运气
				   item.setHands(item.getHands()+1);//手记
				   item.setWatk(item.getWatk()+10);//攻击
                   item.setMatk(item.getMatk()+10);//魔法力
				   
				  item.setOwner("10☆");
                  MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                  MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
				  cm.喇叭(1," 恭喜玩家:<"+cm.getName()+">成功将装备升为10☆,大家为他鼓掌吧!");
				  cm.sendOk("升星成功了！");
				   cm.getPlayer().set输出Log("概率",1,-cm.getPlayer(). get输出Log("概率",1));
                  cm.dispose();
				  } else {
					    var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                   item.setStr(item.getStr()+10);//力量
		           item.setDex(item.getDex()+10);//敏捷
		           item.setInt(item.getInt()+10);//智力
                   item.setLuk(item.getLuk()+10);//运气
				   item.setHands(item.getHands()+1);//手记
				   item.setWatk(item.getWatk()+10);//攻击
                   item.setMatk(item.getMatk()+10);//魔法力
				  item.setOwner("10☆");
                  MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                  MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
				  cm.sendOk("升星成功了！");
				   cm.喇叭(1," 恭喜玩家:<"+cm.getName()+">成功将装备升为10☆,大家为他鼓掌吧!");
				   cm.getPlayer().set输出Log("概率",1,-cm.getPlayer(). get输出Log("概率",1));
                  cm.dispose();
					  }
				  } else {
					    var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                   item.setStr(item.getStr()-10);//力量
		           item.setDex(item.getDex()-10);//敏捷
		           item.setInt(item.getInt()-10);//智力
                   item.setLuk(item.getLuk()-10);//运气
				   item.setHands(item.getHands()-1);//手记
				   item.setWatk(item.getWatk()-10);//攻击
                   item.setMatk(item.getMatk()-10);//魔法力
				  
				   item.setOwner("8☆");
                  MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                  MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
                    cm.sendOk("升星失败了！");
					cm.喇叭(2," 玩家:<"+cm.getName()+">升10☆失败,退回8☆请再接再厉!");
                     cm.getPlayer().set输出Log("概率",1,-cm.getPlayer(). get输出Log("概率",1));
						cm.dispose();
					}
					}
					
					} else {
                    cm.sendOk("您的材料不足,或者装备等级9达到10级！");
                     status = -1; 
                }
            } else if (selection == 5) {
				var textz = "\r\n\r\n";
                //textz += "1-10星[可以使用材料增加成功概率]\r\n\r\n";
				textz += "[可以使用材料增加成功概率]\r\n\r\n";
				textz += "1-5星[升级失败扣除材料]5-10星[失败会降低1星扣除属性]\r\n\r\n";
				textz += "1-10星[可以使用普通防爆[[失败不扣除任何星以及属性]]\r\n\r\n";
				textz += "10-15星[可以使用高级防爆[[失败不扣除任何星以及属性]]\r\n\r\n";
				textz += "10-15星[如不使用任何防爆[[失败扣除装备]]\r\n\r\n";
				textz += "10-15星[成功几率50%40%30%20%10%]\r\n\r\n";
cm.sendSimple (textz); 
	status = -1; 
            } else if (selection == 6) {
                 if (cm.haveItem(2531000, 1) && cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getHands()>=10 && cm.getPlayer(). get输出Log('高级防爆')<1) {
                     cm.gainItem(2531000, -1);
					  cm.sendOk("使用高级防爆成功！");
 cm.getPlayer().set输出Log('高级防爆',1,1)	;
  status = -1; 
                } else {
                    cm.sendOk("您的材料不足,或者装备等级没有达到10级！或者已经存在一个高级防爆");
                     status = -1; 
                }
            } else if (selection == 7) {
                if (cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getHands()==10) {
					if (cm.getPlayer().getCSPoints(1)<50000) {
                    cm.sendOk("你的点券不足50000");
                    cm.dispose();
					
					 } else {
                    var statup = new java.util.ArrayList();
                   
					cm.gainNX(-50000);
					sj = Math.floor(Math.random()*100);
					if(sj <=50){//随机成功
					 if (getItemType(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getItemId()) == 7) {
					var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                   item.setStr(item.getStr()+15);//力量
		           item.setDex(item.getDex()+15);//敏捷
		           item.setInt(item.getInt()+15);//智力
                   item.setLuk(item.getLuk()+15);//运气
				   item.setHands(item.getHands()+1);//手记
				   item.setWatk(item.getWatk()+15);//攻击
                   item.setMatk(item.getMatk()+15);//魔法力
				  item.setOwner("11☆");
                  MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                  MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
				  cm.sendOk("升星成功了！");
				   cm.喇叭(1," 恭喜玩家:<"+cm.getName()+">成功将装备升为11☆,大家为他鼓掌吧!");
				   cm.getPlayer().set输出Log("普通防爆",1,-cm.getPlayer(). get输出Log("普通防爆",1));
				   cm.getPlayer().set输出Log("高级防爆",1,-cm.getPlayer(). get输出Log("高级防爆",1));
                  cm.dispose();
				  } else {
					  var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                   item.setStr(item.getStr()+15);//力量
		           item.setDex(item.getDex()+15);//敏捷
		           item.setInt(item.getInt()+15);//智力
                   item.setLuk(item.getLuk()+15);//运气
				   item.setHands(item.getHands()+1);//手记
				   item.setWatk(item.getWatk()+15);//攻击
                   item.setMatk(item.getMatk()+15);//魔法力
				  item.setOwner("11☆");
                  MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                  MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
				  cm.sendOk("升星成功了！");
				   cm.喇叭(1," 恭喜玩家:<"+cm.getName()+">成功将装备升为11☆,大家为他鼓掌吧!");
				   cm.getPlayer().set输出Log("普通防爆",1,-cm.getPlayer(). get输出Log("普通防爆",1));
				   cm.getPlayer().set输出Log("高级防爆",1,-cm.getPlayer(). get输出Log("高级防爆",1));
                  cm.dispose();
					  }
				   } else {
					   if(cm.getPlayer(). get输出Log("高级防爆")>=1){//
					    cm.getPlayer().set输出Log("高级防爆",1,-cm.getPlayer(). get输出Log("高级防爆",1));
						cm.喇叭(2," 玩家:<"+cm.getName()+">使用高级防爆失败了,不扣除任何属性!");
					    cm.sendOk("升星失败了,装备不发生任何变化");
                        cm.dispose();
						} else if(cm.getPlayer(). get输出Log("普通防爆")>=1){
							 cm.getPlayer().set输出Log("普通防爆",1,-cm.getPlayer(). get输出Log("普通防爆",1));
							 if (getItemType(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getItemId()) == 7) {
							 var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                   item.setStr(item.getStr()-75);//力量 扣除的生之前1-10星所有属性
		           item.setDex(item.getDex()-75);//敏捷
		           item.setInt(item.getInt()-75);//智力
                   item.setLuk(item.getLuk()-75);//运气
				   item.setHands(item.getHands()-10);//手记//前面几星扣除几个
				   item.setWatk(item.getWatk()-75);//攻击
                   item.setMatk(item.getMatk()-75);//魔法力
				 item.setOwner("");
                  MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                  MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
				   cm.喇叭(2," 玩家:<"+cm.getName()+">使用普通防爆扣除所有星级,装备不消失!");
					cm.sendOk("升星失败了,装备不消失 扣除之前所有星级！");
					cm.dispose();
					 } else {
							 var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                   item.setStr(item.getStr()-75);//力量 扣除的生之前1-10星所有属性
		           item.setDex(item.getDex()-75);//敏捷
		           item.setInt(item.getInt()-75);//智力
                   item.setLuk(item.getLuk()-75);//运气
				   item.setHands(item.getHands()-10);//手记//前面几星扣除几个
				   item.setWatk(item.getWatk()-75);//攻击
                   item.setMatk(item.getMatk()-75);//魔法力
				 item.setOwner("");
                  MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                  MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
					cm.sendOk("升星失败了,装备不消失 扣除之前所有星级！");
					 cm.喇叭(2," 玩家:<"+cm.getName()+">使用普通防爆扣除所有星级,装备不消失!");
					cm.dispose();
						}
					 } else {
					MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                    cm.sendOk("升星失败了,由于你没有任何防护装备消失！");
					 cm.喇叭(2," 玩家:<"+cm.getName()+">没有任何保护直接消失了装备!");
					cm.dispose();
						}
					}
					}
					} else	if (cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getHands()==11) {
						if (cm.getPlayer().getCSPoints(1)<50000) {
                    cm.sendOk("你的点券不足50000");
                    cm.dispose();
					 
					 } else {
                    var statup = new java.util.ArrayList();
                   
					cm.gainNX(-50000);
					sj = Math.floor(Math.random()*100);
					if(sj <=40){//随机成功
					 if (getItemType(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getItemId()) == 7) {
					var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                   item.setStr(item.getStr()+20);//力量
		           item.setDex(item.getDex()+20);//敏捷
		           item.setInt(item.getInt()+20);//智力
                   item.setLuk(item.getLuk()+20);//运气
				   item.setHands(item.getHands()+1);//手记
				   item.setWatk(item.getWatk()+20);//攻击
                   item.setMatk(item.getMatk()+20);//魔法力
				  
				  item.setOwner("12☆");
                  MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                  MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
				  cm.sendOk("升星成功了！");
				   cm.喇叭(1," 恭喜玩家:<"+cm.getName()+">成功将装备升为12☆,大家为他鼓掌吧!");
				   cm.getPlayer().set输出Log("普通防爆",1,-cm.getPlayer(). get输出Log("普通防爆",1));
				   cm.getPlayer().set输出Log("高级防爆",1,-cm.getPlayer(). get输出Log("高级防爆",1));
                  cm.dispose();
				  } else {
					  var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                   item.setStr(item.getStr()+20);//力量
		           item.setDex(item.getDex()+20);//敏捷
		           item.setInt(item.getInt()+20);//智力
                   item.setLuk(item.getLuk()+20);//运气
				   item.setHands(item.getHands()+1);//手记
				   item.setWatk(item.getWatk()+20);//攻击
                   item.setMatk(item.getMatk()+20);//魔法力
				  item.setOwner("12☆");
                  MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                  MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
				  cm.sendOk("升星成功了！");
				   cm.喇叭(1," 恭喜玩家:<"+cm.getName()+">成功将装备升为12☆,大家为他鼓掌吧!");
				   cm.getPlayer().set输出Log("普通防爆",1,-cm.getPlayer(). get输出Log("普通防爆",1));
				   cm.getPlayer().set输出Log("高级防爆",1,-cm.getPlayer(). get输出Log("高级防爆",1));
                  cm.dispose();
					  }
				   } else {
					   if(cm.getPlayer(). get输出Log("高级防爆")>=1){//
					    cm.getPlayer().set输出Log("高级防爆",1,-cm.getPlayer(). get输出Log("高级防爆",1));
						cm.喇叭(2," 玩家:<"+cm.getName()+">使用高级防爆失败了,不扣除任何属性!");
					    cm.sendOk("升星失败了,装备不发生任何变化");
                        cm.dispose();
						} else if(cm.getPlayer(). get输出Log("普通防爆")>=1){
							 cm.getPlayer().set输出Log("普通防爆",1,-cm.getPlayer(). get输出Log("普通防爆",1));
							 if (getItemType(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getItemId()) == 7) {
							 var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                   item.setStr(item.getStr()-90);//力量 扣除的生之前1-10星所有属性
		           item.setDex(item.getDex()-90);//敏捷
		           item.setInt(item.getInt()-90);//智力
                   item.setLuk(item.getLuk()-90);//运气
				   item.setHands(item.getHands()-11);//手记//前面几星扣除几个
				   item.setWatk(item.getWatk()-90);//攻击
                   item.setMatk(item.getMatk()-90);//魔法力
				  
				 item.setOwner("");
                  MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                  MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
				   cm.喇叭(2," 玩家:<"+cm.getName()+">使用普通防爆扣除所有星级,装备不消失!");
					cm.sendOk("升星失败了,装备不消失 扣除之前所有星级！");
					cm.dispose();
					} else {
						 var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                            item.setStr(item.getStr()-90);//力量 扣除的生之前1-10星所有属性
		           item.setDex(item.getDex()-90);//敏捷
		           item.setInt(item.getInt()-90);//智力
                   item.setLuk(item.getLuk()-90);//运气
				   item.setHands(item.getHands()-11);//手记//前面几星扣除几个
				   item.setWatk(item.getWatk()-90);//攻击
                   item.setMatk(item.getMatk()-90);//魔法力
				 item.setOwner("");
                  MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                  MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
				   cm.喇叭(2," 玩家:<"+cm.getName()+">使用普通防爆扣除所有星级,装备不消失!");
					cm.sendOk("升星失败了,装备不消失 扣除之前所有星级！");
					cm.dispose();
						}
					 } else {
					MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                    cm.sendOk("升星失败了,由于你没有任何防护装备消失！");
					 cm.喇叭(2," 玩家:<"+cm.getName()+">没有任何保护直接消失了装备!");
					cm.dispose();
						}
					}
					}
					} else	if (cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getHands()==12) {
						if (cm.getPlayer().getCSPoints(1)<50000) {
                    cm.sendOk("你的点券不足50000");
                    cm.dispose();
					 
					 } else {
                    var statup = new java.util.ArrayList();
                   
					cm.gainNX(-50000);
					sj = Math.floor(Math.random()*100);
					if(sj <=30){//随机成功
					 if (getItemType(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getItemId()) == 7) {
					var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                   item.setStr(item.getStr()+25);//力量
		           item.setDex(item.getDex()+25);//敏捷
		           item.setInt(item.getInt()+25);//智力
                   item.setLuk(item.getLuk()+25);//运气
				   item.setHands(item.getHands()+1);//手记
				   item.setWatk(item.getWatk()+25);//攻击
                   item.setMatk(item.getMatk()+25);//魔法力
				  item.setOwner("13☆");
                  MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                  MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
				  cm.sendOk("升星成功了！");
				   cm.喇叭(1," 恭喜玩家:<"+cm.getName()+">成功将装备升为13☆,大家为他鼓掌吧!");
				   cm.getPlayer().set输出Log("普通防爆",1,-cm.getPlayer(). get输出Log("普通防爆",1));
				   cm.getPlayer().set输出Log("高级防爆",1,-cm.getPlayer(). get输出Log("高级防爆",1));
                  cm.dispose();
				   } else {
					   var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                   item.setStr(item.getStr()+25);//力量
		           item.setDex(item.getDex()+25);//敏捷
		           item.setInt(item.getInt()+25);//智力
                   item.setLuk(item.getLuk()+25);//运气
				   item.setHands(item.getHands()+1);//手记
				   item.setWatk(item.getWatk()+25);//攻击
                   item.setMatk(item.getMatk()+25);//魔法力
				  item.setOwner("13☆");
                  MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                  MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
				  cm.sendOk("升星成功了！");
				   cm.喇叭(1," 恭喜玩家:<"+cm.getName()+">成功将装备升为13☆,大家为他鼓掌吧!");
				   cm.getPlayer().set输出Log("普通防爆",1,-cm.getPlayer(). get输出Log("普通防爆",1));
				   cm.getPlayer().set输出Log("高级防爆",1,-cm.getPlayer(). get输出Log("高级防爆",1));
                  cm.dispose();
					   }
				   } else {
					   if(cm.getPlayer(). get输出Log("高级防爆")>=1){//
					    cm.getPlayer().set输出Log("高级防爆",1,-cm.getPlayer(). get输出Log("高级防爆",1));
						cm.喇叭(2," 玩家:<"+cm.getName()+">使用高级防爆失败了,不扣除任何属性!");
					    cm.sendOk("升星失败了,装备不发生任何变化");
                        cm.dispose();
						} else if(cm.getPlayer(). get输出Log("普通防爆")>=1){
							 cm.getPlayer().set输出Log("普通防爆",1,-cm.getPlayer(). get输出Log("普通防爆",1));
							 if (getItemType(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getItemId()) == 7) {
							 var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                   item.setStr(item.getStr()-110);//力量 扣除的生之前1-10星所有属性
		           item.setDex(item.getDex()-110);//敏捷
		           item.setInt(item.getInt()-110);//智力
                   item.setLuk(item.getLuk()-110);//运气
				   item.setHands(item.getHands()-12);//手记//前面几星扣除几个
				   item.setWatk(item.getWatk()-110);//攻击
                   item.setMatk(item.getMatk()-110);//魔法力
				 item.setOwner("");
                  MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                  MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
					cm.sendOk("升星失败了,装备不消失 扣除之前所有星级！");
					 cm.喇叭(2," 玩家:<"+cm.getName()+">使用普通防爆扣除所有星级,装备不消失!");
					cm.dispose();
					} else {
						 var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                   item.setStr(item.getStr()-110);//力量 扣除的生之前1-10星所有属性
		           item.setDex(item.getDex()-110);//敏捷
		           item.setInt(item.getInt()-110);//智力
                   item.setLuk(item.getLuk()-110);//运气
				   item.setHands(item.getHands()-12);//手记//前面几星扣除几个
				   item.setWatk(item.getWatk()-110);//攻击
                   item.setMatk(item.getMatk()-110);//魔法力
				 item.setOwner("");
                  MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                  MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
				   cm.喇叭(2," 玩家:<"+cm.getName()+">使用普通防爆扣除所有星级,装备不消失!");
					cm.sendOk("升星失败了,装备不消失 扣除之前所有星级！");
					cm.dispose();
						}
					 } else {
					MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                    cm.sendOk("升星失败了,由于你没有任何防护装备消失！");
					 cm.喇叭(2," 玩家:<"+cm.getName()+">没有任何保护直接消失了装备!");
					cm.dispose();
						}
					}
					}
					} else	if (cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getHands()==13) {
						if (cm.getPlayer().getCSPoints(1)<50000) {
                    cm.sendOk("你的点券不足50000");
                    cm.dispose();
					 
					 } else {
                    var statup = new java.util.ArrayList();
                   
					cm.gainNX(-50000);
					sj = Math.floor(Math.random()*100);
					if(sj <=20){//随机成功
					if (getItemType(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getItemId()) == 7) {
					var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                   item.setStr(item.getStr()+30);//力量
		           item.setDex(item.getDex()+30);//敏捷
		           item.setInt(item.getInt()+30);//智力
                   item.setLuk(item.getLuk()+30);//运气
				   item.setHands(item.getHands()+1);//手记
				   item.setWatk(item.getWatk()+30);//攻击
                   item.setMatk(item.getMatk()+30);//魔法力
				  item.setOwner("14☆");
                  MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                  MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
				  cm.sendOk("升星成功了！");
				   cm.喇叭(1," 恭喜玩家:<"+cm.getName()+">成功将装备升为14☆,大家为他鼓掌吧!");
				   cm.getPlayer().set输出Log("普通防爆",1,-cm.getPlayer(). get输出Log("普通防爆",1));
				   cm.getPlayer().set输出Log("高级防爆",1,-cm.getPlayer(). get输出Log("高级防爆",1));
                  cm.dispose();
				   } else {
					   var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                     item.setStr(item.getStr()+30);//力量
		           item.setDex(item.getDex()+30);//敏捷
		           item.setInt(item.getInt()+30);//智力
                   item.setLuk(item.getLuk()+30);//运气
				   item.setHands(item.getHands()+1);//手记
				   item.setWatk(item.getWatk()+30);//攻击
                   item.setMatk(item.getMatk()+30);//魔法力
				  item.setOwner("14☆");
                  MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                  MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
				  cm.sendOk("升星成功了！");
				   cm.喇叭(1," 恭喜玩家:<"+cm.getName()+">成功将装备升为14☆,大家为他鼓掌吧!");
				   cm.getPlayer().set输出Log("普通防爆",1,-cm.getPlayer(). get输出Log("普通防爆",1));
				   cm.getPlayer().set输出Log("高级防爆",1,-cm.getPlayer(). get输出Log("高级防爆",1));
                  cm.dispose();
					   } 
				   } else {
					   if(cm.getPlayer(). get输出Log("高级防爆")>=1){//
					    cm.getPlayer().set输出Log("高级防爆",1,-cm.getPlayer(). get输出Log("高级防爆",1));
					    cm.sendOk("升星失败了,装备不发生任何变化");
						cm.喇叭(2," 玩家:<"+cm.getName()+">使用高级防爆失败了,不扣除任何属性!");
                        cm.dispose();
						} else if(cm.getPlayer(). get输出Log("普通防爆")>=1){
							 cm.getPlayer().set输出Log("普通防爆",1,-cm.getPlayer(). get输出Log("普通防爆",1));
							 if (getItemType(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getItemId()) == 7) {
							 var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                   item.setStr(item.getStr()-135);//力量 扣除的生之前1-10星所有属性
		           item.setDex(item.getDex()-135);//敏捷
		           item.setInt(item.getInt()-135);//智力
                   item.setLuk(item.getLuk()-135);//运气
				   item.setHands(item.getHands()-13);//手记//前面几星扣除几个
				   item.setWatk(item.getWatk()-135);//攻击
                   item.setMatk(item.getMatk()-135);//魔法力
				item.setOwner("");
                  MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                  MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
					cm.sendOk("升星失败了,装备不消失 扣除之前所有星级！");
					 cm.喇叭(2," 玩家:<"+cm.getName()+">使用普通防爆扣除所有星级,装备不消失!");
					cm.dispose();
					 } else {
						 	 var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                 item.setStr(item.getStr()-135);//力量 扣除的生之前1-10星所有属性
		           item.setDex(item.getDex()-135);//敏捷
		           item.setInt(item.getInt()-135);//智力
                   item.setLuk(item.getLuk()-135);//运气
				   item.setHands(item.getHands()-13);//手记//前面几星扣除几个
				   item.setWatk(item.getWatk()-135);//攻击
                   item.setMatk(item.getMatk()-135);//魔法力
				item.setOwner("");
                  MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                  MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
					cm.sendOk("升星失败了,装备不消失 扣除之前所有星级！");
					 cm.喇叭(2," 玩家:<"+cm.getName()+">使用普通防爆扣除所有星级,装备不消失!");
					cm.dispose();
						 }
					 } else {
					MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                    cm.sendOk("升星失败了,由于你没有任何防护装备消失！");
					 cm.喇叭(2," 玩家:<"+cm.getName()+">没有任何保护直接消失了装备!");
					cm.dispose();
						}
					}
					}
					} else	if (cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getHands()==14) {
						if (cm.getPlayer().getCSPoints(1)<50000) {
                    cm.sendOk("你的点券不足50000");
                    cm.dispose();
					 
					 } else {
                    var statup = new java.util.ArrayList();
                   
					cm.gainNX(-50000);
					sj = Math.floor(Math.random()*100);
					if(sj <=10){//随机成功
					 if (getItemType(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getItemId()) == 7) {
					var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                   item.setStr(item.getStr()+50);//力量
		           item.setDex(item.getDex()+50);//敏捷
		           item.setInt(item.getInt()+50);//智力
                   item.setLuk(item.getLuk()+50);//运气
				   item.setHands(item.getHands()+1);//手记
				   item.setWatk(item.getWatk()+50);//攻击
                   item.setMatk(item.getMatk()+50);//魔法力
				  item.setOwner("15☆");
                  MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                  MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
				  cm.sendOk("升星成功了！");
				   cm.喇叭(1," 恭喜玩家:<"+cm.getName()+">成功将装备升为15☆,大家为他鼓掌吧!");
				   cm.getPlayer().set输出Log("普通防爆",1,-cm.getPlayer(). get输出Log("普通防爆",1));
				   cm.getPlayer().set输出Log("高级防爆",1,-cm.getPlayer(). get输出Log("高级防爆",1));
                  cm.dispose();
				  } else {
					  var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                   item.setStr(item.getStr()+50);//力量
		           item.setDex(item.getDex()+50);//敏捷
		           item.setInt(item.getInt()+50);//智力
                   item.setLuk(item.getLuk()+50);//运气
				   item.setHands(item.getHands()+1);//手记
				   item.setWatk(item.getWatk()+50);//攻击
                   item.setMatk(item.getMatk()+50);//魔法力
				  
				  item.setOwner("15☆");
                  MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                  MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
				  cm.sendOk("升星成功了！");
				    cm.喇叭(1," 恭喜玩家:<"+cm.getName()+">成功将装备升为15☆,大家为他鼓掌吧!");
				   cm.getPlayer().set输出Log("普通防爆",1,-cm.getPlayer(). get输出Log("普通防爆",1));
				   cm.getPlayer().set输出Log("高级防爆",1,-cm.getPlayer(). get输出Log("高级防爆",1));
                  cm.dispose();
					  } 
				   } else {
					   if(cm.getPlayer(). get输出Log("高级防爆")>=1){//
					    cm.getPlayer().set输出Log("高级防爆",1,-cm.getPlayer(). get输出Log("高级防爆",1));
					    cm.sendOk("升星失败了,装备不发生任何变化");
						 cm.喇叭(2," 玩家:<"+cm.getName()+">使用高级防爆失败了,不扣除任何属性!");
                        cm.dispose();
						} else if(cm.getPlayer(). get输出Log("普通防爆")>=1){
							 cm.getPlayer().set输出Log("普通防爆",1,-cm.getPlayer(). get输出Log("普通防爆",1));
							  if (getItemType(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getItemId()) == 7) {
							 var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                   item.setStr(item.getStr()-165);//力量 扣除的生之前1-10星所有属性
		           item.setDex(item.getDex()-165);//敏捷
		           item.setInt(item.getInt()-165);//智力
                   item.setLuk(item.getLuk()-165);//运气
				   item.setHands(item.getHands()-14);//手记//前面几星扣除几个
				   item.setWatk(item.getWatk()-165);//攻击
                   item.setMatk(item.getMatk()-165);//魔法力
				    
				 item.setOwner("");//不给任何名字
                  MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                  MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
					cm.sendOk("升星失败了,装备不消失 扣除之前所有星级！");
					 cm.喇叭(2," 玩家:<"+cm.getName()+">使用普通防爆扣除所有星级,装备不消失!");
					cm.dispose();
					} else {
						var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
                   item.setStr(item.getStr()-165);//力量 扣除的生之前1-10星所有属性
		           item.setDex(item.getDex()-165);//敏捷
		           item.setInt(item.getInt()-165);//智力
                   item.setLuk(item.getLuk()-165);//运气
				   item.setHands(item.getHands()-14);//手记//前面几星扣除几个
				   item.setWatk(item.getWatk()-165);//攻击
                   item.setMatk(item.getMatk()-165);//魔法力
				 item.setOwner("");//不给任何名字
                  MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                  MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
					cm.sendOk("升星失败了,装备不消失 扣除之前所有星级！");
					 cm.喇叭(2," 玩家:<"+cm.getName()+">使用普通防爆扣除所有星级,装备不消失!");
					cm.dispose();
						}
					 } else {
					MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
					  cm.喇叭(2," 玩家:<"+cm.getName()+">没有任何保护直接消失了装备!");
                    cm.sendOk("升星失败了,由于你没有任何防护装备消失！");
					cm.dispose();
						}
					}
					}
					} else {
                    cm.sendOk("您的材料不足,或者已经达到15级！");
                     status = -1; 
                }
            } else if (selection == 8) {
               if (cm.haveItem(4310079, 1)) {
                    cm.gainItem(4310079, -1);
                    cm.gainItem(4310196, 1);
 cm.sendOk("兑换成功！");
                    cm.dispose();
                } else {
                    cm.sendOk("您的材料不足！");
                    cm.dispose();
                }
                
            } else if (selection == 9) {
               if (cm.haveItem(4170002, 33) && cm.haveItem(4000463, 99)) {
                    cm.gainItem(4170002, -33);
					 cm.gainItem(4000463, -99);
                    cm.gainItem(1452220, 1);
 cm.sendOk("兑换成功！");
                    cm.dispose();
                } else {
                    cm.sendOk("您的材料不足！");
                    cm.dispose();
                }
                
            } else if (selection == 10) {
             var textz = "\r\n\r\n";
                textz += "1星[每次增加四维+5双攻+5]\r\n\r\n";
				textz += "2星[每次增加四维+5双攻+5]\r\n\r\n";
				textz += "3星[每次增加四维+5双攻+5]\r\n\r\n";
				textz += "4星[每次增加四维+5双攻+5]\r\n\r\n";
				textz += "5星[每次增加四维+5双攻+5]\r\n\r\n";
				textz += "6星[每次增加四维+10双攻+10]\r\n\r\n";
				textz += "7星[每次增加四维+10双攻+10]\r\n\r\n";
				textz += "8星[每次增加四维+10双攻+10]\r\n\r\n";
				textz += "9星[每次增加四维+10双攻+10]\r\n\r\n";
				textz += "10星[每次增加四维+10双攻+10]\r\n\r\n";
				textz += "11星[每次增加四维+15双攻+51]\r\n\r\n";
				textz += "12星[每次增加四维+20双攻+20]\r\n\r\n";
				textz += "13星[每次增加四维+25双攻+25]\r\n\r\n";
				textz += "14星[每次增加四维+30双攻+30]\r\n\r\n";
				textz += "15星[每次增加四维+50双攻+50]\r\n\r\n";
cm.sendSimple (textz); 
	status = -1; 
            } else if (selection == 11) {
            if (cm.haveItem(4170002, 33) && cm.haveItem(4000463, 99)) {
                    cm.gainItem(4170002, -33);
					cm.gainItem(4000463, -99);
                    cm.gainItem(1402214, 1);
 cm.sendOk("兑换成功！");
                    cm.dispose();
                } else {
                    cm.sendOk("您的材料不足！");
                    cm.dispose();
                }
            } else if (selection == 12) {
               if (cm.haveItem(4170002, 33) && cm.haveItem(4000463, 99)) {
                    cm.gainItem(4170002, -33);
					cm.gainItem(4000463, -99);
                    cm.gainItem(1422156, 1);
 cm.sendOk("兑换成功！");
                    cm.dispose();
                } else {
                    cm.sendOk("您的材料不足！");
                    cm.dispose();
                }
            } else if (selection == 13) {
               if (cm.haveItem(4170002, 33) && cm.haveItem(4000463, 99)) {
                    cm.gainItem(4170002, -33);
					cm.gainItem(4000463, -99);
                    cm.gainItem(1472230, 1);
 cm.sendOk("兑换成功！");
                    cm.dispose();
                } else {
                    cm.sendOk("您的材料不足！");
                    cm.dispose();
                }
            } else if (selection == 14) {
               if (cm.haveItem(4170002, 33) && cm.haveItem(4000463, 99)) {
                    cm.gainItem(4170002, -33);
					cm.gainItem(4000463, -99);
                    cm.gainItem(1332242, 1);
 cm.sendOk("兑换成功！");
                    cm.dispose();
                } else {
                    cm.sendOk("您的材料不足！");
                    cm.dispose();
                }
            } else if (selection == 15) {
               if (cm.haveItem(4170002, 33) && cm.haveItem(4000463, 99)) {
                    cm.gainItem(4170002, -33);
					cm.gainItem(4000463, -99);
                    cm.gainItem(1382226, 1);
 cm.sendOk("兑换成功！");
                    cm.dispose();
                } else {
                    cm.sendOk("您的材料不足！");
                    cm.dispose();
                }
            }else if(selection == 16){
                if (cm.haveItem(4170002, 33) && cm.haveItem(4000463, 99)) {
                    cm.gainItem(4170002, -33);
					cm.gainItem(4000463, -99);
                    cm.gainItem(1482183, 1);
 cm.sendOk("兑换成功！");
                    cm.dispose();
                } else {
                    cm.sendOk("您的材料不足！");
                    cm.dispose();
                }
            }else if(selection == 17){
                if (cm.haveItem(4170002, 33) && cm.haveItem(4000463, 99)) {
                    cm.gainItem(4170002, -33);
					cm.gainItem(4000463, -99);
                    cm.gainItem(1492194, 1);
 cm.sendOk("兑换成功！");
                    cm.dispose();
                } else {
                    cm.sendOk("您的材料不足！");
                    cm.dispose();
                }
}            else if(selection == 18){
                if (cm.haveItem(4170002, 33) && cm.haveItem(4000463, 99)) {
                    cm.gainItem(4170002, -33);
					cm.gainItem(4000463, -99);
                    cm.gainItem(1462208, 1);
 cm.sendOk("兑换成功！");
                    cm.dispose();
                } else {
                    cm.sendOk("您的材料不足！");
                    cm.dispose();
                }
            }
        }
    }
}
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


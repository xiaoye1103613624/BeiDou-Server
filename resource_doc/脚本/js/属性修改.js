
/* 
 * 脚本类型: cm
 * 脚本用途: 装备属性修改面板
 * 脚本作者: LONGMS
 * 制作时间: 2019.3.31
 
 cm.sendGetText("可以输入任何字符"); 
 
 
 */

 
 
 
var status = -1;
var beauty = 0;
var tosend = 0;
var sl;
var mats;
var dds;
function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
	
    if (cm.getInventory(1).getItem(1) == null) {
            cm.sendOk("如果需要修改，请把物品放在背包第一格!");
            cm.dispose();
            return;
        } 		
	
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0 && status == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        } else {
            if (status == 0) {
                cm.sendNext("如果需要点卷中介服务在来找我吧。");
                cm.dispose();
            }
            status--;
        }
        if (status == 0) {
            var item = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.EQUIP).getItem(1).copy();
			var 装备 = cm.getInventory(1).getItem(1).getItemId();
            var text = "";
            text =  "#e#b装备属性修改！请把装备放到第一栏#n\r\n";
			text += "#e#r装备：#v"+ 装备 +"##t"+ 装备 +"#\r\n#k#n#b";
            text += "#L0#力量#l     #L1#敏捷#l     #L2#智力#l     #L3#运气#l    \r\n";			
			text += "#L4#物理攻击#l #L5#魔法攻击#l #L6#物理防御#l #L7#魔法防御#l\r\n";		
			text += "#L8#命中修改#l #L9#回避修改#l #L10#移动速度#l #L11#跳跃修改#l    \r\n";
			text += "#L12#HP血量#l   #L13#MP蓝量#l   #L22#手技#l\r\n";

			text += "#L14#可升级次数#l     #L15#已升级次数#l   #L17#金锤子升级次数#l \r\n";
			text += "#L16#制作人名字#l   #L18#修改交易("+item.getFlag()+")#l\r\n";
			text += "#L19#潜能1(#r"+item.getPotential1()+"#b)#l #L20#潜能2(#r"+item.getPotential2()+"#b)#l #L21#潜能3(#r"+item.getPotential3()+"#b)#l\r\n";
            cm.sendSimple(text);
		}	
		
		
          else if (status == 1) {
            if (selection == 0) {
                    beauty = 0
                    cm.sendGetNumber("需要修改的 #r力量#k 能力值", 0, 0, 32767 ); 
                    
            } else if (selection == 1) {
                    beauty = 1
                    cm.sendGetNumber("需要修改的 #r敏捷#k 能力值", 0,0, 32767 ); 

            } else if (selection == 2) {
                    beauty = 2
                    cm.sendGetNumber("需要修改的 #r智力#k 能力值", 0,0, 32767 ); 
                                    
            } else if (selection == 3) {
                    beauty = 3			
                    cm.sendGetNumber("需要修改的 #r运气#k 能力值", 0,0, 32767 ); 
                   
            } else if (selection == 4) {
                    beauty = 4			
                    cm.sendGetNumber("需要修改的 #r物理攻击#k 能力值", 0,0, 32767 ); 
			
            } else if (selection == 5) {
                    beauty = 5			
                    cm.sendGetNumber("需要修改的 #r魔法攻击#k 能力值", 0,0, 32767 ); 			
			
            } else if (selection == 6) {
                    beauty = 6			
                    cm.sendGetNumber("需要修改的 #r物理防御#k 能力值", 0,0, 32767 ); 				
			
            } else if (selection == 7) {
                    beauty = 7			
                    cm.sendGetNumber("需要修改的 #r魔法防御#k 能力值", 0,0, 32767 ); 			
			
            } else if (selection == 8) {
                    beauty = 8			
                    cm.sendGetNumber("需要修改的 #r命中率#k 能力值", 0,0, 32767 ); 			
			
            } else if (selection == 9) {
                    beauty = 9			
                    cm.sendGetNumber("需要修改的 #r回避率#k 能力值", 0,0, 32767 ); 			
			
            } else if (selection == 10) {
                    beauty = 10			
                    cm.sendGetNumber("需要修改的 #r移动速度#k 能力值", 0,0, 32767 ); 			
			
            } else if (selection == 11) {
                    beauty = 11			
                    cm.sendGetNumber("需要修改的 #r跳跃力#k 能力值", 0,0, 32767 ); 				
			
            } else if (selection == 12) {
                    beauty = 12			
                    cm.sendGetNumber("需要修改的 #rHP#k 能力值", 0,0, 32767 ); 				
			
            } else if (selection == 13) {
                    beauty = 13			
                    cm.sendGetNumber("需要修改的 #rMP#k 能力值", 0,0, 32767 ); 				
			
            } else if (selection == 14) {
                    beauty = 14			
                    cm.sendGetNumber("需要修改的 #r可升级次数#k 能力值", 0,0, 32767 ); 			
			
            } else if (selection == 15) {
                    beauty = 15			
                    cm.sendGetNumber("需要修改的 #r已经升级次数#k 能力值", 0,0, 127 ); 			
			
            } else if (selection == 16) {
                    beauty = 16			
                    cm.sendGetText("需要修改的 #r制作人#k 名称"); 			
			
            } else if (selection == 17) {
                    beauty = 17			
                    cm.sendGetNumber("需要修改的 #r金锤子次数#k 能力值", 0,0,2 ); 			
            } else if (selection == 18) {
                    beauty = 18			
                    cm.sendGetNumber("需要修改的 #r交易值#k 0为可交易 24为可交易一次", 0,0,100); 			
            } else if (selection == 19) {
                    beauty = 19			
                    cm.sendGetNumber("需要修改的 #r潜能1#k", 0,0,1000);
            } else if (selection == 20) {
                    beauty = 20			
                    cm.sendGetNumber("需要修改的 #r潜能2#k", 0,0,1000);					
            } else if (selection == 21) {
                    beauty = 21			
                    cm.sendGetNumber("需要修改的 #r潜能3#k", 0,0,1000);
			} else if (selection == 22) {
                    beauty = 22			
                    cm.sendGetNumber("需要修改的 #r手技#k", 0,0,1000);
			}
			
        } else if (status == 2) {
			var item = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.EQUIP).getItem(1).copy();		
			
			
           if (beauty == 0) {
               item.setStr(selection);
			   Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
			   Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false); 
			   cm.playerMessage(5, "当前装备：力量修改为:"+selection+""); 
			   cm.sendOk("当前装备：力量修改为:"+selection+"");
			   
            } else if (beauty == 1) {
               item.setDex(selection);
			   Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
			   Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);    
               cm.playerMessage(5, "当前装备：敏捷修改为:"+selection+"");	
               cm.sendOk("当前装备：敏捷修改为:"+selection+"");			   
            } else if (beauty == 2) {
               item.setInt(selection);
			   Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
			   Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);
               cm.playerMessage(5, "当前装备：智力修改为:"+selection+"");			   
               cm.sendOk("当前装备：智力修改为:"+selection+"");	
            } else if (beauty == 3) {
               item.setLuk(selection);
			   Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
			   Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);             			
               cm.playerMessage(5, "当前装备：运气修改为:"+selection+"");
               cm.sendOk("当前装备：运气修改为:"+selection+"");				   
            } else if (beauty == 4) {
               item.setWatk(selection);
			   Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
			   Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);               				
               cm.playerMessage(5, "当前装备：物理攻击修改为:"+selection+"");
               cm.sendOk("当前装备：物理攻击修改为:"+selection+"");				   
            } else if (beauty == 5) {
               item.setMatk(selection);
			   Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
			   Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);             							
               cm.playerMessage(5, "当前装备：魔法攻击修改为:"+selection+"");
               cm.sendOk("当前装备：魔法攻击修改为:"+selection+"");				   
            } else if (beauty == 6) {
               item.setWdef(selection);
			   Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
			   Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);            										
               cm.playerMessage(5, "当前装备：物理防御修改为:"+selection+"");		
               cm.sendOk("当前装备：物理防御修改为:"+selection+"");			   
            } else if (beauty == 7) {
               item.setMdef(selection);
			   Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
			   Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false); 
			   cm.playerMessage(5, "当前装备：魔法防御修改为:"+selection+"");	
               cm.sendOk("当前装备：魔法防御修改为:"+selection+"");			
            } else if (beauty == 8) {
               item.setAcc(selection);
			   Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
			   Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);               					
               cm.playerMessage(5, "当前装备：命中率修改为:"+selection+"");	
               cm.sendOk("当前装备：命中率修改为:"+selection+"");			   
            } else if (beauty == 9) {
               item.setAvoid(selection);
			   Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
			   Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);             						
               cm.playerMessage(5, "当前装备：回避率修改为:"+selection+"");
               cm.sendOk("当前装备：回避率修改为:"+selection+"");			   
            } else if (beauty == 10) {
               item.setSpeed(selection);
			   Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
			   Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);               					
               cm.playerMessage(5, "当前装备：移动速度修改为:"+selection+"");
               cm.sendOk("当前装备：移动速度修改为:"+selection+"");			   
            } else if (beauty == 11) {
               item.setJump(selection);
			   Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
			   Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);              								
               cm.playerMessage(5, "当前装备：跳跃速度修改为:"+selection+"");
               cm.sendOk("当前装备：跳跃速度修改为:"+selection+"");			   
            } else if (beauty == 12) {
               item.setHp(selection);
			   Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
			   Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);             				
               cm.playerMessage(5, "当前装备：HP修改为:"+selection+"");
               cm.sendOk("当前装备：HP修改为:"+selection+"");			   
            } else if (beauty == 13) {
               item.setMp(selection);
			   Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
			   Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);            					
               cm.playerMessage(5, "当前装备：MP修改为:"+selection+"");
               cm.sendOk("当前装备：MP修改为:"+selection+"");			   
            } else if (beauty == 14) {
               item.setUpgradeSlots(selection);
			   Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
			   Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);                  				
               cm.playerMessage(5, "当前装备：可升级修改为:"+selection+"");
               cm.sendOk("当前装备：可升级修改为:"+selection+"");			   
            } else if (beauty == 15) {
               item.setLevel(selection);
			   Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
			   Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);            					
               cm.playerMessage(5, "当前装备：当前等级修改为:"+selection+"");
               cm.sendOk("当前装备：当前等级修改为:"+selection+"");			   
            } else if (beauty == 16) {
			   guildName = cm.getText();
               item.setOwner(guildName);
			   Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
			   Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);              			
               cm.playerMessage(5, "当前装备：制作人姓名修改为:"+guildName+"");
               cm.sendOk("当前装备：制作人姓名修改为:"+guildName+"");			   
            } else if (beauty == 17) {
			  
               item.setViciousHammer(selection);
			   Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
			   Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);              			
               cm.playerMessage(5, "当前装备：金锤子次数成功修改为:"+selection+"");
               cm.sendOk("当前装备：金锤子次数成功修改为:"+selection+"");			   
            } else if (beauty == 18) {
			  
               item.setFlag(selection);
			   Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
			   Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);              			
               cm.playerMessage(5, "当前装备：交易成功修改为:"+selection+"");
               cm.sendOk("当前装备：交易修改为:"+selection+"");
            } else if (beauty == 19) {
			  
               item.setPotential1(selection);
			   Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
			   Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);              			
               cm.playerMessage(5, "当前装备：潜能1成功修改为:"+selection+"");
               cm.sendOk("当前装备：潜能1修改为:"+selection+"");

            } else if (beauty == 20) {
			  
               item.setPotential2(selection);
			   Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
			   Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);              			
               cm.playerMessage(5, "当前装备：潜能2成功修改为:"+selection+"");
               cm.sendOk("当前装备：潜能2修改为:"+selection+"");
            } else if (beauty == 21) {
			  
               item.setPotential3(selection);
			   Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
			   Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);              			
               cm.playerMessage(5, "当前装备：潜能3成功修改为:"+selection+"");
               cm.sendOk("当前装备：潜能3修改为:"+selection+"");			
			
			 } else if (beauty == 22) {
               item.setHands(selection);
			   Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
			   Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);    
               cm.playerMessage(5, "当前装备：手技修改为:"+selection+"");	
               cm.sendOk("当前装备：手技修改为:"+selection+"");		
			
			}				
			//cm.dispose();

			
			
			

            
            status = -1;
        } else {
            cm.dispose();
        }
    }
}	
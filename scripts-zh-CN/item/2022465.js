function start() {
    status = -1;
    action(1, 0, 0);
}
function action(mode, type, selection) {
    if (mode == -1) {
        im.dispose();
    }
    else {
        if (status >= 0 && mode == 0) {
            im.sendOk("感谢你的光临！");
            im.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        }
        else {
            status--;
        }
        if (status == 0) {
            var tex2 = "";
            var text = ""; 
            //text += "#e#b请在下方选择你要的新手武器。#r\r\n\r\n"
            text += "#n#L1##v1302007# #z1302007# #e#b(全属性#r1#b攻击力#r30#b)#k#l\r\n"//3
            text += "#n#L8##v1432000# #z1432000# #e#b(全属性#r1#b攻击力#r30#b)#k#l\r\n"//3				
			text += "#n#L2##v1452002# #z1452002# #e#b(全属性#r1#b攻击力#r30#b)#k#l\r\n"//3
			text += "#n#L9##v1462001# #z1462001# #e#b(全属性#r1#b攻击力#r30#b)#k#l\r\n"//3
			text += "#n#L3##v1372005# #z1372005# #e#b(全属性#r1#b魔法力#r30#b)#k#l\r\n"//3			              
			text += "#n#L4##v1472000# #z1472000# #e#b(全属性#r1#b攻击力#r10#b)#k#l\r\n"//3			              
			text += "#n#L5##v1332000# #z1332000# #e#b(全属性#r1#b攻击力#r30#b)#k#l\r\n"//3			              
			text += "#n#L6##v1482000# #z1482000# #e#b(全属性#r1#b攻击力#r20#b)#k#l\r\n"//3
			text += "#n#L7##v1492000# #z1492000# #e#b(全属性#r1#b共计力#r15#b)#k#l\r\n"//3
            im.sendSimple(text);
        }else if(status == 1){
			if (selection == 1) {
			createWuqi(1302007,1,1,1,1,30,1,"10级新手武器");
			}else if(selection ==2){                                      
			createWuqi(1452002,1,1,1,1,30,1,"10级新手武器");
			}else if(selection ==3){                                      
			 createWuqi(1372005,1,1,1,1,30,30,"10级新手武器");
			}else if(selection ==4){                                    
			createWuqi(1472000,1,1,1,1,10,1,"10级新手武器");
			}else if(selection ==5){                                    
			createWuqi(1332000,1,1,1,1,30,1,"10级新手武器");
			}else if(selection ==6){                                     
            createWuqi(1482000,1,1,1,1,20,1,"10级新手武器");
			}else if(selection ==7){                                     
			createWuqi(1492000,1,1,1,1,15,1,"10级新手武器");
			}else if(selection ==8){                                     
			createWuqi(1432000,1,1,1,1,30,1,"10级新手武器");
			}else if(selection ==9){                                     
			createWuqi(1462001,1,1,1,1,30,1,"10级新手武器");
			}else{
				im.dispose();
			}
			
		} 
		
    }
}


function createWuqi(id,liliang,minjie,zhili,yunqi,goongji,mofa,name){
	if (im.getInventory(1).isFull(0)){//判断第四个也就是其它栏的装备栏是否有一个空格
		   im.sendOk("#b装备栏空间不足1格.");	
		   im.dispose();
	}else{
		
		
		im.gainItem(id,liliang,minjie,zhili,yunqi,0,0,goongji,mofa,0,0,0,0,0,0);
		im.used();
		im.sendOk("领取成功！");
        /*Packages.handling.world.World.Broadcast.broadcastMessage
        (Packages.tools.MaplePacketCreator.serverNotice(3,im.getC().getChannel(),
        "[新手武器]" + " : " + "玩家 ["+im.getName()+"]打开了10级新手武器箱子,获得"+name+"!",true));*/
		im.dispose();
	}
}

function packageHave(type,number){
	var object = -1;
	var count = 0;
	var flag = true;
	for(var i = 0;i<32;i++){
		object = im.getInventory(type).getItem(i);
		if(null == object){
			count++;
			if(count>=number){
				flag = false;
				break;
			}
		}
	}
	return flag;
}

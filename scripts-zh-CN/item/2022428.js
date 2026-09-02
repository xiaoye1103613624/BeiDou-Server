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
			text += "#L2##v1402214#   红色双手剑  #l\r\n"//3
			text += "#L3##v1432182#   红色枪  #l\r\n"//3			              			              
			text += "#L5##v1382226#   红色法杖  #l\r\n"//3			              
			text += "#L6##v1452220#   红色弓  #l\r\n"//3
			text += "#L7##v1462208#   红色弩  #l\r\n"//3			                
			text += "#L8##v1472230#   红色拳  #l\r\n"//3		                
			text += "#L9##v1332242#   红色短刀  #l\r\n"//3			              
			text += "#L10##v1492194#  红色短枪  #l\r\n"//3
			text += "#L11##v1482183#  红色爪  #l\r\n"//3
			text += "#L12##v1422156#  红色锤  #l\r\n"//3
            im.sendSimple(text);
        }else if(status == 1){
			if (selection == 1) {
			createWuqi(1302343,1);
			}else if(selection ==2){                                      
			createWuqi(1402214,1);
			}else if(selection ==3){                                      
			createWuqi(1432182,1);
			}else if(selection ==4){                                    
			createWuqi(1322255,1);
			}else if(selection ==5){                                    
			createWuqi(1382226,1);
			}else if(selection ==6){                                     
            createWuqi(1452220,1);
			}else if(selection ==7){                                     
			createWuqi(1462208,1);
			}else if(selection ==8){                                     
			createWuqi(1472230,1);
			}else if(selection ==9){                                     
			createWuqi(1332242,1);
			}else if(selection ==10){                                     
			createWuqi(1492194,1);
			}else if(selection ==11){                                     
			createWuqi(1482183,1);
			}else if(selection ==12){                                     
			createWuqi(1422156,1);
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
        "[新手武器]" + " : " + "玩家 ["+im.getName()+"]打开了神秘之影武器箱子,获得"+name+"!",true));*/
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

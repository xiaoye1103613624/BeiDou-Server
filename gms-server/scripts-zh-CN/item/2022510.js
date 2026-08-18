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
            text += "#e#b请选择你要的必成卷#r#n\r\n\r\n"
            text += "#L1##v 2043003##z2043003##l\r\n"//3			              
			text += "#L2##v 2044003##z2044003##l\r\n"//3
			text += "#L3##v 2044103##z2044103##l\r\n"//3			              
			text += "#L4##v 2043103##z2043103##l\r\n"//3			              
			text += "#L5##v 2043203##z2043203##l\r\n"//3			              
			text += "#L6##v 2044203##z2044203##l\r\n"//3
			text += "#L7##v 2044403##z2044403##l\r\n"//3			                
			text += "#L8##v 2044303##z2044303##l\r\n"//3		                
			text += "#L9##v 2044503##z2044503##l\r\n"//3			              
			text += "#L10##v2044603##z2044603##l\r\n"//3
			text += "#L11##v2043303##z2043303##l\r\n"//3		              
			text += "#L12##v2044703##z2044703##l\r\n"//3		              
			text += "#L13##v2044908##z2044908##l\r\n"//3		              
			text += "#L14##v2044815##z2044815##l\r\n"//3		              
			text += "#L15##v2043803##z2043803##l\r\n"//3		              
			text += "#L16##v2043703##z2043703##l\r\n"//3
            im.sendSimple(text);
        }else if(status == 1){
			if (selection == 1) {
				                    createWuqi(2043003,50,50,50,50,170,1,"");
			}else if(selection ==2){                                      
				                    createWuqi(2044003,50,50,50,50,180,1,"");
			}else if(selection ==3){                                      
				                    createWuqi(2044103,50,50,50,50,170,1,"");
			}else if(selection ==4){                                      
				                    createWuqi(2043103,50,50,50,50,180,1,"");
			}else if(selection ==5){                                      
				                    createWuqi(2043203,50,50,50,50,170,1,"");
			}else if(selection ==6){                                      
				                    createWuqi(2044203,50,50,50,50,180,1,"");
			}else if(selection ==7){                                      
				                    createWuqi(2044403,75,75,75,75,1,220,"");
			}else if(selection ==8){                                      
				                    createWuqi(2044303,75,75,75,75,1,230,"");
			}else if(selection ==9){                                      
				                    createWuqi(2044503,50,50,50,50,180,1,"");
			}else if(selection ==10){                                     
				                    createWuqi(2044603,50,50,50,50,170,1,"");
			}else if(selection ==11){                                     
				                    createWuqi(2043303,50,50,50,50,170,1,"");
			}else if(selection ==12){                                     
				                    createWuqi(2044703,50,50,50,50,180,1,"");
			}else if(selection ==13){                                     
				                    createWuqi(2044908,50,50,50,50,180,1,"");
			}else if(selection ==14){                                     
				                    createWuqi(2044815,50,50,50,50,110,1,"");
			}else if(selection ==15){                                     
				                    createWuqi(2043803,50,50,50,50,130,1,"");
			}else if(selection ==16){                                     
				                    createWuqi(2043703,50,50,50,50,130,1,"");
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
        ///*World.Broadcast.broadcastMessage
        //(Packages.tools.MaplePacketCreator.serverNotice(3,im.getC().getChannel(),
        //"[武器箱子]" + " : " + "玩家 ["+im.getName()+"]打开了一个自选150武器箱子,获得"+name+"!",true));*/
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

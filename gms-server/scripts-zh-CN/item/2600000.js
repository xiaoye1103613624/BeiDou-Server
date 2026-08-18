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
            //text += "#e#r请在下方选择你要的新手武器。#n#r\r\n\r\n"
            text += "#n#L1 ##v 1302336##d#z1302336##b #e(力量#r20#b攻击力#r100#b)#l\r\n"//3			              
			text += "#n#L2 ##v 1402253##d#z1402253##b #e(力量#r20#b攻击力#r110#b)#l\r\n"//3
			text += "#n#L3 ##v 1312201##d#z1312201##b #e(力量#r20#b攻击力#r100#b)#l\r\n"//3			              
			text += "#n#L4 ##v 1412180##d#z1412180##b #e(力量#r20#b攻击力#r110#b)#l\r\n"//3			              
			text += "#n#L5 ##v 1322253##d#z1322253##b #e(力量#r20#b攻击力#r100#b)#l\r\n"//3			              
			text += "#n#L6 ##v 1422187##d#z1422187##b #e(力量#r20#b攻击力#r110#b)#l\r\n"//3
			text += "#n#L7 ##v 1382263##d#z1382263##b #e(智力#r30#b魔法力#r140#b)#l\r\n"//3
			text += "#n#L8 ##v 1372225##d#z1372225##b #e(智力#r30#b魔法力#r130#b)#l\r\n"//3
			text += "#n#L9 ##v 1442270##d#z1442270##b #e(力量#r20#b攻击力#r110#b)#l\r\n"//3
			text += "#n#L10##v 1432216##d#z1432216##b #e(力量#r20#b攻击力#r110#b)#l\r\n"//3
			text += "#n#L11##v 1452255##d#z1452255##b #e(敏捷#r20#b攻击力#r100#b)#l\r\n"//3
			text += "#n#L12##v 1462241##d#z1462241##b #e(敏捷#r20#b攻击力#r110#b)#l\r\n"//3
			text += "#n#L13##v 1472263##d#z1472263##b #e(运气#r20#b攻击力#r60#b)#l\r\n"//3
			text += "#n#L14##v 1332277##d#z1332277##b #e(运气#r20#b攻击力#r110#b)#l\r\n"//3
			text += "#n#L15##v 1482218##d#z1482218##b #e(力量#r20#b攻击力#r80#b)#l\r\n"//3
			text += "#n#L16##v 1492233##d#z1492233##b #e(敏捷#r20#b攻击力#r80#b)#l\r\n"//3
            im.sendSimple(text);
        }else if(status == 1){
			if (selection == 1) {
				                    createWuqi(1302336,20,0,0,0,100,0,"冒险岛寻宝单手剑");
			}else if(selection ==2){                                      
				                    createWuqi(1402253,20,0,0,0,110,0,"冒险岛寻宝双手剑");
			}else if(selection ==3){                                      
				                    createWuqi(1312201,20,0,0,0,100,0,"冒险岛寻宝单手斧");
			}else if(selection ==4){                                    
				                    createWuqi(1412180,20,0,0,0,110,0,"冒险岛寻宝双手斧");
			}else if(selection ==5){                                    
				                    createWuqi(1322253,20,0,0,0,100,0,"冒险岛寻宝单手锤");
			}else if(selection ==6){                                     
				                    createWuqi(1422187,20,0,0,0,110,0,"冒险岛寻宝双手锤");
			}else if(selection ==7){                                     
				                    createWuqi(1382263,0,0,0,30,0,140,"冒险岛寻宝长杖");//法杖长
			}else if(selection ==8){                                     
				                    createWuqi(1372225,0,0,0,30,0,130,"冒险岛寻宝短仗");//法杖短
			}else if(selection ==9){                                     
				                    createWuqi(1442270,20,0,0,0,110,0,"冒险岛寻宝长矛");
			}else if(selection ==10){                                     
				                    createWuqi(1432216,20,0,0,0,110,0,"冒险岛寻宝长枪");
			}else if(selection ==11){                                     
				                    createWuqi(1452255,0,20,0,0,100,0,"冒险岛寻宝弓");
			}else if(selection ==12){                                     
				                    createWuqi(1462241,0,20,0,0,110,0,"冒险岛寻宝弩");
			}else if(selection ==13){                                     
				                    createWuqi(1472263,0,0,20,0,60,0,"冒险岛寻宝拳套");
			}else if(selection ==14){                                     
				                    createWuqi(1332277,0,0,20,0,110,0,"冒险岛寻宝短刀");
			}else if(selection ==15){                                     
				                    createWuqi(1482218,20,0,0,0,80,0,"冒险岛寻宝指节");
			}else if(selection ==16){                                     
				                    createWuqi(1492233,0,20,0,0,80,0,"冒险岛寻宝短枪");
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
        /*World.Broadcast.broadcastMessage
        (Packages.tools.MaplePacketCreator.serverNotice(3,im.getC().getChannel(),
        "[新手武器]" + " : " + "玩家 ["+im.getName()+"]打开了冒险岛寻宝箱子,获得"+name+"!",true));*/
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

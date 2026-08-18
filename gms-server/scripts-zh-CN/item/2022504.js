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
            text += "#L1#你有几率可以开出#v2041133# #v2614000# #v4310023##l\r\n\r\n"//3
			
			//text += "#r开启金蛋后需解卡#l\r\n"//3
			//text += "#L2#Bwuqi#l\r\n"//3
            im.sendSimple(text);
        }else if(status == 1){
			if (selection == 1) {
				var number = random(1,11); 
				randomCreateWuqi(number);
			}else{
				im.dispose();
			}
			
		} 
		
    }
}

function random(lower, upper) {
	return Math.floor(Math.random() * (upper - lower)) + lower;
}

function createWuqi(id,liliang,minjie,zhili,yunqi,goongji,mofa,name){
	if (im.getInventory(1).isFull(1)){//判断第四个也就是其它栏的装备栏是否有一个空格
		   im.sendOk("#b装栏空间不足2格.");	
		   im.dispose();
	}else{
		
		
		im.gainItem(id,liliang,minjie,zhili,yunqi,0,0,goongji,mofa,0,0,0,0,0,0);

		
		im.sendOk("领取成功！");
        /*World.Broadcast.broadcastMessage
        (Packages.tools.MaplePacketCreator.serverNotice(3,im.getC().getChannel(),
        "[随机坐骑]" + " : " + "玩家 ["+im.getName()+"]打开了随机坐骑箱子"+name+"!",true));*/
		im.dispose();
	}
}
function randomCreateWuqi(number){
	switch(number){
	case 1:
    im.gainItem(2041133,1);
    im.gainItem(2022504,-1);
	cm.喇叭(1,"玩家："+cm.getName()+" 开启了活动箱子获得了XX物品!");

		break;                                 
	case 2:                                    
     im.gainItem(4310023,1);
     im.gainItem(2022504,-1);
	 cm.喇叭(1,"玩家："+cm.getName()+" 开启了活动箱子获得了XX物品!");
		break;                                 
	case 3:                                    
     im.gainItem(2614000,1);
     im.gainItem(2022504,-1);
	 cm.喇叭(1,"玩家："+cm.getName()+" 开启了活动箱子获得了XX物品!");
		break;                                 
	case 4:                                    
     im.gainItem(4310154,7);
     im.gainItem(2022504,-1);
	 cm.喇叭(1,"玩家："+cm.getName()+" 开启了活动箱子获得了XX物品!");
		break;                                 
	case 5:                                    
     im.gainItem(4310154,6);
     im.gainItem(2022504,-1);
	 cm.喇叭(1,"玩家："+cm.getName()+" 开启了活动箱子获得了XX物品!");
		break;                         
	case 6:                            
     im.gainItem(4310154,5);
     im.gainItem(2022504,-1);
	 cm.喇叭(1,"玩家："+cm.getName()+" 开启了活动箱子获得了XX物品!");
		break;                                 
	case 7:                                    
     im.gainItem(4310154,4);
     im.gainItem(2022504,-1);
	 cm.喇叭(1,"玩家："+cm.getName()+" 开启了活动箱子获得了XX物品!");
		break;                                 
	case 8:                                    
     im.gainItem(4310154,3);
     im.gainItem(2022504,-1);
	 cm.喇叭(1,"玩家："+cm.getName()+" 开启了活动箱子获得了XX物品!");
		break;                                 
	case 9:                                    
     im.gainItem(4310154,2);
     im.gainItem(2022504,-1);
	 cm.喇叭(1,"玩家："+cm.getName()+" 开启了活动箱子获得了XX物品!");
		break;                                 
	case 10:                                   
     im.gainItem(4310154,1);
     im.gainItem(2022504,-1);
	 cm.喇叭(1,"玩家："+cm.getName()+" 开启了活动箱子获得了XX物品!");
		break;

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
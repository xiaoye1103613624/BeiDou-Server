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
            text += "#L1#野外福利金蛋可以开出#v4031473# #v2614000# #v4000463# #v3992025# #v4000492##l\r\n"//3
			text += "#L2#开完金蛋 需要解卡一次#l\r\n"//3
			//text += "#L2#Bwuqi#l\r\n"//3
            im.sendSimple(text);
        }else if(status == 1){
			if (selection == 1) {
				var number = random(1,30); 
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
    im.gainItem(4031473,1);
	im.gainMeso(2000000);
    im.gainItem(2022517,-1);
        break;
	case 2:                                    
    im.gainItem(2614000,1);
	im.gainMeso(1800000);
    im.gainItem(2022517,-1);
		break;                                 
	case 3:                                    
    im.gainItem(4000463,1);
	im.gainMeso(1700000);
    im.gainItem(2022517,-1);
		break;                                 
	case 4:                                    
    im.gainItem(4000492,1);
	im.gainMeso(1600000);
    im.gainItem(2022517,-1);
		break;                                 
	case 5:                                    
    im.gainItem(3992025,6);
	im.gainMeso(1500000);
    im.gainItem(2022517,-1);
		break;                         
	case 6:                            
    im.gainItem(3992025,7);
	im.gainMeso(1400000);
    im.gainItem(2022517,-1);
		break;                                 
	case 7:                                    
    im.gainItem(3992025,8);
	im.gainMeso(1300000);
    im.gainItem(2022517,-1);
		break;                                 
	case 8:                                    
    im.gainItem(3992025,9);
	im.gainMeso(1200000);
    im.gainItem(2022517,-1);
		break;                                 
	case 9:                                    
    im.gainItem(3992025,10);
	im.gainMeso(1100000);
    im.gainItem(2022517,-1);
		break;                                 
	case 10:                                   
    im.gainItem(3992025,20);
	im.gainMeso(1000000);
    im.gainItem(2022517,-1);
		break;
	case 11:                                   
    im.gainItem(2000005,1);
	im.gainMeso(950000);
    im.gainItem(2022517,-1);
		break;
    case 12:                                   
    im.gainItem(2000005,2);
	im.gainMeso(900000);
    im.gainItem(2022517,-1);
		break;
    case 13:                                   
    im.gainItem(2000005,3);
	im.gainMeso(850000);
    im.gainItem(2022517,-1);
		break;
    case 14:                                   
    im.gainItem(2000005,4);
	im.gainMeso(800000);
    im.gainItem(2022517,-1);
		break;
    case 15:                                   
    im.gainItem(2000005,5);
	im.gainMeso(750000);
    im.gainItem(2022517,-1);
		break;
    case 16:                                   
    im.gainItem(2000005,6);
	im.gainMeso(700000);
    im.gainItem(2022517,-1);
		break;
    case 17:                                   
    im.gainItem(2000005,7);
	im.gainMeso(650000);
    im.gainItem(2022517,-1);
		break;
    case 18:                                   
    im.gainItem(2000005,8);
	im.gainMeso(600000);
    im.gainItem(2022517,-1);
		break;
    case 19:                                   
    im.gainItem(2000005,9);
	im.gainMeso(550000);
    im.gainItem(2022517,-1);
		break;
    case 20:                                   
    im.gainItem(2000005,10);
	im.gainMeso(500000);
    im.gainItem(2022517,-1);
		break;
    case 21:                                   
    im.gainItem(4310153,5);
	im.gainMeso(450000);
    im.gainItem(2022517,-1);
		break;
    case 22:                                   
    im.gainItem(4310153,6);
	im.gainMeso(400000);
    im.gainItem(2022517,-1);
		break;
    case 23:                                   
    im.gainItem(4310153,7);
	im.gainMeso(350000);
    im.gainItem(2022517,-1);
		break;
    case 24:                                   
    im.gainItem(4310153,8);
	im.gainMeso(300000);
    im.gainItem(2022517,-1);
		break;
    case 25:                                   
    im.gainItem(4310153,9);
	im.gainMeso(250000);
    im.gainItem(2022517,-1);
		break;
    case 26:                                   
    im.gainItem(4310153,10);
	im.gainMeso(200000);
    im.gainItem(2022517,-1);
		break;
    case 27:                                   
    im.gainItem(4310153,15);
	im.gainMeso(150000);
    im.gainItem(2022517,-1);
		break;
    case 28:                                   
    im.gainItem(4310153,20);
	im.gainMeso(100000);
    im.gainItem(2022517,-1);
		break;
    case 29:                                   
    im.gainItem(4310153,25);
	im.gainMeso(50000);
    im.gainItem(2022517,-1);
		break;
    case 30:                                   
    im.gainItem(4310153,30);
	im.gainMeso(10000);
    im.gainItem(2022517,-1);
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
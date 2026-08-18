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
            var text = "#e#b五一活动箱！\r\n#l"; 
            text += "#r#L1#你有几率可以开出#v3994612# #v3994616# #v2614001# #v2614002##l\r\n\r\n"//3
			
			//text += "#r开启金蛋后需解卡#l\r\n"//3
			//text += "#L2#Bwuqi#l\r\n"//3
            im.sendSimple(text);
        }else if(status == 1){
			if (selection == 1) {
				var number = random(1,67); 
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
        /* World.Broadcast.broadcastMessage
        (Packages.tools.MaplePacketCreator.serverNotice(3,im.getC().getChannel(),
        "[随机坐骑]" + " : " + "玩家 ["+im.getName()+"]打开了随机坐骑箱子"+name+"!",true)); */
		im.dispose();
	}
}
function randomCreateWuqi(number){
	switch(number){
	case 1:
    im.gainItem(2614000,10);
    im.gainItem(2022614,-1);
	im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了破攻石X10!");
		break;                                 
	case 2:                                    
    im.gainItem(2614000,15);
    im.gainItem(2022614,-1);
	im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了破攻石X15!");
		break;                                 
	case 3:                                    
    im.gainItem(2614000,20);
    im.gainItem(2022614,-1);
	im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了破攻石X20!");
		break;                                 
	case 4:                                    
    im.gainItem(2614000,25);
    im.gainItem(2022614,-1);
	im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了破攻石X25!");
	 
		break;                                 
	case 5:                                    
    im.gainItem(2614000,30);
    im.gainItem(2022614,-1);
	im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了破攻石X30!");
		break;                         
	case 6:                            
    im.gainItem(2614000,35);
    im.gainItem(2022614,-1);
	im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了破攻石X35!");
		break;                                 
	case 7:                                    
    im.gainItem(2614000,40);
    im.gainItem(2022614,-1);
	im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了破攻石X40!");
		break;                                 
	case 8:                                    
    im.gainItem(2614000,45);
    im.gainItem(2022614,-1);
	im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了破攻石X45!");
		break;                                 
	case 9:                                    
    im.gainItem(2614000,50);
    im.gainItem(2022614,-1);
	im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了破攻石X50!");
		break;                                 
	case 10:                                   
     im.gainItem(4310153,1000);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了小怪币X1000!");
		break;
	case 11:                                   
     im.gainItem(4310153,5000);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了小怪币X5000!");
		break;	
	case 12:                                   
     im.gainItem(4310153,10000);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了小怪币X10000!");
		break;
	case 13:                                   
     im.gainItem(4310153,15000);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了小怪币X15000!");
		break;
	case 14:                                   
     im.gainItem(4310153,20000);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了小怪币X20000!");
		break;
	case 15:                                   
     im.gainItem(4310153,25000);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了小怪币X25000!");
		break;
	case 16:                                   
     im.gainItem(4310153,30000);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了小怪币X30000!");
		break;
	case 17:                                   
     im.gainItem(3992025,100);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了强化星星X100!");
		break;
	case 18:                                   
     im.gainItem(3992025,500);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了强化星星X500!");
		break;
	case 19:                                   
     im.gainItem(3992025,1000);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了强化星星X1000!");
		break;
	case 20:                                   
     im.gainItem(3992025,1500);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了强化星星X1500!");
		break;
	case 21:                                   
     im.gainItem(3992025,2000);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了强化星星X2000!");
		break;
	case 22:                                   
     im.gainItem(3992025,2500);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了强化星星X2500!");
		break;
	case 23:                                   
     im.gainItem(3992025,3000);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了强化星星X3000!");
		break;
	case 24:                                   
     im.gainItem(3992025,3500);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了强化星星X3500!");
		break;
	case 25:                                   
     im.gainItem(3992025,4000);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了强化星星X4000!");
		break;
	case 26:                                   
     im.gainItem(3992025,4500);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了强化星星X4500!");
		break;
	case 27:                                   
     im.gainItem(3992025,5000);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了强化星星X5000!");
		break;
	case 28:                                   
     im.gainItem(3994612,5);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了1级丹药X5!");
		break;
	case 29:                                   
     im.gainItem(3994612,10);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了1级丹药X10!");
		break;
	case 30:                                   
     im.gainItem(3994612,15);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了1级丹药X15!");
		break;
	case 31:                                   
     im.gainItem(3994612,20);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了1级丹药X20!");
		break;
	case 32:                                   
     im.gainItem(3994612,25);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了1级丹药X25!");
		break;
	case 33:                                   
     im.gainItem(3994612,30);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了1级丹药X30!");
		break;
	case 34:                                   
     im.gainItem(3994612,35);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了1级丹药X35!");
		break;
	case 35:                                   
     im.gainItem(3994612,40);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了1级丹药X40!");
		break;
	case 36:                                   
     im.gainItem(3994612,45);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了1级丹药X45!");
		break;
	case 37:                                   
     im.gainItem(3994612,50);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了1级丹药X50!");
		break;
	case 38:                                   
     im.gainItem(3994616,5);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了2级丹药X5!");
		break;
	case 39:                                   
     im.gainItem(3994616,10);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了2级丹药X10!");
		break;
	case 40:                                   
     im.gainItem(3994616,15);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了2级丹药X15!");
		break;
	case 41:                                   
     im.gainItem(3994616,20);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了2级丹药X20!");
		break;
	case 42:                                   
     im.gainItem(3994616,25);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了2级丹药X25!");
		break;
	case 43:                                   
     im.gainItem(3994616,30);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了2级丹药X30!");
		break;
	case 44:                                   
     im.gainItem(3994616,35);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了2级丹药X35!");
		break;
	case 45:                                   
     im.gainItem(3994616,40);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了2级丹药X40!");
		break;
	case 46:                                   
     im.gainItem(3994616,45);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了2级丹药X45!");
		break;
	case 47:                                   
     im.gainItem(3994616,50);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了2级丹药X50!");
		break;
	case 48:                                   
     im.gainItem(2614001,1);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了四大陆专属破攻石（一阶段）X1!");
		break;
	case 49:                                   
     im.gainItem(2614001,2);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了四大陆专属破攻石（一阶段）X2!");
		break;
	case 50:                                   
     im.gainItem(2614001,3);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了四大陆专属破攻石（一阶段）X3!");
		break;
	case 51:                                   
     im.gainItem(2614001,4);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了四大陆专属破攻石（一阶段）X4!");
		break;
	case 52:                                   
     im.gainItem(2614001,5);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了四大陆专属破攻石（一阶段）X5!");
		break;
	case 53:                                   
     im.gainItem(2614001,6);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了四大陆专属破攻石（一阶段）X6!");
		break;
	case 54:                                   
     im.gainItem(2614001,7);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了四大陆专属破攻石（一阶段）X7!");
		break;
	case 55:                                   
     im.gainItem(2614001,8);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了四大陆专属破攻石（一阶段）X8!");
		break;
	case 56:                                   
     im.gainItem(2614001,9);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了四大陆专属破攻石（一阶段）X9!");
		break;
	case 57:                                   
     im.gainItem(2614001,10);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了四大陆专属破攻石（一阶段）X10!");
		break;		
	case 58:                                   
     im.gainItem(2614002,1);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了四大陆专属破攻石（二阶段）X1!");
		break;	
	case 59:                                   
     im.gainItem(2614002,2);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了四大陆专属破攻石（二阶段）X2!");
		break;
	case 60:                                   
     im.gainItem(2614002,3);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了四大陆专属破攻石（二阶段）X3!");
		break;
	case 61:                                   
     im.gainItem(2614002,4);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了四大陆专属破攻石（二阶段）X4!");
		break;
	case 62:                                   
     im.gainItem(2614002,5);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了四大陆专属破攻石（二阶段）X5!");
		break;
	case 63:                                   
     im.gainItem(2614002,6);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了四大陆专属破攻石（二阶段）X6!");
		break;
	case 64:                                   
     im.gainItem(2614002,7);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了四大陆专属破攻石（二阶段）X7!");
		break;
	case 65:                                   
     im.gainItem(2614002,8);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了四大陆专属破攻石（二阶段）X8!");
		break;
	case 66:                                   
     im.gainItem(2614002,9);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了四大陆专属破攻石（二阶段）X9!");
		break;
	case 67:                                   
     im.gainItem(2614002,10);
     im.gainItem(2022614,-1);
	 im.喇叭(1,"玩家："+im.getName()+" 开启了五一活动箱子 获得了四大陆专属破攻石（二阶段）X10!");
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
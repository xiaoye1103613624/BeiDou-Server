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
            text += "#L1#随机获得一只坐骑#l\r\n"//3
			//text += "#L2#Bwuqi#l\r\n"//3
            im.sendSimple(text);
        }else if(status == 1){
			if (selection == 1) {
				var number = random(1,18); 
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
		createWuqi(1912337,25,25,25,25,25,25," 获得了心爱的坐骑");
		createWuqi(1902337,25,25,25,25,25,25," 获得了心爱的鞍子");
     im.gainItem(2022503,-1);
		break;                                 
	case 2:                                    
		createWuqi(1912338,25,25,25,25,25,25," 获得了心爱的坐骑");
		createWuqi(1902338,25,25,25,25,25,25," 获得了心爱的坐骑");
     im.gainItem(2022503,-1);
		break;                                 
	case 3:                                    
		createWuqi(1912340,25,25,25,25,25,25," 获得了心爱的坐骑");
		createWuqi(1902340,25,25,25,25,25,25," 获得了心爱的坐骑");
     im.gainItem(2022503,-1);
		break;                                 
	case 4:                                    
		createWuqi(1912341,25,25,25,25,25,25," 获得了心爱的坐骑");
		createWuqi(1902341,25,25,25,25,25,25," 获得了心爱的坐骑");
     im.gainItem(2022503,-1);
		break;                                 
	case 5:                                    
		createWuqi(1912342,25,25,25,25,25,25," 获得了心爱的坐骑");
		createWuqi(1902342,25,25,25,25,25,25," 获得了心爱的坐骑");
     im.gainItem(2022503,-1);
		break;                         
	case 6:                            
		createWuqi(1912343,25,25,25,25,25,25," 获得了心爱的坐骑");
		createWuqi(1902343,25,25,25,25,25,25," 获得了心爱的坐骑");
     im.gainItem(2022503,-1);
		break;                                 
	case 7:                                    
		createWuqi(1912344,25,25,25,25,25,25," 获得了心爱的坐骑");
		createWuqi(1902344,25,25,25,25,25,25," 获得了心爱的坐骑");
     im.gainItem(2022503,-1);
		break;                                 
	case 8:                                    
		createWuqi(1912345,25,25,25,25,25,25," 获得了心爱的坐骑");
		createWuqi(1902345,25,25,25,25,25,25," 获得了心爱的坐骑");
     im.gainItem(2022503,-1);
		break;                                 
	case 9:                                    
		createWuqi(1912346,25,25,25,25,25,25," 获得了心爱的坐骑");
		createWuqi(1902346,25,25,25,25,25,25," 获得了心爱的坐骑");
     im.gainItem(2022503,-1);
		break;                                 
	case 10:                                   
		createWuqi(1912350,25,25,25,25,25,25," 获得了心爱的坐骑");
		createWuqi(1902350,25,25,25,25,25,25," 获得了心爱的坐骑");
     im.gainItem(2022503,-1);
		break;                                  
	case 11:                                   
		createWuqi(1912402,25,25,25,25,25,25," 获得了心爱的坐骑");
		createWuqi(1902402,25,25,25,25,25,25," 获得了心爱的坐骑");
     im.gainItem(2022503,-1);
		break;                                  
	case 12:                                   
		createWuqi(1912403,25,25,25,25,25,25," 获得了心爱的坐骑");
		createWuqi(1902403,25,25,25,25,25,25," 获得了心爱的坐骑");
     im.gainItem(2022503,-1);
		break;                                  
	case 13:                                   
		createWuqi(1912404,25,25,25,25,25,25," 获得了心爱的坐骑");
		createWuqi(1902404,25,25,25,25,25,25," 获得了心爱的坐骑");
     im.gainItem(2022503,-1);
		break;                                  
	case 14:                                   
		createWuqi(1912405,25,25,25,25,25,25," 获得了心爱的坐骑");
		createWuqi(1902405,25,25,25,25,25,25," 获得了心爱的坐骑");
     im.gainItem(2022503,-1);
		break;                                  
	case 15:                                   
		createWuqi(1912406,25,25,25,25,25,25," 获得了心爱的坐骑");
		createWuqi(1902406,25,25,25,25,25,25," 获得了心爱的坐骑");
     im.gainItem(2022503,-1);
		break;                                  
	case 16:                                   
		createWuqi(1912407,25,25,25,25,25,25," 获得了心爱的坐骑");
		createWuqi(1902407,25,25,25,25,25,25," 获得了心爱的坐骑");
     im.gainItem(2022503,-1);
		break;                                 
	case 17:                                   
		createWuqi(1912409,25,25,25,25,25,25," 获得了心爱的坐骑");
		createWuqi(1902409,25,25,25,25,25,25," 获得了心爱的坐骑");
     im.gainItem(2022503,-1);
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
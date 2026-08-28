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
            text += "#L1#随机获得[#r低级,中级,高级#k]3件饰品#l\r\n"//3
			//text += "#L2#Bwuqi#l\r\n"//3
            im.sendSimple(text);
        }else if(status == 1){
			if (selection == 1) {
				var number = random(1,6); 
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
	if (im.getInventory(1).isFull(2)){//判断第四个也就是其它栏的装备栏是否有一个空格
		   im.sendOk("#b装栏空间不足3格.");	
		   im.dispose();
	}else{
		
		
		im.gainItem(id,liliang,minjie,zhili,yunqi,0,0,goongji,mofa,0,0,0,0,0,0);

		im.sendOk("领取成功！");
        /*World.Broadcast.broadcastMessage
        (Packages.tools.MaplePacketCreator.serverNotice(3,im.getC().getChannel(),
        "[饰品箱子]" + " : " + "玩家 ["+im.getName()+"]打开了随机饰品箱子"+name+"!",true));*/
		im.dispose();
	}
}
function randomCreateWuqi(number){
	switch(number){
	case 1:
		createWuqi(1022224,20,20,20,20,20,20,"获得饰品3件套");
		createWuqi(1022225,30,30,30,30,30,30,"获得饰品3件套");
		createWuqi(1022228,10,10,10,10,10,10,"获得饰品3件套");
     im.gainItem(2022514,-1);
		break;                                 
	case 2:                                    
		createWuqi(1012376,10,10,10,10,10,10,"获得饰品3件套");
		createWuqi(1012377,20,20,20,20,20,20,"获得饰品3件套");
		createWuqi(1012471,30,30,30,30,30,30,"获得饰品3件套");
     im.gainItem(2022514,-1);
		break;                                 
	case 3:                                    
		createWuqi(1132243,10,10,10,10,10,10,"获得饰品3件套");
		createWuqi(1132244,20,20,20,20,20,20,"获得饰品3件套");
		createWuqi(1132245,30,30,30,30,30,30,"获得饰品3件套");
     im.gainItem(2022514,-1);
		break;                                 
	case 4:                                    
		createWuqi(1032220,10,10,10,10,10,10,"获得饰品3件套");
		createWuqi(1032221,20,20,20,20,20,20,"获得饰品3件套");
		createWuqi(1032222,30,30,30,30,30,30,"获得饰品3件套");
     im.gainItem(2022514,-1);
		break;                                 
	case 5:                                    
		createWuqi(1122264,10,10,10,10,10,10,"获得饰品3件套");
		createWuqi(1122265,20,20,20,20,20,20,"获得饰品3件套");
		createWuqi(1122266,30,30,30,30,30,30,"获得饰品3件套");
     im.gainItem(2022514,-1);
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
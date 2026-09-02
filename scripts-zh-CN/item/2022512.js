function start() {
    status = -1;
    action(1, 0, 0);
}
function action(mode, type, selection) {
    if (mode == -1) {
        im.dispose();
    }else {
        if (status >= 0 && mode == 0) {
            im.sendOk("感谢你的光临！");
            im.dispose();
            return;
        }
	if (mode == 1) {
		status++;
	}else {
		status--;
	}
	if (status == 0) {
		var tex2 = "";
		var text = ""; 
		text += "#e#d每日首冲奖励后获得的礼包\r\n\r\n"
		text += "#L1##r领取每日首冲礼包#l\r\n\r\n\r\n"//3
		im.sendSimple(text);
	}else if(status == 1){
		if (selection == 1) {
			if(im.getPlayer().getAccountLog("首冲礼包",1) >= 100){
				im.sendOk("#b一个账号只能领取一次");	
				im.dispose();
				return;
			}//混沌卷10张 戒指强化卷10张 星星强化卷10张 祝福卷10张
			if (im.getInventory(2).isFull(4)){
		            im.sendOk("#b消耗栏空间不足6格.");	
					im.dispose();
				}else{
					//im.gainItem(4000463,10);
					//im.gainItem(2614000,10);
					//im.gainItem(4031473,20000);//抽奖钥匙
					im.gainzb(4000);//元宝
					im.getPlayer().setAccountLog("首冲礼包",1,1);
					im.used();
					im.sendOk("领取成功！");
					im.dispose();
				}
			}else{
				im.dispose();
			}
		} 
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

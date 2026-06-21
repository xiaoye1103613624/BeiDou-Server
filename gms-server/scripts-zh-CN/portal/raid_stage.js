function enter(pi) {
    if (!pi.dojoAgent_NextMap(false, false)) {
		//if (cm.判断等级()>=10){	
		if (pi.getPlayer().getLevel() >=10) {	
		pi.playerMessage("请把当前地图怪物清理干净才能进入下一关！");
		}else {
			pi.playerMessage("请把当前地图怪物清理干净才能进入下一关！");

		}
    }
	var date = new Date();
	pi.getPlayer().setBossLog(date .getDate()+"27关酷兽"); 
}
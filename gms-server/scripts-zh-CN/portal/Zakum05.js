/*
 ZEVMS冒险岛(079)游戏服务端
 脚本：扎昆
 */

function enter(pi) {
    
			
			
	
			if (!pi.haveItem(4001017)) {
				pi.playerMessage(5, "由于你沒有火焰之眼，所以不能挑战扎昆。");
				return false;
			}
			pi.playPortalSE();
			pi.warp(pi.getPlayer().getMapId() + 100, "west00");
			return true;
}
function enter(pi) {
	if (pi.getBossLog("龙族变身秘药") > 0 && !pi.判断物品数量(2210003,1)) {
		pi.useItem(2210003);
		pi.gainItem(2210003,1);
	}
	return false;
}
function action(mode, type, selection) {
    if (mode == 1) {
			if (im.getInventory(1).isFull(0) ) {
			    im.sendOk("#b装备栏空间不足1格.");	
	            im.dispose();
	        return false;
			}
        if (im.haveItem(2022707,1)) {
		var suiji = Math.floor(Math.random() * wpid.length);
		im.gainItem(wpid[suiji],15,15,15,15,0,0,15,15,120,120,5,5,0,0);
		im.gainItem(2022707,-1);
        im.dispose();
        } else {
		im.sendOk("你缺少#v"+2022707+"#"+1+"个.");
        }
	
    }
    im.dispose();
}
var suiji;
var wpid = Array(
1122197,
1112738,
1082432,
1072664,
1052460,
1032142,
1003540,
1122197,
1112738,
1082432,
1072664,
1052460,
1032142,
1003540,
1132152
);
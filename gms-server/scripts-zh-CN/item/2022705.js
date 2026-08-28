
function action(mode, type, selection) {
    if (mode == 1) {
			if (im.getInventory(1).isFull(0) ) {
			    im.sendOk("#b装备栏空间不足1格.");	
	            im.dispose();
	        return false;
			}
        if (im.haveItem(2022705,1)) {
		var suiji = Math.floor(Math.random() * wpid.length);
		im.gainItem(wpid[suiji],15,15,15,15,0,0,15,15,120,120,5,5,0,0);
		im.gainItem(2022705,-1);
        im.dispose();
        } else {
		im.sendOk("你缺少#v"+2022705+"#"+1+"个.");
        }
	
    }
    im.dispose();
}
var suiji;
var wpid = Array(
1102467,
1082438,
1072672,
1052467,
1003561,
1102467,
1082438,
1072672,
1052467,
1003561,
1132161
);
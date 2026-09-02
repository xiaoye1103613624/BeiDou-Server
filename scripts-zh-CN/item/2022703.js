
function action(mode, type, selection) {
    if (mode == 1) {
			if (im.getInventory(1).isFull(0) ) {
			    im.sendOk("#b装备栏空间不足1格.");	
	            im.dispose();
	        return false;
			}
        if (im.haveItem(2022703,1)) {
		var suiji = Math.floor(Math.random() * wpid.length);
		im.gainItem(wpid[suiji],2,2,2,2,0,0,2,2,100,100,5,5,0,0);
		im.gainItem(2022703,-1);
        im.dispose();
        } else {
		im.sendOk("你缺少#v"+2022703+"#"+1+"个.");
        }
	
    }
    im.dispose();
}
var suiji;
var wpid = Array(
1003242,
1052357,
1102294,
1072521,
1082314,
1003242,
1052357,
1102294,
1072521,
1082314,
1132092
);
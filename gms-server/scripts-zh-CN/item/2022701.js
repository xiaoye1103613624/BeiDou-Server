
function action(mode, type, selection) {
    if (mode == 1) {
			if (im.getInventory(1).isFull(0) ) {
			    im.sendOk("#b装备栏空间不足1格.");	
	            im.dispose();
	        return false;
			}
        if (im.haveItem(2022701,1)) {
		var suiji = Math.floor(Math.random() * wpid.length);
		im.gainItem(wpid[suiji],10,10,10,10,0,0,10,10,100,100,5,5,0,0);
		im.gainItem(2022701,-1);
        im.dispose();
        } else {
		im.sendOk("你缺少#v"+2022701+"#"+1+"个.");
        }
	
    }
    im.dispose();
}
var suiji;
var wpid = Array(
1102441,
1082433,
1072666,
1052461,
1003552,
1102441,
1082433,
1072666,
1052461,
1003552,
1132154
);

function action(mode, type, selection) {
    if (mode == 1) {
			if (im.getInventory(1).isFull(0) ) {
			    im.sendOk("#b装备栏空间不足1格.");	
	            im.dispose();
	        return false;
			}
        if (im.haveItem(2022700,1)) {
		var suiji = Math.floor(Math.random() * wpid.length);
		im.gainItem(wpid[suiji],8,8,8,8,0,0,8,8,100,100,5,5,0,0);
		im.gainItem(2022700,-1);
        im.dispose();
        } else {
		im.sendOk("你缺少#v"+2022700+"#"+1+"个.");
        }
	
    }
    im.dispose();
}
var suiji;
var wpid = Array(
1122174,
1082401,
1072618,
1062148,
1042231,
1122174,
1082401,
1072618,
1062148,
1042231,
1032121

);
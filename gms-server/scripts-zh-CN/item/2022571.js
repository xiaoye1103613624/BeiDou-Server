
function action(mode, type, selection) {
    if (mode == 1) {
			if (im.getInventory(1).isFull(0) ) {
			    im.sendOk("#b装备栏空间不足1格.");	
	            im.dispose();
	        return false;
			}
        if (im.haveItem(2022571,1)) {
		var suiji = Math.floor(Math.random() * wpid.length);
		im.gainItem(wpid[suiji],5,5,5,5,0,0,5,5,100,100,5,5,0,0);;
		im.gainItem(2022571,-1);
        im.dispose();
        } else {
		im.sendOk("你缺少#v"+2022571+"#"+1+"个.");
        }
	
    }
    im.dispose();
}
var suiji;
var wpid = Array(
1102322,
1082391,
1072610,
1052405,
1003364,
1102322,
1082391,
1072610,
1052405,
1003364,
1132110

);
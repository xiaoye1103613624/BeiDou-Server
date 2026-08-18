
function action(mode, type, selection) {
    if (mode == 1) {
			if (im.getInventory(1).isFull(0) ) {
			    im.sendOk("#b装备栏空间不足1格.");	
	            im.dispose();
	        return false;
			}
        if (im.haveItem(2022615,1)) {
		var suiji = Math.floor(Math.random() * wpid.length);
		im.gainItem(wpid[suiji],60,60,60,60,0,0,60,60,0,0,0,0,0,0);;
		im.gainItem(2022615,-1);
        im.dispose();
        } else {
		im.sendOk("你缺少#v"+2022615+"#"+1+"个.");
        }
	
    }
    im.dispose();
}
var suiji;
var wpid = Array(
1102482,
1082544,
1082546,
1082545,
1082543,
1132176,
1132175,
1132177,
1132174,
1132178,
1102481,
1102484,
1102485,
1102483,
1072747,
1072745,
1072744,
1072746,
1072743,
1082547

);
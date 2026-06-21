load("nashorn:mozilla_compat.js");
importPackage(Packages.client);
importPackage(Packages.client.inventory);
importPackage(Packages.server);
importPackage(Packages.tools);
var 蓝色箭头 = "#fUI/UIWindow/Quest/icon2/7#";
var 红色箭头 = "#fUI/UIWindow.img/PvP/Scroll/enabled/next2#";
function start() {

    if (cm.getChar().getMapId() != 209000015) {
        cm.sendSimple("#b各大排名可查看！\r\n\r\n#r#L0#"+红色箭头+"人气排行榜#l\t\t\t#r#L1#");
    } else {
        cm.sendOk("不要再这个地图使用我")
    }
}
function action(mode, type, selection) {
    cm.dispose();
    if (selection == 0) { //人气排行
        //cm.人气排名();
		 cm.人气排行榜();
		//cm.getPlayer().cleanKeybinding();
		// cm.战斗力排行榜();
		// cm.刷新状态();
		// checkDrop
		// cm.dc();
		 // cm.warp(105200219,0);
        cm.dispose();
    } else if (selection == 1) {
        //Level
        cm.等级排名();
        cm.dispose();
    } else if (selection == 2) {
        //MapGui
        cm.家族排名();
        cm.dispose();
		} else if (selection == 4) {
        //MapGui
        cm.伤害排行榜();
        cm.dispose();
		} else if (selection == 5) {
        //MapGui
        cm.破功排行榜();
        cm.dispose();
    } else if (selection == 10) {
        //MapGui
        cm.金币排行();
        cm.dispose();

    }
}
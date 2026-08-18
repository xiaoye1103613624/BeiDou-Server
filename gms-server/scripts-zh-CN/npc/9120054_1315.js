
var 坐骑合成 =Array(
Array(1903021,Array(1902435,1902430,1902348,4011007,4011008),Array(1,1,1,50,50),"神兵坐骑",Array(3088,3088,3088,3088,2888,2888)),
Array(1913021,Array(1912435,1912430,1912348,4011007,4011008),Array(1,1,1,50,50),"神兵鞍子",Array(3088,3088,3088,3088,2888,2888))
);
var 选择;
var 判定 = true;

// 道具的数量 是 不能超过3w个 金币 点卷抵用 元宝不能超过21个e 装备的属性不能超过30000


function start() {
    status = -1;

    action(1, 0, 0);
}
function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    }
    else {
        if (status >= 0 && mode == 0) {
            cm.sendOk("感谢你的光临！");
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        }
        else {
            status--;
        }
	if (status == 0){
		var text ="请选择您想要合成的坐骑或者是鞍子#l\r\n";
		for (var a=0;a<坐骑合成.length;a++){
			text +="#L"+a+"##d合成一个#v"+坐骑合成[a][0]+"##z"+坐骑合成[a][0]+"##r全属性3088   伤害增加30%#l\r\n";
		}
		cm.sendSimple(text);
	}else if (status == 1){
		选择 = selection;
		var text = "您想要合成的是#v"+坐骑合成[选择][0]+"#,所需要的物品为:#l\r\n";
		for (var a=0;a<坐骑合成[选择][1].length;a++){
			text +="#v"+坐骑合成[选择][1][a]+"##z"+坐骑合成[选择][1][a]+"#  *  "+坐骑合成[选择][2][a]+" #l\r\n";
		}
		cm.sendSimple(text);
	}else if (status == 2){
		for (var a=0;a<坐骑合成[选择][1].length;a++){
			if (cm.haveItem(坐骑合成[选择][1][a],坐骑合成[选择][2][a]) == false){
				判定 = false;
			}
		}
		if (判定 == true){
			for (var a=0;a<坐骑合成[选择][1].length;a++){
				cm.gainItem(坐骑合成[选择][1][a],-坐骑合成[选择][2][a]);
			}
			var ii = Packages.server.MapleItemInformationProvider.getInstance();
			var toDrop = ii.getEquipById(坐骑合成[选择][0]).copy();
			toDrop.setStr(坐骑合成[选择][4][0]);
			toDrop.setDex(坐骑合成[选择][4][1]);
			toDrop.setInt(坐骑合成[选择][4][2]);
			toDrop.setLuk(坐骑合成[选择][4][3]);
			toDrop.setWatk(坐骑合成[选择][4][4]);
			toDrop.setMatk(坐骑合成[选择][4][5]);
			Packages.server.MapleInventoryManipulator.addFromDrop(cm.getClient(), toDrop, false);
			cm.喇叭(2,"恭喜玩家：["+cm.getName()+"]成功制作"+坐骑合成[选择][3]+"全属性3088！")
			cm.sendOk("恭喜您,坐骑合成成功!");
			cm.dispose();
			return;
		}else{
			cm.sendOk("抱歉,您的道具不足,装备合成失败!");
			cm.dispose();
			return;
		}
	}
	}	
}
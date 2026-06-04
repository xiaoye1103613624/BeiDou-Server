



var itemlist = [
    [4251401,1],
	[4250802,1],
	[4250902,1],
	[4251002,1],
	[4251102,1],
	[4011007,10],
	[4021009,10],
];


function start() {
    status = -1;

    action(1, 0, 0)
}

function action(mode, type, selection) {
    if (status <= 0 && mode <= 0) {
        cm.dispose();
        return
    }
    if (mode == 1) {
        status++
    } else {
        status--
    }
    if (status == 0) {
		
		var str = "确定需要合成 #v4251201# 吗?\r\n\r\n";
		for(var i=0;i<itemlist.length;i++){
			str += "需要 #v"+itemlist[i][0]+"# * "+itemlist[i][1]+" 拥有：#c"+itemlist[i][0]+"#\r\n";
		}
		cm.sendYesNo(str);
	}else if (status == 1) {
		var isOk = true;
		for(var i=0;i<itemlist.length;i++){
			if(!cm.haveItem(itemlist[i][0],itemlist[i][1])){
				isOk = false;
			}
		}
		if(isOk){
			for(var s=0;s<itemlist.length;s++){
				cm.gainItem(itemlist[s][0],-itemlist[s][1]);
			}
			cm.gainItem(4251201,1);
			cm.sendOk("合成成功！");
			cm.dispose();
			
		}else{
			cm.sendOk("合成失败，材料不足！");
			cm.dispose();
		}
	}
}

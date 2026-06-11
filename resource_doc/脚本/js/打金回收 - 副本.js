var status = -1;
var acc = "#fEffect/CharacterEff/1112903/0/0#";//红桃心
var 表情高兴 = "#fUI/GuildBBS/GuildBBS/Emoticon/Basic/2#";
var stype;
var Beans = 0;
var wtlist = new Array(

        Array(4007001,1),//粉末
        Array(4007002,1),//粉末
        Array(4007003,1),//粉末
        Array(4007004,1),//粉末
        Array(2000000,50),
        Array(2000001,50),
        Array(2000002,50),
        Array(2000003,50),
        Array(2010000,50),
        Array(2010001,50),
        Array(2010002,50),
        Array(2010003,50),
        Array(2010004,50),
        Array(2010005,50),
        Array(2010006,50),
        Array(2010007,50),		
        Array(2010009,50),				
        Array(2290138,50)	
);
var xhslist = new Array();
function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            cm.dispose();
        }
        status--;
    }
    if (status == 0) {
        var selStr = "                  #d"+表情高兴+" 打 金 回 收 "+表情高兴+"\r\n\r\n#l";
            selStr += " ----------------------------------------------------- \r\n";
			selStr += "判断你可回收";

			for(var i = 0; i < wtlist.length; i++){
				if(cm.haveItem(wtlist[i][0],1)){
					xhslist.push(wtlist[i][0]);
					Beans += cm.getPlayer().getItemQuantity(wtlist[i][0], false) * wtlist[i][1];
					selStr += "#i"+wtlist[i][0]+":#";
				}
			}
						selStr += "\r\n           #d  『背包检测:可获得 #r"+Beans+" 金币#d』#l#k\r\n               #L0#"+acc+""+acc+" [一键回收] "+acc+""+acc+"#l\r\n";
 selStr += "\r\n ---------------------------------------------------- \r\n";
			
			for(var i = 0; i < wtlist.length; i++){
			if(i % 1 ==0)

		      selStr +="\r\n"
				if(cm.haveItem(wtlist[i][0],1)){
				//	Beans += cm.getPlayer().getItemQuantity(wtlist[i][0], false) * wtlist[i][1];
				}
				selStr += "#b#i"+wtlist[i][0]+":##z"+wtlist[i][0]+":# 回收价格#r"+wtlist[i][1]+"#k金币 ";//显示需要物品的图形
			}

			//selStr += "背包可回收:(不回收的放仓库)";

 
 cm.sendSimple(selStr);
    } else if (status == 1) {
		stype = selection;
		if(selection == 0){	

			var selStr = "";
			for(var i = 0; i < wtlist.length; i++){
				if(cm.haveItem(wtlist[i][0],1)){
					xhslist.push(wtlist[i][0]);
					selStr += "#i"+wtlist[i][0]+":#";
				}
			}
			selStr += "\r\n";
			selStr += "#b你确定要将你背包里面的这些物品回收成#r#e"+Beans+"#b#n金币吗？";//hsjf
			cm.sendYesNo(selStr);
		} else if(selection == 1){
			cm.dispose();
			cm.openNpc(9010000, "赏金猎人商城");
			//cm.sendOk("#b当前NPC正在努力更新中，请留意公告,001");
			
		}
	} else if (status ==2){
		if(stype == 0){
			for(var i =0; i < xhslist.length; i++){
			cm.removeAll(xhslist[i]); 

			}
			cm.gainMeso(Beans);
		   // cm.全服黄色喇叭("[回收讯息] : 恭喜["+cm.getPlayer().getName()+"]将道具装备回收获得: "+Beans+" 豆豆点")
		//	cm.喇叭(3,"["+cm.getName()+"]在赏金路程中所向披靡，恭喜你获得"+Beans+"豆豆点");
			cm.sendOk("#b打金分解机 你成功获得了#r#e"+Beans+"#b#n金币!");
			cm.dispose();

		}
    }
}
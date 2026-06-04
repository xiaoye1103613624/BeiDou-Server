var status = -1;
var acc = "#fEffect/CharacterEff/1112903/0/0#";//红桃心
var 表情高兴 = "#fUI/GuildBBS/GuildBBS/Emoticon/Basic/2#";
var stype;
var Beans = 0;
var wtlist = new Array(
        Array(4031627,1),//%50
        Array(4031633,1),
        Array(4031634,1),
        Array(4031635,1),
        Array(4031636,1),
		
        Array(4031630,2),
        Array(4031637,2),
        Array(4031638,2),
        Array(4031639,2),
        Array(4031640,2),
		
        Array(4031628,3),
        Array(4031641,3),
        Array(4031642,3),
        Array(4031643,3),
        Array(4031644,3),
		
        Array(4031631,4),
        Array(4031645,4),
        Array(4031646,4),
        Array(4031647,4),
        Array(4031648,4)
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
        var selStr = "               #e#r#v3994742# 钓 鱼 回 收 #v3994742##n\r\n";
            selStr += "\r\n ----------------------------------------------------- \r\n";

			for(var i = 0; i < wtlist.length; i++){
				if(cm.haveItem(wtlist[i][0],1)){
					xhslist.push(wtlist[i][0]);
					Beans += cm.getPlayer().getItemQuantity(wtlist[i][0], false) * wtlist[i][1];
				}
			}
			selStr += "\t\t\t  #b当前回收可获得 #e#r"+Beans+" #b#n条 #z3994742#\r\n";

			selStr += "#r#e             #L0#"+acc+""+acc+" [一键回收] "+acc+""+acc+"#l#k#n\r\n";  
            selStr += "\r\n ----------------------------------------------------- ";	
			selStr += "#r#e        #L1#" + acc + acc + " [使用彩虹鱼兑换黄金鱼] " + acc + acc + "#l#k#n\r\n";
            selStr += "\r\n ----------------------------------------------------- \r\n";			

			for(var i = 0; i < wtlist.length; i++){
			if(i % 1 ==0)
		      selStr +="\r\n"
				if(cm.haveItem(wtlist[i][0],1)){
				}
				selStr += "#r1条 #b#z"+wtlist[i][0]+":##v"+wtlist[i][0]+":# #k 兑换 #r"+wtlist[i][1]+"条 #b#z3994742##v3994742#";
			}

			cm.sendSimple(selStr);
			
    } else if (status == 1) {
		stype = selection;
		if(selection == 0){	
            if (Beans <= 0) {          
                cm.sendOk("你背包里没有任何可回收的鱼，无法进行操作。");
                cm.dispose();
                return;                
            }
			var selStr = "";
			for(var i = 0; i < wtlist.length; i++){
				if(cm.haveItem(wtlist[i][0],1)){
					xhslist.push(wtlist[i][0]);
					selStr += "#i"+wtlist[i][0]+":#";
				}
			}
			selStr += "\r\n";
			selStr += "#b背包里面的所有鱼一共可以回收 #r#e"+Beans+"#b#n 小鱼";
			cm.sendYesNo(selStr);
		} else if(selection == 1){
			cm.dispose();
			cm.openNpc(9330108, "回收鱼1");
		}
	} else if (status ==2){
		if(stype == 0){
			// 移除背包中所有可回收鱼
			for(var i =0; i < xhslist.length; i++){
				cm.removeAll(xhslist[i]); 
			}
			
			// ********** 核心修改：拆分发放物品，单次最多30000 **********
			var maxOnce = 30000; // 单次发放最大数量
			var total = Beans;   // 总需发放数量
			var times = Math.floor(total / maxOnce); // 完整发放次数（30000/次）
			var remainder = total % maxOnce;        // 最后一次剩余数量
			
			// 循环发放完整批次（30000/次）
			for(var t = 0; t < times; t++){
				cm.gainItem(3994742, maxOnce);
			}
			// 发放剩余数量（若有，不足30000）
			if(remainder > 0){
				cm.gainItem(3994742, remainder);
			}
			// **********************************************************
			
			// 全服喇叭公告+回收成功提示
		    cm.全服黄色喇叭("[小鱼回收讯息] : 恭喜["+cm.getPlayer().getName()+"]将 小鱼儿 回收获得: "+Beans+" 条 彩虹鱼");
			cm.sendOk("#b回收成功，你获得了#r#e"+Beans+"#b#n小鱼!");
			cm.dispose();
		}
    }
}
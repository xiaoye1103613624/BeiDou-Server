
var 星星 = "#fEffect/CharacterEff/1112903/0/0#";
var 爱心 = "#fEffect/CharacterEff/1032063/0/0#";
var 红色箭头 = "#fUI/UIWindow/Quest/icon6/7#";
var 正方形 = "#fUI/UIWindow/Quest/icon3/6#";
var 蓝色箭头 = "#fUI/UIWindow/Quest/icon2/7#";
var ttt ="#fUI/UIWindow.img/Quest/icon9/0#";
var xxx ="#fUI/UIWindow.img/Quest/icon8/0#";
var sss ="#fUI/UIWindow.img/QuestIcon/3/0#";
var 表情 = "#fUI/GuildBBS.img/GuildBBS/Emoticon/Basic/0#";
var 表情1 = "#fUI/GuildBBS.img/GuildBBS/Emoticon/Basic/1#";
var 表情2 = "#fUI/GuildBBS.img/GuildBBS/Emoticon/Basic/2#";
var 蓝色小兔子 = "#fEffect/CharacterEff.img/1112960/3/1#";
var 小红星 = "#fEffect/CharacterEff.img/1112926/0/0#";
var 小蓝星 = "#fEffect/CharacterEff.img/1112925/0/0#";
var 音符3 = "#fEffect/CharacterEff.img/1112949/2/0#";
var 蓝色时钟 = "#fUI/UIWindow.img/Quest/TimeQuest/AlarmClock/default/0/0#"; 
var 红色时钟 = "#fUI/UIWindow.img/Quest/TimeQuest/AlarmClock/default/4/0#"; 
var 任务简介 = "#fUI/UIWindow.img/Quest/summary#"; 
var 任务提示 = "#fUI/UIWindow.img/Quest/BtAlert/mouseOver/0#"; 
var 成功了 = "#fEffect/BasicEff.img/Fishing/6#"; 
var 橙条 = "#fUI/UIWindow.img/Minigame/Common/barTeamA#"; 
var 蘑菇 = "#fUI/UIWindow.img/Minigame/Common/mark#";

///////////////////////////////////////////
var vipLevel = 0;
var itemList = new Array();//玩家背包
var item分解池 = new Array(4007001,4007002,4007003,4007004);
var item分解比例 = new Array(500,1500,7500,28000);
var 需要分解Array = new Array();
var 分解物品Array = new java.util.ArrayList();
var 分解物品数量Array = new java.util.ArrayList();
var 分解点券Array = new java.util.ArrayList();
var 分解次数 = 0;

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
		if(status == 0){
			var pdd = "";
			pdd += "\t\t\t\t#b[打金分解机]#k\r\n";
			pdd += "此机器只会分解#v4007001##v4007002##v4007003##v4007004#\r\n";
			
			pdd += "分解比例获取获得以下物品:500 1500 7500 28000 金币\r\n";
			//pdd += "1000#r[100%]#k\r\n";
		
		
			pdd += "\t\t\t\t#d是否确认进行分解#k";
			cm.sendYesNo(pdd);
			
		}
        else if (status == 1) {
            var text = "";
			text += ""+蘑菇+蘑菇+蘑菇+蘑菇+蘑菇+蘑菇+蘑菇+蘑菇+蘑菇+蘑菇+蘑菇+蘑菇+蘑菇+蘑菇+蘑菇+蘑菇+蘑菇+蘑菇+蘑菇+蘑菇+"\r\n\r\n";
			text += "尊敬的#r"+cm.getPlayer().getName()+"#k,您好!\r\n";
			itemList = cm.getInventory(4).list().iterator();
			var text = "";
			text += "本次分解列表:\r\n";
				while (itemList.hasNext()) {
					var item = itemList.next();
					//text += "#v" + item.getItemId() + "##z"+item.getItemId()+"# x #c"+ item.getItemId() +"#\r\n";
					for(var i = 0; i<item分解池.length; i++) {
					    	if (item.getItemId() == item分解池[i]) {
								text += "#v" + item.getItemId() + "##z"+item.getItemId()+"# x 可以回收金币"+ item分解比例[i] +"\r\n";
								分解次数 += item.getQuantity();
								分解物品Array.add(item.getItemId());//添加物品id
								分解物品数量Array.add(item.getQuantity());
								分解点券Array.add(item分解比例[i]);
						}
					}
				}
			text += "总共分解#b:"+分解次数+"#k次\r\n";
			text += "#L0##d是否进行分解#k???";
			cm.sendSimple(text);
        } else if (status == 2){
        // 第一部分代码
		//var 其他栏空位 = cm.getChar().getInventory(MapleInventoryType.ETC).getNumFreeSlot();
			if(selection == 0){
				if(cm.getMeso() < (分解次数*0)){
					cm.sendOk("金币不够");
					cm.dispose();
				}else if(分解次数 < 1){
					cm.sendOk("分解次数不能是#r0#k次");
					cm.dispose();
				
				}else{
					//cm.sendOk("分解物品有"+分解物品Array.size()+"种");
					for(var j = 0; j < 分解物品Array.size();j++){
						cm.gainItem(分解物品Array.get(j),-(分解物品数量Array.get(j)));
						cm.gainMeso(分解点券Array.get(j)*(分解物品数量Array.get(j)));
						cm.dispose();
					}
					//cm.sendOk(分解次数);
					for(var i = 0;i < 分解次数;i++){
						var chance = Math.ceil(Math.random()* 100);
					if(chance > 0 && chance <=110){
						//cm.gainItem(2041200,1);
						cm.dispose();
				
					}
					}
					cm.sendOk("分解成功");
					
					cm.dispose();
				}
			} 
        // ------------------------------beauty部分-------------------------------------- //
        } else if (status == 2){       
        // 第二部分代码
        }
    }
}



var 铅笔图标 = "#fUI/UIWindow.img/PvP/btWrite/mouseOver/0#";
var 警报灯 = "#fUI/StatusBar/BtClaim/normal/0#";
var 兔子1 = "#fEffect/CharacterEff/1082565/0/0#";
var 兔子2 = "#fEffect/CharacterEff/1082565/2/0#";
var 兔子3 = "#fEffect/CharacterEff/1082565/4/0#";
var selectio;
var 商店物品 = Array( 

//                代码  数量 价格 后面不管他
////////////////////////////////////////////尾数不能加
			Array(2049135,1,15, "", ""),
			Array(2049122,1,1, "", ""),
			Array(2049122,1,3, "", ""),	
			Array(2049104,1,2, "", "")
			
			);
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
	cm.Lunpan();
           // cm.Guaguale();
            return;
        }
        if (mode == 1) {
            status++;
        }
        else {
            status--;
        }
        if (status == 0) {
			var str1 = "";	
			for (var i = 0; i < 商店物品.length; i++){
                   str1 += "#L"+i+"##v"+商店物品[i][0]+"##z"+商店物品[i][0]+"#× #b"+商店物品[i][1]+"#d "+商店物品[i][4]+"#d  价格:#r"+商店物品[i][2]+"#d "+商店物品[i][3]+"#l\r\n";
            }
            cm.sendSimple("你好，这里是 - 师父商城 - \r\n 您目前的师父积分:#r"+cm.getBossRank8("出师积分",2)+"#n#d  \r\n\r\n"+str1);//#L2#"+兔子2+"#r推广系统介绍 - 奖励分红详细说明
		} else if (status == 1) {
			selectio = selection;
			cm.sendGetNumber("请你填写你要购买的数量:",0,1,1000);
				
        } else if (status == 2) {
			if(cm.getBossRank8("出师积分",2) >= 商店物品[selectio][2] * selection){

					cm.gainItem(商店物品[selectio][0],商店物品[selectio][1] * selection); 
				cm.setBossRank8("出师积分",2,-商店物品[selectio][2] * selection);
				cm.sendOk("购买成功~!\r\n购买数量:#r#e"+selection+"");
				cm.喇叭(1,"["+ cm.getPlayer().getName() + "] 在师傅商店购买了道具!");
                cm.dispose();
			} else {
				cm.sendOk("出师积分不足 "+商店物品[selectio][1] * selection+"~!");
                cm.dispose();
			}
		}
	}
}



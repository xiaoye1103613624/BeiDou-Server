var 铅笔图标 = "#fUI/UIWindow.img/PvP/btWrite/mouseOver/0#";
var 警报灯 = "#fUI/StatusBar/BtClaim/normal/0#";
var 兔子1 = "#fEffect/CharacterEff/1082565/0/0#";
var 兔子2 = "#fEffect/CharacterEff/1082565/2/0#";
var 兔子3 = "#fEffect/CharacterEff/1082565/4/0#";
var selectio;
var 商店物品 = Array( 

//                代码  数量 价格 家族等级需要多少级才能   备注
			Array(4000000,1,1,1,""),
			Array(4170000,2,1,2,""),	





////////////////////////////////////////////尾数不能加 《 , 》
			Array(4000000,1,299,5,"")


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
        cm.dispose();
           // cm.sendOk("感谢你的光临！");
//	cm.Lunpan();
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
                   str1 += "#L"+i+"##v"+商店物品[i][0]+"##z"+商店物品[i][0]+"#× #b"+商店物品[i][1]+"#d "+商店物品[i][4]+"#d  价格:#r"+商店物品[i][2]+"#k #d需要当前家族LV.#r"+商店物品[i][3]+"#k#l\r\n";
            }
            cm.sendSimple("你好，这里是 - 家族贡献商城 - \r\n您目前的贡献积分:#r"+cm.getBossRank("贡献值",2)+"#n#d  家族:[#r"+cm.getPlayer().getGuild().getName()+"#k] 家族等级:#r"+cm.getPlayer().getGuild().getLevel()+"#n#d \r\n\r\n"+str1);//#L2#"+兔子2+"#r推广系统介绍 - 奖励分红详细说明
		} else if (status == 1) {
			selectio = selection;
			cm.sendGetNumber("请你填写你要购买的数量:",0,1,1000);
				
        } else if (status == 2) {
		 if(cm.getPlayer().getGuild().getLevel() < 商店物品[selectio][3]){
				cm.sendOk("您选中的物品,与家族等级不符无法购买\r\n所在的家族名字: ["+cm.getPlayer().getGuild().getName()+" ] \r\n当前的家族等级: "+cm.getPlayer().getGuild().getLevel()+"\r\n\r\n#r需要家族等级："+商店物品[selectio][3]+"");
                cm.dispose();
			} else if(cm.getBossRank("贡献值",2) < 商店物品[selectio][2] * selection){
				cm.sendOk("贡献值需要 "+商店物品[selectio][1] * selection+"~ ,做家族建设任务可获得贡献值!");
                cm.dispose();

			} else {
					cm.gainItem(商店物品[selectio][0],商店物品[selectio][1] * selection); 
				cm.setBossRank("贡献值",1,-商店物品[selectio][2] * selection);
				cm.sendOk("购买成功~!\r\n购买数量:#r#e"+selection+"");
				cm.喇叭(1,"["+ cm.getPlayer().getName() + "] 在 ["+cm.getPlayer().getGuild().getName()+"] 家族商店购买了道具!");
                cm.dispose();
			}
		}
	}
}



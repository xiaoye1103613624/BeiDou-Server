/*
 * 
 * @QILIN
 * @npc翅膀进价+2级
 */
 var 奖励 = "#fUI/CashShop/CSDiscount/bonus#";
 var 正方箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";
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
                
   cm.sendOk("感谢使用.");
   cm.dispose();
   return;                    
                }
                if (mode == 1) {
   status++;
  }
  else {
   status--;
  }
          if (status == 0) {
	var tex2 = "";	
	var text = "";
	for(i = 0; i < 10; i++){
		text += "";
	}				
	text += "#r进价终极翅膀需要以下物品:#n\r\n#v1102798##d#z1102798# X 1 #v4000463##d#z4000463# X 5000\r\n        #v4000313##d#z4000313# X 8000 #v4001126##d#z4001126# X 8000\r\n        #v4000038##z4000038# X 1000    #v4310148##z4310148# X 200 #v4310034##z4310034# X 1000 #v4310029##z4310029# X 1000金币：8000万\r\n         "+奖励+"\r\n#v1102723##z1102723# 全属性+80,功魔+50, X 1"
	text += "\r\n#e#k#L1#"+正方箭头+"确定进价终极翅膀";//永久
	//text += "     \r\n"
        cm.sendSimple(text);
        } else if (selection == 1) {
                      if(!cm.canHold(1102723,1)){
			cm.sendOk("请清理你的背包，至少空出2个位置！");
            cm.dispose();
        }
else if(cm.getMeso() < 80000000) {
            cm.sendOk("抱歉您的金币不足8000万，请凑足了再来！");
            cm.dispose();
        }		else if(cm.haveItem(1102798,1) && cm.haveItem(4000463,5000)&& cm.haveItem(4310148,200)&& cm.haveItem(4310034,1000)&& cm.haveItem(4310029,1000) && cm.haveItem(4000313,8000) && cm.haveItem(4001126,8000) && cm.haveItem(4000038,1000)){
				cm.gainItem(1102798, -1);
				cm.gainItem(4000313, -8000);
				cm.gainItem(4000463, -5000);
				cm.gainItem(4310148, -200);
				cm.gainItem(4310034, -1000);
				cm.gainItem(4310029, -1000);
				cm.gainMeso(-80000000);
				cm.gainItem(4001126, -8000);
				cm.gainItem(4000038, -1000);
            cm.给属性装备(1102723, 0, 0, 80, 80, 80, 80, 0, 0, 50, 50,0, 0, 30, 30, 0, 0, 0);
            cm.sendOk("恭喜你成功进价为终极翅膀全属性+50，功魔+50,祝你游戏愉快。");
            cm.dispose();
cm.全服黄色喇叭("[翅膀进价] : 恭喜玩家 【"+cm.getPlayer().getName()+"】 成功进价终极翅膀全属性+50，功魔+50,未来的明日之星！")
 
			}else{
            cm.sendOk("进价翅膀材料或者不足#v1102798#X1#v4000463#X5000#v4000313#X8000#v4001126#X8000#v4000038#X1000#v4000038##z4000038# X 1000    #v4310148##z4310148# X 200 #v4310034##z4310034# X 1000 #v4310029##z4310029# X 1000\r\n");
            cm.dispose();
			}
		}
    }
}





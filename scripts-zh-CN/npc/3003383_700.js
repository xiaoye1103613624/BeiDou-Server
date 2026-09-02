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
	text += "#r翅膀七级进价需要以下物品:#n\r\n#v1103029##d#z1103029# X 1 #v4000463##d#z4000463# X 3000\r\n        #v4000313##d#z4000313# X 5000 #v4001126##d#z4001126# X 5000\r\n  #v4001006# X 800  #v4000244# X 800 #v4000245# X 800      #v4000038##z4000038# X 500 金币：7000万\r\n         "+奖励+"\r\n#v1102798##z1102798# 全属性+17 功魔+19,X 1"
	text += "\r\n#e#k#L1#"+正方箭头+"确定进价七级翅膀";//永久
	//text += "     \r\n"
        cm.sendSimple(text);
        } else if (selection == 1) {
                      if(!cm.canHold(1102798,1)){
			cm.sendOk("请清理你的背包，至少空出2个位置！");
            cm.dispose();
        }
else if(cm.getMeso() < 70000000) {
            cm.sendOk("抱歉您的金币不足7000万，请凑足了再来！");
            cm.dispose();
        }		else if(cm.haveItem(1103029,1) && cm.haveItem(4000463,3000) && cm.haveItem(4001006,800)&& cm.haveItem(4000244,800)&& cm.haveItem(4000245,800) && cm.haveItem(4000313,5000) && cm.haveItem(4001126,5000) && cm.haveItem(4000038,500)){
				cm.gainItem(1103029, -1);
				cm.gainItem(4000313, -5000);
				cm.gainItem(4000463, -3000);
				cm.gainItem(4001006, -800);
				cm.gainItem(4000244, -800);
				cm.gainItem(4000245, -800);
				cm.gainMeso(-70000000);
				cm.gainItem(4001126, -5000);
				cm.gainItem(4000038, -500);
            cm.给属性装备(1102798, 0, 0, 17, 17, 17, 17, 0, 0, 19, 19,0, 0, 0, 0, 0, 0, 0);
            cm.sendOk("恭喜你成功进价为七级翅膀全属性+17，功魔+19,祝你游戏愉快。");
            cm.dispose();
cm.全服黄色喇叭("[翅膀进价] : 恭喜玩家 【"+cm.getPlayer().getName()+"】 成功进价七级翅膀全属性+17，功魔+19,未来的明日之星！")
 
			}else{
            cm.sendOk("进价翅膀材料或者不足#v1103029#X1#v4000463#X3000#v4000313#X5000#v4001126#X5000#v4000038#X500#v4001006# X 800 #v4000244# X 800 #v4000245# X 800\r\n");
            cm.dispose();
			}
		}
    }
}





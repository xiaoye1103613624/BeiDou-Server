/*麒麟端作者qq1500663066或327321366*/



var 中条猫 ="#fUI/ChatBalloon/37/n#";
var 猫右 =  "#fUI/ChatBalloon/37/ne#";
var 猫左 =  "#fUI/ChatBalloon/37/nw#";
var 右 =    "#fUI/ChatBalloon/37/e#";
var 左 =    "#fUI/ChatBalloon/37/w#";
var 下条猫 ="#fUI/ChatBalloon/37/s#";
var 猫下右 ="#fUI/ChatBalloon/37/se#";
var 猫下左 ="#fUI/ChatBalloon/37/sw#";
var 皇冠白 ="#fUI/GuildMark/Mark/Etc/00009004/16#";
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
            for (i = 0; i < 10; i++) {
                text += "";
            }
			text += ""
			text += "                  #k"+皇冠白+" #r#e#w 材 料 兑 换 #n#k "+皇冠白+"\r\n\r\n";
			text += "  "+猫左+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+猫右+"\r\n";
			text += "\t#L44##k[#v3600001##r#c3600001##k/5+10万金币]合成#d#fUI/Basic/BtHide3/mouseOver/0##v4170005##l\r\n\r\n"
			text += "\t#L55##k[#v3600001##r#c3600001##k/10+10万金币]合成#d#fUI/Basic/BtHide3/mouseOver/0##v4170006##l\r\n\r\n"
			text += "\t#L101##k[#v4001126##r#c4001126##k/500+2百万金币]合成#d#fUI/Basic/BtHide3/mouseOver/0##v4000463##l\r\n\r\n"			
			text += "\t#L1##k[#v4170005##r#c4170005##k/10+3百万金币]合成#d#fUI/Basic/BtHide3/mouseOver/0##v4310034##l\r\n\r\n"
            text += "\t#L2##k[#v4170006##r#c4170006##k/10+4百万金币]合成#d#fUI/Basic/BtHide3/mouseOver/0##v4310029##l\r\n\r\n"
			text += "\t#L3##k[#v4310150##r#c4310150##k/15+5百万金币]合成#d#fUI/Basic/BtHide3/mouseOver/0##v4310148##l\r\n\r\n"
			text += "\t#L66##k[#v4032391##r#c4032391##k/200]兑换#d#fUI/Basic/BtHide3/mouseOver/0##v2340000##l\r\n\r\n"
			text += "\t#L67##k[#v4032392##r#c4032392##k/200]兑换#d#fUI/Basic/BtHide3/mouseOver/0##v2049116##l\r\n\r\n"
			text += "\t#L68##k[#v2049100##r#c2049100##k/2]兑换#d#fUI/Basic/BtHide3/mouseOver/0##v2049116##l\r\n\r\n"
            text += "\t#L99##k[#v4170002##r#c4170002##k/30]兑换#d#fUI/Basic/BtHide3/mouseOver/0##v1902001##l\r\n\r\n"
			text += "\t#L100##k[#v4170005##r#c4170005##k/35]兑换#d#fUI/Basic/BtHide3/mouseOver/0##v1912000##l\r\n\r\n"
            cm.sendSimple(text);
		}
		else if (selection == 3) {
			if(cm.getMeso() < 10000000) {
            cm.sendOk("抱歉您的金币不足500万，请凑足了再来！");
            cm.dispose();}
            else{
			
			if(cm.haveItem(4310150,15) ){
				cm.gainMeso(-5000000);
				cm.gainItem(4310150,-15);
				cm.gainItem(4310148,1);//星之币
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(3, cm.getClient().getChannel(), "『材料合成』" + " : " + "[" + cm.getChar().getName() + "]通过合成材料，获得了星之币！")); 
				cm.dispose();
			}else{
				cm.sendOk("\t纪念币数量不足。");
				cm.dispose();
			}
			}
		}
		
				else if (selection == 101) {
			if(cm.getMeso() < 5000000) {
            cm.sendOk("抱歉您的金币不足200万，请凑足了再来！");
            cm.dispose();}
            else{
			
			if(cm.haveItem(4001126,500) ){
				cm.gainMeso(-2000000);
				cm.gainItem(4001126,-500);
				cm.gainItem(4000463,1);//国庆纪念币
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(3, cm.getClient().getChannel(), "『材料合成』" + " : " + "[" + cm.getChar().getName() + "]通过合成材料，获得了国庆纪念币！")); 
				cm.dispose();
			}else{
				cm.sendOk("\t纪念币数量不足。");
				cm.dispose();
			}
			}
		}
		
		else if (selection == 55) {
			if(cm.getMeso() < 100000) {
            cm.sendOk("抱歉您的金币不足10万，请凑足了再来！");
            cm.dispose();}
            else{
			
			if(cm.haveItem(3600001,10) ){
				cm.gainMeso(-100000);
				cm.gainItem(3600001,-10);
				cm.gainItem(4170006,1);//天空蛋
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(3, cm.getClient().getChannel(), "『材料合成』" + " : " + "[" + cm.getChar().getName() + "]通过合成材料，获得了天空蛋！")); 
				cm.dispose();
			}else{
				cm.sendOk("\t跑环币数量不足。");
				cm.dispose();
			}
			}
		}
		
		else if (selection == 44) {
			if(cm.getMeso() < 100000) {
            cm.sendOk("抱歉您的金币不足10万，请凑足了再来！");
            cm.dispose();}
            else{
			
			if(cm.haveItem(3600001,5) ){
				cm.gainMeso(-100000);
				cm.gainItem(3600001,-5);
				cm.gainItem(4170005,1);//玩具蛋
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(3, cm.getClient().getChannel(), "『材料合成』" + " : " + "[" + cm.getChar().getName() + "]通过合成材料，获得了玩具蛋！")); 
				cm.dispose();
			}else{
				cm.sendOk("\t跑环币数量不足。");
				cm.dispose();
			}
			}
		}

		else if (selection == 1) {
			if(cm.getMeso() < 8000000) {
            cm.sendOk("抱歉您的金币不足300万，请凑足了再来！");
            cm.dispose();}
            else{
			
			if(cm.haveItem(4170005,10) ){
				cm.gainMeso(-3000000);
				cm.gainItem(4170005,-10);
				cm.gainItem(4310034,1);//正义币
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(3, cm.getClient().getChannel(), "『材料合成』" + " : " + "[" + cm.getChar().getName() + "]通过合成材料，获得了正义币！")); 
				cm.dispose();
			}else{
				cm.sendOk("\t玩具蛋数量不足。");
				cm.dispose();
			}
			}
		}
		else if (selection == 2) {
			if(cm.getMeso() < 8000000) {
            cm.sendOk("抱歉您的金币不足400万，请凑足了再来！");
            cm.dispose();}
            else{
			
			if(cm.haveItem(4170006,10) ){
				cm.gainMeso(-4000000);
				cm.gainItem(4170006,-10);
				cm.gainItem(4310029,1);//十字币
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(3, cm.getClient().getChannel(), "『材料合成』" + " : " + "[" + cm.getChar().getName() + "]通过合成材料，获得了十字币！")); 
				cm.dispose();
			}else{
				cm.sendOk("\t天空蛋数量不足。");
				cm.dispose();
			}
			}
		}else if (selection == 67) {
			if(cm.haveItem(4032392,200) ){
				cm.gainItem(4032392,-200);
				cm.gainItem(2049116,1);
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(3, cm.getClient().getChannel(), "『兑换中心』" + " : " + "[" + cm.getChar().getName() + "]通过兑换物品，获得了一张强化混沌卷轴！")); 
				cm.dispose();
			}else{
				cm.sendOk("\t材料不足。");
				cm.dispose();
			}
		}
		else if (selection == 66) {
			if(cm.haveItem(4032391,200) ){
				cm.gainItem(4032391,-200);
				cm.gainItem(2340000,1);
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(3, cm.getClient().getChannel(), "『兑换中心』" + " : " + "[" + cm.getChar().getName() + "]通过兑换物品，获得了一张祝福卷轴！")); 
				cm.dispose();
			}else{
				cm.sendOk("\t材料不足。");
				cm.dispose();
			}
			}
		else if (selection == 68) {
			if(cm.haveItem(2049100,2) ){
				cm.gainItem(2049100,-2);
				cm.gainItem(2049116,1);
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(3, cm.getClient().getChannel(), "『兑换中心』" + " : " + "[" + cm.getChar().getName() + "]通过兑换物品，获得了一张强化混沌卷轴！")); 
				cm.dispose();
			}else{
				cm.sendOk("\t材料不足。");
				cm.dispose();
			}
		}
		else if (selection == 99) {
			if(cm.haveItem(4170002,30) ){
				cm.gainItem(4170002,-30);
				cm.gainItem(1902001,1);
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(3, cm.getClient().getChannel(), "『兑换中心』" + " : " + "[" + cm.getChar().getName() + "]通过兑换物品，获得了野猪坐骑！")); 
				cm.dispose();
			}else{
				cm.sendOk("\t材料不足。");
				cm.dispose();
			}
		}
		else if (selection == 100) {
			if(cm.haveItem(4170005,35) ){
				cm.gainItem(4170005,-35);
				cm.gainItem(1912000,1);
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(3, cm.getClient().getChannel(), "『兑换中心』" + " : " + "[" + cm.getChar().getName() + "]通过兑换物品，获得了坐骑鞍子！")); 
				cm.dispose();
			}else{
				cm.sendOk("\t材料不足。");
				cm.dispose();
			}
		}
   else if (status == 2) {
            if (beauty == 1) {
				var zliang = cm.getPlayer().getItemQuantity(4001126, false);
                if (zliang > 0){
					cm.removeAll(4001126);
					cm.gainMeso(8000*zliang);					
							
                    cm.sendOk("合成成功。共合成了:[#r"+(zliang)+"#k] 个。");
					cm.worldMessage(6,"[相框合成]：玩家 "+cm.getName()+" 努力搬砖,在自由相框合成了："+(zliang*8000)+" 金币。");
					cm.dispose();
                } else {
                    cm.sendOk("您的物品不足，无法合成。");
                    cm.dispose()
                }            		
            }if (beauty == 4) {
				var zliang = cm.getPlayer().getItemQuantity(4001128, false);
                if (zliang > 0){
					cm.removeAll(4001128);  
					cm.getPlayer().modifyCSPoints(2,zliang*2, true);		
                    cm.sendOk("合成成功。共合成了:[#r"+(zliang)+"#k] 个。");
					cm.worldMessage(6,"[合成中心]：玩家 "+cm.getName()+" 努力搬砖,在自由相框用炸药桶合成了："+(zliang*2)+" 抵用卷。");//公告
					cm.dispose();
                } else {
                    cm.sendOk("您的物品不足，无法合成。");
                    cm.dispose()
                }
            }
			
        }
    }
}
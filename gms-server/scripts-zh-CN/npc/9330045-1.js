/* ==================
 脚本类型:  双倍卡领取NPC	    
 脚本作者： 枫叶
 联系方式QQ： 1848350048
 =====================
 */
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
			text += "\t\t"+彩虹+"  #e#d 领 取 礼 包 #k#n  #r  "+彩虹+"#b#k#n\r\r\n"+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+"\r\n\r\n";
            
			//text += "\t#L66##k领取#d#fUI/Basic/BtHide3/mouseOver/0#内测礼包#l\r\n\r\n"
			//text += "\t#L72##k领取#d#fUI/Basic/BtHide3/mouseOver/0#点卷礼包#l\r\n\r\n"//3
			//text += "\t#L67##k领取#d#fUI/Basic/BtHide3/mouseOver/0#女神礼包#l\r\n\r\n"
			//text += "\t#L100##k领取#d#fUI/Basic/BtHide3/mouseOver/0#男神礼包#l\r\n\r\n"
			//text += "\t#L101##k领取#d#fUI/Basic/BtHide3/mouseOver/0#透明帽礼包#l\r\n\r\n"
			//text += "\t#L102##k领取#d#fUI/Basic/BtHide3/mouseOver/0#透明面具礼包#l\r\n\r\n"
			//text += "\t#L103##k领取#d#fUI/Basic/BtHide3/mouseOver/0#透明眼饰礼包#l\r\n\r\n"
			//text += "\t#L104##k领取#d#fUI/Basic/BtHide3/mouseOver/0#透明耳环礼包#l\r\n\r\n"
			//text += "\t#L105##k领取#d#fUI/Basic/BtHide3/mouseOver/0#透明鞋子礼包#l\r\n\r\n"
			//text += "\t#L106##k领取#d#fUI/Basic/BtHide3/mouseOver/0#透明手套礼包#l\r\n\r\n"
			//text += "\t#L107##k领取#d#fUI/Basic/BtHide3/mouseOver/0#透明盾牌礼包#l\r\n\r\n"
			//text += "\t#L108##k领取#d#fUI/Basic/BtHide3/mouseOver/0#透明披风礼包#l\r\n\r\n"
			//text += "\t#L488##k领取#d#fUI/Basic/BtHide3/mouseOver/0#3.16维护礼包#l\r\n\r\n"
			
            //text += "\t#L99##k[#v4170002##r#c4170002##k/30]领取#d#fUI/Basic/BtHide3/mouseOver/0##v1902001##l\r\n\r\n"//3
			//text += "\t#L100##k[#v4170005##r#c4170005##k/35]领取#d#fUI/Basic/BtHide3/mouseOver/0##v1912000##l\r\n\r\n"//3
			//text += "\t#L1100##k[#v4001128##r#c4001128##k/100]领取#d#fUI/Basic/BtHide3/mouseOver/0##v4001126##l50个\r\n\r\n"
            
			
			
			//text += "     #L68##b100枫叶兑换#d#fUI/Basic/BtHide3/mouseOver/0#10HP  (#r比例 100:10#d)#l\r\n\r\n"
			//text += "     #L69##b100枫叶兑换#d#fUI/Basic/BtHide3/mouseOver/0#10MP  (#r比例 100:10#d)#l\r\n\r\n"
            cm.sendSimple(text);
		}else if (selection == 48) {//游戏中心
				cm.dispose();
				cm.openNpc(9310072, 48);
        }else if (selection == 488) {//游戏中心
				cm.dispose();
				cm.openNpc(3003332, "维护礼包");
        }
		else if (selection == 72) { 
			cm.dispose();
				cm.openNpc(9900004, "点卷礼包");
        }
		else if (selection == 70) { 
			var zliang = cm.getPlayer().getItemQuantity(4032226, false);
			if (zliang == 0) {
                    cm.sendOk("你的物品不足兑换.");
                    status = -1;
                } else {
                    beauty = 70
					cm.sendYesNo("当前共有: #r"+zliang+"#k 个，是否把它们全部兑换吗？");
					}
        }
		else if (selection == 68) {
			var PlayselfMaxHp = cm.getPlayer().getMaxHp();
			if(cm.haveItem(4001126,100) ){
				cm.gainItem(4001126,-100);
				cm.getPlayer().getStat().setMaxHp(cm.getPlayer().getStat().getMaxHp()+ 10);
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(3, cm.getClient().getChannel(), "『兑换中心』" + " : " + "[" + cm.getChar().getName() + "]通过兑换物品，获得了10HP！")); 
				cm.dispose();
			}else{
				cm.sendOk("\t材料不足。");
				cm.dispose();
			}
		}else if (selection == 69) {
			var PlayselfMaxMp = cm.getPlayer().getMaxMp();
			if(cm.haveItem(4001126,100) ){
				cm.gainItem(4001126,-100);
				cm.getPlayer().getStat().setMaxMp(cm.getPlayer().getStat().getMaxMp()+ 10);
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(3, cm.getClient().getChannel(), "『兑换中心』" + " : " + "[" + cm.getChar().getName() + "]通过兑换物品，获得了10MP！")); 
				cm.dispose();
			}else{
				cm.sendOk("\t材料不足。");
				cm.dispose();
			}
		}
		else if (selection == 67) {
			if(cm.haveItem(4001239,1) ){
				cm.gainItem(4001239,-1);
				cm.gainItem(1142574,50,50,50,50,80,80,20,20,5,5,8,8,0,0);//女神徽章
				cm.gainItem(5150038,1);//超级明星美发卡
				cm.gainMeso(2000000);//给金币200万
				cm.gainDY(100000);//给抵用卷10万
				cm.getPlayer().modifyCSPoints(1,50000, true);//给点卷，1为点卷0为抵用卷
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(3, cm.getClient().getChannel(), "『女神认证』" + " : " + "[" + cm.getChar().getName() + "]通过女神认证，获得了女神礼包！")); 
				cm.dispose();
			}else{
				cm.sendOk("\t抱歉，您还没有资格领取女神礼包哦。请找管理确认后再来领取！");
				cm.dispose();
			}
		}
				else if (selection == 101) {
			if(cm.haveItem(4001239,1) ){
				cm.gainItem(4001239,-1);
				cm.gainItem(1002186,3,3,3,3,50,50,2,2,2,2,8,8,0,0);//透明帽
				cm.gainItem(5150038,1);//超级明星美发卡
				cm.gainMeso(2000000);//给金币200万
				cm.gainDY(50000);//给抵用卷5万
				cm.getPlayer().modifyCSPoints(1,50000, true);//给点卷，1为点卷0为抵用卷
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(3, cm.getClient().getChannel(), "『透明帽礼包』" + " : " + "[" + cm.getChar().getName() + "]通过透明帽礼包，获得了透明帽！")); 
				cm.dispose();
			}else{
				cm.sendOk("\t抱歉，您还没有资格领取透明帽礼包哦。请找管理确认后再来领取！");
				cm.dispose();
			}
		}
						else if (selection == 102) {
			if(cm.haveItem(4001239,1) ){
				cm.gainItem(4001239,-1);
				cm.gainItem(1012057,3,3,3,3,50,50,2,2,2,2,8,8,0,0);//透明面具
				cm.gainItem(5150038,1);//超级明星美发卡
				cm.gainMeso(2000000);//给金币200万
				cm.gainDY(50000);//给抵用卷5万
				cm.getPlayer().modifyCSPoints(1,50000, true);//给点卷，1为点卷0为抵用卷
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(3, cm.getClient().getChannel(), "『透明面具礼包』" + " : " + "[" + cm.getChar().getName() + "]通过透明面具礼包，获得了透明面具！")); 
				cm.dispose();
			}else{
				cm.sendOk("\t抱歉，您还没有资格领取透明面具礼包哦。请找管理确认后再来领取！");
				cm.dispose();
			}
		}
			else if (selection == 103) {
			if(cm.haveItem(4001239,1) ){
				cm.gainItem(4001239,-1);
				cm.gainItem(1022048,3,3,3,3,50,50,2,2,2,2,8,8,0,0);//透明眼饰
				cm.gainItem(5150038,1);//超级明星美发卡
				cm.gainMeso(2000000);//给金币200万
				cm.gainDY(50000);//给抵用卷5万
				cm.getPlayer().modifyCSPoints(1,50000, true);//给点卷，1为点卷0为抵用卷
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(3, cm.getClient().getChannel(), "『透明眼饰礼包』" + " : " + "[" + cm.getChar().getName() + "]通过透明眼饰礼包，获得了透明眼饰！")); 
				cm.dispose();
			}else{
				cm.sendOk("\t抱歉，您还没有资格领取透明眼饰礼包哦。请找管理确认后再来领取！");
				cm.dispose();
			}
		}
					else if (selection == 104) {
			if(cm.haveItem(4001239,1) ){
				cm.gainItem(4001239,-1);
				cm.gainItem(1032024,3,3,3,3,50,50,2,2,2,2,8,8,0,0);//透明耳环
				cm.gainItem(5150038,1);//超级明星美发卡
				cm.gainMeso(2000000);//给金币200万
				cm.gainDY(50000);//给抵用卷5万
				cm.getPlayer().modifyCSPoints(1,50000, true);//给点卷，1为点卷0为抵用卷
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(3, cm.getClient().getChannel(), "『透明耳环礼包』" + " : " + "[" + cm.getChar().getName() + "]通过透明耳环礼包，获得了透明耳环！")); 
				cm.dispose();
			}else{
				cm.sendOk("\t抱歉，您还没有资格领取透明耳环礼包哦。请找管理确认后再来领取！");
				cm.dispose();
			}
		}
			else if (selection == 105) {
			if(cm.haveItem(4001239,1) ){
				cm.gainItem(4001239,-1);
				cm.gainItem(1072153,3,3,3,3,50,50,2,2,2,2,8,8,0,0);//透明鞋子
				cm.gainItem(5150038,1);//超级明星美发卡
				cm.gainMeso(2000000);//给金币200万
				cm.gainDY(50000);//给抵用卷5万
				cm.getPlayer().modifyCSPoints(1,50000, true);//给点卷，1为点卷0为抵用卷
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(3, cm.getClient().getChannel(), "『透明鞋子礼包』" + " : " + "[" + cm.getChar().getName() + "]通过透明鞋子礼包，获得了透明鞋子！")); 
				cm.dispose();
			}else{
				cm.sendOk("\t抱歉，您还没有资格领取透明鞋子礼包哦。请找管理确认后再来领取！");
				cm.dispose();
			}
		}
					else if (selection == 106) {
			if(cm.haveItem(4001239,1) ){
				cm.gainItem(4001239,-1);
				cm.gainItem(1082102,3,3,3,3,50,50,2,2,2,2,8,8,0,0);//透明手套
				cm.gainItem(5150038,1);//超级明星美发卡
				cm.gainMeso(2000000);//给金币200万
				cm.gainDY(50000);//给抵用卷5万
				cm.getPlayer().modifyCSPoints(1,50000, true);//给点卷，1为点卷0为抵用卷
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(3, cm.getClient().getChannel(), "『透明手套礼包』" + " : " + "[" + cm.getChar().getName() + "]通过透明手套礼包，获得了透明手套！")); 
				cm.dispose();
			}else{
				cm.sendOk("\t抱歉，您还没有资格领取透明手套礼包哦。请找管理确认后再来领取！");
				cm.dispose();
			}
		}
			else if (selection == 107) {
			if(cm.haveItem(4001239,1) ){
				cm.gainItem(4001239,-1);
				cm.gainItem(1092064,3,3,3,3,50,50,2,2,2,2,8,8,0,0);//透明盾牌
				cm.gainItem(5150038,1);//超级明星美发卡
				cm.gainMeso(2000000);//给金币200万
				cm.gainDY(50000);//给抵用卷5万
				cm.getPlayer().modifyCSPoints(1,50000, true);//给点卷，1为点卷0为抵用卷
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(3, cm.getClient().getChannel(), "『透明盾牌礼包』" + " : " + "[" + cm.getChar().getName() + "]通过透明盾牌礼包，获得了透明盾牌！")); 
				cm.dispose();
			}else{
				cm.sendOk("\t抱歉，您还没有资格领取透明盾牌礼包哦。请找管理确认后再来领取！");
				cm.dispose();
			}
		}
			else if (selection == 108) {
			if(cm.haveItem(4001239,1) ){
				cm.gainItem(4001239,-1);
				cm.gainItem(1053346,100,100,100,100,0,0,100,100,0,0,0,0,0,0);//透明披风
				cm.gainItem(5150038,1);//超级明星美发卡
				cm.gainMeso(2000000);//给金币200万
				cm.gainDY(50000);//给抵用卷5万
				cm.getPlayer().modifyCSPoints(1,50000, true);//给点卷，1为点卷0为抵用卷
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(3, cm.getClient().getChannel(), "『透明披风礼包』" + " : " + "[" + cm.getChar().getName() + "]通过透明披风礼包，获得了透明披风！")); 
				cm.dispose();
			}else{
				cm.sendOk("\t抱歉，您还没有资格领取透明披风礼包哦。请找管理确认后再来领取！");
				cm.dispose();
			}
		}
				else if (selection == 100) {
			if(cm.haveItem(4001239,1) ){
				cm.gainItem(4001239,-1);
				cm.gainItem(1142796,10,10,10,10,50,50,5,5,5,5,8,8,0,0);//男神徽章
				cm.gainItem(5150038,1);//超级明星美发卡
				cm.gainMeso(2000000);//给金币200万
				cm.gainDY(100000);//给抵用卷10万
				cm.getPlayer().modifyCSPoints(1,50000, true);//给点卷，1为点卷0为抵用卷
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(3, cm.getClient().getChannel(), "『男神认证』" + " : " + "[" + cm.getChar().getName() + "]通过男神认证，获得了男神礼包！")); 
				cm.dispose();
			}else{
				cm.sendOk("\t抱歉，您还没有资格领取男神礼包哦。请找管理确认后再来领取！");
				cm.dispose();
			}
		}
		else if (selection == 66) {
			if(cm.haveItem(4001239,1) ){
				cm.gainItem(4001239,-1);
				cm.gainItem(5150038,1);//超级明星美发卡
				cm.gainItem(1052457,9999,9999,9999,9999,50,50,999,999,5,5,8,8,0,0);//超级明星美发卡
				cm.gainMeso(2000000);//给金币200万
				cm.gainItem(4000463,50);//国庆纪念币
				cm.gainItem(4000313,150);//进阶币
				cm.gainDY(10000);//给抵用卷1万
				cm.getPlayer().modifyCSPoints(1,20000, true);//给点卷，1为点卷0为抵用卷
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(3, cm.getClient().getChannel(), "『领取礼包』" + " : " + "[" + cm.getChar().getName() + "]领取了内测礼包，获得了丰厚的奖励！")); 
				cm.dispose();
			}else{
				cm.sendOk("\t抱歉，您还没有资格领取内测礼包哦。请找管理确认后再来领取！");
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
		else if (selection == 1100) {
			if(cm.haveItem(4001128,100) ){
				cm.gainItem(4001128,-100);
				cm.gainItem(4001126,50);
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(3, cm.getClient().getChannel(), "『兑换中心』" + " : " + "[" + cm.getChar().getName() + "]通过兑换物品，获得了50个枫叶！")); 
				cm.dispose();
			}else{
				cm.sendOk("\t材料不足。");
				cm.dispose();
			}
		}

		else if (selection == 5) {
			if(cm.haveItem(4170005,1) && cm.haveItem(4170013,1) && cm.haveItem(4170002,1)){
				cm.gainItem(4170005,-1);
				cm.gainItem(4170013,-1);
				cm.gainItem(4170002,-1);
				cm.gainItem(2340000,1);
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(3, cm.getClient().getChannel(), "『相框兑换』" + " : " + "[" + cm.getChar().getName() + "]通过团队任务收益，兑换了一张祝福卷轴！")); 
				cm.dispose();
			}else{
				cm.sendOk("\t材料不足。");
				cm.dispose();
			}
		}else if (selection == 6) {
			if(cm.haveItem(4170001,1) && cm.haveItem(4170004,1) && cm.haveItem(4170009,1) ){
				cm.gainItem(4170001,-1);
				cm.gainItem(4170004,-1);
				cm.gainItem(4170009,-1);
				cm.gainItem(2049116,1);
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(3, cm.getClient().getChannel(), "『相框兑换』" + " : " + "[" + cm.getChar().getName() + "]通过团队任务收益，兑换了一张正向混沌卷轴！")); 
				cm.dispose();
			}else{
				cm.sendOk("\t材料不足。");
				cm.dispose();
			}
		}else if (selection == 3) {
			
			if(cm.getPlayer().getMeso() >= 100 ){ //物品条件
				cm.getPlayer().modifyCSPoints(1,-100, true);//点券
				cm.gainItem(4001126,100);
				//cm.gainMeso(+18000000);//给金币
				
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(3, cm.getClient().getChannel(), "『兑换中心』" + " : " + "[" + cm.getChar().getName() + "]兑换了100个枫叶！")); 
		        cm.dispose();
			}else{
				cm.sendOk("\t点券不足。");
				cm.dispose();
			}
        }else if (selection == 2) { 
			/*if(cm.getPlayer().getBossLogD("金币兑换点券") > 4){
				cm.sendOk("\t今天已经兑换过5次.");
				cm.dispose();
				return;
			}*/
			if(cm.getPlayer().getMeso() >= 18000000 ){ //物品条件
				cm.getPlayer().modifyCSPoints(1,10000, true);//点券
				cm.gainMeso(-18000000);
				cm.getPlayer().setBossLog("金币兑换点券");
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(3, cm.getClient().getChannel(), "『相框兑换』" + " : " + "[" + cm.getChar().getName() + "]努力搬砖兑换了10000点券！")); 
		        cm.dispose();
			}else{
				cm.sendOk("\t金币不足。");
				cm.dispose();
			}
        } else if (selection == 1) { 
			var zliang = cm.getPlayer().getItemQuantity(4001126, false);
			if (zliang == 0) {
                    cm.sendOk("你的物品不足兑换.");
                    status = -1;
                } else {
                    beauty = 1
					cm.sendYesNo("当前共有: #r"+zliang+"#k 个，是否把它们全部兑换吗？");
					}
        } else if (selection == 4) { 
			var zliang = cm.getPlayer().getItemQuantity(4001128, false);
			if (zliang == 0) {
                    cm.sendOk("你的物品不足兑换.");
                    status = -1;
                } else {
                    beauty = 4
					cm.sendYesNo("当前共有: #r"+zliang+"#k 个，是否把它们全部兑换吗？");
					}
        }  else if (status == 2) {
            if (beauty == 1) {
				var zliang = cm.getPlayer().getItemQuantity(4001126, false);
                if (zliang > 0){
					cm.removeAll(4001126);
					cm.gainMeso(8000*zliang);					
							
                    cm.sendOk("兑换成功。共兑换了:[#r"+(zliang)+"#k] 个。");
					cm.worldMessage(6,"[相框兑换]：玩家 "+cm.getName()+" 努力搬砖,在自由相框兑换了："+(zliang*8000)+" 金币。");
					cm.dispose();
                } else {
                    cm.sendOk("您的物品不足，无法兑换。");
                    cm.dispose()
                }            		
            }if (beauty == 4) {
				var zliang = cm.getPlayer().getItemQuantity(4001128, false);
                if (zliang > 0){
					cm.removeAll(4001128);  
					cm.getPlayer().modifyCSPoints(2,zliang*2, true);		
                    cm.sendOk("兑换成功。共兑换了:[#r"+(zliang)+"#k] 个。");
					cm.worldMessage(6,"[兑换中心]：玩家 "+cm.getName()+" 努力搬砖,在自由相框用炸药桶兑换了："+(zliang*2)+" 抵用卷。");//公告
					cm.dispose();
                } else {
                    cm.sendOk("您的物品不足，无法兑换。");
                    cm.dispose()
                }
            }
			if (beauty == 70) {
				var zliang = cm.getPlayer().getItemQuantity(4032226, false);
                if (zliang > 0){
					cm.removeAll(4032226);  
					cm.getPlayer().modifyCSPoints(1,zliang*1000, true);	//给点卷*数量，1为点卷，2为抵用卷	
                    cm.sendOk("兑换成功。共兑换了:[#r"+(zliang)+"#k] 个。");
					cm.worldMessage(6,"[兑换中心]：玩家 "+cm.getName()+" 努力搬砖,在自由相框用黄金猪猪兑换了："+(zliang*1000)+" 点卷。");//公告
					cm.dispose();
                } else {
                    cm.sendOk("您的物品不足，无法兑换。");
                    cm.dispose()
                }
            }
        }
    }
}

var 花草 ="#fEffect/SetEff/208/effect/walk2/4#";
var 花草1 ="#fEffect/SetEff/208/effect/walk2/3#";
var 小花 ="#fMap/MapHelper/weather/birthday/2#";
var 桃花 ="#fMap/MapHelper/weather/rose/4#";
var 金枫叶 ="#fMap/MapHelper/weather/maple/2#";
var 红枫叶 ="#fMap/MapHelper/weather/maple/1#";
var 彩虹 ="#fEffect/ItemEff/1071085/effect/walk1/2#";
var 中条猫 ="#fUI/ChatBalloon/37/n#";
var 猫右 =  "#fUI/ChatBalloon/37/ne#";
var 猫左 =  "#fUI/ChatBalloon/37/nw#";
var 右 =    "#fUI/ChatBalloon/37/e#";
var 左 =    "#fUI/ChatBalloon/37/w#";
var 下条猫 ="#fUI/ChatBalloon/37/s#";
var 猫下右 ="#fUI/ChatBalloon/37/se#";
var 猫下左 ="#fUI/ChatBalloon/37/sw#";
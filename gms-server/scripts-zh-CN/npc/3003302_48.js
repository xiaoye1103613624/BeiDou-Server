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
			text += "                  #k"+皇冠白+" #r#e#w 毕 业 饰 品 #n#k "+皇冠白+"\r\n\r\n";
			text += "  "+猫左+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+猫右+"\r\n";
			text += "  "+皇冠白+" #r#e#w 以 下 为 全 属 性 装 备 请 谨 慎 兑 换 #n#k "+皇冠白+"\r\n";
            //text += "注意:联盟戒指,为固有装备一人只能有一个,多买只扣不给.\r\n";
			text += "\t#L66##k[#v4021009##r#c4021009##k/10]兑换#d#fUI/Basic/BtHide3/mouseOver/0##i1112426:##l#b（全属性+15	攻魔+10）\r\n\r\n"
			text += "\t#L68##k[#v4011007##r#c4011007##k/10]兑换#d#fUI/Basic/BtHide3/mouseOver/0##i1112736:##l#b（全属性+15	攻魔+10）\r\n\r\n"		
			text += "\t#L666##k[#v1132004##r#c1132004##k/5]兑换#d#fUI/Basic/BtHide3/mouseOver/0##i1132115:##l#b（全属性+15	攻魔+10）\r\n\r\n"
			text += "\t#L69##k[#v4310148##r#c4310148##k/10]兑换#d#fUI/Basic/BtHide3/mouseOver/0##i1032019:##l#b（全属性+10	攻魔+5）\r\n\r\n"
			text += "\t#L111##k[#v4001197##r#c4001197##k/80]兑换#d#fUI/Basic/BtHide3/mouseOver/0##i1112907:##l#b（全属性+10	攻魔+5）\r\n\r\n"
			text += "\t#L121##k[#v4251200##r#c4251200##k/15]兑换#d#fUI/Basic/BtHide3/mouseOver/0##i1022168:##l#b（全属性+10	攻魔+5）\r\n\r\n"
			text += "\t#L112##k[#v4001200##r#c4001200##k/10]兑换#d#fUI/Basic/BtHide3/mouseOver/0##i1142146:##l#b（全属性+10	攻魔+5）\r\n\r\n"
			//text += "\t#L1231##k[#v4001239##r#c4001239##k/1]兑换#d#fUI/Basic/BtHide3/mouseOver/0##v1112901##l#b（首冲补领）\r\n\r\n"
            //text += "\t#L1232##k[#v4001239##r#c4001239##k/2]兑换#d#fUI/Basic/BtHide3/mouseOver/0##v1112901##l#b（V1补领）\r\n\r\n"
			//text += "\t#L1233##k[#v4001239##r#c4001239##k/3]兑换#d#fUI/Basic/BtHide3/mouseOver/0##v1112901##l#b（V2补领）\r\n\r\n"
			//text += "\t#L1234##k[#v4001239##r#c4001239##k/4]兑换#d#fUI/Basic/BtHide3/mouseOver/0##v1112901##l#b（V3补领）\r\n\r\n"
			//text += "     #L68##b100枫叶兑换#d#fUI/Basic/BtHide3/mouseOver/0#10HP  (#r比例 100:10#d)#l\r\n\r\n"
			//text += "     #L69##b100枫叶兑换#d#fUI/Basic/BtHide3/mouseOver/0#10MP  (#r比例 100:10#d)#l\r\n\r\n"
            cm.sendSimple(text);
		}else if (selection == 48) {//游戏中心
				cm.dispose();
				cm.openNpc(9310072, 48);
        } 
		else if (selection == 666) {
			if(cm.haveItem(1132004,5) ){
				cm.gainItem(1132004,-5);
				cm.gainItem(1132115,15,15,15,15,100,100,10,10,10,10,5,5,0,0);//黑武功腰带
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(3, cm.getClient().getChannel(), "『兑换中心』" + " : " + "[" + cm.getChar().getName() + "]通过饰品物品，获得了一个武公腰带！")); 
				cm.dispose();
			}else{
				cm.sendOk("\t材料不足。");
				cm.dispose();
			}
		}
		else if (selection == 66) {
			if(cm.haveItem(4021009,10) ){
				cm.gainItem(4021009,-10);
				cm.gainItem(1112426,15,15,15,15,100,100,10,10,10,10,5,5,0,0);//蒲公英戒指
				//cm.gainItem(5150038,1);//超级明星美发卡
				//cm.gainMeso(2000000);//给金币200万
				//cm.gainDY(100000);//给抵用卷10万
				//cm.getPlayer().modifyCSPoints(1,50000, true);//给点卷，1为点卷0为抵用卷
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(3, cm.getClient().getChannel(), "『联盟戒指』" + " : " + "[" + cm.getChar().getName() + "]通过饰品兑换，获得了蒲公英戒指！")); 
				cm.dispose();
			}else{
				cm.sendOk("\t抱歉，您还没有资格兑换联盟戒指哦。请找管理确认后再来领取！");
				cm.dispose();
			}
		}
		else if (selection == 68) {
			if(cm.haveItem(4011007,10) ){
				cm.gainItem(4011007,-10);
				cm.gainItem(1112736,15,15,15,15,100,100,10,10,10,10,5,5,0,0);//英雄戒指
				//cm.gainItem(5150038,1);//超级明星美发卡
				//cm.gainMeso(2000000);//给金币200万
				//cm.gainDY(100000);//给抵用卷10万
				//cm.getPlayer().modifyCSPoints(1,50000, true);//给点卷，1为点卷0为抵用卷
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(3, cm.getClient().getChannel(), "『英雄戒指』" + " : " + "[" + cm.getChar().getName() + "]通过饰品兑换，获得了英雄戒指！")); 
				cm.dispose();
			}else{
				cm.sendOk("\t抱歉，您还没有资格兑换英雄戒指哦。请找管理确认后再来领取！");
				cm.dispose();
			}
		}
				else if (selection == 69) {
			if(cm.haveItem(4310148,10) ){
				cm.gainItem(4310148,-10);
				cm.gainItem(1032019,10,10,10,10,30,30,5,5,5,5,10,10,0,0);//水仙耳环
				//cm.gainItem(5150038,1);//超级明星美发卡
				//cm.gainMeso(2000000);//给金币200万
				//cm.gainDY(100000);//给抵用卷10万
				//cm.getPlayer().modifyCSPoints(1,50000, true);//给点卷，1为点卷0为抵用卷
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(3, cm.getClient().getChannel(), "『水仙耳环』" + " : " + "[" + cm.getChar().getName() + "]通过饰品兑换，获得了水仙耳环！")); 
				cm.dispose();
			}else{
				cm.sendOk("\t抱歉，您还没有资格兑换水仙耳环哦。请找管理确认后再来领取！");
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
				else if (selection == 111) {
			if(cm.haveItem(4001197,80)){
				cm.gainItem(4001197,-80);
				cm.gainItem(1112907,10,10,10,10,20,20,5,5,15,15,100,100,0,0);//小鱼戒指
				//cm.gainItem(5150038,1);//超级明星美发卡
				//cm.gainMeso(2000000);//给金币200万
				//cm.gainDY(100000);//给抵用卷10万
				//cm.getPlayer().modifyCSPoints(1,50000, true);//给点卷，1为点卷0为抵用卷
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(3, cm.getClient().getChannel(), "『小鱼戒指』" + " : " + "[" + cm.getChar().getName() + "]通过饰品兑换，获得了小鱼戒指！")); 
				cm.dispose();
			}else{
				cm.sendOk("\t抱歉，您还没有资格兑换小鱼戒指哦。请找管理确认后再来领取！");
				cm.dispose();
			}
		}
						else if (selection == 112) {
			if(cm.haveItem(4001200,10)){
				cm.gainItem(4001200,-10);
				cm.gainItem(1142146,10,10,10,10,3,3,5,5,8,8,10,10,0,0);//钓鱼王称号
				//cm.gainItem(5150038,1);//超级明星美发卡
				//cm.gainMeso(2000000);//给金币200万
				//cm.gainDY(100000);//给抵用卷10万
				//cm.getPlayer().modifyCSPoints(1,50000, true);//给点卷，1为点卷0为抵用卷
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(3, cm.getClient().getChannel(), "『钓鱼王称号』" + " : " + "[" + cm.getChar().getName() + "]通过饰品兑换，获得了钓鱼王称号！")); 
				cm.dispose();
			}else{
				cm.sendOk("\t抱歉，您还没有资格兑换小鱼戒指哦。请找管理确认后再来领取！");
				cm.dispose();
			}
			}
						else if (selection == 121) {
			if(cm.haveItem(4251200,15)){
				cm.gainItem(4251200,-15)
				cm.gainItem(1022168,10,10,10,10,5,5,5,5,50,50,20,20,0,0);//黑羽眼镜
				//cm.gainItem(5150038,1);//超级明星美发卡
				//cm.gainMeso(2000000);//给金币200万
				//cm.gainDY(100000);//给抵用卷10万
				//cm.getPlayer().modifyCSPoints(1,50000, true);//给点卷，1为点卷0为抵用卷
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(3, cm.getClient().getChannel(), "『黑羽眼镜』" + " : " + "[" + cm.getChar().getName() + "]通过饰品兑换，获得了黑羽眼镜！")); 
				cm.dispose();
			}else{
				cm.sendOk("\t抱歉，您还没有资格兑换黑羽眼镜哦。请找管理确认后再来领取！");
				cm.dispose();
			}
		}
						else if (selection == 1231) {
			if(cm.haveItem(4001239,1)){
				cm.gainItem(4001239,-1);
				cm.gainItem(1112901,40,40,40,40,0,0,20,20,0,0,0,0,0,0);//钓鱼王称号
				//cm.gainItem(5150038,1);//超级明星美发卡
				//cm.gainMeso(2000000);//给金币200万
				//cm.gainDY(100000);//给抵用卷10万
				//cm.getPlayer().modifyCSPoints(1,50000, true);//给点卷，1为点卷0为抵用卷
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(3, cm.getClient().getChannel(), "『兑换中心』" + " : " + "[" + cm.getChar().getName() + "]通过补领，获得了闪电环绕戒指")); 
				cm.dispose();
			}else{
				cm.sendOk("\t抱歉，您还没有资格兑换戒指哦。请找管理确认后再来领取！");
				cm.dispose();
			}	
		}
						else if (selection == 1232) {
			if(cm.haveItem(4001239,2)){
				cm.gainItem(4001239,-2);
				cm.gainItem(1112901,80,80,80,80,0,0,40,40,0,0,0,0,0,0);//钓鱼王称号
				//cm.gainItem(5150038,1);//超级明星美发卡
				//cm.gainMeso(2000000);//给金币200万
				//cm.gainDY(100000);//给抵用卷10万
				//cm.getPlayer().modifyCSPoints(1,50000, true);//给点卷，1为点卷0为抵用卷
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(3, cm.getClient().getChannel(), "『兑换中心』" + " : " + "[" + cm.getChar().getName() + "]通过补领，获得了闪电环绕戒指")); 
				cm.dispose();
			}else{
				cm.sendOk("\t抱歉，您还没有资格兑换戒指哦。请找管理确认后再来领取！");
				cm.dispose();
			}
		}
						else if (selection == 1233) {
			if(cm.haveItem(4001239,3)){
				cm.gainItem(4001239,-3);
				cm.gainItem(1112901,120,120,120,120,0,0,80,80,0,0,0,0,0,0);//钓鱼王称号
				//cm.gainItem(5150038,1);//超级明星美发卡
				//cm.gainMeso(2000000);//给金币200万
				//cm.gainDY(100000);//给抵用卷10万
				//cm.getPlayer().modifyCSPoints(1,50000, true);//给点卷，1为点卷0为抵用卷
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(3, cm.getClient().getChannel(), "『兑换中心』" + " : " + "[" + cm.getChar().getName() + "]通过补领，获得了闪电环绕戒指")); 
				cm.dispose();
			}else{
				cm.sendOk("\t抱歉，您还没有资格兑换戒指哦。请找管理确认后再来领取！");
				cm.dispose();
			}
		}
						else if (selection == 1234) {
			if(cm.haveItem(4001239,4)){
				cm.gainItem(4001239,-4);
				cm.gainItem(1112901,250,250,250,250,0,0,150,150,0,0,0,0,0,0);//钓鱼王称号
				//cm.gainItem(5150038,1);//超级明星美发卡
				//cm.gainMeso(2000000);//给金币200万
				//cm.gainDY(100000);//给抵用卷10万
				//cm.getPlayer().modifyCSPoints(1,50000, true);//给点卷，1为点卷0为抵用卷
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(3, cm.getClient().getChannel(), "『兑换中心』" + " : " + "[" + cm.getChar().getName() + "]通过补领，获得了闪电环绕戒指")); 
				cm.dispose();
			}else{
				cm.sendOk("\t抱歉，您还没有资格兑换戒指哦。请找管理确认后再来领取！");
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
//importPackage(java.lang);
//importPackage(Packages.tools);
//importPackage(Packages.client);
var 美化1 = "#fUI/ChatBalloon.img/pet/123/nw#";//选择道具
var 美化3 = "#fUI/ChatBalloon.img/pet/123/ne#";//选择道具
var 美化2 = "#fUI/ChatBalloon.img/pet/123/n#";//选择道具
var 美化4 = "#fUI/ChatBalloon.img/pet/123/sw#";//选择道具
var 美化5 = "#fUI/ChatBalloon.img/pet/123/se#";//选择道具
var 美化6 = "#fUI/ChatBalloon.img/pet/123/s#";//选择道具
var 美化7 = "#fUI/ChatBalloon.img/156/arrow#";//选择道具
var aaa = "#fUI/UIWindow.img/Quest/icon9/0#";
var zzz = "#fUI/UIWindow.img/Quest/icon8/0#";
var sss = "#fUI/UIWindow.img/QuestIcon/3/0#";
var 正在进行中 = "#fUI/UIWindow/Quest/Tab/enabled/1#";
var 完成 = "#fUI/UIWindow/Quest/Tab/enabled/2#";
var 正在进行中蓝 = "#fUI/UIWindow/MonsterCarnival/icon1#";
var 完成红 = "#fUI/UIWindow/MonsterCarnival/icon0#";
var acc = "#fEffect/CharacterEff/1112903/0/0#";//红桃心
var aaa = "#fUI/UIWindow.img/Quest/icon9/0#";//红色右箭头
var zzz = "#fUI/UIWindow.img/Quest/icon8/0#";//蓝色右箭头
//var status = 0;
function start() {

if (cm.getInventory(1).isFull(3) || cm.getInventory(2).isFull(3) || cm.getInventory(3).isFull(3) || cm.getInventory(4).isFull(3)) {
	cm.sendOk("#b请保证全体背包4个格子,否则无法打开.");
	cm.dispose();
} else{
status = -1;
action(1, 0, 0);
}
}
function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (status >= 0 && mode == 0) {
			cm.dispose();
			return;
		}
		if (mode == 1) {
			status++;
		} else {
			status--;
		}
		if (status == 0) {
			sol = cm.getPlayer().getItemQuantity(3700141, false);
领过没 = 0
    if(cm.getBossLog("超值投资卡")>0){
领过没 = "今已领"
} else {
领过没 = "未领取"
}

			var text = "   "+美化1+""+美化2+""+美化2+""+美化2+""+美化2+"#d『投 资 系 统』#k"+美化2+""+美化2+""+美化2+""+美化2+""+美化3+"#k\r\n";
			
				text+= "         #L1##b每日礼包("+领过没+")#l    #L2##r办理投资卡 #l#k\r\n\r\n";
				text+= "    ↓-----------------<开通立即获得>--------------↓\r\n\r\n";
				
				text+= "#k                立即获得:#i4001126# #r30000个#k \r\n\r\n";

				text+= "\r\n    ↓-----------------<每日领取奖励>--------------↓\r\n";
				
				text+= "#k           每日领取:#r 每日100元宝#k\r\n";
				text+= "#k           每日领取:#r  1个 #k#z5360015#三小时权\r\n";
				text+= "#k           每日领取:#r  1个 #k#z5211047#\r\n";
				
			cm.sendSimple(text);  
		} else if(status == 1){
			if(selection == 1){
			if(sol > 0){
			if(cm.haveItem(3700141, 1) && cm.getBossLog("超值投资卡") == 0){
                //cm.gainItem(3700141,1,1*720);//投资卡凭证
                cm.gainItem(5360015,1,1*3);
                cm.gainItem(5211047,1,1*3);
                cm.setmoneyb(+100);
               // cm.gainItem(5360015,1,1*24);
                cm.喇叭(1,"[每日投资卡奖励] : 恭喜玩家 "+cm.getPlayer().getName()+" 成功领取了每日奖励")
                cm.setBossLog("超值投资卡");
                cm.dispose();
		} else {
                cm.sendOk("您今天已经领取过投资卡每日奖励了哦~!");
                cm.dispose();
					}
		} else {
			var text = "检测到您没有办理#i4300000#投资卡哦~!\r\n";
				text+= "需要消耗#r#i4300000##k#n ~!是否要办理？";
                cm.sendYesNo(text);  
				}
		} else if(selection == 2){
				var text = "测到您没有办理过投资#i4300000#业务、请确认后再来找我~!\r\n";
				cm.sendYesNo(text);  
			}
		} else if(status == 2){
			if (cm.haveItem(4300000, 1) == false) {
				cm.sendOk("办理失败,请联系群管理员 #r乌鸦 737576153 ~");
				cm.dispose();
		} else if (cm.getPlayer().getBossLog("投资卡开通") >=1){
                        cm.sendOk("你好像有月卡了~");
						cm.dispose();				
		} else {
                cm.gainItem(4300000,-1);
				cm.gainItem(3700141,1,1*720);//投资卡凭证
                cm.gainItem(4001126,30000);
				cm.getPlayer().setBossLog("投资卡开通");
                cm.全服漂浮喇叭("〖办理投资卡〗 恭喜 ["+cm.getName()+"]办理投资卡成功,大家快感谢Ta~~", 5121004);
                cm.getPlayer().dropMessage(-1, "智能检测机器人：尊敬的投资卡用户 你好，今日的投资卡奖励没有领取喔~请前往领取");
				cm.sendOk("办理成功,感谢您对开心的支持 投资卡辅助功能在拍卖 投资卡功能专区");
				cm.dispose();
			}
		}
	}
}

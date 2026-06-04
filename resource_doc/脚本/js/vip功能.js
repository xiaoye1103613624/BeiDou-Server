//
var KaixinMSQQ3116519632技能进阶 = "#fUI/ChatBalloon.img/pet/183/nw#";//不可删除删除将会导致NPC不能用,严重可能导致全服掉线
var 美化3 = "#fUI/ChatBalloon.img/pet/183/ne#";//选择道具
var 美化2 = "#fUI/ChatBalloon.img/pet/183/n#";//选择道具
var KaixinMSQQ3116519632技能进阶1 = "#fUI/ChatBalloon.img/pet/183/head#";//不可删除删除将会导致NPC不能用,严重可能导致全服掉线
var 美化4 = "#fUI/ChatBalloon.img/pet/183/sw#";//选择道具
var 美化5 = "#fUI/ChatBalloon.img/pet/183/se#";//选择道具
var 美化6 = "#fUI/ChatBalloon.img/pet/183/s#";//选择道具
var meihua = "#fUI/NameTag.img/pet/203/w#";//选择道具
//var meihua1 = "#fUI/NameTag.img/pet/203/e#";//选择道具
var KaixinMS = "#fUI/NameTag.img/pet/203/e#";//选择道具

//var KaixinMSjinyong = [50000,1102378,1102496,1102377,1102385,1102453,1102487];
var KaixinMSjinyong = [50000,1102378,1102496,1102377,1102385,1102453,1102487];//禁止转移属性物品
//var 美化7 = "#fUI/ChatBalloon.img/156/arrow#";//选择道具
var acc = "#fEffect/CharacterEff/1112903/0/0#";//红桃心
var aaa = "#fUI/UIWindow.img/Quest/icon9/0#";//红色右箭头
var zzz = "#fUI/UIWindow.img/Quest/icon8/0#";//蓝色右箭头
var sss = "#fUI/UIWindow.img/QuestIcon/3/0#";//选择道具
var 蓝色角点 = "#fUI/UIWindow.img/PvP/Scroll/enabled/next2#";
var 蓝色箭头 = "#fUI/UIWindow/Quest/icon2/7#";
var 红色箭头 = "#fUI/UIWindow/Quest/icon6/7#";
var 圆形 = "#fUI/UIWindow/Quest/icon3/6#";
var 美化new = "#fUI/UIWindow/Quest/icon2/7#";
var 美化ne = "#fUI/UIWindow/Quest/icon6/7#";
var 感叹号 = "#fUI/UIWindow/Quest/icon0#";
var 正方箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";
var 中条猫 ="#fUI/ChatBalloon/37/n#";
var 猫右 =  "#fUI/ChatBalloon/37/ne#";
var 猫左 =  "#fUI/ChatBalloon/37/nw#";
var 右 =    "#fUI/ChatBalloon/37/e#";
var 左 =    "#fUI/ChatBalloon/37/w#";
var 下条猫 ="#fUI/ChatBalloon/37/s#";
var 猫下右 ="#fUI/ChatBalloon/37/se#";
var 猫下左 ="#fUI/ChatBalloon/37/sw#";
var 皇冠白 ="#fUI/GuildMark/Mark/Etc/00009004/16#";
var 蓝色角点 = "#fUI/UIWindow.img/PvP/Scroll/enabled/next2#";
var 草莓 = "#fUI/GuildMark/Mark/Plant/00003000/1#"; // 红色草莓
var 草莓1 = "#fUI/GuildMark/Mark/Plant/00003000/10#"; // 淡蓝色草莓
var 草莓2 = "#fUI/GuildMark/Mark/Plant/00003000/11#"; // 紫色草莓
var 草莓3 = "#fUI/GuildMark/Mark/Plant/00003000/15#"; // 白色草莓
var 草莓4 = "#fUI/GuildMark/Mark/Plant/00003000/3#"; // 黄色草莓
var 草莓5 = "#fUI/GuildMark/Mark/Plant/00003000/8#"; // 绿色草莓
var 小烟花 = "#fItem/Etc/0427/04270001/Icon9/0#";  //
var 大黄星 = "#fItem/Etc/0427/04270001/Icon9/1#";  //
var 小水滴 = "#fItem/Etc/0427/04270001/Icon10/5#";  //
var 大水滴 = "#fItem/Etc/0427/04270001/Icon10/4#";  //
var 音符123 ="#fEffect/CharacterEff.img/1112949/3/0#";
var 红爱心 ="#fEffect/CharacterEff.img/1112915/0/0#";
var 小烟花 ="#fMap/MapHelper/weather/squib/squib4/1#";//小烟花
var 粉色爱心 = "#fUI/CashShop/CSEffect/effect/0#";
var 音符1234 ="#fEffect/CharacterEff.img/1112949/1/0#";
var 礼包物品 = "#v1302000#";
var x1 = "1302000,+1";// 物品ID,数量
var x2;
var x3;
var x4;
var 爱心 = "#fEffect/CharacterEff/1022223/4/0#";
var 礼包物品 = "#v1302000#";
var add = "#fEffect/CharacterEff/1112903/0/0#";//红桃心
var aaa = "#fUI/UIWindow.img/Quest/icon9/0#";//红色右箭头
var zzz = "#fUI/UIWindow.img/Quest/icon8/0#";//蓝色右箭头
var sss = "#fUI/UIWindow.img/QuestIcon/3/0#";//选择道具
var 正方箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";
var 感叹号 = "#fUI/UIWindow/Quest/icon0#";
var 美化new = "#fUI/UIWindow/Quest/icon5/1#";
var 红色箭头 = "#fEffect/CharacterEff/1112908/0/1#";  //彩光3
var ttt1 = "#fEffect/CharacterEff/1062114/1/0#";  //爱心
var 蓝色角点 = "#fUI/UIWindow.img/PvP/Scroll/enabled/next2#";
function start() {
        status = -1;
        action(1, 0, 0);
}
function action(mode, type, selection) {if (mode == -1) { cm.dispose();} else {if (status >= 0 && mode == 0) {//cm.sendOk("感谢你的光临！");
            cm.dispose();return; }if (mode == 1) { status++;} else {status--;}if (status == 0) { 
			var text = "";
            for (i = 0; i < 10; i++) {text += "";}                                                                                                           
	     	text += ""+KaixinMSQQ3116519632技能进阶+""+美化2+""+美化2+""+美化2+""+美化2+""+美化2+""+美化2+""+美化2+""+KaixinMSQQ3116519632技能进阶1+""+美化2+""+美化2+""+美化2+""+美化2+""+美化2+""+美化2+""+美化2+""+美化3+"\r\n\r\n"
			text += "开启活动后不能同时开启其他活动得结束后才能开启其他活动\r\n";
			text += "方法1：点击以下任意活动开始\r\n";
			text += "方法2：让所有玩家点击 《江》NPC 进入 \r\n";
			text += "方法3：进入后人数感觉达标了以后点最底下的活动开始按钮 \r\n";

			text += "#d#L3#Ola Ola#k#l\r\n";
			text += "#d#L1#打椰子比赛#k#l\r\n";
			text += "#d#L4#打瓶盖比赛#k#l\r\n";
			//text += "#d#L5#寻宝#k#l\r\n";
			
			text += "#L2##r#e----------------[活动开始GO]----------------#k#l\r\n";
			cm.sendSimpleS(text,2);
	} else if (selection == 1) {
        cm.processCommand("!选择活动 打椰子");
		cm.worldMessage(12,"[打椰子活动]" + " : " + " 开启中想参加的小伙伴们前往自由市场右边NPC 江 进入");
		cm.worldMessage(6,"[打椰子活动]" + " : " + " 开启中想参加的小伙伴们前往自由市场右边NPC 江 进入");
		cm.worldMessage(12,"[打椰子活动]" + " : " + " 开启中想参加的小伙伴们前往自由市场右边NPC 江 进入");
		cm.worldMessage(6,"[打椰子活动]" + " : " + " 开启中想参加的小伙伴们前往自由市场右边NPC 江 进入");
	    cm.worldMessage(12,"[打椰子活动]" + " : " + " 开启中想参加的小伙伴们前往自由市场右边NPC 江 进入");
		cm.worldMessage(6,"[打椰子活动]" + " : " + " 开启中想参加的小伙伴们前往自由市场右边NPC 江 进入");
		cm.worldMessage(12,"[打椰子活动]" + " : " + " 开启中想参加的小伙伴们前往自由市场右边NPC 江 进入");
		cm.worldMessage(6,"[打椰子活动]" + " : " + " 开启中想参加的小伙伴们前往自由市场右边NPC 江 进入");
		cm.dispose();
	} else if (selection == 2) {
        cm.processCommand("!活动开始");
		cm.dispose();
	} else if (selection == 3) {
        cm.processCommand("!选择活动 上楼上楼");
		cm.worldMessage(12,"[上楼上楼]" + " : " + " 开启中想参加的小伙伴们前往自由市场右边NPC 江 进入");
		cm.worldMessage(6,"[上楼上楼]" + " : " + " 开启中想参加的小伙伴们前往自由市场右边NPC 江 进入");
		cm.worldMessage(12,"[上楼上楼]" + " : " + " 开启中想参加的小伙伴们前往自由市场右边NPC 江 进入");
		cm.worldMessage(6,"[上楼上楼]" + " : " + " 开启中想参加的小伙伴们前往自由市场右边NPC 江 进入");
	    cm.worldMessage(12,"[上楼上楼]" + " : " + " 开启中想参加的小伙伴们前往自由市场右边NPC 江 进入");
		cm.worldMessage(6,"[上楼上楼]" + " : " + " 开启中想参加的小伙伴们前往自由市场右边NPC 江 进入");
		cm.worldMessage(12,"[上楼上楼]" + " : " + " 开启中想参加的小伙伴们前往自由市场右边NPC 江 进入");
		cm.worldMessage(6,"[上楼上楼]" + " : " + " 开启中想参加的小伙伴们前往自由市场右边NPC 江 进入");
		cm.dispose();
	} else if (selection == 4) {
        cm.processCommand("!选择活动 打瓶盖");
		cm.worldMessage(12,"[打瓶盖]" + " : " + " 开启中想参加的小伙伴们前往自由市场右边NPC 江 进入");
		cm.worldMessage(6,"[打瓶盖]" + " : " + " 开启中想参加的小伙伴们前往自由市场右边NPC 江 进入");
		cm.worldMessage(12,"[打瓶盖]" + " : " + " 开启中想参加的小伙伴们前往自由市场右边NPC 江 进入");
		cm.worldMessage(6,"[打瓶盖]" + " : " + " 开启中想参加的小伙伴们前往自由市场右边NPC 江 进入");
	    cm.worldMessage(12,"[打瓶盖]" + " : " + " 开启中想参加的小伙伴们前往自由市场右边NPC 江 进入");
		cm.worldMessage(6,"[打瓶盖]" + " : " + " 开启中想参加的小伙伴们前往自由市场右边NPC 江 进入");
		cm.worldMessage(12,"[打瓶盖]" + " : " + " 开启中想参加的小伙伴们前往自由市场右边NPC 江 进入");
		cm.worldMessage(6,"[打瓶盖]" + " : " + " 开启中想参加的小伙伴们前往自由市场右边NPC 江 进入");
		cm.dispose();

	} else if (selection == 5) {
        cm.processCommand("!选择活动 寻宝");
		cm.worldMessage(12,"[寻宝]" + " : " + " 开启中想参加的小伙伴们前往自由市场右边NPC 江 进入");
		cm.worldMessage(6,"[寻宝]" + " : " + " 开启中想参加的小伙伴们前往自由市场右边NPC 江 进入");
		cm.worldMessage(12,"[寻宝]" + " : " + " 开启中想参加的小伙伴们前往自由市场右边NPC 江 进入");
		cm.worldMessage(6,"[寻宝]" + " : " + " 开启中想参加的小伙伴们前往自由市场右边NPC 江 进入");
	    cm.worldMessage(12,"[寻宝]" + " : " + " 开启中想参加的小伙伴们前往自由市场右边NPC 江 进入");
		cm.worldMessage(6,"[寻宝]" + " : " + " 开启中想参加的小伙伴们前往自由市场右边NPC 江 进入");
		cm.worldMessage(12,"[寻宝]" + " : " + " 开启中想参加的小伙伴们前往自由市场右边NPC 江 进入");
		cm.worldMessage(6,"[寻宝]" + " : " + " 开启中想参加的小伙伴们前往自由市场右边NPC 江 进入");
		cm.dispose();




///////////////////////////////////////////////////////////////////////////////////////////////////////////

    }
  }
}
//全服漂浮喇叭

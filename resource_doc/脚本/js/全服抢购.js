var 蓝色箭头 = "#fUI/UIWindow/Quest/icon2/7#";
var a = "#fUI/UIWindow.img/PvP/Scroll/enabled/next2# ";
var 圆形 = "#fUI/UIWindow/Quest/icon3/6#";
var 美化new = "#fUI/UIWindow/Quest/icon5/1#";
var 感叹号 = "#fUI/UIWindow/Quest/icon0#";
var 感叹号2 = "#fUI/UIWindow/Quest/icon1#";
var 红色箭头 = "#fUI/UIWindow.img/PvP/Scroll/enabled/next2# ";
var 花花1 = "#fUI/GuildMark/Mark/Pattern/00004020/1#";
var 花花2 = "#fUI/GuildMark/Mark/Pattern/00004020/3#";
var 花花3 = "#fUI/GuildMark/Mark/Pattern/00004020/5#";
var 花花4 = "#fUI/GuildMark/Mark/Pattern/00004020/7#";
var 花花5 = "#fUI/GuildMark/Mark/Pattern/00004020/9#";
var 花花6 = "#fUI/GuildMark/Mark/Pattern/00004020/11#";
var 花花7 = "#fUI/GuildMark/Mark/Pattern/00004020/13#";
var 花花8 = "#fUI/GuildMark/Mark/Pattern/00004020/14#";
var 花花9 = "#fUI/GuildMark/Mark/Pattern/00004020/15#";
var 圆点 = "#fEffect/CharacterEff/1112908/0/1#";  //彩光3
var 小粉心 = "#fEffect/CharacterEff/1062114/1/0#";  //蓝心
var 蓝色角点 = "#fUI/UIWindow.img/PvP/Scroll/enabled/next2#";
var 表情 = "#fUI/GuildBBS.img/GuildBBS/Emoticon/Basic/0#"; 
var 表情1 = "#fUI/GuildBBS.img/GuildBBS/Emoticon/Basic/1#"; 
var 表情2 = "#fUI/GuildBBS.img/GuildBBS/Emoticon/Basic/2#"; 
var 蓝色小兔子 = "#fEffect/CharacterEff.img/1112960/3/1#"; 
var 小红星 = "#fEffect/CharacterEff.img/1112926/0/0#";
var 小蓝星 = "#fEffect/CharacterEff.img/1112925/0/0#";
var 蓝色时钟 = "#fUI/UIWindow.img/Quest/TimeQuest/AlarmClock/default/0/0#";  
var 红色时钟 = "#fUI/UIWindow.img/Quest/TimeQuest/AlarmClock/default/4/0#";  
var 成功了 = "#fEffect/BasicEff.img/Fishing/6#";  
var 经验值 = "#fUI/UIWindow.img/QuestIcon/8/0#";  
var 神秘人 = "#fUI/UIWindow.img/MinigameTable/default#";
var 小黄星 = "#fItem/Etc/0427/04270001/Icon9/0#";  //小黄星
var 铅笔图标 = "#fUI/UIWindow.img/PvP/btWrite/mouseOver/0#";
var 警报灯 = "#fUI/StatusBar/BtClaim/normal/0#";
var H字母 = "#fUI/CashShop/CSEffect/effect/1#";
var 金币图标 = "#fUI/UIWindow.img/Item/BtCoin/normal/0#";
var 黄人 = "#fEffect/ItemEff/1102616/effect/jump/2#";
var 红人 = "#fEffect/ItemEff/1102617/effect/alert/2#";
var 点击 = "#fUI/Basic.img/dcMark/0#"; 
var 购物中心 = "#fEffect/CharacterEff1.img/QQ1408745/0/4#";

/* 
 * Spiegelmann - Monster Carnival
 */

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0 && status == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
            商品代码 = 4310100;        //5830001       4000487暗影币
            商品还有没 = cm.getPlayer().getxmqjbl(商品代码, 0); 
            价格是多少 = 238;
			物品数量 = 198;
            冠名 = "第三期超值大礼包";
			
			text = "#w\t\t\t"+购物中心+"\r\n\r\n";
            //text = "#bHi~[" + cm.getName() + "]这里是【欢乐福袋】中心\r\n\r\n";
			text += "#d本次活动为：#v3993001##b【 " + 冠名 + " 】#v3993001##k \r\n\r\n";
        //    text += "#d本期的宝物：#r#i" + 商品代码 + "# #z" + 商品代码 + "# * " + 物品数量 + "#k \r\n\r\n";
			text += "#d本期的宝物：#r#i" + 商品代码 + "# 一万积分 * " + 物品数量 + "#k \r\n\r\n";
			
            text += ""+红色时钟+" #d剩余数量：#r#e" + 商品还有没 + " #d#n份 "+警报灯+"\r\n\r\n";
            text += ""+红色时钟+" #d物品单价：#r#e" + 价格是多少 + "#d#n 赞助#d "+警报灯+"  当前余额: #b#e" + cm.getPlayer().getmoney() + " #n#k\r\n\r\n";
        //    text += ""+蓝色时钟+" #d购买须知：每期只能购买#r#e1#n#d次，购买后赠送#r#e" + 价格是多少 + "#d#n 累计\r\n\r\n";
			text += ""+蓝色时钟+" #d购买须知：每期只能购买#r#e1#n#d次\r\n\r\n";
			text += ""+蓝色时钟+" #d额外赠送：购买后赠送 #r#e" + 价格是多少 + "#d#n 累计.\r\n\r\n";
		//	text += ""+蓝色时钟+" #d活动时间：即日起至#r本周日24：00止\r\n\r\n\r\n";

            if (商品还有没 <= 0) {    
                text += "\t\t\t  #r本期商品已经抢劫一空了#b \r\n";        
            } else {
				 text += "\t\t\t  #L1##i5830001##e#r点击抢购#i5830001##l#n\r\n\r\n";
			}
        //    if (cm.getPlayer().isAdmin()) { // 检查是否是管理员     三个判断GM 哪个方便用哪个
		//	if(cm.getChar().isGM()){
			if (cm.getPlayer().getGMLevel() > 99) {
				text += "\r\n#r--------------------GM选项--------------------#k\r\n";
                text += "#L2#管理员设置商品数量#l \r\n\r\n";
            }

            cm.sendSimpleS(text, 2);
        } else if (status == 1) {
            if (selection == 1) {
                if (商品还有没 <= 0) { 
                    cm.sendOkS("本期商品已经抢劫一空了，无法购买！", 2);
                    cm.dispose();
                } else if (cm.getPlayer().getmoney() < 价格是多少) {
                    cm.sendOkS("所需的赞助点.不足!无法购买", 2);
                    cm.dispose();
                } else if (cm.getPlayer().getOneTimeLog(冠名) >= 1) { 
                    cm.sendOkS("本期宝物你已经抢过一次了，等待下期活动吧！.", 2);
                    cm.dispose();
                } else if (cm.getInventory(1).isFull(2) || cm.getInventory(2).isFull(2) || cm.getInventory(3).isFull(2) || cm.getInventory(4).isFull(2) || cm.getInventory(5).isFull(2)) {
					cm.sendOk("请保证背包所有栏位至少保留3个空格！");
					cm.dispose();
					return;
                } else {
                    cm.getPlayer().setxmqjbl(商品代码, 商品还有没 - 1);
                //    cm.getPlayer().setOneTimeLog("购买商品即时抢购");
                    cm.getPlayer().setOneTimeLog(冠名);
                    cm.getPlayer().setmoney(cm.getPlayer().getmoney() - 价格是多少);  //-赞助
					cm.getPlayer().setlpjf(cm.getPlayer().getlpjf() + 价格是多少); //+累计积分
					cm.getPlayer().dropMessage(5, "赞助点：-" +价格是多少+ "");   //红字私聊提示
					cm.getPlayer().dropMessage(5, "累计积分：+" +价格是多少+ "");   //红字私聊提示
                    cm.gainItem(商品代码, 物品数量);
				//	cm.gainItem(4000487, 价格是多少); //------------------------------------------额外赠送暗影币
                    cm.sendOkS("抢购成功 获得#i" + 商品代码 + "# *" + 物品数量 + " 福袋 :[ " + 冠名 + " ] ", 2);
					cm.全服漂浮喇叭( "【全服抢购】恭喜土豪玩家 "+cm.getPlayer().getName()+" 在全服抢购活动中，抢到了" + 冠名 + "！" , 5120015 );
					cm.喇叭(2, "恭喜土豪玩家[" + cm.getName() + "]:在全服抢购活动中，抢到了" + 冠名 + "！");
					Packages.tools.FileoutputUtil.log("log\\玩家相关\\全服抢购.log", "[" + cm.getName() + "]花费 " +价格是多少+ " 赞助，抢购了 " + 冠名 + " ");  //  记录日志
                    cm.dispose();
                }
            } else if (selection == 2 && cm.getPlayer().isAdmin()) { // 管理员设置商品数量
                cm.sendGetNumber("请输入新的商品数量：", 商品还有没, 0, 999999); // 设置数量范围
            }
        } else if (status == 2) { // 管理员设置商品数量后的处理
            if (cm.getPlayer().isAdmin()) {
                var 新的数量 = selection;
            //    cm.getPlayer().setxmqjbl(商品代码, 新的数量);
				cm.getPlayer().setxmqjbl(商品代码, 新的数量, 0); // 0 表示写入永久值
                cm.sendOk("商品数量已更新为：" + 新的数量);
                cm.dispose();
            }
        }
    }
}
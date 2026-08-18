var 星星 = "#fUI/CN_Chat.img/roomList/Vip#";
var 奖励 = "#fUI/CashShop/CSDiscount/bonus#";
function start() {
    status = -1;

    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (status >= 0 && mode == 0) {

            cm.sendOk("感谢你的光临！");
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        } else {
            status--;
        }
        if (status == 0) {
			var tex2 = "";
            var text = "";
            for (i = 0; i < 10; i++) {
                text += "";
            }
            text += "#b会员VIP2需要以下道具物品#l:\r\n\r\n" 
			text += "#kVIP1称号#v1142174##z1142174# 数量 +1\r\n\r\n"  
			text += "#r"+奖励+"\r\n" 
			text += "1.会员VIP2#v1142176##z1142176# 数量 +1\r\n" 
			text += "2.赞助点卷:数量 +150000\r\n" 
			text += "3.游戏金币:数量 +5千万\r\n"  
			text += "4.祝福卷轴:数量 15张\r\n" 
			text += "5.强化混沌卷轴:数量 15张\r\n" 
			text += "6.材料:#v2614000#数量 +5\r\n" 
			text += "#v4000424# 数量 +1#v1142176# 数量 +1\r\n\r\n" 
                        text += ""+奖励+"\r\n\r\n" 
                        text += "#v1142176# 全属性:100  攻魔:+50\r\n"
						text += "#v1112901# 全属性:120  攻魔:+80\r\n"
                        text += "#L1##r#e#v4000424#兑换会员VIP2#l\r\n\r\n"
            cm.sendOk(text); 
        } else if (selection == 1) {
			if (cm.getInventory(4).isFull(0)){//判断第四个也就是其它栏的装备栏是否有一个空格
		    cm.sendOk("#b请保证其它栏位至少有1个空格,否则无法兑换.");
		    cm.dispose();
            } else if (!cm.haveItem(4000424, 1)) {//判断物品
		    cm.sendOk("请你尽快收集道具物品,赞助中心在线充值哦!#v4000424#");
		    cm.dispose();
			} else if (!cm.haveItem(1142174, 1)) {//判断物品
		    cm.sendOk("请你把会员VIP1称号放在背包里哦！#v1142174#");
		    cm.dispose();
			} else if (!cm.haveItem(1112901, 1)) {//判断物品
		    cm.sendOk("请你把闪电环绕戒指放在背包里哦！#v1112901#");
		    cm.dispose();
			} else {
			cm.gainItem(4000424, -1);//扣除物品
			cm.gainItem(1112901, -1);//扣除物品
			cm.gainItem(1142174, -1);//扣物品
			cm.gainItem(2340000, +15);//祝福
			cm.gainItem(2049116, +15);//混沌
			cm.gainItem(2614000, +5);//破功石
			cm.gainItem(4001126, +10000);//扣物品	
			cm.gainNX(+150000);//扣点卷10000点
			cm.gainMeso(+50000000);//扣金币1
                        cm.给属性装备(1142176, 1, 0, 100, 100, 100, 100, 100, 100, 50, 50,100, 100, 0, 0, 0, 0, 0);
						cm.给属性装备(1112901, 1, 0, 120, 120, 120, 120, 0, 0, 80, 80,0, 0, 0, 0, 0, 0, 0);
			cm.sendOk("#b恭喜你成功领取了会员VIP2:全属性+100！#v1142176##z1142176#");
			cm.worldMessage("[赞助公告]：玩家【"+cm.getName()+"】领取会员VIP2感谢你对本服的大力支持！");//公告
		    cm.dispose();
			}
        }
    }
}
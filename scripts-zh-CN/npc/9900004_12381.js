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
            text += "#b首次赞助需要以下道具物品#l:\r\n\r\n" 
			text += ""+奖励+"\r\n" 
			text += "1.首次赞助#v1142609##z1142609# 数量 +1\r\n" 
			text += "2.赞助点卷:数量 +50000\r\n" 
			text += "3.游戏金币:数量 +1千万\r\n" 
			text += "4.祝福卷轴:数量 1张\r\n" 
			text += "5.混沌卷轴:数量 1张\r\n" 
			text += "6.材料:#v4001126##z4001126# 数量 +1000\r\n" 
			text += "#v4000423# 数量 +1\r\n\r\n" 
                        text += ""+奖励+"\r\n\r\n" 
                        text += "#v1142081# 全属性:+8\r\n"
                        text += "#L1##r#e#v4000423#兑换首次赞助#l\r\n\r\n"
            cm.sendOk(text); 
        } else if (selection == 1) {
			if (cm.getInventory(4).isFull(0)){//判断第四个也就是其它栏的装备栏是否有一个空格
		    cm.sendOk("#b请保证其它栏位至少有1个空格,否则无法兑换.");
		    cm.dispose();
            } else if (!cm.haveItem(4000422, 1)) {//判断物品
		    cm.sendOk("请你尽快收集道具物品,赞助中心在线充值哦!#v4000423##z4000423#.");
		    cm.dispose();
			} else {
			cm.gainItem(4000422, -1);//扣除物品
			//cm.gainItem(1142081, -1);//扣物品
			cm.gainItem(2340000, +1);//祝福
			cm.gainItem(2049117, +1);//混沌
			cm.gainItem(4001126, +1000);//扣物品
			cm.gainNX(+50000);//扣点卷100000点
			cm.gainMeso(+10000000);//扣金币10000000
                        cm.给属性装备(1142081, 1, 0, 8, 8, 8, 8, 30, 30, 3, 3,30, 30, 0, 0, 0, 0, 0);
			cm.sendOk("#b恭喜你成功领取了首次赞助:全属性+8！#v1142609##z1142609#");
			cm.worldMessage(6,"[会员公告]：玩家【"+cm.getName()+"】领取首次赞助感谢你对本服的大力支持！");//公告
		    cm.dispose();
			}
        }
    }
}
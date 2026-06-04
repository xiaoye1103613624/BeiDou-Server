var chance4 = Math.floor(Math.random() * 1000 + 1000);//点卷随机变量 *20为每次最小数字20 +10是浮动数字 最大为30 可以自行设定大小
var chance5 = Math.floor(Math.random() * 10 + 5);//抵用卷随机变量
var chance6 = Math.floor(Math.random() * 100000 + 200000);//金币随机变量
var chance9 = Math.floor(Math.random() * 1 + 1);//元宝币
function start() {
    status = -1;
    action(1, 0, 0);
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
				var 鱼儿 = cm.itemQuantity(3994748);
				var 鱼儿1 = cm.itemQuantity(3994749);
				var 鱼儿2 = cm.itemQuantity(3994747);
				var 鱼儿3 = cm.itemQuantity(3994751);
				var 总和 = 鱼儿+鱼儿1+鱼儿2+鱼儿3;
			 //var text = "       #e#r#v3011000# 开心 - 钓鱼管理员 #v3011000#\r\n#k#n\r\n";	
			var text = "      #k#b当前钓鱼积分：#r" + cm.getBossRank1("钓鱼积分1",1) + "#k 今日钓鱼成功次数：#r" + cm.getBossLog("钓鱼积分") + "#n#k\r\n";//钓鱼成功次数
			text += "  钓鱼需要鱼饵,任何椅子都可以在本地图实现钓鱼哦~\r\n";//3
			text += "  鱼饵获得方式: 下面领取鱼饵500个\r\n";//,整点福利每天送800个
			//text += "     #d#L1#购买 #z5340001# #v5340001# X1需要：点卷x3000#l\r\n";//3
			text += "     #b#L100#每日领取 #z2300001# #v2300001# X 500 每日限领一次#l\r\n";//3
		    text += "\r\n      #d#L10##v3090006#      钓鱼拼图合成      #v3090006##l#k\r\n";//3
			text += "\r\n  #r#L5#每30秒几率获得小鱼儿,3000钓鱼积分兑换#v4000038##z4000038# X1 #l\r\n";
			text += "\r\n #k#L4#  〖回收所有鱼,你目前可回收的鱼总共有 #r<"+总和+">#k#n条〗 #l\r\n";//3
			cm.sendSimple(text);
		} else if (status == 1) {
			if (selection == 0) {
				beauty = 0;
                var txt = "请输入所需兑换数值,自行查看背包是否有空位,单笔兑换最高3w\r\n\r\n"
                cm.sendGetNumber(txt, cm.getPlayer().getCSPoints(1) / 3000, 1, cm.getPlayer().getCSPoints(1) / 3000);
			} else if (selection == 10) {

		 if (cm.getPlayer().getClient().getChannel() !=1) {
					cm.sendOk("只有在1线可以召唤");
					cm.dispose();
        } else if (cm.haveItem(3994807, 1) == false) {
                    cm.sendOk("你可能没有拼出一整张拼图哦");
                    cm.dispose();
		} else if (cm.haveItem(3994808, 1) == false) {
                    cm.sendOk("你可能没有拼出一整张拼图哦");
                    cm.dispose();
		} else if (cm.haveItem(3994809, 1) == false) {
                    cm.sendOk("你可能没有拼出一整张拼图哦");
                    cm.dispose();
		} else if (cm.haveItem(3994810, 1) == false) {
                    cm.sendOk("你可能没有拼出一整张拼图哦");
                    cm.dispose();	
		} else if (cm.haveItem(3994811, 1) == false) {
                    cm.sendOk("你可能没有拼出一整张拼图哦");
                    cm.dispose();
		} else if (cm.haveItem(3994812, 1) == false) {
                    cm.sendOk("你可能没有拼出一整张拼图哦");
                    cm.dispose();
		} else if (cm.haveItem(3994813, 1) == false) {
                    cm.sendOk("你可能没有拼出一整张拼图哦");
                    cm.dispose();
		} else if (cm.haveItem(3994814, 1) == false) {
                    cm.sendOk("你可能没有拼出一整张拼图哦");
                    cm.dispose();
		} else if (cm.haveItem(3994815, 1) == false) {
                    cm.sendOk("你可能没有拼出一整张拼图哦");
                    cm.dispose();
		} else if (cm.haveItem(3994816, 1) == false) {
                    cm.sendOk("你可能没有拼出一整张拼图哦");
                    cm.dispose();
		} else if (cm.haveItem(3994817, 1) == false) {
                    cm.sendOk("你可能没有拼出一整张拼图哦");
                    cm.dispose();
		} else if (cm.haveItem(3994818, 1) == false) {
                    cm.sendOk("你可能没有拼出一整张拼图哦");
                    cm.dispose();
        } else if (cm.getInventory(4).isFull()) {
                    cm.sendOk("请保证 #b其他栏#k 至少有2个位置。");
                    cm.dispose();
        } else if (cm.getMonsterCount(993185100)>=1){
                    cm.sendOk("当前boss正在挑战中，不可进行召唤，如需进入挑战请从拍卖进入");
                    cm.dispose();
		} else {
                    cm.gainItem(3994807,-1);
                    cm.gainItem(3994808,-1);
                    cm.gainItem(3994809,-1);
                    cm.gainItem(3994810,-1);
                    cm.gainItem(3994811,-1);
                    cm.gainItem(3994812,-1);
                    cm.gainItem(3994813,-1);
                    cm.gainItem(3994814,-1);
                    cm.gainItem(3994815,-1);
                    cm.gainItem(3994816,-1);
                    cm.gainItem(3994817,-1);
                    cm.gainItem(3994818,-1);
                    cm.warp(993185100,0);//9700046
                    //cm.spawnMobOnMap(9500319,1,200000,66666666,-362,225);
					cm.召唤怪物(9500319, 666666, 10000000, 1, 993185100, -362, 225); 
				    //cm.spawnMobOnMap(9500319, 1, -362, 225, 993185100,6666666);
	//               代码，数量，X坐标，Y坐标，地图，  血量
                    cm.gainNX(chance4);
					cm.gainMeso(chance6);//给与玩家随机变量游戏币
                    cm.gainItem(2022582,20);//洗血箱子
                    //cm.gainBeans(+20000);//活力值
                    cm.setmoneyb(+ chance9);//元宝
                    cm.全服漂浮喇叭("恭喜["+cm.getName()+"] 成功将钓鱼获得的尖兵拼图合成并领取奖励,召唤出全服福利雪人,1X 点击拍卖 => 即时活动 => 钓鱼拼图 即可显示传送", 5121011);
                    cm.喇叭(2,"钓鱼拼图：["+cm.getName()+"]成功将钓鱼获得的尖兵拼图合成并领取了保底奖励,同时并召唤出全服福利雪人波斯,各种好礼爆不停！");
                    cm.喇叭(2,"钓鱼拼图：["+cm.getName()+"]成功将钓鱼获得的尖兵拼图合成并领取了保底奖励,同时并召唤出全服福利雪人波斯,各种好礼爆不停！");
                    cm.喇叭(2,"钓鱼拼图：["+cm.getName()+"]1X 点击拍卖 => 即时活动 => 钓鱼拼图 即可显示传送 已为冒险家们开启直达传送门");
                    cm.喇叭(2,"钓鱼拼图：["+cm.getName()+"]1X 点击拍卖 => 即时活动 => 钓鱼拼图 即可显示传送 已为冒险家们开启直达传送门");
                    cm.喇叭(2,"钓鱼拼图：["+cm.getName()+"]1X 点击拍卖 => 即时活动 => 钓鱼拼图 即可显示传送 已为冒险家们开启直达传送门");
					//cm.warp()
                    cm.sendNext("领取成功保底奖励：\r\n 随机获取元宝、点券、游戏币、20个洗血箱子 \r\n并自动为你召集队友");//
					cm.dispose();
				}
			} else if (selection == 100) {
				if (cm.canHold(2300001,500) == false) {
                cm.sendOk("您的消耗栏背包空间不足，请整理后再兑换。");
                cm.dispose();
			} else if (cm.getPlayer().getBossLog("每日鱼饵") >= 1) {
					cm.sendOk("今天每日鱼饵已领过请明天再来");
					cm.dispose();
				} else {
					//cm.gainNX(-5000);
                    cm.playerMessage(1, "[休闲钓鱼功能]:领取成功:500 钓鱼-鱼饵");
					cm.getPlayer().setBossLog("每日鱼饵");
					cm.gainItem(2300001,500);
					cm.dispose();
				}
			} else if (selection == 1) {
				if(cm.getPlayer().getCSPoints(1) < 2999){
					cm.sendOk("点卷不足3000无法购买");
					cm.dispose();
			} else if (cm.canHold(5340001,1) == false) {
					cm.sendOk("您的消耗栏背包空间不足，请整理后再兑换。");
					cm.dispose();
				} else {
					cm.gainNX(-3000);
					cm.gainItem(5340001,1);
					cm.dispose();
				}

			} else if (selection == 3) {
				if(cm.getPlayer().getCSPoints(1) < 5000){
					cm.sendOk("点卷不足无法购买");
					cm.dispose();
				} else {
					cm.gainNX(-5000);
					cm.gainItem(5340001,1);
					cm.dispose();
				}
			} else if (selection == 5) {
				if(cm.getBossRank1("钓鱼积分1",1) < 3000){
					cm.sendOk("#d钓鱼积分少于X 3000 无法兑换");
					cm.dispose();
				} else {
                    cm.setBossRank1("钓鱼积分1",1,-3000);
                    cm.喇叭(2,"恭喜玩家["+cm.getName()+"]成功使用3000钓鱼积分 获得1个金杯！");
 					cm.gainItem(4000038,1);
 					//cm.喇叭(2, "【破功突破】 - 恭喜玩家 "+cm.getPlayer().getName()+" 破功至 "+(cm.getPlayer().getDamage()*10000+199999)+"");
					//cm.getPlayer().setLimitBreak(cm.getPlayer().getDamage() + 10000);
					cm.sendOk("#r使用3000钓鱼积分 获得1个金杯！");
					cm.dispose();
				}

			} else if (selection == 4) {
				var 鱼儿 = cm.itemQuantity(3994748);
				var 鱼儿1 = cm.itemQuantity(3994747);
				var 鱼儿2 = cm.itemQuantity(3994749);
				var 鱼儿3 = cm.itemQuantity(3994751);
				//var 总和 = 鱼儿+鱼儿1+鱼儿2;
				if(鱼儿 <= 0&&鱼儿1 <= 0&&鱼儿2 <= 0&&鱼儿3 <= 0){
					cm.sendOk("道具不足无法兑换钓鱼积分，可回收的鱼:\r\n#i3994748# 价值(1点钓鱼积分)\r\n#i3994749# 价值(2点钓鱼积分)\r\n#i3994747# 价值(3点钓鱼积分)\r\n#i3994751# 价值(5点钓鱼积分)");
					cm.dispose();
				} else {
					cm.removeAll(3994748);
					cm.removeAll(3994749);
					cm.removeAll(3994747);
					cm.removeAll(3994751);
                    cm.个人存档();
                    cm.playerMessage(1, "[休闲钓鱼]:回收小鱼积分明细\r\n\r\n银鲤鱼回收获:"+鱼儿+"\r\n\r\n红鲤鱼回收获:"+鱼儿1*2+"\r\n\r\n金鲤鱼回收获:"+鱼儿2*3+"\r\n\r\n金枪鱼回收获:"+鱼儿3*5+"");
				    //cm.获得破功();
				    cm.setBossRank1("钓鱼积分1",1,+鱼儿*1);
				    cm.setBossRank1("钓鱼积分1",1,+鱼儿1*2);
				    cm.setBossRank1("钓鱼积分1",1,+鱼儿2*3);
				    cm.setBossRank1("钓鱼积分1",1,+鱼儿3*5);





 //cm.getPlayer().setqiandao(cm.getPlayer().getqiandao()+鱼儿*1);
 //cm.getPlayer().setqiandao(cm.getPlayer().getqiandao()+鱼儿1*2);
 //cm.getPlayer().setqiandao(cm.getPlayer().getqiandao()+鱼儿2*3);
 //cm.getPlayer().setqiandao(cm.getPlayer().getqiandao()+鱼儿3*5);
				    //cm.添加破功(+鱼儿*1);
				    //cm.添加破功(+鱼儿1*2);
				    //cm.添加破功(+鱼儿2*3);
				   // cm.添加破功(+鱼儿3*5);
					cm.喇叭(2,"恭喜玩家["+cm.getName()+"]将钓鱼获得的小鱼回收获得增加了钓鱼积分");//"+总和*1+"
					cm.dispose();
				}
        } else if (status == 2) {
            if (beauty == 0) {
                cm.gainItem(4000463, selection);
               //cm.gainItem(4000463,鱼儿2);
                cm.gainNX(-selection * 3000);
                cm.sendOk("兑换成功。");
                cm.dispose();
            /*} else if (beauty == 1) {
                cm.gainItem(4000463, -selection);
                cm.gainNX(selection * 2800);
                cm.sendOk("兑换成功。");
                cm.dispose();
}*/
}
            }
        }
    }
}

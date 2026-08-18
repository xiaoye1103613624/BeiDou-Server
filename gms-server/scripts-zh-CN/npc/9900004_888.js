var 爱心 = "#fEffect/CharacterEff/1022223/4/0#";
var 红色箭头 = "#fUI/UIWindow/Quest/icon6/7#";
var 正方形 = "#fUI/UIWindow/Quest/icon3/6#";
var 蓝色箭头 = "#fUI/UIWindow/Quest/icon2/7#";
var 金币 = "#fItem/Special/0900.img/09000001/iconRaw/1#";
var 点券 = "#fUI/CashShop/CashItem/0#";
var 奖励 = "#fUI/CashShop/CSDiscount/bonus#";
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

            cm.sendOk("感谢你的光临！");
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
                text += ""+奖励+"\r\n"
                text += "#k#v4032391#×10 #v4000038#×1 #v4170006#×10 #v4170005#×10\r\n";
                text += "#v3600001#×30   #v4001126#×200 #v4000313#×30 #v4032392#×5\r\n";
				text += ""+金币+"冒险币 100万 \r\n";
				text += ""+金币+"点卷 4千 \r\n";
				text += "领取时请将会员VIP1#v1142174#放入背包中，并确保背包其他栏有空格#k#n\r\n";
                text += "\t\t\t\t#L1#"+蓝色箭头+"领取VIP1每日礼包\r\n\r\n";//3
            cm.sendSimple(text);
           }
        } else if (selection == 1) {
if(cm.getBossLog('VIPfuli') >0)
{
cm.sendOk("你今天已经领取过一次");
cm.dispose();

}
/*else if (cm.getzb()<0)
{
	cm.sendOk("你的充值积分不足400,无法领取会员VIP1每日礼包");
cm.dispose();
}*/
          else  if (cm.haveItem(1142174, 1)) {

if(cm.canHold(4002003, 400) && cm.canHold(1122017, 1))
{
cm.gainNX(4000);//点卷
//cm.getPlayer().modifyCSPoints(2, 9000, true);//抵用
cm.gainMeso(1000000);
//cm.gainItem(5211047,1);//双倍卡
//cm.gainItem(5360014,1);//双爆卡
cm.gainItem(4170005,10);//玩具蛋
cm.gainItem(4170006,10);//天空蛋
cm.gainItem(3600001,30);//跑环币
//cm.gainItem(4001006,10);//火焰羽毛
//cm.gainItem(4000244,10);//蓝色双脚龙的灵魂
//cm.gainItem(4000245,10);//蓝色双脚龙的鳞片
cm.gainItem(4032391,10);//破碎的碎片1
cm.gainItem(4032392,5);//破碎的碎片2
cm.gainItem(4000038,1);//金杯
//cm.gainItem(4000463,10);//国庆纪念币
cm.gainItem(4001126,200);//枫叶
cm.gainItem(4000313,30);//进阶币
cm.setBossLog('VIPfuli');

cm.worldMessage("[会员公告]:会员VIP1玩家【"+cm.getPlayer().getName()+"】领取了今天的会员VIP1每日礼包，制作装备快人一步哟！");
            //cm.dispose();
}
else
{
  cm.sendOk("请清理背包,确保有足够空间!\r\n");
   cm.dispose();
}
			}else{
            cm.sendOk("请确认已经将会员VIP1勋章放入背包");
            cm.dispose();
			}
        } 
		
    }
}

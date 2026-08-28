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
                text += "#k#v4000463#×30 #v4032391#×80 #v4000038#×20 #v4170006#×60 #v4170005#×60\r\n";
                text += "#v3600001#×200  #v4001126#×800 #v4000313#×200 #v4032392#×80\r\n";
				text += "#r#v4001129#×1  #v4310150#×20  #v4001197#×4\r\n";
				text += ""+金币+"冒险币 800万 \r\n";
				text += ""+金币+"点卷 10万 \r\n";
				text += "领取时请将会员VIP4#v1142803#放入背包中，并确保背包其他栏有空格#k#n\r\n";
                text += "\t\t\t\t#L1#"+蓝色箭头+"领取VIP4每日礼包\r\n\r\n";//3
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
	cm.sendOk("你的充值积分不足3000,无法会员VIP4每日礼包");
cm.dispose();
}*/
          else  if (cm.haveItem(1142803, 1)) {

if(cm.canHold(4002003, 400) && cm.canHold(1122017, 1))
{
cm.gainNX(100000);//点卷
//cm.getPlayer().modifyCSPoints(2, 15000, true);//抵用
cm.gainMeso(8000000);
//cm.gainItem(5211047,1);//双倍卡
//cm.gainItem(5360014,1);//双爆卡
//cm.gainItem(3600001,120);//跑环币
//cm.gainItem(4001006,30);//火焰羽毛
cm.gainItem(3600001,200);//跑环币
cm.gainItem(4310150,20);//商品纪念币
cm.gainItem(4001197,4);//淡蓝色矿石
cm.gainItem(4001129,1);//技能币
cm.gainItem(4170005,60);//玩具蛋
cm.gainItem(4170006,60);//天空蛋
//cm.gainItem(4000244,50);//蓝色双脚龙的灵魂
//cm.gainItem(4000245,50);//蓝色双脚龙的鳞片
cm.gainItem(4000463,30);//国庆币
cm.gainItem(4000313,200);//进阶币
cm.gainItem(4032392,80);//破碎的碎片2
cm.gainItem(4032391,80);//破碎的碎片1
cm.gainItem(4001126,800);//枫叶
cm.gainItem(4000038,20)//金杯
cm.setBossLog('VIPfuli');

cm.worldMessage("[会员公告]:会员4玩家【"+cm.getPlayer().getName()+"】领取了今天的会员VIP4每日礼包！");
            cm.dispose();
}
else
{
  cm.sendOk("请清理背包,确保有足够空间!\r\n");
   cm.dispose();
}
			}else{
            cm.sendOk("请确认已经将会员VIP4戒指放入背包");
            cm.dispose();
			}
        } 
		
    }
}

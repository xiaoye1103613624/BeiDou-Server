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
                text += "#k#v4000463#×5 #v4032391#×50 #v4000038#×10 #v4170006#×30 #v4170005#×30\r\n";
                text += "#v3600001#×100  #v4001126#×500 #v4000313#×100 #v4032392#×50\r\n";
				text += "#r#v4001129#×1  #v4310150#×10  #v4001197#×2\r\n";
				text += ""+金币+"冒险币 500万 \r\n";
				text += ""+金币+"点卷 5万 \r\n";
				text += "领取时请将会员VIP3#v1142178#放入背包中，并确保背包其他栏有空格#k#n\r\n";
                text += "\t\t\t\t#L1#"+蓝色箭头+"领取VIP3每日礼包\r\n\r\n";//3
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
	cm.sendOk("你的充值积分不足3000,无法领取VIP3每日礼包");
cm.dispose();
}*/
          else  if (cm.haveItem(1142178, 1)) {

if(cm.canHold(4002003, 400) && cm.canHold(1122017, 1))
{
cm.gainNX(50000);//点卷
//cm.getPlayer().modifyCSPoints(2, 15000, true);//抵用
cm.gainMeso(5000000);
//cm.gainItem(5211047,1);//双倍卡
//cm.gainItem(5360014,1);//双爆卡
cm.gainItem(3600001,100);//跑环币
cm.gainItem(4310150,10);//商品纪念币
cm.gainItem(4001197,2);//淡蓝色矿石
cm.gainItem(4001129,1);//技能币
cm.gainItem(4170005,30);//玩具蛋
cm.gainItem(4170006,30);//天空蛋
//cm.gainItem(4001006,50);//火焰羽毛
//cm.gainItem(4000244,50);//蓝色双脚龙的灵魂
//cm.gainItem(4000245,50);//蓝色双脚龙的鳞片
cm.gainItem(4000463,5);//国庆币
cm.gainItem(4000313,100);//进阶币
cm.gainItem(4032392,50);//破碎的碎片2
cm.gainItem(4032391,50);//破碎的碎片1
cm.gainItem(4001126,500);//枫叶
cm.gainItem(4000038,10)//金杯
cm.setBossLog('VIPfuli');

cm.worldMessage("[会员公告]:VIP3玩家【"+cm.getPlayer().getName()+"】领取了今天的VIP3每日礼包，制作装备快人一步哟！");
            //cm.dispose();
}
else
{
  cm.sendOk("请清理背包,确保有足够空间!\r\n");
   cm.dispose();
}
			}else{
            cm.sendOk("请确认已经将VIP3称号放入背包");
            cm.dispose();
			}
        } 
		
    }
}

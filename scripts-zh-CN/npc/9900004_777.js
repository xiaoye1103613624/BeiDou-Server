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
                text += "#k#v4032391#×5 #v4001126#×100 #v4170006#×3\r\n";
                text += "#v4000038#×1  #v3600001#×10 #v4000313#×10 #v4170005#×3\r\n";
				text += ""+金币+"冒险币 50万 \r\n";
				text += ""+金币+"点卷 2千 \r\n";
				text += "领取时请将首充称号#v1142173#放入背包中，并确保背包其他栏有空格#k#n\r\n";
				text += "\t\t\t\t#L1#"+蓝色箭头+"领取首充会员每日礼包\r\n\r\n";//3
            cm.sendSimple(text);
           }
        } else if (selection == 1) {
if(cm.getBossLog('每日VIP') >0)
{
cm.sendOk("你今天已经领取过一次");
cm.dispose();

}
/*else if (cm.getzb()<0)
{
	cm.sendOk("你的充值积分不足200,无法领取首充会员每日礼包");
cm.dispose();
}*/
          else  if (cm.haveItem(1142173, 1)) {

if(cm.canHold(4002003, 400) && cm.canHold(1122017, 1))
{
cm.gainNX(2000);//点卷
//cm.getPlayer().modifyCSPoints(2, 5000, true);//抵用
cm.gainMeso(500000);//金币
//cm.gainItem(5211047,1);//双倍卡
cm.gainItem(3600001,10);//跑环币
cm.gainItem(4170006,3);//天空蛋
cm.gainItem(4170005,3);//玩具蛋
//cm.gainItem(4001006,5);//火焰羽毛
//cm.gainItem(4000244,5);//蓝色双脚龙的灵魂
//cm.gainItem(4000245,5);//蓝色双脚龙的鳞片
//cm.gainItem(4000463,5);//国庆纪念币
cm.gainItem(4032391,5);//破碎的卷轴碎片1
cm.gainItem(4000313,10);//进阶币
cm.gainItem(4001126,100);//枫叶
cm.gainItem(4000038,1);//金杯
cm.setBossLog('每日VIP');

cm.全服黄色喇叭("[会员公告]:首充会员玩家【"+cm.getPlayer().getName()+"】领取了今天的首充会员每日礼包，制作装备快人一步哟！");
//            cm.dispose();
}
else
{
  cm.sendOk("请清理背包,确保有足够空间!\r\n");
   cm.dispose();
}
			}else{
            cm.sendOk("请确认已经将首充称号放入背包");
            cm.dispose();
			}
        } 
		
    }
}

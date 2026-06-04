var  a1 = "#fUI/ChatBalloon.img/28/w#";//右上
var  a2 = "#fUI/ChatBalloon.img/28/e#";//上中
var  a3 = "#fUI/ChatBalloon.img/28/n#";//右上
var  a4 = "#fUI/ChatBalloon.img/28/s#";//右上
var  a5 = "#fUI/ChatBalloon.img/28/nw#";//右上
var  a6 = "#fUI/ChatBalloon.img/28/ne#";//右上
var  a7 = "#fUI/ChatBalloon.img/155/nw#";//右上
var  a8 = "#fUI/ChatBalloon.img/19/nw#";//右上
var  a9 = "#fUI/ChatBalloon.img/19/ne#";//右上
var  a10 = "#fUI/ChatBalloon.img/28/sw#";//右上
var  a11 = "#fUI/ChatBalloon.img/28/se#";//右上
var  a12 = "#fUI/ChatBalloon.img/28/s#";//右上
var status = -1;
var select = -1;

function start() {
    cm.sendOk(
            "#fUI/ChatBalloon.img/28/nw##fUI/ChatBalloon.img/28/n##fUI/ChatBalloon.img/28/n##fUI/ChatBalloon.img/28/n##fUI/ChatBalloon.img/28/n##fUI/ChatBalloon.img/28/n##fUI/ChatBalloon.img/28/n##fUI/ChatBalloon.img/28/n##fUI/ChatBalloon.img/28/n##fUI/ChatBalloon.img/28/n##fUI/ChatBalloon.img/28/n##fUI/ChatBalloon.img/28/n##fUI/ChatBalloon.img/28/n##fUI/ChatBalloon.img/28/n##fUI/ChatBalloon.img/28/n##fUI/ChatBalloon.img/28/n##fUI/ChatBalloon.img/28/n##fUI/ChatBalloon.img/28/n##fUI/ChatBalloon.img/28/n##fUI/ChatBalloon.img/28/n##fUI/ChatBalloon.img/28/n##fUI/ChatBalloon.img/28/n##fUI/ChatBalloon.img/28/n##fUI/ChatBalloon.img/28/n##fUI/ChatBalloon.img/28/n##fUI/ChatBalloon.img/28/n##fUI/ChatBalloon.img/28/n##fUI/ChatBalloon.img/28/ne#\r\n\r\n" +
			"   #fUI/ChatBalloon.img/28/w##d亲爱的：" + cm.getChar().getName() + "[#r以下是" + cm.getChannelServer().getServerName() + " 玩家指令]#fUI/ChatBalloon.img/28/e##k\r\n\r\n" +
			"  \t   #d▇▇▆▅▄▃▂#d万用指令区#d▂▃▄▅▆▇▇\r\n\r\n" +
			
		//	"      #d@解卡组队#k       -  #r「解卡组队异常」#k\r\n" +
			
			"      #d@爆率#k           -  #r「查看怪物物品掉落」#k\r\n" +
			
			"      #d@复活术#k         -  #r「可以原地复活」#k\r\n" +
			
			"      #d@清除所有状态#k   -  #r「清除所有现有BUFF」#k\r\n" +
			
			"      #d@测试伤害#k       -  #r「可以测试秒伤」#k\r\n" +
			
			"      #d@解卡#k           -  #r「解卡异常+查看角色倍率」#k\r\n" + 
	 
			"      #d@怪物/@mob#k      -  #r「查看身旁怪物信息」#k\r\n" +
			
			"      #d@怪物数量#k       -  #r「查看当前地图怪物数量」#k\r\n" +
	
			"      #d@离线挂机#k       -  #r「启用离线挂机」#k\r\n" +
		
			"      #d@力量 点数#k      -  #r「力量-快捷加点」#k\r\n" +
			
			"      #d@敏捷 点数#k      -  #r「敏捷-快捷加点」#k\r\n" +
			
			"      #d@智力 点数#k      -  #r「智力-快捷加点」#k\r\n" +
			
			"      #d@运气 点数#k      -  #r「运气-快捷加点」#k\r\n\r\n" +
			
			"      #b---------------以下为月卡功能---------------- #k\r\n\r\n"+
			
			"      #d@自动存金币#k     -  #r「金币满10亿自动兑换成道具」#k\r\n" +
			
			"      #d@物品落脚下#k     -  #r「掉落脚下」#k\r\n" +
			
			"      #d@刷钱模式#k       -  #r「开启/关闭自动贩卖」#k\r\n\r\n" +
			
			"  \t   #d▇▇▆▅▄▃▂#d很高兴认识您#d▂▃▄▅▆▇▇\r\n" +
            "#fUI/ChatBalloon.img/28/sw##fUI/ChatBalloon.img/28/s##fUI/ChatBalloon.img/28/s##fUI/ChatBalloon.img/28/s##fUI/ChatBalloon.img/28/s##fUI/ChatBalloon.img/28/s##fUI/ChatBalloon.img/28/s##fUI/ChatBalloon.img/28/s##fUI/ChatBalloon.img/28/s##fUI/ChatBalloon.img/28/s##fUI/ChatBalloon.img/28/s##fUI/ChatBalloon.img/28/s##fUI/ChatBalloon.img/28/s##fUI/ChatBalloon.img/28/s##fUI/ChatBalloon.img/28/s##fUI/ChatBalloon.img/28/s##fUI/ChatBalloon.img/28/s##fUI/ChatBalloon.img/28/s##fUI/ChatBalloon.img/28/s##fUI/ChatBalloon.img/28/s##fUI/ChatBalloon.img/28/s##fUI/ChatBalloon.img/28/s##fUI/ChatBalloon.img/28/s##fUI/ChatBalloon.img/28/s##fUI/ChatBalloon.img/28/s##fUI/ChatBalloon.img/28/s##fUI/ChatBalloon.img/28/s##fUI/ChatBalloon.img/28/se#\r\n"


            );
    cm.dispose();
}
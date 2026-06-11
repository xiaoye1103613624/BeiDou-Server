var 皇冠白 ="#fUI/GuildMark/Mark/Etc/00009004/15#";
var 圆形 = "#fUI/UIWindow/Quest/icon3/6#";
var 感叹号 = "#fUI/UIWindow/Quest/icon0#";
var 正方箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";
var 奖励 = "#fUI/UIWindow/Quest/reward#";
var 圆形 = "#fUI/UIWindow/Quest/icon3/6#";
var 感叹号 = "#fUI/UIWindow/Quest/icon0#";
var 粉心 = "#fEffect/CharacterEff/1042176/2/0#";
var 红心 = "#fEffect/CharacterEff/1082229/0/0#";
var 粉星 = "#fEffect/CharacterEff/1112926/0/1#";
var HOT = "#fUI/CashShop/CSEffect/hot/3#";
var 新 = "#fUI/CashShop/CSEffect/event/3#";
var 点券图标 = "#fUI/CashShop/CashItem/0#";
var 元宝 = 0;
var 赞助 = 0;
var say = "";
var 赞助充值礼包;
var chr = 0;
function start() {
	em = cm.getEventManager("ServerMessage");
	chr = cm.getPlayer();
	元宝 = cm.getmoneyb();
    赞助 = cm.getPlayer().getmoney();
	破功 = cm.getPlayer().getDamage(); 
	status = -1;
    action(1, 0, 0);
}
function action(mode, type, selection) {
    if(mode == -1){cm.dispose();return;}
    if(status == 0 && mode == 0) {cm.dispose();return;}
    if(mode == 1) {status++;} else {status--;}
    if (status == 0) {
		赞助礼包 = chr.getBossLog1("赞助礼包");
		say  = "";
		say += "                "+皇冠白+" - #r#e节 日 礼 包#n#k - "+皇冠白+"#l\r\n";
		say += "      #r#e╭#n"+粉星+""+粉星+"#e～～～～～～～～～～～～～～"+粉星+"～╮#n\r\n";
        say += "              #k"+粉心+" [当前赞助：#r"+extend(赞助,5)+"#k余额] "+粉心+"      \r\n";
		say += "     #r#e╰ ～～～～～～～～～～～～～～～～～～ ╯#n#l\r\n";
		say += "	  #k#e╭～～～～～～ #n领 取 状 态#e ～～～～～～╮#n\r\n";
		say += "          #L0 #"+ 正方箭头 +" #k[#b“双诞”限时活动礼包:#r 298元#k]#l\r\n\r\n";
		say += "          #L1 #"+ 正方箭头 +" #k[#b“双诞”限时活动礼包:#r 498元#k]#l\r\n\r\n";
		say += "          #L2 #"+ 正方箭头 +" #k[#b“双诞”限时活动礼包:#r 888元#k]#l\r\n\r\n";
		say += "     #k#e╰ ～～～～～～～～～～～～～～～～～～ ╯#n#l\r\n\r\n";
		say += "  "+ 红心 +" 礼包购买不限次数，截止 2026年1月3日24点 结束 "+ 红心 +"#k\r\n\r\n";

		cm.sendSimple(say);
    }else if(status == 1) {
		if (selection == 0) {
			warp = selection;
			金额 = 298;
			say = ""+HOT+"\r\n";
			say += ""+正方箭头+"[活动礼包]：[ #r"+赞助+" #k / #b 298 #k] \r\n\r\n";
			say += ""+正方箭头+"#k[获得]:[" + 点券图标 + "][累计]:[#r"+金额+"#k]\r\n\r\n";
			say += ""+正方箭头+"#k[获得]:[" + 点券图标 + "][点卷]:[#r298W#k]\r\n\r\n";
			say += ""+正方箭头+"#k[获得]:[" + 点券图标 + "][抵用]:[#r298W#k]\r\n";
			
			say += ""+正方箭头+"#k[获得]:[#b#v 4000487##k][#b#z 4000487##k] x #r888个#k\r\n";
			say += ""+感叹号+"#k[道具介绍]:[#r绯红副本刷材料货币#k]\r\n";
			
			say += ""+正方箭头+"#k[获得]:[#b#v 4310100##k][#b#z 4310100##k] x #r298个#k\r\n";
			say += ""+感叹号+"#k[道具介绍]:[#r可以回收获得积分#k]\r\n";
			
			say += ""+正方箭头+"#k[获得]:[#b#v 4322952##k][#b#z 4322952##k] x #r58个#k\r\n";
			say += ""+感叹号+"#k[道具介绍]:[#r强化小鱼戒指的重要材料#k]\r\n";

			say += ""+正方箭头+"#k[获得]:[#b#v 1082102##k][#b#z 1082102##k] x #r1个#k\r\n";
			say += ""+感叹号+"#k[道具介绍]:[#r全属性200-200#k]\r\n";
			
			say += ""+正方箭头+"#k[获得]:[#b#v 1072153##k][#b#z 1072153##k] x #r1个#k\r\n";
			say += ""+感叹号+"#k[道具介绍]:[#r全属性200-200#k]\r\n";
			
			say += ""+正方箭头+"#k[获得]:[#b#v 3604010##k][#b#z 3604010##k] x #r5个#k\r\n";
			say += ""+感叹号+"#k[道具介绍]:[#r放背包额外伤害+1%，也可增加分身伤害#k]\r\n";

			cm.sendYesNo(say);
		}else if (selection == 1) {
			warp = selection;
			金额 = 498;
			say = ""+新+"\r\n";
			say += ""+正方箭头+"[活动礼包]：[ #r"+赞助+" #k / #b 498 #k] \r\n\r\n";
			say += ""+正方箭头+"#k[获得]:[" + 点券图标 + "][累计]:[#r"+金额+"#k]\r\n\r\n";
			say += ""+正方箭头+"#k[获得]:[" + 点券图标 + "][点卷]:[#r498W#k]\r\n\r\n";
			say += ""+正方箭头+"#k[获得]:[" + 点券图标 + "][抵用]:[#r498W#k]\r\n";
			
			say += ""+正方箭头+"#k[获得]:[#b#v 4000487##k][#b#z 4000487##k] x #r1888个#k\r\n";
			say += ""+感叹号+"#k[道具介绍]:[#r绯红副本刷材料货币#k]\r\n";
			
			say += ""+正方箭头+"#k[获得]:[#b#v 4310100##k][#b#z 4310100##k] x #r498个#k\r\n";
			say += ""+感叹号+"#k[道具介绍]:[#r可以回收获得积分#k]\r\n";
			
			say += ""+正方箭头+"#k[获得]:[#b#v 4322952##k][#b#z 4322952##k] x #r108个#k\r\n";
			say += ""+感叹号+"#k[道具介绍]:[#r强化小鱼戒指的重要材料#k]\r\n";

			say += ""+正方箭头+"#k[获得]:[#b#v 1012618##k][#b#z 1012618##k] x #r1个#k\r\n";
			say += ""+感叹号+"#k[道具介绍]:[#r全属性200-200#k]\r\n";
			
			say += ""+正方箭头+"#k[获得]:[#b#v 1122222##k][#b#z 1122222##k] x #r1个#k\r\n";
			say += ""+感叹号+"#k[道具介绍]:[#r全属性200-200 额外属性10%#k]\r\n";
			
			say += ""+正方箭头+"#k[获得]:[#b#v 3604010##k][#b#z 3604010##k] x #r10个#k\r\n";
			say += ""+感叹号+"#k[道具介绍]:[#r放背包额外伤害+1%，也可增加分身伤害#k]\r\n";
			
			cm.sendYesNo(say);
		}else if (selection == 2) {
			warp = selection;
			金额 = 888;
			say = ""+新+"\r\n";
			say += ""+正方箭头+"[活动礼包]：[ #r"+赞助+" #k / #b 888 #k] \r\n\r\n";
			say += ""+正方箭头+"#k[获得]:[" + 点券图标 + "][累计]:[#r"+金额+"#k]\r\n\r\n";
			say += ""+正方箭头+"#k[获得]:[" + 点券图标 + "][点卷]:[#r888W#k]\r\n\r\n";
			say += ""+正方箭头+"#k[获得]:[" + 点券图标 + "][抵用]:[#r888W#k]\r\n";
			
			say += ""+正方箭头+"#k[获得]:[#b#v 4322952##k][#b#z 4322952##k] x #r188个#k\r\n";
			say += ""+感叹号+"#k[道具介绍]:[#r强化小鱼戒指的重要材料#k]\r\n";
			
			say += ""+正方箭头+"#k[获得]:[#b#v 4000487##k][#b#z 4000487##k] x #r2888个#k\r\n";
			say += ""+感叹号+"#k[道具介绍]:[#r绯红副本刷材料货币#k]\r\n";
			
			say += ""+正方箭头+"#k[获得]:[#b#v 3605021##k][#b#z 3605021#(永久)#k] x #r1个#k\r\n";
			say += ""+感叹号+"#k[道具介绍]:[#r进入每日充值地图无限制#k]\r\n";
			
			cm.sendYesNo(say);
		}
    }else if(status == 2) {

		if(warp == 0){
		    金额 = 298;
            if(赞助 < 金额){cm.sendOk(""+正方箭头+" [ 您的赞助金额不足#r"+金额+"#k元]");cm.dispose();return;}
			if (cm.getInventory(4).isFull(3)) {cm.sendOk("其他栏不足4个空位");cm.dispose();return;}
			if (cm.getInventory(3).isFull(3)) {cm.sendOk("消耗栏不足4个空位");cm.dispose();return;}
			if (cm.getInventory(1).isFull(3)) {cm.sendOk("装备栏不足4个空位");cm.dispose();return;}
			cm.gainItem(4000487,888);//暗影币
			cm.gainItem(4310100,298);//一万积分
			cm.gainItem(4322952,58);//黄金鱼
			cm.gainItem(3604010,5);//分身伤害
            cm.给抵用券(2980000);//抵用券
            cm.给点券(2980000);//点券
            cm.gainItem(1082102,0,0,200,200,200,200,0,0,200,200,0,0,0,0,0,0);//透明手套
            cm.gainItem(1072153,0,0,200,200,200,200,0,0,200,200,0,0,0,0,0,0);//透明靴子
			cm.getPlayer().setmoney(cm.getPlayer().getmoney() - 金额);  //-赞助
			cm.getPlayer().setlpjf(cm.getPlayer().getlpjf() + 金额); //+累计积分
			cm.getPlayer().dropMessage(5, "点券：+2980000");   //红字私聊提示
			cm.getPlayer().dropMessage(5, "抵用：+2980000");   //红字私聊提示
			cm.getPlayer().dropMessage(5, "赞助点：-" +金额+ "");   //红字私聊提示
			cm.getPlayer().dropMessage(5, "累计积分：+" +金额+ "");   //红字私聊提示
			cm.全服黄色喇叭("赞助赞助礼包 : 恭喜玩家["+cm.getPlayer().getName()+"]在赞助中心购买了298国庆礼包！");
			cm.全服漂浮喇叭( "【活动礼包】恭喜土豪玩家 "+cm.getPlayer().getName()+" 购买了298元限时活动礼包！" , 5120015 );
			cm.playerMessage(1, "恭喜：领取完成！");
			Packages.tools.FileoutputUtil.log("log\\玩家相关\\“双诞”298元活动礼包.log", "[" + cm.getName() + "]在赞助中心购买了298国庆礼包！");
			cm.dispose();
			
		}else if(warp == 1){
		    金额 = 498;
            if(赞助 < 金额){cm.sendOk(""+正方箭头+" [ 您的赞助金额不足#r"+金额+"#k元]");cm.dispose();return;}
			if (cm.getInventory(4).isFull(3)) {cm.sendOk("其他栏不足4个空位");cm.dispose();return;}
			if (cm.getInventory(3).isFull(3)) {cm.sendOk("消耗栏不足4个空位");cm.dispose();return;}
			if (cm.getInventory(1).isFull(3)) {cm.sendOk("装备栏不足4个空位");cm.dispose();return;}
			cm.gainItem(4000487,1888);//暗影币
			cm.gainItem(4310100,498);//一万积分
			cm.gainItem(4322952,108);//黄金鱼
			cm.gainItem(3604010,10);//分身伤害
            cm.给抵用券(4980000);//抵用券
            cm.给点券(4980000);//点券
            cm.gainItem(1012618,0,0,200,200,200,200,0,0,200,200,0,0,0,0,0,0);//脸饰
            cm.gainItem(1122222,0,0,200,200,200,200,0,0,200,200,0,0,0,0,0,0);//吊坠
			cm.getPlayer().setmoney(cm.getPlayer().getmoney() - 金额);  //-赞助
			cm.getPlayer().setlpjf(cm.getPlayer().getlpjf() + 金额); //+累计积分
			cm.getPlayer().dropMessage(5, "点券：+4980000");   //红字私聊提示
			cm.getPlayer().dropMessage(5, "抵用：+4980000");   //红字私聊提示
			cm.getPlayer().dropMessage(5, "赞助点：-" +金额+ "");   //红字私聊提示
			cm.getPlayer().dropMessage(5, "累计积分：+" +金额+ "");   //红字私聊提示
			cm.全服黄色喇叭("赞助赞助礼包 : 恭喜玩家["+cm.getPlayer().getName()+"]在赞助中心领取了498国庆礼包！");
			cm.全服漂浮喇叭( "【活动礼包】恭喜土豪玩家 "+cm.getPlayer().getName()+" 购买了498元限时活动礼包！" , 5120015 );
			cm.playerMessage(1, "恭喜：领取完成！");
			Packages.tools.FileoutputUtil.log("log\\玩家相关\\“双诞”498元活动礼包.log", "[" + cm.getName() + "]在赞助中心购买了498国庆礼包！");
			cm.dispose();
		}else if(warp == 2){
		    金额 = 888;
            if(赞助 < 金额){cm.sendOk(""+正方箭头+" [ 您的赞助金额不足#r"+金额+"#k元]");cm.dispose();return;}
			if (cm.getInventory(4).isFull(3)) {cm.sendOk("其他栏不足4个空位");cm.dispose();return;}
			if (cm.getInventory(3).isFull(3)) {cm.sendOk("消耗栏不足4个空位");cm.dispose();return;}
			if (cm.getInventory(1).isFull(3)) {cm.sendOk("装备栏不足4个空位");cm.dispose();return;}
			cm.gainItem(4000487,2888);//暗影币
			cm.gainItem(4322952,188);//黄金鱼
			cm.gainItem(3605021,1);//VIP
            cm.给抵用券(8888888);//抵用券
            cm.给点券(8888888);//点券
			cm.getPlayer().setmoney(cm.getPlayer().getmoney() - 金额);  //-赞助
			cm.getPlayer().setlpjf(cm.getPlayer().getlpjf() + 金额); //+累计积分
			cm.getPlayer().dropMessage(5, "点券：+8888888");   //红字私聊提示
			cm.getPlayer().dropMessage(5, "抵用：+8888888");   //红字私聊提示
			cm.getPlayer().dropMessage(5, "赞助点：-" +金额+ "");   //红字私聊提示
			cm.getPlayer().dropMessage(5, "累计积分：+" +金额+ "");   //红字私聊提示
			cm.全服黄色喇叭("赞助赞助礼包 : 恭喜玩家["+cm.getPlayer().getName()+"]在赞助中心领取了888国庆礼包！");
			cm.全服漂浮喇叭( "【活动礼包】恭喜土豪玩家 "+cm.getPlayer().getName()+" 购买了888元限时活动礼包！" , 5120015 );
			cm.playerMessage(1, "恭喜：领取完成！");
			Packages.tools.FileoutputUtil.log("log\\玩家相关\\“双诞”888元活动礼包.log", "[" + cm.getName() + "]在赞助中心购买了888国庆礼包！");
			cm.dispose();
		}
	}
}


function extend(say, num) {//空格
    var curLength = say.toString().length;
    if (curLength < num) {
        for (var i = 0; i < num - curLength; i++) {
            say += " ";
        }
    }
    return say;
}


var ca = java.util.Calendar.getInstance();
var year = ca.get(java.util.Calendar.YEAR); //获得年份
var month = ca.get(java.util.Calendar.MONTH) + 1; //获得月份
var day = ca.get(java.util.Calendar.DATE);//获取日
var hour = ca.get(java.util.Calendar.HOUR_OF_DAY); //获得小时
var minute = ca.get(java.util.Calendar.MINUTE);//获得分钟
var second = ca.get(java.util.Calendar.SECOND); //获得秒
var weekday = ca.get(java.util.Calendar.DAY_OF_WEEK);
var 正方箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";
var 爱心 = "#fEffect/CharacterEff/1022223/4/0#";
var 大粉红爱心 = "#fItem/Etc/0427/04270001/Icon8/4#";  //
var 小粉红爱心 = "#fItem/Etc/0427/04270001/Icon8/5#";  //
var 小黄星 = "#fItem/Etc/0427/04270001/Icon9/0#";  //
var 大黄星 = "#fItem/Etc/0427/04270001/Icon9/1#";  //
var 小水滴 = "#fItem/Etc/0427/04270001/Icon10/5#";  //
var 大水滴 = "#fItem/Etc/0427/04270001/Icon10/4#";  //
var tz = "#fEffect/CharacterEff/1082565/4/0#";  //粉兔子
var tz1 = "#fEffect/CharacterEff/1082565/0/0#";  //橙兔子
var tz2 = "#fEffect/CharacterEff/1082565/2/0#";  //蓝兔子
var 邪恶小兔 = "#fEffect/CharacterEff/1112960/3/0#";  //邪恶小兔 【小】
var 邪恶小兔2 = "#fEffect/CharacterEff/1112960/3/1#";  //邪恶小兔 【大】
var 花草 ="#fEffect/SetEff/208/effect/walk2/4#";
var 花草1 ="#fEffect/SetEff/208/effect/walk2/3#";
var 小花 ="#fMap/MapHelper/weather/birthday/2#";
var 桃花 ="#fMap/MapHelper/weather/rose/4#";
var 金枫叶 ="#fMap/MapHelper/weather/maple/2#";
var 红枫叶 ="#fMap/MapHelper/weather/maple/1#";
var 银杏叶 ="#fMap/MapHelper/weather/maple/3#";
var 小烟花 ="#fMap/MapHelper/weather/squib/squib4/1#";
var 星星 ="#fMap/MapHelper/weather/witch/3#";
//var tz = "#fEffect/CharacterEff/1082565/4/0#";  //兔子粉

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (status == 0 && mode == 0) {
        im.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
    if (im.getLevel() <= 7) {
			im.openNpc(9900004,5);
    }else if (status == 0) {//[#g#h ##n#k#d]
		var selStr = "                  #k"+邪恶小兔+"#e#g7天每日福利#n#k"+邪恶小兔+"\r\n";//#n#k豆豆点：#r" + im.getBeans() + "#k点\t\t//
		
		
	    selStr += "  #L0##r每日点数#l #L1##r每日金币#l #L2##r每日点券#l #L3##r每日元宝#l#k\r\n\r\n\r\n\r\n";
	   im.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
        case 0:
		if (im.getPlayer().getBossLog("每日2万点数") == 0){
			im.getPlayer().getAccountLog("杀怪点数",1);
			im.getPlayer().setAccountLog("杀怪点数",1,+20000);
		    im.getPlayer().setBossLog("每日2万点数");
		    im.sendOk("领取成功2万");
            /*World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice
			(9,im.getC().getChannel(),"每日点数" + " : 恭喜[" + im.getPlayer().getName() +"]成功领取了【每日点数】2万！！ ",true));*/
	        im.dispose();
		}else{
		    im.sendOk("你账号已经领取过了");
		    im.dispose();
		}
            break;
		case 1:
		if (im.getPlayer().getBossLog("每日金币") == 0){
			im.gainMeso(100000000);
		    im.getPlayer().setBossLog("每日金币");
		    im.sendOk("领取成功");
            /*World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice
			(9,im.getC().getChannel(),"每日金币" + " : 恭喜[" + im.getPlayer().getName() +"]成功领取了【每日金币】1亿！！ ",true));*/
	        im.dispose();
		}else{
		    im.sendOk("你账号已经领取过了");
		    im.dispose();
		}
            break;
		case 2:
		if (im.getPlayer().getBossLog("每日点券") == 0){
			im.gainNX(10000);
		    im.getPlayer().setBossLog("每日点券");
		    im.sendOk("领取成功");
            /*World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice
			(9,im.getC().getChannel(),"每日点券" + " : 恭喜[" + im.getPlayer().getName() +"]成功领取了【每日点券】1万！！ ",true));*/
	        im.dispose();
		}else{
		    im.sendOk("你账号已经领取过了");
		    im.dispose();
		}
            break;
		case 3:
		if (im.getPlayer().getBossLog("每日元宝") == 0){
			im.setzb(im.getzb()+50);
		    im.getPlayer().setBossLog("每日元宝");
		    im.sendOk("领取成功");
            /*World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice
			(9,im.getC().getChannel(),"每日元宝" + " : 恭喜[" + im.getPlayer().getName() +"]成功领取了【每日元宝】50！！ ",true));*/
	        im.dispose();
		}else{
		    im.sendOk("你账号已经领取过了");
		    im.dispose();
		}
            break;
		case 4:
		if(im.getPlayer().getAccountLog("每日丹药") == 1){
		im.sendOk("IP每天只能兑换一个");
	    im.dispose();
	    return;
		}
		if (im.haveItem(4000487,1)){
		    im.gainItem(3994613,1)
		    im.gainItem(4000487,-1)
		    im.getPlayer().setAccountLog("每日丹药");
		    im.sendOk("领取成功");
	        im.dispose();
		}else{
		    im.sendOk("你还没购买");
		    im.dispose();
		}
            break;
		}
    }

}
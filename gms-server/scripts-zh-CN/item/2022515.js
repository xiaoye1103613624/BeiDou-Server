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
		var selStr = "                #k"+邪恶小兔+"#e#g欢迎来到随身拍卖#n#k"+邪恶小兔+"\r\n";//#n#k豆豆点：#r" + im.getBeans() + "#k点\t\t//
		//selStr += " #d\t\t\t #d您的当前在线时间：#r"+im.getGamePoints()+" #d分钟#k#n\r\n"
		//
		//selStr += "      #d"+爱心+"余额:#r"+im.getmoneyb()+"    #d"+爱心+"点券:#r"+im.getPlayer().getCSPoints(1)+"    #d"+爱心+"抵用:#r"+im.getPlayer().getCSPoints(2)+"#k\r\n";
		
        selStr += "-------------------  * #d常用功能 *  -------------------";
		
	    selStr += "#L0#"+小烟花+"#r鬼屋房子#l\r\n";
	    //selStr += "#L0#"+小烟花+"#r自由市场#l #L88##r随身仓库#l #L2##r元宝购物#l #L3##g赞助福利"+小烟花+"#l#k\r\n";
	    //selStr += "#L4#"+小烟花+"#b每日答题#l #L5##d杂货商店#l #L6##d日常系列#l #L7##g首充福利"+小烟花+"#l#k\r\n\r\n";
		//
        //selStr += "-------------------  * #d我要变强 *  -------------------\r\n";
		//
        //selStr += "#L8#"+小烟花+"#r必做主线#l #L9##b装备制作#l #L10##b勋章称号#l #L11##b破攻系统"+小烟花+"#l#k\r\n";
		//
        ////selStr += "-------------------  * #d各种功能 *  -------------------\r\n";
		//
        //selStr += "#L12#"+小烟花+"#r万能兑换#l #L13##b点卷商城#l #L14##b快速转职#l #L15##b拜师学艺"+小烟花+"#l#k\r\n";
        //selStr += "#L16#"+小烟花+"#d副本传送#l #L17##d排行系统#l #L18##d删除物品#l #L19##d返回BOSS"+小烟花+"#l#k\r\n\r\n";
	   
	   
	   
	   
	   
       //selStr += " #L800#"+小烟花+"#r必做主线#l   #L1#"+小烟花+"#d万能传送#l   #L1212121#"+小烟花+"#g首充福利 #l\r\n";//   #L30010#"+小烟花+"#d装备觉醒#l\r\n";
       //selStr += " #L100055#"+小烟花+"#b每日答题#l   #L3#"+小烟花+"#d杂货商店#l\r\n";
       //selStr += " #L4#"+小烟花+"#r日常系列#l   #L6#"+小烟花+"#b点卷商城#l   \r\n";
		//#L7#"+小烟花+"#b元宝抽奖#l
       //selStr += " #L10#"+小烟花+"#r装备制作#l   #L1701#"+小烟花+"#b拜师学艺#l   #L 10086#"+小烟花+"#b返回BOSS #l\r\n";//#L10086#"+小烟花+"#d技能抽奖 #l\r\n";
       //selStr += " #L152202#"+小烟花+"#r万能兑换#l   #L20#"+小烟花+"#b快速转职#l   #L81100#"+小烟花+"#d删除物品#l #l\r\n";//
	   //selStr += " #L8#"+小烟花+"#r勋章称号#l   #L11#"+小烟花+"#b血量兑换#l   #L77#"+小烟花+"#r排行系统#l\r\n";
	   //selStr += " #L8100#"+小烟花+"#b破攻系统#l   #L110002#"+小烟花+"#b副本传送#l   #L1110002#"+小烟花+"#r赞助福利#l\r\n\r\n";
       //selStr += ""+大黄星+""+大黄星+""+大黄星+""+大黄星+""+大黄星+""+大黄星+""+大黄星+""+大黄星+""+大黄星+""+大黄星+""+大黄星+""+大黄星+""+大黄星+""+大黄星+""+大黄星+""+大黄星+"#k\r\n";
       im.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
        case 0:
            im.warpParty(682000303);
            im.dispose();
            break;
		case 1:
            im.dispose();
            im.openNpc(9900001,0);
            break;
		case 2:
            im.dispose();
            im.openNpc(9900004,1110003);
            break;
		case 3:
            im.dispose();
            im.openNpc(9900004,1110001);
            break;
		case 4:
            im.dispose();
            im.openNpc(9900004,100055);
            break;
		case 5:
            im.dispose();
	        im.openShop(7);
            break;
		case 6:
            im.dispose();
            im.openNpc(9900004,4);
            break;
		case 7:
            im.dispose();
            im.openNpc(9900004,1212121);
            break;
		case 8:
            im.dispose();
            im.openNpc(9900004,12011);
            break;
		case 9:
            im.dispose();
            im.openNpc(9900004,10001);
            break;
		case 10:
            im.dispose();
            im.openNpc(9250022,103);
            break;
		case 11:
            im.dispose();
            im.openNpc(9900004,8100);
            break;
			
		case 12:
            im.dispose();
            im.openNpc(9900004,19999);
            break;
		case 13:
            im.dispose();
            im.openNpc(9900004,9);
            break;
		case 14:
            im.dispose();
            im.openNpc(9050001,0);
            break;
		case 15:
            im.dispose();
            im.openNpc(9900004,1701);
            break;
		case 16:
            im.dispose();
            im.openNpc(9900004,290555);
            break;
		case 17:
            im.dispose();
            im.openNpc(9040004,0);
            break;
		case 18:
            im.dispose();
            im.openNpc(9900004,81100);
            break;
		case 19:
            im.dispose();
            im.openNpc(9200001,333);
            break;
		case 88:
            im.dispose();
            im.openNpc(9200001,321);
            break;
			
			
			
			
			
		//case 1:
        //    im.dispose();
        //    im.openNpc(9900004,1);
        //    break;
		case 110002:
            im.dispose();
            im.openNpc(9900004,290555);
            break;
		case 1110002:
            im.dispose();
            im.openNpc(9900004,1110001);
            break;
	    case 800:
            im.dispose();
            im.openNpc(9900004,12011);
            break;
		case 30010:
            im.dispose();
            im.openNpc(9900004,30010);
            break;
		case 100055:
            im.dispose();
            im.openNpc(9900004,100055);
            break;
		case 8001:
            im.dispose();
            im.openNpc(9900004,8001);
            break;
        case 2:
            im.dispose();
            im.openNpc(9900004,2);
            break;
	    case 1502:
            im.dispose();
            im.openNpc(9900004,4844);
            break;
		case 152202:
            im.dispose();
            im.openNpc(9900004,19999);
            break;
		case 10086:
            im.dispose();
            im.openNpc(9200001,333);//9900004,1086
            break;
        case 3:
            im.dispose();
            //im.openNpc(9310100,0);
	        im.openShop(7);
            break;
        case 4:
            im.dispose();
            im.openNpc(9900004,4);
            break;
		 case 8100:
            im.dispose();
            im.openNpc(9900004,8100);
            break;
        case 81100:
            im.dispose();
            im.openNpc(9900004,81100);
            break;
        case 5:
            im.dispose();
            im.openNpc(9900004,6);
            break;
	    case 1201:
            im.dispose();
            im.openNpc(9900004,1201);
            break;
        case 6:
            im.dispose();
            im.openNpc(9900004,9);
            break;
		case 1701:
            im.dispose();
            im.openNpc(9900004,1701);
            break;
        case 7:
            im.dispose();
            im.openNpc(9900004,8);
            break;
        case 8:
            im.dispose();
            im.openNpc(9250022,103);
            break;
        case 9:
            im.dispose();
            im.openNpc(9000041,0);
            break;
        case 10:
            im.dispose();
            im.openNpc(9900004,10001);
            break;
        case 11:
            im.dispose();
            im.openNpc(9900001,0);
            break;
		case 14:
            im.dispose();
            im.openWeb("http://wpa.qq.com/msgrd?V=1&Uin=1342041396&Site");
			break;
	    case 12:
            im.dispose();
            im.openNpc(9000041,4);
			 break;
		case 13:
            im.dispose();
			im.openWeb("http://new.shoukabao.com/Payment/Service/91bcd7b1d3c4b5f30d42cfa622de98d7");
			 break;
		case 15:
            im.dispose();
            im.openNpc(2020000,0);
            break;
		case 16:
            im.dispose();
            im.openNpc(9030100,0);
            break;
        case 17:
            im.dispose();
            im.openNpc(9050003,0);
            break;
	    case 77:
            im.dispose();
            im.openNpc(9040004,0);
            break;
	    case 78:
            im.dispose();
            im.openNpc(9900004,9999);
            break;
		case 18:
            im.dispose();
            im.openNpc(9050000,0);
            break;
	    case 99:
            im.dispose();
            im.openNpc(9250010,0);
            break;
	    case 20:
            im.dispose();
            im.openNpc(9050001,0);
            break;
	    case 1601:
            im.dispose();
            im.openNpc(9900004,1601);
            break;
	    case 19:
            im.dispose();
            im.openNpc(9310097,0);
            break;
		case 199:
            im.dispose();
            im.openNpc(9900004,1314);
            break;
		case 1212121:
            im.dispose();
            im.openNpc(9900004,1212121);
            break;
		}
    }

}
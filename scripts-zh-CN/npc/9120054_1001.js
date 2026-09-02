var 星星 = "#fEffect/CharacterEff/1114000/2/0#";
var 爱心 = "#fEffect/CharacterEff/1022223/4/0#";
var 红色箭头 = "#fUI/UIWindow/Quest/icon6/7#";
var 正方形 = "#fUI/UIWindow/Quest/icon3/6#";
var 蓝色箭头 = "#fUI/UIWindow/Quest/icon2/7#";
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
                text += "";
            }
			text += "\r\n"

			text += "#L1##k#v2510000#根据制作公式说明合成#v1002186#全属性2\r\n\r\n"//3透明帽子
			text += "#L2##k#v2510001#根据制作公式说明合成#v1022079#全属性2\r\n\r\n"//3透明眼镜
			text += "#L3##k#v2510002#根据制作公式说明合成#v1012289#全属性2\r\n\r\n"//3透明脸饰
			text += "#L4##k#v2510003#根据制作公式说明合成#v1004974#全属性2\r\n\r\n"//3航天帽子
			text += "#L5##k#v2510004#根据制作公式说明合成#v1053208#全属性2\r\n\r\n"//3航天服
			text += "#L6##k#v2510005#根据制作公式说明合成#v1702852#全属性2\r\n\r\n"//3航天星航枪
			text += "#L7##k#v2510006#根据制作公式说明合成#v1053367#全属性2\r\n\r\n"//3梦境小星星斗篷
			text += "#L8##k#v2510007#根据制作公式说明合成#v1702874#全属性2\r\n\r\n"//3梦境小星星武器
			text += "#L9##k#v2510008#根据制作公式说明合成#v1004776#全属性2\r\n\r\n"//3花栗鼠雨帽
			text += "#L10##k#v2510009#根据制作公式说明合成#v1053049#全属性2\r\n\r\n"//3花栗鼠雨衣
			text += "#L11##k#v2510010#根据制作公式说明合成#v1702830#全属性2\r\n\r\n"//3花栗鼠雨透明伞
			text += "#L12##k#v2510011#根据制作公式说明合成#v1702977#全属性2\r\n\r\n"//3可妮兔武器
			text += "#L13##k#v2510012#根据制作公式说明合成#v1005318#全属性2\r\n\r\n"//3可妮兔帽子
			text += "#L14##k#v2510013#根据制作公式说明合成#v1053440#全属性2\r\n\r\n"//3可妮兔衣服
			text += "#L15##k#v2510014#根据制作公式说明合成#v1702976#全属性2\r\n\r\n"//3莎莉武器
			text += "#L16##k#v2510015#根据制作公式说明合成#v1005320#全属性2\r\n\r\n"//3莎莉帽子
			text += "#L17##k#v2510016#根据制作公式说明合成#v1053442#全属性2\r\n\r\n"//3莎莉衣服
			text += "#L18##k#v2510017#根据制作公式说明合成#v1702975#全属性2\r\n\r\n"//3蛙里奥武器
			text += "#L19##k#v2510018#根据制作公式说明合成#v1005319#全属性2\r\n\r\n"//3蛙里奥帽子
			text += "#L20##k#v2510019#根据制作公式说明合成#v1053441#全属性2\r\n\r\n"//3蛙里奥衣服
			text += "#L21##k#v2510020#根据制作公式说明合成#v1005113#全属性2\r\n\r\n"//3熊宝宝暖帽
			text += "#L22##k#v2510021#根据制作公式说明合成#v1053285#全属性2\r\n\r\n"//3熊宝宝羽绒服
			text += "#L23##k#v2510022#根据制作公式说明合成#v1702365#全属性2\r\n\r\n"//3熊宝宝武器

            cm.sendSimple(text);
        } else if (selection == 1) { //透明帽子
			if( cm.haveItem(2510000,1) &&cm.haveItem(2210006,10) && cm.haveItem(4000040,10)&& cm.haveItem(4000176,10)&& cm.haveItem(1002019,1) && cm.getMeso()>=50000000) {
            cm.gainItem(2510000,-1);//透明帽子合成公式
			cm.gainItem(2210006,-10);//TT色蜗牛壳儿
			cm.gainItem(4000040,-10);//蘑菇王芽孢
			cm.gainItem(4000176,-10);//僵尸蘑菇王的孢子
			cm.gainItem(1002019,-1);//白色头巾
			cm.getPlayer().setBossLog("透明帽子",1,1)
			cm.gainItem(1002186,2,2,2,2,0,0,2,2,0,0,0,0,0,0);//透明帽子
			cm.gainMeso(-50000000);
            Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(9,cm.getClient().getChannel(),"透明帽子合成公告" + " : " + cm.getPlayer().getName() +"透明帽子合成 全属性50. 收集套装可升级属性",true));
			cm.dispose();
		   }else{
			cm.sendOk("制作失败 材料不足");
			cm.dispose();
		   }
        } else if (selection == 2) {  //透明眼镜
		
			if( cm.haveItem(2510001,1) && cm.haveItem(4000082,10)&& cm.haveItem(4000124,10)&& cm.haveItem(1022060,1) && cm.getMeso()>=50000000) {
            cm.gainItem(2510001,-1);//透明眼镜合成公式
			cm.gainItem(4000082,-10);//僵尸丢失的金齿
			cm.gainItem(4000124,-10);//战甲吹泡泡鱼的内存卡
			cm.gainItem(1022060,-1);//狐猴
			cm.getPlayer().setBossLog("透明眼镜",1,1)
			cm.gainItem(1022079,2,2,2,2,0,0,2,2,0,0,0,0,0,0);//透明眼镜
			cm.gainMeso(-50000000);
            Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(9,cm.getClient().getChannel(),"透明眼镜合成公告" + " : " + cm.getPlayer().getName() +"透明眼镜合成 全属性50. 收集套装可升级属性",true));
			cm.dispose();
		   }else{
			cm.sendOk("制作失败 材料不足");
			cm.dispose();
		   }
		  
        } else if (selection == 3) { //透明脸饰
			if( cm.haveItem(2510002,1) && cm.haveItem(4000460,10)&& cm.haveItem(4000461,10)&& cm.haveItem(4000462,10)&& cm.haveItem(1012307,1) && cm.getMeso()>=50000000) {
            cm.gainItem(2510002,-1);//透明脸饰合成公式
			cm.gainItem(4000460,-10);//多多的头盔
			cm.gainItem(4000461,-10);//雷卡的角
			cm.gainItem(4000462,-10);//玄冰独角兽的面具
			cm.gainItem(1012307,-1);//冰晶脸饰  
			cm.getPlayer().setBossLog("透明脸饰",1,1)
			cm.gainItem(1012289,2,2,2,2,0,0,2,2,0,0,0,0,0,0);//透明脸饰
			cm.gainMeso(-50000000);
            Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(9,cm.getClient().getChannel(),"透明脸饰合成公告" + " : " + cm.getPlayer().getName() +"透明脸饰合成 全属性50. 收集套装可升级属性",true));
			cm.dispose();
		   }else{
			cm.sendOk("制作失败 材料不足");
			cm.dispose();
		   }
        } else if (selection == 4) {//航天帽子
		if( cm.haveItem(2510003,1) && cm.haveItem(4011007,1)&& cm.haveItem(4011008,1)&& cm.haveItem(1002357,1) && cm.getMeso()>=50000000) {
            cm.gainItem(2510003,-1);//航天帽子合成公式
			cm.gainItem(4011007,-1);//月石
			cm.gainItem(4011008,-1);//锂
			cm.gainItem(1002357,-1);//扎昆头盔  
			cm.getPlayer().setBossLog("航天帽子",1,1)
			cm.gainItem(1004974,2,2,2,2,0,0,2,2,0,0,0,0,0,0);//透明脸饰
			cm.gainMeso(-50000000);
            Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(9,cm.getClient().getChannel(),"航天帽子合成公告" + " : " + cm.getPlayer().getName() +"航天帽子合成 全属性50. 收集套装可升级属性",true));
			cm.dispose();
		   }else{
			cm.sendOk("制作失败 材料不足");
			cm.dispose();
		   }
        } else if (selection == 5) { //航天服
		if( cm.haveItem(2510004,1) && cm.haveItem(4000082,5)&& cm.haveItem(4000021,200)&& cm.haveItem(1042027,1) && cm.getMeso()>=50000000) {
            cm.gainItem(2510004,-1);//航天帽子合成公式
			cm.gainItem(4000082,-1);//僵尸丢失的金齿 
			cm.gainItem(4000021,-200);//动物皮
			cm.gainItem(1042027,-1);//蓝色篮球衫   
			cm.getPlayer().setBossLog("航天服",1,1)
			cm.gainItem(1053208,2,2,2,2,0,0,2,2,0,0,0,0,0,0);//透明脸饰
			cm.gainMeso(-50000000);
            Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(9,cm.getClient().getChannel(),"航天服合成公告" + " : " + cm.getPlayer().getName() +"航天服合成 全属性50. 收集套装可升级属性",true));
			cm.dispose();
		   }else{
			cm.sendOk("制作失败 材料不足");
			cm.dispose();
		   }
        } else if (selection == 6) { //航天星航枪
		if( cm.haveItem(2510005,1) && cm.haveItem(4000040,20)&& cm.haveItem(4000176,20)&& cm.haveItem(1492012,1) && cm.getMeso()>=50000000) {
            cm.gainItem(2510005,-1);//航天星航枪合成公式
			cm.gainItem(4000040,-20);//蘑菇王芽孢
			cm.gainItem(4000176,-20);//僵尸蘑菇王的孢子
			cm.gainItem(1492012,-1);//金枪  
			cm.getPlayer().setBossLog("航天星航枪",1,1)
			cm.gainItem(1702852,2,2,2,2,0,0,2,2,0,0,0,0,0,0);//透明脸饰
			cm.gainMeso(-50000000);
            Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(9,cm.getClient().getChannel(),"航天星航枪合成公告" + " : " + cm.getPlayer().getName() +"航天星航枪合成 全属性50. 收集套装可升级属性",true));
			cm.dispose();
		   }else{
			cm.sendOk("制作失败 材料不足");
			cm.dispose();
		   }
        } else if (selection == 7) { //梦境小星星斗篷
		if( cm.haveItem(2510006,1) && cm.haveItem(2210006,10)&& cm.haveItem(4000067,10)&& cm.haveItem(1042027,1) && cm.getMeso()>=50000000) {
            cm.gainItem(2510006,-1);//梦境小星星斗篷合成公式
			cm.gainItem(2210006,-10);//TT色蜗牛壳儿
			cm.gainItem(4000067,-10);//幼魔精灵的角
			cm.gainItem(1042027,-1);//蓝色篮球衫  
			cm.getPlayer().setBossLog("梦境小星星斗篷",1,1)
			cm.gainItem(1053367,2,2,2,2,0,0,2,2,0,0,0,0,0,0);//透明脸饰
			cm.gainMeso(-50000000);
            Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(9,cm.getClient().getChannel(),"梦境斗篷合成公告" + " : " + cm.getPlayer().getName() +"梦境小星星斗篷合成 全属性50. 收集套装可升级属性",true));
			cm.dispose();
		   }else{
			cm.sendOk("制作失败 材料不足");
			cm.dispose();
		   }
		} else if (selection == 8) { //梦境小星星武器
		if( cm.haveItem(2510007,1) && cm.haveItem(4000067,10)&& cm.haveItem(4000124,10)&& cm.haveItem(1402016,1) && cm.getMeso()>=50000000) {
            cm.gainItem(2510007,-1);//梦境小星星武器公式
			cm.gainItem(4000067,-10);//魔精灵的角
			cm.gainItem(4000124,-10);//战甲吹泡泡鱼的内存卡
			cm.gainItem(1402016,-1);//所罗门  
			cm.getPlayer().setBossLog("梦境小星星武器",1,1)
			cm.gainItem(1702874,2,2,2,2,0,0,2,2,0,0,0,0,0,0);//透明脸饰
			cm.gainMeso(-50000000);
            Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(9,cm.getClient().getChannel(),"梦境武器合成公告" + " : " + cm.getPlayer().getName() +"梦境小星星武器合成 全属性50. 收集套装可升级属性",true));
			cm.dispose();
		   }else{
			cm.sendOk("制作失败 材料不足");
			cm.dispose();
		   }
		   } else if (selection == 9) { //花栗鼠雨帽
		if( cm.haveItem(2510008,1) && cm.haveItem(4000123,100)&& cm.haveItem(4000128,100)&& cm.haveItem(1002357,1) && cm.getMeso()>=50000000) {
            cm.gainItem(2510008,-1);//花栗鼠雨帽公式
			cm.gainItem(4000123,-10);//旧运动眼镜
			cm.gainItem(4000128,-10);//黄小丑戴的帽子
			cm.gainItem(1002357,-1);//所罗门  
			cm.getPlayer().setBossLog("花栗鼠雨帽",1,1)
			cm.gainItem(1004776,2,2,2,2,0,0,2,2,0,0,0,0,0,0);//透明脸饰
			cm.gainMeso(-50000000);
            Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(9,cm.getClient().getChannel(),"花栗鼠雨帽合成公告" + " : " + cm.getPlayer().getName() +"花栗鼠雨帽合成 全属性50. 收集套装可升级属性",true));
			cm.dispose();
		   }else{
			cm.sendOk("制作失败 材料不足");
			cm.dispose();
		   }
		   } else if (selection == 10) { //花栗鼠雨衣
		if( cm.haveItem(2510009,1) && cm.haveItem(4000444,200)&& cm.haveItem(4000449,200)&& cm.haveItem(4000454,200) && cm.getMeso()>=50000000) {
            cm.gainItem(2510009,-1);//花栗鼠雨衣公式
			cm.gainItem(4000444,-200);//绿色衣襟
			cm.gainItem(4000449,-200);//蓝色衣襟
			cm.gainItem(4000454,-200);//红色衣襟
			 
			cm.getPlayer().setBossLog("花栗鼠雨衣",1,1)
			cm.gainItem(1053049,2,2,2,2,0,0,2,2,0,0,0,0,0,0);//透明脸饰
			cm.gainMeso(-50000000);
            Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(9,cm.getClient().getChannel(),"花栗鼠雨衣合成公告" + " : " + cm.getPlayer().getName() +"花栗鼠雨衣合成 全属性50. 收集套装可升级属性",true));
			cm.dispose();
		   }else{
			cm.sendOk("制作失败 材料不足");
			cm.dispose();
		   }
		   } else if (selection == 11) { //花栗鼠雨透明伞
		if( cm.haveItem(2510010,1) && cm.haveItem(4000124,10)&& cm.haveItem(4000082,10)&& cm.haveItem(1302017,1) && cm.getMeso()>=50000000) {
            cm.gainItem(2510010,-1);//花栗鼠雨透明伞公式
			cm.gainItem(4000124,-10);//战甲吹泡泡鱼的内存卡
			cm.gainItem(4000082,-10);//僵尸丢失的金齿
			cm.gainItem(1302017,-1);//蓝色雨伞  
			cm.getPlayer().setBossLog("花栗鼠雨透明伞",1,1)
			cm.gainItem(1702830,2,2,2,2,0,0,2,2,0,0,0,0,0,0);//透明脸饰
			cm.gainMeso(-50000000);
            Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(9,cm.getClient().getChannel(),"花栗鼠雨透明伞公告" + " : " + cm.getPlayer().getName() +"花栗鼠雨透明伞合成 全属性50. 收集套装可升级属性",true));
			cm.dispose();
		   }else{
			cm.sendOk("制作失败 材料不足");
			cm.dispose();
		   }
		   } else if (selection == 12) { //可妮兔武器
		if( cm.haveItem(2510011,1) && cm.haveItem(4000169,1000)&& cm.haveItem(4031561,10) && cm.getMeso()>=50000000) {
            cm.gainItem(2510011,-1);//可妮兔武器公式
			cm.gainItem(4000169,-1000);//捣米棒
			cm.gainItem(4031561,-10);//赫丽娜的邮票  
			cm.getPlayer().setBossLog("可妮兔武器",1,1)
			cm.gainItem(1702977,2,2,2,2,0,0,2,2,0,0,0,0,0,0);//透明脸饰
			cm.gainMeso(-50000000);
            Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(9,cm.getClient().getChannel(),"可妮兔武器公告" + " : " + cm.getPlayer().getName() +"可妮兔武器合成 全属性50. 收集套装可升级属性",true));
			cm.dispose();
		   }else{
			cm.sendOk("制作失败 材料不足");
			cm.dispose();
		   }
		      } else if (selection == 13) { //可妮兔帽子
		if( cm.haveItem(2510012,1) && cm.haveItem(4000445,400)&& cm.haveItem(4000450,400)&& cm.haveItem(4000455,400) && cm.getMeso()>=50000000) {
            cm.gainItem(2510012,-1);//可妮兔帽子公式
			cm.gainItem(4000445,-400);//追忆绿色高帽
			cm.gainItem(4000450,-400);//后悔蓝色高帽 
			cm.gainItem(4000455,-400);//后悔红色高帽  
			cm.getPlayer().setBossLog("可妮兔帽子",1,1)
			cm.gainItem(1005318,2,2,2,2,0,0,2,2,0,0,0,0,0,0);//透明脸饰
			cm.gainMeso(-50000000);
            Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(9,cm.getClient().getChannel(),"可妮兔帽子公告" + " : " + cm.getPlayer().getName() +"可妮兔帽子合成 全属性50. 收集套装可升级属性",true));
			cm.dispose();
		   }else{
			cm.sendOk("制作失败 材料不足");
			cm.dispose();
		   }
		   	      } else if (selection == 14) { //可妮兔衣服
		if( cm.haveItem(2510013,1) && cm.haveItem(4000460,10)&& cm.haveItem(4000461,10)&& cm.haveItem(4000462,10) && cm.getMeso()>=50000000) {
            cm.gainItem(2510013,-1);//可妮兔衣服公式
			cm.gainItem(4000460,-10);//多的头盔
			cm.gainItem(4000461,-10);//雷卡的角
			cm.gainItem(4000462,-10);//玄冰独角兽的面具  
			cm.getPlayer().setBossLog("可妮兔衣服",1,1)
			cm.gainItem(1053440,2,2,2,2,0,0,2,2,0,0,0,0,0,0);//透明脸饰
			cm.gainMeso(-50000000);
            Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(9,cm.getClient().getChannel(),"可妮兔衣服公告" + " : " + cm.getPlayer().getName() +"可妮兔衣服合成 全属性50. 收集套装可升级属性",true));
			cm.dispose();
		   }else{
			cm.sendOk("制作失败 材料不足");
			cm.dispose();
		   }
		   	      } else if (selection == 15) { //莎莉武器
		if( cm.haveItem(2510014,1) && cm.haveItem(4000109,1000)&& cm.haveItem(4002000,10) &&cm.getMeso()>=50000000) {
            cm.gainItem(2510014,-1);//莎莉武器公式
			cm.gainItem(4000109,-100);//玩具小鸭
			cm.gainItem(4002000,-10);//蜗牛邮票
			cm.getPlayer().setBossLog("莎莉武器",1,1)
			cm.gainItem(1702976,2,2,2,2,0,0,2,2,0,0,0,0,0,0);//透明脸饰
			cm.gainMeso(-50000000);
            Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(9,cm.getClient().getChannel(),"莎莉武器公告" + " : " + cm.getPlayer().getName() +"莎莉武器合成 全属性50. 收集套装可升级属性",true));
			cm.dispose();
		   }else{
			cm.sendOk("制作失败 材料不足");
			cm.dispose();
		   }
		      	      } else if (selection == 16) { //莎莉帽子
		if( cm.haveItem(2510015,1) && cm.haveItem(4000445,400)&& cm.haveItem(4000450,400)&& cm.haveItem(4000455,400) && cm.getMeso()>=50000000) {
            cm.gainItem(2510015,-1);//莎莉帽子公式
			cm.gainItem(4000445,-400);//追忆绿色高帽
			cm.gainItem(4000450,-400);//后悔蓝色高帽
			cm.gainItem(4000455,-400);//后悔红色高帽  
			cm.getPlayer().setBossLog("莎莉帽子",1,1)
			cm.gainItem(1005320,2,2,2,2,0,0,2,2,0,0,0,0,0,0);//透明脸饰
			cm.gainMeso(-50000000);
            Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(9,cm.getClient().getChannel(),"莎莉帽子公告" + " : " + cm.getPlayer().getName() +"莎莉帽子合成 全属性50. 收集套装可升级属性",true));
			cm.dispose();
		   }else{
			cm.sendOk("制作失败 材料不足");
			cm.dispose();
		   }
		      	      } else if (selection == 17) { //莎莉衣服
		if( cm.haveItem(2510016,1) && cm.haveItem(4000040,20)&& cm.haveItem(4000176,20) && cm.getMeso()>=50000000) {
            cm.gainItem(2510016,-1);//莎莉衣服公式
			cm.gainItem(4000040,-20);//蘑菇王芽孢
			cm.gainItem(4000176,-20);//僵尸蘑菇王的孢子
			  
			cm.getPlayer().setBossLog("莎莉衣服",1,1)
			cm.gainItem(1053442,2,2,2,2,0,0,2,2,0,0,0,0,0,0);//透明脸饰
			cm.gainMeso(-50000000);
            Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(9,cm.getClient().getChannel(),"莎莉衣服公告" + " : " + cm.getPlayer().getName() +"莎莉衣服合成 全属性50. 收集套装可升级属性",true));
			cm.dispose();
		   }else{
			cm.sendOk("制作失败 材料不足");
			cm.dispose();
		   }
		           } else if (selection == 18) { //蛙里奥武器
		if( cm.haveItem(2510017,1) && cm.haveItem(4002000,10)&& cm.haveItem(4002001,10)&& cm.haveItem(4002002,10) &&cm.haveItem(4002003,10) && cm.getMeso()>=50000000) {
            cm.gainItem(2510017,-1);//蛙里奥武器公式
			cm.gainItem(4002000,-10);//蜗牛邮票
			cm.gainItem(4002001,-10);//蓝蜗牛邮票
			cm.gainItem(4002002,-10);//木妖邮票  
			cm.gainItem(4002003,-10);//绿水灵邮票
			cm.getPlayer().setBossLog("蛙里奥武器",1,1)
			cm.gainItem(1702975,2,2,2,2,0,0,2,2,0,0,0,0,0,0);//透明脸饰
			cm.gainMeso(-50000000);
            Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(9,cm.getClient().getChannel(),"蛙里奥武器公告" + " : " + cm.getPlayer().getName() +"蛙里奥武器合成 全属性50. 收集套装可升级属性",true));
			cm.dispose();
		   }else{
			cm.sendOk("制作失败 材料不足");
			cm.dispose();
		   }
		   		      	      } else if (selection == 19) { //蛙里奥帽子
		if( cm.haveItem(2510018,1) && cm.haveItem(4000247,500)&& cm.haveItem(4000041,500)&& cm.getMeso()>=50000000) {
            cm.gainItem(2510018,-1);//蛙里奥帽子公式
			cm.gainItem(4000247,-500);//青蛙的后腿
			cm.gainItem(4000041,-500);//巫婆的试验用青蛙  
			cm.getPlayer().setBossLog("蛙里奥帽子",1,1)
			cm.gainItem(1005319,2,2,2,2,0,0,2,2,0,0,0,0,0,0);//透明脸饰
			cm.gainMeso(-50000000);
            Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(9,cm.getClient().getChannel(),"蛙里奥帽子公告" + " : " + cm.getPlayer().getName() +"蛙里奥帽子合成 全属性50. 收集套装可升级属性",true));
			cm.dispose();
		   }else{
			cm.sendOk("制作失败 材料不足");
			cm.dispose();
		   }
		   		      	      } else if (selection == 20) { //蛙里奥衣服
		if( cm.haveItem(2510019,1) && cm.haveItem(4000247,500)&& cm.haveItem(4000247,500)&& cm.getMeso()>=50000000) {
            cm.gainItem(2510019,-1);//蛙里奥衣服公式
			cm.gainItem(4000247,-500);//青蛙的后腿
			cm.gainItem(4000041,-500);//巫婆的试验用青蛙 
			cm.getPlayer().setBossLog("蛙里奥衣服",1,1)
			cm.gainItem(1053441,2,2,2,2,0,0,2,2,0,0,0,0,0,0);//透明脸饰
			cm.gainMeso(-50000000);
            Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(9,cm.getClient().getChannel(),"蛙里奥衣服公告" + " : " + cm.getPlayer().getName() +"蛙里奥衣服合成 全属性50. 收集套装可升级属性",true));
			cm.dispose();
		   }else{
			cm.sendOk("制作失败 材料不足");
			cm.dispose();
		   }
		   	   		      	      } else if (selection == 21) { //熊宝宝暖帽
		if( cm.haveItem(2510020,1) && cm.haveItem(4000108,500)&& cm.haveItem(4000283,500)&& cm.getMeso()>=50000000) {
            cm.gainItem(2510020,-1);//熊宝宝暖帽公式
			cm.gainItem(4000108,-500);//可爱的熊猫娃娃
			cm.gainItem(4000283,-500);//熊掌 
			cm.getPlayer().setBossLog("熊宝宝暖帽",1,1)
			cm.gainItem(1005113,2,2,2,2,0,0,2,2,0,0,0,0,0,0);//透明脸饰
			cm.gainMeso(-50000000);
            Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(9,cm.getClient().getChannel(),"熊宝宝暖帽公告" + " : " + cm.getPlayer().getName() +"熊宝宝暖帽合成 全属性50. 收集套装可升级属性",true));
			cm.dispose();
		   }else{
			cm.sendOk("制作失败 材料不足");
			cm.dispose();
		   }
		      	   		      	      } else if (selection == 22) { //熊宝宝羽绒服
		if( cm.haveItem(2510021,1) && cm.haveItem(4000108,500)&& cm.haveItem(4000283,500)&& cm.getMeso()>=50000000) {
            cm.gainItem(2510021,-1);//熊宝宝羽绒服公式
			cm.gainItem(4000108,-500);//可爱的熊猫娃娃
			cm.gainItem(4000283,-500);//熊掌 
			cm.getPlayer().setBossLog("熊宝宝羽绒服",1,1)
			cm.gainItem(1053285,2,2,2,2,0,0,2,2,0,0,0,0,0,0);//透明脸饰
			cm.gainMeso(-50000000);
            Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(9,cm.getClient().getChannel(),"熊宝宝羽绒服公告" + " : " + cm.getPlayer().getName() +"熊宝宝羽绒服合成 全属性50. 收集套装可升级属性",true));
			cm.dispose();
		   }else{
			cm.sendOk("制作失败 材料不足");
			cm.dispose();
		   }
		      	   		      	      } else if (selection == 23) { //熊宝宝武器
		if( cm.haveItem(2510022,1) && cm.haveItem(4001241,20)&& cm.haveItem(2210006,20)&& cm.getMeso()>=50000000) {
            cm.gainItem(2510022,-1);//熊宝宝武器公式
			cm.gainItem(4001241,-20);//暴力熊足
			cm.gainItem(2210006,-20);//彩色蜗牛壳
			cm.getPlayer().setBossLog("熊宝宝武器",1,1)
			cm.gainItem(1702365,2,2,2,2,0,0,2,2,0,0,0,0,0,0);//透明脸饰
			cm.gainMeso(-50000000);
            Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(9,cm.getClient().getChannel(),"熊宝宝武器公告" + " : " + cm.getPlayer().getName() +"熊宝宝武器合成 全属性50. 收集套装可升级属性",true));
			cm.dispose();
		   }else{
			cm.sendOk("制作失败 材料不足");
			cm.dispose();
		   }
		      	   		      	      } else if (selection == 24) { //套服
		if(cm.getLevel() >= 10 && cm.haveItem(1902411,1) && cm.getMeso()>=500000) {
            cm.gainItem(1902411,-1);
			cm.gainItem(1902411,188,188,188,188,5000,5000,188,188,0,0,0,0,0,0);
			cm.gainItem(1912411,0,0,0,0,5000,5000,0,0,0,0,0,0,0,0);
			cm.gainMeso(-500000);
            Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(9,cm.getClient().getChannel(),"星球椅子公告" + " : " + cm.getPlayer().getName() +"星球椅子兑换成功.",true));
			cm.dispose();
		   }else{
			cm.sendOk("制作失败 材料不足");
			cm.dispose();
		   }
		        	   		      	      } else if (selection == 25) { //套服
		if(cm.getLevel() >= 10 && cm.haveItem(1902411,1) && cm.getMeso()>=500000) {
            cm.gainItem(1902411,-1);
			cm.gainItem(1902411,188,188,188,188,5000,5000,188,188,0,0,0,0,0,0);
			cm.gainItem(1912411,0,0,0,0,5000,5000,0,0,0,0,0,0,0,0);
			cm.gainMeso(-500000);
            Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(9,cm.getClient().getChannel(),"星球椅子公告" + " : " + cm.getPlayer().getName() +"星球椅子兑换成功.",true));
			cm.dispose();
		   }else{
			cm.sendOk("制作失败 材料不足");
			cm.dispose();
		   } 
        }
    }
}



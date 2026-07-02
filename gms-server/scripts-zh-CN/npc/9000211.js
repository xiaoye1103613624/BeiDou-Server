var 星星 = "#fEffect/CharacterEff/1114000/2/0#";
var 爱心 = "#fEffect/CharacterEff/1022223/4/0#";
var 红色箭头 = "#fEffect/UIWindow/Quest/icon6/7#";
var 正方形 = "#fEffect/UIWindow/Quest/icon3/6#";
var 蓝色箭头 = "#fEffect/UIWindow/Quest/icon2/7#";
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
        
            text += "                  #e#r#v4000463#转生系统#v4000463##l\r\n\r\n"//3
			
			text += "#e#k转生介绍:最多10转#l\r\n\r\n"//3
			
			text += "#e#k转生奖励:[#v1112918#全属性100]#l\r\n\r\n"//3
			
			text += "#e#k当前转生次数"+cm.getPlayer().getReborns()+"/次#l\r\n\r\n"//3
			
			text +="#L1##r我要转生#l   #L2##r转生奖励#l   #L3##r转生挑战#l\r\n"//3
			text +="#L4##d一次转生[转生后变为新手,保留键盘技能]#l\r\n"//3
			text +="#L5##r二次转生奖励#v1902420##v1912420#全属性400#l\r\n"//3
			text +="#L6##k三次转生[转生后变为200级,清空键盘技能]#l\r\n"//3
			text +="#L7##k三次转生奖励:500万破功加入角色里面#l\r\n"//3
			text +="#L8##k四次转生[转生后变为200级,清空键盘技能]#l\r\n"//3
			text +="#L9##k四次转生奖励:#v2022615# #l\r\n"//3
			cm.sendSimple(text);
        } else if (selection == 1) {//红蜗牛王
           if(cm.getPlayer().getReborns()<10 && cm.getChar().getLevel() >= 250&&cm.haveItem(4031454)>=1){
			 
		    p = cm.getChar();
            p.setLevel(200);
            p.levelUp();
            cm.getPlayer().gainReborns(+1);
			cm.gainItem(4031454,-1);
            cm.sendOk("您做得非常好#k,您已经成功降级了！");
			 cm.dispose();
			}else{
            cm.sendOk("您没有达到250级,或者已经超过10次转生了,背包需求#v4031454#!");
            cm.dispose();
			}
         } else if (selection == 2) {//木妖王
             if (cm.getPlayer().getReborns()>=10 && cm.getPlayer().getOneTimeLog("转生奖励1")<1){
				  cm.getPlayer().setOneTimeLog("转生奖励1");
		   
	        cm.gainItem(1112918,100,100,100,100,100,100,100,100,0,0,0,0,0,0);

            cm.sendOk("领取奖励！");
			 cm.dispose();
			}else{
            cm.sendOk("您还没有达到要求10次转生!");
            cm.dispose();
			}
			} else if (selection == 7) {//木妖王
             if (cm.getPlayer().getReborns()>=30 && cm.getPlayer().getOneTimeLog("转生奖励3")<1){
				  cm.getPlayer().setOneTimeLog("转生奖励3");
		   
	      var pogongVipczz=cm.getPlayer().getVipczz();
						 cm.getPlayer().setVipczz(pogongVipczz+500);

            cm.sendOk("领取奖励！");
			 cm.dispose();
			}else{
            cm.sendOk("您还没有达到要求30次转生!");
            cm.dispose();
			}
			} else if (selection == 9) {//木妖王
             if (cm.getPlayer().getReborns()>=50 && cm.getPlayer().getOneTimeLog("转生奖励4")<1){
				  cm.getPlayer().setOneTimeLog("转生奖励4");
		 
	        cm.gainItem(2022615,1);
			cm.gainItem(4030002,1);
            cm.sendOk("领取奖励！");
			 cm.dispose();
			}else{
            cm.sendOk("您还没有达到要求50次转生!");
            cm.dispose();
			}
		} else if (selection == 3) {//木妖王
             if (cm.getChar().getLevel() >= 250&&cm.haveItem(4031454) <1){
				cm.warpParty(910027400,0);
					
					cm.刷新地图();
	        cm.spawnMobOnMap(9300752,1,-277,-387,910027400,1888); // npc实现血量	
            
			 cm.dispose();
			}else{
            cm.sendOk("您还没有达到250级!或者背包里有#v4031454#");
            cm.dispose();
			}
			 } else if (selection == 5) {//木妖王
             if (cm.getPlayer().getReborns()>=20 && cm.getPlayer().getOneTimeLog("转生奖励2")<1){
				  cm.getPlayer().setOneTimeLog("转生奖励2");
		   
	        cm.gainItem(1902420,400,400,400,400,400,400,400,400,0,0,0,0,0,0);
			 cm.gainItem(1912420,400,400,400,400,400,400,400,400,0,0,0,0,0,0);

            cm.sendOk("领取奖励！");
			 cm.dispose();
			}else{
            cm.sendOk("您还没有达到要求20次转生!");
            cm.dispose();
			}
			} else if (selection == 6) {//红蜗牛王
           if(cm.getPlayer().getClient().getChannel()==1 && cm.getPlayer().getReborns()<30 && cm.getPlayer().getReborns()>=20 && cm.getChar().getLevel() >= 250&&cm.getMeso()>=2000000000){
			 
		    p = cm.getChar();
            p.setLevel(200);
            p.levelUp();
            cm.getPlayer().gainReborns(+1);
			cm.gainMeso(-2000000000);
			  cm.getPlayer().cleanKeybinding();
		 cm.getPlayer().changeChannel(2)
            cm.sendOk("您做得非常好#k,您已经成功降级了！");
			 cm.dispose();
			}else{
            cm.sendOk("您没有达到250级,或者已经超过20次转生了,背包需求20e金币!,或者不在1线");
            cm.dispose();
			}
			} else if (selection == 8) {//红蜗牛王
           if(cm.getPlayer().getReborns()<50 && cm.getPlayer().getReborns()>=30 && cm.getChar().getLevel() >= 250&&cm.getMeso()>=2000000000){
			 
		    p = cm.getChar();
            p.setLevel(200);
            p.levelUp();
            cm.getPlayer().gainReborns(+1);
			cm.gainMeso(-2000000000);
			 
            cm.sendOk("您做得非常好#k,您已经成功降级了！");
			 cm.dispose();
			}else{
            cm.sendOk("您没有达到250级,或者已经超过50次转生了,背包需求20e金币!");
            cm.dispose();
			}
			} else if (selection == 4) {//红蜗牛王
           var c = cm.getChar();
			 var oStr = c.getStr();
			 var oDex = c.getDex();
			 var oInt = c.getInt();
			 var oLuk = c.getLuk();
			 var 总属性 = oStr + oDex + oInt + oLuk;
          
			if(cm.getPlayer().getReborns()<10){
				 cm.sendOk("你还没完成10次一阶段转生");
				  cm.dispose();
			 }else if(cm.getLevel() < 250){
				cm.sendOk("转生需要达到250级才可以哦");
				 cm.dispose();
				 }else if(cm.getPlayer().getReborns() >=20){
				cm.sendOk("你已经完成过20次转生了");
				 cm.dispose();
				 }else if(cm.getMeso() <2000000000){
				cm.sendOk("你没有20e金币");
				 cm.dispose();
			 }else if(c.getRemainingAp() > 0){
				// 剩余能力值需要为0 还有没加的需要加完才能转生
				cm.sendOk("请加完所有的能力值之后再来转生");
			 cm.dispose();
			 }else if(oStr >= 32767 && oDex >= 32767 && oInt >= 32767 && oLuk >= 32767){
				 cm.sendOk("所有能力已经达到上限,无法继续转生");
				  cm.dispose();
			 }else {
				 var stat = new java.util.ArrayList();
				
				 c.resetStats(4,4,4,4);
				 cm.getPlayer().setRemainingAp(cm.getPlayer().getReborns() * 200);
				 cm.gainMeso(-2000000000);
				 c.setLevel(2);
				  cm.getPlayer().gainReborns(+1);
				 cm.changeJob(0);
				 cm.刷新状态();
				 cm.喇叭(1,"玩家["+cm.getPlayer().getName()+"]渡劫成功,完成第"+cm.getPlayer().getReborns()+"次转生!大家恭喜TA吧!!!");
				 cm.dispose();
			 }
        }
    }
}

var 最小人数 = 1;
var 最大小人数 = 1;
var 星星 = "#fEffect/CharacterEff/1114000/2/0#";
var 爱心 = "#fEffect/CharacterEff/1022223/4/0#";
var 红色箭头 = "#fEffect/UIWindow/Quest/icon6/7#";
var 正方形 = "#fEffect/UIWindow/Quest/icon3/6#";
var 蓝色箭头 = "#fEffect/UIWindow/Quest/icon2/7#";
var minPartySize = 1;
var maxPartySize = 1;
var minLevel = 200;
var maxLevel = 255;

var em;
var eim ;

function start() {
if (cm.getParty() == null) { // 没有组队
           cm.sendOk("开个单人组吧！  ");
           cm.dispose();
} else {
    status = -1;

    action(1, 0, 0);
}
}
function action(mode, type, selection) {
	em = cm.getEventManager("knsy");//调用事件
	if(em ==null){
		cm.sendOk("脚本错误，请联系管理员");
		cm.dispose();
		return;
	}
    if (mode == -1) {
        cm.dispose();
    }
	
    else {
        if (status >= 0 && mode == 0) {

          //  cm.sendOk("感谢你的光临！");
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
			eim = em.getInstance("BossQuest123")
			
            var text = "";
            text += "   	      #v1142684# ---- #d#e每日试炼之塔#k#n ---- #v1142684# \r\n\r\n#b"
            text += "      本塔每关会召唤4只怪各种材料非常强大的怪物\r\n"
     //       text += " 掉线可重返但必须队友是同时进入,且在里面且同线才可重返\r\n"
            //text += "               你的总通关次数/积分 ："+cm.getBossRank7("每日试炼之塔分数",1)+"\r\n\r\n"
	      if(cm.getBossLog("每日试炼次数") < 1){
            //text += "                     #r今日未挑战\r\n"//text += "本塔每关会召唤8只怪各种材料非常强大的boss请组队进行挑战\r\n\r\n"
            text += "             #L0#" + 蓝色箭头 + "#r挑战每日试炼之塔#l          \r\n\r\n"
 // text += "\r\n                    暂时下架本npc          \r\n\r\n"
		  } else {
            text += "\r\n                   #r今 天 已 挑 战\r\n"		
			cm.dispose(); // 结束对话
			}
         //   text += "               #L1#" + 蓝色箭头 + "通关兑换奖励#l          \r\n\r\n"
			//if(eim !=null){
				//if(eim.getProperty("掉线重返"+cm.getPlayer().getId()) !=null){
				//	if(eim.getProperty("掉线重返"+cm.getPlayer().getId()).equals("1")){
				//		text+="                   #d#L2#掉线重返#l#k\r\n";
				//	}
				//}
		//	}
            cm.sendSimple(text);
		} else if (status == 1) {
			if(selection ==2){
				eim.registerPlayer(cm.getPlayer());
                cm.sendOk("#e#d已为你重返地图");
				cm.dispose();
				return;
			}
            if (selection == 0) {
            var party = cm.getParty().getMembers();
            var inMap = cm.partyMembersInMap();
            var levelValid = 0;
            for (var i = 0; i < party.size(); i++) {
                if (party.get(i).getLevel() >= minLevel && party.get(i).getLevel() <= maxLevel)
                    levelValid++;
            }
            if (inMap < minPartySize || inMap > maxPartySize) {
                cm.sendOk("检测需求1:请确保你的队伍人数"+minPartySize+"人以上.\r\n检测需求2:请把你的队伍人员召集到当前地图才可进入副本.");
                cm.dispose();
             //  } else  if (cm.getPlayer().getLimitBreak() < 1999999) {
				//	cm.sendOk("破功值低于200万无法当队长");
				//	cm.dispose();
         } else if (!cm.getParty() == null) { // 没有组队
           cm.sendOk("开个单人组");
           cm.dispose();
		} else if (cm.getMeso() < 1000){//判断多少金币
			cm.sendOk("#e#r没有1000金币无法为你开启传送大门！\r\n\r\n#e#r每人每日可以进入1次,当前进入次数"+cm.getPlayer().getBossLog('wuxianhuoli')+"");
			cm.dispose();
		//	return;
	//	}
		} else if (cm.getPlayerCount(912010000) > 0){
			cm.sendOk("已经有人挑战你无法进入！请换线尝试");
			cm.dispose();
		//	return;
		//}
		} else if(cm.getLevel() < 249){//判断等级
			cm.sendOk("请达到250级在进入！进入需求:1E5000万金币,999万破功");
			cm.dispose();
			//return;
		//}
		} else if (cm.getPlayer().getDamage() < 99999999) {
					cm.sendOk("请达到250级在进入！进入需求:1E5000万金币,9999万破功");
					cm.dispose();
				//			return;
		//}
		// } else if (cm.getPlayer().getBossLog("转生",1) < 100){
			//cm.sendOk("请修炼到达元婴期进入！进入需求:1E5000万金币,999万破功,");
			cm.dispose();
		 //} else if (cm.getBossLog("转生",1) <0) {
	       // cm.sendOk("修炼到达元婴期进入！进入需求:1E5000万金币,999万破功");
			//cm.dispose();
		 } else if (cm.getParty() == null) { // No Party
			cm.sendOk("你没有队伍无法进入！进入需求:1E5000万金币,999万破功");
			cm.dispose();

		 } else if (!cm.isLeader()) {
			cm.sendOk("请让你的队长和我说话~进入需求:1E5000万金币,999万破功");
			cm.dispose();
          } else if (cm.getBossLog("每日试炼次数")>=1) {  // 没有组队if (cm.isLeader()) { 
            cm.sendOk("你的队友可能有人已经挑战过了,请确认一下。");
            cm.dispose();
           } else if (inMap < 最小人数 || inMap > 最大小人数) {//判断初始地图 队伍的人数，是否匹配限定人数
                cm.sendOk("你的队伍人数必须大于1人以上");
                cm.dispose();
			} else {
            var party = cm.getParty().getMembers();//声明变量 队伍 = party 赋值 party = 获取队伍所有人
            var inMap = cm.partyMembersInMap(); //声明变量 初始地图 = inMap 赋值 inMap = 获取队伍所有人所在地图
            var levelValid = 0;
                var em = cm.getEventManager("knsy");//调用事件
                if (em == null) {//如果事件脚本不存在
                    cm.sendOk("事件发生错误，请联系管理员.");
                    cm.dispose();
            } else {
                    //判断副本地图人数，是否为0
                    if (cm.getPlayerCount(912010000) <= 0) {
                        // cm.getPlayer().setBossLog("试炼之路",0);
                      //  em.startInstance(cm.getParty(), cm.getMap());
                      em.startInstance(cm.getParty(), cm.getPlayer().getMap());//传送队伍进入副本
                      cm.setBossLog("每日试炼次数");
						cm.dispose();
                    } else {
                        cm.sendOk("#e#d提示:#n当前频道有人正在挑战,请更换频道或等待");
                    }
                }
                cm.dispose();
          }
           } else if (selection == 1) {

                cm.dispose();
                //cm.openNpc(9000288,4);
            }
       }
    }
}
var 永恒回忆 = "#fEffect/CharacterEff1.img/QQ1408745/0/13#";
var 蓝心 = "#fEffect/CharacterEff/1022223/4/0#";
var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";//红桃心
var 群粉心 =" "+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+"\r\n";
var 蘑菇 = "#fUI/UIWindow.img/Minigame/Common/mark#";
var 红箭头 = "#fUI/UIWindow.img/Quest/icon9/0#";//红色右箭头
var 蓝箭头 = "#fUI/UIWindow.img/Quest/icon8/0#";//蓝色右箭头
var 选择道具 = "#fUI/UIWindow.img/QuestIcon/3/0#";//选择道具
var 蓝框箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";
var 感叹号 = "#fUI/UIWindow/Quest/icon0#";
var 美化new = "#fUI/UIWindow/Quest/icon5/1#";
var 圆形 = "#fUI/UIWindow/Quest/icon3/6#";
var 黑桃1 = "#fUI/GuildMark/Mark/Pattern/00004018/1#";
var 黑桃2 = "#fUI/GuildMark/Mark/Pattern/00004018/2#";
var 黑桃3 = "#fUI/GuildMark/Mark/Pattern/00004018/3#";
var 黑桃4 = "#fUI/GuildMark/Mark/Pattern/00004018/4#";
var 黑桃5 = "#fUI/GuildMark/Mark/Pattern/00004018/5#";
var 黑桃6 = "#fUI/GuildMark/Mark/Pattern/00004018/6#";
var 黑桃7 = "#fUI/GuildMark/Mark/Pattern/00004018/7#";
var 黑桃8 = "#fUI/GuildMark/Mark/Pattern/00004018/8#";
var 黑桃9 = "#fUI/GuildMark/Mark/Pattern/00004018/9#";
var 黑桃10 = "#fUI/GuildMark/Mark/Pattern/00004018/10#";
var 黑桃11 = "#fUI/GuildMark/Mark/Pattern/00004018/11#";
var 黑桃12 = "#fUI/GuildMark/Mark/Pattern/00004018/12#";
var 黑桃13 = "#fUI/GuildMark/Mark/Pattern/00004018/13#";
var 黑桃14 = "#fUI/GuildMark/Mark/Pattern/00004018/14#";
var 黑桃15 = "#fUI/GuildMark/Mark/Pattern/00004018/15#";
var 黑桃16 = "#fUI/GuildMark/Mark/Pattern/00004018/16#";
var 花花1 = "#fUI/GuildMark/Mark/Pattern/00004020/1#";
var 花花2 = "#fUI/GuildMark/Mark/Pattern/00004020/3#";
var 花花3 = "#fUI/GuildMark/Mark/Pattern/00004020/5#";
var 花花4 = "#fUI/GuildMark/Mark/Pattern/00004020/7#";
var 花花5 = "#fUI/GuildMark/Mark/Pattern/00004020/9#";
var 花花6 = "#fUI/GuildMark/Mark/Pattern/00004020/11#";
var 花花7 = "#fUI/GuildMark/Mark/Pattern/00004020/13#";
var 花花8 = "#fUI/GuildMark/Mark/Pattern/00004020/14#";
var 花花9 = "#fUI/GuildMark/Mark/Pattern/00004020/15#";
var 圆点 = "#fEffect/CharacterEff/1112908/0/1#";  //彩光3
var 小粉心 = "#fEffect/CharacterEff/1062114/1/0#";  //蓝心
var 蓝色角点 = "#fUI/UIWindow.img/PvP/Scroll/enabled/next2#";
var 表情 = "#fUI/GuildBBS.img/GuildBBS/Emoticon/Basic/0#"; 
var 表情1 = "#fUI/GuildBBS.img/GuildBBS/Emoticon/Basic/1#"; 
var 表情2 = "#fUI/GuildBBS.img/GuildBBS/Emoticon/Basic/2#"; 
var 蓝色小兔子 = "#fEffect/CharacterEff.img/1112960/3/1#"; 
var 小红星 = "#fEffect/CharacterEff.img/1112926/0/0#";
var 小蓝星 = "#fEffect/CharacterEff.img/1112925/0/0#";
var 蓝色时钟 = "#fUI/UIWindow.img/Quest/TimeQuest/AlarmClock/default/0/0#";  
var 红色时钟 = "#fUI/UIWindow.img/Quest/TimeQuest/AlarmClock/default/4/0#";  
var 成功了 = "#fEffect/BasicEff.img/Fishing/6#";  
var 经验值 = "#fUI/UIWindow.img/QuestIcon/8/0#";  
var 神秘人 = "#fUI/UIWindow.img/MinigameTable/default#";
var 小黄星 = "#fItem/Etc/0427/04270001/Icon9/0#";  //小黄星
var CDKEY兑换 ="#fEffect/CharacterEff2.img/QQ1408745/0/1#";
var 游戏攻略 ="#fEffect/CharacterEff2.img/QQ1408745/0/0#";

/*if (im.getBossRank6("消费积分",1) == -1) {
       im.setBossRank6("累计积分",1,0);
       im.setBossRank5("消费积分",1,0);
       im.setBossRank1("钓鱼积分1",1,0);
       im.setBossRank7("无尽之塔分数",1,0);
       im.setBossRank2("钓鱼成功次数",1,0);
       im.setBossRank3("破功次数",1,0);
       im.setBossRank2("钓鱼成功次数",1,0);
       im.setBossRank4("每日充值2",1,0);
       im.setBossRank3("每周任务",1,0);
}*/
function start() {
    status = -1;

    action(1, 0, 0);
}
function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (status >= 0 && mode == 0) {

            //cm.sendOk("感谢你的光临！");
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
			/*if(cm.getJob() >= 0 && cm.getJob()<= 522 && cm.hasSkill(1017) == false){
			cm.teachSkill(1017,1,1);
			}else if(cm.getJob() >=1000 || cm.getJob() <= 2112 && cm.hasSkill(20001019) == false){
			cm.teachSkill(20001019,1,1);
			}*/
            var tex2 = "";
            var text = "";
            for (i = 0; i < 10; i++) {
                text += "";
            }

			text += "\t\t\t"+永恒回忆+"\r\n"+群粉心+"";
    text += "  #d" + 花花1 + "玩家名称: #r" + padString(cm.getPlayer().getName(), 12) + "\r\n";
	text += "  #d" + 花花6 + "在线时间: #r" + padString(cm.getPlayer().getTodayOnlineTime() + "#d 分钟", 12) + "  " + 花花4 + "#d赞助余额: #r" + padString(cm.getPlayer().getmoney() + "#d 点", 4) + "\r\n";
    text += "  #d" + 花花4 + "当前元宝: #r" + padString(cm.getmoneyb() + "#d 点", 12) + "   " + 花花5 + "#d当前累计: #r" + padString(cm.getPlayer().getlpjf() + "#d 点", 4) + "\r\n";
    text += "  #d" + 花花7 + "拥有点卷: #r" + padString(cm.getPlayer().getCSPoints(1) + "#d 点", 12) + "   " + 花花9 + "#d当前破功: #r" + padString(cm.getPlayer().getDamage() + "#d 点", 12) + "\r\n"+群粉心+"\r\n";
  
            if (cm.getPlayer().getMapId() != 910000000) {
			text += "#r#L0#"+小黄星+"自由市场"+小黄星+"#l"
		} else {
            text += "#r#L110#"+小黄星+"离线挂机"+小黄星+"#l"
			}
			text +="#n  #L1#"+小黄星+"快捷传送"+小黄星+"#l  #L2#"+小黄星+"去匠人街"+小黄星+"#l\r\n\r\n";
			
			text +="#b#L3#"+小黄星+"快捷商店"+小黄星+"#l  #L4#"+小黄星+"新手礼包"+小黄星+"#l  #L6#"+小黄星+"转职系统"+小黄星+"#l\r\n\r\n";
			
			text +="#d#L7#"+小黄星+"挑战大厅"+小黄星+"#l  #L8#"+小黄星+"每日需要"+小黄星+"#l  #L9#"+小黄星+"随身仓库"+小黄星+"#l\r\n\r\n";

			text +="#d#L10#"+小黄星+"师徒中心"+小黄星+"#l  #L11#"+小黄星+"交易中心"+小黄星+"#l  #d#L12#"+小黄星+"赞助中心"+小黄星+"#l\r\n\r\n";
			
			text +="#d#L13#"+小黄星+"每日任务"+小黄星+"#l  #L14#"+小黄星+"装备分解"+小黄星+"#l  #L15#"+小黄星+"飞升渡劫"+小黄星+"#l\r\n\r\n";
			
			text +="#d#L16#"+小黄星+"怪怪卡片"+小黄星+"#l  #L17#"+小黄星+"成长戒指"+小黄星+"#l  #L18#"+小黄星+"怀旧任务"+小黄星+"#l\r\n\r\n";
			
			text +="#d#L19#"+小黄星+"去匠人街"+小黄星+"#l  #L20#"+小黄星+"查询爆率"+小黄星+"#l  #L21#"+小黄星+"删除物品"+小黄星+"#l\r\n";
			
			//text +="#d#L22#"+小黄星+"材料回收"+小黄星+"#l  #L23#"+小黄星+"装备还原"+小黄星+"#l  #L24#"+小黄星+"飞升渡劫"+小黄星+"#l\r\n";
			
			text +="#k    \r\n\r\n"+群粉心+"";
			
			text +="#L887#"+游戏攻略+"#l#L888#"+CDKEY兑换+"#l\r\n" 
			
			//text +="#k    \r\n"+群粉心+"";
			//text +="#k#L9#"+黑桃13+"吸怪系统"+黑桃13+"#l  #L82#"+黑桃9+"查看爆率"+黑桃9+"#l  #L10#"+黑桃12+"赞助系统"+黑桃12+"#l\r\n\r\n"+群粉心+"";

		    cm.sendSimple(text);
        } else if (selection == 0) {//自由市场
			cm.warp(910000000,0);
			cm.dispose();
		} else if (selection == 110) {
            cm.processCommand("@离线挂机");
			
		} else if (selection == 1) {//快捷传送
            cm.openNpc(9900004,"传送中心");
			
        } else if (selection == 2) {//强化大厅
		    cm.warp(910141010,0);
			cm.dispose();
		
		} else if (selection == 19) {//去匠人街
		    cm.warp(910001000,0);
			cm.dispose();
		
        } else if (selection == 7) {//挑战大厅
			cm.warp(960000000,0);
		    cm.dispose();
			
        } else if (selection == 13) {//任务系统
            cm.openNpc(9000435,0);			
			
		} else if (selection == 4) {//主线任务勋章
            cm.openNpc(9900004, "新手帮助");	

        } else if (selection == 5) {//每日任务 
            cm.openNpc(9900004,"服务系统");
			
        } else if (selection == 6) { //快速转职
            cm.openNpc(9900004,"快速转职");
			
		} else if (selection == 3) { //快捷商店
            cm.openNpc(9000431,"快捷商店");	

		} else if (selection == 9) { //随身仓库
            cm.openNpc(9000431,"仓库");	

		} else if (selection == 10) { //师徒系统
            cm.openNpc(9000431,"推广系统");				
			
		} else if (selection == 11) { //交易中心
            cm.openNpc(9000444,"交易中心");	
		
        } else if (selection == 12) {//赞助系统
            cm.openNpc(9000445,0);	
		
        } else if (selection == 16) {//卡片收集
            cm.openNpc(9900004, "卡片收集");
			
		} else if (selection == 18) { //怀旧任务
            cm.openNpc(9000431,"怀旧任务");				
			
        } else if (selection == 15) {//赞助介绍
            cm.openNpc(9900004, "飞升渡劫");	

		} else if (selection == 8) {//主线任务项链
            cm.openNpc(9900004, "每日需要");
		
		} else if (selection == 17) {//经验戒指导航
            cm.openNpc(9900004, "经验戒指导航");
			
		} else if (selection == 14) {//打金分解
            cm.openNpc(9900004, "打金分解");
			
		} else if (selection == 19) {//技能突破
            cm.openNpc(9900004, "技能突破");			
			
		} else if (selection == 20) {//查询爆率
            cm.openNpc(9900004, "查询爆率");	
		
		} else if (selection == 21) { //删除物品
            cm.openNpc(9000431,"删除物品");		
		
		} else if (selection == 22) {//打金回收
            cm.openNpc(9900004, "打金回收");	
		
		} else if (selection == 23) {//装备还原
            cm.openNpc(9900004, "装备还原");	
		
		} else if (selection == 24) {//重生职业
            cm.openNpc(9900004, "飞升渡劫");
			
			
			
		} else if (selection == 887) {//游戏攻略
            cm.openNpc(9900004, "游戏攻略");

		} else if (selection == 888) {//CDKCZ
            cm.openNpc(9900004, "CDKCZ");

			
	    } else if (selection == 68759) {
            cm.openNpc(9000431,"管理员属性修改");				
			
		}
    }
}
// 自定义 padString 函数：手动填充空格
function padString(str, width) {
    while (str.length < width) {
        str += " "; // 填充空格直到达到指定的宽度
    }
    return str;
}



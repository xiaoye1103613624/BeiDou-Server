/*
QQ327321366
重生职业
*/
var 琴符 = "#fEffect/CharacterEff/1032063/0/0#";
var 音符 = "#fEffect/CharacterEff/1032063/0/0#";
var 表情高兴 = "#fUI/GuildBBS/GuildBBS/Emoticon/Basic/2#";
//道具代码：
var 职业道具 = 3991027;
var 新手道具 = 3991027;

var status = 0;

	function start() {
		status = -1;
		action(1, 0, 0);
		}

	function action(mode, type, selection) {
		if (mode == -1) {
		cm.dispose();
		} else {
		if (status >= 0 && mode == 0) {
		cm.dispose();
		return;
		}
		if (mode == 1)
		status++;
		else
		status--;


	if (status == 0) {
	    var textz ="\r\n\t\t#r#e"+琴符+"【重生职业系统】"+音符+"\r\n";
		textz += "#b转职说明：技能不保留，会清空键盘上所有技能\r\n";
		textz += "#b等级要求：120级,自动把身上的装备脱下\r\n";
		//textz += "#b需要物品：#i4310086:##r#z4310086:##b \r\n";
		textz += "#b需要物品：#r#z4310086:##b \r\n";
		textz += "请选择您想重生的职业：#k#n\r\n";
		textz += "\t\t\t\t#r 【- 冒险家 -】 #k#l\r\n";
		textz += "\t\t  #L1#"+表情高兴+" 重生职业 [英雄] #k#l\r\n";
		textz += "\t\t  #L2#"+表情高兴+" 重生职业 [圣骑士] #k#l#k#l\r\n";
        textz += "\t\t  #L3#"+表情高兴+" 重生职业 [黑骑士] #k#l#k#l\r\n";
		textz += "\t\t  #L4#"+表情高兴+" 重生职业 [火毒魔导师] #k#l#k#l\r\n";
		textz += "\t\t  #L5#"+表情高兴+" 重生职业 [冰雷魔导师] #k#l#k#l\r\n";
        textz += "\t\t  #L6#"+表情高兴+" 重生职业 [主教] #k#l#k#l\r\n";
		textz += "\t\t  #L7#"+表情高兴+" 重生职业 [神射手] #k#l#k#l\r\n";
		textz += "\t\t  #L8#"+表情高兴+" 重生职业 [箭神] #k#l#k#l\r\n";
        textz += "\t\t  #L9#"+表情高兴+" 重生职业 [隐士] #k#l#k#l\r\n";
		textz += "\t\t  #L10#"+表情高兴+" 重生职业 [侠盗] #k#l#k#l\r\n";
		textz += "\t\t  #L11#"+表情高兴+" 重生职业 [冲锋队长] #k#l#k#l\r\n";
        textz += "\t\t  #L12#"+表情高兴+" 重生职业 [船长] #k#l#k#l\r\n\r\n";
	//	textz += "\t\t\t\t#r 【- 骑士团 -】 #k#l\r\n";
	//	textz += "\t\t  #L13#"+表情高兴+" 重生职业 [魂骑士] #k#l#k#l\r\n";
	//	textz += "\t\t  #L14#"+表情高兴+" 重生职业 [炎术士] #k#l#k#l\r\n";
     //   textz += "\t\t  #L15#"+表情高兴+" 重生职业 [风灵使者] #k#l#k#l\r\n";
	//	textz += "\t\t  #L16#"+表情高兴+" 重生职业 [夜行者] #k#l#k#l\r\n";
	//	textz += "\t\t  #L17#"+表情高兴+" 重生职业 [奇袭者] #k#l#k#l\r\n\r\n";
	//	textz += "\t\t\t\t#r 【- 战神 -】 #k#l\r\n";
    //    textz += "\t\t  #L18#"+表情高兴+" 重生职业 [战神] #k#l#k#l\r\n";
		//textz += "\t\t\t\t#r 【- 新手 -】 #k#l\r\n";
		//textz += "\t\t  #L18#"+表情高兴+" 重生职业 [新手] "+表情高兴+"#k#l#k#l\r\n";
		cm.sendSimple (textz);  


} else if (status == 1) {
if (selection == 1) {
/*if (cm.getBossLog('职业') >= 1) {
            cm.sendOk("每天只能兑换1次哦，请明天再来找我吧！");
	    cm.dispose();
        }else{*/
			if (cm.haveItem(4310086,1)){
				if (cm.getLevel() > 119){
				cm.gainItem(4310086,-1);
				cm.unequipEverything(); //脱装备语句
                cm.getPlayer().clearSkills(); //清理技能
                cm.getPlayer().changeJob(112);//新手职业
				cm.getChar().resetStats(4,4,4,4);
				cm.喇叭(2,"恭喜玩家[" + cm.getPlayer().getName() + "]重生职业成功,【英雄】大家一起祝贺他!");
                cm.sendOk("哈哈，恭喜你年轻人，你已经完成了#r重生职业【英雄】#k！\r\n\r\n#e#d");
                //cm.setBossLog('职业');
                cm.dispose();
		}else{
                cm.sendOk("好像你没有达到120级,抱歉,无法操作！");
                cm.dispose();
		 }
            }else{
                cm.sendOk("请把#v4310086##z4310086#1个交给我！");
                cm.dispose();
				}
}else if (selection == 2) {
		 if (cm.haveItem(4310086,1)){
				if (cm.getLevel() > 119){
				cm.gainItem(4310086,-1);
				cm.unequipEverything(); //脱装备语句
                cm.getPlayer().clearSkills(); //清理技能
                cm.getPlayer().changeJob(122);//新手职业
				cm.getChar().resetStats(4,4,4,4);
				cm.喇叭(2,"恭喜玩家[" + cm.getPlayer().getName() + "]重生职业成功,【圣骑士】大家一起祝贺他!");
                cm.sendOk("哈哈，恭喜你年轻人，你已经完成了#r重生职业【圣骑士】#k！\r\n\r\n#e#d");
                cm.dispose();
		}else{
                cm.sendOk("好像你没有达到120级,抱歉,无法操作！");
                cm.dispose();
		 }
            }else{
                cm.sendOk("请把#v4310086##z4310086#1个交给我！");
                cm.dispose();
				}
}else if (selection == 3) {
		 if (cm.haveItem(4310086,1)){
				if (cm.getLevel() > 119){
				cm.gainItem(4310086,-1);
				cm.unequipEverything(); //脱装备语句
                cm.getPlayer().clearSkills(); //清理技能
                cm.getPlayer().changeJob(132);//新手职业
				cm.getChar().resetStats(4,4,4,4);
				cm.喇叭(2,"恭喜玩家[" + cm.getPlayer().getName() + "]重生职业成功,【黑骑士】大家一起祝贺他!");
                cm.sendOk("哈哈，恭喜你年轻人，你已经完成了#r重生职业【黑骑士】#k！\r\n\r\n#e#d");
                cm.dispose();
		}else{
                cm.sendOk("好像你没有达到120级,抱歉,无法操作！");
                cm.dispose();
		 }
            }else{
                cm.sendOk("请把#v4310086##z4310086#1个交给我！");
                cm.dispose();
				}
				}else if (selection == 4) {
		 if (cm.haveItem(4310086,1)){
				if (cm.getLevel() > 119){
				cm.gainItem(4310086,-1);
				cm.unequipEverything(); //脱装备语句
                cm.getPlayer().clearSkills(); //清理技能
                cm.getPlayer().changeJob(212);//新手职业
				cm.getChar().resetStats(4,4,4,4);
				cm.喇叭(2,"恭喜玩家[" + cm.getPlayer().getName() + "]重生职业成功,【火毒魔导师】大家一起祝贺他!");
                cm.sendOk("哈哈，恭喜你年轻人，你已经完成了#r重生职业【火毒魔导师】#k！\r\n\r\n#e#d");
                cm.dispose();
		}else{
                cm.sendOk("好像你没有达到120级,抱歉,无法操作！");
                cm.dispose();
		 }
            }else{
                cm.sendOk("请把#v4310086##z4310086#1个交给我！");
                cm.dispose();
				}
				}else if (selection == 5) {
		 if (cm.haveItem(4310086,1)){
				if (cm.getLevel() > 119){
				cm.gainItem(4310086,-1);
				cm.unequipEverything(); //脱装备语句
                cm.getPlayer().clearSkills(); //清理技能
                cm.getPlayer().changeJob(222);//新手职业
				cm.getChar().resetStats(4,4,4,4);
				cm.喇叭(2,"恭喜玩家[" + cm.getPlayer().getName() + "]重生职业成功,【冰雷魔导师】大家一起祝贺他!");
                cm.sendOk("哈哈，恭喜你年轻人，你已经完成了#r重生职业【冰雷魔导师】#k！\r\n\r\n#e#d");
                cm.dispose();
		}else{
                cm.sendOk("好像你没有达到120级,抱歉,无法操作！");
                cm.dispose();
		 }
            }else{
                cm.sendOk("请把#v4310086##z4310086#1个交给我！");
                cm.dispose();
				}
				}else if (selection == 6) {
		 if (cm.haveItem(4310086,1)){
				if (cm.getLevel() > 119){
				cm.gainItem(4310086,-1);
				cm.unequipEverything(); //脱装备语句
                cm.getPlayer().clearSkills(); //清理技能
                cm.getPlayer().changeJob(232);//新手职业
				cm.getChar().resetStats(4,4,4,4);
				cm.喇叭(2,"恭喜玩家[" + cm.getPlayer().getName() + "]重生职业成功,【主教】大家一起祝贺他!");
                cm.sendOk("哈哈，恭喜你年轻人，你已经完成了#r重生职业【主教】#k！\r\n\r\n#e#d");
                cm.dispose();
		}else{
                cm.sendOk("好像你没有达到120级,抱歉,无法操作！");
                cm.dispose();
		 }
            }else{
                cm.sendOk("请把#v4310086##z4310086#1个交给我！");
                cm.dispose();
				}
				}else if (selection == 7) {
		 if (cm.haveItem(4310086,1)){
				if (cm.getLevel() > 119){
				cm.gainItem(4310086,-1);
				cm.unequipEverything(); //脱装备语句
                cm.getPlayer().clearSkills(); //清理技能
                cm.getPlayer().changeJob(312);//新手职业
				cm.getChar().resetStats(4,4,4,4);
				cm.喇叭(2,"恭喜玩家[" + cm.getPlayer().getName() + "]重生职业成功,【神射手】大家一起祝贺他!");
                cm.sendOk("哈哈，恭喜你年轻人，你已经完成了#r重生职业【神射手】#k！\r\n\r\n#e#d");
                cm.dispose();
		}else{
                cm.sendOk("好像你没有达到120级,抱歉,无法操作！");
                cm.dispose();
		 }
            }else{
                cm.sendOk("请把#v4310086##z4310086#1个交给我！");
                cm.dispose();
				}
				}else if (selection == 8) {
		 if (cm.haveItem(4310086,1)){
				if (cm.getLevel() > 119){
				cm.gainItem(4310086,-1);
				cm.unequipEverything(); //脱装备语句
                cm.getPlayer().clearSkills(); //清理技能
                cm.getPlayer().changeJob(322);//新手职业
				cm.getChar().resetStats(4,4,4,4);
				cm.喇叭(2,"恭喜玩家[" + cm.getPlayer().getName() + "]重生职业成功,【箭神】大家一起祝贺他!");
                cm.sendOk("哈哈，恭喜你年轻人，你已经完成了#r重生职业【箭神】#k！\r\n\r\n#e#d");
                cm.dispose();
		}else{
                cm.sendOk("好像你没有达到120级,抱歉,无法操作！");
                cm.dispose();
		 }
            }else{
                cm.sendOk("请把#v4310086##z4310086#1个交给我！");
                cm.dispose();
				}
				}else if (selection == 9) {
		 if (cm.haveItem(4310086,1)){
				if (cm.getLevel() > 119){
				cm.gainItem(4310086,-1);
				cm.unequipEverything(); //脱装备语句
                cm.getPlayer().clearSkills(); //清理技能
                cm.getPlayer().changeJob(412);//新手职业
				cm.getChar().resetStats(4,4,4,4);
				cm.喇叭(2,"恭喜玩家[" + cm.getPlayer().getName() + "]重生职业成功,【隐士】大家一起祝贺他!");
                cm.sendOk("哈哈，恭喜你年轻人，你已经完成了#r重生职业【隐士】#k！\r\n\r\n#e#d");
                cm.dispose();
		}else{
                cm.sendOk("好像你没有达到120级,抱歉,无法操作！");
                cm.dispose();
		 }
            }else{
                cm.sendOk("请把#v4310086##z4310086#1个交给我！");
                cm.dispose();
				}
				}else if (selection == 10) {
		 if (cm.haveItem(4310086,1)){
				if (cm.getLevel() > 119){
				cm.gainItem(4310086,-1);
				cm.unequipEverything(); //脱装备语句
                cm.getPlayer().clearSkills(); //清理技能
                cm.getPlayer().changeJob(422);//新手职业
				cm.getChar().resetStats(4,4,4,4);
				cm.喇叭(2,"恭喜玩家[" + cm.getPlayer().getName() + "]重生职业成功,【侠盗】大家一起祝贺他!");
                cm.sendOk("哈哈，恭喜你年轻人，你已经完成了#r重生职业【侠盗】#k！\r\n\r\n#e#d");
                cm.dispose();
		}else{
                cm.sendOk("好像你没有达到120级,抱歉,无法操作！");
                cm.dispose();
		 }
            }else{
                cm.sendOk("请把#v4310086##z4310086#1个交给我！");
                cm.dispose();
				}
				}else if (selection == 11) {
		 if (cm.haveItem(4310086,1)){
				if (cm.getLevel() > 119){
				cm.gainItem(4310086,-1);
				cm.unequipEverything(); //脱装备语句
                cm.getPlayer().clearSkills(); //清理技能
                cm.getPlayer().changeJob(512);//新手职业
				cm.getChar().resetStats(4,4,4,4);
				cm.喇叭(2,"恭喜玩家[" + cm.getPlayer().getName() + "]重生职业成功,【冲锋队长】大家一起祝贺他!");
                cm.sendOk("哈哈，恭喜你年轻人，你已经完成了#r重生职业【冲锋队长】#k！\r\n\r\n#e#d");
                cm.dispose();
		}else{
                cm.sendOk("好像你没有达到120级,抱歉,无法操作！");
                cm.dispose();
		 }
            }else{
                cm.sendOk("请把#v4310086##z4310086#1个交给我！");
                cm.dispose();
				}
				}else if (selection == 12) {
		 if (cm.haveItem(4310086,1)){
				if (cm.getLevel() > 119){
				cm.gainItem(4310086,-1);
				cm.unequipEverything(); //脱装备语句
                cm.getPlayer().clearSkills(); //清理技能
                cm.getPlayer().changeJob(522);//新手职业
				cm.getChar().resetStats(4,4,4,4);
				cm.喇叭(2,"恭喜玩家[" + cm.getPlayer().getName() + "]重生职业成功,【船长】大家一起祝贺他!");
                cm.sendOk("哈哈，恭喜你年轻人，你已经完成了#r重生职业【船长】#k！\r\n\r\n#e#d");
                cm.dispose();
		}else{
                cm.sendOk("好像你没有达到120级,抱歉,无法操作！");
                cm.dispose();
		 }
            }else{
                cm.sendOk("请把#v4310086##z4310086#1个交给我！");
                cm.dispose();
				}
				}else if (selection == 13) {
		 if (cm.haveItem(4310086,1)){
				if (cm.getLevel() > 119){
				cm.gainItem(4310086,-1);
				cm.unequipEverything(); //脱装备语句
                cm.getPlayer().clearSkills(); //清理技能
                cm.getPlayer().changeJob(1111);//新手职业
				cm.getChar().resetStats(4,4,4,4);
				cm.喇叭(2,"恭喜玩家[" + cm.getPlayer().getName() + "]重生职业成功,【魂骑士】大家一起祝贺他!");
                cm.sendOk("哈哈，恭喜你年轻人，你已经完成了#r重生职业【魂骑士】#k！\r\n\r\n#e#d");
                cm.dispose();
		}else{
                cm.sendOk("好像你没有达到120级,抱歉,无法操作！");
                cm.dispose();
		 }
            }else{
                cm.sendOk("请把#v4310086##z4310086#1个交给我！");
                cm.dispose();
				}
				}else if (selection == 14) {
		 if (cm.haveItem(4310086,1)){
				if (cm.getLevel() > 119){
				cm.gainItem(4310086,-1);
				cm.unequipEverything(); //脱装备语句
                cm.getPlayer().clearSkills(); //清理技能
                cm.getPlayer().changeJob(1211);//新手职业
				cm.getChar().resetStats(4,4,4,4);
				cm.喇叭(2,"恭喜玩家[" + cm.getPlayer().getName() + "]重生职业成功,【炎术士】大家一起祝贺他!");
                cm.sendOk("哈哈，恭喜你年轻人，你已经完成了#r重生职业【炎术士】#k！\r\n\r\n#e#d");
                cm.dispose();
		}else{
                cm.sendOk("好像你没有达到120级,抱歉,无法操作！");
                cm.dispose();
		 }
            }else{
                cm.sendOk("请把#v4310086##z4310086#1个交给我！");
                cm.dispose();
				}
				}else if (selection == 15) {
		 if (cm.haveItem(4310086,1)){
				if (cm.getLevel() > 119){
				cm.gainItem(4310086,-1);
				cm.unequipEverything(); //脱装备语句
                cm.getPlayer().clearSkills(); //清理技能
                cm.getPlayer().changeJob(1311);//新手职业
				cm.getChar().resetStats(4,4,4,4);
				cm.喇叭(2,"恭喜玩家[" + cm.getPlayer().getName() + "]重生职业成功,【风灵使者】大家一起祝贺他!");
                cm.sendOk("哈哈，恭喜你年轻人，你已经完成了#r重生职业【风灵使者】#k！\r\n\r\n#e#d");
                cm.dispose();
		}else{
                cm.sendOk("好像你没有达到120级,抱歉,无法操作！");
                cm.dispose();
		 }
            }else{
                cm.sendOk("请把#v4310086##z4310086#1个交给我！");
                cm.dispose();
				}
				}else if (selection == 16) {
		 if (cm.haveItem(4310086,1)){
				if (cm.getLevel() > 119){
				cm.gainItem(4310086,-1);
				cm.unequipEverything(); //脱装备语句
                cm.getPlayer().clearSkills(); //清理技能
                cm.getPlayer().changeJob(1411);//新手职业
				cm.getChar().resetStats(4,4,4,4);
				cm.喇叭(2,"恭喜玩家[" + cm.getPlayer().getName() + "]重生职业成功,【夜行者】大家一起祝贺他!");
                cm.sendOk("哈哈，恭喜你年轻人，你已经完成了#r重生职业【夜行者】#k！\r\n\r\n#e#d");
                cm.dispose();
		}else{
                cm.sendOk("好像你没有达到120级,抱歉,无法操作！");
                cm.dispose();
		 }
            }else{
                cm.sendOk("请把#v4310086##z4310086#1个交给我！");
                cm.dispose();
				}
				}else if (selection == 17) {
		 if (cm.haveItem(4310086,1)){
				if (cm.getLevel() > 119){
				cm.gainItem(4310086,-1);
				cm.unequipEverything(); //脱装备语句
                cm.getPlayer().clearSkills(); //清理技能
                cm.getPlayer().changeJob(1511);//新手职业
				cm.getChar().resetStats(4,4,4,4);
				cm.喇叭(2,"恭喜玩家[" + cm.getPlayer().getName() + "]重生职业成功,【奇袭者】大家一起祝贺他!");
                cm.sendOk("哈哈，恭喜你年轻人，你已经完成了#r重生职业【奇袭者】#k！\r\n\r\n#e#d");
                cm.dispose();
		}else{
                cm.sendOk("好像你没有达到120级,抱歉,无法操作！");
                cm.dispose();
		 }
            }else{
                cm.sendOk("请把#v4310086##z4310086#1个交给我！");
                cm.dispose();
				}
				}else if (selection == 18) {
		 if (cm.haveItem(4310086,1)){
				if (cm.getLevel() > 119){
				cm.gainItem(4310086,-1);
				cm.unequipEverything(); //脱装备语句
                cm.getPlayer().clearSkills(); //清理技能
                cm.getPlayer().changeJob(2112);//新手职业
				cm.getChar().resetStats(4,4,4,4);
				cm.喇叭(2,"恭喜玩家[" + cm.getPlayer().getName() + "]重生职业成功,【战神】大家一起祝贺他!");
                cm.sendOk("哈哈，恭喜你年轻人，你已经完成了#r重生职业【战神】#k！\r\n\r\n#e#d");
                cm.dispose();
		}else{
                cm.sendOk("好像你没有达到120级,抱歉,无法操作！");
                cm.dispose();
		 }
            }else{
                cm.sendOk("请把#v4310086##z4310086#1个交给我！");
                cm.dispose();
				}
				}
}
}
}
var aaa ="#fUI/UIWindow.img/PvP/Scroll/enabled/next2#";

var status = 0;
var typed=0;
var rmb = 0;
var Gift = "#fUI/UIWindow2/crossHunterUI/reward/button/normal/0#";

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		im.dispose();
	} else {
		if (mode == 0 && status == 0) {
			cm.dispose();
			return;
		}
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
			var selStr = "#d#e请选择您所需要学习的32点技能：50%成功率#n#k\r\n\r\n";
            var dabai = cm.getJob()
			if(dabai ==112||dabai ==111||dabai ==110){
			selStr +=" 英雄技能-2级技能点突破#k\r\n";
			selStr +="#L2#"+aaa+" 学习 1 个#s1121008##l#k\r\n\r\n";
			}
			if(dabai ==122||dabai ==121||dabai ==120){
			selStr +=" 圣骑技能-2级技能点突破#k\r\n";
			selStr +="#L3#"+aaa+" 学习 1 个#s1221009##l#k\r\n\r\n";
			}
			if(dabai ==132||dabai ==131||dabai ==130){
			selStr +=" 黑骑技能-2级技能点突破#k\r\n";
			selStr +="#L5#"+aaa+" 学习 1 个#s1320006##l#k\r\n\r\n";
			}
			if(dabai ==212||dabai ==211||dabai ==210){
			selStr +=" 火毒技能-2级技能点突破#k\r\n";
			selStr +="#L7#"+aaa+" 学习 1 个#s2121006##l#k\r\n\r\n";
			}
			if(dabai ==222||dabai ==221||dabai ==220){
			selStr +=" 冰雷技能-2级技能点突破#k\r\n";
			selStr +="#L8#"+aaa+" 学习 1 个#s2221006##l#k\r\n\r\n";
			}
			if(dabai ==232||dabai ==231||dabai ==220){
			selStr +=" 主教技能-2级技能点突破#k\r\n";
			selStr +="#L10#"+aaa+" 学习 1 个#s2311004##l#k\r\n\r\n";
			}
			if(dabai ==412||dabai ==411||dabai ==410){
			selStr +=" 飞侠技能-2级技能点突破#k\r\n";
			selStr +="#L12#"+aaa+" 学习 1 个#s4121007##l#k\r\n\r\n";
			}
			if(dabai ==422||dabai ==421||dabai ==420){
			selStr +=" 刀飞技能-2级技能点突破#k\r\n";
			selStr +="#L14#"+aaa+" 学习 1 个#s4220002##l#k\r\n\r\n";
			}
			if(dabai ==312||dabai ==311||dabai ==310){
			selStr +=" 弓手技能-2级技能点突破#k\r\n";
			selStr +="#L15#"+aaa+" 学习 1 个#s3121004##l#k\r\n";
			}
			if(dabai ==322||dabai ==321||dabai ==320){
			selStr +="\r\n 弩手技能-2级技能点突破#k\r\n";
			selStr +="#L17#"+aaa+" 学习 1 个#s3221002##l#k\r\n\r\n";
			}
			if(dabai ==512||dabai ==511||dabai ==510){
			selStr +="\r\n 队长技能-2级技能点突破#k\r\n";
			selStr +="#L24#"+aaa+" 学习 1 个#s5121004##l#k\r\n\r\n";
			}
			if(dabai ==522||dabai ==521||dabai ==520){
			selStr +=" 船长技能-2级技能点突破#k\r\n";
			selStr +="#L25#"+aaa+" 学习 1 个#s5221007##l#k\r\n\r\n";
			}
            cm.sendSimple(selStr);	
		} else if (status == 1) {
			if (selection == 2) {
				typed=2;
				cm.sendYesNo("#b您是否想要学习32点#s1121008# 需要支付1000W金币  您是否想要学习？");
				
			} else if (selection == 3) {
				typed=3;
				cm.sendYesNo("#b您是否想要学习32点#s1221009#   您是否想要学习？");
				
			} else if (selection == 5) {
				typed=5;
				cm.sendYesNo("#b您是否想要学习32点#s1320006# 需要支付1000W金币  您是否想要学习？");
				
			} else if (selection == 7) {
				typed=7;
				cm.sendYesNo("#b您是否想要学习32点#s2121006# 需要支付1000W金币  您是否想要学习？");
			} else if (selection == 8) {
				typed=8;
				cm.sendYesNo("#b您是否想要学习32点#s2221006# 需要支付1000W金币  您是否想要学习？");
				
			} else if (selection == 10) {
				typed=10;
				cm.sendYesNo("#b您是否想要学习32点#s2311004# ？  您是否想要学习？");
				
			} else if (selection == 12) {
				typed=12;
				cm.sendYesNo("#b您是否想要学习32点#s4121007# 需要支付1000W金币  您是否想要学习？");
				
			} else if (selection == 14) {
				typed=14;
				cm.sendYesNo("#b您是否想要学习32点#s4220002# 需要支付1000W金币  您是否想要学习？");
			} else if (selection == 15) {
				typed=15;
				cm.sendYesNo("#b您是否想要学习32点#s3121004# 需要支付1000W金币  您是否想要学习？");
				
			} else if (selection == 17) {
				typed=17;
				cm.sendYesNo("#b您是否想要学习32点#s3221002# 需要支付1000W金币  您是否想要学习？");
				
			} else if (selection == 24) {
				typed=24;
				cm.sendYesNo("#b您是否想要学习32点#s5121004# 需要支付1000W金币  您是否想要学习？");
			} else if (selection == 25) {
				typed=25;
				cm.sendYesNo("#b您是否想要学习32点#s5221007# 需要支付1000W金币 您是否想要学习？");
				
			}
		} else if (status == 2) {
			if (typed==2) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
							} else	if (cm.getPlayer().getOneTimeLog("qingwu") < 1) {
					cm.sendOk("你还没突破该技能的第一点");
					cm.dispose();
			} else if (!cm.haveItem(4460014, 0)) {
				cm.sendOk("你没有该技能的突破书");
				cm.dispose();
			} else 	if (xxx > 40  ) {
					cm.gainItem(4460014, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.teachSkill(1121008, 32, 32);
					cm.teachSkill(1121008, cm.getPlayer().getSkillLevel(1121008),32);
					cm.gainMeso(- 10000000 );
					//cm.gainItem(4460014, -1);
					cm.sendOk("恭喜您成功学习了#s1121008#.");
					cm.dispose();
				}
			} else if (typed==3) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
							} else	if (cm.getPlayer().getOneTimeLog("lianhuanhuanpo") < 1) {
					cm.sendOk("你还没突破该技能的第一点");
					cm.dispose();
			} else if (!cm.haveItem(4460016, 0)) {
				cm.sendOk("你没有该技能的突破书");
				cm.dispose();
			} else 	if (xxx > 40  ) {
					cm.gainItem(4460016, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(1221009,32)
					cm.teachSkill(1221009, cm.getPlayer().getSkillLevel(1221009),32);
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460016, -1);
					cm.sendOk("恭喜您成功学习了#s1221009#.");
					cm.dispose();
				}
			
			} else if (typed==5) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
							} else	if (cm.getPlayer().getOneTimeLog("elong") < 1) {
					cm.sendOk("你还没突破该技能的第一点");
					cm.dispose();
			} else if (!cm.haveItem(4460069, 0)) {
				cm.sendOk("你没有该技能的突破书");
				cm.dispose();
			} else 	if (xxx > 40  ) {
					cm.gainItem(4460069, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(1320006,32)
					cm.teachSkill(1320006, cm.getPlayer().getSkillLevel(1320006),32);
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460069, -1);
					cm.sendOk("恭喜您成功学习了#s1320006#.");
					cm.dispose();
				}
			
			} else if (typed==7) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
							} else	if (cm.getPlayer().getOneTimeLog("baopo") < 1) {
					cm.sendOk("你还没突破该技能的第一点");
					cm.dispose();
			} else if (!cm.haveItem(4460023, 0)) {
				cm.sendOk("你没有该技能的突破书");
				cm.dispose();
			} else 	if (xxx > 40  ) {
					cm.gainItem(4460023, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(2121006,32)
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460023, -1);
					cm.sendOk("恭喜您成功学习了#s2121006#.");
					cm.dispose();
				}
				
			} else if (typed==8) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
							} else	if (cm.getPlayer().getOneTimeLog("shandian") < 1) {
					cm.sendOk("你还没突破该技能的第一点");
					cm.dispose();
			} else if (!cm.haveItem(4460025, 0)) {
				cm.sendOk("你没有该技能的突破书");
				cm.dispose();
			} else 	if (xxx > 40  ) {
					cm.gainItem(4460025, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(2221006,32)
					cm.teachSkill(2221006, cm.getPlayer().getSkillLevel(2221006),32);
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460025, -1);
					cm.sendOk("恭喜您成功学习了#s2221006#.");
					cm.dispose();
				}
			
			} else if (typed==10) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
							} else	if (cm.getPlayer().getOneTimeLog("shengguang") < 1) {
					cm.sendOk("你还没突破该技能的第一点");
					cm.dispose();
			} else if (!cm.haveItem(4460029, 0)) {
				cm.sendOk("你没有该技能的突破书");
				cm.dispose();
			} else 	if (xxx > 40  ) {
					cm.gainItem(4460029, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(2311004,32)
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460029, -1);
					cm.sendOk("恭喜您成功学习了#s2311004#.");
					cm.dispose();
				}
			
			} else if (typed==12) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
							} else	if (cm.getPlayer().getOneTimeLog("sanlianhuan") < 1) {
					cm.sendOk("你还没突破该技能的第一点");
					cm.dispose();
			} else if (!cm.haveItem(4460033, 0)) {
				cm.sendOk("你没有该技能的突破书");
				cm.dispose();
			} else 	if (xxx > 40  ) {
					cm.gainItem(4460033, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(4121007,32)
					cm.teachSkill(4121007, cm.getPlayer().getSkillLevel(4121007),32);
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460033, -1);
					cm.sendOk("恭喜您成功学习了#s4121007#.");
					cm.dispose();
				}
			
			} else if (typed==14) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else	if (cm.getPlayer().getOneTimeLog("jiadongzuo") < 1) {
					cm.sendOk("你还没突破该技能的第一点");
					cm.dispose();
			} else if (!cm.haveItem(4460037, 0)) {
				cm.sendOk("你没有该技能的突破书");
				cm.dispose();
			} else 	if (xxx > 40  ) {
					cm.gainItem(4460037, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(4220002,32)
					cm.teachSkill(4220002, cm.getPlayer().getSkillLevel(4220002),32);
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460037, -1);
					cm.sendOk("恭喜您成功学习了#s4220002#.");
					cm.dispose();
				}
			} else if (typed==15) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else	if (cm.getPlayer().getOneTimeLog("baofeng") < 1) {
					cm.sendOk("你还没突破该技能的第一点");
					cm.dispose();
			} else if (!cm.haveItem(4460039, 0)) {
				cm.sendOk("你没有该技能的突破书");
				cm.dispose();
			} else 	if (xxx > 40  ) {
					cm.gainItem(4460039, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(3121004,32)
					cm.teachSkill(3121004, cm.getPlayer().getSkillLevel(3121004),32);
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460039, -1);
					cm.sendOk("恭喜您成功学习了#s3121004#.");
					cm.dispose();
				}
			
			} else if (typed==17) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else	if (cm.getPlayer().getOneTimeLog("nuhuoyan") < 1) {
					cm.sendOk("你还没突破该技能的第一点");
					cm.dispose();
			} else if (!cm.haveItem(4460043, 0)) {
				cm.sendOk("你没有该技能的突破书");
				cm.dispose();
			} else 	if (xxx > 40  ) {
					cm.gainItem(4460043, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(3221002,32)
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460043, -1);
					cm.sendOk("恭喜您成功学习了#s3221002#.");
					cm.dispose();
				}
				
			} else if (typed==24) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
				} else	if (cm.getPlayer().getOneTimeLog("suoming") < 1) {
					cm.sendOk("你还没突破该技能的第一点");
					cm.dispose();
			} else if (!cm.haveItem(4460057, 0)) {
				cm.sendOk("你没有该技能的突破书");
				cm.dispose();
			} else 	if (xxx > 40  ) {
					cm.gainItem(4460057, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(5121004,32)
					cm.teachSkill(5121004, cm.getPlayer().getSkillLevel(5121004),32);
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460057, -1);
					cm.sendOk("恭喜您成功学习了#s5121004#.");
					cm.dispose();
				}
			} else if (typed==25) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else if (!cm.haveItem(4460059, 0)) {
				cm.sendOk("你没有该技能的突破书");
				cm.dispose();
				} else	if (cm.getPlayer().getOneTimeLog("jinshu") < 1) {
					cm.sendOk("你还没突破该技能的第一点");
					cm.dispose();
			} else 	if (xxx > 40  ) {
					cm.gainItem(4460059, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(5221007,32)
										cm.teachSkill(5221007, cm.getPlayer().getSkillLevel(5221007),32);
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460059, -1);
					cm.sendOk("恭喜您成功学习了#s5221007#.");
					cm.dispose();
				}
			
           }
		}
	}
  }
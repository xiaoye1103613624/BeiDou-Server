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
			var selStr = "#d#e请选择您所需要学习的31点技能：50%成功率#n#k\r\n\r\n";
			selStr +=" 英雄技能-技能点突破#k\r\n";
			//selStr +="#L26#"+aaa+" 学习 1 个#s1101004##l#k\r\n";
			//selStr +="#L1#"+aaa+" 学习 1 个#s1111008##l#k\r\n";
			selStr +="#L40#"+aaa+" 学习 1 个#s1121002##l#k\r\n";
			selStr +="#L43#"+aaa+" 学习 1 个#s1120003##l#k\r\n";
			selStr +="#L2#"+aaa+" 学习 1 个#s1121008##l#k\r\n\r\n";
			selStr +=" 圣骑技能-技能点突破#k\r\n";
			//selStr +="#L27#"+aaa+" 学习 1 个#s1201004##l#k\r\n";
			selStr +="#L41#"+aaa+" 学习 1 个#s1221002##l#k\r\n";
			selStr +="#L44#"+aaa+" 学习 1 个#s1221003##l#k\r\n";
			//selStr +="#L50#"+aaa+" 学习 1 个#s1001005##l#k\r\n";
			selStr +="#L3#"+aaa+" 学习 1 个#s1221009##l#k\r\n\r\n";
			selStr +=" 黑骑技能-技能点突破#k\r\n";
			//selStr +="#L28#"+aaa+" 学习 1 个#s1301004##l#k\r\n";
			//selStr +="#L4#"+aaa+" 学习 1 个#s1311001##l#k\r\n";
			selStr +="#L42#"+aaa+" 学习 1 个#s1321002##l#k\r\n";
			selStr +="#L5#"+aaa+" 学习 1 个#s1320006##l#k\r\n\r\n";
			selStr +=" 火毒技能-技能点突破#k\r\n";
			//selStr +="#L29#"+aaa+" 学习 1 个#s2101001##l#k\r\n";
			//selStr +="#L6#"+aaa+" 学习 1 个#s2111005##l#k\r\n";
			//selStr +="#L45#"+aaa+" 学习 1 个#s2110001##l#k\r\n";
			selStr +="#L7#"+aaa+" 学习 1 个#s2121006##l#k\r\n\r\n";
			selStr +=" 冰雷技能-技能点突破#k\r\n";
			//selStr +="#L30#"+aaa+" 学习 1 个#s2201001##l#k\r\n";
			selStr +="#L8#"+aaa+" 学习 1 个#s2221006##l#k\r\n\r\n";
			//selStr +=" 主教技能-1级技能点突破#k\r\n";
			//selStr +="#L31#"+aaa+" 学习 1 个#s2301005##l#k\r\n";
			//selStr +="#L9#"+aaa+" 学习 1 个#s2301003##l#k\r\n";
			//selStr +="#L10#"+aaa+" 学习 1 个#s2311004##l#k\r\n\r\n";
			selStr +=" 飞侠技能-1级技能点突破#k\r\n";
			//selStr +="#L32#"+aaa+" 学习 1 个#s4100001##l#k\r\n";
			//selStr +="#L11#"+aaa+" 学习 1 个#s4111002##l#k\r\n";
			selStr +="#L12#"+aaa+" 学习 1 个#s4121007##l#k\r\n\r\n";
			selStr +=" 刀飞技能-1级技能点突破#k\r\n";
			//selStr +="#L33#"+aaa+" 学习 1 个#s4201005##l#k\r\n";
			selStr +="#L13#"+aaa+" 学习 1 个#s4221001##l#k\r\n";
			selStr +="#L14#"+aaa+" 学习 1 个#s4220002##l#k\r\n\r\n";
			selStr +=" 弓手技能-1级技能点突破#k\r\n";
			//selStr +="#L34#"+aaa+" 学习 1 个#s3000001##l#k\r\n";
			selStr +="#L15#"+aaa+" 学习 1 个#s3121004##l#k\r\n";
			selStr +="#L16#"+aaa+" 学习 1 个#s3121002##l#k\r\n\r\n";
		//	selStr +=" 双刀技能-1级技能点突破#k\r\n";
		//	selStr +="#L22#"+aaa+" 学习 1 个#s4331002##l#k\r\n";
		//	selStr +="#L23#"+aaa+" 学习 1 个#s4311002##l#k\r\n\r\n";
			selStr +="\r\n 弩手技能-1级技能点突破#k\r\n";
			//selStr +="#L35#"+aaa+" 学习 1 个#s3000001##l#k\r\n";
			selStr +="#L17#"+aaa+" 学习 1 个#s3221002##l#k\r\n";
			//selStr +="#L18#"+aaa+" 学习 1 个#s3211006##l#k\r\n\r\n";
			//selStr +=" 战神技能-1级技能点突破#k\r\n";
			//selStr +="#L36#"+aaa+" 学习 1 个#s21100001##l#k\r\n";
			//selStr +="#L19#"+aaa+" 学习 1 个#s21120005##l#k\r\n\r\n";
			selStr +="\r\n 队长技能-1级技能点突破#k\r\n";
			//selStr +="#L38#"+aaa+" 学习 1 个#s5101006##l#k\r\n";
			selStr +="#L24#"+aaa+" 学习 1 个#s5121004##l#k\r\n\r\n";
			selStr +=" 船长技能-1级技能点突破#k\r\n";
			//selStr +="#L39#"+aaa+" 学习 1 个#s5201003##l#k\r\n";
			selStr +="#L25#"+aaa+" 学习 1 个#s5221007##l#k\r\n\r\n";
		//	selStr +=" 龙神技能-1级技能点突破#k\r\n";
			//selStr +="#L37#"+aaa+" 学习 1 个#s22111001##l#k\r\n";
			//selStr +="#L20#"+aaa+" 学习 1 个#s22171002##l#k\r\n";
		//	selStr +="#L21#"+aaa+" 学习 1 个#s22141002##l#k\r\n\r\n";
                        cm.sendSimple(selStr);	
		} else if (status == 1) {
			if (selection == 1) {
				typed=1;
				cm.sendYesNo("#b您是否想要学习31点#s1111008#  需要支付1000W金币 您是否想要学习？");
			} else if (selection == 2) {
				typed=2;
				cm.sendYesNo("#b您是否想要学习31点#s1121008#  需要支付1000W金币 您是否想要学习？");
			} else if (selection == 3) {
				typed=3;
				cm.sendYesNo("#b您是否想要学习31点#s1221009#  需要支付1000W金币 您是否想要学习？");
			} else if (selection == 4) {
				typed=4;
				cm.sendYesNo("#b您是否想要学习31点#s1311001#  需要支付1000W金币 您是否想要学习？");
			} else if (selection == 5) {
				typed=5;
				cm.sendYesNo("#b您是否想要学习31点#s1320006#  需要支付1000W金币 您是否想要学习？");
			} else if (selection == 6) {
				typed=6;
				cm.sendYesNo("#b您是否想要学习21点#s2111005#  需要支付1000W金币 您是否想要学习？");
			} else if (selection == 7) {
				typed=7;
				cm.sendYesNo("#b您是否想要学习31点#s2121006#  需要支付1000W金币 您是否想要学习？");
			} else if (selection == 8) {
				typed=8;
				cm.sendYesNo("#b您是否想要学习31点#s2221006#  需要支付1000W金币 您是否想要学习？");
			} else if (selection == 9) {
				typed=9;
				cm.sendYesNo("#b您是否想要学习31点#s2301003# ？ 需要支付1000W金币 您是否想要学习？");
			} else if (selection == 10) {
				typed=10;
				cm.sendYesNo("#b您是否想要学习31点#s2311004# ？ 需要支付1000W金币 您是否想要学习？");
			} else if (selection == 11) {
				typed=11;
				cm.sendYesNo("#b您是否想要学习31点#s4111002# ？ 需要支付1000W金币 您是否想要学习？");
			} else if (selection == 12) {
				typed=12;
				cm.sendYesNo("#b您是否想要学习31点#s4121007# ？ 需要支付1000W金币 您是否想要学习？");
			} else if (selection == 13) {
				typed=13;
				cm.sendYesNo("#b您是否想要学习31点#s4221001#  需要支付1000W金币 您是否想要学习？");
			} else if (selection == 14) {
				typed=14;
				cm.sendYesNo("#b您是否想要学习31点#s4220002# 需要支付1000W金币  您是否想要学习？");
			} else if (selection == 15) {
				typed=15;
				cm.sendYesNo("#b您是否想要学习31点#s3121004#  需要支付1000W金币 您是否想要学习？");
			} else if (selection == 16) {
				typed=16;
				cm.sendYesNo("#b您是否想要学习31点#s3121002#  需要支付1000W金币 您是否想要学习？");
			} else if (selection == 17) {
				typed=17;
				cm.sendYesNo("#b您是否想要学习31点#s3221002#  需要支付1000W金币 您是否想要学习？");
			} else if (selection == 18) {
				typed=18;
				cm.sendYesNo("#b您是否想要学习31点#s3211006#  需要支付1000W金币 您是否想要学习？");
			} else if (selection == 19) {
				typed=19;
				cm.sendYesNo("#b您是否想要学习31点#s21120005#  需要支付1000W金币 您是否想要学习？");
			} else if (selection == 20) {
				typed=20;
				cm.sendYesNo("#b您是否想要学习31点#s22171002# 需要支付1000W金币  您是否想要学习？");
			} else if (selection == 21) {
				typed=21;
				cm.sendYesNo("#b您是否想要学习16点#s22141002#  需要支付1000W金币 您是否想要学习？");
			} else if (selection == 22) {
				typed=22;
				cm.sendYesNo("#b您是否想要学习31点#s4331002#  需要支付1000W金币 您是否想要学习？");
			} else if (selection == 23) {
				typed=23;
				cm.sendYesNo("#b您是否想要学习21点#s4311002# 需要支付1000W金币 您是否想要学习？");
			} else if (selection == 24) {
				typed=24;
				cm.sendYesNo("#b您是否想要学习31点#s5121004#  需要支付1000W金币 您是否想要学习？");
			} else if (selection == 25) {
				typed=25;
				cm.sendYesNo("#b您是否想要学习31点#s5221007# 需要支付1000W金币  您是否想要学习？");
			} else if (selection == 26) {
				typed=26;
				cm.sendYesNo("#b您是否想要学习21点#s1101004#  需要支付1000W金币 您是否想要学习？");
			} else if (selection == 27) {
				typed=27;
				cm.sendYesNo("#b您是否想要学习21点#s1201004#  需要支付1000W金币 您是否想要学习？");
			} else if (selection == 28) {
				typed=28;
				cm.sendYesNo("#b您是否想要学习21点#s1301004#  需要支付1000W金币 您是否想要学习？");
			} else if (selection == 29) {
				typed=29;
				cm.sendYesNo("#b您是否想要学习21点#s2101001#  需要支付1000W金币 您是否想要学习？");
			} else if (selection == 30) {
				typed=30;
				cm.sendYesNo("#b您是否想要学习21点#s2201001#  需要支付1000W金币 您是否想要学习？");
			} else if (selection == 31) {
				typed=31;
				cm.sendYesNo("#b您是否想要学习31点#s2301005#  需要支付1000W金币 您是否想要学习？");
			} else if (selection == 32) {
				typed=32;
				cm.sendYesNo("#b您是否想要学习31点#s4100001#  需要支付1000W金币 您是否想要学习？");
			} else if (selection == 33) {
				typed=33;
				cm.sendYesNo("#b您是否想要学习31点#s4201005#  需要支付1000W金币 您是否想要学习？");
			} else if (selection == 34) {
				typed=34;
				cm.sendYesNo("#b您是否想要学习21点#s3000001#  需要支付1000W金币 您是否想要学习？");
			} else if (selection == 35) {
				typed=35;
				cm.sendYesNo("#b您是否想要学习21点#s3000001#  需要支付1000W金币 您是否想要学习？");
			} else if (selection == 36) {
				typed=36;
				cm.sendYesNo("#b您是否想要学习21点#s21100001#  需要支付1000W金币 您是否想要学习？");
			} else if (selection == 37) {
				typed=37;
				cm.sendYesNo("#b您是否想要学习21点#s22111001#  需要支付1000W金币 您是否想要学习？");
			} else if (selection == 38) {
				typed=38;
				cm.sendYesNo("#b您是否想要学习21点#s5101006#  需要支付1000W金币 您是否想要学习？");
			} else if (selection == 39) {
				typed=39;
				cm.sendYesNo("#b您是否想要学习21点#s5201003#  需要支付1000W金币 您是否想要学习？");
			} else if (selection == 40) {
				typed=40;
				cm.sendYesNo("#b您是否想要学习31点#s1121002#  需要支付1000W金币  您是否想要学习？");
			} else if (selection == 41) {
				typed=41;
				cm.sendYesNo("#b您是否想要学习31点#s1221002#  需要支付1000W金币  您是否想要学习？");
			} else if (selection == 42) {
				typed=42;
				cm.sendYesNo("#b您是否想要学习31点#s1321002#  需要支付1000W金币  您是否想要学习？");
			} else if (selection == 43) {
				typed=43;
				cm.sendYesNo("#b您是否想要学习31点#s1120003# 需要支付1000W金币  您是否想要学习？");
			} else if (selection == 44) {
				typed=44;
				cm.sendYesNo("#b您是否想要学习21点#s1221003# 需要支付1000W金币  您是否想要学习？");
			} else if (selection == 45) {
				typed=45;
				cm.sendYesNo("#b您是否想要学习31点#s2110001# 需要支付1000W金币   您是否想要学习？");
			} else if (selection == 46) {
				typed=46;
				cm.sendYesNo("#b您是否想要学习#s2300000#   您是否想要学习？");
			} else if (selection == 47) {
				typed=47;
				cm.sendYesNo("#b您是否想要学习#s2301001#   您是否想要学习？");
			} else if (selection == 48) {
				typed=48;
				cm.sendYesNo("#b您是否想要学习#s2311004#   您是否想要学习？");
			} else if (selection == 49) {
				typed=49;
				cm.sendYesNo("#b您是否想要学习#s2301003#   您是否想要学习？");
			} else if (selection == 50) {
				typed=50;
				cm.sendYesNo("#b您是否想要学习21点#s1001005#  需要支付50W金币 您是否想要学习？");
			} else if (selection == 51) {
				typed=51;
				cm.sendYesNo("#b您是否想要学习#s2311004#   您是否想要学习？");
			} else if (selection == 52) {
				typed=52;
				cm.sendYesNo("#b您是否想要学习#s1100000#   您是否想要学习？");
			} else if (selection == 53) {
				typed=53;
				cm.sendYesNo("#b您是否想要学习#s1100001#   您是否想要学习？");
			} else if (selection == 54) {
				typed=54;
				cm.sendYesNo("#b您是否想要学习#s1100002#   您是否想要学习？");
			} else if (selection == 55) {
				typed=55;
				cm.sendYesNo("#b您是否想要学习#s1100003#   您是否想要学习？");
			} else if (selection == 56) {
				typed=56;
				cm.sendYesNo("#b您是否想要学习#s1101004#   您是否想要学习？");
			} else if (selection == 57) {
				typed=57;
				cm.sendYesNo("#b您是否想要学习#s1101005#   您是否想要学习？");
			} else if (selection == 58) {
				typed=58;
				cm.sendYesNo("#b您是否想要学习#s1101006#   您是否想要学习？");
			} else if (selection == 59) {
				typed=59;
				cm.sendYesNo("#b您是否想要学习#s1101007#   您是否想要学习？");
			} else if (selection == 60) {
				typed=60;
				cm.sendYesNo("#b您是否想要学习#s1200000#   您是否想要学习？");
			} else if (selection == 61) {
				typed=61;
				cm.sendYesNo("#b您是否想要学习#s1200001#   您是否想要学习？");
			} else if (selection == 62) {
				typed=62;
				cm.sendYesNo("#b您是否想要学习#s1200002#   您是否想要学习？");
			} else if (selection == 63) {
				typed=63;
				cm.sendYesNo("#b您是否想要学习#s1200003#   您是否想要学习？");
			} else if (selection == 64) {
				typed=64;
				cm.sendYesNo("#b您是否想要学习#s1201004#   您是否想要学习？");
			} else if (selection == 65) {
				typed=65;
				cm.sendYesNo("#b您是否想要学习#s1201005#   您是否想要学习？");
			} else if (selection == 66) {
				typed=66;
				cm.sendYesNo("#b您是否想要学习#s1201006#   您是否想要学习？");
			} else if (selection == 67) {
				typed=67;
				cm.sendYesNo("#b您是否想要学习#s1201007#   您是否想要学习？");
			} else if (selection == 68) {
				typed=68;
				cm.sendYesNo("#b您是否想要学习#s1300001#   您是否想要学习？");
			} else if (selection == 69) {
				typed=69;
				cm.sendYesNo("#b您是否想要学习#s1300002#   您是否想要学习？");
			} else if (selection == 70) {
				typed=70;
				cm.sendYesNo("#b您是否想要学习#s1300003#   您是否想要学习？");
			} else if (selection == 71) {
				typed=71;
				cm.sendYesNo("#b您是否想要学习#s1301004#   您是否想要学习？");
			} else if (selection == 72) {
				typed=72;
				cm.sendYesNo("#b您是否想要学习#s1301005#   您是否想要学习？");
			} else if (selection == 73) {
				typed=73;
				cm.sendYesNo("#b您是否想要学习#s1301006#   您是否想要学习？");
			} else if (selection == 74) {
				typed=74;
				cm.sendYesNo("#b您是否想要学习#s1301007#   您是否想要学习？");
			} else if (selection == 75) {
				typed=75;
			cm.sendYesNo("#b您是否想要学习#s1300000#   您是否想要学习？");
			}
		} else if (status == 2) {
			if (typed==1) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 2000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else 	if (xxx > 50  ) {
					cm.gainItem(4460011, -1);
					cm.gainMeso(- 2000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(1111008,31)
					cm.gainMeso(- 2000000 );
					cm.gainItem(4460011, -1);
					cm.getPlayer().setOneTimeLog("hupaoxiao");
					cm.sendOk("恭喜您成功学习了#s1111008#.");
					cm.dispose();
				}
			} else if (typed==2) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else 	if (xxx > 85  ) {
					cm.gainItem(4460013, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					//cm.teachSkill(1121008, 30,30);
					//cm.getPlayer().getdiyskills(1121008)//获取diy技能上线，返回-1就是没上限
					cm.getPlayer().gaindiyskills(1121008,31)
					cm.teachSkill(1121008, cm.getPlayer().getSkillLevel(1121008),31);
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460013, -1);
					cm.getPlayer().setOneTimeLog("qingwu");
					cm.sendOk("恭喜您成功学习了#s1121008#.");
					cm.dispose();
				}
			} else if (typed==3) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else 	if (xxx > 50  ) {
					cm.gainItem(4460015, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(1221009,31)
					cm.teachSkill(1221009, cm.getPlayer().getSkillLevel(1221009),31);
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460015, -1);
					cm.getPlayer().setOneTimeLog("lianhuanhuanpo");
					cm.sendOk("恭喜您成功学习了#s1221009#.");
					cm.dispose();
				}
			} else if (typed==4) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else 	if (xxx > 50  ) {
					cm.gainItem(4460017, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(1311001,31)
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460017, -1);
					cm.getPlayer().setOneTimeLog("qianglianji");
					cm.sendOk("恭喜您成功学习了#s1311001#.");
					cm.dispose();
				}
			} else if (typed==5) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else 	if (xxx > 50  ) {
					cm.gainItem(4460019, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(1320006,31)
					cm.teachSkill(1320006, cm.getPlayer().getSkillLevel(1320006),31);
					//cm.teachSkill(1320006, 30, 31);
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460019, -1);
					cm.getPlayer().setOneTimeLog("elong");
					cm.sendOk("恭喜您成功学习了#s1320006#.");
					cm.dispose();
				}
			} else if (typed==6) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
				cm.dispose();
			} else 	if (xxx > 50  ) {
					cm.gainItem(4460020, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(2111005,21)
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460020, -1);
					cm.getPlayer().setOneTimeLog("hdkuangbao");
					cm.sendOk("恭喜您成功学习了#s2111005#.");
					cm.dispose();
				}
			} else if (typed==7) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else 	if (xxx > 50  ) {
					cm.gainItem(4460022, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(2121006,31)
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460022, -1);
					cm.getPlayer().setOneTimeLog("baopo");
					cm.sendOk("恭喜您成功学习了#s2121006#.");
					cm.dispose();
				}
			} else if (typed==8) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else 	if (xxx > 50  ) {
					cm.gainItem(4460024, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(2221006,31)
					cm.teachSkill(2221006, cm.getPlayer().getSkillLevel(2221006),31);
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460024, -1);
					cm.getPlayer().setOneTimeLog("shandian");
					cm.sendOk("恭喜您成功学习了#s2221006#.");
					cm.dispose();
				}
			} else if (typed==9) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else 	if (xxx > 100  ) {
					cm.gainItem(4460026, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(2301003,21)
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460026, -1);
					cm.getPlayer().setOneTimeLog("baohu");
					cm.sendOk("恭喜您成功学习了#s2301003#.");
					cm.dispose();
				}
			} else if (typed==10) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else 	if (xxx > 50  ) {
					cm.gainItem(4460028, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(2311004,31)
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460028, -1);
					cm.getPlayer().setOneTimeLog("shengguang");
					cm.sendOk("恭喜您成功学习了#s2311004#.");
					cm.dispose();
				}
			} else if (typed==11) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else 	if (xxx > 50 ) {
					cm.gainItem(4460030, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(4111002,31)
					cm.teachSkill(4111002, cm.getPlayer().getSkillLevel(4111002),31);
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460030, -1);
					cm.getPlayer().setOneTimeLog("yingzi");
					cm.sendOk("恭喜您成功学习了#s4111002#.");
					cm.dispose();
				}
			} else if (typed==12) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else 	if (xxx > 50  ) {
					cm.gainItem(4460032, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(4121007,31)
					cm.teachSkill(4121007, cm.getPlayer().getSkillLevel(4121007),31);
					//cm.teachSkill(4121007, 30, 31);
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460032, -1);
					cm.getPlayer().setOneTimeLog("sanlianhuan");
					cm.sendOk("恭喜您成功学习了#s4121007#.");
					cm.dispose();
				}
			} else if (typed==13) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else 	if (xxx > 50  ) {
					cm.gainItem(4460034, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(4221001,31)
					cm.teachSkill(4221001, cm.getPlayer().getSkillLevel(4221001),31);
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460034, -1);
					cm.getPlayer().setOneTimeLog("ansha");
					cm.sendOk("恭喜您成功学习了#s4221001#.");
					cm.dispose();
				}
			} else if (typed==14) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else 	if (xxx > 50  ) {
					cm.gainItem(4460036, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(4220002,31)
					cm.teachSkill(4220002, cm.getPlayer().getSkillLevel(4220002),31);
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460036, -1);
					cm.getPlayer().setOneTimeLog("jiadongzuo");
					cm.sendOk("恭喜您成功学习了#s4220002#.");
					cm.dispose();
				}
			} else if (typed==15) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else 	if (xxx > 50  ) {
					cm.gainItem(4460038, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(3121004,31)
					cm.teachSkill(3121004, cm.getPlayer().getSkillLevel(3121004),31);
					//cm.teachSkill(3121004, 30, 31);
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460038, -1);
					cm.getPlayer().setOneTimeLog("baofeng");
					cm.sendOk("恭喜您成功学习了#s3121004#.");
					cm.dispose();
				}
			} else if (typed==16) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else 	if (xxx > 50  ) {
					cm.gainItem(4460040, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(3121002,31)
					cm.teachSkill(3121002, cm.getPlayer().getSkillLevel(3121002),31);
					//cm.teachSkill(3121002, 30, 31);
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460040, -1);
					cm.getPlayer().setOneTimeLog("gonghouyan");
					cm.sendOk("恭喜您成功学习了#s3121002#.");
					cm.dispose();
				}
			} else if (typed==17) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else 	if (xxx > 50  ) {
					cm.gainItem(4460042, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(3221002,31)
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460042, -1);
					cm.getPlayer().setOneTimeLog("nuhuoyan");
					cm.sendOk("恭喜您成功学习了#s3221002#.");
					cm.dispose();
				}
			} else if (typed==18) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else 	if (xxx > 50  ) {
					cm.gainItem(4460044, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(3211006,31)
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460044, -1);
					cm.getPlayer().setOneTimeLog("jiansaoshe");
					cm.sendOk("恭喜您成功学习了#s3211006#.");
					cm.dispose();
				}
							} else if (typed==19) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else 	if (xxx > 50  ) {
					cm.gainItem(4460046, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(21120005,31)
					cm.teachSkill(21120005, cm.getPlayer().getSkillLevel(21120005),31);
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460046, -1);
					cm.getPlayer().setOneTimeLog("juxiong");
					cm.sendOk("恭喜您成功学习了#s21120005#.");
					cm.dispose();
				}
							} else if (typed==20) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else 	if (xxx > 50  ) {
					cm.gainItem(4460048, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(22171002,16)
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460048, -1);
					cm.getPlayer().setOneTimeLog("mlfensheng");
					cm.sendOk("恭喜您成功学习了#s22171002#.");
					cm.dispose();
				}
							} else if (typed==21) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else 	if (xxx > 50  ) {
					cm.gainItem(4460050, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(22141002,16)
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460050, -1);
					cm.getPlayer().setOneTimeLog("mlkuangbao");
					cm.sendOk("恭喜您成功学习了#s22141002#.");
					cm.dispose();
				}
							} else if (typed==22) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else 	if (xxx > 50  ) {
					cm.gainItem(4460052, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(4331002,31)
					cm.teachSkill(4331002, cm.getPlayer().getSkillLevel(4331002),31);
					//cm.teachSkill(4331002, 31, 31);
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460052, -1);
					cm.getPlayer().setOneTimeLog("jingxiang");
					cm.sendOk("恭喜您成功学习了#s4331002#.");
					cm.dispose();
				}
							} else if (typed==23) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else 	if (xxx > 50  ) {
					cm.gainItem(4460054, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(4311002,21)
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460054, -1);
					cm.getPlayer().setOneTimeLog("liuyun");
					cm.sendOk("恭喜您成功学习了#s4311002#.");
					cm.dispose();
				}
							} else if (typed==24) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else 	if (xxx > 50  ) {
					cm.gainItem(4460056, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(5121004,31)
					cm.teachSkill(5121004, cm.getPlayer().getSkillLevel(5121004),31);
					//cm.teachSkill(5121004, 30, 31);
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460056, -1);
					cm.getPlayer().setOneTimeLog("suoming");
					cm.sendOk("恭喜您成功学习了#s5121004#.");
					cm.dispose();
				}
							} else if (typed==25) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else 	if (xxx > 50  ) {
					cm.gainItem(4460058, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(5221007,31)
					cm.teachSkill(5221007, cm.getPlayer().getSkillLevel(5221007),31);
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460058, -1);
					cm.getPlayer().setOneTimeLog("jinshu");
					cm.sendOk("恭喜您成功学习了#s5221007#.");
					cm.dispose();
				}
							} else if (typed==26) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else 	if (xxx > 50  ) {
					cm.gainItem(4460061, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(1101004,21)
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460061, -1);
					cm.getPlayer().setOneTimeLog("kuaisujian");
					cm.sendOk("恭喜您成功学习了#s1101004#.");
					cm.dispose();
				}
							} else if (typed==27) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else 	if (xxx > 50  ) {
					cm.gainItem(4460061, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(1201004,21)
										cm.teachSkill(1201004, cm.getPlayer().getSkillLevel(1201004),21);
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460061, -1);
					cm.getPlayer().setOneTimeLog("kuaisujian1");
					cm.sendOk("恭喜您成功学习了#s1201004#.");
					cm.dispose();
				}
							} else if (typed==28) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else 	if (xxx > 50  ) {
					cm.gainItem(4460061, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(1301004,21)
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460061, -1);
					cm.getPlayer().setOneTimeLog("kuaisuqiang");
					cm.sendOk("恭喜您成功学习了#s1301004#.");
					cm.dispose();
				}
				} else if (typed==29) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else 	if (xxx > 50  ) {
					cm.gainItem(4460062, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(2101001,21)
					cm.teachSkill(2101001, cm.getPlayer().getSkillLevel(2101001),21);
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460062, -1);
					cm.getPlayer().setOneTimeLog("jingshenli");
					cm.sendOk("恭喜您成功学习了#s2101001#.");
					cm.dispose();
				}
				} else if (typed==30) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else 	if (xxx > 50  ) {
					cm.gainItem(4460062, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(2201001,21)
					cm.teachSkill(2201001, cm.getPlayer().getSkillLevel(2201001),21);
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460062, -1);
					cm.getPlayer().setOneTimeLog("jingshenli1");
					cm.sendOk("恭喜您成功学习了#s2201001#.");
					cm.dispose();
				}
							} else if (typed==31) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else 	if (xxx > 100  ) {
					cm.gainItem(4460062, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(2301005,31)
					cm.teachSkill(2301005, cm.getPlayer().getSkillLevel(2301005),31);
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460062, -1);
					cm.getPlayer().setOneTimeLog("shengjianshu");
					cm.sendOk("恭喜您成功学习了#s2311004#.");
					cm.dispose();
				}
							} else if (typed==32) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else 	if (xxx > 50  ) {
					cm.gainItem(4460063, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(4100001,31)
					cm.teachSkill(4100001, cm.getPlayer().getSkillLevel(4100001),31);
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460063, -1);
					cm.getPlayer().setOneTimeLog("touzhi");
					cm.sendOk("恭喜您成功学习了#s4100001#.");
					cm.dispose();
				}
							} else if (typed==33) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else if (!cm.haveItem(4460063, 1)) {
			} else 	if (xxx > 50  ) {
					cm.gainItem(4460063, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(4201005,31)
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460063, -1);
					cm.getPlayer().setOneTimeLog("huixuanzhan");
					cm.sendOk("恭喜您成功学习了#s4201005#.");
					cm.dispose();
				}
							} else if (typed==34) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else 	if (xxx > 50  ) {
					cm.gainItem(4460064, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(3000001,21)
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460064, -1);
					cm.getPlayer().setOneTimeLog("qianglijian");
					cm.sendOk("恭喜您成功学习了#s3000001#.");
					cm.dispose();
				}
							} else if (typed==35) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
			} else 	if (xxx > 50  ) {
					cm.gainItem(4460064, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(3000001,21)
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460064, -1);
					cm.getPlayer().setOneTimeLog("qianglijian1");
					cm.sendOk("恭喜您成功学习了#s3000001#.");
					cm.dispose();
				}
							} else if (typed==36) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else 	if (xxx > 50  ) {
					cm.gainItem(4460065, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(21100001,21)
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460065, -1);
					cm.getPlayer().setOneTimeLog("sanchong");
					cm.sendOk("恭喜您成功学习了#s21100001#.");
					cm.dispose();
				}
				} else if (typed==37) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else 	if (xxx > 50  ) {
					cm.gainItem(4460066, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(22111001,21)
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460066, -1);
					cm.getPlayer().setOneTimeLog("mofadun");
					cm.sendOk("恭喜您成功学习了#s22111001#.");
					cm.dispose();
				}
							} else if (typed==38) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else 	if (xxx > 50  ) {
					cm.gainItem(4460068, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(5101006,21)
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460068, -1);
					cm.getPlayer().setOneTimeLog("jisuquan");
					cm.sendOk("恭喜您成功学习了#s5101006#.");
					cm.dispose();
				}
							} else if (typed==39) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else 	if (xxx > 50  ) {
					cm.gainItem(4460068, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(5201003,21)
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460068, -1);
					cm.getPlayer().setOneTimeLog("sushe");
					cm.sendOk("恭喜您成功学习了#s5201003#.");
					cm.dispose();
				}
				} else if (typed==40) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else 	if (xxx > 50  ) {
					cm.gainItem(4460070, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(1121002,31)
					cm.teachSkill(1121002, cm.getPlayer().getSkillLevel(1121002),31);
					//cm.teachSkill(5121004, 30, 31);
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460070, -1);
					//cm.getPlayer().setOneTimeLog("suoming");
					cm.sendOk("恭喜您成功学习了#s1121002#.");
					cm.dispose();
				}
							} else if (typed==41) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else 	if (xxx > 50  ) {
					cm.gainItem(4460070, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(1221002,31)
					cm.teachSkill(1221002, cm.getPlayer().getSkillLevel(1221002),31);
					//cm.teachSkill(5121004, 30, 31);
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460070, -1);
					//cm.getPlayer().setOneTimeLog("suoming");
					cm.sendOk("恭喜您成功学习了#s1221002#.");
					cm.dispose();
				}
				} else if (typed==42) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else 	if (xxx > 50  ) {
					cm.gainItem(4460070, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(1321002,31)
					cm.teachSkill(1321002, cm.getPlayer().getSkillLevel(1321002),31);
					//cm.teachSkill(5121004, 30, 31);
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460070, -1);
					//cm.getPlayer().setOneTimeLog("suoming");
					cm.sendOk("恭喜您成功学习了#s1321002#.");
					cm.dispose();
				}
							} else if (typed==43) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else 	if (xxx > 50  ) {
					cm.gainItem(4460072, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(1120003,31)
					cm.teachSkill(1120003, cm.getPlayer().getSkillLevel(1120003),31);
					//cm.teachSkill(5121004, 30, 31);
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460072, -1);
					cm.getPlayer().setOneTimeLog("jjdq");
					cm.sendOk("恭喜您成功学习了#s1120003#.");
					cm.dispose();
				}
							} else if (typed==44) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else 	if (xxx > 50  ) {
					cm.gainItem(4460074, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(1221003,21)
					cm.teachSkill(1221003, cm.getPlayer().getSkillLevel(1221003),21);
					//cm.teachSkill(5121004, 30, 31);
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460074, -1);
					//cm.getPlayer().setOneTimeLog("suoming");
					cm.sendOk("恭喜您成功学习了#s1221003#.");
					cm.dispose();
				}		
				       } else if (typed==45) {
			var xxx = Math.floor(Math.random() * 100);
		    if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else 	if (xxx > 50  ) {
					cm.gainItem(4460071, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(2110001,31)
					cm.teachSkill(2110001, cm.getPlayer().getSkillLevel(2110001),31);
					//cm.teachSkill(5121004, 30, 31);
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460071, -1);
					//cm.getPlayer().setOneTimeLog("suoming");
					cm.sendOk("恭喜您成功学习了#s2110001#.");
					cm.dispose();
				}	
								       } else if (typed==48) {
                var xxx = Math.floor(Math.random() * 100);
				if (xxx > 50) {
					cm.getPlayer().gaindiyskills(2311004,32)
					cm.gainItem(2022519, -1);
					cm.sendOk("恭喜您成功学习了#s2311004#.");
					cm.dispose();
				} else {
					cm.gainItem(2022519, -1);
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose();
				}
								       } else if (typed==49) {
                var xxx = Math.floor(Math.random() * 100);
				if (xxx > 50) {
					cm.getPlayer().gaindiyskills(2301003,21)
					cm.gainItem(2022519, -1);
					cm.sendOk("恭喜您成功学习了#s2301003#.");
					cm.dispose();
				} else {
					cm.gainItem(2022519, -1);
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose();
				}
								       } else if (typed==50) {
                var xxx = Math.floor(Math.random() * 100);
			var xxx = Math.floor(Math.random() * 100);
		   if (cm.getMeso() <= 10000000){
				cm.sendOk("金币不足1000W无法学习");
				cm.dispose();
			} else 	if (xxx > 50  ) {
					cm.gainItem(4460080, -1);
					cm.gainMeso(- 10000000 );
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose()
				} else {
					cm.getPlayer().gaindiyskills(1001005,21)
					cm.teachSkill(1001005, cm.getPlayer().getSkillLevel(1001005),21);
					//cm.teachSkill(5121004, 30, 31);
					cm.gainMeso(- 10000000 );
					cm.gainItem(4460080, -1);
					cm.getPlayer().setOneTimeLog("qungtigongji");
					cm.sendOk("恭喜您成功学习了#s1001005#.");
					cm.dispose();
				}
								       } else if (typed==51) {
                var xxx = Math.floor(Math.random() * 100);
				if (xxx > 50) {
					cm.getPlayer().gaindiyskills(2311004,32)
					cm.gainItem(2022519, -1);
					cm.sendOk("恭喜您成功学习了#s2311004#.");
					cm.dispose();
				} else {
					cm.gainItem(2022519, -1);
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose();
				}
								       } else if (typed==52) {
                var xxx = Math.floor(Math.random() * 100);
				if (xxx > 50) {
					cm.getPlayer().gaindiyskills(1100000,22)
					cm.gainItem(2022519, -1);
					cm.sendOk("恭喜您成功学习了#s1100000#.");
					cm.dispose();
				} else {
					cm.gainItem(2022519, -1);
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose();
				}
								       } else if (typed==53) {
                var xxx = Math.floor(Math.random() * 100);
				if (xxx > 50) {
					cm.getPlayer().gaindiyskills(1100001,22)
					cm.gainItem(2022519, -1);
					cm.sendOk("恭喜您成功学习了#s1100001#.");
					cm.dispose();
				} else {
					cm.gainItem(2022519, -1);
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose();
				}
								       } else if (typed==54) {
                var xxx = Math.floor(Math.random() * 100);
				if (xxx > 50) {
					cm.getPlayer().gaindiyskills(1100002,32)
					cm.gainItem(2022519, -1);
					cm.sendOk("恭喜您成功学习了#s1100002#.");
					cm.dispose();
				} else {
					cm.gainItem(2022519, -1);
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose();
				}
								       } else if (typed==55) {
                var xxx = Math.floor(Math.random() * 100);
				if (xxx > 50) {
					cm.getPlayer().gaindiyskills(1100003,32)
					cm.gainItem(2022519, -1);
					cm.sendOk("恭喜您成功学习了#s1100003#.");
					cm.dispose();
				} else {
					cm.gainItem(2022519, -1);
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose();
				}
								       } else if (typed==56) {
                var xxx = Math.floor(Math.random() * 100);
				if (xxx > 50) {
					cm.getPlayer().gaindiyskills(1101004,22)
					cm.gainItem(2022519, -1);
					cm.sendOk("恭喜您成功学习了#s1101004#.");
					cm.dispose();
				} else {
					cm.gainItem(2022519, -1);
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose();
				}
								       } else if (typed==57) {
                var xxx = Math.floor(Math.random() * 100);
				if (xxx > 50) {
					cm.getPlayer().gaindiyskills(1101005,22)
					cm.gainItem(2022519, -1);
					cm.sendOk("恭喜您成功学习了#s1101005#.");
					cm.dispose();
				} else {
					cm.gainItem(2022519, -1);
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose();
				}
								       } else if (typed==58) {
                var xxx = Math.floor(Math.random() * 100);
				if (xxx > 50) {
					cm.getPlayer().gaindiyskills(1101006,22)
					cm.gainItem(2022519, -1);
					cm.sendOk("恭喜您成功学习了#s1101006#.");
					cm.dispose();
				} else {
					cm.gainItem(2022519, -1);
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose();
				}
								       } else if (typed==59) {
                var xxx = Math.floor(Math.random() * 100);
				if (xxx > 50) {
					cm.getPlayer().gaindiyskills(1101007,32)
					cm.gainItem(2022519, -1);
					cm.sendOk("恭喜您成功学习了#s1101007#.");
					cm.dispose();
				} else {
					cm.gainItem(2022519, -1);
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose();
				}
								       } else if (typed==60) {
                var xxx = Math.floor(Math.random() * 100);
				if (xxx > 50) {
					cm.getPlayer().gaindiyskills(1200000,22)
					cm.gainItem(2022519, -1);
					cm.sendOk("恭喜您成功学习了#s1200000#.");
					cm.dispose();
				} else {
					cm.gainItem(2022519, -1);
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose();
				}
								       } else if (typed==61) {
                var xxx = Math.floor(Math.random() * 100);
				if (xxx > 50) {
					cm.getPlayer().gaindiyskills(1200001,22)
					cm.gainItem(2022519, -1);
					cm.sendOk("恭喜您成功学习了#s1200001#.");
					cm.dispose();
				} else {
					cm.gainItem(2022519, -1);
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose();
				}
								       } else if (typed==62) {
                var xxx = Math.floor(Math.random() * 100);
				if (xxx > 50) {
					cm.getPlayer().gaindiyskills(1200002,32)
					cm.gainItem(2022519, -1);
					cm.sendOk("恭喜您成功学习了#s1200002#.");
					cm.dispose();
				} else {
					cm.gainItem(2022519, -1);
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose();
				}
								       } else if (typed==63) {
                var xxx = Math.floor(Math.random() * 100);
				if (xxx > 50) {
					cm.getPlayer().gaindiyskills(1200003,32)
					cm.gainItem(2022519, -1);
					cm.sendOk("恭喜您成功学习了#s1200003#.");
					cm.dispose();
				} else {
					cm.gainItem(2022519, -1);
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose();
				}
								       } else if (typed==64) {
                var xxx = Math.floor(Math.random() * 100);
				if (xxx > 50) {
					cm.getPlayer().gaindiyskills(1201004,22)
					cm.gainItem(2022519, -1);
					cm.sendOk("恭喜您成功学习了#s1201004#.");
					cm.dispose();
				} else {
					cm.gainItem(2022519, -1);
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose();
				}
								       } else if (typed==65) {
                var xxx = Math.floor(Math.random() * 100);
				if (xxx > 50) {
					cm.getPlayer().gaindiyskills(1201005,22)
					cm.gainItem(2022519, -1);
					cm.sendOk("恭喜您成功学习了#s1201005#.");
					cm.dispose();
				} else {
					cm.gainItem(2022519, -1);
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose();
				}
								       } else if (typed==66) {
                var xxx = Math.floor(Math.random() * 100);
				if (xxx > 50) {
					cm.getPlayer().gaindiyskills(1201006,22)
					cm.gainItem(2022519, -1);
					cm.sendOk("恭喜您成功学习了#s1201006#.");
					cm.dispose();
				} else {
					cm.gainItem(2022519, -1);
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose();
				}
								       } else if (typed==67) {
                var xxx = Math.floor(Math.random() * 100);
				if (xxx > 50) {
					cm.getPlayer().gaindiyskills(1201004,32)
					cm.gainItem(2022519, -1);
					cm.sendOk("恭喜您成功学习了#s1201004#.");
					cm.dispose();
				} else {
					cm.gainItem(2022519, -1);
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose();
				}
								       } else if (typed==68) {
                var xxx = Math.floor(Math.random() * 100);
				if (xxx > 50) {
					cm.getPlayer().gaindiyskills(1300001,22)
					cm.gainItem(2022519, -1);
					cm.sendOk("恭喜您成功学习了#s1300001#.");
					cm.dispose();
				} else {
					cm.gainItem(2022519, -1);
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose();
				}
								       } else if (typed==69) {
                var xxx = Math.floor(Math.random() * 100);
				if (xxx > 50) {
					cm.getPlayer().gaindiyskills(1300002,32)
					cm.gainItem(2022519, -1);
					cm.sendOk("恭喜您成功学习了#s1300002#.");
					cm.dispose();
				} else {
					cm.gainItem(2022519, -1);
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose();
				}
								       } else if (typed==70) {
                var xxx = Math.floor(Math.random() * 100);
				if (xxx > 50) {
					cm.getPlayer().gaindiyskills(1300003,32)
					cm.gainItem(2022519, -1);
					cm.sendOk("恭喜您成功学习了#s1300003#.");
					cm.dispose();
				} else {
					cm.gainItem(2022519, -1);
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose();
				}
								       } else if (typed==71) {
                var xxx = Math.floor(Math.random() * 100);
				if (xxx > 50) {
					cm.getPlayer().gaindiyskills(1301004,22)
					cm.gainItem(2022519, -1);
					cm.sendOk("恭喜您成功学习了#s1301004#.");
					cm.dispose();
				} else {
					cm.gainItem(2022519, -1);
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose();
				}
								       } else if (typed==72) {
                var xxx = Math.floor(Math.random() * 100);
				if (xxx > 50) {
					cm.getPlayer().gaindiyskills(1301005,22)
					cm.gainItem(2022519, -1);
					cm.sendOk("恭喜您成功学习了#s1301005#.");
					cm.dispose();
				} else {
					cm.gainItem(2022519, -1);
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose();
				}
								       } else if (typed==73) {
                var xxx = Math.floor(Math.random() * 100);
				if (xxx > 50) {
					cm.getPlayer().gaindiyskills(1301006,22)
					cm.gainItem(2022519, -1);
					cm.sendOk("恭喜您成功学习了#s1301006#.");
					cm.dispose();
				} else {
					cm.gainItem(2022519, -1);
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose();
				}
								       } else if (typed==74) {
                var xxx = Math.floor(Math.random() * 100);
				if (xxx > 50) {
					cm.getPlayer().gaindiyskills(1301007,32)
					cm.gainItem(2022519, -1);
					cm.sendOk("恭喜您成功学习了#s1301007#.");
					cm.dispose();
				} else {
					cm.gainItem(2022519, -1);
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose();
				}
												       } else if (typed==75) {
                var xxx = Math.floor(Math.random() * 100);
				if (xxx > 50) {
					cm.getPlayer().gaindiyskills(1300000,22)
					cm.gainItem(2022519, -1);
					cm.sendOk("恭喜您成功学习了#s1300000#.");
					cm.dispose();
				} else {
					cm.gainItem(2022519, -1);
					cm.sendOk("学习失败：\r\n\r\n#r1) .");
					cm.dispose();
				}
           }
		}
	}
  }
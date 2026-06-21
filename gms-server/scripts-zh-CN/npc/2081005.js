/*
 *
 *  此脚本由乐章网络制作完成
 * 购买商业脚本请加群:1049548
 *
 */




var aaa = "#fUI/UIWindow.img/Quest/icon9/0#";
var zzz = "#fUI/UIWindow.img/Quest/icon8/0#";
var sss = "#fUI/UIWindow.img/QuestIcon/3/0#";

//------------------------------------------------------------------------

var chosenMap = -1;
var monsters = 0;
var towns = 0;
var bosses = 0;
var fuben = 0;
var expMaps = 0;

//------------------------------------------------------------------------

var bossmaps = Array(
		Array(104000400,1000,"#m104000400#【#r蜗牛王BOSS#k】    "),
		Array(260010201,1000,"仙人掌沙漠W【#r仙人掌BOSS#k】   "),
		Array(230020100,1000,"#m230020100#【#r贝壳精BOSS#k】      "),
		Array(101030404,1000,"#m101030404#【#r树妖王BOSS#k】    "),
		Array(100040105,1000,"#m100040105#【#r僵尸猴BOSS#k】    "),
		Array(220050100,1000,"#m220050100#【#r猫头鹰BOSS#k】      "),
		Array(100000005,1000,"#m100000005#【#r蘑菇王BOSS#k】  "),
		Array(800010100,1000,"#m800010100#【#r蓝蘑菇王BOSS#k】    "),
		Array(105070002,1000,"#m105070002#【#r僵尸蘑菇王BOSS#k】"),
		Array(110040000,1000,"#m110040000#【#r贝壳王BOSS#k】      "),
		Array(250010304,1000,"#m250010304#【#r流浪熊BOSS#k】  "),
		Array(200010300,1000,"#m200010300#【#r艾力杰BOSS#k】    "),
		Array(261030000,1000,"研究所通道【#r吉米啦BOSS#k】    "),
		Array(250010503,1000,"妖怪的森林【#r大妙仙BOSS#k】    "),
		Array(222010310,1000,"#m222010310#【#r九尾狐BOSS#k】          "),
		Array(240020401,2000,"#m240020401#【#r喷火龙BOSS#k】  "),
		Array(240020101,2000,"#m240020101#【#r格瑞芬BOSS#k】  "),
		Array(105090900,2000,"#m105090900#【#r蝙蝠怪BOSS#k】  "),
		Array(107000300,2000,"鳄鱼的深坛【#r大多尔BOSS#k】    "),
		Array(240040401,2000,"大海兽峡谷【#r海兽BOSS#k】      "),
		Array(702070400,2000,"藏经阁顶层【#r妖僧BOSS#k】      "),
		Array(220080000,2000,"时间塔底部【#r闹钟BOSS#k】      "),
		Array(230040420,5000,"皮亚奴斯洞【#r鱼王BOSS#k】      "),
		Array(211042300,5000,"扎昆的入口【#r扎昆BOSS#k】      "),
		Array(240050400,6000,"黑龙王洞穴【#r黑龙BOSS#k】      ")													
		);

//------------------------------------------------------------------------

var monstermaps = Array(
		Array(104040000,80,"射手训练场　 适合  1 ~ 15 级玩家 "), 
		Array(103000101,80,"地铁<第1地区>适合 20 ~ 30 级玩家 "), 
	    Array(101040001,80,"野猪的领土　 适合 20 ~ 35 级玩家 "), 
		Array(101040003,100,"钢之黑怪之地 适合 20 ~ 35 级玩家 "), 
		Array(101030001,100,"野猪的领土Ⅱ 适合 20 ~ 35 级玩家 "), 
		Array(105070001,200,"蚂蚁广场 　　适合 20 ~ 40 级玩家 "), 
		Array(222010000,200,"乌山入口　　 适合 20 ~ 50 级玩家 "), 
		Array(230020000,200,"东海叉路　　 适合 30 ~ 40 级玩家 "), 
		Array(100040103,200,"#m100040103#   适合 30 ~ 50 级玩家 "), 
		Array(200040000,200,"云彩公园Ⅲ　 适合 35 ~ 60 级玩家 "),
		Array(230010400,200,"西海叉路　　 适合 40 ~ 50 级玩家 "),  
		Array(101030110,500,"第1军营　　　适合 40 ~ 60 级玩家 "), 
		Array(106000002,500,"危险的峡谷Ⅱ 适合 40 ~ 60 级玩家 "), 
		Array(101030103,500,"遗迹发掘地Ⅲ 适合 40 ~ 60 级玩家 "), 
		Array(105090300,800,"龙穴　　　　 适合 40 ~ 70 级玩家 "), 
		Array(220010500,800,"露台大厅　　 适合 40 ~ 70 级玩家 "),
		Array(251010000,800,"十年药草地　 适合 45 ~ 60 级玩家 "), 
		Array(250020000,800,"初级修炼场　 适合 50 ~ 60 级玩家 "),
		Array(103000105,800,"地铁<第4地区>适合 50 ~ 70 级玩家 "), 
		Array(800020130,800,"大佛的邂逅　 适合 50 ~ 70 级玩家 "),
		Array(211041400,800,"死亡之林Ⅳ　 适合 55 ~ 70 级玩家 "),
		Array(105040306,1000,"巨人之林 　  适合 60 ~ 80 级玩家 "), 
        Array(541010010,1000,"幽灵船２  　 适合 60 ~ 90 级玩家 "),
		Array(200010301,1000,"黑暗庭院Ⅰ   适合 70 ~ 90 级玩家 "),
		Array(600020300,2000,"狼蛛洞穴Ⅰ　适合 80 ~ 120 级玩家 "), 
		Array(240010500,2000,"山羊峡谷　　适合 85 ~ 120 级玩家 "),
		Array(240020100,2000,"火焰死亡战场适合 85 ~ 120 级玩家 "),
		Array(220070201,2000,"消失的时间　适合 85 ~ 120 级玩家 "), 
	//	Array(240040000,2000,"龙的峡谷　　　　    适合 95 ~ 120 级玩家。"),
		Array(551030100,3000,"阴森世界入口适合 95 ~ 120 级玩家 "),  
		Array(541020000,3000,"乌鲁城入口　适合 95 ~ 150 级玩家 "),
	    Array(240040500,3000,"龙之巢穴入口适合 100~ 150 级玩家 ") 
		);

//------------------------------------------------------------------------

var townmaps = Array(
		
		Array(1000000,2000,"彩虹岛新手村  "), 
		Array(104000000,2000,"明珠港        "), 
		Array(100000000,1,"射手村        "), 
		Array(100000104,1,"射手村美发店  "),
		Array(101000000,1,"魔法密林      "), 
		Array(102000000,1,"勇士部落      "), 
		Array(103000000,1,"废弃都市      "), 
		Array(120000000,1,"诺特勒斯号码头"),
		Array(105040300,2000,"林中之城      "),
		Array(110000000,2000,"黄金海岸      "),
		Array(140000000,2000,"里恩          "),
		Array(200000000,2000,"天空之城      "),
		Array(211000000,2000,"冰峰雪域      "), 
		Array(230000000,2000,"水下世界      "),  
		Array(222000000,2000,"童话村        "), 
		Array(220000000,2000,"玩具城        "),
		Array(701000000,2000,"东方神州      "),
		Array(250000000,2000,"武陵          "), 
		Array(702000000,2000,"少林寺        "), 
		Array(500000000,2000,"泰国          "),
		Array(260000000,2000,"阿里安特      "),  
		Array(600000000,2000,"新叶城        "), 
		Array(240000000,2000,"神木村        "),  
		Array(261000000,2000,"马加提亚      "), 
		Array(221000000,2000,"地球防御本部  "), 
		Array(251000000,2000,"百草堂        "),
		Array(701000200,2000,"上海豫园      "),
		Array(550000000,2000,"吉隆大都市    "),
		Array(130000000,2000,"圣地          "),
		Array(551000000,2000,"甘榜村        "),
		Array(801000000,2000,"昭和村        "), 
		Array(540010000,2000,"新加坡机场    "),
		Array(541000000,2000,"新加坡码头    "),
		Array(300000000,2000,"艾林森林      "), 
		Array(270000100,2000,"时间神殿      "), 
		Array(702100000,2000,"藏经阁        "), 
		Array(800000000,2000,"古代神社      "), 
		Array(130000200,2000,"圣地岔路      "),
		Array(741000208,2000,"钓鱼场        "),
		Array(925020000,2000,"武陵道场入口  "),
		Array(702090101,2000,"英语村        "),  
		Array(700000000,2000,"红鸾宫        ")
		);

//------------------------------------------------------------------------

var fubenmaps = Array(
		Array(105040316,10,"沉睡森林跳跳"),	
		Array(103000900,10,"地铁三号线跳跳"), 
		Array(109040001,10,"冒险岛活动跳跳"),     
		Array(280020000,10,"火山跳跳"), 
		Array(101000100,10,"忍苦跳跳") 											
		);

//------------------------------------------------------------------------

//------------------------------------------------------------------------
// 经验地图
var expMapsArray = Array(
        Array(270010300,0,"#m270010300#"),
        Array(270010400,0,"#m270010400#"),
        Array(270010500,0,"#m270010500#"),
		Array(270020300,0,"#m270020300#"),
        Array(270020400,0,"#m270020400#"),
        Array(270020500,0,"#m270020500#"),
		Array(270030300,0,"#m270030300#"),
        Array(270030400,0,"#m270030400#"),
        Array(270030500,0,"#m270030500#")										
		);

var psbenmaps = Array(
        Array(104010001,100000,"#m104010001#          (花费10W金币)"),
        Array(101020004,100000,"#m101020004#  (花费10W金币)"),
        Array(701010300,100000,"#m701010300#          (花费10W金币)"),
		Array(110020001,100000,"#m110020001#      (花费10W金币)"),
        Array(230020200,100000,"#m230020200#              (花费10W金币)"),
        Array(222010400,100000,"#m222010400#          (花费10W金币)"),
		Array(105090301,100000,"#m105090301#        (花费10W金币)"),
        Array(250010700,100000,"#m250010700#         (花费10W金币)")
		);
//------------------------------------------------------------------------

	function start() {
		status = -1;
		action(1, 0, 0);
		}
	function action(mode, type, selection) {
	if (mode == -1) {
		cm.sendOk("#b好的,下次再见.");
		cm.dispose();
		} else {
	if (status >= 0 && mode == 0) {
		cm.sendOk("#b好的,下次再见.");
		cm.dispose();
		return;
		}
	if (mode == 1) {
		status++;
		} else {
		status--;
		}

//------------------------------------------------------------------------

	if (status == 0) {

   	     var add = "暂未启用#r#k\r\n\r\n";           //var add = "废话不多说送你进去#r#k\r\n\r\n";

//		add += "#r　　　　　　　　　新物品展览#k\r\n";

//		add += "#b座椅#k\r\n";

//		add += "#v3010154# #v3010179# #v3010169# #v3010171# #v3010174# #v3010182# #v3010183# #v3010053##b\r\n\r\n";

//		add += "#b坐骑#k\r\n";

//		add += "#v1902060# #v1912053# #v1902062# #v1912055# #v1902063# #v1912056# #v1902040# #v1912057#\r\n\r\n";

	//	add += "#L2##e#r#v2591009#副本大厅#l";

	//	add += "#L6##e#r#v2591003#跑商材料#l";
		
	//	add += "#L0##e#r走你#l";

	//	add += "#L1##e#r#v2591007#练级传送#l\r\n\r\n";
		
     //   add += "#L3##e#r#v2591006#BOSS传送#l";
		
	   // add += "#L10##e#r#v2591005#进阶BOSS#l"; 
		
	//	add += "#L4##e#r#v2591004#跳跳地图#l\r\n\r\n"; 
		
		//add += "#L6##d拍照地图#l\r\n\r\n"; 
		cm.sendSimple (add);    

//------------------------------------------------------------------------
				
	} else if (status == 1) {

	if (selection == 0){
		cm.warp(240050000, 0);
		cm.dispose();
		
		}

	if (selection == 1) {
		var selStr = "#d　　　　　　　　　选择你的目的地吧.#k#b";
		for (var i = 0; i < monstermaps.length; i++) {
		selStr += "\r\n#L" + i + "#" + monstermaps[i][2] + "消耗#L" + i + "#" + monstermaps[i][1]*cm.getPlayer().getLevel() + "金币";
		}
		cm.sendSimple(selStr);
		monsters = 1;
		}

	if (selection == 2) {
		cm.warp(555000400, 0);
		cm.dispose();
		}
if (selection == 10) {
		cm.warp(970000002, 0);
		cm.dispose();
		}
	if (selection == 3) {
		var selStr = "#k\r\n#d　　　　　　　　　选择你的目的地吧.#k#b";
		for (var i = 0; i < bossmaps.length; i++) {
		selStr += "\r\n#L" + i + "#" + bossmaps[i][2] + " 消耗#L" + i + "#" + bossmaps[i][1]*cm.getPlayer().getLevel() + "金币";
		//selStr += "\r\n消耗#L" + i + "#" + bossmaps[i][1] + "";
		}
		cm.sendSimple(selStr);
		bosses = 1;
		}

	if (selection == 4) {
		var selStr = "#d　　　　　　　　　选择你的目的地吧.#k#b";
		for (var i = 0; i < fubenmaps.length; i++) {
		selStr += "\r\n#L" + i + "#" + fubenmaps[i][2] + "";
		}
		cm.sendSimple(selStr);
		fuben = 1;
		}
		
	if (selection == 5) {
	cm.warp(910000000, 0);
	cm.dispose();
		}
	if (selection == 6){
		var selStr = "#d　　　　　　　　　选择你的目的地吧.#k#b";
		for (var i = 0; i < psbenmaps.length; i++) {
		selStr += "\r\n#L" + i + "#" +  psbenmaps[i][2] + "";
		}
		cm.sendSimple(selStr);
		psben = 1;
		}
		

//------------------------------------------------------------------------

	} else if (status == 2) {

	if (towns == 1) {
		cm.sendYesNo("你确定要去 " + townmaps[selection][2] + "?");
		chosenMap = selection;
		towns = 2;

	} else if (monsters == 1) {
		cm.sendYesNo("你确定要去 " + monstermaps[selection][2] + "?");
		chosenMap = selection;
		monsters = 2;

	} else if (bosses == 1) {
		cm.sendYesNo("你确定要去 " + bossmaps[selection][2] + "?");
		chosenMap = selection;
		bosses = 2;

	} else if (expMaps == 1) {
		cm.sendYesNo("你确定要去 " + expMapsArray[selection][2] + "?");
		chosenMap = selection;
		expMaps = 2;
		

	}else if (fuben == 1) {
		cm.sendYesNo("你确定要去 " + fubenmaps[selection][2] + "?");
		chosenMap = selection;
		fuben = 2;
		}else if (psben == 1) {
		cm.sendYesNo("你确定要去 " + psbenmaps[selection][2] + "?");
		chosenMap = selection;
		psben = 2;

		}

//----------------------------------------------------------------------

	} else if (status == 3) {

	if (towns == 2) {
		if(cm.getMeso()>=townmaps[chosenMap][1]){
		cm.warp(townmaps[chosenMap][0], 0);
		cm.gainMeso(-townmaps[chosenMap][1]*cm.getPlayer().getLevel());
		}else{
		cm.sendOk("你没有足够的金币哦!");
		}
		cm.dispose();

	} else if (monsters == 2) {
		if(cm.getMeso()>=monstermaps[chosenMap][1]){
		cm.warp(monstermaps[chosenMap][0], 0);
		cm.gainMeso(-monstermaps[chosenMap][1]*cm.getPlayer().getLevel());
		cm.dispose();
		}else{
		cm.sendOk("你没有足够的金币哦!");
		cm.dispose();
		}
		cm.dispose();

	} else if (bosses == 2) {
		if(cm.getMeso()>=bossmaps[chosenMap][1]*cm.getPlayer().getLevel()){
		cm.warp(bossmaps[chosenMap][0], 0);
		cm.gainMeso(-bossmaps[chosenMap][1]*cm.getPlayer().getLevel());
		var mapId = bossmaps[chosenMap][0];
		var JobName = getJobName(cm.getPlayer().getJob());
		cm.喇叭(5, "玩家[" + cm.getPlayer().getName() + "] : 职业【"+JobName+"】前往 "+cm.getPlayer().getMap().getMapName()+" 进行挑战");
		}else{
		cm.sendOk("你没有足够的金币哦!");
		}
		cm.dispose();

	} else if (fuben == 2) {
		if(cm.getMeso()>=fubenmaps[chosenMap][1]){
		cm.warp(fubenmaps[chosenMap][0], 0);
		cm.gainMeso(-fubenmaps[chosenMap][1]);
		cm.dispose();
		}else{
		cm.sendOk("你没有足够的金币哦!");
		cm.dispose();
		}
		cm.dispose();
		} else if (psben == 2) {
		if(cm.getMeso()>=psbenmaps[chosenMap][1]){
		cm.warp(psbenmaps[chosenMap][0], 0);
		cm.gainMeso(-psbenmaps[chosenMap][1]);
		cm.dispose();
		}else{
		cm.sendOk("你没有足够的金币哦!");
		cm.dispose();
		}
		cm.dispose();
		
	}else if (expMaps == 2) {
		if(cm.getMeso()>=expMapsArray[chosenMap][1]){
		cm.warp(expMapsArray[chosenMap][0], 0);
		cm.gainMeso(-expMapsArray[chosenMap][1]);
		cm.dispose();
		}else{
		cm.sendOk("你没有足够的金币哦!");
		cm.dispose();
		}
		cm.dispose();

	}

//------------------------------------------------------------------------

		}
		}
		}

function getJobName(jobId){
	 switch (jobId) {
            case 0:
                return "新手";
            case 1000:
                return "初心者";
            case 2000:
                return "战神";

            case 100:
                return "战士";// Warrior
            case 110:
                return "剑客";
            case 111:
                return "勇士";
            case 112:
                return "英雄";
            case 120:
                return "准骑士";
            case 121:
                return "骑士";
            case 122:
                return "圣骑士";
            case 130:
                return "枪战士";
            case 131:
                return "龙骑士";
            case 132:
                return "黑骑士";

            case 200:
                return "魔法师";
            case 210:
                return "法师(火,毒)";
            case 211:
                return "巫师(火,毒)";
            case 212:
                return "魔导师(火,毒)";
            case 220:
                return "法师(雷,冰)";
            case 221:
                return "巫师(雷,冰)";
            case 222:
                return "魔导师(雷,冰)";
            case 230:
                return "牧师";
            case 231:
                return "祭司";
            case 232:
                return "主教";

            case 300:
                return "弓箭手";
            case 310:
                return "猎手";
            case 311:
                return "射手";
            case 312:
                return "神射手";
            case 320:
                return "弩弓手";
            case 321:
                return "游侠";
            case 322:
                return "箭神";

            case 400:
                return "飞侠";
            case 410:
                return "刺客";
            case 411:
                return "无影人";
            case 412:
                return "隐士";
            case 420:
                return "侠客";
            case 421:
                return "独行客";
            case 422:
                return "侠盗";

            case 500:
                return "海盜";
            case 510:
                return "拳手";
            case 511:
                return "斗士";
            case 512:
                return "冲锋队长";
            case 520:
                return "火枪手";
            case 521:
                return "大副";
            case 522:
                return "船长";

            case 1100:
                return "魂骑士1转";
            case 1110:
                return "魂骑士2转";
            case 1111:
                return "魂骑士3转";
            case 1112:
                return "魂骑士4转";

            case 1200:
                return "炎术士1转";
            case 1210:
                return "炎术士2转";
            case 1211:
                return "炎术士3转";
            case 1212:
                return "炎术士4转";

            case 1300:
                return "风灵使者1转";
            case 1310:
                return "风灵使者2转";
            case 1311:
                return "风灵使者3转";
            case 1312:
                return "风灵使者4转";

            case 1400:
                return "夜行者1转";
            case 1410:
                return "夜行者2转";
            case 1411:
                return "夜行者3转";
            case 1412:
                return "夜行者4转";

            case 1500:
                return "奇袭者1转";
            case 1510:
                return "奇袭者2转";
            case 1511:
                return "奇袭者3转";
            case 1512:
                return "奇袭者4转";

            case 2100:
                return "战神1转";
            case 2110:
                return "战神2转";
            case 2111:
                return "战神3转";
            case 2112:
                return "战神4转";
            default:
                return "未知的职业";
        }
    }


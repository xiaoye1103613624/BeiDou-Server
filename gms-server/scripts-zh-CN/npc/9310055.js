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
		/* Array(104000400,0,"红蜗牛王 45min"),
		Array(260010201,0,"仙人长老 45min"),
		Array(230020100,0,"火蚌壳 45min"),
		Array(101030404,0,"树妖王 45min"),
		Array(100040105,0,"浮士德 45min"),
		Array(220050100,0,"提莫 45min"),
		Array(100000005,0,"铁甲猪公园III 30min"),
		Array(105070002,0,"蘑菇王之墓30min"), 
		Array(110040000,0,"巨居蟹 45min"), 
		Array(250010304,0,"武陵流浪熊 45min"), 
		Array(200010300,0,"天空艾利捷 45min"), 
		Array(261030000,0,"奇美拉 45min"), 
		Array(250010503,0,"喵仙怪人 45min"), 
		Array(222010310,0,"九尾狐 45min"), 
		Array(105090900,0,"被诅咒的寺院 ???"),
		Array(107000300,0,"废弃多尔 ???"),
		Array(240040401,0,"寒霜冰龙 2h"),
		Array(702070400,0,"少林妖僧"),
		Array(220080000,0,"闹钟"),
		Array(230040420,0,"鱼王24h 2h"),
		Array(211042300,0,"扎昆入口"),
		Array(800040410,0,"#m800040410#"),
		Array(240050400,0,"暗黑龙王") */
		Array(104000400,50000,"#m104000400#5W【#r蜗牛王BOSS#k】"),
		Array(260010201,50000,"仙人掌沙漠5W【#r仙人掌BOSS#k】"),
		Array(230020100,50000,"#m230020100#5W【#r贝壳精BOSS#k】"),
		Array(101030404,50000,"#m101030404#5W【#r树妖王BOSS#k】"),
		Array(100040105,50000,"#m100040105#5W【#r僵尸猴BOSS#k】"),
		Array(220050100,50000,"#m220050100#5W【#r猫头鹰BOSS#k】"),
		Array(100000005,50000,"#m100000005#5W【#r蘑菇王BOSS#k】"),
		Array(105070002,50000,"#m105070002#5W【#r僵尸蘑菇王BOSS#k】"),
		Array(110040000,50000,"#m110040000#5W【#r贝壳王BOSS#k】"),
		Array(250010304,50000,"#m250010304#5W【#r流浪熊BOSS#k】"),
		Array(200010300,50000,"#m200010300#5W【#r艾力杰BOSS#k】"),
		Array(261030000,50000,"研究所通道5W【#r吉米啦BOSS#k】"),
		Array(250010503,50000,"妖怪的森林5W【#r大妙仙BOSS#k】"),
		Array(222010310,50000,"#m222010310#5W【#r九尾狐BOSS#k】"),
		Array(240020401,50000,"#m240020401#5W【#r喷火龙BOSS#k】"),
		Array(240020101,50000,"#m240020101#5W【#r格瑞芬BOSS#k】"),
		Array(105090900,50000,"#m105090900#5W【#r蝙蝠怪BOSS#k】"),
		Array(107000300,50000,"鳄鱼的深坛5W【#r大多尔BOSS#k】"),
		Array(240040401,50000,"大海兽峡谷5W【#r海兽BOSS#k】"),
		Array(702070400,50000,"藏经阁顶层5W【#r妖僧BOSS#k】"),
		Array(220080000,80000,"时间塔底部8W【#r闹钟BOSS#k】"),
		Array(230040420,100000,"皮亚奴斯洞10W【#r鱼王BOSS#k】"),
		Array(211042301,500000,"扎昆的入口50W【#r扎昆BOSS#k】"),
		//Array(800040410,0,"枫城天守阁【#r天皇BOSS#k】"),
		//Array(270050000,0,"忘却的黄昏【#r品克宾BOSS#k】"),
		Array(240050400,800000,"黑龙王洞穴80W【#r黑龙王BOSS#k】")

			 
														
		);

//------------------------------------------------------------------------

var monstermaps = Array(
		Array(104040000,800,"射手训练场　　　 　 适合  1 ~ 15 级玩家。"), 
		Array(103000101,800,"地铁一号线<第1地区> 适合 20 ~ 30 级玩家。"), 
	    Array(101040001,800,"野猪的领土　　　　　适合 20 ~ 35 级玩家。"), 
		Array(101040003,1000,"钢之黑怪之地　　　　适合 20 ~ 35 级玩家。"), 
		Array(101030001,1000,"野猪的领土Ⅱ　　　　适合 20 ~ 35 级玩家。"), 
		Array(105070001,2000,"蚂蚁广场 　　　　　 适合 20 ~ 40 级玩家。"), 
		Array(222010000,2000,"乌山入口　　　　　　适合 20 ~ 50 级玩家。"), 
		Array(230020000,2000,"东海叉路　　　　　　适合 30 ~ 40 级玩家。"), 
		Array(100040103,2000,"#m100040103# 	      适合 30 ~ 50 级玩家。"), 
		Array(200040000,2000,"云彩公园Ⅲ　　　　　适合 35 ~ 60 级玩家。"),
		Array(230010400,2000,"西海叉路　　　　　　适合 40 ~ 50 级玩家。"), 
		Array(105090301,5000,"#m105090301# 	      适合 40 ~ 60 级玩家。"), 
		Array(101030110,5000,"第1军营　　　　　　 适合 40 ~ 60 级玩家。"), 
		Array(106000002,5000,"危险的峡谷Ⅱ　　　　适合 40 ~ 60 级玩家。"), 
		Array(101030103,5000,"遗迹发掘地Ⅲ　　　　适合 40 ~ 60 级玩家。"), 
		Array(105090300,8000,"龙穴　　　　　　　　适合 40 ~ 70 级玩家。"), 
		Array(220010500,8000,"露台大厅　　　　　　适合 40 ~ 70 级玩家。"),
		Array(251010000,8000,"十年药草地　　　　　适合 45 ~ 60 级玩家。"), 
		Array(250020000,8000,"初级修炼场　　　　　适合 50 ~ 60 级玩家。"),
		Array(103000105,8000,"地铁一号线<第4地区> 适合 50 ~ 70 级玩家。"), 
		Array(800020130,8000,"大佛的邂逅　　　　　适合 50 ~ 70 级玩家。"),
		Array(211041400,8000,"死亡之林Ⅳ　　　　　适合 55 ~ 70 级玩家。"),
		Array(105040306,8000,"巨人之林 　　　　　 适合 60 ~ 80 级玩家。"), 
        Array(541010010,8000,"幽灵船２  　　　　　适合 60 ~ 90 级玩家。"),
		Array(200010301,8000,"黑暗庭院Ⅰ　　　　　适合 70 ~ 90 级玩家。"),
		Array(600020300,10000,"狼蛛洞穴Ⅰ　　　　　适合 80 ~ 120 级玩家。"), 
		Array(240010500,10000,"山羊峡谷　　　　    适合 85 ~ 120 级玩家。"),
		Array(240020100,10000,"火焰死亡战场　　　　适合 85 ~ 120 级玩家。"),
		Array(220070201,12000,"消失的时间　　　　　适合 85 ~ 120 级玩家。"), 
		Array(240040000,12000,"龙的峡谷　　　　    适合 95 ~ 120 级玩家。"),
		Array(551030100,12000,"阴森世界入口　　　　适合 95 ~ 120 级玩家。"),  
		Array(541020000,15000,"乌鲁城入口　　　　　适合 95 ~ 150 级玩家。"),
	    Array(240040500,15000,"龙之巢穴入口　　　　适合 100 ~ 150 级玩家。") 
		);

//------------------------------------------------------------------------

var townmaps = Array(
		//Array(910000000,0,"自由市场"), 
		//Array(701000210,0,"大擂台"), 
		Array(1000000,0,"彩虹岛新手村"), 
		Array(104000000,0,"明珠港"), 
		Array(100000000,0,"射手村"), 
		Array(101000000,0,"魔法密林"), 
		Array(102000000,0,"勇士部落"), 
		Array(103000000,0,"废弃都市"), 
		Array(120000000,0,"诺特勒斯号码头"),
		Array(105040300,0,"林中之城"),
		Array(110000000,0,"黄金海岸"),
		Array(140000000,0,"里恩"),
		Array(200000000,0,"天空之城"),
		Array(211000000,0,"冰峰雪域"), 
		Array(230000000,0,"水下世界"),  
		Array(222000000,0,"童话村"), 
		Array(220000000,0,"玩具城"),
		Array(701000000,0,"东方神州"),
		Array(250000000,0,"武陵"), 
		Array(702000000,0,"少林寺"), 
		Array(500000000,0,"泰国"),
		Array(260000000,0,"阿里安特"),  
		Array(600000000,0,"新叶城"), 
		Array(240000000,0,"神木村"),  
		Array(261000000,0,"马加提亚"), 
		Array(221000000,0,"地球防御本部"), 
		Array(251000000,0,"百草堂"),
		Array(701000200,0,"上海豫园"),
		Array(550000000,0,"吉隆大都市"),
		Array(130000000,0,"圣地"),
		Array(551000000,0,"甘榜村"),
		Array(801000000,0,"昭和村"), 
		Array(540010000,0,"新加坡机场"),
		Array(541000000,0,"新加坡码头"),
		Array(300000000,0,"艾林森林"), 
		Array(270000100,0,"时间神殿"), 
		Array(702100000,0,"藏经阁"), 
		Array(800000000,0,"古代神社"), 
		Array(130000200,0,"圣地岔路"),
		Array(741000208,0,"钓鱼场"),
		Array(925020000,0,"武陵道场入口"),
		Array(702090101,0,"英语村"),  
		Array(700000000,0,"红鸾宫")
		//Array(749020000,0,"国庆蛋糕地图")
		);

//------------------------------------------------------------------------

var fubenmaps = Array(
       // Array(109080000,0,"打椰子"),
       // Array(109080010,0,"冰地"),
       // Array(109040000,0,"向高地"),
		//Array(109030001,0,"上楼"),
		//Array(109060000,0,"滚雪球"),
		//Array(109010000,0,"寻宝"),
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
        Array(104010001,20000,"#m104010001#(花费2W金币)"),
        Array(101020004,20000,"#m101020004#(花费2W金币)"),
        Array(701010300,20000,"#m701010300#(花费2W金币)"),
		Array(110020001,20000,"#m110020001#(花费2W金币)"),
        Array(230020200,20000,"#m230020200#(花费2W金币)"),
        Array(222010400,20000,"#m222010400#(花费2W金币)"),
		Array(240030300,20000,"#m240030300#(花费2W金币)"),
		Array(250010700,20000,"#m250010700#(花费2W金币)")
		);
//---------  Array(270030400,0,"#m270030400#"),
		//Array(270030300,0,"#m270030300#"),
      //  Array(270030500,0,"#m270030500#")	---------------------------------------------------------------

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

   	    var add = "　　　　　　　　　#r#k\r\n\r\n";

//		add += "#r　　　　　　　　　新物品展览#k\r\n";

//		add += "#b座椅#k\r\n";

//		add += "#v3010154# #v3010179# #v3010169# #v3010171# #v3010174# #v3010182# #v3010183# #v3010053##b\r\n\r\n";

//		add += "#b坐骑#k\r\n";

//		add += "#v1902060# #v1912053# #v1902062# #v1912055# #v1902063# #v1912056# #v1902040# #v1912057#\r\n\r\n";

	//	add += "#L2##e#r#v2591009#副本大厅#l";

	//	add += "#L6##e#r#v2591003#跑商材料#l";
		
	//	add += "#L0##e#r#v2591008#城镇传送#l";

		add += "#L2#请送我去日蜈蚣#l\r\n\r\n";
		
     //   add += "#L3##e#r#v2591006#BOSS传送#l";
		
	  //  add += "#L10##e#r#v2591005#进阶BOSS#l"; 
		
	//	add += "#L4##e#r#v2591004#跳跳地图#l\r\n\r\n"; 
		
		//add += "#L6##d拍照地图#l\r\n\r\n"; 
		cm.sendSimple (add);    

//------------------------------------------------------------------------
				
	} else if (status == 1) {

	if (selection == 0){
		var selStr = "#d　　　　　　　　　选择你的目的地吧.#k#b";
		for (var i = 0; i < townmaps.length; i++) {
		selStr += "\r\n#L" + i + "#" + townmaps[i][2] + "";
		}
		cm.sendSimple(selStr);
		towns = 1;
		}

	if (selection == 1) {
		var selStr = "#d　　　　　　　　　选择你的目的地吧.#k#b";
		for (var i = 0; i < monstermaps.length; i++) {
		selStr += "\r\n#L" + i + "#" + monstermaps[i][2] + "";
		}
		cm.sendSimple(selStr);
		monsters = 1;
		}

	if (selection == 2) {
		cm.warp(701010320, 0);
		cm.dispose();
		}
if (selection == 10) {
		cm.warp(970000002, 0);
		cm.dispose();
		}
	if (selection == 3) {
		var selStr = "#k\r\n#d　　　　　　　　　选择你的目的地吧.#k#b";
		for (var i = 0; i < bossmaps.length; i++) {
		selStr += "\r\n#L" + i + "#" + bossmaps[i][2] + "";
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
		cm.gainMeso(-townmaps[chosenMap][1]);
		}else{
		cm.sendOk("你没有足够的金币哦!");
		}
		cm.dispose();

	} else if (monsters == 2) {
		if(cm.getMeso()>=monstermaps[chosenMap][1]){
		cm.warp(monstermaps[chosenMap][0], 0);
		cm.gainMeso(-monstermaps[chosenMap][1]);
		}else{
		cm.sendOk("你没有足够的金币哦!");
		}
		cm.dispose();

	} else if (bosses == 2) {
		if(cm.getMeso()>=bossmaps[chosenMap][1]){
		cm.warp(bossmaps[chosenMap][0], 0);
		cm.gainMeso(-bossmaps[chosenMap][1]);
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
		}else{
		cm.sendOk("你没有足够的金币哦!");
		}
		cm.dispose();
		} else if (psben == 2) {
		if(cm.getMeso()>=psbenmaps[chosenMap][1]){
		cm.warp(psbenmaps[chosenMap][0], 0);
		cm.gainMeso(-psbenmaps[chosenMap][1]);
		}else{
		cm.sendOk("你没有足够的金币哦!");
		}
		cm.dispose();
		
	}else if (expMaps == 2) {
		if(cm.getMeso()>=expMapsArray[chosenMap][1]){
		cm.warp(expMapsArray[chosenMap][0], 0);
		cm.gainMeso(-expMapsArray[chosenMap][1]);
		}else{
		cm.sendOk("你没有足够的金币哦!");
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


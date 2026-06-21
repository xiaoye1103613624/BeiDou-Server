
/*
	作者：狗哥
	QQ联系：1418181168
	制作时间：2022年/6月/25日
*/

var 广播 = "#fUI/CN_Chat/ChattingRoom/BtVolUp/0/normal/0#";
var xx = "#fItem/Etc/0427/04270001/Icon9/0#";  //小黄星
var 展示 = "抽奖界面";
var 购物状态 = false;
var 分割线 = "#fUI/Login.img/WorldSelect/channel/chgauge1#";
var 广播 = "#fUI/CN_Chat/ChattingRoom/BtVolUp/0/normal/0#";

var 返回 = 2147000000;
var 功能名称 = "快乐转蛋机";

var 快乐百宝卷元宝 = 1;//元宝购买价格
var 快乐百宝卷价格 = 30000000;//点券购买价格
var 快乐百宝卷抵用 = 40000000;//抵用购买价格

var 消耗物品 = { 代码: 5220000, 数量: 1 };//抽奖条件

var 最大输入数量 = 100;//

var 获得积分 = 1;//每次抽奖获得积分数量

var 播报道具 = [
1004549,//帽子
1102840,//披风
1082658,//手套
1052952,//盔甲
1073077,//鞋子
1132289,//腰带
1402214,//双手剑
1432182,//枪
1462208,//弩
1452220,//弓
1472230,//短枪
1332242,//刀
1382226,//仗
1482183,//拳甲

1402184,// 狂龙战士双手剑
1402142,// 君主双手剑
1402224,// 柳德之剑
1302107,// 神话之境

1022313,// T级眼镜
1022314,// T级眼镜
1022315,// T级眼镜
1022316,// T级眼镜
1022317,// T级眼镜
	
1032327, //T级耳环
1032328, // T级耳环
1032329, // T级耳环
1032330, // T级耳环
1032331, //T级耳环
	
1012752,// T级面饰
1012753,// T级面饰
1012754,// T级面饰
1012755,// T级面饰
1012756,// T级面饰
	
1122163,// T级项链
1122164,// T级项链
1122165,// T级项链
1122166,// T级项链
1122167,// T级项链
1092022,//T5盾牌
1092069,// 战龙盾牌
1092035,// 可乐盾牌
1092051,// 啤酒杯盾牌
1003843,// 奇怪的狐狸面具
1002850,// 
1102604,// 
1032234,//蓝色桃心耳环
1702472,// 
1112952,// 希那的愤怒
1112951,// 麦格纳斯的愤怒
1112666,// 霸王的永恒戒指
1113064,// 狂战士的不朽戒指	
4000464, // 中国心
2614000, // 突破石头
2049122, //正向混沌
3994720, //十万金币
3994732, //百万金币
3994730, // 千万金币
3994731, //一亿金币

1112763, //S级宝石戒指
1112767, //S级宝石戒指
1112771, //S级宝石戒指
1112775, //S级宝石戒指
2643002, //技能神石

]


var 积分商城 = [
	{ 物品: 1703193, 需要积分: 1000 }, //百裂刀
	{ 物品: 1032234, 需要积分: 500 }, //蓝色桃心耳环
	{ 物品: 1003843, 需要积分: 500 }, //奇怪的狐狸面具
	{ 物品: 1004549, 需要积分: 500 }, //神豪帽子
	{ 物品: 1102840, 需要积分: 500 }, //披风
	{ 物品: 1082658, 需要积分: 500 }, //手套
	{ 物品: 1052952, 需要积分: 500 }, //盔甲
	{ 物品: 1073077, 需要积分: 500 }, //鞋子
	{ 物品: 1132289, 需要积分: 500 }, //腰带
	{ 物品: 1402214, 需要积分: 200 }, //红色双手剑
	{ 物品: 1432182, 需要积分: 200 }, //枪
	{ 物品: 1462208, 需要积分: 200 }, //弩
	{ 物品: 1452220, 需要积分: 200 }, //弓
	{ 物品: 1472230, 需要积分: 200 }, //拳套
	{ 物品: 1492194, 需要积分: 200 }, //短枪
	{ 物品: 1332242, 需要积分: 200 }, //刀
	{ 物品: 1382226, 需要积分: 200 }, //仗
	{ 物品: 1482183, 需要积分: 200 }, //拳甲
	{ 物品: 2049104, 需要积分: 60 }, //勋章强化卷
	{ 物品: 2460005, 需要积分: 60 }, //勋章强化卷
	{ 物品: 2460007, 需要积分: 30 }, //勋章强化卷
	{ 物品: 2022699, 需要积分: 20 }, //勋章强化卷
	{ 物品: 3994731, 需要积分: 20 }, //一亿金币
	{ 物品: 2049122, 需要积分: 10 },
	{ 物品: 2049100, 需要积分: 5 },
    { 物品: 2340000, 需要积分: 1 },
]

var 奖池列表 = [//奖池几率在 1 ~ 1000之间(几率值越低，越难出现)
	{ 物品: 1004549, 数量: 1, 几率: 50 }, // 帽子
	{ 物品: 1102840, 数量: 1, 几率: 50 }, // 披风
	{ 物品: 1082658, 数量: 1, 几率: 50 }, // 手套
	{ 物品: 1052952, 数量: 1, 几率: 50 }, // 盔甲
	{ 物品: 1073077, 数量: 1, 几率: 50 }, //鞋子
	{ 物品: 1132289, 数量: 1, 几率: 30 }, // 腰带
		
	{ 物品: 1402214, 数量: 1, 几率: 200 }, // 双手剑
	{ 物品: 1432182, 数量: 1, 几率: 200 }, // 红色枪
	{ 物品: 1462208, 数量: 1, 几率: 210 }, // 弩
	{ 物品: 1452220, 数量: 1, 几率: 200 }, //弓
	{ 物品: 1472230, 数量: 1, 几率: 20 }, // 拳套
	{ 物品: 1492194, 数量: 1, 几率: 200 }, // 短枪
	{ 物品: 1332242, 数量: 1, 几率: 200 }, // 刀
	{ 物品: 1382226, 数量: 1, 几率: 210 }, // 仗
	{ 物品: 1482183, 数量: 1, 几率: 200 }, //拳甲

	{ 物品: 1402184, 数量: 1, 几率: 100 }, // 狂龙战士双手剑
	{ 物品: 1402142, 数量: 1, 几率: 80 }, // 君主双手剑
	{ 物品: 1402224, 数量: 1, 几率: 60 }, // 柳德之剑
	{ 物品: 1302107, 数量: 1, 几率: 40 }, // 神话之境

	{ 物品: 1703193, 数量: 1, 几率: 50 }, //百裂刀
	{ 物品: 1022313, 数量: 1, 几率: 500 }, //T级眼镜
	{ 物品: 1022314, 数量: 1, 几率: 400 },
	{ 物品: 1022315, 数量: 1, 几率: 300 },
	{ 物品: 1022316, 数量: 1, 几率: 100 },
	{ 物品: 1022317, 数量: 1, 几率: 10 },
	
	{ 物品: 1032327, 数量: 1, 几率: 500 }, //T级耳环
	{ 物品: 1032328, 数量: 1, 几率: 400 },
	{ 物品: 1032329, 数量: 1, 几率: 300 },
	{ 物品: 1032330, 数量: 1, 几率: 100 },
	{ 物品: 1032331, 数量: 1, 几率: 10 },
	
	{ 物品: 1012752, 数量: 1, 几率: 500 }, //T级面饰
	{ 物品: 1012753, 数量: 1, 几率: 400 },
	{ 物品: 1012754, 数量: 1, 几率: 300 },
	{ 物品: 1012755, 数量: 1, 几率: 100 },
	{ 物品: 1012756, 数量: 1, 几率: 10 },
	
	{ 物品: 1122163, 数量: 1, 几率: 500 }, //T级项链
	{ 物品: 1122164, 数量: 1, 几率: 400 },
	{ 物品: 1122165, 数量: 1, 几率: 300 },
	{ 物品: 1122166, 数量: 1, 几率: 100 },
	{ 物品: 1122167, 数量: 1, 几率: 10 },
	
	{ 物品: 1092022, 数量: 1, 几率: 500 }, //T5盾牌
	
	{ 物品: 1092069, 数量: 1, 几率: 500 },//战龙盾牌
	{ 物品: 1092035, 数量: 1, 几率: 500 },//可乐盾牌
	{ 物品: 1092051, 数量: 1, 几率: 500 },//啤酒杯盾牌
	{ 物品: 1003843, 数量: 1, 几率: 50 }, //奇怪的狐狸面具
	{ 物品: 1002850, 数量: 1, 几率: 50 },
	{ 物品: 1102604, 数量: 1, 几率: 50 },
	{ 物品: 1032234, 数量: 1, 几率: 50 }, //蓝色桃心耳环
	{ 物品: 1702472, 数量: 1, 几率: 50 },
	{ 物品: 1112763, 数量: 1, 几率: 200 }, //S级宝石戒指
	{ 物品: 1112767, 数量: 1, 几率: 200 }, //S级宝石戒指
	{ 物品: 1112771, 数量: 1, 几率: 200 }, //S级宝石戒指
	{ 物品: 1112775, 数量: 1, 几率: 200 }, //S级宝石戒指
	{ 物品: 1113189, 数量: 1, 几率: 700 }, //天堂戒指
	{ 物品: 1113190, 数量: 1, 几率: 700 }, //天堂戒指
	{ 物品: 1113191, 数量: 1, 几率: 700 }, //天堂戒指
	{ 物品: 1113192, 数量: 1, 几率: 700 }, //天堂戒指
	{ 物品: 1113193, 数量: 1, 几率: 700 }, //天堂戒指
	{ 物品: 1113194, 数量: 1, 几率: 700 }, //天堂戒指
	{ 物品: 1112952, 数量: 1, 几率: 200 },//希那的愤怒
	{ 物品: 1112951, 数量: 1, 几率: 200 },//麦格纳斯的愤怒
	{ 物品: 1112666, 数量: 1, 几率: 200 },//霸王的永恒戒指
	{ 物品: 1113064, 数量: 1, 几率: 60 },//狂战士的不朽戒指	
	{ 物品: 1402037, 数量: 1, 几率: 100 },
	{ 物品: 1402063, 数量: 1, 几率: 100 },
	{ 物品: 1442057, 数量: 1, 几率: 100 },
	{ 物品: 1012011, 数量: 1, 几率: 200 },//鼻子
	{ 物品: 1012056, 数量: 1, 几率: 200 },
	{ 物品: 1012132, 数量: 1, 几率: 100 },
	{ 物品: 1012309, 数量: 1, 几率: 100 },
	{ 物品: 1012190, 数量: 1, 几率: 200 },
	{ 物品: 1012189, 数量: 1, 几率: 300 },
	{ 物品: 1012188, 数量: 1, 几率: 500 },
	{ 物品: 1022021, 数量: 1, 几率: 200 }, //晕呼呼眼镜
	{ 物品: 1022022, 数量: 1, 几率: 200 }, //晕呼呼眼镜
	{ 物品: 1012373, 数量: 1, 几率: 1000 }, //休彼德曼的胡子
	{ 物品: 1132009, 数量: 1, 几率: 50 },
	{ 物品: 1132008, 数量: 1, 几率: 500 },
	{ 物品: 1132007, 数量: 1, 几率: 600 },
	{ 物品: 1132006, 数量: 1, 几率: 700 },
	{ 物品: 1132005, 数量: 1, 几率: 800 },
	{ 物品: 1022047, 数量: 1, 几率: 300 },
	{ 物品: 1022058, 数量: 1, 几率: 250 },
	{ 物品: 1022060, 数量: 1, 几率: 200 },
	{ 物品: 1022067, 数量: 1, 几率: 80 },
	{ 物品: 1122028, 数量: 1, 几率: 100 },
	{ 物品: 1122027, 数量: 1, 几率: 100 },
	{ 物品: 1122026, 数量: 1, 几率: 100 },
	{ 物品: 1122025, 数量: 1, 几率: 100 },
	{ 物品: 1122024, 数量: 1, 几率: 100 },
	{ 物品: 1050127, 数量: 1, 几率: 50 },
	{ 物品: 1050100, 数量: 1, 几率: 50 },
	{ 物品: 1051098, 数量: 1, 几率: 50 },
	{ 物品: 1051140, 数量: 1, 几率: 50 },
	{ 物品: 1002939, 数量: 1, 几率: 20 },//安全帽	
	
	{ 物品: 1442046, 数量: 1, 几率: 500},
	{ 物品: 1372038, 数量: 1, 几率: 500},
	{ 物品: 1372037, 数量: 1, 几率: 500},
	{ 物品: 1372036, 数量: 1, 几率: 500},
	{ 物品: 1372035, 数量: 1, 几率: 500},
	{ 物品: 1302105, 数量: 1, 几率: 500 },
	{ 物品: 1312039, 数量: 1, 几率: 500 },
	{ 物品: 1322065, 数量: 1, 几率: 500 },
	{ 物品: 1332081, 数量: 1, 几率: 500 },
	{ 物品: 1372046, 数量: 1, 几率: 500 },
	{ 物品: 1382062, 数量: 1, 几率: 500 },
	{ 物品: 1402053, 数量: 1, 几率: 500 },
	{ 物品: 1412035, 数量: 1, 几率: 500 },
	{ 物品: 1422039, 数量: 1, 几率: 500 },
	{ 物品: 1432050, 数量: 1, 几率: 500 },
	{ 物品: 1442071, 数量: 1, 几率: 500 },
	{ 物品: 1452062, 数量: 1, 几率: 500 },
	{ 物品: 1462056, 数量: 1, 几率: 500 },
	{ 物品: 1472077, 数量: 1, 几率: 500 },
	{ 物品: 1482029, 数量: 1, 几率: 300 },
	{ 物品: 1492030, 数量: 1, 几率: 300 },
	{ 物品: 1322026, 数量: 1, 几率: 900 },
	{ 物品: 1322025, 数量: 1, 几率: 900 },
	{ 物品: 1322024, 数量: 1, 几率: 900 },
	{ 物品: 1322023, 数量: 1, 几率: 900 },
	{ 物品: 1322022, 数量: 1, 几率: 900 },
	{ 物品: 1322021, 数量: 1, 几率: 1000 },
	{ 物品: 1442018, 数量: 1, 几率: 1000 },
	{ 物品: 1312169, 数量: 1, 几率: 1000 },
	{ 物品: 1372033, 数量: 1, 几率: 1000 },//圣贤短杖
	{ 物品: 1372017, 数量: 1, 几率: 1000 },//领路灯
	{ 物品: 1332053, 数量: 1, 几率: 1000 },//野外烧烤串	
	{ 物品: 1402014, 数量: 1, 几率: 10 },
	{ 物品: 1322027, 数量: 1, 几率: 20 },
	{ 物品: 1402044, 数量: 1, 几率: 1000 },
	{ 物品: 1302063, 数量: 1, 几率: 50 },
	{ 物品: 1302021, 数量: 1, 几率: 1000 },
	{ 物品: 1302022, 数量: 1, 几率: 1000 },
	{ 物品: 1302024, 数量: 1, 几率: 1000 },
	{ 物品: 1302031, 数量: 1, 几率: 1000 },
	{ 物品: 1302061, 数量: 1, 几率: 1000 },
	{ 物品: 1302013, 数量: 1, 几率: 1000 },
	{ 物品: 1322012, 数量: 1, 几率: 1000 },
	{ 物品: 1432015, 数量: 1, 几率: 1000 },
	{ 物品: 1432013, 数量: 1, 几率: 1000 },
	{ 物品: 1382041, 数量: 1, 几率: 200 },
	{ 物品: 1382016, 数量: 1, 几率: 1000 },
	{ 物品: 1382015, 数量: 1, 几率: 1000 },
	{ 物品: 1432008, 数量: 1, 几率: 900 },
	{ 物品: 1432039, 数量: 1, 几率: 200 },
	{ 物品: 1442021, 数量: 1, 几率: 900 },
	{ 物品: 1302016, 数量: 1, 几率: 900 },
	{ 物品: 1302017, 数量: 1, 几率: 400 },
	{ 物品: 1302025, 数量: 1, 几率: 300 },
	{ 物品: 1302026, 数量: 1, 几率: 200 },
	{ 物品: 1302027, 数量: 1, 几率: 100 },
	{ 物品: 1302028, 数量: 1, 几率: 50 },
	{ 物品: 1302029, 数量: 1, 几率: 10 },
	{ 物品: 1092049, 数量: 1, 几率: 50 },
	{ 物品: 1092050, 数量: 1, 几率: 100 },
	{ 物品: 1092008, 数量: 1, 几率: 500 },
	{ 物品: 1092030, 数量: 1, 几率: 500 },
	{ 物品: 1092029, 数量: 1, 几率: 500 },
	{ 物品: 1032025, 数量: 1, 几率: 500 },
	{ 物品: 1032032, 数量: 1, 几率: 500 },
	{ 物品: 1032035, 数量: 1, 几率: 500 },
	{ 物品: 1032047, 数量: 1, 几率: 500 },
	{ 物品: 1032058, 数量: 1, 几率: 500 },
	{ 物品: 1032057, 数量: 1, 几率: 500 },
	{ 物品: 1032056, 数量: 1, 几率: 500 },
	{ 物品: 1032055, 数量: 1, 几率: 500 },
	{ 物品: 1082149, 数量: 1, 几率: 100 },
	{ 物品: 1082148, 数量: 1, 几率: 200 },
	{ 物品: 1082147, 数量: 1, 几率: 200 },
	{ 物品: 1082146, 数量: 1, 几率: 200 },
	{ 物品: 1082150, 数量: 1, 几率: 200 },
	{ 物品: 1082145, 数量: 1, 几率: 200 },
	{ 物品: 1082002, 数量: 1, 几率: 1000 },
	{ 物品: 1082175, 数量: 1, 几率: 300 },//马绍尔手套
	{ 物品: 1082176, 数量: 1, 几率: 400 },//马绍尔手套
	{ 物品: 1082177, 数量: 1, 几率: 500 },//马绍尔手套
	{ 物品: 1082178, 数量: 1, 几率: 300 },//马绍尔手套
	{ 物品: 1082179, 数量: 1, 几率: 400 },//马绍尔手套	
	{ 物品: 1102041, 数量: 1, 几率: 100 },
	{ 物品: 1102040, 数量: 1, 几率: 200 },
	{ 物品: 1102042, 数量: 1, 几率: 100 },
	{ 物品: 1102043, 数量: 1, 几率: 200 },
	{ 物品: 1102163, 数量: 1, 几率: 50 },
	{ 物品: 2070005, 数量: 1, 几率: 500 },
	{ 物品: 2070006, 数量: 1, 几率: 150 }, //齿轮飞镖
	{ 物品: 4000464, 数量: 1, 几率: 200 },//中国心
	{ 物品: 2614000, 数量: 1, 几率: 150 },//突破石头
	{ 物品: 2049122, 数量: 8, 几率: 150 },//正向混沌
	{ 物品: 3994720, 数量: 1, 几率: 1000 }, //十万金币
	{ 物品: 3994732, 数量: 1, 几率: 800 }, //百万金币
	{ 物品: 3994730, 数量: 1, 几率: 800 }, //千万金币
	{ 物品: 3994731, 数量: 1, 几率: 300 },  //一亿金币
	{ 物品: 2643002, 数量: 1, 几率: 300 },  //技能神石
	
	
	
	

];

var 存取;
function start() {
	status = -1;

	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
		return;
	} else {
		if (status >= 0 && mode == 0) {
			cm.dispose();
			return;
		}
		if (mode == 1) {
			status++;
		} else {
			status--;
		}
		if (status == 0) {
			var text = "#w#e#d";
			switch (展示) {
				case "抽奖界面":
					text += "#g┏━#e#r冒险岛提示#g━━━━━━━━━━━━━━━━━┓\r\n";
					text += "\t#d" + 广播 + " 神豪装备每件增伤5%整套增伤20%共计50%\r\n";
					text += "\t#d" + 广播 + " 欢迎来到 [#r" + 功能名称 + "#d]\r\n";
					text += "\t#d" + 广播 + " 开启宝箱需要 #v" + 消耗物品.代码 + ":##b#z" + 消耗物品.代码 + "#\r\n";
					text += "\t#d" + 广播 + " 屏蔽功能是自动贩卖进商店\r\n";
					text += "#g┗━━━━━━━━━━━━━━━━━━━━━━━┛#d\r\n";
					text += " #L1#" + xx + "[#b开启宝箱#d]:需要[#b#z" + 消耗物品.代码 + "##d]" + xx + "#l\r\n";
					text += " #L2#" + xx + "[#k屏蔽道具#d]:抽奖获得时，自动贩卖至商店" + xx + "#l\r\n";
					text += " #L3#" + xx + "[#b取消屏蔽#d]:取消您已经屏蔽的道具" + xx + "#l\r\n";
					text += " #L4#" + xx + "[#b积分商城#d]:用百宝积分换取极品道具" + xx + "#l\r\n";
					text += " #L8#" + xx + "[#r购买百宝#d]:元宝购买#d快乐百宝卷" + xx + "#l\r\n";
			//		text += " #L5#" + xx + "[#b购买百宝#d]:#r点券购买#d快乐百宝卷" + xx + "#l\r\n";
			//		text += " #L7#" + xx + "[#b购买百宝#d]:#r抵用购买#d快乐百宝卷" + xx + "#l\r\n";
					text += " #L6#" + xx + "[#b抽奖仓库#d]:抽获的物品自动存入" + xx + "#l\r\n";
					text += "\r\n  " + 分割线 + "  奖池内容展示  " + 分割线 + "\r\n" + 内容展示();
					break;
				case "屏蔽道具":
					var 屏蔽道具 = 屏蔽道具内容();
					/*if (屏蔽道具.length != 0) {
						text += "#g┏━#e#r冒险岛提示#g━━━━━━━━━━━━━━━━━┓\r\n";
						text += "\t#d" + 广播 + " 目前已经选择屏蔽的道具如下：\r\n  ";
						for (var i = 0; i < 屏蔽道具.length; i++) {
							text += "#v" + 屏蔽道具[i]["itemid"] + ":#";
							if ((i + 1) % 9 == 0) {
								text += "\r\n  ";
							}
						}
						text += "\r\n#g┗━━━━━━━━━━━━━━━━━━━━━━━┛#d\r\n";
					}*/
					text += "#g┏━#e#r冒险岛提示#g━━━━━━━━━━━━━━━━━┓\r\n";
					text += "\t#d" + 广播 + " 请选择您想要屏蔽掉的道具：\r\n";
					var index = new Array();
					for (var j = 0; j < 奖池列表.length; j++) {
						if (index.indexOf(奖池列表[j].物品) === -1) {
							var 检测 = true;
							for (var k = 0; k < 屏蔽道具.length; k++) {
								if (奖池列表[j].物品 == 屏蔽道具[k]["itemid"]) {
									检测 = false;
									break;
								}
							}
							if (检测 == true) {
								index.push(奖池列表[j].物品);
							}
						}
					}
					for (var l = 0; l < index.length; l++) {
						text += "#L" + index[l] + "##v" + index[l] + ":##l";
						if ((l + 1) % 5 == 0) {
							text += "\r\n";
						}
					}
					text += "\r\n\r\n#g┗━━━━━━━━━━━━━━━━━━━━━━━┛#d\r\n";
					text += " #L666#" + xx + "[#b返回抽奖界面#d]" + xx + "#l\r\n";
					cm.sendSimple(text);
					break;
				case "取消屏蔽":
					var 屏蔽道具 = 屏蔽道具内容();
					if (屏蔽道具.length != 0) {
						text += "#g┏━#e#r冒险岛提示#g━━━━━━━━━━━━━━━━━┓\r\n";
						text += "\t#d" + 广播 + " 目前已经选择屏蔽的道具如下：\r\n";
						for (var m = 0; m < 屏蔽道具.length; m++) {
							text += "#L" + 屏蔽道具[m]["itemid"] + "##v" + 屏蔽道具[m]["itemid"] + ":##l";
							if ((m + 1) % 5 == 0) {
								text += "\r\n";
							}
						}
						text += "\r\n\r\n#g┗━━━━━━━━━━━━━━━━━━━━━━━┛#d\r\n";
						text += " #L666#" + xx + "[#b返回抽奖界面#d]" + xx + "#l\r\n";
					} else {
						text += " 您没有屏蔽任何抽奖道具呢！\r\n";
						text += " #L666#" + xx + "[#b返回抽奖界面#d]" + xx + "#l\r\n\r\n";
					}
					cm.sendSimple(text);
					break;
				case "抽奖仓库":
					text += "#g┏━#e#r冒险岛提示#g━━━━━━━━━━━━━━━━━┓\r\n";
					text += "\t#d" + 广播 + " 每次抽奖获取的物品都在这里取哦！\r\n";
					text += "\t\t  #L" + 返回 + "#" + xx + "[#b返回主界面#d]" + xx + "#l\r\n\r\n";
					text += "#g┗━━━━━━━━━━━━━━━━━━━━━━━┛#d\r\n";
					var list = 打开抽奖仓库();
					for (var i = 0, j = 1; i < list.length; i++) {
						text += "#L" + list[i]["itemid"] + "##v" + list[i]["itemid"] + ":##l";
						if (j % 6 == 0) {
							text += "\r\n";
						}
						j++;
					}
					text += " \r\n";
					cm.sendOk(text);
					break;
				default:
					cm.sendOk("#e#d出错了，截图联系管理员修复。，错误代码：0");
					cm.dispose();
					break;
			}
			text += "\r\n";
			cm.sendSimple(text);
		} else if (status == 1) {
			    var maxBtn = (展示 == "抽奖界面") ? 8 :   // 抽奖界面最多到按钮 8
			                 (展示 == "抽奖仓库") ? 2147000000 : 0;

			    if (selection == null || selection < 0 ||
			        ((展示 == "抽奖界面") && selection > maxBtn) ||
			        ((展示 == "抽奖仓库") && (selection != 返回 && isNaN(selection)))) {
			        cm.sendOk("操作异常，请重新尝试。");
					Packages.tools.FileoutputUtil.log("log\\玩家相关\\强开非法记录.log", "[" + cm.getName() + "] 抽奖NPC脚本【9330113】强开非法记录 selection=" + selection);
			        cm.dispose();
			        return;
			    }
			sele1 = selection;
			switch (展示) {
				case "抽奖仓库":
					if (sele1 == 返回) {
						展示 = "抽奖界面";
						start();
					} else {
						var 仓库数量 = 读取仓库数量(sele1);
						var 最高可取 = 仓库数量;
						var text = "#e#d";
						if (Math.floor(sele1 / 10000) == 207) {
							最高可取 = 1;
						}
						text += "#g┏━#e#r冒险岛提示#g━━━━━━━━━━━━━━━━━┓\r\n";
						text += "\t拥有该物品数量：#v" + sele1 + ":##b#z" + sele1 + "##r" + 仓库数量 + "#d件\r\n";
						text += "\t请在下方输入要取出多少件！\r\n";
						text += "#g┗━━━━━━━━━━━━━━━━━━━━━━━┛#d\r\n";
						cm.sendGetNumber(text, 1, 1, 最高可取);
					}
					break;
				case "抽奖界面":
					var text = "#e#d\r\n";
					switch (sele1) {
						case 6:
							展示 = "抽奖仓库"; start(); return;
							break;
						case 1:
							var itemsum = 物品数量(消耗物品.代码);
							text += " 您背包拥有：#v" + 消耗物品.代码 + ":##b#z" + 消耗物品.代码 + "##d  #r" + itemsum + "#d个 \r\n";
							var maxsum = (itemsum >= 最大输入数量 ? 最大输入数量 : itemsum);
							if (maxsum != 0) {
								text += " 最高可输入抽奖次数：#r" + maxsum + "#d次\r\n";
								text += " 每次消耗：#r" + 消耗物品.数量 + "#d 个 #v" + 消耗物品.代码 + ":##b#z" + 消耗物品.代码 + "#\r\n";
								cm.sendGetNumber(text, 1, 1, maxsum);
							} else {
								text += " 呃~~~您没有#v" + 消耗物品.代码 + ":##b#z" + 消耗物品.代码 + "##d 无法抽奖哦！\r\n";
								cm.sendOk(text);
								展示 = "抽奖界面";
								status = -1;
								return;
							}
							break;
						case 2: 展示 = "屏蔽道具"; start(); return;
						case 3: 展示 = "取消屏蔽"; start(); return;
						case 4:
							text += "#g┏━#e#r冒险岛提示#g━━━━━━━━━━━━━━━━━┓\r\n";
							text += "\t#d" + 广播 + " 账号共拥有 [#r" + getjf() + "#d] 积分#n\r\n";
							text += "#g┗━━━━━━━━━━━━━━━━━━━━━━━┛#d\r\n";
							for (var q = 0; q < 积分商城.length; q++) {
								text += " #L" + q + "##k购买:#v" + 积分商城[q].物品 + ":##b#z" + 积分商城[q].物品 + "# #k需要 [#r#e" + 积分商城[q].需要积分 + "#k#n] 积分#l\r\n";
							}
							cm.sendSimple(text);
							break;
						case 5:
							var 点券 = cm.getPlayer().getCSPoints(1);
							text += "#g┏━#e#r冒险岛提示#g━━━━━━━━━━━━━━━━━┓\r\n";
							text += "\t#k" + 广播 + " 账号共拥有 [#r" + 点券 + "#k] 点券\r\n\r\n";
							text += " 请在下方输入您要购买的数量：\r\n\r\n";
							text += " #v" + 消耗物品.代码 + ":##b#z" + 消耗物品.代码 + "# #d单价：#r" + 快乐百宝卷价格 + "#d点券\r\n\r\n";
							var sum = 点券 / 快乐百宝卷价格;
							text += "#g┗━━━━━━━━━━━━━━━━━━━━━━━┛#d\r\n";
							if (sum >= 1) {
								cm.sendGetNumber(text, 1, 1, sum);
							} else {
								text += " \t\t\t\t\t#r点券不足以购买！#d\r\n\r\n";
								cm.sendOk(text);
								status = -1;
								return;
							}
							break;
						case 8:
							var 元宝 = cm.getPlayer().getmoneyb();
							text += "#g┏━#e#r冒险岛提示#g━━━━━━━━━━━━━━━━━┓\r\n";
							text += "\t#k" + 广播 + " 账号共拥有 [#r" + 元宝 + "#k] 元宝\r\n\r\n";
							text += " 请在下方输入您要购买的数量：\r\n\r\n";
							text += " #v" + 消耗物品.代码 + ":##b#z" + 消耗物品.代码 + "# #d单价：#r" + 快乐百宝卷元宝 + "#d元宝\r\n\r\n";
							var sum = 元宝 / 快乐百宝卷元宝;
							text += "#g┗━━━━━━━━━━━━━━━━━━━━━━━┛#d\r\n";
							if (sum >= 1) {
								cm.sendGetNumber(text, 1, 1, sum);
							} else {
								text += " \t\t\t\t\t#r元宝不足以购买！#d\r\n\r\n";
								cm.sendOk(text);
								status = -1;
								return;
							}
							break;
						case 7:
							var 抵用 = cm.getPlayer().getCSPoints(2);
							text += "#g┏━#e#r冒险岛提示#g━━━━━━━━━━━━━━━━━┓\r\n";
							text += "\t#k" + 广播 + " 账号共拥有 [#r" + 抵用 + "#k] 抵用\r\n\r\n";
							text += " 请在下方输入您要购买的数量：\r\n\r\n";
							text += " #v" + 消耗物品.代码 + ":##b#z" + 消耗物品.代码 + "# #d单价：#r" + 快乐百宝卷抵用 + "#d抵用\r\n\r\n";
							var sum = 抵用 / 快乐百宝卷抵用;
							text += "#g┗━━━━━━━━━━━━━━━━━━━━━━━┛#d\r\n";
							if (sum >= 1) {
								cm.sendGetNumber(text, 1, 1, sum);
							} else {
								text += " \t\t\t\t\t#r抵用不足以购买！#d\r\n\r\n";
								cm.sendOk(text);
								status = -1;
								return;
							}
							break;	
						default:
							cm.sendOk("#e#d出错了，截图联系管理员修复。，错误代码：1_1");
							cm.dispose();
							break;
					}
					break;
				case "屏蔽道具":
					switch (sele1) {
						case 666:
							展示 = "抽奖界面";
							start();
							return;
						default:
							开始屏蔽道具(sele1);
							展示 = "屏蔽道具";
							start();
							return;
					}
				case "取消屏蔽":
					switch (sele1) {
						case 666:
							展示 = "抽奖界面";
							start();
							return;
						default:
							取消屏蔽道具(sele1);
							展示 = "取消屏蔽";
							start();
							return;
					}
				default:
					cm.sendOk("#e#d出错了，截图联系管理员修复。，错误代码：1");
					cm.dispose();
					break;
			}
		} else if (status == 2) {
			sele2 = selection;
			switch (展示) {
				case "抽奖仓库":
					if (sele1 >= 2000000) {
						if (!cm.canHold(sele1, sele2)) {
							cm.sendOk("#e#d你需要背包空间不足。");
							cm.dispose();
						} else {
							取出仓库数量(sele1, sele2);
							cm.gainItem(sele1, sele2);
							start();
						}
					} else {
						if (cm.getInventory(1).isFull(sele2 - 1)) {
							cm.sendOk("#e#d您的装备背包空间不足#r" + sele2 + "#d个。");
							cm.dispose();
						} else {
							取出仓库数量(sele1, sele2);
							for (var i = 0; i < sele2; i++) {
								cm.gainItem(sele1, 1);
							}
							start();
						}
					}
					break;
				case "抽奖界面":
					var text = "#e#d\r\n";
					switch (sele1) {
						case 1:
							/*if (空出背包格数(sele2)[0]) {
								cm.sendOk(空出背包格数(sele2)[1]);
								cm.dispose();
								return;
							}*/
							text += "#e#d本次抽奖详情：\r\n";
							var 获得文本 = "";
							var 售出文本 = "";
							cm.gainItem(消耗物品.代码, -消耗物品.数量 * sele2);
							gainjf(+获得积分 * sele2);
							cm.getPlayer().dropMessage(5, "获得了 " + sele2 + " 积分");
							var money = 0;
							var 屏蔽道具 = 屏蔽道具内容();
							for (var n = 0; n < sele2; n++) {
								var finalitem = Array();
								var random = Math.floor(Math.random() * 1000) + 1;
								for (var o = 0; o < 奖池列表.length; o++) {
									if (random <= 奖池列表[o].几率) {
										finalitem.push({ 道具: 奖池列表[o].物品, 数量: 奖池列表[o].数量 });
									}
								}
								var sj = Math.floor(Math.random() * finalitem.length);
								var itemId = finalitem[sj].道具;
								var quantity = finalitem[sj].数量;
								var 检测 = true;
								for (var p = 0; p < 屏蔽道具.length; p++) {
									if (itemId == 屏蔽道具[p]["itemid"]) {
										var 售出价格 = getPrice(屏蔽道具[p]["itemid"]) * quantity;
										money += 售出价格;
										检测 = false;
									}
								}
								if (检测 == true) {
									获得文本 += " 状态:#r获得#d#v" + itemId + ":##b#z" + itemId + "# #r" + quantity + "#d个\r\n";
									 for (var p = 0; p < 播报道具.length; p++) {
									if (itemId == 播报道具[p]) {
										//cm.道具喇叭(9,itemId,1,"快乐百宝箱")
										cm.道具喇叭(itemId, "恭喜 [" + cm.getPlayer().getName() + "] 在快乐百宝箱获得！ ");
										//getItemName
										//cm.sendOk(cm.getInventory(1).getItem(1))
										//cm.gainGachaponItemTime(itemId,1,"[" + cm.getPlayer().getName() + "]");
										//cm.Itemlaba("1111111",5076000,true);
									}
								}
									//cm.gainGachaponItem(itemId, quantity, 功能名称, " " + quantity + "个   恭喜 [" + cm.getPlayer().getName() + "] 获得！");
									//
									存入抽奖仓库(itemId, quantity);
								} else {
									售出文本 += " 状态:#r售出#d#v" + itemId + ":##b#z" + itemId + "# #r" + quantity + "#d个 价格(#r" + 售出价格 + "#d)\r\n";
								}
							}
							text += "" + 获得文本 + "" + 售出文本 + "\r\n";
							if (money != 0) {
								text += "\t\t\t合计售出总价格：[#r" + money + "#d]金币";
								cm.gainMeso(+money);
							}
							cm.sendOk(text);
							status = -1;
							return;
						case 4:
							购物状态 = true;
							text += "#g┏━#e#r冒险岛提示#g━━━━━━━━━━━━━━━━━┓\r\n";
							text += "\t#d" + 广播 + " 账号共拥有 [#r" + getjf() + "#d] 积分\r\n";
							text += "#g┗━━━━━━━━━━━━━━━━━━━━━━━┛#d\r\n";
							text += " 您要够买的道具：#v" + 积分商城[sele2].物品 + ":##b#z" + 积分商城[sele2].物品 + "#\r\n";
							text += " 购买该道具需要：[#r" + 积分商城[sele2].需要积分 + "#d]积分\r\n\r\n";
							if (积分商城[sele2].物品 <= 1999999) {
								text += " \t\t\t\t\t\t是否确定要购买呢？\r\n";
								cm.sendYesNo(text);
							} else {
								text += " \t请输入购买数量！\r\n";
								cm.sendGetNumber(text, 1, 1, 9999);
							}
							break;
						case 5:
							if (!cm.canHold(消耗物品.代码, sele2)) {
								cm.sendOk("#e#d你需要背包空间不足。");
								cm.dispose();
							} else {
								cm.gainNX(-快乐百宝卷价格 * sele2);
								cm.gainItem(消耗物品.代码, sele2);
								cm.sendOk("#e#d购买完成！获得：#v" + 消耗物品.代码 + ":##b#z" + 消耗物品.代码 + "# " + sele2 + "个");
								status = -1;
								return;
							}
							break;
						case 8:
							if (!cm.canHold(消耗物品.代码, sele2)) {
								cm.sendOk("#e#d你需要背包空间不足。");
								cm.dispose();
							} else {
								cm.setmoneyb(-快乐百宝卷元宝 * sele2);
								cm.gainItem(消耗物品.代码, sele2);
								cm.sendOk("#e#d购买完成！获得：#v" + 消耗物品.代码 + ":##b#z" + 消耗物品.代码 + "# " + sele2 + "个");
								status = -1;
								return;
							}
							break;
						case 7:
							if (!cm.canHold(消耗物品.代码, sele2)) {
								cm.sendOk("#e#d你需要背包空间不足。");
								cm.dispose();
							} else {
								cm.gainDY(-快乐百宝卷抵用 * sele2);
								cm.gainItem(消耗物品.代码, sele2);
								cm.sendOk("#e#d购买完成！获得：#v" + 消耗物品.代码 + ":##b#z" + 消耗物品.代码 + "# " + sele2 + "个");
								status = -1;
								return;
							}
							break;	
						default:
							cm.sendOk("#e#d出错了，截图联系管理员修复。，错误代码：2_1");
							cm.dispose();
							break;
					}
					break;
				case "屏蔽道具":
					break;
				case "取消屏蔽":
					break;
				default:
					cm.sendOk("#e#d出错了，截图联系管理员修复。，错误代码：2");
					cm.dispose();
					break;
			}
		} else if (status == 3) {
			sele3 = selection;
			if (购物状态 == false) {
				展示 = "抽奖界面";
				start();
				return;
			} else {
				var 积分 = getjf();
				if (sele3 == -1) {
					if (积分 < 积分商城[sele2].需要积分) {
						cm.sendOk("#e#d呃~~~积分不够呢！");
		cm.dispose();
					} else if (cm.canHold(积分商城[sele2].物品,1) == false) {
						cm.sendOk("#e#d背包空间不足！");
						cm.dispose();
					} else {
						cm.gainItem(积分商城[sele2].物品, 1);
						gainjf(-积分商城[sele2].需要积分);
						cm.getPlayer().dropMessage(5, "使用了 " + 积分商城[sele2].需要积分 + " 积分");
						cm.sendOk("#e#d购买完成，请打开背包查看！");
						cm.dispose();
					}
				} else {
					if (积分 < 积分商城[sele2].需要积分 * sele3) {
						cm.sendOk("#e#d呃~~~积分不够呢！");
						status = -1;
						return;
					} else {
						cm.gainItem(积分商城[sele2].物品, sele3);
						gainjf(-积分商城[sele2].需要积分 * sele3);
						cm.getPlayer().dropMessage(5, "使用了 " + (积分商城[sele2].需要积分 * sele3) + " 积分");
						cm.sendOk("#e#d购买完成，请打开背包查看！");
						cm.dispose();
					}
				}
			}
		}
	}
}

function 存入抽奖仓库(itemid, sum) {
	var 检测 = true;
	var 玩家名称 = cm.getPlayer().getName();
	var list = 打开抽奖仓库();
	for (var i = 0; i < list.length; i++) {
		if (itemid == list[i]["itemid"]) {
			检测 = false;
			sqlMultiPurpose("UPDATE 抽奖仓库 SET count = count + " + sum + " WHERE itemid = " + itemid + " and name = '" + 玩家名称 + "'");
			break;
		}
	}
	if (检测 == true) {
		sqlMultiPurpose("INSERT INTO 抽奖仓库 (name, itemid, count) values ('" + 玩家名称 + "'," + itemid + "," + sum + ")");
	}
}

function 取出仓库数量(itemid, sum) {
	var 玩家名称 = cm.getPlayer().getName();
	sqlMultiPurpose("UPDATE 抽奖仓库 SET count = count - " + sum + " WHERE itemid = " + itemid + " and name = '" + 玩家名称 + "'");
	if (读取仓库数量(itemid) == 0) {
		sqlMultiPurpose("DELETE FROM 抽奖仓库 WHERE name = '" + 玩家名称 + "' AND itemid = " + itemid + "");
	}
}

function 读取仓库数量(itemid) {
	var 玩家名称 = cm.getPlayer().getName();
	var ret = sqlSelect("SELECT * FROM 抽奖仓库 WHERE name = '" + 玩家名称 + "' AND itemid = " + itemid + "");
	return ret[0]["count"];
}

function 打开抽奖仓库() {
	var 玩家名称 = cm.getPlayer().getName();
	return sqlSelect("SELECT * FROM 抽奖仓库 WHERE name = '" + 玩家名称 + "'");
}

function 空出背包格数(sum) {
	var text = "#e#d\r\n";
	var boolean = false;
	var 位置;
	for (var i = 1; i <= 5; i++) {
		switch (i) {
			case 1: 位置 = "装备"; break;
			case 2: 位置 = "消耗"; break;
			case 3: 位置 = "设置"; break;
			case 4: 位置 = "其他"; break;
			case 5: 位置 = "特殊"; break;
		}
		if (cm.getInventory(i).isFull(sum - 1)) {
			text += "#d请保证背包#b" + 位置 + "栏#d至少有 #r" + sum + "#d 个空格\r\n";
			boolean = true;
		}
	}
	return list = [boolean, text];
}

function 物品数量(itemid) {
	return cm.getPlayer().getItemQuantity(itemid, false);
}

function 内容展示() {
	var index = new Array();
	var text = "";
	for (var i = 0; i < 奖池列表.length; i++) {
		//text += "#v" + 奖池列表[i].物品 + ":# 代码:" + 奖池列表[i].物品 + " 几率:" + 奖池列表[i].几率 + "\r\n";
		if (index.indexOf(奖池列表[i].物品) === -1) {
			index.push(奖池列表[i].物品);
		}
	}
	for (var j = 0; j < index.length; j++) {
		text += "#v" + index[j] + ":#";
	}
	return text;
}

//"SELECT * FROM 世界BOSS WHERE id = " + id + ""
//"UPDATE 世界BOSS SET " + type + " = " + num + " WHERE id = ?"
//"INSERT INTO 世界BOSS (玩家名称, id, 怪物代码, 击杀积分, 地图代码, 积分, 技能点, 状态) values (?,?,?,?,?,?,?,?)"


function getPrice(itemid) {//获取Wz 售卖价格
	var price = Packages.server.MapleItemInformationProvider.getInstance().getWholePrice(itemid);
	return price;
}

function 取消屏蔽道具(itemid) {
	var id = cm.getPlayer().getId();
	sqlMultiPurpose("DELETE FROM 屏蔽奖池道具 WHERE id = " + id + " AND itemid = " + itemid + "");
}

function 开始屏蔽道具(itemid) {
	var id = cm.getPlayer().getId();
	sqlMultiPurpose("INSERT INTO 屏蔽奖池道具 (id, itemid) values (" + id + "," + itemid + ")");
}

function 屏蔽道具内容() {
	var id = cm.getPlayer().getId();
	return sqlSelect("SELECT * FROM 屏蔽奖池道具 WHERE id = " + id + "");
}

function getjf() {
	return getAccData("抽奖积分");
}

function gainjf(sum) {
	var accid = cm.getPlayer().getAccountID();
	sqlMultiPurpose("UPDATE accounts SET 抽奖积分 = 抽奖积分 + " + sum + " WHERE id = " + accid + "");
}

function getAccData(type) {//
	var accid = cm.getPlayer().getAccountID();
	var t = 0;
	var con = cm.getConnection();
	var ps = con.prepareStatement("SELECT * FROM accounts WHERE id = " + accid + "");
	var rs = ps.executeQuery();
	while (rs.next()) {
		t = rs.getInt(type);
	}
	rs.close();
	ps.close();
	con.close();
	return t;
}

function sqlSelect(sql) {
	var con = cm.getConnection();
	var ret = new Array();
	var ps = con.prepareStatement(sql);
	var rs = ps.executeQuery();
	var metaData = ps.getMetaData();
	while (rs.next()) {
		var rsdata = new java.util.HashMap();
		for (var j = 1; j <= metaData.getColumnCount(); j++) {
			columnLabel = metaData.getColumnLabel(j);
			rsdata.put(columnLabel, rs.getObject(columnLabel));
		}
		if (!rsdata.isEmpty()) {
			ret.push(rsdata);
		}
	}
	rs.close();
	ps.close();
	con.close();
	return ret;
}

function sqlMultiPurpose(sql) {//

	var con = cm.getConnection();
	var ps = con.prepareStatement(sql);
	ret = ps.executeUpdate();
	ps.close();
	con.close();
	return ret;
}
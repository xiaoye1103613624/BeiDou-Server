var status = -1;
var 蓝色箭头 = "#fUI/UIWindow/Quest/icon2/7#";
var 射手村染色高级会员卡 = 5151001;
var 射手村护肤中心会员卡 = 5153000;
var 皇家理发卷 = 5150040;
var 皇家整容卷 = 5152053;
var 超级明星美发卡 = 5150038;

var hair_Colo_boy1 = [33150,34070,34070,34080,34090,34100,34110,34120,34130,34140,34150,34160,34170,34180,34190,34200,
				34210,34220,34230,34240,34250,34260,34270,34280,34290,34300,34310,34320,34330,34340,34350,34360,34370,
				34380,34400,34410,34420,34430,34440,34450,34470,34480,34490,34510,34540,34560,34590,34600,34610,34620,
				34630,34640,34650,34660,34670,34680,34690,34700,34710,34720,34730,34740,34750,34760,34770,34780,34790,
				34800,34810,34820,34830,34840,34850,34860,34870,34880,34890,34900,34910,34940,34950,34960,34970,34980,
				34990,35000,35010,35020,35030,35040,35050,35060,35070,35080,35090,35100,35110,35120,35150,35160,35170,
				35180,35190,35200,35210,35220,35230,35240,35250,35260,35270,35280,35290,35300,35310,35330,35340,35350,
				35360,35400,35410,35420,35430,35440,35450,35460];//男发型1*/

var hair_Colo_boy2 = [35470,35480,35490,35500,35510,35520,35530,35540,35550,35560,35570,36510,36520,36530,36540,36550,
				36560,36570,36580,36590,36600,36620,36630,36640,36650,36670,36680,36690,36700,36710,36720,36730,36740,
				36750,36760,36770,36780,36790,36800,36810,36820,36830,36840,36850,36880,36890,36900,36910,36920,36930,
				35960,36000,36010,36020,36030,36040,36050,36070,36080,36090,36100,36110,36120,36130,36140,36150,36160,
				36170,36180,36190,36200,36210,36220,36230,36240,36250,36260,36280,36290,36300,36310,36320,36330,36340,
				36350,36360,36370,36380,36390,36400,36410,36420,36430,36440,36450,36460,36470,36480,36490,35750,35760,
				35770,35780,35790,35800,35820,35830,35950,35600,35620,35630,35640,35650,35660,35680,35690,35700,35710,
				35720,35730];//男发型2

var hair_Colo_boy3 = [37440,37450,37460,36940,36950,36980,36990,37000,37010,37020,37030,37040,37050,37060,37070,37080,
				37090,37100,37110,37120,37130,37140,37180,37190,37200,37210,37220,37230,37240,37250,37260,37270,37280,
				37290,37300,37310,37320,37330,37340,37350,38270,37500,37510,37520,37530,37540,37550,37560,37570,37580,
				37590,37600,37610,37620,37630,37640,37650,38010,38020,38030,38040,38050,38060,38070,38080,38090,38100,
				38110,38120,38130,38220,38240,38250,38260,37370,37380,37400,37410,37420,37880,37890,37900,37910,37920,
				37930,37940,37950,37960,37970,37980,37990,38000];//男发型 
//
var hair_Colo_boy4 = [37670,37680,37690,37700,37710,37720,37730,37740,37750,37760,37770,37780,37790,37800,37810,37820,
				37830,37840,37850,37860,38160,38280,38290,38300,38310,38320,38330,38340,38350,38360,38370,38380,38390,
				38400,38410,38420,38430,38440,38450,38460,38470,38480,38490,38500,38520,38530,38540,38560,38570,38580,
				38590,38600,38610,38620,38630,38640,38650,38660,38670,38680,38690,38700,38710,38730,38740,38750,38760,
				38770,38790,38800,38810,38840,38850,38860,38880,38890,38900,38910,38930,38940,34870,38150];//女发型 
//
var hair_Colo_boy5 = [33170,33180,33190,33240,33250,36860,33260,33270,33280,33290,33300,33310,33320,33330,33340,33360,
				33370,33380,33390,33400,33410,33430,33440,33460,33470,33480,33500,33520,33540,33550,33580,33600,33630,
				33640,33660,33810,33930,33940,33950,33960];//男发型 
//456789是脸型    
var face_Colo_boy9 = [24001,24002,24003,24004,24007,24008,24009,24010,24011,24012,24013,24014,24015,24016,24017,24018,
				24019,24020,24021,24022,24023,24024,24025,24026,24027,24028,24029,24030,24031,24032,24035,24036,24037,
				24038,24039,24040,24041,24050,24051,24052,24053,24054,24055,24056,24057,24058,24059,24060,24061,24062,
				24063,24064,24065,24066,24067,24068,24069,24070,24071,24072,24073,24074,24075,21863,21865,21868,21877,
				21878,21879,21880,21881,21882,21883,21885,21886,21827,21828,21829,21830,21831,21833,21834,21835,21836,
				21838,21841,21842,21843,21844,21845,21846,21847,21848,21849,21850,21852,21853,21854,21855,21858,21860,
				24087,24088,24090,24097,24098,24077,24078,24079,24080,24081,24082];

var face_Colo_boy10 =[21887,21888,21889,21891,21892,21893,21896,21899,23000,23001,23002,23003,23004,23005,23006,25006,
				25007,23099,25000,23023,23024,23025,23026,23027,23028,23029,23030,23031,23032,23033,23034,23035,23038,
				23039,23040,23041,23042,23043,23044,23053,23054,23055,23056,23057,23058,23059,23060,23061,23062,23063,
				23064,23065,23066,23067,23068,23069,23070,23071,23072,23073,23074,23075,23076,23077,23078,23079,23008,
				23010,23011,23012,23013,23014,23015,23016,23017,23018,23019,23020,23021];//



var hair_Colo_new;
var hair_Colo_new1;
	function action(mode, type, selection) {
		if (mode == 0) {
			cm.dispose();
			return;
		} else {
			status++;
		}

		if (status == 0) {
			var text = "";
			//text += "如果你有 #b#t5150040##k, 我就可以施展我的技术为了打造属于您的发型。\r\n";
			//text += "#L0#使用: #i5150040##b#t5150040##k随机换发型#l\r\n\r\n\r\n";
			text += "#d#e---------↓↓↓超级明星美容区域↓↓↓---------#k#n\r\n\r\n"
			/*text += "这里为你准备了最新 发型/脸型,不要挑花眼哦!\r\n";
			text += "进行手术需要#v" + 超级明星美发卡 + "##b#z" + 超级明星美发卡 + "##k\r\n";
			text += "#e#r- 发型区域#n#k\r\n";
			text += "#L1##rHOT#k选择发型第一版 x130 款#l\r\n";
			text += "#L2##rHOT#k选择发型第二版 x130 款#l#k\r\n";
			text += "#L3##rHOT#k选择发型第三版 x130 款#l#k\r\n";
			text += "#L4##rHOT#k选择发型第四版 x130 款#l#k\r\n";
			text += "#L5##rHOT#k选择发型第五版 x130 款#l#k\r\n\r\n";
			text += "#e#r- 脸型区域#n#k\r\n";*/
			text += "#L9##k第一版美容(男女通用) x130 款#rNEW#l#k\r\n\r\n";
			text += "#L10#第二版美容(男女通用) x130 款#rNEW#l#k\r\n\r\n";
			//text += "#e#r- 皮肤区域#n#k\r\n";
			text+=  "#L88# #r皮肤护理 #b(使用：#v5153000##z5153000#)#l\r\n\r\n"
			//text += "#e#r- 染发区域#n#k\r\n";
			text+=  "#L66# #r染发护理 #b(使用：#v5151001##z5151001#)#l\r\n"			
			cm.sendSimple(text);
		} else if (status == 1) {
			if(selection == 0){
				beauty = 0;
				var hair = cm.getPlayerStat("HAIR");
				hair_Colo_new = [];

				if (cm.getPlayerStat("GENDER") == 0) {
				hair_Colo_new = [35280,36490,35220,36790,36740,36810,35050,36890,36910,33000,35000,35010,35020,35030,35040,35050,35060,35070,35080,35090,35100,35110,35120,35130,35150,35160,35170,35180,35190,35210,35230,35240,35250,35260,35270,35290,35300,35330,35340,35350,35360,35430,35440,35460,35470,35510,35550,35560,35600,35630,35640,35650,35660,35680,35690,35710,35720,35750,35760,35770,35780,36690,36710,36720,36730,36740,36750,36760,36770,36780,36790,36800,36810,36820,36830,36840,36850,36860,36880,36890,36900,36910,36920,36930,36940,36950,36980,36990,33810,33980,33670,33580,33320,36000,36010,36020,36030,36040,36050,36070,36080,36090,36110,36120,36130,36140,36150,36170,36180,36190,36210,36220,36230,36240,36280,36300,36310,36330,36440,36450,36470,36510,36520,36560,36580,36590,36680,36700,33150];
				} else {
				hair_Colo_new = [31560,31230,36980,34450,34970,34890,34860,34810,34770,34750,34670,34600,33140,34160,34300,34310,34260,34240,34210,38290,38160,38100,38020,38010,38120,34060,34870,34800,34760,34330,34840,34850,34830,34110,34510,34250,34270,38900,38910,38930,38940,38890,38880,38860,38850,38840,38810,38800,38700,38710,38730,38740,38390,38760,38770,38790,38600,38610,38620,38630,38640,38650,38660,38670,38680,38690,38500,38520,38530,38540,38560,38570,38580,38400,38410,38420,38430,38450,38460,38470,38480,38490,38300,38320,38330,38340,38350,38360,38370,38380,38220,38240,38250,38260,38270,38280,38110,38130,38150,38000,38030,38040,38050,38060,38070,38080,38090,37900,37910,37920,37930];
				}
				for (var i = 0; i < hair_Colo_new.length; i++) {
				hair_Colo_new[i] = hair_Colo_new[i] + (hair % 10);
				}
				cm.sendYesNo("确定要使用 #b#t5150040##k 随机剪发了？？");
			}else if (selection == 1) {
				var hair = cm.getPlayerStat("HAIR");
				var face = cm.getPlayerStat("FACE");
				if (cm.getPlayerStat("GENDER") == 0) {
					beauty = 1;
					cm.sendStyle("确定要使用 #b#t" + 超级明星美发卡 + "##k #r在1版#k剪发了？？\r\n目前你的发型是:#t" + cm.getPlayer().getHair() + "#\r\n以下是[#r超级皇家理发库#k]中的所有发型", 超级明星美发卡, hair_Colo_boy1);
				} else {

					beauty = 1;
				cm.sendStyle("确定要使用 #b#t" + 超级明星美发卡 + "##k #r在1版#k剪发了？？\r\n目前你的发型是:#t" + cm.getPlayer().getHair() + "#\r\n以下是[#r超级皇家理发库#k]中的所有发型", 超级明星美发卡, hair_Colo_boy1);
				}
			} else if (selection == 2) {
				if (cm.getPlayerStat("GENDER") == 0) {
					beauty = 2;
					cm.sendStyle("确定要使用 #b#t" + 超级明星美发卡 + "##k #r在2版#k剪发了？？\r\n目前你的发型是:#t" + cm.getPlayer().getHair() + "#\r\n以下是[#r超级皇家理发库#k]中的所有发型", 超级明星美发卡, hair_Colo_boy2);
				} else {
					beauty = 2;
					cm.sendStyle("确定要使用 #b#t" + 超级明星美发卡 + "##k #r在2版#k剪发了？？\r\n目前你的发型是:#t" + cm.getPlayer().getHair() + "#\r\n以下是[#r超级皇家理发库#k]中的所有发型", 超级明星美发卡, hair_Colo_boy2);
						}
						
					} else if (selection == 3) {
				if (cm.getPlayerStat("GENDER") == 0) {
					beauty = 3;
					cm.sendStyle("确定要使用 #b#t" + 超级明星美发卡 + "##k #r在3版#k剪发了？？\r\n目前你的发型是:#t" + cm.getPlayer().getHair() + "#\r\n以下是[#r超级皇家理发库#k]中的所有发型", 超级明星美发卡, hair_Colo_boy3);
				} else {
					beauty = 3;
					cm.sendStyle("确定要使用 #b#t" + 超级明星美发卡 + "##k #r在3版#k剪发了？？\r\n目前你的发型是:#t" + cm.getPlayer().getHair() + "#\r\n以下是[#r超级皇家理发库#k]中的所有发型", 超级明星美发卡, hair_Colo_boy3);
				    }

							
					} else if (selection == 4) {
				if (cm.getPlayerStat("GENDER") == 0) {
					beauty = 4;
					cm.sendStyle("确定要使用 #b#t" + 超级明星美发卡 + "##k #r在4版#k剪发了？？\r\n目前你的发型是:#t" + cm.getPlayer().getHair() + "#\r\n以下是[#r超级皇家理发库#k]中的所有发型", 超级明星美发卡, hair_Colo_boy4);
				} else {
					beauty = 4;
					cm.sendStyle("确定要使用 #b#t" + 超级明星美发卡 + "##k #r在4版#k剪发了？？\r\n目前你的发型是:#t" + cm.getPlayer().getHair() + "#\r\n以下是[#r超级皇家理发库#k]中的所有发型", 超级明星美发卡, hair_Colo_boy4);
				    }

							
					} else if (selection == 5) {
				if (cm.getPlayerStat("GENDER") == 0) {
					beauty = 5;
					cm.sendStyle("确定要使用 #b#t" + 超级明星美发卡 + "##k #r在5版#k剪发了？？\r\n目前你的发型是:#t" + cm.getPlayer().getHair() + "#\r\n以下是[#r超级皇家理发库#k]中的所有发型", 超级明星美发卡, hair_Colo_boy5);
				} else {
					beauty = 5;
					cm.sendStyle("确定要使用 #b#t" + 超级明星美发卡 + "##k #r在5版#k剪发了？？\r\n目前你的发型是:#t" + cm.getPlayer().getHair() + "#\r\n以下是[#r超级皇家理发库#k]中的所有发型", 超级明星美发卡, hair_Colo_boy5);
				    }

				}else if(selection == 88){
			    cm.dispose();
                cm.openNpc(1540107,3);
				}else if(selection == 66){
			    cm.dispose();
                cm.openNpc(1540107,2);

				
					} else if (selection == 6) {
				if (cm.getPlayerStat("GENDER") == 0) {
					beauty = 6;
					cm.sendStyle("确定要使用 #b#t" + 超级明星美发卡 + "##k #r固定!!#k剪发了？？\r\n目前你的发型是:#t" + cm.getPlayer().getHair() + "#\r\n以下是[#r超级皇家理发库#k]中的所有发型", 超级明星美发卡, hair_Colo_boy6);
				} else {
					beauty = 6;
					cm.sendStyle("确定要使用 #b#t" + 超级明星美发卡 + "##k #r固定!!#k剪发了？？\r\n目前你的发型是:#t" + cm.getPlayer().getHair() + "#\r\n以下是[#r超级皇家理发库#k]中的所有发型", 超级明星美发卡, hair_Colo_boy6);
				    }

							
					} else if (selection == 7) {
				if (cm.getPlayerStat("GENDER") == 0) {
					beauty = 7;
					cm.sendStyle("确定要使用 #b#t" + 超级明星美发卡 + "##k #r固定!!#k剪发了？？\r\n目前你的发型是:#t" + cm.getPlayer().getHair() + "#\r\n以下是[#r超级皇家理发库#k]中的所有发型", 超级明星美发卡, hair_Colo_boy7);
				} else {
					beauty = 7;
					cm.sendStyle("确定要使用 #b#t" + 超级明星美发卡 + "##k #r固定!!#k剪发了？？\r\n目前你的发型是:#t" + cm.getPlayer().getHair() + "#\r\n以下是[#r超级皇家理发库#k]中的所有发型", 超级明星美发卡, hair_Colo_boy7);
				    }

							
					} else if (selection == 8) {
				if (cm.getPlayerStat("GENDER") == 0) {
					beauty = 8;
					cm.sendStyle("确定要使用 #b#t" + 超级明星美发卡 + "##k #r固定!!#k剪发了？？\r\n目前你的发型是:#t" + cm.getPlayer().getHair() + "#\r\n以下是[#r超级皇家理发库#k]中的所有发型", 超级明星美发卡, hair_Colo_boy8);
				} else {
					beauty = 8;
					cm.sendStyle("确定要使用 #b#t" + 超级明星美发卡 + "##k #r固定!!#k剪发了？？\r\n目前你的发型是:#t" + cm.getPlayer().getHair() + "#\r\n以下是[#r超级皇家理发库#k]中的所有发型", 超级明星美发卡, hair_Colo_boy8);
				    }

					
				} else if (selection == 9) {
				if (cm.getPlayerStat("GENDER") == 0) {
					beauty = 9;
					cm.sendStyle("确定要使用 #b#t" + 超级明星美发卡 + "##k #r固定!!#k了？？\r\n目前你的脸型是:#t" + cm.getPlayer().getFace() + "#\r\n以下是[#r超级皇家理发库#k]中的所有脸型", 超级明星美发卡, face_Colo_boy9);
				} else {
					beauty = 9;
					cm.sendStyle("确定要使用 #b#t" + 超级明星美发卡 + "##k #r固定!!#k脸型了？？\r\n目前你的脸型是:#t" + cm.getPlayer().getFace() + "#\r\n以下是[#r超级皇家理发库#k]中的所有脸型", 超级明星美发卡, face_Colo_boy9);
						}
			} else if (selection == 10) {
				if (cm.getPlayerStat("GENDER") == 0) {
					beauty = 10;
					cm.sendStyle("确定要使用 #b#t" + 超级明星美发卡 + "##k #r固定!!#k了？？\r\n目前你的脸型是:#t" + cm.getPlayer().getFace() + "#\r\n以下是[#r超级皇家理发库#k]中的所有脸型", 超级明星美发卡, face_Colo_boy10);
				} else {
					beauty = 10;
					cm.sendStyle("确定要使用 #b#t" + 超级明星美发卡 + "##k #r固定!!#k脸型了？？\r\n目前你的脸型是:#t" + cm.getPlayer().getFace() + "#\r\n以下是[#r超级皇家理发库#k]中的所有脸型", 超级明星美发卡, face_Colo_boy10);
						}
				} else if (selection ==11) {
				if (cm.getPlayerStat("GENDER") == 0) {
					beauty = 11;
					cm.sendStyle("确定要使用 #b#t" + 超级明星美发卡 + "##k #r固定!!#k了？？\r\n目前你的脸型是:#t" + cm.getPlayer().getFace() + "#\r\n以下是[#r超级皇家理发库#k]中的所有脸型", 超级明星美发卡, face_Colo_boy11);
				} else {
					beauty = 11;
					cm.sendStyle("确定要使用 #b#t" + 超级明星美发卡 + "##k #r固定!!#k脸型了？？\r\n目前你的脸型是:#t" + cm.getPlayer().getFace() + "#\r\n以下是[#r超级皇家理发库#k]中的所有脸型", 超级明星美发卡, face_Colo_boy11);
						}
				} else if (selection == 12) {
				if (cm.getPlayerStat("GENDER") == 0) {
					beauty = 12;
					cm.sendStyle("确定要使用 #b#t" + 超级明星美发卡 + "##k #r固定!!#k了？？\r\n目前你的脸型是:#t" + cm.getPlayer().getFace() + "#\r\n以下是[#r超级皇家理发库#k]中的所有脸型", 超级明星美发卡, face_Colo_boy12);
				} else {
					beauty =12;
					cm.sendStyle("确定要使用 #b#t" + 超级明星美发卡 + "##k #r固定!!#k脸型了？？\r\n目前你的脸型是:#t" + cm.getPlayer().getFace() + "#\r\n以下是[#r超级皇家理发库#k]中的所有脸型", 超级明星美发卡, face_Colo_boy12);
						}
					} else if (selection == 13) {
				if (cm.getPlayerStat("GENDER") == 0) {
					beauty = 13;
					cm.sendStyle("确定要使用 #b#t" + 超级明星美发卡 + "##k #r固定!!#k了？？\r\n目前你的脸型是:#t" + cm.getPlayer().getFace() + "#\r\n以下是[#r超级皇家理发库#k]中的所有脸型", 超级明星美发卡, face_Colo_boy13);
				} else {
					beauty = 13;
					cm.sendStyle("确定要使用 #b#t" + 超级明星美发卡 + "##k #r固定!!#k脸型了？？\r\n目前你的脸型是:#t" + cm.getPlayer().getFace() + "#\r\n以下是[#r超级皇家理发库#k]中的所有脸型", 超级明星美发卡, face_Colo_boy13);
						}
			}
		} else if (status == 2) {
			if(beauty == 0){
				if (cm.setRandomAvatar(5150040, hair_Colo_new) == 1) {
					cm.sendOk("对你的新发型满意吗?");
				} else {
					cm.sendOk("貌似没有#b#t5150040##k。");
				}
				cm.dispose();
			}else if (beauty == 1) {
				if (cm.getPlayerStat("GENDER") == 0) {
					if (cm.haveItem(超级明星美发卡)) {
						cm.gainItem(超级明星美发卡, -1);
						cm.setHair(hair_Colo_boy1[selection]);
						cm.sendOk("享受!");
						cm.dispose();
					} else {
						cm.sendOk("您貌似没有#b#t" + 超级明星美发卡 + "##k..");
						cm.dispose();
					}
				} else {
					if (cm.haveItem(超级明星美发卡)) {
						cm.gainItem(超级明星美发卡, -1);
						cm.setHair(hair_Colo_boy1[selection]);
						cm.sendOk("享受!");
						cm.dispose();
					} else {
						cm.sendOk("您貌似没有#b#t" + 超级明星美发卡 + "##k..");
						cm.dispose();
					}
				}
			} else if (beauty == 2) {
				if (cm.getPlayerStat("GENDER") == 0) {
					if (cm.haveItem(超级明星美发卡)) {
						cm.gainItem(超级明星美发卡, -1);
						cm.setHair(hair_Colo_boy2[selection]);
						cm.sendOk("享受!");
						cm.dispose();
					} else {
						cm.sendOk("您貌似没有#b#t" + 超级明星美发卡 + "##k..");
						cm.dispose();
					}
				} else {
					if (cm.haveItem(超级明星美发卡)) {
						cm.gainItem(超级明星美发卡, -1);
						cm.setHair(hair_Colo_boy2[selection]);
						cm.sendOk("享受!");
						cm.dispose();
					} else {
						cm.sendOk("您貌似没有#b#t" + 超级明星美发卡 + "##k..");
						cm.dispose();
					}
				}
			//------
			} else if (beauty ==3) {
				if (cm.getPlayerStat("GENDER") == 0) {
					if (cm.haveItem(超级明星美发卡)) {
						cm.gainItem(超级明星美发卡, -1);
						cm.setHair(hair_Colo_boy3[selection]);
						cm.sendOk("享受!");
						cm.dispose();
					} else {
						cm.sendOk("您貌似没有#b#t" + 超级明星美发卡 + "##k..");
						cm.dispose();
					}
				} else {
					if (cm.haveItem(超级明星美发卡)){
						cm.gainItem(超级明星美发卡, -1);
						cm.setHair(hair_Colo_boy3[selection]);
						cm.sendOk("享受!");
						cm.dispose();
					} else {
						cm.sendOk("您貌似没有#b#t" + 超级明星美发卡 + "##k..");
						cm.dispose();
					}
				}
			
			
			//===
						//------
			} else if (beauty ==4) {
				if (cm.getPlayerStat("GENDER") == 0) {
					if (cm.haveItem(超级明星美发卡)) {
						cm.gainItem(超级明星美发卡, -1);
						cm.setHair(hair_Colo_boy4[selection]);
						cm.sendOk("享受!");
						cm.dispose();
					} else {
						cm.sendOk("您貌似没有#b#t" + 超级明星美发卡 + "##k..");
						cm.dispose();
					}
				} else {
					if (cm.haveItem(超级明星美发卡)){
						cm.gainItem(超级明星美发卡, -1);
							cm.setHair(hair_Colo_boy4[selection]);
						cm.sendOk("享受!");
						cm.dispose();
					} else {
						cm.sendOk("您貌似没有#b#t" + 超级明星美发卡 + "##k..");
						cm.dispose();
					}
				}
			
			
			//===
							//------
			} else if (beauty ==5) {
				if (cm.getPlayerStat("GENDER") == 0) {
					if (cm.haveItem(超级明星美发卡)) {
						cm.gainItem(超级明星美发卡, -1);
						cm.setHair(hair_Colo_boy5[selection]);
						cm.sendOk("享受!");
						cm.dispose();
					} else {
						cm.sendOk("您貌似没有#b#t" + 超级明星美发卡 + "##k..");
						cm.dispose();
					}
				} else {
					if (cm.haveItem(超级明星美发卡)){
						cm.gainItem(超级明星美发卡, -1);
					cm.setHair(hair_Colo_boy5[selection]);
						cm.sendOk("享受!");
						cm.dispose();
					} else {
						cm.sendOk("您貌似没有#b#t" + 超级明星美发卡 + "##k..");
						cm.dispose();
					}
				}
			
			
			//===
						//===
							//------
			} else if (beauty ==6) {
				if (cm.getPlayerStat("GENDER") == 0) {
					if (cm.haveItem(超级明星美发卡)) {
						cm.gainItem(超级明星美发卡, -1);
						cm.setHair(hair_Colo_boy6[selection]);
						cm.sendOk("享受!");
						cm.dispose();
					} else {
						cm.sendOk("您貌似没有#b#t" + 超级明星美发卡 + "##k..");
						cm.dispose();
					}
				} else {
					if (cm.haveItem(超级明星美发卡)){
						cm.gainItem(超级明星美发卡, -1);
					cm.setHair(hair_Colo_boy6[selection]);
						cm.sendOk("享受!");
						cm.dispose();
					} else {
						cm.sendOk("您貌似没有#b#t" + 超级明星美发卡 + "##k..");
						cm.dispose();
					}
				}
			
			
			//===
									//===
							//------
			} else if (beauty ==7) {
				if (cm.getPlayerStat("GENDER") == 0) {
					if (cm.haveItem(超级明星美发卡)) {
						cm.gainItem(超级明星美发卡, -1);
						cm.setHair(hair_Colo_boy7[selection]);
						cm.sendOk("享受!");
						cm.dispose();
					} else {
						cm.sendOk("您貌似没有#b#t" + 超级明星美发卡 + "##k..");
						cm.dispose();
					}
				} else {
					if (cm.haveItem(超级明星美发卡)){
						cm.gainItem(超级明星美发卡, -1);
					cm.setHair(hair_Colo_boy7[selection]);
						cm.sendOk("享受!");
						cm.dispose();
					} else {
						cm.sendOk("您貌似没有#b#t" + 超级明星美发卡 + "##k..");
						cm.dispose();
					}
				}
			
			
			//===
										//------
			} else if (beauty ==8) {
				if (cm.getPlayerStat("GENDER") == 0) {
					if (cm.haveItem(超级明星美发卡)) {
						cm.gainItem(超级明星美发卡, -1);
						cm.setHair(hair_Colo_boy8[selection]);
						cm.sendOk("享受!");
						cm.dispose();
					} else {
						cm.sendOk("您貌似没有#b#t" + 超级明星美发卡 + "##k..");
						cm.dispose();
					}
				} else {
					if (cm.haveItem(超级明星美发卡)){
						cm.gainItem(超级明星美发卡, -1);
						cm.setHair(hair_Colo_boy8[selection]);
						cm.sendOk("享受!");
						cm.dispose();
					} else {
						cm.sendOk("您貌似没有#b#t" + 超级明星美发卡 + "##k..");
						cm.dispose();
					}
				}
			
			
			//===
				
										//------
			} else if (beauty ==9) {
				if (cm.getPlayerStat("GENDER") == 0) {
					if (cm.haveItem(超级明星美发卡)) {
						cm.gainItem(超级明星美发卡, -1);
						cm.setFace(face_Colo_boy9[selection]);
						cm.sendOk("享受!");
						cm.dispose();
					} else {
						cm.sendOk("您貌似没有#b#t" + 超级明星美发卡 + "##k..");
						cm.dispose();
					}
				} else {
					if (cm.haveItem(超级明星美发卡)){
						cm.gainItem(超级明星美发卡, -1);
						cm.setFace(face_Colo_boy9[selection]);
						cm.sendOk("享受!");
						cm.dispose();
					} else {
						cm.sendOk("您貌似没有#b#t" + 超级明星美发卡 + "##k..");
						cm.dispose();
					}
				}
			
			
			//===
				
										//------
			} else if (beauty ==10) {
				if (cm.getPlayerStat("GENDER") == 0) {
					if (cm.haveItem(超级明星美发卡)) {
						cm.gainItem(超级明星美发卡, -1);
						cm.setFace(face_Colo_boy10[selection]);
						cm.sendOk("享受!");
						cm.dispose();
					} else {
						cm.sendOk("您貌似没有#b#t" + 超级明星美发卡 + "##k..");
						cm.dispose();
					}
				} else {
					if (cm.haveItem(超级明星美发卡)){
						cm.gainItem(超级明星美发卡, -1);
						cm.setFace(face_Colo_boy10[selection]);
						cm.sendOk("享受!");
						cm.dispose();
					} else {
						cm.sendOk("您貌似没有#b#t" + 超级明星美发卡 + "##k..");
						cm.dispose();
					}
				}
			
			
			//===
				
										//------
			} else if (beauty ==11) {
				if (cm.getPlayerStat("GENDER") == 0) {
					if (cm.haveItem(超级明星美发卡)) {
						cm.gainItem(超级明星美发卡, -1);
						cm.setFace(face_Colo_boy11[selection]);
						cm.sendOk("享受!");
						cm.dispose();
					} else {
						cm.sendOk("您貌似没有#b#t" + 超级明星美发卡 + "##k..");
						cm.dispose();
					}
				} else {
					if (cm.haveItem(超级明星美发卡)){
						cm.gainItem(超级明星美发卡, -1);
					cm.setFace(face_Colo_boy11[selection]);
						cm.sendOk("享受!");
						cm.dispose();
					} else {
						cm.sendOk("您貌似没有#b#t" + 超级明星美发卡 + "##k..");
						cm.dispose();
					}
				}
			
			
			//===
				
										//------
			} else if (beauty ==12) {
				if (cm.getPlayerStat("GENDER") == 0) {
					if (cm.haveItem(超级明星美发卡)) {
						cm.gainItem(超级明星美发卡, -1);
						cm.setFace(face_Colo_boy12[selection]);
						cm.sendOk("享受!");
						cm.dispose();
					} else {
						cm.sendOk("您貌似没有#b#t" + 超级明星美发卡 + "##k..");
						cm.dispose();
					}
				} else {
					if (cm.haveItem(超级明星美发卡)){
						cm.gainItem(超级明星美发卡, -1);
						cm.setFace(face_Colo_boy12[selection]);
						cm.sendOk("享受!");
						cm.dispose();
					} else {
						cm.sendOk("您貌似没有#b#t" + 超级明星美发卡 + "##k..");
						cm.dispose();
					}
				}
			
			
			//===
				
										//------
			} else if (beauty ==13) {
				if (cm.getPlayerStat("GENDER") == 0) {
					if (cm.haveItem(超级明星美发卡)) {
						cm.gainItem(超级明星美发卡, -1);
						cm.setFace(face_Colo_boy13[selection]);
						cm.sendOk("享受!");
						cm.dispose();
					} else {
						cm.sendOk("您貌似没有#b#t" + 超级明星美发卡 + "##k..");
						cm.dispose();
					}
				} else {
					if (cm.haveItem(超级明星美发卡)){
						cm.gainItem(超级明星美发卡, -1);
					cm.setFace(face_Colo_boy13[selection]);
						cm.sendOk("享受!");
						cm.dispose();
					} else {
						cm.sendOk("您貌似没有#b#t" + 超级明星美发卡 + "##k..");
						cm.dispose();
					}
				}
			
			
			//===

				
				
			}
		}
	}
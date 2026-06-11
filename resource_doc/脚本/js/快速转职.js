var 美化1 = "#fUI/ChatBalloon.img/pet/153/nw#";//选择道具
var 美化2 = "#fUI/ChatBalloon.img/pet/153/ne#";//选择道具
var 美化3 = "#fUI/ChatBalloon.img/pet/153/n#";//选择道具
//var 美化8 = "#fUI/ChatBalloon.img/pet/153/head#";//选择道具
var 美化4 = "#fUI/ChatBalloon.img/pet/153/sw#";//选择道具
var 美化5 = "#fUI/ChatBalloon.img/pet/153/se#";//选择道具
var 美化6 = "#fUI/ChatBalloon.img/pet/153/s#";//选择道具
var 美化8 = "#fUI/GuildMark.img/Mark/Etc/00009023/14#";//选择道具
var 美化9 = "#fUI/GuildMark.img/Mark/Plant/00003002/14#";//选择道具
var 美化10 = "#fUI/GuildMark.img/Mark/Plant/00003002/7#";//选择道具
var 美化11 = "#fUI/GuildMark.img/Mark/Plant/00003002/1#";//选择道具
var 美化12 = "#fUI/ChatBalloon.img/pet/153/w#";//选择道具
var 美化13 = "#fUI/ChatBalloon.img/pet/153/e#";//选择道具
var 美化14 = "#fUI/ChatBalloon.img/pet/153/arrow#";//选择道具
var 大白 = "#fMob/3400004/stand/0#";
//var 大白 = "#fSkill/100/Skill/1001005/effect/2#";
var status = -1;
var maxSkills =  true;//转职后全满技能  开启：true 关闭：false
var Job_list_Map = {
	
	0:[//职业列表//新手 - 一转
		[
			{ job_id: 100, name: "战士", level: 10, js: "" },
			{ id: 1000000, max_Level: 16 },
			{ id: 1000001, max_Level: 10 },
			{ id: 1000002, max_Level: 8 },
			{ id: 1001003, max_Level: 20 },
			{ id: 1001004, max_Level: 20 },
			{ id: 1001005, max_Level: 20 }
		],[
			{ job_id: 200, name: "魔法师", level: 8, js: "" },
			{ id: 2000000, max_Level: 16 },
			{ id: 2000001, max_Level: 10 },
			{ id: 2001002, max_Level: 20 },
			{ id: 2001003, max_Level: 20 },
			{ id: 2001004, max_Level: 20 },
			{ id: 2001005, max_Level: 20 }
		],[
			{ job_id: 300, name: "弓箭手", level: 10, js: "" },
			{ id: 3000000, max_Level: 16 },
			{ id: 3000001, max_Level: 20 },
			{ id: 3000002, max_Level: 8 },
			{ id: 3001003, max_Level: 20 },
			{ id: 3001004, max_Level: 20 },
			{ id: 3001005, max_Level: 20 }
		],[
			{ job_id: 400, name: "飞侠", level: 10, js: "" },
			{ id: 4000000, max_Level: 20 },
			{ id: 4000001, max_Level: 8 },
			{ id: 4001002, max_Level: 20 },
			{ id: 4001003, max_Level: 20 },
			{ id: 4001334, max_Level: 20 },
			{ id: 4001344, max_Level: 20 }
		],[
			{ job_id: 500, name: "海盗", level: 10, js: "" },
			{ id: 5000000, max_Level: 20 },
			{ id: 5001001, max_Level: 20 },
			{ id: 5001002, max_Level: 20 },
			{ id: 5001003, max_Level: 20 },
			{ id: 5001005, max_Level: 10 }
		],

	],


	100:[//战士 - 二转
		[
			{ job_id: 110, name: "剑客", level: 30, js: "" },
			{ id: 1100000, max_Level: 20 },
			{ id: 1100001, max_Level: 20 },
			{ id: 1100002, max_Level: 30 },
			{ id: 1100003, max_Level: 30 },
			{ id: 1101004, max_Level: 20 },
			{ id: 1101005, max_Level: 20 },
			{ id: 1101006, max_Level: 20 },
			{ id: 1101007, max_Level: 30 }
		],[
			{ job_id: 120, name: "准骑士", level: 30, js: "" },
			{ id: 1200000, max_Level: 20 },
			{ id: 1200001, max_Level: 20 },
			{ id: 1200002, max_Level: 30 },
			{ id: 1200003, max_Level: 30 },
			{ id: 1201004, max_Level: 20 },
			{ id: 1201005, max_Level: 20 },
			{ id: 1201006, max_Level: 20 },
			{ id: 1201007, max_Level: 30 }
		],[
			{ job_id: 130, name: "枪战", level: 30, js: "" },
			{ id: 1300000, max_Level: 20 },
			{ id: 1300001, max_Level: 20 },
			{ id: 1300002, max_Level: 30 },
			{ id: 1300003, max_Level: 30 },
			{ id: 1301004, max_Level: 20 },
			{ id: 1301005, max_Level: 20 },
			{ id: 1301006, max_Level: 20 },
			{ id: 1301007, max_Level: 30 }
		]
	],
	110:[//剑客 - 三转
		[
			{ job_id: 111, name: "勇士", level: 70, js: "" },
			{ id: 1110000, max_Level: 20 },
			{ id: 1110001, max_Level: 20 },
			{ id: 1111002, max_Level: 30 },
			{ id: 1111003, max_Level: 30 },
			{ id: 1111004, max_Level: 30 },
			{ id: 1111005, max_Level: 30 },
			{ id: 1111006, max_Level: 30 },
			{ id: 1111007, max_Level: 20 },
			{ id: 1111008, max_Level: 30 }
		]
	],
	111:[//勇士 - 四转
		[
			{ job_id: 112, name: "英雄", level: 120, js: "" },
			{ id: 1120003, max_Level: 30 },
			{ id: 1120004, max_Level: 30 },
			{ id: 1120005, max_Level: 30 },
			{ id: 1121000, max_Level: 30 },
			{ id: 1121001, max_Level: 30 },
			{ id: 1121002, max_Level: 30 },
			{ id: 1121006, max_Level: 30 },
			{ id: 1121008, max_Level: 30 },
			{ id: 1121010, max_Level: 30 },
			{ id: 1121011, max_Level: 5 }
		]
	],
	120:[//准骑士 - 三转
		[
			{ job_id: 121, name: "骑士", level: 70, js: "" },
			{ id: 1210000, max_Level: 20 },
			{ id: 1210001, max_Level: 20 },
			{ id: 1211002, max_Level: 30 },
			{ id: 1211003, max_Level: 30 },
			{ id: 1211004, max_Level: 30 },
			{ id: 1211005, max_Level: 30 },
			{ id: 1211006, max_Level: 30 },
			{ id: 1211007, max_Level: 30 },
			{ id: 1211008, max_Level: 30 },
			{ id: 1211009, max_Level: 20 }
		]
	],
	121:[//骑士 - 四转
		[
			{ job_id: 122, name: "圣骑士", level: 120, js: "" },
			{ id: 1220005, max_Level: 30 },
			{ id: 1220006, max_Level: 30 },
			{ id: 1220010, max_Level: 30 },
			{ id: 1221000, max_Level: 30 },
			{ id: 1221001, max_Level: 30 },
			{ id: 1221002, max_Level: 30 },
			{ id: 1221003, max_Level: 20 },
			{ id: 1221004, max_Level: 20 },
			{ id: 1221007, max_Level: 30 },
			{ id: 1221009, max_Level: 30 },
			{ id: 1221011, max_Level: 30 },
			{ id: 1221012, max_Level: 5 }
		]
	],
	130:[//枪战 - 三转
		[
			{ job_id: 131, name: "龙骑", level: 70, js: "" },
			{ id: 1310000, max_Level: 20 },
			{ id: 1311001, max_Level: 30 },
			{ id: 1311002, max_Level: 30 },
			{ id: 1311003, max_Level: 30 },
			{ id: 1311004, max_Level: 30 },
			{ id: 1311005, max_Level: 30 },
			{ id: 1311006, max_Level: 30 },
			{ id: 1311007, max_Level: 20 },
			{ id: 1311008, max_Level: 20 }
		]
	],
	
	131:[//龙骑 - 四转
		[
			{ job_id: 132, name: "黑骑", level: 120, js: "" },
			{ id: 1320005, max_Level: 30 },
			{ id: 1320006, max_Level: 30 },
			{ id: 1320008, max_Level: 30 },
			{ id: 1320009, max_Level: 30 },
			{ id: 1321000, max_Level: 30 },
			{ id: 1321001, max_Level: 30 },
			{ id: 1321002, max_Level: 30 },
			{ id: 1321003, max_Level: 30 },
			{ id: 1321007, max_Level: 30 },
			{ id: 1321010, max_Level: 5 }
		]
	],
	200:[//魔法师 - 二转
		[
			{ job_id: 210, name: "法师（火，毒）", level: 30, js: "" },
			{ id: 2100000, max_Level: 20 },
			{ id: 2101001, max_Level: 20 },
			{ id: 2101002, max_Level: 20 },
			{ id: 2101003, max_Level: 20 },
			{ id: 2101004, max_Level: 30 },
			{ id: 2101005, max_Level: 30 }
		],[
			{ job_id: 220, name: "法师（雷，冰）", level: 30, js: "" },
			{ id: 2200000, max_Level: 20 },
			{ id: 2201001, max_Level: 20 },
			{ id: 2201002, max_Level: 20 },
			{ id: 2201003, max_Level: 20 },
			{ id: 2201004, max_Level: 30 },
			{ id: 2201005, max_Level: 30 }
		],[
			{ job_id: 230, name: "牧师", level: 30, js: "" },
			{ id: 2300000, max_Level: 20 },
			{ id: 2301001, max_Level: 20 },
			{ id: 2301002, max_Level: 30 },
			{ id: 2301003, max_Level: 20 },
			{ id: 2301004, max_Level: 20 },
			{ id: 2301005, max_Level: 30 }
		]
	],
	210:[//法师（火，毒） - 三转
		[
			{ job_id: 211, name: "巫师（火，毒）", level: 70, js: "" },
			{ id: 2110000, max_Level: 20 },
			{ id: 2110001, max_Level: 30 },
			{ id: 2111002, max_Level: 30 },
			{ id: 2111003, max_Level: 30 },
			{ id: 2111004, max_Level: 20 },
			{ id: 2111005, max_Level: 20 },
			{ id: 2111006, max_Level: 30 }
		]
	],
	220:[//法师（雷，冰） - 三转
		[
			{ job_id: 221, name: "巫师（雷，冰）", level: 70, js: "" },
			{ id: 2210000, max_Level: 20 },
			{ id: 2210001, max_Level: 30 },
			{ id: 2211002, max_Level: 30 },
			{ id: 2211003, max_Level: 30 },
			{ id: 2211004, max_Level: 20 },
			{ id: 2211005, max_Level: 20 },
			{ id: 2211006, max_Level: 30 }
		]
	],
	230:[//牧师 - 三转
		[
			{ job_id: 231, name: "祭司", level: 70, js: "" },
			{ id: 2310000, max_Level: 20 },
			{ id: 2311001, max_Level: 20 },
			{ id: 2311002, max_Level: 20 },
			{ id: 2311003, max_Level: 30 },
			{ id: 2311004, max_Level: 30 },
			{ id: 2311005, max_Level: 30 },
			{ id: 2311006, max_Level: 30 }
		]
	],		
	211:[//巫师（火，毒） - 四转
		[
			{ job_id: 212, name: "魔导师（火，毒）", level: 120, js: "" },
			{ id: 2121000, max_Level: 30 },
			{ id: 2121001, max_Level: 30 },
			{ id: 2121002, max_Level: 30 },
			{ id: 2121003, max_Level: 30 },
			{ id: 2121004, max_Level: 30 },
			{ id: 2121005, max_Level: 30 },
			{ id: 2121006, max_Level: 30 },
			{ id: 2121007, max_Level: 30 },
			{ id: 2121008, max_Level: 5 }
		]
	],		
	221:[//巫师（雷，冰） - 四转
		[
			{ job_id: 222, name: "魔导师（雷，冰）", level: 120, js: "" },
			{ id: 2221000, max_Level: 30 },
			{ id: 2221001, max_Level: 30 },
			{ id: 2221002, max_Level: 30 },
			{ id: 2221003, max_Level: 30 },
			{ id: 2221004, max_Level: 30 },
			{ id: 2221005, max_Level: 30 },
			{ id: 2221006, max_Level: 30 },
			{ id: 2221007, max_Level: 30 },
			{ id: 2221008, max_Level: 5 }
		]
	],		
	231:[//祭司 - 四转
		[
			{ job_id: 232, name: "主教", level: 120, js: "" },
			{ id: 2321000, max_Level: 30 },
			{ id: 2321001, max_Level: 30 },
			{ id: 2321002, max_Level: 30 },
			{ id: 2321003, max_Level: 30 },
			{ id: 2321004, max_Level: 30 },
			{ id: 2321005, max_Level: 30 },
			{ id: 2321006, max_Level: 30 },
			{ id: 2321007, max_Level: 30 },
			{ id: 2321008, max_Level: 30 },
			{ id: 2321009, max_Level: 5 }
		]
	],
	300:[//弓箭手 - 二转
		[
			{ job_id: 310, name: "猎人", level: 30, js: "" },
			{ id: 3100000, max_Level: 20 },
			{ id: 3100001, max_Level: 30 },
			{ id: 3101002, max_Level: 20 },
			{ id: 3101003, max_Level: 20 },
			{ id: 3101004, max_Level: 20 },
			{ id: 3101005, max_Level: 30 }
		],[
			{ job_id: 320, name: "弩弓手", level: 30, js: "" },
			{ id: 3200000, max_Level: 20 },
			{ id: 3200001, max_Level: 30 },
			{ id: 3201002, max_Level: 20 },
			{ id: 3201003, max_Level: 20 },
			{ id: 3201004, max_Level: 20 },
			{ id: 3201005, max_Level: 30 }
		]
	],
	310:[//猎人 - 三转
		[
			{ job_id: 311, name: "射手", level: 70, js: "" },
			{ id: 3110000, max_Level: 20 },
			{ id: 3110001, max_Level: 20 },
			{ id: 3111002, max_Level: 20 },
			{ id: 3111003, max_Level: 30 },
			{ id: 3111004, max_Level: 30 },
			{ id: 3111005, max_Level: 30 },
			{ id: 3111006, max_Level: 30 }
		]
	],
	311:[//射手 - 四转
		[
			{ job_id: 312, name: "神射手", level: 120, js: "" },
			{ id: 3120005, max_Level: 30 },
			{ id: 3121000, max_Level: 30 },
			{ id: 3121002, max_Level: 30 },
			{ id: 3121003, max_Level: 30 },
			{ id: 3121004, max_Level: 30 },
			{ id: 3221003, max_Level: 30 },
			{ id: 3121006, max_Level: 30 },
			{ id: 3121007, max_Level: 30 },
			{ id: 3121008, max_Level: 30 },
			{ id: 3121009, max_Level: 5 }
		]
	],
	320:[//弩弓手 - 三转
		[
			{ job_id: 321, name: "游侠", level: 70, js: "" },
			{ id: 3210000, max_Level: 20 },
			{ id: 3210001, max_Level: 20 },
			{ id: 3211002, max_Level: 20 },
			{ id: 3211003, max_Level: 30 },
			{ id: 3211004, max_Level: 30 },
			{ id: 3211005, max_Level: 30 },
			{ id: 3211006, max_Level: 30 }
		]
	],
	321:[//游侠 - 四转
		[
			{ job_id: 322, name: "箭神", level: 120, js: "" },
			{ id: 3220004, max_Level: 30 },
			{ id: 3221000, max_Level: 30 },
			{ id: 3221001, max_Level: 30 },
			{ id: 3221002, max_Level: 30 },
			{ id: 3221005, max_Level: 30 },
			{ id: 3221003, max_Level: 30 },
			{ id: 3221006, max_Level: 30 },
			{ id: 3221007, max_Level: 30 },
			{ id: 3221008, max_Level: 5 }
		]
	],
	400:[//飞侠 - 二转
		[
			{ job_id: 410, name: "刺客", level: 30, js: "" },
			{ id: 4100000, max_Level: 20 },
			{ id: 4100001, max_Level: 30 },
			{ id: 4100002, max_Level: 20 },
			{ id: 4101003, max_Level: 20 },
			{ id: 4101004, max_Level: 20 },
			{ id: 4101005, max_Level: 30 }

			],[
			{ job_id: 420, name: "侠客", level: 30, js: "" },
			{ id: 4200000, max_Level: 20 },
			{ id: 4200001, max_Level: 20 },
			{ id: 4201002, max_Level: 20 },
			{ id: 4201003, max_Level: 20 },
			{ id: 4201004, max_Level: 30 },
			{ id: 4201005, max_Level: 30 },
		],
	],
	410:[//刺客 - 三转
		[
			{ job_id: 411, name: "无影人", level: 70, js: "" },
			{ id: 4110000, max_Level: 20 },
			{ id: 4111001, max_Level: 20 },
			{ id: 4111002, max_Level: 30 },
			{ id: 4111003, max_Level: 20 },
			{ id: 4111004, max_Level: 30 },
			{ id: 4111005, max_Level: 30 },
			{ id: 4111006, max_Level: 20 }
		]
	],
	411:[//无影人 - 四转
		[
			{ job_id: 412, name: "隐士", level: 120, js: "" },
			{ id: 4120002, max_Level: 30 },
			{ id: 4120005, max_Level: 30 },
			{ id: 4121000, max_Level: 30 },
			{ id: 4121003, max_Level: 30 },
			{ id: 4121004, max_Level: 30 },
			{ id: 4121006, max_Level: 30 },
			{ id: 4121007, max_Level: 30 },
			{ id: 4121008, max_Level: 30 },
			{ id: 4121009, max_Level: 5 }
		]
	],
	420:[//侠客 - 三转
		[
			{ job_id: 421, name: "独行客", level: 70, js: "" },
			{ id: 4210000, max_Level: 20 },
			{ id: 4211001, max_Level: 30 },
			{ id: 4211002, max_Level: 30 },
			{ id: 4211003, max_Level: 20 },
			{ id: 4211004, max_Level: 30 },
			{ id: 4211005, max_Level: 20 },
			{ id: 4211006, max_Level: 30 }
		]
	],
	421:[//独行客 - 四转
		[
			{ job_id: 422, name: "侠盗", level: 120, js: "" },
			{ id: 4220002, max_Level: 30 },
			{ id: 4220005, max_Level: 30 },
			{ id: 4221000, max_Level: 30 },
			{ id: 4221001, max_Level: 30 },
			{ id: 4221003, max_Level: 30 },
			{ id: 4221004, max_Level: 30 },
			{ id: 4221006, max_Level: 30 },
			{ id: 4221007, max_Level: 30 },
			{ id: 4221008, max_Level: 5 }
		]
	],
	500:[//海盗 - 二转
		[
			{ job_id: 510, name: "拳手", level: 30, js: "" },
			{ id: 5100000, max_Level: 10 },
			{ id: 5100001, max_Level: 20 },
			{ id: 5101002, max_Level: 20 },
			{ id: 5101003, max_Level: 20 },
			{ id: 5101004, max_Level: 20 },
			{ id: 5101005, max_Level: 10 },
			{ id: 5101006, max_Level: 20 },
			{ id: 5101007, max_Level: 10 }
		],[
			{ job_id: 520, name: "火枪手", level: 30, js: "" },
			{ id: 5200000, max_Level: 20 },
			{ id: 5201001, max_Level: 20 },
			{ id: 5201002, max_Level: 20 },
			{ id: 5201003, max_Level: 20 },
			{ id: 5201004, max_Level: 20 },
			{ id: 5201005, max_Level: 10 },
			{ id: 5201006, max_Level: 20 }
		]
	],
	510:[//拳手 - 三转
		[
			{ job_id: 511, name: "斗士", level: 70, js: "" },
			{ id: 5110000, max_Level: 20 },
			{ id: 5110001, max_Level: 40 },
			{ id: 5111002, max_Level: 30 },
			{ id: 5111004, max_Level: 20 },
			{ id: 5111005, max_Level: 20 },
			{ id: 5111006, max_Level: 30 }
		]
	],
	511:[//斗士 - 四转
		[
			{ job_id: 512, name: "冲锋队长", level: 120, js: "" },
			{ id: 5121000, max_Level: 30 },
			{ id: 5121001, max_Level: 30 },
			{ id: 5121002, max_Level: 30 },
			{ id: 5121003, max_Level: 30 },
			{ id: 5121004, max_Level: 30 },
			{ id: 5121005, max_Level: 30 },
			{ id: 5121007, max_Level: 30 },
			{ id: 5121008, max_Level: 5 },
			{ id: 5121009, max_Level: 30 },
			{ id: 5121010, max_Level: 30 }
		]
	],
	520:[//火枪手 - 三转
		[
			{ job_id: 521, name: "大副", level: 70, js: "" },
			{ id: 5210000, max_Level: 20 },
			{ id: 5211001, max_Level: 30 },
			{ id: 5211002, max_Level: 30 },
			{ id: 5211004, max_Level: 30 },
			{ id: 5211005, max_Level: 30 },
			{ id: 5211006, max_Level: 30 }
		]
	],
	521:[//大副 - 四转
		[
			{ job_id: 522, name: "船长", level: 120, js: "" },
			{ id: 5221000, max_Level: 30 },
			{ id: 5220001, max_Level: 30 },
			{ id: 5220002, max_Level: 30 },
			{ id: 5221003, max_Level: 30 },
			{ id: 5221004, max_Level: 30 },
			{ id: 5221006, max_Level: 30 },
			{ id: 5221007, max_Level: 30 },
			{ id: 5221008, max_Level: 30 },
			{ id: 5221009, max_Level: 30 },
			{ id: 5221010, max_Level: 5 },
			{ id: 5220011, max_Level: 30 }
		]
	],
	
	1000:[//初心者 - 一转
		[
			{ job_id: 1100, name: "魂骑士 - 一转", level: 10, js: "" },
			{ id: 11000000, max_Level: 10 },
			{ id: 11001001, max_Level: 10 },
			{ id: 11001002, max_Level: 20 },
			{ id: 11001003, max_Level: 20 },
			{ id: 11001004, max_Level: 20 }
		],[
			{ job_id: 1200, name: "炎术士 - 一转", level: 10, js: "" },
			{ id: 12000000, max_Level: 10 },
			{ id: 12001001, max_Level: 10 },
			{ id: 12001002, max_Level: 10 },
			{ id: 12001003, max_Level: 20 },
			{ id: 12001004, max_Level: 20 }
		],[
			{ job_id: 1300, name: "风灵使者 - 一转", level: 10, js: "" },
			{ id: 13000000, max_Level: 20 },
			{ id: 13000001, max_Level: 8 },
			{ id: 13001002, max_Level: 10 },
			{ id: 13001003, max_Level: 20 },
			{ id: 13001004, max_Level: 20 }
		],[
			{ job_id: 1400, name: "夜行者 - 一转", level: 10, js: "" },
			{ id: 14000000, max_Level: 10 },
			{ id: 14000001, max_Level: 8 },
			{ id: 14001002, max_Level: 10 },
			{ id: 14001003, max_Level: 10 },
			{ id: 14001004, max_Level: 20 },
			{ id: 14001005, max_Level: 20 }
		],[
			{ job_id: 1500, name: "奇袭者 - 一转", level: 10, js: "" },
			{ id: 15000000, max_Level: 10 },
			{ id: 15001001, max_Level: 20 },
			{ id: 15001002, max_Level: 20 },
			{ id: 15001003, max_Level: 10 },
			{ id: 15001004, max_Level: 20 }
		]
	],
	1100:[//魂骑士 - 二转
		[
			{ job_id: 1110, name: "魂骑士 - 二转", level: 30, js: "" },
			{ id: 11100000, max_Level: 20 },
			{ id: 11101001, max_Level: 20 },
			{ id: 11101002, max_Level: 30 },
			{ id: 11101003, max_Level: 20 },
			{ id: 11101004, max_Level: 30 },
			{ id: 11101005, max_Level: 10 }
		]
	],
	1110:[//魂骑士 - 三转
		[
			{ job_id: 1111, name: "魂骑士 - 三转", level: 70, js: "" },
			{ id: 11110000, max_Level: 20 },
			{ id: 11111001, max_Level: 20 },
			{ id: 11111002, max_Level: 20 },
			{ id: 11111003, max_Level: 20 },
			{ id: 11111004, max_Level: 30 },
			{ id: 11110005, max_Level: 20 },
			{ id: 11111006, max_Level: 30 },
			{ id: 11111007, max_Level: 20 }
		]
	],
	1200:[//炎术士 - 二转
		[
			{ job_id: 1210, name: "炎术士 - 二转", level: 30, js: "" },
			{ id: 12101000, max_Level: 20 },
			{ id: 12101001, max_Level: 20 },
			{ id: 12101002, max_Level: 20 },
			{ id: 12101003, max_Level: 20 },
			{ id: 12101004, max_Level: 20 },
			{ id: 12101005, max_Level: 20 },
			{ id: 12101006, max_Level: 20 }
		]
	],
	1210:[//炎术士 - 三转
		[
			{ job_id: 1211, name: "炎术士 - 三转", level: 70, js: "" },
			{ id: 12110000, max_Level: 20 },
			{ id: 12110001, max_Level: 20 },
			{ id: 12111002, max_Level: 20 },
			{ id: 12111003, max_Level: 20 },
			{ id: 12111004, max_Level: 20 },
			{ id: 12111005, max_Level: 30 },
			{ id: 12111006, max_Level: 30 }
		]
	],
	1300:[//风灵使者 - 二转
		[
			{ job_id: 1310, name: "风灵使者 - 二转", level: 30, js: "" },
			{ id: 13100000, max_Level: 20 },
			{ id: 13101001, max_Level: 20 },
			{ id: 13101002, max_Level: 30 },
			{ id: 13101003, max_Level: 20 },
			{ id: 13100004, max_Level: 20 },
			{ id: 13101005, max_Level: 20 },
			{ id: 13101006, max_Level: 10 }
		]
	],
	1310:[//风灵使者 - 三转
		[
			{ job_id: 1311, name: "风灵使者 - 三转", level: 70, js: "" },
			{ id: 13111000, max_Level: 20 },
			{ id: 13111001, max_Level: 30 },
			{ id: 13111002, max_Level: 20 },
			{ id: 13110003, max_Level: 20 },
			{ id: 13111004, max_Level: 20 },
			{ id: 13111005, max_Level: 10 },
			{ id: 13111006, max_Level: 20 },
			{ id: 13111007, max_Level: 20 }
		]
	],
	1400:[//夜行者 - 二转
		[
			{ job_id: 1410, name: "夜行者 - 二转", level: 30, js: "" },
			{ id: 14100000, max_Level: 20 },
			{ id: 14100001, max_Level: 30 },
			{ id: 14101002, max_Level: 20 },
			{ id: 14101003, max_Level: 20 },
			{ id: 14101004, max_Level: 20 },
			{ id: 14100005, max_Level: 10 },
			{ id: 14101006, max_Level: 20 }
		]
	],
	1410:[//夜行者 - 三转
		[
			{ job_id: 1411, name: "夜行者 - 三转", level: 70, js: "" },
			{ id: 14111000, max_Level: 30 },
			{ id: 14111001, max_Level: 20 },
			{ id: 14111002, max_Level: 30 },
			{ id: 14110003, max_Level: 20 },
			{ id: 14110004, max_Level: 20 },
			{ id: 14111005, max_Level: 20 },
			{ id: 14111006, max_Level: 30 }
		]
	],
	1500:[//奇袭者 - 二转
		[
			{ job_id: 1510, name: "奇袭者 - 二转", level: 30, js: "" },
			{ id: 15100000, max_Level: 10 },
			{ id: 15100001, max_Level: 20 },
			{ id: 15101002, max_Level: 20 },
			{ id: 15101003, max_Level: 20 },
			{ id: 15100004, max_Level: 20 },
			{ id: 15101005, max_Level: 20 },
			{ id: 15101006, max_Level: 20 }
		]
	],
	1510:[//奇袭者 - 三转
		[
			{ job_id: 1511, name: "奇袭者 - 三转", level: 70, js: "" },
			{ id: 15110000, max_Level: 20 },
			{ id: 15111001, max_Level: 20 },
			{ id: 15111002, max_Level: 10 },
			{ id: 15111003, max_Level: 20 },
			{ id: 15111004, max_Level: 20 },
			{ id: 15111005, max_Level: 20 },
			{ id: 15111006, max_Level: 20 },
			{ id: 15111007, max_Level: 30 }
		]
	],
	
	

	2000:[
			[
		 { job_id: 2100, name: "战神 - 1转", level: 10, js: "" },
			{ id: 21000000, max_Level: 10 },
			{ id: 21001001, max_Level: 15 },
			{ id: 21000002, max_Level: 20 },
			{ id: 21001003, max_Level: 20 }
		]
	],
	2100:[//战神 - 二转
		[
			{ job_id: 2110, name: "战神 - 二转", level: 30, js: "" },
			{ id: 21100000, max_Level: 20 },
			{ id: 21100001, max_Level: 20 },
			{ id: 21100002, max_Level: 30 },
			{ id: 21101003, max_Level: 20 },
			{ id: 21100004, max_Level: 20 },
			{ id: 21100005, max_Level: 20 }
		]
	],
	2110:[//战神 - 三转
		[
			{ job_id: 2111, name: "战神 - 三转", level: 70, js: "" },
			{ id: 21110000, max_Level: 20 },
			{ id: 21111001, max_Level: 20 },
			{ id: 21110002, max_Level: 20 },
			{ id: 21110003, max_Level: 30 },
			{ id: 21110004, max_Level: 30 },
			{ id: 21111005, max_Level: 20 },
			{ id: 21110006, max_Level: 20 },
			{ id: 21110007, max_Level: 20 },
			{ id: 21110008, max_Level: 20 }
		]
	],
	2111:[//战神 - 四转
		[
			{ job_id: 2112, name: "战神 - 四转", level: 120, js: "" },
			{ id: 21121000, max_Level: 30 },
			{ id: 21120001, max_Level: 30 },
			{ id: 21120002, max_Level: 30 },
			{ id: 21121003, max_Level: 30 },
			{ id: 21120004, max_Level: 30 },
			{ id: 21120005, max_Level: 30 },
			{ id: 21120006, max_Level: 30 },
			{ id: 21120007, max_Level: 30 },
			{ id: 21121008, max_Level: 5 },
			{ id: 21120009, max_Level: 30 },
			{ id: 21120010, max_Level: 30 }
			]
	],

	
};
var Job_list = [];
var job_list_sel;
/*function start() {

          if (cm.getPlayer().getMapId() == 910000000) {
            cm.sendOk("你不能在自由使用快速转职.");
            cm.dispose();
			} else {

status = -1;
			action(1, 0, 0);
}
}*/
function action(mode, type, selection) {
    if (mode == -1) {
       // cm.sendOk("#b好的,下次再见.");
        cm.dispose();
    } else {
        if (status >= 0 && mode == 0) {
            //cm.sendOk("#b好的,下次再见.");
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        } else {
            status--;
        }
	}
	if (status == 0) {
		Job_list = Job_list_Map[cm.getJob()];
		if(Job_list != null){
			var text = " #l"+美化1+""+美化3+""+美化3+""+美化3+""+美化3+""+美化3+" "+美化8+" 快 速 转 职 "+美化8+" "+美化3+""+美化3+""+美化3+""+美化3+""+美化3+""+美化2+"#l\r\n\r\n";//嗨，我是 #b开心快速转职官#k 我可以帮助你快速转职哦~~！\r\n\r\n#b
				for(var i = 0; i < Job_list.length; i++){
					var s_text = "";
					for(var j = 1; j < Job_list[i].length; j++){
						s_text +="#s"+Job_list[i][j].id+"# ";
					}
					text += "\t\t#b#e#L"+i+"#"+Job_list[i][0].name+" #n#d等级要求：#r#e"+Job_list[i][0].level+"#d#n级#l\t"+Job_list[i][0].js+"\r\n\r\n";
				}
			cm.sendSimple(text);
		} else {
			cm.sendOk("#d伟大的 #r[#h #]#k\r\n\r\n你已经通过一个漫长而充满挑战的道路\r\n完成了所以的转职成为风起云涌的人物"); 
			cm.dispose();
		}
    } else if (status == 1) {
		job_list_sel = selection;
		var s_text = "";
		for(var i = 1; i < Job_list[job_list_sel].length; i++){
			s_text +="#s"+Job_list[job_list_sel][i].id+"# ";
		}
		var text = "#d你选择的职业是：#b"+Job_list[job_list_sel][0].name+" #d最低等级要求：#r#e"+Job_list[job_list_sel][0].level+"#d#n\r\n\t"+Job_list[job_list_sel][0].js+"\r\n\t"+(maxSkills ? "\t#b#e  #n\r\n\r\n":"\r\n\r\n\r\n")+"#r\t\t\t\t\t\t\t\t是否要继续完成转职？";
		cm.sendYesNo(text);
	} else if (status == 2) {
		if(cm.getPlayer().getLevel() >= Job_list[job_list_sel][0].level){
			cm.changeJob(Job_list[job_list_sel][0].job_id);
			if(maxSkills){
				for(var i = 1; i < Job_list[job_list_sel].length; i++){
					cm.teachSkill(Job_list[job_list_sel][i].id, 0, Job_list[job_list_sel][i].max_Level);
			        cm.teachSkill(1026,1,1);
			        //cm.teachSkill(20001026,1,1);
			       // cm.teachSkill(20011026,1,1);			
				}
			}
			cm.worldMessage(6,"[转职系统]: 恭喜 [" + cm.getPlayer().getName() + "] 成为 [" + Job_list[job_list_sel][0].name + "] 快速转职成功！");
			cm.sendOk("转职成功！加油锻炼，当你变的强大的时候记的来找我哦！");
            cm.dispose();
		} else {
			cm.sendOk("怎么样？冒险还算顺利吧。有努力就有回报。当然这一切都不是容易的。当你到达 #r["+Job_list[job_list_sel][0].level+"级]#k 的时候就可以进行#b[" + Job_list[job_list_sel][0].name + " 转职]#k到时别忘记来找我哦！");
			cm.dispose();
		}
    } else {
		cm.sendOk("脚本错误请联系技术处理QQ:\r\nstatus:"+status+"\r\nselection:"+selection+"\r\nmode:"+mode);
		cm.dispose();
	}
}

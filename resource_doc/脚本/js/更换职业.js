

var 感叹 = "#fUI/UIWindow/Quest/icon0#";
var 开 = "#fUI/Basic/CheckBox/0#";   //有框框 无√
var 关 = "#fUI/Basic/CheckBox/1#";   //有框框 有√
var xx = "#fItem/Etc/0427/04270001/Icon9/0#";  //小黄星
var 广播 = "#fUI/CN_Chat/ChattingRoom/BtVolUp/0/normal/0#";

var 功能使用等级 = 10;
var 列表 = [
    { 等级: 120, 职业: [112, 122, 132, 212, 222, 232, 312, 322, 412, 422, 512, 522, 2112] },
    { 等级: 70, 职业: [111, 121, 131, 211, 221, 231, 311, 321, 411, 421, 511, 521,  2111] },
    { 等级: 30, 职业: [110, 120, 130, 210, 220, 230, 310, 320, 410, 420, 510, 520, 2110] },
    { 等级: 10, 职业: [100, 200, 300, 400, 500, 2100] },
]

var Job_list_Map = [
    [
        { job_id: 100, name: "战士", level: 10, js: "" },
        { id: 1000000, max_Level: 16 },
        { id: 1000001, max_Level: 10 },
        { id: 1000002, max_Level: 8 },
        { id: 1001003, max_Level: 20 },
        { id: 1001004, max_Level: 20 },
        { id: 1001005, max_Level: 20 }
    ], [
        { job_id: 200, name: "魔法师", level: 8, js: "" },
        { id: 2000000, max_Level: 16 },
        { id: 2000001, max_Level: 10 },
        { id: 2001002, max_Level: 20 },
        { id: 2001003, max_Level: 20 },
        { id: 2001004, max_Level: 20 },
        { id: 2001005, max_Level: 20 }
    ], [
        { job_id: 300, name: "弓箭手", level: 10, js: "" },
        { id: 3000000, max_Level: 16 },
        { id: 3000001, max_Level: 20 },
        { id: 3000002, max_Level: 8 },
        { id: 3001003, max_Level: 20 },
        { id: 3001004, max_Level: 20 },
        { id: 3001005, max_Level: 20 }
    ], [
        { job_id: 400, name: "飞侠", level: 10, js: "" },
        { id: 4000000, max_Level: 20 },
        { id: 4000001, max_Level: 8 },
        { id: 4001002, max_Level: 20 },
        { id: 4001003, max_Level: 20 },
        { id: 4001334, max_Level: 20 },
        { id: 4001344, max_Level: 20 }
    ], [
        { job_id: 500, name: "海盗", level: 10, js: "" },
        { id: 5000000, max_Level: 20 },
        { id: 5001001, max_Level: 20 },
        { id: 5001002, max_Level: 20 },
        { id: 5001003, max_Level: 20 },
        { id: 5001005, max_Level: 10 }
    ],


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
    ], [
        { job_id: 120, name: "准骑士", level: 30, js: "" },
        { id: 1200000, max_Level: 20 },
        { id: 1200001, max_Level: 20 },
        { id: 1200002, max_Level: 30 },
        { id: 1200003, max_Level: 30 },
        { id: 1201004, max_Level: 20 },
        { id: 1201005, max_Level: 20 },
        { id: 1201006, max_Level: 20 },
        { id: 1201007, max_Level: 30 }
    ], [
        { job_id: 130, name: "枪战", level: 30, js: "" },
        { id: 1300000, max_Level: 20 },
        { id: 1300001, max_Level: 20 },
        { id: 1300002, max_Level: 30 },
        { id: 1300003, max_Level: 30 },
        { id: 1301004, max_Level: 20 },
        { id: 1301005, max_Level: 20 },
        { id: 1301006, max_Level: 20 },
        { id: 1301007, max_Level: 30 }
    ],
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
    ],
    [
        { job_id: 112, name: "英雄", level: 120, js: "" },
        { id: 1120003, max_Level: 10 },
        { id: 1120004, max_Level: 10 },
        { id: 1120005, max_Level: 10 },
        { id: 1121000, max_Level: 10 },
        { id: 1121001, max_Level: 10 },
        { id: 1121002, max_Level: 10 },
        { id: 1121006, max_Level: 10 },
        { id: 1121008, max_Level: 10 },
        { id: 1121010, max_Level: 10 },
        { id: 1121011, max_Level: 5 }
    ],
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
    ],
    [
        { job_id: 122, name: "圣骑士", level: 120, js: "" },
        { id: 1220005, max_Level: 10 },
        { id: 1220006, max_Level: 10 },
        { id: 1220010, max_Level: 10 },
        { id: 1221000, max_Level: 10 },
        { id: 1221001, max_Level: 10 },
        { id: 1221002, max_Level: 10 },
        { id: 1221003, max_Level: 10 },
        { id: 1221004, max_Level: 10 },
        { id: 1221007, max_Level: 10 },
        { id: 1221009, max_Level: 10 },
        { id: 1221011, max_Level: 10 },
        { id: 1221012, max_Level: 5 }
    ],
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
    ],
    [
        { job_id: 132, name: "黑骑", level: 120, js: "" },
        { id: 1320005, max_Level: 10 },
        { id: 1320006, max_Level: 10 },
        { id: 1320008, max_Level: 10 },
        { id: 1320009, max_Level: 10 },
        { id: 1321000, max_Level: 10 },
        { id: 1321001, max_Level: 10 },
        { id: 1321002, max_Level: 10 },
        { id: 1321003, max_Level: 10 },
        { id: 1321007, max_Level: 10 },
        { id: 1321010, max_Level: 5 }
    ],
    [
        { job_id: 210, name: "法师（火，毒）", level: 30, js: "" },
        { id: 2100000, max_Level: 20 },
        { id: 2101001, max_Level: 20 },
        { id: 2101002, max_Level: 20 },
        { id: 2101003, max_Level: 20 },
        { id: 2101004, max_Level: 30 },
        { id: 2101005, max_Level: 30 }
    ], [
        { job_id: 220, name: "法师（雷，冰）", level: 30, js: "" },
        { id: 2200000, max_Level: 20 },
        { id: 2201001, max_Level: 20 },
        { id: 2201002, max_Level: 20 },
        { id: 2201003, max_Level: 20 },
        { id: 2201004, max_Level: 30 },
        { id: 2201005, max_Level: 30 }
    ], [
        { job_id: 230, name: "牧师", level: 30, js: "" },
        { id: 2300000, max_Level: 20 },
        { id: 2301001, max_Level: 20 },
        { id: 2301002, max_Level: 30 },
        { id: 2301003, max_Level: 20 },
        { id: 2301004, max_Level: 20 },
        { id: 2301005, max_Level: 30 }
    ],
    [
        { job_id: 211, name: "巫师（火，毒）", level: 70, js: "" },
        { id: 2110000, max_Level: 20 },
        { id: 2110001, max_Level: 30 },
        { id: 2111002, max_Level: 30 },
        { id: 2111003, max_Level: 30 },
        { id: 2111004, max_Level: 20 },
        { id: 2111005, max_Level: 20 },
        { id: 2111006, max_Level: 30 }
    ],
    [
        { job_id: 221, name: "巫师（雷，冰）", level: 70, js: "" },
        { id: 2210000, max_Level: 20 },
        { id: 2210001, max_Level: 30 },
        { id: 2211002, max_Level: 30 },
        { id: 2211003, max_Level: 30 },
        { id: 2211004, max_Level: 20 },
        { id: 2211005, max_Level: 20 },
        { id: 2211006, max_Level: 30 }
    ],
    [
        { job_id: 231, name: "祭司", level: 70, js: "" },
        { id: 2310000, max_Level: 20 },
        { id: 2311001, max_Level: 20 },
        { id: 2311002, max_Level: 20 },
        { id: 2311003, max_Level: 30 },
        { id: 2311004, max_Level: 30 },
        { id: 2311005, max_Level: 30 },
        { id: 2311006, max_Level: 30 }
    ],
    [
        { job_id: 212, name: "魔导师（火，毒）", level: 120, js: "" },
        { id: 2121000, max_Level: 10 },
        { id: 2121001, max_Level: 10 },
        { id: 2121002, max_Level: 10 },
        { id: 2121003, max_Level: 10 },
        { id: 2121004, max_Level: 10 },
        { id: 2121005, max_Level: 10 },
        { id: 2121006, max_Level: 10 },
        { id: 2121007, max_Level: 10 },
        { id: 2121008, max_Level: 5 }
    ],
    [
        { job_id: 222, name: "魔导师（雷，冰）", level: 120, js: "" },
        { id: 2221000, max_Level: 10 },
        { id: 2221001, max_Level: 10 },
        { id: 2221002, max_Level: 10 },
        { id: 2221003, max_Level: 10 },
        { id: 2221004, max_Level: 10 },
        { id: 2221005, max_Level: 10 },
        { id: 2221006, max_Level: 10 },
        { id: 2221007, max_Level: 10 },
        { id: 2221008, max_Level: 5 }
    ],
    [
        { job_id: 232, name: "主教", level: 120, js: "" },
        { id: 2321000, max_Level: 10 },
        { id: 2321001, max_Level: 10 },
        { id: 2321002, max_Level: 10 },
        { id: 2321003, max_Level: 10 },
        { id: 2321004, max_Level: 10 },
        { id: 2321005, max_Level: 10 },
        { id: 2321006, max_Level: 10 },
        { id: 2321007, max_Level: 10 },
        { id: 2321008, max_Level: 10 },
        { id: 2321009, max_Level: 5 }
    ],
    [
        { job_id: 310, name: "猎人", level: 30, js: "" },
        { id: 3100000, max_Level: 20 },
        { id: 3100001, max_Level: 30 },
        { id: 3101002, max_Level: 20 },
        { id: 3101003, max_Level: 20 },
        { id: 3101004, max_Level: 20 },
        { id: 3101005, max_Level: 30 }
    ], [
        { job_id: 320, name: "弩弓手", level: 30, js: "" },
        { id: 3200000, max_Level: 20 },
        { id: 3200001, max_Level: 30 },
        { id: 3201002, max_Level: 20 },
        { id: 3201003, max_Level: 20 },
        { id: 3201004, max_Level: 20 },
        { id: 3201005, max_Level: 30 }
    ],
    [
        { job_id: 311, name: "射手", level: 70, js: "" },
        { id: 3110000, max_Level: 20 },
        { id: 3110001, max_Level: 20 },
        { id: 3111002, max_Level: 20 },
        { id: 3111003, max_Level: 30 },
        { id: 3111004, max_Level: 30 },
        { id: 3111005, max_Level: 30 },
        { id: 3111006, max_Level: 30 }
    ],
    [
        { job_id: 312, name: "神射手", level: 120, js: "" },
        { id: 3120005, max_Level: 10 },
        { id: 3121000, max_Level: 10 },
        { id: 3121002, max_Level: 10 },
        { id: 3121003, max_Level: 10 },
        { id: 3121004, max_Level: 10 },
        { id: 3221003, max_Level: 10 },
        { id: 3121006, max_Level: 10 },
        { id: 3121007, max_Level: 10 },
        { id: 3121008, max_Level: 10 },
        { id: 3121009, max_Level: 5 }
    ],
    [
        { job_id: 321, name: "游侠", level: 70, js: "" },
        { id: 3210000, max_Level: 20 },
        { id: 3210001, max_Level: 20 },
        { id: 3211002, max_Level: 20 },
        { id: 3211003, max_Level: 30 },
        { id: 3211004, max_Level: 30 },
        { id: 3211005, max_Level: 30 },
        { id: 3211006, max_Level: 30 }
    ],
    [
        { job_id: 322, name: "箭神", level: 120, js: "" },
        { id: 3220004, max_Level: 10 },
        { id: 3221000, max_Level: 10 },
        { id: 3221001, max_Level: 10 },
        { id: 3221002, max_Level: 10 },
        { id: 3221005, max_Level: 10 },
        { id: 3221003, max_Level: 10 },
        { id: 3221006, max_Level: 10 },
        { id: 3221007, max_Level: 10 },
        { id: 3221008, max_Level: 5 }
    ],
    [
        { job_id: 410, name: "刺客", level: 30, js: "" },
        { id: 4100000, max_Level: 20 },
        { id: 4100001, max_Level: 30 },
        { id: 4100002, max_Level: 20 },
        { id: 4101003, max_Level: 20 },
        { id: 4101004, max_Level: 20 },
        { id: 4101005, max_Level: 30 }

    ], [
        { job_id: 420, name: "侠客", level: 30, js: "" },
        { id: 4200000, max_Level: 20 },
        { id: 4200001, max_Level: 20 },
        { id: 4201002, max_Level: 20 },
        { id: 4201003, max_Level: 20 },
        { id: 4201004, max_Level: 30 },
        { id: 4201005, max_Level: 30 },
    ],
    [
        { job_id: 411, name: "无影人", level: 70, js: "" },
        { id: 4110000, max_Level: 20 },
        { id: 4111001, max_Level: 20 },
        { id: 4111002, max_Level: 30 },
        { id: 4111003, max_Level: 20 },
        { id: 4111004, max_Level: 30 },
        { id: 4111005, max_Level: 30 },
        { id: 4111006, max_Level: 20 }
    ],
    [
        { job_id: 412, name: "隐士", level: 120, js: "" },
        { id: 4120002, max_Level: 10 },
        { id: 4120005, max_Level: 10 },
        { id: 4121000, max_Level: 10 },
        { id: 4121003, max_Level: 10 },
        { id: 4121004, max_Level: 10 },
        { id: 4121006, max_Level: 10 },
        { id: 4121007, max_Level: 10 },
        { id: 4121008, max_Level: 10 },
        { id: 4121009, max_Level: 5 }
    ],
    [
        { job_id: 421, name: "独行客", level: 70, js: "" },
        { id: 4210000, max_Level: 20 },
        { id: 4211001, max_Level: 30 },
        { id: 4211002, max_Level: 30 },
        { id: 4211003, max_Level: 20 },
        { id: 4211004, max_Level: 30 },
        { id: 4211005, max_Level: 20 },
        { id: 4211006, max_Level: 30 }
    ],
    [
        { job_id: 422, name: "侠盗", level: 120, js: "" },
        { id: 4220002, max_Level: 10 },
        { id: 4220005, max_Level: 10 },
        { id: 4221000, max_Level: 10 },
        { id: 4221001, max_Level: 10 },
        { id: 4221003, max_Level: 10 },
        { id: 4221004, max_Level: 10 },
        { id: 4221006, max_Level: 10 },
        { id: 4221007, max_Level: 10 },
        { id: 4221008, max_Level: 5 }
    ],
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
    ],
    [
        { job_id: 520, name: "火枪手", level: 30, js: "" },
        { id: 5200000, max_Level: 20 },
        { id: 5201001, max_Level: 20 },
        { id: 5201002, max_Level: 20 },
        { id: 5201003, max_Level: 20 },
        { id: 5201004, max_Level: 20 },
        { id: 5201005, max_Level: 10 },
        { id: 5201006, max_Level: 20 }
    ],
    [
        { job_id: 511, name: "斗士", level: 70, js: "" },
        { id: 5110000, max_Level: 20 },
        { id: 5110001, max_Level: 40 },
        { id: 5111002, max_Level: 30 },
        { id: 5111004, max_Level: 20 },
        { id: 5111005, max_Level: 20 },
        { id: 5111006, max_Level: 30 }
    ],
    [
        { job_id: 512, name: "冲锋队长", level: 120, js: "" },
        { id: 5121000, max_Level: 10 },
        { id: 5121001, max_Level: 10 },
        { id: 5121002, max_Level: 10 },
        { id: 5121003, max_Level: 10 },
        { id: 5121004, max_Level: 10 },
        { id: 5121005, max_Level: 10 },
        { id: 5121007, max_Level: 10 },
        { id: 5121008, max_Level: 5 },
        { id: 5121009, max_Level: 10 },
        { id: 5121010, max_Level: 10 }
    ],
    [
        { job_id: 521, name: "大副", level: 70, js: "" },
        { id: 5210000, max_Level: 20 },
        { id: 5211001, max_Level: 30 },
        { id: 5211002, max_Level: 30 },
        { id: 5211004, max_Level: 30 },
        { id: 5211005, max_Level: 30 },
        { id: 5211006, max_Level: 30 }
    ],
    [
        { job_id: 522, name: "船长", level: 120, js: "" },
        { id: 5221000, max_Level: 10 },
        { id: 5220001, max_Level: 10 },
        { id: 5220002, max_Level: 10 },
        { id: 5221003, max_Level: 10 },
        { id: 5221004, max_Level: 10 },
        { id: 5221006, max_Level: 10 },
        { id: 5221007, max_Level: 10 },
        { id: 5221008, max_Level: 10 },
        { id: 5221009, max_Level: 10 },
        { id: 5221010, max_Level: 5 },
        { id: 5220011, max_Level: 10 }
    ],
    [
        { job_id: 1100, name: "魂骑士 - 一转", level: 10, js: "" },
        { id: 11000000, max_Level: 10 },
        { id: 11001001, max_Level: 10 },
        { id: 11001002, max_Level: 20 },
        { id: 11001003, max_Level: 20 },
        { id: 11001004, max_Level: 20 }
    ], [
        { job_id: 1200, name: "炎术士 - 一转", level: 10, js: "" },
        { id: 12000000, max_Level: 10 },
        { id: 12001001, max_Level: 10 },
        { id: 12001002, max_Level: 10 },
        { id: 12001003, max_Level: 20 },
        { id: 12001004, max_Level: 20 }
    ], [
        { job_id: 1300, name: "风灵使者 - 一转", level: 10, js: "" },
        { id: 13000000, max_Level: 20 },
        { id: 13000001, max_Level: 8 },
        { id: 13001002, max_Level: 10 },
        { id: 13001003, max_Level: 20 },
        { id: 13001004, max_Level: 20 }
    ], [
        { job_id: 1400, name: "夜行者 - 一转", level: 10, js: "" },
        { id: 14000000, max_Level: 10 },
        { id: 14000001, max_Level: 8 },
        { id: 14001002, max_Level: 10 },
        { id: 14001003, max_Level: 10 },
        { id: 14001004, max_Level: 20 },
        { id: 14001005, max_Level: 20 }
    ], [
        { job_id: 1500, name: "奇袭者 - 一转", level: 10, js: "" },
        { id: 15000000, max_Level: 10 },
        { id: 15001001, max_Level: 20 },
        { id: 15001002, max_Level: 20 },
        { id: 15001003, max_Level: 10 },
        { id: 15001004, max_Level: 20 }
    ],
    [
        { job_id: 1110, name: "魂骑士 - 二转", level: 30, js: "" },
        { id: 11100000, max_Level: 20 },
        { id: 11101001, max_Level: 20 },
        { id: 11101002, max_Level: 30 },
        { id: 11101003, max_Level: 20 },
        { id: 11101004, max_Level: 30 },
        { id: 11101005, max_Level: 10 }
    ],
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
    ],
    [
        { job_id: 1210, name: "炎术士 - 二转", level: 30, js: "" },
        { id: 12101000, max_Level: 20 },
        { id: 12101001, max_Level: 20 },
        { id: 12101002, max_Level: 20 },
        { id: 12101003, max_Level: 20 },
        { id: 12101004, max_Level: 20 },
        { id: 12101005, max_Level: 20 },
        { id: 12101006, max_Level: 20 }
    ],
    [
        { job_id: 1211, name: "炎术士 - 三转", level: 70, js: "" },
        { id: 12110000, max_Level: 20 },
        { id: 12110001, max_Level: 20 },
        { id: 12111002, max_Level: 20 },
        { id: 12111003, max_Level: 20 },
        { id: 12111004, max_Level: 20 },
        { id: 12111005, max_Level: 30 },
        { id: 12111006, max_Level: 30 }
    ],
    [
        { job_id: 1310, name: "风灵使者 - 二转", level: 30, js: "" },
        { id: 13100000, max_Level: 20 },
        { id: 13101001, max_Level: 20 },
        { id: 13101002, max_Level: 30 },
        { id: 13101003, max_Level: 20 },
        { id: 13100004, max_Level: 20 },
        { id: 13101005, max_Level: 20 },
        { id: 13101006, max_Level: 10 }
    ],
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
    ],
    [
        { job_id: 1410, name: "夜行者 - 二转", level: 30, js: "" },
        { id: 14100000, max_Level: 20 },
        { id: 14100001, max_Level: 30 },
        { id: 14101002, max_Level: 20 },
        { id: 14101003, max_Level: 20 },
        { id: 14101004, max_Level: 20 },
        { id: 14100005, max_Level: 10 },
        { id: 14101006, max_Level: 20 }
    ],
    [
        { job_id: 1411, name: "夜行者 - 三转", level: 70, js: "" },
        { id: 14111000, max_Level: 30 },
        { id: 14111001, max_Level: 20 },
        { id: 14111002, max_Level: 30 },
        { id: 14110003, max_Level: 20 },
        { id: 14110004, max_Level: 20 },
        { id: 14111005, max_Level: 20 },
        { id: 14111006, max_Level: 30 }
    ],
    [
        { job_id: 1510, name: "奇袭者 - 二转", level: 30, js: "" },
        { id: 15100000, max_Level: 10 },
        { id: 15100001, max_Level: 20 },
        { id: 15101002, max_Level: 20 },
        { id: 15101003, max_Level: 20 },
        { id: 15100004, max_Level: 20 },
        { id: 15101005, max_Level: 20 },
        { id: 15101006, max_Level: 20 }
    ],
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
    ],
    [
        { job_id: 2100, name: "战神 - 1转", level: 10, js: "" },
        { id: 21000000, max_Level: 10 },
        { id: 21001001, max_Level: 15 },
        { id: 21000002, max_Level: 20 },
        { id: 21001003, max_Level: 20 }
    ],
    [
        { job_id: 2110, name: "战神 - 二转", level: 30, js: "" },
        { id: 21100000, max_Level: 20 },
        { id: 21100001, max_Level: 20 },
        { id: 21100002, max_Level: 30 },
        { id: 21101003, max_Level: 20 },
        { id: 21100004, max_Level: 20 },
        { id: 21100005, max_Level: 20 }
    ],
    [
        { job_id: 2111, name: "战神 - 三转", level: 70, js: "" },
        { id: 21110000, max_Level: 20 },
        { id: 21111001, max_Level: 20 },
        { id: 21110002, max_Level: 20 },
        { id: 21110003, max_Level: 30 },
        { id: 21110004, max_Level: 30 },
        { id: 21111005, max_Level: 20 },
        { id: 21110006, max_Level: 20 },
        //{ id: 21110007, max_Level: 20 },
        //{ id: 21110008, max_Level: 20 }
    ],
    [
        { job_id: 2112, name: "战神 - 四转", level: 120, js: "" },
        { id: 21121000, max_Level: 10 },
        { id: 21120001, max_Level: 10 },
        { id: 21120002, max_Level: 10 },
        { id: 21121003, max_Level: 10 },
        { id: 21120004, max_Level: 10 },
        { id: 21120005, max_Level: 10 },
        { id: 21120006, max_Level: 10 },
        { id: 21120007, max_Level: 10 },
        { id: 21121008, max_Level: 5 },
        //{ id: 21120009, max_Level: 10 },
        //{ id: 21120010, max_Level: 10 }
    ]
]

function minSkillLevel(jobid) {//
    for (var i = 0; i < Job_list_Map.length; i++) {
        if (jobid == Job_list_Map[i][0].job_id) {
            for (var j = 1; j < Job_list_Map[i].length; j++) {
                cm.teachSkill(Job_list_Map[i][j].id, 0, Job_list_Map[i][j].max_Level);
            }
            break;
        }
    }
}

function start() {//
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
        return;
    }
    if (status == 0 && mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
    if (status == 0) {
        if (cm.getPlayer().getLevel() < 功能使用等级) {
            cm.sendOk("#d" + 功能使用等级 + "级后再来找我吧！");
            cm.dispose();
            return;
        }
        var text = "#d\r\n";
        text += "#k┏━━━━━━━━━━#r自由换职区#k━━━━━━━━━━┓\r\n\r\n";
        text += "\t#d" + 广播 + " 欢迎使用:[#r更换职业功能#d]\r\n";
        text += "\t#d" + 广播 + " 请选择您更换的职业：\r\n\r\n";
        text += seleJob();
        text += "\r\n#k┗━━━━━━━━━━━━━━━━━━━━━━━━━┛#d\r\n";
        cm.sendYesNo(text);
    } else if (status == 1) {
        sele = selection;
        cm.sendYesNo("#d您确定要将职业转换成为[#r" + getJobNameById(sele) + "#d]吗？");
    } else if (status == 2) {
        startExecution(sele);
        minSkillLevel(sele);
        cm.sendOk("#d转换成功！祝您冒险之旅愉快。");
        cm.dispose();
    }
}

function getJobNameById(job) {
    switch (job) {
        case 0:
            return "新手";
        case 1000:
            return "初心者";
        case 2000:
            return "战童";
        case 2001:
            return "小不点";
        case 3000:
            return "市民";

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
            return "法师";
        case 210:
            return "法师(火.毒)";
        case 211:
            return "巫师(火.毒)";
        case 212:
            return "魔导师(火.毒)";
        case 220:
            return "法师(冰.雷)";
        case 221:
            return "巫师(冰.雷)";
        case 222:
            return "魔导师(冰.雷)";
        case 230:
            return "牧师";
        case 231:
            return "祭司";
        case 232:
            return "主教";

        case 300:
            return "弓箭手";
        case 310:
            return "猎人";
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
            return "独行侠";
        case 422:
            return "侠盗";
        case 430:
            return "见习刀客";
        case 431:
            return "双刀客";
        case 432:
            return "双刀侠";
        case 433:
            return "血刀";
        case 434:
            return "暗影双刀";

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
        case 1110:
        case 1111:
        case 1112:
            return "魂骑士";

        case 1200:
        case 1210:
        case 1211:
        case 1212:
            return "炎术士";

        case 1300:
        case 1310:
        case 1311:
        case 1312:
            return "风灵使者";

        case 1400:
        case 1410:
        case 1411:
        case 1412:
            return "夜行者";

        case 1500:
        case 1510:
        case 1511:
        case 1512:
            return "奇袭者";

        case 2100:
        case 2110:
        case 2111:
        case 2112:
            return "战神";

        case 2200:
        case 2210:
        case 2211:
        case 2212:
        case 2213:
        case 2214:
        case 2215:
        case 2216:
        case 2217:
        case 2218:
            return "龙神";

        case 3200:
        case 3210:
        case 3211:
        case 3212:
            return "唤灵斗师";

        case 3300:
        case 3310:
        case 3311:
        case 3312:
            return "豹弩游侠";

        case 3500:
        case 3510:
        case 3511:
        case 3512:
            return "机械师";

        default:
            return "未知";
    }
}

function startExecution(jobid) {
    var level = cm.getPlayer().getLevel();
    var gainAp = level * 5;
    var gainSp = (level - 10) * 3;
    cm.getPlayer().clearSkills();//清除技能
    cm.processCommand("@清除所有状态");
    //cm.getPlayer().sendMacros0();//宏技能 这个
    cm.resetStats(4, 4, 4, 4);//重置AP点
    if (jobid >= 1100 && jobid <= 1511) {
        gainAp += Math.min(Math.max(level - 10, 0), 60);
    }
    if (jobid >= 200 && jobid <= 232) {
        gainAp += 6;
    }
    cm.processCommand("!ap " + gainAp + "");
    cm.processCommand("!sp " + gainSp + "");
	cm.gainItem(2022518,-1);
    //cm.getPlayer().gainAp(-cm.getPlayer().getRemainingAp());
    //cm.getPlayer().gainsp(-cm.getPlayer().getRemainingSp());
    cm.changeJob(jobid);
}

function seleJob() {
    var text = "";
    for (var i = 0; i < 列表.length; i++) {
        if (cm.getPlayer().getLevel() >= 列表[i].等级) {
            for (var j = 0; j < 列表[i].职业.length; j++) {
                text += " #L" + 列表[i].职业[j] + "#[#r#e" + getJobNameById(列表[i].职业[j]) + "#d#n]#l";
                if (Math.floor(列表[i].职业[j] / 100) != Math.floor(列表[i].职业[j + 1] / 100)) {
                    text += "\r\n\r\n";
                }
            }
            break;
        }
    }
    return text;
}

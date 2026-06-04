
/*
    功能：拍卖按钮功能
    适配：小斌端(079版本)
    作者：QQ462110111
    制作日期：2023-09-14
    功能类型：需要授权才可以使用
*/
var 开心冒险岛 = "#fEffect/CharacterEff1.img/QQ1408745/1/12#";
var dd = " ";
var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";
var 下方向 = "#fEffect/CharacterEff/Shaman/modeSelect/arrow/4/0#";
var 群粉心 = ""+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+"\r\n";
var 感叹 = "#fUI/UIWindow/Quest/icon0#";
var 开 = "#fUI/Basic/CheckBox/0#";   //有框框 无√
var 关 = "#fUI/Basic/CheckBox/1#";   //有框框 有√
var xx = "#fItem/Etc/0427/04270001/Icon9/0#";  //小黄星
var 广播 = "#fUI/CN_Chat/ChattingRoom/BtVolUp/0/normal/0#";
var 美化new = "#fUI/UIWindow/Quest/icon5/1#";

var 列表 = [
    {
		
        坐标: 2140000000, 标题: "常用功能", 权限: false, 编辑A: " ", 编辑B: "",
        内容: [
		   { 标题: "#k自由市场", Npc: 9900004, 链接: "返回自由" },	
		   { 标题: "#k全能传送", Npc: 9900004, 链接: "传送中心" },
		//{ 标题: "#k全能传送", Npc: 9900004, 链接: 1 },
           { 标题: "#k个人信息", Npc: 9900004, 链接: "个人信息" },
           // { 标题: "#k无限资源", Npc: 9900004, 链接: "无限资源" },
			//{ 标题: "#k二 段 跳", Npc: 9900004, 链接: "二段跳" },
          //  { 标题: "#r伤害皮肤", Npc: 9900004, 链接: "伤害皮肤" },	
            //{ 标题: "等级突破", Npc: 9310057, 链接: 0 },		  
            { 标题: "#k吸怪系统", Npc: 9900004, 链接: "吸怪盒子" },
            { 标题: "#r飞升系统", Npc: 9000442, 链接: 0 },	
            { 标题: "#k快速转职", Npc: 9900004, 链接: 2 },	//		
            { 标题: "#d爆率查询", Npc: 9900004, 链接: "查询爆率" },
            { 标题: "#k身外化身", Npc: 9900004, 链接: "身外化身" },
            { 标题: "聚合附加", Npc: 9310059, 链接: "聚合功能1" },
			{ 标题: "强化突破", Npc: 9310059, 链接: "强化突破" },
			{ 标题: "#k快速转职", Npc: 9000431, 链接: 0 },	//	
          //  { 标题: "#r全服双倍", Npc: 9310059, 链接: "全服双倍" },
        ]
    },
    {
        坐标: 2140000001, 标题: "任务系统", 权限: false, 编辑A: " ", 编辑B: "",
        内容: [
            { 标题: "在线奖励", Npc: 9900004, 链接: 9 },
            { 标题: "每日跑商", Npc: 9010009, 链接: 0 },
            { 标题: "赏金任务", Npc: 9900004, 链接: 1236 },
            { 标题: "每日副本", Npc: 9900004, 链接: 180832 },
            { 标题: "每日BOSS", Npc: 9900004, 链接: 180834 },
            { 标题: "每日收集", Npc: 9900004, 链接: 180836 },
			{ 标题: "每周跑环", Npc: 9010009, 链接: 0 },	
			{ 标题: "每周跳跳", Npc: 9062508, 链接: 0 },	

        ]
    },
    {
        坐标: 2140000002, 标题: "挑战系统", 权限: false, 编辑A: " ", 编辑B: "",
        内容: [
			{ 标题: "#kVIP练功房", Npc: 9000434, 链接: "练功房" },
			{ 标题: "#k萌新BOSS", Npc: 9000434, 链接: "传统BOSS" },
			{ 标题: "#k女神BOSS", Npc: 9000434, 链接: "女神BOSS" },
			{ 标题: "#k高级BOSS", Npc: 9000434, 链接: "高级BOSS" },
			{ 标题: "#k终级BOSS", Npc: 9000434, 链接: "终级BOSS" },
			{ 标题: "#k神级BOSS", Npc: 9000434, 链接: "神级BOSS" },
        ]
    },

    {
        坐标: 2140000003, 标题: "购物系统", 权限: false, 编辑A: " ", 编辑B: "\r\n\r\n",
        内容: [
            { 标题: "#k杂货商店", Npc: 9900004, 链接: "杂货商店" },	//	
            { 标题: "#k武器商店", Npc: 9900004, 链接: "武器商店" },	//	
            { 标题: "#k防具商店", Npc: 9900004, 链接: "防具商店" },	//	
            { 标题: "#r赞助系统", Npc: 9900004, 链接: 18086 },
			{ 标题: "#k超值月卡", Npc: 9900004, 链接: "超值月卡" },

        ]
    },

    {
        坐标: 2140000012, 标题: "装备系统", 权限: false, 编辑A: " ", 编辑B: "",
        内容: [
          //  { 标题: "美容美发", Npc: 9201064, 链接: 0 },		
            { 标题: "时装升级", Npc: 1530134, 链接: 0 },
            { 标题: "属性转换", Npc: 1530000, 链接: 0 },
	
            { 标题: "装备系统", Npc: 9310072, 链接: 0 },	
        ]
    },
    {
        坐标: 2140000011, 标题: "强化系统", 权限: false, 编辑A: " ", 编辑B: "",
        内容: [


		

            { 标题: "武器升星", Npc: 9000441, 链接: "武器升星星" },
            { 标题: "防具升星", Npc: 9000441, 链接: "防具升星星" },			
			
        ]
    },
    {
        坐标: 2140000013, 标题: "兑换系统", 权限: false, 编辑A: " ", 编辑B: "",
        内容: [
            { 标题: "合成低贝", Npc: 9900004, 链接: 1808528 },
            { 标题: "矿石合成", Npc: 9000437, 链接: "矿石合成" },
            { 标题: "必成合成", Npc: 9310074, 链接: 100 },
            { 标题: "正义合成", Npc: 9000437, 链接: "合成正义混沌" },
            { 标题: "彩蛋合成", Npc: 9900004, 链接: 180855 },
            { 标题: "卷轴合成", Npc: 9310072, 链接: 21 },
            { 标题: "中 国 心", Npc: 9900004, 链接: 1808523 },
            { 标题: "时 装 蛋", Npc: 9900004, 链接: 180856 },
            { 标题: "技能兑换", Npc: 9000437, 链接: "技能书兑换" },			
            { 标题: "技能合成", Npc: 9000437, 链接: "技能册合成" },
            
        ]
    },
    {
        坐标: 2140000014, 标题: "综合系统", 权限: false, 编辑A: " ", 编辑B: "\r\n\r\n",
        内容: [
            { 标题: "结婚登记", Npc: 9310084, 链接: 0 },
            { 标题: "个人挑战", Npc: 9000434, 链接: "个人挑战" },
            { 标题: "呱 呱 卡", Npc: 9390364, 链接: 0 },
            { 标题: "快乐系统", Npc: 9900004, 链接: 9390364 },		
            { 标题: "#rP K 系统", Npc: 9900004, 链接: 9390365 },	

            { 标题: "#r师徒系统", Npc: 9310073, 链接: 0 },
			{ 标题: "#r删除物品", Npc: 9900004, 链接: "删除物品" },	

            { 标题: "#r家族系统", Npc: 9900004, 链接: "家族建设" },	
        ]
    },

    {
        坐标: 2140000004, 标题:"#rGM管理#n#g(玩家看不见)#n", 权限: true, 编辑A: " ", 编辑B: "",
        内容: [
            { 标题: "查询玩家", Npc: 9900004, 链接: "在线玩家" },
            { 标题: "开启吃鸡", Npc: 9900004, 链接: "吃鸡123456789" },
            { 标题: "在线充值", Npc: 9900004, 链接: "KaixinMS管理面板" },
            { 标题: "属性修改", Npc: 9900004, 链接: "属性修改" },
            //{ 标题: "在线玩家", Npc: 9900004, 链接: 1012 },
			{ 标题: "#r无限资源", Npc: 9900004, 链接: "无限资源" },
            { 标题: "二 段 跳", Npc: 9900004, 链接: "二段跳" },
            { 标题: "全服活动", Npc: 9900004, 链接: "vip功能" },
            { 标题: "高级搜索", Npc: 9900003, 链接: "AdvancedSearch" },
          //  { 标题: "双爆2小时", Npc: 9900004, 链接: "开启全双爆2小时" },
            //{ 标题: "双爆4小时", Npc: 9900004, 链接: "开启全服双爆4小时" },
           // { 标题: "双爆8小时", Npc: 9900004, 链接: "开启全服双爆8小时" },
            //{ 标题: "双爆12小时", Npc: 9900004, 链接: "开启全服双爆12小时" },
            
            { 标题: "刷新地图", Npc: 9900003, 链接: "刷新地图" },
            { 标题: "拍 卖 行", Npc: 9330115, 链接: "拍卖" },	

        ]
    },
 /*   {
        坐标: 2140000005, 标题: "#rGM/重载", 权限: true, 编辑A: " ", 编辑B: "",
        内容: [
            { 标题: "刷新地图", Npc: 9900003, 链接: "刷新地图" },
            { 标题: "拍 卖 行", Npc: 9330115, 链接: "拍卖" },			
        ]
    },*/
]

var 标题, 分割;

var  b7 = "#fUI/ChatBalloon.img/155/nw#";//右上
var  b8 = "#fUI/ChatBalloon.img/19/nw#";//右上
var  b9 = "#fUI/ChatBalloon.img/19/ne#";//右上

function start() {//
    if (标题 == null) {
        标题 = 列表[0].标题;
        分割 = 分割线();
        随机界面();
		//cm.刷新地图(910000000)
}
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
        if (mode == 1) {
            status++;
        } else {
            status--;
        }
	/*	 if (cm.getPlayer().getLevel() <= 8 ) {//等级
        //cm.个人公告("等级不足 8 级无法打开 ,请提升等级后尝试");
        cm.sendOk("等级不足 8 级无法打开 ,请提升等级后尝试");
        cm.dispose();
        return;
    }*/
	if (cm.getPlayer().getMapId() == 180000001) {
    cm.getPlayer().dropMessage(5, "这里无法打开");
    cm.dispose();
    return;
}	
	 if (cm.getBossRank6("消费积分",1) == -1) {
       cm.setBossRank6("累计积分",1,0);
       cm.setBossRank5("消费积分",1,0);
       cm.setBossRank1("钓鱼积分1",1,0);
       cm.setBossRank7("无尽之塔分数",1,0);
       cm.setBossRank2("钓鱼成功次数",1,0);
       cm.setBossRank3("破功次数",1,0);
       cm.setBossRank2("钓鱼成功次数",1,0);
       cm.setBossRank4("每日充值2",1,0);
       cm.setBossRank3("每周任务",1,0);
       //status = -1;
 } else {
 
	   status = -1;
}
	
	
	
        if (status == 0) {
            var text = "#k";
            text += ""+dd+"\r\n\t\t\t"+开心冒险岛+"\r\n"+群粉心+""
;
		//	cm.召唤怪物(100100, 100000000000, 10000000, 1, 200000301, 162,174); 
            //text +="\r\n\t" + b7 + "  #r#d欢迎来到~e新 开心 "+b8+"~e 聚合功能 e~"+b9+"#n#k #n\r\n\r\n";
			
           // text +="  #b元宝:「#r" + cm.getString(cm.getmoneyb(),2) + "#b」在线时间:「#r" + cm.getString(cm.getPlayer().getTodayOnlineTime(),2) + " 分钟#b」点卷余额:「#r" + cm.getString(cm.getPlayer().getCSPoints(1),2) + "#b」\r\n";

            var isgm = cm.getPlayer().isGM();
            for (var i = 0; i < 列表.length; i++) {
                var 权限 = (列表[i].权限 ? (isgm ? true : false) : true);
                if (权限) {
                    if (标题 == 列表[i].标题) {
                        text += "#e" + 列表[i].编辑A + "" + 列表[i].编辑A + "#L" + 列表[i].坐标 + "##r" + 列表[i].标题 + "#d" + 列表[i].编辑B + "#l#n";
                    } else {
                        text += "#e" + 列表[i].编辑A + "#L" + 列表[i].坐标 + "##b" + 列表[i].标题 + "#d" + 列表[i].编辑B + "#l#n";
                    }
                }
            }
            text += "\r\n\r\n"+nex1+"";
			
			if (cm.getMonsterCount(993185100) >= 1) {
				text += " #d#e#L77677887#全民拼图 召唤雪人 世界BOSS传送门#k.#r火热 进行中 #k#l#n\r\n\r\n";
            }		
			
            text += "" + x_ + "";
            for (var i = 0; i < 列表.length; i++) {
                if (标题 == 列表[i].标题) {
                    for (var j = 0, jj = 1; j < 列表[i].内容.length; j++, jj++) {
                        text += " #L" + j + "#" + a7 + "#d" + 列表[i].内容[j].标题 + "" + a8 + "#l";
                        if (jj % 3 == 0) {
                            if (列表[i].内容.length - jj == 0) {
                                text += "\r\n" + x_ + "";
                            } else {
                                text += "\r\n\r\n" + x_ + "";
                            }
                        } else if (列表[i].内容.length - jj == 0 && jj % 3 >= 1) {
                            text += "\r\n" + x_ + "";
                        }
                    }
                    break;
                }
            }

            text += "\r\n " + nex2 + "\r\n";
            cm.sendSimple(text);
        } else if (status == 1) {
            sele1 = selection;
			if (sele1 == 77677887) {
				cm.warp(993185100,0);
               cm.喇叭(2,"钓鱼拼图：["+cm.getName()+"]从 1X 点击拍卖 => 钓鱼拼图 已进入地图");
                cm.dispose();
				return;
			}
            for (var i = 0; i < 列表.length; i++) {
                if (sele1 == 列表[i].坐标) {
                    标题 = 列表[i].标题;
                    start();
                    return;
                }
            }
            for (var i = 0; i < 列表.length; i++) {
                if (标题 == 列表[i].标题) {
                   var is = 列表[i].内容[sele1];
                    cm.dispose();
                    cm.openNpc(is.Npc, is.链接);
                    break;
                }
            }
        }
    }
}

function 分割线() {
    var text = "";
    var list = [
        //{ UI: "#fEffect/CharacterEff.img/1022223/7/0#", length: 21 },
        //{ UI: "#fEffect/CharacterEff.img/1022223/8/0#", length: 22 },
        { UI: "#fEffect/CharacterEff.img/1022223/9/0#", length: 24 },
    ];
    var random = Math.floor(Math.random() * list.length);
    for (var i = 0; i < list[random].length; i++) {
        //var random = Math.floor(Math.random() * list.length);
        text += list[random].UI;
    }
    text += "";
    return text;
}

function 随机界面() {
    var list = [
        { ui: "028", len: 26, _x: 0 },
    ];
    var chance = Math.floor(Math.random() * list.length);
    var ui = parseInt(list[chance].ui);
    //cm.个人喇叭(5, ui)
    var len = list[chance].len;
    var _x = list[chance]._x;
    a1 = "#fUI/ChatBalloon.img/" + ui + "/nw#";//右上
    a2 = "#fUI/ChatBalloon.img/" + ui + "/n#";//上中
    a3 = "#fUI/ChatBalloon.img/" + ui + "/ne#";//右上
    a7 = "#fUI/ChatBalloon.img/" + ui + "/w#";//内容左
    a8 = "#fUI/ChatBalloon.img/" + ui + "/e#";//内容右
    a4 = "#fUI/ChatBalloon.img/" + ui + "/sw#";//右下
    a5 = "#fUI/ChatBalloon.img/" + ui + "/s#";//下中
    a6 = "#fUI/ChatBalloon.img/" + ui + "/se#";//右下
    nex1 = "" + a1 + "";
    nex2 = "" + a4 + "";
    x_ = "";
    for (var i = 0; i < _x; i++) {
        x_ += " ";
    }
    for (var i = 0; i < len; i++) {
        nex1 += "" + a2 + "";
        nex2 += "" + a5 + "";
    }
    nex1 += "" + a3 + "";
    nex2 += "" + a6 + "";
}


var 感叹 = "#fUI/UIWindow/Quest/icon0#";
var 开 = "#fUI/Basic/CheckBox/0#";   //有框框 无√
var 关 = "#fUI/Basic/CheckBox/1#";   //有框框 有√
var xx = "#fItem/Etc/0427/04270001/Icon9/0#";  //小黄星
var 广播 = "#fUI/CN_Chat/ChattingRoom/BtVolUp/0/normal/0#";
var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";//红桃心
var 装备 = "#fUI/UIWindow.img/Shop/TabSell/enabled/0#";
var 消耗 = "#fUI/UIWindow.img/Shop/TabSell/enabled/1#";
var 设置 = "#fUI/UIWindow.img/Shop/TabSell/enabled/2#";
var 其他 = "#fUI/UIWindow.img/Shop/TabSell/enabled/3#";
var 特殊 = "#fUI/UIWindow.img/Shop/TabSell/enabled/4#";
var 消耗金币 = 1000000;









var 几率 = [100, 100];//1次突破/2次突破几率

var 列表 = [
    {
        选项: "英雄技能突破",
        匹配: [
            {
                消耗道具: 2022692,
                适应职业群: [110, 112],//100~132 之间都是战士代码范围
                技能: [//前置等级表示突破时要求的技能等级必须达到才可以突破
				    { 代码: 1000000, 前置等级: 16 },//生命恢复
					{ 代码: 1000002, 前置等级: 8 },//生命恢复
					{ 代码: 1001003, 前置等级: 20 },//圣甲术
                    { 代码: 1001004, 前置等级: 20 },//强力攻击
				    { 代码: 1001005, 前置等级: 20 },//群体攻击
					//以上是一转
					{ 代码: 1100002, 前置等级: 30 },//终极剑
					{ 代码: 1100003, 前置等级: 30 },//终极斧
					{ 代码: 1101004, 前置等级: 20 },//快速剑
					{ 代码: 1101005, 前置等级: 20 },//快速斧
					{ 代码: 1101006, 前置等级: 20 },//愤怒之火
					{ 代码: 1101007, 前置等级: 30 },//伤害反击
					{ 代码: 1111002, 前置等级: 30 },//生命加强
					{ 代码: 1111008, 前置等级: 30 },//生命加强
					{ 代码: 1120003, 前置等级: 30 },//生命加强
					{ 代码: 1121002, 前置等级: 30 },//生命加强
					{ 代码: 1121010, 前置等级: 30 },//生命加强
					{ 代码: 1121008, 前置等级: 30 },//生命加强
                ]
            },

        ]
    },
    {
        选项: "圣骑技能突破",
        匹配: [
            {
                消耗道具: 2022692,
                适应职业群: [120, 122],//100~132 之间都是战士代码范围
                技能: [//前置等级表示突破时要求的技能等级必须达到才可以突破
				    { 代码: 1000000, 前置等级: 16 },//生命恢复
					{ 代码: 1000002, 前置等级: 8 },//生命恢复
					{ 代码: 1001003, 前置等级: 20 },//圣甲术
                    { 代码: 1001004, 前置等级: 20 },//强力攻击
				    { 代码: 1001005, 前置等级: 20 },//群体攻击
					//以上是一转
					{ 代码: 1200002, 前置等级: 30 },//终极剑
					{ 代码: 1200003, 前置等级: 30 },//终极钝器
				    { 代码: 1201004, 前置等级: 20 },//快速剑
					{ 代码: 1201005, 前置等级: 20 },//快速钝器
					{ 代码: 1201007, 前置等级: 30 },//伤害反击
					//以上是二转
					{ 代码: 1211003, 前置等级: 30 },//烈焰之剑
					{ 代码: 1211004, 前置等级: 30 },//烈焰钝器
					{ 代码: 1211005, 前置等级: 30 },//寒冰之剑
					{ 代码: 1211006, 前置等级: 30 },//寒冰钝器
					{ 代码: 1211007, 前置等级: 30 },//雷电之剑
					{ 代码: 1211008, 前置等级: 30 },//雷电钝器
					//以上是三转
					{ 代码: 1221002, 前置等级: 30 },//稳如泰山
					{ 代码: 1221003, 前置等级: 30 },//圣灵之剑
					{ 代码: 1221004, 前置等级: 30 },//圣灵之锤
					{ 代码: 1221009, 前置等级: 30 },//连环环破
                ]
            },

        ]
    },
    {
        选项: "黑骑技能突破",
        匹配: [
            {
                消耗道具: 2022692,
                适应职业群: [130, 132],//100~132 之间都是战士代码范围
                技能: [//前置等级表示突破时要求的技能等级必须达到才可以突破
				    { 代码: 1000000, 前置等级: 16 },//生命恢复
					{ 代码: 1000002, 前置等级: 8 },//生命恢复
					{ 代码: 1001003, 前置等级: 20 },//圣甲术
                    { 代码: 1001004, 前置等级: 20 },//强力攻击
				    { 代码: 1001005, 前置等级: 20 },//群体攻击
					//以上是一转
				    { 代码: 1300002, 前置等级: 30 },//终极枪
					{ 代码: 1300003, 前置等级: 30 },//终极矛
				    { 代码: 1301004, 前置等级: 20 },//快速枪
				    { 代码: 1301005, 前置等级: 20 },//快速矛
					{ 代码: 1301007, 前置等级: 30 },//神圣之火
					//以上是二转
					{ 代码: 1311001, 前置等级: 30 },//枪连击
					{ 代码: 1311002, 前置等级: 30 },//矛连击
					{ 代码: 1311003, 前置等级: 30 },//枪连击
					{ 代码: 1311004, 前置等级: 30 },//枪连击
					{ 代码: 1311006, 前置等级: 30 },//龙咆哮
					//以上是三转
				    { 代码: 1320006, 前置等级: 30 },//稳如泰山
					{ 代码: 1321002, 前置等级: 30 },//恶龙附身
                ]
            },

        ]
    },
]

var myskill;

function start() {
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
        myskill = null;
        var text = "#d";
		text += " #k┏━#r技能突破#k━━━━━━━━━━━━━━━━━━┓\r\n";
        text += "\t#d" + 粉心 + ":技能达到满级后可以突破2级\r\n";
        text += "\t#d" + 粉心 + ":突破概率，60%\r\n";
        text += "\t#d" + 粉心 + ":下方选择要突破的技能：\r\n";
		text += " #k┗━━━━━━━━━━━━━━━━━━━━━━━━┛#k\r\n";
        for (var i = 0; i < 列表.length; i++) {
            text += "#L"+ i + "#" + 列表[i].选项 + "#l\r\n";
        }
        cm.sendOk(text);
    } else if (status == 1) {
        sl1 = selection;
        var myjobid = cm.getPlayer().getJob();
        for (var i = 0; i < 列表[sl1].匹配.length; i++) {
            for (var j = 0; j < 列表[sl1].匹配[i].适应职业群.length; j++) {
                if (myjobid >= 列表[sl1].匹配[i].适应职业群[0] && myjobid <= 列表[sl1].匹配[i].适应职业群[1]) {
                    myskill = 列表[sl1].匹配[i];
                    break;
                }
            }
            if (myskill != null) {
                break;
            }
        }
        if (myskill == null) {
            cm.sendOk("没有适配到您当前职业的可突破技能！");
            cm.dispose();
        } else {
            var text = "#d";
            text += "突破所需条件:#v" + myskill.消耗道具 + ":#x1个\r\n";
			text += "突破所需金币："+消耗金币+" \r\n";
            text += "下方选择要突破的技能：\r\n";
            text += "注意：突破需要前置等级(技能自身等级)\r\n";
            for (var k = 0; k < myskill.技能.length; k++) {
                text += "#L" + k + "##s" + myskill.技能[k].代码 + "##q" + myskill.技能[k].代码 + "#(#r前置:" + myskill.技能[k].前置等级 + "级以上#d)#l\r\n";
            }
            cm.sendOk(text);
        }
    } else if (status == 2) {
        sl2 = selection;
        var mylv = cm.getPlayer().getSkillLevel(myskill.技能[sl2].代码);
        var maxLv = getSkillMaxLv(myskill.技能[sl2].代码);
        if (mylv >= maxLv) {
            cm.sendOk("这个技能您已经达到满级了！");
            status = -1; return;
        } else {
            var 概率 = 几率[2 - (maxLv - mylv)];
            var text = "#d";
            text += "突破所需条件:#v" + myskill.消耗道具 + ":#x1个\r\n";
			text += "突破所需金币："+消耗金币+" \r\n";
            text += "选择要突破技能：#s" + myskill.技能[sl2].代码 + "##q" + myskill.技能[sl2].代码 + "#(#r前置:" + myskill.技能[sl2].前置等级 + "级以上#d)\r\n";
            text += "当前角色该技能:" + mylv + "级 (最高可突破到:" + maxLv + "级)\r\n";
            text += "突破成功几率:" + 概率 + "% (突破失败技能等级会下降)\r\n";
            text += "是否确定要突破？\r\n";
            cm.sendYesNo(text);
        }
    } else if (status == 3) {
        var mylv = cm.getPlayer().getSkillLevel(myskill.技能[sl2].代码);
        var maxLv = getSkillMaxLv(myskill.技能[sl2].代码);
        if (mylv >= maxLv) {
            cm.sendOk("这个技能您已经达到满级了！");
            status = -1; return;
        } else if (mylv < myskill.技能[sl2].前置等级) {
            cm.sendOk("必须将：#s" + myskill.技能[sl2].代码 + "##q" + myskill.技能[sl2].代码 + "# 学习到" + myskill.技能[sl2].前置等级 + "级以上才可操作！");
            status = -1; return;
        } else {
            cm.gainItem(myskill.消耗道具, -1);
			cm.gainMeso(-消耗金币);
            var 概率 = 几率[2 - (maxLv - mylv)];
            if (概率 >= Math.floor(Math.random() * 100) + 1) {
                cm.teachSkill(myskill.技能[sl2].代码, mylv + 1, mylv + 1);
                cm.getPlayer().dropMessage(1, "恭喜您，技能突破成功！");
				cm.喇叭(4,"[技能突破] 玩家:<"+cm.getName()+">战士系列[技能]突破成功,当前达到："+cm.getPlayer().getSkillLevel(myskill.技能[sl2].代码)+"级,大家恭喜吧！！！");
            } else {
                //cm.teachSkill(myskill.技能[sl2].代码, mylv - 1, mylv - 1);
                cm.getPlayer().dropMessage(1, "很遗憾，技能突破失败！");
				cm.喇叭(4,"[技能突破] 玩家:<"+cm.getName()+">战士系列[技能]突破失败,脸比技术还黑！！！");
            }
            cm.dispose();
        }
    }
}

function getSkillMaxLv(id) {
    var Skill = Packages.client.SkillFactory.getSkill(id);
    var maxLv = Skill.getMaxLevel();
    return maxLv;
}
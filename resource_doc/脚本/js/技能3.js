
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
var 消耗金币 = 10000000;


var 列表 = [
    {
        选项: "神射手技能突破",
        匹配: [
            {
                消耗道具: 2022694,
                适应职业群: [310, 312],//100~132 之间都是战士代码范围
                技能: [//前置等级表示突破时要求的技能等级必须达到才可以突破
                    { 代码: 3000000, 前置等级: 20, 突破概率: 100 },//精准箭
					{ 代码: 3000001, 前置等级: 20, 突破概率: 100 },//强力箭
					{ 代码: 3000002, 前置等级: 8, 突破概率: 100 },//远程箭  距离190
					{ 代码: 3001003, 前置等级: 20, 突破概率: 100 },//集中术
				    { 代码: 3001005, 前置等级: 20, 突破概率: 100 },//二连射
					//以上是一转
				    { 代码: 3100001, 前置等级: 30, 突破概率: 80 },//终极弓
					{ 代码: 3101002, 前置等级: 20, 突破概率: 80 },//快速箭
					{ 代码: 3101004, 前置等级: 20, 突破概率: 80 },//无形箭
				    { 代码: 3101005, 前置等级: 30, 突破概率: 80 },//爆炸箭
					//以上是二转
				    { 代码: 3111004, 前置等级: 30, 突破概率: 60 },//箭雨
				    { 代码: 3111006, 前置等级: 30, 突破概率: 60 },//箭扫射
					//以上是三转
					{ 代码: 3121002, 前置等级: 30, 突破概率: 40 },//火眼晶晶
					{ 代码: 3121004, 前置等级: 30, 突破概率: 40 },//暴风箭雨
					{ 代码: 3121008, 前置等级: 30, 突破概率: 40 },//集中精力
					//以上是四转
                ]
            },

        ]
    },
    {
        选项: "箭神技能突破",
        匹配: [
            {
                消耗道具: 2022694,
                适应职业群: [320, 322],//100~132 之间都是战士代码范围
                技能: [//前置等级表示突破时要求的技能等级必须达到才可以突破
                    { 代码: 3000000, 前置等级: 20, 突破概率: 100 },//精准箭
					{ 代码: 3000001, 前置等级: 20, 突破概率: 100 },//强力箭
					{ 代码: 3000002, 前置等级: 8, 突破概率: 100 },//远程箭  距离190
					{ 代码: 3001003, 前置等级: 20, 突破概率: 100 },//集中术
				    { 代码: 3001005, 前置等级: 20, 突破概率: 100 },//二连射
					//以上是一转
					{ 代码: 3200001, 前置等级: 30, 突破概率: 80 },//终极弩
					{ 代码: 3201004, 前置等级: 20, 突破概率: 80 },//无形弩
				    { 代码: 3201002, 前置等级: 20, 突破概率: 80 },//快速弩
					{ 代码: 3201005, 前置等级: 30, 突破概率: 80 },//穿透箭
					//以上是二转
					{ 代码: 3211003, 前置等级: 30, 突破概率: 60 },//寒冰箭
					{ 代码: 3211004, 前置等级: 30, 突破概率: 60 },//升龙弩
					{ 代码: 3211006, 前置等级: 30, 突破概率: 40 },//箭扫射
					//以上是三转
					{ 代码: 3221002, 前置等级: 30, 突破概率: 30 },//火眼晶晶
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
        text += "\t\t#d" + 粉心 + "" + 粉心 + "" + 粉心 + "技能达到满级后可以突破一级" + 粉心 + "" + 粉心 + "" + 粉心 + "\r\n";
        //text += "\t#d" + 粉心 + ":突破概率，60%\r\n";
        text += "\t\t#d" + 粉心 + "" + 粉心 + "" + 粉心 + "下方选择当前职业突破的技能" + 粉心 + "" + 粉心 + "" + 粉心 + "\r\n";
		text += " #k┗━━━━━━━━━━━━━━━━━━━━━━━┛#k\r\n";
        for (var i = 0; i < 列表.length; i++) {
            text += "\t\t\t\t #L"+ i + "#" + 列表[i].选项 + "#l\r\n";
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
                text += "#L" + k + "##s" + myskill.技能[k].代码 + "##q" + myskill.技能[k].代码 + "#(#r前置:" + myskill.技能[k].前置等级 + "级以上#d)  #b成功率:" + myskill.技能[k].突破概率 + "%#d#l\r\n";
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
            var 技能突破概率 = myskill.技能[sl2].突破概率; // 获取当前技能的突破概率
            var text = "#d";
            text += "突破所需条件:#v" + myskill.消耗道具 + ":#x1个\r\n";
			text += "突破所需金币："+消耗金币+" \r\n";
            text += "选择要突破技能：#s" + myskill.技能[sl2].代码 + "##q" + myskill.技能[sl2].代码 + "#(#r前置:" + myskill.技能[sl2].前置等级 + "级以上#d)\r\n";
            text += "当前角色该技能:" + mylv + "级 (最高可突破到:" + maxLv + "级)\r\n";
            text += "突破成功几率:" + 技能突破概率 + "% (突破失败技能等级会下降)\r\n";
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
            var 技能突破概率 = myskill.技能[sl2].突破概率; // 获取当前技能的突破概率
            if (技能突破概率 >= Math.floor(Math.random() * 100) + 1) {
                cm.teachSkill(myskill.技能[sl2].代码, mylv + 1, mylv + 1);
                cm.getPlayer().dropMessage(1, "恭喜您，技能突破成功！");
				cm.喇叭(2,"[技能突破] 玩家:<"+cm.getName()+">弓箭手系列[技能]突破成功,当前达到："+cm.getPlayer().getSkillLevel(myskill.技能[sl2].代码)+"级,大家恭喜吧！！！");
            } else {
                //cm.teachSkill(myskill.技能[sl2].代码, mylv - 1, mylv - 1);
                cm.getPlayer().dropMessage(1, "很遗憾，技能突破失败！");
				cm.喇叭(3,"[技能突破] 玩家:<"+cm.getName()+">弓箭手系列[技能]突破失败,好惨啊！！！");
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
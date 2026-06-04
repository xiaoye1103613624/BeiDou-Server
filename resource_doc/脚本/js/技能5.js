
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
        选项: "冲锋队长技能突破",
        匹配: [
            {
                消耗道具: 2022696,
                适应职业群: [510, 512],//100~132 之间都是海盗代码范围
                技能: [//前置等级表示突破时要求的技能等级必须达到才可以突破
					{ 代码: 5000000, 前置等级: 20, 突破概率: 100 },//快动作 
                    { 代码: 5001001, 前置等级: 20, 突破概率: 100 },//百裂拳
				    { 代码: 5001002, 前置等级: 20, 突破概率: 100 },//半月踢
					//以上是一转
					{ 代码: 5101006, 前置等级: 20, 突破概率: 80 },//急速拳
				    { 代码: 5101002, 前置等级: 20, 突破概率: 80 },//回马
				    { 代码: 5101003, 前置等级: 20, 突破概率: 80 },//升龙连击
					{ 代码: 5101004, 前置等级: 20, 突破概率: 80 },//贯骨击
					//以上是二转
				    { 代码: 5111005, 前置等级: 20, 突破概率: 60 },//超人变形
					{ 代码: 5111006, 前置等级: 30, 突破概率: 60 },//碎石乱击
					//以上是三转
					{ 代码: 5121003, 前置等级: 20, 突破概率: 40 },//超级变身
					{ 代码: 5121001, 前置等级: 30, 突破概率: 40 },//潜龙出渊
					{ 代码: 5121007, 前置等级: 30, 突破概率: 40 },//光速拳
					{ 代码: 5121004, 前置等级: 30, 突破概率: 40 },//金手指
					//以上是四转
                ]
            },

        ]
    },
    {
        选项: "船长技能突破",
        匹配: [
            {
                消耗道具: 2022696,
                适应职业群: [520, 522],//100~132 之间都是海盗代码范围
                技能: [//前置等级表示突破时要求的技能等级必须达到才可以突破
                    { 代码: 5000000, 前置等级: 20, 突破概率: 100 },//快动作 
                    { 代码: 5001001, 前置等级: 20, 突破概率: 100 },//百裂拳
				    { 代码: 5001002, 前置等级: 20, 突破概率: 100 },//半月踢
					{ 代码: 5001003, 前置等级: 20, 突破概率: 100 },//双弹射击
					//以上是一转
					{ 代码: 5201003, 前置等级: 20, 突破概率: 80 },//速射
					{ 代码: 5201001, 前置等级: 20, 突破概率: 80 },//快枪手
					//以上是二转
					{ 代码: 5210000, 前置等级: 20, 突破概率: 60 },//三连射杀
					//以上是三转
					{ 代码: 5221003, 前置等级: 30, 突破概率: 40 },//地毯空袭
					{ 代码: 5221007, 前置等级: 30, 突破概率: 40 },//急速射
					{ 代码: 5221004, 前置等级: 30, 突破概率: 40 },//金属风暴
					{ 代码: 5221008, 前置等级: 30, 突破概率: 40 },//重量炮击
					{ 代码: 5221006, 前置等级: 10, 突破概率: 40 },//武装
					//以上是四转
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
				cm.喇叭(2,"[技能突破] 玩家:<"+cm.getName()+">海盗系列[技能]突破成功,当前达到："+cm.getPlayer().getSkillLevel(myskill.技能[sl2].代码)+"级,大家恭喜吧！！！");
            } else {
                //cm.teachSkill(myskill.技能[sl2].代码, mylv - 1, mylv - 1);
                cm.getPlayer().dropMessage(1, "很遗憾，技能突破失败！");
				cm.喇叭(3,"[技能突破] 玩家:<"+cm.getName()+">海盗系列[技能]突破失败,好惨啊！！！");
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
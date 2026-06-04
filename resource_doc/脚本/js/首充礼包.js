// 定义全局变量
var 萌新系统 = "#fEffect/CharacterEff1.img/QQ1408745/0/9#";
var 赞助中心 = "#fEffect/CharacterEff1.img/QQ1408745/1/9#";
var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";
var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";
var 群粉心 = ""+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+"\r\n";
var 红箭头 = "#fUI/UIWindow.img/Quest/icon9/0#"; // 红色右箭头
var 蓝箭头 = "#fUI/UIWindow.img/Quest/icon8/0#"; // 蓝色右箭头
var 提示 = "#fUI/CN_Chat/ChattingRoom/BtVolUp/0/normal/0#";
var 银杏叶 = "#fMap/MapHelper/weather/maple/3#";
var 彩色礼包 = "#v5680200#";
var 彩虹 = "#fEffect/ItemEff/1071085/effect/walk1/2#";
var 金币图标 = "#fUI/UIWindow.img/Item/BtCoin/normal/0#";
var 警报灯 = "#fUI/StatusBar/BtClaim/normal/0#";
var 点券图标 = "#fUI/CashShop/CashItem/0#";
var 奖励 = "#fUI/UIWindow.img/Quest/reward#";
var 魔法箭头右 = "#fUI/UIWindow.img/MonsterBook/arrowRight/normal/0#";  // 魔法箭头右
var 魔法箭头左 = "#fUI/UIWindow.img/MonsterBook/arrowLeft/normal/0#";   // 魔法箭头左
var 感叹号 = "#fUI/UIWindow/Quest/icon0#";
var 正方箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";
var 功能名称 = "28元首充豪华礼包";


// 脚本主体
function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
        return;
    }

    if (status >= 0 && mode == 0) {
        cm.sendOk("感谢你的光临！");
        cm.dispose();
        return;
    }

    if (mode == 1) {
        status++;
    } else {
        status--;
    }

    if (status == 0) {
        displayMainOptions();
    } else if (status == 1) {
        handleSelection(selection);
    }
}

// 显示主界面选项
function displayMainOptions() {
    var text = "";
//    text += "     " + 彩虹 + "#e#r首充28元,享受超级豪华礼包!" + 彩虹 + "#n\r\n\r\n";
	text += " #k┏━#r冒险岛提示#k━━━━━━━━━━━━━━━━━┓\r\n";
	text += "\t#d" + 提示 + " 欢迎来到:[#r" + 功能名称 + "#d]\r\n";
	text += "\t#d" + 提示 + " 每个角色只能购买一次，不可重复购买！\r\n";
	text += "\t#d" + 提示 + " 需要 #r28#d 赞助点，当前余额：#b" + cm.getPlayer().getmoney() + " #d\r\n";
	text += "\t#d" + 提示 + " 自强玩家必备礼包，购买后可横扫所有复古BOSS\r\n";
	text += " #k┗━━━━━━━━━━━━━━━━━━━━━━━┛#d\r\n";
	
	text += "\t"+正方箭头+"#k[获得]:[" + 金币图标 + "][金币]:[#r2亿#k]\r\n\r\n";
	text += "\t"+正方箭头+"#k[获得]:[" + 点券图标 + "][点卷]:[#r2W#k]\r\n\r\n";
	text += "\t"+正方箭头+"#k[获得]:[" + 点券图标 + "][抵用]:[#r2W#k]\r\n\r\n";
	text += "\t"+正方箭头+"#k[获得]:[" + 点券图标 + "][累计]:[#r28点#k]\r\n";
			
	text += "\t"+正方箭头+"#k[获得]:[#b#v 1132300##k][#b#z 1132300##k] x #r1个#k\r\n";
	text += "\t"+感叹号+"#k[道具介绍]:[#r轮回石碑超强属性#k]\r\n";
			
	text += "\t"+正方箭头+"#k[获得]:[#b#v 2022518##k][#b#z 2022518##k] x #r2个#k\r\n";
	text += "\t"+感叹号+"#k[道具介绍]:[#r4转满30级技能#k]\r\n";
			
	text += "\t"+正方箭头+"#k[获得]:[#b#v 2614001##k][#b#z 2614001##k] x #r10个#k\r\n";
	text += "\t"+感叹号+"#k[道具介绍]:[#r双击突破十万伤害上限#k]\r\n";
	
	text += "\t"+正方箭头+"#k[获得]:[#b#v 5680080##k][#b#z 5680080##k] x #r1个#k\r\n";
	text += "\t"+感叹号+"#k[道具介绍]:[#r放背包自动激活#k]\r\n";
	
	text += "\t"+正方箭头+"#k[获得]:[#b#v 5220000##k][#b#z 5220000##k] x #r100个#k\r\n";
	text += "\t"+感叹号+"#k[道具介绍]:[#r自由市场百宝机使用#k]\r\n";
	
	text += "\t"+正方箭头+"#k[获得]:[#b#v 3605006##k][#b#z 3605006##k] x #r800个#k\r\n";
	text += "\t"+感叹号+"#k[道具介绍]:[#r可以在匠人街强化轮回碑石至满级#k]\r\n\r\n";
	
	text += "\t"+正方箭头+"#k[获得]:[#b#v 5010019##k][#b#z 5010019##k] x #r1个#k\r\n";
	text += "\t"+感叹号+"#k[道具介绍]:[#r原价188月卡，现在28送永久#k]\r\n\r\n";
//	text += "" + 分割线() + "\r\n";
	text += "\t\t  #r#e#L1#" + 彩色礼包 + "打开28元首冲礼盒" + 彩色礼包 + "#l\r\n\r\n\r\n";
	
     cm.sendSimple(text);
}

// 处理用户选择
function handleSelection(selection) {
    if (selection == 1) {
        if (cm.getOneTimeLog("首冲礼包领取") >= 1) {
            cm.sendOk("你已经领取过首冲礼包，请不要重复领取！");
			cm.gainItem(2022508, -1);
            cm.dispose();
            return;
        }

        if (cm.getInventory(1).isFull(2) || cm.getInventory(2).isFull(2) || cm.getInventory(3).isFull(2) || cm.getInventory(4).isFull(2) || cm.getInventory(5).isFull(2)) {
            cm.sendOk("请保证背包所有栏位至少保留3个空格！");
            cm.dispose();
            return;
        }

        if (cm.getPlayer().getmoney() >= 28) {
            cm.gainItem(1132300, 1); //轮回石碑
			cm.gainItem(5010019, 1); //月卡
            cm.gainItem(3605006, 800); //女神赐福
			cm.gainItem(2022518, 2); //4转满技能
			cm.gainItem(5680080, 1); //无限弓标弹
			cm.gainItem(2614001, 10); //十万突破
			cm.gainItem(5220000, 100); //百宝卷
            cm.给抵用券(20000); // 抵用券
            cm.给点券(20000); // 点券
            cm.gainItem(3994731, 2); //一亿金币
            cm.getPlayer().setmoney(cm.getPlayer().getmoney() - 28); // -赞助
			cm.getPlayer().setlpjf(cm.getPlayer().getlpjf() + 28); //+累计积分
            cm.gainItem(2022508, -1);
            cm.setOneTimeLog("首冲礼包领取");

            cm.sendOk("首冲礼盒领取成功！");
            cm.喇叭(2, "28元首冲：[" + cm.getName() + "]咬咬牙，跺跺脚，拿下首冲礼盒，准备横扫所有复古BOSS！");
            cm.喇叭(2, "28元首冲：[" + cm.getName() + "]咬咬牙，跺跺脚，拿下首冲礼盒，准备横扫所有复古BOSS！");
            cm.喇叭(2, "28元首冲：[" + cm.getName() + "]咬咬牙，跺跺脚，拿下首冲礼盒，准备横扫所有复古BOSS！");
            cm.getPlayer().dropMessage(5, "赞助点：-28"); // 红字私聊提示
			cm.getPlayer().dropMessage(5, "累计积分：+28");   //红字私聊提示
			Packages.tools.FileoutputUtil.log("log\\玩家相关\\28元首充礼包.log", "[" + cm.getName() + "] 购买了首充礼包，当前拥有  " + cm.getPlayer().getmoney() + "  赞助余额 ");  //  记录日志
            cm.dispose();
        } else {
            cm.sendOk("您的赞助点不足#r28#k点，请充值后再来吧！");
            cm.dispose();
        }
    }
}

function 分割线() {
	var text = "  ";
	var list = [
		"#fEffect/CharacterEff.cmg/1022223/7/0#",
		"#fEffect/CharacterEff.cmg/1022223/8/0#",
		"#fEffect/CharacterEff.cmg/1022223/9/0#",
	];
	for (var i = 0; i < 21; i++) {
		var random = Math.floor(Math.random() * list.length);
		text += list[random];
	}
	text += "  ";
	return text;
}
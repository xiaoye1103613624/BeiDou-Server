//importPackage(java.lang);
//importPackage(Packages.tools);
//importPackage(Packages.client);

var 美化1 = "#fUI/ChatBalloon.img/pet/123/nw#";
var 美化3 = "#fUI/ChatBalloon.img/pet/123/ne#";
var 美化2 = "#fUI/ChatBalloon.img/pet/123/n#";
var 美化4 = "#fUI/ChatBalloon.img/pet/123/sw#";
var 美化5 = "#fUI/ChatBalloon.img/pet/123/se#";
var 美化6 = "#fUI/ChatBalloon.img/pet/123/s#";
var 美化7 = "#fUI/UIWindow.img/156/arrow#";
var aaa = "#fUI/UIWindow.img/Quest/icon9/0#";
var zzz = "#fUI/UIWindow.img/Quest/icon8/0#";
var sss = "#fUI/UIWindow.img/QuestIcon/3/0#";
var 正在进行中 = "#fUI/UIWindow/Quest/Tab/enabled/1#";
var 完成 = "#fUI/UIWindow/Quest/Tab/enabled/2#";
var 正在进行中蓝 = "#fUI/UIWindow/MonsterCarnival/icon1#";
var 完成红 = "#fUI/UIWindow/MonsterCarnival/icon0#";
var acc = "#fEffect/CharacterEff/1112903/0/0#";
var aaa = "#fUI/UIWindow.img/Quest/icon9/0#";
var zzz = "#fUI/UIWindow.img/Quest/icon8/0#";

var 办理赞助点 = 100; // 每次所需赞助点
var 道具1 = 3603006;
var 道具2 = 4310196;
var 道具3 = 4310100;


var status = -1;

function start() {
    if (cm.getInventory(1).isFull(3) || cm.getInventory(2).isFull(3) ||
        cm.getInventory(3).isFull(3) || cm.getInventory(4).isFull(3)) {
        cm.sendOk("#b请保证全体背包 4 个空位，否则无法打开。");
        cm.dispose();
    } else {
        action(1, 0, 0);
    }
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
        return;
    }
    if (mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) status++;
    else status--;

    if (status == 0) {
        var 领过没 = cm.getPlayer().getBossLog("领取每日理财奖励1") > 0 ? "#b已领取今日理财" : "#g领取今日理财";
		var cur = cm.getPlayer().getmoney();
        var 已办理次数 = cm.getPlayer().getOneTimeLogcs("投资卡开通");

        var text = "   " + 美化1 + "" + 美化2 + "" + 美化2 + "" + 美化2 + "" + 美化2 + "#d『投 资 系 统』#k" +
                   美化2 + "" + 美化2 + "" + 美化2 + "" + 美化2 + "" + 美化3 + "#k\r\n";
        text += "          #L1#" + 领过没 + "#l    #L2##r办理投资#l#k\r\n\r\n\r\n";
        if (已办理次数 <= 0) {
		text += "#d         办理消耗#r#e " + 办理赞助点 + " #n#d赞助点  拥有：#b#e" + cur + " #d#n赞助点#k\r\n\r\n";
        text += "    ↓-----------------<开通立即获得>--------------↓\r\n";
        text += "#k                立即获得:#i4001126# #r30000个#k \r\n";
        text += "\r\n    ↓-----------------<每日领取奖励>--------------↓\r\n";
        text += "#k           每日领取:#r 每日100元宝#k\r\n";
        text += "#k           每日领取:#r  1 个 #k#z" + 道具1 + "#\r\n";
        text += "#k           每日领取:#r  1 个 #k#z" + 道具2 + "#\r\n";
		text += "#k           每日领取:#r  1 个 #k#z" + 道具3 + "#\r\n";
        } else{
        text += "#r                  #d已办理次数：#r#e" + 已办理次数 + "#d/#b10 #d#n次#n#k\r\n\r\n";
		text += "#d          办理消耗#r#e " + 办理赞助点 + " #n#d赞助点  拥有：#b#e" + cur + " #d#n赞助点#k\r\n\r\n";
        text += "    ↓-----------------<开通立即获得>--------------↓\r\n";
        text += "#k                立即获得:#i4001126# #r30000个#k \r\n";
        text += "\r\n    ↓-----------------<每日领取奖励>--------------↓\r\n";
        text += "#k           每日领取:#r 每日" + 已办理次数 + "00元宝#k\r\n";
        text += "#k           每日领取:#r  " + 已办理次数 + "个 #k#z" + 道具1 + "#\r\n";
        text += "#k           每日领取:#r  " + 已办理次数 + "个 #k#z" + 道具2 + "#\r\n";
		text += "#k           每日领取:#r  " + 已办理次数 + "个 #k#z" + 道具3 + "#\r\n";
		}
        cm.sendSimple(text);
    } else if (status == 1) {
        if (selection == 1) { // 领取每日
    // 先判断是否今天已领
            if (cm.getPlayer().getBossLog("领取每日理财奖励1") > 0) {
                cm.sendOk("您今天已经领取过投资卡每日奖励了哦~!");
                cm.dispose();
                return;
            }

            // 未领取，开始发奖
	        var 已办理次数 = cm.getPlayer().getOneTimeLogcs("投资卡开通");
            if (已办理次数 <= 0) {
                cm.sendOk("您尚未办理过投资卡。");
                cm.dispose();
                return;
            }
			if (cm.getInventory(1).isFull(2) || cm.getInventory(2).isFull(2) || cm.getInventory(3).isFull(2) || cm.getInventory(4).isFull(2) || cm.getInventory(5).isFull(2)) {
                cm.sendOk("请保证背包所有栏位至少保留3个空格以上！");
                cm.dispose();
                return;
            }
            // 发放奖励（份数 = 已办理次数）
            cm.gainItem(道具1, 1 * 已办理次数);
            cm.gainItem(道具2, 1 * 已办理次数);
            cm.gainItem(道具3, 1 * 已办理次数);
            cm.setmoneyb(+100 * 已办理次数);

            cm.getPlayer().setBossLog("领取每日理财奖励1"); // 标记今日已领
            cm.喇叭(1, "[理财投资] : 玩家 " + cm.getPlayer().getName() + " 领取了 " + 已办理次数 + " 份理财投资奖励！");
            cm.dispose();
        } else if (selection == 2) { // 办理投资卡
            var cur = cm.getPlayer().getmoney();
            var 已办理次数 = cm.getPlayer().getOneTimeLogcs("投资卡开通");

            if (已办理次数 >= 10) {
                cm.sendOk("每人最多办理 10 次~");
                cm.dispose();
                return;
            }
            if (cur < 办理赞助点) {
                cm.sendOk("办理失败，您的赞助点不足（需要 " + 办理赞助点 + " 点，当前 " + cur + " 点）。");
                cm.dispose();
                return;
            }

            // 扣赞助点
            cm.getPlayer().setmoney(cur - 办理赞助点);

            // 发奖励
            cm.gainItem(4001126, 30000);        // 立即材料
            // 永久记录办理次数
            cm.getPlayer().setOneTimeLog("投资卡开通");
            cm.全服漂浮喇叭("〖办理投资卡〗 恭喜 [" + cm.getName() + "] 办理投资卡成功，大家快感谢 Ta~~", 5121004);
            cm.sendOk("办理成功，感谢您对本服的支持！");
            cm.dispose();
        }
    }
}
/* =========================================================
   【加备用群礼包】完整脚本（最终修复版）
   ========================================================= */
var 星星 = "#fEffect/CharacterEff/1114000/2/0#";
var 点券图标 = "#fUI/CashShop/CashItem/0#";
var 密码位数 = 8;
var 礼包密码 = "88888888";

var 奖励道具1 = 4310144; var 奖励道具数量1 = 30000;
var 奖励道具2 = 3994742; var 奖励道具数量2 = 1000;
var 奖励道具3 = 4001245; var 奖励道具数量3 = 10;
var 抵用 = 88888;

var 群加群链接 = "http://qm.qq.com/cgi-bin/qm/qr?_wv=1027&k=L5V5D8MVd0scHv3mjd6e2cJkE_cF5hkx&authKey=mQjlemPpKiFTl2CxeavE3kYMkpSw%2B9dHJJrGCvqt51ORhnW%2F%2BmEaLAwsfQ2MGcqJ&noverify=0&group_code=973060878";

function start() {
    /* 背包空位检测 */
    for (var i = 1; i <= 5; i++) {
        if (cm.getInventory(i).isFull(2)) {
            var 栏位名 = ["装备", "消耗", "设置", "其他", "特殊"][i - 1];
            cm.sendOk("请保证 #b" + 栏位名 + "栏#k 至少有 " + (i == 5 ? 2 : 4) + " 个位置。");
            cm.dispose();
            return;
        }
    }

    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1 || mode == 0) {
        cm.dispose();
        return;
    }

    if (mode == 1) status++;
    else status--;

    /* -----------------------------------------------------
       第 0 步：展示礼包内容 + 两个按钮
       ----------------------------------------------------- */
    if (status == 0) {
        var text = "亲爱的冒险家#r"+cm.getName()+"#k你好, " + cm.开服名称() + " 欢迎您的到来！\r\n";
        text += "本功能的奖励是加入【#r防失联备用群#k】玩家礼包。\r\n\r\n";
        text += "礼包内容如下：\r\n";
        text += "#r#i" + 奖励道具1 + "#*" + 奖励道具数量1 + "  ";
        text += "#i" + 奖励道具2 + "#*" + 奖励道具数量2 + "  ";
        text += "#i" + 奖励道具3 + "#*" + 奖励道具数量3 + "  ";
        text += 点券图标 + "抵用券:" + 抵用 + "\r\n\r\n";
        text += "    #L1##b" + 星星 + "点击加入备用群：973060878 查看礼包码" + 星星 + "#l\r\n\r\n";
        text += "          #L2##r" + 星星 + "点击输入礼包码领取奖励" + 星星 + "#l";
        cm.sendSimple(text);
        return;   // 必须 return，下一轮再处理 selection
    }

    /* -----------------------------------------------------
       第 1 步：处理按钮结果
       ----------------------------------------------------- */
    if (status == 1) {
        if (selection == 1) {           // 加入群
            cm.openWeb(群加群链接);
            cm.dispose();
            return;
        } else if (selection == 2) {    // 输入礼包码
            cm.sendGetText("#d请输入礼包码（#r群公告查看#d）：\r\n");
        } else {                        // 其它意外情况
            cm.dispose();
        }
        return;
    }

    /* -----------------------------------------------------
       第 2 步：校验长度
       ----------------------------------------------------- */
    if (status == 2) {
        var code = cm.getText().trim();
        if (code.getBytes("GBK").length != 密码位数) {
            cm.sendOk("礼包码错误！");
            cm.dispose();
            return;
        }
        cm.sendYesNo("您输入的礼包码是 [#r" + code + "#k]，确定领取吗？");
    }

    /* -----------------------------------------------------
       第 3 步：发奖
       ----------------------------------------------------- */
    if (status == 3) {
        var code = cm.getText().trim();
        if (code != 礼包密码) {
            cm.sendOk("对不起，礼包码错误！请前往群公告查看。");
            cm.dispose();
            return;
        }
		if (cm.getPlayer().getPrizeLog("加备用群礼包") >= 1) { 
			cm.sendOk("该账号下已经领取过该礼包，不能重复领取！");
			cm.dispose();
            cm.dispose();
            return;
        }

        cm.给抵用券(抵用);//抵用券
        cm.gainItem(奖励道具1, 奖励道具数量1);
        cm.gainItem(奖励道具2, 奖励道具数量2);
        cm.gainItem(奖励道具3, 奖励道具数量3);
		cm.getPlayer().setPrizeLog("加备用群礼包"); 
        cm.sendOk("#b领取成功！#k\r\n奖励已发放，请查收背包。");
		cm.喇叭(3, "【备用群礼包】[" + cm.getName() + "]加入备用群，领取超级大礼包！");
        cm.dispose();
    }
}
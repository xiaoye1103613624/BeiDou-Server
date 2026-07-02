var 感叹号 = "#fEffect/UIWindow/Quest/icon0#";
var 圆形 = "#fEffect/UIWindow/Quest/icon3/6#";
var 广播 = "#fUI/CN_Chat/ChattingRoom/BtVolUp/0/normal/0#";
var 皇冠白 = "#fUI/GuildMark/Mark/Etc/00009004/15#";
var 正方箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";
var 奖励 = "#fEffect/UIWindow/Quest/reward#";
var 红蓝点 = "#fEffect/CharacterEff.img/1032054/0/0#";
var 蓝星 = "#fEffect/CharacterEff.img/1052203/1/0#";
var 红星 = "#fEffect/CharacterEff.img/1052203/2/0#";
var 大蓝星 = "#fEffect/CharacterEff.img/1022223/2/0#";
var 大红星 = "#fEffect/CharacterEff.img/1022223/1/0#";
var 蓝点 = "#fEffect/CharacterEff.img/1022223/6/0#";
var 红点 = "#fEffect/CharacterEff.img/1022223/7/0#";
var ca = java.util.Calendar.getInstance();
var year = ca.get(java.util.Calendar.YEAR); //获得年份
var month = ca.get(java.util.Calendar.MONTH) + 1; //获得月份
var day = ca.get(java.util.Calendar.DATE);//获取日
var hour = ca.get(java.util.Calendar.HOUR_OF_DAY); //获得小时
var minute = ca.get(java.util.Calendar.MINUTE);//获得分钟
var second = ca.get(java.util.Calendar.SECOND); //获得秒
var say = 0;
var pt = 0;
var 消耗 = 15000;
var 血量 = 50000000000;
var 队长战力 = 40000;
var 队员战力 = 25000;
var down = 150;
var up = 255;
var say = 0;

var ul_nums = [
    "#fEffect/UIWindow/KeyConfig/key/11#",
    "#fEffect/UIWindow/KeyConfig/key/2#",
    "#fEffect/UIWindow/KeyConfig/key/3#",
    "#fEffect/UIWindow/KeyConfig/key/4#",
    "#fEffect/UIWindow/KeyConfig/key/5#",
    "#fEffect/UIWindow/KeyConfig/key/6#",
    "#fEffect/UIWindow/KeyConfig/key/7#",
    "#fEffect/UIWindow/KeyConfig/key/8#",
    "#fEffect/UIWindow/KeyConfig/key/9#",
    "#fEffect/UIWindow/KeyConfig/key/10#",
];

// 检测玩家当前仙级
function getxmwnjlc(log) {
    return getxmwnjljsc(log);
}

function getxmwnjljsc(jiluid) {
    var xmsjfh = 0;
    zhjsid = cm.getPlayer().getId();
    var conn = cm.getConnection();
    var sql = "SELECT * FROM xmwnjl WHERE characterid = " + zhjsid + " AND bossid = '" + jiluid + "' ;";
    var pstmt = conn.prepareStatement(sql);
    var result = pstmt.executeQuery();		
    if (result.next()) {
        xmsjfh = result.getInt("count");
    } 
    result.close();
    pstmt.close();
    conn.close();
    return xmsjfh;
}

function start() {
    // ? 修复：检查变量是否存在 + 提供默认值
    核心 = cm.itemQuantity(4001126) || 0;  // 如果 itemQuantity 不存在，使用 0
    chr = cm.getPlayer();
    pt = chr.getParty();
    点券 = chr.getCSPoints(1) || 0;
    等级 = chr.getLevel();
    赞助 = cm.getmoneyb() || 0;

    // ? 修复：检查 BossLog 是否存在，并设置默认值（防止null/undefined）
    妖塔 = chr.getBossLog("通天塔层数1") || 0;
    次数 = chr.getBossLog("通天塔次数") || 0;
    战斗力 = chr.getBossLog("战斗力") || 0;
    永久 = chr.getBossLog("通天塔永久记录") || 0;

    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    // 获取当前时间
    var ca = java.util.Calendar.getInstance();
    var hour = ca.get(java.util.Calendar.HOUR_OF_DAY); // 24小时制的小时数

    // 检查是否在20:00至22:00之间
    if (hour < 1 || hour >= 22) {
        cm.sendOk("当前通天塔暂未开放，开启时间为#r每天1:00至22:00#k。\r\n服务器当前时间：#r" + hour +":" + minute + ":" + second + "#n#k");
        cm.dispose();
        return;
    }
    if (mode == -1 || (status == 0 && mode == 0)) {
        cm.dispose();
        return;
    }
    if (mode == 1) status++;
    else status--;

    if (status == 0) {
        var say = "";  // 修复：改用局部变量 var say
		say += "\t#r#e   	     "+ 红星 + ""+ 大红星 + ""+ 红点 + "通 天 塔"+ 红蓝点 + ""+ 蓝点 + ""+ 大蓝星 + ""+ 蓝星 + "#k#n \r\n\r\n";
        say += 广播 + ":#k这里是通天塔,只有达到仙帝境界才可挑战。#k\r\n";
        say += 广播 + ":#k每层都有奖励,每1层都会出现一只BOSS哦。#k\r\n";
        say += 广播 + ":#k全部通关奖励大量#r累积充值#k与#r各种高级材料。#k\r\n";
		say += 广播 + ":#k月卡会员通关53层获得#r双倍#k奖励。#k\r\n";
		say += 广播 + ":#k每日最多通关一次，最多进入三次。#k\r\n";
		say += 广播 + ":#r开放进入时间：1：00-22：00。#k\r\n";
		say += 感叹号 + "[挑战中途如果放弃，再次进入会继续挑战当前层]\r\n";
    //    say += 感叹号 + "[历史最高层数]:[ #b" + _showScore(永久) + "#k ]\r\n";
        say += 感叹号 + "[今日挑战层数]:[ #b" + _showScore(妖塔) + "#k / #b" + _showScore(53) + "#k ]\r\n";
        say += 感叹号 + "[每天进入次数]:[ #r" + _showScore(次数) + "#k / #b" + _showScore(3) + "#k ]\r\n\r\n";
		
        say += "\t\t\t\t #L0#" + 正方箭头 + "[#r开始挑战副本#k]#l\r\n";
        // say += "#k#L1#" + 正方箭头 + "[#b查看最牛排行#k]#l\r\n";

        cm.sendSimple(say);  // sendSimple 可能需要参数调整
    } else if (status == 1) {
        switch (selection) {
            case 0:
                // 检查妖塔是否 >= 53（避免 undefined 导致错误）
                if (妖塔 >= 53) {
                    cm.getPlayer().dropMessage(5, "已到达顶点，您是绝对的巅峰王者！请明日再来！");
                    cm.dispose();
                    return;
                }
                // 检查地图是否有人（修复判断逻辑）
                if (cm.getPlayerCount(253000008) > 0) {
                    cm.sendOk("里面已经有人在挑战...");
                    cm.dispose();
                    return;
                }
				        // 获取玩家当前仙级
				var 当前仙级 = getxmwnjlc("XM飞升系统_仙级");
				// 检查是否达到仙帝境界（仙帝对应的索引是 13）
				if (当前仙级 < 12) {
					cm.sendOk("你尚未达到仙帝境界，无法开启通天塔！");
					cm.dispose();
					return;  // 重要：必须 return，否则继续执行会报错
				}
                // 修复 openNpc（可能需替换为 warp）
                cm.openNpc(9310540, 88888);  
                // 或者改用 Warp：cm.warp(253000008);
                break;
            case 1:
                // 确保 displayBossLogRanks 方法存在
                cm.displayBossLogRanks("通天塔层数1");
                cm.dispose();
                break;
        }
    }
}

function extend(text, num) { //空格
    var curLength = text.toString().length;
    if (curLength < num) {
        for (var i = 0; i < num - curLength; i++) {
            text += " ";
        }
    }
    return text;
}

function _showScore(num, ext) {
    var showTxt = "";
    var tempNums = num.toString().split("");
    for (var i = 0; i < tempNums.length; i++) {
        showTxt += ul_nums[parseInt(tempNums[i])];
    }
    var sss = "";
    for (var i = tempNums.length; i < ext; i++) {
        sss += " ";
    }
    return showTxt + sss;
}
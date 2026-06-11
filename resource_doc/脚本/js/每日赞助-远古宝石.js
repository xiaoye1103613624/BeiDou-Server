var status = 0;
var 粉星 = "#fEffect/CharacterEff/1112926/0/1#";
var ul_cloud = "#fItem/Etc/0403/04031309/info/iconRaw#"; //

var dungeonid = 105040321; // 地下城起始地图ID
var dungeons = 15; // 地下城地图的数量

function start() {
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
        if (mode == 1)
            status++;
        else
            status--;

        if (status == 0) {
            if (cm.getInventory(1).isFull(2) || cm.getInventory(2).isFull(2) || cm.getInventory(3).isFull(2) || cm.getInventory(4).isFull(2) || cm.getInventory(5).isFull(2)) {
                cm.sendOk("请保证背包所有栏位至少保留3个空格！");
                cm.dispose();
                return;
            }
            var textz = _getTitle("每日赞助");

            var 每日赞助 = cm.getBossLog("每日赞助");
            var 可领取次数 = Math.floor(每日赞助 / 50);
            var 已领取次数 = cm.getBossLog("已领取50元礼包次数") || 0;
            var 剩余可领取次数 = 可领取次数 - 已领取次数;
			
			textz += "      #r#e╭～#n" + 粉星 + "" + 粉星 + "#e～～～～～～～～～～～～～～～～╮#n\r\n";
            textz += "	  	 #d您当前今日充值：[ #r" + 每日赞助 + "元#d ] 已领取：[#b" + 已领取次数 + "#d/#r" + 可领取次数 + "次#d]\r\n";
			textz += "	       #r特殊说明：每满50元，可以循环领取一次#d\r\n";
            textz += "     #r#e╰ ～～～～～～～～～～～～～～～～～～～ ╯#n#l\r\n";

            if (cm.getPlayer().getBossLog("每日10礼包") == 0) { 
                textz += "#d#L0##d每日赞助满10元礼包:#l\r\n\r\n#v4322899##r×3#v3605006##r×5 #v4000487#×5 #v3603000#×1#k\r\n\r\n";
            } else {
                textz += " #d#d每日赞助满10元礼包:（#r已领取#d）\r\n\r\n#v4322899#×3#v3605006#×5 #v4000487#×5 #v3603000#×1 #k\r\n\r\n";
            }
            if (cm.getPlayer().getBossLog("每日30礼包") == 0) { 
                textz += "#d#L1##d每日赞助满30元礼包#l:\r\n\r\n#v4322898##r×3 #v3605006##r×15 #v4000487#×15 #v3602000#×1#k\r\n\r\n";
            } else {
                textz += " #d#d每日赞助满30元礼包:（#r已领取#d）\r\n\r\n#v4322898#×3 #v3605006#×15 #v4000487#×15 #v3602000#×1#k\r\n\r\n";
            }
            if (cm.getPlayer().getBossLog("每日50礼包") == 0) { 
                textz += "#d#L2##d每日赞助满50元礼包#l:\r\n\r\n#r#v4322897#×3 #v3605006#×25 #v4000487#×25 #v3603006#×1 #v2550008:#×1#k\r\n\r\n";//#v2290285##r×5 #v2340000#×5 
            } else {
                textz += " #d#d每日赞助满50元礼包:（#r已领取#d）\r\n\r\n#v4322897#×3 #v3605006#×25 #v4000487#×25 #v3603006#×1 #v2550008:#×1#k\r\n\r\n";
            }
        //    if (剩余可领取次数 >= 1) {
			if (剩余可领取次数 >= 1 && cm.getPlayer().getBossLog("每日10礼包") >= 1 && cm.getPlayer().getBossLog("每日30礼包") >= 1 && cm.getPlayer().getBossLog("每日50礼包") >= 1) {
                textz += "#r#L3#" + 粉星 + "一键领取#b"+剩余可领取次数+"次#r10-50元礼包，已领取[ #b" + 已领取次数 + "#d/#r" + 可领取次数 + "次#d ]" + 粉星 + "#l\r\n\r\n";
                textz += "   #d特殊说明：充值金额为50的倍数即可循环领取\r\n";
            }
            textz += "  #r#L4#" + ul_cloud + ul_cloud + ul_cloud + "进入每日充值地图" + ul_cloud + ul_cloud + ul_cloud + "#l\r\n\r\n";
            
            cm.sendSimpleS(textz,2);
        } else if (status == 1) {
            if (selection == 0) {

                if (cm.getPlayer().getBossLog("每日赞助", 1,1) < 10) {
                    cm.sendOk("抱歉，您每日赞助金额不足。.");
                    cm.dispose();
                } else if (cm.getPlayer().getBossLog("每日10礼包") == 0) {
                    cm.gainItem(4322899, 3);
                    cm.gainItem(3605006, 5);
                    cm.gainItem(4000487, 5);
					cm.gainItem(3603000, 1); //金币加成
                    cm.getPlayer().setBossLog("每日10礼包");
                    cm.sendOk("恭喜你，你获得了每日10赞助礼包! .");
                    cm.喇叭(3, "【每日礼包】[" + cm.getName() + "]领取每日10元礼包！");
					Packages.tools.FileoutputUtil.log("log\\玩家相关\\每日充值礼包.log", "[" + cm.getName() + "]领取每日10元礼包");  //  记录日志
                    cm.dispose();
                } else
                    cm.sendOk("抱歉，您尚未积分，或者已经领取了该档礼包~.");
                cm.dispose();

            } else if (selection == 1) {

                if (cm.getPlayer().getBossLog("每日赞助", 1,1) < 30) {
                    cm.sendOk("抱歉，您每日赞助金额不足。.");
                    cm.dispose();
                } else if (cm.getPlayer().getBossLog("每日30礼包") == 0) {
                    cm.gainItem(4322898, 3);
                    cm.gainItem(3605006, 15);
                    cm.gainItem(4000487, 15);
					cm.gainItem(3602000, 1); //经验加成
                    cm.getPlayer().setBossLog("每日30礼包");
                    cm.sendOk("恭喜你，你获得了每日30赞助礼包! .");
                    cm.喇叭(3, "【每日礼包】[" + cm.getName() + "]领取每日30元礼包！");
					Packages.tools.FileoutputUtil.log("log\\玩家相关\\每日充值礼包.log", "[" + cm.getName() + "]领取每日30元礼包");  //  记录日志
                    cm.dispose();
                } else
                    cm.sendOk("抱歉，您尚未积分，或者已经领取了该档礼包~.");
                cm.dispose();

            } else if (selection == 2) {
                if (cm.getPlayer().getBossLog("每日赞助", 1,1) < 50) {
                    cm.sendOk("抱歉，您每日赞助金额不足。.");
                    cm.dispose();
                } else if (cm.getPlayer().getBossLog("每日50礼包") == 0) {
                    cm.gainItem(4322897, 3);
                    cm.gainItem(3605006, 25);
                    cm.gainItem(4000487, 25);
					cm.gainItem(3603006, 1); //暴率加成
					cm.gainItem(2550008, 1); //双倍频道卡24小时
                    cm.getPlayer().setBossLog("每日50礼包");
                    cm.getPlayer().setBossLog("已领取50元礼包次数");
                    cm.sendOk("恭喜你，你获得了每日50赞助礼包! .");
                    cm.喇叭(3, "【每日礼包】[" + cm.getName() + "]领取每日50元礼包！");
					Packages.tools.FileoutputUtil.log("log\\玩家相关\\每日充值礼包.log", "[" + cm.getName() + "]领取每日50元礼包");  //  记录日志
                    cm.dispose();
                } else
                    cm.sendOk("抱歉，您尚未积分，或者已经领取了该档礼包~.");
                cm.dispose();
            
            } else if (selection == 3) {
                var 可领取次数 = Math.floor(cm.getBossLog("每日赞助") / 50);
                var 已领取次数 = cm.getBossLog("已领取50元礼包次数") || 0;
                var 剩余可领取次数 = 可领取次数 - 已领取次数;
                if (剩余可领取次数 > 0) {
                    // 领取10元礼包
                    cm.gainItem(4322899, 3 * 剩余可领取次数);
                    cm.gainItem(3605006, 5 * 剩余可领取次数);
                    cm.gainItem(4000487, 5 * 剩余可领取次数);
					cm.gainItem(3603000, 1 * 剩余可领取次数); //经验加成
                    cm.getPlayer().dropMessage(5, "领取10元礼包 "+剩余可领取次数+"次。");   //红字私聊提示
                    // 领取30元礼包
                    cm.gainItem(4322898, 3 * 剩余可领取次数);
                    cm.gainItem(3605006, 15 * 剩余可领取次数);
                    cm.gainItem(4000487, 15 * 剩余可领取次数);
					cm.gainItem(3602000,  1 * 剩余可领取次数); //经验加成
                    cm.getPlayer().dropMessage(5, "领取30元礼包 "+剩余可领取次数+"次。");   //红字私聊提示
                    // 领取50元礼包
                    cm.gainItem(4322897, 3 * 剩余可领取次数);
                    cm.gainItem(3605006, 25 * 剩余可领取次数);
                    cm.gainItem(4000487, 25 * 剩余可领取次数);
					cm.gainItem(3603006,  1 * 剩余可领取次数); //暴率加成
					cm.gainItem(2550008,  1 * 剩余可领取次数); //双倍频道卡24小时
                    cm.getPlayer().dropMessage(5, "领取50元礼包 "+剩余可领取次数+"次。");   //红字私聊提示
                    cm.sendOk("恭喜你，你领取了"+ 剩余可领取次数 +" 次，所有礼包！");
                    cm.喇叭(3, "【每日礼包】[" + cm.getName() + "]一键领取了10-50元日充礼包  "+剩余可领取次数+" 次！");
					Packages.tools.FileoutputUtil.log("log\\玩家相关\\每日充值礼包.log", "[" + cm.getName() + "]一键领取了10-50元日充礼包 "+剩余可领取次数+"次 ");  //  记录日志
                    cm.getPlayer().setBossLog("每日10礼包", 0,剩余可领取次数);
                    cm.getPlayer().setBossLog("每日30礼包", 0,剩余可领取次数);
                    cm.getPlayer().setBossLog("每日50礼包", 0,剩余可领取次数);
                    cm.getPlayer().setBossLog("已领取50元礼包次数", 0,剩余可领取次数);
                    cm.dispose();
                } else {
                    cm.sendOk("您已经领取了所有可领取的礼包。当前还可以领取 "+剩余可领取次数+"次！");
                }
                cm.dispose();
            } else if (selection == 4) {
                // 新增：只要持有 cardItemId，就跳过 50 元限制 3605021   3700069
				var hasCard = cm.haveItem(3605021, 1);
				if (!hasCard && cm.getBossLog("每日赞助") < 50) {
					cm.sendOk("抱歉，您的每日充值金额不足 50 元，无法进入地图。");
					cm.dispose();
					return;
				}
				if (!cm.haveItem(5010019)) {
					cm.sendOk("至少需要先拥有#i5010019#");
					cm.dispose();
					return;
				}
				if (cm.getPlayer().getParty() != null) {
					cm.sendOk("只能一个人进入，请先退出组队");
					cm.dispose();
                } else {
					enterDungeon();
                }
            }
        }
    }
}

function enterDungeon() {
    for (var i = 0; i < dungeons; i++) {
        if (cm.getPlayerCount(dungeonid + i) == 0) {          // 找到空图
            /* ① 持有 3605021 直接进，不计时 */
            if (cm.haveItem(3605021, 1)) {
                cm.warp(dungeonid + i, 0);
                cm.dispose();
                return;
            }

            /* ② 无道具，走充值判断 */
            if (cm.getPlayer().getlpjf() <= 1) {              // 累计赞助不足
                cm.sendOk("你累计必须大于1，才可以进入该地图。");
                cm.dispose();
                return;
            }
            if (!cm.haveItem(5010019)) {                      // 会员
                cm.sendOk("你没有持有#i5010019#，无法进入该地图。");
                cm.dispose();
                return;
            }

            /* ③ 充值达标，给计时 */
            cm.warp(dungeonid + i, 0);
            var now = new Date();
            var end = new Date(now.getFullYear(), now.getMonth(), now.getDate() + 1, 0, 0, 0);
            var timeLimit = Math.floor((end - now) / 1000);
            cm.getPlayer().startMapTimeLimitTask(timeLimit,
            cm.getChannelServer().getMapFactory().getMap(910000000));
            cm.dispose();
            return;
        }
    }
    cm.sendOk("目前所有迷你地下城都有人，请稍后再尝试。");
    cm.dispose();
}

function _getTitle(t) {
    return " " + ul_cloud + ul_cloud + ul_cloud + ul_cloud + "#r#e『" + t + "』#k#n" + ul_cloud + ul_cloud + ul_cloud + ul_cloud + "\r\n\r\n";
}
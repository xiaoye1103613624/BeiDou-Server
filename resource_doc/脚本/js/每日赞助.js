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
            // 背包所有栏位至少保留3个空格
            if (cm.getInventory(1).isFull(2) || cm.getInventory(2).isFull(2) || cm.getInventory(3).isFull(2) || cm.getInventory(4).isFull(2) || cm.getInventory(5).isFull(2)) {
                cm.sendOk("请保证背包所有栏位至少保留3个空格！");
                cm.dispose();
                return;
            }
            var textz = _getTitle("每日赞助");

            var 每日赞助 = cm.getBossLog("每日赞助");
            var 可领取次数 = Math.floor(每日赞助 / 100);
            var 已领取次数 = cm.getBossLog("已领取100元礼包次数") || 0;
            var 剩余可领取次数 = 可领取次数 - 已领取次数;
			
			textz += "      #r#e╭～#n" + 粉星 + "" + 粉星 + "#e～～～～～～～～～～～～～～～～╮#n\r\n";
            textz += "	  	 #d您当前今日充值：[ #r" + 每日赞助 + "元#d ] 已领取：[#b" + 已领取次数 + "#d/#r" + 可领取次数 + "次#d]\r\n";
            textz += "	       #r特殊说明：每满100元，可以循环领取一次#d\r\n";
            textz += "     #r#e╰ ～～～～～～～～～～～～～～～～～～～ ╯#n#l\r\n";

            // 10元礼包
            if (cm.getPlayer().getBossLog("每日10礼包") == 0) { 
                textz += "#d#L0##d每日赞助满10元礼包:#l\r\n\r\n#v4322899##r×1#v3605006##r×5 #v4000487#×5 #v3603000#×1#k\r\n\r\n";
            } else {
                textz += " #d#d每日赞助满10元礼包:（#r已领取#d）\r\n\r\n#v4322899#×1#v3605006#×5 #v4000487#×5 #v3603000#×1 #k\r\n\r\n";
            }
            // 30元礼包
            if (cm.getPlayer().getBossLog("每日30礼包") == 0) { 
                textz += "#d#L1##d每日赞助满30元礼包#l:\r\n\r\n#v4322899##r×3 #v3605006##r×15 #v4000487#×15 #v3602000#×1#k\r\n\r\n";
            } else {
                textz += " #d#d每日赞助满30元礼包:（#r已领取#d）\r\n\r\n#v4322899#×3 #v3605006#×15 #v4000487#×15 #v3602000#×1 #k\r\n\r\n";
            }
            // 50元礼包
            if (cm.getPlayer().getBossLog("每日50礼包") == 0) { 
                textz += "#d#L2##d每日赞助满50元礼包#l:\r\n\r\n#r#v4322899#×5 #v3605006#×25 #v4000487#×25 #v3603006#×1 #k\r\n\r\n";
            } else {
                textz += " #d#d每日赞助满50元礼包:（#r已领取#d）\r\n\r\n#v4322899#×5 #v3605006#×25 #v4000487#×25 #v3603006#×1 #k\r\n\r\n";
            }
			// 100元礼包
			if (cm.getPlayer().getBossLog("每日100礼包") == 0) { 
                textz += "#d#L3##d每日赞助满100元礼包#l:\r\n\r\n#r#v4322899#×5 #v3605006#×25 #v4000487#×25  #v2550008#×1 #v3605021#×1#k\r\n\r\n";
            } else {
                textz += " #d#d每日赞助满100元礼包:（#r已领取#d）\r\n\r\n#v4322899#×5 #v3605006#×25 #v4000487#×25  #v2550008#×1#k\r\n\r\n";
            }
			// 一键领取（需满足所有单礼包已领取+剩余次数≥1）
			if (剩余可领取次数 >= 1 && cm.getPlayer().getBossLog("每日10礼包") >= 1 && cm.getPlayer().getBossLog("每日30礼包") >= 1 && cm.getPlayer().getBossLog("每日50礼包") >= 1 && cm.getPlayer().getBossLog("每日100礼包") >= 1) {
                textz += "#r#L4#" + 粉星 + "一键领取#b"+剩余可领取次数+"次#r10-100元礼包，已领取[ #b" + 已领取次数 + "#d/#r" + 可领取次数 + "次#d ]" + 粉星 + "#l\r\n\r\n";
                textz += "   #d特殊说明：充值金额为100的倍数即可循环领取\r\n";
            }
            // 进入每日充值地图（修改为L5，避免和一键领取冲突）
            textz += "  #r#L5#" + ul_cloud + ul_cloud + ul_cloud + "进入每日充值地图" + ul_cloud + ul_cloud + ul_cloud + "#l\r\n\r\n";
            
            cm.sendSimpleS(textz,2);
        } else if (status == 1) {
            // 单个领取10元礼包（L0）
            if (selection == 0) {
                if (cm.getPlayer().getBossLog("每日赞助", 1,1) < 10) {
                    cm.sendOk("抱歉，您每日赞助金额不足。");
                    cm.dispose();
                } else if (cm.getPlayer().getBossLog("每日10礼包") == 0) {
                    cm.gainItem(4322899, 1);
                    cm.gainItem(3605006, 5);
                    cm.gainItem(4000487, 5);
					cm.gainItem(3603000, 1); //金币加成
                    cm.getPlayer().setBossLog("每日10礼包", 1); // 标记为已领取（值设为1）
                    cm.sendOk("恭喜你，你获得了每日10赞助礼包!");
                    cm.喇叭(3, "【每日礼包】[" + cm.getName() + "]领取每日10元礼包！");
					Packages.tools.FileoutputUtil.log("log\\玩家相关\\每日充值礼包.log", "[" + cm.getName() + "]领取每日10元礼包");
                    cm.dispose();
                } else {
                    cm.sendOk("抱歉，您已经领取了该档礼包~");
                    cm.dispose();
                }
            } 
            // 单个领取30元礼包（L1）
            else if (selection == 1) {
                if (cm.getPlayer().getBossLog("每日赞助", 1,1) < 30) {
                    cm.sendOk("抱歉，您每日赞助金额不足。");
                    cm.dispose();
                } else if (cm.getPlayer().getBossLog("每日30礼包") == 0) {
                    cm.gainItem(4322899, 3);
                    cm.gainItem(3605006, 15);
                    cm.gainItem(4000487, 15);
					cm.gainItem(3602000, 1); //经验加成
                    cm.getPlayer().setBossLog("每日30礼包", 1); // 标记为已领取
                    cm.sendOk("恭喜你，你获得了每日30赞助礼包!");
                    cm.喇叭(3, "【每日礼包】[" + cm.getName() + "]领取每日30元礼包！");
					Packages.tools.FileoutputUtil.log("log\\玩家相关\\每日充值礼包.log", "[" + cm.getName() + "]领取每日30元礼包");
                    cm.dispose();
                } else {
                    cm.sendOk("抱歉，您已经领取了该档礼包~");
                    cm.dispose();
                }
            } 
            // 单个领取50元礼包（L2）- 移除已领取次数累加
            else if (selection == 2) {
                if (cm.getPlayer().getBossLog("每日赞助", 1,1) < 50) {
                    cm.sendOk("抱歉，您每日赞助金额不足。");
                    cm.dispose();
                } else if (cm.getPlayer().getBossLog("每日50礼包") == 0) {
                    cm.gainItem(4322899, 5);
                    cm.gainItem(3605006, 25);
                    cm.gainItem(4000487, 25);
					cm.gainItem(3603006, 1); //暴率加成
                    cm.getPlayer().setBossLog("每日50礼包", 1); // 仅标记已领取，不更新100元礼包次数
                    cm.sendOk("恭喜你，你获得了每日50赞助礼包!");
                    cm.喇叭(3, "【每日礼包】[" + cm.getName() + "]领取每日50元礼包！");
					Packages.tools.FileoutputUtil.log("log\\玩家相关\\每日充值礼包.log", "[" + cm.getName() + "]领取每日50元礼包");
                    cm.dispose();
                } else {
                    cm.sendOk("抱歉，您已经领取了该档礼包~");
                    cm.dispose();
                }
			} 
            // 单个领取100元礼包（L3）- 保留次数累加（单领100算1次）
			else if (selection == 3) {
                if (cm.getPlayer().getBossLog("每日赞助", 1,1) < 100) {
                    cm.sendOk("抱歉，您每日赞助金额不足。");
                    cm.dispose();
                } else if (cm.getPlayer().getBossLog("每日100礼包") == 0) {
                    cm.gainItem(4322899, 5);
                    cm.gainItem(3605006, 25);
                    cm.gainItem(4000487, 25);
					cm.gainItem(3605021, 1);
					cm.gainItem(2550008, 1); //双倍频道卡24小时
                    cm.getPlayer().setBossLog("每日100礼包", 1); // 标记为已领取
                    // 仅单领100元礼包时，更新已领取100元礼包次数（核心修正）
                    var 已领取次数 = cm.getBossLog("已领取100元礼包次数") || 0;
                    cm.getPlayer().setBossLog("已领取100元礼包次数", 0, 已领取次数 + 1);
                    cm.sendOk("恭喜你，你获得了每日100赞助礼包!");
                    cm.喇叭(3, "【每日礼包】[" + cm.getName() + "]领取每日100元礼包！");
					Packages.tools.FileoutputUtil.log("log\\玩家相关\\每日充值礼包.log", "[" + cm.getName() + "]领取每日100元礼包");
                    cm.dispose();
                } else {
                    cm.sendOk("抱歉，您已经领取了该档礼包~");
                    cm.dispose();
                }
            } 
            // 一键领取剩余次数（L4）
            else if (selection == 4) {
                var 每日赞助 = cm.getBossLog("每日赞助");
                var 可领取次数 = Math.floor(每日赞助 / 100);
                var 已领取次数 = cm.getBossLog("已领取100元礼包次数") || 0;
                var 剩余可领取次数 = 可领取次数 - 已领取次数;
                
                // 双重校验：剩余次数>0 + 所有单礼包已领取
                if (剩余可领取次数 > 0 && cm.getPlayer().getBossLog("每日10礼包") >=1 && cm.getPlayer().getBossLog("每日30礼包")>=1 && cm.getPlayer().getBossLog("每日50礼包")>=1 && cm.getPlayer().getBossLog("每日100礼包")>=1) {
                    // 领取10元礼包*剩余次数
                    cm.gainItem(4322899, 1 * 剩余可领取次数);
                    cm.gainItem(3605006, 5 * 剩余可领取次数);
                    cm.gainItem(4000487, 5 * 剩余可领取次数);
					cm.gainItem(3603000, 1 * 剩余可领取次数);
                    cm.getPlayer().dropMessage(5, "领取10元礼包 "+剩余可领取次数+"次。");
                    
                    // 领取30元礼包*剩余次数
                    cm.gainItem(4322899, 3 * 剩余可领取次数);
                    cm.gainItem(3605006, 15 * 剩余可领取次数);
                    cm.gainItem(4000487, 15 * 剩余可领取次数);
					cm.gainItem(3602000, 1 * 剩余可领取次数);
                    cm.getPlayer().dropMessage(5, "领取30元礼包 "+剩余可领取次数+"次。");
                    
                    // 领取50元礼包*剩余次数
                    cm.gainItem(4322899, 5 * 剩余可领取次数);
                    cm.gainItem(3605006, 25 * 剩余可领取次数);
                    cm.gainItem(4000487, 25 * 剩余可领取次数);
					cm.gainItem(3603006, 1 * 剩余可领取次数);
                    cm.getPlayer().dropMessage(5, "领取50元礼包 "+剩余可领取次数+"次。");
					
					// 领取100元礼包*剩余次数
                    cm.gainItem(4322899, 5 * 剩余可领取次数);
                    cm.gainItem(3605006, 25 * 剩余可领取次数);
                    cm.gainItem(4000487, 25 * 剩余可领取次数);
					cm.gainItem(3605021, 1 * 剩余可领取次数);
					cm.gainItem(2550008, 1 * 剩余可领取次数);
                    cm.getPlayer().dropMessage(5, "领取100元礼包 "+剩余可领取次数+"次。");

                    // 提示+喇叭+日志
                    cm.sendOk("恭喜你，成功领取"+ 剩余可领取次数 +"次10-100元所有礼包！");
                    cm.喇叭(3, "【每日礼包】[" + cm.getName() + "]一键领取了10-100元日充礼包  "+剩余可领取次数+" 次！");
					Packages.tools.FileoutputUtil.log("log\\玩家相关\\每日充值礼包.log", "[" + cm.getName() + "]一键领取了10-100元日充礼包 "+剩余可领取次数+"次 ");
                    
                    // 累加剩余次数到已领取次数
                    cm.getPlayer().setBossLog("已领取100元礼包次数", 0, 剩余可领取次数);
                    cm.dispose();
                } else {
                    // 精准提示：区分“无剩余次数”和“未领完单礼包”
                    if(剩余可领取次数 <=0){
                        cm.sendOk("您已领取所有可领取礼包，当前剩余可领取次数："+剩余可领取次数+"次！");
                    }else{
                        cm.sendOk("请先领取完10/30/50/100元单个礼包，再进行一键领取！");
                    }
                    cm.dispose();
                }
            } 
            // 进入每日充值地图（L5）
            else if (selection == 5) {
                // 持有3605021跳过100元充值限制
				var hasCard = cm.haveItem(3605021, 1);
				if (!hasCard && cm.getBossLog("每日赞助") < 100) {
					cm.sendOk("抱歉，您的每日充值金额不足100元且未持有#i3605021#，无法进入地图。");
					cm.dispose();
					return;
				}
				// 校验必备道具5010019
				if (!cm.haveItem(5010019)) {
					cm.sendOk("至少需要先拥有#i5010019#，才能进入地图！");
					cm.dispose();
					return;
				}
				// 校验组队状态
				if (cm.getPlayer().getParty() != null) {
					cm.sendOk("只能单人进入，请先退出组队！");
					cm.dispose();
                } else {
					// 扣除道具（仅扣一次）
					if(hasCard) cm.gainItem(3605021, -1);
					// 进入地下城
					enterDungeon();
					// 启动5分钟地图计时（返回主城910000000）
					cm.getPlayer().startMapTimeLimitTask(300, cm.getChannelServer().getMapFactory().getMap(910000000));
                }
            }
        }
    }
}

function enterDungeon() {
    for (var i = 0; i < dungeons; i++) {
        if (cm.getPlayerCount(dungeonid + i) == 0) {          // 找到空地图
            cm.warp(dungeonid + i, 0);
            cm.dispose();
            return;
        }
    }
    // 所有地图满员提示
    cm.sendOk("目前所有迷你地下城都有人，请稍后再尝试。");
    cm.dispose();
}

// 标题格式化函数
function _getTitle(t) {
    return " " + ul_cloud + ul_cloud + ul_cloud + ul_cloud + "#r#e『" + t + "』#k#n" + ul_cloud + ul_cloud + ul_cloud + ul_cloud + "\r\n\r\n";
}
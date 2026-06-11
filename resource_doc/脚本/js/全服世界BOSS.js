importClass(Packages.server.events.WorldBoss);
importClass(Packages.server.MapleItemInformationProvider);
importClass(Packages.handling.channel.ChannelServer);

var 黄条上 = "#fUI/ChatBalloon.img/pet/25/head#";
var 黄条下 = "#fUI/ChatBalloon.img/pet/25/s#";
var 黄条下左 = "#fUI/ChatBalloon.img/pet/25/sw#";
var 黄条下右 = "#fUI/ChatBalloon.img/pet/25/se#";
var 黄条左 = "#fUI/ChatBalloon.img/pet/25/nw#";
var 黄条右 = "#fUI/ChatBalloon.img/pet/25/ne#";
var 五子棋 = "#fUI/ChatBalloon.img/miniroom/Omok#";
var 斜金币 = "#fUI/ChatBalloon.img/miniroom/PersonalShop#";
var 熊猫 = "#fUI/ChatBalloon.img/pet/1/nw#";
var 大箭头 ="#fUI/Basic/BtHide3/mouseOver/0#";
var 毛球 = "#fUI/ChatBalloon.img/pet/12/nw#";
var 蓝色箭头 = "#fUI/UIWindow/Quest/icon2/7#";
var 红色箭头 = "#fUI/UIWindow/Quest/icon6/7#";
var 金冠 = "#fUI/UIWindow.img/UserInfo/bossPetCrown#";
var 红蓝点 = "#fEffect/CharacterEff.img/1032054/0/0#";
var 蓝加 = "#fUI/Basic.img/BtMax/mouseOver/0#";
var 蓝星 = "#fEffect/CharacterEff.img/1052203/1/0#";
var 红星 = "#fEffect/CharacterEff.img/1052203/2/0#";
var 大蓝星 = "#fEffect/CharacterEff.img/1022223/2/0#";
var 提示 = "#fUI/CN_Chat/ChattingRoom/BtVolUp/0/normal/0#";
var 大红星 = "#fEffect/CharacterEff.img/1022223/1/0#";
var 蓝点 = "#fEffect/CharacterEff.img/1022223/6/0#";
var 红点 = "#fEffect/CharacterEff.img/1022223/7/0#";
var 窗口名称="世界BOSS召唤系统";

/* ↓↓↓↓↓↓↓↓↓↓↓↓↓↓需要设置的地方↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓*/
var BOSS列表 = new Array(
    Array(9410066, 20000000, 4031456, 1000000), //怪物ID，血量，需要的召唤道具，道具数量
    Array(9410066, 20000000, 4031456, 2000000),
    Array(9410066, 30000000, 4031456, 3000000),
    Array(9410066, 30000000, 4031456, 4000000),
	Array(9410066, 40000000, 4031456, 6000000)
);
                 
var 召唤地图ID = 910000251;
var 坐标x = 5;
var 坐标y = 35;

/* ↑↑↑↑↑↑↑↑↑↑↑↑需要设置的地方↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑*/

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            cm.sendOk("如果收集到了足够的材料再来找我吧。");
            cm.dispose();
            return;
        }
        status--;
    }
    
    // 每次获取最新状态
    var 当前阶段 = WorldBoss.getNowStage();
    var 最大阶段 = WorldBoss.getMaxStage();
    var 当前道具ID = WorldBoss.getItemId();
    var 当前道具数量 = WorldBoss.getItemQuantity();
    var 需要道具数量 = WorldBoss.getItemQuantityNeed();
    var 已召唤 = WorldBoss.isSpawned();
    var 已击杀 = WorldBoss.isKilled();
    var BOSS = WorldBoss.getNowBoss();
    
    // 新增：检查BOSS是否应该死亡但未切换阶段
    if(BOSS != null && WorldBoss.getNowBoss().getMobHp() <= 0 && !已击杀) {
        WorldBoss.recordKill(cm.getPlayer().getId()); // 使用玩家的ID作为击杀者
        已击杀 = WorldBoss.isKilled(); // 立即更新状态
        // 广播消息
        var nextStage = WorldBoss.getNowStage();
        if(nextStage <= BOSS列表.length) {
            var nextBossName = BOSS列表[nextStage-1][1]; // 直接使用数组中的名称
            cm.喇叭(2, "【世界BOSS】" + BOSS.getMonster().getStats().getName() + "已被击败！");
            cm.喇叭(2, "【世界BOSS】召唤下一个BOSS！");
        } else {
           cm.喇叭(2, "【世界BOSS】#r" + BOSS.getMonster().getStats().getName() + "#k已被击败！");
            cm.喇叭(2, "【世界BOSS】所有BOSS已被击败，活动结束！");
        }
        
        // 重置当前状态
        已击杀 = WorldBoss.isKilled();
    }
    
    if (status == 0) {
        var text = "";
        if(最大阶段 == 0){
            text += "抱歉，世界BOSS尚未开放，请耐心等候。\r\n";
			            if(cm.getPlayer().getGMLevel() >= 6) {
                text += "\r\n#r--------------------GM选项--------------------#k";
                text += "\r\n#L3##b重置世界BOSS进度#k#l";
            }
        } else if(当前阶段 > BOSS列表.length) {
            text +="           #e世界BOSS活动已结束#n\r\n\r\n";
            text += "所有阶段BOSS都已被击败！\r\n\r\n";
            text += "感谢各位冒险家的参与！\r\n\r\n";
            
            if(cm.getPlayer().getGMLevel() >= 6) {
                text += "\r\n#r--------------------GM选项--------------------#k";
                text += "\r\n#L3##b重置世界BOSS进度#k#l";
            }
        } else {
         //   text += "          #e世界BOSS召唤系统#n\r\n\r\n";
			text = "\t#r#e   	     "+ 红星 + ""+ 大红星 + ""+ 红点 + "" + cm.开服名称() + ""+ 红蓝点 + ""+ 蓝点 + ""+ 大蓝星 + ""+ 蓝星 + "#k \r\n";
			text += ""+ 黄条左 + ""+ 黄条上 + ""+ 黄条上 + ""+ 黄条上 + ""+ 黄条上 + ""+ 黄条上 + ""+ 金冠 + "#b#e#r"+窗口名称+"#b#n"+ 金冠 + ""+ 黄条上 + ""+ 黄条上 + ""+ 黄条上 + ""+ 黄条上 + ""+ 黄条上 + ""+ 黄条上 + ""+ 黄条右 + "#k  \r\n";
            text += ""+ 提示 + " 全服玩家可以一起收集材料来召唤强大的世界BOSS！\r\n\r\n";
            text += ""+ 提示 + " #r所有参与击败BOSS的玩家都能获得奖励。#k\r\n\r\n";
            
            text += ""+ 提示 + " #d当前阶段#k: #r" + 当前阶段 + "#k/#b" + BOSS列表.length + "#k\r\n";
            text += ""+ 提示 + " #d当前BOSS#k: ";
            if(BOSS != null){
                text += "#r" + BOSS.getMonster().getStats().getName() + "#k\r\n";
            } else {
                text += "#r" + MapleItemInformationProvider.getInstance().getMobName(BOSS列表[当前阶段-1][0]) + "#k(未召唤)\r\n";
            }
            
            text += ""+ 提示 + " #d当前需要#k: #r" + 当前道具数量 + "#k/#b" + 需要道具数量 + "#k\r\n";
			xmjcjd = parseInt(100 / 需要道具数量 * 当前道具数量);
			text += ""+ 提示 + " #d当前进度#k: #B" + xmjcjd + "[%]##n#r[" + xmjcjd + "]%#k\r\n";
			
		//	selText += "#d当前收集进度：【#r" + collectedCount + "/" + 收集奖励列表.length + "#d】#B" + 当前道具数量 + "[%]##n#b[" + 需要道具数量 + "]%\r\n"
            
            text += ""+ 提示 + " #d召唤状态#k: ";
            if(已召唤 && !已击杀) {
                var maxHp = WorldBoss.getMaxHp();
                var hp = WorldBoss.getNowBoss() != null ? WorldBoss.getNowBoss().getMobHp() : 0;
                text += "#g已召唤#k (血量: #r" + hp + "#k/#b" + maxHp + "#k)\r\n";
            } else if(已击杀) {
                text += "#r已击杀#k\r\n";
            } else {
                text += "未召唤\r\n";
            }
            
            if(!已召唤 && 当前阶段 <= BOSS列表.length && !已击杀) {
                text += "\r\n\t\t\t#L1#"+ 蓝加 + "#b贡献材料召唤BOSS"+ 蓝加 + "#k#l\r\n\r\n";
                //text += "\r\n#L2##b查询当前进度#k#l\r\n";
            } else if(已召唤 && !已击杀) {
            //    text += "\r\n#L2##b前往挑战BOSS#k#l\r\n";
				text += "\t#r#e   	   #L2#"+ 蓝色箭头 + ""+ 红色箭头 + "#b前往挑战BOSS#k#l \r\n";
            }
            text += "\r\n"+ 黄条下左 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下右 + "#k  ";
            if(cm.getPlayer().getGMLevel() >= 6) {
                text += "\r\n#r--------------------GM选项--------------------#k";
                text += "\r\n#L3##b重置世界BOSS进度#k#l";
            }
        }
        cm.sendSimple(text);
    } else if (status == 1) {
        if(selection == 1) {
            // 玩家选择贡献材料
            if(已召唤 || 已击杀 || 当前阶段 > BOSS列表.length) {
                cm.sendOk(已召唤 ? "BOSS已经被召唤了，无需再贡献材料。" : 已击杀 ? "BOSS已被击败，请等待下一阶段开启。" : "活动已结束，无需贡献材料。");
                cm.dispose();
                return;
            }
            
            var 剩余数量 = 需要道具数量 - 当前道具数量; // 当前还需要的道具数量
            var 玩家拥有数量 = cm.itemQuantity(当前道具ID); // 玩家拥有的道具数量
            var 最大限制数量 = 30000; // 设置的最大限制数量

            // 计算玩家可以输入的最大值，取玩家拥有数量、剩余数量和最大限制数量中的最小值
            var 可输入最大值 = Math.min(玩家拥有数量, 剩余数量, 玩家拥有数量);

            var text = "当前还需要#r" + 剩余数量 + "#k个#i" + 当前道具ID + "#。\n";
            text += "你拥有#b" + 玩家拥有数量 + "#k个#i" + 当前道具ID + "#。\r\n";
			text += "每贡献 2 个，奖励 1 抵用券。\r\n";
            text += "请输入你想贡献的数量(1-" + 可输入最大值 + "):";

            // 设置默认值为可输入的最大值，确保最大值不超过10000
            cm.sendGetNumber(text, 可输入最大值, 1, 可输入最大值);
            choose = 1;
            
        } else if (selection == 2) {
			// 获取当前服务器时间
			var currentTime = new Date();
			var currentHour = currentTime.getHours();

			// 检查当前时间是否在晚上18:00到22:00之间
			var isWithinTimeRange = (currentHour >= 18 && currentHour < 22);
            // 查看状态或前往挑战
            if(已召唤 && !已击杀) {
                if (isWithinTimeRange) {
                // 如果当前时间在允许的范围内，提示玩家是否继续
                cm.sendYesNo("你将前往挑战世界BOSS，是否继续？");
                choose = 2;
				} else {
                // 如果当前时间不在允许的范围内，提示玩家无法进入
                cm.sendOk("抱歉，世界BOSS挑战仅在晚上18:00到22:00之间开放进入。");
                cm.dispose();
					}
				} else {
                var text = "当前进度: #b" + 当前道具数量 + "#k/#r" + 需要道具数量 + "#k\n";
                if(已击杀) {
                    text += "本阶段BOSS已被击败\n";
                    if(当前阶段 < BOSS列表.length) {
                        text += "下一阶段需要#i" + BOSS列表[当前阶段][2] + "# x #r" + BOSS列表[当前阶段][3] + "#k";
                    }
                } else {
                    text += "还需要#r" + (需要道具数量 - 当前道具数量) + "#k个#i" + 当前道具ID + "#\n";
                    text += "收集齐后将自动召唤BOSS";
                }
                cm.sendOk(text);
                cm.dispose();
            }
            
        } else if (selection == 3) {
            // GM重置选项
            cm.sendYesNo("你确定要重置世界BOSS进度吗？这将清空当前所有进度。");
            choose = 3;
        }
    } else if (status == 2) {
        switch(choose) {
            case 1:
                // 玩家提交材料
                var 贡献数量 = selection;
                cm.gainItem(当前道具ID, -贡献数量);
				
				var 抵用券数量 = Math.floor(贡献数量 / 2);
				if (抵用券数量 > 0) {
					cm.给抵用券(抵用券数量);
				}                
				
                if(!WorldBoss.addItem(贡献数量)) {
                    cm.sendOk("提交材料失败，请联系管理员。");
                    cm.dispose();
                    return;
                }
                
                // 检查是否达到召唤条件
                if(WorldBoss.getItemQuantity() >= WorldBoss.getItemQuantityNeed()) {
                    var cs = ChannelServer.getInstance(1);
                    var map = cs.getMapFactory().getMap(召唤地图ID);
                    map.resetFully();
                    
                    // 确保使用正确的BOSS数据
                    var currentBossData = BOSS列表[当前阶段-1];
                    WorldBoss.addBoss(currentBossData[0], currentBossData[1], currentBossData[2], currentBossData[3]);
                    
                    if(WorldBoss.spawn(map, 坐标x, 坐标y)) {
                        var msg = "【世界BOSS】" + cm.getChar().getName() + "贡献了" + 贡献数量 + "个枫叶，BOSS已被召唤！";
                        cm.喇叭(2, msg);
                        cm.喇叭(2, "【世界BOSS】" + BOSS.getMonster().getStats().getName() + "已在活动地图出现！");
                        cm.喇叭(2, "【世界BOSS】请各位冒险家前往1线参与挑战！");
						cm.全服漂浮喇叭("【世界BOSS】已在活动地图出现！请各位冒险家前往1线参与挑战！", 5121000); // 发送全服喇叭，包含奖励详情
                    } else {
                        cm.sendOk("召唤BOSS失败，请联系管理员。");
                        cm.dispose();
                        return;
                    }
                } else {
                    cm.sendOk("感谢你的贡献！你贡献了#r" + 贡献数量 + "#k个#i" + 当前道具ID + "#。");
					cm.喇叭(1, "玩家:[" + cm.getPlayer().getName() + "] 贡献了" + 贡献数量 + "个材料！距离召唤世界BOSS还需要" + (需要道具数量 - 当前道具数量 - 贡献数量) + "个！");
                }
                break;
                
            case 2:
                // 玩家前往挑战
                if(已击杀 || 当前阶段 > BOSS列表.length) {
                    cm.sendOk(已击杀 ? "本阶段BOSS已被击败，请等待下一阶段开启。" : "活动已结束，没有BOSS可挑战。");
                    cm.dispose();
                    return;
                }
                
                if(!已召唤) {
                    cm.sendOk("BOSS尚未被召唤。");
                    cm.dispose();
                    return;
                }
                
                var cs = ChannelServer.getInstance(1);
                var map = cs.getMapFactory().getMap(召唤地图ID);
                
                // 如果BOSS不存在则重新召唤
                if(BOSS == null || !map.getAllMonstersThreadsafe().contains(BOSS.getMonster())) {
                    WorldBoss.spawn(map, 坐标x, 坐标y);
                }
                
                if(cm.getPlayer().getClient().getChannel() != 1) {
                 //   cm.changeChannel(1);
					cm.sendOk("世界BOSS在1频道开放。");
					cm.dispose();
                } else {
					// 玩家已经在1频道，直接传送玩家到BOSS地图
					cm.warp(召唤地图ID);
					cm.dispose();
				}
				break;
				
            case 3:
                // GM重置
                WorldBoss.reset();
                var cs = ChannelServer.getInstance(1);
                var map = cs.getMapFactory().getMap(召唤地图ID);
                map.resetFully();
                
                // 重新注册所有BOSS
                for(var i = 0; i < BOSS列表.length; i++) {
                    WorldBoss.addBoss(BOSS列表[i][0], BOSS列表[i][1], BOSS列表[i][2], BOSS列表[i][3]);
                }
                WorldBoss.saveToDB();
                
                cm.喇叭(2, "【世界BOSS】已重置，请前往NPC处查看新阶段内容。");
                cm.sendOk("已成功重置世界BOSS进度。");
                break;
        }
        cm.dispose();
    } else {
        cm.dispose();
    }
}

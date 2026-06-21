function start() {
    if (cm.getPlayer().getMapId() == 910210000) {
        if (cm.getPlayer().getClient().getChannel() < 10) {
            cm.dispose();
            if (!cm.getMap().getAllMonstersThreadsafe().size() == 0) {
                cm.getPlayer().dropMessage(6, "请把地图里所有的BOSS清除的干干净净再点我~!");
                cm.dispose();
                return;
            }
            var currentStage = cm.getBossLog("无尽野王");
            if (currentStage == 0) {
                cm.setBossLog("无尽野王");
                cm.getPlayer().dropMessage(6, "此处是无尽野王!难度系数[未知]每清完BOSS点我进行下一关卡！~\r\n1111~!\r\n\r\n#b那就开始挑战了噢！~!#b");
				cm.getPlayer().dropMessage(6, "此处是无尽野王!难度系数[未知]每清完BOSS点我进行下一关卡！~\r\n1111~!\r\n\r\n#b那就开始挑战了噢！~!#b");
				cm.getPlayer().dropMessage(6, "此处是无尽野王!难度系数[未知]每清完BOSS点我进行下一关卡！~\r\n1111~!\r\n\r\n#b那就开始挑战了噢！~!#b");
                cm.dispose();
            } else if (currentStage >= 1 && currentStage <= 84) {
                cm.setBossLog("无尽野王");
                cm.喇叭(3, "玩家[" + cm.getPlayer().getName() + "]开始了无尽野王第" + currentStage + "关！！");
                var mobId = getMobIdForStage(currentStage);
                cm.spawnMobOnMap(mobId, 1, 472, 165, 910210000, 3000000000);
                cm.dispose();
            } else if (currentStage == 85) {
                // 检查是否已经领取过奖励
                if (cm.getBossLog("无尽野王_奖励") == 1) {
                    cm.getPlayer().dropMessage(6, "你已经领取过通关奖励了！");
					cm.warp(910000000,0);
                    cm.dispose();
                } else {
                    // 设置奖励已领取标志
                    cm.setBossLog("无尽野王_奖励");
                    cm.喇叭(3, "玩家[" + cm.getPlayer().getName() + "]完成了无尽野王！获得 快乐百宝卷100张！");
                    cm.gainItem(5220000, 100); //通关奖励-快乐百宝卷
                    cm.getPlayer().dropMessage(1, "恭喜通关：获得 快乐百宝卷100张");
                    cm.dispose();
                }
            } else {
                cm.getPlayer().dropMessage(6, "您今天已经通关了哦~！");
                cm.dispose();
            }
        } else {
            cm.getPlayer().dropMessage(6, "无尽野王NPC只能在1-8线挑战~!");
            cm.dispose();
        }
    }
}

function getMobIdForStage(stage) {
    // 根据关卡返回对应的怪物ID
    var mobIds = [
        2220000, 2220000, 2220000, 6130101, 6130101, 6130101, 9400205, 9400205, 9400205, 
		6300005, 6300005, 6300005, 3220000, 3220000, 3220000, 3220001, 3220001, 3220001, 
		4220000, 4220000, 4220000, 5220002, 5220002, 5220002, 5220003, 5220003, 5220003, 
		5220000, 5220000, 5220000, 7220000, 7220000, 7220000, 8220000, 8220000, 8220000, 
		8220002, 8220002, 8220002, 7220002, 7220002, 7220002, 7220001, 7220001, 7220001, 
		
		8180000, 8180000, 8180000, 8180001, 8180001, 8180001, 8130100, 8130100, 8130100, 
		6220000, 6220000, 6220000, 8220005, 8220005, 8220005, 8220005, 8220005, 8220005, 
		8220006, 8220006, 8220006, 8220003, 8220003, 8220003, 8520000, 8520000, 8520000, 
		8510000, 8510000, 8510000, 9420522, 9420522, 9420522, 9600009, 9600009, 9600009, 
		9600025, 9600025, 9600025
    ];
    return mobIds[stage - 1];
}
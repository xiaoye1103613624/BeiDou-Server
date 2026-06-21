function start() {
    if (cm.getPlayer().getMapId() == 875010400) {
        if (cm.getPlayer().getClient().getChannel() < 10) {
            cm.dispose();
            if (!cm.getMap().getAllMonstersThreadsafe().size() == 0) {
                cm.getPlayer().dropMessage(6, "请把地图里所有的BOSS清除的干干净净再点我~!");
                cm.dispose();
                return;
            }
            var currentStage = cm.getBossLog("星火秘境1");
            if (currentStage == 0) {
                cm.setBossLog("星火秘境1");
                cm.getPlayer().dropMessage(6, "此处是星火秘境!难度系数[未知]每清完BOSS点我进行下一关卡！~~~~~~~~~~那就开始挑战了噢！~!");
				cm.getPlayer().dropMessage(6, "此处是星火秘境!难度系数[未知]每清完BOSS点我进行下一关卡！~~~~~~~~~~那就开始挑战了噢！~!");
				cm.getPlayer().dropMessage(6, "此处是星火秘境!难度系数[未知]每清完BOSS点我进行下一关卡！~~~~~~~~~~那就开始挑战了噢！~!");
                cm.dispose();
            } else if (currentStage >= 1 && currentStage <= 20) {
                cm.setBossLog("星火秘境1");
                cm.喇叭(3, "玩家[" + cm.getPlayer().getName() + "]开始了星火秘境第" + currentStage + "关！！");
				var mobInfo = getMobInfoForStage(currentStage);
				cm.spawnMobOnMap(mobInfo[0], 1, 1074, -208, 875010400, mobInfo[1]);
                cm.dispose();
            } else if (currentStage == 21) {
				if (cm.getInventory(1).isFull(2) || cm.getInventory(2).isFull(2) || cm.getInventory(3).isFull(2) || cm.getInventory(4).isFull(2) || cm.getInventory(5).isFull(2)) {
					cm.sendOk("请保证背包所有栏位至少保留3个空格！");
					cm.dispose();
					return;
				}
                // 检查是否已经领取过奖励
                if (cm.getBossLog("星火秘境_奖励1") == 1) {
                    cm.getPlayer().dropMessage(6, "你已经领取过通关奖励了！");
					cm.warp(350012020,0);
                    cm.dispose();
                } else {
                    // 设置奖励已领取标志
                    cm.setBossLog("星火秘境_奖励1");
                    cm.喇叭(3, "玩家[" + cm.getPlayer().getName() + "]完成了星火秘境！获得 [突破百万之石100%]！");
                    cm.gainItem(2614002, 1); //通关奖励-突破百万之石
					cm.warp(350012020, 0); 
                    cm.getPlayer().dropMessage(1, "恭喜通关：\r\n获得 突破百万之石100% * 1");
					cm.全服漂浮喇叭("【星火秘境】：[恭喜" + cm.getPlayer().getName() + "]通关：获得 [突破百万之石100%]！", 5121000);
                    cm.dispose();
                }
            } else {
                cm.getPlayer().dropMessage(6, "您今天已经通关了哦~！");
                cm.dispose();
            }
        } else {
            cm.getPlayer().dropMessage(6, "星火秘境NPC只能在1-8线挑战~!");
            cm.dispose();
        }
    }
}


// 新增函数
function getMobInfoForStage(stage) {
    var info = [
        [3501008, 100000000000],
        [8880605, 200000000000],
        [9833434, 350000000000],
        [9400729, 500000000000],
        [2500200, 700000000000],
        [8620012, 900000000000],
        [9400633, 1100000000000],
        [9400514, 1300000000000],
        [9300351, 1500000000000],
        [8220024, 1700000000000],
		
        [9802005, 1900000000000],
        [8787127, 2100000000000], //8787125
        [8787127, 2300000000000],
        [8787128, 2500000000000],
        [8787129, 2700000000000],
        [8787130, 2900000000000],
        [1110932, 3100000000000],
        [9601221, 3300000000000],
		[9600000, 8000000000000],  //金蛋
        [9600138, 500000000] //福袋
    ];
    return info[stage - 1];
}
function enter(pi) {
    if (pi.getMap().getAllMonstersThreadsafe().size() == 0) {
        var fame = pi.getPlayer().getFame(); // 获取玩家的人气值
        if (fame > 2000) {
            fame = 2000; // 如果人气值超过2000点，按照2000点计算
        }
        var rewardCount = Math.floor(fame / 5); // 假设每3人气值可以获得1个奖励物品
        var rewardCount1 = Math.floor(fame / 20); // 假设每5人气值可以获得1个奖励物品
        if (rewardCount > 0) {
            pi.gainItem(4310020, rewardCount); // 根据人气值给予奖励物品
            pi.gainItem(3605006, rewardCount1); // 根据人气值给予奖励物品
            pi.warp(951000000, 0); // 传送玩家
            pi.playerMessage(5, "额外奖励：" + rewardCount + "个怪物公园纪念币 、 " + rewardCount1 + "个女神的赐福！");
            pi.playerMessage(5, "每5人气值增加1个怪物公园纪念币；每20人气值增加1个女神的赐福！");
        } else {
            pi.warp(951000000, 0); // 传送玩家
            pi.gainItem(4310020, 1); // 根据人气值给予奖励物品
            pi.playerMessage(5, "你的人气值不足3点，给予1个怪物公园纪念币作为安慰奖");
        }
    } else {
        pi.playerMessage(5, "传送门还没有打开。请把地图怪物清理完。");
    }
}
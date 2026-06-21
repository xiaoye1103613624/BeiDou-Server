var dungeonid = 910001005; // 地下城起始地图ID
var dungeons = 1; // 地下城地图的数量
var dailyLimit = 10; // 每日进入次数限制

function enter(pi) {
    // 获取玩家今日已进入的次数
    var enteredCount = pi.getPlayer().getBossLog("每日挖矿地图") || 0;

    // 检查是否达到每日进入次数上限
    if (enteredCount >= dailyLimit) {
        pi.getPlayer().dropMessage(5, "今日已经进入 " + enteredCount + " 次，每日可以进入 " + dailyLimit + " 次。无法再次进入。");
        return;
    }

    // 遍历所有地下城地图，寻找空的地图
    for (var i = 0; i < dungeons; i++) {
        if (pi.getPlayerCount(dungeonid + i) == 0) { // 检查当前地下城是否为空
            if (pi.getPlayer().getlpjf() >= 0) { // 检查玩家累计赞助是否超过0
                if (pi.haveItem(2022520)) { // 检查玩家是否持有入场券
                    pi.warp(dungeonid + i, 0); // 将玩家传送到空的地下城地图

                    // 增加进入次数记录
                    enteredCount++;
                    pi.getPlayer().setBossLog("每日挖矿地图", enteredCount);

                    // 提示玩家当前进入次数和每日限制
                    pi.getPlayer().dropMessage(5, "今日已进入 " + enteredCount + " 次，每日可以进入 " + dailyLimit + " 次。");

                    return;
                } else {
                    pi.getPlayer().dropMessage(5, "你没有持有拍卖盒子，无法进入该地图。");
                    return;
                }
            } else {
                pi.getPlayer().dropMessage(5, "你的累计赞助不足0，无法进入该地图。");
                return;
            }
        }
    }

    // 如果所有地下城都有人，提示玩家
    pi.getPlayer().dropMessage(5, "当前频道 秘密矿山 有人，请稍后换个频道再尝试。");
}
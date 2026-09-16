// Patience jump finish portal -> event winner map
function enter(pi) {
    var player = pi.getPlayer();
    player.saveLocation("EVENT");
    pi.warp(109050000, 0);
    player.dropMessage(6, "【忍苦跳跳】已到达终点！请找 #b活动获奖处 NPC（9000002）#k 领取奖励。");
}

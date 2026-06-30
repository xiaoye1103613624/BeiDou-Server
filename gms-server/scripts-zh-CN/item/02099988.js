// 吸吸吸吸 - 地图级别吸怪道具使用脚本

function start() {
    var player = rm.getPlayer();

    // 切换吸怪状态
    if (player.mobVacuumTask != null) {
        // 已启用吸怪，关闭它
        player.stopMobVacuum();
        player.dropMessage(1, "吸怪效果已关闭");
    } else {
        // 未启用吸怪，开启它
        var result = player.startMobVacuum();
        if (result) {
            player.dropMessage(1, "吸怪效果已启用！");
        } else {
            player.dropMessage(1, "吸怪启动失败，请稍后重试");
        }
    }
}

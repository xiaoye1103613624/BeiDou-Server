
function start(ms) {
    ms.playerMessage(5, "正在去 里恩 的路上......30秒后到达。");

    var Timer = Java.type('java.util.Timer');
    var TimerTask = Java.type('java.util.TimerTask');
    var player = ms.getPlayer();
    var targetMap = 140020300;

    var WarpTask = Java.extend(TimerTask, {
        run: function() {
            try {
                player.dropMessage(5, "已到达 里恩 企鹅港口，下次见！");
                player.changeMap(targetMap, 0);
            } catch(e) {}
        }
    });
    new Timer().schedule(new WarpTask(), 30000);
}
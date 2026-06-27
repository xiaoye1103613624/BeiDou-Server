

var setupTask;

function init() {
	scheduleNew();
}

function scheduleNew() {
	var cal = java.util.Calendar.getInstance();
	var nextTime = cal.getTimeInMillis();
	while (nextTime <= java.lang.System.currentTimeMillis()) {
		nextTime += 1000;
	}
	setupTask = em.scheduleAtTimestamp("start", nextTime);
}

function cancelSchedule() {
	setupTask.cancel(true);
}

function start() {
	var iter = em.getChannelServer().getPlayerStorage().getAllCharacters().iterator();
	var mydate = new Date();
	while (iter.hasNext()) {
		var chr = iter.next();
		if (mydate.getHours() == 20 && mydate.getMinutes() == 0 && mydate.getSeconds() == 0) {
            chr.startMapEffect("〖整点提示〗 20点整点奖励可领取状态已开启,时长10分钟,领取通道 拍卖 => 整点奖励", 5121007);
            em.broadcastYellowMsg("[整点提示]"+"20点整点奖励可领取状态已开启,时长10分钟,领取通道 拍卖 => 整点奖励");
            em.broadcastYellowMsg("[整点提示]"+"20点整点奖励可领取状态已开启,时长10分钟,领取通道 拍卖 => 整点奖励");
            em.broadcastYellowMsg("[整点提示]"+"20点整点奖励可领取状态已开启,时长10分钟,领取通道 拍卖 => 整点奖励");
		} else if (mydate.getHours() == 21 && mydate.getMinutes() == 0 && mydate.getSeconds() == 0) {
            chr.startMapEffect("〖整点提示〗 21点整点奖励可领取状态已开启,时长10分钟,领取通道 拍卖 => 整点奖励", 5121007);
            em.broadcastYellowMsg("[整点提示]"+"21点整点奖励可领取状态已开启,时长10分钟,领取通道 拍卖 => 整点奖励");
            em.broadcastYellowMsg("[整点提示]"+"21点整点奖励可领取状态已开启,时长10分钟,领取通道 拍卖 => 整点奖励");
            em.broadcastYellowMsg("[整点提示]"+"21点整点奖励可领取状态已开启,时长10分钟,领取通道 拍卖 => 整点奖励");
		} else if (mydate.getHours() == 22 && mydate.getMinutes() == 0 && mydate.getSeconds() == 0) {
            chr.startMapEffect("〖整点提示〗 22点整点奖励可领取状态已开启,时长10分钟,领取通道 拍卖 => 整点奖励", 5121007);
            em.broadcastYellowMsg("[整点提示]"+"22点整点奖励可领取状态已开启,时长10分钟,领取通道 拍卖 => 整点奖励");
            em.broadcastYellowMsg("[整点提示]"+"22点整点奖励可领取状态已开启,时长10分钟,领取通道 拍卖 => 整点奖励");
            em.broadcastYellowMsg("[整点提示]"+"22点整点奖励可领取状态已开启,时长10分钟,领取通道 拍卖 => 整点奖励");
		} else if (mydate.getHours() == 23 && mydate.getMinutes() == 0 && mydate.getSeconds() == 0) {
            chr.startMapEffect("〖整点提示〗 23点整点奖励可领取状态已开启,时长10分钟,领取通道 拍卖 => 整点奖励", 5121007);
            em.broadcastYellowMsg("[整点提示]"+"23点整点奖励可领取状态已开启,时长10分钟,领取通道 拍卖 => 整点奖励");
            em.broadcastYellowMsg("[整点提示]"+"23点整点奖励可领取状态已开启,时长10分钟,领取通道 拍卖 => 整点奖励");
            em.broadcastYellowMsg("[整点提示]"+"23点整点奖励可领取状态已开启,时长10分钟,领取通道 拍卖 => 整点奖励");
		}
	}
	scheduleNew();
}


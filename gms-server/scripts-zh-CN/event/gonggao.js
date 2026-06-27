var setupTask;
var dates = new Array(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24); //设置几点给奖励

function init() {
	scheduleNew();
}

function scheduleNew() {
	var cal = java.util.Calendar.getInstance();
	cal.set(java.util.Calendar.SECOND, 5);
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
	var mDate = new Date();
	for (var i = 0; i < dates.length; i++) {
		if (mDate.getSeconds() == 0 && mDate.getMinutes() == 0 && mDate.getHours() == dates[i]) {
			em.broadcastYellowMsg("☆赏金任务开始,10分钟内可领取任务获得奖励☆!");
			em.broadcastYellowMsg("☆赏金任务开始,10分钟内可领取任务获得奖励☆!");
			em.broadcastYellowMsg("☆赏金任务开始,10分钟内可领取任务获得奖励☆!");
			var iter = em.getChannelServer().getPlayerStorage().getAllCharacters().iterator();
			while (iter.hasNext()) {
				var chr = iter.next();
				//给点券
				//chr.modifyCSPoints(1, 500, true);
				//gainItem
				//chr.gainIten(4001126, 200);
                               // chr.gainMeso(9999999,true);
			}
			break;
		}
	}
	scheduleNew();
}

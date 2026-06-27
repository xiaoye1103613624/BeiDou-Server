
var setupTask;

function init() {
	scheduleNew();
}

function scheduleNew() {
	setupTask = em.schedule("start", 1000);
}

function cancelSchedule() {
	setupTask.cancel(true);
}

function start(	) {
	scheduleNew();
	var date = new Date();
	var hours = date.getHours(); //时
	var minute = date.getMinutes(); //分
	var second = date.getSeconds(); //秒
	if (hours>9 && minute > 0 && second > 0) {
		em.broadcastPlayerMsg(5, "通关成功,领取奖励!");
	}
}

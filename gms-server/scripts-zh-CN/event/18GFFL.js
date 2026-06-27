
 
  /* global em, java, Packages */
//【配置区】-----------------------------------------------------
var config_hours = 18;//24小时制。0点为0，不为24
var config_minutes = 0;//分钟0~59。
var config_seconds = 0;//秒0~59。
//-----------------------------------------------------

var setupTask;

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
function monsterDamaged(eim, chr, mobId, damage) {

}
function cancelSchedule() {
    if(setupTask != null){
        setupTask.cancel(true);
        setupTask = null;
    }
}

function start() {
	var mDate = new Date();
	if (mDate.getHours() == config_hours && mDate.getMinutes() == config_minutes  && mDate.getSeconds() == config_seconds ) {
		em.broadcastServerMsg(5122000, "『干饭福利』一万点卷 一万抵用 1000鱼 5元宝 1破功 1聚灵珠", true);
		var iter = em.getChannelServer().getPlayerStorage().getAllCharacters().iterator();
		while (iter.hasNext()) {
			var chr = iter.next();
			em.broadcastServerMsg(5,chr.getName(),false);
			chr.modifyCSPoints(1, 10000);//点卷
			chr.modifyCSPoints(2, 10000);//抵用
			//chr.gainMeso(555555,true);//游戏币
    chr.gainItem(3994742, 1000);//, "鱼"),
    chr.gainItem(2022509, 5);//, "元宝"),
    chr.gainItem(2614000, 1);//, "破功"),
    chr.gainItem(3605009, 1);//, "聚灵珠"),
    //chr.gainItem(4310148, 1);//, "升星币x1"),
    //chr.gainItem(4310148, 2);//, "升星币x2"),
    //chr.gainItem(4310148, 3);//, "升星币x3"),
  /*  chr.gainItem(2000016, 50);//, "白色药水x50"),
    chr.gainItem(2000017, 50);//, "蓝色药水x50"),
    chr.gainItem(2000017, 50);//, "蓝色药水x50"),
    chr.gainItem(2000016, 50);//, "白色药水x50"),
    chr.gainItem(2000015, 50);//, "橙色药水x50"),
    chr.gainItem(2000018, 50);//, "活力药水x50")	*/		
			
		}
	}
	scheduleNew();
}

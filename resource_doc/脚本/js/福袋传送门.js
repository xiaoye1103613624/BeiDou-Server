

//【配置区】-----------------------------------------------------
var _eventName = "WY_HuntingBoss_event";//事件名称(需要和事件脚本名称一致)
//-----------------------------------------------------
var 绿水灵 = "#fUI/UIWindow/Minigame/Omok/stone/1/white/0#";
var 猪猪 = "#fUI/UIWindow/Minigame/Omok/stone/2/black/0#";
var 熊熊 = "#fUI/UIWindow/Minigame/Omok/stone/6/black/0#";
var 彩虹 ="#fEffect/ItemEff/1071085/effect/walk1/2#";
var 小喇叭 = "#fUI/CN_Chat/ChattingRoom/BtVolUp/0/normal/0#";
var zzz = "#fUI/UIWindow.img/Quest/icon8/0#";//蓝色右箭头
var 正在进行 = "#fUI/UIWindow/Quest/Tab/disabled/1#";
var 可以开始 = "#fUI/UIWindow/Quest/Tab/enabled/0#";
var 任务简介 = "#fUI/UIWindow/Quest/summary#";
var 奖励 = "#fUI/UIWindow/Quest/reward#";
var 选择获得 = "#fUI/UIWindow/Quest/select#";
var 几率获得 = "#fUI/UIWindow/Quest/prob#";
var 无条件获得 = "#fUI/UIWindow/Quest/basic#";
var status = -1;//模组状态
var chr =null;
var say = "";
var em =null;
var needItemId = 0;
var needItemNum = 0;

function start(){
	if(cm.getChannelServer().getChannel() != 1){
		cm.sendOk("福袋任务只在一频道进行");
		cm.dispose();
		return;
	}
    chr = cm.getPlayer();	
    action(1,0,0);
}

function action(mode, type, selection) {
    if(mode == -1){cm.dispose();return;}
    if(status == 0 && mode == 0) {cm.dispose();return;}
    if(mode == 1) {status++;} else {status--;}

    if(status == 0){
        em = cm.getEventManager(_eventName);
        if(em == null){
            cm.sendOk("福袋任务配置文件不存在，请联系管理员：");
            cm.dispose();
            return;
        }
        var eventState = em.getProperty("state");

        say = "#w"+彩虹+"#r欢迎来到「"+cm.开服名称()+"」 - 怪物福袋#k\r\n\r\n";
		say +="#d______________________________________________________#r#k\r\n\r\n";
		
        say += 熊熊+"#dHi！勇敢的冒险家，不知道你是否听过#b怪物福袋#d？\r\n\r\n";
        say += 任务简介+"\r\n"+ 小喇叭+ " 参与#b怪物福袋，就可以获得丰厚的奖励哦。\r\n";
        say += 小喇叭+" 怪物福袋只有在每天#r11时~23时的30分#d会有BOSS领降临主城\r\n";
		say += 小喇叭+" #r注意：福袋只会出现在①频道,20分钟内击败可获得奖励\r\n";
        say += 小喇叭+" #b参与怪物福袋玩家,参与福袋后获得根据怪物#r★级#b给予随机奖励，有积分、金币、点券、抵用券、元宝、破攻石等。\r\n\r\n";
        if(eventState == 0){
            say += "福袋状态：#k[未刷新]#d\r\n\r\n";
            say += "#k刷新怪物福袋时会有喇叭提示，请不要着急！\r\n \r\n";
            cm.sendOk(say);
            cm.dispose();
        }else if(eventState == 1){
           //var mob_id =em.getProperty("mob_id");
			//var mob_map =em.getProperty("mob_map");
			var mob_level =em.getProperty("mob_level");
			var mob_name =em.getProperty("mob_name");
			var mob_mapname =em.getProperty("mob_mapname");
            say += "#d福袋状态：#g[怪物福袋已开启]#d\r\n";
            say += "#d怪物福袋：#r【"+mob_name+"】\r\n";
			// 数字 → N 个★
			var starStr = "";
			for(var i = 0; i < parseInt(mob_level); i++){
			    starStr += "★";
			}
			say += "#d怪物难度：#r" + starStr + "\r\n";
			say += "#d福袋主城：#r"+mob_mapname+"\r\n";
            say += "#L1#"+zzz+"#b传送到福袋所在主城#l";
            cm.sendSimple(say);
        }else if(eventState == 2){
            say += "任务状态：#k[还未开启]#d\r\n\r\n";
            say += "#k福袋已经被掠夺，请耐心等待下次福袋出现吧！\r\n \r\n";
            cm.sendOk(say);
            cm.dispose();
        }
    }else if(status == 1){
        cm.warp(em.getProperty("mob_map"));
		cm.dispose();
    }else{
        cm.dispose();
    }
}
function enter(pi) {
    if(!pi.isQuestStarted(32100)&&!pi.isQuestCompleted(32100)){
		pi.playerMessage(5, "一股神秘的力量把你拦在了外面");
		return false;
	}
	else if (pi.isQuestStarted(32100)) {
        pi.playerMessage(5, "你到了妖精学院艾利涅湖前。");
		pi.playPortalSound();
		pi.warp(101070000);
		return true;
    }else if(pi.isQuestCompleted(32104)){
		pi.playerMessage(5, "你到了妖精学院艾利涅。");
	}

    pi.playPortalSound();
    pi.warp(101070010);
    return true;
}

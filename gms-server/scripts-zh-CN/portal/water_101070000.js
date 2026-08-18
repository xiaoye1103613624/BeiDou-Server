function enter(pi) {
    if(!pi.isQuestStarted(32102)&&!pi.isQuestCompleted(32102)){
		pi.playerMessage(5, "一股神秘的力量把你送回了原地。");
		return false;
	}
	else if (!pi.isQuestCompleted(32104)) {
		pi.playerMessage(5, "感觉身体变得很奇怪，不自觉的沉了下去。去找潘喜帮忙吧。");
		pi.forceCompleteQuest(32102);
		pi.playPortalSound();
		pi.warp(101070000);
		return true;
    }else{
    pi.playPortalSound();
    pi.warp(101070010);
    return true;
	}
}

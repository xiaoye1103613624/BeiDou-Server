function enter(pi) {
    if(!pi.haveItem(4032923,1)||pi.isQuestStarted(31149)){
		pi.playerMessage(5, "似乎需要钥匙才能进入这里，回去问问看村长阿勒斯会不会知道什么吧。")
		pi.forceCompleteQuest(31149);
		return false;
	}
	else if (pi.isQuestStarted(31149)) {
        pi.forceCompleteQuest(31149);
        pi.playerMessage(5, "任务完成。");
    }

    pi.playPortalSound();
    pi.warp(271040000, 0);
    return true;
}

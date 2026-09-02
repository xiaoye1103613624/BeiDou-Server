function enter(pi) {
    var questReq = [32162, 32166, 32172, 32181, 32176, 32186];

    if (!pi.isQuestCompleted(questReq[2])) {
        pi.playerMessage(5, "航海士，现在还不能进入这里耶。");
        return false;
    }

    pi.playerMessage(5, "你到了第2观测站。");
    pi.playPortalSound();
    pi.warp(141020000);
    return true;
}

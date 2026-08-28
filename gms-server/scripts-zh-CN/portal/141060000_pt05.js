function enter(pi) {
    var questReq = [32162, 32166, 32172, 32181, 32176, 32186];

    if (!pi.isQuestCompleted(questReq[5])) {
        pi.playerMessage(5, "航海士，现在还不能进入这里耶。");
        return false;
    }

    pi.playerMessage(5, "你到了冰川破坏者的巢穴。");
    pi.playPortalSound();
    pi.warp(141050000);
    return true;
}

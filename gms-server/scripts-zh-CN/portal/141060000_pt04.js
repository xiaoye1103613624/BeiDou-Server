function enter(pi) {
    var questReq = [32162, 32166, 32172, 32181, 32176, 32186];

    if (!pi.isQuestCompleted(questReq[4])) {
        pi.playerMessage(5, "航海士，现在还不能进入这里耶。");
        return false;
    }

    var msgs = [
        "好的，航海士！就按你说的做！",
        "我要停船啦。你没晕船吧？哈哈哈。"
    ];
    pi.playerMessage(5, msgs[Math.floor(Math.random() * msgs.length)]);
    pi.playPortalSound();
    pi.warp(141040000);
    return true;
}

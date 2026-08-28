function enter(pi) {
    var questReq = [32162, 32166, 32172, 32181, 32176, 32186];

    if (!pi.isQuestCompleted(questReq[3])) {
        pi.playerMessage(5, "航海士，现在还不能进入这里耶。");
        return false;
    }

    var msgs = [
        "好的！我最喜欢陆地了。",
        "好的，航海士。我就让你瞧瞧我高超的停靠技术！",
        "好的，航海士。就按你说的做！"
    ];
    pi.playerMessage(5, msgs[Math.floor(Math.random() * msgs.length)]);
    pi.playPortalSound();
    pi.warp(141030000);
    return true;
}

/*
    传送到703000001 - 2022新叶城 - 被占领的 新叶城
*/

function enter(pi) {
    if (pi.getQuestStatus(56216) != 2) {
        pi.topMessage("目前无法进入，这好像是和未来的新叶城有某种联系");
        pi.playerMessage("目前无法进入，这好像是和未来的新叶城有某种联系");
        return false;
    }

    pi.warp(703010003, 0);
    return true;
}

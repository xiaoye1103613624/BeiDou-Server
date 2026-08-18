// 鲁塔比斯第三封印门 -> 血腥女王（南部庭院）
function enter(pi) {
    if (pi.getPlayer().getLevel() < 120) {
        pi.message("需要等级 120 以上才能进入。");
        return false;
    }
    pi.playPortalSound();
    pi.warp(105200300, "sp");
    return true;
}

// 鲁塔比斯第二封印门 -> 半半（东侧庭院）
function enter(pi) {
    if (pi.getPlayer().getLevel() < 120) {
        pi.message("需要等级 120 以上才能进入。");
        return false;
    }
    pi.playPortalSound();
    pi.warp(105200100, "sp");
    return true;
}

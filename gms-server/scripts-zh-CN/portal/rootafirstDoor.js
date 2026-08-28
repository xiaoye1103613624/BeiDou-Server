// 鲁塔比斯第一封印门 -> 皮埃尔（西侧庭院）
function enter(pi) {
    if (pi.getPlayer().getLevel() < 120) {
        pi.message("需要等级 120 以上才能进入。");
        return false;
    }
    pi.playPortalSound();
    pi.warp(105200200, "sp");
    return true;
}

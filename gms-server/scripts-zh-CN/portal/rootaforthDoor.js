// 鲁塔比斯第四封印门 -> 贝伦（北部庭院）
function enter(pi) {
    if (pi.getPlayer().getLevel() < 120) {
        pi.message("需要等级 120 以上才能进入。");
        return false;
    }
    pi.playPortalSound();
    pi.warp(105200400, "sp");
    return true;
}

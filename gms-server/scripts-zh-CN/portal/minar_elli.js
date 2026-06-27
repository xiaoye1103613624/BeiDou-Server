function enter(pi) {
    if (pi.haveItem(4031346)) {
        if (pi.getMapId() == 240010100) {
            pi.playPortalSound();
            pi.warp(101010000, "minar00");
        } else {
            pi.playPortalSound();
            pi.warp(240010100, "elli00");
        }
        pi.gainItem(4031346, -1);
        return true;
    } else {
        pi.playerMessage("传送到神祕的地方需要魔法种子。");
        return false;
    }
}
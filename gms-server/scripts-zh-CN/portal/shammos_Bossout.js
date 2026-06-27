function enter(pi) {
        if (pi.getPlayer().getParty() != null && pi.getMap().getAllMonstersThreadsafe().size() == 0 && pi.isLeader()) {
                pi.warpParty_Instanced(921120600);
                pi.playPortalSound();
        } else {
                pi.playerMessage(5,"璇峰厛娑堢伃鑾辨牸鏂€?);
        }
}
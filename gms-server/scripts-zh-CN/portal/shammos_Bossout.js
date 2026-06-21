function enter(pi) {
        if (pi.getPlayer().getParty() != null && pi.getMap().getAllMonstersThreadsafe().size() == 0 && pi.isLeader()) {
                pi.warpParty_Instanced(921120600);
                pi.playPortalSE();
        } else {
                pi.playerMessage(5,"请先消灭莱格斯。");
        }
}
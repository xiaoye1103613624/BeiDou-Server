function start() {//
    cm.teachSkill(14101004, 20, 20);
    cm.getPlayer().changeKeybinding(88, 1, 14101004);
    // 让玩家下线,确认服务端有没有该函数
    //cm.getPlayer().fakeRelog();
    cm.dispose();
}
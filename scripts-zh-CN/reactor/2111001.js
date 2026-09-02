function act() {
    var chaos = false;
    try {
        var data = rm.getPlayer().getQuestRecord(9031002).getCustomData();
        chaos = (data === "CHAOS_ZAKUM");
    } catch (e) {
        chaos = false;
    }

    if (rm.getPlayer().getEventInstance() != null) {
        rm.getPlayer().getEventInstance().setProperty("summoned", "true");
        rm.getPlayer().getEventInstance().setProperty("canEnter", "false");
    }

    rm.changeMusic("Bgm06/FinalFight");

    if (chaos) {
        // 混沌扎昆：本体壳 + 手臂 8800103~8800109
        rm.spawnFakeMonster(8800100);
        for (var i = 8800103; i <= 8800109; i++) {
            rm.spawnMonster(i);
        }
        rm.mapMessage(5, "【混沌炎魔】火焰之眼的力量正在召唤混沌扎昆！");
        try {
            rm.getPlayer().getQuestRecord(9031002).setCustomData("");
        } catch (e2) {
        }
    } else {
        rm.spawnFakeMonster(8800000);
        for (var j = 8800003; j < 8800011; j++) {
            rm.spawnMonster(j);
        }
        rm.mapMessage(5, "【炎魔苏醒】火焰之眼的力量正在召唤扎昆！");
    }

    rm.createMapMonitor(280030000, "ps00");
}

// 进阶挑战副本 — Event 倒计时（开战 20 分钟，通关后 2 分钟清场）
var ChallengeFatigueManager = Java.type("org.gms.config.ChallengeFatigueManager");
var status = -1;
var SCRIPT_BRIDGE_QUEST = 9031001;
var CHALLENGE_TYPE = ChallengeFatigueManager.typeAdvanced();
var FIGHT_MINUTES = 20;
var selectedBoss = null;

var BOSSES = [
    {name: "鲁塔比斯大厅", special: "warp", map: 105200000, mobs: [8910100, 8900100, 8920100, 8930100]},
    {name: "半半（混沌）", map: 105200510, mobs: [8910000], x: -700, y: 455},
    {name: "皮埃尔（混沌）", map: 105200610, mobs: [8900000], x: 400, y: 531},
    {name: "血腥女王（混沌）", map: 105200710, mobs: [8920000], x: 0, y: 180},
    {name: "贝伦（混沌）", map: 105200810, mobs: [8930000], x: 0, y: 180},
    {name: "希纳斯", map: 271040100, mobs: [8850011], x: -1063, y: 115},
    {name: "进阶扎昆", map: 280030000, mobs: [8800102], x: -10, y: -204, special: "reactor", reactorName: "boss", flag: "CHAOS_ZAKUM"},
    {name: "混沌扎昆（远征版）", mobs: [8800102], special: "expedition", script: "xy/匠人街/混沌扎昆"}
];

function consumeTicket(boss) {
    var characterId = cm.getPlayer().getId();
    var accountId = cm.getClient().getAccID();
    var mapId = boss.map != null ? boss.map : 0;
    var preview = ChallengeFatigueManager.getInfo(characterId, CHALLENGE_TYPE);
    if (!preview.get("success") || preview.get("remaining") <= 0) {
        cm.sendOk((preview.get("message") || "次数不足") + "\r\n可使用 #b#t2004901##k 增加次数。");
        return false;
    }
    var consume = ChallengeFatigueManager.consumeEnter(
        characterId, accountId, CHALLENGE_TYPE, boss.name, mapId, boss.mobs.join(","));
    if (!consume.get("success")) {
        cm.sendOk(consume.get("message") + "\r\n可使用 #b#t2004901##k 增加次数。");
        return false;
    }
    cm.getPlayer().dropMessage(5, "进阶挑战次数剩余：" + consume.get("remaining"));
    return true;
}

function startChallengeEvent(boss) {
    var em = cm.getEventManager("ChallengeDungeon");
    if (em == null) {
        cm.sendOk("挑战事件未加载，请联系管理员执行 !reloadevents。");
        return false;
    }
    if (cm.getPlayer().getEventInstance() != null) {
        cm.sendOk("你已在其他副本中。");
        return false;
    }

    em.setProperty("cfgMap", "" + boss.map);
    em.setProperty("cfgMobs", boss.mobs.join(","));
    em.setProperty("cfgX", "" + (boss.x != null ? boss.x : 0));
    em.setProperty("cfgY", "" + (boss.y != null ? boss.y : 0));
    em.setProperty("cfgFight", "" + FIGHT_MINUTES);
    em.setProperty("cfgName", boss.name);
    em.setProperty("cfgSpecial", boss.special || "");
    em.setProperty("cfgReactor", boss.reactorName || "");
    em.setProperty("cfgFlag", boss.flag || "");

    if (!em.startInstance(cm.getPlayer())) {
        cm.sendOk("当前频道挑战大厅已满，请稍后再试或换线。");
        return false;
    }
    cm.getPlayer().dropMessage(5, "开战时限 " + FIGHT_MINUTES + " 分钟，通关后 2 分钟送回。");
    return true;
}

function startChallenge(boss) {
    if (boss.special === "warp") {
        if (!consumeTicket(boss)) {
            cm.dispose();
            return;
        }
        cm.warp(boss.map, 0);
        cm.getPlayer().dropMessage(5, "已抵达鲁塔比斯大厅。请走四扇封印门进入庭院，再进入 Boss 房触发官方召唤。");
        cm.dispose();
        return;
    }
    if (boss.special === "expedition") {
        if (!consumeTicket(boss)) {
            cm.dispose();
            return;
        }
        cm.dispose();
        cm.openNpc(9031000, boss.script);
        return;
    }

    // Event 开战：先检查次数，进本成功后再扣次
    var characterId = cm.getPlayer().getId();
    var preview = ChallengeFatigueManager.getInfo(characterId, CHALLENGE_TYPE);
    if (!preview.get("success") || preview.get("remaining") <= 0) {
        cm.sendOk((preview.get("message") || "次数不足") + "\r\n可使用 #b#t2004901##k 增加次数。");
        cm.dispose();
        return;
    }
    if (!startChallengeEvent(boss)) {
        cm.dispose();
        return;
    }
    var accountId = cm.getClient().getAccID();
    var mapId = boss.map != null ? boss.map : 0;
    var consume = ChallengeFatigueManager.consumeEnter(
        characterId, accountId, CHALLENGE_TYPE, boss.name, mapId, boss.mobs.join(","));
    cm.getPlayer().dropMessage(5, "进阶挑战次数剩余：" + consume.get("remaining"));
    cm.dispose();
}

function openDropViewer(boss) {
    cm.getQuestRecord(SCRIPT_BRIDGE_QUEST).setCustomData(boss.name + "|" + boss.mobs.join(","));
    cm.dispose();
    cm.openNpc(9031000, "xy/匠人街/挑战掉落查询");
}

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1 || mode === 0) {
        cm.dispose();
        return;
    }
    status++;

    if (status === 0) {
        var info = ChallengeFatigueManager.getInfo(cm.getPlayer().getId(), CHALLENGE_TYPE);
        var text = "#e#b<进阶挑战副本>#k#n\r\n";
        text += "Boss 战开战时限 #b" + FIGHT_MINUTES + " 分钟#k，通关后 #b2 分钟#k 送回。\r\n";
        text += "四皇大厅仅传送；扎昆走祭坛反应堆。\r\n";
        text += "今日剩余次数：#r" + info.get("remaining") + "#k（每日重置为"
            + info.get("dailyBase") + "次，可用 #b#t2004901##k 叠加）\r\n\r\n";
        for (var i = 0; i < BOSSES.length; i++) {
            text += "#L" + i + "#" + BOSSES[i].name + "#l\r\n";
        }
        cm.sendSimple(text);
    } else if (status === 1) {
        selectedBoss = BOSSES[selection];
        var menu = "#e#b" + selectedBoss.name + "#k#n\r\n";
        if (selectedBoss.map) {
            menu += "地图：" + selectedBoss.map + "\r\n";
        }
        if (selectedBoss.special !== "warp" && selectedBoss.special !== "expedition") {
            menu += "时限：" + FIGHT_MINUTES + " 分钟\r\n";
        }
        menu += "\r\n#L0#开始挑战#l\r\n";
        menu += "#L1#查看掉落#l\r\n";
        cm.sendSimple(menu);
    } else if (status === 2) {
        if (selection === 0) {
            startChallenge(selectedBoss);
        } else {
            openDropViewer(selectedBoss);
        }
    }
}

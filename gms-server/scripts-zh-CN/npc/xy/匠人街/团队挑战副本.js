// 团队挑战副本 — Event 倒计时（开战 30 分钟，通关后 2 分钟清场）
var ChallengeFatigueManager = Java.type("org.gms.config.ChallengeFatigueManager");
var status = -1;
var SCRIPT_BRIDGE_QUEST = 9031001;
var CHALLENGE_TYPE = ChallengeFatigueManager.typeTeam();
var FIGHT_MINUTES = 30;
var selectedBoss = null;

var BOSSES = [
    {name: "希拉", map: 262030300, mobs: [8870000], x: 250, y: 196},
    {name: "麦格纳斯", map: 401060100, mobs: [8880000], x: 2452, y: -1347},
    {name: "暴君 / 困难麦格纳斯", map: 401060200, mobs: [8880002], x: 2831, y: -1347},
    {name: "四凶 · 穷奇", map: 511000100, mobs: [8880830], x: -135, y: 381},
    {name: "四凶 · 梼杌", map: 105200800, mobs: [8880831], x: 1105, y: 180},
    {name: "四凶 · 混沌", map: 105200900, mobs: [8880832], x: 1105, y: 180}
];

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

    var characterId = cm.getPlayer().getId();
    var accountId = cm.getClient().getAccID();
    var preview = ChallengeFatigueManager.getInfo(characterId, CHALLENGE_TYPE);
    if (!preview.get("success") || preview.get("remaining") <= 0) {
        cm.sendOk((preview.get("message") || "次数不足") + "\r\n可使用 #b#t2004902##k 增加次数。");
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

    var consume = ChallengeFatigueManager.consumeEnter(
        characterId, accountId, CHALLENGE_TYPE, boss.name, boss.map, boss.mobs.join(","));
    cm.getPlayer().dropMessage(5, "团队挑战次数剩余：" + consume.get("remaining")
        + "｜开战时限 " + FIGHT_MINUTES + " 分钟，通关后 2 分钟送回。");
    return true;
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
        var text = "#e#b<团队挑战副本>#k#n\r\n";
        text += "开战时限 #b" + FIGHT_MINUTES + " 分钟#k，通关后 #b2 分钟#k 拾取并送回。\r\n";
        text += "主要掉落：A/S 级宝石、暴君装、四凶材料\r\n";
        text += "今日剩余次数：#r" + info.get("remaining") + "#k（每日重置为"
            + info.get("dailyBase") + "次，可用 #b#t2004902##k 叠加）\r\n\r\n";
        for (var i = 0; i < BOSSES.length; i++) {
            text += "#L" + i + "#" + BOSSES[i].name + "#l\r\n";
        }
        cm.sendSimple(text);
    } else if (status === 1) {
        selectedBoss = BOSSES[selection];
        var menu = "#e#b" + selectedBoss.name + "#k#n\r\n";
        menu += "地图：" + selectedBoss.map + "｜时限：" + FIGHT_MINUTES + " 分钟\r\n\r\n";
        menu += "#L0#开始挑战#l\r\n";
        menu += "#L1#查看掉落#l\r\n";
        cm.sendSimple(menu);
    } else if (status === 2) {
        if (selection === 0) {
            startChallengeEvent(selectedBoss);
            cm.dispose();
        } else {
            openDropViewer(selectedBoss);
        }
    }
}

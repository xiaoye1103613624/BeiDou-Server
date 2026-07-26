// 普通挑战副本 — Event 倒计时（开战 15 分钟，通关后 2 分钟清场）
var ChallengeFatigueManager = Java.type("org.gms.config.ChallengeFatigueManager");
var status = -1;
var SCRIPT_BRIDGE_QUEST = 9031001;
var CHALLENGE_TYPE = ChallengeFatigueManager.typeNormal();
var FIGHT_MINUTES = 15;
var selectedBoss = null;

var BOSSES = [
    {name: "地鼠王", map: 101073300, mobs: [3501008], x: 18, y: 222},
    {name: "盖奥勒克", map: 141050300, mobs: [3502008], x: 432, y: 111},
    {name: "黑色之翼飞船", map: 350023500, mobs: [8240046], x: -362, y: -150},
    {name: "贝尔加莫特", map: 240070203, mobs: [7220003], x: 570, y: 392},
    {name: "钻机", map: 703011000, mobs: [9600086], x: 616, y: 217},
    {name: "斯乌", map: 105200100, mobs: [8240098, 8240105], x: 2450, y: 221}
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
        cm.sendOk((preview.get("message") || "次数不足") + "\r\n可使用 #b#t2004900##k 增加次数。");
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
    cm.getPlayer().dropMessage(5, "普通挑战次数剩余：" + consume.get("remaining")
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
        var text = "#e#b<普通挑战副本>#k#n\r\n";
        text += "开战时限 #b" + FIGHT_MINUTES + " 分钟#k，通关后 #b2 分钟#k 拾取并送回。\r\n";
        text += "主要掉落：勇者之石、圣者之石、一级宝石、二级宝石\r\n";
        text += "今日剩余次数：#r" + info.get("remaining") + "#k（每日重置为"
            + info.get("dailyBase") + "次，可用 #b#t2004900##k 叠加）\r\n\r\n";
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

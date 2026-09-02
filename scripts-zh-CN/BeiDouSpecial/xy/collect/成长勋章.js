/**
 * 成长勋章（1142747）
 * - 地区整区卡满5 → 注入：四维+5 攻/魔+2
 * - 野外Boss 每种满5 → 注入：四维+1
 * - 远征Boss 每种满5 → 注入：四维+2 攻/魔+1
 * - 勋章池注入其它勋章；幻化外观每次500W
 *
 * 入口：收集系统 / 匠人街收藏成就
 */
var MedalGrowthService = Java.type("org.gms.service.MedalGrowthService");
var ItemConstants = Java.type("org.gms.constants.inventory.ItemConstants");
var InventoryType = Java.type("org.gms.client.inventory.InventoryType");

var BASE = 1142747;
var ILLUSION_COST_WAN = 500;

var status = -1;
var menuMode = "";
var selIndex = -1;
var bagMedals = [];

// ===== 野外Boss（定时刷新）每种单独注入 =====
var FIELD_ELITES = [
    { id: 2388006, name: "蘑菇王" },
    { id: 2388002, name: "浮士德" },
    { id: 2388007, name: "鳄鱼王" },
    { id: 2388044, name: "大王蜈蚣" },
    { id: 2388025, name: "树妖王" },
    { id: 2388008, name: "僵尸蘑菇王" },
    { id: 2388026, name: "蝙蝠怪" },
    { id: 2388015, name: "艾利杰" },
    { id: 2388012, name: "远古精灵" },
    { id: 2388016, name: "驮狼雪人" },
    { id: 2388052, name: "巨型蜈蚣" },
    { id: 2388010, name: "肯德熊" },
    { id: 2388013, name: "妖怪绅士" },
    { id: 2388009, name: "九尾狐" },
    { id: 2388031, name: "朱诺" }
];

// ===== 远征Boss 每种单独注入 =====
var EXPEDITION_BOSSES = [
    { id: 2388023, name: "扎昆" },
    { id: 2388022, name: "帕普拉图斯" },
    { id: 2388020, name: "皮亚奴斯" },
    { id: 2388018, name: "火焰龙" },
    { id: 2388019, name: "天鹰" },
    { id: 2388033, name: "大海兽" },
    { id: 2388024, name: "暗黑龙王" }
];

// ===== 地区（互不重复，与卡片收集一致）=====
var TOWNS = [
    { id: "henesys", name: "射手村", cards: [2380000,2380001,2380004,2380002,2380007,2380006,2380009,2381002,2383030,2382053] },
    { id: "ellinia", name: "魔法密林", cards: [2380005,2380011,2381007,2382018,2382029,2382040,2383029] },
    { id: "perion", name: "废弃都市", cards: [2380010,2380012,2381003,2381006,2382002,2383019,2382019,2383008] },
    { id: "shanghai", name: "上海外滩", cards: [2381044,2381045,2381046,2381047,2382077,2382078,2382079,2382080] },
    { id: "sleepy", name: "勇士部落", cards: [2381000,2381008,2381022,2382003,2381001,2381014,2381018,2382065,2382069,2382063,2383005,2383036,2380003,2384003,2384029] },
    { id: "anttunnel", name: "林中之城", cards: [2381016,2381024,2382039,2383012,2384006,2384015,2384001,2383039,2383043,2384020,2384036] },
    { id: "orbis", name: "天空之城", cards: [2383021,2381012,2383003,2382052,2383022,2383023,2382020,2382064,2383020,2384030,2381032,2381037,2381038] },
    { id: "elnath", name: "冰峰雪域", cards: [2383013,2384009,2383031,2383040,2383045,2385004,2384026,2383038,2383037,2382047,2382023,2383017,2382021,2382006,2385021,2384035,2385006] },
    { id: "muLung", name: "武陵", cards: [2383015,2383018,2382045,2382051,2382060,2382070,2382071,2384002,2383041,2383047,2383032,2384013] },
    { id: "herb", name: "百草堂", cards: [2384017,2384025,2383006,2383010,2383025,2383027,2384008,2383035] },
    { id: "ludi", name: "玩具城", cards: [2384014,2382048,2382062,2382049,2383002,2383004,2382034,2382037,2381034,2382054,2382066,2382004,2382015,2382038,2382025,2382005,2382016,2381011,2381027,2382026,2382000,2382001,2382031,2382022,2382033,2384019,2384032,2385010,2385015,2385012,2385020,2386002,2386009,2386004,2386010,2387000,2387001] },
    { id: "aqua", name: "海底世界", cards: [2381009,2381026,2382056,2382059,2381035,2385013,2386000,2386003,2386007,2386012,2381017,2382027,2382035,2382043,2382044,2382007,2381029,2381021,2381013,2386014] },
    { id: "korean", name: "童话村", cards: [2382068,2383014,2383024,2383034,2384022,2384023,2384021] },
    { id: "omega", name: "地球防御本部", cards: [2382050,2382061,2383000,2383026,2382042,2382055,2382067,2383011] },
    { id: "leafre", name: "神木村", cards: [2384024,2384033,2384027,2385001,2384028,2385002,2385005,2385007,2385011,2385014,2385019,2385016,2385017,2385018,2385022,2386001,2386005,2386006,2386008,2386013,2386015,2386016,2386011,2386017,2387004,2387003,2387002] }
];

function svc() { return MedalGrowthService.get(); }

function start() {
    status = -1;
    menuMode = "";
    selIndex = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode !== 1) { cm.dispose(); return; }
    status++;

    if (status === 0) {
        showMain();
    } else if (status === 1) {
        handleMain(selection);
    } else if (status === 2) {
        handleSub(selection);
    } else if (status === 3) {
        handleConfirm(selection);
    } else {
        cm.dispose();
    }
}

function getCardLv(cardId) {
    var cards = cm.getPlayer().getMonsterBook().getCards();
    var c = cards.get(cardId);
    return c != null ? c : 0;
}

function cardDone(cardId) { return getCardLv(cardId) >= 5; }

function regionProgress(town) {
    var done = 0;
    for (var i = 0; i < town.cards.length; i++) {
        if (cardDone(town.cards[i])) done++;
    }
    return { done: done, total: town.cards.length, full: done >= town.cards.length };
}

function showMain() {
    var s = svc();
    var p = cm.getPlayer();
    var text = "#e#b<成长勋章 · 探索印记>#k#n\r\n";
    text += "本体：#i" + BASE + "# #z" + BASE + "#\r\n";
    text += "当前：" + s.describeStats(p) + "\r\n\r\n";
    text += "#d地区整区满卡注入：四维+5 攻/魔+2#k\r\n";
    text += "#d野外Boss每种满5：四维+1#k\r\n";
    text += "#d远征Boss每种满5：四维+2 攻/魔+1#k\r\n";
    text += "#d勋章池幻化：每次 " + ILLUSION_COST_WAN + "W 金币#k\r\n\r\n";

    if (!s.ownsGrowthMedal(p)) {
        text += "#L1##b领取初始勋章#k#l\r\n";
    } else {
        text += "#L2##b地区收集注入#k#l\r\n";
        text += "#L3##b野外Boss注入#k#l\r\n";
        text += "#L4##b远征Boss注入#k#l\r\n";
        text += "#L5##b勋章池（注入外观）#k#l\r\n";
        text += "#L6##b幻化外观（" + ILLUSION_COST_WAN + "W）#k#l\r\n";
    }
    text += "\r\n#L0#离开#l";
    cm.sendSimple(text);
}

function handleMain(selection) {
    if (selection === 0) { cm.dispose(); return; }
    var s = svc();
    var p = cm.getPlayer();

    if (selection === 1) {
        var r = s.claimBaseMedal(p);
        if (r === "OK") {
            cm.sendOk("领取成功！#i" + BASE + "# 已放入背包。\r\n收集怪物卡后来注入属性吧。");
        } else {
            cm.sendOk("#r" + r + "#k");
        }
        cm.dispose();
        return;
    }

    menuMode = "" + selection;
    status = 1;
    if (selection === 2) showRegions();
    else if (selection === 3) showElites();
    else if (selection === 4) showExpeds();
    else if (selection === 5) showPoolInject();
    else if (selection === 6) showIllusion();
    else cm.dispose();
}

function showRegions() {
    var s = svc();
    var p = cm.getPlayer();
    var t = "#e地区收集注入#n（整区卡种各满5）\r\n完成：#b四维+5 攻+2 魔+2#k\r\n\r\n";
    for (var i = 0; i < TOWNS.length; i++) {
        var town = TOWNS[i];
        var pr = regionProgress(town);
        var inj = s.hasRegionInjected(p, town.id);
        t += "#L" + i + "#" + town.name + " [" + pr.done + "/" + pr.total + "]";
        if (inj) t += " #g[已注入]#k";
        else if (pr.full) t += " #b[可注入]#k";
        else t += " #d未集齐#k";
        t += "#l\r\n";
    }
    t += "\r\n#L999#返回#l";
    cm.sendSimple(t);
}

function showElites() {
    var s = svc();
    var p = cm.getPlayer();
    var t = "#e野外Boss注入#n（定时刷新Boss，每种满5）\r\n完成：#b四维+1#k\r\n\r\n";
    for (var i = 0; i < FIELD_ELITES.length; i++) {
        var e = FIELD_ELITES[i];
        var lv = getCardLv(e.id);
        var inj = s.hasEliteInjected(p, e.id);
        t += "#L" + i + "##v" + e.id + "# " + e.name + " " + lv + "/5";
        if (inj) t += " #g[已注入]#k";
        else if (lv >= 5) t += " #b[可注入]#k";
        t += "#l\r\n";
    }
    t += "\r\n#L999#返回#l";
    cm.sendSimple(t);
}

function showExpeds() {
    var s = svc();
    var p = cm.getPlayer();
    var t = "#e远征Boss注入#n（每种满5算一次）\r\n完成：#b四维+2 攻+1 魔+1#k\r\n\r\n";
    for (var i = 0; i < EXPEDITION_BOSSES.length; i++) {
        var e = EXPEDITION_BOSSES[i];
        var lv = getCardLv(e.id);
        var inj = s.hasExpedInjected(p, e.id);
        t += "#L" + i + "##v" + e.id + "# " + e.name + " " + lv + "/5";
        if (inj) t += " #g[已注入]#k";
        else if (lv >= 5) t += " #b[可注入]#k";
        t += "#l\r\n";
    }
    t += "\r\n#L999#返回#l";
    cm.sendSimple(t);
}

function showPoolInject() {
    bagMedals = [];
    var inv = cm.getPlayer().getInventory(InventoryType.EQUIP);
    var items = inv.list().toArray();
    var seen = {};
    for (var i = 0; i < items.length; i++) {
        var id = items[i].getItemId();
        if (ItemConstants.isMedal(id) && id !== BASE && !seen[id]) {
            seen[id] = true;
            bagMedals.push(id);
        }
    }

    var pool = svc().getPool(cm.getPlayer());
    var t = "#e勋章池#n\r\n已注入外观：\r\n";
    if (pool.size() === 0) t += "#d（空）#k\r\n";
    else {
        for (var j = 0; j < pool.size(); j++) {
            t += "#i" + pool.get(j) + "# ";
            if ((j + 1) % 6 === 0) t += "\r\n";
        }
        t += "\r\n";
    }
    t += "\r\n选择背包中的勋章注入池（消耗该勋章）：\r\n";
    if (bagMedals.length === 0) {
        t += "#d背包装备栏没有可注入的勋章#k\r\n";
    } else {
        for (var k = 0; k < bagMedals.length; k++) {
            t += "#L" + k + "##i" + bagMedals[k] + "# #z" + bagMedals[k] + "##l\r\n";
        }
    }
    t += "\r\n#L999#返回#l";
    cm.sendSimple(t);
}

function showIllusion() {
    var pool = svc().getPool(cm.getPlayer());
    var t = "#e幻化外观#n\r\n每次消耗 #r" + ILLUSION_COST_WAN + "W#k 金币，只改外观，卡属性保留。\r\n\r\n";
    t += "#L1000##i" + BASE + "# 还原默认外观#l\r\n";
    for (var i = 0; i < pool.size(); i++) {
        var mid = pool.get(i);
        t += "#L" + i + "##i" + mid + "# #z" + mid + "##l\r\n";
    }
    if (pool.size() === 0) t += "#d池中暂无其它外观#k\r\n";
    t += "\r\n#L999#返回#l";
    cm.sendSimple(t);
}

function handleSub(selection) {
    if (selection === 999) {
        status = -1;
        action(1, 0, 0);
        return;
    }
    selIndex = selection;
    var s = svc();
    var p = cm.getPlayer();

    if (menuMode === "2") {
        var town = TOWNS[selection];
        if (!town) { cm.dispose(); return; }
        var pr = regionProgress(town);
        if (s.hasRegionInjected(p, town.id)) {
            cm.sendOk(town.name + " 已注入过。"); cm.dispose(); return;
        }
        if (!pr.full) {
            cm.sendOk(town.name + " 未集齐（" + pr.done + "/" + pr.total + "），每种卡需满5张。"); cm.dispose(); return;
        }
        cm.sendYesNo("确认注入 #b" + town.name + "#k？\r\n获得：四维+5，攻击+2，魔力+2");
    } else if (menuMode === "3") {
        var e = FIELD_ELITES[selection];
        if (!e) { cm.dispose(); return; }
        if (s.hasEliteInjected(p, e.id)) {
            cm.sendOk(e.name + " 已注入过。"); cm.dispose(); return;
        }
        if (!cardDone(e.id)) {
            cm.sendOk(e.name + " 卡片不足（" + getCardLv(e.id) + "/5）。"); cm.dispose(); return;
        }
        cm.sendYesNo("确认注入野外Boss #b" + e.name + "#k？\r\n获得：四维+1");
    } else if (menuMode === "4") {
        var x = EXPEDITION_BOSSES[selection];
        if (!x) { cm.dispose(); return; }
        if (s.hasExpedInjected(p, x.id)) {
            cm.sendOk(x.name + " 已注入过。"); cm.dispose(); return;
        }
        if (!cardDone(x.id)) {
            cm.sendOk(x.name + " 卡片不足（" + getCardLv(x.id) + "/5）。"); cm.dispose(); return;
        }
        cm.sendYesNo("确认注入远征Boss #b" + x.name + "#k？\r\n获得：四维+2，攻击+1，魔力+1");
    } else if (menuMode === "5") {
        if (selection < 0 || selection >= bagMedals.length) { cm.dispose(); return; }
        var mid = bagMedals[selection];
        cm.sendYesNo("确认将 #i" + mid + "# #z" + mid + "# 注入勋章池？\r\n#r该勋章会从背包消失。#k");
    } else if (menuMode === "6") {
        var target = (selection === 1000) ? BASE : svc().getPool(p).get(selection);
        if (selection !== 1000 && (selection < 0 || selection >= svc().getPool(p).size())) {
            cm.dispose(); return;
        }
        selIndex = selection === 1000 ? -1 : selection;
        var tid = selection === 1000 ? BASE : svc().getPool(p).get(selection);
        cm.sendYesNo("确认幻化为 #i" + tid + "#？\r\n消耗 #r" + ILLUSION_COST_WAN + "W#k 金币。");
    } else {
        cm.dispose();
    }
}

function handleConfirm(selection) {
    // sendYesNo: mode already 1 means yes
    var s = svc();
    var p = cm.getPlayer();
    var r;

    if (menuMode === "2") {
        r = s.injectRegion(p, TOWNS[selIndex].id);
        cm.sendOk(r === "OK" ? ("#g注入成功！#k\r\n" + s.describeStats(p)) : ("#r" + r + "#k"));
    } else if (menuMode === "3") {
        r = s.injectElite(p, FIELD_ELITES[selIndex].id);
        cm.sendOk(r === "OK" ? ("#g注入成功！#k\r\n" + s.describeStats(p)) : ("#r" + r + "#k"));
    } else if (menuMode === "4") {
        r = s.injectExped(p, EXPEDITION_BOSSES[selIndex].id);
        cm.sendOk(r === "OK" ? ("#g注入成功！#k\r\n" + s.describeStats(p)) : ("#r" + r + "#k"));
    } else if (menuMode === "5") {
        r = s.addMedalToPool(p, bagMedals[selIndex]);
        cm.sendOk(r === "OK" ? "#g已注入勋章池！#k" : ("#r" + r + "#k"));
    } else if (menuMode === "6") {
        var tid = (selIndex < 0) ? BASE : s.getPool(p).get(selIndex);
        r = s.illusion(p, tid);
        cm.sendOk(r === "OK" ? ("#g幻化成功！#k\r\n" + s.describeStats(p)) : ("#r" + r + "#k"));
    }
    cm.dispose();
}

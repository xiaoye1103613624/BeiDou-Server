/**
 * 怪物卡片收集系统（城镇 / 野外精英 / 远征Boss）
 *
 * - 按城镇展示普通怪卡片进度，集齐该城镇可领一次奖励
 * - 野外精英、远征 Boss 独立列表与独立总奖励
 * - 进度读取 MonsterBook（不查背包）
 * - 城镇/大区奖励沿用老脚本 OneTimeLog：怪怪卡片-{城镇名}
 * - 不依赖 MonsterCardCollectionService
 *
 * 入口：收集系统 → 卡片收集（xy/collect/卡片收集）
 */

var SEL_BACK = 0;
var EXT_ELITE = "卡片收集_野外精英";
var EXT_EXPED = "卡片收集_远征Boss";

var currentTownId = "";
var currentMode = ""; // town | elite | exped

// ==================== 野外精英（从城镇列表拆出） ====================
var FIELD_ELITES = [
    { id: 2388006, need: 5 }, // 蘑菇王
    { id: 2388002, need: 5 }, // 浮士德
    { id: 2388007, need: 5 }, // 鳄鱼王
    { id: 2388044, need: 5 }, // 大王蜈蚣
    { id: 2388025, need: 5 }, // 树妖王
    { id: 2388008, need: 5 }, // 僵尸蘑菇王
    { id: 2388026, need: 5 }, // 蝙蝠怪
    { id: 2388015, need: 5 }, // 艾利杰
    { id: 2388012, need: 1 }, // 远古精灵（原需求1）
    { id: 2388016, need: 5 }, // 驮狼雪人
    { id: 2388052, need: 5 }, // 巨型蜈蚣
    { id: 2388010, need: 5 }, // 肯德熊
    { id: 2388013, need: 5 }, // 妖怪绅士
    { id: 2388009, need: 5 }, // 九尾狐
    { id: 2388031, need: 5 }  // 朱诺
];

// ==================== 远征 / 副本 Boss ====================
var EXPEDITION_BOSSES = [
    { id: 2388023, need: 5 }, // 扎昆
    { id: 2388022, need: 3 }, // 帕普拉图斯
    { id: 2388020, need: 3 }, // 皮亚奴斯
    { id: 2388018, need: 5 }, // 火焰龙
    { id: 2388019, need: 5 }, // 天鹰
    { id: 2388033, need: 5 }, // 大海兽
    { id: 2388024, need: 3 }  // 暗黑龙王
];

/**
 * 城镇配置：cards 已剔除野外精英与远征卡，避免与独立线重复要求。
 * reward 与老嘉年华一致；logKey 沿用 OneTimeLog。
 */
var TOWNS = [
    {
        id: "henesys", name: "射手村", logKey: "怪怪卡片-射手村", continent: "victoria",
        cards: [
            { id: 2380000, need: 5 }, { id: 2380001, need: 5 }, { id: 2380004, need: 5 },
            { id: 2380002, need: 5 }, { id: 2380007, need: 5 }, { id: 2380006, need: 5 },
            { id: 2380009, need: 5 }, { id: 2381002, need: 5 }, { id: 2383030, need: 5 },
            { id: 2382053, need: 5 }
        ],
        reward: { ap: 20, nx: 3000, exp: 100000, items: [[2022519, 3], [2022509, 5]] }
    },
    {
        id: "ellinia", name: "魔法密林", logKey: "怪怪卡片-魔法密林", continent: "victoria",
        cards: [
            { id: 2380005, need: 5 }, { id: 2380011, need: 5 }, { id: 2381007, need: 5 },
            { id: 2382018, need: 5 }, { id: 2382029, need: 5 }, { id: 2382040, need: 5 },
            { id: 2383029, need: 5 }
        ],
        reward: { ap: 20, nx: 3000, exp: 500000, items: [[2022519, 3], [2022509, 5]] }
    },
    {
        id: "perion", name: "废弃都市", logKey: "怪怪卡片-废弃都市", continent: "victoria",
        cards: [
            { id: 2380010, need: 5 }, { id: 2380012, need: 5 }, { id: 2381003, need: 5 },
            { id: 2381006, need: 5 }, { id: 2382002, need: 5 }, { id: 2383019, need: 5 },
            { id: 2382019, need: 5 }, { id: 2383008, need: 5 }
        ],
        reward: { ap: 20, nx: 3000, exp: 500000, items: [[2022519, 3], [2022509, 5]] }
    },
    {
        id: "shanghai", name: "上海外滩", logKey: "怪怪卡片-上海外滩", continent: "victoria",
        cards: [
            { id: 2381044, need: 5 }, { id: 2381045, need: 5 }, { id: 2381046, need: 5 },
            { id: 2381047, need: 5 }, { id: 2382077, need: 5 }, { id: 2382078, need: 5 },
            { id: 2382079, need: 5 }, { id: 2382080, need: 5 }
        ],
        reward: { ap: 20, nx: 3000, exp: 500000, items: [[2022519, 3], [2022509, 5]] }
    },
    {
        id: "sleepy", name: "勇士部落", logKey: "怪怪卡片-勇士部落", continent: "victoria",
        cards: [
            { id: 2381000, need: 5 }, { id: 2381008, need: 5 }, { id: 2381022, need: 5 },
            { id: 2382003, need: 5 }, { id: 2381001, need: 5 }, { id: 2381014, need: 5 },
            { id: 2381018, need: 5 }, { id: 2382065, need: 5 }, { id: 2382069, need: 5 },
            { id: 2382063, need: 5 }, { id: 2383005, need: 5 }, { id: 2383036, need: 5 },
            { id: 2380003, need: 5 }, { id: 2384003, need: 5 }, { id: 2384029, need: 5 }
        ],
        reward: { ap: 20, nx: 3000, exp: 500000, items: [[2022519, 3], [2022509, 5]] }
    },
    {
        id: "anttunnel", name: "林中之城", logKey: "怪怪卡片-林中之城", continent: "victoria",
        cards: [
            { id: 2381016, need: 5 }, { id: 2381024, need: 5 }, { id: 2382039, need: 5 },
            { id: 2383012, need: 5 }, { id: 2384006, need: 5 }, { id: 2384015, need: 5 },
            { id: 2384001, need: 5 }, { id: 2383039, need: 5 }, { id: 2383043, need: 5 },
            { id: 2384020, need: 5 }, { id: 2384036, need: 5 }
        ],
        reward: { ap: 20, nx: 3000, exp: 500000, items: [[2022519, 3], [2022509, 5]] }
    },
    {
        id: "orbis", name: "天空之城", logKey: "怪怪卡片-天空之城", continent: "ossyria",
        cards: [
            { id: 2383021, need: 5 }, { id: 2381012, need: 5 }, { id: 2383003, need: 5 },
            { id: 2382052, need: 5 }, { id: 2383022, need: 5 }, { id: 2383023, need: 5 },
            { id: 2382020, need: 5 }, { id: 2382064, need: 5 }, { id: 2383020, need: 5 },
            { id: 2384030, need: 5 }, { id: 2381032, need: 5 }, { id: 2381037, need: 5 },
            { id: 2381038, need: 5 }
        ],
        reward: { ap: 35, nx: 3000, exp: 0, items: [[2022699, 1]] }
    },
    {
        id: "elnath", name: "冰峰雪域", logKey: "怪怪卡片-冰峰雪域", continent: "ossyria",
        cards: [
            { id: 2383013, need: 5 }, { id: 2384009, need: 5 }, { id: 2383031, need: 5 },
            { id: 2383040, need: 5 }, { id: 2383045, need: 5 }, { id: 2385004, need: 5 },
            { id: 2384026, need: 5 }, { id: 2383038, need: 5 }, { id: 2383037, need: 5 },
            { id: 2382047, need: 5 }, { id: 2382023, need: 5 }, { id: 2383017, need: 5 },
            { id: 2382021, need: 5 }, { id: 2382006, need: 5 }, { id: 2385021, need: 5 },
            { id: 2384035, need: 5 }, { id: 2385006, need: 5 }
        ],
        reward: { ap: 40, nx: 3000, exp: 0, items: [[2022699, 1]] }
    },
    {
        id: "muLung", name: "武陵", logKey: "怪怪卡片-武陵", continent: "ossyria",
        cards: [
            { id: 2383015, need: 5 }, { id: 2383018, need: 5 }, { id: 2382045, need: 5 },
            { id: 2382051, need: 5 }, { id: 2382060, need: 5 }, { id: 2382070, need: 5 },
            { id: 2382071, need: 5 }, { id: 2384002, need: 5 }, { id: 2383041, need: 5 },
            { id: 2383047, need: 5 }, { id: 2383032, need: 5 }, { id: 2384013, need: 5 }
        ],
        reward: { ap: 30, nx: 3000, exp: 0, items: [[2022699, 1]] }
    },
    {
        id: "herb", name: "百草堂", logKey: "怪怪卡片-百草堂", continent: "ossyria",
        cards: [
            { id: 2384017, need: 5 }, { id: 2384025, need: 5 }, { id: 2383006, need: 5 },
            { id: 2383010, need: 5 }, { id: 2383025, need: 5 }, { id: 2383027, need: 5 },
            { id: 2384008, need: 5 }, { id: 2383035, need: 5 }
        ],
        reward: { ap: 15, nx: 3000, exp: 0, items: [[2022699, 1]] }
    },
    {
        id: "ludi", name: "玩具城", logKey: "怪怪卡片-玩具城", continent: "ossyria",
        cards: [
            { id: 2384014, need: 5 }, { id: 2382048, need: 5 }, { id: 2382062, need: 5 },
            { id: 2382049, need: 5 }, { id: 2383002, need: 5 }, { id: 2383004, need: 5 },
            { id: 2382034, need: 5 }, { id: 2382037, need: 5 }, { id: 2381034, need: 5 },
            { id: 2382054, need: 5 }, { id: 2382066, need: 5 }, { id: 2382004, need: 5 },
            { id: 2382015, need: 5 }, { id: 2382038, need: 5 }, { id: 2382025, need: 5 },
            { id: 2382005, need: 5 }, { id: 2382016, need: 5 }, { id: 2381011, need: 5 },
            { id: 2381027, need: 5 }, { id: 2382026, need: 5 }, { id: 2382000, need: 5 },
            { id: 2382001, need: 5 }, { id: 2382031, need: 5 }, { id: 2382022, need: 5 },
            { id: 2382033, need: 5 }, { id: 2384019, need: 5 }, { id: 2384032, need: 5 },
            { id: 2385010, need: 5 }, { id: 2385015, need: 5 }, { id: 2385012, need: 5 },
            { id: 2385020, need: 5 }, { id: 2386002, need: 5 }, { id: 2386009, need: 5 },
            { id: 2386004, need: 5 }, { id: 2386010, need: 5 }, { id: 2387000, need: 5 },
            { id: 2387001, need: 5 }
        ],
        reward: { ap: 50, nx: 3000, exp: 0, items: [[2022699, 1]] }
    },
    {
        id: "aqua", name: "海底世界", logKey: "怪怪卡片-海底世界", continent: "ossyria",
        cards: [
            { id: 2381009, need: 5 }, { id: 2381026, need: 5 }, { id: 2382056, need: 5 },
            { id: 2382059, need: 5 }, { id: 2381035, need: 5 }, { id: 2385013, need: 5 },
            { id: 2386000, need: 5 }, { id: 2386003, need: 5 }, { id: 2386007, need: 5 },
            { id: 2386012, need: 5 }, { id: 2381017, need: 5 }, { id: 2382047, need: 5 },
            { id: 2382027, need: 5 }, { id: 2382035, need: 5 }, { id: 2382043, need: 5 },
            { id: 2382044, need: 5 }, { id: 2382007, need: 5 }, { id: 2381029, need: 5 },
            { id: 2381021, need: 5 }, { id: 2381013, need: 5 }, { id: 2386014, need: 5 }
        ],
        reward: { ap: 35, nx: 3000, exp: 0, items: [[2022699, 1]] }
    },
    {
        id: "korean", name: "童话村", logKey: "怪怪卡片-童话村", continent: "ossyria",
        cards: [
            { id: 2382068, need: 5 }, { id: 2383014, need: 5 }, { id: 2383024, need: 5 },
            { id: 2383034, need: 5 }, { id: 2384022, need: 5 }, { id: 2384023, need: 5 },
            { id: 2384021, need: 5 }
        ],
        reward: { ap: 15, nx: 3000, exp: 0, items: [[2022699, 1]] }
    },
    {
        id: "omega", name: "地球防御本部", logKey: "怪怪卡片-地球本部", continent: "ossyria",
        cards: [
            { id: 2382050, need: 5 }, { id: 2382061, need: 5 }, { id: 2383000, need: 5 },
            { id: 2383026, need: 5 }, { id: 2382042, need: 5 }, { id: 2382055, need: 5 },
            { id: 2382067, need: 5 }, { id: 2383011, need: 5 }
        ],
        reward: { ap: 15, nx: 3000, exp: 0, items: [[2022699, 1]] }
    },
    {
        id: "leafre", name: "神木村", logKey: "怪怪卡片-神木村", continent: "ossyria",
        cards: [
            { id: 2384024, need: 5 }, { id: 2384033, need: 5 }, { id: 2384027, need: 5 },
            { id: 2385001, need: 5 }, { id: 2384028, need: 5 }, { id: 2385002, need: 5 },
            { id: 2385005, need: 5 }, { id: 2385007, need: 5 }, { id: 2385011, need: 5 },
            { id: 2385014, need: 5 }, { id: 2385019, need: 5 }, { id: 2385016, need: 5 },
            { id: 2385017, need: 5 }, { id: 2385018, need: 5 }, { id: 2385022, need: 5 },
            { id: 2386001, need: 5 }, { id: 2386005, need: 5 }, { id: 2386006, need: 5 },
            { id: 2386008, need: 5 }, { id: 2386013, need: 5 }, { id: 2386015, need: 5 },
            { id: 2386016, need: 5 }, { id: 2386011, need: 5 }, { id: 2386017, need: 5 },
            { id: 2387004, need: 5 }, { id: 2387003, need: 5 }, { id: 2387002, need: 5 }
        ],
        reward: { ap: 40, nx: 3000, exp: 0, items: [[2022699, 1]] }
    }
];

var TOWN_BY_ID = {};
(function initTowns() {
    for (var i = 0; i < TOWNS.length; i++) TOWN_BY_ID[TOWNS[i].id] = TOWNS[i];
})();

var VICTORIA_TOWNS = ["henesys", "ellinia", "perion", "shanghai", "sleepy", "anttunnel"];
var OSSYRIA_TOWNS = ["orbis", "elnath", "muLung", "herb", "ludi", "aqua", "korean", "omega", "leafre"];

var ELITE_SET_REWARD = { ap: 50, nx: 5000, exp: 0, items: [[2022519, 5], [2022699, 2]] };
var EXPED_SET_REWARD = { ap: 80, nx: 8000, exp: 0, items: [[2022519, 8], [2022699, 3]] };
var VICTORIA_REWARD = { ap: 80, nx: 0, exp: 0, items: [[2011104, 2000], [2011105, 2000], [2022519, 5]] };
var OSSYRIA_REWARD = { ap: 45, nx: 0, exp: 0, items: [[2022519, 5], [2022699, 3]] };

// ==================== 入口 ====================
function start() {
    levelMain();
}

function getCardLevel(cardId) {
    var cards = cm.getPlayer().getMonsterBook().getCards();
    var count = cards.get(cardId);
    return count != null ? count : 0;
}

function isDone(entry) {
    return getCardLevel(entry.id) >= entry.need;
}

function countDone(list) {
    var n = 0;
    for (var i = 0; i < list.length; i++) {
        if (isDone(list[i])) n++;
    }
    return n;
}

function allDone(list) {
    return countDone(list) >= list.length;
}

function hasClaimed(logKey) {
    return cm.getPlayer().getOneTimeLog(logKey) >= 1;
}

function progressText(list) {
    return "#b" + countDone(list) + "#k/#r" + list.length + "#k";
}

function makeBar(cur, need) {
    var filled = Math.min(cur, need);
    var s = "";
    for (var i = 0; i < need; i++) s += (i < filled ? "■" : "□");
    return s;
}

function checkInvSpace() {
    for (var i = 1; i <= 5; i++) {
        if (cm.getInventory(i).isFull(0)) {
            cm.sendOk("#b请保证背包各栏至少有1个空格，否则无法领取。");
            cm.dispose();
            return false;
        }
    }
    return true;
}

function grantReward(reward, announceTitle, announceMsg) {
    if (reward.ap > 0) cm.getPlayer().gainAp(reward.ap);
    if (reward.nx > 0) cm.gainNX(reward.nx);
    if (reward.exp > 0) cm.gainExp(reward.exp);
    if (reward.items) {
        for (var i = 0; i < reward.items.length; i++) {
            cm.gainItem(reward.items[i][0], reward.items[i][1]);
        }
    }
    try {
        cm.喇叭(2, "【" + announceTitle + "】 恭喜玩家:【" + cm.getPlayer().getName() + "】 : " + announceMsg);
        cm.getMap().startMapEffect("【" + announceTitle + "】 恭喜玩家： " + cm.getName() + " " + announceMsg, 5121001);
    } catch (e) {}
}

function formatReward(reward) {
    var t = "";
    if (reward.ap > 0) t += "能力值 x " + reward.ap + "\r\n";
    if (reward.nx > 0) t += "点券 x " + reward.nx + "\r\n";
    if (reward.exp > 0) t += "经验 x " + reward.exp + "\r\n";
    if (reward.items) {
        for (var i = 0; i < reward.items.length; i++) {
            t += "#v" + reward.items[i][0] + "##z" + reward.items[i][0] + "# x" + reward.items[i][1] + "\r\n";
        }
    }
    return t;
}

function renderCardLines(list) {
    var text = "";
    for (var i = 0; i < list.length; i++) {
        var e = list[i];
        var lv = getCardLevel(e.id);
        var ok = lv >= e.need;
        text += "#v" + e.id + "# #t" + e.id + "#  "
            + makeBar(lv, e.need) + " #r" + lv + "#k/#b" + e.need + "#k"
            + (ok ? "  #g[已满]#k" : "  #d未完成#k") + "\r\n";
    }
    return text;
}

// ==================== Level: 主菜单 ====================
function levelMain() {
    var text = "\t\t#e#r< 怪物卡片收集中心 >#k#n\r\n\r\n";
    text += "#d按城镇集齐普通怪卡片可领奖；野外精英与远征 Boss 单独统计。#k\r\n\r\n";
    text += "#L1##b城镇卡片收集#k  （地区普通怪）#l\r\n";
    text += "#L2##b野外精英 Boss#k  进度：" + progressText(FIELD_ELITES)
        + (hasClaimed(EXT_ELITE) ? "  #g[已领总奖]#k" : "") + "#l\r\n";
    text += "#L3##b远征 Boss 卡片#k  进度：" + progressText(EXPEDITION_BOSSES)
        + (hasClaimed(EXT_EXPED) ? "  #g[已领总奖]#k" : "") + "#l\r\n\r\n";
    text += "#L4##r领取：金银岛区域总奖励#k#l\r\n";
    text += "#L5##r领取：神秘岛区域总奖励#k#l\r\n\r\n";
    text += "#L" + SEL_BACK + "##g返回收集系统#k#l\r\n";
    cm.sendNextSelectLevel("HandleMain", text);
}

function levelHandleMain(selection) {
    if (selection === SEL_BACK) {
        cm.dispose();
        cm.openNpc(9900001, "xy/portal/收集系统");
        return;
    }
    if (selection === 1) {
        currentMode = "town";
        levelTownList();
        return;
    }
    if (selection === 2) {
        currentMode = "elite";
        levelBossSet("elite");
        return;
    }
    if (selection === 3) {
        currentMode = "exped";
        levelBossSet("exped");
        return;
    }
    if (selection === 4) {
        claimContinent("victoria");
        return;
    }
    if (selection === 5) {
        claimContinent("ossyria");
        return;
    }
    levelMain();
}

// ==================== 城镇列表 ====================
function levelTownList() {
    var text = "\t\t#e#r< 城镇卡片收集 >#k#n\r\n\r\n";
    text += "#d集齐该城镇列表中全部卡片后可领取城镇奖励（每角色一次）。#k\r\n";
    text += "#d精英/远征 Boss 已拆到独立栏目，无需在本列表收集。#k\r\n\r\n";
    for (var i = 0; i < TOWNS.length; i++) {
        var t = TOWNS[i];
        var done = countDone(t.cards);
        var claimed = hasClaimed(t.logKey);
        text += "#L" + (i + 1) + "##b" + t.name + "#k  进度：" + done + "/" + t.cards.length;
        if (claimed) text += "  #g[已领奖]#k";
        else if (done >= t.cards.length) text += "  #r[可领奖]#k";
        text += "#l\r\n";
    }
    text += "\r\n#L" + SEL_BACK + "##g返回上级#k#l\r\n";
    cm.sendNextSelectLevel("HandleTownList", text);
}

function levelHandleTownList(selection) {
    if (selection === SEL_BACK) {
        levelMain();
        return;
    }
    var idx = selection - 1;
    if (idx < 0 || idx >= TOWNS.length) {
        levelTownList();
        return;
    }
    currentTownId = TOWNS[idx].id;
    levelTownDetail();
}

function levelTownDetail() {
    var town = TOWN_BY_ID[currentTownId];
    if (!town) {
        levelTownList();
        return;
    }
    var text = "\t\t#e#r< " + town.name + " >#k#n\r\n\r\n";
    text += "进度：" + progressText(town.cards) + "\r\n\r\n";
    text += renderCardLines(town.cards);
    text += "\r\n━━━ 集齐奖励 ━━━\r\n" + formatReward(town.reward) + "\r\n";
    if (hasClaimed(town.logKey)) {
        text += "#g你已领取过该城镇奖励。#k\r\n";
    } else if (allDone(town.cards)) {
        text += "#L1##r领取城镇奖励#k#l\r\n";
    } else {
        text += "#d尚未集齐，无法领取。#k\r\n";
    }
    text += "\r\n#L" + SEL_BACK + "##g返回城镇列表#k#l\r\n";
    cm.sendNextSelectLevel("HandleTownDetail", text);
}

function levelHandleTownDetail(selection) {
    if (selection === SEL_BACK) {
        levelTownList();
        return;
    }
    if (selection === 1) {
        claimTown(currentTownId);
        return;
    }
    levelTownDetail();
}

function claimTown(townId) {
    var town = TOWN_BY_ID[townId];
    if (!town) {
        levelTownList();
        return;
    }
    if (!checkInvSpace()) return;
    if (!allDone(town.cards)) {
        cm.sendOkLevel("TownDetail", "#b卡片种类或数量不足，无法领取。");
        return;
    }
    if (hasClaimed(town.logKey)) {
        cm.sendOkLevel("TownDetail", "你已经完成过该地区卡片活动，一个角色只能领取一次！");
        return;
    }
    cm.getPlayer().setOneTimeLog(town.logKey);
    grantReward(town.reward, "怪怪卡片-" + town.name, "完成了" + town.name + "地区的怪怪卡任务。");
    cm.sendOkLevel("TownDetail", "恭喜你领取成功！\r\n\r\n" + formatReward(town.reward));
}

// ==================== 精英 / 远征 ====================
function levelBossSet(mode) {
    if (mode === "elite" || mode === "exped") {
        currentMode = mode;
    }
    var isElite = currentMode === "elite";
    var list = isElite ? FIELD_ELITES : EXPEDITION_BOSSES;
    var title = isElite ? "野外精英 Boss 卡片" : "远征 Boss 卡片";
    var logKey = isElite ? EXT_ELITE : EXT_EXPED;
    var reward = isElite ? ELITE_SET_REWARD : EXPED_SET_REWARD;

    var text = "\t\t#e#r< " + title + " >#k#n\r\n\r\n";
    text += "进度：" + progressText(list) + "\r\n\r\n";
    text += renderCardLines(list);
    text += "\r\n━━━ 全部集齐奖励 ━━━\r\n" + formatReward(reward) + "\r\n";
    if (hasClaimed(logKey)) {
        text += "#g你已领取过该项总奖励。#k\r\n";
    } else if (allDone(list)) {
        text += "#L1##r领取总奖励#k#l\r\n";
    } else {
        text += "#d尚未全部集齐，无法领取总奖励。#k\r\n";
    }
    text += "\r\n#L" + SEL_BACK + "##g返回上级#k#l\r\n";
    cm.sendNextSelectLevel("HandleBossSet", text);
}

function levelHandleBossSet(selection) {
    if (selection === SEL_BACK) {
        levelMain();
        return;
    }
    if (selection === 1) {
        claimBossSet(currentMode);
        return;
    }
    levelBossSet(currentMode);
}

function claimBossSet(mode) {
    var isElite = mode === "elite";
    var list = isElite ? FIELD_ELITES : EXPEDITION_BOSSES;
    var logKey = isElite ? EXT_ELITE : EXT_EXPED;
    var reward = isElite ? ELITE_SET_REWARD : EXPED_SET_REWARD;
    var title = isElite ? "野外精英卡片" : "远征Boss卡片";

    if (!checkInvSpace()) return;
    if (!allDone(list)) {
        cm.sendOkLevel("BossSet", "#b卡片尚未全部集齐，无法领取。");
        return;
    }
    if (hasClaimed(logKey)) {
        cm.sendOkLevel("BossSet", "你已经领取过该项总奖励，一个角色只能一次！");
        return;
    }
    cm.getPlayer().setOneTimeLog(logKey);
    grantReward(reward, title, "领取了" + title + "总奖励。");
    cm.sendOkLevel("BossSet", "恭喜你领取成功！\r\n\r\n" + formatReward(reward));
}

// ==================== 大区总奖 ====================
function claimContinent(kind) {
    if (!checkInvSpace()) return;
    var ids = kind === "victoria" ? VICTORIA_TOWNS : OSSYRIA_TOWNS;
    var logKey = kind === "victoria" ? "怪怪卡片-金银岛区域总奖励" : "怪怪卡片-神秘岛区域总奖励";
    var reward = kind === "victoria" ? VICTORIA_REWARD : OSSYRIA_REWARD;
    var label = kind === "victoria" ? "金银岛" : "神秘岛";

    for (var i = 0; i < ids.length; i++) {
        var town = TOWN_BY_ID[ids[i]];
        if (!hasClaimed(town.logKey)) {
            cm.sendOkLevel("Main", "你还有城镇未完成：" + town.name + "，无法领取" + label + "总奖励。");
            return;
        }
    }
    if (hasClaimed(logKey)) {
        cm.sendOkLevel("Main", "你已经领取过" + label + "区域总奖励，一个角色只能一次！");
        return;
    }
    cm.getPlayer().setOneTimeLog(logKey);
    grantReward(reward, "怪怪卡片-" + label + "区域", "领取了怪卡片-" + label + "区域总奖励。");
    cm.sendOkLevel("Main", "恭喜你领取成功！\r\n\r\n" + formatReward(reward));
}

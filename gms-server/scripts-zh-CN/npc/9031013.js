// 匠人街 · 戒指工作台 9031013 —— 材料兑换（矿石 / 水晶 / 锻造石互换）
// 排版约定：仅展示物品图标 + 数量，不显示名称；鼠标悬停图标可查看物品信息。
var status = -1;

var OpLogManager = Java.type('org.gms.log.OpLogManager');

var cat = 0;   // 1 矿石兑换  2 水晶兑换  3 锻造石互换
var sel = 0;   // 一级条目索引
var sel2 = 0;  // 二级条目索引（锻造石目标 / 水晶材料方式）
var qty = 1;   // 批量数量

// ============ 矿石兑换数据：产物 -> 材料列表[ [物品ID, 数量], ... ]，手续费单价/个 ============
var ORE_EXCHANGES = [
    { // 0 星石：9 种宝石母矿 ×10
        product: 4021009, cost: 20000, tip: "宝石母矿",
        mats: [[4020000, 10], [4020001, 10], [4020002, 10], [4020003, 10], [4020004, 10],
            [4020005, 10], [4020006, 10], [4020007, 10], [4020008, 10]]
    },
    { // 1 月石：7 种矿物母矿 ×10
        product: 4011007, cost: 20000, tip: "矿物母矿",
        mats: [[4010000, 10], [4010001, 10], [4010002, 10], [4010003, 10], [4010004, 10],
            [4010005, 10], [4010006, 10]]
    },
    { // 2 粉末 -> 星石：8 种魔法粉末 ×15
        product: 4021009, cost: 30000, tip: "魔法粉末",
        mats: [[4007000, 15], [4007001, 15], [4007002, 15], [4007003, 15], [4007004, 15],
            [4007005, 15], [4007006, 15], [4007007, 15]]
    },
    { // 3 粉末 -> 月石：8 种魔法粉末 ×15
        product: 4011007, cost: 30000, tip: "魔法粉末",
        mats: [[4007000, 15], [4007001, 15], [4007002, 15], [4007003, 15], [4007004, 15],
            [4007005, 15], [4007006, 15], [4007007, 15]]
    }
];

// ============ 水晶兑换：5 种水晶（对应母矿 ×10 或 另外四种母矿 ×20），手续费 2W/个 ============
var CRYSTAL_COST = 20000;
var CRYSTAL_EXCHANGES = [
    { product: 4005000, main: 4004000, others: [4004001, 4004002, 4004003, 4004004] },
    { product: 4005002, main: 4004002, others: [4004000, 4004001, 4004003, 4004004] },
    { product: 4005001, main: 4004001, others: [4004000, 4004002, 4004003, 4004004] },
    { product: 4005003, main: 4004003, others: [4004000, 4004001, 4004002, 4004004] },
    { product: 4005004, main: 4004004, others: [4004000, 4004001, 4004002, 4004003] }
];

// ============ 锻造石互换数据表：消耗 src 的 in 个 => 获得 tgt 的 out 个，手续费单价 × 获取数 ============
var FORGE_EXCHANGES = [
    { // 圣者之石 4032171
        src: 4032171,
        targets: [
            { tgt: 4032169, in: 1, out: 1, fee: 10000 },
            { tgt: 4032170, in: 2, out: 1, fee: 10000 }
        ]
    },
    { // 勇者之石 4032169
        src: 4032169,
        targets: [
            { tgt: 4032171, in: 1, out: 1, fee: 10000 },
            { tgt: 4032170, in: 2, out: 1, fee: 10000 }
        ]
    },
    { // 贤者之石 4032170
        src: 4032170,
        targets: [
            { tgt: 4032169, in: 1, out: 2, fee: 20000 },
            { tgt: 4032171, in: 1, out: 2, fee: 20000 }
        ]
    }
];

// ------------------- 工具函数 -------------------
function wan(n) {
    return n % 10000 === 0 ? (n / 10000) + "W" : n + "";
}

function ic(id) {
    return "#v" + id + "#";
}

function ratioText(t) {
    return t.in + ":" + t.out;
}

// 材料清单排版（图标 × 数量，每行一条）
function buildMatsText(mats, mult) {
    var s = "";
    for (var i = 0; i < mats.length; i++) {
        s += "  " + ic(mats[i][0]) + " × #r" + (mats[i][1] * mult) + "#k\r\n";
    }
    return s;
}

// ------------------- 入口 -------------------
function start() {
    status = -1;
    cat = 0;
    sel = 0;
    sel2 = 0;
    qty = 1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1) {
        cm.dispose();
        return;
    }
    if (mode === 0) {
        status--;
        if (status < 0) { cm.dispose(); return; }
    } else {
        status++;
    }

    if (status === 0) {
        cat = 0;
        cm.sendSimple(
            "#e匠人街 · 材料兑换#n#b\r\n\r\n" +
            "#L1#  ○ 矿石兑换#l\r\n" +
            "#L2#  ○ 水晶兑换#l\r\n" +
            "#L3#  ○ 锻造石互换#l\r\n\r\n" +
            "#k全部支持输入批量数量"
        );
        return;
    }

    if (status === 1 && cat === 0) {
        if (selection < 1 || selection > 3) { cm.dispose(); return; }
        cat = selection;
    }

    if (cat === 1) { stepOre(status, selection); return; }
    if (cat === 2) { stepCrystal(status, selection); return; }
    if (cat === 3) { stepForge(status, selection); return; }

    cm.dispose();
}

// ------------------- 矿石兑换 -------------------
function stepOre(st, selection) {
    if (st === 1) { // 选择产物
        var m = "";
        for (var x = 0; x < ORE_EXCHANGES.length; x++) {
            m += "#L" + x + "# " + ic(ORE_EXCHANGES[x].product) + " ×1  （" + ORE_EXCHANGES[x].tip + "）#l\r\n";
        }
        cm.sendSimple("选择要制作的产物（图标悬停查看信息）：\r\n" + m);
    } else if (st === 2) { // 数量
        if (selection < 0 || selection >= ORE_EXCHANGES.length) { cm.dispose(); return; }
        sel = selection;
        var e = ORE_EXCHANGES[sel];
        cm.sendGetNumber(
            "制作 " + ic(e.product) + " ×1\r\n" +
            "请填写制作数量：\r\n" +
            "材料（每个）：\r\n" +
            buildMatsText(e.mats, 1) +
            "手续费：#r" + wan(e.cost) + "# /个",
            1, 1, 100
        );
    } else if (st === 3) { // 确认
        qty = selection;
        if (qty < 1) { cm.dispose(); return; }
        confirmExchange(ORE_EXCHANGES[sel], qty);
    } else if (st === 4) { // 执行
        doExchange(ORE_EXCHANGES[sel], qty);
        return;
    }
}

// ------------------- 水晶兑换 -------------------
function stepCrystal(st, selection) {
    if (st === 1) { // 选择水晶
        var s = "";
        for (var i = 0; i < CRYSTAL_EXCHANGES.length; i++) {
            s += "#L" + i + "# " + ic(CRYSTAL_EXCHANGES[i].product) + "#l\r\n";
        }
        cm.sendSimple("选择要兑换的水晶（悬停图标查看信息）：\r\n" + s);
    } else if (st === 2) { // 材料方式
        if (selection < 0 || selection >= CRYSTAL_EXCHANGES.length) { cm.dispose(); return; }
        sel = selection;
        var c = CRYSTAL_EXCHANGES[sel];
        cm.sendSimple(
            ic(c.product) + " 的材料方式：\r\n" +
            "#L0# 对应母矿 " + ic(c.main) + " ×#r10#k#l\r\n" +
            "#L1# 其它四种母矿 各 ×#r20#k#l\r\n\r\n" +
            "手续费：#r" + wan(CRYSTAL_COST) + "# /个"
        );
    } else if (st === 3) { // 数量
        if (selection < 0 || selection > 1) { cm.dispose(); return; }
        sel2 = selection;
        cm.sendGetNumber(
            "兑换 " + ic(CRYSTAL_EXCHANGES[sel].product) + " \r\n" +
            "材料方式：" + ((sel2 === 0) ? "对应母矿 ×10" : "其它四种母矿各 ×20") + "\r\n" +
            "请输入数量（手续费 " + wan(CRYSTAL_COST) + "/个）：",
            1, 1, 100
        );
    } else if (st === 4) { // 确认
        qty = selection;
        if (qty < 1) { cm.dispose(); return; }
        var mats;
        if (sel2 === 0) {
            mats = [[CRYSTAL_EXCHANGES[sel].main, 10]];
        } else {
            mats = [];
            for (var j = 0; j < CRYSTAL_EXCHANGES[sel].others.length; j++) {
                mats.push([CRYSTAL_EXCHANGES[sel].others[j], 20]);
            }
        }
        confirmExchange({ product: CRYSTAL_EXCHANGES[sel].product, cost: CRYSTAL_COST, mats: mats }, qty);
        return;
    } else if (st === 5) { // 执行
        var matArr;
        if (sel2 === 0) {
            matArr = [[CRYSTAL_EXCHANGES[sel].main, 10]];
        } else {
            matArr = [];
            for (var k = 0; k < CRYSTAL_EXCHANGES[sel].others.length; k++) {
                matArr.push([CRYSTAL_EXCHANGES[sel].others[k], 20]);
            }
        }
        doExchange({ product: CRYSTAL_EXCHANGES[sel].product, cost: CRYSTAL_COST, mats: matArr }, qty);
        return;
    }
}

// ------------------- 锻造石互换 -------------------
function stepForge(st, selection) {
    if (st === 1) { // 选择来源锻造石
        var s = "";
        for (var i = 0; i < FORGE_EXCHANGES.length; i++) {
            s += "#L" + i + "# " + ic(FORGE_EXCHANGES[i].src) + " (1:1 / 2:1)#l\r\n";
        }
        cm.sendSimple("选择要兑换的锻造石（图标悬停查看信息）：\r\n" + s);
    } else if (st === 2) { // 选择目标
        if (selection < 0 || selection >= FORGE_EXCHANGES.length) { cm.dispose(); return; }
        sel = selection;
        var src = FORGE_EXCHANGES[sel];
        var str = ic(src.src) + " 可兑换：\r\n";
        for (var j = 0; j < src.targets.length; j++) {
            var t = src.targets[j];
            str += "#L" + j + "#   → " + ic(t.tgt) + " (" + ratioText(t) + ", 手续费 " + wan(t.fee) + "/个)#l\r\n";
        }
        cm.sendSimple(str);
    } else if (st === 3) { // 数量
        if (selection < 0 || selection >= FORGE_EXCHANGES[sel].targets.length) { cm.dispose(); return; }
        sel2 = selection;
        var t2 = FORGE_EXCHANGES[sel].targets[sel2];
        cm.sendGetNumber(
            "消耗 " + ic(FORGE_EXCHANGES[sel].src) + " (" + ratioText(t2) + ")\r\n" +
            "请输入需要的 " + ic(t2.tgt) + " 数量：\r\n" +
            "手续费单价：#r" + wan(t2.fee) + "# 按获取数量计",
            1, 1, 100
        );
    } else if (st === 4) { // 校验并确认
        qty = selection;
        if (qty < 1) { cm.dispose(); return; }
        var t3 = FORGE_EXCHANGES[sel].targets[sel2];
        if ((qty * t3.in) % t3.out !== 0) {
            cm.sendOk("该比例下获取数量必须为 " + t3.out + " 的倍数，请重新操作。");
            cm.dispose();
            return;
        }
        confirmForge();
        return;
    } else if (st === 5) { // 执行
        doForge();
        return;
    }
}

// ------------------- 确认与执行公共部分 -------------------
function confirmExchange(e, qty2) {
    var meso = e.cost * qty2;
    cm.sendYesNo(
        "#e确认兑换#n\r\n\r\n" +
        "获得：" + ic(e.product) + " × #r" + qty2 + "#k\r\n\r\n" +
        "#b需要材料：\r\n" + buildMatsText(e.mats, qty2) +
        "手续费：#r" + wan(meso) + "#k\r\n\r\n是否确认？"
    );
}

// 通用执行：材料 + 手续费 -> 产物
function doExchange(e, qty2) {
    var totalFee = e.cost * qty2;
    if (!cm.canHold(e.product, qty2)) {
        cm.sendOk("背包空间不足，请清理后再来。");
        cm.dispose();
        return;
    }
    if (cm.getMeso() < totalFee) {
        cm.sendOk("手续费不足，需要 #r" + wan(totalFee) + "#k。");
        cm.dispose();
        return;
    }
    var missing = "";
    for (var i = 0; i < e.mats.length; i++) {
        if (!cm.haveItem(e.mats[i][0], e.mats[i][1] * qty2)) {
            missing += "  " + ic(e.mats[i][0]) + " × #r" + (e.mats[i][1] * qty2) + "#k\r\n";
        }
    }
    if (missing !== "") {
        cm.sendOk("材料不足，缺少：\r\n" + missing);
        cm.dispose();
        return;
    }
    for (var j = 0; j < e.mats.length; j++) {
        cm.gainItem(e.mats[j][0], -e.mats[j][1] * qty2);
    }
    cm.gainMeso(-totalFee);
    cm.gainItem(e.product, qty2);
    try {
        OpLogManager.recordExchange(cm.getPlayer(), e.product, qty2,
            "兑换 材料=" + JSON.stringify(e.mats) + " 手续费=" + totalFee);
    } catch (ex) { /* 日志失败不影响玩法 */ }
    cm.sendOk("兑换成功！已获得 " + ic(e.product) + " × #r" + qty2 + "#k。");
    cm.dispose();
}

function confirmForge() {
    var src = FORGE_EXCHANGES[sel];
    var t = src.targets[sel2];
    var need = qty * t.in / t.out;
    var fee = t.fee * qty;
    cm.sendYesNo(
        "#e确认互换#n\r\n\r\n" +
        "消耗 " + ic(src.src) + " × #r" + need + "#k\r\n" +
        "获得 " + ic(t.tgt) + " × #r" + qty + "#k\r\n" +
        "手续费：#r" + wan(fee) + "#k\r\n\r\n是否确认？"
    );
}

function doForge() {
    var src = FORGE_EXCHANGES[sel];
    var t = src.targets[sel2];
    var need = qty * t.in / t.out;
    var fee = t.fee * qty;
    if (!cm.canHold(t.tgt, qty)) {
        cm.sendOk("背包空间不足，请清理后再试。");
        cm.dispose();
        return;
    }
    if (cm.getMeso() < fee) {
        cm.sendOk("手续费不足，需要 #r" + wan(fee) + "#k。");
        cm.dispose();
        return;
    }
    if (!cm.haveItem(src.src, need)) {
        cm.sendOk("材料不足，缺少 " + ic(src.src) + " × #r" + need + "#k。");
        cm.dispose();
        return;
    }
    cm.gainItem(src.src, -need);
    cm.gainMeso(-fee);
    cm.gainItem(t.tgt, qty);
    try {
        OpLogManager.recordForge(cm.getPlayer(), t.tgt, qty,
            "互换 消耗=" + JSON.stringify([src.src, need]) + " 手续费=" + fee);
    } catch (ex) { /* 日志失败不影响玩法 */ }
    cm.sendOk("互换成功！已获得 " + ic(t.tgt) + " × #r" + qty + "#k。");
    cm.dispose();
}
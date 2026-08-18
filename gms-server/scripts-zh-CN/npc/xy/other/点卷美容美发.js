// 点卷美容美发（经典 sendStyle）
// 费用：每次 2000 点券 + 1000000 金币
// 入口：自由市场 9105006 菜单「点卷选款美容」

var CASH_NX = 1;
var NX_COST = 2000;
var MESO_COST = 1000000;

var status = 0;
var beauty = 0;
var hairnew = [];
var facenew = [];
var haircolor = [];
var colors = [];

// 高版本可用发型（含 4xxxx/5xxxx/6xxxx 一批；实际展示经 getCosmeticItem 过滤）
var mhair = [
    30000, 30010, 30020, 30030, 30040, 30050, 30060, 30070, 30080, 30090,
    30100, 30110, 30120, 30130, 30140, 30150, 30160, 30170, 30180, 30190,
    30200, 30210, 30220, 30230, 30240, 30250, 30260, 30270, 30280, 30290,
    30300, 30310, 30320, 30330, 30340, 30350, 30360, 30370, 30400, 30410,
    30420, 30440, 30450, 30460, 30470, 30480, 30490, 30510, 30520, 30530,
    30540, 30550, 30560, 30570, 30580, 30590, 30600, 30610, 30620, 30630,
    30640, 30650, 30660, 30670, 30680, 30690, 30700, 30710, 30720, 30730,
    30740, 30750, 30760, 30770, 30780, 30790, 30800, 30810, 30820, 30830,
    30840, 30860, 30870, 30880, 30890, 30900, 30910, 30920, 30930, 30940,
    30950, 30990, 33000, 33040, 33100,
    40000, 40010, 40020, 40030, 40040, 40050, 40060, 40070, 40080, 40090,
    40100, 40110, 40120, 40250, 40260, 40270, 40280, 40290,
    50000, 50010, 50020, 50030, 50040, 50050, 51000, 51010, 51020, 51030,
    60000, 60010, 60020, 60030, 61000, 61010, 62000, 63000, 64000, 65000, 66000, 67000, 68000
];
var fhair = [
    31000, 31010, 31020, 31030, 31040, 31050, 31060, 31070, 31080, 31090,
    31100, 31110, 31120, 31130, 31140, 31150, 31160, 31170, 31180, 31190,
    31200, 31210, 31220, 31230, 31240, 31250, 31260, 31270, 31280, 31290,
    31300, 31310, 31320, 31330, 31340, 31350, 31400, 31410, 31420, 31440,
    31450, 31460, 31470, 31480, 31490, 31510, 31520, 31530, 31540, 31550,
    31560, 31570, 31580, 31590, 31600, 31610, 31620, 31630, 31640, 31650,
    31660, 31670, 31680, 31690, 31700, 31710, 31720, 31730, 31740, 31750,
    31760, 31770, 31780, 31790, 31800, 31810, 31820, 31830, 31840, 31850,
    31860, 31870, 31880, 31890, 31910, 31920, 31930, 31940, 31950,
    34010, 34020, 34030, 34050, 34110,
    41000, 41010, 41020, 41030, 41040, 41050, 41100, 41200, 41300, 41400,
    51000, 51100, 51200, 51300, 51400, 51500,
    61000, 61100, 61200, 61300, 61400, 61500, 64000, 65000, 66000, 67000, 68000
];
var mface = [
    20000, 20001, 20002, 20003, 20004, 20005, 20006, 20007, 20008, 20009,
    20010, 20011, 20012, 20013, 20014, 20015, 20016, 20017, 20018, 20019,
    20020, 20021, 20022, 20023, 20024, 20025, 20026, 20027, 20028, 20029,
    20031, 20032, 20035, 20036, 20037, 20040, 20043, 20044, 20045, 20046
];
var fface = [
    21000, 21001, 21002, 21003, 21004, 21005, 21006, 21007, 21008, 21009,
    21010, 21011, 21012, 21013, 21014, 21016, 21017, 21018, 21019, 21020,
    21021, 21022, 21023, 21024, 21025, 21026, 21027, 21029, 21030, 21031,
    21033, 21034, 21035, 21036, 21037, 21038, 21040, 21041, 21042, 21043
];
var skin = [0, 1, 2, 3, 4, 5, 9, 10, 11, 12, 13];

function pushIfItemExists(array, itemid) {
    var id = cm.getCosmeticItem(itemid);
    if (id != -1 && !cm.isCosmeticEquipped(id)) {
        array.push(id);
    }
}

function canPay() {
    var nx = cm.getPlayer().getCashShop().getCash(CASH_NX);
    if (nx < NX_COST) {
        cm.sendOk("点券不足。需要 #r" + NX_COST + "#k，当前 #b" + nx + "#k。");
        return false;
    }
    if (cm.getMeso() < MESO_COST) {
        cm.sendOk("金币不足。需要 #r" + MESO_COST + "#k 金币。");
        return false;
    }
    return true;
}

function pay() {
    cm.getPlayer().getCashShop().gainCash(CASH_NX, -NX_COST);
    cm.gainMeso(-MESO_COST);
}

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode != 1) {
        cm.dispose();
        return;
    }
    status++;
    if (status == 0) {
        var text = "#e点卷美容美发#n\r\n";
        text += "每次改造型消耗 #r" + NX_COST + "#k 点券 + #r" + MESO_COST + "#k 金币。\r\n";
        text += "当前点券：#b" + cm.getPlayer().getCashShop().getCash(CASH_NX) + "#k　金币：#b" + cm.getMeso() + "#k\r\n\r\n";
        text += "#L1#更换发型#l\r\n";
        text += "#L2#更换发色#l\r\n";
        text += "#L3#更换脸型#l\r\n";
        text += "#L4#更换瞳色#l\r\n";
        text += "#L5#更换肤色#l\r\n";
        text += "#L0#离开#l";
        cm.sendSimple(text);
        return;
    }
    if (status == 1) {
        if (selection == 0) {
            cm.dispose();
            return;
        }
        if (!canPay()) {
            cm.dispose();
            return;
        }
        beauty = selection;
        hairnew = [];
        facenew = [];
        haircolor = [];
        colors = [];
        if (selection == 1) {
            var list = cm.getPlayer().getGender() == 0 ? mhair : fhair;
            var color = cm.getPlayer().getHair() % 10;
            for (var i = 0; i < list.length; i++) {
                pushIfItemExists(hairnew, list[i] - (list[i] % 10) + color);
            }
            if (hairnew.length == 0) {
                cm.sendOk("当前没有可用的发型素材。");
                cm.dispose();
                return;
            }
            cm.sendStyle("请选择发型（将扣除 " + NX_COST + " 点券 + " + MESO_COST + " 金币）：", hairnew);
        } else if (selection == 2) {
            var baseHair = parseInt(cm.getPlayer().getHair() / 10) * 10;
            for (var k = 0; k < 8; k++) {
                pushIfItemExists(haircolor, baseHair + k);
            }
            if (haircolor.length == 0) {
                cm.sendOk("当前没有可用的发色。");
                cm.dispose();
                return;
            }
            cm.sendStyle("请选择发色：", haircolor);
        } else if (selection == 3) {
            var faces = cm.getPlayer().getGender() == 0 ? mface : fface;
            var lens = cm.getPlayer().getFace() % 1000 - (cm.getPlayer().getFace() % 100);
            for (var j = 0; j < faces.length; j++) {
                pushIfItemExists(facenew, faces[j] + lens);
            }
            if (facenew.length == 0) {
                cm.sendOk("当前没有可用的脸型素材。");
                cm.dispose();
                return;
            }
            cm.sendStyle("请选择脸型：", facenew);
        } else if (selection == 4) {
            var baseFace = parseInt(cm.getPlayer().getFace() / 1000) * 1000 + parseInt(cm.getPlayer().getFace() % 100);
            for (var c = 0; c < 9; c++) {
                pushIfItemExists(colors, baseFace + (c * 100));
            }
            if (colors.length == 0) {
                cm.sendOk("当前没有可用的瞳色。");
                cm.dispose();
                return;
            }
            cm.sendStyle("请选择瞳色：", colors);
        } else if (selection == 5) {
            cm.sendStyle("请选择肤色：", skin);
        } else {
            cm.dispose();
        }
        return;
    }
    if (status == 2) {
        if (!canPay()) {
            cm.dispose();
            return;
        }
        pay();
        if (beauty == 1) {
            cm.setHair(hairnew[selection]);
        } else if (beauty == 2) {
            cm.setHair(haircolor[selection]);
        } else if (beauty == 3) {
            cm.setFace(facenew[selection]);
        } else if (beauty == 4) {
            cm.setFace(colors[selection]);
        } else if (beauty == 5) {
            cm.setSkin(skin[selection]);
        }
        cm.sendOk("造型已更新！扣除 #r" + NX_COST + "#k 点券与 #r" + MESO_COST + "#k 金币。");
        cm.dispose();
    }
}

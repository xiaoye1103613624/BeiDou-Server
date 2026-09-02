/*
	自由金猪 / 通用抽奖薄脚本
	奖池与消耗均读 xy_lottery_*（后台配置），不在脚本硬编码。
	绑定 NPC：默认 9310022；其它 NPC 复制本文件并改名为 {npcId}.js 即可。
 */
var status = -1;
var poolPage = 0;
var POOL_PAGE_SIZE = 40;
var mode = ""; // menu | pool

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(modeSel, type, selection) {
    if (modeSel == -1) {
        cm.dispose();
        return;
    }
    if (modeSel == 0) {
        if (mode === "pool" && status >= 1) {
            mode = "menu";
            status = 0;
            showMainMenu();
            return;
        }
        cm.dispose();
        return;
    }
    status++;
    if (status == 0) {
        mode = "menu";
        showMainMenu();
    } else if (status == 1) {
        if (mode === "pool") {
            handlePoolSelection(selection);
            return;
        }
        if (selection == 1000) {
            mode = "pool";
            poolPage = 0;
            showPoolPage();
            return;
        }
        var times = selection;
        var draws = cm.xyLotteryMultiDraws();
        var ok = false;
        for (var i = 0; i < draws.size(); i++) {
            if (draws.get(i) == times) {
                ok = true;
                break;
            }
        }
        if (!ok) {
            cm.sendOk("无效的连抽次数。");
            cm.dispose();
            return;
        }
        var gained = cm.xyLotteryDraw(times);
        if (gained == null || gained.size() == 0) {
            cm.dispose();
            return;
        }
        var txt = "恭喜获得：\r\n";
        for (var j = 0; j < gained.size(); j++) {
            txt += "#v" + gained.get(j) + "##z" + gained.get(j) + "#\r\n";
        }
        cm.sendOk(txt);
        cm.dispose();
    } else {
        cm.dispose();
    }
}

function showMainMenu() {
    var machine = cm.xyLotteryMachine();
    if (machine == null || machine.getEnabled() != 1) {
        cm.sendOk("抽奖机未配置或未启用，请联系管理员。");
        cm.dispose();
        return;
    }
    var draws = cm.xyLotteryMultiDraws();
    if (draws == null || draws.size() == 0) {
        cm.sendOk("未配置连抽档位。");
        cm.dispose();
        return;
    }
    var name = machine.getName() == null ? "抽奖" : machine.getName();
    var cost = cm.xyLotteryCostLabel();
    var txt = "#e#b" + name + "#k#n\r\n";
    txt += "消耗：#r" + cost + "#k\r\n\r\n";
    for (var i = 0; i < draws.size(); i++) {
        var n = draws.get(i);
        txt += "#L" + n + "##b" + n + " 连抽#k#l\r\n";
    }
    txt += "\r\n#L1000##d查看奖池#k#l";
    cm.sendSimple(txt);
}

function showPoolPage() {
    var pages = cm.xyLotteryPoolPageCount(POOL_PAGE_SIZE);
    var body = cm.xyLotteryPoolPreview(poolPage, POOL_PAGE_SIZE);
    var nav = "\r\n";
    if (poolPage > 0) {
        nav += "#L1##b上一页#k#l  ";
    }
    if (poolPage < pages - 1) {
        nav += "#L2##b下一页#k#l  ";
    }
    nav += "#L0##d返回#k#l";
    cm.sendSimple(body + nav);
    status = 0;
    mode = "pool";
}

function handlePoolSelection(selection) {
    if (selection == 0) {
        mode = "menu";
        status = -1;
        action(1, 0, 0);
        return;
    }
    if (selection == 1) {
        poolPage = Math.max(0, poolPage - 1);
    } else if (selection == 2) {
        poolPage = poolPage + 1;
    }
    showPoolPage();
}

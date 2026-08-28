/*
	自由金猪抽奖（薄脚本）— 奖池/消耗/连抽均读 xy_lottery_* 缓存
	入口：cm.openNpc(9310022, 303) 或直接对话绑定本脚本的 NPC
 */
var status = -1;
var poolPage = 0;
var POOL_PAGE_SIZE = 40;
var uiMode = "menu";

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
        return;
    }
    if (mode == 0) {
        cm.dispose();
        return;
    }
    status++;
    if (status == 0) {
        uiMode = "menu";
        showMainMenu();
    } else if (status == 1) {
        if (uiMode === "pool") {
            if (selection == 0) {
                status = -1;
                action(1, 0, 0);
                return;
            }
            if (selection == 1) {
                poolPage = Math.max(0, poolPage - 1);
            } else if (selection == 2) {
                poolPage++;
            }
            showPoolPage();
            return;
        }
        if (selection == 1000) {
            uiMode = "pool";
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
    var name = machine.getName() == null ? "抽奖" : "" + machine.getName();
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
    if (poolPage >= pages) {
        poolPage = pages - 1;
    }
    var body = cm.xyLotteryPoolPreview(poolPage, POOL_PAGE_SIZE);
    var nav = "\r\n";
    if (poolPage > 0) {
        nav += "#L1##b上一页#k#l  ";
    }
    if (poolPage < pages - 1) {
        nav += "#L2##b下一页#k#l  ";
    }
    nav += "#L0##d返回#k#l";
    status = 0;
    uiMode = "pool";
    cm.sendSimple(body + nav);
}

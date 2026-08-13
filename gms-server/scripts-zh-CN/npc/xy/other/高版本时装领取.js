// 高版本时装领取（本批 80 件：40 套服 + 40 帽子）
// 客户端 Data 已有外观；本脚本发放服务端已登记物品
var PAGE = 10;
var ITEMS = [1053017, 1053016, 1053014, 1053001, 1053000, 1052961, 1052920, 1052915, 1052902, 1052851, 1052812, 1052811, 1052682, 1052681, 1052680, 1052679, 1052665, 1052664, 1052659, 1052642, 1052641, 1052640, 1052552, 1052551, 1052447, 1052446, 1052435, 1052409, 1052369, 1052324, 1052281, 1052280, 1052279, 1052278, 1052277, 1052268, 1052207, 1052206, 1052205, 1052174, 1004734, 1004733, 1004673, 1004672, 1004671, 1004662, 1004611, 1004539, 1004538, 1004537, 1004536, 1004534, 1004515, 1004419, 1004418, 1004417, 1004383, 1004322, 1004321, 1004320, 1004319, 1004318, 1004317, 1004316, 1004315, 1004314, 1004313, 1004312, 1004311, 1004310, 1004309, 1004308, 1004307, 1004306, 1004305, 1004304, 1004303, 1004302, 1004301, 1004297];
var status = 0;
var page = 0;

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
        var text = "#e高版本时装领取#n\r\n";
        text += "本批共 #b" + ITEMS.length + "#k 件（客户端已有外观）。\r\n";
        text += "#L1#逐页领取（每页最多 " + PAGE + " 件）#l\r\n";
        text += "#L2#一键领取全部（需足够背包空位）#l\r\n";
        text += "#L0#离开#l";
        cm.sendSimple(text);
        return;
    }
    if (status == 1) {
        if (selection == 0) {
            cm.dispose();
            return;
        }
        if (selection == 2) {
            var ok = 0;
            for (var i = 0; i < ITEMS.length; i++) {
                if (!cm.canHold(ITEMS[i], 1)) {
                    cm.sendOk("背包空间不足，已领取 #b" + ok + "#k 件后中止。");
                    cm.dispose();
                    return;
                }
                cm.gainItem(ITEMS[i], 1);
                ok++;
            }
            cm.sendOk("已领取全部 #b" + ok + "#k 件时装。");
            cm.dispose();
            return;
        }
        page = 0;
        showPage();
        return;
    }
    if (status == 2) {
        if (selection == 998) {
            page--;
            showPage();
            return;
        }
        if (selection == 999) {
            page++;
            showPage();
            return;
        }
        if (selection == 997) {
            status = -1;
            action(1, 0, 0);
            return;
        }
        var id = ITEMS[selection];
        if (id == null) {
            cm.dispose();
            return;
        }
        if (!cm.canHold(id, 1)) {
            cm.sendOk("背包空间不足。");
            cm.dispose();
            return;
        }
        cm.gainItem(id, 1);
        cm.sendOk("已领取 #v" + id + "# #z" + id + "#");
        cm.dispose();
    }
}

function showPage() {
    var total = ITEMS.length;
    var pages = Math.max(1, Math.ceil(total / PAGE));
    if (page < 0) page = 0;
    if (page > pages - 1) page = pages - 1;
    var start = page * PAGE;
    var end = Math.min(start + PAGE, total);
    var text = "#e时装列表#n (" + (page + 1) + "/" + pages + ")\r\n";
    for (var i = start; i < end; i++) {
        text += "#L" + i + "##v" + ITEMS[i] + "# #z" + ITEMS[i] + "##l\r\n";
    }
    if (page > 0) text += "#L998#上一页#l  ";
    if (page < pages - 1) text += "#L999#下一页#l";
    text += "\r\n#L997#返回#l";
    status = 1;
    cm.sendSimple(text);
}

var status = -1;
var selectionLog = [];

function start() {
    action(1, 0, 0)
}

function action(d, c, b) {
    if (status == 0 && d == 0) {
        cm.dispose();
        return
    }(d == 1) ? status++ : status--;
    selectionLog[status] = b;
    var a = -1;
    if (status <= a++) {
        cm.dispose()
    } else {
        if (status === a++) {
            cm.sendNextN("欢迎来到怪物公园！\r\n我是站在那边的休彼德蔓的妹妹，我叫#b#p9071006##k，\r\n请多多关照！", 36, 9071006);
        } else {
            cm.dispose()
        }
    }
};
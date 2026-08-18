function enter(pi) {
    var b = pi.getPortal().getId();
    var a = pi.getPortal().getName();
    if (pi.getQuestStatus(3861) > 0) {
        pi.warp(252020700, 2)
    } else {
        pi.playerMessage(-1, "现在还不能通过这道门。")
    }
};
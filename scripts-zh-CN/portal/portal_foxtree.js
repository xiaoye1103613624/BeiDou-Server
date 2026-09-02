function enter(pi) {
    var b = pi.getPortal().getId();
    var a = pi.getPortal().getName();
    pi.warp(940200011,0);//传送到狐狸树
}
var status = -1;
var selectionLog = [];

function start(pi) {
    action(1, 0, 0)
}
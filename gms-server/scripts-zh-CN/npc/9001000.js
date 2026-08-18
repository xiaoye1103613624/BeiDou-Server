function start() {
    cm.sendSimple("欢迎来到冒险岛！请选择您要传送的地图：\r\n#b\r\n#L0#传送至自由市场#l\r\n#L1#传送至可乐村#l\r\n");
}

function action(mode, type, selection) {
    if (mode == 1) {
        switch (selection) {
            case 0:
                cm.warp(910000000, 0);
                break;
            case 1:
                cm.warp(219000000, 0);
                break;
        }
    }
    cm.dispose();
}

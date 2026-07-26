// 自由市场 · 传说中的美容师 9105006
// WZ 标注 hair_royal2；本服对接自定义美容院界面（同 @美容美发）
function start() {
    var text = "#e传说中的美容师#n\r\n\r\n";
    text += "我可以做出你喜欢的最新造型！\r\n\r\n";
    text += "#L1#打开美容院（发型/脸型/皮肤等）#l\r\n";
    text += "#L2#皇家发型（会员卡）#l\r\n";
    text += "#L0#离开#l";
    cm.sendSimple(text);
}

function action(mode, type, selection) {
    if (mode !== 1) {
        cm.dispose();
        return;
    }
    if (selection === 0) {
        cm.dispose();
        return;
    }
    if (selection === 1) {
        var BeautyPackets = Java.type("org.gms.server.beauty.BeautyPackets");
        cm.getClient().sendPacket(BeautyPackets.beautyOpen());
        cm.dispose();
        return;
    }
    if (selection === 2) {
        cm.dispose();
        cm.openNpc(9105006, "xy/other/皇家发型");
        return;
    }
    cm.dispose();
}

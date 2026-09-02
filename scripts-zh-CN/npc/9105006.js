// 自由市场 · 传说中的美容师 9105006
// WZ 标注 hair_royal2；本服对接自定义美容院界面（同 @美容美发）
// + 点卷选款美容（经典 sendStyle）
function start() {
    var text = "#e传说中的美容师#n\r\n\r\n";
    text += "我可以做出你喜欢的最新造型！\r\n";
    text += "#r每次应用/选款改造型：2000 点券 + 100万金币#k\r\n\r\n";
    text += "#L1#打开美容院（存档栏换发型/脸型/皮肤）#l\r\n";
    text += "#L3#点卷选款美容（经典列表选款）#l\r\n";
    text += "#L2#皇家发型（会员卡）#l\r\n";
    text += "#L4#高版本时装领取（本批80件）#l\r\n";
    text += "#L0#离开#l";
    cm.sendSimple(text);
}

function openBeautySalon() {
    var c = cm.getClient();
    var BeautyPackets = Java.type("org.gms.server.beauty.BeautyPackets");
    // Prefer unified helper (needs server rebuild); fall back to OPEN+DATA for hot script reload.
    try {
        BeautyPackets.openSalon(c);
        return;
    } catch (ignored) {
    }
    var BeautyStorage = Java.type("org.gms.server.beauty.BeautyStorage");
    var chrId = cm.getPlayer().getId();
    var unlocked = BeautyStorage.getUnlockedSlots(chrId);
    if (unlocked <= 0) {
        BeautyStorage.setUnlockedSlots(chrId, 6);
        unlocked = 6;
    }
    c.sendPacket(BeautyPackets.beautyOpen());
    c.sendPacket(BeautyPackets.beautyData(unlocked, BeautyStorage.loadAll(chrId)));
}

function action(mode, type, selection) {
    // Graal 可能传入 Java 数值包装类型，用 == 避免 === 误判导致整段菜单“点了没反应”
    if (mode != 1) {
        cm.dispose();
        return;
    }
    if (selection == 0) {
        cm.dispose();
        return;
    }
    if (selection == 1) {
        try {
            openBeautySalon();
            cm.getPlayer().dropMessage(5, "正在打开美容院…");
        } catch (e) {
            cm.getPlayer().dropMessage(5, "美容院打开失败: " + e);
        }
        cm.dispose();
        return;
    }
    if (selection == 2) {
        cm.dispose();
        cm.openNpc(9105006, "xy/other/皇家发型");
        return;
    }
    if (selection == 3) {
        cm.dispose();
        cm.openNpc(9105006, "xy/other/点卷美容美发");
        return;
    }
    if (selection == 4) {
        cm.dispose();
        cm.openNpc(9105006, "xy/other/高版本时装领取");
        return;
    }
    cm.dispose();
}

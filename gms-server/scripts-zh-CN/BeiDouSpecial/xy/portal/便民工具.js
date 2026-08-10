/**
 * @description 便民工具入口（可挂载到任意NPC或侧边工具栏）
 * 包含：仓库管理、皇家发型、精美时装、阿尔泰皮肤、发色选择、七彩棱镜染色、益智答题、物品查询、宠物成长
 */

var 圆形 = "#fUI/UIWindow/Quest/icon3/6#";
var status = -1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === 1) { status++; }
    else { cm.dispose(); return; }

    if (status === 0) { showMenu(); }
    else if (status === 1) { doSelect(selection); }
    else { cm.dispose(); }
}

function showMenu() {
    var text = "";
    text += "\t★━━\t#e #r便民工具#k#n \t━━★\r\n";
    text += "\t" + 圆形.repeat(8) + "\r\n\r\n";
    text += "━━━ 存储与整理 ━━━\r\n";
    text += "#L166##r仓库管理#k  #d存取装备与道具#k#l\r\n";
    text += "\r\n━━━ 外观定制 ━━━\r\n";
    text += "#L181#美容院  #d存档栏换发型/脸型/皮肤#k#l\r\n";
    text += "#L174#皇家发型  #d换一个帅气的发型#k#l\r\n";
    text += "#L173#精美时装  #d换装展示个人风格#k#l\r\n";
    text += "#L178#阿尔泰皮肤  #d111款二次元帽子皮肤#k#l\r\n";
    text += "#L177#发色选择  #d选择心仪的发色#k#l\r\n";
    text += "#L180#七彩棱镜染色  #d现金装备HSL调色#k#l\r\n";
    text += "\r\n━━━ 娱乐互动 ━━━\r\n";
    text += "#L176#益智答题  #d答对题目赢奖励#k#l\r\n";
    text += "#L175#物品查询  #d查询物品详细信息#k#l\r\n";
    text += "\r\n━━━ 宠物 ━━━\r\n";
    text += "#L179#宠物成长喂养  #d消耗初级/高级宠物精华升级#k#l\r\n";
    text += "\r\n#L9#关闭#l\r\n";
    cm.sendSimple(text);
}

function doSelect(selection) {
    switch (selection) {
        case 166: openSub("xy/仓库"); break;
        case 181: openBeautySalon(); break;
        case 174: openSub("xy/other/皇家发型"); break;
        case 173: openSub("xy/other/精美时装"); break;
        case 178: openSub("xy/阿尔泰动漫皮肤"); break;
        case 177: openSub("xy/other/发色选择"); break;
        case 176: openSub("xy/other/益智答题"); break;
        case 175: openSub("xy/gm/物品查询"); break;
        case 179: openSub("xy/other/宠物成长"); break;
        case 180: openColoringPrism(); break;
        case 9:   cm.dispose(); break;
        default:
            cm.sendOk("该功能暂未开放，敬请期待！");
            cm.dispose();
    }
}

function openSub(scriptName) {
    cm.dispose();
    cm.openNpc(9900001, scriptName);
}

function openBeautySalon() {
    try {
        var c = cm.getClient();
        var BeautyPackets = Java.type("org.gms.server.beauty.BeautyPackets");
        try {
            BeautyPackets.openSalon(c);
        } catch (ignored) {
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
        cm.getPlayer().dropMessage(5, "正在打开美容院…");
    } catch (e) {
        cm.getPlayer().dropMessage(5, "美容院打开失败: " + e);
    }
    cm.dispose();
}

// 七彩棱镜：直接发送 OPEN 封包打开客户端染色窗口
function openColoringPrism() {
    cm.dispose();
    cm.getPlayer().getClient().sendPacket(
        Java.type('org.gms.server.coloring.ColoringPrismPackets').open());
}

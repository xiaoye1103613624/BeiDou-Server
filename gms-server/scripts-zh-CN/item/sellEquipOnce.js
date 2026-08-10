/**
 * 装备出售卡（itemId=2432080 / 2432076）使用脚本
 * 功能：使用后打开 xy/vip/一键出售装备 脚本，不消耗道具
 * 说明：引擎变量名固定为 im，im.openNpc 会以"cm"变量名加载目标脚本
 *      本脚本未调用 im.gainItem(-1)，因此不会扣除道具
 */
function start() {
    var mapId = im.getMapId();
    if (mapId === 300000012) {
        im.playerMessage(5, "监狱内无法使用装备出售卡。");
        im.dispose();
        return;
    }
    im.dispose();
    im.openNpc(9900001, "xy/vip/一键出售装备");
}

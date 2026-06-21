/**
 * 一键出售装备卡（无需确认）使用脚本
 * 功能：使用后打开 xy/vip/一键出售装备 脚本，不消耗道具
 * 说明：引擎变量名固定为 im，im.openNpc 会以"cm"变量名加载目标脚本
 *      本脚本未调用 im.gainItem(-1)，因此不会扣除道具
 */
function start() {
    im.dispose();
    im.openNpc(9900001, "xy/vip/一键出售装备");
}

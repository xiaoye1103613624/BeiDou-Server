/**
 * 月光宝盒（itemId=2432077）使用脚本
 * 功能：使用后打开拍卖菜单（npc/xy_拍卖_v001.js），不消耗道具
 * 说明：引擎变量名固定为 im（区别于NPC对话脚本的 cm）
 */
function start() {
    im.dispose();
    im.openNpc(9900001, "xy_拍卖_v001");
}

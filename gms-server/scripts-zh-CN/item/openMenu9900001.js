/**
 * xy快捷功能（itemId=2432077）使用脚本
 * 功能：使用后打开 xy_拍卖_v001 拍卖菜单，不消耗道具
 * 说明：引擎变量名固定为 im（区别于NPC对话脚本的 cm）
 */
function start() {
    im.dispose();
    im.openNpc(9900001, "xy_拍卖_v001");
}

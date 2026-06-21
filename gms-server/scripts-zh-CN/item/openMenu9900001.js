/**
 * 北斗主菜单卡（itemId=2432345）使用脚本
 * 功能：使用后打开9900001主菜单（npc/9900001.js），不消耗道具
 * 说明：本脚本通过物品使用触发，引擎变量名固定为 im（区别于NPC对话脚本的 cm）
 *      im.openNpc 内部会以"cm"变量名加载 npc/9900001.js，不会再触发本脚本逻辑
 *      物品消耗与否由服务端ScriptedItemHandler控制，本脚本未调用 im.gainItem(-1)，因此不会扣除道具
 */
function start() {
    im.dispose();
    im.openNpc(9900001);
}

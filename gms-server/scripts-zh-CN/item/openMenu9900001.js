/**
 * 北斗菜单卡（itemId=2432077）使用脚本
 * 功能：使用后打开 xy_拍卖_v001 拍卖/功能菜单，不消耗道具
 * 限制：监狱地图（MapId.JAIL=300000012）不可使用
 * 说明：引擎变量名固定为 im（区别于NPC对话脚本的 cm）
 */
function start() {
    var mapId = im.getMapId();
    if (mapId === 300000012) {
        im.playerMessage(5, "监狱内无法使用北斗菜单卡。");
        im.dispose();
        return;
    }
    im.dispose();
    im.openNpc(9900001, "xy_拍卖_v001");
}

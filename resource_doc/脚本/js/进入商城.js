function action(mode, type, selection) {
    // 定义禁止跳转的地图ID数组
    var forbiddenMaps = [410000101, 410000102, 410000103, 410000121, 410000122, 410000123]; // 示例地图ID，你可以根据需要修改

    // 获取玩家当前所在地图ID
    var currentMapId = cm.getPlayer().getMapId();

    // 检查当前地图是否在禁止列表中
    if (forbiddenMaps.indexOf(currentMapId) !== -1) {
        cm.sendOk("你不能在这个地图中使用此功能！");
        cm.dispose();
    } else {
        // 如果不在禁止列表中，则允许跳转
        cm.openNpc(9000444, 0);
    }
}
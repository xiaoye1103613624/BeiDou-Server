function start() {
    cm.sendYesNo("确定要放弃挑战返回入口地点吗？");
}

function action(mode, type, selection) {
    if (mode == 1) { // 确认选择是“是”
        cm.warp(220080000, 0); // 传送到入口地点
        cm.dispose(); // 结束对话
    } else { // 选择了“否”
        cm.dispose(); // 结束对话
    }
}

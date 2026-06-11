var 列表1 = {
    返回地点: 230050000 // 返回地点的地图ID
};

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (status >= 0 && mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        } else {
            status--;
        }

        if (status == 0) {
            // 弹出确认对话框
            cm.sendYesNo("确定要放弃挑战返回#r#e #m" + 列表1.返回地点 + "##k#n 吗？");
        } else if (status == 1) {
            if (selection == 1) { // 玩家点击了“否”
                cm.dispose(); // 结束对话
            } else {
                cm.warp(列表1.返回地点, 0); 
                cm.dispose(); // 结束对话
            }
        }
    }
}
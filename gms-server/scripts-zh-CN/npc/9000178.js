// 新手 BUFF：每批最多 2 个 + 错开挂状态，避免同屏连发 giveBuff/特效打满客户端主线程
var BUFFS = [
    2022458,
    2022070,
    2022071,
    2022423,
    2022461, // 双倍金币掉落
    2022463, // 双倍物品掉落
    2022093
];
var BATCH = 2;
var GAP_MS = 400;
var idx = 0;

function applyNextBatch() {
    var batch = [];
    for (var i = 0; i < BATCH && idx < BUFFS.length; i++) {
        batch.push(BUFFS[idx]);
        idx++;
    }
    if (batch.length === 0) {
        cm.sendOk("新手BUFF已全部领取，祝你一臂之力");
        return;
    }
    cm.useItemsStaggered(batch, GAP_MS);
    if (idx >= BUFFS.length) {
        cm.sendOk("新手BUFF领取完成（共" + BUFFS.length + "个），祝你一臂之力");
    } else {
        cm.sendYesNo("已挂上 " + idx + "/" + BUFFS.length + " 个增益。\r\n是否继续领取下一批？");
    }
}

function start() {
    idx = 0;
    applyNextBatch();
}

function action(mode, type, selection) {
    if (mode != 1) {
        cm.dispose();
        return;
    }
    if (idx < BUFFS.length) {
        applyNextBatch();
    } else {
        cm.dispose();
    }
}

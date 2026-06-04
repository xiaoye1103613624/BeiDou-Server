/*
 * ==================
 * 脚本类型: NPC - 轮回石碑
 * 脚本作者：北斗项目组
 * 对应NPC：9900002（需在WZ中添加此NPC）
 * 功能说明：
 *   1. 显示轮回石碑的剩余时间
 *   2. 所有者可以提前移除石碑
 *   3. 非所有者只能查看状态
 * ==================
 */

function start() {
    if (!cm.hasSamsaraStone()) {
        cm.sendOk("此轮回石碑已失效。");
        cm.dispose();
        return;
    }

    var owner = cm.getSamsaraOwner();
    var expireTime = cm.getSamsaraExpireTime();
    var remainingMs = expireTime - Date.now();
    var remainingMin = Math.max(0, Math.ceil(remainingMs / 60000));

    if (owner !== null && cm.getPlayer().getId() === owner.getId()) {
        // 所有者：可以查看和移除
        var text = "#e#b【轮回石碑】#k#n\r\n\r\n";
        text += "#d" + "".padStart(20, "——") + "#k\r\n";
        text += "你的轮回石碑正在发挥作用！\r\n";
        text += "怪物刷新速度已提升约 #r3.3倍#k\r\n";
        text += "剩余时间：#b" + remainingMin + "#k 分钟\r\n";
        text += "#d" + "".padStart(20, "——") + "#k\r\n\r\n";
        text += "#L0##r提前移除石碑#k#l\r\n";
        cm.sendSimple(text);
    } else {
        // 非所有者：只能查看
        var ownerName = owner !== null ? owner.getName() : "未知";
        var text = "#e#b【轮回石碑】#k#n\r\n\r\n";
        text += "#d" + "".padStart(20, "——") + "#k\r\n";
        text += "此石碑由 #b" + ownerName + "#k 召唤\r\n";
        text += "怪物刷新速度已提升约 #r3.3倍#k\r\n";
        text += "剩余时间：#b" + remainingMin + "#k 分钟\r\n";
        text += "#d" + "".padStart(20, "——") + "#k\r\n";
        cm.sendOk(text);
        cm.dispose();
    }
}

function action(mode, type, selection) {
    if (mode === -1) {
        cm.dispose();
        return;
    }
    if (mode === 0) {
        cm.dispose();
        return;
    }

    if (selection === 0) {
        cm.removeSamsaraStone();
        cm.sendOk("轮回石碑已被移除，怪物刷新速度恢复正常。");
        cm.dispose();
    }
}

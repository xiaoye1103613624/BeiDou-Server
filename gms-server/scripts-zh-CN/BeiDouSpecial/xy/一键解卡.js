/**
 * 个人解卡 — 一键解卡 + 开启头顶实时信息
 *
 * 直接触发，无需确认：
 *   1. 关闭卡住的 NPC/任务对话
 *   2. 恢复客户端操作权限
 *   3. 清除 NPC 点击状态
 *   4. 开启头顶实时信息展示（经验倍率/金币倍率/爆率/网络延迟）
 *
 * 内部实现对应 Java 层：
 *   - DisposeCommand.java （解卡逻辑）
 *   - Character.toggleOverheadInfo() （头顶信息定时刷新）
 */

// ======================== Java 类型导入 ========================
var NPCScriptManager = Java.type("org.gms.scripting.npc.NPCScriptManager");
var QuestScriptManager = Java.type("org.gms.scripting.quest.QuestScriptManager");
var PacketCreator = Java.type("org.gms.util.PacketCreator");

function start() {
    // 直接执行解卡，无需菜单确认
    doPersonalDispose();
    // 开启头顶实时信息展示
    startOverheadIfNeeded();
    // 顶部飘字提示
    cm.getPlayer().dropMessage(5, "[解卡完成] 已恢复操作权限，头顶信息已开启。使用 @dispose 可快速自救。");
    // 直接关闭对话
    cm.dispose();
}

/**
 * 个人解卡：关闭所有脚本、恢复操作权限、清除点击状态
 * 等效于 @dispose 命令
 */
function doPersonalDispose() {
    var c = cm.getClient();
    // 1. 关闭 NPC 对话脚本
    NPCScriptManager.getInstance().dispose(c);
    // 2. 关闭任务脚本
    QuestScriptManager.getInstance().dispose(c);
    // 3. 恢复客户端操作权限
    c.sendPacket(PacketCreator.enableActions());
    // 4. 清除 NPC 点击状态
    c.removeClickedNPC();
}

/**
 * 确保头顶实时信息已开启
 * 等效于 @overhead 命令
 */
function startOverheadIfNeeded() {
    var player = cm.getPlayer();
    if (!player.isOverheadInfoEnabled()) {
        player.toggleOverheadInfo();
    }
}

/**
 * @description 拍卖行中心 — 诊断版（排查闪退用）
 * 在聊天窗口输出诊断信息，确认脚本执行路径
 */
var status = -1;

function start() {
    status = -1;
    // 诊断：确认脚本已启动
    cm.getPlayer().dropMessage(5, "[拍卖诊断] 脚本start()已执行, 即将sendSimple...");
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === 1) {
        status++;
    } else if (mode === -1) {
        status--;
    } else {
        cm.dispose();
        return;
    }

    if (status === 0) {
        cm.getPlayer().dropMessage(5, "[拍卖诊断] status=0, 准备发送sendSimple");
        cm.sendSimple("#L0#诊断选项 - 点我#l");
        cm.getPlayer().dropMessage(5, "[拍卖诊断] sendSimple已发送(如果看到这条说明没闪退)");
    } else if (status === 1) {
        cm.getPlayer().dropMessage(5, "[拍卖诊断] 用户选择了选项" + selection);
        cm.sendOk("诊断：NPC对话正常工作！");
        cm.dispose();
    } else {
        cm.dispose();
    }
}

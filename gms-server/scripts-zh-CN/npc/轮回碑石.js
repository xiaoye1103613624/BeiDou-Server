/**
 * 轮回碑石 NPC 脚本（兼容/手工触发）。
 * 主路径为专用「轮回」技能（1021 系）+ ReincarnationSupport.tryHandleSkill。
 */
function start() {
    cm.tryActivateReincarnation();
    cm.dispose();
}

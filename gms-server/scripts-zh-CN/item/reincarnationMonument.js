/**
 * 轮回石碑（2430023）：使用后激活当前地图轮回效果。
 * 地图规则与技能施放逻辑由 ReincarnationSupport 统一管理。
 */
function start() {
    im.tryActivateReincarnationConsume(2430023);
    im.dispose();
}

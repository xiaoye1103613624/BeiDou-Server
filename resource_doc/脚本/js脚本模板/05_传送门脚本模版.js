/* ============================================================
 * 脚本类型: 传送门脚本
 * 模版说明: 玩家踩上传送门时触发
 * 文件命名: 传送门名称.js（如 EBoat1.js）
 * 存放位置: scripts-zh-CN/portal/
 *
 * 调用链路:
 *   玩家踩上传送门 → PortalScriptManager → enter(pi)
 *
 * 全局变量:
 *   pi = PortalScriptManager 实例
 *
 * 返回值:
 *   true  = 允许传送（执行默认传送行为）
 *   false = 阻止传送（自定义处理，不执行默认行为）
 *
 * pi.getPortal() 说明:
 *   返回当前传送门的 Portal 对象
 *   常用属性: getName()、getTargetMapId()、getTarget()
 * ============================================================ */

/**
 * 传送门入口函数
 * @param {PortalPlayerInteraction} pi
 * @returns {boolean} true=允许默认传送，false=拦截
 */
function enter(pi) {
    /* ---- 方式一：播放音效后传送到目标地图 ---- */
    pi.playPortalSound();
    pi.warp(pi.getPortal().getTargetMapId(), pi.getPortal().getTarget());
    return true;

    /* ---- 方式二：带条件判断的传送 ---- */
    /*
    var player = pi.getPlayer();

    if (player.getLevel() < 10) {
        player.dropMessage(5, "等级不足10级，无法进入该地图。");
        return false;
    }

    pi.playPortalSound();
    pi.warp(100000000, 0);
    return true;
    */

    /* ---- 方式三：剧情传送（变换BGM、不播放音效）---- */
    /*
    pi.warp(200090000, 4);
    pi.changeMusic("Bgm04/ArabPirate");
    return true;
    */
}

/* ============================================================
 * 【传送门脚本 pi 常用方法】
 *
 * ---- 传送 ----
 * pi.warp(mapId, portalId)
 *     传送玩家到目标地图的指定传送门
 * pi.getPortal()
 *     获取当前传送门对象
 * pi.getPortal().getTargetMapId()
 *     获取目标地图ID
 * pi.getPortal().getTarget()
 *     获取目标传送门名称
 *
 * ---- 音效 ----
 * pi.playPortalSound()
 *     播放传送音效
 * pi.changeMusic("路径")
 *     更换BGM（如 "Bgm04/ArabPirate"）
 *
 * ---- 玩家 ----
 * pi.getPlayer()
 *     获取 Character 对象
 * pi.getPlayer().dropMessage(type, text)
 *     发送消息
 *
 * ---- 地图 ----
 * pi.getMapId()
 *     获取当前地图ID
 * pi.getPlayerCount(mapId)
 *     获取指定地图的玩家数量
 * ============================================================ */

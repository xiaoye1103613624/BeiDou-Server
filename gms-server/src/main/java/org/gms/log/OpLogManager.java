package org.gms.log;

import org.gms.client.Character;
import org.gms.manager.ServerManager;
import org.gms.server.ItemInformationProvider;
import org.gms.service.OpLogService;

/**
 * 统一操作日志静态门面。
 * <p>供 GraalVM JS 脚本通过 {@code Java.type('org.gms.log.OpLogManager')} 调用。
 * 脚本只负责上报日志事件，服务端根据「操作类型 → 样式绑定」决定聊天框展示并全服广播。</p>
 */
public final class OpLogManager {

    private OpLogManager() {}

    private static OpLogService service() {
        var context = ServerManager.getApplicationContext();
        if (context == null) {
            throw new IllegalStateException("Spring 上下文不可用");
        }
        return context.getBean(OpLogService.class);
    }

    /**
     * 记录一条操作日志（自动填充玩家信息/账号/IP/世界频道）。
     *
     * @param chr     操作角色
     * @param opType  操作类型（见 {@link OpLogType}）
     * @param summary 摘要，如 "兑换[星石 * 10]"
     * @param detail  完整详情（审计用）
     */
    public static boolean record(Character chr, int opType, String summary, String detail) {
        return service().record(chr, opType, summary, detail);
    }

    /**
     * 记录一条操作日志（全量参数版）。
     */
    public static boolean record(int opType, Integer characterId, String characterName, Integer accountId,
                                 String summary, String detail, String ip, String worldChannel) {
        return service().record(opType, characterId, characterName, accountId, summary, detail, ip, worldChannel);
    }

    /**
     * 记录一次「兑换」操作并全服广播。
     * 物品名称由服务端解析，脚本无需展示名称。
     *
     * @param chr      操作角色
     * @param itemId   获得物品ID
     * @param qty      获得数量
     * @param detail   完整详情（审计用）
     */
    public static boolean recordExchange(Character chr, int itemId, int qty, String detail) {
        String name = ItemInformationProvider.getInstance().getName(itemId);
        return service().record(chr, OpLogType.EXCHANGE, "兑换[" + name + " * " + qty + "]", detail);
    }

    /**
     * 记录一次「锻造石互换」操作并全服广播。
     */
    public static boolean recordForge(Character chr, int itemId, int qty, String detail) {
        String name = ItemInformationProvider.getInstance().getName(itemId);
        return service().record(chr, OpLogType.FORGE, "互换[" + name + " * " + qty + "]", detail);
    }

    /**
     * 记录一次「限购兑换」操作并全服广播（白底粉字）。
     * <p>summary 额外携带今日当前/剩余兑换次数，供日志与公告展示。</p>
     *
     * @param chr          操作角色
     * @param itemId       获得物品ID
     * @param qty          获得数量
     * @param currentCount 兑换后今日已购次数
     * @param dailyMax     每日上限
     * @param detail       完整详情（审计用）
     */
    public static boolean recordLimited(Character chr, int itemId, int qty, int currentCount, int dailyMax, String detail) {
        String name = ItemInformationProvider.getInstance().getName(itemId);
        String display = (name == null || name.isEmpty()) ? ("#" + itemId) : name;
        String summary = "兑换[" + display + " * " + qty + "] 当前" + currentCount + "/" + dailyMax
                + " 剩余" + (dailyMax - currentCount);
        return service().record(chr, OpLogType.LIMITED, summary, detail);
    }

    /**
     * 记录一次「装备注能」操作并全服广播。
     *
     * @param chr        操作角色
     * @param equipResult 摘要形态："⚡" + newLevel（供展示）
     * @param detail     完整详情（审计用）
     */
    public static boolean recordInfusion(Character chr, String equipResult, String detail) {
        String display = (equipResult == null || equipResult.isEmpty()) ? "⚡注能" : equipResult;
        return service().record(chr, OpLogType.INFUSION, "注能[" + display + "]", detail);
    }

    /**
     * 记录宝石镶嵌操作。equipResult 摘要如 "宝5"。
     */
    public static boolean recordGem(Character chr, String equipResult, String detail) {
        String display = (equipResult == null || equipResult.isEmpty()) ? "宝" : equipResult;
        return service().record(chr, OpLogType.GEM, "宝石镶嵌[" + display + "]", detail);
    }

    /**
     * 记录装备破界操作。equipResult 摘要如 "破界+3"。
     */
    public static boolean recordBreakthrough(Character chr, String equipResult, String detail) {
        String display = (equipResult == null || equipResult.isEmpty()) ? "破界" : equipResult;
        return service().record(chr, OpLogType.BREAKTHROUGH, "破界[" + display + "]", detail);
    }

    /**
     * 操作类型码常量（供脚本用，避免魔法数字）。
     */
    public static final int EXCHANGE = OpLogType.EXCHANGE;
    public static final int CRAFT = OpLogType.CRAFT;
    public static final int FORGE = OpLogType.FORGE;
    public static final int ENHANCE = OpLogType.ENHANCE;
    public static final int ALCHEMY = OpLogType.ALCHEMY;
    public static final int RECYCLE = OpLogType.RECYCLE;
    public static final int SHOP = OpLogType.SHOP;
    public static final int SPONSOR = OpLogType.SPONSOR;
    public static final int LIMITED = OpLogType.LIMITED;
    public static final int INFUSION = OpLogType.INFUSION;
    public static final int GEM = OpLogType.GEM;
    public static final int BREAKTHROUGH = OpLogType.BREAKTHROUGH;
    public static final int ADMIN = OpLogType.ADMIN;
    public static final int GM = OpLogType.GM;
    public static final int OTHER = OpLogType.OTHER;
}
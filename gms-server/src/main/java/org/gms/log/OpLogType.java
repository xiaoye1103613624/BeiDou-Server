package org.gms.log;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 操作日志类型码常量 + 聊天样式预设。
 */
public final class OpLogType {

    public static final int OTHER = 0;
    public static final int EXCHANGE = 1;
    public static final int CRAFT = 2;
    public static final int FORGE = 3;
    public static final int ENHANCE = 4;
    public static final int ALCHEMY = 5;
    public static final int RECYCLE = 6;
    public static final int SHOP = 7;
    public static final int SPONSOR = 8;
    public static final int ADMIN = 9;
    public static final int GM = 10;
    public static final int LIMITED = 11;
    public static final int INFUSION = 12;
    public static final int GEM = 13;
    public static final int BREAKTHROUGH = 14;

    private OpLogType() {}

    /**
     * 聊天样式预设（Web 后台下拉用）。
     * 键为 PacketCreator.serverNotice(type, msg) 的 type 值。
     */
    public static Map<Integer, String> chatStylePresets() {
        Map<Integer, String> m = new LinkedHashMap<>();
        m.put(0, "白底黑字(默认)");
        m.put(1, "红字提示");
        m.put(5, "白底粉字");
        m.put(6, "白底蓝字");
        return m;
    }

    /**
     * 兜底默认名称（DB 无绑定行时使用）。
     */
    public static String fallbackName(int opType) {
        return switch (opType) {
            case EXCHANGE -> "兑换";
            case CRAFT -> "打造";
            case FORGE -> "锻造";
            case ENHANCE -> "强化";
            case ALCHEMY -> "炼金";
            case RECYCLE -> "回收";
            case SHOP -> "商店";
            case SPONSOR -> "赞助";
            case LIMITED -> "限购";
            case INFUSION -> "注能";
            case GEM -> "宝石镶嵌";
            case BREAKTHROUGH -> "破界";
            case ADMIN -> "管理";
            case GM -> "GM指令";
            default -> "其他";
        };
    }
}
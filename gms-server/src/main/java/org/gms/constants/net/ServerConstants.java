package org.gms.constants.net;

/**
 * 服务器常量类
 * <p>服务器版本号、屏蔽角色名列表、北导版本与构建时间等</p>
 */
public class ServerConstants {

    /** 服务器版本号 */
    public static final short VERSION = 83;

    /** 调试变量数组（用于数据包测试） */
    public static int[] DEBUG_VALUES = new int[10];

    /** 屏蔽角色名列表 */
    public static final String[] BLOCKED_NAMES = {"admin", "owner", "moderator", "intern", "donor", "administrator", "FREDRICK", "help", "helper", "alert", "notice", "maplestory", "fuck", "wizet", "fucking", "negro", "fuk", "fuc", "penis", "pussy", "asshole", "gay",
            "nigger", "homo", "suck", "cum", "shit", "shitty", "condom", "security", "official", "rape", "nigga", "sex", "tit", "boner", "orgy", "clit", "asshole", "fatass", "bitch", "support", "gamemaster", "cock", "gaay", "gm",
            "operate", "master", "sysop", "party", "GameMaster", "community", "message", "event", "test", "meso", "Scania", "yata", "AsiaSoft", "henesys"};
    /** 200级恭喜消息模板 */
    public static final String LEVEL_200 = "[Congrats] %s has reached Level %d! Congratulate %s on such an amazing achievement!";

    /** 北导版本号 */
    public static final String BEI_DOU_VERSION = "1.10";
    /** 北导构建时间 */
    public static final String BEI_DOU_BUILD_TIME = "2025-06-22 12:45:59";
}
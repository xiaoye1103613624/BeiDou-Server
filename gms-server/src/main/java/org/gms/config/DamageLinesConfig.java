package org.gms.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 伤害 long 化双端开关（与客户端 xy-config.yaml 的 damage.* 成对）。
 *
 * <p>为什么不放 game_config 表：本轮改造不允许任何未授权的 DDL/DML，
 * 游戏侧配置沿用 Spring 的 application.yml，运维只需改配置文件 / JVM 参数。
 *
 * <p>契约（与客户端 Gameplay/Combat/Damage.h 逐字节一致）：
 * 每条伤害行 = int32 vanillaLine + int64 真实值（小端，共 12 字节）。
 * 未溢出时 vanillaLine 写真值，溢出写 INT_MAX 哨兵；暴击置 0x80000000 位。
 * 真实值字段永远非负。
 *
 * <p><b>锁步发布</b>：客户端 damage.long_lines 与服务端本开关必须同时为
 * true/false，否则攻击包会整体读错位（旧端连新端 / 新端连旧端都不兼容）。
 */
@Component
public class DamageLinesConfig {

    /** 2^53：double 能精确表示的最大整数，与插件 damagelong 的 kExactIntLimit 一致。 */
    public static final long EXACT_INT_CAP = 9007199254740992L;

    private static volatile boolean longLines = false;
    private static volatile long damageCap = EXACT_INT_CAP;

    @Value("${gms.service.damage.long-lines:false}")
    private boolean configuredLongLines;

    @Value("${gms.service.damage.cap:9007199254740992}")
    private long configuredCap;

    @PostConstruct
    public void init() {
        longLines = configuredLongLines || Boolean.getBoolean("gms.damage.longLines");
        damageCap = normalizeCap(configuredCap);
    }

    public static boolean isLongLines() {
        return longLines;
    }

    public static long getCap() {
        return damageCap;
    }

    public static long normalizeCap(long value) {
        if (value <= 0 || value > EXACT_INT_CAP) {
            return EXACT_INT_CAP;
        }
        return value;
    }

    /** 服务端侧唯一饱和函数：真实伤害夹到 [0, cap]。 */
    public static long clampDamage(long value) {
        if (value <= 0) {
            return 0;
        }
        return Math.min(value, getCap());
    }

    /** 仍为 int 的旧 API（血条包、脚本、HP 操作）统一从这里收窄。 */
    public static int clampToInt(long value) {
        if (value >= Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        if (value <= Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        return (int) value;
    }

    /** 12 字节模式的 int32 兼容字段：暴击符号位只放在这里。 */
    public static int vanillaLine(long value, boolean critical) {
        long safe = clampDamage(value);
        int base = safe > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) safe;
        if (critical) {
            base |= Integer.MIN_VALUE;
        }
        return base;
    }

    /** legacy 4 字节/行广播：暴击沿用服务端旧的负数编码。 */
    public static int legacyBroadcastLine(long value, boolean critical) {
        int shown = clampToInt(clampDamage(value));
        if (!critical) {
            return shown;
        }
        return -Integer.MAX_VALUE + shown - 1;
    }
}

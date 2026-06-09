package org.gms.net.server.coordinator.session;

import java.util.regex.Pattern;

/**
 * 硬件ID记录
 * 封装玩家客户端硬件标识，用于多客户端检测和登录验证
 *
 * @param hwid 硬件ID字符串
 */
public record Hwid(String hwid) {
    /** 硬件ID长度 */
    private static final int HWID_LENGTH = 8;
    /** 有效主机字符串匹配模式：MAC地址_硬件ID */
    private static final Pattern VALID_HOST_STRING_PATTERN = Pattern.compile("[0-9A-F]{12}_[0-9A-F]{8}");

    private static boolean isValidHostString(String hostString) {
        return VALID_HOST_STRING_PATTERN.matcher(hostString).matches();
    }

    public static Hwid fromHostString(String hostString) throws IllegalArgumentException {
        if (hostString == null || !isValidHostString(hostString)) {
            throw new IllegalArgumentException("hostString has invalid format");
        }

        final String[] split = hostString.split("_");
        if (split.length != 2 || split[1].length() != HWID_LENGTH) {
            throw new IllegalArgumentException("Hwid validation failed for hwid: " + hostString);
        }

        return new Hwid(split[1]);
    }
}
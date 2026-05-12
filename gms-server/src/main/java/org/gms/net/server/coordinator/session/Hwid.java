package org.gms.net.server.coordinator.session;

import java.util.regex.Pattern;

/**
 * 会话与并发协调「Hwid」。
 * 在多开检测、登录绕过、匹配、事件召回等场景下集中管理跨连接状态。
 */
public record Hwid(String hwid) {
    private static final int HWID_LENGTH = 8;
    // First part is a mac address (without dashes), second part is the hwid
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

package org.gms.client;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 【类型】DefaultDates（final class），包 {@code org.gms.client}。
 *
 * 提供系统默认日期常量：生日默认值（2005-05-11，MapleGlobal 上线日）与临时封禁默认时间。
 * 工具类，不可实例化。
 */
final public class DefaultDates {
    // May 11 2005 is the date MapleGlobal released, so it's a symbolic default value

    private DefaultDates() {
    }

    public static LocalDate getBirthday() {
        return LocalDate.parse("2005-05-11");
    }

    public static LocalDateTime getTempban() {
        return LocalDateTime.parse("2005-05-11T00:00:00");
    }
}

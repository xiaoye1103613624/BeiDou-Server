package org.gms.client;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 默认日期工具类
 * 提供默认生日和封禁日期，使用MapleGlobal发布日期作为象征性默认值
 */
final public class DefaultDates {
    /** 使用MapleGlobal发布日期（2005-05-11）作为象征性默认值 */

    private DefaultDates() {
    }

    public static LocalDate getBirthday() {
        return LocalDate.parse("2005-05-11");
    }

    public static LocalDateTime getTempban() {
        return LocalDateTime.parse("2005-05-11T00:00:00");
    }
}
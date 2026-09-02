package org.gms.service.activity;

import org.gms.constants.activity.ActivityScheduleType;
import org.gms.dao.entity.ActivityScheduleDO;
import org.gms.exception.BizException;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

final class ActivityScheduleHelper {

    private static final DateTimeFormatter DATE_TIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter TIME_SHORT =
            DateTimeFormatter.ofPattern("HH:mm");

    private ActivityScheduleHelper() {
    }

    static LocalDateTime parseDateTime(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        String value = text.trim().replace('T', ' ');
        if (value.length() == 16) {
            value = value + ":00";
        }
        try {
            return LocalDateTime.parse(value, DATE_TIME);
        } catch (DateTimeParseException e) {
            throw BizException.illegalArgument("invalid datetime: " + text);
        }
    }

    static LocalTime parseTime(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        String value = text.trim();
        try {
            if (value.length() == 5) {
                return LocalTime.parse(value, TIME_SHORT);
            }
            return LocalTime.parse(value, TIME_FMT);
        } catch (DateTimeParseException e) {
            throw BizException.illegalArgument("invalid time: " + text);
        }
    }

    static String formatDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).format(DATE_TIME);
    }

    static String formatTime(Time time) {
        if (time == null) {
            return null;
        }
        return time.toLocalTime().format(TIME_FMT);
    }

    static Date toDate(LocalDateTime ldt) {
        if (ldt == null) {
            return null;
        }
        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }

    static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * 计算下一次执行时间。fromExclusive 之后的下一场。
     */
    static LocalDateTime computeNextRun(ActivityScheduleDO schedule, LocalDateTime fromExclusive) {
        String type = schedule.getScheduleType();
        if (ActivityScheduleType.ONCE.equals(type)) {
            LocalDateTime start = toLocalDateTime(schedule.getStartAt());
            if (start == null) {
                return null;
            }
            return start.isAfter(fromExclusive) ? start : null;
        }

        LocalTime cron = schedule.getCronTime() == null ? null : schedule.getCronTime().toLocalTime();
        if (cron == null) {
            return null;
        }

        Set<Integer> days = parseDays(schedule.getDaysOfWeek());
        LocalDateTime cursor = fromExclusive.plusSeconds(1).withNano(0);
        for (int i = 0; i < 400; i++) {
            LocalDate date = cursor.toLocalDate();
            if (ActivityScheduleType.WEEKLY.equals(type) && !days.isEmpty()) {
                int iso = date.getDayOfWeek().getValue(); // 1=Mon ... 7=Sun
                if (!days.contains(iso)) {
                    cursor = date.plusDays(1).atStartOfDay();
                    continue;
                }
            }
            LocalDateTime candidate = LocalDateTime.of(date, cron);
            if (candidate.isAfter(fromExclusive)) {
                return candidate;
            }
            cursor = date.plusDays(1).atStartOfDay();
        }
        return null;
    }

    static Set<Integer> parseDays(String daysOfWeek) {
        Set<Integer> days = new HashSet<>();
        if (daysOfWeek == null || daysOfWeek.isBlank()) {
            // 空表示每天
            days.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7));
            return days;
        }
        for (String part : daysOfWeek.split(",")) {
            String p = part.trim();
            if (p.isEmpty()) {
                continue;
            }
            int d = Integer.parseInt(p);
            if (d < 1 || d > 7) {
                throw BizException.illegalArgument("daysOfWeek must be 1-7");
            }
            days.add(d);
        }
        return days;
    }
}

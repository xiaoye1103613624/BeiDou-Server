/*
    This file is part of the HeavenMS MapleStory Server
    Copyleft (L) 2016 - 2019 RonanLana

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.gms.server.expeditions;

import org.gms.config.GameConfig;
import org.gms.util.DatabaseConnection;
import org.gms.util.Pair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;

/**
 * 远征队Boss日志
 * 管理每个玩家的Boss击败记录，实现Boss冷却时间机制
 * 支持按角色、按频道控制Boss进入频率
 *
 * @author Conrad
 * @author Ronan
 */
public class ExpeditionBossLog {

    /**
     * Boss日志条目枚举
     */
    public enum BossLogEntry {
        /** 扎昆 */
        ZAKUM(2, 1, false),
        /** 黑龙 */
        HORNTAIL(2, 1, false),
        /** 品客缤 */
        PINKBEAN(1, 1, false),
        /** 斯卡加 */
        SCARGA(1, 1, false),
        /** 帕普拉图斯 */
        PAPULATUS(2, 1, false);

        /** 条目数 */
        private final int entries;
        /** 时间长度 */
        private final int timeLength;
        /** 最小频道 */
        private final int minChannel;
        /** 最大频道 */
        private final int maxChannel;
        /** 是否按周计算 */
        private final boolean week;

        BossLogEntry(int entries, int timeLength, boolean week) {
            this(entries, 0, Integer.MAX_VALUE, timeLength, week);
        }

        BossLogEntry(int entries, int minChannel, int maxChannel, int timeLength, boolean week) {
            this.entries = entries;
            this.minChannel = minChannel;
            this.maxChannel = maxChannel;
            this.timeLength = timeLength;
            this.week = week;
        }

        private static List<Pair<Timestamp, BossLogEntry>> getBossLogResetTimestamps(Calendar timeNow, boolean week) {
            List<Pair<Timestamp, BossLogEntry>> resetTimestamps = new LinkedList<>();

            Timestamp ts = new Timestamp(timeNow.getTime().getTime());  // reset all table entries actually, thanks Conrad
            for (BossLogEntry b : BossLogEntry.values()) {
                if (b.week == week) {
                    resetTimestamps.add(new Pair<>(ts, b));
                }
            }

            return resetTimestamps;
        }

        private static BossLogEntry getBossEntryByName(String name) {
            for (BossLogEntry b : BossLogEntry.values()) {
                if (name.contentEquals(b.name())) {
                    return b;
                }
            }

            return null;
        }

    }

    public static void resetBossLogTable() {
        /*
        Boss logs resets 12am, weekly thursday 12AM - thanks Smitty Werbenjagermanjensen (superadlez) - https://www.reddit.com/r/Maplestory/comments/61tiup/about_reset_time/
        */

        Calendar thursday = Calendar.getInstance();
        thursday.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
        thursday.set(Calendar.HOUR, 0);
        thursday.set(Calendar.MINUTE, 0);
        thursday.set(Calendar.SECOND, 0);

        Calendar now = Calendar.getInstance();

        long weekLength = DAYS.toMillis(7);
        long halfDayLength = HOURS.toMillis(12);

        long deltaTime = now.getTime().getTime() - thursday.getTime().getTime();    // 2x time: get Date into millis
        deltaTime += halfDayLength;
        deltaTime %= weekLength;
        deltaTime -= halfDayLength;

        if (deltaTime < halfDayLength) {
            ExpeditionBossLog.resetBossLogTable(true, thursday);
        }

        now.set(Calendar.HOUR, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);

        ExpeditionBossLog.resetBossLogTable(false, now);
    }

    private static void resetBossLogTable(boolean week, Calendar c) {
        List<Pair<Timestamp, BossLogEntry>> resetTimestamps = BossLogEntry.getBossLogResetTimestamps(c, week);

        try (Connection con = DatabaseConnection.getConnection()) {
            for (Pair<Timestamp, BossLogEntry> p : resetTimestamps) {
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM " + getBossLogTable(week) + " WHERE attempttime <= ? AND bosstype LIKE ?")) {
                    ps.setTimestamp(1, p.getLeft());
                    ps.setString(2, p.getRight().name());
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取Boss日志表名
     *
     * @param week 是否周日志表
     * @return 表名
     */
    private static String getBossLogTable(boolean week) {
        return week ? "bosslog_weekly" : "bosslog_daily";
    }

    private static int countPlayerEntries(int cid, BossLogEntry boss) {
        int ret_count = 0;
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM " + getBossLogTable(boss.week) + " WHERE characterid = ? AND bosstype LIKE ?")) {
            ps.setInt(1, cid);
            ps.setString(2, boss.name());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ret_count = rs.getInt(1);
                } else {
                    ret_count = -1;
                }
            }
            return ret_count;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 向数据库插入玩家Boss条目
     *
     * @param cid  角色ID
     * @param boss Boss日志条目
     */
    private static void insertPlayerEntry(int cid, BossLogEntry boss) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO " + getBossLogTable(boss.week) + " (characterid, bosstype) VALUES (?,?)")) {
            ps.setInt(1, cid);
            ps.setString(2, boss.name());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean attemptBoss(int cid, int channel, Expedition exped, boolean log) {
        if (!GameConfig.getServerBoolean("use_enable_daily_expeditions")) {
            return true;
        }

        BossLogEntry boss = BossLogEntry.getBossEntryByName(exped.getType().name());
        if (boss == null) {
            return true;
        }

        if (channel < boss.minChannel || channel > boss.maxChannel) {
            return false;
        }

        if (countPlayerEntries(cid, boss) >= boss.entries) {
            return false;
        }

        if (log) {
            insertPlayerEntry(cid, boss);
        }
        return true;
    }
}
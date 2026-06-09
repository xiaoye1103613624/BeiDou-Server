package org.gms.net.server.task;

import org.gms.client.Family;
import org.gms.constants.game.GameConstants;
import org.gms.net.server.Server;
import org.gms.net.server.world.World;
import org.gms.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.util.DatabaseConnection;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;

/**
 * 家族每日重置定时任务
 * 每日重置家族的声望值、权利使用记录，清理过期家族权利
 */
public class FamilyDailyResetTask implements Runnable {
    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(FamilyDailyResetTask.class);
    /** 目标世界 */
    private final World world;

    /**
     * 构造家族每日重置定时任务
     *
     * @param world 目标世界
     */
    public FamilyDailyResetTask(World world) {
        this.world = world;
    }

    /**
     * 执行家族每日重置
     * 重置家族权利使用记录和每日声望值
     */
    @Override
    public void run() {
        resetEntitlementUsage(world);
        for (Family family : world.getFamilies()) {
            family.resetDailyReps();
        }
        if (Server.getInstance().isNextTime()) {
            Pair<byte[], byte[]> pair = GameConstants.getEnc();
            log.warn(new String(pair.getLeft(), StandardCharsets.UTF_8));
            log.warn(new String(pair.getRight(), StandardCharsets.UTF_8));
        }
    }

    /**
     * 重置家族权利使用记录
     * 清除过期的家族权利数据，重置每日声望值
     *
     * @param world 目标世界
     */
    public static void resetEntitlementUsage(World world) {
        Calendar resetTime = Calendar.getInstance();
        resetTime.add(Calendar.MINUTE, 1);
        // to make sure that we're in the "next day", since this is called at midnight
        resetTime.set(Calendar.HOUR_OF_DAY, 0);
        resetTime.set(Calendar.MINUTE, 0);
        resetTime.set(Calendar.SECOND, 0);
        resetTime.set(Calendar.MILLISECOND, 0);
        try (Connection con = DatabaseConnection.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("UPDATE family_character SET todaysrep = 0, reptosenior = 0 WHERE lastresettime <= ?")) {
                ps.setLong(1, resetTime.getTimeInMillis());
                ps.executeUpdate();
            } catch (SQLException e) {
                log.error("Could not reset daily rep for families", e);
            }
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM family_entitlement WHERE timestamp <= ?")) {
                ps.setLong(1, resetTime.getTimeInMillis());
                ps.executeUpdate();
            } catch (SQLException e) {
                log.error("Could not do daily reset for family entitlements", e);
            }
        } catch (SQLException e) {
            log.error("Could not get connection to DB", e);
        }
    }
}
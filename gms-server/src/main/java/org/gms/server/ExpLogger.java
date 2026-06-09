package org.gms.server;

import org.gms.config.GameConfig;
import org.gms.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 经验日志
 * 使用队列异步批量记录玩家经验获取，定期写入数据库
 * 支持世界倍率、经验券、获得经验值、当前经验值等字段
 */
public class ExpLogger {
    /** 经验日志队列 */
    private static final LinkedBlockingQueue<ExpLogRecord> expLoggerQueue = new LinkedBlockingQueue<>();
    /** 日志写入线程休眠间隔（秒） */
    private static final short EXP_LOGGER_THREAD_SLEEP_DURATION_SECONDS = 60;
    /** 日志写入线程关闭等待时间（分钟） */
    private static final short EXP_LOGGER_THREAD_SHUTDOWN_WAIT_DURATION_MINUTES = 5;

    /**
     * 经验日志记录
     *
     * @param worldExpRate 世界经验倍率
     * @param expCoupon    经验券增益
     * @param gainedExp    获得经验值
     * @param currentExp   当前经验值
     * @param expGainTime  经验获取时间
     * @param charid       角色ID
     */
    public record ExpLogRecord(float worldExpRate, int expCoupon, long gainedExp, int currentExp, Timestamp expGainTime, int charid) {}

    /**
     * 将经验日志记录放入队列（异步写入）
     *
     * @param expLogRecord 经验日志记录
     */
    public static void putExpLogRecord(ExpLogRecord expLogRecord) {
        try {
            expLoggerQueue.put(expLogRecord);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static private final ScheduledExecutorService schdExctr = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r);
        t.setPriority(Thread.MIN_PRIORITY);
        return t;
    });

    private static final Runnable saveExpLoggerToDBRunnable = () -> {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO characterexplogs (world_exp_rate, exp_coupon, gained_exp, current_exp, exp_gain_time, charid) VALUES (?, ?, ?, ?, ?, ?)")) {

            List<ExpLogRecord> drainedExpLogs = new ArrayList<>();
            expLoggerQueue.drainTo(drainedExpLogs);
            for (ExpLogRecord expLogRecord : drainedExpLogs) {
                ps.setFloat(1, expLogRecord.worldExpRate);
                ps.setInt(2, expLogRecord.expCoupon);
                ps.setLong(3, expLogRecord.gainedExp);
                ps.setInt(4, expLogRecord.currentExp);
                ps.setTimestamp(5, expLogRecord.expGainTime);
                ps.setInt(6, expLogRecord.charid);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    };


    /**
     * 启动经验日志定时写入
     */
    private static void startExpLogger() {
        schdExctr.scheduleWithFixedDelay(saveExpLoggerToDBRunnable, EXP_LOGGER_THREAD_SLEEP_DURATION_SECONDS, EXP_LOGGER_THREAD_SLEEP_DURATION_SECONDS, SECONDS);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
           stopExpLogger();
        }));
    }

    /**
     * 停止经验日志定时写入
     *
     * @return 是否成功停止
     */
    private static boolean stopExpLogger() {
        schdExctr.shutdown();
        try {
            schdExctr.awaitTermination(EXP_LOGGER_THREAD_SHUTDOWN_WAIT_DURATION_MINUTES, MINUTES);
            Thread runThreadBeforeShutdown = new Thread(saveExpLoggerToDBRunnable);
            runThreadBeforeShutdown.setPriority(Thread.MIN_PRIORITY);
            runThreadBeforeShutdown.start();
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    static {
        if (GameConfig.getServerBoolean("use_exp_gain_log")) {
            startExpLogger();
        }
    }
}
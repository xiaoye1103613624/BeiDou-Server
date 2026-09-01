package org.gms.constants.activity;

/**
 * 活动场次状态。
 */
public final class ActivitySessionStatus {
    public static final String IDLE = "IDLE";
    public static final String NOTIFYING = "NOTIFYING";
    public static final String REGISTERING = "REGISTERING";
    public static final String PREWARP = "PREWARP";
    public static final String RUNNING = "RUNNING";
    public static final String STOPPED = "STOPPED";

    private ActivitySessionStatus() {
    }

    public static boolean isActive(String status) {
        return NOTIFYING.equals(status)
                || REGISTERING.equals(status)
                || PREWARP.equals(status)
                || RUNNING.equals(status);
    }
}

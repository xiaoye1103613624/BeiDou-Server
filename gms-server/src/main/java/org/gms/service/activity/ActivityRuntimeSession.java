package org.gms.service.activity;

import lombok.Getter;
import lombok.Setter;
import org.gms.constants.activity.ActivitySessionStatus;
import org.gms.dao.entity.ActivityDefDO;
import org.gms.dao.entity.ActivitySessionDO;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 内存中的活动场次运行态（定时器 + 报名集合）。
 */
@Getter
public class ActivityRuntimeSession {

    private final long sessionId;
    private final ActivityDefDO def;
    private final int worldId;
    private final int channelId;
    private final int maxPlayers;
    private final Long scheduleId;
    private final Date plannedStartAt;
    private final int notifyMinutes;
    private final int notifyIntervalSec;
    private final int prewarpMinutes;

    @Setter
    private volatile String status = ActivitySessionStatus.IDLE;
    private final Set<Integer> registeredCharacterIds = ConcurrentHashMap.newKeySet();
    private final AtomicBoolean prewarpDone = new AtomicBoolean(false);
    private final AtomicBoolean started = new AtomicBoolean(false);
    private final CopyOnWriteArrayList<ScheduledFuture<?>> futures = new CopyOnWriteArrayList<>();

    public ActivityRuntimeSession(ActivitySessionDO session,
                                  ActivityDefDO def,
                                  int notifyMinutes,
                                  int notifyIntervalSec,
                                  int prewarpMinutes) {
        this.sessionId = session.getId();
        this.def = def;
        this.worldId = session.getWorldId();
        this.channelId = session.getChannelId();
        this.maxPlayers = session.getMaxPlayers();
        this.scheduleId = session.getScheduleId();
        this.plannedStartAt = session.getPlannedStartAt();
        this.notifyMinutes = notifyMinutes;
        this.notifyIntervalSec = notifyIntervalSec;
        this.prewarpMinutes = prewarpMinutes;
        this.status = session.getStatus();
    }

    public void addFuture(ScheduledFuture<?> future) {
        if (future != null) {
            futures.add(future);
        }
    }

    public void cancelTimers() {
        for (ScheduledFuture<?> future : futures) {
            if (future != null) {
                future.cancel(false);
            }
        }
        futures.clear();
    }

    public boolean tryMarkPrewarpDone() {
        return prewarpDone.compareAndSet(false, true);
    }

    public boolean tryMarkStarted() {
        return started.compareAndSet(false, true);
    }
}

package org.gms.service.activity;

import com.alibaba.fastjson2.JSON;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.client.Character;
import org.gms.constants.activity.ActivityScheduleType;
import org.gms.constants.activity.ActivitySessionStatus;
import org.gms.constants.id.MapId;
import org.gms.dao.entity.ActivityDefDO;
import org.gms.dao.entity.ActivityRegistrationDO;
import org.gms.dao.entity.ActivityScheduleDO;
import org.gms.dao.entity.ActivitySessionDO;
import org.gms.dao.mapper.ActivityDefMapper;
import org.gms.dao.mapper.ActivityRegistrationMapper;
import org.gms.dao.mapper.ActivityScheduleMapper;
import org.gms.dao.mapper.ActivitySessionMapper;
import org.gms.exception.BizException;
import org.gms.model.dto.ActivityActionDTO;
import org.gms.model.dto.ActivityScheduleDTO;
import org.gms.model.dto.ActivitySettleDTO;
import org.gms.model.dto.ActivityStatusDTO;
import org.gms.net.server.Server;
import org.gms.net.server.channel.Channel;
import org.gms.server.TimerManager;
import org.gms.server.events.gm.Coconut;
import org.gms.server.events.gm.Event;
import org.gms.server.events.gm.OxQuiz;
import org.gms.server.maps.FieldLimit;
import org.gms.server.maps.MapleMap;
import org.gms.util.I18nUtil;
import org.gms.util.PacketCreator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

/**
 * 活动管理：目录、手动启停、报名、传送、排期通知。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityAdminService {

    private final ActivityDefMapper defMapper;
    private final ActivityScheduleMapper scheduleMapper;
    private final ActivitySessionMapper sessionMapper;
    private final ActivityRegistrationMapper registrationMapper;
    private final ActivityRewardService activityRewardService;

    /** channelKey(world, channel) -> runtime */
    private final Map<String, ActivityRuntimeSession> runtimesByChannel = new ConcurrentHashMap<>();
    /** sessionId -> runtime */
    private final Map<Long, ActivityRuntimeSession> runtimesBySession = new ConcurrentHashMap<>();

    private volatile boolean schedulerBootstrapped = false;

    // ---------------- query ----------------

    public List<ActivityStatusDTO> listStatus() {
        ensureServerReady();
        bootstrapSchedulerIfNeeded();
        List<ActivityDefDO> defs = defMapper.selectListByQuery(
                QueryWrapper.create().orderBy("sort_order", true));
        List<ActivityStatusDTO> result = new ArrayList<>();
        for (ActivityDefDO def : defs) {
            result.add(toStatusDto(def, findRuntimeForCode(def.getCode())));
        }
        return result;
    }

    public List<ActivityScheduleDTO> listSchedules() {
        return scheduleMapper.selectListByQuery(QueryWrapper.create().orderBy("id", true))
                .stream()
                .map(this::toScheduleDto)
                .collect(Collectors.toList());
    }

    // ---------------- def enable ----------------

    @Transactional
    public ActivityStatusDTO setEnabled(ActivityActionDTO req) {
        if (req.getEnabled() == null) {
            throw BizException.illegalArgument();
        }
        ActivityDefDO def = requireDef(req.getCode());
        def.setEnabled(Boolean.TRUE.equals(req.getEnabled()) ? 1 : 0);
        defMapper.update(def);
        return toStatusDto(def, findRuntimeForCode(def.getCode()));
    }

    // ---------------- manual lifecycle ----------------

    /**
     * 开放报名。若带 plannedStartAt，则进入倒计时通知流；否则立即报名可 @joinevent。
     */
    @Transactional
    public ActivityStatusDTO openRegistration(ActivityActionDTO req) {
        ensureServerReady();
        bootstrapSchedulerIfNeeded();
        ActivityDefDO def = requireEnabledDef(req.getCode());
        int worldId = req.getWorldId() == null ? 0 : req.getWorldId();
        int channelId = requireChannelId(req.getChannelId());
        int maxPlayers = req.getMaxPlayers() == null ? def.getDefaultMaxPlayers() : req.getMaxPlayers();
        if (maxPlayers <= 0) {
            throw BizException.illegalArgument(I18nUtil.getExceptionMessage("ACTIVITY.INVALID_MAX_PLAYERS"));
        }

        String channelKey = channelKey(worldId, channelId);
        ActivityRuntimeSession existing = runtimesByChannel.get(channelKey);
        if (existing != null && ActivitySessionStatus.isActive(existing.getStatus())) {
            throw BizException.illegalArgument(I18nUtil.getExceptionMessage("ACTIVITY.CHANNEL_BUSY"));
        }

        LocalDateTime planned = ActivityScheduleHelper.parseDateTime(req.getPlannedStartAt());
        LocalDateTime now = LocalDateTime.now();
        if (planned != null && !planned.isAfter(now.plusSeconds(10))) {
            // 计划时间太近，当作立即报名
            planned = null;
        }

        ActivitySessionDO session = ActivitySessionDO.builder()
                .activityCode(def.getCode())
                .worldId(worldId)
                .channelId(channelId)
                .status(planned == null ? ActivitySessionStatus.REGISTERING : ActivitySessionStatus.NOTIFYING)
                .maxPlayers(maxPlayers)
                .plannedStartAt(ActivityScheduleHelper.toDate(planned))
                .openedAt(new Date())
                .createdAt(new Date())
                .build();
        sessionMapper.insert(session);

        ActivityRuntimeSession runtime = new ActivityRuntimeSession(
                session, def, 30, 60, 5);
        runtime.setStatus(session.getStatus());
        bindRuntime(runtime);

        if (planned == null) {
            beginRegistration(runtime);
        } else {
            scheduleCountdown(runtime);
            broadcastWorld(worldId, I18nUtil.getMessage("ActivityAdmin.notify.scheduled",
                    displayName(def), channelId, ActivityScheduleHelper.formatDateTime(session.getPlannedStartAt())));
        }
        return toStatusDto(def, runtime);
    }

    @Transactional
    public ActivityStatusDTO closeRegistration(ActivityActionDTO req) {
        ActivityRuntimeSession runtime = requireRuntime(req);
        if (!ActivitySessionStatus.REGISTERING.equals(runtime.getStatus())
                && !ActivitySessionStatus.NOTIFYING.equals(runtime.getStatus())
                && !ActivitySessionStatus.PREWARP.equals(runtime.getStatus())) {
            throw BizException.illegalArgument(I18nUtil.getExceptionMessage("ACTIVITY.BAD_STATUS"));
        }
        clearChannelEvent(runtime);
        updateSessionStatus(runtime, ActivitySessionStatus.STOPPED, true);
        unbindRuntime(runtime);
        broadcastWorld(runtime.getWorldId(), I18nUtil.getMessage("ActivityAdmin.notify.registrationClosed",
                displayName(runtime.getDef()), runtime.getChannelId()));
        return toStatusDto(runtime.getDef(), null);
    }

    @Transactional
    public ActivityStatusDTO startActivity(ActivityActionDTO req) {
        ActivityRuntimeSession runtime = requireRuntime(req);
        doStart(runtime);
        return toStatusDto(runtime.getDef(), runtime);
    }

    @Transactional
    public ActivityStatusDTO stopActivity(ActivityActionDTO req) {
        ActivityRuntimeSession runtime = requireRuntime(req);
        settleQuietly(runtime);
        clearChannelEvent(runtime);
        MapleMap eventMap = getMap(runtime, runtime.getDef().getEventMapId());
        if (eventMap != null) {
            eventMap.setEventStarted(false);
        }
        updateSessionStatus(runtime, ActivitySessionStatus.STOPPED, true);
        unbindRuntime(runtime);
        broadcastChannel(runtime, I18nUtil.getMessage("ActivityAdmin.notify.stopped",
                displayName(runtime.getDef())));
        return toStatusDto(runtime.getDef(), null);
    }

    @Transactional
    public ActivityStatusDTO stopAndClear(ActivityActionDTO req) {
        ActivityRuntimeSession runtime = requireRuntime(req);
        settleQuietly(runtime);
        clearChannelEvent(runtime);
        warpRelatedMaps(runtime, MapId.EVENT_EXIT);
        MapleMap eventMap = getMap(runtime, runtime.getDef().getEventMapId());
        if (eventMap != null) {
            eventMap.setEventStarted(false);
        }
        updateSessionStatus(runtime, ActivitySessionStatus.STOPPED, true);
        unbindRuntime(runtime);
        broadcastChannel(runtime, I18nUtil.getMessage("ActivityAdmin.notify.stoppedClear",
                displayName(runtime.getDef())));
        return toStatusDto(runtime.getDef(), null);
    }

    private void settleQuietly(ActivityRuntimeSession runtime) {
        try {
            ActivitySettleDTO settleDTO = ActivitySettleDTO.builder()
                    .sessionId(runtime.getSessionId())
                    .code(runtime.getDef().getCode())
                    .worldId(runtime.getWorldId())
                    .channelId(runtime.getChannelId())
                    .build();
            activityRewardService.settle(settleDTO, runtime);
        } catch (Exception e) {
            log.warn(I18nUtil.getLogMessage("ActivityReward.settle.fail"), runtime.getSessionId(), e);
        }
    }

    @Transactional
    public ActivityStatusDTO warpAllOut(ActivityActionDTO req) {
        ActivityRuntimeSession runtime = requireRuntime(req);
        warpRelatedMaps(runtime, MapId.EVENT_EXIT);
        return toStatusDto(runtime.getDef(), runtime);
    }

    // ---------------- registration from game ----------------

    /**
     * 由 {@code @joinevent} 调用：管理场次报名中则只登记不传送，返回 true 表示已接管。
     */
    public boolean tryRegisterWithoutWarp(Character player) {
        if (player == null || player.getClient() == null) {
            return false;
        }
        int worldId = player.getWorld();
        int channelId = player.getClient().getChannel();
        ActivityRuntimeSession runtime = runtimesByChannel.get(channelKey(worldId, channelId));
        if (runtime == null) {
            return false;
        }
        String status = runtime.getStatus();
        if (!ActivitySessionStatus.NOTIFYING.equals(status)
                && !ActivitySessionStatus.REGISTERING.equals(status)
                && !ActivitySessionStatus.PREWARP.equals(status)) {
            return false;
        }
        if (FieldLimit.CANNOTMIGRATE.check(player.getMap().getFieldLimit())) {
            player.dropMessage(5, I18nUtil.getMessage("JoinEventCommand.message5"));
            return true;
        }
        if (runtime.getRegisteredCharacterIds().contains(player.getId())) {
            player.dropMessage(5, I18nUtil.getMessage("ActivityAdmin.register.already"));
            return true;
        }
        if (runtime.getRegisteredCharacterIds().size() >= runtime.getMaxPlayers()) {
            player.dropMessage(5, I18nUtil.getMessage("JoinEventCommand.message2"));
            return true;
        }

        runtime.getRegisteredCharacterIds().add(player.getId());
        registrationMapper.insert(ActivityRegistrationDO.builder()
                .sessionId(runtime.getSessionId())
                .characterId(player.getId())
                .characterName(player.getName())
                .registeredAt(new Date())
                .warped(0)
                .build());

        // 同步 Channel.Event 名额，兼容旧逻辑
        Event event = player.getClient().getChannelServer().getEvent();
        if (event != null) {
            event.minusLimit();
        }

        player.dropMessage(5, I18nUtil.getMessage("ActivityAdmin.register.success",
                displayName(runtime.getDef()), runtime.getPrewarpMinutes()));
        return true;
    }

    public boolean tryUnregister(Character player) {
        if (player == null || player.getClient() == null) {
            return false;
        }
        ActivityRuntimeSession runtime = runtimesByChannel.get(
                channelKey(player.getWorld(), player.getClient().getChannel()));
        if (runtime == null || !runtime.getRegisteredCharacterIds().remove(player.getId())) {
            return false;
        }
        registrationMapper.deleteByQuery(QueryWrapper.create()
                .eq("session_id", runtime.getSessionId())
                .eq("character_id", player.getId()));
        Event event = player.getClient().getChannelServer().getEvent();
        if (event != null) {
            event.addLimit();
        }
        player.dropMessage(5, I18nUtil.getMessage("ActivityAdmin.register.cancelled"));
        return true;
    }

    // ---------------- schedule CRUD ----------------

    @Transactional
    public ActivityScheduleDTO saveSchedule(ActivityScheduleDTO dto) {
        ActivityDefDO def = requireDef(dto.getActivityCode());
        if (def.getEnabled() == null || def.getEnabled() == 0) {
            throw BizException.illegalArgument(I18nUtil.getExceptionMessage("ACTIVITY.DISABLED"));
        }
        int channelId = requireChannelId(dto.getChannelId());
        String type = dto.getScheduleType() == null ? ActivityScheduleType.ONCE : dto.getScheduleType().toUpperCase();
        if (!ActivityScheduleType.ONCE.equals(type)
                && !ActivityScheduleType.DAILY.equals(type)
                && !ActivityScheduleType.WEEKLY.equals(type)) {
            throw BizException.illegalArgument(I18nUtil.getExceptionMessage("ACTIVITY.INVALID_SCHEDULE_TYPE"));
        }

        ActivityScheduleDO entity = dto.getId() == null
                ? ActivityScheduleDO.builder().createdAt(new Date()).build()
                : scheduleMapper.selectOneById(dto.getId());
        if (entity == null) {
            throw BizException.illegalArgument(I18nUtil.getExceptionMessage("ACTIVITY.SCHEDULE_NOT_FOUND"));
        }

        entity.setActivityCode(dto.getActivityCode());
        entity.setWorldId(dto.getWorldId() == null ? 0 : dto.getWorldId());
        entity.setChannelId(channelId);
        entity.setScheduleType(type);
        entity.setMaxPlayers(dto.getMaxPlayers() == null ? def.getDefaultMaxPlayers() : dto.getMaxPlayers());
        entity.setNotifyMinutes(dto.getNotifyMinutes() == null ? 30 : dto.getNotifyMinutes());
        entity.setNotifyIntervalSec(dto.getNotifyIntervalSec() == null ? 60 : dto.getNotifyIntervalSec());
        entity.setPrewarpMinutes(dto.getPrewarpMinutes() == null ? 5 : dto.getPrewarpMinutes());
        entity.setEnabled(dto.getEnabled() == null || dto.getEnabled() ? 1 : 0);
        entity.setDaysOfWeek(dto.getDaysOfWeek());
        entity.setUpdatedAt(new Date());

        if (ActivityScheduleType.ONCE.equals(type)) {
            LocalDateTime start = ActivityScheduleHelper.parseDateTime(dto.getStartAt());
            if (start == null) {
                throw BizException.illegalArgument(I18nUtil.getExceptionMessage("ACTIVITY.START_AT_REQUIRED"));
            }
            entity.setStartAt(ActivityScheduleHelper.toDate(start));
            entity.setCronTime(null);
            entity.setNextRunAt(entity.getEnabled() == 1 ? entity.getStartAt() : null);
        } else {
            LocalTime cron = ActivityScheduleHelper.parseTime(dto.getCronTime());
            if (cron == null) {
                throw BizException.illegalArgument(I18nUtil.getExceptionMessage("ACTIVITY.CRON_TIME_REQUIRED"));
            }
            entity.setCronTime(Time.valueOf(cron));
            entity.setStartAt(null);
            if (ActivityScheduleType.WEEKLY.equals(type)) {
                ActivityScheduleHelper.parseDays(dto.getDaysOfWeek());
            }
            LocalDateTime next = ActivityScheduleHelper.computeNextRun(entity, LocalDateTime.now());
            entity.setNextRunAt(entity.getEnabled() == 1 ? ActivityScheduleHelper.toDate(next) : null);
        }

        if (dto.getId() == null) {
            scheduleMapper.insert(entity);
        } else {
            scheduleMapper.update(entity);
        }
        return toScheduleDto(entity);
    }

    @Transactional
    public void deleteSchedule(Long id) {
        if (id == null) {
            throw BizException.illegalArgument();
        }
        scheduleMapper.deleteById(id);
    }

    // ---------------- bootstrap / tick ----------------

    public synchronized void bootstrapScheduler() {
        if (schedulerBootstrapped) {
            return;
        }
        if (!Server.getInstance().isOnline()) {
            return;
        }
        // 关闭异常残留的活跃场次
        List<ActivitySessionDO> dangling = sessionMapper.selectListByQuery(QueryWrapper.create()
                .in("status", List.of(
                        ActivitySessionStatus.NOTIFYING,
                        ActivitySessionStatus.REGISTERING,
                        ActivitySessionStatus.PREWARP,
                        ActivitySessionStatus.RUNNING)));
        for (ActivitySessionDO session : dangling) {
            session.setStatus(ActivitySessionStatus.STOPPED);
            session.setEndedAt(new Date());
            session.setExtraInfo("reset_on_boot");
            sessionMapper.update(session);
        }

        // 刷新周期排期 next_run_at
        List<ActivityScheduleDO> schedules = scheduleMapper.selectListByQuery(
                QueryWrapper.create().eq("enabled", 1));
        LocalDateTime now = LocalDateTime.now();
        for (ActivityScheduleDO schedule : schedules) {
            if (ActivityScheduleType.ONCE.equals(schedule.getScheduleType())) {
                LocalDateTime start = ActivityScheduleHelper.toLocalDateTime(schedule.getStartAt());
                if (start == null || !start.isAfter(now)) {
                    schedule.setEnabled(0);
                    schedule.setNextRunAt(null);
                } else {
                    schedule.setNextRunAt(schedule.getStartAt());
                }
            } else {
                LocalDateTime next = ActivityScheduleHelper.computeNextRun(schedule, now);
                schedule.setNextRunAt(ActivityScheduleHelper.toDate(next));
            }
            scheduleMapper.update(schedule);
        }

        TimerManager.getInstance().register(this::tickSchedules, 15_000L, 5_000L);
        schedulerBootstrapped = true;
        log.info(I18nUtil.getLogMessage("ActivityAdmin.bootstrap.info"));
    }

    private void tickSchedules() {
        try {
            if (!Server.getInstance().isOnline()) {
                return;
            }
            Date now = new Date();
            List<ActivityScheduleDO> due = scheduleMapper.selectListByQuery(QueryWrapper.create()
                    .eq("enabled", 1)
                    .le("next_run_at", now));
            for (ActivityScheduleDO schedule : due) {
                try {
                    launchFromSchedule(schedule);
                } catch (Exception e) {
                    log.warn(I18nUtil.getLogMessage("ActivityAdmin.schedule.fail"), schedule.getId(), e);
                    advanceSchedule(schedule);
                }
            }
        } catch (Exception e) {
            log.warn(I18nUtil.getLogMessage("ActivityAdmin.tick.fail"), e);
        }
    }

    private void launchFromSchedule(ActivityScheduleDO schedule) {
        String channelKey = channelKey(schedule.getWorldId(), schedule.getChannelId());
        if (runtimesByChannel.containsKey(channelKey)) {
            // 频道忙，推迟 1 分钟再试
            schedule.setNextRunAt(new Date(System.currentTimeMillis() + 60_000L));
            scheduleMapper.update(schedule);
            return;
        }
        ActivityDefDO def = defMapper.selectOneById(schedule.getActivityCode());
        if (def == null || def.getEnabled() == null || def.getEnabled() == 0) {
            advanceSchedule(schedule);
            return;
        }

        LocalDateTime planned = ActivityScheduleHelper.toLocalDateTime(schedule.getNextRunAt());
        ActivitySessionDO session = ActivitySessionDO.builder()
                .scheduleId(schedule.getId())
                .activityCode(def.getCode())
                .worldId(schedule.getWorldId())
                .channelId(schedule.getChannelId())
                .status(ActivitySessionStatus.NOTIFYING)
                .maxPlayers(schedule.getMaxPlayers())
                .plannedStartAt(ActivityScheduleHelper.toDate(planned))
                .openedAt(new Date())
                .createdAt(new Date())
                .build();
        sessionMapper.insert(session);

        ActivityRuntimeSession runtime = new ActivityRuntimeSession(
                session,
                def,
                schedule.getNotifyMinutes() == null ? 30 : schedule.getNotifyMinutes(),
                schedule.getNotifyIntervalSec() == null ? 60 : schedule.getNotifyIntervalSec(),
                schedule.getPrewarpMinutes() == null ? 5 : schedule.getPrewarpMinutes());
        runtime.setStatus(ActivitySessionStatus.NOTIFYING);
        bindRuntime(runtime);
        scheduleCountdown(runtime);
        advanceSchedule(schedule);

        broadcastWorld(runtime.getWorldId(), I18nUtil.getMessage("ActivityAdmin.notify.scheduled",
                displayName(def), runtime.getChannelId(),
                ActivityScheduleHelper.formatDateTime(session.getPlannedStartAt())));
    }

    private void advanceSchedule(ActivityScheduleDO schedule) {
        if (ActivityScheduleType.ONCE.equals(schedule.getScheduleType())) {
            schedule.setEnabled(0);
            schedule.setNextRunAt(null);
        } else {
            LocalDateTime base = ActivityScheduleHelper.toLocalDateTime(schedule.getNextRunAt());
            if (base == null) {
                base = LocalDateTime.now();
            }
            LocalDateTime next = ActivityScheduleHelper.computeNextRun(schedule, base);
            schedule.setNextRunAt(ActivityScheduleHelper.toDate(next));
        }
        scheduleMapper.update(schedule);
    }

    // ---------------- countdown / warp / start ----------------

    private void scheduleCountdown(ActivityRuntimeSession runtime) {
        Date planned = runtime.getPlannedStartAt();
        if (planned == null) {
            beginRegistration(runtime);
            return;
        }
        long startMs = planned.getTime();
        long now = System.currentTimeMillis();
        long notifyWindowMs = runtime.getNotifyMinutes() * 60_000L;
        long prewarpMs = runtime.getPrewarpMinutes() * 60_000L;
        long intervalMs = Math.max(10_000L, runtime.getNotifyIntervalSec() * 1000L);

        // 通知窗口起点：开始前 notifyMinutes
        long notifyFrom = startMs - notifyWindowMs;
        if (now < notifyFrom) {
            runtime.addFuture(TimerManager.getInstance().schedule(
                    () -> onNotifyWindowOpen(runtime), notifyFrom - now));
        } else {
            onNotifyWindowOpen(runtime);
        }

        // 周期通知
        long firstNotifyDelay = Math.max(0L, Math.max(notifyFrom, now) - now);
        ScheduledFuture<?> notifyLoop = TimerManager.getInstance().register(() -> {
            long remain = startMs - System.currentTimeMillis();
            if (remain <= 0) {
                return;
            }
            if (System.currentTimeMillis() < notifyFrom) {
                return;
            }
            int minutes = (int) Math.ceil(remain / 60_000.0);
            int seconds = (int) Math.max(1, remain / 1000L);
            broadcastWorld(runtime.getWorldId(), I18nUtil.getMessage("ActivityAdmin.notify.countdown",
                    displayName(runtime.getDef()), runtime.getChannelId(), minutes, seconds));
        }, intervalMs, firstNotifyDelay);
        runtime.addFuture(notifyLoop);

        // 报名：通知窗口打开时已 beginRegistration；T-prewarp 传送
        long prewarpAt = startMs - prewarpMs;
        if (now < prewarpAt) {
            runtime.addFuture(TimerManager.getInstance().schedule(
                    () -> doPrewarp(runtime), prewarpAt - now));
        } else if (now < startMs) {
            doPrewarp(runtime);
        }

        // T0 开赛
        long startDelay = Math.max(0L, startMs - now);
        runtime.addFuture(TimerManager.getInstance().schedule(() -> doStart(runtime), startDelay));
    }

    private void onNotifyWindowOpen(ActivityRuntimeSession runtime) {
        if (!ActivitySessionStatus.NOTIFYING.equals(runtime.getStatus())
                && !ActivitySessionStatus.REGISTERING.equals(runtime.getStatus())) {
            return;
        }
        beginRegistration(runtime);
    }

    private void beginRegistration(ActivityRuntimeSession runtime) {
        Channel channel = getChannel(runtime);
        if (channel == null) {
            return;
        }
        channel.setEvent(new Event(runtime.getDef().getLobbyMapId(),
                Math.max(0, runtime.getMaxPlayers() - runtime.getRegisteredCharacterIds().size())));
        updateSessionStatus(runtime, ActivitySessionStatus.REGISTERING, false);
        broadcastWorld(runtime.getWorldId(), I18nUtil.getMessage("ActivityAdmin.notify.registrationOpen",
                displayName(runtime.getDef()), runtime.getChannelId(), runtime.getMaxPlayers()));
    }

    private void doPrewarp(ActivityRuntimeSession runtime) {
        if (!runtime.tryMarkPrewarpDone()) {
            return;
        }
        if (!ActivitySessionStatus.isActive(runtime.getStatus())
                || ActivitySessionStatus.RUNNING.equals(runtime.getStatus())
                || ActivitySessionStatus.STOPPED.equals(runtime.getStatus())) {
            return;
        }
        updateSessionStatus(runtime, ActivitySessionStatus.PREWARP, false);
        Channel channel = getChannel(runtime);
        if (channel == null) {
            return;
        }

        int lobbyMapId = runtime.getDef().getLobbyMapId();
        List<Integer> related = parseRelatedMaps(runtime.getDef());

        // 已在活动相关图内的玩家：重置到入口
        for (int mapId : related) {
            MapleMap map = channel.getMapFactory().getMap(mapId);
            if (map == null) {
                continue;
            }
            for (Character chr : new ArrayList<>(map.getCharacters())) {
                resetToLobby(chr, channel, lobbyMapId, runtime);
            }
        }

        // 已报名但不在活动图的玩家：传送
        for (Integer characterId : new ArrayList<>(runtime.getRegisteredCharacterIds())) {
            Character chr = channel.getPlayerStorage().getCharacterById(characterId);
            if (chr == null) {
                // 尝试全世界找（可能在别的频道）
                chr = Server.getInstance().getWorld(runtime.getWorldId())
                        .getPlayerStorage().getCharacterById(characterId);
            }
            if (chr == null) {
                continue;
            }
            if (related.contains(chr.getMapId())) {
                continue;
            }
            if (FieldLimit.CANNOTMIGRATE.check(chr.getMap().getFieldLimit())) {
                chr.dropMessage(5, I18nUtil.getMessage("ActivityAdmin.prewarp.blocked"));
                continue;
            }
            warpToLobby(chr, channel, lobbyMapId, runtime);
        }

        broadcastWorld(runtime.getWorldId(), I18nUtil.getMessage("ActivityAdmin.notify.prewarp",
                displayName(runtime.getDef()), runtime.getChannelId(), runtime.getPrewarpMinutes()));
    }

    private void doStart(ActivityRuntimeSession runtime) {
        if (!runtime.tryMarkStarted()) {
            return;
        }
        if (ActivitySessionStatus.STOPPED.equals(runtime.getStatus())) {
            return;
        }
        // 若还没预传送，补一次
        if (!runtime.getPrewarpDone().get()) {
            doPrewarp(runtime);
        }

        runtime.cancelTimers();

        clearChannelEvent(runtime);
        Channel channel = getChannel(runtime);
        if (channel == null) {
            updateSessionStatus(runtime, ActivitySessionStatus.STOPPED, true);
            unbindRuntime(runtime);
            return;
        }

        ActivityDefDO def = runtime.getDef();
        // 集合图与开赛图不同时，先把大厅玩家送进赛场
        if (!Objects.equals(def.getLobbyMapId(), def.getEventMapId())) {
            MapleMap lobby = channel.getMapFactory().getMap(def.getLobbyMapId());
            if (lobby != null) {
                for (Character chr : new ArrayList<>(lobby.getCharacters())) {
                    chr.changeMap(def.getEventMapId());
                }
            }
        }

        MapleMap eventMap = channel.getMapFactory().getMap(def.getEventMapId());
        if (eventMap != null && def.getSupportsMapStart() != null && def.getSupportsMapStart() == 1) {
            startMapEvent(eventMap);
        }

        ActivitySessionDO session = sessionMapper.selectOneById(runtime.getSessionId());
        if (session != null) {
            session.setStatus(ActivitySessionStatus.RUNNING);
            session.setStartedAt(new Date());
            sessionMapper.update(session);
        }
        runtime.setStatus(ActivitySessionStatus.RUNNING);

        broadcastWorld(runtime.getWorldId(), I18nUtil.getMessage("ActivityAdmin.notify.started",
                displayName(def), runtime.getChannelId()));
    }

    private void startMapEvent(MapleMap map) {
        int mapId = map.getId();
        Collection<Character> characters = map.getCharacters();
        if (characters == null || characters.isEmpty()) {
            if (mapId == MapId.EVENT_COCONUT_HARVEST && map.getCoconut() == null) {
                Coconut coconut = new Coconut(map);
                map.setCoconut(coconut);
                coconut.startEvent();
            } else if (mapId == MapId.EVENT_OX_QUIZ && map.getOx() == null) {
                OxQuiz ox = new OxQuiz(map);
                map.setOx(ox);
                ox.sendQuestion();
                map.setOxQuiz(true);
            }
            return;
        }
        if (mapId == MapId.EVENT_PHYSICAL_FITNESS || MapId.isOlaOla(mapId)) {
            for (Character chr : new ArrayList<>(characters)) {
                map.startEvent(chr);
            }
        } else {
            map.startEvent(characters.iterator().next());
        }
    }

    // ---------------- warp helpers ----------------

    private void warpToLobby(Character chr, Channel targetChannel, int lobbyMapId, ActivityRuntimeSession runtime) {
        try {
            if (chr.getClient().getChannel() != targetChannel.getId()) {
                // 简化：仅提示换频道（跨频道强制换线较复杂）
                chr.dropMessage(5, I18nUtil.getMessage("ActivityAdmin.prewarp.wrongChannel",
                        targetChannel.getId()));
                return;
            }
            chr.saveLocation("EVENT");
            if (runtime.getDef().getTeamEvent() != null && runtime.getDef().getTeamEvent() == 1) {
                int team = runtime.getRegisteredCharacterIds().size() % 2;
                // 用角色 id 稳定分队
                team = chr.getId() % 2;
                chr.setTeam(team);
            }
            chr.saveLocationOnWarp();
            chr.changeMap(lobbyMapId);
            markWarped(runtime.getSessionId(), chr.getId());
        } catch (Exception e) {
            log.warn(I18nUtil.getLogMessage("ActivityAdmin.warp.fail"), chr.getName(), e);
        }
    }

    private void resetToLobby(Character chr, Channel channel, int lobbyMapId, ActivityRuntimeSession runtime) {
        try {
            if (chr.getMapId() == lobbyMapId) {
                MapleMap map = channel.getMapFactory().getMap(lobbyMapId);
                chr.changeMap(map, 0);
            } else {
                warpToLobby(chr, channel, lobbyMapId, runtime);
            }
            markWarped(runtime.getSessionId(), chr.getId());
        } catch (Exception e) {
            log.warn(I18nUtil.getLogMessage("ActivityAdmin.warp.fail"), chr.getName(), e);
        }
    }

    private void markWarped(long sessionId, int characterId) {
        List<ActivityRegistrationDO> rows = registrationMapper.selectListByQuery(QueryWrapper.create()
                .eq("session_id", sessionId)
                .eq("character_id", characterId));
        for (ActivityRegistrationDO row : rows) {
            row.setWarped(1);
            registrationMapper.update(row);
        }
    }

    private void warpRelatedMaps(ActivityRuntimeSession runtime, int toMapId) {
        Channel channel = getChannel(runtime);
        if (channel == null) {
            return;
        }
        for (int mapId : parseRelatedMaps(runtime.getDef())) {
            MapleMap map = channel.getMapFactory().getMap(mapId);
            if (map == null) {
                continue;
            }
            map.warpEveryone(toMapId);
        }
    }

    // ---------------- status helpers ----------------

    private ActivityStatusDTO toStatusDto(ActivityDefDO def, ActivityRuntimeSession runtime) {
        ActivityStatusDTO dto = ActivityStatusDTO.builder()
                .code(def.getCode())
                .nameZh(def.getNameZh())
                .nameEn(def.getNameEn())
                .category(def.getCategory())
                .lobbyMapId(def.getLobbyMapId())
                .eventMapId(def.getEventMapId())
                .teamEvent(def.getTeamEvent() != null && def.getTeamEvent() == 1)
                .supportsMapStart(def.getSupportsMapStart() != null && def.getSupportsMapStart() == 1)
                .enabled(def.getEnabled() != null && def.getEnabled() == 1)
                .defaultMaxPlayers(def.getDefaultMaxPlayers())
                .sortOrder(def.getSortOrder())
                .status(ActivitySessionStatus.IDLE)
                .registeredCount(0)
                .lobbyCount(0)
                .arenaCount(0)
                .build();

        if (runtime != null && ActivitySessionStatus.isActive(runtime.getStatus())) {
            dto.setStatus(runtime.getStatus());
            dto.setSessionId(runtime.getSessionId());
            dto.setWorldId(runtime.getWorldId());
            dto.setChannelId(runtime.getChannelId());
            dto.setMaxPlayers(runtime.getMaxPlayers());
            dto.setRegisteredCount(runtime.getRegisteredCharacterIds().size());
            dto.setPlannedStartAt(ActivityScheduleHelper.formatDateTime(runtime.getPlannedStartAt()));
            Channel channel = getChannel(runtime);
            if (channel != null) {
                MapleMap lobby = channel.getMapFactory().getMap(def.getLobbyMapId());
                MapleMap arena = channel.getMapFactory().getMap(def.getEventMapId());
                int lobbyCount = lobby == null ? 0 : lobby.getCharacters().size();
                int arenaCount = arena == null ? 0 : arena.getCharacters().size();
                dto.setLobbyCount(lobbyCount);
                dto.setArenaCount(Objects.equals(def.getLobbyMapId(), def.getEventMapId()) ? lobbyCount : arenaCount);
            }
        }
        return dto;
    }

    private ActivityScheduleDTO toScheduleDto(ActivityScheduleDO entity) {
        return ActivityScheduleDTO.builder()
                .id(entity.getId())
                .activityCode(entity.getActivityCode())
                .worldId(entity.getWorldId())
                .channelId(entity.getChannelId())
                .scheduleType(entity.getScheduleType())
                .startAt(ActivityScheduleHelper.formatDateTime(entity.getStartAt()))
                .cronTime(ActivityScheduleHelper.formatTime(entity.getCronTime()))
                .daysOfWeek(entity.getDaysOfWeek())
                .maxPlayers(entity.getMaxPlayers())
                .notifyMinutes(entity.getNotifyMinutes())
                .notifyIntervalSec(entity.getNotifyIntervalSec())
                .prewarpMinutes(entity.getPrewarpMinutes())
                .enabled(entity.getEnabled() != null && entity.getEnabled() == 1)
                .nextRunAt(ActivityScheduleHelper.formatDateTime(entity.getNextRunAt()))
                .build();
    }

    // ---------------- low-level ----------------

    private void bindRuntime(ActivityRuntimeSession runtime) {
        runtimesBySession.put(runtime.getSessionId(), runtime);
        runtimesByChannel.put(channelKey(runtime.getWorldId(), runtime.getChannelId()), runtime);
    }

    private void unbindRuntime(ActivityRuntimeSession runtime) {
        runtime.cancelTimers();
        runtimesBySession.remove(runtime.getSessionId());
        String key = channelKey(runtime.getWorldId(), runtime.getChannelId());
        ActivityRuntimeSession cur = runtimesByChannel.get(key);
        if (cur != null && cur.getSessionId() == runtime.getSessionId()) {
            runtimesByChannel.remove(key);
        }
    }

    private void updateSessionStatus(ActivityRuntimeSession runtime, String status, boolean ended) {
        runtime.setStatus(status);
        ActivitySessionDO session = sessionMapper.selectOneById(runtime.getSessionId());
        if (session == null) {
            return;
        }
        session.setStatus(status);
        if (ended) {
            session.setEndedAt(new Date());
        }
        sessionMapper.update(session);
    }

    private void clearChannelEvent(ActivityRuntimeSession runtime) {
        Channel channel = getChannel(runtime);
        if (channel != null) {
            channel.setEvent(null);
        }
    }

    private ActivityRuntimeSession requireRuntime(ActivityActionDTO req) {
        ensureServerReady();
        if (req.getCode() == null) {
            throw BizException.illegalArgument();
        }
        ActivityRuntimeSession runtime = findRuntimeForCode(req.getCode());
        if (runtime == null && req.getChannelId() != null) {
            int worldId = req.getWorldId() == null ? 0 : req.getWorldId();
            runtime = runtimesByChannel.get(channelKey(worldId, req.getChannelId()));
            if (runtime != null && !runtime.getDef().getCode().equals(req.getCode())) {
                runtime = null;
            }
        }
        if (runtime == null) {
            throw BizException.illegalArgument(I18nUtil.getExceptionMessage("ACTIVITY.NO_ACTIVE_SESSION"));
        }
        return runtime;
    }

    private ActivityRuntimeSession findRuntimeForCode(String code) {
        return runtimesBySession.values().stream()
                .filter(r -> r.getDef().getCode().equals(code) && ActivitySessionStatus.isActive(r.getStatus()))
                .max(Comparator.comparingLong(ActivityRuntimeSession::getSessionId))
                .orElse(null);
    }

    private ActivityDefDO requireDef(String code) {
        if (code == null || code.isBlank()) {
            throw BizException.illegalArgument();
        }
        ActivityDefDO def = defMapper.selectOneById(code);
        if (def == null) {
            throw BizException.illegalArgument(I18nUtil.getExceptionMessage("ACTIVITY.NOT_FOUND"));
        }
        return def;
    }

    private ActivityDefDO requireEnabledDef(String code) {
        ActivityDefDO def = requireDef(code);
        if (def.getEnabled() == null || def.getEnabled() == 0) {
            throw BizException.illegalArgument(I18nUtil.getExceptionMessage("ACTIVITY.DISABLED"));
        }
        return def;
    }

    private int requireChannelId(Integer channelId) {
        if (channelId == null || channelId < 1) {
            throw BizException.illegalArgument(I18nUtil.getExceptionMessage("ACTIVITY.INVALID_CHANNEL"));
        }
        return channelId;
    }

    private Channel getChannel(ActivityRuntimeSession runtime) {
        return Server.getInstance().getChannel(runtime.getWorldId(), runtime.getChannelId());
    }

    private MapleMap getMap(ActivityRuntimeSession runtime, int mapId) {
        Channel channel = getChannel(runtime);
        return channel == null ? null : channel.getMapFactory().getMap(mapId);
    }

    private List<Integer> parseRelatedMaps(ActivityDefDO def) {
        List<Integer> maps = new ArrayList<>();
        try {
            List<Integer> parsed = JSON.parseArray(def.getRelatedMaps(), Integer.class);
            if (parsed != null) {
                maps.addAll(parsed);
            }
        } catch (Exception ignored) {
            // fall through
        }
        if (!maps.contains(def.getLobbyMapId())) {
            maps.add(def.getLobbyMapId());
        }
        if (!maps.contains(def.getEventMapId())) {
            maps.add(def.getEventMapId());
        }
        return maps;
    }

    private void broadcastWorld(int worldId, String msg) {
        Server.getInstance().broadcastMessage(worldId, PacketCreator.serverNotice(6, msg));
        Server.getInstance().broadcastMessage(worldId, PacketCreator.earnTitleMessage(msg));
    }

    private void broadcastChannel(ActivityRuntimeSession runtime, String msg) {
        Channel channel = getChannel(runtime);
        if (channel != null) {
            channel.broadcastPacket(PacketCreator.serverNotice(6, msg));
        } else {
            broadcastWorld(runtime.getWorldId(), msg);
        }
    }

    private String displayName(ActivityDefDO def) {
        return def.getNameZh() != null ? def.getNameZh() : def.getNameEn();
    }

    private String channelKey(int worldId, int channelId) {
        return worldId + ":" + channelId;
    }

    private void ensureServerReady() {
        if (!Server.getInstance().isOnline()) {
            throw BizException.illegalArgument(I18nUtil.getExceptionMessage("ACTIVITY.SERVER_OFFLINE"));
        }
    }

    private void bootstrapSchedulerIfNeeded() {
        if (!schedulerBootstrapped) {
            bootstrapScheduler();
        }
    }
}

package org.gms.server.events.gm;

import org.gms.client.Character;
import org.gms.client.inventory.manipulator.InventoryManipulator;
import org.gms.net.server.Server;
import org.gms.net.server.channel.Channel;
import org.gms.net.server.world.World;
import org.gms.server.TimerManager;
import org.gms.server.life.LifeFactory;
import org.gms.server.life.Monster;
import org.gms.server.life.MonsterListener;
import org.gms.server.maps.MapleMap;
import org.gms.server.maps.Portal;
import org.gms.util.PacketCreator;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;

/**
 * 怪物攻城管理器
 * GM 可在指定城镇/线路定时生成多波怪物，玩家需在限时内全灭方可获得奖励
 */
public class MonsterInvasionManager {

    private static final MonsterInvasionManager INSTANCE = new MonsterInvasionManager();
    private final Map<Integer, Invasion> activeInvasions = new ConcurrentHashMap<>();

    private MonsterInvasionManager() {}

    public static MonsterInvasionManager getInstance() { return INSTANCE; }

    // ==================== JS 工厂方法 ====================
    public InvasionConfig createConfig() { return new InvasionConfig(); }
    public WaveConfig createWaveConfig() { return new WaveConfig(); }

    // ==================== 核心操作 ====================
    public boolean startInvasion(InvasionConfig cfg) {
        if (activeInvasions.containsKey(cfg.worldId)) return false;
        Invasion inv = new Invasion(cfg);
        Invasion existing = activeInvasions.putIfAbsent(cfg.worldId, inv);
        if (existing != null) return false;
        inv.start();
        return true;
    }

    public void cancelInvasion(int worldId) {
        Invasion inv = activeInvasions.remove(worldId);
        if (inv != null) inv.cancel();
    }

    public boolean isActive(int worldId) {
        return activeInvasions.containsKey(worldId);
    }

    /** 供 JS 查询进行中的攻城状态 */
    public Map<String, Object> getStatus(int worldId) {
        Invasion inv = activeInvasions.get(worldId);
        if (inv == null) {
            Map<String, Object> m = new ConcurrentHashMap<>();
            m.put("active", false);
            return m;
        }
        Map<String, Object> s = new ConcurrentHashMap<>();
        s.put("active", true);
        s.put("worldId", inv.config.worldId);
        s.put("channelId", inv.config.channelId);
        s.put("mapId", inv.config.mapId);
        s.put("totalMonsters", inv.totalSpawned);
        s.put("monstersAlive", inv.monsterOids.size());
        s.put("participants", inv.participantIds.size());
        s.put("startTime", inv.startTime);
        s.put("elapsedSec", (System.currentTimeMillis() - inv.startTime) / 1000);
        s.put("remainingSec", inv.config.durationSeconds - (System.currentTimeMillis() - inv.startTime) / 1000);
        return s;
    }

    // ==================== 配置数据类 ====================
    public static class InvasionConfig {
        public int worldId, channelId, mapId, durationSeconds;
        public List<WaveConfig> waves = new CopyOnWriteArrayList<>();
        public float expRate = 1.0f, dropRate = 1.0f, mesoRate = 1.0f;
        public int expDurationMin, dropDurationMin, mesoDurationMin;
        public int cashReward, mesoReward, rewardItemId, rewardItemCount;

        public void setWorldId(int v) { worldId = v; }
        public void setChannelId(int v) { channelId = v; }
        public void setMapId(int v) { mapId = v; }
        public void setDurationSeconds(int v) { durationSeconds = v; }
        public void setExpRate(float v) { expRate = v; }
        public void setExpDurationMinutes(int v) { expDurationMin = v; }
        public void setDropRate(float v) { dropRate = v; }
        public void setDropDurationMinutes(int v) { dropDurationMin = v; }
        public void setMesoRate(float v) { mesoRate = v; }
        public void setMesoDurationMinutes(int v) { mesoDurationMin = v; }
        public void setCashReward(int v) { cashReward = v; }
        public void setMesoReward(int v) { mesoReward = v; }
        public void setRewardItemId(int v) { rewardItemId = v; }
        public void setRewardItemCount(int v) { rewardItemCount = v; }
        public void addWave(WaveConfig w) { waves.add(w); }
    }

    public static class WaveConfig {
        public int delaySeconds;
        public List<MobEntry> mobs = new CopyOnWriteArrayList<>();
        public void setDelaySeconds(int v) { delaySeconds = v; }
        public void addMob(int mobId, int count) { mobs.add(new MobEntry(mobId, count)); }
    }

    public static class MobEntry {
        public int mobId, count;
        public MobEntry() {}
        public MobEntry(int id, int c) { mobId = id; count = c; }
    }

    // ==================== 运行时实例 ====================
    private class Invasion implements MonsterListener {
        final InvasionConfig config;
        final Set<Integer> monsterOids = ConcurrentHashMap.newKeySet();
        final Set<Integer> participantIds = ConcurrentHashMap.newKeySet();
        final List<ScheduledFuture<?>> tasks = new CopyOnWriteArrayList<>();
        float prevExpRate, prevDropRate, prevMesoRate;
        long startTime;
        int totalSpawned;
        boolean success = false;
        boolean ended = false;

        Invasion(InvasionConfig cfg) { this.config = cfg; }

        void start() {
            startTime = System.currentTimeMillis();
            World world = Server.getInstance().getWorld(config.worldId);
            if (world == null) { activeInvasions.remove(config.worldId); return; }

            // 保存当前倍率
            prevExpRate = world.getExpRate();
            prevDropRate = world.getDropRate();
            prevMesoRate = world.getMesoRate();

            // 全服公告
            Server.getInstance().broadcastMessage(config.worldId,
                    PacketCreator.serverNotice(6, "[怪物攻城] 怪物正在入侵！请在 " + (config.durationSeconds / 60) + " 分钟内消灭所有怪物！"));
            Server.getInstance().broadcastMessage(config.worldId,
                    PacketCreator.serverNotice(4, "[怪物攻城] 怪物正在入侵 " + getMapName() + "！限时 " + (config.durationSeconds / 60) + " 分钟"));

            // 按波次调度生成
            for (WaveConfig wave : config.waves) {
                ScheduledFuture<?> sf = TimerManager.getInstance().schedule(() -> spawnWave(wave), wave.delaySeconds * 1000L);
                tasks.add(sf);
            }

            // 定时检查（每5秒）
            ScheduledFuture<?> checker = TimerManager.getInstance().register(() -> periodicCheck(), 5000, 5000);
            tasks.add(checker);

            // 超时任务
            ScheduledFuture<?> timeout = TimerManager.getInstance().schedule(this::onTimeout, config.durationSeconds * 1000L);
            tasks.add(timeout);
        }

        void spawnWave(WaveConfig wave) {
            if (ended) return;
            Channel channel = getChannel();
            if (channel == null) return;
            MapleMap map = channel.getMapFactory().getMap(config.mapId);
            if (map == null) return;

            // 播放入侵提示
            map.startMapEffect("怪物入侵！", 5120000, 10000);
            map.broadcastMessage(PacketCreator.serverNotice(5, "怪物攻城：新一波怪物来袭！"));

            int spawnIndex = 0;
            for (MobEntry entry : wave.mobs) {
                for (int i = 0; i < entry.count; i++) {
                    Monster monster = LifeFactory.getMonster(entry.mobId);
                    if (monster == null) continue;
                    Point pos = getSpawnPosition(map, spawnIndex++);
                    map.spawnMonsterOnGroundBelow(monster, pos);
                    monster.addListener(this);
                    monsterOids.add(monster.getObjectId());
                    totalSpawned++;
                }
            }

            // 显示倒计时
            long remaining = config.durationSeconds - (System.currentTimeMillis() - startTime) / 1000;
            if (remaining > 0) {
                map.broadcastMessage(PacketCreator.getClock((int) (remaining)));
            }
        }

        void periodicCheck() {
            if (ended) return;
            Channel channel = getChannel();
            if (channel == null) return;
            MapleMap map = channel.getMapFactory().getMap(config.mapId);
            if (map == null) return;

            // 兜底清理：移除地图上不存在的怪物 OID
            Set<Integer> aliveOids = ConcurrentHashMap.newKeySet();
            for (Monster m : map.getAllMonsters()) {
                aliveOids.add(m.getObjectId());
            }
            monsterOids.removeIf(oid -> !aliveOids.contains(oid));

            // 追踪参与者
            for (Character chr : map.getAllPlayers()) {
                participantIds.add(chr.getId());
            }

            // 全灭 → 成功
            if (monsterOids.isEmpty() && totalSpawned > 0) {
                onSuccess();
            }
        }

        // MonsterListener 即时回调
        @Override
        public void monsterKilled(int aniTime) {
            // OID 会在 listener 触发后从 periodicCheck 兜底清理
            // 这里做即时检查
            if (ended) return;
            // 延迟一小段时间让 monster 从 map 上移除
            Thread.startVirtualThread(() -> {
                try { Thread.sleep(100); } catch (InterruptedException ignored) {}
                Channel channel = getChannel();
                if (channel == null) return;
                MapleMap map = channel.getMapFactory().getMap(config.mapId);
                if (map == null) return;
                int alive = 0;
                for (Monster m : map.getAllMonsters()) {
                    if (monsterOids.contains(m.getObjectId())) alive++;
                }
                if (alive == 0 && totalSpawned > 0 && !ended) {
                    onSuccess();
                }
            });
        }

        @Override
        public void monsterDamaged(Character from, int trueDmg) {
            if (from != null) participantIds.add(from.getId());
        }

        @Override
        public void monsterHealed(int trueHeal) {}

        // ==================== 成功 / 超时 / 取消 ====================
        synchronized void onSuccess() {
            if (ended) return;
            ended = true;
            success = true;
            cancelAllTasks();

            Channel channel = getChannel();
            if (channel != null) {
                MapleMap map = channel.getMapFactory().getMap(config.mapId);
                if (map != null) map.broadcastMessage(PacketCreator.serverNotice(5, "怪物攻城结束！所有怪物已被消灭！"));
            }

            distributeRewards();

            Server.getInstance().broadcastMessage(config.worldId,
                    PacketCreator.serverNotice(6, "[怪物攻城] 攻城成功！所有怪物已被消灭，参与玩家已获得奖励！"));
            activeInvasions.remove(config.worldId);
        }

        synchronized void onTimeout() {
            if (ended) return;
            ended = true;
            cancelAllTasks();

            // 杀死剩余怪物
            Channel channel = getChannel();
            if (channel != null) {
                MapleMap map = channel.getMapFactory().getMap(config.mapId);
                if (map != null) {
                    for (Monster m : map.getAllMonsters()) {
                        if (monsterOids.contains(m.getObjectId())) {
                            m.setHpZero();
                        }
                    }
                }
            }

            Server.getInstance().broadcastMessage(config.worldId,
                    PacketCreator.serverNotice(6, "[怪物攻城] 时间到！攻城失败，怪物已撤退。"));
            restoreRates();
            activeInvasions.remove(config.worldId);
        }

        void cancel() {
            if (ended) return;
            ended = true;
            cancelAllTasks();

            Channel channel = getChannel();
            if (channel != null) {
                MapleMap map = channel.getMapFactory().getMap(config.mapId);
                if (map != null) {
                    for (Monster m : map.getAllMonsters()) {
                        if (monsterOids.contains(m.getObjectId())) {
                            m.setHpZero();
                        }
                    }
                }
            }

            Server.getInstance().broadcastMessage(config.worldId,
                    PacketCreator.serverNotice(6, "[怪物攻城] GM 已取消了本次攻城。"));
            restoreRates();
        }

        // ==================== 奖励发放 ====================
        void distributeRewards() {
            World world = Server.getInstance().getWorld(config.worldId);
            if (world == null) return;

            // 全服倍率
            if (config.expRate > 1.0f && config.expDurationMin > 0) {
                world.setExpRate(config.expRate);
                scheduleRateRestore(() -> world.setExpRate(prevExpRate), config.expDurationMin);
            }
            if (config.dropRate > 1.0f && config.dropDurationMin > 0) {
                world.setDropRate(config.dropRate);
                scheduleRateRestore(() -> world.setDropRate(prevDropRate), config.dropDurationMin);
            }
            if (config.mesoRate > 1.0f && config.mesoDurationMin > 0) {
                world.setMesoRate(config.mesoRate);
                scheduleRateRestore(() -> world.setMesoRate(prevMesoRate), config.mesoDurationMin);
            }

            // 遍历所有线路发放个人奖励
            if (config.cashReward > 0 || config.mesoReward > 0 || config.rewardItemId > 0) {
                for (Channel ch : world.getChannels()) {
                    for (Character chr : ch.getPlayerStorage().getAllCharacters()) {
                        if (participantIds.contains(chr.getId())) {
                            try {
                                if (config.cashReward > 0) {
                                    chr.getCashShop().gainCash(1, config.cashReward);
                                }
                                if (config.mesoReward > 0) {
                                    chr.gainMeso(config.mesoReward, true);
                                }
                                if (config.rewardItemId > 0) {
                                    InventoryManipulator.addById(chr.getClient(), config.rewardItemId,
                                            (short) config.rewardItemCount, "怪物攻城奖励", -1, (short) 0, -1L);
                                }
                                chr.dropMessage(5, "[怪物攻城] 恭喜！你获得了攻城胜利奖励！");
                            } catch (Exception ignored) {}
                        }
                    }
                }
            }

            // 全服公告奖励信息
            StringBuilder sb = new StringBuilder("[怪物攻城] 攻城胜利！奖励已发放：");
            if (config.expRate > 1.0f) sb.append("EXP").append(config.expRate).append("x ").append(config.expDurationMin).append("分钟 ");
            if (config.dropRate > 1.0f) sb.append("掉率").append(config.dropRate).append("x ").append(config.dropDurationMin).append("分钟 ");
            if (config.mesoRate > 1.0f) sb.append("金币").append(config.mesoRate).append("x ").append(config.mesoDurationMin).append("分钟 ");
            if (config.cashReward > 0) sb.append("点券+").append(config.cashReward).append(" ");
            if (config.mesoReward > 0) sb.append("金币+").append(config.mesoReward).append(" ");
            Server.getInstance().broadcastMessage(config.worldId, PacketCreator.serverNotice(6, sb.toString()));
        }

        void scheduleRateRestore(Runnable restore, int minutes) {
            TimerManager.getInstance().schedule(() -> {
                restore.run();
                Server.getInstance().broadcastMessage(config.worldId,
                        PacketCreator.serverNotice(6, "[怪物攻城] 攻城奖励倍率已恢复。"));
            }, minutes * 60000L);
        }

        void restoreRates() {
            // 如果还没有恢复倍率，立即恢复
            World world = Server.getInstance().getWorld(config.worldId);
            if (world != null) {
                world.setExpRate(prevExpRate);
                world.setDropRate(prevDropRate);
                world.setMesoRate(prevMesoRate);
            }
        }

        // ==================== 辅助方法 ====================
        Channel getChannel() {
            World w = Server.getInstance().getWorld(config.worldId);
            if (w == null) return null;
            return w.getChannel(config.channelId);
        }

        Point getSpawnPosition(MapleMap map, int index) {
            String[] portalNames = {"sp", "pt", "tp", "p00", "p01", "p02", "p03", "p04"};
            for (int i = 0; i < portalNames.length; i++) {
                String name = portalNames[(index + i) % portalNames.length];
                Portal portal = map.getPortal(name);
                if (portal != null) {
                    Point pos = portal.getPosition();
                    int offsetX = (int) ((Math.random() - 0.5) * 200);
                    return new Point(pos.x + offsetX, pos.y);
                }
            }
            Portal p0 = map.getPortal(0);
            return p0 != null ? p0.getPosition() : new Point(0, 0);
        }

        String getMapName() {
            try {
                Channel c = getChannel();
                if (c != null) {
                    MapleMap m = c.getMapFactory().getMap(config.mapId);
                    if (m != null) return m.getMapName();
                }
            } catch (Exception ignored) {}
            return "地图" + config.mapId;
        }

        void cancelAllTasks() {
            for (ScheduledFuture<?> t : tasks) {
                try { t.cancel(false); } catch (Exception ignored) {}
            }
            tasks.clear();
        }
    }
}

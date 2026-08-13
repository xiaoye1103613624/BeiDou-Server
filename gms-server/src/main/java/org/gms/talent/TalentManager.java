package org.gms.talent;

import com.mybatisflex.core.query.QueryWrapper;
import org.gms.client.Character;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.manipulator.InventoryManipulator;
import org.gms.constants.inventory.ItemConstants;
import org.gms.dao.entity.CharacterTalentDO;
import org.gms.dao.mapper.CharacterTalentMapper;
import org.gms.manager.ServerManager;
import org.gms.util.Randomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 角色天赋运行时状态：加载、查询、学习、汇总。
 */
public final class TalentManager {
    private static final Logger log = LoggerFactory.getLogger(TalentManager.class);

    private final Character owner;
    private final Map<TalentId, Integer> levels = new ConcurrentHashMap<>();
    private long lastDeathRebornMs;
    private long lastPainTrainMs;

    public TalentManager(Character owner) {
        this.owner = owner;
    }

    public void load() {
        levels.clear();
        try {
            CharacterTalentMapper mapper = mapper();
            List<CharacterTalentDO> rows = mapper.selectListByQuery(
                    QueryWrapper.create().eq("character_id", owner.getId()));
            if (rows != null) {
                for (CharacterTalentDO row : rows) {
                    TalentId tid = TalentId.fromId(row.getTalentId() == null ? 0 : row.getTalentId());
                    if (tid != null && row.getLevel() != null && row.getLevel() > 0) {
                        levels.put(tid, Math.min(tid.maxLevel(), row.getLevel()));
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Load talents failed for chr {}: {}", owner.getId(), e.getMessage());
        }
    }

    public int getLevel(TalentId id) {
        if (id == null) {
            return 0;
        }
        return levels.getOrDefault(id, 0);
    }

    public int getLevel(int talentId) {
        return getLevel(TalentId.fromId(talentId));
    }

    public Map<TalentId, Integer> allLevels() {
        EnumMap<TalentId, Integer> copy = new EnumMap<>(TalentId.class);
        copy.putAll(levels);
        return Collections.unmodifiableMap(copy);
    }

    public int pointsSpent(TalentTier tier) {
        int sum = 0;
        for (Map.Entry<TalentId, Integer> e : levels.entrySet()) {
            if (e.getKey().tier() == tier) {
                sum += e.getValue();
            }
        }
        return sum;
    }

    public boolean isTierUnlocked(TalentTier tier) {
        return switch (tier) {
            case PRIMARY -> true;
            case MID -> pointsSpent(TalentTier.PRIMARY) >= TalentTier.PRIMARY.pointsToUnlockNext();
            case ADVANCED -> pointsSpent(TalentTier.MID) >= TalentTier.MID.pointsToUnlockNext();
            case ULTIMATE -> isTierUnlocked(TalentTier.ADVANCED);
        };
    }

    public LearnResult learn(TalentId talent) {
        if (talent == null || owner == null) {
            return LearnResult.fail("无效的天赋。");
        }
        if (!isTierUnlocked(talent.tier())) {
            return LearnResult.fail("尚未解锁" + talent.tier().label() + "天赋。");
        }
        int cur = getLevel(talent);
        if (cur >= talent.maxLevel()) {
            return LearnResult.fail(talent.displayName() + " 已达满级。");
        }
        int bookId = talent.itemId();
        if (owner.getItemQuantity(bookId, false) < 1) {
            return LearnResult.fail("缺少天赋书：" + talent.displayName() + "。");
        }

        InventoryType invType = ItemConstants.getInventoryType(bookId);
        InventoryManipulator.removeById(owner.getClient(), invType, bookId, 1, false, false);

        boolean success = true;
        int rate = 100;
        if (talent.tier() == TalentTier.ULTIMATE) {
            rate = TalentConfig.ultimateSuccessRate(cur);
            success = Randomizer.nextInt(100) < rate;
        }

        if (!success) {
            return LearnResult.failedLearn(talent, cur, rate);
        }

        int next = cur + 1;
        levels.put(talent, next);
        persist(talent, next);
        owner.recalcLocalStats();
        owner.markCombatStatsDirty();
        return LearnResult.ok(talent, next, rate);
    }

    private void persist(TalentId talent, int level) {
        try {
            CharacterTalentMapper mapper = mapper();
            CharacterTalentDO existing = mapper.selectOneByQuery(QueryWrapper.create()
                    .eq("character_id", owner.getId())
                    .eq("talent_id", talent.id()));
            if (existing == null) {
                mapper.insert(CharacterTalentDO.builder()
                        .characterId(owner.getId())
                        .talentId(talent.id())
                        .level(level)
                        .build());
            } else {
                existing.setLevel(level);
                mapper.update(existing);
            }
        } catch (Exception e) {
            log.error("Persist talent {} lv{} for chr {} failed: {}",
                    talent.id(), level, owner.getId(), e.getMessage());
        }
    }

    public long getLastDeathRebornMs() {
        return lastDeathRebornMs;
    }

    public void setLastDeathRebornMs(long t) {
        this.lastDeathRebornMs = t;
    }

    public long getLastPainTrainMs() {
        return lastPainTrainMs;
    }

    public void setLastPainTrainMs(long t) {
        this.lastPainTrainMs = t;
    }

    private static CharacterTalentMapper mapper() {
        return ServerManager.getApplicationContext().getBean(CharacterTalentMapper.class);
    }

    public record LearnResult(boolean success, boolean consumed, String message, int newLevel, int successRate) {
        public static LearnResult ok(TalentId t, int newLevel, int rate) {
            return new LearnResult(true, true,
                    "学习成功！" + t.displayName() + " → Lv." + newLevel, newLevel, rate);
        }

        public static LearnResult failedLearn(TalentId t, int cur, int rate) {
            return new LearnResult(false, true,
                    "学习失败…（成功率 " + rate + "%）" + t.displayName() + " 仍为 Lv." + cur,
                    cur, rate);
        }

        public static LearnResult fail(String msg) {
            return new LearnResult(false, false, msg, 0, 0);
        }
    }
}

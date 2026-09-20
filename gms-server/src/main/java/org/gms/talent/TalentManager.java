package org.gms.talent;

import com.mybatisflex.core.query.QueryWrapper;
import org.gms.client.Character;
import org.gms.constants.inventory.ItemConstants;
import org.gms.client.inventory.manipulator.InventoryManipulator;
import org.gms.dao.entity.CharacterTalentDO;
import org.gms.dao.mapper.CharacterTalentMapper;
import org.gms.manager.ServerManager;

import java.util.EnumMap;
import java.util.Map;

/**
 * 角色天赋运行时管理：缓存 + 持久化（xy_character_talent）。
 * <p>
 * 由 {@code Character#getTalentManager()} 懒加载创建，DB 仅首次访问时读一次。
 */
public class TalentManager {
    private final Character owner;
    private final Map<TalentId, Integer> levels = new EnumMap<>(TalentId.class);

    public TalentManager(Character owner) {
        this.owner = owner;
    }

    public void load() {
        CharacterTalentMapper mapper = ServerManager.getApplicationContext().getBean(CharacterTalentMapper.class);
        levels.clear();
        if (mapper == null || owner.getId() <= 0) {
            return;
        }
        for (CharacterTalentDO row : mapper.selectListByQuery(
                QueryWrapper.create().eq("character_id", owner.getId()))) {
            TalentId tid = TalentId.fromId(row.getTalentId());
            if (tid != null) {
                levels.put(tid, row.getLevel());
            }
        }
    }

    public int getLevel(TalentId talentId) {
        return levels.getOrDefault(talentId, 0);
    }

    public boolean isUnlocked(TalentTier tier) {
        return owner.getLevel() >= tier.getUnlockLevel();
    }

    public boolean canLearn(TalentId talentId) {
        return isUnlocked(talentId.getTier()) && getLevel(talentId) < TalentConfig.MAX_TALENT_LEVEL;
    }

    public record LearnResult(boolean success, boolean leveled, int newLevel, String message) {
    }

    public LearnResult learn(TalentId talentId) {
        if (owner.getLevel() < talentId.getTier().getUnlockLevel()) {
            return new LearnResult(false, false, 0, "未达阶层解锁等级。");
        }
        int cur = getLevel(talentId);
        if (cur >= TalentConfig.MAX_TALENT_LEVEL) {
            return new LearnResult(false, false, cur, "该天赋已至满级。");
        }
        int bookId = talentId.getTier().getItemId();
        if (owner.getItemQuantity(bookId, false) <= 0) {
            return new LearnResult(false, false, cur, "缺少对应天赋书。");
        }
        InventoryManipulator.removeById(owner.getClient(),
                ItemConstants.getInventoryType(bookId), bookId, 1, false, false);
        int next = cur + 1;
        levels.put(talentId, next);
        persist(talentId, next);
        owner.recalcLocalStats();
        owner.markCombatStatsDirty();
        return new LearnResult(true, true, next, "学习成功！当前 Lv." + next);
    }

    /** 兑换：消耗同阶层收集进度，使已学天赋 +1（受 MAX 限制）。 */
    public int exchangeOneBook(TalentId targetTier) {
        int cur = getLevel(targetTier);
        if (cur <= 0 || cur >= TalentConfig.MAX_TALENT_LEVEL) {
            return 0;
        }
        int next = cur + 1;
        levels.put(targetTier, next);
        persist(targetTier, next);
        owner.recalcLocalStats();
        owner.markCombatStatsDirty();
        return next;
    }

    private void persist(TalentId tid, int level) {
        CharacterTalentMapper mapper = ServerManager.getApplicationContext().getBean(CharacterTalentMapper.class);
        if (mapper == null) {
            return;
        }
        CharacterTalentDO existing = mapper.selectOneByQuery(QueryWrapper.create()
                .eq("character_id", owner.getId()).eq("talent_id", tid.getId()));
        if (existing != null) {
            existing.setLevel(level);
            mapper.updateByQuery(existing, QueryWrapper.create()
                    .eq("character_id", owner.getId()).eq("talent_id", tid.getId()));
        } else {
            mapper.insert(new CharacterTalentDO(owner.getId(), tid.getId(), level));
        }
    }
}

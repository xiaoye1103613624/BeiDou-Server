package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.dao.entity.CharacterChallengeFatigueDO;
import org.gms.dao.entity.CharacterChallengeLogDO;
import org.gms.dao.mapper.CharacterChallengeFatigueMapper;
import org.gms.dao.mapper.CharacterChallengeLogMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 三种挑战副本独立每日次数。
 * <p>
 * 跨天将剩余次数重置为 {@link #DAILY_BASE}；当日可用恢复剂无限叠加。
 */
@Slf4j
@Service
@AllArgsConstructor
public class ChallengeFatigueService {

    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_ADVANCED = 2;
    public static final int TYPE_TEAM = 3;

    public static final int DAILY_BASE = 3;

    /** 复制自 2004130–32 的独立恢复剂（不占用原炼金药丸 ID） */
    public static final int ITEM_NORMAL = 2004900;
    public static final int ITEM_ADVANCED = 2004901;
    public static final int ITEM_TEAM = 2004902;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private final CharacterChallengeFatigueMapper fatigueMapper;
    private final CharacterChallengeLogMapper logMapper;

    public static String typeName(int challengeType) {
        return switch (challengeType) {
            case TYPE_NORMAL -> "普通挑战";
            case TYPE_ADVANCED -> "进阶挑战";
            case TYPE_TEAM -> "团队挑战";
            default -> "未知挑战";
        };
    }

    public static Integer typeByItemId(int itemId) {
        return switch (itemId) {
            case ITEM_NORMAL -> TYPE_NORMAL;
            case ITEM_ADVANCED -> TYPE_ADVANCED;
            case ITEM_TEAM -> TYPE_TEAM;
            default -> null;
        };
    }

    @Transactional
    public CharacterChallengeFatigueDO getOrCreate(Integer characterId, int challengeType) {
        validateType(challengeType);
        CharacterChallengeFatigueDO row = fatigueMapper.selectOneByQuery(
                QueryWrapper.create()
                        .where("character_id = ?", characterId)
                        .and("challenge_type = ?", challengeType));
        Date now = new Date();
        String today = DATE_FORMAT.format(now);
        if (row == null) {
            row = CharacterChallengeFatigueDO.builder()
                    .characterId(characterId)
                    .challengeType(challengeType)
                    .remaining(DAILY_BASE)
                    .lastResetDate(now)
                    .createTime(now)
                    .updateTime(now)
                    .build();
            fatigueMapper.insert(row);
            return row;
        }
        String lastReset = row.getLastResetDate() != null ? DATE_FORMAT.format(row.getLastResetDate()) : null;
        if (!today.equals(lastReset)) {
            row.setRemaining(DAILY_BASE);
            row.setLastResetDate(now);
            row.setUpdateTime(now);
            fatigueMapper.update(row);
            log.info("角色 {} {} 次数跨天重置为 {}", characterId, typeName(challengeType), DAILY_BASE);
        }
        return row;
    }

    @Transactional
    public int getRemaining(Integer characterId, int challengeType) {
        return getOrCreate(characterId, challengeType).getRemaining();
    }

    /**
     * 消耗 1 次进入挑战；不足则抛异常。
     *
     * @return 消耗后剩余次数
     */
    @Transactional
    public int consumeEnter(Integer characterId, Integer accountId, int challengeType,
                            String bossName, Integer mapId, String mobIds) {
        CharacterChallengeFatigueDO row = getOrCreate(characterId, challengeType);
        if (row.getRemaining() <= 0) {
            throw new IllegalStateException(typeName(challengeType) + "次数已用完，可使用恢复剂增加次数");
        }
        row.setRemaining(row.getRemaining() - 1);
        row.setUpdateTime(new Date());
        fatigueMapper.update(row);
        insertLog(characterId, accountId, challengeType, "ENTER", bossName, mapId, mobIds, null, row.getRemaining());
        return row.getRemaining();
    }

    /**
     * 使用恢复剂增加 1 次（可无限叠加，不受上限限制）。
     *
     * @return 增加后剩余次数
     */
    @Transactional
    public int restoreOne(Integer characterId, Integer accountId, int challengeType, int itemId) {
        CharacterChallengeFatigueDO row = getOrCreate(characterId, challengeType);
        row.setRemaining(row.getRemaining() + 1);
        row.setUpdateTime(new Date());
        fatigueMapper.update(row);
        insertLog(characterId, accountId, challengeType, "RESTORE", null, null, null, itemId, row.getRemaining());
        return row.getRemaining();
    }

    private void insertLog(Integer characterId, Integer accountId, int challengeType, String actionType,
                           String bossName, Integer mapId, String mobIds, Integer itemId, int remainingAfter) {
        Date now = new Date();
        logMapper.insert(CharacterChallengeLogDO.builder()
                .characterId(characterId)
                .accountId(accountId)
                .challengeType(challengeType)
                .actionType(actionType)
                .bossName(bossName)
                .mapId(mapId)
                .mobIds(mobIds)
                .itemId(itemId)
                .remainingAfter(remainingAfter)
                .createTime(now)
                .build());
    }

    private void validateType(int challengeType) {
        if (challengeType != TYPE_NORMAL && challengeType != TYPE_ADVANCED && challengeType != TYPE_TEAM) {
            throw new IllegalArgumentException("无效的挑战类型: " + challengeType);
        }
    }
}

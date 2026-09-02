package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.client.Character;
import org.gms.client.Job;
import org.gms.client.inventory.WeaponType;
import org.gms.combat.power.CombatPowerCalculator;
import org.gms.combat.power.EquipRankCategory;
import org.gms.combat.power.EquipScoreCalculator;
import org.gms.combat.stat.CombatStatProfile;
import org.gms.dao.entity.RankingCombatPowerDO;
import org.gms.dao.entity.RankingEquipScoreDO;
import org.gms.dao.mapper.RankingCombatPowerMapper;
import org.gms.dao.mapper.RankingEquipScoreMapper;
import org.gms.model.dto.CombatPowerRankItemDTO;
import org.gms.model.dto.EquipScoreRankItemDTO;
import org.gms.model.dto.RankingFilterOptionDTO;
import org.gms.model.dto.RankingQueryReqDTO;
import org.gms.net.server.Server;
import org.gms.net.server.world.World;
import org.gms.server.ItemInformationProvider;
import org.gms.util.DatabaseConnection;
import org.gms.util.I18nUtil;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.gms.dao.entity.table.RankingCombatPowerDOTableDef.RANKING_COMBAT_POWER_D_O;
import static org.gms.dao.entity.table.RankingEquipScoreDOTableDef.RANKING_EQUIP_SCORE_D_O;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankingService {
    public static final int DEFAULT_LIMIT = 20;
    public static final int MAX_LIMIT = 50;

    private final RankingCombatPowerMapper combatPowerMapper;
    private final RankingEquipScoreMapper equipScoreMapper;

    public List<CombatPowerRankItemDTO> listCombatPower(RankingQueryReqDTO req) {
        int limit = normalizeLimit(req == null ? null : req.getLimit());
        Integer niche = req == null ? null : req.getFilter();
        QueryWrapper qw = QueryWrapper.create()
                .orderBy(RANKING_COMBAT_POWER_D_O.COMBAT_POWER, false)
                .limit(limit);
        // null / <0 = 总榜；>=0 = jobNiche（0=新手）
        if (niche != null && niche >= 0) {
            qw.where(RANKING_COMBAT_POWER_D_O.JOB_NICHE.eq(niche));
        }
        List<RankingCombatPowerDO> rows = combatPowerMapper.selectListByQuery(qw);
        List<CombatPowerRankItemDTO> result = new ArrayList<>(rows.size());
        int rank = 1;
        for (RankingCombatPowerDO row : rows) {
            result.add(CombatPowerRankItemDTO.builder()
                    .rank(rank++)
                    .characterId(row.getCharacterId())
                    .name(row.getName())
                    .world(row.getWorld())
                    .job(row.getJob())
                    .jobName(resolveJobName(row.getJob()))
                    .jobNiche(row.getJobNiche())
                    .jobNicheName(I18nUtil.getMessage("ranking.jobNiche." + row.getJobNiche()))
                    .level(row.getLevel())
                    .combatPower(row.getCombatPower())
                    .baseDamage(row.getBaseDamage())
                    .build());
        }
        return result;
    }

    public List<EquipScoreRankItemDTO> listEquipScore(RankingQueryReqDTO req) {
        int limit = normalizeLimit(req == null ? null : req.getLimit());
        Integer slot = req == null ? null : req.getFilter();
        QueryWrapper qw = QueryWrapper.create()
                .orderBy(RANKING_EQUIP_SCORE_D_O.SCORE, false)
                .limit(limit);
        // null / <=0(ALL) = 总榜；>0 = 部位
        if (slot != null && slot > EquipRankCategory.ALL) {
            qw.where(RANKING_EQUIP_SCORE_D_O.SLOT_CATEGORY.eq(slot));
        }
        List<RankingEquipScoreDO> rows = equipScoreMapper.selectListByQuery(qw);
        List<EquipScoreRankItemDTO> result = new ArrayList<>(rows.size());
        int rank = 1;
        for (RankingEquipScoreDO row : rows) {
            result.add(toEquipDto(rank++, row));
        }
        return result;
    }

    public List<RankingFilterOptionDTO> listJobNicheOptions() {
        List<RankingFilterOptionDTO> list = new ArrayList<>();
        // -1 = 总榜；0 = 新手（job DIV 100），不可与总榜共用 0
        list.add(RankingFilterOptionDTO.builder().id(-1).name(I18nUtil.getMessage("ranking.filter.all")).build());
        for (int i = 0; i <= Job.getMax(); i++) {
            list.add(RankingFilterOptionDTO.builder()
                    .id(i)
                    .name(I18nUtil.getMessage("ranking.jobNiche." + i))
                    .build());
        }
        return list;
    }

    public List<RankingFilterOptionDTO> listSlotCategoryOptions() {
        List<RankingFilterOptionDTO> list = new ArrayList<>();
        for (Integer id : EquipRankCategory.listCategories()) {
            list.add(RankingFilterOptionDTO.builder()
                    .id(id)
                    .name(I18nUtil.getMessage("ranking.slot." + id))
                    .build());
        }
        return list;
    }

    public synchronized void refreshAll() {
        long start = System.currentTimeMillis();
        refreshCombatPower();
        refreshEquipScore();
        log.info(I18nUtil.getLogMessage("RankingService.refresh.info1"), System.currentTimeMillis() - start);
    }

    private void refreshCombatPower() {
        Map<Integer, Character> online = indexOnlineCharacters();
        List<RankingCombatPowerDO> batch = new ArrayList<>();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT id, world, name, job, level, str, dex, `int`, luk FROM characters WHERE gm < 2");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int cid = rs.getInt("id");
                int job = rs.getInt("job");
                int level = rs.getInt("level");
                String name = rs.getString("name");
                int world = rs.getInt("world");
                long power;
                int baseDamage;
                Character chr = online.get(cid);
                if (chr != null) {
                    boolean magic = CombatPowerCalculator.isMagicJob(chr.getJob());
                    baseDamage = magic
                            ? chr.calculateMaxBaseMagicDamage(chr.getTotalMagic())
                            : chr.calculateMaxBaseDamage(chr.getTotalWatk());
                    power = CombatPowerCalculator.computeOnline(chr);
                } else {
                    EquipAgg agg = loadEquippedAgg(con, cid);
                    CombatStatProfile empty = CombatStatProfile.EMPTY;
                    power = CombatPowerCalculator.computeOffline(
                            job,
                            rs.getInt("str"), rs.getInt("dex"), rs.getInt("int"), rs.getInt("luk"),
                            agg.str, agg.dex, agg.inte, agg.luk,
                            agg.watk, agg.matk, agg.weaponId,
                            empty);
                    boolean magic = CombatPowerCalculator.isMagicJob(Job.getById(job));
                    baseDamage = magic
                            ? CombatPowerCalculator.calcMaxBaseMagicDamage(agg.matk, rs.getInt("int") + agg.inte)
                            : CombatPowerCalculator.calcMaxBasePhysicalDamage(
                            rs.getInt("str") + agg.str,
                            rs.getInt("dex") + agg.dex,
                            rs.getInt("luk") + agg.luk,
                            agg.watk,
                            agg.weaponId > 0
                                    ? ItemInformationProvider.getInstance().getWeaponType(agg.weaponId)
                                    : WeaponType.NOT_A_WEAPON,
                            Job.getById(job));
                }
                batch.add(RankingCombatPowerDO.builder()
                        .characterId(cid)
                        .world(world)
                        .name(name)
                        .job(job)
                        .jobNiche(job / 100)
                        .level(level)
                        .combatPower(power)
                        .baseDamage(baseDamage)
                        .updatedAt(now)
                        .build());
            }
        } catch (Exception e) {
            log.error(I18nUtil.getLogMessage("RankingService.refresh.error1"), e);
            return;
        }
        combatPowerMapper.deleteByQuery(QueryWrapper.create().where("1 = 1"));
        if (!batch.isEmpty()) {
            combatPowerMapper.insertBatch(batch);
        }
    }

    private void refreshEquipScore() {
        List<RankingEquipScoreDO> batch = new ArrayList<>();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        Set<Long> seen = new HashSet<>();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     """
                             SELECT ii.inventoryitemid, ii.characterid, ii.itemid, ii.position, ii.inventorytype,
                                    c.name AS cname, c.world,
                                    ie.str, ie.dex, ie.`int`, ie.luk, ie.hp, ie.mp, ie.watk, ie.matk,
                                    ie.wdef, ie.mdef, ie.acc, ie.avoid, ie.hands, ie.speed, ie.jump,
                                    ie.upgradeslots, ie.level, ie.vicious, ie.itemlevel, ie.itemexp
                             FROM inventoryitems ii
                             INNER JOIN inventoryequipment ie ON ie.inventoryitemid = ii.inventoryitemid
                             INNER JOIN characters c ON c.id = ii.characterid
                             WHERE c.gm < 2 AND ii.characterid > 0
                               AND (ii.inventorytype = -1 OR ii.inventorytype = 1)
                             """);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                long iid = rs.getLong("inventoryitemid");
                if (!seen.add(iid)) {
                    continue;
                }
                int itemId = rs.getInt("itemid");
                short position = rs.getShort("position");
                byte invType = rs.getByte("inventorytype");
                boolean equipped = invType == -1;
                int slotCategory = equipped
                        ? EquipRankCategory.fromEquippedPosition(position)
                        : EquipRankCategory.fromItemId(itemId);
                short str = rs.getShort("str");
                short dex = rs.getShort("dex");
                short inte = rs.getShort("int");
                short luk = rs.getShort("luk");
                short hp = rs.getShort("hp");
                short mp = rs.getShort("mp");
                short watk = rs.getShort("watk");
                short matk = rs.getShort("matk");
                short wdef = rs.getShort("wdef");
                short mdef = rs.getShort("mdef");
                short acc = rs.getShort("acc");
                short avoid = rs.getShort("avoid");
                short hands = rs.getShort("hands");
                short speed = rs.getShort("speed");
                short jump = rs.getShort("jump");
                byte level = rs.getByte("level");
                short vicious = rs.getShort("vicious");
                byte itemLevel = rs.getByte("itemlevel");
                boolean weapon = slotCategory == EquipRankCategory.WEAPON;
                long score = EquipScoreCalculator.score(
                        str, dex, inte, luk, hp, mp, watk, matk, wdef, mdef, acc, avoid,
                        speed, jump, level, vicious, itemLevel, weapon);
                if (score <= 0) {
                    continue;
                }
                batch.add(RankingEquipScoreDO.builder()
                        .inventoryItemId(iid)
                        .characterId(rs.getInt("characterid"))
                        .characterName(rs.getString("cname"))
                        .world(rs.getInt("world"))
                        .itemId(itemId)
                        .itemName(ii.getName(itemId))
                        .position(position)
                        .slotCategory(slotCategory)
                        .equipped(equipped ? 1 : 0)
                        .score(score)
                        .str(str).dex(dex).inte(inte).luk(luk)
                        .hp(hp).mp(mp).watk(watk).matk(matk)
                        .wdef(wdef).mdef(mdef).acc(acc).avoid(avoid)
                        .hands(hands).speed(speed).jump(jump)
                        .upgradeslots(rs.getByte("upgradeslots"))
                        .level(level).vicious(vicious)
                        .itemlevel(itemLevel).itemexp(rs.getInt("itemexp"))
                        .updatedAt(now)
                        .build());
            }
        } catch (Exception e) {
            log.error(I18nUtil.getLogMessage("RankingService.refresh.error2"), e);
            return;
        }
        equipScoreMapper.deleteByQuery(QueryWrapper.create().where("1 = 1"));
        if (!batch.isEmpty()) {
            // insertBatch 可能有包大小限制，分片插入
            int chunk = 500;
            for (int i = 0; i < batch.size(); i += chunk) {
                equipScoreMapper.insertBatch(batch.subList(i, Math.min(i + chunk, batch.size())));
            }
        }
    }

    private EquipAgg loadEquippedAgg(Connection con, int characterId) throws Exception {
        EquipAgg agg = new EquipAgg();
        try (PreparedStatement ps = con.prepareStatement(
                """
                        SELECT ii.itemid, ii.position, ie.str, ie.dex, ie.`int`, ie.luk, ie.watk, ie.matk
                        FROM inventoryitems ii
                        INNER JOIN inventoryequipment ie ON ie.inventoryitemid = ii.inventoryitemid
                        WHERE ii.characterid = ? AND ii.inventorytype = -1
                        """)) {
            ps.setInt(1, characterId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    agg.str += rs.getInt("str");
                    agg.dex += rs.getInt("dex");
                    agg.inte += rs.getInt("int");
                    agg.luk += rs.getInt("luk");
                    agg.watk += rs.getInt("watk");
                    agg.matk += rs.getInt("matk");
                    if (rs.getShort("position") == -11) {
                        agg.weaponId = rs.getInt("itemid");
                    }
                }
            }
        }
        return agg;
    }

    private Map<Integer, Character> indexOnlineCharacters() {
        Map<Integer, Character> map = new HashMap<>();
        try {
            for (World world : Server.getInstance().getWorlds()) {
                for (Character chr : world.getPlayerStorage().getAllCharacters()) {
                    if (chr != null && chr.gmLevel() < 2) {
                        map.put(chr.getId(), chr);
                    }
                }
            }
        } catch (Exception e) {
            log.warn(I18nUtil.getLogMessage("RankingService.refresh.warn1"), e);
        }
        return map;
    }

    private EquipScoreRankItemDTO toEquipDto(int rank, RankingEquipScoreDO row) {
        return EquipScoreRankItemDTO.builder()
                .rank(rank)
                .inventoryItemId(row.getInventoryItemId())
                .characterId(row.getCharacterId())
                .characterName(row.getCharacterName())
                .world(row.getWorld())
                .itemId(row.getItemId())
                .itemName(row.getItemName())
                .position(row.getPosition())
                .slotCategory(row.getSlotCategory())
                .slotCategoryName(I18nUtil.getMessage("ranking.slot." + row.getSlotCategory()))
                .equipped(row.getEquipped() != null && row.getEquipped() == 1)
                .score(row.getScore())
                .attStr(row.getStr())
                .attDex(row.getDex())
                .attInt(row.getInte())
                .attLuk(row.getLuk())
                .hp(row.getHp())
                .mp(row.getMp())
                .pAtk(row.getWatk())
                .mAtk(row.getMatk())
                .pDef(row.getWdef())
                .mDef(row.getMdef())
                .acc(row.getAcc())
                .avoid(row.getAvoid())
                .hands(row.getHands())
                .speed(row.getSpeed())
                .jump(row.getJump())
                .upgradeSlots(row.getUpgradeslots())
                .level(row.getLevel())
                .vicious(row.getVicious())
                .itemLevel(row.getItemlevel())
                .itemExp(row.getItemexp())
                .build();
    }

    private static int normalizeLimit(Integer limit) {
        if (limit == null || limit <= 0) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }

    private static String resolveJobName(Integer jobId) {
        if (jobId == null) {
            return "";
        }
        Job job = Job.getById(jobId);
        return job != null ? job.getName() : String.valueOf(jobId);
    }

    private static final class EquipAgg {
        int str, dex, inte, luk, watk, matk, weaponId;
    }
}

package org.gms.service.activity;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.client.Character;
import org.gms.client.inventory.manipulator.InventoryManipulator;
import org.gms.constants.activity.ActivityClaimStatus;
import org.gms.constants.activity.ActivityGrantMode;
import org.gms.constants.activity.ActivityOutcome;
import org.gms.constants.id.MapId;
import org.gms.dao.entity.ActivityDefDO;
import org.gms.dao.entity.ActivityParticipantResultDO;
import org.gms.dao.entity.ActivityRewardClaimDO;
import org.gms.dao.entity.ActivityRewardTierDO;
import org.gms.dao.entity.ActivitySessionDO;
import org.gms.dao.mapper.ActivityDefMapper;
import org.gms.dao.mapper.ActivityParticipantResultMapper;
import org.gms.dao.mapper.ActivityRewardClaimMapper;
import org.gms.dao.mapper.ActivityRewardTierMapper;
import org.gms.dao.mapper.ActivitySessionMapper;
import org.gms.exception.BizException;
import org.gms.model.dto.ActivityClaimDTO;
import org.gms.model.dto.ActivityParticipantResultDTO;
import org.gms.model.dto.ActivityRewardTierDTO;
import org.gms.model.dto.ActivitySettleDTO;
import org.gms.net.server.Server;
import org.gms.net.server.channel.Channel;
import org.gms.server.events.gm.Coconut;
import org.gms.server.maps.MapleMap;
import org.gms.util.I18nUtil;
import org.gms.util.PacketCreator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 活动奖励：档位配置、成绩结算、领取发放、名字公示。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityRewardService {

    private final ActivityRewardTierMapper tierMapper;
    private final ActivityParticipantResultMapper resultMapper;
    private final ActivityRewardClaimMapper claimMapper;
    private final ActivitySessionMapper sessionMapper;
    private final ActivityDefMapper defMapper;

    public List<ActivityRewardTierDTO> listTiers(String activityCode) {
        QueryWrapper qw = QueryWrapper.create().orderBy("priority", true).orderBy("id", true);
        if (activityCode != null && !activityCode.isBlank()) {
            qw.eq("activity_code", activityCode);
        }
        return tierMapper.selectListByQuery(qw).stream().map(this::toTierDto).collect(Collectors.toList());
    }

    @Transactional
    public ActivityRewardTierDTO saveTier(ActivityRewardTierDTO dto) {
        if (dto.getActivityCode() == null || dto.getTierCode() == null) {
            throw BizException.illegalArgument();
        }
        if (defMapper.selectOneById(dto.getActivityCode()) == null) {
            throw BizException.illegalArgument(I18nUtil.getExceptionMessage("ACTIVITY.NOT_FOUND"));
        }
        ActivityRewardTierDO entity = dto.getId() == null
                ? ActivityRewardTierDO.builder().build()
                : tierMapper.selectOneById(dto.getId());
        if (entity == null) {
            throw BizException.illegalArgument(I18nUtil.getExceptionMessage("ACTIVITY.TIER_NOT_FOUND"));
        }
        entity.setActivityCode(dto.getActivityCode());
        entity.setTierCode(dto.getTierCode());
        entity.setTierName(dto.getTierName() == null ? dto.getTierCode() : dto.getTierName());
        entity.setPriority(dto.getPriority() == null ? 100 : dto.getPriority());
        entity.setExclusiveGroup(dto.getExclusiveGroup());
        entity.setMatchJson(dto.getMatchJson() == null || dto.getMatchJson().isBlank() ? "{}" : dto.getMatchJson());
        entity.setGrantMode(dto.getGrantMode() == null ? ActivityGrantMode.CLAIM_NPC : dto.getGrantMode());
        entity.setMesos(dto.getMesos() == null ? 0L : dto.getMesos());
        entity.setExp(dto.getExp() == null ? 0 : dto.getExp());
        entity.setItemId(nz(dto.getItemId()));
        entity.setItemQty(nz(dto.getItemQty()));
        entity.setItem2Id(nz(dto.getItem2Id()));
        entity.setItem2Qty(nz(dto.getItem2Qty()));
        entity.setAnnounceName(Boolean.TRUE.equals(dto.getAnnounceName()) ? 1 : 0);
        entity.setAnnounceTpl(dto.getAnnounceTpl());
        entity.setEnabled(dto.getEnabled() == null || dto.getEnabled() ? 1 : 0);
        entity.setRemark(dto.getRemark());
        if (dto.getId() == null) {
            tierMapper.insert(entity);
        } else {
            tierMapper.update(entity);
        }
        return toTierDto(entity);
    }

    @Transactional
    public void deleteTier(Long id) {
        if (id == null) {
            throw BizException.illegalArgument();
        }
        tierMapper.deleteById(id);
    }

    /**
     * 结算一场：写成绩 → 匹配档位 → 生成领取单 → 自动发放 → 名字公示。
     */
    @Transactional
    public int settle(ActivitySettleDTO req, ActivityRuntimeSession runtimeOrNull) {
        ActivitySessionDO session;
        ActivityDefDO def;
        ActivityRuntimeSession runtime = runtimeOrNull;

        if (req.getSessionId() != null) {
            session = sessionMapper.selectOneById(req.getSessionId());
        } else if (runtime != null) {
            session = sessionMapper.selectOneById(runtime.getSessionId());
        } else {
            throw BizException.illegalArgument(I18nUtil.getExceptionMessage("ACTIVITY.NO_ACTIVE_SESSION"));
        }
        if (session == null) {
            throw BizException.illegalArgument(I18nUtil.getExceptionMessage("ACTIVITY.NO_ACTIVE_SESSION"));
        }
        def = defMapper.selectOneById(session.getActivityCode());
        if (def == null) {
            throw BizException.illegalArgument(I18nUtil.getExceptionMessage("ACTIVITY.NOT_FOUND"));
        }

        List<ActivityParticipantResultDTO> results = req.getResults();
        if (results == null || results.isEmpty()) {
            results = collectAutoResults(session, def, runtime);
        }
        if (results.isEmpty()) {
            return 0;
        }

        persistResults(session.getId(), results);
        List<ActivityRewardTierDO> tiers = tierMapper.selectListByQuery(QueryWrapper.create()
                .eq("activity_code", def.getCode())
                .eq("enabled", 1)
                .orderBy("priority", true));

        int created = 0;
        List<ActivityRewardClaimDO> announceClaims = new ArrayList<>();
        for (ActivityParticipantResultDTO result : results) {
            List<ActivityRewardTierDO> matched = matchTiers(tiers, result);
            for (ActivityRewardTierDO tier : matched) {
                if (existsClaim(session.getId(), tier.getTierCode(), result.getCharacterId())) {
                    continue;
                }
                ActivityRewardClaimDO claim = ActivityRewardClaimDO.builder()
                        .sessionId(session.getId())
                        .tierId(tier.getId())
                        .tierCode(tier.getTierCode())
                        .characterId(result.getCharacterId())
                        .characterName(result.getCharacterName())
                        .grantMode(tier.getGrantMode())
                        .status(ActivityClaimStatus.PENDING)
                        .mesos(tier.getMesos() == null ? 0L : tier.getMesos())
                        .exp(tier.getExp() == null ? 0 : tier.getExp())
                        .itemId(nz(tier.getItemId()))
                        .itemQty(nz(tier.getItemQty()))
                        .item2Id(nz(tier.getItem2Id()))
                        .item2Qty(nz(tier.getItem2Qty()))
                        .announceName(tier.getAnnounceName())
                        .createdAt(new Date())
                        .expireAt(new Date(System.currentTimeMillis() + 24L * 3600_000L))
                        .build();
                claimMapper.insert(claim);
                created++;

                if (ActivityGrantMode.AUTO_BAG.equals(tier.getGrantMode())
                        || ActivityGrantMode.AUTO_MAIL.equals(tier.getGrantMode())) {
                    Character online = findOnline(session.getWorldId(), result.getCharacterId());
                    if (online != null && tryGrant(online, claim)) {
                        claim.setStatus(ActivityClaimStatus.CLAIMED);
                        claim.setClaimedAt(new Date());
                        claimMapper.update(claim);
                    }
                    // 发不出则保留 PENDING，玩家可找获奖处领取
                }

                if (tier.getAnnounceName() != null && tier.getAnnounceName() == 1) {
                    announceClaims.add(claim);
                    announce(session.getWorldId(), def, tier, result.getCharacterName());
                }
            }
        }
        log.info(I18nUtil.getLogMessage("ActivityReward.settle.info"),
                session.getId(), def.getCode(), results.size(), created, announceClaims.size());
        return created;
    }

    /**
     * 玩家领取所有 PENDING 奖励，返回成功条数。
     */
    @Transactional
    public int claimAll(Character player) {
        if (player == null) {
            return 0;
        }
        List<ActivityRewardClaimDO> pending = claimMapper.selectListByQuery(QueryWrapper.create()
                .eq("character_id", player.getId())
                .eq("status", ActivityClaimStatus.PENDING)
                .orderBy("id", true));
        int ok = 0;
        Date now = new Date();
        for (ActivityRewardClaimDO claim : pending) {
            if (claim.getExpireAt() != null && claim.getExpireAt().before(now)) {
                claim.setStatus(ActivityClaimStatus.EXPIRED);
                claimMapper.update(claim);
                continue;
            }
            if (tryGrant(player, claim)) {
                claim.setStatus(ActivityClaimStatus.CLAIMED);
                claim.setClaimedAt(now);
                claimMapper.update(claim);
                ok++;
            } else {
                player.dropMessage(5, I18nUtil.getMessage("ActivityReward.claim.bagFull"));
                break;
            }
        }
        return ok;
    }

    public List<ActivityClaimDTO> listPendingClaims(int characterId) {
        return claimMapper.selectListByQuery(QueryWrapper.create()
                        .eq("character_id", characterId)
                        .eq("status", ActivityClaimStatus.PENDING))
                .stream()
                .map(this::toClaimDto)
                .collect(Collectors.toList());
    }

    public List<ActivityClaimDTO> listSessionClaims(long sessionId) {
        return claimMapper.selectListByQuery(QueryWrapper.create().eq("session_id", sessionId).orderBy("id", true))
                .stream()
                .map(this::toClaimDto)
                .collect(Collectors.toList());
    }

    // ---------------- auto collect ----------------

    private List<ActivityParticipantResultDTO> collectAutoResults(ActivitySessionDO session,
                                                                 ActivityDefDO def,
                                                                 ActivityRuntimeSession runtime) {
        Map<Integer, ActivityParticipantResultDTO> map = new HashMap<>();

        // 报名者至少 PARTICIPATED
        if (runtime != null) {
            for (Integer cid : runtime.getRegisteredCharacterIds()) {
                Character chr = findOnline(session.getWorldId(), cid);
                String name = chr != null ? chr.getName() : ("#" + cid);
                map.put(cid, baseResult(cid, name, ActivityOutcome.PARTICIPATED));
            }
        }

        Channel channel = Server.getInstance().getChannel(session.getWorldId(), session.getChannelId());
        if (channel == null) {
            return new ArrayList<>(map.values());
        }

        List<Integer> related = parseRelated(def);
        for (int mapId : related) {
            MapleMap mapleMap = channel.getMapFactory().getMap(mapId);
            if (mapleMap == null) {
                continue;
            }
            for (Character chr : new ArrayList<>(mapleMap.getCharacters())) {
                ActivityParticipantResultDTO dto = map.computeIfAbsent(chr.getId(),
                        id -> baseResult(chr.getId(), chr.getName(), ActivityOutcome.PARTICIPATED));
                dto.setCharacterName(chr.getName());
                dto.setTeamId((int) chr.getTeam());
                if (mapId == MapId.EVENT_WINNER) {
                    dto.setOutcome(ActivityOutcome.WIN);
                }
            }
        }

        // 团队对抗：读椰子比分
        if (def.getTeamEvent() != null && def.getTeamEvent() == 1) {
            applyTeamOutcome(channel, def, map);
        }

        // 跳跳/问答：在获奖处 = COMPLETE/WIN；按进入顺序简单排名（无精确用时则用 id）
        if ("JUMP".equals(def.getCategory()) || "QUIZ".equals(def.getCategory()) || "TREASURE".equals(def.getCategory())) {
            applyRankByWinnerMap(channel, map);
        }

        return new ArrayList<>(map.values());
    }

    private void applyTeamOutcome(Channel channel, ActivityDefDO def,
                                  Map<Integer, ActivityParticipantResultDTO> map) {
        Integer winTeam = null;
        boolean draw = false;
        MapleMap eventMap = channel.getMapFactory().getMap(def.getEventMapId());
        if (eventMap != null && eventMap.getCoconut() != null) {
            Coconut coconut = eventMap.getCoconut();
            if (coconut.getMapleScore() > coconut.getStoryScore()) {
                winTeam = 0;
            } else if (coconut.getStoryScore() > coconut.getMapleScore()) {
                winTeam = 1;
            } else {
                draw = true;
            }
        }
        // 雪球等：若有人已在获奖处，其队伍为胜
        if (winTeam == null && !draw) {
            MapleMap winnerMap = channel.getMapFactory().getMap(MapId.EVENT_WINNER);
            if (winnerMap != null) {
                for (Character chr : winnerMap.getCharacters()) {
                    winTeam = (int) chr.getTeam();
                    break;
                }
            }
        }
        for (ActivityParticipantResultDTO dto : map.values()) {
            if (draw) {
                dto.setOutcome(ActivityOutcome.DRAW);
            } else if (winTeam != null && dto.getTeamId() != null) {
                dto.setOutcome(Objects.equals(dto.getTeamId(), winTeam) ? ActivityOutcome.WIN : ActivityOutcome.LOSE);
            }
        }
    }

    private void applyRankByWinnerMap(Channel channel, Map<Integer, ActivityParticipantResultDTO> map) {
        MapleMap winnerMap = channel.getMapFactory().getMap(MapId.EVENT_WINNER);
        if (winnerMap == null) {
            return;
        }
        List<Character> winners = new ArrayList<>(winnerMap.getCharacters());
        for (int i = 0; i < winners.size(); i++) {
            Character chr = winners.get(i);
            ActivityParticipantResultDTO dto = map.computeIfAbsent(chr.getId(),
                    id -> baseResult(chr.getId(), chr.getName(), ActivityOutcome.COMPLETE));
            dto.setCharacterName(chr.getName());
            dto.setRankNo(i + 1);
            dto.setOutcome(i == 0 ? ActivityOutcome.WIN : ActivityOutcome.COMPLETE);
        }
    }

    // ---------------- match ----------------

    private List<ActivityRewardTierDO> matchTiers(List<ActivityRewardTierDO> tiers,
                                                   ActivityParticipantResultDTO result) {
        List<ActivityRewardTierDO> matched = new ArrayList<>();
        Set<String> usedGroups = new HashSet<>();
        for (ActivityRewardTierDO tier : tiers) {
            if (!matches(tier, result)) {
                continue;
            }
            String group = tier.getExclusiveGroup();
            if (group != null && !group.isBlank()) {
                if (usedGroups.contains(group)) {
                    continue;
                }
                usedGroups.add(group);
            }
            matched.add(tier);
        }
        return matched;
    }

    private boolean matches(ActivityRewardTierDO tier, ActivityParticipantResultDTO result) {
        JSONObject json;
        try {
            json = JSON.parseObject(tier.getMatchJson() == null ? "{}" : tier.getMatchJson());
        } catch (Exception e) {
            return false;
        }
        if (json == null) {
            json = new JSONObject();
        }
        if (json.containsKey("outcomes")) {
            List<String> outcomes = json.getList("outcomes", String.class);
            if (outcomes != null && !outcomes.isEmpty()
                    && (result.getOutcome() == null || !outcomes.contains(result.getOutcome()))) {
                return false;
            }
        }
        if (json.containsKey("rankFrom") || json.containsKey("rankTo")) {
            if (result.getRankNo() == null) {
                return false;
            }
            int from = json.getIntValue("rankFrom", 1);
            int to = json.getIntValue("rankTo", from);
            if (result.getRankNo() < from || result.getRankNo() > to) {
                return false;
            }
        }
        if (json.containsKey("scoreMin")) {
            if (result.getScore() == null || result.getScore() < json.getIntValue("scoreMin")) {
                return false;
            }
        }
        if (json.containsKey("finishTimeMaxMs")) {
            if (result.getFinishTimeMs() == null
                    || result.getFinishTimeMs() > json.getLongValue("finishTimeMaxMs")) {
                return false;
            }
        }
        if (json.containsKey("tags")) {
            List<String> need = json.getList("tags", String.class);
            Set<String> have = new HashSet<>();
            if (result.getTags() != null) {
                for (String t : result.getTags().split(",")) {
                    have.add(t.trim());
                }
            }
            for (String t : need) {
                if (!have.contains(t)) {
                    return false;
                }
            }
        }
        return true;
    }

    // ---------------- grant ----------------

    private boolean tryGrant(Character player, ActivityRewardClaimDO claim) {
        try {
            if (claim.getMesos() != null && claim.getMesos() > 0) {
                if (!player.canHoldMeso(claim.getMesos())) {
                    return false;
                }
            }
            if (nz(claim.getItemId()) > 0 && nz(claim.getItemQty()) > 0
                    && !player.canHold(claim.getItemId(), claim.getItemQty())) {
                return false;
            }
            if (nz(claim.getItem2Id()) > 0 && nz(claim.getItem2Qty()) > 0
                    && !player.canHold(claim.getItem2Id(), claim.getItem2Qty())) {
                return false;
            }

            if (claim.getMesos() != null && claim.getMesos() > 0) {
                player.gainMeso(claim.getMesos(), true, false, true);
            }
            if (claim.getExp() != null && claim.getExp() > 0) {
                player.gainExp((long) claim.getExp(), true, true);
            }
            if (nz(claim.getItemId()) > 0 && nz(claim.getItemQty()) > 0) {
                InventoryManipulator.addById(player.getClient(), claim.getItemId(),
                        (short) claim.getItemQty().intValue(), "ActivityReward", -1, (short) 0, -1);
            }
            if (nz(claim.getItem2Id()) > 0 && nz(claim.getItem2Qty()) > 0) {
                InventoryManipulator.addById(player.getClient(), claim.getItem2Id(),
                        (short) claim.getItem2Qty().intValue(), "ActivityReward", -1, (short) 0, -1);
            }
            player.dropMessage(5, I18nUtil.getMessage("ActivityReward.claim.success", claim.getTierCode()));
            return true;
        } catch (Exception e) {
            log.warn(I18nUtil.getLogMessage("ActivityReward.grant.fail"), player.getName(), claim.getId(), e);
            return false;
        }
    }

    private void announce(int worldId, ActivityDefDO def, ActivityRewardTierDO tier, String name) {
        String tpl = tier.getAnnounceTpl();
        String msg;
        if (tpl == null || tpl.isBlank()) {
            msg = I18nUtil.getMessage("ActivityReward.announce.default",
                    name, def.getNameZh(), tier.getTierName());
        } else {
            msg = tpl.replace("{0}", name)
                    .replace("{1}", def.getNameZh() == null ? def.getCode() : def.getNameZh())
                    .replace("{2}", tier.getTierName());
        }
        Server.getInstance().broadcastMessage(worldId, PacketCreator.serverNotice(6, msg));
        Server.getInstance().broadcastMessage(worldId, PacketCreator.earnTitleMessage(msg));
    }

    // ---------------- helpers ----------------

    private void persistResults(long sessionId, List<ActivityParticipantResultDTO> results) {
        for (ActivityParticipantResultDTO dto : results) {
            ActivityParticipantResultDO existing = resultMapper.selectOneByQuery(QueryWrapper.create()
                    .eq("session_id", sessionId)
                    .eq("character_id", dto.getCharacterId()));
            ActivityParticipantResultDO row = ActivityParticipantResultDO.builder()
                    .sessionId(sessionId)
                    .characterId(dto.getCharacterId())
                    .characterName(dto.getCharacterName())
                    .teamId(dto.getTeamId())
                    .rankNo(dto.getRankNo())
                    .score(dto.getScore())
                    .finishTimeMs(dto.getFinishTimeMs())
                    .outcome(dto.getOutcome())
                    .tags(dto.getTags())
                    .createdAt(new Date())
                    .build();
            if (existing == null) {
                resultMapper.insert(row);
            } else {
                row.setId(existing.getId());
                resultMapper.update(row);
            }
        }
    }

    private boolean existsClaim(long sessionId, String tierCode, int characterId) {
        return claimMapper.selectCountByQuery(QueryWrapper.create()
                .eq("session_id", sessionId)
                .eq("tier_code", tierCode)
                .eq("character_id", characterId)) > 0;
    }

    private ActivityParticipantResultDTO baseResult(int id, String name, String outcome) {
        return ActivityParticipantResultDTO.builder()
                .characterId(id)
                .characterName(name)
                .outcome(outcome)
                .build();
    }

    private Character findOnline(int worldId, int characterId) {
        try {
            return Server.getInstance().getWorld(worldId).getPlayerStorage().getCharacterById(characterId);
        } catch (Exception e) {
            return null;
        }
    }

    private List<Integer> parseRelated(ActivityDefDO def) {
        List<Integer> maps = new ArrayList<>();
        try {
            List<Integer> parsed = JSON.parseArray(def.getRelatedMaps(), Integer.class);
            if (parsed != null) {
                maps.addAll(parsed);
            }
        } catch (Exception ignored) {
        }
        maps.add(MapId.EVENT_WINNER);
        maps.add(def.getLobbyMapId());
        maps.add(def.getEventMapId());
        return maps.stream().distinct().collect(Collectors.toList());
    }

    private ActivityRewardTierDTO toTierDto(ActivityRewardTierDO e) {
        return ActivityRewardTierDTO.builder()
                .id(e.getId())
                .activityCode(e.getActivityCode())
                .tierCode(e.getTierCode())
                .tierName(e.getTierName())
                .priority(e.getPriority())
                .exclusiveGroup(e.getExclusiveGroup())
                .matchJson(e.getMatchJson())
                .grantMode(e.getGrantMode())
                .mesos(e.getMesos())
                .exp(e.getExp())
                .itemId(e.getItemId())
                .itemQty(e.getItemQty())
                .item2Id(e.getItem2Id())
                .item2Qty(e.getItem2Qty())
                .announceName(e.getAnnounceName() != null && e.getAnnounceName() == 1)
                .announceTpl(e.getAnnounceTpl())
                .enabled(e.getEnabled() != null && e.getEnabled() == 1)
                .remark(e.getRemark())
                .build();
    }

    private ActivityClaimDTO toClaimDto(ActivityRewardClaimDO e) {
        return ActivityClaimDTO.builder()
                .id(e.getId())
                .sessionId(e.getSessionId())
                .tierCode(e.getTierCode())
                .status(e.getStatus())
                .mesos(e.getMesos())
                .exp(e.getExp())
                .itemId(e.getItemId())
                .itemQty(e.getItemQty())
                .item2Id(e.getItem2Id())
                .item2Qty(e.getItem2Qty())
                .build();
    }

    private int nz(Integer v) {
        return v == null ? 0 : v;
    }
}

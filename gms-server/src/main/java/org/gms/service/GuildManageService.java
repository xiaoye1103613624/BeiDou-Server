package org.gms.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.client.Job;
import org.gms.dao.entity.AllianceDO;
import org.gms.dao.entity.AllianceguildsDO;
import org.gms.dao.entity.CharactersDO;
import org.gms.dao.entity.GuildsDO;
import org.gms.dao.mapper.AllianceMapper;
import org.gms.dao.mapper.AllianceguildsMapper;
import org.gms.dao.mapper.CharactersMapper;
import org.gms.dao.mapper.GuildsMapper;
import org.gms.model.dto.GuildListRtnDTO;
import org.gms.model.dto.GuildManageReqDTO;
import org.gms.model.dto.GuildMemberRtnDTO;
import org.gms.net.server.Server;
import org.gms.util.I18nUtil;
import org.gms.util.RequireUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static org.gms.dao.entity.table.CharactersDOTableDef.CHARACTERS_D_O;
import static org.gms.dao.entity.table.GuildsDOTableDef.GUILDS_D_O;
import static org.gms.dao.entity.table.AllianceDOTableDef.ALLIANCE_D_O;
import static org.gms.dao.entity.table.AllianceguildsDOTableDef.ALLIANCEGUILDS_D_O;

/**
 * 【业务服务】GuildManageService：公会管理服务类，负责公会的查询、更新和解散操作。
 */
@Service
@AllArgsConstructor
@Slf4j
public class GuildManageService {

    /** 公会数据访问接口 */
    private final GuildsMapper guildsMapper;
    /** 角色数据访问接口 */
    private final CharactersMapper charactersMapper;
    /** 联盟数据访问接口 */
    private final AllianceMapper allianceMapper;
    /** 联盟公会关联数据访问接口 */
    private final AllianceguildsMapper allianceguildsMapper;

    /**
     * 分页查询公会列表。
     * 
     * <p>支持按公会名称模糊搜索，按GP降序排列，包含成员数、会长名称、联盟名称等信息。</p>
     * 
     * @param condition 查询条件（包含公会名称、分页信息）
     * @return 分页后的公会列表
     */
    public Page<GuildListRtnDTO> getGuildList(GuildManageReqDTO condition) {
        QueryWrapper qw = QueryWrapper.create()
                .select(GUILDS_D_O.ALL_COLUMNS)
                .from(GUILDS_D_O);
        
        // 按公会名称模糊搜索
        if (!RequireUtil.isEmpty(condition.getGuildName())) {
            qw.and(GUILDS_D_O.NAME.like(condition.getGuildName()));
        }
        
        // 按GP降序排列
        qw.orderBy(GUILDS_D_O.GP, false);
        Page<GuildsDO> page = guildsMapper.paginate(condition.getPageNo(), condition.getPageSize(), qw);

        // 收集需要关联查询的ID
        Set<Long> guildIds = page.getRecords().stream().map(GuildsDO::getGuildid).collect(Collectors.toSet());
        Set<Long> leaderIds = page.getRecords().stream().map(GuildsDO::getLeader).collect(Collectors.toSet());
        Set<Long> allianceIds = page.getRecords().stream()
                .map(GuildsDO::getAllianceId).filter(id -> id != null && id > 0).collect(Collectors.toSet());

        // 批量查询成员数、会长名称、联盟名称
        Map<Long, Long> memberCountMap = new HashMap<>();
        Map<Long, String> leaderNameMap = new HashMap<>();
        Map<Long, String> allianceNameMap = new HashMap<>();

        if (!guildIds.isEmpty()) {
            List<CharactersDO> members = charactersMapper.selectListByQuery(
                    QueryWrapper.create().select(CHARACTERS_D_O.GUILDID, CHARACTERS_D_O.ID).from(CHARACTERS_D_O)
                            .where(CHARACTERS_D_O.GUILDID.in(guildIds).and(CHARACTERS_D_O.GUILDRANK.gt(0)))
            );
            for (CharactersDO m : members) {
                memberCountMap.merge((long) m.getGuildid(), 1L, Long::sum);
            }
        }
        if (!leaderIds.isEmpty()) {
            List<CharactersDO> leaders = charactersMapper.selectListByQuery(
                    QueryWrapper.create().select(CHARACTERS_D_O.ID, CHARACTERS_D_O.NAME).from(CHARACTERS_D_O)
                            .where(CHARACTERS_D_O.ID.in(leaderIds))
            );
            for (CharactersDO l : leaders) {
                leaderNameMap.put((long) l.getId(), l.getName());
            }
        }
        if (!allianceIds.isEmpty()) {
            List<AllianceDO> alliances = allianceMapper.selectListByQuery(
                    QueryWrapper.create().select(ALLIANCE_D_O.ID, ALLIANCE_D_O.NAME).from(ALLIANCE_D_O)
                            .where(ALLIANCE_D_O.ID.in(allianceIds))
            );
            for (AllianceDO a : alliances) {
                allianceNameMap.put(a.getId(), a.getName());
            }
        }

        // 构建返回结果
        List<GuildListRtnDTO> records = page.getRecords().stream().map(g -> GuildListRtnDTO.builder()
                .guildid(g.getGuildid())
                .name(g.getName())
                .leaderId(g.getLeader())
                .leaderName(leaderNameMap.getOrDefault(g.getLeader(), "-"))
                .gp(g.getGp())
                .capacity(g.getCapacity())
                .notice(g.getNotice())
                .allianceId(g.getAllianceId())
                .allianceName(g.getAllianceId() != null && g.getAllianceId() > 0
                        ? allianceNameMap.getOrDefault(g.getAllianceId(), "-") : "-")
                .memberCount(memberCountMap.getOrDefault(g.getGuildid(), 0L).intValue())
                .logo(g.getLogo() != null ? g.getLogo().intValue() : 0)
                .logoColor(g.getLogoColor())
                .logoBG(g.getLogoBG())
                .logoBGColor(g.getLogoBGColor())
                .build()).collect(Collectors.toList());

        return new Page<>(records, page.getPageNumber(), page.getPageSize(), page.getTotalRow());
    }

    /**
     * 查询公会成员列表。
     * 
     * <p>查询指定公会的所有成员，包含在线状态、职位头衔等信息。</p>
     * 
     * @param guildId 公会ID
     * @return 公会成员列表
     */
    public List<GuildMemberRtnDTO> getGuildMembers(Long guildId) {
        // 获取公会信息以得到职位头衔
        GuildsDO guild = guildsMapper.selectOneById(guildId);
        if (guild == null) {
            return Collections.emptyList();
        }
        String[] rankTitles = {
                guild.getRank1title(), guild.getRank2title(), guild.getRank3title(),
                guild.getRank4title(), guild.getRank5title()
        };

        // 查询公会成员
        List<CharactersDO> members = charactersMapper.selectListByQuery(
                QueryWrapper.create().from(CHARACTERS_D_O)
                        .where(CHARACTERS_D_O.GUILDID.eq(guildId).and(CHARACTERS_D_O.GUILDRANK.gt(0)))
                        .orderBy(CHARACTERS_D_O.GUILDRANK, true)
        );

        // 获取在线玩家ID
        Set<Integer> onlineCharIds = new HashSet<>();
        try {
            Server.getInstance().getWorlds().forEach(world -> {
                world.getPlayerStorage().getAllCharacters().forEach(chr -> onlineCharIds.add(chr.getId()));
            });
        } catch (Exception ignored) {}

        // 构建成员信息
        return members.stream().map(m -> {
            String rankTitle = (m.getGuildrank() >= 1 && m.getGuildrank() <= 5)
                    ? rankTitles[m.getGuildrank() - 1] : "Member";
            return GuildMemberRtnDTO.builder()
                    .charId(m.getId())
                    .name(m.getName())
                    .level(m.getLevel())
                    .jobId(m.getJob())
                    .jobName(Job.getById(m.getJob()).getName())
                    .guildRank(m.getGuildrank())
                    .rankTitle(rankTitle)
                    .online(onlineCharIds.contains(m.getId()))
                    .allianceRank(m.getAllianceRank())
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * 更新公会信息。
     * 
     * @param data 公会数据
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateGuild(GuildsDO data) {
        RequireUtil.requireNotNull(data.getGuildid(), I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "guildid"));
        guildsMapper.update(data);
    }

    /**
     * 解散公会。
     * 
     * <p>清除所有成员的公会信息（guildid设为0，guildrank设为5，allianceRank设为5），然后删除公会记录。</p>
     * 
     * @param guildId 公会ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void disbandGuild(Long guildId) {
        RequireUtil.requireNotNull(guildId, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "guildId"));
        // 清除所有成员的公会信息
        charactersMapper.updateByQuery(
                CharactersDO.builder().guildid(0).guildrank(5).allianceRank(5).build(),
                QueryWrapper.create().where(CHARACTERS_D_O.GUILDID.eq(guildId))
        );
        // 删除公会记录
        guildsMapper.deleteById(guildId);
    }
}
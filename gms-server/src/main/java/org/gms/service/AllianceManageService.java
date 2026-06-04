package org.gms.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.dao.entity.AllianceDO;
import org.gms.dao.entity.AllianceguildsDO;
import org.gms.dao.entity.GuildsDO;
import org.gms.dao.mapper.AllianceMapper;
import org.gms.dao.mapper.AllianceguildsMapper;
import org.gms.dao.mapper.GuildsMapper;
import org.gms.model.dto.BasePageDTO;
import org.gms.util.I18nUtil;
import org.gms.util.RequireUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static org.gms.dao.entity.table.AllianceDOTableDef.ALLIANCE_D_O;
import static org.gms.dao.entity.table.AllianceguildsDOTableDef.ALLIANCEGUILDS_D_O;
import static org.gms.dao.entity.table.GuildsDOTableDef.GUILDS_D_O;

/**
 * 【业务服务】AllianceManageService：联盟管理服务类，负责联盟的查询、更新和解散操作。
 */
@Service
@AllArgsConstructor
@Slf4j
public class AllianceManageService {

    /** 联盟数据访问接口 */
    private final AllianceMapper allianceMapper;
    /** 联盟公会关联数据访问接口 */
    private final AllianceguildsMapper allianceguildsMapper;
    /** 公会数据访问接口 */
    private final GuildsMapper guildsMapper;

    /**
     * 分页查询联盟列表。
     * 
     * <p>包含联盟的基本信息和成员公会数量。</p>
     * 
     * @param condition 分页条件
     * @return 分页后的联盟列表
     */
    public Page<Map<String, Object>> getAllianceList(BasePageDTO condition) {
        QueryWrapper qw = QueryWrapper.create().from(ALLIANCE_D_O).orderBy(ALLIANCE_D_O.ID, true);
        Page<AllianceDO> page = allianceMapper.paginate(condition.getPageNo(), condition.getPageSize(), qw);

        // 收集联盟ID用于查询成员公会
        Set<Long> allianceIds = page.getRecords().stream().map(AllianceDO::getId).collect(Collectors.toSet());
        Map<Long, Long> guildCountMap = new HashMap<>();

        if (!allianceIds.isEmpty()) {
            List<AllianceguildsDO> agList = allianceguildsMapper.selectListByQuery(
                    QueryWrapper.create().from(ALLIANCEGUILDS_D_O)
                            .where(ALLIANCEGUILDS_D_O.ALLIANCEID.in(allianceIds))
            );
            // 统计每个联盟的公会数量
            for (AllianceguildsDO ag : agList) {
                guildCountMap.merge((long) ag.getAllianceid(), 1L, Long::sum);
            }
        }

        // 构建返回结果
        List<Map<String, Object>> records = page.getRecords().stream().map(a -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", a.getId());
            map.put("name", a.getName());
            map.put("capacity", a.getCapacity());
            map.put("notice", a.getNotice());
            map.put("rank1", a.getRank1());
            map.put("rank2", a.getRank2());
            map.put("rank3", a.getRank3());
            map.put("rank4", a.getRank4());
            map.put("rank5", a.getRank5());
            map.put("guildCount", guildCountMap.getOrDefault(a.getId(), 0L).intValue());
            return map;
        }).collect(Collectors.toList());

        return new Page<>(records, page.getPageNumber(), page.getPageSize(), page.getTotalRow());
    }

    /**
     * 获取联盟详情（包含成员公会列表）。
     * 
     * @param allianceId 联盟ID
     * @return 联盟详情（包含联盟信息和成员公会列表）
     */
    public Map<String, Object> getAllianceDetail(Long allianceId) {
        AllianceDO alliance = allianceMapper.selectOneById(allianceId);
        if (alliance == null) return null;

        // 查询联盟公会关联
        List<AllianceguildsDO> agList = allianceguildsMapper.selectListByQuery(
                QueryWrapper.create().from(ALLIANCEGUILDS_D_O).where(ALLIANCEGUILDS_D_O.ALLIANCEID.eq(allianceId))
        );
        
        // 查询公会名称
        Set<Long> guildIds = agList.stream().map(a -> (long) a.getGuildid()).collect(Collectors.toSet());
        Map<Long, String> guildNames = new HashMap<>();
        if (!guildIds.isEmpty()) {
            List<GuildsDO> guilds = guildsMapper.selectListByQuery(
                    QueryWrapper.create().select(GUILDS_D_O.GUILDID, GUILDS_D_O.NAME).from(GUILDS_D_O)
                            .where(GUILDS_D_O.GUILDID.in(guildIds))
            );
            for (GuildsDO g : guilds) {
                guildNames.put(g.getGuildid(), g.getName());
            }
        }

        // 构建成员公会列表
        List<Map<String, Object>> guildList = agList.stream().map(ag -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("allianceGuildId", ag.getId());
            m.put("guildId", ag.getGuildid());
            m.put("guildName", guildNames.getOrDefault((long) ag.getGuildid(), "-"));
            return m;
        }).collect(Collectors.toList());

        // 构建结果
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("alliance", alliance);
        result.put("guilds", guildList);
        return result;
    }

    /**
     * 更新联盟信息。
     * 
     * @param data 联盟数据
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateAlliance(AllianceDO data) {
        RequireUtil.requireNotNull(data.getId(), I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "id"));
        allianceMapper.update(data);
    }

    /**
     * 解散联盟（级联删除关联数据）。
     * 
     * <p>清除所有成员公会的allianceId，删除联盟公会关联记录，最后删除联盟记录。</p>
     * 
     * @param allianceId 联盟ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void disbandAlliance(Long allianceId) {
        RequireUtil.requireNotNull(allianceId, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "allianceId"));
        
        // 查询联盟的所有公会关联
        List<AllianceguildsDO> agList = allianceguildsMapper.selectListByQuery(
                QueryWrapper.create().from(ALLIANCEGUILDS_D_O).where(ALLIANCEGUILDS_D_O.ALLIANCEID.eq(allianceId))
        );
        
        // 清除所有成员公会的allianceId
        for (AllianceguildsDO ag : agList) {
            guildsMapper.update(GuildsDO.builder().guildid((long) ag.getGuildid()).allianceId(0L).build());
            allianceguildsMapper.deleteById(ag.getId());
        }
        
        // 删除联盟记录
        allianceMapper.deleteById(allianceId);
    }
}
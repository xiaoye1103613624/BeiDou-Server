package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.dao.entity.AllianceDO;
import org.gms.dao.entity.AllianceguildsDO;
import org.gms.dao.entity.GuildsDO;
import org.gms.dao.mapper.AllianceMapper;
import org.gms.dao.mapper.AllianceguildsMapper;
import org.gms.dao.mapper.GuildsMapper;
import org.gms.manager.ServerManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * 联盟管理服务 —— 查询、编辑、删除联盟
 */
@Slf4j
@Service
@AllArgsConstructor
public class AllianceService {

    private final AllianceMapper allianceMapper;
    private final AllianceguildsMapper allianceguildsMapper;
    private final GuildsMapper guildsMapper;

    @PostConstruct
    public void init() {
        log.info("联盟管理服务初始化完成");
    }

    /** 查询所有联盟（含下属公会列表） */
    public List<Map<String, Object>> getAllAlliances() {
        List<AllianceDO> alliances = allianceMapper.selectAll();
        List<AllianceguildsDO> ags = allianceguildsMapper.selectAll();
        List<Map<String, Object>> result = new ArrayList<>();
        for (AllianceDO a : alliances) {
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
            // 下属公会
            List<Map<String, String>> guildList = new ArrayList<>();
            for (AllianceguildsDO ag : ags) {
                if (ag.getAllianceid().equals(a.getId().intValue())) {
                    GuildsDO g = guildsMapper.selectOneById((long) ag.getGuildid());
                    Map<String, String> gm = new LinkedHashMap<>();
                    gm.put("guildId", String.valueOf(ag.getGuildid()));
                    gm.put("guildName", g != null ? g.getName() : "未知");
                    guildList.add(gm);
                }
            }
            map.put("guilds", guildList);
            map.put("guildCount", guildList.size());
            result.add(map);
        }
        return result;
    }

    /** 删除联盟 */
    @Transactional
    public void deleteAlliance(Long id) {
        // 清除下属公会的 allianceId
        AllianceDO a = allianceMapper.selectOneById(id);
        if (a != null) {
            List<AllianceguildsDO> ags = allianceguildsMapper.selectListByQuery(
                    QueryWrapper.create().where("allianceid = ?", id.intValue()));
            for (AllianceguildsDO ag : ags) {
                GuildsDO g = guildsMapper.selectOneById((long) ag.getGuildid());
                if (g != null) {
                    g.setAllianceId(null);
                    guildsMapper.update(g);
                }
            }
            allianceguildsMapper.deleteByQuery(
                    QueryWrapper.create().where("allianceid = ?", id.intValue()));
        }
        allianceMapper.deleteById(id);
        log.info("联盟已删除：allianceId={}", id);
    }
}

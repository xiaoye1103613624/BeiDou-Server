package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.dao.entity.GuildsDO;
import org.gms.dao.mapper.GuildsMapper;
import org.gms.manager.ServerManager;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * 公会管理服务 —— 查询、删除公会
 */
@Slf4j
@Service
@AllArgsConstructor
public class GuildService {

    private final GuildsMapper guildsMapper;

    @PostConstruct
    public void init() {
        log.info("公会管理服务初始化完成");
    }

    /** 查询所有公会（含成员数、会长名） */
    public List<Map<String, Object>> getAllGuilds() {
        List<GuildsDO> guilds = guildsMapper.selectAll();
        List<Map<String, Object>> result = new ArrayList<>();
        for (GuildsDO g : guilds) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("guildid", g.getGuildid());
            map.put("name", g.getName());
            map.put("leader", g.getLeader());
            map.put("leaderName", getCharacterName(g.getLeader()));
            map.put("gp", g.getGp());
            map.put("capacity", g.getCapacity());
            map.put("notice", g.getNotice());
            map.put("logo", g.getLogo());
            map.put("logoColor", g.getLogoColor());
            map.put("logoBG", g.getLogoBG());
            map.put("logoBGColor", g.getLogoBGColor());
            map.put("rank1title", g.getRank1title());
            map.put("rank2title", g.getRank2title());
            map.put("rank3title", g.getRank3title());
            map.put("rank4title", g.getRank4title());
            map.put("rank5title", g.getRank5title());
            map.put("allianceId", g.getAllianceId());
            map.put("signature", g.getSignature());
            map.put("memberCount", getMemberCount(g.getGuildid()));
            String allianceName = g.getAllianceId() != null && g.getAllianceId() > 0
                    ? getAllianceName(g.getAllianceId()) : "";
            map.put("allianceName", allianceName);
            result.add(map);
        }
        return result;
    }

    /** 获取公会成员列表 */
    public List<Map<String, Object>> getGuildMembers(Long guildId) {
        List<Map<String, Object>> members = new ArrayList<>();
        try {
            DataSource ds = ServerManager.getApplicationContext().getBean(DataSource.class);
            try (Connection con = ds.getConnection();
                 PreparedStatement ps = con.prepareStatement(
                         "SELECT id, name, level, job, guildrank FROM characters WHERE guildid = ? ORDER BY guildrank ASC, name ASC")) {
                ps.setLong(1, guildId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> m = new LinkedHashMap<>();
                        m.put("id", rs.getInt("id"));
                        m.put("name", rs.getString("name"));
                        m.put("level", rs.getInt("level"));
                        m.put("job", rs.getInt("job"));
                        m.put("guildrank", rs.getInt("guildrank"));
                        members.add(m);
                    }
                }
            }
        } catch (Exception e) {
            log.error("查询公会成员失败", e);
        }
        return members;
    }

    /** 删除公会 */
    public void deleteGuild(Long guildId) {
        guildsMapper.deleteById(guildId);
        log.info("公会已删除：guildId={}", guildId);
    }

    /** 获取公会成员数 */
    private int getMemberCount(Long guildId) {
        try {
            DataSource ds = ServerManager.getApplicationContext().getBean(DataSource.class);
            try (Connection con = ds.getConnection();
                 PreparedStatement ps = con.prepareStatement(
                         "SELECT COUNT(*) as cnt FROM characters WHERE guildid = ?")) {
                ps.setLong(1, guildId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt("cnt");
                }
            }
        } catch (Exception e) { /* ignore */ }
        return 0;
    }

    /** 根据角色ID获取名称 */
    private String getCharacterName(Long charId) {
        if (charId == null) return "";
        try {
            DataSource ds = ServerManager.getApplicationContext().getBean(DataSource.class);
            try (Connection con = ds.getConnection();
                 PreparedStatement ps = con.prepareStatement(
                         "SELECT name FROM characters WHERE id = ?")) {
                ps.setLong(1, charId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getString("name");
                }
            }
        } catch (Exception e) { /* ignore */ }
        return String.valueOf(charId);
    }

    private String getAllianceName(Long allianceId) {
        try {
            DataSource ds = ServerManager.getApplicationContext().getBean(DataSource.class);
            try (Connection con = ds.getConnection();
                 PreparedStatement ps = con.prepareStatement(
                         "SELECT name FROM alliance WHERE id = ?")) {
                ps.setLong(1, allianceId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getString("name");
                }
            }
        } catch (Exception e) { /* ignore */ }
        return "";
    }
}

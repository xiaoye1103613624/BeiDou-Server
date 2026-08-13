package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.gms.client.Skill;
import org.gms.client.SkillFactory;
import org.gms.dao.entity.SkillTechDO;
import org.gms.dao.mapper.SkillTechMapper;
import org.gms.skilltech.SkillTechManager;
import org.gms.skilltech.SkillTechWzSync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class SkillTechService {
    private final SkillTechMapper mapper;

    public List<SkillTechDO> listAll() {
        return mapper.selectListByQuery(QueryWrapper.create().orderBy("skill_id", true));
    }

    public Map<String, Object> previewSkill(int skillId) {
        Skill skill = SkillFactory.getSkill(skillId);
        Map<String, Object> m = new HashMap<>();
        m.put("skillId", skillId);
        m.put("name", SkillFactory.getSkillName(skillId));
        if (skill == null) {
            m.put("loaded", false);
            return m;
        }
        m.put("loaded", true);
        m.put("maxLevel", skill.getMaxLevel());
        m.put("spMaxLevel", skill.getSpMaxLevel());
        m.put("fourthJob", skill.isFourthJob());
        if (skill.getMaxLevel() > 0) {
            var eff = skill.getEffect(skill.getMaxLevel());
            Map<String, Object> top = new HashMap<>();
            top.put("damage", eff.getDamage());
            top.put("mpCon", (int) eff.getMpCon());
            top.put("attackCount", eff.getAttackCount());
            top.put("cooltime", eff.getCooldown());
            m.put("topLevelEffect", top);
        }
        return m;
    }

    @Transactional
    public void save(SkillTechDO dto) {
        if (dto == null || dto.getSkillId() == null) {
            throw new IllegalArgumentException("skillId is required");
        }
        Skill skill = SkillFactory.getSkill(dto.getSkillId());
        if (skill == null) {
            throw new IllegalArgumentException("未知技能: " + dto.getSkillId() + "（检查 Skill.wz）");
        }
        if (dto.getEnabled() == null) {
            dto.setEnabled(1);
        }
        if (dto.getClientSynced() == null) {
            dto.setClientSynced(0);
        }
        // 未填 spMax：默认锁定为当前 WZ/技改前效果等级
        if (dto.getSpMaxLevel() == null || dto.getSpMaxLevel() <= 0) {
            SkillTechDO existing = findBySkillId(dto.getSkillId());
            if (existing != null && existing.getSpMaxLevel() != null) {
                dto.setSpMaxLevel(existing.getSpMaxLevel());
            } else {
                // 若已技改过，用当前 spMax；否则用当前 maxLevel 作为「原始上限」
                int curSp = skill.getSpMaxLevel();
                int curMax = skill.getMaxLevel();
                dto.setSpMaxLevel(curSp > 0 && curSp < curMax ? curSp : curMax);
            }
        }
        if (dto.getEffectMaxLevel() == null || dto.getEffectMaxLevel() <= 0) {
            dto.setEffectMaxLevel(Math.max(dto.getSpMaxLevel(), skill.getMaxLevel()));
        }
        if (dto.getEffectMaxLevel() < dto.getSpMaxLevel()) {
            throw new IllegalArgumentException("effectMaxLevel 不能小于 spMaxLevel");
        }
        if (dto.getSkillName() == null || dto.getSkillName().isBlank()) {
            String name = SkillFactory.getSkillName(dto.getSkillId());
            dto.setSkillName(name != null ? name : String.valueOf(dto.getSkillId()));
        }
        // 改了效果等级/属性后需重新同步客户端
        dto.setClientSynced(0);

        SkillTechDO existing = findBySkillId(dto.getSkillId());
        if (dto.getId() == null && existing != null) {
            dto.setId(existing.getId());
        }
        if (dto.getId() == null) {
            mapper.insert(dto);
        } else {
            mapper.update(dto);
        }
        // 热应用：重载 WZ 技能再套技改（保证从干净 WZ 基底扩展）
        SkillFactory.reloadAllSkills();
    }

    @Transactional
    public void delete(Long id) {
        if (id != null) {
            mapper.deleteById(id);
            SkillFactory.reloadAllSkills();
        }
    }

    public void reload() {
        SkillFactory.reloadAllSkills();
    }

    /**
     * 同步客户端 Skill.img：先写服务端 XML 扩展节点，再尝试 orange-wz MCP。
     */
    public Map<String, Object> syncClient(Integer skillId) {
        Map<String, Object> result = SkillTechWzSync.sync(skillId);
        if (Boolean.TRUE.equals(result.get("clientSynced")) && skillId != null) {
            SkillTechDO row = findBySkillId(skillId);
            if (row != null) {
                row.setClientSynced(1);
                mapper.update(row);
            }
        }
        return result;
    }

    private SkillTechDO findBySkillId(int skillId) {
        return mapper.selectOneByQuery(QueryWrapper.create().eq(SkillTechDO::getSkillId, skillId));
    }
}

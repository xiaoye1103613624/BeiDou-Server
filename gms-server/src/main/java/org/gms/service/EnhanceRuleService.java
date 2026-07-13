package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.gms.dao.entity.EquipEnhanceRuleDO;
import org.gms.dao.mapper.EquipEnhanceRuleMapper;
import org.gms.server.combat.CombatSourceManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class EnhanceRuleService {
    private final EquipEnhanceRuleMapper mapper;

    public List<EquipEnhanceRuleDO> listAll() {
        return mapper.selectListByQuery(
                QueryWrapper.create().orderBy("sort_order", true).orderBy("id", true));
    }

    @Transactional
    public void save(EquipEnhanceRuleDO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("rule is required");
        }
        if (dto.getEnabled() == null) {
            dto.setEnabled(1);
        }
        if (dto.getSortOrder() == null) {
            dto.setSortOrder(0);
        }
        if (dto.getEquipType() == null || dto.getEquipType().isBlank()) {
            dto.setEquipType("ALL");
        }
        if (dto.getId() == null) {
            mapper.insert(dto);
        } else {
            mapper.update(dto);
        }
    }

    @Transactional
    public void delete(Long id) {
        if (id != null) {
            mapper.deleteById(id);
        }
    }

    public void reload() {
        CombatSourceManager.reload();
    }
}

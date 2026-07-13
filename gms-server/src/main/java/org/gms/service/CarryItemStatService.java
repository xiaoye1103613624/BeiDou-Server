package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.gms.dao.entity.CarryItemStatDO;
import org.gms.dao.mapper.CarryItemStatMapper;
import org.gms.server.combat.CombatSourceManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class CarryItemStatService {
    private final CarryItemStatMapper mapper;

    public List<CarryItemStatDO> listAll() {
        return mapper.selectListByQuery(QueryWrapper.create().orderBy("item_id", true));
    }

    @Transactional
    public void save(CarryItemStatDO dto) {
        if (dto == null || dto.getItemId() == null || dto.getItemId() <= 0) {
            throw new IllegalArgumentException("itemId is required");
        }
        if (dto.getEnabled() == null) {
            dto.setEnabled(1);
        }
        if (dto.getRequireEquipped() == null) {
            dto.setRequireEquipped(0);
        }
        if (dto.getId() == null) {
            QueryWrapper qw = QueryWrapper.create().eq("item_id", dto.getItemId());
            CarryItemStatDO existing = mapper.selectOneByQuery(qw);
            if (existing != null) {
                dto.setId(existing.getId());
                mapper.update(dto);
                return;
            }
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

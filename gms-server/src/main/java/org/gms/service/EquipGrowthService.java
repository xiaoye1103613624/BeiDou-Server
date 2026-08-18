package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.gms.dao.entity.EquipGrowthDO;
import org.gms.dao.mapper.EquipGrowthMapper;
import org.gms.model.dto.EquipGrowthDTO;
import org.gms.server.ItemInformationProvider;
import org.gms.server.equipgrowth.EquipGrowthConfigManager;
import org.gms.server.equipgrowth.EquipGrowthTipManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class EquipGrowthService {
    private final EquipGrowthMapper mapper;
    private final EquipGrowthConfigManager configManager;

    public List<EquipGrowthDTO> listAll() {
        configManager.reload();
        return mapper.selectAll().stream()
                .sorted(Comparator
                        .comparing((EquipGrowthDO r) -> r.getSortOrder() == null ? 0 : r.getSortOrder())
                        .thenComparing(r -> r.getItemId() == null ? 0 : r.getItemId()))
                .map(this::toDto)
                .toList();
    }

    public EquipGrowthDTO get(int itemId) {
        EquipGrowthDO row = findByItemId(itemId);
        return row == null ? null : toDto(row);
    }

    private EquipGrowthDO findByItemId(int itemId) {
        return mapper.selectOneByQuery(QueryWrapper.create().where("item_id = ?", itemId));
    }

    @Transactional
    public void save(EquipGrowthDTO dto) {
        if (dto == null || dto.getItemId() == null || dto.getItemId() <= 0) {
            throw new IllegalArgumentException("itemId required");
        }
        EquipGrowthDO existing = findByItemId(dto.getItemId());
        EquipGrowthDO row = existing != null ? existing : new EquipGrowthDO();
        row.setItemId(dto.getItemId());
        row.setItemName(dto.getItemName());
        row.setEnabled(dto.getEnabled() == null ? 1 : dto.getEnabled());
        // 技能写在各等级段；全局 skills_json 仅兼容旧数据，管理端保存时清空
        row.setLevelsJson(dto.getLevelsJson());
        int derived = EquipGrowthConfigManager.deriveMaxLevelFromLevelsJson(dto.getLevelsJson());
        if (derived > 0) {
            row.setMaxLevel(derived);
        } else {
            row.setMaxLevel(dto.getMaxLevel() == null ? 0 : dto.getMaxLevel());
        }
        row.setSortOrder(dto.getSortOrder() == null ? 0 : dto.getSortOrder());
        row.setRemark(dto.getRemark());
        row.setSkillsJson("[]");
        row.setSource(dto.getSource() != null ? dto.getSource() : "DB");
        if (row.getId() == null) {
            mapper.insert(row);
        } else {
            mapper.update(row);
        }
        configManager.reload();
    }

    @Transactional
    public void delete(Long id) {
        if (id != null) {
            mapper.deleteById(id);
            configManager.reload();
        }
    }

    public void reload() {
        configManager.reload();
    }

    @Transactional
    public MapResult initFromWz(String mode, List<Integer> itemIds) {
        String m = mode == null ? "NEW_ONLY" : mode.toUpperCase();
        Set<Integer> targets = new HashSet<>();
        if (itemIds != null && !itemIds.isEmpty()) {
            targets.addAll(itemIds);
        } else {
            // 与套装「从 WZ 导入」一致：空列表 = 扫服务端 Character.wz 全部可成长装
            targets.addAll(ItemInformationProvider.getInstance().listWzGrowthCapableItemIds());
        }
        int imported = 0;
        int skipped = 0;
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        List<Integer> sorted = new ArrayList<>(targets);
        sorted.sort(Comparator.naturalOrder());
        for (int itemId : sorted) {
            int max;
            try {
                max = ii.getEquipLevel(itemId, true);
            } catch (Throwable t) {
                skipped++;
                continue;
            }
            if (max <= 1) {
                skipped++;
                continue;
            }
            EquipGrowthDO existing = findByItemId(itemId);
            if (existing != null && "NEW_ONLY".equals(m)) {
                skipped++;
                continue;
            }
            String levelsJson = EquipGrowthConfigManager.buildLevelsJsonFromWz(itemId);
            String name;
            try {
                name = ii.getName(itemId);
            } catch (Throwable t) {
                name = null;
            }
            EquipGrowthDO row = existing != null ? existing : new EquipGrowthDO();
            row.setItemId(itemId);
            row.setItemName(name);
            row.setEnabled(1);
            // max = WZ 物品等级上限；段数通常为 max-1
            int derived = EquipGrowthConfigManager.deriveMaxLevelFromLevelsJson(levelsJson);
            row.setMaxLevel(derived > 0 ? derived : max);
            row.setSortOrder(0);
            row.setLevelsJson(levelsJson);
            row.setSkillsJson("[]");
            row.setSource("WZ+DB");
            if (row.getId() == null) {
                mapper.insert(row);
            } else {
                mapper.update(row);
            }
            imported++;
        }
        configManager.reload();
        return new MapResult(imported, skipped);
    }

    public String previewTip(int itemId) {
        if (configManager.hasCustomLevels(itemId)) {
            return configManager.buildCustomTipText(itemId);
        }
        return EquipGrowthTipManager.buildWzOnlyGrowthText(itemId);
    }

    private EquipGrowthDTO toDto(EquipGrowthDO row) {
        EquipGrowthDTO dto = new EquipGrowthDTO();
        dto.setId(row.getId());
        dto.setItemId(row.getItemId());
        dto.setItemName(row.getItemName());
        dto.setEnabled(row.getEnabled());
        dto.setMaxLevel(row.getMaxLevel());
        dto.setSortOrder(row.getSortOrder());
        dto.setRemark(row.getRemark());
        dto.setLevelsJson(row.getLevelsJson());
        dto.setSkillsJson(row.getSkillsJson());
        dto.setSource(row.getSource());
        dto.setLevelCount(EquipGrowthConfigManager.countLevels(row.getLevelsJson()));
        dto.setTipPreview(previewTip(row.getItemId() == null ? 0 : row.getItemId()));
        return dto;
    }

    public record MapResult(int imported, int skipped) {}
}

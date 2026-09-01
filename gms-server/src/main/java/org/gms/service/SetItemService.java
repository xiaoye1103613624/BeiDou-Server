package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.gms.combat.format.CombatStatFormatter;
import org.gms.combat.provider.SetBonusStatProvider;
import org.gms.combat.stat.CombatStatType;
import org.gms.dao.entity.SetItemDO;
import org.gms.dao.mapper.SetItemMapper;
import org.gms.exception.BizException;
import org.gms.model.dto.SetItemDTO;
import org.gms.model.dto.SetItemDetailDTO;
import org.gms.model.dto.SetItemPreviewDTO;
import org.gms.model.dto.SetItemPreviewRequest;
import org.gms.model.dto.SetItemWzImportRequest;
import org.gms.server.setitem.SetBonus;
import org.gms.server.setitem.SetBonusColor;
import org.gms.server.setitem.SetDefinition;
import org.gms.server.setitem.SetItemManager;
import org.gms.server.setitem.SetTiersV2Parser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.gms.dao.entity.table.SetItemDOTableDef.SET_ITEM_D_O;

@Service
@AllArgsConstructor
public class SetItemService {
    private final SetItemMapper setItemMapper;

    public List<SetItemDTO> listAll() {
        QueryWrapper qw = QueryWrapper.create()
                .orderBy(SET_ITEM_D_O.SORT_ORDER.asc(), SET_ITEM_D_O.SET_ID.asc());
        return setItemMapper.selectListByQuery(qw).stream().map(this::toDto).toList();
    }

    public List<SetItemDetailDTO> listMerged() {
        SetItemManager.loadOrSeed();
        Map<Integer, SetItemDO> dbMap = new HashMap<>();
        for (SetItemDO row : setItemMapper.selectListByQuery(QueryWrapper.create())) {
            dbMap.put(row.getSetId(), row);
        }
        List<SetItemDetailDTO> out = new ArrayList<>();
        for (SetDefinition def : SetItemManager.listAllDefinitions()) {
            out.add(toDetail(def, dbMap.get(def.setId)));
        }
        return out;
    }

    public SetItemDetailDTO getDetail(int setId) {
        SetItemManager.loadOrSeed();
        SetDefinition def = SetItemManager.getDefinition(setId);
        if (def == null) {
            return null;
        }
        QueryWrapper qw = QueryWrapper.create().where(SET_ITEM_D_O.SET_ID.eq(setId));
        SetItemDO db = setItemMapper.selectOneByQuery(qw);
        return toDetail(def, db);
    }

    public List<SetItemDetailDTO> listWzSets() {
        SetItemManager.loadOrSeed();
        List<SetItemDetailDTO> out = new ArrayList<>();
        for (int setId : SetItemManager.getValidWzSetIds()) {
            SetDefinition def = SetItemManager.getDefinition(setId);
            if (def != null) {
                QueryWrapper qw = QueryWrapper.create().where(SET_ITEM_D_O.SET_ID.eq(setId));
                SetItemDO db = setItemMapper.selectOneByQuery(qw);
                out.add(toDetail(def, db));
            }
        }
        return out;
    }

    @Transactional
    public Map<String, Object> importFromWz(SetItemWzImportRequest req) {
        SetItemManager.loadOrSeed();
        String mode = req.getMode() == null ? "NEW_ONLY" : req.getMode().toUpperCase();
        List<Integer> ids = req.getSetIds();
        if (ids == null || ids.isEmpty()) {
            ids = new ArrayList<>(SetItemManager.getValidWzSetIds());
        }
        int imported = 0;
        int skipped = 0;
        for (int setId : ids) {
            SetDefinition def = SetItemManager.getDefinition(setId);
            if (def == null || !def.fromWz) {
                skipped++;
                continue;
            }
            QueryWrapper qw = QueryWrapper.create().where(SET_ITEM_D_O.SET_ID.eq(setId));
            SetItemDO existing = setItemMapper.selectOneByQuery(qw);
            if (existing != null && "NEW_ONLY".equals(mode)) {
                skipped++;
                continue;
            }
            SetItemDO entity = existing != null ? existing : new SetItemDO();
            entity.setSetId(setId);
            entity.setSetName(def.setName);
            entity.setCompleteCount(def.completeCount);
            entity.setItemIds(SetItemManager.itemIdsToCsv(def));
            if (entity.getEnabled() == null) {
                entity.setEnabled(1);
            }
            if (entity.getSortOrder() == null) {
                entity.setSortOrder(0);
            }
            if (!"OVERWRITE".equals(mode) && existing != null) {
                if (entity.getTiersJson() == null || entity.getTiersJson().isBlank()) {
                    entity.setTiersJson(SetTiersV2Parser.toTiersJson(def));
                }
            } else {
                entity.setTiersJson(SetTiersV2Parser.toTiersJson(def));
            }
            if (existing == null) {
                setItemMapper.insert(entity);
            } else {
                setItemMapper.update(entity);
            }
            imported++;
        }
        SetItemManager.reload();
        return Map.of("imported", imported, "skipped", skipped);
    }

    public SetItemPreviewDTO preview(SetItemPreviewRequest req) {
        SetItemManager.loadOrSeed();
        int setId = req.getSetId() == null ? 0 : req.getSetId();
        int count = req.getEquippedCount() == null ? 0 : req.getEquippedCount();
        int jobId = req.getJobId() == null ? 100 : req.getJobId();
        SetDefinition def = SetItemManager.getDefinition(setId);
        SetBonus bonus = SetItemManager.previewBonus(setId, count, jobId);
        StringBuilder sb = new StringBuilder();
        if (def != null) {
            sb.append(def.setName).append(" (").append(count).append("/")
                    .append(def.completeCount > 0 ? def.completeCount : def.itemIds.size()).append(")\r\n");
        }
        CombatStatFormatter.appendSetBonusLines(sb, bonus);
        List<org.gms.combat.stat.CombatStatModifier> mods = new ArrayList<>();
        SetBonusStatProvider.appendFromSetBonus(mods, bonus, "preview");
        var profile = new org.gms.combat.stat.CombatStatResolver().resolve(mods);
        return SetItemPreviewDTO.builder()
                .tooltipText(sb.toString().trim())
                .bonusSummary(sb.toString().trim())
                .finalDamageMultiplier(profile.finalDamageMultiplier)
                .finalDamageDisplayPercent((int) Math.round((profile.finalDamageMultiplier - 1.0) * 100.0))
                .build();
    }

    public Map<String, Object> statFieldMeta() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("basicStats", List.of(
                field("str", "力量", "int"),
                field("dex", "敏捷", "int"),
                field("int", "智力", "int"),
                field("luk", "运气", "int"),
                field("pad", "物理攻击", "int"),
                field("mad", "魔法攻击", "int"),
                field("pdd", "物理防御", "int"),
                field("mdd", "魔法防御", "int"),
                field("acc", "命中", "int"),
                field("eva", "回避", "int"),
                field("mhp", "HP", "int"),
                field("mmp", "MP", "int"),
                field("allStat", "全属性", "int"),
                field("speed", "移速", "int"),
                field("jump", "跳跃", "int")
        ));
        result.put("basicStatsPercent", List.of(
                field("strR", "力量%", "percent"),
                field("dexR", "敏捷%", "percent"),
                field("intR", "智力%", "percent"),
                field("lukR", "运气%", "percent"),
                field("mhpR", "HP%", "percent"),
                field("mmpR", "MP%", "percent")
        ));
        List<Map<String, String>> combat = new ArrayList<>();
        for (CombatStatType t : CombatStatType.values()) {
            Map<String, String> m = new LinkedHashMap<>();
            m.put("key", t.getKey());
            m.put("label", t.getLabel());
            m.put("stackRule", t.getStackRule().name());
            combat.add(m);
        }
        result.put("combatStats", combat);
        return result;
    }

    public Map<String, Map<String, String>> colorMeta() {
        Map<String, Map<String, String>> result = new LinkedHashMap<>();
        for (SetBonusColor color : SetBonusColor.values()) {
            Map<String, String> entry = new LinkedHashMap<>();
            entry.put("code", color.getCode());
            entry.put("label", color.getLabel());
            result.put(color.name(), entry);
        }
        return result;
    }

    private static Map<String, String> field(String key, String label, String type) {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("key", key);
        m.put("label", label);
        m.put("type", type);
        return m;
    }

    @Transactional
    public void save(SetItemDTO dto) {
        if (dto == null || dto.getSetId() == null || dto.getSetId() <= 0) {
            throw BizException.illegalArgument();
        }
        SetItemDO entity = toEntity(dto);
        if (entity.getId() == null) {
            setItemMapper.insert(entity);
        } else {
            setItemMapper.update(entity);
        }
    }

    @Transactional
    public void delete(Long id) {
        if (id != null) {
            setItemMapper.deleteById(id);
        }
    }

    public void reload() {
        SetItemManager.reload();
    }

    private SetItemDTO toDto(SetItemDO entity) {
        return SetItemDTO.builder()
                .id(entity.getId())
                .setId(entity.getSetId())
                .setName(entity.getSetName())
                .completeCount(entity.getCompleteCount())
                .itemIds(entity.getItemIds())
                .enabled(entity.getEnabled())
                .sortOrder(entity.getSortOrder())
                .remark(entity.getRemark())
                .tiersJson(entity.getTiersJson())
                .build();
    }

    private SetItemDetailDTO toDetail(SetDefinition def, SetItemDO db) {
        return SetItemDetailDTO.builder()
                .id(db != null ? db.getId() : null)
                .setId(def.setId)
                .setName(def.setName)
                .completeCount(def.completeCount)
                .itemIds(db != null && db.getItemIds() != null && !db.getItemIds().isBlank()
                        ? db.getItemIds() : SetItemManager.itemIdsToCsv(def))
                .enabled(def.enabled ? 1 : 0)
                .sortOrder(db != null ? db.getSortOrder() : 0)
                .remark(db != null ? db.getRemark() : null)
                .tiersJson(db != null && db.getTiersJson() != null && !db.getTiersJson().isBlank()
                        ? db.getTiersJson() : SetTiersV2Parser.toTiersJson(def))
                .source(SetItemManager.sourceLabel(def))
                .fromWz(def.fromWz)
                .fromDb(def.fromDb)
                .tierCount(def.tiers.size())
                .itemCount(def.itemIds.size())
                .build();
    }

    private SetItemDO toEntity(SetItemDTO dto) {
        return SetItemDO.builder()
                .id(dto.getId())
                .setId(dto.getSetId())
                .setName(dto.getSetName())
                .completeCount(dto.getCompleteCount())
                .itemIds(dto.getItemIds())
                .enabled(dto.getEnabled() == null ? 1 : dto.getEnabled())
                .sortOrder(dto.getSortOrder() == null ? 0 : dto.getSortOrder())
                .remark(dto.getRemark())
                .tiersJson(dto.getTiersJson())
                .build();
    }
}

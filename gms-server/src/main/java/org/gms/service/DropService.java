package org.gms.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.gms.dao.entity.DropDataDO;
import org.gms.dao.entity.DropDataGlobalDO;
import org.gms.dao.mapper.DropDataGlobalMapper;
import org.gms.dao.mapper.DropDataMapper;
import org.gms.model.dto.DropSearchReqDTO;
import org.gms.model.dto.DropSearchRtnDTO;
import org.gms.model.dto.MobDropGroupDTO;
import org.gms.server.ItemInformationProvider;
import org.gms.server.life.MonsterInformationProvider;
import org.gms.server.quest.Quest;
import org.gms.util.Pair;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
public class DropService {
    private final DropDataMapper dropDataMapper;
    private final DropDataGlobalMapper dropDataGlobalMapper;
    private final GameIconService gameIconService;

    public Page<MobDropGroupDTO> getMobGroupList(DropSearchReqDTO data) {
        Integer pageNo = data.getPageNo() == null || data.getPageNo() < 1 ? 1 : data.getPageNo();
        Integer pageSize = data.getPageSize() == null || data.getPageSize() < 1 ? 20 : data.getPageSize();

        List<Integer> dropperIds = null;
        if (data.getDropperName() != null && !data.getDropperName().isEmpty()) {
            dropperIds = MonsterInformationProvider.getMobsIDsFromName(data.getDropperName())
                    .stream()
                    .map(Pair::getLeft)
                    .toList();
            if (dropperIds.isEmpty()) {
                return new Page<>(Collections.emptyList(), pageNo, pageSize, 0);
            }
        }

        List<Integer> itemIds = null;
        if (data.getItemName() != null && !data.getItemName().isEmpty()) {
            itemIds = ItemInformationProvider.getItemsIDsFromName(data.getItemName())
                    .stream()
                    .map(Pair::getLeft)
                    .toList();
            if (itemIds.isEmpty()) {
                return new Page<>(Collections.emptyList(), pageNo, pageSize, 0);
            }
        }

        long total = dropDataMapper.countMobGroups(
                data.getDropperId(), dropperIds, data.getItemId(), itemIds, data.getQuestId());
        if (total == 0) {
            return new Page<>(Collections.emptyList(), pageNo, pageSize, 0);
        }
        long offset = (long) (pageNo - 1) * pageSize;
        List<MobDropGroupDTO> rows = dropDataMapper.selectMobGroups(
                data.getDropperId(), dropperIds, data.getItemId(), itemIds, data.getQuestId(),
                offset, pageSize);
        for (MobDropGroupDTO row : rows) {
            row.setDropperName(getMobName(row.getDropperId()));
            String iconUrl = gameIconService.iconUrl(GameIconService.CATEGORY_MOB, row.getDropperId());
            row.setMobIconUrl(iconUrl);
            row.setHasIcon(iconUrl != null);
        }
        return new Page<>(rows, pageNo, pageSize, total);
    }

    public Page<DropSearchRtnDTO> getDropList(DropSearchReqDTO data, boolean isGlobal) {
        if (isGlobal) {
            DropDataGlobalDO dropDataGlobalDO = new DropDataGlobalDO();
            if (data.getContinent() != null) dropDataGlobalDO.setContinent(data.getContinent());
            if (data.getItemId() != null) dropDataGlobalDO.setItemid(data.getItemId());
            if (data.getQuestId() != null) dropDataGlobalDO.setQuestid(data.getQuestId());
            if (data.getEnabled() != null) dropDataGlobalDO.setEnabled(data.getEnabled());

            QueryWrapper queryWrapper = QueryWrapper.create(dropDataGlobalDO);
            // 物品名称模糊查询
            if (data.getItemName() != null && !data.getItemName().isEmpty()) {
                List<Integer> itemIds = ItemInformationProvider.getItemsIDsFromName(data.getItemName())
                        .stream()
                        .map(Pair::getLeft)
                        .toList();
                if (!itemIds.isEmpty()) {
                    queryWrapper.and(DropDataGlobalDO::getItemid).in(itemIds);
                } else {
                    // 如果没有匹配的物品，返回空结果
                    return new Page<>(Collections.emptyList(), data.getPageNo(), data.getPageSize(), 0);
                }
            }

            Page<DropDataGlobalDO> paginate = dropDataGlobalMapper.paginate(data.getPageNo(), data.getPageSize(), queryWrapper);
            return new Page<>(
                    paginate.getRecords().stream()
                            .map(record -> DropSearchRtnDTO.builder()
                                    .id(record.getId())
                                    .continent(record.getContinent())
                                    .itemId(record.getItemid())
                                    .itemName(getItemName(record.getItemid()))
                                    .itemIconUrl(gameIconService.iconUrl(GameIconService.CATEGORY_ITEM, record.getItemid()))
                                    .minimumQuantity(record.getMinimumQuantity())
                                    .maximumQuantity(record.getMaximumQuantity())
                                    .questId(record.getQuestid())
                                    .questName(getQuestName(record.getQuestid()))
                                    .chance(record.getChance())
                                    .comments(record.getComments())
                                    .enabled(record.getEnabled() == null ? 1 : record.getEnabled())
                                    .build())
                            .toList(),
                    paginate.getPageNumber(),
                    paginate.getPageSize(),
                    paginate.getTotalRow()
            );
        } else {
            DropDataDO dropDataDO = new DropDataDO();
            if (data.getDropperId() != null) dropDataDO.setDropperid(data.getDropperId());
            if (data.getItemId() != null) dropDataDO.setItemid(data.getItemId());
            if (data.getQuestId() != null) dropDataDO.setQuestid(data.getQuestId());

            QueryWrapper queryWrapper = QueryWrapper.create(dropDataDO);
            // 怪物名称模糊查询
            if (data.getDropperName() != null && !data.getDropperName().isEmpty()) {
                List<Integer> mobIds = MonsterInformationProvider.getMobsIDsFromName(data.getDropperName())
                        .stream()
                        .map(Pair::getLeft)
                        .toList();
                if (!mobIds.isEmpty()) {
                    queryWrapper.and(DropDataDO::getDropperid).in(mobIds);
                } else {
                    // 如果没有匹配的怪物，返回空结果
                    return new Page<>(Collections.emptyList(), data.getPageNo(), data.getPageSize(), 0);
                }
            }
            // 物品名称模糊查询
            if (data.getItemName() != null && !data.getItemName().isEmpty()) {
                List<Integer> itemIds = ItemInformationProvider.getItemsIDsFromName(data.getItemName())
                        .stream()
                        .map(Pair::getLeft)
                        .toList();
                if (!itemIds.isEmpty()) {
                    queryWrapper.and(DropDataDO::getItemid).in(itemIds);
                } else {
                    // 如果没有匹配的物品，返回空结果
                    return new Page<>(Collections.emptyList(), data.getPageNo(), data.getPageSize(), 0);
                }
            }

            Page<DropDataDO> paginate = dropDataMapper.paginate(data.getPageNo(), data.getPageSize(), queryWrapper);
            return new Page<>(
                    paginate.getRecords().stream()
                            .map(record -> DropSearchRtnDTO.builder()
                                    .id(record.getId())
                                    .dropperId(record.getDropperid())
                                    .dropperName(getMobName(record.getDropperid()))
                                    .mobIconUrl(gameIconService.iconUrl(GameIconService.CATEGORY_MOB, record.getDropperid()))
                                    .itemId(record.getItemid())
                                    .itemName(getItemName(record.getItemid()))
                                    .itemIconUrl(gameIconService.iconUrl(GameIconService.CATEGORY_ITEM, record.getItemid()))
                                    .minimumQuantity(record.getMinimumQuantity())
                                    .maximumQuantity(record.getMaximumQuantity())
                                    .questId(record.getQuestid())
                                    .questName(getQuestName(record.getQuestid()))
                                    .chance(record.getChance())
                                    .build())
                            .toList(),
                    paginate.getPageNumber(),
                    paginate.getPageSize(),
                    paginate.getTotalRow()
            );
        }
    }

    public Long modifyDropData(DropSearchRtnDTO data, boolean isGlobal, boolean isDelete) {
        Long dropDataId;
        if (isDelete) {
            (isGlobal ? dropDataGlobalMapper : dropDataMapper).deleteById(data.getId());
            dropDataId = data.getId();
        } else {
            if (isGlobal) {
                DropDataGlobalDO dropDataGlobalDO = DropDataGlobalDO.builder()
                        .id(data.getId())
                        .continent(data.getContinent())
                        .itemid(data.getItemId())
                        .minimumQuantity(data.getMinimumQuantity())
                        .maximumQuantity(data.getMaximumQuantity())
                        .questid(data.getQuestId())
                        .chance(data.getChance())
                        .comments(data.getComments())
                        .enabled(data.getEnabled() == null ? 1 : data.getEnabled())
                        .build();
                dropDataGlobalMapper.insertOrUpdate(dropDataGlobalDO, true);
                dropDataId = dropDataGlobalDO.getId();
                // 保存后按默认版本补齐物品图标（已存在则跳过）
                if (data.getItemId() != null && data.getItemId() > 0) {
                    gameIconService.ensureIcon(GameIconService.CATEGORY_ITEM, data.getItemId());
                }
            } else {
                DropDataDO dropDataDO = DropDataDO.builder()
                        .id(data.getId())
                        .dropperid(data.getDropperId())
                        .itemid(data.getItemId())
                        .minimumQuantity(data.getMinimumQuantity())
                        .maximumQuantity(data.getMaximumQuantity())
                        .questid(data.getQuestId())
                        .chance(data.getChance())
                        .build();
                dropDataMapper.insertOrUpdate(dropDataDO, true);
                dropDataId = dropDataDO.getId();
                if (data.getDropperId() != null && data.getDropperId() > 0) {
                    gameIconService.ensureIcon(GameIconService.CATEGORY_MOB, data.getDropperId());
                }
                if (data.getItemId() != null && data.getItemId() > 0) {
                    gameIconService.ensureIcon(GameIconService.CATEGORY_ITEM, data.getItemId());
                }
            }
        }
        MonsterInformationProvider.getInstance().clearDrops();
        return dropDataId;
    }

    public void toggleGlobalDropEnabled(Long id) {
        DropDataGlobalDO existing = dropDataGlobalMapper.selectOneById(id);
        if (existing == null) {
            return;
        }
        int next = (existing.getEnabled() == null || existing.getEnabled() == 1) ? 0 : 1;
        existing.setEnabled(next);
        dropDataGlobalMapper.update(existing);
        MonsterInformationProvider.getInstance().clearDrops();
    }

    private String getItemName(Integer itemId) {
        return itemId == null ? null : ItemInformationProvider.getInstance().getName(itemId);
    }

    private String getMobName(Integer mobId) {
        return mobId == null ? null : MonsterInformationProvider.getInstance().getMobNameFromId(mobId);
    }

    private String getQuestName(Integer questId) {
        return questId == null ? null : Quest.getInstance(questId).getName();
    }
}

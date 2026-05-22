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
import org.gms.server.ItemInformationProvider;
import org.gms.server.life.MonsterInformationProvider;
import org.gms.server.quest.Quest;
import org.gms.util.Pair;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 【业务服务】DropService：封装掉落数据（怪物掉落、全局掉落）的分页查询、新增、修改与删除相关的业务逻辑。
 *
 * <p>掉落数据分为两类：</p>
 * <ul>
 *   <li><b>怪物掉落（drop_data）</b>：关联 dropperId（怪物 ID），支持按怪物名和物品名模糊筛选</li>
 *   <li><b>全局掉落（drop_data_global）</b>：按洲际（continent）划分，支持按物品名模糊筛选</li>
 * </ul>
 * <p>所有修改操作完成后会调用 {@link MonsterInformationProvider#clearDrops()} 清除服务端掉落缓存，
 * 确保下次查询时重新加载最新数据。</p>
 */
@Service
@AllArgsConstructor
public class DropService {
    private final DropDataMapper dropDataMapper;
    private final DropDataGlobalMapper dropDataGlobalMapper;

    /**
     * 分页查询掉落列表。
     *
     * <p>支持多条件组合筛选：dropperId/怪物名、itemId/物品名、questId、continent（仅全局模式）。
     * 怪物名和物品名通过模糊匹配解析为 ID 列表后再用 IN 查询。</p>
     *
     * @param data     查询请求参数（含分页、筛选条件）
     * @param isGlobal true 查询全局掉落表，false 查询怪物掉落表
     * @return 分页后的掉落数据列表（含解析后的物品名、怪物名、任务名）
     */
    public Page<DropSearchRtnDTO> getDropList(DropSearchReqDTO data, boolean isGlobal) {
        if (isGlobal) {
            // === 全局掉落查询 ===
            DropDataGlobalDO dropDataGlobalDO = new DropDataGlobalDO();
            if (data.getContinent() != null) dropDataGlobalDO.setContinent(data.getContinent());
            if (data.getItemId() != null) dropDataGlobalDO.setItemid(data.getItemId());
            if (data.getQuestId() != null) dropDataGlobalDO.setQuestid(data.getQuestId());

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
                                    .minimumQuantity(record.getMinimumQuantity())
                                    .maximumQuantity(record.getMaximumQuantity())
                                    .questId(record.getQuestid())
                                    .questName(getQuestName(record.getQuestid()))
                                    .chance(record.getChance())
                                    .comments(record.getComments())
                                    .build())
                            .toList(),
                    paginate.getPageNumber(),
                    paginate.getPageSize(),
                    paginate.getTotalRow()
            );
        } else {
            // === 怪物掉落查询 ===
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
                                    .itemId(record.getItemid())
                                    .itemName(getItemName(record.getItemid()))
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

    /**
     * 新增、修改或删除一条掉落数据。
     *
     * <p>操作完成后会调用 {@link MonsterInformationProvider#clearDrops()} 清除服务端缓存，
     * 以保证下次查询或游戏中掉落逻辑使用最新配置。</p>
     *
     * @param data     掉落数据 DTO（新增时 id 为 null，修改时 id 必填）
     * @param isGlobal true 操作全局掉落表，false 操作怪物掉落表
     * @param isDelete true 执行删除操作
     * @return 操作后的数据 ID
     */
    public Long modifyDropData(DropSearchRtnDTO data, boolean isGlobal, boolean isDelete) {
        Long dropDataId;
        if (isDelete) {
            // 按主键删除
            (isGlobal ? dropDataGlobalMapper : dropDataMapper).deleteById(data.getId());
            dropDataId = data.getId();
        } else {
            // 新增或更新（insertOrUpdate）
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
                        .build();
                dropDataGlobalMapper.insertOrUpdate(dropDataGlobalDO, true);
                dropDataId = dropDataGlobalDO.getId();
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
            }
        }
        // 清除服务端掉落缓存，确保数据即时生效
        MonsterInformationProvider.getInstance().clearDrops();
        return dropDataId;
    }

    /**
     * 根据物品 ID 查询物品名称。
     *
     * @param itemId 物品 ID
     * @return 物品名称，id 为 null 时返回 null
     */
    private String getItemName(Integer itemId) {
        return itemId == null ? null : ItemInformationProvider.getInstance().getName(itemId);
    }

    /**
     * 根据怪物 ID 查询怪物名称。
     *
     * @param mobId 怪物 ID
     * @return 怪物名称，id 为 null 时返回 null
     */
    private String getMobName(Integer mobId) {
        return mobId == null ? null : MonsterInformationProvider.getInstance().getMobNameFromId(mobId);
    }

    /**
     * 根据任务 ID 查询任务名称。
     *
     * @param questId 任务 ID
     * @return 任务名称，id 为 null 时返回 null
     */
    private String getQuestName(Integer questId) {
        return questId == null ? null : Quest.getInstance(questId).getName();
    }
}

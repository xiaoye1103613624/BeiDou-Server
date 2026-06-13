package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.dao.entity.*;
import org.gms.dao.mapper.*;
import org.gms.model.dto.NewbieGiftSaveDTO;
import org.gms.model.dto.NewbieGiftSaveDTO.CurrencyDTO;
import org.gms.model.dto.NewbieGiftSaveDTO.ItemDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 新手礼包服务类，负责礼包配置的增删改查和领取逻辑。
 */
@Slf4j
@Service
@AllArgsConstructor
public class NewbieGiftService {

    /**
     * 新手礼包配置主表数据访问对象
     */
    private final NewbieGiftConfigMapper configMapper;

    /**
     * 新手礼包物品表数据访问对象
     */
    private final NewbieGiftItemMapper itemMapper;

    /**
     * 新手礼包货币表数据访问对象
     */
    private final NewbieGiftCurrencyMapper currencyMapper;

    /**
     * 新手礼包领取记录表数据访问对象
     */
    private final NewbieGiftRecordMapper recordMapper;

    /**
     * 获取所有礼包配置列表。
     *
     * @return 礼包配置DTO列表
     */
    public List<NewbieGiftSaveDTO> getConfigList() {
        List<NewbieGiftConfigDO> configs = configMapper.selectAll();
        List<NewbieGiftItemDO> allItems = itemMapper.selectAll();
        List<NewbieGiftCurrencyDO> allCurrencies = currencyMapper.selectAll();

        List<NewbieGiftSaveDTO> result = new ArrayList<>();
        for (NewbieGiftConfigDO config : configs) {
            result.add(toDTO(config, allItems, allCurrencies));
        }
        return result;
    }

    /**
     * 根据ID获取礼包配置。
     *
     * @param id 礼包配置ID
     * @return 礼包配置DTO，不存在则返回null
     */
    public NewbieGiftSaveDTO getConfigById(Long id) {
        NewbieGiftConfigDO config = configMapper.selectOneById(id);
        if (config == null) return null;
        return toDTO(config, itemMapper.selectAll(), currencyMapper.selectAll());
    }

    /**
     * 保存礼包配置（新增或更新），级联保存物品和货币子表。
     *
     * @param dto 礼包配置DTO
     * @return 保存后的礼包配置DTO
     */
    @Transactional
    public NewbieGiftSaveDTO saveConfig(NewbieGiftSaveDTO dto) {
        NewbieGiftConfigDO config = NewbieGiftConfigDO.builder()
                .id(dto.getId())
                .giftName(dto.getGiftName())
                .minLevel(dto.getMinLevel() != null ? dto.getMinLevel() : 1)
                .maxLevel(dto.getMaxLevel() != null ? dto.getMaxLevel() : 200)
                .enabled(dto.getEnabled() != null ? dto.getEnabled() : 1)
                .build();
        if (config.getId() != null) {
            configMapper.update(config);
            // 删除旧的关联物品和货币
            deleteItemsAndCurrencies(config.getId());
        } else {
            configMapper.insert(config);
        }

        // 保存物品奖励
        if (dto.getItems() != null) {
            for (ItemDTO item : dto.getItems()) {
                itemMapper.insert(NewbieGiftItemDO.builder()
                        .giftId(config.getId())
                        .itemId(item.getItemId())
                        .quantity(item.getQuantity() != null ? item.getQuantity() : 1)
                        .build());
            }
        }

        // 保存货币奖励
        if (dto.getCurrencies() != null) {
            for (CurrencyDTO cur : dto.getCurrencies()) {
                currencyMapper.insert(NewbieGiftCurrencyDO.builder()
                        .giftId(config.getId())
                        .currencyType(cur.getCurrencyType())
                        .amount(cur.getAmount() != null ? cur.getAmount() : 0)
                        .build());
            }
        }

        return getConfigById(config.getId());
    }

    /**
     * 删除礼包配置，级联删除物品、货币和领取记录。
     *
     * @param id 礼包配置ID
     */
    @Transactional
    public void deleteConfig(Long id) {
        deleteItemsAndCurrencies(id);
        recordMapper.deleteByQuery(QueryWrapper.create().where("gift_id = ?", id));
        configMapper.deleteById(id);
    }

    /**
     * 删除指定礼包配置下的所有物品和货币记录。
     *
     * @param configId 礼包配置ID
     */
    private void deleteItemsAndCurrencies(Long configId) {
        itemMapper.deleteByQuery(QueryWrapper.create().where("gift_id = ?", configId));
        currencyMapper.deleteByQuery(QueryWrapper.create().where("gift_id = ?", configId));
    }

    /**
     * 领取礼包（事务性：检查礼包→防重复领取→记录→构建发放信息）。
     * 实际物品和货币发放由调用方（脚本）执行。
     *
     * @param characterId 角色ID
     * @param giftId      礼包配置ID
     * @return 以"OK:"开头表示成功，否则返回错误信息
     */
    @Transactional
    public String claimGift(int characterId, long giftId) {
        // 检查礼包是否存在且启用
        NewbieGiftConfigDO config = configMapper.selectOneById(giftId);
        if (config == null || config.getEnabled() != 1) {
            return "该礼包不存在或已下架";
        }

        // 检查是否已领取
        List<NewbieGiftRecordDO> records = recordMapper.selectListByQuery(
                QueryWrapper.create()
                        .where("character_id = ?", characterId)
                        .where("gift_id = ?", giftId));
        if (!records.isEmpty()) {
            return "你已经领取过该礼包，每个礼包只能领取一次";
        }

        // 先插入领取记录防止并发重复领取
        try {
            recordMapper.insert(NewbieGiftRecordDO.builder()
                    .characterId(characterId)
                    .giftId(giftId)
                    .build());
        } catch (Exception e) {
            return "你已经领取过该礼包";
        }

        // 获取物品和货币列表
        List<NewbieGiftItemDO> items = itemMapper.selectListByQuery(
                QueryWrapper.create().where("gift_id = ?", giftId));
        List<NewbieGiftCurrencyDO> currencies = currencyMapper.selectListByQuery(
                QueryWrapper.create().where("gift_id = ?", giftId));

        // 构建结果信息
        StringBuilder sb = new StringBuilder();
        sb.append("成功领取礼包：").append(config.getGiftName()).append("\r\n\r\n");

        if (!items.isEmpty()) {
            sb.append("【物品奖励】\r\n");
            for (NewbieGiftItemDO item : items) {
                sb.append("#i").append(item.getItemId()).append("# ×").append(item.getQuantity()).append("\r\n");
            }
        }

        if (!currencies.isEmpty()) {
            sb.append("\r\n【货币奖励】\r\n");
            for (NewbieGiftCurrencyDO cur : currencies) {
                switch (cur.getCurrencyType()) {
                    case "meso":
                        sb.append("金币: +").append(cur.getAmount()).append("\r\n");
                        break;
                    case "cash":
                        sb.append("点卷: +").append(cur.getAmount()).append("\r\n");
                        break;
                    case "credit":
                        sb.append("抵用券: +").append(cur.getAmount()).append("\r\n");
                        break;
                }
            }
        }

        // 返回结果字符串，由调用方（脚本）执行实际发放
        return "OK:" + sb.toString();
    }

    /**
     * 获取礼包的物品列表（供脚本发放使用）。
     *
     * @param giftId 礼包配置ID
     * @return 物品列表
     */
    public List<NewbieGiftItemDO> getGiftItems(long giftId) {
        return itemMapper.selectListByQuery(QueryWrapper.create().where("gift_id = ?", giftId));
    }

    /**
     * 获取礼包的货币列表（供脚本发放使用）。
     *
     * @param giftId 礼包配置ID
     * @return 货币列表
     */
    public List<NewbieGiftCurrencyDO> getGiftCurrencies(long giftId) {
        return currencyMapper.selectListByQuery(QueryWrapper.create().where("gift_id = ?", giftId));
    }

    /**
     * 获取礼包配置。
     *
     * @param giftId 礼包配置ID
     * @return 礼包配置DO
     */
    public NewbieGiftConfigDO getGiftConfig(long giftId) {
        return configMapper.selectOneById(giftId);
    }

    /**
     * 将DO实体转换为DTO对象，包含关联的物品和货币列表。
     *
     * @param config        礼包配置DO
     * @param allItems      所有物品列表
     * @param allCurrencies 所有货币列表
     * @return 转换后的DTO对象
     */
    private NewbieGiftSaveDTO toDTO(NewbieGiftConfigDO config,
                                     List<NewbieGiftItemDO> allItems,
                                     List<NewbieGiftCurrencyDO> allCurrencies) {
        List<ItemDTO> itemDTOs = new ArrayList<>();
        for (NewbieGiftItemDO it : allItems) {
            if (it.getGiftId().equals(config.getId())) {
                itemDTOs.add(ItemDTO.builder()
                        .id(it.getId()).itemId(it.getItemId()).quantity(it.getQuantity()).build());
            }
        }

        List<CurrencyDTO> curDTOs = new ArrayList<>();
        for (NewbieGiftCurrencyDO cu : allCurrencies) {
            if (cu.getGiftId().equals(config.getId())) {
                curDTOs.add(CurrencyDTO.builder()
                        .id(cu.getId()).currencyType(cu.getCurrencyType()).amount(cu.getAmount()).build());
            }
        }

        return NewbieGiftSaveDTO.builder()
                .id(config.getId()).giftName(config.getGiftName())
                .minLevel(config.getMinLevel()).maxLevel(config.getMaxLevel())
                .enabled(config.getEnabled())
                .items(itemDTOs).currencies(curDTOs).build();
    }
}
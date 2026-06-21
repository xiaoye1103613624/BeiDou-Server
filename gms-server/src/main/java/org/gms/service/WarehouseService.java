package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.config.GameConfig;
import org.gms.config.WarehouseManager;
import org.gms.dao.entity.WarehouseConfigDO;
import org.gms.dao.entity.WarehouseItemDO;
import org.gms.dao.mapper.WarehouseConfigMapper;
import org.gms.dao.mapper.WarehouseItemMapper;
import org.gms.model.dto.WarehouseConfigDTO;
import org.gms.model.dto.WarehouseItemDTO;
import org.gms.server.ItemInformationProvider;
import org.gms.util.DatabaseConnection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 仓库服务类，负责仓库系统的配置管理和物品存取操作。
 * <p>
 * 提供仓库物品白名单配置的增删改查、仓库物品的存取操作，
 * 并在配置变更时同步更新 {@link WarehouseManager} 的内存缓存。
 * </p>
 */
@Slf4j
@Service
@AllArgsConstructor
public class WarehouseService {

    /** 仓库配置 Mapper */
    private final WarehouseConfigMapper configMapper;

    /** 仓库物品 Mapper */
    private final WarehouseItemMapper itemMapper;

    /**
     * 初始化方法，在 Spring 容器启动时执行。
     * 从数据库加载所有仓库配置并初始化到 WarehouseManager。
     */
    @PostConstruct
    public void init() {
        refreshConfigCache();
        log.info("仓库配置加载完成");
    }

    // ==================== 配置管理 ====================

    /**
     * 获取仓库物品配置（白名单），支持按条件筛选，按 sortOrder 和 itemId 排序。
     *
     * @param itemId        物品ID筛选（可选）
     * @param inventoryType 物品栏类型筛选（可选）
     * @param enabled       启用状态筛选（可选，0=禁用 1=启用）
     * @return 仓库配置 DTO 列表
     */
    public List<WarehouseConfigDTO> getConfigList(Integer itemId, Integer inventoryType, Integer enabled) {
        // 构建动态查询条件（第一条用 where，后续用 and）
        QueryWrapper qw = QueryWrapper.create();
        boolean hasCondition = false;
        if (itemId != null) {
            qw.where("item_id = ?", itemId);
            hasCondition = true;
        }
        if (inventoryType != null) {
            if (hasCondition) {
                qw.and("inventory_type = ?", inventoryType);
            } else {
                qw.where("inventory_type = ?", inventoryType);
                hasCondition = true;
            }
        }
        if (enabled != null) {
            if (hasCondition) {
                qw.and("enabled = ?", enabled);
            } else {
                qw.where("enabled = ?", enabled);
                hasCondition = true;
            }
        }
        List<WarehouseConfigDO> configs;
        if (hasCondition) {
            configs = configMapper.selectListByQuery(qw);
        } else {
            configs = configMapper.selectAll();
        }
        return configs.stream()
                .sorted(Comparator.comparingInt((WarehouseConfigDO c) ->
                        c.getSortOrder() != null ? c.getSortOrder() : 200)
                        .thenComparingInt(WarehouseConfigDO::getItemId))
                .map(this::toConfigDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取单个配置。
     *
     * @param id 配置ID
     * @return 配置 DTO，不存在返回 null
     */
    public WarehouseConfigDTO getConfigById(Long id) {
        WarehouseConfigDO config = configMapper.selectOneById(id);
        return config != null ? toConfigDTO(config) : null;
    }

    /**
     * 保存仓库物品配置（新增或更新）。
     *
     * @param dto 配置 DTO
     * @return 保存后的配置 DTO
     */
    @Transactional
    public WarehouseConfigDTO saveConfig(WarehouseConfigDTO dto) {
        WarehouseConfigDO config = WarehouseConfigDO.builder()
                .id(dto.getId())
                .itemId(dto.getItemId())
                .itemName(dto.getItemName())
                .inventoryType(dto.getInventoryType())
                .enabled(dto.getEnabled() != null ? dto.getEnabled() : 1)
                .sortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 200)
                .dropMapId(dto.getDropMapId() != null ? dto.getDropMapId() : 0)
                .build();
        if (config.getId() != null) {
            configMapper.update(config);
        } else {
            configMapper.insert(config);
        }
        // 刷新内存缓存
        refreshConfigCache();
        return getConfigById(config.getId());
    }

    /**
     * 删除仓库物品配置。
     *
     * @param id 配置ID
     */
    @Transactional
    public void deleteConfig(Long id) {
        configMapper.deleteById(id);
        // 刷新内存缓存
        refreshConfigCache();
    }

    /**
     * 批量删除仓库物品配置。
     *
     * @param ids 配置ID列表
     */
    @Transactional
    public void deleteConfigBatch(List<Long> ids) {
        configMapper.deleteBatchByIds(ids);
        refreshConfigCache();
    }

    /**
     * 刷新配置缓存，从数据库重新加载所有仓库配置到 WarehouseManager。
     */
    private void refreshConfigCache() {
        WarehouseManager.load(configMapper.selectAll());
    }

    /**
     * 一键根据物品ID从 WZ 数据更新仓库白名单配置的物品名称。
     * <p>
     * 遍历所有配置，通过 {@link ItemInformationProvider} 查询最新物品名称并回填，
     * 查询失败或返回空的物品保持原名称不变。
     * </p>
     *
     * @return 实际更新（名称发生变化）的条数
     */
    @Transactional
    public int refreshItemNames() {
        List<WarehouseConfigDO> configs = configMapper.selectAll();
        int updated = 0;
        for (WarehouseConfigDO config : configs) {
            String newName;
            try {
                newName = ItemInformationProvider.getInstance().getName(config.getItemId());
            } catch (Exception e) {
                log.warn("查询物品 {} 名称失败", config.getItemId(), e);
                continue;
            }
            if (newName != null && !newName.equals(config.getItemName())) {
                config.setItemName(newName);
                configMapper.update(config);
                updated++;
            }
        }
        refreshConfigCache();
        return updated;
    }

    // ==================== 物品存取 ====================

    /**
     * 查询仓库物品列表。
     * <p>
     * 支持三种查询方式：按账号ID、按角色ID（自动解析账号ID）、组合查询。
     * </p>
     *
     * @param accountId     账号ID（可选，为 null 时可通过 characterId 自动解析）
     * @param characterId   角色ID（账号共享模式下可为 null）
     * @param inventoryType 物品栏类型（null=所有类型）
     * @return 仓库物品 DTO 列表
     */
    public List<WarehouseItemDTO> getWarehouseItems(Integer accountId, Integer characterId, Integer inventoryType) {
        // 通过角色ID自动解析账号ID（无需同时传入账号ID和角色ID）
        Integer resolvedAccountId = accountId;
        if (resolvedAccountId == null && characterId != null) {
            resolvedAccountId = findAccountIdByCharacterId(characterId);
            if (resolvedAccountId == null) {
                log.warn("无法通过角色ID {} 找到对应账号", characterId);
                return Collections.emptyList();
            }
        }
        if (resolvedAccountId == null) {
            log.warn("查询仓库物品需要提供账号ID或角色ID");
            return Collections.emptyList();
        }

        QueryWrapper qw = QueryWrapper.create()
                .where("account_id = ?", resolvedAccountId);
        // 账号共享模式下不按角色ID过滤
        if (!isAccountShared() && characterId != null) {
            qw.and("character_id = ?", characterId);
        }
        if (inventoryType != null) {
            qw.and("inventory_type = ?", inventoryType);
        }
        qw.orderBy("inventory_type", true).orderBy("item_id", true);

        List<WarehouseItemDO> items = itemMapper.selectListByQuery(qw);
        return items.stream()
                .map(this::toItemDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据角色ID查询对应的账号ID。
     *
     * @param characterId 角色ID
     * @return 账号ID，未找到返回 null
     */
    private Integer findAccountIdByCharacterId(Integer characterId) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT accountid FROM characters WHERE id = ?")) {
            ps.setInt(1, characterId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("accountid");
                }
            }
        } catch (SQLException e) {
            log.error("根据角色ID {} 查询账号ID失败", characterId, e);
        }
        return null;
    }

    /**
     * 查询某物品在仓库中的存放数量。
     *
     * @param accountId   账号ID
     * @param characterId 角色ID（账号共享模式下可为 null）
     * @param itemId      物品ID
     * @return 已存放的数量
     */
    public int getItemQuantity(Integer accountId, Integer characterId, Integer itemId) {
        QueryWrapper qw = QueryWrapper.create()
                .where("account_id = ?", accountId)
                .and("item_id = ?", itemId);
        if (!isAccountShared() && characterId != null) {
            qw.and("character_id = ?", characterId);
        }

        List<WarehouseItemDO> items = itemMapper.selectListByQuery(qw);
        return items.stream().mapToInt(WarehouseItemDO::getQuantity).sum();
    }

    /**
     * 存入物品到仓库。
     * <p>
     * 对于非装备类物品，如果仓库中已有同 itemId 的记录，则累加数量；
     * 对于装备类物品（inventoryType=1），每个物品单独一条记录。
     * </p>
     *
     * @param accountId     账号ID
     * @param characterId   角色ID
     * @param itemId        物品ID
     * @param inventoryType 物品栏类型
     * @param quantity      存入数量
     * @return 存入成功返回 true
     */
    @Transactional
    public boolean depositItem(Integer accountId, Integer characterId, Integer itemId,
                               Integer inventoryType, Integer quantity) {
        // 检查物品是否在白名单中
        if (!canStoreItem(itemId)) {
            log.warn("物品 {} 不在仓库白名单中", itemId);
            return false;
        }

        // 检查堆叠上限
        int currentQty = getItemQuantity(accountId, characterId, itemId);
        int maxStack = getMaxStack();
        if (currentQty + quantity > maxStack) {
            log.warn("仓库物品 {} 已达堆叠上限 {}，当前 {}，尝试存入 {}", itemId, maxStack, currentQty, quantity);
            return false;
        }

        // 非装备类物品：如果已有同 itemId 记录，则累加数量
        if (inventoryType != 1) {
            QueryWrapper qw = QueryWrapper.create()
                    .where("account_id = ?", accountId)
                    .and("item_id = ?", itemId);
            if (!isAccountShared()) {
                qw.and("character_id = ?", characterId);
            }

            WarehouseItemDO existing = itemMapper.selectOneByQuery(qw);
            if (existing != null) {
                existing.setQuantity(existing.getQuantity() + quantity);
                itemMapper.update(existing);
                return true;
            }
        }

        // 新建仓库物品记录
        WarehouseItemDO item = WarehouseItemDO.builder()
                .accountId(accountId)
                .characterId(characterId)
                .itemId(itemId)
                .inventoryType(inventoryType)
                .quantity(quantity)
                .build();
        itemMapper.insert(item);
        return true;
    }

    /**
     * 从仓库取出物品。
     *
     * @param accountId     账号ID
     * @param characterId   角色ID
     * @param itemId        物品ID
     * @param inventoryType 物品栏类型
     * @param quantity      取出数量
     * @return 实际取出的数量（-1=操作失败）
     */
    @Transactional
    public int withdrawItem(Integer accountId, Integer characterId, Integer itemId,
                            Integer inventoryType, Integer quantity) {
        // 查询仓库中该物品的所有记录
        QueryWrapper qw = QueryWrapper.create()
                .where("account_id = ?", accountId)
                .and("item_id = ?", itemId)
                .and("inventory_type = ?", inventoryType);
        if (!isAccountShared()) {
            qw.and("character_id = ?", characterId);
        }
        qw.orderBy("id", true);

        List<WarehouseItemDO> items = itemMapper.selectListByQuery(qw);
        if (items.isEmpty()) {
            return -1;
        }

        int totalStored = items.stream().mapToInt(WarehouseItemDO::getQuantity).sum();
        if (totalStored < quantity) {
            return -1;
        }

        int remaining = quantity;
        // 从最早的记录开始扣减，直到满足取出数量
        for (WarehouseItemDO item : items) {
            if (remaining <= 0) break;
            if (item.getQuantity() <= remaining) {
                remaining -= item.getQuantity();
                itemMapper.deleteById(item.getId());
            } else {
                item.setQuantity(item.getQuantity() - remaining);
                itemMapper.update(item);
                remaining = 0;
            }
        }

        return quantity - remaining;
    }

    /**
     * 删除指定仓库物品记录（管理员操作）。
     *
     * @param id 仓库物品ID
     */
    @Transactional
    public void deleteWarehouseItem(Long id) {
        itemMapper.deleteById(id);
    }

    /**
     * 批量删除仓库物品记录（管理员操作）。
     *
     * @param ids 仓库物品ID列表
     */
    @Transactional
    public void deleteWarehouseItemBatch(List<Long> ids) {
        itemMapper.deleteBatchByIds(ids);
    }

    // ==================== 查询辅助 ====================

    /**
     * 获取有仓库数据的账号ID列表。
     *
     * @return 账号ID列表（去重）
     */
    public List<Integer> getAccountList() {
        List<WarehouseItemDO> items = itemMapper.selectAll();
        return items.stream()
                .map(WarehouseItemDO::getAccountId)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * 获取某账号下有仓库数据的角色ID列表。
     *
     * @param accountId 账号ID
     * @return 角色ID列表（去重）
     */
    public List<Integer> getCharacterList(Integer accountId) {
        QueryWrapper qw = QueryWrapper.create()
                .where("account_id = ?", accountId);
        List<WarehouseItemDO> items = itemMapper.selectListByQuery(qw);
        return items.stream()
                .map(WarehouseItemDO::getCharacterId)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    // ==================== 游戏参数 ====================

    /**
     * 检查物品是否在仓库白名单中且已启用。
     *
     * @param itemId 物品ID
     * @return 可以存放返回 true
     */
    public boolean canStoreItem(int itemId) {
        return WarehouseManager.canStoreItem(itemId);
    }

    /**
     * 获取仓库可叠加物品的最大存放数量。
     *
     * @return 最大堆叠数量
     */
    public int getMaxStack() {
        return WarehouseManager.getMaxStack();
    }

    /**
     * 检查仓库是否为账号共享模式。
     *
     * @return 账号共享返回 true
     */
    public boolean isAccountShared() {
        return WarehouseManager.isAccountShared();
    }

    // ==================== 数据转换 ====================

    /**
     * 将 DO 转换为 Config DTO。
     */
    private WarehouseConfigDTO toConfigDTO(WarehouseConfigDO config) {
        return WarehouseConfigDTO.builder()
                .id(config.getId())
                .itemId(config.getItemId())
                .itemName(config.getItemName())
                .inventoryType(config.getInventoryType())
                .enabled(config.getEnabled())
                .sortOrder(config.getSortOrder() != null ? config.getSortOrder() : 200)
                .dropMapId(config.getDropMapId() != null ? config.getDropMapId() : 0)
                .createTime(config.getCreateTime())
                .updateTime(config.getUpdateTime())
                .build();
    }

    /**
     * 将 DO 转换为 Item DTO，同时通过 WZ 数据解析物品名称。
     */
    private WarehouseItemDTO toItemDTO(WarehouseItemDO item) {
        String itemName = null;
        try {
            itemName = ItemInformationProvider.getInstance().getName(item.getItemId());
        } catch (Exception e) {
            // WZ 解析失败时忽略，保留原始 itemId 供前端展示
        }
        return WarehouseItemDTO.builder()
                .id(item.getId())
                .accountId(item.getAccountId())
                .characterId(item.getCharacterId())
                .itemId(item.getItemId())
                .itemName(itemName)
                .inventoryType(item.getInventoryType())
                .quantity(item.getQuantity())
                .createTime(item.getCreateTime())
                .updateTime(item.getUpdateTime())
                .build();
    }
}

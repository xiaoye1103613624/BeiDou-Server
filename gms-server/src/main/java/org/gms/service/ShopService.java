package org.gms.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Row;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.dao.entity.ShopitemsDO;
import org.gms.dao.entity.ShopsDO;
import org.gms.dao.mapper.ShopitemsMapper;
import org.gms.dao.mapper.ShopsMapper;
import org.gms.model.dto.ShopItemSearchRtnDTO;
import org.gms.model.dto.ShopSearchReqDTO;
import org.gms.model.dto.ShopSearchRtnDTO;
import org.gms.server.ItemInformationProvider;
import org.gms.server.ShopFactory;
import org.gms.server.life.LifeFactory;
import org.gms.util.BasePageUtil;
import org.gms.util.I18nUtil;
import org.gms.util.Pair;
import org.gms.util.RequireUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.gms.dao.entity.table.ShopitemsDOTableDef.SHOPITEMS_D_O;
import static org.gms.dao.entity.table.ShopsDOTableDef.SHOPS_D_O;

/**
 * NPC 商店后台：商店绑定 NPC，商品挂在 shopitems。
 * 运行时按 NPC 取店，同一 NPC 只允许一家商店。
 */
@Slf4j
@Service
@AllArgsConstructor
public class ShopService {
    private static final String UNKNOWN_NPC_NAME = "MISSINGNO";

    private final ShopsMapper shopsMapper;
    private final ShopitemsMapper shopitemsMapper;

    public Page<ShopSearchRtnDTO> getShopList(ShopSearchReqDTO data) {
        QueryWrapper queryWrapper = QueryWrapper.create().select().from(SHOPS_D_O)
                .leftJoin(SHOPITEMS_D_O).on(SHOPS_D_O.SHOPID.eq(SHOPITEMS_D_O.SHOPID));
        if (data.getNpcId() != null) {
            queryWrapper.and(SHOPS_D_O.NPCID.eq(data.getNpcId()));
        }
        if (data.getShopId() != null) {
            queryWrapper.and(SHOPS_D_O.SHOPID.eq(data.getShopId()));
        }
        if (data.getItemId() != null) {
            queryWrapper.and(SHOPITEMS_D_O.ITEMID.eq(data.getItemId()));
        }
        List<Row> queryAsList = shopsMapper.selectListByQueryAs(queryWrapper, Row.class);
        List<ShopSearchRtnDTO> matchedShopsDOList = new ArrayList<>();
        for (Row row : queryAsList) {
            Integer npcId = row.getInt("npcid");
            String npcName = LifeFactory.getNPCName(npcId);
            if (RequireUtil.isEmpty(npcName)) {
                continue;
            }
            if (!RequireUtil.isEmpty(data.getNpcName()) && !npcName.contains(data.getNpcName())) {
                continue;
            }
            Integer itemId = row.getInt("itemid");
            if (itemId != null) {
                if (data.getItemId() != null && !Objects.equals(itemId, data.getItemId())) {
                    continue;
                }
                String itemName = ItemInformationProvider.getInstance().getName(itemId);
                if (!RequireUtil.isEmpty(data.getItemName()) && !RequireUtil.isEmpty(itemName) && !itemName.contains(data.getItemName())) {
                    continue;
                }
            }
            matchedShopsDOList.add(ShopSearchRtnDTO.builder()
                    .shopId(row.getLong("shopid"))
                    .npcId(row.getInt("npcid"))
                    .npcName(npcName)
                    .build());
        }
        return BasePageUtil.create(matchedShopsDOList.stream().distinct().toList(), data).page();
    }

    public Page<ShopItemSearchRtnDTO> getShopItemList(ShopSearchReqDTO data) {
        QueryWrapper queryWrapper = QueryWrapper.create(ShopitemsDO.builder()
                .shopid(data.getShopId())
                .build());
        Page<ShopitemsDO> paginate = shopitemsMapper.paginate(data.getPageNo(), data.getPageSize(), queryWrapper);
        return new Page<>(
                paginate.getRecords().stream().map(this::fromShopItemDO).toList(),
                paginate.getPageNumber(),
                paginate.getPageSize(),
                paginate.getTotalRow()
        );
    }

    /**
     * 按道具反查：哪些 NPC 商店在卖该物品。
     */
    public Page<ShopItemSearchRtnDTO> getItemShopList(ShopSearchReqDTO data) {
        RequireUtil.requireTrue(
                data.getItemId() != null || !RequireUtil.isEmpty(data.getItemName()),
                I18nUtil.getExceptionMessage("ShopService.itemQuery.required"));
        QueryWrapper queryWrapper = QueryWrapper.create().select().from(SHOPITEMS_D_O)
                .leftJoin(SHOPS_D_O).on(SHOPITEMS_D_O.SHOPID.eq(SHOPS_D_O.SHOPID));
        if (data.getItemId() != null) {
            queryWrapper.and(SHOPITEMS_D_O.ITEMID.eq(data.getItemId()));
        }
        List<Row> rows = shopitemsMapper.selectListByQueryAs(queryWrapper, Row.class);
        List<ShopItemSearchRtnDTO> matched = new ArrayList<>();
        for (Row row : rows) {
            Integer itemId = row.getInt("itemid");
            Pair<String, String> nameDesc = itemId == null
                    ? null
                    : ItemInformationProvider.getInstance().getNameDesc(itemId);
            String itemName = nameDesc == null ? "" : nameDesc.getLeft();
            if (!RequireUtil.isEmpty(data.getItemName()) && !itemName.contains(data.getItemName())) {
                continue;
            }
            Integer npcId = row.getInt("npcid");
            String npcName = npcId == null ? "" : LifeFactory.getNPCName(npcId);
            matched.add(ShopItemSearchRtnDTO.builder()
                    .id(row.getLong("shopitemid"))
                    .shopId(row.getLong("shopid"))
                    .npcId(npcId)
                    .npcName(npcName)
                    .itemId(itemId)
                    .price(row.getInt("price"))
                    .pitch(row.getInt("pitch"))
                    .position(row.getInt("position"))
                    .itemName(itemName)
                    .itemDesc(nameDesc == null ? "" : nameDesc.getRight())
                    .build());
        }
        return BasePageUtil.create(matched, data).page();
    }

    public ShopItemSearchRtnDTO getShopItem(Long id) {
        return fromShopItemDO(shopitemsMapper.selectOneById(id));
    }

    public Long addShop(ShopSearchRtnDTO data) {
        Integer npcId = requireValidNpcId(data.getNpcId());
        requireNpcAvailable(npcId, null);
        if (data.getShopId() != null) {
            ShopsDO existing = shopsMapper.selectOneById(data.getShopId());
            RequireUtil.requireTrue(existing == null, I18nUtil.getExceptionMessage("ShopService.shopId.exists"));
        }
        ShopsDO shopsDO = ShopsDO.builder()
                .shopid(data.getShopId())
                .npcid(npcId)
                .build();
        if (data.getShopId() != null) {
            shopsMapper.insert(shopsDO);
        } else {
            shopsMapper.insertSelective(shopsDO);
        }
        ShopFactory.getInstance().reloadShops();
        log.info(I18nUtil.getLogMessage("ShopService.addShop.info"), shopsDO.getShopid(), npcId);
        return shopsDO.getShopid();
    }

    public void updateShop(ShopSearchRtnDTO data) {
        RequireUtil.requireNotNull(data.getShopId(), I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "shopId"));
        Integer npcId = requireValidNpcId(data.getNpcId());
        ShopsDO existing = shopsMapper.selectOneById(data.getShopId());
        RequireUtil.requireNotNull(existing, I18nUtil.getExceptionMessage("ShopService.shop.notExist"));
        requireNpcAvailable(npcId, data.getShopId());
        existing.setNpcid(npcId);
        shopsMapper.update(existing);
        ShopFactory.getInstance().reloadShops();
        log.info(I18nUtil.getLogMessage("ShopService.updateShop.info"), data.getShopId(), npcId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteShop(Long shopId) {
        RequireUtil.requireNotNull(shopId, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "shopId"));
        ShopsDO existing = shopsMapper.selectOneById(shopId);
        RequireUtil.requireNotNull(existing, I18nUtil.getExceptionMessage("ShopService.shop.notExist"));
        long itemCount = shopitemsMapper.selectCountByQuery(
                QueryWrapper.create().where(SHOPITEMS_D_O.SHOPID.eq(shopId)));
        shopitemsMapper.deleteByQuery(
                QueryWrapper.create().where(SHOPITEMS_D_O.SHOPID.eq(shopId)));
        shopsMapper.deleteById(shopId);
        ShopFactory.getInstance().reloadShops();
        log.info(I18nUtil.getLogMessage("ShopService.deleteShop.info"), shopId, itemCount);
    }

    public Long modifyShopItem(ShopItemSearchRtnDTO data, boolean isDelete) {
        Long shopItemId;
        if (isDelete) {
            shopitemsMapper.deleteById(data.getId());
            shopItemId = data.getId();
        } else {
            ShopitemsDO shopitemsDO = ShopitemsDO.builder()
                    .shopitemid(data.getId())
                    .shopid(data.getShopId())
                    .itemid(data.getItemId())
                    .price(data.getPrice())
                    .pitch(data.getPitch())
                    .position(data.getPosition())
                    .build();
            shopitemsMapper.insertOrUpdate(shopitemsDO, true);
            shopItemId = shopitemsDO.getShopitemid();
        }
        ShopFactory.getInstance().reloadShops();
        return shopItemId;
    }

    private Integer requireValidNpcId(Integer npcId) {
        RequireUtil.requireNotNull(npcId, I18nUtil.getExceptionMessage("ShopService.npcId.required"));
        RequireUtil.requireTrue(npcId > 0, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_ZERO", "npcId"));
        String npcName = LifeFactory.getNPCName(npcId);
        RequireUtil.requireTrue(!RequireUtil.isEmpty(npcName) && !UNKNOWN_NPC_NAME.equals(npcName),
                I18nUtil.getExceptionMessage("ShopService.npc.unknown"));
        return npcId;
    }

    private void requireNpcAvailable(Integer npcId, Long excludeShopId) {
        ShopsDO occupied = shopsMapper.selectOneByQuery(
                QueryWrapper.create().where(SHOPS_D_O.NPCID.eq(npcId)));
        if (occupied == null) {
            return;
        }
        if (excludeShopId != null && Objects.equals(occupied.getShopid(), excludeShopId)) {
            return;
        }
        RequireUtil.requireTrue(false, I18nUtil.getExceptionMessage("ShopService.npc.occupied"));
    }

    private ShopItemSearchRtnDTO fromShopItemDO(ShopitemsDO shopitemsDO) {
        Pair<String, String> nameDesc = ItemInformationProvider.getInstance().getNameDesc(shopitemsDO.getItemid());
        return ShopItemSearchRtnDTO.builder()
                .id(shopitemsDO.getShopitemid())
                .shopId(shopitemsDO.getShopid())
                .itemId(shopitemsDO.getItemid())
                .price(shopitemsDO.getPrice())
                .pitch(shopitemsDO.getPitch())
                .position(shopitemsDO.getPosition())
                .itemName(nameDesc == null ? "" : nameDesc.getLeft())
                .itemDesc(nameDesc == null ? "" : nameDesc.getRight())
                .build();
    }
}

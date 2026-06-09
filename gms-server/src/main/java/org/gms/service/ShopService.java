package org.gms.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Row;
import lombok.AllArgsConstructor;
import org.gms.dao.entity.ShopitemsDO;
import org.gms.dao.mapper.ShopitemsMapper;
import org.gms.dao.mapper.ShopsMapper;
import org.gms.model.dto.ShopItemSearchRtnDTO;
import org.gms.model.dto.ShopSearchReqDTO;
import org.gms.model.dto.ShopSearchRtnDTO;
import org.gms.server.ItemInformationProvider;
import org.gms.server.ShopFactory;
import org.gms.server.life.LifeFactory;
import org.gms.util.BasePageUtil;
import org.gms.util.Pair;
import org.gms.util.RequireUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.gms.dao.entity.table.ShopitemsDOTableDef.SHOPITEMS_D_O;
import static org.gms.dao.entity.table.ShopsDOTableDef.SHOPS_D_O;

/**
 * 商店服务类
 * 提供商店和商店物品的查询、修改、删除等管理功能
 */
@Service
@AllArgsConstructor
public class ShopService {
    /**
     * 商店数据访问对象
     */
    private final ShopsMapper shopsMapper;

    /**
     * 商店物品数据访问对象
     */
    private final ShopitemsMapper shopitemsMapper;

    /**
     * 查询商店列表
     * 支持按NPC ID、商店ID、物品ID、NPC名称、物品名称进行筛选
     *
     * @param data 查询条件
     * @return 分页的商店列表
     */
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

    /**
     * 查询商店物品列表
     * 根据商店ID分页查询该商店下的所有物品
     *
     * @param data 查询条件，包含商店ID、页码、每页大小
     * @return 分页的商店物品列表
     */
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
     * 查询单个商店物品
     *
     * @param id 商店物品ID
     * @return 商店物品信息
     */
    public ShopItemSearchRtnDTO getShopItem(Long id) {
        return fromShopItemDO(shopitemsMapper.selectOneById(id));
    }

    /**
     * 修改或删除商店物品
     * 修改完成后会重新加载商店缓存
     *
     * @param data     商店物品数据
     * @param isDelete 是否为删除操作
     * @return 操作的商店物品ID
     */
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

    /**
     * 将商店物品实体转换为DTO
     *
     * @param shopitemsDO 商店物品实体
     * @return 商店物品DTO
     */
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
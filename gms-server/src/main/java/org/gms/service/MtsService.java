package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.gms.dao.entity.MtsCartDO;
import org.gms.dao.mapper.MtsCartMapper;
import org.gms.dao.mapper.MtsItemsMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.gms.dao.entity.table.MtsCartDOTableDef.MTS_CART_D_O;
import static org.gms.dao.entity.table.MtsItemsDOTableDef.MTS_ITEMS_D_O;

/**
 * 拍卖行服务类，提供拍卖行数据的管理功能。
 */
@Service
@AllArgsConstructor
public class MtsService {

    /**
     * 拍卖行购物车数据访问对象
     */
    private final MtsCartMapper mtsCartMapper;

    /**
     * 拍卖行物品数据访问对象
     */
    private final MtsItemsMapper mtsItemsMapper;

    /**
     * 根据角色ID删除拍卖行数据，级联删除购物车和物品记录。
     *
     * @param cid 角色ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteMtsByCharacterId(int cid) {
        QueryWrapper queryWrapper = QueryWrapper.create().where(MTS_CART_D_O.CID.eq(cid));
        // 查询该角色的所有购物车记录
        List<MtsCartDO> mtsCartDOS = mtsCartMapper.selectListByQuery(queryWrapper);
        // 提取购物车ID列表
        List<Integer> mtsIds = mtsCartDOS.stream().map(MtsCartDO::getId).toList();
        if (!mtsIds.isEmpty()) {
            // 先删除关联的物品记录
            mtsItemsMapper.deleteByQuery(QueryWrapper.create().where(MTS_ITEMS_D_O.ID.in(mtsIds)));
            // 再删除购物车记录
            mtsCartMapper.deleteByQuery(queryWrapper);
        }
    }
}
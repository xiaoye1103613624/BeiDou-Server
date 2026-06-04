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
 * 【业务服务】MtsService：拍卖行服务类，负责拍卖行相关数据的管理。
 * 
 * <p>提供拍卖行购物车和物品的管理功能，主要用于角色删除时清理相关数据。</p>
 */
@Service
@AllArgsConstructor
public class MtsService {
    /** 拍卖行购物车数据访问接口 */
    private final MtsCartMapper mtsCartMapper;
    /** 拍卖行物品数据访问接口 */
    private final MtsItemsMapper mtsItemsMapper;

    /**
     * 根据角色ID删除拍卖行相关数据。
     * 
     * <p>级联删除角色的购物车记录及其关联的物品记录。
     * 先查询角色的购物车记录，获取ID列表，然后删除关联的物品记录，最后删除购物车记录。</p>
     * 
     * @param cid 角色ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteMtsByCharacterId(int cid) {
        // 查询角色的购物车记录
        QueryWrapper queryWrapper = QueryWrapper.create().where(MTS_CART_D_O.CID.eq(cid));
        List<MtsCartDO> mtsCartDOS = mtsCartMapper.selectListByQuery(queryWrapper);
        List<Integer> mtsIds = mtsCartDOS.stream().map(MtsCartDO::getId).toList();
        
        if (!mtsIds.isEmpty()) {
            // 先删除购物车关联的物品记录
            mtsItemsMapper.deleteByQuery(QueryWrapper.create().where(MTS_ITEMS_D_O.ID.in(mtsIds)));
            // 再删除购物车记录
            mtsCartMapper.deleteByQuery(queryWrapper);
        }
    }
}
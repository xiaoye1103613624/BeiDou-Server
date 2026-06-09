package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.gms.client.MonsterBook;
import org.gms.dao.entity.MonsterbookDO;
import org.gms.dao.mapper.MonsterbookMapper;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.gms.dao.entity.table.MonsterbookDOTableDef.MONSTERBOOK_D_O;

/**
 * 怪物图鉴服务类
 * 提供玩家怪物图鉴的查询功能
 */
@Service
@AllArgsConstructor
public class MonsterBookService {
    /** 怪物图鉴数据访问对象 */
    private final MonsterbookMapper monsterbookMapper;

    /**
     * 根据角色ID获取怪物图鉴列表
     *
     * @param cid 角色ID
     * @return 怪物图鉴列表
     */
    public List<MonsterbookDO> getByCharacterId(int cid) {
        return monsterbookMapper.selectListByQuery(QueryWrapper.create().where(MONSTERBOOK_D_O.CHARID.eq(cid)).orderBy(MONSTERBOOK_D_O.CHARID, true));
    }
}
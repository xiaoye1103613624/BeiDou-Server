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
 * 【业务服务】MonsterBookService：封装 `service` 相关应用逻辑与数据协作。
 */
@Service
@AllArgsConstructor
public class MonsterBookService {
    private final MonsterbookMapper monsterbookMapper;

    public List<MonsterbookDO> getByCharacterId(int cid) {
        return monsterbookMapper.selectListByQuery(QueryWrapper.create().where(MONSTERBOOK_D_O.CHARID.eq(cid)).orderBy(MONSTERBOOK_D_O.CHARID, true));
    }
}

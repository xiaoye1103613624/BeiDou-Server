package org.gms.util;

import com.mybatisflex.core.query.QueryWrapper;
import org.gms.dao.entity.ExtendValueDO;
import static org.gms.dao.entity.table.ExtendValueDOTableDef.EXTEND_VALUE_D_O;
import org.gms.dao.mapper.ExtendValueMapper;
import org.gms.manager.ServerManager;

import java.sql.Date;

public class ExtendUtil {
    private static final ExtendValueMapper extendValueMapper = ServerManager.getApplicationContext().getBean(ExtendValueMapper.class);

    /**
     * 根据扩展ID、扩展类型和扩展名称获取扩展值对象
     *
     * @param extendId    扩展ID
     * @param extendType  扩展类型
     * @param extendName  扩展名称
     * @return 返回对应的ExtendValueDO对象，如果未找到则返回null
     */
    public static ExtendValueDO getExtendValue(String extendId, String extendType, String extendName) {
        return extendValueMapper.selectOneByQuery(QueryWrapper.create()
                .where(EXTEND_VALUE_D_O.EXTEND_ID.eq(extendId))
                .and(EXTEND_VALUE_D_O.EXTEND_TYPE.eq(extendType))
                .and(EXTEND_VALUE_D_O.EXTEND_NAME.eq(extendName)));

    }

    /**
     * 保存或更新扩展值。
     *
     * @param extendId     扩展ID
     * @param extendType   扩展类型
     * @param extendName   扩展名称
     * @param extendValue  扩展值
     */
    public static void saveOrUpdateExtendValue(String extendId, String extendType, String extendName, String extendValue) {
        ExtendValueDO extendValueDO = getExtendValue(extendId, extendType, extendName);
        if (extendValueDO == null) {
            extendValueMapper.insertSelective(ExtendValueDO.builder()
                    .extendId(extendId)
                    .extendType(extendType)
                    .extendName(extendName)
                    .extendValue(extendValue)
                    .createTime(new Date(System.currentTimeMillis()))
                    .build());
        } else {
            extendValueDO.setCreateTime(null);
            extendValueDO.setUpdateTime(new Date(System.currentTimeMillis()));
            extendValueDO.setExtendValue(extendValue);
            extendValueMapper.update(extendValueDO);
        }
    }
}

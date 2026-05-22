package org.gms.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.gms.dao.entity.NxcodeItemsDO;

/**
 * 【映射器】NxcodeItemsMapper（interface），包 {@code org.gms.dao.mapper}。
 *
 * 对应数据库表 nxcode_items 的数据访问接口，提供Nx兑换码物品相关查询操作。
 *
 * @author 萧曵
 */
public interface NxcodeItemsMapper extends BaseMapper<NxcodeItemsDO> {
    @Delete("DELETE FROM nxcode_items WHERE codeid IN (SELECT id FROM nxcode WHERE expiration <= #{timeClear})")
    void clearExpirations(long timeClear);
}

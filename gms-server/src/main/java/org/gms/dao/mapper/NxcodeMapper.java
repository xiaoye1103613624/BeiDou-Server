package org.gms.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.gms.dao.entity.NxcodeDO;

/**
 * 【映射器】NxcodeMapper（interface），包 {@code org.gms.dao.mapper}。
 *
 * 对应数据库表 nxcode 的数据访问接口，提供Nx兑换码相关查询操作。
 *
 * @author 萧曵
 */
public interface NxcodeMapper extends BaseMapper<NxcodeDO> {
    @Delete("DELETE FROM nxcode WHERE expiration <= #{timeClear}")
    void clearExpirations(long timeClear);
}

package org.gms.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.gms.dao.entity.NxcouponsDO;

import java.util.List;

/**
 * 【映射器】NxcouponsMapper（interface），包 {@code org.gms.dao.mapper}。
 *
 * 对应数据库表 nxcoupons 的数据访问接口，提供Nx优惠券相关查询操作。
 *
 * @author 萧曵
 */
public interface NxcouponsMapper extends BaseMapper<NxcouponsDO> {
    @Select("SELECT couponid FROM nxcoupons WHERE (activeday & #{weekDay}) = #{weekDay} AND starthour <= #{hourDay} AND endhour > #{hourDay}")
    List<Integer> selectActiveCouponIds(@Param("weekDay") int weekDay, @Param("hourDay") int hourDay);
}

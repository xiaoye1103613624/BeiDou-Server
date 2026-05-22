package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.gms.dao.entity.NxcouponsDO;
import org.gms.dao.mapper.NxcouponsMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * NX优惠券服务
 * <p>提供NX优惠券的查询功能</p>
 */
@Service
@AllArgsConstructor
public class NxCouponService {
    private final NxcouponsMapper nxcouponsMapper;

    /**
     * 查询指定时间段内生效的优惠券ID列表
     *
     * @param weekDay 星期几（0-6）
     * @param hourDay 小时（0-23）
     * @return 生效中的优惠券ID列表
     */
    public List<Integer> selectActiveCouponIds(int weekDay, int hourDay) {
        return nxcouponsMapper.selectActiveCouponIds(weekDay, hourDay);
    }

    /**
     * 根据条件查询优惠券列表
     *
     * @param condition 查询条件
     * @return 优惠券列表
     */
    public List<NxcouponsDO> getNxCoupons(NxcouponsDO condition) {
        QueryWrapper queryWrapper = QueryWrapper.create(condition);
        return nxcouponsMapper.selectListByQuery(queryWrapper);
    }
}

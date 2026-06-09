package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.gms.dao.entity.NxcouponsDO;
import org.gms.dao.mapper.NxcouponsMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * NX优惠券服务类
 * 管理NX优惠券的查询和激活状态
 */
@Service
@AllArgsConstructor
public class NxCouponService {
    /** NX优惠券数据访问对象 */
    private final NxcouponsMapper nxcouponsMapper;

    /**
     * 查询指定星期和小时可用的优惠券ID列表
     *
     * @param weekDay 星期几
     * @param hourDay 小时
     * @return 活跃优惠券ID列表
     */
    public List<Integer> selectActiveCouponIds(int weekDay, int hourDay) {
        return nxcouponsMapper.selectActiveCouponIds(weekDay, hourDay);
    }

    /**
     * 根据条件查询NX优惠券列表
     *
     * @param condition 查询条件
     * @return NX优惠券列表
     */
    public List<NxcouponsDO> getNxCoupons(NxcouponsDO condition) {
        QueryWrapper queryWrapper = QueryWrapper.create(condition);
        return nxcouponsMapper.selectListByQuery(queryWrapper);
    }
}
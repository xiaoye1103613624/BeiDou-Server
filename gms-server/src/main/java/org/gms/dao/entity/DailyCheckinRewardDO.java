package org.gms.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 每日签到奖励配置。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("daily_checkin_reward")
public class DailyCheckinRewardDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.None)
    private Integer day;

    private Integer iconItemId;

    private Integer mesos;

    private Integer itemId;

    private Integer itemQty;

    private Integer expireDays;

    private Integer item2Id;

    private Integer item2Qty;

    private Integer item2Expire;

    private Integer slotType;

    private Integer slotCount;

    private String remark;
}

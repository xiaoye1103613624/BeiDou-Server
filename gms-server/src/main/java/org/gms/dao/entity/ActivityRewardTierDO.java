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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("activity_reward_tier")
public class ActivityRewardTierDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String activityCode;

    private String tierCode;

    private String tierName;

    private Integer priority;

    private String exclusiveGroup;

    private String matchJson;

    private String grantMode;

    private Long mesos;

    private Integer exp;

    private Integer itemId;

    private Integer itemQty;

    private Integer item2Id;

    private Integer item2Qty;

    private Integer announceName;

    private String announceTpl;

    private Integer enabled;

    private String remark;
}

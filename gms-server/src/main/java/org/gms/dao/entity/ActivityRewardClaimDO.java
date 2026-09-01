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
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("activity_reward_claim")
public class ActivityRewardClaimDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Long sessionId;

    private Long tierId;

    private String tierCode;

    private Integer characterId;

    private String characterName;

    private String grantMode;

    private String status;

    private Long mesos;

    private Integer exp;

    private Integer itemId;

    private Integer itemQty;

    private Integer item2Id;

    private Integer item2Qty;

    private Integer announceName;

    private Date createdAt;

    private Date claimedAt;

    private Date expireAt;
}
